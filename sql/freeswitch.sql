CREATE TABLE cdr_table_a (
    uuid VARCHAR(255) COMMENT 'uuid',
    call_uuid VARCHAR(255) COMMENT 'uuid',
    caller_id_name VARCHAR(255) COMMENT '主叫名称',
    caller_id_number VARCHAR(255) COMMENT '主叫号码',
    destination_number VARCHAR(255) COMMENT '被叫号码',
    start_stamp DATETIME COMMENT '呼叫时间',
    answer_stamp DATETIME COMMENT '应答时间',
    end_stamp DATETIME COMMENT '结束时间',
    uduration INT COMMENT '呼叫时长',
    local_ip_v4 VARCHAR(255) COMMENT 'IP地址',
    billsec INT COMMENT 'billsec',
    hangup_cause VARCHAR(255) COMMENT '挂断原因',
    sip_network_ip VARCHAR(255) COMMENT 'sip ip'
)ENGINE = InnoDB DEFAULT CHARSET = utf8  ROW_FORMAT = DYNAMIC COMMENT ='呼叫数据';
CREATE TABLE cdr_table_b (
    uuid VARCHAR(255) COMMENT 'uuid',
    call_uuid VARCHAR(255) COMMENT 'uuid',
    caller_id_name VARCHAR(255) COMMENT '主叫名称',
    caller_id_number VARCHAR(255) COMMENT '主叫号码',
    destination_number VARCHAR(255) COMMENT '被叫号码',
    start_stamp DATETIME COMMENT '呼叫时间',
    answer_stamp DATETIME COMMENT '应答时间',
    end_stamp DATETIME COMMENT '结束时间',
    uduration INT COMMENT '呼叫时长',
    local_ip_v4 VARCHAR(255) COMMENT 'IP地址',
    billsec INT COMMENT 'billsec',
    hangup_cause VARCHAR(255) COMMENT '挂断原因',
    sip_network_ip VARCHAR(255) COMMENT 'sip ip'
    )ENGINE = InnoDB DEFAULT CHARSET = utf8  ROW_FORMAT = DYNAMIC COMMENT ='接听数据';
    CREATE TABLE cdr_table_ab (
    uuid VARCHAR(255) COMMENT 'uuid',
    call_uuid VARCHAR(255) COMMENT 'uuid',
    caller_id_name VARCHAR(255) COMMENT '主叫名称',
    caller_id_number VARCHAR(255) COMMENT '主叫号码',
    destination_number VARCHAR(255) COMMENT '被叫号码',
    start_stamp DATETIME COMMENT '呼叫时间',
    answer_stamp DATETIME COMMENT '应答时间',
    end_stamp DATETIME COMMENT '结束时间',
    uduration INT COMMENT '呼叫时长',
    local_ip_v4 VARCHAR(255) COMMENT 'IP地址',
    billsec INT COMMENT 'billsec',
    hangup_cause VARCHAR(255) COMMENT '挂断原因',
    sip_network_ip VARCHAR(255) COMMENT 'sip ip'
)ENGINE = InnoDB DEFAULT CHARSET = utf8  ROW_FORMAT = DYNAMIC COMMENT ='通话数据';

CREATE TABLE `aliases`  (
    `sticky` int NULL DEFAULT NULL,
    `alias` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `command` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `hostname` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    INDEX `alias1`(`alias`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE `calls`  (
    `call_uuid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `call_created` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `call_created_epoch` int NULL DEFAULT NULL,
    `caller_uuid` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `callee_uuid` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `hostname` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    INDEX `callsidx1`(`hostname`) USING BTREE,
    INDEX `eruuindex`(`caller_uuid`, `hostname`) USING BTREE,
    INDEX `eeuuindex`(`callee_uuid`) USING BTREE,
    INDEX `eeuuindex2`(`call_uuid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE `channels`  (
    `uuid` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `direction` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `created` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `created_epoch` int NULL DEFAULT NULL,
    `name` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `state` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `cid_name` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `cid_num` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `ip_addr` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `dest` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `application` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `application_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL,
    `dialplan` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `context` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `read_codec` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `read_rate` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `read_bit_rate` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `write_codec` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `write_rate` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `write_bit_rate` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `secure` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `hostname` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `presence_id` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `presence_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL,
    `accountcode` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `callstate` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `callee_name` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `callee_num` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `callee_direction` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `call_uuid` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `sent_callee_name` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `sent_callee_num` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `initial_cid_name` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `initial_cid_num` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `initial_ip_addr` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `initial_dest` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `initial_dialplan` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `initial_context` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
INDEX `chidx1`(`hostname`) USING BTREE,
INDEX `uuindex`(`uuid`, `hostname`) USING BTREE,
INDEX `uuindex2`(`call_uuid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE `complete`  (
    `sticky` int NULL DEFAULT NULL,
    `a1` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `a2` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `a3` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `a4` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `a5` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `a6` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `a7` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `a8` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `a9` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `a10` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `hostname` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    INDEX `complete1`(`a1`, `hostname`) USING BTREE,
    INDEX `complete2`(`a2`, `hostname`) USING BTREE,
    INDEX `complete3`(`a3`, `hostname`) USING BTREE,
    INDEX `complete4`(`a4`, `hostname`) USING BTREE,
    INDEX `complete5`(`a5`, `hostname`) USING BTREE,
    INDEX `complete6`(`a6`, `hostname`) USING BTREE,
    INDEX `complete7`(`a7`, `hostname`) USING BTREE,
    INDEX `complete8`(`a8`, `hostname`) USING BTREE,
    INDEX `complete9`(`a9`, `hostname`) USING BTREE,
    INDEX `complete10`(`a10`, `hostname`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE `fifo_bridge`  (
    `fifo_name` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NOT NULL,
    `caller_uuid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NOT NULL,
    `caller_caller_id_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `caller_caller_id_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `consumer_uuid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NOT NULL,
    `consumer_outgoing_uuid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `bridge_start` int NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE `fifo_callers`  (
    `fifo_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NOT NULL,
    `uuid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NOT NULL,
    `caller_caller_id_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `caller_caller_id_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `timestamp` int NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE `interfaces`  (
    `type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `name` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `description` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `ikey` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `filename` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `syntax` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `hostname` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;
CREATE TABLE `nat`  (
    `sticky` int NULL DEFAULT NULL,
    `port` int NULL DEFAULT NULL,
    `proto` int NULL DEFAULT NULL,
    `hostname` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    INDEX `nat_map_port_proto`(`port`, `proto`, `hostname`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE `recovery`  (
    `runtime_uuid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `technology` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `profile_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `hostname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `uuid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `metadata` text CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL,
    INDEX `recovery1`(`technology`) USING BTREE,
    INDEX `recovery2`(`profile_name`) USING BTREE,
    INDEX `recovery3`(`uuid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE `registrations`  (
    `reg_user` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `realm` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `token` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL,
    `expires` int NULL DEFAULT NULL,
    `network_ip` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `network_port` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `network_proto` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `hostname` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `metadata` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    INDEX `regindex1`(`reg_user`, `realm`, `hostname`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE `sip_authentication`  (
    `nonce` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `expires` bigint NULL DEFAULT NULL,
    `profile_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `hostname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `last_nc` int NULL DEFAULT NULL,
    INDEX `sa_nonce`(`nonce`) USING BTREE,
    INDEX `sa_hostname`(`hostname`) USING BTREE,
    INDEX `sa_expires`(`expires`) USING BTREE,
    INDEX `sa_last_nc`(`last_nc`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE `sip_dialogs`  (
    `call_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `uuid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `sip_to_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `sip_to_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `sip_from_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `sip_from_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `contact_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `contact_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `state` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `direction` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `user_agent` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `profile_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `hostname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `contact` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `presence_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `presence_data` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `call_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `call_info_state` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT '',
    `expires` bigint NULL DEFAULT 0,
    `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `rpid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `sip_to_tag` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `sip_from_tag` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `rcd` int NOT NULL DEFAULT 0,
    INDEX `sd_uuid`(`uuid`) USING BTREE,
    INDEX `sd_hostname`(`hostname`) USING BTREE,
    INDEX `sd_presence_data`(`presence_data`) USING BTREE,
    INDEX `sd_call_info`(`call_info`) USING BTREE,
    INDEX `sd_call_info_state`(`call_info_state`) USING BTREE,
    INDEX `sd_expires`(`expires`) USING BTREE,
    INDEX `sd_rcd`(`rcd`) USING BTREE,
    INDEX `sd_sip_to_tag`(`sip_to_tag`) USING BTREE,
    INDEX `sd_sip_from_user`(`sip_from_user`) USING BTREE,
    INDEX `sd_sip_from_host`(`sip_from_host`) USING BTREE,
    INDEX `sd_sip_to_host`(`sip_to_host`) USING BTREE,
    INDEX `sd_presence_id`(`presence_id`) USING BTREE,
    INDEX `sd_call_id`(`call_id`) USING BTREE,
    INDEX `sd_sip_from_tag`(`sip_from_tag`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE `sip_presence`  (
    `sip_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `sip_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `rpid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `expires` bigint NULL DEFAULT NULL,
    `user_agent` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `profile_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `hostname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `network_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `network_port` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `open_closed` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    INDEX `sp_hostname`(`hostname`) USING BTREE,
    INDEX `sp_open_closed`(`open_closed`) USING BTREE,
    INDEX `sp_sip_user`(`sip_user`) USING BTREE,
    INDEX `sp_sip_host`(`sip_host`) USING BTREE,
    INDEX `sp_profile_name`(`profile_name`) USING BTREE,
    INDEX `sp_expires`(`expires`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE `sip_registrations`  (
    `call_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `sip_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `sip_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `presence_hosts` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `contact` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `ping_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `ping_count` int NULL DEFAULT NULL,
    `ping_time` bigint NULL DEFAULT NULL,
    `force_ping` int NULL DEFAULT NULL,
    `rpid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `expires` bigint NULL DEFAULT NULL,
    `ping_expires` int NOT NULL DEFAULT 0,
    `user_agent` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `server_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `server_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `profile_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `hostname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `network_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `network_port` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `sip_username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `sip_realm` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `mwi_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `mwi_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `orig_server_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `orig_hostname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `sub_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    INDEX `sr_call_id`(`call_id`) USING BTREE,
    INDEX `sr_sip_user`(`sip_user`) USING BTREE,
    INDEX `sr_sip_host`(`sip_host`) USING BTREE,
    INDEX `sr_sub_host`(`sub_host`) USING BTREE,
    INDEX `sr_mwi_user`(`mwi_user`) USING BTREE,
    INDEX `sr_mwi_host`(`mwi_host`) USING BTREE,
    INDEX `sr_profile_name`(`profile_name`) USING BTREE,
    INDEX `sr_presence_hosts`(`presence_hosts`) USING BTREE,
    INDEX `sr_expires`(`expires`) USING BTREE,
    INDEX `sr_ping_expires`(`ping_expires`) USING BTREE,
    INDEX `sr_hostname`(`hostname`) USING BTREE,
    INDEX `sr_status`(`status`) USING BTREE,
    INDEX `sr_ping_status`(`ping_status`) USING BTREE,
    INDEX `sr_network_ip`(`network_ip`) USING BTREE,
    INDEX `sr_network_port`(`network_port`) USING BTREE,
    INDEX `sr_sip_username`(`sip_username`) USING BTREE,
    INDEX `sr_sip_realm`(`sip_realm`) USING BTREE,
    INDEX `sr_orig_server_host`(`orig_server_host`) USING BTREE,
    INDEX `sr_orig_hostname`(`orig_hostname`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE `sip_shared_appearance_dialogs`  (
    `profile_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `hostname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `contact_str` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `call_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `network_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `expires` bigint NULL DEFAULT NULL,
    INDEX `ssd_profile_name`(`profile_name`) USING BTREE,
    INDEX `ssd_hostname`(`hostname`) USING BTREE,
    INDEX `ssd_contact_str`(`contact_str`) USING BTREE,
    INDEX `ssd_call_id`(`call_id`) USING BTREE,
    INDEX `ssd_expires`(`expires`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE `sip_shared_appearance_subscriptions`  (
    `subscriber` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `call_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `aor` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `profile_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `hostname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `contact_str` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `network_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    INDEX `ssa_hostname`(`hostname`) USING BTREE,
    INDEX `ssa_network_ip`(`network_ip`) USING BTREE,
    INDEX `ssa_subscriber`(`subscriber`) USING BTREE,
    INDEX `ssa_profile_name`(`profile_name`) USING BTREE,
    INDEX `ssa_aor`(`aor`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE `sip_subscriptions`  (
    `proto` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `sip_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `sip_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `sub_to_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `sub_to_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `presence_hosts` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `event` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `contact` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `call_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `full_from` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `full_via` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `expires` bigint NULL DEFAULT NULL,
    `user_agent` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `accept` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `profile_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `hostname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `network_port` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `network_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `version` int NOT NULL DEFAULT 0,
    `orig_proto` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `full_to` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    INDEX `ss_call_id`(`call_id`) USING BTREE,
    INDEX `ss_multi`(`call_id`, `profile_name`, `hostname`) USING BTREE,
    INDEX `ss_hostname`(`hostname`) USING BTREE,
    INDEX `ss_network_ip`(`network_ip`) USING BTREE,
    INDEX `ss_sip_user`(`sip_user`) USING BTREE,
    INDEX `ss_sip_host`(`sip_host`) USING BTREE,
    INDEX `ss_presence_hosts`(`presence_hosts`) USING BTREE,
    INDEX `ss_event`(`event`) USING BTREE,
    INDEX `ss_proto`(`proto`) USING BTREE,
    INDEX `ss_sub_to_user`(`sub_to_user`) USING BTREE,
    INDEX `ss_sub_to_host`(`sub_to_host`) USING BTREE,
    INDEX `ss_expires`(`expires`) USING BTREE,
    INDEX `ss_orig_proto`(`orig_proto`) USING BTREE,
    INDEX `ss_network_port`(`network_port`) USING BTREE,
    INDEX `ss_profile_name`(`profile_name`) USING BTREE,
    INDEX `ss_version`(`version`) USING BTREE,
    INDEX `ss_full_from`(`full_from`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE `tasks`  (
    `task_id` int NULL DEFAULT NULL,
    `task_desc` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `task_group` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `task_runtime` bigint NULL DEFAULT NULL,
    `task_sql_manager` int NULL DEFAULT NULL,
    `hostname` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    INDEX `tasks1`(`hostname`, `task_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE `user`  (
    `id` int NOT NULL AUTO_INCREMENT,
    `user` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分机号',
    `password` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分机号密码',
    `comment` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '分机号描述',
    `create_time` timestamp NULL DEFAULT NULL COMMENT '分机号创建时间',
    `update_time` timestamp NULL DEFAULT NULL COMMENT '分机号更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `user`(`user`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 90 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

CREATE TABLE `voicemail_msgs`  (
    `created_epoch` int NULL DEFAULT NULL,
    `read_epoch` int NULL DEFAULT NULL,
    `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `domain` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `uuid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `cid_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `cid_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `in_folder` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `message_len` int NULL DEFAULT NULL,
    `flags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `read_flags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `forwarded_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    INDEX `voicemail_msgs_idx1`(`created_epoch`) USING BTREE,
    INDEX `voicemail_msgs_idx2`(`username`) USING BTREE,
    INDEX `voicemail_msgs_idx3`(`domain`) USING BTREE,
    INDEX `voicemail_msgs_idx4`(`uuid`) USING BTREE,
    INDEX `voicemail_msgs_idx5`(`in_folder`) USING BTREE,
    INDEX `voicemail_msgs_idx6`(`read_flags`) USING BTREE,
    INDEX `voicemail_msgs_idx7`(`forwarded_by`) USING BTREE,
    INDEX `voicemail_msgs_idx8`(`read_epoch`) USING BTREE,
    INDEX `voicemail_msgs_idx9`(`flags`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE `voicemail_prefs`  (
    `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `domain` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `name_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `greeting_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    INDEX `voicemail_prefs_idx1`(`username`) USING BTREE,
    INDEX `voicemail_prefs_idx2`(`domain`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;
