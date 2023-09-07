CREATE TABLE `face_person` (
    id                      bigint unsigned not null auto_increment COMMENT '主键id',
    img_id                  varchar(40) not null comment '图片自定义编号',
    img_url                 varchar(256) not null comment '图片地址',
    extract                 varchar(256) not null comment '人脸特征数组',
    person_name             varchar(40) not null comment '人员姓名',
    person_age              int(3) unsigned comment '年龄',
    gender                  tinyint not null comment '性别 0.未知 1.男 2.女',
    address                 varchar(256) not null comment '地址',
    update_user_id          bigint unsigned comment '修改人编号',
    update_time             datetime comment '修改时间',
    create_user_id          bigint unsigned not null comment '创建人编号',
    create_time             datetime not null comment '创建时间',
    primary key (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8  ROW_FORMAT = DYNAMIC COMMENT ='人员信息';