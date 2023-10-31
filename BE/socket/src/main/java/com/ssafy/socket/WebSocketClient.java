package com.ssafy.socket;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class WebSocketClient {

    private WebSocketStompClient stompClient; // WebSocket 클라이언트

    private MySessionHandler sessionHandler;  // 세션 핸들러

    String channelId = "";

    // 락 파일 경로 및 객체
    private static final String LOCK_FILE = "websocket.lock";  // 상수 LOCK_FILE에 "websocket.lock" 파일명을 저장합니다.
    private static Path lockFilePath = Paths.get(LOCK_FILE);  // lockFilePath에 "websocket.lock" 파일의 경로를 설정합니다.

    // 애플리케이션이 이미 실행 중인지 확인하는 메서드입니다.
    private static boolean isAppAlreadyRunning() {
        try {
            FileChannel channel = FileChannel.open(lockFilePath, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            // "websocket.lock" 파일을 생성하거나 엽니다.

            FileLock lock = channel.tryLock();  // 파일 락을 시도합니다.
            if (lock == null) {  // 락이 획득되지 않았다면
                channel.close();  // 파일 채널을 닫습니다.
                return true;  // 이미 애플리케이션이 실행 중이라고 판단합니다.
            }
        } catch (IOException e) {
            e.printStackTrace();  // 입출력 예외가 발생한 경우 에러를 출력합니다.
        }
        return false;  // 애플리케이션이 실행 중이 아닌 것으로 판단합니다.
    }


    // 클라이언트 애플리케이션을 시작하는 메서드
    public void start() {
        if (isAppAlreadyRunning()) {
            System.out.println("The application is already running.");
            return;
        }


        // UI 생성을 별도의 스레드에서 처리
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // 불가사리 이미지 추가
                ImageIcon starIcon = new ImageIcon(getClass().getResource("/squid.png"));
                Image starImage = starIcon.getImage().getScaledInstance(starIcon.getIconWidth() / 2, starIcon.getIconHeight() / 2, Image.SCALE_SMOOTH);
                ImageIcon smallStarIcon = new ImageIcon(starImage);
                JLabel starLabel = new JLabel(smallStarIcon);

                // 채널 ID 입력 필드 추가
                JTextField channelIdField = new JTextField(13);
                Dimension fieldPreferredSize = channelIdField.getPreferredSize();
                fieldPreferredSize.height *= 1.9; // 상하 길이를 두 배로 늘리기
                channelIdField.setPreferredSize(fieldPreferredSize);
                channelIdField.setFont(new Font("Arial", Font.PLAIN, 18)); // 폰트 설정
                channelIdField.setHorizontalAlignment(JTextField.CENTER); // 채널 ID Field에 대한 텍스트 가운데 정렬 설정

                JButton connectButton = new JButton("Connect"); // 연결 버튼
                connectButton.setMaximumSize(channelIdField.getPreferredSize());
                connectButton.setMinimumSize(channelIdField.getPreferredSize());
                connectButton.setPreferredSize(channelIdField.getPreferredSize());


                Dimension buttonPreferredSize = connectButton.getPreferredSize();
                buttonPreferredSize.height *= 1.1; // 버튼의 크기를 1.1배로 늘리기
                connectButton.setPreferredSize(buttonPreferredSize);

                JButton exitButton = new JButton("Exit"); // 프로세스 종료 버튼
                exitButton.setMaximumSize(channelIdField.getPreferredSize());
                exitButton.setMinimumSize(channelIdField.getPreferredSize());
                exitButton.setPreferredSize(channelIdField.getPreferredSize());

                JButton disconnectButton = new JButton("disconnect"); // 프로세스 종료 버튼
                disconnectButton.setFont(new Font("Arial", Font.BOLD, 18)); // disconnect 버튼 폰트 설정
                disconnectButton.setMaximumSize(channelIdField.getPreferredSize());
                disconnectButton.setMinimumSize(channelIdField.getPreferredSize());
                disconnectButton.setPreferredSize(channelIdField.getPreferredSize());
                disconnectButton.setVisible(false);

                // Algoarium 레이블 생성
                JLabel titleLabel = new JLabel("Algoarium");
                titleLabel.setFont(new Font("Arial", Font.BOLD, 80)); // 폰트 설정
                titleLabel.setForeground(Color.WHITE); // 글자색 설정

                // 채팅 연결 버튼 이전에 채널 ID 입력 필드 추가
                JPanel panel = new JPanel(); // 패널 생성
                panel.setOpaque(false); // 패널을 투명하게 설정

                GroupLayout layout = new GroupLayout(panel);
                panel.setLayout(layout);

                layout.setAutoCreateGaps(true); // 자동 간격 생성
                layout.setAutoCreateContainerGaps(true); // 컨테이너 간격 자동 생성

                layout.setHorizontalGroup(
                        layout.createParallelGroup(GroupLayout.Alignment.CENTER)
//                                .addComponent(starLabel)  // 불가사리 이미지
                                .addComponent(titleLabel) // Algoarium 레이블 추가
                                .addComponent(channelIdField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)  // 채널 ID 입력 필드
                                .addComponent(connectButton)  // Connect 버튼
                                .addComponent(disconnectButton)  // disConnect 버튼
//                                .addComponent(exitButton)  // Exit 버튼
                );

                layout.setVerticalGroup(
                        layout.createSequentialGroup()
                                .addGap(100)
//                                .addComponent(starLabel)  // 불가사리 이미지
                                .addComponent(titleLabel) // Algoarium 레이블 추가
                                .addGap(40)
                                .addComponent(channelIdField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)  // 채널 ID 입력 필드
                                .addGap(10)
                                .addComponent(connectButton)  // Connect 버튼
                                .addComponent(disconnectButton)  // disConnect 버튼
                                .addGap(40)
//                                .addComponent(exitButton)  // Exit 버튼
                );

                JFrame frame = new JFrame("Algoarium"); // 프레임 생성
                frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE); // 프레임 닫기 버튼 동작 설정

                // 창 크기 조절 비활성화
                frame.setResizable(false); // 창 크기 조절 비활성화


                // 시스템 트레이 설정
                if (SystemTray.isSupported()) {
                    SystemTray tray = SystemTray.getSystemTray();

                    // Show 옵션 클릭 시 실행되는 액션
                    ActionListener showAction = new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            frame.setVisible(true);
                            frame.setExtendedState(JFrame.NORMAL);
                        }
                    };

                    // 시스템 트레이 팝업 메뉴
                    PopupMenu popup = new PopupMenu();
                    MenuItem showItem = new MenuItem("Show");
                    showItem.addActionListener(showAction);
                    popup.add(showItem);

                    // Exit 옵션 클릭 시 애플리케이션 종료
                    MenuItem exitItem = new MenuItem("Exit");
                    exitItem.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            if (stompClient != null) {
                                stompClient.stop(); // WebSocket 클라이언트 정지
                            }
                            System.exit(0); // 시스템 종료
                        }
                    });

                    popup.add(exitItem);

                    ImageIcon icon = new ImageIcon(getClass().getResource("/squid.png"));
                    Image image = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
                    TrayIcon trayIcon = new TrayIcon(image, "Algoarium", popup);

                    trayIcon.addMouseListener(new MouseAdapter() {
                        public void mouseClicked(MouseEvent e) {
                            if (e.getButton() == MouseEvent.BUTTON1) { // BUTTON1은 좌클릭을 의미합니다.
                                frame.setVisible(true);
                                frame.setExtendedState(JFrame.NORMAL);
                            }
                        }
                    });

                    try {
                        tray.add(trayIcon);
                    } catch (AWTException e) {
                        e.printStackTrace();
                    }

                    // Exit 버튼 클릭 시 애플리케이션 종료
                    exitButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // 프로그램 종료 시 WebSocket 클라이언트 정지
                            if (stompClient != null) {
                                stompClient.stop();
                            }
                            System.exit(0); // 시스템 종료
                        }
                    });

                    // 프레임을 아이콘화할 때 처리할 이벤트
                    frame.addWindowListener(new WindowAdapter() {
                        public void windowIconified(WindowEvent e) {
                            try {
                                tray.add(trayIcon); // 시스템 트레이에 아이콘 추가
                                frame.setVisible(false); // 프레임 숨기기
                            } catch (AWTException ex) {
                                ex.printStackTrace();
                            }
                        }

                        public void windowClosing(WindowEvent e) {
                            // 창이 닫히는 이벤트를 처리합니다.
                            frame.setVisible(false);
                        }
                    });
                }

                // Disconnect 버튼 ActionListener
                disconnectButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                            @Override
                            protected Void doInBackground() throws Exception {

                                if (sessionHandler != null) {
                                    sessionHandler.disconnect();  // 만약 이전에 생성한 MySessioinHandler가 있다면 disconnect 호출.
                                }

                                channelIdField.setVisible(true);
                                channelIdField.setText("");
                                connectButton.setVisible(true);
//                                connectButton.setEnabled(true);
//                                connectButton.setText("Connect");
                                disconnectButton.setVisible(false);
                                titleLabel.setText("Algoarium");

                                return null;
                            }
                        };

                        worker.execute();
                    }
                });

                // Disconnect 버튼에 대한 MouseListener (hover 효과 추가)
                disconnectButton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        // 버튼에 커서가 들어왔을 때의 동작: 버튼 배경색 변경
                        disconnectButton.setBackground(Color.DARK_GRAY);
                        disconnectButton.setForeground(Color.WHITE);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        // 버튼에서 커서가 나갔을 때의 동작: 버튼 배경색 원래대로
                        disconnectButton.setBackground(UIManager.getColor("Button.background"));
                        disconnectButton.setForeground(UIManager.getColor("Button.foreground"));
                    }
                });


                // Connect 버튼 ActionListener
                connectButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
//                        // 실행 창 숨기기
//                        frame.setVisible(false);

                        // 백그라운드 스레드에서 작업 수행
                        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                            @Override
                            protected Void doInBackground() throws Exception {
//                                String url = "ws://192.168.100.169:8080/websocket";
//                                String url = "ws://192.168.100.169:8090/websocket";
                                String url = "ws://j9d204.p.ssafy.io:8090//websocket";

                                stompClient = new WebSocketStompClient(new StandardWebSocketClient());
                                stompClient.setMessageConverter(new MappingJackson2MessageConverter());

                                channelId = channelIdField.getText();
                                if (channelId.equals("")){
                                    JOptionPane.showMessageDialog(null, "Let's Login!", "Algoarium", JOptionPane.WARNING_MESSAGE);
                                    return null;
                                }
                                sessionHandler = new MySessionHandler(channelId);  // 여기서 세션 핸들러 초기화

                                channelIdField.setVisible(false);
                                disconnectButton.setVisible((true));
                                connectButton.setVisible(false);
//                                connectButton.setEnabled(false);
//                                connectButton.setText("Connected!");
                                titleLabel.setText("Connected!");

                                stompClient.connect(url, sessionHandler, new StompSessionHandlerAdapter() {
                                });
                                return null;
                            }
                        };

                        worker.execute();
                    }
                });

                // Connect 버튼에 대한 MouseListener (hover 효과 추가)
                connectButton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        // 버튼에 커서가 들어왔을 때의 동작: 버튼 배경색 변경
                        connectButton.setBackground(Color.DARK_GRAY);
                        connectButton.setForeground(Color.WHITE);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        // 버튼에서 커서가 나갔을 때의 동작: 버튼 배경색 원래대로
                        connectButton.setBackground(UIManager.getColor("Button.background"));
                        connectButton.setForeground(UIManager.getColor("Button.foreground"));
                    }
                });

                // 백그라운드 이미지 추가
                ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/see.jpg"));
                frame.setContentPane(new JLabel(backgroundIcon));
                frame.setLayout(new FlowLayout());

                frame.setIconImage(starImage); // 프레임 아이콘 설정

                frame.getContentPane().add(panel); // 패널을 프레임에 추가
                frame.pack(); // 크기 자동 조절
                frame.setVisible(true); // 화면에 표시
                frame.setLocationRelativeTo(null); // 화면 중앙에 표시

                // 버튼 폰트 설정
                Font buttonFont = new Font("Arial", Font.BOLD, 18);
                connectButton.setFont(buttonFont);
                exitButton.setFont(buttonFont);

                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        // 창을 닫았을 때의 동작 변경: 아이콘화하고 프레임 숨기기
                        frame.setExtendedState(JFrame.ICONIFIED);
                        frame.setVisible(false);
                    }
                });
            }
        });
    }

    // 애플리케이션 메인 메서드
    public static void main(String[] args) {
        WebSocketClient client = new WebSocketClient(); // WebSocketClient 객체 생성
        client.start(); // 클라이언트 시작
    }
}
