/*
Navicat MySQL Data Transfer

Source Server         : 10.19.34.3_ehr_admin
Source Server Version : 50639
Source Host           : 10.19.34.3:3306
Source Database       : attend_base_dev

Target Server Type    : MYSQL
Target Server Version : 50639
File Encoding         : 65001

Date: 2020-08-28 16:29:36
*/

-- SET FOREIGN_KEY_CHECKS=0;



-- ----------------------------
-- Table structure for `qrtz_CALENDARS`
-- ----------------------------
-- DROP TABLE IF EXISTS `qrtz_CALENDARS`;
CREATE TABLE `qrtz_CALENDARS` (
                                  `SCHED_NAME` varchar(120) NOT NULL COMMENT '计划名称',
                                  `CALENDAR_NAME` varchar(200) NOT NULL,
                                  `CALENDAR` blob NOT NULL,
                                  PRIMARY KEY (`SCHED_NAME`,`CALENDAR_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='日历信息';

-- ----------------------------
-- Records of qrtz_CALENDARS
-- ----------------------------



-- ----------------------------
-- Table structure for `qrtz_FIRED_TRIGGERS`
-- ----------------------------
-- DROP TABLE IF EXISTS `qrtz_FIRED_TRIGGERS`;
CREATE TABLE `qrtz_FIRED_TRIGGERS` (
                                       `SCHED_NAME` varchar(120) NOT NULL COMMENT '计划名称',
                                       `ENTRY_ID` varchar(95) NOT NULL COMMENT '组标识',
                                       `TRIGGER_NAME` varchar(200) NOT NULL COMMENT '触发器名称',
                                       `TRIGGER_GROUP` varchar(200) NOT NULL COMMENT '触发器组',
                                       `INSTANCE_NAME` varchar(200) NOT NULL COMMENT '当前实例的名称',
                                       `FIRED_TIME` bigint(13) NOT NULL COMMENT '当前执行时间',
                                       `SCHED_TIME` bigint(13) NOT NULL COMMENT '计划时间',
                                       `PRIORITY` int(11) NOT NULL COMMENT '权重',
                                       `STATE` varchar(16) NOT NULL COMMENT '状态：WAITING:等待 \r\nPAUSED:暂停 \r\nACQUIRED:正常执行 \r\nBLOCKED：阻塞 \r\nERROR：错误',
                                       `JOB_NAME` varchar(200) DEFAULT NULL COMMENT '作业名称',
                                       `JOB_GROUP` varchar(200) DEFAULT NULL COMMENT '作业组',
                                       `IS_NONCONCURRENT` varchar(1) DEFAULT NULL COMMENT '是否并行',
                                       `REQUESTS_RECOVERY` varchar(1) DEFAULT NULL COMMENT '是否要求唤醒',
                                       PRIMARY KEY (`SCHED_NAME`,`ENTRY_ID`),
                                       KEY `IDX_qrtz_FT_TRIG_INST_NAME` (`SCHED_NAME`,`INSTANCE_NAME`),
                                       KEY `IDX_qrtz_FT_INST_JOB_REQ_RCVRY` (`SCHED_NAME`,`INSTANCE_NAME`,`REQUESTS_RECOVERY`),
                                       KEY `IDX_qrtz_FT_J_G` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
                                       KEY `IDX_qrtz_FT_JG` (`SCHED_NAME`,`JOB_GROUP`),
                                       KEY `IDX_qrtz_FT_T_G` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
                                       KEY `IDX_qrtz_FT_TG` (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='保存已经触发的触发器状态信息';

-- ----------------------------
-- Records of qrtz_FIRED_TRIGGERS
-- ----------------------------

-- ----------------------------
-- Table structure for `qrtz_JOB_DETAILS`
-- ----------------------------
-- DROP TABLE IF EXISTS `qrtz_JOB_DETAILS`;
CREATE TABLE `qrtz_JOB_DETAILS` (
                                    `SCHED_NAME` varchar(120) NOT NULL COMMENT '计划名称',
                                    `JOB_NAME` varchar(200) NOT NULL COMMENT '集群中job的名字',
                                    `JOB_GROUP` varchar(200) NOT NULL COMMENT '集群中job的所属组的名字',
                                    `DESCRIPTION` varchar(250) DEFAULT NULL COMMENT '描述',
                                    `JOB_CLASS_NAME` varchar(250) NOT NULL COMMENT '作业程序类名',
                                    `IS_DURABLE` varchar(1) NOT NULL COMMENT '是否持久',
                                    `IS_NONCONCURRENT` varchar(1) NOT NULL COMMENT '是否并行',
                                    `IS_UPDATE_DATA` varchar(1) NOT NULL COMMENT '是否更新',
                                    `REQUESTS_RECOVERY` varchar(1) NOT NULL COMMENT '是否要求唤醒',
                                    `JOB_DATA` blob,
                                    PRIMARY KEY (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
                                    KEY `IDX_qrtz_J_REQ_RECOVERY` (`SCHED_NAME`,`REQUESTS_RECOVERY`),
                                    KEY `IDX_qrtz_J_GRP` (`SCHED_NAME`,`JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='job 详细信息';

-- ----------------------------
-- Records of qrtz_JOB_DETAILS
-- ----------------------------
-- ----------------------------
-- Table structure for `qrtz_LOCKS`
-- ----------------------------
-- DROP TABLE IF EXISTS `qrtz_LOCKS`;
CREATE TABLE `qrtz_LOCKS` (
                              `SCHED_NAME` varchar(120) NOT NULL COMMENT '计划名称',
                              `LOCK_NAME` varchar(40) NOT NULL COMMENT '锁名称',
                              PRIMARY KEY (`SCHED_NAME`,`LOCK_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='存储程序的悲观锁的信息(假如使用了悲观锁) ';

-- ----------------------------
-- Records of qrtz_LOCKS

-- ----------------------------
-- Table structure for `qrtz_PAUSED_TRIGGER_GRPS`
-- ----------------------------
-- DROP TABLE IF EXISTS `qrtz_PAUSED_TRIGGER_GRPS`;
CREATE TABLE `qrtz_PAUSED_TRIGGER_GRPS` (
                                            `SCHED_NAME` varchar(120) NOT NULL COMMENT '计划名称',
                                            `TRIGGER_GROUP` varchar(200) NOT NULL COMMENT '触发器组',
                                            PRIMARY KEY (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='存放暂停掉的触发器';

-- ----------------------------
-- Records of qrtz_PAUSED_TRIGGER_GRPS
-- ----------------------------

-- ----------------------------
-- Table structure for `qrtz_SCHEDULER_STATE`
-- ----------------------------
-- DROP TABLE IF EXISTS `qrtz_SCHEDULER_STATE`;
CREATE TABLE `qrtz_SCHEDULER_STATE` (
                                        `SCHED_NAME` varchar(120) NOT NULL COMMENT '计划名称',
                                        `INSTANCE_NAME` varchar(200) NOT NULL COMMENT '实例名称',
                                        `LAST_CHECKIN_TIME` bigint(13) NOT NULL COMMENT '最后的检查时间',
                                        `CHECKIN_INTERVAL` bigint(13) NOT NULL COMMENT '检查间隔',
                                        PRIMARY KEY (`SCHED_NAME`,`INSTANCE_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='调度器状态';

-- ----------------------------
-- Records of qrtz_SCHEDULER_STATE



-- ----------------------------
-- Table structure for `qrtz_TRIGGERS`
-- ----------------------------
-- DROP TABLE IF EXISTS `qrtz_TRIGGERS`;
CREATE TABLE `qrtz_TRIGGERS` (
                                 `SCHED_NAME` varchar(120) NOT NULL COMMENT '计划名称',
                                 `TRIGGER_NAME` varchar(200) NOT NULL COMMENT '触发器名称',
                                 `TRIGGER_GROUP` varchar(200) NOT NULL COMMENT '触发器组',
                                 `JOB_NAME` varchar(200) NOT NULL COMMENT '作业名称',
                                 `JOB_GROUP` varchar(200) NOT NULL COMMENT '作业组',
                                 `DESCRIPTION` varchar(250) DEFAULT NULL COMMENT '描述',
                                 `NEXT_FIRE_TIME` bigint(13) DEFAULT NULL COMMENT '下次执行时间',
                                 `PREV_FIRE_TIME` bigint(13) DEFAULT NULL COMMENT '前一次',
                                 `PRIORITY` int(11) DEFAULT NULL COMMENT '优先权',
                                 `TRIGGER_STATE` varchar(16) NOT NULL COMMENT '触发器状态',
                                 `TRIGGER_TYPE` varchar(8) NOT NULL COMMENT '触发器类型',
                                 `START_TIME` bigint(13) NOT NULL COMMENT '开始时间',
                                 `END_TIME` bigint(13) DEFAULT NULL COMMENT '结束时间',
                                 `CALENDAR_NAME` varchar(200) DEFAULT NULL COMMENT '日历名称',
                                 `MISFIRE_INSTR` smallint(2) DEFAULT NULL COMMENT '失败次数',
                                 `JOB_DATA` blob,
                                 PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
                                 KEY `IDX_qrtz_T_J` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
                                 KEY `IDX_qrtz_T_JG` (`SCHED_NAME`,`JOB_GROUP`),
                                 KEY `IDX_qrtz_T_C` (`SCHED_NAME`,`CALENDAR_NAME`),
                                 KEY `IDX_qrtz_T_G` (`SCHED_NAME`,`TRIGGER_GROUP`),
                                 KEY `IDX_qrtz_T_STATE` (`SCHED_NAME`,`TRIGGER_STATE`),
                                 KEY `IDX_qrtz_T_N_STATE` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
                                 KEY `IDX_qrtz_T_N_G_STATE` (`SCHED_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
                                 KEY `IDX_qrtz_T_NEXT_FIRE_TIME` (`SCHED_NAME`,`NEXT_FIRE_TIME`),
                                 KEY `IDX_qrtz_T_NFT_ST` (`SCHED_NAME`,`TRIGGER_STATE`,`NEXT_FIRE_TIME`),
                                 KEY `IDX_qrtz_T_NFT_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`),
                                 KEY `IDX_qrtz_T_NFT_ST_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_STATE`),
                                 KEY `IDX_qrtz_T_NFT_ST_MISFIRE_GRP` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_GROUP`,`TRIGGER_STATE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='触发器';

-- ----------------------------
-- Records of qrtz_TRIGGERS



-- ----------------------------
-- Table structure for `qrtz_SIMPLE_TRIGGERS`
-- ----------------------------
-- DROP TABLE IF EXISTS `qrtz_SIMPLE_TRIGGERS`;
CREATE TABLE `qrtz_SIMPLE_TRIGGERS` (
                                        `SCHED_NAME` varchar(120) NOT NULL COMMENT '计划名称',
                                        `TRIGGER_NAME` varchar(200) NOT NULL COMMENT '触发器名称',
                                        `TRIGGER_GROUP` varchar(200) NOT NULL COMMENT '触发器组',
                                        `REPEAT_COUNT` bigint(7) NOT NULL COMMENT '重复次数',
                                        `REPEAT_INTERVAL` bigint(12) NOT NULL COMMENT '重复间隔',
                                        `TIMES_TRIGGERED` bigint(10) NOT NULL COMMENT '触发次数',
                                        PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='简单的触发器';

-- ----------------------------
-- Records of qrtz_SIMPLE_TRIGGERS
-- ----------------------------



-- ----------------------------
-- Table structure for `qrtz_SIMPROP_TRIGGERS`
-- ----------------------------
-- DROP TABLE IF EXISTS `qrtz_SIMPROP_TRIGGERS`;
CREATE TABLE `qrtz_SIMPROP_TRIGGERS` (
                                         `SCHED_NAME` varchar(120) NOT NULL COMMENT '计划名称',
                                         `TRIGGER_NAME` varchar(200) NOT NULL COMMENT '触发器名称',
                                         `TRIGGER_GROUP` varchar(200) NOT NULL COMMENT '触发器组',
                                         `STR_PROP_1` varchar(512) DEFAULT NULL,
                                         `STR_PROP_2` varchar(512) DEFAULT NULL,
                                         `STR_PROP_3` varchar(512) DEFAULT NULL,
                                         `INT_PROP_1` int(11) DEFAULT NULL,
                                         `INT_PROP_2` int(11) DEFAULT NULL,
                                         `LONG_PROP_1` bigint(20) DEFAULT NULL,
                                         `LONG_PROP_2` bigint(20) DEFAULT NULL,
                                         `DEC_PROP_1` decimal(13,4) DEFAULT NULL,
                                         `DEC_PROP_2` decimal(13,4) DEFAULT NULL,
                                         `BOOL_PROP_1` varchar(1) DEFAULT NULL,
                                         `BOOL_PROP_2` varchar(1) DEFAULT NULL,
                                         PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='存储CalendarIntervalTrigger和DailyTimeIntervalTrigger两种类型的触发器';

-- ----------------------------
-- Records of qrtz_SIMPROP_TRIGGERS
-- ----------------------------


-- ----------------------------
-- Table structure for `qrtz_BLOB_TRIGGERS`
-- ----------------------------
-- DROP TABLE IF EXISTS `qrtz_BLOB_TRIGGERS`;
CREATE TABLE `qrtz_BLOB_TRIGGERS` (
                                      `SCHED_NAME` varchar(120) NOT NULL COMMENT '计划名',
                                      `TRIGGER_NAME` varchar(200) NOT NULL COMMENT '触发器名称',
                                      `TRIGGER_GROUP` varchar(200) NOT NULL COMMENT '触发器组',
                                      `BLOB_DATA` blob,
                                      PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
                                      KEY `SCHED_NAME` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='以Blob 类型存储的触发器';

-- ----------------------------
-- Records of qrtz_BLOB_TRIGGERS
-- ----------------------------



-- ----------------------------
-- Table structure for `qrtz_CRON_TRIGGERS`
-- ----------------------------
-- DROP TABLE IF EXISTS `qrtz_CRON_TRIGGERS`;
CREATE TABLE `qrtz_CRON_TRIGGERS` (
                                      `SCHED_NAME` varchar(120) NOT NULL COMMENT '计划名称',
                                      `TRIGGER_NAME` varchar(200) NOT NULL COMMENT '触发器名称',
                                      `TRIGGER_GROUP` varchar(200) NOT NULL COMMENT '触发器组',
                                      `CRON_EXPRESSION` varchar(120) NOT NULL COMMENT '时间表达式',
                                      `TIME_ZONE_ID` varchar(80) DEFAULT NULL COMMENT '时区ID     nvarchar     80',
                                      PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='定时触发器';

-- ----------------------------
-- Records of qrtz_CRON_TRIGGERS
-- ----------------------------
