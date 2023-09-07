/*----------------------------------------------------------------系统配置类--------------------------------------------------------------------------*/
/*系统配置*/
create table sys_config(
    config_name          varchar(40) not null comment '配置名称',
    k                    varchar(40) not null comment '配置名称（枚举）',
    v                    varchar(40) not null comment '配置值',
    update_user_id       bigint unsigned comment '修改人编号',
    update_time          datetime comment '修改时间',
    create_user_id       bigint unsigned comment '创建人编号',
    create_time          datetime comment '创建时间',
    primary key (k)
)engine=innodb default charset=utf8 COMMENT='系统配置';

/*字典类型表*/
create table sys_dictionary_type(
    id                   varchar(40) not null comment '主键',
    code                 varchar(240) not null comment '编码',
    status               tinyint not null comment '状态 1.启用 2.禁用',
    name                 varchar(240) not null comment '字典类型名称',
    update_user_id       bigint unsigned comment '修改人编号',
    update_time          datetime comment '修改时间',
    create_user_id       bigint unsigned comment '创建人编号',
    create_time          datetime comment '创建时间',
    primary key (id)
)engine=innodb default charset=utf8 COMMENT='字典类型表';
/*字典类型条目表*/
create table sys_dictionary_item(
    id                   varchar(40) not null comment '主键',
    sort                 int unsigned not null comment '序号',
    name                 varchar(512) comment '字典类型条目名称',
    value                varchar(1026) not null comment '值',
    type_id              varchar(40) not null comment '类型ID',
    update_user_id       bigint unsigned comment '修改人编号',
    update_time          datetime comment '修改时间',
    create_user_id       bigint unsigned comment '创建人编号',
    create_time          datetime comment '创建时间',
    primary key (id)
)engine=innodb default charset=utf8 COMMENT='字典类型条目表';

create table sys_config(
   config_name          varchar(40) not null comment '配置名称',
   k                    varchar(40) not null comment '配置名称（枚举）',
   v                    varchar(40) not null comment '配置值',
   update_user_id       bigint unsigned comment '修改人编号',
   update_time          datetime comment '修改时间',
   create_user_id       bigint unsigned comment '创建人编号',
   create_time          datetime comment '创建时间',
   primary key (k)
)engine=innodb default charset=utf8 COMMENT='系统配置';

CREATE TABLE IF NOT EXISTS sys_area (
    parent_id           int(20) DEFAULT null COMMENT '地区父节点',
    area_id             int(20) NOT NULL AUTO_INCREMENT COMMENT '地区Id',
    area_code           varchar(50) NOT NULL COMMENT '地区编码',
    area_name           varchar(20) NOT NULL COMMENT '地区名',
    level               tinyint(4) DEFAULT 1 COMMENT '地区级别（1:省份province,2:市city,3:区县district,4:街道street）',
    city_code           varchar(50) DEFAULT NULL COMMENT '城市编码',
    center              varchar(50) DEFAULT NULL COMMENT '城市中心点（即：经纬度坐标）',
    PRIMARY KEY (area_id),
    KEY area_code (area_code),
    KEY parent_id (parent_id),
    KEY level (level),
    KEY area_name (area_name)
) ENGINE=InnoDB AUTO_INCREMENT=3260 DEFAULT CHARSET=utf8 COMMENT='地区码表';

CREATE TABLE IF NOT EXISTS sys_logs (
    id                  int unsigned not null auto_increment COMMENT '主键',
    tenant_id           bigint NOT NULL DEFAULT 1 COMMENT '租户编号',
    type                tinyint NOT NULL DEFAULT 0 COMMENT '日志类型 0.其他 1.登录 2.新增 3.修改 4.删除',
    ip                  varchar(255) DEFAULT NULL COMMENT '访问者ip',
    ip_attribution      varchar(255) DEFAULT NULL COMMENT 'ip所属地',
    method             varchar(255) DEFAULT NULL COMMENT '请求方式',
    api                 varchar(255) DEFAULT NULL COMMENT '访问接口',
    param               text DEFAULT NULL COMMENT '请求参数',
    result              text DEFAULT NULL COMMENT '响应参数',
    duration            varchar(255) DEFAULT NULL COMMENT '持续时间',
    update_user_id      bigint unsigned comment '修改人编号',
    update_time         datetime comment '修改时间',
    create_user_id      bigint unsigned comment '创建人编号',
    create_time         datetime comment '创建时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=3260 DEFAULT CHARSET=utf8 COMMENT='系统日志';

/*租户*/
create table sys_tenant (
    id                  int unsigned not null auto_increment COMMENT '主键',
    tenant_name         varchar(30) NOT NULL COMMENT '租户名',
    tenant_user_id      bigint NULL DEFAULT NULL COMMENT '租户联系人编号',
    tenant_user_name    varchar(30) NULL DEFAULT NULL COMMENT '租户联系人名称',
    status              tinyint NOT NULL DEFAULT 0 COMMENT '租户状态（ 0停用 1正常 ）',
    account_count       int NOT NULL default 0 COMMENT '账号数量',
    update_user_id      bigint unsigned comment '修改人编号',
    update_time         datetime comment '修改时间',
    create_user_id      bigint unsigned comment '创建人编号',
    create_time         datetime comment '创建时间',
    primary key (id)
)engine=innodb default charset=utf8 COMMENT='租户基本信息';

/*租户关联权限表*/
create table sys_tenant_connect_privilege(
    tenant_id            bigint NOT NULL DEFAULT 1 COMMENT '租户编号',
    privilege_id         varchar(1024) not null not null comment '权限编号',
    update_user_id       bigint unsigned comment '修改人编号',
    update_time          datetime comment '修改时间',
    create_user_id       bigint unsigned comment '创建人编号',
    create_time          datetime comment '创建时间'
)engine=innodb default charset=utf8 COMMENT='租户联权限表';

/*短信接口*/
create table sms_sms_config (
    id              int unsigned not null auto_increment COMMENT '主键',
    sms_type        tinyint not null  COMMENT '短信类型',
    config_name     varchar(20) not null  COMMENT '配置名称',
    account         varchar(40)  COMMENT '账号',
    password        varchar(40)  COMMENT '密码',
    balance         varchar(256)  COMMENT '余额',
    is_active       tinyint not null  COMMENT '是否启用',
    sign            varchar(1024)  COMMENT '签名',
    sign_place      tinyint not null  COMMENT '签名位置',
    update_user_id  bigint unsigned comment '修改人编号',
    update_time     datetime comment '修改时间',
    create_user_id  bigint unsigned comment '创建人编号',
    create_time     datetime comment '创建时间',
    primary key (id)
)engine=innodb default charset=utf8 COMMENT='短信接口';

/*短信模板*/
create table sms_mobile_message_template (
    id              int unsigned not null auto_increment COMMENT '主键',
    config_id       int unsigned not null  COMMENT '短信接口id',
    code            varchar(40)  COMMENT '编号',
    type            tinyint not null  COMMENT '类型',
    title           varchar(20)  COMMENT '标题',
    content         varchar(160)  COMMENT '内容',
    receiver        varchar(10)  COMMENT '接收人',
    variable        varchar(80)  COMMENT '变量',
    update_user_id  bigint unsigned comment '修改人编号',
    update_time     datetime comment '修改时间',
    create_user_id  bigint unsigned comment '创建人编号',
    create_time     datetime comment '创建时间',
    primary key( id)
)engine=innodb default charset=utf8 COMMENT='短信模板';

/*短信*/
create table sms_mobile_message (
    id              bigint unsigned not null auto_increment COMMENT '主键',/*主键*/
    sender_id       int unsigned COMMENT '发送人id',
    template_id     varchar(40) COMMENT '模板号',
    type            tinyint not null COMMENT '类型',
    content         varchar(256) COMMENT '内容',
    mobile          char(11) COMMENT '手机号',
    handle_time     datetime COMMENT '操作时间',
    status          tinyint not null COMMENT '状态',
    msg_id          varchar(41) COMMENT '响应编号',
    callback_status varchar(41) COMMENT '返回状态',
    resend_num      tinyint COMMENT '重发次数',
    variable        varchar(256) COMMENT '变量',
    tenant_id       bigint NOT NULL DEFAULT 1 COMMENT '租户编号',
    update_user_id  bigint unsigned comment '修改人编号',
    update_time     datetime comment '修改时间',
    create_user_id  bigint unsigned comment '创建人编号',
    create_time     datetime comment '创建时间',
    primary key(id)
)engine=innodb default charset=utf8 COMMENT='短信';

/*平台通知公告*/
create table sms_public_notice (
   id               bigint unsigned not null auto_increment COMMENT '主键',
   notice_type      tinyint not null COMMENT '通知类型',
   title            varchar(80) not null COMMENT '标题',
   content          varchar(2048) not null COMMENT '内容',
   begin_time       datetime COMMENT '公告开始时间',
   end_time         datetime COMMENT '公告结束时间',
   status           tinyint not null COMMENT '状态 1正常,2已过期',
   update_user_id   bigint unsigned comment '修改人编号',
   update_time      datetime comment '修改时间',
   create_user_id   bigint unsigned comment '创建人编号',
   create_time      datetime comment '创建时间',
   primary key(id),
   index status_notice_type_agent_id (status, notice_type)
) engine=innodb default charset=utf8 COMMENT='平台通知公告';


/*已读公告客户*/
create table sms_read_notice_user (
  id bigint unsigned not null auto_increment  COMMENT '主键',
  notice_id bigint unsigned not null  COMMENT '平台通知公告主键',
  user_id bigint unsigned not null  COMMENT '用户主键',
  update_user_id       bigint unsigned comment '修改人编号',
  update_time          datetime comment '修改时间',
  create_user_id       bigint unsigned comment '创建人编号',
  create_time          datetime comment '创建时间',
  primary key(id),
  unique index idx_notice_id_user_id (notice_id, user_id),
  index user_id_notice_id (user_id, notice_id)
) engine=innodb default charset=utf8 COMMENT='已读公告客户';

/*定时器任务表*/
create table sms_quartz(
    id                   bigint unsigned not null auto_increment comment '主键',
    classes_name         varchar(512) not null  comment '包路径',
    cron_expression      varchar(40) not null comment 'cron表达式',
    task_name            varchar(40) not null comment '任务名',
    group_name           varchar(40) not null comment '任务组名',
    description          varchar(1024)  not null comment '任务描述',
    type                 tinyint not null comment '任务类型',
    task_status          tinyint not null default 0 comment '任务状态',
    start_time           datetime comment '开始时间',
    end_time             datetime comment '结束时间',
    tenant_id            bigint NOT NULL DEFAULT 1 COMMENT '租户编号',
    update_user_id       bigint unsigned comment '修改人编号',
    update_time          datetime comment '修改时间',
    create_user_id       bigint unsigned comment '创建人编号',
    create_time          datetime comment '创建时间',
    primary key(id)
)engine=innodb default charset=utf8 COMMENT='定时器任务表';

-- ----------------------------
-- Table structure for sys_oauth_client
-- ----------------------------
CREATE TABLE sys_oauth_client  (
    client_id                varchar(256)  not null comment '客户端ID',
    resource_ids             varchar(256)  null default null  comment '资源id列表',
    client_secret            varchar(256)  null default null  comment '客户端密钥',
    scope                    varchar(256)  null default null  comment '域',
    authorized_grant_types   varchar(256)  null default null  comment '授权方式',
    web_server_redirect_uri  varchar(256)  null default null  comment '回调地址',
    authorities              varchar(256)  null default null  comment '权限列表',
    access_token_validity    int null default null  comment '认证令牌时效',
    refresh_token_validity   int null default null  comment '刷新令牌时效',
    additional_information   varchar(4096)  null default null  comment '扩展信息',
    autoapprove              varchar(256)  null default null  comment '是否自动放行',
    update_user_id           bigint unsigned comment '修改人编号',
    update_time              datetime comment '修改时间',
    create_user_id           bigint unsigned comment '创建人编号',
    create_time              datetime comment '创建时间',
  primary key (`client_id`) USING BTREE
) engine=innodb default charset=utf8 COMMENT='客户端信息';
/*-----------------------------------------------------------------基础类-------------------------------------------------------------------------*/
/*用户信息表*/
create table bean_mini(
    id                   bigint unsigned not null auto_increment comment '主键',
    open_id              varchar(32) not null  comment '微信登录open_id',
    phone                varchar(23) comment '手机号',
    nick_name            varchar(128)  comment '昵称',
    avatar_url           varchar(1024) comment '微信头像',
    gender               tinyint not null default 0 comment '性别 0.未知 1.男 2.女',
    login_last_time      datetime comment '登录时间',
    update_user_id       bigint unsigned comment '修改人编号',
    update_time          datetime comment '修改时间',
    create_user_id       bigint unsigned comment '创建人编号',
    create_time          datetime comment '创建时间',
    primary key (id),
    unique key idx_open_id(open_id)
)engine=innodb default charset=utf8 COMMENT='微信小程序信息';
/*用户微信小程序中间表*/
create table bean_mini_user(
   id                   bigint unsigned not null auto_increment comment '主键',
   mini_id              bigint unsigned not null comment '微信小程序主键',
   user_id              bigint unsigned not null comment '用户主键',
   update_user_id       bigint unsigned comment '修改人编号',
   update_time          datetime comment '修改时间',
   create_user_id       bigint unsigned comment '创建人编号',
   create_time          datetime comment '创建时间',
   primary key (id),
   unique key idx_mini_id_user_id(mini_id,user_id)
)engine=innodb default charset=utf8 COMMENT='用户微信小程序中间表';

/*用户信息表*/
create table bean_user(
    id                   bigint unsigned not null auto_increment comment '主键',
    user_name            varchar(20) not null comment '人员名称',
    nick_name            varchar(128)  comment '昵称',
    login_account        varchar(128)  comment '账号',
    password             varchar(256)  comment '密码',
    credentialssalt      varchar(1024) comment '加盐',
    image_url            varchar(1024) comment '图像地址',
    phone                varchar(18)  comment '电话',
    gender               tinyint not null default 0 comment '性别 0.未知 1.男 2.女',
    id_card              varchar(20)  comment '身份证号',
    province_id          int unsigned default null comment '省区编码',
    city_id              int unsigned default null comment '市区编码',
    area_id              int unsigned default null comment '县区编码',
    address              varchar(1024) comment '居住地址',
    memo                 varchar(1024) comment '备注',
    tenant_id            bigint NOT NULL DEFAULT 1 COMMENT '租户编号',
    login_last_time      datetime comment '登录时间',
    update_user_id       bigint unsigned comment '修改人编号',
    update_time          datetime comment '修改时间',
    create_user_id       bigint unsigned comment '创建人编号',
    create_time          datetime comment '创建时间',
    primary key (id),
    unique index idx_phone (phone),
    unique index idx_id_card (id_card)
)engine=innodb default charset=utf8 COMMENT='用户基本表';

/*用户设置表*/
create table bean_user_set(
  id                        bigint unsigned not null comment '主键',
  is_admin                  tinyint not null default 0 comment '是否核心管理员 1是 0否',
  is_enabled                tinyint not null default 1 comment '是否启用 1是 0否',
  tenant_id                 bigint NOT NULL DEFAULT 1 COMMENT '租户编号',
  update_user_id            bigint unsigned comment '修改人编号',
  update_time               datetime comment '修改时间',
  create_user_id            bigint unsigned comment '创建人编号',
  create_time               datetime comment '创建时间',
  primary key (id)
)engine=innodb default charset=utf8 COMMENT='用户基本表';

/*用户关联角色表*/
create table bean_user_connect_role(
    user_id                 bigint unsigned not null comment '用户编号',
    tenant_id               bigint NOT NULL DEFAULT 1 COMMENT '租户编号',
    role_id                 bigint unsigned not null comment '角色编号',
    update_user_id           bigint unsigned comment '修改人编号',
    update_time              datetime comment '修改时间',
    create_user_id           bigint unsigned comment '创建人编号',
    create_time              datetime comment '创建时间'
)engine=innodb default charset=utf8 COMMENT='用户关联角色表';
/*角色表*/
create table bean_role(
    id                       bigint unsigned not null auto_increment comment '主键',
    role_name                varchar(20) not null comment '角色名称',
    tenant_id                bigint NOT NULL DEFAULT 1 COMMENT '租户编号',
    memo                     varchar(1024) comment '备注',
    update_user_id           bigint unsigned comment '修改人编号',
    update_time              datetime comment '修改时间',
    create_user_id           bigint unsigned comment '创建人编号',
    create_time              datetime comment '创建时间',
    primary key (id)
)engine=innodb default charset=utf8 COMMENT='角色表';
/*角色关联权限表*/
create table bean_role_connect_privilege(
   role_id              bigint unsigned not null comment '角色编号',
   tenant_id bigint NOT NULL DEFAULT 1 COMMENT '租户编号',
   privilege_id         varchar(1024) not null not null comment '权限编号',
   update_user_id           bigint unsigned comment '修改人编号',
   update_time              datetime comment '修改时间',
   create_user_id           bigint unsigned comment '创建人编号',
   create_time              datetime comment '创建时间'
)engine=innodb default charset=utf8 COMMENT='角色关联权限表';
/*权限表*/
create table bean_privilege(
    id                   varchar(1024) not null comment '主键',
    privilege_name       varchar(20) not null comment '权限名称',
    is_open              tinyint not null default 0 comment '是否开启 1.是 0否',
    request_url          varchar(1024) comment '请求路径',
    menu_id              varchar(1024) not null comment '菜单编号',
    memo                 varchar(1024) comment '备注',
    update_user_id       bigint unsigned comment '修改人编号',
    update_time          datetime comment '修改时间',
    create_user_id       bigint unsigned comment '创建人编号',
    create_time          datetime comment '创建时间',
    primary key (id)
)engine=innodb default charset=utf8 COMMENT='权限表';

/*菜单表*/
create table bean_menu(
    id                   varchar(1024) not null comment '主键',
    parent_id            varchar(360) comment '父级菜单',
    level                int not null comment '级别',
    menu_name            varchar(40) not null comment '菜单名称',
    path                 varchar(480) comment '跳转路径',
    view_path            varchar(480) comment '页面路径',
    icon                 varchar(60) comment '小图标',
    is_open              tinyint not null default 0 comment '是否开启 1.是 0否',
    hide_menu            tinyint not null default 0 comment '是否隐藏 1.是 0否',
    num                  int comment '序号',
    memo                 varchar(1024) comment '备注',
    update_user_id       bigint unsigned comment '修改人编号',
    update_time          datetime comment '修改时间',
    create_user_id       bigint unsigned comment '创建人编号',
    create_time          datetime comment '创建时间',
    primary key (id),
    key idx_parent_id_menu_name (parent_id, menu_name)
)engine=innodb default charset=utf8 COMMENT='菜单表';

/*部门表*/
create table bean_department(
    id                      bigint unsigned not null auto_increment comment '主键',
    parent_id               bigint unsigned comment '父级菜单',
    department_name         varchar(40) not null comment '部门名称',
    is_enable               tinyint not null default 0 comment '是否开启 1.是 0否',
    memo                    varchar(1024) comment '备注',
    tenant_id               bigint NOT NULL DEFAULT 1 COMMENT '租户编号',
    update_user_id          bigint unsigned comment '修改人编号',
    update_time             datetime comment '修改时间',
    create_user_id          bigint unsigned comment '创建人编号',
    create_time             datetime comment '创建时间',
    primary key (id),
    key idx_parent_id_menu_name (parent_id, department_name)
)engine=innodb default charset=utf8 COMMENT='部门表';
/*用户关联部门表*/
create table bean_user_connect_department(
    user_id             bigint unsigned not null comment '用户编号',
    tenant_id           bigint NOT NULL DEFAULT 1 COMMENT '租户编号',
    department_id       bigint unsigned not null  comment '部门编号',
    update_user_id      bigint unsigned comment '修改人编号',
    update_time         datetime comment '修改时间',
    create_user_id      bigint unsigned comment '创建人编号',
    create_time         datetime comment '创建时间'
)engine=innodb default charset=utf8 COMMENT='用户关联部门表';

/*部门关联权限表*/
create table bean_department_connect_privilege(
    department_id        bigint unsigned not null  comment '部门编号',
    tenant_id            bigint NOT NULL DEFAULT 1 COMMENT '租户编号',
    privilege_id         varchar(1024) not null not null comment '权限编号',
    update_user_id       bigint unsigned comment '修改人编号',
    update_time          datetime comment '修改时间',
    create_user_id       bigint unsigned comment '创建人编号',
    create_time          datetime comment '创建时间'
)engine=innodb default charset=utf8 COMMENT='部门关联权限表';
/*职位表*/
create table bean_position(
    id                   bigint unsigned not null auto_increment comment '主键',
    tenant_id            bigint NOT NULL DEFAULT 1 COMMENT '租户编号',
    parent_id            bigint unsigned comment '父级菜单',
    position_name        varchar(40) not null comment '部门名称',
    is_enable            tinyint not null default 0 comment '是否开启 1.是 0否',
    update_user_id       bigint unsigned comment '修改人编号',
    update_time          datetime comment '修改时间',
    create_user_id       bigint unsigned comment '创建人编号',
    create_time          datetime comment '创建时间',
    memo                 varchar(1024) comment '备注',
    primary key (id),
    key idx_parent_id_menu_name (parent_id, position_name)
)engine=innodb default charset=utf8 COMMENT='职位表';
/*用户关联职位表*/
create table bean_user_connect_position(
    user_id              bigint unsigned not null comment '用户编号',
    tenant_id            bigint NOT NULL DEFAULT 1 COMMENT '租户编号',
    position_id          bigint unsigned not null  comment '部门编号',
    update_user_id       bigint unsigned comment '修改人编号',
    update_time          datetime comment '修改时间',
    create_user_id       bigint unsigned comment '创建人编号',
    create_time          datetime comment '创建时间'
)engine=innodb default charset=utf8 COMMENT='用户关联职位表';

/*职位关联权限表*/
create table bean_position_connect_privilege(
    position_id          bigint unsigned not null  comment '部门编号',
    tenant_id            bigint NOT NULL DEFAULT 1 COMMENT '租户编号',
    privilege_id         varchar(1024) not null comment '权限编号',
    update_user_id       bigint unsigned comment '修改人编号',
    update_time          datetime comment '修改时间',
    create_user_id       bigint unsigned comment '创建人编号',
    create_time          datetime comment '创建时间'
)engine=innodb default charset=utf8 COMMENT='职位关联权限表';