# spring-boot-fs Management API Roadmap

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 分阶段交付 `spring-boot-fs` 的 21 个管理页面接口，并保证聚合事务、引用校验和运行时缓存一致性。

**Architecture:** 管理请求使用独立 Param/VO，经 `*ManageService` 调用现有 MyBatis-Plus Service/Mapper。聚合配置在事务中完整保存，企业运行时配置通过事务提交后的事件定向刷新 Redis；日志接口保持只读。

**Tech Stack:** Java 8、Spring Boot、Spring MVC、Spring Transaction、MyBatis-Plus、MapStruct、Bean Validation、Redis、JUnit 4、Mockito、MockMvc、Maven。

---

## 交付拆分

### Phase 1：公共基础与系统配置

覆盖页面：FreeSWITCH 平台、流媒体服务、FS 注册网关、路由网关、路由组、外呼路由。

详细计划：`docs/superpowers/plans/2026-07-27-spring-boot-fs-system-config-api.md`

验收命令：

```powershell
mvn -pl spring-boot-business/spring-boot-fs -am -DskipTests compile
mvn -pl spring-boot-business/spring-boot-fs -am -Dtest=PlatformManageServiceTest,MediaServerManageServiceTest,FsGatewayManageServiceTest,RouteGatewayManageServiceTest,RouteGroupManageServiceTest,RouteCallManageServiceTest,FsSystemConfigControllerTest -Dsurefire.failIfNoSpecifiedTests=false test
```

### Phase 2：企业资源

覆盖页面：企业管理、企业会议、号码资源、放音文件。

主要新增：

```text
spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/param/fs/company
spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/vo/fs/company
spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/service/manage/company
spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/controller/api/fs/manage/company
```

关键行为：企业唯一性、密钥脱敏、号码组关系完整替换、被引用资源删除保护、企业级缓存刷新。

### Phase 3：呼叫中心配置

覆盖页面：溢出策略、技能、技能组、坐席。

主要新增：

```text
spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/param/fs/callcenter
spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/vo/fs/callcenter
spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/service/manage/callcenter
spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/controller/api/fs/manage/callcenter
```

关键行为：技能组、坐席和溢出策略的事务性聚合保存；跨企业关系拒绝；在线坐席状态限制；企业、坐席和技能组定向刷新。

### Phase 4：IVR 与呼入路由

覆盖页面：IVR 流程、VDN 日程、呼入路由。

主要新增：

```text
spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/param/fs/ivr
spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/vo/fs/ivr
spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/service/manage/ivr
spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/controller/api/fs/manage/ivr
```

关键行为：VDN 四表聚合保存；路由目标类型校验；日程范围校验；按现有唯一索引保持单接入号码；企业级缓存刷新。

### Phase 5：运行记录

覆盖页面：通话记录、坐席状态日志、主叫记忆、话单推送日志。

主要新增：

```text
spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/param/fs/log
spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/vo/fs/log
spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/service/manage/log
spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/controller/api/fs/manage/log
```

关键行为：日志只读、强制时间范围、话单四表聚合详情、录音元数据、主叫记忆单条清理。

### Phase 6：全量契约与回归

- [ ] 为 21 个页面补齐 Swagger 描述和请求示例。
- [ ] 生成接口清单并与设计文档逐项核对。
- [ ] 运行 `spring-boot-entity` 与 `spring-boot-fs` 全量测试。
- [ ] 运行聚合构建，确认无循环依赖和 MapStruct 生成错误。
- [ ] 检查密码、密钥、会议密码和 SIP 密码不明文回显。
- [ ] 检查所有配置删除路径都有引用保护。

最终验证：

```powershell
mvn -pl spring-boot-common/spring-boot-entity,spring-boot-business/spring-boot-fs -am test
mvn -pl spring-boot-business/spring-boot-fs -am package -DskipTests
```

## 执行约束

- 严格按 TDD：先写失败测试，确认失败原因，再写最小实现。
- 每个阶段独立可编译、可测试、可回滚。
- 不直接使用 Entity 接收管理端 JSON。
- 不在 Controller 中编写查询、事务或缓存逻辑。
- 不执行 FreeSWITCH reload/restart 或系统文件删除。
- 未经用户明确要求，不执行 `git commit`。
