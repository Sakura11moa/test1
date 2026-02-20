/*
 Navicat Premium Data Transfer

 Source Server         : htk
 Source Server Type    : MySQL
 Source Server Version : 80300 (8.3.0)
 Source Host           : localhost:3306
 Source Schema         : db_gym

 Target Server Type    : MySQL
 Target Server Version : 80300 (8.3.0)
 File Encoding         : 65001

 Date: 13/02/2026 22:56:20
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `adminNo` int NOT NULL AUTO_INCREMENT,
  `adminAccount` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `adminPassword` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `employee_no` int NULL DEFAULT NULL COMMENT '关联员工编号',
  `token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`adminNo`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, 'admin', 'admin', NULL, '');
INSERT INTO `admin` VALUES (2, 'coach1', '123456', 1, NULL);
INSERT INTO `admin` VALUES (3, 'coach2', '123456', 4, NULL);
INSERT INTO `admin` VALUES (4, 'coach3', '123456', 5, NULL);

-- ----------------------------
-- Table structure for balance_snapshot
-- ----------------------------
DROP TABLE IF EXISTS `balance_snapshot`;
CREATE TABLE `balance_snapshot`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `snapshot_date` date NOT NULL COMMENT '快照日期',
  `balance_amount` decimal(12, 2) NOT NULL DEFAULT 0.00 COMMENT '储值余额',
  `card_deferred_amount` decimal(12, 2) NOT NULL DEFAULT 0.00 COMMENT '会籍预收金额',
  `course_deferred_amount` decimal(12, 2) NOT NULL DEFAULT 0.00 COMMENT '课程预收金额',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_date`(`snapshot_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '财务余额快照表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of balance_snapshot
-- ----------------------------

-- ----------------------------
-- Table structure for booking_audit_log
-- ----------------------------
DROP TABLE IF EXISTS `booking_audit_log`;
CREATE TABLE `booking_audit_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `booking_no` int NOT NULL,
  `old_status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `new_status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `operator_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'ADMIN' COMMENT '操作人类型：ADMIN/COACH',
  `operator_id` int NULL DEFAULT NULL COMMENT '操作人ID（对应adminNo或employeeNo）',
  `action` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'APPROVE/REJECT/CANCEL',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_booking_no`(`booking_no` ASC) USING BTREE,
  INDEX `idx_admin_id`(`operator_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 98 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of booking_audit_log
-- ----------------------------
INSERT INTO `booking_audit_log` VALUES (79, 53, '0', '1', 'ADMIN', 1, 'APPROVE', NULL, '2026-02-12 18:50:47');
INSERT INTO `booking_audit_log` VALUES (80, 53, '1', '6', 'COACH', 1, 'FINISH', NULL, '2026-02-12 18:50:52');
INSERT INTO `booking_audit_log` VALUES (81, 55, '0', '1', 'COACH', 1, 'APPROVE', NULL, '2026-02-12 18:52:17');
INSERT INTO `booking_audit_log` VALUES (82, 54, '0', '1', 'COACH', 1, 'APPROVE', NULL, '2026-02-12 18:52:18');
INSERT INTO `booking_audit_log` VALUES (83, 55, '1', '6', 'COACH', 1, 'FINISH', NULL, '2026-02-12 18:52:20');
INSERT INTO `booking_audit_log` VALUES (84, 56, '0', '1', 'COACH', 1, 'APPROVE', NULL, '2026-02-12 18:54:31');
INSERT INTO `booking_audit_log` VALUES (85, 56, '1', '6', 'COACH', 1, 'FINISH', NULL, '2026-02-12 18:54:33');
INSERT INTO `booking_audit_log` VALUES (86, 54, '1', '6', 'COACH', 1, 'FINISH', NULL, '2026-02-12 19:06:26');
INSERT INTO `booking_audit_log` VALUES (87, 57, '0', '2', 'COACH', 1, 'REJECT', '1', '2026-02-12 20:09:36');
INSERT INTO `booking_audit_log` VALUES (88, 58, '0', '1', 'COACH', 1, 'APPROVE', NULL, '2026-02-13 14:41:24');
INSERT INTO `booking_audit_log` VALUES (89, 58, '1', '6', 'COACH', 1, 'FINISH', NULL, '2026-02-13 14:41:26');
INSERT INTO `booking_audit_log` VALUES (90, 59, '0', '1', 'COACH', 1, 'APPROVE', NULL, '2026-02-13 21:30:21');
INSERT INTO `booking_audit_log` VALUES (91, 59, '1', '6', 'COACH', 1, 'FINISH', NULL, '2026-02-13 21:31:00');
INSERT INTO `booking_audit_log` VALUES (92, 60, '0', '1', 'COACH', 5, 'APPROVE', NULL, '2026-02-13 21:40:37');
INSERT INTO `booking_audit_log` VALUES (93, 61, '0', '1', 'COACH', 1, 'APPROVE', NULL, '2026-02-13 21:45:37');
INSERT INTO `booking_audit_log` VALUES (94, 61, '1', '6', 'COACH', 1, 'FINISH', NULL, '2026-02-13 21:45:38');
INSERT INTO `booking_audit_log` VALUES (95, 62, '0', '1', 'COACH', 1, 'APPROVE', NULL, '2026-02-13 21:46:21');
INSERT INTO `booking_audit_log` VALUES (96, 62, '1', '6', 'COACH', 1, 'FINISH', NULL, '2026-02-13 21:46:23');
INSERT INTO `booking_audit_log` VALUES (97, 63, '0', '1', 'COACH', 5, 'APPROVE', NULL, '2026-02-13 22:16:05');

-- ----------------------------
-- Table structure for checkin
-- ----------------------------
DROP TABLE IF EXISTS `checkin`;
CREATE TABLE `checkin`  (
  `checkNo` int NOT NULL AUTO_INCREMENT,
  `memberNo` int NULL DEFAULT NULL,
  `checkDate` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`checkNo`) USING BTREE,
  INDEX `memberNo`(`memberNo` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of checkin
-- ----------------------------
INSERT INTO `checkin` VALUES (1, 1, '2023-06-20 00:00:00');
INSERT INTO `checkin` VALUES (2, NULL, '2023-06-29 00:00:00');
INSERT INTO `checkin` VALUES (3, 6, '2026-02-10 00:00:00');

-- ----------------------------
-- Table structure for course
-- ----------------------------
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course`  (
  `courseNo` int NOT NULL AUTO_INCREMENT,
  `courseName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `courseTime` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `courseDuration` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `coursePrice` decimal(10, 2) NULL DEFAULT NULL,
  `courseDesc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `courseIntegral` int NULL DEFAULT NULL,
  `venueNo` int NULL DEFAULT NULL COMMENT '上课场地编号，对应 venue.venue_no',
  `employeeNo` int NULL DEFAULT NULL COMMENT '教练员工编号',
  `managerNo` int NULL DEFAULT NULL COMMENT '经理编号',
  PRIMARY KEY (`courseNo`) USING BTREE,
  INDEX `idx_venueNo`(`venueNo` ASC) USING BTREE,
  INDEX `employeeNo`(`employeeNo` ASC) USING BTREE,
  INDEX `managerNo`(`managerNo` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of course
-- ----------------------------
INSERT INTO `course` VALUES (1, '基础瑜伽', '2024-07-28 09:00:00', '60', 100.00, '基础瑜伽课程，适合初学者，场地：瑜伽教室A', 200, 2, 1, 2);
INSERT INTO `course` VALUES (2, '流瑜伽', '2024-07-28 19:00:00', '60', 120.00, '流瑜伽课程，提升柔韧与力量，场地：瑜伽教室B', 220, 3, 5, 2);
INSERT INTO `course` VALUES (3, '自由泳基础', '2024-07-23 16:00:00', '60', 150.00, '游泳基础课程，适合零基础，场地：室内游泳池', 200, 4, 1, 2);
INSERT INTO `course` VALUES (4, '跑步机有氧', '2024-07-30 16:00:00', '45', 80.00, '跑步机有氧训练，燃脂塑形，场地：主健身房', 100, 1, 1, 2);
INSERT INTO `course` VALUES (5, '哑铃力量训练', '2024-07-29 16:00:00', '60', 120.00, '全身力量训练课程，哑铃为主，场地：主健身房', 180, 1, 1, 2);
INSERT INTO `course` VALUES (6, '篮球基础训练', '2024-07-31 18:00:00', '90', 160.00, '篮球基础运球与投篮训练，场地：篮球场A', 220, 5, 1, 2);
INSERT INTO `course` VALUES (7, '篮球进阶实战', '2024-08-01 18:00:00', '90', 180.00, '对抗与战术训练，场地：篮球场B', 260, 6, 1, 2);
INSERT INTO `course` VALUES (8, '羽毛球初级班', '2024-08-02 19:00:00', '60', 120.00, '基础步伐与发接球训练，场地：羽毛球场1号', 180, 7, 1, 2);
INSERT INTO `course` VALUES (9, '羽毛球双打班', '2024-08-03 19:00:00', '60', 140.00, '双打配合与战术训练，场地：羽毛球场2号', 200, 8, 1, 2);
INSERT INTO `course` VALUES (10, '有氧操课', '2024-08-04 19:30:00', '45', 90.00, '音乐律动有氧操，适合所有人群，场地：有氧操房', 150, 10, 4, 2);

-- ----------------------------
-- Table structure for course_evaluation
-- ----------------------------
DROP TABLE IF EXISTS `course_evaluation`;
CREATE TABLE `course_evaluation`  (
  `eval_no` bigint NOT NULL AUTO_INCREMENT,
  `booking_no` int NOT NULL COMMENT '关联预约单号',
  `member_no` int NOT NULL,
  `course_no` int NOT NULL,
  `score` int NOT NULL COMMENT '评分 1-5',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '评价内容',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`eval_no`) USING BTREE,
  UNIQUE INDEX `uk_booking`(`booking_no` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_evaluation
-- ----------------------------

-- ----------------------------
-- Table structure for course_purchase
-- ----------------------------
DROP TABLE IF EXISTS `course_purchase`;
CREATE TABLE `course_purchase`  (
  `purchase_no` bigint NOT NULL AUTO_INCREMENT,
  `member_no` int NOT NULL,
  `course_no` int NOT NULL,
  `quantity` int NOT NULL,
  `unit_price` decimal(10, 2) NULL DEFAULT NULL,
  `total_amount` decimal(10, 2) NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'PAID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`purchase_no`) USING BTREE,
  INDEX `idx_member_time`(`member_no` ASC, `create_time` ASC) USING BTREE,
  INDEX `idx_course_time`(`course_no` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 45 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_purchase
-- ----------------------------
INSERT INTO `course_purchase` VALUES (36, 6, 1, 1, 100.00, 100.00, 'PAID', '2026-02-12 18:46:37');
INSERT INTO `course_purchase` VALUES (37, 6, 1, 3, 100.00, 300.00, 'PAID', '2026-02-12 18:50:33');
INSERT INTO `course_purchase` VALUES (38, 6, 7, 1, 180.00, 180.00, 'PAID', '2026-02-12 18:54:20');
INSERT INTO `course_purchase` VALUES (39, 6, 1, 1, 100.00, 100.00, 'PAID', '2026-02-12 19:00:24');
INSERT INTO `course_purchase` VALUES (40, 6, 1, 1, 100.00, 100.00, 'PAID', '2026-02-13 21:29:43');
INSERT INTO `course_purchase` VALUES (41, 6, 2, 1, 120.00, 120.00, 'PAID', '2026-02-13 21:40:11');
INSERT INTO `course_purchase` VALUES (42, 6, 1, 1, 100.00, 100.00, 'PAID', '2026-02-13 21:44:58');
INSERT INTO `course_purchase` VALUES (43, 6, 1, 1, 100.00, 100.00, 'PAID', '2026-02-13 21:46:03');
INSERT INTO `course_purchase` VALUES (44, 6, 2, 1, 120.00, 120.00, 'PAID', '2026-02-13 22:08:31');

-- ----------------------------
-- Table structure for course_refund
-- ----------------------------
DROP TABLE IF EXISTS `course_refund`;
CREATE TABLE `course_refund`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `member_no` int NOT NULL,
  `course_no` int NOT NULL,
  `deferred_id` bigint NOT NULL,
  `original_purchase_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '原购课单号',
  `refund_times` int NOT NULL,
  `refund_amount` decimal(10, 2) NOT NULL,
  `refund_channel` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `refund_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '退课原因',
  `operator_id` int NULL DEFAULT NULL COMMENT '操作人ID',
  `status` tinyint NULL DEFAULT 1,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_original_purchase_no`(`original_purchase_no` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_refund
-- ----------------------------

-- ----------------------------
-- Table structure for course_stock
-- ----------------------------
DROP TABLE IF EXISTS `course_stock`;
CREATE TABLE `course_stock`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `member_no` int NOT NULL,
  `course_no` int NOT NULL,
  `total_times` int NOT NULL DEFAULT 0,
  `remain_times` int NOT NULL DEFAULT 0,
  `version` int NOT NULL DEFAULT 0,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_member_course`(`member_no` ASC, `course_no` ASC) USING BTREE,
  INDEX `idx_course`(`course_no` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_stock
-- ----------------------------
INSERT INTO `course_stock` VALUES (16, 6, 1, 7, 0, 11, '2026-02-13 21:46:21');
INSERT INTO `course_stock` VALUES (17, 6, 7, 1, 0, 1, '2026-02-12 18:54:31');
INSERT INTO `course_stock` VALUES (18, 6, 2, 2, 0, 3, '2026-02-13 22:16:05');

-- ----------------------------
-- Table structure for course_stock_log
-- ----------------------------
DROP TABLE IF EXISTS `course_stock_log`;
CREATE TABLE `course_stock_log`  (
  `log_no` bigint NOT NULL AUTO_INCREMENT,
  `member_no` int NOT NULL,
  `course_no` int NOT NULL,
  `change_times` int NOT NULL,
  `after_remain` int NOT NULL,
  `biz_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `biz_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`log_no`) USING BTREE,
  INDEX `idx_member_course_time`(`member_no` ASC, `course_no` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 74 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_stock_log
-- ----------------------------
INSERT INTO `course_stock_log` VALUES (1, 6, 3, -1, 9, 'BOOK', '3', '预约审批通过扣减', '2026-02-08 19:07:26');
INSERT INTO `course_stock_log` VALUES (2, 6, 6, -1, 9, 'BOOK', '6', '预约审批通过扣减', '2026-02-08 23:51:56');
INSERT INTO `course_stock_log` VALUES (3, 6, 5, -1, 9, 'BOOK', '5', '预约审批通过扣减', '2026-02-08 23:52:00');
INSERT INTO `course_stock_log` VALUES (4, 6, 1, -1, 9, 'BOOK', '9', '预约审批通过扣减', '2026-02-08 23:59:52');
INSERT INTO `course_stock_log` VALUES (5, 6, 1, -1, 8, 'BOOK', '14', '预约审批通过扣减', '2026-02-09 14:06:12');
INSERT INTO `course_stock_log` VALUES (6, 7, 1, 1, 1, 'PURCHASE', '18', '购买课程，增加1次', '2026-02-09 14:27:46');
INSERT INTO `course_stock_log` VALUES (7, 6, 2, -1, 9, 'BOOK', '11', '教练审批通过扣减', '2026-02-10 15:02:47');
INSERT INTO `course_stock_log` VALUES (8, 6, 1, -1, 7, 'BOOK', '8', '教练审批通过扣减', '2026-02-10 15:03:28');
INSERT INTO `course_stock_log` VALUES (9, 6, 3, -1, 8, 'BOOK', '15', '教练审批通过扣减', '2026-02-10 15:25:11');
INSERT INTO `course_stock_log` VALUES (10, 6, 3, -1, 7, 'BOOK', '16', '教练审批通过扣减', '2026-02-10 15:25:39');
INSERT INTO `course_stock_log` VALUES (11, 6, 3, -1, 6, 'BOOK', '17', '教练审批通过扣减', '2026-02-10 15:25:59');
INSERT INTO `course_stock_log` VALUES (12, 6, 3, -1, 5, 'BOOK', '18', '教练审批通过扣减', '2026-02-10 15:26:43');
INSERT INTO `course_stock_log` VALUES (13, 6, 3, -1, 4, 'BOOK', '19', '教练审批通过扣减', '2026-02-10 15:28:35');
INSERT INTO `course_stock_log` VALUES (14, 6, 3, -1, 3, 'BOOK', '21', '教练审批通过扣减', '2026-02-10 15:31:43');
INSERT INTO `course_stock_log` VALUES (15, 6, 3, -1, 2, 'BOOK', '22', '教练审批通过扣减', '2026-02-10 15:34:21');
INSERT INTO `course_stock_log` VALUES (16, 6, 4, -1, 9, 'BOOK', '26', '教练审批通过扣减', '2026-02-10 15:44:31');
INSERT INTO `course_stock_log` VALUES (17, 6, 4, -1, 8, 'BOOK', '24', '教练审批通过扣减', '2026-02-10 15:44:39');
INSERT INTO `course_stock_log` VALUES (18, 6, 4, -1, 7, 'BOOK', '23', '管理员审批通过扣减', '2026-02-10 15:54:02');
INSERT INTO `course_stock_log` VALUES (19, 6, 10, -1, 9, 'BOOK', '29', '教练审批通过扣减', '2026-02-10 16:36:19');
INSERT INTO `course_stock_log` VALUES (20, 6, 10, -1, 8, 'BOOK', '27', '教练审批通过扣减', '2026-02-10 16:36:27');
INSERT INTO `course_stock_log` VALUES (21, 6, 10, -1, 7, 'BOOK', '34', '教练审批通过扣减', '2026-02-10 16:49:54');
INSERT INTO `course_stock_log` VALUES (22, 6, 10, -1, 6, 'BOOK', '35', '教练审批通过扣减', '2026-02-10 16:50:36');
INSERT INTO `course_stock_log` VALUES (23, 6, 2, -1, 8, 'BOOK', '32', '管理员审批通过扣减', '2026-02-10 17:00:30');
INSERT INTO `course_stock_log` VALUES (24, 6, 1, 1, 8, 'PURCHASE', '19', '购买课程，增加1次', '2026-02-10 17:39:41');
INSERT INTO `course_stock_log` VALUES (25, 6, 1, 2, 10, 'PURCHASE', '20', '购买课程，增加2次', '2026-02-10 17:44:31');
INSERT INTO `course_stock_log` VALUES (26, 6, 1, 2, 12, 'PURCHASE', '21', '购买课程，增加2次', '2026-02-10 17:45:07');
INSERT INTO `course_stock_log` VALUES (27, 6, 1, 2, 14, 'PURCHASE', '22', '购买课程，增加2次', '2026-02-10 17:45:14');
INSERT INTO `course_stock_log` VALUES (28, 6, 2, -1, 7, 'BOOK', '31', '教练审批通过扣减', '2026-02-10 19:56:17');
INSERT INTO `course_stock_log` VALUES (29, 6, 1, 1, 15, 'PURCHASE', '23', '购买课程，增加1次', '2026-02-11 00:30:47');
INSERT INTO `course_stock_log` VALUES (30, 6, 1, 2, 17, 'PURCHASE', '24', '购买课程，增加2次', '2026-02-12 13:16:25');
INSERT INTO `course_stock_log` VALUES (31, 6, 1, 1, 18, 'PURCHASE', '25', '购买课程，增加1次', '2026-02-12 16:03:43');
INSERT INTO `course_stock_log` VALUES (32, 6, 1, 1, 19, 'PURCHASE', '26', '购买课程，增加1次', '2026-02-12 16:05:17');
INSERT INTO `course_stock_log` VALUES (33, 6, 4, -1, 6, 'BOOK', '41', '管理员审批通过扣减', '2026-02-12 16:07:27');
INSERT INTO `course_stock_log` VALUES (34, 6, 1, 1, 20, 'PURCHASE', '27', '购买课程，增加1次', '2026-02-12 16:24:12');
INSERT INTO `course_stock_log` VALUES (35, 6, 1, 1, 21, 'PURCHASE', '28', '购买课程，增加1次', '2026-02-12 16:24:28');
INSERT INTO `course_stock_log` VALUES (36, 6, 4, -1, 5, 'BOOK', '38', '教练审批通过扣减', '2026-02-12 17:31:22');
INSERT INTO `course_stock_log` VALUES (37, 6, 1, -1, 20, 'BOOK', '42', '教练审批通过扣减', '2026-02-12 17:43:06');
INSERT INTO `course_stock_log` VALUES (38, 6, 2, 1, 8, 'PURCHASE', '29', '购买课程，增加1次', '2026-02-12 17:44:23');
INSERT INTO `course_stock_log` VALUES (39, 6, 2, -1, 7, 'BOOK', '45', '教练审批通过扣减', '2026-02-12 17:45:00');
INSERT INTO `course_stock_log` VALUES (40, 6, 1, 1, 21, 'PURCHASE', '30', '购买课程，增加1次', '2026-02-12 17:46:28');
INSERT INTO `course_stock_log` VALUES (41, 6, 2, -1, 6, 'BOOK', '37', '教练审批通过扣减', '2026-02-12 18:07:22');
INSERT INTO `course_stock_log` VALUES (42, 6, 1, 1, 22, 'PURCHASE', '31', '购买课程，增加1次', '2026-02-12 18:16:28');
INSERT INTO `course_stock_log` VALUES (43, 6, 6, 1, 10, 'PURCHASE', '32', '购买课程，增加1次', '2026-02-12 18:16:37');
INSERT INTO `course_stock_log` VALUES (44, 6, 1, -1, 21, 'BOOK', '46', '教练审批通过扣减', '2026-02-12 18:17:43');
INSERT INTO `course_stock_log` VALUES (45, 6, 1, -1, 20, 'BOOK', '47', '教练审批通过扣减', '2026-02-12 18:19:06');
INSERT INTO `course_stock_log` VALUES (46, 6, 6, -1, 9, 'BOOK', '48', '教练审批通过扣减', '2026-02-12 18:26:43');
INSERT INTO `course_stock_log` VALUES (47, 6, 6, -1, 8, 'BOOK', '49', '教练审批通过扣减', '2026-02-12 18:34:11');
INSERT INTO `course_stock_log` VALUES (48, 6, 2, -1, 5, 'BOOK', '39', '教练审批通过扣减', '2026-02-12 18:34:34');
INSERT INTO `course_stock_log` VALUES (49, 6, 3, 1, 3, 'PURCHASE', '33', '购买课程，增加1次', '2026-02-12 18:35:07');
INSERT INTO `course_stock_log` VALUES (50, 6, 3, -1, 2, 'BOOK', '50', '教练审批通过扣减', '2026-02-12 18:35:42');
INSERT INTO `course_stock_log` VALUES (51, 6, 10, -1, 5, 'BOOK', '51', '教练审批通过扣减', '2026-02-12 18:37:44');
INSERT INTO `course_stock_log` VALUES (52, 6, 10, 1, 6, 'PURCHASE', '34', '购买课程，增加1次', '2026-02-12 18:38:43');
INSERT INTO `course_stock_log` VALUES (53, 6, 10, 1, 7, 'PURCHASE', '35', '购买课程，增加1次', '2026-02-12 18:38:52');
INSERT INTO `course_stock_log` VALUES (54, 6, 10, -1, 6, 'BOOK', '52', '教练审批通过扣减', '2026-02-12 18:39:06');
INSERT INTO `course_stock_log` VALUES (55, 6, 1, 1, 1, 'PURCHASE', '36', '购买课程，增加1次', '2026-02-12 18:46:36');
INSERT INTO `course_stock_log` VALUES (56, 6, 1, 3, 3, 'PURCHASE', '37', '购买课程，增加3次', '2026-02-12 18:50:33');
INSERT INTO `course_stock_log` VALUES (57, 6, 1, -1, 2, 'BOOK', '53', '管理员审批通过扣减', '2026-02-12 18:50:47');
INSERT INTO `course_stock_log` VALUES (58, 6, 1, -1, 1, 'BOOK', '55', '教练审批通过扣减', '2026-02-12 18:52:17');
INSERT INTO `course_stock_log` VALUES (59, 6, 1, -1, 0, 'BOOK', '54', '教练审批通过扣减', '2026-02-12 18:52:18');
INSERT INTO `course_stock_log` VALUES (60, 6, 7, 1, 1, 'PURCHASE', '38', '购买课程，增加1次', '2026-02-12 18:54:20');
INSERT INTO `course_stock_log` VALUES (61, 6, 7, -1, 0, 'BOOK', '56', '教练审批通过扣减', '2026-02-12 18:54:31');
INSERT INTO `course_stock_log` VALUES (62, 6, 1, 1, 1, 'PURCHASE', '39', '购买课程，增加1次', '2026-02-12 19:00:23');
INSERT INTO `course_stock_log` VALUES (63, 6, 1, -1, 0, 'BOOK', '58', '教练审批通过扣减', '2026-02-13 14:41:24');
INSERT INTO `course_stock_log` VALUES (64, 6, 1, 1, 1, 'PURCHASE', '40', '购买课程，增加1次', '2026-02-13 21:29:42');
INSERT INTO `course_stock_log` VALUES (65, 6, 1, -1, 0, 'BOOK', '59', '教练审批通过扣减', '2026-02-13 21:30:21');
INSERT INTO `course_stock_log` VALUES (66, 6, 2, 1, 1, 'PURCHASE', '41', '购买课程，增加1次', '2026-02-13 21:40:10');
INSERT INTO `course_stock_log` VALUES (67, 6, 2, -1, 0, 'BOOK', '60', '教练审批通过扣减', '2026-02-13 21:40:37');
INSERT INTO `course_stock_log` VALUES (68, 6, 1, 1, 1, 'PURCHASE', '42', '购买课程，增加1次', '2026-02-13 21:44:57');
INSERT INTO `course_stock_log` VALUES (69, 6, 1, -1, 0, 'BOOK', '61', '教练审批通过扣减', '2026-02-13 21:45:37');
INSERT INTO `course_stock_log` VALUES (70, 6, 1, 1, 1, 'PURCHASE', '43', '购买课程，增加1次', '2026-02-13 21:46:02');
INSERT INTO `course_stock_log` VALUES (71, 6, 1, -1, 0, 'BOOK', '62', '教练审批通过扣减', '2026-02-13 21:46:21');
INSERT INTO `course_stock_log` VALUES (72, 6, 2, 1, 1, 'PURCHASE', '44', '购买课程，增加1次', '2026-02-13 22:08:30');
INSERT INTO `course_stock_log` VALUES (73, 6, 2, -1, 0, 'BOOK', '63', '教练审批通过扣减', '2026-02-13 22:16:05');

-- ----------------------------
-- Table structure for deferred_revenue
-- ----------------------------
DROP TABLE IF EXISTS `deferred_revenue`;
CREATE TABLE `deferred_revenue`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `member_no` int NOT NULL COMMENT '会员编号',
  `source_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '来源类型：RECHARGE-充值续入, CARD_RENEW-续卡, PURCHASE-购课',
  `source_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '来源单号(充值号/续卡号/购课号)',
  `source_amount` decimal(10, 2) NOT NULL COMMENT '原始预收金额',
  `deferred_amount` decimal(10, 2) NOT NULL COMMENT '待分摊递延金额(初始等于source_amount)',
  `recognized_amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '已确认收入金额',
  `biz_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '业务类型：CARD-会籍费, COURSE-课程费',
  `source_channel` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '资金来源渠道：CASH-现金, BALANCE-余额',
  `total_periods` int NOT NULL DEFAULT 1 COMMENT '总分摊期数(会籍按月，课程按次数)',
  `recognized_periods` int NOT NULL DEFAULT 0 COMMENT '已分摊期数',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-进行中, 2-已完成, 3-已取消',
  `start_date` date NULL DEFAULT NULL COMMENT '开始分摊日期',
  `end_date` date NULL DEFAULT NULL COMMENT '结束分摊日期',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_source`(`source_type` ASC, `source_no` ASC) USING BTREE,
  INDEX `idx_member_status`(`member_no` ASC, `status` ASC) USING BTREE,
  INDEX `idx_deferred_biz_type_status`(`biz_type` ASC, `status` ASC) USING BTREE,
  INDEX `idx_deferred_source_type`(`source_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 52 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '递延收益明细表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of deferred_revenue
-- ----------------------------
INSERT INTO `deferred_revenue` VALUES (22, 6, 'RECHARGE', '98', 30.00, 30.00, 0.00, 'BALANCE', 'CASH', 1, 0, 1, '2026-02-12', NULL, '充值-预收账款-储值余额', '2026-02-12 18:48:08', '2026-02-13 21:27:03');
INSERT INTO `deferred_revenue` VALUES (23, 6, 'PURCHASE', '37', 300.00, 300.00, 300.00, 'COURSE', 'BALANCE', 3, 3, 2, '2026-02-12', NULL, '购课-预收账款-课程费', '2026-02-12 18:50:33', '2026-02-13 21:27:03');
INSERT INTO `deferred_revenue` VALUES (24, 6, 'PURCHASE', '38', 180.00, 180.00, 180.00, 'COURSE', 'BALANCE', 1, 1, 2, '2026-02-12', NULL, '购课-预收账款-课程费', '2026-02-12 18:54:20', '2026-02-13 21:27:03');
INSERT INTO `deferred_revenue` VALUES (25, 7, 'CARD_RENEW', '27', 300.00, 300.00, 0.00, 'CARD', 'CASH', 1, 0, 1, '2026-02-12', '2026-06-12', '现金续卡-预收账款-会籍费', '2026-02-12 18:57:00', '2026-02-13 16:07:45');
INSERT INTO `deferred_revenue` VALUES (26, 6, 'PURCHASE', '39', 100.00, 100.00, 100.00, 'COURSE', 'BALANCE', 1, 1, 2, '2026-02-12', NULL, '购课-预收账款-课程费', '2026-02-12 19:00:23', '2026-02-13 21:27:03');
INSERT INTO `deferred_revenue` VALUES (30, 6, 'CARD_RENEW', '31', 300.00, 300.00, 0.00, 'CARD', 'CASH', 1, 0, 1, '2026-02-12', '2027-10-10', '现金续卡-预收账款-会籍费', '2026-02-12 20:13:33', '2026-02-13 16:07:45');
INSERT INTO `deferred_revenue` VALUES (33, 6, 'CARD_RENEW', '34', 300.00, 300.00, 0.00, 'CARD', 'BALANCE', 1, 0, 1, '2026-02-12', '2027-11-09', '余额续卡-预收账款内部划转', '2026-02-12 20:22:15', '2026-02-13 16:07:45');
INSERT INTO `deferred_revenue` VALUES (34, 6, 'CARD_RENEW', '35', 300.00, 300.00, 0.00, 'CARD', 'BALANCE', 1, 0, 1, '2026-02-12', '2027-12-09', '余额续卡-预收账款内部划转', '2026-02-12 20:22:28', '2026-02-13 16:07:45');
INSERT INTO `deferred_revenue` VALUES (35, 6, 'CARD_RENEW', '36', 300.00, 300.00, 0.00, 'CARD', 'BALANCE', 1, 0, 1, '2026-02-13', '2028-01-08', '余额续卡-预收账款内部划转', '2026-02-13 14:45:21', '2026-02-13 16:07:45');
INSERT INTO `deferred_revenue` VALUES (36, 6, 'RECHARGE', '99', 50.00, 50.00, 0.00, 'BALANCE', 'ONLINE', 1, 0, 1, '2026-02-13', NULL, '充值-预收账款-储值余额', '2026-02-13 16:22:52', '2026-02-13 21:27:03');
INSERT INTO `deferred_revenue` VALUES (37, 6, 'PURCHASE', '40', 100.00, 100.00, 100.00, 'COURSE', 'BALANCE', 1, 1, 2, '2026-02-13', NULL, '购课-预收账款-课程费', '2026-02-13 21:29:42', '2026-02-13 21:56:32');
INSERT INTO `deferred_revenue` VALUES (38, 6, 'PURCHASE', '41', 120.00, 120.00, 0.00, 'COURSE', 'BALANCE', 1, 0, 1, '2026-02-13', NULL, '购课-预收账款-课程费', '2026-02-13 21:40:10', '2026-02-13 21:40:10');
INSERT INTO `deferred_revenue` VALUES (39, 6, 'RECHARGE', '100', 30.00, 30.00, 0.00, 'BALANCE', 'ONLINE', 1, 0, 1, '2026-02-13', NULL, '充值-预收账款-储值余额', '2026-02-13 21:44:44', '2026-02-13 21:56:32');
INSERT INTO `deferred_revenue` VALUES (40, 6, 'PURCHASE', '42', 100.00, 100.00, 100.00, 'COURSE', 'BALANCE', 1, 1, 2, '2026-02-13', NULL, '购课-预收账款-课程费', '2026-02-13 21:44:57', '2026-02-13 21:45:38');
INSERT INTO `deferred_revenue` VALUES (41, 6, 'PURCHASE', '43', 100.00, 100.00, 100.00, 'COURSE', 'BALANCE', 1, 1, 2, '2026-02-13', NULL, '购课-预收账款-课程费', '2026-02-13 21:46:02', '2026-02-13 21:46:23');
INSERT INTO `deferred_revenue` VALUES (42, 6, 'CARD_RENEW', '37', 300.00, 300.00, 0.00, 'CARD', 'BALANCE', 1, 0, 1, '2028-01-09', '2028-02-07', '余额续卡-预收账款内部划转', '2026-02-13 21:46:44', '2026-02-13 21:46:44');
INSERT INTO `deferred_revenue` VALUES (43, 6, 'CARD_RENEW', '38', 300.00, 300.00, 0.00, 'CARD', 'BALANCE', 1, 0, 1, '2028-02-08', '2028-03-08', '余额续卡-预收账款内部划转', '2026-02-13 21:47:57', '2026-02-13 21:47:57');
INSERT INTO `deferred_revenue` VALUES (44, 6, 'CARD_RENEW', '39', 300.00, 300.00, 0.00, 'CARD', 'BALANCE', 1, 0, 1, '2028-03-09', '2028-04-07', '余额续卡-预收账款内部划转', '2026-02-13 21:48:20', '2026-02-13 21:48:20');
INSERT INTO `deferred_revenue` VALUES (45, 6, 'CARD_RENEW', '40', 300.00, 300.00, 0.00, 'CARD', 'BALANCE', 1, 0, 1, '2028-04-08', '2028-05-07', '余额续卡-预收账款内部划转', '2026-02-13 21:49:03', '2026-02-13 21:49:03');
INSERT INTO `deferred_revenue` VALUES (46, 6, 'CARD_RENEW', '41', 300.00, 300.00, 0.00, 'CARD', 'CASH', 1, 0, 1, '2028-05-08', '2028-06-06', '现金续卡-预收账款-会籍费', '2026-02-13 21:49:30', '2026-02-13 21:49:30');
INSERT INTO `deferred_revenue` VALUES (47, 6, 'CARD_RENEW', '42', 300.00, 300.00, 0.00, 'CARD', 'BALANCE', 1, 0, 1, '2028-06-07', '2028-07-06', '余额续卡-预收账款内部划转', '2026-02-13 21:57:25', '2026-02-13 21:57:25');
INSERT INTO `deferred_revenue` VALUES (48, 6, 'CARD_RENEW', '43', 300.00, 300.00, 0.00, 'CARD', 'CASH', 1, 0, 1, '2028-07-07', '2028-08-05', '现金续卡-预收账款-会籍费', '2026-02-13 22:00:07', '2026-02-13 22:00:07');
INSERT INTO `deferred_revenue` VALUES (49, 2, 'CARD_RENEW', '44', 300.00, 300.00, 0.00, 'CARD', 'BALANCE', 1, 0, 1, '2026-02-13', '2026-03-15', '余额续卡-预收账款内部划转', '2026-02-13 22:06:53', '2026-02-13 22:06:53');
INSERT INTO `deferred_revenue` VALUES (50, 5, 'CARD_RENEW', '45', 300.00, 300.00, 0.00, 'CARD', 'CASH', 1, 0, 1, '2026-02-13', '2026-03-15', '现金续卡-预收账款-会籍费', '2026-02-13 22:07:27', '2026-02-13 22:07:27');
INSERT INTO `deferred_revenue` VALUES (51, 6, 'PURCHASE', '44', 120.00, 120.00, 0.00, 'COURSE', 'BALANCE', 1, 0, 1, '2026-02-13', NULL, '购课-预收账款-课程费', '2026-02-13 22:08:30', '2026-02-13 22:08:30');

-- ----------------------------
-- Table structure for employee
-- ----------------------------
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee`  (
  `employeeNo` int NOT NULL AUTO_INCREMENT,
  `employeeAge` int NULL DEFAULT NULL,
  `employeeName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `employeeGender` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `employeePhone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `employeeTime` datetime NULL DEFAULT NULL,
  `employeeMessage` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `employeeJob` int NULL DEFAULT NULL COMMENT '1 教练 2前台 3保洁 4经理',
  PRIMARY KEY (`employeeNo`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of employee
-- ----------------------------
INSERT INTO `employee` VALUES (1, 35, '橙灰', 'M', '15798645769', '2023-06-20 12:18:29', '冠军教练', 1);
INSERT INTO `employee` VALUES (2, 40, '小黄', 'M', '13568974568', '2023-06-29 09:38:48', '项目经理', 4);
INSERT INTO `employee` VALUES (3, 24, '小胡', 'F', '13598756985', '2023-06-29 10:09:15', NULL, 2);
INSERT INTO `employee` VALUES (4, 23, '啊狗', 'M', '', '2026-02-10 16:24:52', '', 1);
INSERT INTO `employee` VALUES (5, 20, '啊三', 'M', '', '2026-02-10 16:37:02', '', 1);

-- ----------------------------
-- Table structure for equipment
-- ----------------------------
DROP TABLE IF EXISTS `equipment`;
CREATE TABLE `equipment`  (
  `equipmentNo` int NOT NULL AUTO_INCREMENT,
  `equipmentName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `equipmentLocation` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `equipmentState` int NULL DEFAULT NULL,
  `equipmentMessage` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `employeeJob` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`equipmentNo`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of equipment
-- ----------------------------
INSERT INTO `equipment` VALUES (1, '123123', '大厅', 0, '无', NULL);
INSERT INTO `equipment` VALUES (2, '瑜伽垫', '大厅', 1, '无', NULL);

-- ----------------------------
-- Table structure for finance_ledger
-- ----------------------------
DROP TABLE IF EXISTS `finance_ledger`;
CREATE TABLE `finance_ledger`  (
  `ledger_no` bigint NOT NULL AUTO_INCREMENT,
  `member_no` int NULL DEFAULT NULL COMMENT '关联会员（可空）',
  `biz_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'RECHARGE/PURCHASE/CARD_RENEW/ADJUST',
  `biz_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '业务单号',
  `direction` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'IN/OUT',
  `amount` decimal(10, 2) NOT NULL COMMENT '金额（正数）',
  `channel` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'BALANCE/CASH/ONLINE等',
  `balance_after` decimal(10, 2) NULL DEFAULT NULL COMMENT '余额支付必填：变动后余额',
  `operator_admin_id` int NULL DEFAULT NULL COMMENT '操作管理员ID',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ledger_no`) USING BTREE,
  UNIQUE INDEX `uk_biz`(`biz_type` ASC, `biz_no` ASC) USING BTREE,
  INDEX `idx_member_time`(`member_no` ASC, `create_time` ASC) USING BTREE,
  INDEX `idx_type_time`(`biz_type` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 105 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '财务总账流水表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of finance_ledger
-- ----------------------------
INSERT INTO `finance_ledger` VALUES (72, 6, 'RECHARGE', '98', 'IN', 30.00, 'ONLINE', NULL, NULL, '预收账款-储值余额', '2026-02-12 18:48:08');
INSERT INTO `finance_ledger` VALUES (73, 6, 'PURCHASE', '37', 'NONE', 300.00, 'BALANCE', NULL, NULL, '预收账款-课程费(共3次)', '2026-02-12 18:50:33');
INSERT INTO `finance_ledger` VALUES (74, 6, 'COURSE_RECOGNIZE', '53', 'DEFERRED', 100.00, 'BALANCE', NULL, NULL, '已实现收入-课程核销', '2026-02-12 18:50:52');
INSERT INTO `finance_ledger` VALUES (75, 6, 'COURSE_RECOGNIZE', '55', 'DEFERRED', 100.00, 'BALANCE', NULL, NULL, '已实现收入-课程核销', '2026-02-12 18:52:20');
INSERT INTO `finance_ledger` VALUES (76, 6, 'PURCHASE', '38', 'NONE', 180.00, 'BALANCE', NULL, NULL, '预收账款-课程费(共1次)', '2026-02-12 18:54:20');
INSERT INTO `finance_ledger` VALUES (77, 6, 'COURSE_RECOGNIZE', '56', 'DEFERRED', 180.00, 'BALANCE', NULL, NULL, '已实现收入-课程核销', '2026-02-12 18:54:33');
INSERT INTO `finance_ledger` VALUES (78, 7, 'CARD_RENEW', '27', 'IN', 300.00, 'CASH', NULL, 1, '预收账款-会籍费(1个月,每月300.00)', '2026-02-12 18:57:00');
INSERT INTO `finance_ledger` VALUES (79, 6, 'PURCHASE', '39', 'NONE', 100.00, 'BALANCE', NULL, NULL, '预收账款-课程费(共1次)', '2026-02-12 19:00:23');
INSERT INTO `finance_ledger` VALUES (80, 6, 'COURSE_RECOGNIZE', '54', 'DEFERRED', 100.00, 'BALANCE', NULL, NULL, '已实现收入-课程核销', '2026-02-12 19:06:26');
INSERT INTO `finance_ledger` VALUES (81, 6, 'CARD_RENEW', '31', 'IN', 300.00, 'CASH', NULL, 1, '预收账款-会籍费(1个月,每月300.00)', '2026-02-12 20:13:33');
INSERT INTO `finance_ledger` VALUES (82, 6, 'CARD_RENEW', '34', 'NONE', 300.00, 'BALANCE', NULL, NULL, '预收账款内部划转-储值余额→会籍费', '2026-02-12 20:22:15');
INSERT INTO `finance_ledger` VALUES (83, 6, 'CARD_RENEW', '35', 'NONE', 300.00, 'BALANCE', NULL, NULL, '预收账款内部划转-储值余额→会籍费', '2026-02-12 20:22:28');
INSERT INTO `finance_ledger` VALUES (84, 6, 'COURSE_RECOGNIZE', '58', 'DEFERRED', 100.00, 'BALANCE', NULL, NULL, '已实现收入-课程核销', '2026-02-13 14:41:26');
INSERT INTO `finance_ledger` VALUES (85, 6, 'CARD_RENEW', '36', 'NONE', 300.00, 'BALANCE', NULL, NULL, '预收账款内部划转-储值余额→会籍费', '2026-02-13 14:45:21');
INSERT INTO `finance_ledger` VALUES (86, 6, 'RECHARGE', '99', 'IN', 50.00, 'ONLINE', NULL, NULL, '预收账款-储值余额', '2026-02-13 16:22:52');
INSERT INTO `finance_ledger` VALUES (87, 6, 'PURCHASE', '40', 'NONE', 100.00, 'BALANCE', NULL, NULL, '预收账款-课程费(共1次)', '2026-02-13 21:29:42');
INSERT INTO `finance_ledger` VALUES (88, 6, 'COURSE_RECOGNIZE', '59', 'DEFERRED', 100.00, 'BALANCE', NULL, NULL, '已实现收入-课程核销', '2026-02-13 21:31:00');
INSERT INTO `finance_ledger` VALUES (89, 6, 'PURCHASE', '41', 'NONE', 120.00, 'BALANCE', NULL, NULL, '预收账款-课程费(共1次)', '2026-02-13 21:40:10');
INSERT INTO `finance_ledger` VALUES (90, 6, 'RECHARGE', '100', 'IN', 30.00, 'ONLINE', NULL, NULL, '预收账款-储值余额', '2026-02-13 21:44:44');
INSERT INTO `finance_ledger` VALUES (91, 6, 'PURCHASE', '42', 'NONE', 100.00, 'BALANCE', NULL, NULL, '预收账款-课程费(共1次)', '2026-02-13 21:44:57');
INSERT INTO `finance_ledger` VALUES (92, 6, 'COURSE_RECOGNIZE', '61', 'DEFERRED', 100.00, 'BALANCE', NULL, NULL, '已实现收入-课程核销', '2026-02-13 21:45:38');
INSERT INTO `finance_ledger` VALUES (93, 6, 'PURCHASE', '43', 'NONE', 100.00, 'BALANCE', NULL, NULL, '预收账款-课程费(共1次)', '2026-02-13 21:46:02');
INSERT INTO `finance_ledger` VALUES (94, 6, 'COURSE_RECOGNIZE', '62', 'DEFERRED', 100.00, 'BALANCE', NULL, NULL, '已实现收入-课程核销', '2026-02-13 21:46:23');
INSERT INTO `finance_ledger` VALUES (95, 6, 'CARD_RENEW', '37', 'NONE', 300.00, 'BALANCE', NULL, NULL, '预收账款内部划转-储值余额→会籍费', '2026-02-13 21:46:44');
INSERT INTO `finance_ledger` VALUES (96, 6, 'CARD_RENEW', '38', 'NONE', 300.00, 'BALANCE', NULL, NULL, '预收账款内部划转-储值余额→会籍费', '2026-02-13 21:47:57');
INSERT INTO `finance_ledger` VALUES (97, 6, 'CARD_RENEW', '39', 'NONE', 300.00, 'BALANCE', NULL, NULL, '预收账款内部划转-储值余额→会籍费', '2026-02-13 21:48:20');
INSERT INTO `finance_ledger` VALUES (98, 6, 'CARD_RENEW', '40', 'NONE', 300.00, 'BALANCE', NULL, NULL, '预收账款内部划转-储值余额→会籍费', '2026-02-13 21:49:03');
INSERT INTO `finance_ledger` VALUES (99, 6, 'CARD_RENEW', '41', 'IN', 300.00, 'CASH', NULL, 1, '预收账款-会籍费(1个月,每月300.00)', '2026-02-13 21:49:30');
INSERT INTO `finance_ledger` VALUES (100, 6, 'CARD_RENEW', '42', 'NONE', 300.00, 'BALANCE', NULL, NULL, '预收账款内部划转-储值余额→会籍费', '2026-02-13 21:57:25');
INSERT INTO `finance_ledger` VALUES (101, 6, 'CARD_RENEW', '43', 'IN', 300.00, 'CASH', NULL, 1, '预收账款-会籍费(1个月,每月300.00)', '2026-02-13 22:00:07');
INSERT INTO `finance_ledger` VALUES (102, 2, 'CARD_RENEW', '44', 'NONE', 300.00, 'BALANCE', NULL, NULL, '预收账款内部划转-储值余额→会籍费', '2026-02-13 22:06:53');
INSERT INTO `finance_ledger` VALUES (103, 5, 'CARD_RENEW', '45', 'IN', 300.00, 'CASH', NULL, 1, '预收账款-会籍费(1个月,每月300.00)', '2026-02-13 22:07:27');
INSERT INTO `finance_ledger` VALUES (104, 6, 'PURCHASE', '44', 'NONE', 120.00, 'BALANCE', NULL, NULL, '预收账款-课程费(共1次)', '2026-02-13 22:08:30');

-- ----------------------------
-- Table structure for manager
-- ----------------------------
DROP TABLE IF EXISTS `manager`;
CREATE TABLE `manager`  (
  `managerNo` int NOT NULL AUTO_INCREMENT,
  `employeeNo` int NULL DEFAULT NULL,
  PRIMARY KEY (`managerNo`) USING BTREE,
  INDEX `employeeNo`(`employeeNo` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of manager
-- ----------------------------
INSERT INTO `manager` VALUES (1, 2);

-- ----------------------------
-- Table structure for member
-- ----------------------------
DROP TABLE IF EXISTS `member`;
CREATE TABLE `member`  (
  `memberNo` int NOT NULL AUTO_INCREMENT,
  `memberUsername` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `memberPassword` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `memberName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `memberAge` int NULL DEFAULT NULL,
  `memberGender` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `memberPhone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `cardTime` datetime NULL DEFAULT NULL,
  `memberHeight` double NULL DEFAULT NULL,
  `memberWeight` double NULL DEFAULT NULL,
  `cardClass` int NULL DEFAULT NULL,
  `cardNextClass` int NULL DEFAULT NULL,
  `memberIntegral` int NULL DEFAULT NULL,
  `memberChange` int NULL DEFAULT NULL,
  `personalizedSignature` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `memberPower` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `expire_time` datetime NULL DEFAULT NULL COMMENT '会员卡到期时间',
  `card_status` tinyint NOT NULL DEFAULT 1 COMMENT '会员卡状态：1有效 0过期 2停卡',
  PRIMARY KEY (`memberNo`) USING BTREE,
  UNIQUE INDEX `memberPhone`(`memberPhone` ASC) USING BTREE COMMENT '手机号唯一'
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of member
-- ----------------------------
INSERT INTO `member` VALUES (1, 'admin', 'admin', 'admin', 18, 'F', '18470558967', '2023-06-20 12:12:01', 175, 60, 200, 150, 47, 39, 'hhh', '1', NULL, '2026-03-14 12:59:43', 1);
INSERT INTO `member` VALUES (2, '0031023684', '123456', '北京小熊', 20, 'M', '18470668957', '2023-06-20 12:16:16', 180, 70, 50, 50, 65, 1060, '1', '1', NULL, '2026-03-15 22:06:54', 1);
INSERT INTO `member` VALUES (5, '13800000000', 'e10adc3949ba59abbe56e057f20f883e', '??????', 25, 'M', '13800000000', '2025-12-26 15:57:40', NULL, NULL, NULL, NULL, 0, 0, NULL, '0', NULL, '2026-03-15 22:07:28', 1);
INSERT INTO `member` VALUES (6, '18177612352', 'e10adc3949ba59abbe56e057f20f883e', '123123', 20, 'M', '18177612352', '2025-12-26 15:58:12', 180, NULL, NULL, NULL, 1271, 1560, NULL, '0', NULL, '2028-08-05 13:08:55', 1);
INSERT INTO `member` VALUES (7, '17807816152', '36f17c3939ac3e7b2fc9396fa8e953ea', 'qwe', 2, 'F', '17807816152', '2026-02-04 18:41:34', NULL, NULL, NULL, NULL, 0, 0, NULL, '0', NULL, '2026-06-12 14:02:36', 1);

-- ----------------------------
-- Table structure for member_card_renewal
-- ----------------------------
DROP TABLE IF EXISTS `member_card_renewal`;
CREATE TABLE `member_card_renewal`  (
  `renewal_no` bigint NOT NULL AUTO_INCREMENT,
  `member_no` int NOT NULL COMMENT '会员编号',
  `card_type_id` int NOT NULL COMMENT '卡项ID',
  `days_added` int NOT NULL COMMENT '增加天数',
  `amount` decimal(10, 2) NOT NULL COMMENT '实付金额',
  `pay_channel` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '支付渠道：BALANCE/CASH',
  `old_expire_time` datetime NULL DEFAULT NULL COMMENT '原到期时间',
  `new_expire_time` datetime NULL DEFAULT NULL COMMENT '新到期时间',
  `operator_admin_id` int NULL DEFAULT NULL COMMENT '操作管理员ID',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`renewal_no`) USING BTREE,
  INDEX `idx_member_time`(`member_no` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 46 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '会员续卡记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of member_card_renewal
-- ----------------------------
INSERT INTO `member_card_renewal` VALUES (1, 6, 1, 30, 300.00, 'CASH', NULL, '2026-03-13 00:39:17', 1, '测试-现金续月卡', '2026-02-11 00:39:17');
INSERT INTO `member_card_renewal` VALUES (2, 6, 1, 30, 300.00, 'BALANCE', NULL, '2026-03-13 00:41:01', 1, '测试-余额续月卡', '2026-02-11 00:41:01');
INSERT INTO `member_card_renewal` VALUES (3, 6, 1, 30, 300.00, 'BALANCE', NULL, '2026-03-14 12:14:32', NULL, '', '2026-02-12 12:14:32');
INSERT INTO `member_card_renewal` VALUES (4, 6, 2, 90, 800.00, 'BALANCE', NULL, '2026-05-13 12:24:55', NULL, '', '2026-02-12 12:24:54');
INSERT INTO `member_card_renewal` VALUES (5, 6, 1, 30, 300.00, 'CASH', NULL, '2026-03-14 12:25:55', NULL, '', '2026-02-12 12:25:55');
INSERT INTO `member_card_renewal` VALUES (6, 6, 1, 30, 300.00, 'CASH', NULL, '2026-03-14 12:28:12', NULL, '', '2026-02-12 12:28:11');
INSERT INTO `member_card_renewal` VALUES (7, 1, 1, 30, 300.00, 'CASH', NULL, '2026-03-14 12:44:06', NULL, '', '2026-02-12 12:44:06');
INSERT INTO `member_card_renewal` VALUES (8, 1, 1, 30, 300.00, 'CASH', NULL, '2026-03-14 12:44:19', NULL, '', '2026-02-12 12:44:19');
INSERT INTO `member_card_renewal` VALUES (9, 6, 1, 30, 300.00, 'CASH', NULL, '2026-03-14 12:46:12', NULL, '', '2026-02-12 12:46:12');
INSERT INTO `member_card_renewal` VALUES (10, 6, 1, 30, 300.00, 'CASH', NULL, '2026-03-14 12:54:30', 1, '', '2026-02-12 12:54:29');
INSERT INTO `member_card_renewal` VALUES (11, 1, 1, 30, 300.00, 'CASH', NULL, '2026-03-14 12:59:43', 1, '', '2026-02-12 12:59:42');
INSERT INTO `member_card_renewal` VALUES (12, 6, 1, 30, 300.00, 'BALANCE', NULL, '2026-03-14 12:59:59', 1, '', '2026-02-12 12:59:58');
INSERT INTO `member_card_renewal` VALUES (13, 6, 1, 30, 300.00, 'BALANCE', NULL, '2026-03-14 13:02:02', 1, '', '2026-02-12 13:02:02');
INSERT INTO `member_card_renewal` VALUES (14, 6, 1, 30, 300.00, 'BALANCE', NULL, '2026-03-14 13:03:33', 1, '', '2026-02-12 13:03:33');
INSERT INTO `member_card_renewal` VALUES (15, 6, 1, 30, 300.00, 'BALANCE', NULL, '2026-03-14 13:08:03', 1, '', '2026-02-12 13:08:02');
INSERT INTO `member_card_renewal` VALUES (16, 6, 1, 30, 300.00, 'CASH', NULL, '2026-03-14 13:08:55', 1, '', '2026-02-12 13:08:54');
INSERT INTO `member_card_renewal` VALUES (17, 6, 1, 30, 300.00, 'CASH', '2026-03-14 13:08:55', '2026-04-13 13:08:55', 1, '', '2026-02-12 13:13:27');
INSERT INTO `member_card_renewal` VALUES (18, 6, 3, 365, 2800.00, 'CASH', '2026-04-13 13:08:55', '2027-04-13 13:08:55', 1, '', '2026-02-12 13:13:49');
INSERT INTO `member_card_renewal` VALUES (19, 6, 1, 30, 300.00, 'BALANCE', '2027-04-13 13:08:55', '2027-05-13 13:08:55', 1, '', '2026-02-12 13:14:42');
INSERT INTO `member_card_renewal` VALUES (20, 6, 1, 30, 300.00, 'CASH', '2027-05-13 13:08:55', '2027-06-12 13:08:55', 1, '', '2026-02-12 14:02:22');
INSERT INTO `member_card_renewal` VALUES (21, 7, 1, 30, 300.00, 'CASH', NULL, '2026-03-14 14:02:36', 1, '', '2026-02-12 14:02:36');
INSERT INTO `member_card_renewal` VALUES (22, 6, 1, 30, 300.00, 'BALANCE', '2027-06-12 13:08:55', '2027-07-12 13:08:55', 1, '', '2026-02-12 15:56:01');
INSERT INTO `member_card_renewal` VALUES (23, 6, 1, 30, 300.00, 'BALANCE', '2027-07-12 13:08:55', '2027-08-11 13:08:55', 1, '', '2026-02-12 15:57:02');
INSERT INTO `member_card_renewal` VALUES (24, 6, 1, 30, 300.00, 'BALANCE', '2027-08-11 13:08:55', '2027-09-10 13:08:55', 1, '', '2026-02-12 16:25:18');
INSERT INTO `member_card_renewal` VALUES (25, 7, 1, 30, 300.00, 'CASH', '2026-03-14 14:02:36', '2026-04-13 14:02:36', 1, '', '2026-02-12 17:38:48');
INSERT INTO `member_card_renewal` VALUES (26, 7, 1, 30, 300.00, 'CASH', '2026-04-13 14:02:36', '2026-05-13 14:02:36', 1, '', '2026-02-12 17:58:52');
INSERT INTO `member_card_renewal` VALUES (27, 7, 1, 30, 300.00, 'CASH', '2026-05-13 14:02:36', '2026-06-12 14:02:36', 1, '', '2026-02-12 18:57:00');
INSERT INTO `member_card_renewal` VALUES (31, 6, 1, 30, 300.00, 'CASH', '2027-09-10 13:08:55', '2027-10-10 13:08:55', 1, '', '2026-02-12 20:13:33');
INSERT INTO `member_card_renewal` VALUES (34, 6, 1, 30, 300.00, 'BALANCE', '2027-10-10 13:08:55', '2027-11-09 13:08:55', 1, '', '2026-02-12 20:22:15');
INSERT INTO `member_card_renewal` VALUES (35, 6, 1, 30, 300.00, 'BALANCE', '2027-11-09 13:08:55', '2027-12-09 13:08:55', 1, '', '2026-02-12 20:22:28');
INSERT INTO `member_card_renewal` VALUES (36, 6, 1, 30, 300.00, 'BALANCE', '2027-12-09 13:08:55', '2028-01-08 13:08:55', 1, '', '2026-02-13 14:45:21');
INSERT INTO `member_card_renewal` VALUES (37, 6, 1, 30, 300.00, 'BALANCE', '2028-01-08 13:08:55', '2028-02-07 13:08:55', 1, '', '2026-02-13 21:46:44');
INSERT INTO `member_card_renewal` VALUES (38, 6, 1, 30, 300.00, 'BALANCE', '2028-02-07 13:08:55', '2028-03-08 13:08:55', 1, '', '2026-02-13 21:47:57');
INSERT INTO `member_card_renewal` VALUES (39, 6, 1, 30, 300.00, 'BALANCE', '2028-03-08 13:08:55', '2028-04-07 13:08:55', 1, '', '2026-02-13 21:48:20');
INSERT INTO `member_card_renewal` VALUES (40, 6, 1, 30, 300.00, 'BALANCE', '2028-04-07 13:08:55', '2028-05-07 13:08:55', 1, '', '2026-02-13 21:49:03');
INSERT INTO `member_card_renewal` VALUES (41, 6, 1, 30, 300.00, 'CASH', '2028-05-07 13:08:55', '2028-06-06 13:08:55', 1, '', '2026-02-13 21:49:30');
INSERT INTO `member_card_renewal` VALUES (42, 6, 1, 30, 300.00, 'BALANCE', '2028-06-06 13:08:55', '2028-07-06 13:08:55', 1, '', '2026-02-13 21:57:25');
INSERT INTO `member_card_renewal` VALUES (43, 6, 1, 30, 300.00, 'CASH', '2028-07-06 13:08:55', '2028-08-05 13:08:55', 1, '', '2026-02-13 22:00:07');
INSERT INTO `member_card_renewal` VALUES (44, 2, 1, 30, 300.00, 'BALANCE', NULL, '2026-03-15 22:06:54', 1, '', '2026-02-13 22:06:53');
INSERT INTO `member_card_renewal` VALUES (45, 5, 1, 30, 300.00, 'CASH', NULL, '2026-03-15 22:07:28', 1, '', '2026-02-13 22:07:27');

-- ----------------------------
-- Table structure for member_card_type
-- ----------------------------
DROP TABLE IF EXISTS `member_card_type`;
CREATE TABLE `member_card_type`  (
  `card_type_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '月卡/季卡/年卡',
  `days` int NOT NULL COMMENT '增加天数',
  `card_months` int NOT NULL DEFAULT 1 COMMENT '分摊月数',
  `price` decimal(10, 2) NOT NULL COMMENT '价格',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '1启用 0禁用',
  PRIMARY KEY (`card_type_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '卡项模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of member_card_type
-- ----------------------------
INSERT INTO `member_card_type` VALUES (1, '月卡', 30, 1, 300.00, 1);
INSERT INTO `member_card_type` VALUES (2, '季卡', 90, 3, 800.00, 1);
INSERT INTO `member_card_type` VALUES (3, '年卡', 365, 12, 2800.00, 1);

-- ----------------------------
-- Table structure for receivable_transfer
-- ----------------------------
DROP TABLE IF EXISTS `receivable_transfer`;
CREATE TABLE `receivable_transfer`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `member_no` int NOT NULL COMMENT '会员编号',
  `from_source_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '转出来源：RECHARGE-充值余额',
  `from_source_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '来源单号',
  `to_source_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '转入类型：CARD_RENEW-会籍费, COURSE-课程费',
  `to_renewal_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '关联续卡号(如果是续卡)',
  `amount` decimal(10, 2) NOT NULL COMMENT '划转金额',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-有效, 2-已取消',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_member`(`member_no` ASC) USING BTREE,
  INDEX `idx_transfer_member_time`(`member_no` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '预收账款内部划转记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of receivable_transfer
-- ----------------------------
INSERT INTO `receivable_transfer` VALUES (1, 6, 'RECHARGE', '45', 'CARD_RENEW', '34', 300.00, 1, '储值余额转会籍费', '2026-02-12 20:22:15');
INSERT INTO `receivable_transfer` VALUES (2, 6, 'RECHARGE', '45', 'CARD_RENEW', '35', 300.00, 1, '储值余额转会籍费', '2026-02-12 20:22:28');
INSERT INTO `receivable_transfer` VALUES (3, 6, 'RECHARGE', '45', 'CARD_RENEW', '36', 300.00, 1, '储值余额转会籍费', '2026-02-13 14:45:21');
INSERT INTO `receivable_transfer` VALUES (4, 6, 'RECHARGE', '45', 'CARD_RENEW', '37', 300.00, 1, '储值余额转会籍费', '2026-02-13 21:46:44');
INSERT INTO `receivable_transfer` VALUES (5, 6, 'RECHARGE', '45', 'CARD_RENEW', '38', 300.00, 1, '储值余额转会籍费', '2026-02-13 21:47:57');
INSERT INTO `receivable_transfer` VALUES (6, 6, 'RECHARGE', '45', 'CARD_RENEW', '39', 300.00, 1, '储值余额转会籍费', '2026-02-13 21:48:20');
INSERT INTO `receivable_transfer` VALUES (7, 6, 'RECHARGE', '45', 'CARD_RENEW', '40', 300.00, 1, '储值余额转会籍费', '2026-02-13 21:49:03');
INSERT INTO `receivable_transfer` VALUES (8, 6, 'RECHARGE', '45', 'CARD_RENEW', '42', 300.00, 1, '储值余额转会籍费', '2026-02-13 21:57:25');
INSERT INTO `receivable_transfer` VALUES (9, 2, 'RECHARGE', '31', 'CARD_RENEW', '44', 300.00, 1, '储值余额转会籍费', '2026-02-13 22:06:53');

-- ----------------------------
-- Table structure for recharge
-- ----------------------------
DROP TABLE IF EXISTS `recharge`;
CREATE TABLE `recharge`  (
  `rechargeNo` int NOT NULL AUTO_INCREMENT,
  `rechargeDate` datetime NULL DEFAULT NULL,
  `rechargeMethod` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `rechargeStatus` int NULL DEFAULT NULL,
  `rechargeMoney` double NULL DEFAULT NULL,
  `memberNo` int NULL DEFAULT NULL,
  PRIMARY KEY (`rechargeNo`) USING BTREE,
  INDEX `memberNo`(`memberNo` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 101 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of recharge
-- ----------------------------
INSERT INTO `recharge` VALUES (31, '2024-07-28 00:00:00', '在线充值', 1, 200, 2);
INSERT INTO `recharge` VALUES (32, '2024-07-28 00:00:00', '在线充值', 1, 200, 2);
INSERT INTO `recharge` VALUES (33, '2024-07-28 00:00:00', '在线充值', 1, 100, 2);
INSERT INTO `recharge` VALUES (34, '2024-07-28 00:00:00', '在线充值', 1, 200, 2);
INSERT INTO `recharge` VALUES (35, '2024-07-28 00:00:00', '在线充值', 1, 10, 2);
INSERT INTO `recharge` VALUES (36, '2024-07-28 00:00:00', '在线充值', 1, 200, 2);
INSERT INTO `recharge` VALUES (37, '2024-07-28 00:00:00', '在线充值', 1, 50, 2);
INSERT INTO `recharge` VALUES (38, '2024-07-28 00:00:00', '在线充值', 1, 200, 2);
INSERT INTO `recharge` VALUES (39, '2024-07-28 00:00:00', '在线充值', 1, 200, 2);
INSERT INTO `recharge` VALUES (40, '2024-07-28 00:00:00', '在线充值', 1, 200, 2);
INSERT INTO `recharge` VALUES (41, '2024-07-28 00:00:00', '在线充值', 1, 200, 2);
INSERT INTO `recharge` VALUES (42, '2024-07-28 00:00:00', '在线充值', 1, 200, 2);
INSERT INTO `recharge` VALUES (43, '2024-07-28 00:00:00', '在线充值', 1, 200, 2);
INSERT INTO `recharge` VALUES (44, '2024-07-28 00:00:00', '在线充值', 1, 200, 2);
INSERT INTO `recharge` VALUES (45, '2025-12-27 00:00:00', '在线充值', 1, 50, 6);
INSERT INTO `recharge` VALUES (46, '2025-12-27 00:00:00', '在线充值', 1, 200, 6);
INSERT INTO `recharge` VALUES (47, '2025-12-27 00:00:00', '在线充值', 1, 200, 6);
INSERT INTO `recharge` VALUES (48, '2025-12-27 00:00:00', '在线充值', 1, 200, 6);
INSERT INTO `recharge` VALUES (49, '2025-12-27 00:00:00', '在线充值', 1, 200, 6);
INSERT INTO `recharge` VALUES (50, '2025-12-27 00:00:00', '在线充值', 1, 200, 6);
INSERT INTO `recharge` VALUES (51, '2025-12-27 00:00:00', '在线充值', 1, 200, 6);
INSERT INTO `recharge` VALUES (52, '2026-02-03 00:00:00', '在线充值', 1, 30, 6);
INSERT INTO `recharge` VALUES (53, '2026-02-03 00:00:00', '在线充值', 1, 200, 6);
INSERT INTO `recharge` VALUES (54, '2026-02-06 00:00:00', '在线充值', 1, 200, 6);
INSERT INTO `recharge` VALUES (55, '2026-02-06 00:00:00', '在线充值', 1, 200, 6);
INSERT INTO `recharge` VALUES (56, '2026-02-10 00:00:00', '在线充值', 1, 30, 6);
INSERT INTO `recharge` VALUES (77, '2026-02-11 00:00:00', '在线充值', 1, 50, 6);
INSERT INTO `recharge` VALUES (78, '2026-02-12 00:00:00', '在线充值', 1, 30, 6);
INSERT INTO `recharge` VALUES (79, '2026-02-12 00:00:00', '在线充值', 1, 200, 6);
INSERT INTO `recharge` VALUES (80, '2026-02-12 00:00:00', '在线充值', 1, 200, 6);
INSERT INTO `recharge` VALUES (81, '2026-02-12 00:00:00', '在线充值', 1, 200, 6);
INSERT INTO `recharge` VALUES (82, '2026-02-12 00:00:00', '在线充值', 1, 200, 6);
INSERT INTO `recharge` VALUES (83, '2026-02-12 00:00:00', '在线充值', 1, 10, 6);
INSERT INTO `recharge` VALUES (84, '2026-02-12 00:00:00', '在线充值', 1, 10, 6);
INSERT INTO `recharge` VALUES (85, '2026-02-12 00:00:00', '在线充值', 1, 200, 6);
INSERT INTO `recharge` VALUES (86, '2026-02-12 00:00:00', '在线充值', 1, 10, 6);
INSERT INTO `recharge` VALUES (87, '2026-02-12 00:00:00', '在线充值', 1, 50, 6);
INSERT INTO `recharge` VALUES (88, '2026-02-12 00:00:00', '在线充值', 1, 30, 6);
INSERT INTO `recharge` VALUES (89, '2026-02-12 00:00:00', '在线充值', 1, 30, 6);
INSERT INTO `recharge` VALUES (90, '2026-02-12 00:00:00', '在线充值', 1, 10, 6);
INSERT INTO `recharge` VALUES (91, '2026-02-12 00:00:00', '在线充值', 1, 10, 6);
INSERT INTO `recharge` VALUES (92, '2026-02-12 00:00:00', '在线充值', 1, 10, 6);
INSERT INTO `recharge` VALUES (93, '2026-02-12 00:00:00', '在线充值', 1, 10, 6);
INSERT INTO `recharge` VALUES (94, '2026-02-12 00:00:00', '在线充值', 1, 10, 6);
INSERT INTO `recharge` VALUES (95, '2026-02-12 00:00:00', '在线充值', 1, 30, 6);
INSERT INTO `recharge` VALUES (96, '2026-02-12 00:00:00', '在线充值', 1, 30, 6);
INSERT INTO `recharge` VALUES (97, '2026-02-12 00:00:00', '在线充值', 1, 30, 6);
INSERT INTO `recharge` VALUES (98, '2026-02-12 00:00:00', '在线充值', 1, 30, 6);
INSERT INTO `recharge` VALUES (99, '2026-02-13 00:00:00', '在线充值', 1, 50, 6);
INSERT INTO `recharge` VALUES (100, '2026-02-13 00:00:00', '在线充值', 1, 30, 6);

-- ----------------------------
-- Table structure for register
-- ----------------------------
DROP TABLE IF EXISTS `register`;
CREATE TABLE `register`  (
  `registerNo` int NOT NULL AUTO_INCREMENT,
  `courseNo` int NULL DEFAULT NULL,
  `memberNo` int NULL DEFAULT NULL,
  PRIMARY KEY (`registerNo`) USING BTREE,
  INDEX `courseNo`(`courseNo` ASC) USING BTREE,
  INDEX `memberNo`(`memberNo` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of register
-- ----------------------------

-- ----------------------------
-- Table structure for revenue_recognition
-- ----------------------------
DROP TABLE IF EXISTS `revenue_recognition`;
CREATE TABLE `revenue_recognition`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `member_no` int NOT NULL COMMENT '会员编号',
  `deferred_id` bigint NOT NULL COMMENT '关联递延收益ID',
  `biz_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '业务类型：CARD-会籍费, COURSE-课程费',
  `recognition_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '确认方式：MONTHLY-按月分摊, COURSE_COMPLETE-课程核销',
  `amount` decimal(10, 2) NOT NULL COMMENT '本次确认金额',
  `period` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '分摊期间(YYYY-MM)或课程完成日期',
  `related_biz_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '关联业务单号(预约单号/续卡号)',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_member_time`(`member_no` ASC, `create_time` ASC) USING BTREE,
  INDEX `idx_period`(`period` ASC) USING BTREE,
  INDEX `idx_recognition_period`(`period` ASC) USING BTREE,
  INDEX `idx_recognition_member`(`member_no` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '收入确认记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of revenue_recognition
-- ----------------------------
INSERT INTO `revenue_recognition` VALUES (11, 6, 23, 'COURSE', 'COURSE_COMPLETE', 100.00, '2026-02', '53', '课程完成-确认收入', '2026-02-12 18:50:52');
INSERT INTO `revenue_recognition` VALUES (12, 6, 23, 'COURSE', 'COURSE_COMPLETE', 100.00, '2026-02', '55', '课程完成-确认收入', '2026-02-12 18:52:20');
INSERT INTO `revenue_recognition` VALUES (13, 6, 24, 'COURSE', 'COURSE_COMPLETE', 180.00, '2026-02', '56', '课程完成-确认收入', '2026-02-12 18:54:33');
INSERT INTO `revenue_recognition` VALUES (14, 6, 26, 'COURSE', 'COURSE_COMPLETE', 100.00, '2026-02', '54', '课程完成-确认收入', '2026-02-12 19:06:26');
INSERT INTO `revenue_recognition` VALUES (15, 6, 23, 'COURSE', 'COURSE_COMPLETE', 100.00, '2026-02', '58', '课程完成-确认收入', '2026-02-13 14:41:26');
INSERT INTO `revenue_recognition` VALUES (16, 6, 37, 'COURSE', 'COURSE_COMPLETE', 100.00, '2026-02', '59', '课程完成-确认收入', '2026-02-13 21:31:00');
INSERT INTO `revenue_recognition` VALUES (17, 6, 40, 'COURSE', 'COURSE_COMPLETE', 100.00, '2026-02', '61', '课程完成-确认收入', '2026-02-13 21:45:38');
INSERT INTO `revenue_recognition` VALUES (18, 6, 41, 'COURSE', 'COURSE_COMPLETE', 100.00, '2026-02', '62', '课程完成-确认收入', '2026-02-13 21:46:23');

-- ----------------------------
-- Table structure for venue
-- ----------------------------
DROP TABLE IF EXISTS `venue`;
CREATE TABLE `venue`  (
  `venue_no` int NOT NULL AUTO_INCREMENT COMMENT '场地编号',
  `venue_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '场地名称',
  `venue_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '场地类型：1-健身房，2-瑜伽室，3-游泳池，4-篮球场，5-羽毛球场，6-其他',
  `venue_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '场地位置',
  `venue_capacity` int NULL DEFAULT NULL COMMENT '容纳人数',
  `venue_state` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '1-营业, 2-维护, 4-停用',
  `open_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '开放时间',
  `venue_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注信息',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`venue_no`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '场地信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of venue
-- ----------------------------
INSERT INTO `venue` VALUES (1, '主健身房', '1', '1楼A区', 50, '1', '06:00-22:00', '配备各类健身器材，专业教练指导', '2025-12-26 16:12:20', '2026-02-07 14:54:53');
INSERT INTO `venue` VALUES (2, '瑜伽教室A', '2', '2楼B区', 20, '1', '08:00-20:00', '环境舒适，适合瑜伽和普拉提课程', '2025-12-26 16:12:20', '2026-02-07 23:09:19');
INSERT INTO `venue` VALUES (3, '瑜伽教室B', '2', '2楼C区', 15, '1', '08:00-20:00', '小型私教课程专用教室', '2025-12-26 16:12:20', '2026-02-07 23:08:48');
INSERT INTO `venue` VALUES (4, '室内游泳池', '3', '地下1层', 30, '1', '09:00-21:00', '恒温游泳池，配备救生员', '2025-12-26 16:12:20', '2026-02-07 14:54:52');
INSERT INTO `venue` VALUES (5, '篮球场A', '4', '户外篮球场区', 20, '1', '07:00-23:00', '标准篮球场，适合3v3比赛', '2025-12-26 16:12:20', '2026-02-07 23:09:13');
INSERT INTO `venue` VALUES (6, '篮球场B', '4', '户外篮球场区', 20, '1', '07:00-23:00', '标准篮球场，可举办小型比赛', '2025-12-26 16:12:20', '2026-02-07 14:54:51');
INSERT INTO `venue` VALUES (7, '羽毛球场1号', '5', '3楼羽毛球馆', 4, '1', '08:00-22:00', '专业羽毛球场地', '2025-12-26 16:12:20', '2026-02-07 14:54:50');
INSERT INTO `venue` VALUES (8, '羽毛球场2号', '5', '3楼羽毛球馆', 4, '1', '08:00-22:00', '专业羽毛球场地', '2025-12-26 16:12:20', '2025-12-26 16:12:20');
INSERT INTO `venue` VALUES (9, '羽毛球场3号', '5', '3楼羽毛球馆', 4, '1', '08:00-22:00', '专业羽毛球场地', '2025-12-26 16:12:20', '2025-12-26 16:12:20');
INSERT INTO `venue` VALUES (10, '有氧操房', '1', '2楼A区', 40, '1', '07:00-21:00', '大型有氧运动场地', '2025-12-26 16:12:20', '2025-12-26 16:12:20');

-- ----------------------------
-- Table structure for venue_booking
-- ----------------------------
DROP TABLE IF EXISTS `venue_booking`;
CREATE TABLE `venue_booking`  (
  `booking_no` int NOT NULL AUTO_INCREMENT,
  `member_no` int NOT NULL,
  `course_no` int NOT NULL,
  `venue_no` int NOT NULL,
  `start_time` datetime NULL DEFAULT NULL COMMENT '预约开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '预约结束时间',
  `time_slot` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '预约时段，例如 17:00-19:00',
  `booking_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `booking_date` date NOT NULL COMMENT '预约日期',
  `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '0:PENDING, 1:APPROVED, 2:REJECTED, 3:MEMBER_CANCELLED...',
  `audit_admin_id` int NULL DEFAULT NULL COMMENT '审批管理员ID',
  `audit_time` datetime NULL DEFAULT NULL COMMENT '审批时间',
  `reject_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '驳回原因',
  `timeout_at` datetime NULL DEFAULT NULL COMMENT '自动驳回截止时间',
  `version` int NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  `consume_log_no` bigint NULL DEFAULT NULL COMMENT '本次预约消耗的库存流水号',
  `duration_minutes` int NULL DEFAULT 60 COMMENT '预约时长(分钟)',
  `purchase_no` int NULL DEFAULT NULL COMMENT '关联的购课单号',
  PRIMARY KEY (`booking_no`) USING BTREE,
  UNIQUE INDEX `uk_vb_venue_date_slot_status`(`venue_no` ASC, `booking_date` ASC, `time_slot` ASC, `status` ASC) USING BTREE,
  INDEX `idx_member_no`(`member_no` ASC) USING BTREE,
  INDEX `idx_venue_no`(`venue_no` ASC) USING BTREE,
  INDEX `idx_vb_date_status`(`booking_date` ASC, `status` ASC) USING BTREE,
  INDEX `idx_vb_venue_date`(`venue_no` ASC, `booking_date` ASC) USING BTREE,
  INDEX `idx_venue_time_status`(`venue_no` ASC, `start_time` ASC, `end_time` ASC, `status` ASC) USING BTREE,
  INDEX `idx_member_time_status`(`member_no` ASC, `start_time` ASC, `end_time` ASC, `status` ASC) USING BTREE,
  INDEX `idx_status_timeout`(`status` ASC, `timeout_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 64 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '场地预约记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of venue_booking
-- ----------------------------
INSERT INTO `venue_booking` VALUES (53, 6, 1, 1, '2026-02-12 18:00:00', '2026-02-12 19:00:00', '18:00-19:00', '2026-02-12 18:50:41', '2026-02-12', '6', NULL, '2026-02-12 18:50:52', NULL, '2026-02-13 18:50:41', 2, 57, 60, NULL);
INSERT INTO `venue_booking` VALUES (54, 6, 1, 1, '2026-02-18 18:00:00', '2026-02-18 19:00:00', '18:00-19:00', '2026-02-12 18:52:11', '2026-02-18', '6', NULL, '2026-02-12 19:06:26', NULL, '2026-02-13 18:52:11', 2, 59, 60, NULL);
INSERT INTO `venue_booking` VALUES (55, 6, 1, 1, '2026-02-18 20:30:00', '2026-02-18 21:30:00', '20:30-21:30', '2026-02-12 18:52:12', '2026-02-18', '6', NULL, '2026-02-12 18:52:20', NULL, '2026-02-13 18:52:13', 2, 58, 60, NULL);
INSERT INTO `venue_booking` VALUES (56, 6, 7, 1, '2026-02-18 19:00:00', '2026-02-18 20:00:00', '19:00-20:00', '2026-02-12 18:54:28', '2026-02-18', '6', NULL, '2026-02-12 18:54:33', NULL, '2026-02-13 18:54:28', 2, 61, 60, NULL);
INSERT INTO `venue_booking` VALUES (57, 6, 1, 1, '2026-02-12 18:00:00', '2026-02-12 19:00:00', '18:00-19:00', '2026-02-12 20:09:11', '2026-02-12', '2', NULL, '2026-02-12 20:09:36', '1', '2026-02-13 20:09:12', 1, NULL, 60, NULL);
INSERT INTO `venue_booking` VALUES (58, 6, 1, 1, '2026-02-14 18:00:00', '2026-02-14 19:00:00', '18:00-19:00', '2026-02-13 14:40:18', '2026-02-14', '6', NULL, '2026-02-13 14:41:26', NULL, '2026-02-14 14:40:18', 2, 63, 60, NULL);
INSERT INTO `venue_booking` VALUES (59, 6, 1, 1, '2026-02-13 18:00:00', '2026-02-13 19:00:00', '18:00-19:00', '2026-02-13 21:30:16', '2026-02-13', '6', NULL, '2026-02-13 21:31:00', NULL, '2026-02-14 21:30:17', 2, 65, 60, NULL);
INSERT INTO `venue_booking` VALUES (60, 6, 2, 1, '2026-02-13 18:00:00', '2026-02-13 19:00:00', '18:00-19:00', '2026-02-13 21:40:32', '2026-02-13', '1', NULL, '2026-02-13 21:40:37', NULL, '2026-02-14 21:40:32', 1, 67, 60, NULL);
INSERT INTO `venue_booking` VALUES (61, 6, 1, 3, '2026-02-13 17:00:00', '2026-02-13 18:00:00', '17:00-18:00', '2026-02-13 21:45:33', '2026-02-13', '6', NULL, '2026-02-13 21:45:38', NULL, '2026-02-14 21:45:34', 2, 69, 60, NULL);
INSERT INTO `venue_booking` VALUES (62, 6, 1, 2, '2026-02-20 18:00:00', '2026-02-20 19:00:00', '18:00-19:00', '2026-02-13 21:46:15', '2026-02-20', '6', NULL, '2026-02-13 21:46:22', NULL, '2026-02-14 21:46:16', 2, 71, 60, NULL);
INSERT INTO `venue_booking` VALUES (63, 6, 2, 2, '2026-02-13 09:00:00', '2026-02-13 10:00:00', '09:00-10:00', '2026-02-13 22:15:55', '2026-02-13', '1', NULL, '2026-02-13 22:16:05', NULL, '2026-02-14 22:15:56', 1, 73, 60, NULL);

-- ----------------------------
-- Table structure for venue_slot_lock
-- ----------------------------
DROP TABLE IF EXISTS `venue_slot_lock`;
CREATE TABLE `venue_slot_lock`  (
  `lock_no` bigint NOT NULL AUTO_INCREMENT,
  `venue_no` int NOT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `lock_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'MAINTAIN' COMMENT '锁定类型：MAINTAIN/CLEAN/EVENT',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `status` tinyint NULL DEFAULT 0 COMMENT '0有效/1解除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`lock_no`) USING BTREE,
  INDEX `idx_venue_lock_time`(`venue_no` ASC, `start_time` ASC, `end_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of venue_slot_lock
-- ----------------------------

-- ----------------------------
-- View structure for v_finance_overview
-- ----------------------------
DROP VIEW IF EXISTS `v_finance_overview`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_finance_overview` AS select `fl`.`member_no` AS `member_no`,`fl`.`biz_type` AS `biz_type`,`fl`.`biz_no` AS `biz_no`,`fl`.`direction` AS `direction`,`fl`.`amount` AS `amount`,`fl`.`channel` AS `channel`,`fl`.`create_time` AS `create_time`,(case when (`fl`.`direction` = 'IN') then '预收账款-收到现金' when ((`fl`.`direction` = 'NONE') and (`fl`.`biz_type` in ('CARD_RENEW','PURCHASE'))) then '预收账款-内部划转' when (`fl`.`direction` = 'DEFERRED') then '已实现收入-预收转收入' else '其他' end) AS `account_type`,(case when (`fl`.`direction` = 'DEFERRED') then `fl`.`amount` else 0 end) AS `realized_revenue` from `finance_ledger` `fl`;

-- ----------------------------
-- View structure for v_monthly_revenue_accrual
-- ----------------------------
DROP VIEW IF EXISTS `v_monthly_revenue_accrual`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_monthly_revenue_accrual` AS select `t`.`period` AS `period`,sum((case when (`t`.`biz_type` = 'RECHARGE') then `t`.`amount` else 0 end)) AS `recharge_amount`,sum((case when (`t`.`biz_type` = 'CARD_RENEW') then `t`.`amount` else 0 end)) AS `card_renew_amount`,sum((case when (`t`.`biz_type` = 'PURCHASE') then `t`.`amount` else 0 end)) AS `purchase_amount`,sum((case when (`t`.`biz_type` = 'COURSE_RECOGNIZE') then `t`.`amount` else 0 end)) AS `course_recognize`,sum((case when (`t`.`biz_type` = 'CARD_RECOGNIZE') then `t`.`amount` else 0 end)) AS `card_recognize`,(select coalesce(sum(`rr`.`amount`),0) from `revenue_recognition` `rr` where (date_format(`rr`.`create_time`,'%Y-%m') = `rr`.`period`)) AS `total_realized_revenue` from (select 'RECHARGE' AS `biz_type`,date_format(`finance_ledger`.`create_time`,'%Y-%m') AS `period`,`finance_ledger`.`amount` AS `amount` from `finance_ledger` where ((`finance_ledger`.`direction` = 'IN') and (`finance_ledger`.`biz_type` = 'RECHARGE')) union all select 'CARD_RENEW' AS `biz_type`,date_format(`finance_ledger`.`create_time`,'%Y-%m') AS `period`,`finance_ledger`.`amount` AS `amount` from `finance_ledger` where ((`finance_ledger`.`direction` = 'IN') and (`finance_ledger`.`biz_type` = 'CARD_RENEW') and (`finance_ledger`.`channel` = 'CASH')) union all select 'CARD_RENEW' AS `biz_type`,date_format(`finance_ledger`.`create_time`,'%Y-%m') AS `period`,`finance_ledger`.`amount` AS `amount` from `finance_ledger` where ((`finance_ledger`.`direction` = 'NONE') and (`finance_ledger`.`biz_type` = 'CARD_RENEW') and (`finance_ledger`.`channel` = 'BALANCE')) union all select 'PURCHASE' AS `biz_type`,date_format(`finance_ledger`.`create_time`,'%Y-%m') AS `period`,`finance_ledger`.`amount` AS `amount` from `finance_ledger` where ((`finance_ledger`.`direction` = 'NONE') and (`finance_ledger`.`biz_type` = 'PURCHASE')) union all select 'COURSE_RECOGNIZE' AS `biz_type`,date_format(`finance_ledger`.`create_time`,'%Y-%m') AS `period`,`finance_ledger`.`amount` AS `amount` from `finance_ledger` where ((`finance_ledger`.`direction` = 'DEFERRED') and (`finance_ledger`.`biz_type` = 'COURSE_RECOGNIZE')) union all select 'CARD_RECOGNIZE' AS `biz_type`,date_format(`finance_ledger`.`create_time`,'%Y-%m') AS `period`,`finance_ledger`.`amount` AS `amount` from `finance_ledger` where ((`finance_ledger`.`direction` = 'DEFERRED') and (`finance_ledger`.`biz_type` = 'CARD_RECOGNIZE'))) `t` group by `t`.`period` order by `t`.`period` desc;

SET FOREIGN_KEY_CHECKS = 1;
