/*菜单初始化数据*/
truncate table bean_menu;
insert into bean_menu(parent_id,id,`type`,menu_name,`path`,component,icon,status,hide_in_menu,`order`,auth_code,active_icon,active_path,request_url)
-- 首页
values(null,'home',2,'首页','/home','/index/home/workbench/index','clarity:home-solid',1,0,1,'home:list','clarity:home-solid',null,null)
-- 工作台
,(null,'work',1,'工作台','/work','/work','grommet-icons:test',1,0,2,null,'grommet-icons:test',null,null)
,('work','work.oa',2,'工作流','/work/oa','/work/oa/index','grommet-icons:test',1,0,1,'work.oa:list','grommet-icons:test',null,null)
,('work','work.leave',2,'请假申请','/work/leave/:id','/work/leave/index','grommet-icons:test',1,1,2,'work.leave:list','grommet-icons:test','/work/oa',null)
,('work','work.personal',2,'个人中心','/work/personal','/work/personal/index','grommet-icons:test',1,0,3,'work.personal:list','grommet-icons:test',null,null)
,('work.personal','work.personal.user_save',3,'个人信息保存',null,null,null,1,0,1,'work.personal:user_save',null,null,'/webapi/bean/user/update_login_user_info')
,('work.personal','work.personal.other_save',3,'绑定解绑第三方信息',null,null,null,1,0,2,'work.personal:other_save',null,null,'/webapi/mini/bind_mini_web,/webapi/mini/unbind_mini_web')
-- 流程管理
,(null,'oa',1,'流程管理','/oa','/oa','grommet-icons:test',1,0,3,null,'grommet-icons:test',null,null)
,('oa','oa.repository',2,'流程定义','/oa/repository','/oa/repository/index','grommet-icons:test',1,0,1,'oa.repository:list','grommet-icons:test',null,'/webapi/activiti/activiti/find_repository_list')
,('oa.repository','oa.repository.add',3,'添加',null,null,null,1,0,1,'oa.repository:add',null,null,'/webapi/activiti/activiti/deploy_process_parameter,/webapi/activiti/activiti/deploy_process')
,('oa.repository','oa.repository.deploy',3,'部署',null,null,null,1,0,2,'oa.repository:deploy',null,null,'/webapi/activiti/activiti/deploy_process_parameter,/webapi/activiti/activiti/deploy_process')
,('oa.repository','oa.repository.pending',3,'挂起-激活',null,null,null,1,0,3,'oa.repository:pending',null,null,'/webapi/activiti/activiti/suspended_process_definition')
,('oa.repository','oa.repository.delete',3,'删除',null,null,null,1,0,4,'oa.repository:delete',null,null,'/webapi/activiti/activiti/delete_process')
,('oa','oa.deploy',2,'流程部署','/oa/deploy/:id','/oa/deploy/index','mdi:card-account-details',1,1,2,'oa.deploy:list','mdi:card-account-details','/oa/repository',null)
,('oa.deploy','oa.deploy.deploy',3,'部署',null,null,null,1,0,1,'oa.deploy:deploy',null,null,'/webapi/activiti/activiti/deploy_process_parameter')
,('oa','oa.need',2,'待办流程','/oa/need','/oa/need/index','grommet-icons:test',1,0,3,'oa.need:list','grommet-icons:test',null,null)
,('oa.need','oa.need.pending',3,'挂起-激活',null,null,null,1,0,2,'oa.need:pending',null,null,'/webapi/activiti/activiti/suspended_instance')
,('oa.need','oa.need.reject',3,'驳回',null,null,null,1,0,3,'oa.need:reject',null,null,'/webapi/activiti/activiti/back_process')
,('oa','oa.historic',2,'历史流程','/oa/historic','/oa/historic/index','grommet-icons:test',1,0,4,'oa.historic:list','grommet-icons:test',null,null)
,('oa.historic','oa.historic.detail',3,'详情',null,null,null,1,0,1,'oa.historic:detail',null,null,null)
-- 系统监控
,(null,'monitor',1,'系统监控','/monitor','/monitor','grommet-icons:test',1,0,4,null,'grommet-icons:test',null,null)
,('monitor','monitor.naocs',4,'服务注册中心','/monitor/naocs','https://www.nilongao.cn/nacos/','grommet-icons:test',1,0,1,'monitore.naocs:list','grommet-icons:test',null,null)
,('monitor','monitor.minio',4,'资源文件','/monitor/minio','https://www.nilongao.cn/minio','grommet-icons:test',1,0,2,'monitore.minio:list','grommet-icons:test',null,null)
,('monitor','monitor.sentinel',4,'熔断限流配置','/monitor/sentinel','https://www.nilongao.cn:8850/','grommet-icons:test',1,0,3,'monitore.sentinel:list','grommet-icons:test',null,null)
,('monitor','monitor.skywalking',4,'链路追踪','/monitor/skyWalking','https://www.nilongao.cn:12013/','grommet-icons:test',1,0,4,'monitore.skywalking:list','grommet-icons:test',null,null)
,('monitor','monitor.jenkins',5,'服务构建','http://1.82.217.118:10240/','http://1.82.217.118:10240/','grommet-icons:test',1,0,5,'monitore.jenkins:list','grommet-icons:test',null,null)
,('monitor','monitor.elasticsearch',4,'elasticsearch可视化','/monitor/kibana','https://www.nilongao.cn:10012/','grommet-icons:test',1,0,6,'monitore.elasticsearch:list','grommet-icons:test',null,null)
,('monitor','monitor.swagger',4,'Swagger接口文档','/monitor/swagger','https://www.nilongao.cn/basic-api/doc.html','grommet-icons:test',1,0,7,'monitore.swagger:list','grommet-icons:test',null,null)
,('monitor','monitor.rabbitmq',4,'RabbitMq消息队列','/monitor/rabbitmq','https://www.nilongao.cn/rabbitmq/','grommet-icons:test',1,0,8,'monitore.rabbitmq:list','grommet-icons:test',null,null)
,('monitor','monitor.seata',4,'Seata分布式事务','/monitor/seata','https://www.nilongao.cn:8092/','grommet-icons:test',1,0,9,'monitore.seata:list','grommet-icons:test',null,null)
,('monitor','monitor.xxl-job',4,'XXL-JOB分布式定时器','/monitor/xxl-job','https://www.nilongao.cn/xxl-job-admin/','grommet-icons:test',1,0,10,'monitore.xxl-job:list','grommet-icons:test',null,null)
,('monitor','monitor.certd',4,'自动化SSL证书','/monitor/certd','https://www.nilongao.cn:7001/','grommet-icons:test',1,0,11,'monitore.certd:list','grommet-icons:test',null,null)
,('monitor','monitor.one-api',4,'API语言大模型','/monitor/oneApi','https://www.nilongao.cn:7011/','grommet-icons:test',1,0,12,'monitore.one-api:list','grommet-icons:test',null,null)
,('monitor','monitor.v2raya',4,'代理服务','/monitor/v2raya','https://www.nilongao.cn:7891/','grommet-icons:test',1,0,13,'monitore.v2raya:list','grommet-icons:test',null,null)
-- 视频管理
,(null,'video',1,'视频管理','/video','/vide','grommet-icons:test',1,0,4,null,'grommet-icons:test',null,null)
,('video','video.dispatch',2,'分屏调度','/video/dispatch','/video/dispatch/index','grommet-icons:test',1,0,1,'video.dispatch:list','grommet-icons:test',null,'/webapi/video/device/page')
,('video.dispatch','video.dispatch.play',3,'播放',null,null,null,1,0,1,'video.dispatch:play',null,null,'/webapi/video/play/start')
,('video','video.play',2,'国标设备','/video/play','/video/play/index','grommet-icons:test',1,0,2,'video.play:list','grommet-icons:test',null,'/webapi/video/device/page')
,('video.play','video.play.refresh',3,'刷新',null,null,null,1,0,1,'video.play:refresh',null,null,'/webapi/video/device/channel/sync')
,('video.play','video.play.device_channel',3,'设备通道',null,null,null,1,0,2,'video.play:device_channel',null,null,'/webapi/video/device/channel/page')
,('video.play','video.play.add',3,'添加',null,null,null,1,0,3,'video.play:add',null,null,'/webapi/video/device/save_device')
,('video.play','video.play.update',3,'编辑',null,null,null,1,0,4,'video.play:update',null,null,'/webapi/video/device/save_device')
,('video.play','video.play.delete',3,'删除',null,null,null,1,0,5,'video.play:delete',null,null,'/webapi/video/device/del')
,('video','video.play.channel',2,'通道信息','/video/play/channel/:id','/video/play/channel/index','mdi:card-account-details',1,1,2,'video.play.channel:list','mdi:card-account-details','/video/play','/webapi/video/device/channel/page')
,('video.play.channel','video.play.channel.add',3,'添加',null,null,null,1,0,1,'video.play.channel:add',null,null,'/webapi/video/device/channel/save')
,('video.play.channel','video.play.channel.update',3,'编辑',null,null,null,1,0,2,'video.play.channel:update',null,null,'/webapi/video/device/channel/save')
,('video.play.channel','video.play.channel.delete',3,'删除',null,null,null,1,0,3,'video.play.channel:delete',null,null,'/webapi/video/device/channel/del')
,('video.play.channel','video.play.channel.play',3,'播放',null,null,null,1,0,4,'video.play.channel:play',null,null,'/webapi/video/play/start')
,('video.play.channel','video.play.channel.suspend',3,'暂停',null,null,null,1,0,5,'video.play.channel:suspend',null,null,'/webapi/video/play/start/stop')
,('video.play.channel','video.play.channel.playback',3,'历史回放',null,null,null,1,0,6,'video.play.channel:playback',null,null,'/webapi/video/device/channel/page')
,('video','video.play.record',2,'国标录像','/video/play/record/:deviceId/:channelId','/video/play/record/index','mdi:card-account-details',1,1,3,'video.play.record:list','mdi:card-account-details','/video/play','/webapi/video/device/channel/page')
,('video.play.record','video.play.record.play',3,'播放',null,null,null,1,0,1,'video.play.record:play',null,null,'/webapi/video/playback/start')
,('video.play.record','video.play.record.suspend',3,'暂停',null,null,null,1,0,2,'video.play.record:suspend',null,null,'/webapi/video/playback/stop')
,('video','video.media',2,'流媒体管理','/video/media','/video/media/index','grommet-icons:test',1,0,5,'video.media:list','grommet-icons:test',null,'/webapi/video/media/server/page')
,('video.media','video.media.add',3,'添加',null,null,null,1,0,1,'video.media:add',null,null,'/webapi/video/media/server/save')
,('video.media','video.media.update',3,'编辑',null,null,null,1,0,2,'video.media:update',null,null,'/webapi/video/media/server/save')
,('video.media','video.media.delete',3,'删除',null,null,null,1,0,3,'video.media:delete',null,null,'/webapi/video/media/server/remove')
,('video','video.platform',2,'国标级联','/video/platform','/video/platform/index','grommet-icons:test',1,0,6,'video.platform:list','grommet-icons:test',null,'/webapi/video/parent/platform/page')
,('video.platform','video.platform.add',3,'添加',null,null,null,1,0,1,'video.platform:add',null,null,'/webapi/video/parent/platform/insert')
,('video.platform','video.platform.join',3,'关联',null,null,null,1,0,2,'video.platform:join',null,null,'/webapi/video/platform/gb_channel/insert,/webapi/video/platform/gb_stream/add')
,('video.platform','video.platform.update',3,'编辑',null,null,null,1,0,3,'video.platform:update',null,null,'/webapi/video/parent/platform/update')
,('video.platform','video.platform.delete',3,'删除',null,null,null,1,0,4,'video.platform:delete',null,null,'/webapi/video/parent/platform/delete')
,('video','video.proxy',2,'拉流管理','/video/proxy','/video/proxy/index','grommet-icons:test',1,0,7,'video.proxy:list','grommet-icons:test',null,'/webapi/video/stream/proxy/page')
,('video.proxy','video.proxy.add',3,'添加',null,null,null,1,0,1,'video.proxy:add',null,null,'/webapi/video/stream/proxy/save')
,('video.proxy','video.proxy.enable',3,'启用-停用',null,null,null,1,0,2,'video.proxy:enable',null,null,'/webapi/video/stream/proxy/start,/webapi/video/stream/proxy/stop')
,('video.proxy','video.proxy.play',3,'播放',null,null,null,1,0,3,'video.proxy:play',null,null,'/webapi/video/stream/proxy/get_play_url')
,('video.proxy','video.proxy.update',3,'编辑',null,null,null,1,0,4,'video.proxy:update',null,null,'/webapi/video/stream/proxy/save')
,('video.proxy','video.proxy.delete',3,'删除',null,null,null,1,0,5,'video.proxy:delete',null,null,'/webapi/video/stream/proxy/remove')
,('video','video.push',2,'推流管理','/video/push','/video/push/index','grommet-icons:test',1,0,8,'video.push:list','grommet-icons:test',null,'/webapi/video/stream/push/page')
,('video.push','video.push.add',3,'添加',null,null,null,1,0,1,'video.push:add',null,null,'/webapi/video/stream/push/save')
,('video.push','video.push.play',3,'播放',null,null,null,1,0,2,'video.push:play',null,null,'/webapi/video/stream/push/get_play_url')
,('video.push','video.push.update',3,'编辑',null,null,null,1,0,3,'video.push:update',null,null,'/webapi/video/stream/push/save')
,('video.push','video.push.delete',3,'删除',null,null,null,1,0,4,'video.push:delete',null,null,'/webapi/video/stream/push/remove')
-- 通话中心
,(null,'fs',1,'通话中心','/fs','/fs','grommet-icons:test',1,0,5,null,'grommet-icons:test',null,null)
,('fs','fs.call',2,'客服通话','/fs/call','/fs/call/index','grommet-icons:test',1,0,1,'fs.call:list',null,null,null)
-- 系统设置
,(null,'system',1,'系统设置','/system','/index/system','icon-park-outline:system',1,0,100,null,'icon-park-outline:system',null,null)
,('system','system.tenant',2,'租户管理','/system/tenant','/index/system/tenant/index','icon-park-outline:menu-fold-one',1,0,1,'system.tenant:list','icon-park-outline:menu-fold-one',null,'/webapi/bean/tenant/page')
,('system.tenant','system.tenant.add',3,'新增',null,null,null,1,0,2,'system.tenant:add',null,null,'/webapi/bean/tenant/insert')
,('system.tenant','system.tenant.update',3,'编辑',null,null,null,1,0,3,'system.tenant:update',null,null,'/webapi/bean/tenant/update')
,('system.tenant','system.tenant.delete',3,'删除',null,null,null,1,0,4,'system.tenant:delete',null,null,'/webapi/bean/tenant/remove')
,('system','system.user',2,'用户管理','/system/user','/index/system/user/index','ri:user-3-fill',1,0,1,'system.user:list','ri:user-3-fill',null,'/webapi/bean/user/page')
,('system.user','system.user.add',3,'新增',null,null,null,1,0,2,'system.user:add',null,null,'/webapi/bean/user/insert')
,('system.user','system.user.update',3,'编辑',null,null,null,1,0,3,'system.user:update',null,null,'/webapi/bean/user/update')
,('system.user','system.user.delete',3,'删除',null,null,null,1,0,4,'system.user:delete',null,null,'/webapi/bean/user/delete')
,('system.user','system.user.detail',3,'详情',null,null,null,1,0,5,'system.user:detail',null,null,'/webapi/bean/user/info')
,('system.user','system.user.print',3,'打印',null,null,null,1,0,6,'system.user:print',null,null,'/webapi/bean/user/info')
,('system.user','system.user.export',3,'导出',null,null,null,1,0,7,'system.user:export',null,null,'/webapi/bean/user/info')
,('system','system.user.detail.list',2,'用户详情','/system/user/user_detail/:id','/index/system/user/UserDetail','mdi:card-account-details',1,1,2,'system.user.detail:list','mdi:card-account-details',null,'/webapi/bean/user/info')
,('system','system.role',2,'角色管理','/system/role','/index/system/role/index','carbon:user-role',1,0,3,'system.role:list','carbon:user-role',null,'/webapi/bean/role/page')
,('system.role','system.role.add',3,'新增',null,null,null,1,0,2,'system.role:add',null,null,'/webapi/bean/role/detail')
,('system.role','system.role.update',3,'编辑',null,null,null,1,0,3,'system.role:update',null,null,'/webapi/bean/role/save')
,('system.role','system.role.delete',3,'删除',null,null,null,1,0,4,'system.role:delete',null,null,'/webapi/bean/role/remove')
,('system','system.department',2,'部门管理','/system/department','/index/system/department/index','ic:outline-apartment',1,0,5,'system.department:list','ic:outline-apartment',null,'/webapi/bean/department/page')
,('system.department','system.department.add',3,'新增',null,null,null,1,0,2,'system.department:add',null,null,'/webapi/bean/department/save')
,('system.department','system.department.update',3,'编辑',null,null,null,1,0,3,'system.department:update',null,null,'/webapi/bean/department/save')
,('system.department','system.department.delete',3,'删除',null,null,null,1,0,4,'system.department:delete',null,null,'/webapi/bean/department/remove')
,('system','system.position',2,'职位管理','/system/position','/index/system/position/index','mdi:account-question-outline',1,0,7,'system.position:list','mdi:account-question-outline',null,'/webapi/bean/position/page')
,('system.position','system.position.add',3,'新增',null,null,null,1,0,2,'system.position:add',null,null,'/webapi/bean/position/save')
,('system.position','system.position.update',3,'编辑',null,null,null,1,0,3,'system.position:update',null,null,'/webapi/bean/position/save')
,('system.position','system.position.delete',3,'删除',null,null,null,1,0,4,'system.position:delete',null,null,'/webapi/bean/position/remove')
,('system','system.privilege',2,'权限管理','/system/privilege','/index/system/privilege/index','mdi:account-question-outline',1,0,8,'system.privilege:list','mdi:account-question-outline',null,'/webapi/bean/role/all,/webapi/bean/department/tree,/webapi/bean/position/tree,/webapi/bean/privilege/department_privilege_list,/webapi/bean/privilege/position_privilege_list,/webapi/bean/privilege/role_privilege_list')
,('system.privilege','system.privilege.save',3,'保存',null,null,null,1,0,2,'system.privilege:save',null,null,'/webapi/bean/privilege/department_privilege_save,/webapi/bean/privilege/position_privilege_save,/webapi/bean/privilege/role_privilege_save')
,('system','system.menu',2,'菜单管理','/system/menu','/index/system/menu/index','icon-park-outline:menu-fold-one',1,0,9,'system.menu:list','icon-park-outline:menu-fold-one',null,'/webapi/bean/menu/page')
,('system.menu','system.menu.add',3,'新增',null,null,null,1,0,2,'system.menu:add',null,null,'/webapi/bean/menu/save')
,('system.menu','system.menu.update',3,'编辑',null,null,null,1,0,3,'system.menu:update',null,null,'/webapi/bean/menu/save')
,('system.menu','system.menu.delete',3,'删除',null,null,null,1,0,4,'system.menu:delete',null,null,'/webapi/bean/menu/remove')
,('system','system.sms',1,'短信管理','/system/sms','','icon-park-outline:doc-search-two',1,0,10,null,'icon-park-outline:doc-search-two',null,null)
,('system.sms','system.sms.sms_config',2,'短信配置','/system/sms/sms_config','/index/system/sms/sms_config/index','icon-park-outline:doc-search-two',1,0,1,'system.sms.sms_config:list','icon-park-outline:doc-search-two',null,'/webapi/sms/sms_config/page')
,('system.sms.sms_config','system.sms.sms_config.add',3,'新增',null,null,null,1,0,1,'system.sms.sms_config:add',null,null,'/webapi/sms/sms_config/insert')
,('system.sms.sms_config','system.sms.sms_config.update',3,'编辑',null,null,null,1,0,2,'system.sms.sms_config:update',null,null,'/webapi/sms/sms_config/update')
,('system.sms.sms_config','system.sms.sms_config.delete',3,'删除',null,null,null,1,0,3,'system.sms.sms_config:delete',null,null,'/webapi/sms/sms_config/remove')
,('system.sms','system.sms.mobile_message_template',2,'短信模板','/system/sms/mobile_message_template','/index/system/sms/mobile_message_template/index','icon-park-outline:doc-search-two',1,0,2,'system.sms.mobile_message_template:list','icon-park-outline:doc-search-two',null,'/webapi/sms/mobile_message_template/page')
,('system.sms.mobile_message_template','system.sms.mobile_message_template.add',3,'新增',null,null,null,1,0,1,'system.sms.mobile_message_template:add',null,null,'/webapi/sms/mobile_message_template/insert')
,('system.sms.mobile_message_template','system.sms.mobile_message_template.update',3,'编辑',null,null,null,1,0,2,'system.sms.mobile_message_template:update',null,null,'/webapi/sms/mobile_message_template/update')
,('system.sms.mobile_message_template','system.sms.mobile_message_template.delete',3,'删除',null,null,null,1,0,3,'system.sms.mobile_message_template:delete',null,null,'/webapi/sms/mobile_message_template/remove')
,('system.sms','system.sms.mobile_message',2,'短信记录','/system/sms/mobile_message','/index/system/sms/mobile_message/index','icon-park-outline:doc-search-two',1,0,3,'system.sms.mobile_message:list','icon-park-outline:doc-search-two',null,'/webapi/sms/mobile_message/page')
,('system','system.dictionary',2,'字典管理','/system/dictionary','/index/system/dictionary/index','icon-park-outline:doc-search-two',1,0,11,'system.dictionary:list','icon-park-outline:doc-search-two',null,'/webapi/config/dictionary_type/find_type_list,/webapi/config/dictionary_item/page')
,('system.dictionary','system.dictionary.add_type',3,'新增类型',null,null,null,1,0,1,'system.dictionary:add_type',null,null,'/webapi/config/dictionary_type/save')
,('system.dictionary','system.dictionary.update_type',3,'编辑类型',null,null,null,1,0,2,'system.dictionary:update_type',null,null,'/webapi/config/dictionary_type/save')
,('system.dictionary','system.dictionary.delete_type',3,'删除类型',null,null,null,1,0,3,'system.dictionary:delete_type',null,null,'/webapi/config/dictionary_type/remove')
,('system.dictionary','system.dictionary.add_item',3,'新增条目',null,null,null,1,0,4,'system.dictionary:add_item',null,null,'/webapi/config/dictionary_item/save')
,('system.dictionary','system.dictionary.update_item',3,'编辑条目',null,null,null,1,0,5,'system.dictionary:update_item',null,null,'/webapi/config/dictionary_item/save')
,('system.dictionary','system.dictionary.delete_item',3,'删除条目',null,null,null,1,0,6,'system.dictionary:delete_item',null,null,'/webapi/config/dictionary_item/remove')
,('system','system.config',2,'系统管理','/system/config','/index/system/config/index','icon-park-outline:circles-seven',1,0,12,'system.config:list','icon-park-outline:circles-seven',null,'/webapi/config/config/page')
,('system.config','system.config.update',3,'编辑',null,null,null,1,0,1,'system.config:update',null,null,'/webapi/config/config/update')
,('system','system.oauth_client',2,'客户端管理','/system/oauth_client','/index/system/oauth_client/index','zondicons:tablet',1,0,13,'system.oauth_client:list','zondicons:tablet',null,'/webapi/config/oauth_client/page')
,('system.oauth_client','system.oauth_client.add',3,'新增',null,null,null,1,0,1,'system.oauth_client:add',null,null,'/webapi/config/oauth_client/save')
,('system.oauth_client','system.oauth_client.update',3,'编辑',null,null,null,1,0,2,'system.oauth_client:update',null,null,'/webapi/config/oauth_client/save')
,('system.oauth_client','system.oauth_client.delete',3,'删除',null,null,null,1,0,3,'system.oauth_client:delete',null,null,'/webapi/config/oauth_client/remove')
,('system','system.notice',2,'平台公告通知','/system/notice','/index/system/notice/index','zondicons:tablet',1,0,14,'system.notice:list','zondicons:tablet',null,'/webapi/notice/public_notice/page')
,('system.notice','system.notice.add',3,'新增',null,null,null,1,0,1,'system.notice:add',null,null,'/webapi/notice/public_notice/insert')
,('system.notice','system.notice.update',3,'编辑',null,null,null,1,0,2,'system.notice:update',null,null,'/webapi/notice/public_notice/update')
,('system.notice','system.notice.delete',3,'删除',null,null,null,1,0,3,'system.notice:delete',null,null,'/webapi/notice/public_notice/remove')
,('system','system.logs',2,'系统日志','/system/logs','/index/system/logs/index','zondicons:tablet',1,0,15,'system.logs:list','zondicons:tablet',null,'/webapi/config/logs/page')
,('system.logs','system.logs.detail',3,'详情',null,null,null,1,0,1,'system.logs:detail',null,null,'/webapi/config/logs/detail')
;
/*系统配置*/
insert into sys_config(config_name,k,v)values('minio服务器地址','minio.path','http://1.82.217.118:9000/spring-clond');
insert into sys_config(config_name,k,v)values('打印水印名称','print.name','测试水印信息');
/*租户默认信息*/
insert into sys_tenant(id,tenant_name,tenant_user_id,tenant_user_name,status,account_count,update_user_id,update_time,create_user_id,create_time)values
(1,'系统租户',1,'root',1,0,1,now(),1,now());
/*租户添加默认菜单*/
INSERT INTO sys_tenant_connect_menu(tenant_id,menu_id,update_user_id,update_time,create_user_id,create_time)
(select 1,auth_code,1,now(),1,now() from bean_menu where auth_code is not null and auth_code != '');
# 添加角色
INSERT INTO `bean_role` (`id`, `role_name`, `memo`, `update_user_id`, `update_time`, `create_user_id`, `create_time`, `tenant_id`)
VALUES(1, '系统管理员', '111', 1, '2023-09-09 13:26:40', NULL, NULL, 1)
,(2, '开放角色', '111', 1, '2022-10-16 11:55:37', 1, '2022-10-16 11:55:37', 9);
# 添加职位
INSERT INTO `bean_position` (`id`, `parent_id`, `position_name`, `is_enable`, `memo`, `update_user_id`, `update_time`, `create_user_id`, `create_time`, `tenant_id`)
VALUES (1, NULL, '开发者', 1, NULL, NULL, NULL, NULL, NULL, 1)
,(2, NULL, '运维者', 1, NULL, NULL, NULL, NULL, NULL, 1)
,(3, NULL, '公审者', 1, '发射东风', NULL, NULL, NULL, NULL, 1)
,(5, NULL, '开放职位', 1, '1111', 1, '2022-10-16 11:56:11', 1, '2022-10-16 11:56:11', 9);
# 添加部门
INSERT INTO `bean_department` (`id`, `parent_id`, `department_name`, `is_enable`, `memo`, `update_user_id`, `update_time`, `create_user_id`, `create_time`, `tenant_id`)
VALUES (1, NULL, '科研部', 1, NULL, NULL, NULL, NULL, NULL, 1)
,(10, NULL, '开放部门', 1, '测试', 13, '2024-02-29 14:30:33', 13, '2024-02-29 14:30:33', 9);




/*短信配置*/
insert into sms_sms_config(id,sms_type,config_name,account,password,balance,is_active,sign,sign_place,update_time)values (1,8,'腾讯云短信','AKIDVotiHacaIkvqWyAWdU5QUIsELCn9F6k9','DBtLYUBcniPq32nJbeS0IK6ctufAPO7b','0',1,'仝泽勇我的JAVA学习记',1,now());
insert into sms_mobile_message_template(id,config_id,code,type,title,content,receiver,variable)values (1,1,'1146534',1,'登录验证','{VERIFICATION_CODE}为您的登录验证码，请于{REDIS_CODE}分钟内填写，如非本人操作，请忽略本短信。',null,'{"VERIFICATION_CODE":"","REDIS_CODE":"5"}');
insert into sms_mobile_message_template(id,config_id,code,type,title,content,receiver,variable)values (2,1,'1146535',2,'注册验证码','您正在申请手机注册，验证码为：{VERIFICATION_CODE}，{REDIS_CODE}分钟内有效！',null,'{"VERIFICATION_CODE":"","REDIS_CODE":"5"}');
insert into sms_mobile_message_template(id,config_id,code,type,title,content,receiver,variable)values (3,1,'1146536',3,'密码重置','您的动态验证码为：{VERIFICATION_CODE}，您正在进行密码重置操作，如非本人操作，请忽略本短信！',null,'{"VERIFICATION_CODE":""}');

/*初始化用户*/
insert into bean_user(id,user_name,nick_name,login_account,password,credentialssalt,phone,gender,id_card,login_last_time,update_user_id,update_time,create_user_id,create_time)values
(1,'root','root','root','$2a$10$7eW0yCH6YvIaQhdXpsO3L..vX9uaxUbznzr59l10a1HitY2QJ9uHW','789','18789432816',1,'610125199508234332',now(),1,now(),1,now());
insert into bean_user_set(id,is_admin,is_enabled)values (1,1,1);


/*初始化客户端*/
insert into sys_oauth_client(client_id,resource_ids,client_secret,scope,authorized_grant_types,web_server_redirect_uri,authorities,access_token_validity,refresh_token_validity,additional_information,autoapprove)
values('web-api-client',null,'web-api-client','all','authorization_code,password,refresh_token,implicit,client_credentials','http://1.82.217.118:9200',null,7200,7200,null,null)
,('app-client',null,'app-client','all','authorization_code,password,refresh_token,implicit,client_credentials','http://1.82.217.118:9200',null,7200,7200,null,null)
,('mini-web-app',null,'mini-web-app','all','authorization_code,password,refresh_token,implicit,client_credentials','http://1.82.217.118:9200',null,7200,7200,null,null)
;

/*定时器初始化*/
insert into sms_quartz(id,classes_name,cron_expression,task_name,group_name,description,type,task_status,start_time,end_time)values (1,'cn.com.tzy.springbootsms.config.quartz.task.PubLicScheduler','0 0 0/1 * * ?','QUARTZ_SOCKET_SOCKET','QUARTZ_SOCKET','socket定时推送信息',1,1,null,null);


/*初始化字典*/
insert into sys_dictionary_type(id,code,status,name)values ('OA_ACTIVITI','OA_ACTIVITI',1,'流程相关信息字典');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('OA_ACTIVITI','Leave',1,'请假流程','LeaveOaService:/work/leave');
insert into sys_dictionary_type(id,code,status,name)values ('AUTHORIZATION','AUTHORIZATION',1,'登陆授权方式');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('AUTHORIZATION','AUTHORIZATION_CODE',1,'授权码模式','authorization_code');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('AUTHORIZATION','IMPLICIT',2,'简化模式','implicit');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('AUTHORIZATION','PASSWORD',3,'密码模式','password');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('AUTHORIZATION','CLIENT_CREDENTIALS',4,'客户端模式','client_credentials');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('AUTHORIZATION','REFRESH_TOKEN',5,'刷新令牌','refresh_token');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('AUTHORIZATION','CODE',6,'普通认证模式','code');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('AUTHORIZATION','SMS',7,'短信认证模式','sms');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('AUTHORIZATION','WX_MINI',8,'微信小程序认证类型','wx_mini');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('AUTHORIZATION','WX_MINI_WEB',9,'微信小程序web认证类型','wx_mini_web');
insert into sys_dictionary_type(id,code,status,name)values ('SMS_TYPE','SMS_TYPE',1,'短信类型');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('SMS_TYPE','DXW',1,'短信网','1');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('SMS_TYPE','CLW',2,'创蓝网','2');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('SMS_TYPE','WND',3,'维纳多','3');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('SMS_TYPE','SWLH',4,'商务领航','4');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('SMS_TYPE','ALYDY',5,'阿里云大于','5');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('SMS_TYPE','WYYD',6,'网易易盾','6');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('SMS_TYPE','YTX',7,'云通讯','7');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('SMS_TYPE','TXY',8,'腾讯云','8');
insert into sys_dictionary_type(id,code,status,name)values ('SMS_SEND_TYPE','SMS_SEND_TYPE',1,'短信发送类型');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('SMS_SEND_TYPE','LOGIN_VERIFICATION_CODE',1,'登录验证码','1');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('SMS_SEND_TYPE','REGISTER_VERIFICATION_CODE',2,'注册验证码','2');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('SMS_SEND_TYPE','RESET_VERIFICATION_CODE',3,'重置密码验证码','3');
insert into sys_dictionary_type(id,code,status,name)values ('GENDER','GENDER',1,'性别');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('GENDER','UNKNOWN',1,'未知','0');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('GENDER','MALE',2,'男','1');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('GENDER','FEMALE',3,'女','2');
insert into sys_dictionary_type(id,code,status,name)values ('LOGS_TYPE_ENUM','LOGS_TYPE_ENUM',1,'系统日志类型');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('LOGS_TYPE_ENUM','LOGIN',1,'登录','1');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('LOGS_TYPE_ENUM','INSERT',2,'新增','2');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('LOGS_TYPE_ENUM','UPDATE',3,'修改','3');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('LOGS_TYPE_ENUM','DELETE',4,'删除','4');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('LOGS_TYPE_ENUM','OTHER',5,'其他','0');
insert into sys_dictionary_type(id,code,status,name)values ('TRANSPORT_TYPE_ENUM','TRANSPORT_TYPE_ENUM',1,'SIP传输协议');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('TRANSPORT_TYPE_ENUM','UDP',1,'UDP','1');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('TRANSPORT_TYPE_ENUM','TCP',2,'TCP','2');
insert into sys_dictionary_type(id,code,status,name)values ('CHARSET_TYPE_ENUM','CHARSET_TYPE_ENUM',1,'SIP传输字符编码');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('CHARSET_TYPE_ENUM','UTF_8',1,'UTF_8','1');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('CHARSET_TYPE_ENUM','GB2312',2,'GB2312','2');
insert into sys_dictionary_type(id,code,status,name)values ('DEVICE_ALARM_METHOD_ENUM','DEVICE_ALARM_METHOD_ENUM',1,'SIP报警方式');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('DEVICE_ALARM_METHOD_ENUM','TELEPHONE',1,'电话报警','1');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('DEVICE_ALARM_METHOD_ENUM','DEVICE',2,'设备报警','2');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('DEVICE_ALARM_METHOD_ENUM','SMS_ALARM',3,'短信报警','3');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('DEVICE_ALARM_METHOD_ENUM','GPS_ALARM',4,'GPS报警','4');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('DEVICE_ALARM_METHOD_ENUM','VIDEO_ALARM',5,'视频报警','5');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('DEVICE_ALARM_METHOD_ENUM','DEVICE_FAILURE_ALARM',6,'设备故障报警','6');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('DEVICE_ALARM_METHOD_ENUM','OTHER_ALARM',7,'其他报警','7');
insert into sys_dictionary_type(id,code,status,name)values ('PROXY_TYPE_ENUM','PROXY_TYPE_ENUM',1,'拉流类型');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('PROXY_TYPE_ENUM','DEFAULT',1,'默认拉流','1');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('PROXY_TYPE_ENUM','FFMPEG',2,'ffmpeg拉流','2');
insert into sys_dictionary_type(id,code,status,name)values ('PROXY_RTP_TYPE_ENUM','PROXY_RTP_TYPE_ENUM',1,'拉流方式');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('PROXY_RTP_TYPE_ENUM','PROXY_RTP_TCP',1,'TCP','0');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('PROXY_RTP_TYPE_ENUM','PROXY_RTP_UDP',2,'UDP','1');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('PROXY_RTP_TYPE_ENUM','PROXY_RTP_MULTICAST',3,'组播','2');
insert into sys_dictionary_type(id,code,status,name)values ('STREAM_MODE_TYPE_ENUM','STREAM_MODE_TYPE_ENUM',1,'数据流传输模式');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('STREAM_MODE_TYPE_ENUM','UDP_STREAM_MODE',1,'UDP模式','0');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('STREAM_MODE_TYPE_ENUM','TCP_PASSIVE_STREAM_MODE',2,'TCP被动模式','1');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('STREAM_MODE_TYPE_ENUM','TCP_ACTIVE_STREAM_MODE',3,'TCP主动模式','2');
insert into sys_dictionary_type(id,code,status,name)values ('STREAM_TYPE_ENUM','STREAM_TYPE_ENUM',1,'国标流类型');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('STREAM_TYPE_ENUM','PROXY_STREAM_TYPE',1,'拉流','1');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('STREAM_TYPE_ENUM','PULL_STREAM_TYPE',2,'推流','2');
insert into sys_dictionary_type(id,code,status,name)values ('TREE_TYPE_ENUM','TREE_TYPE_ENUM',1,'设备分组');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('TREE_TYPE_ENUM','BUSINESS_GROUPING',1,'业务分组编码','215');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('TREE_TYPE_ENUM','VIRTUAL_ORGANIZATION',2,'虚拟组织编码','216');
insert into sys_dictionary_type(id,code,status,name)values ('GEO_COORD_SYS_TYPE_ENUM','GEO_COORD_SYS_TYPE_ENUM',1,'地理坐标系');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('GEO_COORD_SYS_TYPE_ENUM','WGS84',1,'WGS84','1');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('GEO_COORD_SYS_TYPE_ENUM','GCJ02',2,'GCJ02','2');
insert into sys_dictionary_type(id,code,status,name)values ('DEVICE_TYPE_ENUM','DEVICE_TYPE_ENUM',1,'设备状态');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('DEVICE_TYPE_ENUM','ONLINE_DEVICE',1,'在线','1');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('DEVICE_TYPE_ENUM','OFF_DEVICE',2,'离线','0');
insert into sys_dictionary_type(id,code,status,name)values ('PTZ_TYPE_ENUM','PTZ_TYPE_ENUM',1,'云台类型');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('PTZ_TYPE_ENUM','UNKNOWN_PTZ',1,'未知','0');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('PTZ_TYPE_ENUM','PTZ_PTZ',2,'球机','1');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('PTZ_TYPE_ENUM','HEMISPHERE_PTZ',3,'半球','2');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('PTZ_TYPE_ENUM','FIXED_PTZ',4,'固定枪机','3');
insert into sys_dictionary_item(type_id,id,sort,name,value)values ('PTZ_TYPE_ENUM','REMOTE_CONTROL_PTZ',5,'遥控枪机','4');