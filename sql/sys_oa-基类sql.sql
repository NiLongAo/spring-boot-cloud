-- ------------请假表----------------
CREATE TABLE `oa_leave` (
    id                      bigint unsigned not null auto_increment COMMENT '主键id',
    start_time              datetime comment '开始时间',
    end_time                datetime comment '结束时间',
    tenant_id            bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
    day                     int(3) DEFAULT NULL COMMENT '请假天数',
    process_instance_id     varchar(500) DEFAULT NULL COMMENT '流程实例主键',
    state                   tinyint not null default 1 COMMENT '状态 1.审核中 2.审核成功 3.审核不通过',
    memo                    varchar(1024) comment '备注',
    user_id                 bigint unsigned not null COMMENT '发起人ID',
    user_name               varchar(20) not null COMMENT '发起人名称',
    department_id           bigint unsigned not null COMMENT '部门ID',
    department_name         varchar(20) not null COMMENT '部门名称',
    update_user_id       bigint unsigned comment '修改人编号',
    update_time          datetime comment '修改时间',
    create_user_id          bigint unsigned not null comment '创建人编号',
    create_time             datetime not null comment '创建时间',
    primary key (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8  ROW_FORMAT = DYNAMIC COMMENT ='请假表';
