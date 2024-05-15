CREATE TABLE fs_media_server (
    id                      varchar(255)  NOT NULL COMMENT '主键id',
    ip                      varchar(50)  NOT NULL COMMENT 'IP',
    ssl_status              tinyint(4)  default 0 COMMENT '是否https',
    hook_ip                 varchar(50)  NOT NULL COMMENT 'hook使用的IP（zlm访问WVP使用的IP）',
    sdp_ip                  varchar(50)  NOT NULL COMMENT 'SDP IP',
    stream_ip               varchar(50)  default null COMMENT '流IP',
    http_port               int unsigned default 0 COMMENT 'HTTP端口',
    http_ssl_port           int unsigned default 0 COMMENT 'HTTPS端口',
    rtmp_port               int unsigned default 0 COMMENT 'RTMP端口',
    rtmp_ssl_port           int unsigned default 0 COMMENT 'RTMPS端口',
    rtp_proxy_port          int unsigned default 0 COMMENT 'RTP收流端口（单端口模式有用）',
    rtsp_port               int unsigned default 0 COMMENT 'RTSP端口',
    rtsp_ssl_port           int unsigned default 0 COMMENT 'RTSPS端口',
    auto_config             tinyint(4) default 1 COMMENT '是否开启自动配置ZLM',
    secret                  varchar(50)  default null COMMENT 'ZLM鉴权参数',
    rtp_enable              tinyint(4) default 0 COMMENT '是否使用多端口模式',
    enable                  tinyint(4) default 0 COMMENT '启用状态',
    keepalive_time          datetime  COMMENT '心跳时间',
    status                  tinyint(4) default 0 COMMENT '状态',
    rtp_port_range          varchar(50)  default null COMMENT '多端口RTP收流端口范围',
    record_assist_port      int unsigned default 0 COMMENT 'assist服务端口',
    default_server          tinyint(4) default 0 COMMENT '是否是默认ZLM',
    hook_alive_interval     int unsigned default 30 COMMENT 'keepalive hook触发间隔,单位秒',
    video_play_prefix       varchar(50)  default null COMMENT '流媒体播放 代理前缀',
    video_http_prefix       varchar(50)  default null COMMENT 'video请求时前缀',
    create_user_id          bigint unsigned comment '创建人编号',
    create_time             datetime  NOT NULL COMMENT '创建时间',
    update_user_id          bigint unsigned comment '修改人编号',
    update_time             datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE,
    UNIQUE KEY idx_http_port (ip,http_port) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8  ROW_FORMAT = DYNAMIC COMMENT ='流媒体服务信息';

INSERT INTO fs_media_server(`id`, `ip`, `ssl_status`, `hook_ip`, `sdp_ip`, `stream_ip`, `http_port`, `http_ssl_port`, `rtmp_port`, `rtmp_ssl_port`, `rtp_proxy_port`, `rtsp_port`, `rtsp_ssl_port`, `auto_config`, `secret`, `rtp_enable`, `status`, `rtp_port_range`, `record_assist_port`, `default_server`, `hook_alive_interval`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('516549846516848451', '192.168.1.131', 0, '192.168.1.26:8660', '192.168.1.131', '192.168.1.131', 8080, 443, 1935, 19350, 10000, 554, 332, 1, '035c73f7-bb6b-4889-a715-d9eb2d1925cc', 1, 1, '30000-30010', 0, 1, 30, 1, '2023-05-16 14:50:54', NULL, '2023-05-19 16:30:59');


CREATE TABLE fs_platform (
    id                          bigint unsigned not null auto_increment COMMENT '主键id',
    local_ip                    varchar(30) DEFAULT null COMMENT '本机IP',
    remote_ip                   varchar(30) DEFAULT null COMMENT '外网IP',
    internal_port               int(6) unsigned DEFAULT '0' COMMENT '注册端口',
    external_port               int(6) unsigned DEFAULT '0' COMMENT '中继端口',
    start_rtp_port              int(6) unsigned DEFAULT '0' COMMENT '起始RTP端口',
    end_rtp_port                int(6) unsigned DEFAULT '0' COMMENT '结束RTP端口',
    ws_port                     int(6) unsigned DEFAULT '0' COMMENT 'ws端口',
    wss_port                    int(6) unsigned DEFAULT '0' COMMENT 'wss端口',
    audio_code                  varchar(256) DEFAULT '' COMMENT '音频编码',
    video_code                  varchar(256) DEFAULT '' COMMENT '视频编码',
    frame_rate                  varchar(30) DEFAULT '' COMMENT '分辨率',
    bit_rate                    varchar(30) DEFAULT '' COMMENT '码率',
    ice_start                   tinyint(4) DEFAULT 1 COMMENT '是否启动ice',
    stun_address                varchar(30) DEFAULT '' COMMENT 'stun地址',
    name                        varchar(255)  DEFAULT NULL COMMENT '名称',
    enable                      tinyint(4) default 1 COMMENT '是否启用',
    status                      tinyint(4) default 0 COMMENT '在线状态',
    audio_record                tinyint(4) DEFAULT 1 COMMENT '是否开启音频',
    video_record                tinyint(4) DEFAULT 1 COMMENT '是否开启视频',
    audio_record_path           varchar(256) DEFAULT '' COMMENT '音频存储地址',
    video_record_path           varchar(256) DEFAULT '' COMMENT '视频存储地址',
    sound_rile_path             varchar(256) DEFAULT '' COMMENT '声音文件地址',
    freeswitch_path             varchar(256) DEFAULT '' COMMENT '交换服务文件路径',
    freeswitch_log_path         varchar(256) DEFAULT '' COMMENT '交换服务日志路径',
    create_user_id              bigint unsigned comment '创建人编号',
    create_time                 datetime  NOT NULL COMMENT '创建时间',
    update_user_id              bigint unsigned comment '修改人编号',
    update_time                 datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8  ROW_FORMAT = DYNAMIC COMMENT ='平台信息' ;

insert into fs_platform(id,local_ip,remote_ip,internal_port,external_port,start_rtp_port,end_rtp_port,ws_port,wss_port,audio_code,video_code,
                        frame_rate,bit_rate,ice_start,stun_address,`name`,`enable`,`status`,audio_record,video_record,audio_record_path,
                        video_record_path,sound_rile_path,freeswitch_path,freeswitch_log_path,create_user_id,create_time,update_user_id,
                        update_time)
values(1,'192.168.1.26','192.168.1.26',5060,5080,16384,16484,5066,7443,null,null,null,null,1,'autonat:192.168.1.26','测试sip',1,0,1,1,'','','','','',1,now(),1,now());


CREATE TABLE fs_gate_way  (
   id                                  bigint unsigned not null auto_increment COMMENT '主键id',
   name                                varchar(30) DEFAULT '' COMMENT '网关名称',
   route_id                            varchar(30) DEFAULT null COMMENT '关联路由id',
   realm                               varchar(30) DEFAULT null COMMENT '服务器地址',
   register                            int(6) unsigned DEFAULT '0' COMMENT '是否注册',
   transport                           int(6) unsigned DEFAULT '0' COMMENT '传输类型',
   retry_seconds                       int(6) unsigned DEFAULT '0' COMMENT '重连间隔（秒）',
   username                            int(6) unsigned DEFAULT '0' COMMENT '账户',
   password                            varchar(256) DEFAULT '' COMMENT '密码',
   selected                            varchar(256) DEFAULT '' COMMENT '是否选中0:未选中，1：已选中',
   create_user_id                      bigint unsigned comment '创建人编号',
   create_time                         datetime  NOT NULL COMMENT '创建时间',
   update_user_id                      bigint unsigned comment '修改人编号',
   update_time                         datetime  NOT NULL COMMENT '更新时间',
   PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8  ROW_FORMAT = DYNAMIC COMMENT ='网关中继信息';


CREATE TABLE fs_route_group (
    id                              bigint unsigned not null auto_increment COMMENT '主键id',
    name                            varchar(255)  NOT NULL DEFAULT '' COMMENT '网关组名称',
    status                          tinyint(4) not null DEFAULT '1' COMMENT '状态',
    create_user_id                  bigint unsigned comment '创建人编号',
    create_time                     datetime  NOT NULL COMMENT '创建时间',
    update_user_id                  bigint unsigned comment '修改人编号',
    update_time                     datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='网关组';

CREATE TABLE fs_route_gateway_group (
    id                              bigint unsigned not null auto_increment COMMENT '主键id',
    route_group_id                  bigint unsigned NOT NULL DEFAULT '0' COMMENT '网关组',
    gateway_id                      bigint unsigned NOT NULL DEFAULT '0' COMMENT '媒体网关',
    create_user_id                  bigint unsigned comment '创建人编号',
    create_time                     datetime  NOT NULL COMMENT '创建时间',
    update_user_id                  bigint unsigned comment '修改人编号',
    update_time                     datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='路由与网关组关系表';

CREATE TABLE fs_route_gateway (
    id                              bigint unsigned not null auto_increment COMMENT '主键id',
    name                            varchar(255)  NOT NULL DEFAULT '' COMMENT '号码',
    media_host                      varchar(255)  NOT NULL DEFAULT '' COMMENT '媒体地址',
    media_port                      int unsigned NOT NULL DEFAULT '0' COMMENT '媒体端口',
    caller_prefix                   varchar(255)  NOT NULL DEFAULT '' COMMENT '主叫号码前缀',
    called_prefix                   varchar(255)  NOT NULL DEFAULT '' COMMENT '被叫号码前缀',
    profile                         varchar(255)  NOT NULL DEFAULT '' COMMENT 'fs的context规则',
    sip_header1                     varchar(255)  NOT NULL DEFAULT '' COMMENT 'sip头1',
    sip_header2                     varchar(255)  NOT NULL DEFAULT '' COMMENT 'sip头2',
    sip_header3                     varchar(255)  NOT NULL DEFAULT '' COMMENT 'sip头3',
    status                          tinyint(4) not null DEFAULT '1' COMMENT '状态',
    create_user_id                  bigint unsigned comment '创建人编号',
    create_time                     datetime  NOT NULL COMMENT '创建时间',
    update_user_id                  bigint unsigned comment '修改人编号',
    update_time                     datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE,
    UNIQUE KEY idx_gateway_name (name) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='媒体网关表';

CREATE TABLE fs_company (
    id                              bigint unsigned not null auto_increment COMMENT '主键id',
    name                            varchar(255)  NOT NULL DEFAULT '' COMMENT '名称',
    company_code                    varchar(255)  NOT NULL DEFAULT '' COMMENT '简称',
    contact                         varchar(255)  NOT NULL DEFAULT '' COMMENT '联系人',
    phone                           varchar(255)  NOT NULL DEFAULT '' COMMENT '电话',
    balance                         double default 0.00 COMMENT '金额',
    bill_type                       int unsigned NOT NULL DEFAULT '0' COMMENT '1:呼出计费,2:呼入计费,3:双向计费,0:全免费',
    pay_type                        int unsigned NOT NULL DEFAULT '0' COMMENT '0:预付费;1:后付费',
    hidden_customer                 int unsigned NOT NULL DEFAULT '0' COMMENT '隐藏客户号码(0:不隐藏;1:隐藏)',
    secret_type                     int unsigned NOT NULL DEFAULT '0' COMMENT '坐席密码等级',
    secret_key                      varchar(32)  NOT NULL DEFAULT '' COMMENT '验证秘钥',
    ivr_limit                       int unsigned NOT NULL DEFAULT '0' COMMENT 'IVR通道数',
    agent_limit                     int unsigned NOT NULL DEFAULT '0' COMMENT '开通坐席',
    group_limit                     int unsigned NOT NULL DEFAULT '0' COMMENT '开通技能组',
    group_agent_limit               int unsigned NOT NULL DEFAULT '0' COMMENT '单技能组中坐席上限',
    record_storage                  int unsigned NOT NULL DEFAULT '0' COMMENT '录音保留天数',
    notify_url                      varchar(255)  DEFAULT '' COMMENT '话单回调通知',
    status                          int unsigned NOT NULL DEFAULT '0' COMMENT '状态(0:禁用企业,1:免费企业;2:试用企业,3:付费企业)',
    create_user_id                  bigint unsigned comment '创建人编号',
    create_time                     datetime  NOT NULL COMMENT '创建时间',
    update_user_id                  bigint unsigned comment '修改人编号',
    update_time                     datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE,
    UNIQUE KEY company_name (name) USING BTREE,
    UNIQUE KEY company_code (company_code) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='企业信息表';

insert into fs_company(id,`name`,company_code,contact,phone,balance,bill_type,pay_type,hidden_customer,secret_type,secret_key,
    ivr_limit,agent_limit,group_limit,group_agent_limit,record_storage,notify_url,`status`,create_user_id,create_time,
    update_user_id,update_time)
values(1,'测试企业','CSQY','admin','18789432816',100.00,1,1,0,0,12645498,10,20,20,20,7,null,3,'1',now(),'1',now());

CREATE TABLE fs_agent (
    id                                  bigint unsigned not null auto_increment COMMENT '主键id',
    company_id                          bigint unsigned NOT NULL DEFAULT '0' COMMENT '企业ID',
    agent_id                            varchar(255)  NOT NULL DEFAULT '' COMMENT '坐席工号',
    agent_key                           varchar(255)  NOT NULL DEFAULT '' COMMENT '坐席账户',
    agent_name                          varchar(255)  NOT NULL DEFAULT '' COMMENT '坐席名称',
    agent_code                          varchar(20)  NOT NULL DEFAULT '' COMMENT '坐席分机号',
    agent_type                          int unsigned NOT NULL DEFAULT '2' COMMENT '座席类型：1:普通座席；2：班长',
    passwd                              varchar(255)  NOT NULL DEFAULT '' COMMENT '座席密码',
    sip_phone                           varchar(255)  NOT NULL DEFAULT '' COMMENT '绑定的电话号码',
    record                              int unsigned NOT NULL DEFAULT '0' COMMENT '是否录音 0 no 1 yes',
    group_id                            bigint unsigned NOT NULL DEFAULT '0' COMMENT '座席主要技能组  不能为空 必填项',
    after_interval                      int unsigned NOT NULL DEFAULT '5' COMMENT '话后自动空闲间隔时长',
    display                             varchar(255)  NOT NULL DEFAULT '' COMMENT '主叫显号',
    ring_time                           int unsigned NOT NULL DEFAULT '10' COMMENT '振铃时长',
    host                                varchar(255)  NOT NULL DEFAULT '0.0.0.0' COMMENT '登录服务器地址',
    state                               int unsigned NOT NULL DEFAULT '0' COMMENT '坐席状态(1:在线,0:不在线)',
    status                              int unsigned NOT NULL DEFAULT '1' COMMENT '状态：1 开通，0关闭',
    register_time                       datetime  DEFAULT NULL COMMENT '注册时间',
    renew_time                          datetime  DEFAULT NULL COMMENT '续订时间',
    keepalive_time                      datetime  DEFAULT NULL COMMENT '心跳时间',
    keep_timeout                        int default 30 COMMENT '心跳间隔 (最低25秒)',
    expires                             int unsigned DEFAULT 86400 COMMENT '注册有效期（单位：秒 默认1天）',
    stream_mode                         tinyint(4)  DEFAULT 0 COMMENT '数据流传输模式 0.UDP:udp传输 1.TCP-PASSIVE：tcp被动模式 2.TCP-ACTIVE：tcp主动模式',
    transport                           tinyint(4)  DEFAULT NULL COMMENT '传输协议 1.UDP 2.TCP',
    charset                             tinyint(4)  default 1 COMMENT '字符集, 1.UTF-8 2.GB2312',
    create_user_id                      bigint unsigned comment '创建人编号',
    create_time                         datetime  NOT NULL COMMENT '创建时间',
    update_user_id                      bigint unsigned comment '修改人编号',
    update_time                         datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE,
    UNIQUE KEY idx_agent_key (agent_key,company_id) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb3 COMMENT='座席工号表';


insert into fs_agent(id,company_id,agent_id,agent_key,agent_name,agent_code,agent_type,passwd,sip_phone,record,group_id,after_interval,
    display,ring_time,`host`,`state`,`status`,register_time,renew_time,keepalive_time,keep_timeout,expires,stream_mode,
    transport,charset,create_user_id,create_time,update_user_id,update_time)
values(1,1,1001,1001,'坐席1001','1001',1,'123456','1001',1,1,60,'1001',10,'0.0.0.0',0,1,null,null,null,30,3600,0,1,2,'1',now(),'1',now())
     ,(2,1,1002,1002,'坐席1002','1002',1,'123456','1002',1,1,60,'1002',10,'0.0.0.0',0,1,null,null,null,30,3600,0,1,2,'1',now(),'1',now())
     ,(3,1,1010,1010,'坐席1010','1010',1,'123456','1010',1,1,60,'1010',10,'0.0.0.0',0,1,null,null,null,30,3600,0,1,2,'1',now(),'1',now())
     ,(4,1,1011,1011,'坐席1011','1011',1,'123456','1011',1,1,60,'1011',10,'0.0.0.0',0,1,null,null,null,30,3600,0,1,2,'1',now(),'1',now())
;

CREATE TABLE fs_user_agent (
    id                                  bigint unsigned not null auto_increment COMMENT '主键id',
    company_id                          bigint unsigned NOT NULL DEFAULT '0' COMMENT '企业ID',
    agent_id                            varchar(255)  NOT NULL DEFAULT '' COMMENT '坐席工号',
    user_id                             varchar(255)  NOT NULL DEFAULT '' COMMENT '用户编号',
    create_user_id                      bigint unsigned comment '创建人编号',
    create_time                         datetime  NOT NULL COMMENT '创建时间',
    update_user_id                      bigint unsigned comment '修改人编号',
    update_time                         datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb3 COMMENT='用户座席中间表';

CREATE TABLE fs_agent_group (
    id                                  bigint unsigned not null auto_increment COMMENT '主键id',
    company_id                          bigint unsigned NOT NULL DEFAULT '0' COMMENT '企业ID',
    agent_id                            bigint unsigned NOT NULL DEFAULT '0' COMMENT '坐席id',
    agent_key                           varchar(50)  NOT NULL DEFAULT '',
    agent_type                          int unsigned NOT NULL DEFAULT '1',
    group_id                            bigint unsigned NOT NULL DEFAULT '0' COMMENT '技能组id',
    status                              tinyint(4) not null DEFAULT '1' COMMENT '状态',
    create_user_id                      bigint unsigned comment '创建人编号',
    create_time                         datetime  NOT NULL COMMENT '创建时间',
    update_user_id                      bigint unsigned comment '修改人编号',
    update_time                         datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE,
    UNIQUE KEY idx_agent_group_agent_group (group_id,agent_id) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='坐席技能组表';

CREATE TABLE fs_agent_sip (
    id                                  bigint unsigned not null auto_increment COMMENT '主键id',
    company_id                          bigint DEFAULT '0' COMMENT '企业主键',
    agent_id                            bigint unsigned NOT NULL DEFAULT '0' COMMENT '坐席主键',
    sip                                 varchar(32)  NOT NULL DEFAULT '' COMMENT 'sip编号',
    sip_pwd                             varchar(255)  DEFAULT '' COMMENT 'sip密码',
    status                              tinyint(4) not null DEFAULT '1' COMMENT '状态',
    create_user_id                      bigint unsigned comment '创建人编号',
    create_time                         datetime  NOT NULL COMMENT '创建时间',
    update_user_id                      bigint unsigned comment '修改人编号',
    update_time                         datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE,
    KEY idx_agent_sip_agent (agent_id) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='sip表';

CREATE TABLE fs_agent_state_log (
    id                                bigint unsigned not null auto_increment COMMENT '主键id',
    company_id                        bigint unsigned NOT NULL DEFAULT '0' COMMENT '企业id',
    group_id                          bigint unsigned NOT NULL DEFAULT '0' COMMENT '主技能组id',
    agent_id                          bigint unsigned NOT NULL DEFAULT '0' COMMENT '坐席id',
    agent_key                         varchar(255)  NOT NULL DEFAULT '' COMMENT '坐席编号',
    agent_name                        varchar(255)  NOT NULL DEFAULT '' COMMENT '坐席名称',
    call_id                           bigint unsigned NOT NULL DEFAULT '0' COMMENT '通话唯一标识',
    login_type                        int unsigned NOT NULL DEFAULT '1' COMMENT '登录类型',
    work_type                         int unsigned NOT NULL DEFAULT '1' COMMENT '工作类型',
    host                              varchar(255)  NOT NULL DEFAULT '' COMMENT '服务站点',
    remote_address                    varchar(255)  NOT NULL DEFAULT '' COMMENT '远端地址',
    before_state                      varchar(50)  NOT NULL DEFAULT '' COMMENT '变更之前状态',
    before_time                       bigint unsigned NOT NULL DEFAULT '0' COMMENT '更变之前时间',
    state                             varchar(50)  NOT NULL DEFAULT '' COMMENT '变更之后状态',
    state_time                        bigint unsigned NOT NULL DEFAULT '0' COMMENT '当前时间(秒)',
    duration                          int unsigned NOT NULL DEFAULT '0' COMMENT '持续时间(秒)',
    busy_desc                         varchar(255)  NOT NULL DEFAULT '' COMMENT '忙碌类型',
    status                            tinyint(4) not null DEFAULT '1' COMMENT '状态',
    create_user_id                    bigint unsigned comment '创建人编号',
    create_time                       datetime  NOT NULL COMMENT '创建时间',
    update_user_id                    bigint unsigned comment '修改人编号',
    update_time                       datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE,
    KEY idx_agent_state_agent_key (agent_key) USING BTREE,
    KEY idx_agent_state_cts (state_time) USING BTREE,
    KEY idx_agent_state_group_id (group_id) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=565 DEFAULT CHARSET=utf8mb3 COMMENT='坐席状态历史表';

CREATE TABLE fs_call_detail (
    id                                bigint unsigned not null auto_increment COMMENT '主键id',
    start_time                        bigint unsigned NOT NULL DEFAULT '0' COMMENT '开始时间',
    end_time                          bigint unsigned NOT NULL DEFAULT '0' COMMENT '结束时间',
    call_id                           bigint unsigned NOT NULL DEFAULT '0' COMMENT '通话ID',
    detail_index                      int unsigned NOT NULL DEFAULT '1' COMMENT '顺序',
    transfer_type                     int unsigned NOT NULL DEFAULT '0' COMMENT '1:进vdn,2:进ivr,3:技能组,4:按键收号,5:外线,6:机器人,10:服务评价',
    transfer_id                       bigint unsigned NOT NULL DEFAULT '0' COMMENT '转接ID',
    reason                            varchar(50)  NOT NULL DEFAULT '' COMMENT '出队列原因:排队挂机或者转坐席',
    status                            tinyint(4) not null DEFAULT '1' COMMENT '状态',
    create_user_id                    bigint unsigned comment '创建人编号',
    create_time                       datetime  NOT NULL COMMENT '创建时间',
    update_user_id                    bigint unsigned comment '修改人编号',
    update_time                       datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE,
    KEY idx_call_detail_call_id (call_id) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通话流程表';

CREATE TABLE fs_call_device (
    id                              bigint unsigned not null auto_increment COMMENT '主键id',
    company_id                      bigint unsigned NOT NULL DEFAULT '0' COMMENT '企业id',
    call_id                         bigint unsigned NOT NULL DEFAULT '0' COMMENT '通话ID',
    device_id                       varchar(50)  NOT NULL DEFAULT '' COMMENT '设备id',
    agent_key                       varchar(50)  NOT NULL DEFAULT '' COMMENT '坐席',
    agent_name                      varchar(50)  NOT NULL DEFAULT '' COMMENT '坐席名称',
    device_type                     int unsigned NOT NULL DEFAULT '1' COMMENT '1:坐席,2:客户,3:外线',
    cdr_type                        int unsigned NOT NULL DEFAULT '1' COMMENT '1:呼入,2:外呼,3:内呼,4:转接,5:咨询,6:监听,7:强插',
    from_agent                      varchar(50)  NOT NULL DEFAULT '' COMMENT '转接或咨询发起者',
    caller                          varchar(50)  NOT NULL DEFAULT '' COMMENT '主叫',
    called                          varchar(50)  NOT NULL DEFAULT '' COMMENT '被叫',
    display                         varchar(50)  NOT NULL DEFAULT '' COMMENT '显号',
    called_location                 varchar(100)  NOT NULL DEFAULT '' COMMENT '被叫归属地',
    caller_location                 varchar(100)  NOT NULL DEFAULT '' COMMENT '被叫归属地',
    call_time                       bigint unsigned NOT NULL DEFAULT '0' COMMENT '呼叫开始时间',
    ring_start_time                 bigint unsigned NOT NULL DEFAULT '0' COMMENT '振铃开始时间',
    ring_end_time                   bigint unsigned NOT NULL DEFAULT '0' COMMENT '振铃结束时间',
    answer_time                     bigint unsigned NOT NULL DEFAULT '0' COMMENT '接通时间',
    bridge_time                     bigint unsigned NOT NULL DEFAULT '0' COMMENT '桥接时间',
    end_time                        bigint unsigned NOT NULL DEFAULT '0' COMMENT '结束时间',
    talk_time                       bigint unsigned NOT NULL DEFAULT '0' COMMENT '通话时长',
    record_start_time               bigint unsigned NOT NULL DEFAULT '0' COMMENT '录音开始时间',
    record_time                     bigint unsigned NOT NULL DEFAULT '0' COMMENT '录音时长',
    sip_protocol                    varchar(50)  NOT NULL DEFAULT '' COMMENT '信令协议(tcp/udp)',
    record                          varchar(255)  NOT NULL DEFAULT '' COMMENT '录音地址',
    record2                         varchar(255)  NOT NULL DEFAULT '' COMMENT '备用录音地址',
    record3                         varchar(255)  NOT NULL DEFAULT '' COMMENT '备用录音地址',
    channel_name                    varchar(50)  NOT NULL DEFAULT '' COMMENT '呼叫地址',
    hangup_cause                    varchar(50)  NOT NULL COMMENT '挂机原因',
    ring_cause                      varchar(50)  NOT NULL DEFAULT '' COMMENT '回铃音识别',
    sip_status                      varchar(50)  NOT NULL DEFAULT '' COMMENT 'sip状态',
    status                          tinyint(4) not null DEFAULT '1' COMMENT '状态',
    create_user_id                  bigint unsigned comment '创建人编号',
    create_time                     datetime  NOT NULL COMMENT '创建时间',
    update_user_id                  bigint unsigned comment '修改人编号',
    update_time                     datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE,
    KEY idx_call_detail_call_id (call_id) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=617 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='话单明细表';

CREATE TABLE fs_call_dtmf (
    id                              bigint unsigned not null auto_increment COMMENT '主键id',
    dtmf_key                        varchar(255)  NOT NULL DEFAULT '' COMMENT '按键号码',
    process_id                      bigint unsigned NOT NULL DEFAULT '0' COMMENT '业务流程id',
    call_id                         bigint unsigned NOT NULL DEFAULT '0' COMMENT '通话标识id',
    dtmf_time                       bigint unsigned NOT NULL DEFAULT '0' COMMENT '按键时间',
    status                          tinyint(4) not null DEFAULT '1' COMMENT '状态',
    create_user_id                  bigint unsigned comment '创建人编号',
    create_time                     datetime  NOT NULL COMMENT '创建时间',
    update_user_id                  bigint unsigned comment '修改人编号',
    update_time                     datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='呼叫按键表';

CREATE TABLE fs_call_log (
   id                               bigint unsigned not null auto_increment COMMENT '主键id',
   company_id                       bigint unsigned NOT NULL DEFAULT '0' COMMENT '企业id',
   call_id                          bigint unsigned NOT NULL DEFAULT '0' COMMENT '话单id',
   caller_display                   varchar(100)  NOT NULL DEFAULT '' COMMENT '主叫显号',
   caller                           varchar(100)  NOT NULL DEFAULT '' COMMENT '主叫',
   called_display                   varchar(100)  NOT NULL DEFAULT '' COMMENT '被叫显号',
   called                           varchar(100)  NOT NULL DEFAULT '' COMMENT '被叫',
   number_location                  varchar(100)  NOT NULL DEFAULT '' COMMENT '客户号码归属地',
   agent_key                        varchar(50)  NOT NULL DEFAULT '' COMMENT '坐席',
   agent_name                       varchar(255)  NOT NULL DEFAULT '' COMMENT '坐席名称',
   group_id                         bigint unsigned NOT NULL DEFAULT '0' COMMENT '技能组',
   login_type                       int unsigned NOT NULL DEFAULT '1' COMMENT '1:sip号,2:webrtc,3:手机',
   task_id                          bigint unsigned NOT NULL DEFAULT '0' COMMENT '任务ID',
   ivr_id                           bigint unsigned NOT NULL DEFAULT '0' COMMENT 'ivr',
   bot_id                           bigint unsigned NOT NULL DEFAULT '0' COMMENT '机器人id',
   call_time                        bigint unsigned NOT NULL DEFAULT '0' COMMENT '呼叫开始时间',
   answer_time                      bigint unsigned NOT NULL DEFAULT '0' COMMENT '接听时间',
   end_time                         bigint unsigned NOT NULL DEFAULT '0' COMMENT '结束时间',
   call_type                        varchar(32)  NOT NULL DEFAULT '' COMMENT '呼叫类型',
   direction                        varchar(32)  NOT NULL DEFAULT '' COMMENT '呼叫方向',
   answer_flag                      int unsigned NOT NULL DEFAULT '0' COMMENT '通话标识(0:接通,1:坐席未接用户未接,2:坐席接通用户未接通,3:用户接通坐席未接通)',
   wait_time                        bigint unsigned NOT NULL DEFAULT '0' COMMENT '累计等待时长',
   answer_count                     int unsigned NOT NULL DEFAULT '0' COMMENT '应答设备数',
   hangup_dir                       int unsigned NOT NULL DEFAULT '1' COMMENT '挂机方向(1:主叫挂机,2:被叫挂机,3:系统挂机)',
   sdk_hangup                       int unsigned NOT NULL DEFAULT '0' COMMENT '是否sdk挂机(1:sdk挂机)',
   hangup_code                      int unsigned NOT NULL DEFAULT '0' COMMENT '挂机原因',
   media_host                       varchar(255)  NOT NULL DEFAULT '' COMMENT '媒体服务器',
   cti_host                         varchar(255)  NOT NULL DEFAULT '' COMMENT 'cti地址',
   client_host                      varchar(255)  NOT NULL DEFAULT '' COMMENT '客户端地址',
   record                           varchar(255)  NOT NULL DEFAULT '' COMMENT '录音地址',
   record2                          varchar(255)  NOT NULL DEFAULT '' COMMENT '备用录音地址',
   record3                          varchar(255)  NOT NULL DEFAULT '' COMMENT '备用录音地址',
   record_type                      int unsigned NOT NULL DEFAULT '1' COMMENT '录音状态',
   record_start_time                bigint unsigned NOT NULL DEFAULT '0' COMMENT '录音开始时间',
   record_time                      bigint unsigned NOT NULL DEFAULT '0' COMMENT '录音时间',
   talk_time                        bigint unsigned NOT NULL DEFAULT '0' COMMENT '通话时长',
   frist_queue_time                 bigint unsigned NOT NULL DEFAULT '0' COMMENT '第一次进队列时间',
   queue_start_time                 bigint unsigned NOT NULL DEFAULT '0' COMMENT '进队列时间',
   queue_end_time                   bigint unsigned NOT NULL DEFAULT '0' COMMENT '出队列时间',
   month_time                       varchar(50)  NOT NULL DEFAULT '' COMMENT '月份',
   follow_data                      varchar(4096)  NOT NULL DEFAULT '' COMMENT '通话随路数据(2048)',
   status                           tinyint(4) not null DEFAULT '1' COMMENT '状态',
   create_user_id                   bigint unsigned comment '创建人编号',
   create_time                      datetime  NOT NULL COMMENT '创建时间',
   update_user_id                   bigint unsigned comment '修改人编号',
   update_time                      datetime  NOT NULL COMMENT '更新时间',
   PRIMARY KEY (id) USING BTREE,
   UNIQUE KEY uniq_call_log_call_id (call_id) USING BTREE,
   KEY idx_call_log_agent (agent_key) USING BTREE,
   KEY idx_call_log_group (group_id) USING BTREE,
   KEY idx_call_log_create_time (company_id,call_time) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=332 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='话单表';

CREATE TABLE fs_company_display (
    id                              bigint unsigned not null auto_increment COMMENT '主键id',
    company_id                      bigint unsigned NOT NULL DEFAULT '0' COMMENT '企业id',
    name                            varchar(255)  NOT NULL DEFAULT '' COMMENT '号码池',
    type                            int unsigned NOT NULL DEFAULT '0' COMMENT '1:呼入号码,2:主叫显号,3:被叫显号',
    status                          tinyint(4) not null DEFAULT '1' COMMENT '状态',
    create_user_id                  bigint unsigned comment '创建人编号',
    create_time                     datetime  NOT NULL COMMENT '创建时间',
    update_user_id                  bigint unsigned comment '修改人编号',
    update_time                     datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE,
    KEY idx_company_display (company_id,name) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='号码池表';

CREATE TABLE fs_company_phone_group (
    id                              bigint unsigned not null auto_increment COMMENT '主键id',
    company_id                      bigint unsigned NOT NULL DEFAULT '0' COMMENT '企业ID',
    display_id                      bigint unsigned NOT NULL DEFAULT '0' COMMENT '号码池id',
    phone_id                        bigint unsigned NOT NULL DEFAULT '0' COMMENT '号码id',
    create_user_id                  bigint unsigned comment '创建人编号',
    create_time                     datetime  NOT NULL COMMENT '创建时间',
    update_user_id                  bigint unsigned comment '修改人编号',
    update_time                     datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='企业号码与号码池中间表';

CREATE TABLE fs_company_phone (
    id                              bigint unsigned not null auto_increment COMMENT '主键id',
    company_id                      bigint unsigned NOT NULL DEFAULT '0' COMMENT '企业id',
    phone                           varchar(255)  NOT NULL DEFAULT '' COMMENT '号码',
    type                            int unsigned NOT NULL DEFAULT '0' COMMENT '1:呼入号码,2:主叫显号,3:被叫显号',
    status                          int unsigned NOT NULL DEFAULT '1' COMMENT '1:未启用,2:启用',
    create_user_id                  bigint unsigned comment '创建人编号',
    create_time                     datetime  NOT NULL COMMENT '创建时间',
    update_user_id                  bigint unsigned comment '修改人编号',
    update_time                     datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE,
    UNIQUE KEY idx_company_phone_phone (company_id,phone,type) USING BTREE,
    KEY idx_company_phone_company_id (company_id) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='企业号码';

CREATE TABLE fs_group (
    id                              bigint unsigned not null auto_increment COMMENT '主键id',
    company_id                      bigint unsigned NOT NULL DEFAULT '0' COMMENT '企业ID',
    name                            varchar(20)  NOT NULL DEFAULT '' COMMENT '技能组名称',
    after_interval                  int unsigned NOT NULL DEFAULT '5' COMMENT '话后自动空闲时长',
    caller_display_id               bigint unsigned NOT NULL DEFAULT '0' COMMENT '主叫显号号码池',
    called_display_id               bigint unsigned NOT NULL DEFAULT '0' COMMENT '被叫显号号码池',
    record_type                     int unsigned NOT NULL DEFAULT '1' COMMENT '1:振铃录音,2:接通录音',
    level_value                     int unsigned NOT NULL DEFAULT '1' COMMENT '技能组优先级',
    tts_engine                      bigint unsigned NOT NULL DEFAULT '0' COMMENT 'tts引擎id',
    play_content                    varchar(100)  NOT NULL DEFAULT '' COMMENT '转坐席时播放内容',
    evaluate                        bigint unsigned NOT NULL DEFAULT '0' COMMENT '转服务评价(0:否,1:是)',
    queue_play                      bigint unsigned NOT NULL DEFAULT '0' COMMENT '排队音',
    transfer_play                   bigint unsigned NOT NULL DEFAULT '0' COMMENT '转接提示音',
    call_time_out                   int unsigned NOT NULL DEFAULT '30' COMMENT '外呼呼叫超时时间',
    group_type                      int unsigned NOT NULL DEFAULT '0' COMMENT '技能组类型',
    notify_position                 int unsigned NOT NULL DEFAULT '0' COMMENT '0:不播放排队位置,1:播放排队位置',
    notify_rate                     int unsigned NOT NULL DEFAULT '10' COMMENT '频次',
    notify_content                  varchar(255)  NOT NULL DEFAULT '' COMMENT '您前面还有$位用户在等待',
    call_memory                     int unsigned NOT NULL DEFAULT '1' COMMENT '主叫记忆(1:开启,0:不开启)',
    status                          tinyint(4) not null DEFAULT '1' COMMENT '状态',
    create_user_id                  bigint unsigned comment '创建人编号',
    create_time                     datetime  NOT NULL COMMENT '创建时间',
    update_user_id                  bigint unsigned comment '修改人编号',
    update_time                     datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE,
    UNIQUE KEY idx_company_name (company_id,name) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='技能组表';

CREATE TABLE fs_group_agent_strategy (
   id                               bigint unsigned not null auto_increment COMMENT '主键id',
   company_id                       bigint unsigned NOT NULL DEFAULT '0' COMMENT '企业ID',
   group_id                         bigint unsigned NOT NULL DEFAULT '0' COMMENT '技能组id',
   strategy_type                    int unsigned NOT NULL DEFAULT '1' COMMENT '1:内置策略,2:自定义',
   strategy_value                   int unsigned NOT NULL DEFAULT '1' COMMENT '(1最长空闲时间、2最长平均空闲、3最少应答次数、4最少通话时长、5最长话后时长、6轮选、7随机)',
   custom_expression                varchar(255)  NOT NULL DEFAULT '' COMMENT '自定义表达式',
   status                           int unsigned NOT NULL DEFAULT '1',
   create_user_id                   bigint unsigned comment '创建人编号',
   create_time                      datetime  NOT NULL COMMENT '创建时间',
   update_user_id                   bigint unsigned comment '修改人编号',
   update_time                      datetime  NOT NULL COMMENT '更新时间',
   PRIMARY KEY (id) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='技能组中坐席分配策略';

CREATE TABLE fs_group_memory (
    id                              bigint unsigned not null auto_increment COMMENT '主键id',
    company_id                      bigint unsigned NOT NULL DEFAULT '0' COMMENT '企业ID',
    group_id                        bigint unsigned NOT NULL DEFAULT '0' COMMENT '技能组ID',
    agent_key                       varchar(32)  NOT NULL DEFAULT '' COMMENT '坐席',
    phone                           varchar(255)  NOT NULL DEFAULT '' COMMENT '客户电话',
    status                          tinyint(4) not null DEFAULT '1' COMMENT '状态',
    create_user_id                  bigint unsigned comment '创建人编号',
    create_time                     datetime  NOT NULL COMMENT '创建时间',
    update_user_id                  bigint unsigned comment '修改人编号',
    update_time                     datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE,
    UNIQUE KEY idx_group_id (group_id,phone,agent_key) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='坐席与客户记忆表';

CREATE TABLE fs_group_memory_config (
    id                              bigint unsigned not null auto_increment COMMENT '主键id',
    company_id                      bigint unsigned NOT NULL DEFAULT '0' COMMENT '企业ID',
    group_id                        bigint unsigned NOT NULL DEFAULT '0' COMMENT '技能组ID',
    success_strategy                int unsigned NOT NULL DEFAULT '0' COMMENT '匹配成功策略',
    success_strategy_value          bigint unsigned NOT NULL DEFAULT '0' COMMENT '匹配成功策略值',
    fail_strategy                   int unsigned NOT NULL DEFAULT '0' COMMENT '匹配失败策略',
    fail_strategy_value             bigint unsigned NOT NULL DEFAULT '0' COMMENT '匹配失败策略值',
    memory_day                      int unsigned NOT NULL DEFAULT '30' COMMENT '记忆天数',
    inbound_cover                   int unsigned NOT NULL DEFAULT '0' COMMENT '呼入覆盖',
    outbound_cover                  int unsigned NOT NULL DEFAULT '0' COMMENT '外呼覆盖',
    status                          int unsigned NOT NULL DEFAULT '1',
    create_user_id                  bigint unsigned comment '创建人编号',
    create_time                     datetime  NOT NULL COMMENT '创建时间',
    update_user_id                  bigint unsigned comment '修改人编号',
    update_time                     datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE,
    UNIQUE KEY idx_group (group_id) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='技能组坐席记忆配置表';

CREATE TABLE fs_group_overflow (
    id                              bigint unsigned not null auto_increment COMMENT '主键id',
    group_id                        bigint unsigned NOT NULL DEFAULT '0' COMMENT '技能组ID',
    overflow_id                     bigint unsigned NOT NULL DEFAULT '0' COMMENT '溢出策略ID',
    level_value                     int unsigned NOT NULL DEFAULT '1' COMMENT '优先级',
    status                          tinyint(4) not null DEFAULT '1' COMMENT '状态',
    create_user_id                  bigint unsigned comment '创建人编号',
    create_time                     datetime  NOT NULL COMMENT '创建时间',
    update_user_id                  bigint unsigned comment '修改人编号',
    update_time                     datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE,
    UNIQUE KEY idx_group_overflow (group_id,overflow_id) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='技能组排队策略表';

CREATE TABLE fs_group_strategy_exp (
    id                              bigint unsigned not null auto_increment COMMENT '主键id',
    company_id                      bigint unsigned NOT NULL DEFAULT '0' COMMENT '企业ID',
    group_id                        bigint unsigned NOT NULL DEFAULT '0' COMMENT '技能组id',
    strategy_key                    varchar(20)  NOT NULL DEFAULT '' COMMENT '自定义值',
    strategy_present                int unsigned NOT NULL DEFAULT '1' COMMENT '百分百',
    strategy_type                   int unsigned NOT NULL DEFAULT '1' COMMENT '类型',
    status                          tinyint(4) not null DEFAULT '1' COMMENT '状态',
    create_user_id                  bigint unsigned comment '创建人编号',
    create_time                     datetime  NOT NULL COMMENT '创建时间',
    update_user_id                  bigint unsigned comment '修改人编号',
    update_time                     datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='坐席自定义策略表';

CREATE TABLE fs_ivr_workflow (
    id                              bigint unsigned not null auto_increment COMMENT '主键id',
    company_id                      bigint unsigned NOT NULL DEFAULT '0' COMMENT '企业id',
    name                            varchar(255)  NOT NULL DEFAULT '' COMMENT '流程名称',
    oss_id                          varchar(255)  NOT NULL DEFAULT '' COMMENT '流程文件名',
    init_params                     text  COMMENT '用来存贮 ivr 流程启动所需要的参数描述',
    create_user                     varchar(255)  NOT NULL DEFAULT '' COMMENT '流程发布人',
    verify_user                     varchar(255)  NOT NULL DEFAULT '' COMMENT '流程审核人',
    content                         text  COMMENT '流程内容(ivr)',
    voice_item                      varchar(10000)  NOT NULL DEFAULT '' COMMENT '该流程用到的语音文件id，以英文逗号,分隔',
    type                            int unsigned NOT NULL DEFAULT '1' COMMENT '1转接，2咨询',
    status                          int unsigned NOT NULL COMMENT '流程状态    1：待发布   2：审核中  3：审核未通过  4：审核通过  5：已上线(ivr)',
    create_user_id                  bigint unsigned comment '创建人编号',
    create_time                     datetime  NOT NULL COMMENT '创建时间',
    update_user_id                  bigint unsigned comment '修改人编号',
    update_time                     datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3 COMMENT='ivr流程表';

CREATE TABLE fs_overflow_config (
    id                              bigint unsigned not null auto_increment COMMENT '主键id',
    company_id                      bigint DEFAULT '0' COMMENT '企业id',
    name                            varchar(255)  NOT NULL DEFAULT '' COMMENT '名称',
    handle_type                     int unsigned NOT NULL DEFAULT '1' COMMENT '1:排队,2:溢出,3:挂机',
    busy_type                       int DEFAULT '1' COMMENT '排队方式(1:先进先出,2:vip,3:自定义)',
    queue_timeout                   int DEFAULT '60' COMMENT '排队超时时间',
    busy_timeout_type               int DEFAULT '1' COMMENT '排队超时(1:溢出,2:挂机)',
    overflow_type                   int DEFAULT '1' COMMENT '溢出(1:group,2:ivr,3:vdn)',
    overflow_value                  int DEFAULT '0' COMMENT '溢出值',
    lineup_expression               varchar(255)  DEFAULT '' COMMENT '自定义排队表达式',
    create_user_id                  bigint unsigned comment '创建人编号',
    create_time                     datetime  NOT NULL COMMENT '创建时间',
    update_user_id                  bigint unsigned comment '修改人编号',
    update_time                     datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE,
    UNIQUE KEY uni_idx_name (company_id,name) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='溢出策略表';

CREATE TABLE fs_overflow_exp (
   id                               bigint unsigned not null auto_increment COMMENT '主键id',
   company_id                       bigint unsigned NOT NULL DEFAULT '0' COMMENT '企业ID',
   overflow_id                      bigint unsigned NOT NULL DEFAULT '0' COMMENT '溢出策略ID',
   exp_key                          varchar(30)  NOT NULL DEFAULT '' COMMENT '自定义值',
   rate                             int unsigned NOT NULL DEFAULT '1' COMMENT '权重',
   status                           tinyint(4) not null DEFAULT '1' COMMENT '状态',
   create_user_id                   bigint unsigned comment '创建人编号',
   create_time                      datetime  NOT NULL COMMENT '创建时间',
   update_user_id                   bigint unsigned comment '修改人编号',
   update_time                      datetime  NOT NULL COMMENT '更新时间',
   PRIMARY KEY (id) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='自定义溢出策略优先级';

CREATE TABLE fs_overflow_front (
    id                              bigint unsigned not null auto_increment COMMENT '主键id',
    company_id                      bigint unsigned NOT NULL DEFAULT '0' COMMENT '企业ID',
    overflow_id                     bigint unsigned NOT NULL DEFAULT '0' COMMENT '策略ID',
    front_type                      int unsigned NOT NULL DEFAULT '1' COMMENT '1:队列长度; 2:队列等待最大时长; 3:呼损率',
    compare_condition               int unsigned NOT NULL DEFAULT '0' COMMENT '0:全部; 1:小于或等于; 2:等于; 3:大于或等于; 4:大于',
    rank_value_start                int unsigned NOT NULL DEFAULT '0',
    rank_value                      int unsigned NOT NULL DEFAULT '0' COMMENT '符号条件值',
    status                          tinyint(4) not null DEFAULT '1' COMMENT '状态',
    create_user_id                  bigint unsigned comment '创建人编号',
    create_time                     datetime  NOT NULL COMMENT '创建时间',
    update_user_id                  bigint unsigned comment '修改人编号',
    update_time                     datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='溢出策略前置条件';

CREATE TABLE fs_playback (
    id                              bigint unsigned not null auto_increment COMMENT '主键id',
    company_id                      bigint unsigned NOT NULL COMMENT '企业ID',
    playback                        varchar(255)  NOT NULL DEFAULT '' COMMENT '放音文件',
    status                          int unsigned NOT NULL DEFAULT '1' COMMENT '1:待审核,2:审核通过',
    create_user_id                  bigint unsigned comment '创建人编号',
    create_time                     datetime  NOT NULL COMMENT '创建时间',
    update_user_id                  bigint unsigned comment '修改人编号',
    update_time                     datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='语音文件表';

CREATE TABLE fs_push_log (
    id                              bigint unsigned not null auto_increment COMMENT '主键id',
    company_id                      bigint unsigned NOT NULL DEFAULT '0' COMMENT '企业id',
    call_id                         bigint unsigned NOT NULL DEFAULT '0' COMMENT 'callid',
    cdr_notify_url                  varchar(255)  NOT NULL DEFAULT '' COMMENT '发送次数',
    content                         varchar(10240)  NOT NULL DEFAULT '' COMMENT '推送内容',
    push_times                      int unsigned NOT NULL DEFAULT '1' COMMENT '推送次数',
    push_response                   varchar(255)  NOT NULL DEFAULT '' COMMENT '推送返回值',
    status                          int unsigned NOT NULL DEFAULT '1' COMMENT '状态(1:推送，0:不推送)',
    create_user_id                  bigint unsigned comment '创建人编号',
    create_time                     datetime  NOT NULL COMMENT '创建时间',
    update_user_id                  bigint unsigned comment '修改人编号',
    update_time                     datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=646 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='话单推送记录表';

CREATE TABLE fs_route_call (
    id                              bigint unsigned not null auto_increment COMMENT '主键id',
    company_id                      bigint unsigned NOT NULL DEFAULT '0' COMMENT '所属企业',
    route_group_id                  bigint unsigned NOT NULL DEFAULT '0' COMMENT '所属路由组',
    route_num                       varchar(32)  NOT NULL DEFAULT '' COMMENT '字冠号码',
    num_max                         int unsigned NOT NULL DEFAULT '0' COMMENT '最长',
    num_min                         int unsigned NOT NULL DEFAULT '0' COMMENT '最短',
    caller_change                   int unsigned NOT NULL DEFAULT '0' COMMENT '主叫替换规则',
    caller_change_num               varchar(32)  NOT NULL DEFAULT '' COMMENT '替换号码',
    called_change                   int unsigned NOT NULL DEFAULT '0' COMMENT '被叫替换规则',
    called_change_num               varchar(32)  NOT NULL DEFAULT '' COMMENT '替换号码',
    status                          tinyint(4) not null DEFAULT '1' COMMENT '状态',
    create_user_id                  bigint unsigned comment '创建人编号',
    create_time                     datetime  NOT NULL COMMENT '创建时间',
    update_user_id                  bigint unsigned comment '修改人编号',
    update_time                     datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE,
    KEY unq_idx_route (company_id,route_num) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字冠路由表';


CREATE TABLE fs_skill (
    id                              bigint unsigned not null auto_increment COMMENT '主键id',
    company_id                      bigint unsigned NOT NULL DEFAULT '0' COMMENT '企业id',
    name                            varchar(255)  NOT NULL DEFAULT '' COMMENT '名称',
    remark                          varchar(255)  NOT NULL DEFAULT '' COMMENT '备注',
    status                          tinyint(4) not null DEFAULT '1' COMMENT '状态',
    create_user_id                  bigint unsigned comment '创建人编号',
    create_time                     datetime  NOT NULL COMMENT '创建时间',
    update_user_id                  bigint unsigned comment '修改人编号',
    update_time                     datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE,
    UNIQUE KEY uniq_skill_name (company_id,name) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='技能表';

CREATE TABLE fs_skill_agent (
    id                              bigint unsigned not null auto_increment COMMENT '主键id',
    company_id                      bigint unsigned NOT NULL DEFAULT '0' COMMENT '企业id',
    skill_id                        bigint unsigned NOT NULL DEFAULT '0' COMMENT '技能id',
    agent_id                        bigint unsigned NOT NULL DEFAULT '0' COMMENT '坐席id',
    rank_value                      int unsigned NOT NULL DEFAULT '0' COMMENT '范围',
    status                          tinyint(4) not null DEFAULT '1' COMMENT '状态',
    create_user_id                  bigint unsigned comment '创建人编号',
    create_time                     datetime  NOT NULL COMMENT '创建时间',
    update_user_id                  bigint unsigned comment '修改人编号',
    update_time                     datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='坐席技能表';

CREATE TABLE fs_skill_group (
    id                              bigint unsigned not null auto_increment COMMENT '主键id',
    company_id                      bigint unsigned NOT NULL DEFAULT '0' COMMENT '企业ID',
    level_value                     int unsigned NOT NULL DEFAULT '1',
    skill_id                        bigint unsigned NOT NULL DEFAULT '0' COMMENT '技能ID',
    group_id                        bigint unsigned NOT NULL DEFAULT '0' COMMENT '技能组ID',
    rank_type                       tinyint(4) not null DEFAULT '1' COMMENT '等级类型(1:全部,2:等于,3:>,4:<,5:介于)',
    rank_value_start                int unsigned NOT NULL DEFAULT '0' COMMENT '介于的开始值',
    rank_value                      int unsigned NOT NULL DEFAULT '1' COMMENT '等级值',
    match_type                      int unsigned NOT NULL DEFAULT '1' COMMENT '匹配规则(1:低到高,2:高到低)',
    share_value                     int unsigned NOT NULL DEFAULT '100' COMMENT '占用率',
    status                          tinyint(4) not null DEFAULT '0' COMMENT '状态',
    create_user_id                  bigint unsigned comment '创建人编号',
    create_time                     datetime  NOT NULL COMMENT '创建时间',
    update_user_id                  bigint unsigned comment '修改人编号',
    update_time                     datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE,
    KEY idx_group_id (group_id) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='技能组技能表';

CREATE TABLE fs_vdn_code (
    id                               bigint unsigned not null auto_increment COMMENT '主键id',
    company_id                       bigint unsigned NOT NULL DEFAULT '0' COMMENT '企业ID',
    name                             varchar(255)  NOT NULL DEFAULT '' COMMENT 'vdn名称',
    status                           tinyint(4) not null DEFAULT '1' COMMENT '状态',
    create_user_id                   bigint unsigned comment '创建人编号',
    create_time                      datetime  NOT NULL COMMENT '创建时间',
    update_user_id                   bigint unsigned comment '修改人编号',
    update_time                      datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='呼入路由表';


CREATE TABLE fs_vdn_config (
    id                              bigint unsigned not null auto_increment COMMENT '主键id',
    company_id                      bigint unsigned NOT NULL DEFAULT '0' COMMENT '企业ID',
    name                            varchar(255) NOT NULL DEFAULT '' COMMENT '子码日程',
    vdn_id                          bigint unsigned NOT NULL DEFAULT '0' COMMENT '呼入路由编号',
    schedule_id                     bigint unsigned NOT NULL DEFAULT '0' COMMENT '日程id',
    route_type                      tinyint(4) not null DEFAULT '1' COMMENT '路由类型(1:技能组,2:放音,3:ivr,4:坐席,5:外呼)',
    route_value                     varchar(255) NOT NULL DEFAULT '0' COMMENT '路由类型值',
    play_type                       tinyint(4) not null DEFAULT '0' COMMENT '放音类型(1:按键导航,2:技能组,3:ivr,4:路由字码,5:挂机)',
    play_value                      bigint unsigned NOT NULL DEFAULT '0' COMMENT '放音类型对应值',
    dtmf_end                        varchar(255) NOT NULL DEFAULT '*' COMMENT '结束音',
    retry                           int unsigned NOT NULL DEFAULT '1' COMMENT '重复播放次数',
    dtmf_max                        int unsigned NOT NULL DEFAULT '1' COMMENT '最大收键长度',
    dtmf_min                        int unsigned NOT NULL DEFAULT '1' COMMENT '最小收键长度',
    status                          int unsigned NOT NULL DEFAULT '1' COMMENT '状态',
    create_user_id                  bigint unsigned comment '创建人编号',
    create_time                     datetime  NOT NULL COMMENT '创建时间',
    update_user_id                  bigint unsigned comment '修改人编号',
    update_time                     datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='呼入路由字码表';

CREATE TABLE fs_vdn_dtmf (
    id                              bigint unsigned not null auto_increment COMMENT '主键id',
    company_id                      bigint unsigned NOT NULL DEFAULT '0' COMMENT '企业ID',
    navigate_id                     bigint unsigned NOT NULL DEFAULT '0' COMMENT '按键导航ID',
    dtmf                            varchar(20) NOT NULL DEFAULT '1' COMMENT '按键',
    route_type                      tinyint(4) not null DEFAULT '0' COMMENT '路由类型(1:技能组,2:IVR,3:路由字码,4:坐席分机,5:挂机)',
    route_value                     bigint unsigned NOT NULL DEFAULT '0' COMMENT '路由值',
    status                          tinyint(4) not null DEFAULT '1' COMMENT '状态',
    create_user_id                  bigint unsigned comment '创建人编号',
    create_time                     datetime  NOT NULL COMMENT '创建时间',
    update_user_id                  bigint unsigned comment '修改人编号',
    update_time                     datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='路由按键导航表';

CREATE TABLE fs_vdn_phone (
    id                              bigint unsigned not null auto_increment COMMENT '主键id',
    company_id                      bigint unsigned NOT NULL DEFAULT '0' COMMENT '企业ID',
    vdn_id                          bigint unsigned NOT NULL DEFAULT '0' COMMENT '路由码',
    phone                           varchar(20) NOT NULL DEFAULT '' COMMENT '特服号',
    status                          tinyint(4) not null DEFAULT '1' COMMENT '状态',
    create_user_id                  bigint unsigned comment '创建人编号',
    create_time                     datetime  NOT NULL COMMENT '创建时间',
    update_user_id                  bigint unsigned comment '修改人编号',
    update_time                     datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE,
    UNIQUE KEY uni_idx_phone (vdn_id,company_id) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='路由号码表';

CREATE TABLE fs_vdn_schedule (
    id                              bigint unsigned not null auto_increment COMMENT '主键id',
    company_id                      bigint unsigned NOT NULL  COMMENT '企业ID',
    name                            varchar(20) DEFAULT '' COMMENT '日程名称',
    level_value                     int unsigned NOT NULL DEFAULT '1' COMMENT '优先级',
    type                            int unsigned NOT NULL DEFAULT '1' COMMENT '1:指定时间,2:相对时间',
    start_time                      datetime COMMENT '开始时间',
    end_time                        datetime COMMENT '结束时间',
    mon                             int unsigned NOT NULL DEFAULT '1' COMMENT '周一',
    tue                             int unsigned NOT NULL DEFAULT '1' COMMENT '周二',
    wed                             int unsigned NOT NULL DEFAULT '1' COMMENT '周三',
    thu                             int unsigned NOT NULL DEFAULT '1' COMMENT '周四',
    fri                             int unsigned NOT NULL DEFAULT '1' COMMENT '周五',
    sat                             int unsigned NOT NULL DEFAULT '1' COMMENT '周六',
    sun                             int unsigned NOT NULL DEFAULT '1' COMMENT '周天',
    status                          tinyint(4) not null DEFAULT '1' COMMENT '状态',
    create_user_id                  bigint unsigned comment '创建人编号',
    create_time                     datetime  NOT NULL COMMENT '创建时间',
    update_user_id                  bigint unsigned comment '修改人编号',
    update_time                     datetime  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='日程表';


# 以下为freeswitch 所需要表
## 通话记录需要手动创建
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
## 以下为自动创建
CREATE TABLE aliases  (
    sticky int NULL DEFAULT NULL,
    alias varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    command varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    hostname varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    INDEX alias1(alias) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE calls  (
    call_uuid varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    call_created varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    call_created_epoch int NULL DEFAULT NULL,
    caller_uuid varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    callee_uuid varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    hostname varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    INDEX callsidx1(hostname) USING BTREE,
    INDEX eruuindex(caller_uuid, hostname) USING BTREE,
    INDEX eeuuindex(callee_uuid) USING BTREE,
    INDEX eeuuindex2(call_uuid) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE channels  (
    uuid varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    direction varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    created varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    created_epoch int NULL DEFAULT NULL,
    name varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    state varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    cid_name varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    cid_num varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    ip_addr varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    dest varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    application varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    application_data text CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL,
    dialplan varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    context varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    read_codec varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    read_rate varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    read_bit_rate varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    write_codec varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    write_rate varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    write_bit_rate varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    secure varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    hostname varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    presence_id varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    presence_data text CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL,
    accountcode varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    callstate varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    callee_name varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    callee_num varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    callee_direction varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    call_uuid varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    sent_callee_name varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    sent_callee_num varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    initial_cid_name varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    initial_cid_num varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    initial_ip_addr varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    initial_dest varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    initial_dialplan varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    initial_context varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
INDEX chidx1(hostname) USING BTREE,
INDEX uuindex(uuid, hostname) USING BTREE,
INDEX uuindex2(call_uuid) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE complete  (
    sticky int NULL DEFAULT NULL,
    a1 varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    a2 varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    a3 varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    a4 varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    a5 varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    a6 varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    a7 varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    a8 varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    a9 varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    a10 varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    hostname varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    INDEX complete1(a1, hostname) USING BTREE,
    INDEX complete2(a2, hostname) USING BTREE,
    INDEX complete3(a3, hostname) USING BTREE,
    INDEX complete4(a4, hostname) USING BTREE,
    INDEX complete5(a5, hostname) USING BTREE,
    INDEX complete6(a6, hostname) USING BTREE,
    INDEX complete7(a7, hostname) USING BTREE,
    INDEX complete8(a8, hostname) USING BTREE,
    INDEX complete9(a9, hostname) USING BTREE,
    INDEX complete10(a10, hostname) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE fifo_bridge  (
    fifo_name varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NOT NULL,
    caller_uuid varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NOT NULL,
    caller_caller_id_name varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    caller_caller_id_number varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    consumer_uuid varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NOT NULL,
    consumer_outgoing_uuid varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    bridge_start int NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE fifo_callers  (
    fifo_name varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NOT NULL,
    uuid varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NOT NULL,
    caller_caller_id_name varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    caller_caller_id_number varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    timestamp int NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE interfaces  (
    type varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    name varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    description varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    ikey varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    filename varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    syntax varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    hostname varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;
CREATE TABLE nat  (
    sticky int NULL DEFAULT NULL,
    port int NULL DEFAULT NULL,
    proto int NULL DEFAULT NULL,
    hostname varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    INDEX nat_map_port_proto(port, proto, hostname) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE recovery  (
    runtime_uuid varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    technology varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    profile_name varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    hostname varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    uuid varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    metadata text CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL,
    INDEX recovery1(technology) USING BTREE,
    INDEX recovery2(profile_name) USING BTREE,
    INDEX recovery3(uuid) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE registrations  (
    reg_user varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    realm varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    token varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    url text CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL,
    expires int NULL DEFAULT NULL,
    network_ip varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    network_port varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    network_proto varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    hostname varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    metadata varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    INDEX regindex1(reg_user, realm, hostname) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE sip_authentication  (
    nonce varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    expires bigint NULL DEFAULT NULL,
    profile_name varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    hostname varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    last_nc int NULL DEFAULT NULL,
    INDEX sa_nonce(nonce) USING BTREE,
    INDEX sa_hostname(hostname) USING BTREE,
    INDEX sa_expires(expires) USING BTREE,
    INDEX sa_last_nc(last_nc) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE sip_dialogs  (
    call_id varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    uuid varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    sip_to_user varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    sip_to_host varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    sip_from_user varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    sip_from_host varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    contact_user varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    contact_host varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    state varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    direction varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    user_agent varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    profile_name varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    hostname varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    contact varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    presence_id varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    presence_data varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    call_info varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    call_info_state varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT '',
    expires bigint NULL DEFAULT 0,
    status varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    rpid varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    sip_to_tag varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    sip_from_tag varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    rcd int unsigned NOT NULL DEFAULT 0,
    INDEX sd_uuid(uuid) USING BTREE,
    INDEX sd_hostname(hostname) USING BTREE,
    INDEX sd_presence_data(presence_data) USING BTREE,
    INDEX sd_call_info(call_info) USING BTREE,
    INDEX sd_call_info_state(call_info_state) USING BTREE,
    INDEX sd_expires(expires) USING BTREE,
    INDEX sd_rcd(rcd) USING BTREE,
    INDEX sd_sip_to_tag(sip_to_tag) USING BTREE,
    INDEX sd_sip_from_user(sip_from_user) USING BTREE,
    INDEX sd_sip_from_host(sip_from_host) USING BTREE,
    INDEX sd_sip_to_host(sip_to_host) USING BTREE,
    INDEX sd_presence_id(presence_id) USING BTREE,
    INDEX sd_call_id(call_id) USING BTREE,
    INDEX sd_sip_from_tag(sip_from_tag) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE sip_presence  (
    sip_user varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    sip_host varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    status varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    rpid varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    expires bigint NULL DEFAULT NULL,
    user_agent varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    profile_name varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    hostname varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    network_ip varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    network_port varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    open_closed varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    INDEX sp_hostname(hostname) USING BTREE,
    INDEX sp_open_closed(open_closed) USING BTREE,
    INDEX sp_sip_user(sip_user) USING BTREE,
    INDEX sp_sip_host(sip_host) USING BTREE,
    INDEX sp_profile_name(profile_name) USING BTREE,
    INDEX sp_expires(expires) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE sip_registrations  (
    call_id varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    sip_user varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    sip_host varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    presence_hosts varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    contact varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    status varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    ping_status varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    ping_count int NULL DEFAULT NULL,
    ping_time bigint NULL DEFAULT NULL,
    force_ping int NULL DEFAULT NULL,
    rpid varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    expires bigint NULL DEFAULT NULL,
    ping_expires int unsigned NOT NULL DEFAULT 0,
    user_agent varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    server_user varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    server_host varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    profile_name varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    hostname varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    network_ip varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    network_port varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    sip_username varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    sip_realm varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    mwi_user varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    mwi_host varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    orig_server_host varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    orig_hostname varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    sub_host varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    INDEX sr_call_id(call_id) USING BTREE,
    INDEX sr_sip_user(sip_user) USING BTREE,
    INDEX sr_sip_host(sip_host) USING BTREE,
    INDEX sr_sub_host(sub_host) USING BTREE,
    INDEX sr_mwi_user(mwi_user) USING BTREE,
    INDEX sr_mwi_host(mwi_host) USING BTREE,
    INDEX sr_profile_name(profile_name) USING BTREE,
    INDEX sr_presence_hosts(presence_hosts) USING BTREE,
    INDEX sr_expires(expires) USING BTREE,
    INDEX sr_ping_expires(ping_expires) USING BTREE,
    INDEX sr_hostname(hostname) USING BTREE,
    INDEX sr_status(status) USING BTREE,
    INDEX sr_ping_status(ping_status) USING BTREE,
    INDEX sr_network_ip(network_ip) USING BTREE,
    INDEX sr_network_port(network_port) USING BTREE,
    INDEX sr_sip_username(sip_username) USING BTREE,
    INDEX sr_sip_realm(sip_realm) USING BTREE,
    INDEX sr_orig_server_host(orig_server_host) USING BTREE,
    INDEX sr_orig_hostname(orig_hostname) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE sip_shared_appearance_dialogs  (
    profile_name varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    hostname varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    contact_str varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    call_id varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    network_ip varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    expires bigint NULL DEFAULT NULL,
    INDEX ssd_profile_name(profile_name) USING BTREE,
    INDEX ssd_hostname(hostname) USING BTREE,
    INDEX ssd_contact_str(contact_str) USING BTREE,
    INDEX ssd_call_id(call_id) USING BTREE,
    INDEX ssd_expires(expires) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE sip_shared_appearance_subscriptions  (
    subscriber varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    call_id varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    aor varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    profile_name varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    hostname varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    contact_str varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    network_ip varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    INDEX ssa_hostname(hostname) USING BTREE,
    INDEX ssa_network_ip(network_ip) USING BTREE,
    INDEX ssa_subscriber(subscriber) USING BTREE,
    INDEX ssa_profile_name(profile_name) USING BTREE,
    INDEX ssa_aor(aor) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE sip_subscriptions  (
    proto varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    sip_user varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    sip_host varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    sub_to_user varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    sub_to_host varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    presence_hosts varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    event varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    contact varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    call_id varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    full_from varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    full_via varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    expires bigint NULL DEFAULT NULL,
    user_agent varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    accept varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    profile_name varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    hostname varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    network_port varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    network_ip varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    version int unsigned NOT NULL DEFAULT 0,
    orig_proto varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    full_to varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    INDEX ss_call_id(call_id) USING BTREE,
    INDEX ss_multi(call_id, profile_name, hostname) USING BTREE,
    INDEX ss_hostname(hostname) USING BTREE,
    INDEX ss_network_ip(network_ip) USING BTREE,
    INDEX ss_sip_user(sip_user) USING BTREE,
    INDEX ss_sip_host(sip_host) USING BTREE,
    INDEX ss_presence_hosts(presence_hosts) USING BTREE,
    INDEX ss_event(event) USING BTREE,
    INDEX ss_proto(proto) USING BTREE,
    INDEX ss_sub_to_user(sub_to_user) USING BTREE,
    INDEX ss_sub_to_host(sub_to_host) USING BTREE,
    INDEX ss_expires(expires) USING BTREE,
    INDEX ss_orig_proto(orig_proto) USING BTREE,
    INDEX ss_network_port(network_port) USING BTREE,
    INDEX ss_profile_name(profile_name) USING BTREE,
    INDEX ss_version(version) USING BTREE,
    INDEX ss_full_from(full_from) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE tasks  (
    task_id int NULL DEFAULT NULL,
    task_desc varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    task_group varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    task_runtime bigint NULL DEFAULT NULL,
    task_sql_manager int NULL DEFAULT NULL,
    hostname varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    INDEX tasks1(hostname, task_id) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE voicemail_msgs  (
    created_epoch int NULL DEFAULT NULL,
    read_epoch int NULL DEFAULT NULL,
    username varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    domain varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    uuid varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    cid_name varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    cid_number varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    in_folder varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    file_path varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    message_len int NULL DEFAULT NULL,
    flags varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    read_flags varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    forwarded_by varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    INDEX voicemail_msgs_idx1(created_epoch) USING BTREE,
    INDEX voicemail_msgs_idx2(username) USING BTREE,
    INDEX voicemail_msgs_idx3(domain) USING BTREE,
    INDEX voicemail_msgs_idx4(uuid) USING BTREE,
    INDEX voicemail_msgs_idx5(in_folder) USING BTREE,
    INDEX voicemail_msgs_idx6(read_flags) USING BTREE,
    INDEX voicemail_msgs_idx7(forwarded_by) USING BTREE,
    INDEX voicemail_msgs_idx8(read_epoch) USING BTREE,
    INDEX voicemail_msgs_idx9(flags) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;

CREATE TABLE voicemail_prefs  (
    username varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    domain varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    name_path varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    greeting_path varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    password varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_cs_0900_ai_ci NULL DEFAULT NULL,
    INDEX voicemail_prefs_idx1(username) USING BTREE,
    INDEX voicemail_prefs_idx2(domain) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_cs_0900_ai_ci ROW_FORMAT = Dynamic;
