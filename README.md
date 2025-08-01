# 📌 任务管理系统 README.md

## 一、项目简介

任务管理系统（Task Manager）是一个基于 Spring Boot + MyBatis-Plus + JWT 构建的 RESTful API 项目。提供任务管理、用户协作、进度跟踪等功能。系统采用JWT认证，支持多角色权限管理（管理员/普通用户），具有回收站、子任务等特色功能。

## 二、核心功能模块

1. **用户管理**
    - 注册/登录（JWT认证）
    - 密码加密（BCrypt）
    - 角色体系（0-管理员，9-员工）
    - 个人资料修改
2. **任务管理**
    - 任务创建（标题/描述/优先级/开始时间/完成时间/截止时间）
    - 任务分配（管理员分配任务给员工）
    - 状态流转（待办→进行中→已完成）
    - 逻辑删除/恢复（回收站机制）
3. **任务协作**
    - 子任务管理（嵌套任务分解）
    - 评论系统（任务讨论）
    - 通知中心（任务状态变更为"已完成"时通知管理员）
4. **数据统计**
    - 任务评分（任务完成后, 管理员根据任务的完成情况打分）
    - 月度评分（按月汇总各员工的总分）

## 三、项目运行方式

### 1. 技术栈
- Java 17
- 数据库：MySQL 8+
- 依赖管理：Maven
- 后端框架：Spring Boot 2.7+
- 安全认证：JWT (JSON Web Token)
- 数据库 ORM：MyBatis-Plus
- API 文档：Springdoc OpenAPI (Swagger UI)
- 日志记录：SLF4J、Logback
### 2. 数据库初始化

#### 创建数据库：
```sql
CREATE DATABASE task_management_sys DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
```
#### 建表：
```sql
-- users 用户表
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键, 账号',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_deleted` tinyint DEFAULT NULL COMMENT '逻辑删除:0-未删除;1-已删除',
  `updated_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `role` tinyint DEFAULT NULL COMMENT '角色:9-普通用户;0-管理员',
  PRIMARY KEY (`id` DESC),
  UNIQUE KEY `idx_username_delete` (`username`,`is_deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- tasks 任务表
CREATE TABLE `tasks` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(100) NOT NULL COMMENT '任务名称',
  `description` text COMMENT '任务描述',
  `due_date` datetime DEFAULT NULL COMMENT '结束时间',
  `priority` int DEFAULT '0' COMMENT '任务优先级',
  `status` enum('PENDING','IN_PROGRESS','COMPLETED','CANCELED','ARCHIVED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'PENDING' COMMENT '任务状态',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '任务创建时间',
  `updated_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '任务更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_user_status` (`status`) USING BTREE,
  KEY `idx_due_date` (`due_date`) USING BTREE,
  KEY `idx_priority` (`priority`) USING BTREE,
  FULLTEXT KEY `idx_title_description` (`title`,`description`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- user_tasks 用户任务关系表
CREATE TABLE `user_tasks` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `task_id` bigint DEFAULT NULL COMMENT '任务id',
  `user_id` bigint DEFAULT NULL COMMENT '用户id',
  `score` float DEFAULT NULL COMMENT '分数',
  `status` varchar(50) DEFAULT NULL COMMENT '任务状态',
  `completed_time` timestamp NULL DEFAULT NULL COMMENT '完成时间',
  `created_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `updated_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- comments 评论表
CREATE TABLE `comments` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `content` text NOT NULL COMMENT '评论内容',
  `task_id` bigint DEFAULT NULL COMMENT '任务id',
  `user_id` bigint DEFAULT NULL COMMENT '评论人id',
  `created_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_task` (`task_id`) USING BTREE,
  KEY `idx_user` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- sub_tasks 子任务表
CREATE TABLE `sub_tasks` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '子任务标题',
  `status` enum('PENDING','IN_PROGRESS','COMPLETED','CANCELED','ARCHIVED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'PENDING' COMMENT '子任务状态',
  `task_id` bigint NOT NULL COMMENT '父级任务id',
  `due_date` timestamp NULL DEFAULT NULL COMMENT '结束时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除:0-未删除;1-已删除',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_task` (`task_id`) USING BTREE,
  KEY `idx_due_date` (`due_date`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- notifications 通知表
CREATE TABLE `notifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `task_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `is_read` tinyint(1) DEFAULT '0',
  `created_time` timestamp NOT NULL,
  PRIMARY KEY (`id`),
  KEY `task_id` (`task_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`task_id`) REFERENCES `tasks` (`id`),
  CONSTRAINT `notifications_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- user_monthly_scores 用户月度评分表
CREATE TABLE `user_monthly_scores` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `year` int NOT NULL,
  `month` int NOT NULL,
  `total_score` float DEFAULT '0',
  `completed_count` int DEFAULT '0',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_year_month` (`user_id`,`year`,`month`),
  CONSTRAINT `user_monthly_scores_chk_1` CHECK ((`month` between 1 and 12))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```
## 三、接口说明 (Swagger 文档)
```text
http://localhost:8080/swagger-ui.html
```