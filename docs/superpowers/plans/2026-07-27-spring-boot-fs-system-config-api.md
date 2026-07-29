# spring-boot-fs System Configuration API Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 交付 FreeSWITCH 平台、流媒体服务、FS 注册网关、路由网关、路由组和外呼路由六个管理页面的后端接口。

**Architecture:** 请求 DTO 和页面 VO 位于 `spring-boot-entity`，管理语义位于 `service/manage/system`，Controller 只转发。路由相关变更在数据库事务提交后发布企业配置刷新事件；平台和 FS 注册网关只保存配置并返回待重载语义，不执行系统命令。

**Tech Stack:** Java 8、Spring Boot、Spring MVC、Spring Transaction、MyBatis-Plus、MapStruct、Bean Validation、JUnit 4、Mockito、MockMvc、Maven。

---

## 文件结构

```text
spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/param/fs/common
spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/param/fs/system
spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/vo/fs/common
spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/vo/fs/system
spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/convert/manage/system
spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/service/manage/system
spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/service/manage/system/impl
spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/controller/api/fs/manage/system
spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/config/fs/runtime
spring-boot-business/spring-boot-fs/src/test/java/cn/com/tzy/springbootfs/manage/system
```

### Task 1: 公共状态参数和下拉返回对象

**Files:**
- Modify: `spring-boot-business/spring-boot-fs/pom.xml`
- Create: `spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/param/fs/common/FsLongStatusParam.java`
- Create: `spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/param/fs/common/FsStringStatusParam.java`
- Create: `spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/vo/fs/common/FsOptionVo.java`
- Create: `spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/config/fs/runtime/FsCompanyConfigChangedEvent.java`
- Create: `spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/config/fs/runtime/FsRuntimeConfigRefreshPublisher.java`
- Test: `spring-boot-business/spring-boot-fs/src/test/java/cn/com/tzy/springbootfs/manage/system/FsCommonContractTest.java`

- [ ] **Step 1: 仅为 spring-boot-fs 启用测试执行**

在 `spring-boot-business/spring-boot-fs/pom.xml` 的 `<build><plugins>` 中覆盖 Surefire：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <skipTests>false</skipTests>
    </configuration>
</plugin>
```

不要修改根 POM；其他模块继续沿用现有跳过测试配置。

- [ ] **Step 2: 写参数校验失败测试**

```java
@RunWith(MockitoJUnitRunner.class)
public class FsCommonContractTest {

    private Validator validator;

    @Before
    public void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void longStatusRequiresIdAndStatus() {
        FsLongStatusParam param = new FsLongStatusParam();
        assertEquals(2, validator.validate(param).size());
    }

    @Test
    public void stringStatusRequiresIdAndStatus() {
        FsStringStatusParam param = new FsStringStatusParam();
        assertEquals(2, validator.validate(param).size());
    }

    @Test
    public void optionCarriesStableDisplayFields() {
        FsOptionVo option = FsOptionVo.builder().id("1").name("default").code("D1").status(1).build();
        assertEquals("1", option.getId());
        assertEquals("default", option.getName());
        assertEquals("D1", option.getCode());
        assertEquals(Integer.valueOf(1), option.getStatus());
    }

    @Test
    public void publisherDeduplicatesCompanyIds() {
        ApplicationEventPublisher applicationEventPublisher = mock(ApplicationEventPublisher.class);
        FsRuntimeConfigRefreshPublisher publisher = new FsRuntimeConfigRefreshPublisher(applicationEventPublisher);
        publisher.publishCompaniesChanged(Arrays.asList(1L, 1L, null, 2L));
        verify(applicationEventPublisher, times(2)).publishEvent(any(FsCompanyConfigChangedEvent.class));
    }
}
```

- [ ] **Step 3: 运行测试确认 RED**

Run:

```powershell
mvn -pl spring-boot-business/spring-boot-fs -am -Dtest=FsCommonContractTest -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: 编译失败，提示公共契约类或运行时刷新发布类不存在。

- [ ] **Step 4: 实现公共契约**

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FsLongStatusParam {
    @NotNull(message = "编号不能为空")
    private Long id;
    @NotNull(message = "状态不能为空")
    private Integer status;
}
```

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FsStringStatusParam {
    @NotBlank(message = "编号不能为空")
    private String id;
    @NotNull(message = "状态不能为空")
    private Integer status;
}
```

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FsOptionVo {
    private String id;
    private String name;
    private String code;
    private Integer status;
}
```

```java
@Getter
public class FsCompanyConfigChangedEvent {
    private final Long companyId;
    public FsCompanyConfigChangedEvent(Long companyId) { this.companyId = companyId; }
}
```

```java
@Component
@RequiredArgsConstructor
public class FsRuntimeConfigRefreshPublisher {
    private final ApplicationEventPublisher publisher;
    public void publishCompanyChanged(Long companyId) {
        if (companyId != null) {
            publisher.publishEvent(new FsCompanyConfigChangedEvent(companyId));
        }
    }
    public void publishCompaniesChanged(Collection<Long> companyIds) {
        if (companyIds != null) {
            companyIds.stream().filter(Objects::nonNull).distinct().forEach(this::publishCompanyChanged);
        }
    }
}
```

- [ ] **Step 5: 运行测试确认 GREEN**

Run the command from Step 3. Expected: `FsCommonContractTest` 4 tests pass.

### Task 2: FreeSWITCH 平台 DTO 与转换器

**Files:**
- Create: `spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/param/fs/system/PlatformPageParam.java`
- Create: `spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/param/fs/system/PlatformSaveParam.java`
- Create: `spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/vo/fs/system/PlatformDetailVo.java`
- Create: `spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/convert/manage/system/PlatformManageConvert.java`
- Test: `spring-boot-business/spring-boot-fs/src/test/java/cn/com/tzy/springbootfs/manage/system/PlatformManageConvertTest.java`

- [ ] **Step 1: 写转换器失败测试**

```java
@Test
public void convertsEditableFieldsWithoutRuntimeStatus() {
    PlatformSaveParam param = PlatformSaveParam.builder()
            .id(1L)
            .name("fs-main")
            .localIp("10.0.0.10")
            .remoteIp("1.1.1.1")
            .internalPort(5060)
            .externalPort(5080)
            .startRtpPort(16000)
            .endRtpPort(17000)
            .enable(1)
            .build();

    Platform entity = PlatformManageConvert.INSTANCE.convert(param);

    assertEquals(Long.valueOf(1L), entity.getId());
    assertEquals("fs-main", entity.getName());
    assertEquals(Integer.valueOf(16000), entity.getStartRtpPort());
    assertNull(entity.getStatus());
}
```

- [ ] **Step 2: 运行测试确认 RED**

```powershell
mvn -pl spring-boot-business/spring-boot-fs -am -Dtest=PlatformManageConvertTest -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: 编译失败，提示平台 Param、VO 或转换器不存在。

- [ ] **Step 3: 实现 DTO 字段**

`PlatformPageParam extends PageModel`，新增 `enable`、`status`。

`PlatformSaveParam` 使用 Lombok `@Data @Builder @NoArgsConstructor @AllArgsConstructor`，字段固定为：

```text
id, localIp, remoteIp, internalPort, externalPort, startRtpPort, endRtpPort,
wsPort, wssPort, audioCode, videoCode, frameRate, bitRate, iceStart,
stunAddress, name, enable, audioRecord, videoRecord, audioRecordPath,
videoRecordPath, soundRilePath, freeswitchPath, freeswitchLogPath
```

校验：`name/localIp` 非空；所有端口范围 `1..65535`；RTP 起止端口非空；`enable/audioRecord/videoRecord/iceStart` 取值 `0..1`。

`PlatformDetailVo` 返回 `Platform` 全部业务字段，包括只读 `status`，不返回审计字段。

- [ ] **Step 4: 实现 MapStruct 转换器**

```java
@Mapper
public interface PlatformManageConvert {
    PlatformManageConvert INSTANCE = Mappers.getMapper(PlatformManageConvert.class);
    Platform convert(PlatformSaveParam param);
    PlatformDetailVo convert(Platform entity);
    List<FsOptionVo> convertOptions(List<Platform> entities);
}
```

为 `convertOptions` 增加 default 映射方法，把 Long ID 转为 String，`code` 使用 `localIp`。

- [ ] **Step 5: 运行测试确认 GREEN**

Run the command from Step 2. Expected: test passes and MapStruct implementation is generated.

### Task 3: FreeSWITCH 平台管理 Service

**Files:**
- Create: `spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/service/manage/system/PlatformManageService.java`
- Create: `spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/service/manage/system/impl/PlatformManageServiceImpl.java`
- Test: `spring-boot-business/spring-boot-fs/src/test/java/cn/com/tzy/springbootfs/manage/system/PlatformManageServiceTest.java`

- [ ] **Step 1: 写 Service 失败测试**

覆盖以下行为：

```java
@Test public void saveRejectsReversedRtpRange();
@Test public void saveRejectsDuplicateName();
@Test public void updatePreservesRuntimeStatus();
@Test public void statusUpdatesEnableOnly();
@Test public void removeRejectsEnabledPlatform();
@Test public void detailReturnsNotFoundForUnknownId();
```

关键断言：

```java
RestResult<?> result = service.save(PlatformSaveParam.builder()
        .name("fs-main").localIp("10.0.0.10")
        .startRtpPort(17000).endRtpPort(16000).build());
assertEquals(RespCode.CODE_2.getValue(), result.getCode());
assertEquals("RTP起始端口不能大于结束端口", result.getMessage());
verify(platformService, never()).save(any(Platform.class));
```

- [ ] **Step 2: 运行测试确认 RED**

```powershell
mvn -pl spring-boot-business/spring-boot-fs -am -Dtest=PlatformManageServiceTest -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: 编译失败，提示 `PlatformManageService` 不存在。

- [ ] **Step 3: 实现接口签名**

```java
public interface PlatformManageService {
    PageResult page(PlatformPageParam param);
    RestResult<PlatformDetailVo> detail(Long id);
    RestResult<Long> save(PlatformSaveParam param);
    RestResult<?> status(FsLongStatusParam param);
    RestResult<?> remove(Long id);
    RestResult<List<FsOptionVo>> select(String keyword, Integer limit);
}
```

- [ ] **Step 4: 实现最小业务逻辑**

- 分页使用 `MyBatisUtils.buildPage` 和 `PlatformMapper.selectPage`。
- `query` 匹配名称、本地 IP、外网 IP。
- 新增和修改均校验名称重复；修改先查询原记录。
- 保存 Param 不允许覆盖数据库中的 `status`。
- `status` 只更新 `enable`，值仅允许 0、1。
- `remove` 只允许删除 `enable != 1` 的记录。
- 成功使用 `RespCode.CODE_0`，业务拒绝使用 `RespCode.CODE_2`。

- [ ] **Step 5: 运行测试确认 GREEN**

Run the command from Step 2. Expected: all six tests pass.

### Task 4: FreeSWITCH 平台 Controller 契约

**Files:**
- Create: `spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/controller/api/fs/manage/system/PlatformManageController.java`
- Test: `spring-boot-business/spring-boot-fs/src/test/java/cn/com/tzy/springbootfs/manage/system/PlatformManageControllerTest.java`

- [ ] **Step 1: 写 MockMvc 失败测试**

```java
@Before
public void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(new PlatformManageController(platformManageService)).build();
}

@Test
public void pageUsesPostJsonContract() throws Exception {
    when(platformManageService.page(any(PlatformPageParam.class))).thenReturn(PageResult.SUCCESS);
    mockMvc.perform(post("/api/fs/platform/page")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"pageNumber\":1,\"pageSize\":10}"))
            .andExpect(status().isOk());
}

@Test
public void saveRejectsMissingRequiredFields() throws Exception {
    mockMvc.perform(post("/api/fs/platform/save")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
            .andExpect(status().isBadRequest());
}
```

- [ ] **Step 2: 运行测试确认 RED**

```powershell
mvn -pl spring-boot-business/spring-boot-fs -am -Dtest=PlatformManageControllerTest -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: 编译失败，提示 Controller 不存在。

- [ ] **Step 3: 实现 Controller**

```java
@RestController("ApiFsPlatformManageController")
@RequestMapping("/api/fs/platform")
public class PlatformManageController extends ApiController {
    private final PlatformManageService platformManageService;

    public PlatformManageController(PlatformManageService platformManageService) {
        this.platformManageService = platformManageService;
    }

    @PostMapping("page") public PageResult page(@Validated @RequestBody PlatformPageParam param) { return platformManageService.page(param); }
    @GetMapping("detail") public RestResult<PlatformDetailVo> detail(@RequestParam Long id) { return platformManageService.detail(id); }
    @PostMapping("save") public RestResult<Long> save(@Validated @RequestBody PlatformSaveParam param) { return platformManageService.save(param); }
    @PostMapping("status") public RestResult<?> status(@Validated @RequestBody FsLongStatusParam param) { return platformManageService.status(param); }
    @DeleteMapping("remove") public RestResult<?> remove(@RequestParam Long id) { return platformManageService.remove(id); }
    @GetMapping("select") public RestResult<List<FsOptionVo>> select(@RequestParam(required = false) String keyword, @RequestParam(defaultValue = "20") Integer limit) { return platformManageService.select(keyword, limit); }
}
```

- [ ] **Step 4: 运行测试确认 GREEN**

Run the command from Step 2. Expected: controller tests pass.

### Task 5: 流媒体服务管理接口

**Files:**
- Create: `spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/param/fs/system/MediaServerPageParam.java`
- Create: `spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/param/fs/system/MediaServerSaveParam.java`
- Create: `spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/vo/fs/system/MediaServerDetailVo.java`
- Create: `spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/convert/manage/system/MediaServerManageConvert.java`
- Create: `spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/service/manage/system/MediaServerManageService.java`
- Create: `spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/service/manage/system/impl/MediaServerManageServiceImpl.java`
- Create: `spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/controller/api/fs/manage/system/MediaServerManageController.java`
- Test: `spring-boot-business/spring-boot-fs/src/test/java/cn/com/tzy/springbootfs/manage/system/MediaServerManageServiceTest.java`
- Test: `spring-boot-business/spring-boot-fs/src/test/java/cn/com/tzy/springbootfs/manage/system/MediaServerManageControllerTest.java`

- [ ] **Step 1: 写 Service 失败测试**

测试方法固定为：

```java
@Test public void saveRejectsDuplicateIpAndHttpPort();
@Test public void saveDoesNotAcceptRuntimeHeartbeatFields();
@Test public void setDefaultClearsPreviousDefaultServer();
@Test public void removeRejectsOnlineServer();
@Test public void statusUpdatesEnableWithoutChangingOnlineStatus();
@Test public void selectReturnsEnabledServersOnly();
```

`setDefaultClearsPreviousDefaultServer` 验证同一事务先执行全表 `defaultServer=0`，再把目标记录更新为 1。

- [ ] **Step 2: 运行测试确认 RED**

```powershell
mvn -pl spring-boot-business/spring-boot-fs -am -Dtest=MediaServerManageServiceTest -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: 编译失败，提示管理 Service 和 DTO 不存在。

- [ ] **Step 3: 实现 DTO 和转换器**

`MediaServerPageParam extends PageModel`，字段：`enable`、`status`、`defaultServer`。

`MediaServerSaveParam` 字段：

```text
id, ip, sslStatus, hookIp, sdpIp, streamIp, httpPort, httpSslPort,
rtmpPort, rtmpSslPort, rtpProxyPort, rtspPort, rtspSslPort, autoConfig,
secret, rtpEnable, enable, rtpPortRange, recordAssistPort, defaultServer,
hookAliveInterval, videoPlayPrefix, videoHttpPrefix
```

不接收 `keepaliveTime` 和 `status`。IP、Hook 地址、SDP IP 必填；端口范围 `0..65535`；布尔值限制 `0..1`。

`MediaServerDetailVo` 在保存字段基础上增加只读 `keepaliveTime`、`status`。

- [ ] **Step 4: 实现 Service 接口**

```java
public interface MediaServerManageService {
    PageResult page(MediaServerPageParam param);
    RestResult<MediaServerDetailVo> detail(String id);
    RestResult<String> save(MediaServerSaveParam param);
    RestResult<?> status(FsStringStatusParam param);
    RestResult<?> setDefault(String id);
    RestResult<?> remove(String id);
    RestResult<List<FsOptionVo>> select(String keyword, Integer limit);
}
```

新增 ID 使用 `RandomUtil.randomNumbers(19)`；默认节点切换使用 `@Transactional(rollbackFor = Exception.class)`；在线 `status=1` 时禁止删除；Secret 在详情中脱敏，修改时空值保持原值。

- [ ] **Step 5: 写 Controller 失败测试**

`MediaServerManageControllerTest` 验证 `page` 使用 POST JSON、`detail` 使用 GET、`default` 使用 POST、`remove` 使用 DELETE，以及空 IP 保存请求返回 HTTP 400。

- [ ] **Step 6: 运行 Controller 测试确认 RED**

```powershell
mvn -pl spring-boot-business/spring-boot-fs -am -Dtest=MediaServerManageControllerTest -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: 编译失败，提示 `MediaServerManageController` 不存在。

- [ ] **Step 7: 实现 Controller 路径**

```text
POST   /api/fs/media_server/page
GET    /api/fs/media_server/detail
POST   /api/fs/media_server/save
POST   /api/fs/media_server/status
POST   /api/fs/media_server/default
DELETE /api/fs/media_server/remove
GET    /api/fs/media_server/select
```

- [ ] **Step 8: 运行 Service 和 Controller 测试确认 GREEN**

```powershell
mvn -pl spring-boot-business/spring-boot-fs -am -Dtest=MediaServerManageServiceTest,MediaServerManageControllerTest -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: both test classes pass.

### Task 6: 两类网关管理接口

**Files:**
- Create: `spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/param/fs/system/FsGatewayPageParam.java`
- Create: `spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/param/fs/system/FsGatewaySaveParam.java`
- Create: `spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/param/fs/system/FsGatewaySelectedParam.java`
- Create: `spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/param/fs/system/RouteGatewayPageParam.java`
- Create: `spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/param/fs/system/RouteGatewaySaveParam.java`
- Create: `spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/vo/fs/system/FsGatewayDetailVo.java`
- Create: `spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/vo/fs/system/RouteGatewayDetailVo.java`
- Create: `spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/service/manage/system/FsGatewayManageService.java`
- Create: `spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/service/manage/system/RouteGatewayManageService.java`
- Create: `spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/service/manage/system/impl/FsGatewayManageServiceImpl.java`
- Create: `spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/service/manage/system/impl/RouteGatewayManageServiceImpl.java`
- Create: `spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/controller/api/fs/manage/system/FsGatewayManageController.java`
- Create: `spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/controller/api/fs/manage/system/RouteGatewayManageController.java`
- Test: `spring-boot-business/spring-boot-fs/src/test/java/cn/com/tzy/springbootfs/manage/system/FsGatewayManageServiceTest.java`
- Test: `spring-boot-business/spring-boot-fs/src/test/java/cn/com/tzy/springbootfs/manage/system/RouteGatewayManageServiceTest.java`
- Test: `spring-boot-business/spring-boot-fs/src/test/java/cn/com/tzy/springbootfs/manage/system/FsGatewayManageControllerTest.java`
- Test: `spring-boot-business/spring-boot-fs/src/test/java/cn/com/tzy/springbootfs/manage/system/RouteGatewayManageControllerTest.java`

- [ ] **Step 1: 写网关失败测试**

```java
@Test public void fsGatewaySelectedUsesExistingSelectedField();
@Test public void fsGatewayPasswordIsNotReturned();
@Test public void routeGatewayRejectsDuplicateName();
@Test public void routeGatewayRemoveRejectsRouteGroupReference();
@Test public void routeGatewayStatusOnlyAcceptsZeroOrOne();
@Test public void routeGatewayChangePublishesAffectedCompanies();
```

- [ ] **Step 2: 运行测试确认 RED**

```powershell
mvn -pl spring-boot-business/spring-boot-fs -am -Dtest=FsGatewayManageServiceTest,RouteGatewayManageServiceTest -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: 编译失败，提示网关管理类不存在。

- [ ] **Step 3: 实现 FS 注册网关契约**

`FsGatewaySaveParam` 字段：`id,name,routeId,realm,register,transport,retrySeconds,username,password,selected`。保持实体当前 `username` 的 Integer 类型，不在本阶段修改数据库结构。

`FsGatewaySelectedParam` 字段为 `@NotNull Long id` 和 `@Pattern(regexp = "[01]") String selected`。

Service 签名：

```java
PageResult page(FsGatewayPageParam param);
RestResult<FsGatewayDetailVo> detail(Long id);
RestResult<Long> save(FsGatewaySaveParam param);
RestResult<?> selected(FsGatewaySelectedParam param);
RestResult<?> remove(Long id);
RestResult<List<FsOptionVo>> select(String keyword, Integer limit);
```

详情密码字段固定返回 null；修改时空密码保持数据库原值；`selected` 只允许字符串 `"0"`、`"1"`。

- [ ] **Step 4: 实现路由网关契约**

`RouteGatewaySaveParam` 字段：`id,name,mediaHost,mediaPort,callerPrefix,calledPrefix,profile,sipHeader1,sipHeader2,sipHeader3,status`。

删除前使用 `RouteGatewayGroupService.count` 检查 `gatewayId` 引用。

保存、状态变更后，通过 `fs_route_gateway_group -> fs_route_call` 查询受影响的企业 ID，并调用 `refreshPublisher.publishCompaniesChanged(companyIds)`。删除仍在存在路由组引用时拒绝，因此不产生缓存刷新。

Controller 路径分别为 `/api/fs/fs_gateway` 和 `/api/fs/route_gateway`；通用动作使用 `page/detail/save/remove/select`，FS 注册网关额外使用 `POST /selected`，路由网关使用 `POST /status`。

- [ ] **Step 5: 写 Controller 失败测试**

两个 Controller 测试分别验证 `page/detail/save/remove/select`；FS 注册网关验证 `POST /selected` 接收 JSON，路由网关验证 `POST /status` 接收 JSON；缺少必填字段返回 HTTP 400。

- [ ] **Step 6: 运行 Controller 测试确认 RED**

```powershell
mvn -pl spring-boot-business/spring-boot-fs -am -Dtest=FsGatewayManageControllerTest,RouteGatewayManageControllerTest -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: 编译失败，提示两个 Controller 不存在。

- [ ] **Step 7: 实现两个 Controller**

Controller 构造器注入对应 ManageService，不直接注入 Mapper 或基础 Service。

- [ ] **Step 8: 运行测试确认 GREEN**

```powershell
mvn -pl spring-boot-business/spring-boot-fs -am -Dtest=FsGatewayManageServiceTest,RouteGatewayManageServiceTest,FsGatewayManageControllerTest,RouteGatewayManageControllerTest -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: all gateway service and controller tests pass.

### Task 7: 路由组聚合管理接口

**Files:**
- Create: `spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/param/fs/system/RouteGroupPageParam.java`
- Create: `spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/param/fs/system/RouteGroupSaveParam.java`
- Create: `spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/param/fs/system/RouteGroupGatewaySaveParam.java`
- Create: `spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/vo/fs/system/RouteGroupDetailVo.java`
- Create: `spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/service/manage/system/RouteGroupManageService.java`
- Create: `spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/service/manage/system/impl/RouteGroupManageServiceImpl.java`
- Create: `spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/controller/api/fs/manage/system/RouteGroupManageController.java`
- Test: `spring-boot-business/spring-boot-fs/src/test/java/cn/com/tzy/springbootfs/manage/system/RouteGroupManageServiceTest.java`
- Test: `spring-boot-business/spring-boot-fs/src/test/java/cn/com/tzy/springbootfs/manage/system/RouteGroupManageControllerTest.java`

- [ ] **Step 1: 写关系替换失败测试**

```java
@Test public void saveGatewaysRejectsUnknownGateway();
@Test public void saveGatewaysReplacesAllRelationsInOneTransaction();
@Test public void removeRejectsRouteCallReference();
@Test public void detailReturnsAssignedGatewayOptions();
@Test public void routeGroupChangePublishesAffectedCompanies();
```

关系替换测试验证：先按 `routeGroupId` 删除旧关系，再为去重后的 gateway ID 逐条构建 `RouteGatewayGroup` 并调用 `saveBatch`。

- [ ] **Step 2: 运行测试确认 RED**

```powershell
mvn -pl spring-boot-business/spring-boot-fs -am -Dtest=RouteGroupManageServiceTest -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: 编译失败，提示路由组管理类不存在。

- [ ] **Step 3: 实现聚合接口**

```java
public interface RouteGroupManageService {
    PageResult page(RouteGroupPageParam param);
    RestResult<RouteGroupDetailVo> detail(Long id);
    RestResult<Long> save(RouteGroupSaveParam param);
    RestResult<?> saveGateways(RouteGroupGatewaySaveParam param);
    RestResult<?> status(FsLongStatusParam param);
    RestResult<?> remove(Long id);
    RestResult<List<FsOptionVo>> select(String keyword, Integer limit);
}
```

`RouteGroupGatewaySaveParam` 字段为 `@NotNull Long routeGroupId` 和 `@NotNull List<Long> gatewayIds`。所有网关必须存在且状态可用。

保存基础信息、状态和网关关系后，查询引用当前路由组的 `RouteCall.companyId`，去重后调用 `refreshPublisher.publishCompaniesChanged(companyIds)`。删除在存在 `RouteCall` 引用时拒绝。

- [ ] **Step 4: 写 Controller 失败测试**

验证 `page/detail/save/save_gateways/status/remove/select` 的 HTTP 方法，`save_gateways` 缺少 `routeGroupId` 时返回 HTTP 400。

- [ ] **Step 5: 运行 Controller 测试确认 RED**

```powershell
mvn -pl spring-boot-business/spring-boot-fs -am -Dtest=RouteGroupManageControllerTest -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: 编译失败，提示 `RouteGroupManageController` 不存在。

- [ ] **Step 6: 实现 Controller 路径**

```text
POST   /api/fs/route_group/page
GET    /api/fs/route_group/detail
POST   /api/fs/route_group/save
POST   /api/fs/route_group/save_gateways
POST   /api/fs/route_group/status
DELETE /api/fs/route_group/remove
GET    /api/fs/route_group/select
```

- [ ] **Step 7: 运行测试确认 GREEN**

```powershell
mvn -pl spring-boot-business/spring-boot-fs -am -Dtest=RouteGroupManageServiceTest,RouteGroupManageControllerTest -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: service and controller tests pass.

### Task 8: 企业运行时配置定向刷新

**Files:**
- Create: `spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/config/fs/runtime/FsRuntimeConfigRefreshListener.java`
- Create: `spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/config/fs/runtime/FsRuntimeConfigService.java`
- Create: `spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/config/fs/runtime/impl/FsRuntimeConfigServiceImpl.java`
- Modify: `spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/config/fs/CompanyRunner.java`
- Test: `spring-boot-business/spring-boot-fs/src/test/java/cn/com/tzy/springbootfs/manage/system/FsRuntimeConfigRefreshTest.java`

- [ ] **Step 1: 写事务事件失败测试**

```java
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = FsRuntimeConfigRefreshTest.Config.class)
public class FsRuntimeConfigRefreshTest {

    @Autowired private TestTransactionService transactionService;
    @Autowired private FsRuntimeConfigService runtimeConfigService;

    @Test
    public void refreshRunsAfterCommit() {
        transactionService.publishAndCommit(7L);
        verify(runtimeConfigService).refreshCompany(7L);
    }

    @Test
    public void refreshDoesNotRunAfterRollback() {
        transactionService.publishAndRollback(7L);
        verify(runtimeConfigService, never()).refreshCompany(anyLong());
    }
}
```

测试配置使用测试内定义的 `TestPlatformTransactionManager extends AbstractPlatformTransactionManager`、`@EnableTransactionManagement`、Mockito `FsRuntimeConfigService` Bean 和真实事件 Publisher/Listener。`doBegin`、`doCommit`、`doRollback` 只记录事务状态，不访问数据库。

- [ ] **Step 2: 运行测试确认 RED**

```powershell
mvn -pl spring-boot-business/spring-boot-fs -am -Dtest=FsRuntimeConfigRefreshTest -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: 编译失败，提示运行时刷新组件不存在。

- [ ] **Step 3: 实现事务监听器**

```java
@Component
@RequiredArgsConstructor
public class FsRuntimeConfigRefreshListener {
    private final FsRuntimeConfigService runtimeConfigService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCompanyChanged(FsCompanyConfigChangedEvent event) {
        runtimeConfigService.refreshCompany(event.getCompanyId());
    }
}
```

- [ ] **Step 4: 提取 CompanyRunner 装载逻辑**

接口：

```java
public interface FsRuntimeConfigService {
    void refreshAll();
    void refreshCompany(Long companyId);
}
```

实现规则：

- 把 `CompanyRunner` 中企业聚合查询与组装逻辑迁移到 `FsRuntimeConfigServiceImpl`，内部方法接受企业 ID 集合。
- `refreshAll()` 保留启动时清理全部 Manager 后重建全部企业的行为。
- `refreshCompany(companyId)` 查询单个有效企业并重新组装完整 `CompanyInfo`，使用 `CompanyInfoManager.put` 覆盖同一企业缓存；本阶段的路由变更不会删除 VDN、Playback、Conference 或 Group 子资源，因此不改动这些 Manager。
- 企业被禁用或不存在时，`refreshCompany` 调用现有 `CompanyInfoManager.del(String.valueOf(companyId))`。
- `CompanyRunner.run` 只调用 `runtimeConfigService.refreshAll()`。
- 日常保存路径不得调用任何 Manager 的 `delAll()`。

- [ ] **Step 5: 运行测试确认 GREEN**

Run the command from Step 2. Expected: commit case invokes once; rollback case invokes zero times.

### Task 9: 外呼路由管理接口

**Files:**
- Create: `spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/param/fs/system/RouteCallPageParam.java`
- Create: `spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/param/fs/system/RouteCallSaveParam.java`
- Create: `spring-boot-common/spring-boot-entity/src/main/java/cn/com/tzy/springbootentity/vo/fs/system/RouteCallDetailVo.java`
- Create: `spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/service/manage/system/RouteCallManageService.java`
- Create: `spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/service/manage/system/impl/RouteCallManageServiceImpl.java`
- Create: `spring-boot-business/spring-boot-fs/src/main/java/cn/com/tzy/springbootfs/controller/api/fs/manage/system/RouteCallManageController.java`
- Test: `spring-boot-business/spring-boot-fs/src/test/java/cn/com/tzy/springbootfs/manage/system/RouteCallManageServiceTest.java`
- Test: `spring-boot-business/spring-boot-fs/src/test/java/cn/com/tzy/springbootfs/manage/system/RouteCallManageControllerTest.java`

- [ ] **Step 1: 写 Service 失败测试**

```java
@Test public void saveRejectsDuplicateCompanyRouteNumber();
@Test public void saveRejectsMinGreaterThanMax();
@Test public void saveRejectsUnknownRouteGroup();
@Test public void savePublishesCompanyRefresh();
@Test public void statusPublishesCompanyRefresh();
@Test public void removePublishesCompanyRefresh();
```

刷新断言：

```java
verify(refreshPublisher).publishCompanyChanged(companyId);
```

- [ ] **Step 2: 运行测试确认 RED**

```powershell
mvn -pl spring-boot-business/spring-boot-fs -am -Dtest=RouteCallManageServiceTest -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: 编译失败，提示外呼路由管理类不存在。

- [ ] **Step 3: 实现 DTO**

`RouteCallPageParam extends PageModel` 字段：`companyId`、`routeGroupId`、`status`。

`RouteCallSaveParam` 字段：

```text
id, companyId, routeGroupId, routeNum, numMax, numMin,
callerChange, callerChangeNum, calledChange, calledChangeNum, status
```

校验：`companyId/routeGroupId/routeNum/numMax/numMin/status` 非空；`numMin/numMax >= 0`；状态和改写类型只接受定义值。

- [ ] **Step 4: 实现 Service**

```java
public interface RouteCallManageService {
    PageResult page(RouteCallPageParam param);
    RestResult<RouteCallDetailVo> detail(Long id);
    RestResult<Long> save(RouteCallSaveParam param);
    RestResult<?> status(FsLongStatusParam param);
    RestResult<?> remove(Long id);
}
```

保存、状态和删除使用 `@Transactional(rollbackFor = Exception.class)`；数据库操作成功后调用 `refreshPublisher.publishCompanyChanged(companyId)`，监听器在提交后刷新。

- [ ] **Step 5: 写 Controller 失败测试**

验证 `page/detail/save/status/remove` 的 HTTP 方法，缺少 `companyId` 或 `routeNum` 的保存请求返回 HTTP 400。

- [ ] **Step 6: 运行 Controller 测试确认 RED**

```powershell
mvn -pl spring-boot-business/spring-boot-fs -am -Dtest=RouteCallManageControllerTest -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: 编译失败，提示 `RouteCallManageController` 不存在。

- [ ] **Step 7: 实现 Controller**

```text
POST   /api/fs/route_call/page
GET    /api/fs/route_call/detail
POST   /api/fs/route_call/save
POST   /api/fs/route_call/status
DELETE /api/fs/route_call/remove
```

- [ ] **Step 8: 运行测试确认 GREEN**

```powershell
mvn -pl spring-boot-business/spring-boot-fs -am -Dtest=RouteCallManageServiceTest,RouteCallManageControllerTest -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: both test classes pass.

### Task 10: 六个页面 Controller 契约回归

**Files:**
- Test: `spring-boot-business/spring-boot-fs/src/test/java/cn/com/tzy/springbootfs/manage/system/FsSystemConfigControllerTest.java`

- [ ] **Step 1: 写统一路径测试**

使用 `MockMvcBuilders.standaloneSetup` 注册六个 Controller，逐个验证：

```text
/api/fs/platform
/api/fs/media_server
/api/fs/fs_gateway
/api/fs/route_gateway
/api/fs/route_group
/api/fs/route_call
```

每个资源至少验证 `page` 的 POST JSON 契约和 `detail` 的 GET 查询参数；验证 `remove` 使用 DELETE；验证非法保存 JSON 返回 HTTP 400。

- [ ] **Step 2: 运行跨 Controller 回归测试**

```powershell
mvn -pl spring-boot-business/spring-boot-fs -am -Dtest=FsSystemConfigControllerTest -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: 作为跨 Controller 回归测试；若失败，失败原因必须明确指向路径、HTTP 方法或校验契约不一致。

- [ ] **Step 3: 只修正 Controller 契约**

不在此步骤修改 Service 行为。统一：

- `page/save/status/default/selected/save_gateways` 使用 POST。
- `detail/select` 使用 GET。
- `remove` 使用 DELETE。
- JSON 请求使用 `@Validated @RequestBody`。
- 查询参数显式使用 `@RequestParam`。

- [ ] **Step 4: 运行测试确认 GREEN**

Run the command from Step 2. Expected: all path and validation assertions pass.

### Task 11: 第一阶段完整验证

**Files:**
- No new files; verify files changed by Tasks 1-10.

- [ ] **Step 1: 运行定向测试**

```powershell
mvn -pl spring-boot-business/spring-boot-fs -am -Dtest=FsCommonContractTest,PlatformManageConvertTest,PlatformManageServiceTest,PlatformManageControllerTest,MediaServerManageServiceTest,MediaServerManageControllerTest,FsGatewayManageServiceTest,RouteGatewayManageServiceTest,RouteGroupManageServiceTest,FsRuntimeConfigRefreshTest,RouteCallManageServiceTest,RouteCallManageControllerTest,FsSystemConfigControllerTest -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: 0 failures, 0 errors.

- [ ] **Step 2: 运行模块编译**

```powershell
mvn -pl spring-boot-business/spring-boot-fs -am -DskipTests compile
```

Expected: `BUILD SUCCESS`，MapStruct 生成类无错误。

- [ ] **Step 3: 检查设计覆盖**

逐项核对设计文档 `docs/superpowers/specs/2026-07-27-spring-boot-fs-management-api-design.md` 的 6.1 至 6.6，确认所有路径、删除保护、密码脱敏和缓存规则都有对应测试。

- [ ] **Step 4: 检查工作区**

```powershell
git status --short
git diff --check
```

Expected: 只有计划内文件发生变化，`git diff --check` 无空白错误。未经用户明确要求，不执行 `git commit`。
