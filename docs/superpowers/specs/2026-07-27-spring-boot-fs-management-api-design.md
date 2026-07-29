# spring-boot-fs 管理接口方案设计

## 1. 背景与目标

`spring-boot-business/spring-boot-fs` 已具备 FreeSWITCH 运行时核心逻辑、41 张业务表对应的 Entity、Mapper、Service，以及启动时装载 Redis 运行时配置的 `CompanyRunner`。当前缺口是管理端接口，后续前端无法完成平台、企业、路由、坐席、技能组、VDN 和运行记录等页面。

本期目标：

1. 定义稳定的管理页面和接口契约。
2. 补齐参数对象、返回对象、业务 Service 和 Controller。
3. 对关联表采用聚合保存，避免前端直接理解 41 张物理表。
4. 数据库事务提交后刷新对应 Redis 运行时配置。
5. 提供分页、详情、保存、状态切换、删除和下拉选择能力。

本期不包含前端页面、不新增通话控制能力、不自动执行 FreeSWITCH reload/restart。

## 2. 现状结论

- 模块只有 `/api/fs/xml` 和媒体 Hook 类接口，没有管理端 Controller。
- 大部分 `fs` Service 只是继承 MyBatis-Plus `IService`，没有分页、校验、聚合保存和删除保护能力。
- `spring-boot-entity` 中没有 `param.fs` 和 `vo.fs` 管理对象。
- `CompanyRunner` 能构建企业、路由、技能组、溢出、VDN、放音和会议等 Redis 数据，但装载逻辑不能被管理接口复用。
- 数据库关联表普遍没有外键，业务层必须负责引用校验和事务一致性。

## 3. 方案选择

### 方案 A：一表一 CRUD

每张表提供 `page/detail/save/remove`。实现快，但会产生约 41 组接口，前端需要理解关联表，技能组、坐席和 VDN 容易出现部分保存成功。

### 方案 B：按页面聚合（推荐）

主表形成页面，关联表作为详情子资源或聚合保存内容；日志表只读；配置写操作统一处理校验、事务和缓存刷新。

### 方案 C：元数据通用 CRUD

Controller 少，但类型安全、Swagger、业务校验、聚合事务和缓存刷新能力不足。

采用方案 B。接口按业务页面聚合，不按表机械暴露。

## 4. 页面与表归属

### 4.1 系统配置

| 管理页面 | 主表 | 子表/关联表 |
| --- | --- | --- |
| FreeSWITCH 平台 | `fs_platform` | 无 |
| 流媒体服务 | `fs_media_server` | 无 |
| FS 注册网关 | `fs_gate_way` | 无 |
| 路由网关 | `fs_route_gateway` | 无 |
| 路由组 | `fs_route_group` | `fs_route_gateway_group` |
| 外呼路由 | `fs_route_call` | 无 |

### 4.2 企业资源

| 管理页面 | 主表 | 子表/关联表 |
| --- | --- | --- |
| 企业管理 | `fs_company` | 无 |
| 企业会议 | `fs_company_conference` | 无 |
| 号码资源 | `fs_company_display`、`fs_company_phone` | `fs_company_phone_group` |
| 放音文件 | `fs_playback` | 无 |

### 4.3 呼叫中心配置

| 管理页面 | 主表 | 子表/关联表 |
| --- | --- | --- |
| 技能组 | `fs_group` | `fs_group_agent_strategy`、`fs_group_strategy_exp`、`fs_group_memory_config`、`fs_group_overflow`、`fs_skill_group` |
| 技能 | `fs_skill` | `fs_skill_agent`、`fs_skill_group` |
| 坐席 | `fs_agent` | `fs_agent_sip`、`fs_agent_group`、`fs_user_agent`、`fs_skill_agent` |
| 溢出策略 | `fs_overflow_config` | `fs_overflow_front`、`fs_overflow_exp` |

### 4.4 呼入与 IVR

| 管理页面 | 主表 | 子表/关联表 |
| --- | --- | --- |
| IVR 流程 | `fs_ivr_workflow` | 无 |
| VDN 日程 | `fs_vdn_schedule` | 无 |
| 呼入路由 | `fs_vdn_code` | `fs_vdn_phone`、`fs_vdn_config`、`fs_vdn_dtmf` |

### 4.5 运行记录

| 管理页面 | 主表 | 子表/关联表 |
| --- | --- | --- |
| 通话记录 | `fs_call_log` | `fs_call_detail`、`fs_call_device`、`fs_call_dtmf` |
| 坐席状态日志 | `fs_agent_state_log` | 无 |
| 主叫记忆 | `fs_group_memory` | 无 |
| 话单推送日志 | `fs_push_log` | 无 |

关联表不单独建立菜单，只在所属页面的详情、抽屉或标签页中管理。

## 5. API 通用约定

- 根路径：`/api/fs`。
- 分页：`POST /{resource}/page`，返回 `PageResult`。
- 详情：`GET /{resource}/detail?id=...`，返回 `RestResult<?>`。
- 保存：`POST /{resource}/save`，`id` 为空表示新增，否则表示修改。
- 删除：`DELETE /{resource}/remove?id=...`。
- 状态切换：`POST /{resource}/status`。
- 下拉选项：`GET /{resource}/select`，支持 `keyword`、`companyId` 和 `limit`。
- 聚合关系保存：`POST /{resource}/save_*`，请求表示完整关系集合替换。

新增对象目录：

```text
spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/param/fs
spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/vo/fs
```

- `*PageParam`：筛选和分页字段。
- `*SaveParam`：可编辑字段，不直接使用 Entity 接收请求。
- `*StatusParam`：`id`、`status`。
- `*DetailVo`：页面聚合详情。
- `*OptionVo`：下拉框轻量字段。
- 日志 VO 可包含子列表，但日志 Entity 不允许被管理接口修改。

## 6. 各管理页面接口

### 6.1 FreeSWITCH 平台 `/api/fs/platform`

`POST /page`、`GET /detail`、`POST /save`、`POST /status`、`DELETE /remove`、`GET /select`。

筛选名称、IP、启用状态；校验 RTP 起止端口、WS/WSS 端口和端口冲突。

### 6.2 流媒体服务 `/api/fs/media_server`

`POST /page`、`GET /detail`、`POST /save`、`POST /status`、`POST /default`、`DELETE /remove`、`GET /select`。

支持 IP、启用、在线和默认节点筛选；设置默认节点时在事务内取消其他默认节点；活动流承载期间禁止删除。

### 6.3 FS 注册网关 `/api/fs/fs_gateway`

`POST /page`、`GET /detail`、`POST /save`、`POST /selected`、`DELETE /remove`、`GET /select`。

`fs_gate_way` 没有通用 `status` 字段，启用语义使用现有 `selected`；同时管理 `register`、`transport`、realm、账号和重试间隔。密码只写不读。

### 6.4 路由网关 `/api/fs/route_gateway`

`POST /page`、`GET /detail`、`POST /save`、`POST /status`、`DELETE /remove`、`GET /select`。

管理媒体地址、端口、号码前缀、Profile 和 SIP Header；被路由组引用时拒绝删除。

### 6.5 路由组 `/api/fs/route_group`

`POST /page`、`GET /detail`、`POST /save`、`POST /save_gateways`、`POST /status`、`DELETE /remove`、`GET /select`。

`save_gateways` 原子替换 `fs_route_gateway_group`；被外呼路由使用时拒绝删除。

### 6.6 外呼路由 `/api/fs/route_call`

`POST /page`、`GET /detail`、`POST /save`、`POST /status`、`DELETE /remove`。

按企业、路由号码、路由组和状态筛选；同一企业 `routeNum` 不重复，`numMin` 不得大于 `numMax`。

### 6.7 企业管理 `/api/fs/company`

`POST /page`、`GET /detail`、`POST /save`、`POST /status`、`DELETE /remove`、`GET /select`。

详情返回资源使用数量；存在坐席、技能组、路由或话单时拒绝删除；`name`、`companyCode` 全局唯一；密钥脱敏，修改时空值表示保持原值。

### 6.8 企业会议 `/api/fs/company_conference`

`POST /page`、`GET /detail`、`POST /save`、`POST /status`、`DELETE /remove`、`GET /select`。

按企业、名称、会议号和状态筛选；会议密码只写不读。

### 6.9 号码资源 `/api/fs/company_number`

- 显号组：`POST /display_page`、`GET /display_detail`、`POST /display_save`、`DELETE /display_remove`。
- 企业号码：`POST /phone_page`、`GET /phone_detail`、`POST /phone_save`、`POST /phone_status`、`DELETE /phone_remove`。
- 关系：`POST /save_display_phones` 原子替换号码组关系。
- 下拉：`GET /phone_select`，按企业、类型和关键字筛选。

显号组被技能组引用时拒绝删除；号码被显号组引用时拒绝删除。

### 6.10 放音文件 `/api/fs/playback`

`POST /page`、`GET /detail`、`POST /save`、`POST /status`、`DELETE /remove`、`GET /select`。

只管理资源引用，不负责上传；被技能组、VDN 或其他配置引用时拒绝删除。后续前端复用现有静态文件上传接口。

### 6.11 技能组 `/api/fs/group`

`POST /page`、`GET /detail`、`POST /save`、`POST /status`、`DELETE /remove`、`GET /select`、`GET /options`。

`GroupSaveParam` 在单个事务中保存：

- `fs_group` 基础配置。
- 单条 `fs_group_agent_strategy`。
- 多条 `fs_group_strategy_exp`。
- 零或一条 `fs_group_memory_config`。
- 完整的 `fs_group_overflow` 关系集合。
- 完整的 `fs_skill_group` 配置集合。

`options` 返回当前企业可选技能、溢出策略、放音和显号组。坐席与技能组关系由坐席页面维护，避免双写。

### 6.12 技能 `/api/fs/skill`

`POST /page`、`GET /detail`、`POST /save`、`POST /status`、`DELETE /remove`、`GET /select`。

技能页展示关联坐席和技能组数量；具体关系参数分别由坐席页和技能组页维护；存在关系时拒绝删除。

### 6.13 坐席 `/api/fs/agent`

`POST /page`、`GET /detail`、`POST /save`、`POST /status`、`DELETE /remove`、`GET /select`、`GET /runtime`。

`AgentSaveParam` 在单个事务中保存坐席基础信息、SIP 号码、用户绑定、技能配置和技能组关系。密码只写不读；在线通话中禁止停用或删除。`runtime` 查询 Redis 中的注册、在线和当前工作状态。

### 6.14 溢出策略 `/api/fs/overflow`

`POST /page`、`GET /detail`、`POST /save`、`DELETE /remove`、`GET /select`。

`save` 在单个事务中保存 `fs_overflow_config`、`fs_overflow_front` 和 `fs_overflow_exp`；被技能组引用时拒绝删除。

### 6.15 IVR 流程 `/api/fs/ivr_workflow`

`POST /page`、`GET /detail`、`POST /save`、`POST /status`、`DELETE /remove`、`GET /select`。

本期只保存现有 `content` 和 `initParams`，不定义新的流程编辑器 DSL；被 VDN 路由引用时拒绝删除。

### 6.16 VDN 日程 `/api/fs/vdn_schedule`

`POST /page`、`GET /detail`、`POST /save`、`POST /status`、`DELETE /remove`、`GET /select`。

校验日期、星期、时间范围和优先级；被 VDN 配置引用时拒绝删除。

### 6.17 呼入路由 `/api/fs/vdn`

`POST /page`、`GET /detail`、`POST /save`、`POST /status`、`DELETE /remove`、`GET /select`、`GET /options`。

`VdnSaveParam` 在单个事务中保存 `fs_vdn_code`、`fs_vdn_phone`、`fs_vdn_config` 和每条配置对应的 `fs_vdn_dtmf`。`options` 返回日程、技能组、放音、IVR 和坐席等路由目标。

当前数据库唯一索引 `uni_idx_phone(vdn_id, company_id)` 限制每个 VDN 只有一个接入号码，因此首期参数使用单个 `phone`；如前端需要一条 VDN 配置多个号码，应先调整索引为企业内号码唯一，再改为列表。

### 6.18 通话记录 `/api/fs/call_log`

`POST /page`、`GET /detail`、`GET /timeline`、`GET /record`、`POST /export`。

页面只读；按企业、主被叫、坐席、技能组、呼叫方向、应答状态和时间范围筛选；默认强制时间范围。详情聚合 `fs_call_detail`、`fs_call_device`、`fs_call_dtmf` 和录音元数据。

### 6.19 坐席状态日志 `/api/fs/agent_state_log`

`POST /page`、`GET /detail`、`POST /export`。页面只读，按企业、技能组、坐席、状态、工作类型和时间范围筛选。

### 6.20 主叫记忆 `/api/fs/group_memory`

`POST /page`、`GET /detail`、`DELETE /remove`。除管理员清理单条失效记忆外不允许修改。

### 6.21 话单推送日志 `/api/fs/push_log`

`POST /page`、`GET /detail`。首期只读；重新推送需先明确现有推送组件的幂等策略，避免重复通知下游。

## 7. 业务层与缓存设计

```text
Controller
  -> Fs*ManageService
      -> 现有 fs IService / Mapper
      -> FsRelationValidator
      -> FsRuntimeConfigRefreshPublisher
          -> @TransactionalEventListener(AFTER_COMMIT)
              -> FsRuntimeConfigService
                  -> RedisService 各 Manager
```

- Controller 只负责参数校验和转发。
- `Fs*ManageService` 负责分页、聚合保存、唯一性校验、引用校验和事务。
- 现有基础 Service 保留，避免把管理语义混入运行时调用 Service。
- 从 `CompanyRunner` 提取 `FsRuntimeConfigService`，启动装载和管理接口刷新共用同一套组装逻辑。
- 配置刷新事件在事务提交后执行，数据库回滚时不能刷新 Redis。

缓存刷新粒度：

| 变更类型 | 刷新动作 |
| --- | --- |
| 企业、路由、会议、号码、放音、技能组、技能、溢出、VDN | 刷新对应 `companyId` 的企业运行时聚合 |
| 坐席、SIP、坐席技能、坐席技能组 | 刷新对应 `agentId` 和受影响技能组 |
| 媒体服务 | 更新媒体服务运行数据；默认节点原子切换 |
| 平台、FS 注册网关 | 更新安全的配置缓存，返回“待重载”提示，不自动执行系统命令 |
| 日志、主叫记忆 | 不刷新配置缓存 |

如果定向刷新第一阶段实现成本过高，可按企业重建，但禁止日常保存调用全局 `delAll()`。

聚合保存统一规则：

1. 主表和子表在同一事务中提交。
2. 子表采用按主资源完整替换语义。
3. 保存前校验关联 ID 存在、状态可用且属于同一 `companyId`。
4. 事务提交后发布缓存刷新事件。
5. 任一步失败时数据库不产生部分数据，Redis 不刷新。

## 8. 校验、删除与安全

- 按数据库唯一索引执行业务层预校验，并保留唯一索引作为最终防线。
- `status`、`type`、`routeType` 等字段使用明确枚举范围。
- IP、端口、URL、号码、日期和时间范围使用 Bean Validation 与业务校验。
- 企业级关联资源必须属于同一企业。
- 分页大小设置上限，日志分页强制时间范围。
- 当前没有统一逻辑删除字段，首期继续物理删除；有引用时拒绝删除并返回引用数量。
- 聚合纯关系表随主资源删除，核心资源不级联删除历史日志。
- 密码、密钥和会议密码不在列表中返回，详情脱敏，日志不打印明文。
- 管理接口不直接执行 FreeSWITCH CLI、系统命令或文件删除。
- 后续权限编码按 `fs:{resource}:query/save/remove/status` 设计，不影响接口路径。

## 9. 测试方案

### 9.1 单元测试

- 分页筛选条件。
- 唯一性和跨企业关联校验。
- 被引用资源删除拒绝。
- 聚合关系完整替换。
- 在线状态限制。
- 密码和密钥不回显。
- 日志查询强制时间范围。

### 9.2 Service 集成测试

- 主表和子表同时保存成功。
- 子表失败时主表回滚。
- 事务回滚时不发布刷新。
- 事务提交后只刷新受影响企业、坐席或技能组。

### 9.3 Controller 测试

- 使用 MockMvc 验证路径、HTTP 方法、参数校验和返回结构。
- 每类页面覆盖 `page/detail/save/remove` 或对应只读接口。
- 聚合页面覆盖完整请求 JSON 和校验错误。

## 10. 实施顺序

1. 建立公共参数、返回对象、异常和校验工具。
2. 提取 `CompanyRunner` 装载逻辑，建立事务提交后的定向缓存刷新。
3. 实现平台、媒体服务、两类网关、路由组和外呼路由。
4. 实现企业、会议、号码资源和放音。
5. 实现溢出、技能、技能组和坐席。
6. 实现 IVR、VDN 日程和呼入路由。
7. 实现话单、坐席状态、主叫记忆和推送日志。
8. 补齐 Swagger、MockMvc 测试和模块构建验证。

每阶段保持模块可编译、对应测试可独立通过。

## 11. 验收标准

- 21 个管理页面均有明确接口契约。
- 41 张 `fs_*` 表均归入独立页面、聚合子资源或只读日志。
- 管理请求不直接使用 Entity 接收 JSON。
- 技能组、坐席、溢出和 VDN 支持事务性聚合保存。
- 配置变更在事务提交后定向刷新运行时缓存。
- 删除前执行引用检查，日志表不暴露任意修改接口。
- 密码和密钥不明文回显。
- `spring-boot-fs` 模块编译通过，新增 Service 和 Controller 测试通过。

## 12. 实施前确认项

1. 第一阶段覆盖全部 21 个管理页面，而不是只做企业、技能组和坐席最小闭环。
2. 使用按页面聚合的接口，不采用一表一 CRUD。
3. 前端尚未开始，可以统一采用本文 `/api/fs/{resource}` 路径。
4. 平台和网关保存后不自动执行 FreeSWITCH reload/restart。
5. 话单重新推送不在首期范围。
6. VDN 首期按数据库现有约束使用单接入号码。
