CREATE DATABASE `test` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;

-- test.auth_group definition

CREATE TABLE `auth_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(150) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- test.auth_user definition

CREATE TABLE `auth_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `password` varchar(128) NOT NULL,
  `last_login` datetime(6) DEFAULT NULL,
  `is_superuser` tinyint(1) NOT NULL,
  `username` varchar(150) NOT NULL,
  `first_name` varchar(150) NOT NULL,
  `last_name` varchar(150) NOT NULL,
  `email` varchar(254) NOT NULL,
  `is_staff` tinyint(1) NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `date_joined` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- test.baekjoon_user definition

CREATE TABLE `baekjoon_user` (
  `bj_user_id` int(11) NOT NULL AUTO_INCREMENT,
  `bj_class` int(11) DEFAULT NULL,
  `bj_id` varchar(100) NOT NULL,
  `rank` int(11) DEFAULT NULL,
  `rating` int(11) DEFAULT NULL,
  `rating_by_class` int(11) DEFAULT NULL,
  `rating_by_problem_sum` int(11) DEFAULT NULL,
  `rating_by_solved_count` int(11) DEFAULT NULL,
  `solved_count` int(11) DEFAULT NULL,
  `tier` int(11) DEFAULT NULL,
  PRIMARY KEY (`bj_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- test.django_content_type definition

CREATE TABLE `django_content_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `app_label` varchar(100) NOT NULL,
  `model` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `django_content_type_app_label_model_76bd3d3b_uniq` (`app_label`,`model`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- test.django_migrations definition

CREATE TABLE `django_migrations` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `app` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `applied` datetime(6) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- test.problem definition

CREATE TABLE `problem` (
  `problem_id` int(11) NOT NULL AUTO_INCREMENT,
  `problem_level` int(11) NOT NULL,
  `problem_number` int(11) NOT NULL,
  `problem_tag` varchar(1000) NOT NULL,
  `solved_user_count` int(11) NOT NULL,
  `title` varchar(1000) NOT NULL,
  PRIMARY KEY (`problem_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8684 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- test.season definition

CREATE TABLE `season` (
  `season_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `promotion_season` bit(1) NOT NULL,
  PRIMARY KEY (`season_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- test.tag definition

CREATE TABLE `tag` (
  `tag_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tag_name` varchar(100) NOT NULL,
  PRIMARY KEY (`tag_id`)
) ENGINE=InnoDB AUTO_INCREMENT=301 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- test.auth_permission definition

CREATE TABLE `auth_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `content_type_id` int(11) NOT NULL,
  `codename` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `auth_permission_content_type_id_codename_01ab375a_uniq` (`content_type_id`,`codename`),
  CONSTRAINT `auth_permission_content_type_id_2f476e4b_fk_django_co` FOREIGN KEY (`content_type_id`) REFERENCES `django_content_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=73 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- test.auth_user_groups definition

CREATE TABLE `auth_user_groups` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `group_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `auth_user_groups_user_id_group_id_94350c0c_uniq` (`user_id`,`group_id`),
  KEY `auth_user_groups_group_id_97559544_fk_auth_group_id` (`group_id`),
  CONSTRAINT `auth_user_groups_group_id_97559544_fk_auth_group_id` FOREIGN KEY (`group_id`) REFERENCES `auth_group` (`id`),
  CONSTRAINT `auth_user_groups_user_id_6a12ed8b_fk_auth_user_id` FOREIGN KEY (`user_id`) REFERENCES `auth_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- test.auth_user_user_permissions definition

CREATE TABLE `auth_user_user_permissions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `permission_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `auth_user_user_permissions_user_id_permission_id_14a6b632_uniq` (`user_id`,`permission_id`),
  KEY `auth_user_user_permi_permission_id_1fbb5f2c_fk_auth_perm` (`permission_id`),
  CONSTRAINT `auth_user_user_permi_permission_id_1fbb5f2c_fk_auth_perm` FOREIGN KEY (`permission_id`) REFERENCES `auth_permission` (`id`),
  CONSTRAINT `auth_user_user_permissions_user_id_a95ead1b_fk_auth_user_id` FOREIGN KEY (`user_id`) REFERENCES `auth_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- test.django_admin_log definition

CREATE TABLE `django_admin_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `action_time` datetime(6) NOT NULL,
  `object_id` longtext DEFAULT NULL,
  `object_repr` varchar(200) NOT NULL,
  `action_flag` smallint(5) unsigned NOT NULL CHECK (`action_flag` >= 0),
  `change_message` longtext NOT NULL,
  `content_type_id` int(11) DEFAULT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `django_admin_log_content_type_id_c4bce8eb_fk_django_co` (`content_type_id`),
  KEY `django_admin_log_user_id_c564eba6_fk_auth_user_id` (`user_id`),
  CONSTRAINT `django_admin_log_content_type_id_c4bce8eb_fk_django_co` FOREIGN KEY (`content_type_id`) REFERENCES `django_content_type` (`id`),
  CONSTRAINT `django_admin_log_user_id_c564eba6_fk_auth_user_id` FOREIGN KEY (`user_id`) REFERENCES `auth_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- test.problem_tag definition

CREATE TABLE `problem_tag` (
  `problem_tag_id` int(11) NOT NULL AUTO_INCREMENT,
  `problem_id` int(11) DEFAULT NULL,
  `tag_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`problem_tag_id`),
  KEY `FKsbd0j3yjggts3lk19llwm0lwq` (`problem_id`),
  KEY `FKbme6cvwwregomxxbj1vb7f3nu` (`tag_id`),
  CONSTRAINT `FKbme6cvwwregomxxbj1vb7f3nu` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`tag_id`),
  CONSTRAINT `FKsbd0j3yjggts3lk19llwm0lwq` FOREIGN KEY (`problem_id`) REFERENCES `problem` (`problem_id`)
) ENGINE=InnoDB AUTO_INCREMENT=22711 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- test.tag_correlation definition

CREATE TABLE `tag_correlation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `jaccard_correlation` double NOT NULL,
  `consine_correlation` double NOT NULL,
  `tag1_id` bigint(20) NOT NULL,
  `tag2_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `tag_correlation_tag1_id_c4065d4c_fk_tag_tag_id` (`tag1_id`),
  KEY `tag_correlation_tag2_id_0b8973df_fk_tag_tag_id` (`tag2_id`),
  CONSTRAINT `tag_correlation_tag1_id_c4065d4c_fk_tag_tag_id` FOREIGN KEY (`tag1_id`) REFERENCES `tag` (`tag_id`),
  CONSTRAINT `tag_correlation_tag2_id_0b8973df_fk_tag_tag_id` FOREIGN KEY (`tag2_id`) REFERENCES `tag` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- test.auth_group_permissions definition

CREATE TABLE `auth_group_permissions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) NOT NULL,
  `permission_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `auth_group_permissions_group_id_permission_id_0cd325b0_uniq` (`group_id`,`permission_id`),
  KEY `auth_group_permissio_permission_id_84c5c92e_fk_auth_perm` (`permission_id`),
  CONSTRAINT `auth_group_permissio_permission_id_84c5c92e_fk_auth_perm` FOREIGN KEY (`permission_id`) REFERENCES `auth_permission` (`id`),
  CONSTRAINT `auth_group_permissions_group_id_b120cbf9_fk_auth_group_id` FOREIGN KEY (`group_id`) REFERENCES `auth_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- test.problem_like definition

CREATE TABLE `problem_like` (
  `problem_like_id` int(11) NOT NULL AUTO_INCREMENT,
  `like_type` bit(1) DEFAULT NULL,
  `memo` mediumtext DEFAULT NULL,
  `problem_id` int(11) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`problem_like_id`),
  KEY `FKsi1vhio5c85snmv46e3o6h1f3` (`problem_id`),
  KEY `FKq5099ofgaialh10ceuuntivkp` (`user_id`),
  CONSTRAINT `FKq5099ofgaialh10ceuuntivkp` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FKsi1vhio5c85snmv46e3o6h1f3` FOREIGN KEY (`problem_id`) REFERENCES `problem` (`problem_id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- test.recommend_problem definition

CREATE TABLE `recommend_problem` (
  `recommend_problem_id` int(11) NOT NULL AUTO_INCREMENT,
  `type` int(11) DEFAULT NULL,
  `problem_id` int(11) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`recommend_problem_id`),
  KEY `FKeot6904nhj2mi65sc132ax9nc` (`problem_id`),
  KEY `FK3g2o91lwyxnru2399vfxvu79c` (`user_id`),
  CONSTRAINT `FK3g2o91lwyxnru2399vfxvu79c` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FKeot6904nhj2mi65sc132ax9nc` FOREIGN KEY (`problem_id`) REFERENCES `problem` (`problem_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2183 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- test.solved_problem_history definition

CREATE TABLE `solved_problem_history` (
  `solved_problem_history_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `solved_or_not` bit(1) DEFAULT NULL,
  `problem_id` int(11) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`solved_problem_history_id`),
  KEY `FK6nxr7h7sex84o6y94xxdtbw18` (`problem_id`),
  KEY `FKiyc4sf1hfbaf42mxcknbbb66c` (`user_id`),
  CONSTRAINT `FK6nxr7h7sex84o6y94xxdtbw18` FOREIGN KEY (`problem_id`) REFERENCES `problem` (`problem_id`),
  CONSTRAINT `FKiyc4sf1hfbaf42mxcknbbb66c` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=772 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- test.`user` definition

CREATE TABLE `user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `kakao_id` varchar(100) NOT NULL,
  `kakao_nickname` varchar(50) NOT NULL,
  `pre_tier` int(11) NOT NULL,
  `profile_image` varchar(500) DEFAULT NULL,
  `refresh_token` varchar(100) NOT NULL,
  `solved_ac_id` varchar(100) DEFAULT NULL,
  `user_ranking_id` bigint(20) DEFAULT NULL,
  `user_status_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UK_38k1yox9t88inwt420tybv18j` (`user_ranking_id`),
  UNIQUE KEY `UK_7kscrsm1ldm3nfa6f6d1a1wp` (`user_status_id`),
  CONSTRAINT `FKo6g0t5ih8a5bsioca8qh5ukg3` FOREIGN KEY (`user_status_id`) REFERENCES `user_status` (`user_status_id`),
  CONSTRAINT `FKsc8noo8amkydk0eop7s3mcymf` FOREIGN KEY (`user_ranking_id`) REFERENCES `user_ranking` (`user_ranking_id`)
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- test.user_ranking definition

CREATE TABLE `user_ranking` (
  `user_ranking_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ranking` int(11) NOT NULL,
  `score` int(11) NOT NULL,
  `tier` int(11) NOT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`user_ranking_id`),
  UNIQUE KEY `UK_dv59hivbq6oh5xyh3uhe4e9m6` (`user_id`),
  CONSTRAINT `FK6fpvqitc43hh6tg37191tc9uk` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- test.user_status definition

CREATE TABLE `user_status` (
  `user_status_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `wis` int(11) NOT NULL,
  `con` int(11) NOT NULL,
  `str` int(11) NOT NULL,
  `luk` int(11) NOT NULL,
  `sma` int(11) NOT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`user_status_id`),
  UNIQUE KEY `UK_9onp4jxeo4eyc7i5124wp38me` (`user_id`),
  CONSTRAINT `FKfs3p2l9uw8wycw403qpr5la1l` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;