package com.d204.algo.ui.oauth

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.d204.algo.MainActivity
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient

class KaKaoApi(private val act: AppCompatActivity) {
    private fun skipLogin() {
        if(AuthApiClient.instance.hasToken()) {
            try {
                UserApiClient.instance.accessTokenInfo { token, _ ->
                    val intent = Intent(act, MainActivity::class.java)
                    intent.putExtra("kakaoToken", token)
                    act.startActivity(intent)
                    act.finish()
                }
            } catch (e: Error) {
                Toast.makeText(act, "카카오 토큰 발급 중 오류 발생", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun setLoginBtn(loginBtn : ImageButton) {
        skipLogin()
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Toast.makeText(act, "카카오 계정으로 로그인 실패", Toast.LENGTH_SHORT).show()
            } else if (token != null) {
                val intent = Intent(act, MainActivity::class.java)
                act.startActivity(intent)
                act.finish()
            }
        }

        loginBtn.setOnClickListener {
            // kakao 실행 가능 여부
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(act)) {
                // 가능하다면 카카오톡으로 로그인하기
                UserApiClient.instance.loginWithKakaoTalk(act) { token, error ->
                    if (error != null) {
                        Toast.makeText(act, "카카오톡으로 로그인 실패", Toast.LENGTH_SHORT).show()
                        Log.e("test", "카카오톡으로 로그인 실패 " + error.message)

                        // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                        // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }


                    } else if (token != null) {
                        Toast.makeText(act, "카카오톡으로 로그인 성공", Toast.LENGTH_SHORT).show()
                        Log.d("test", "카카오톡으로 로그인 성공" + token.accessToken)

                        val intent = Intent(act, MainActivity::class.java)
                        intent.putExtra("kakaoToken",token)
                        act.startActivity(intent)
                        act.finish()
                    }

                    // kakao로 로그인 하지 못 할 경우 계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(act, callback = callback)
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(act, callback = callback)
            }
        }
    }
}