<div align="center"> <a href="https://github.com/anncwb/vue-vben-admin"> <img alt="VbenAdmin Logo" width="200" height="200" src="https://anncwb.github.io/anncwb/images/logo.png"> </a> <br> <br>

[![license](https://img.shields.io/github/license/anncwb/vue-vben-admin.svg)](LICENSE)
<h1>逆龙傲</h1>
</div>


## 简介

逆龙傲 是一个免费丶面向开源 微服务系统架构，可用于架构搭建，学习参考。

## 特性
- **数据库**：mysql5.7
- **spring全家桶**：spring-boot:2.3.2.RELEASE  spring-cloud:Hoxton.SR9 
- **认证授权**：spring-security , oauth2 , jwt
- **统一网关丶鉴权**：spring-cloud-starter-gateway
- **熔断限流**：spring-cloud-starter-alibaba-sentinel
- **分布式事务**：seata AT模式
- **服务监控-日志**：skywalking
- **服务之间通讯**：spring-cloud-starter-openfeign
- **静态资源服务**：minio
- **系统缓存**：redis
- **注册中心、配置中心**：nacos
- **搜索引擎**：elasticsearch7
- **分布式定时器**：xxl-job , quartz
- **消息队列**：rabbitmq
- **工作流**：activiti7
- **人脸认证识别**：Seetaface6
- **发送短信**：目前只测腾讯云，还有其他等待测试
- **国标28181**：对GB28181 相关实现视频系统监控等功能
- **前后端通讯**：netty-socketio
- **在线接口文档**：swagger2

## 模块说明
~~~md
├─.build
│  ├─docker    docker打包脚本
│  └─jenkins   jenkins自动化部署脚本
├─.lib-service                                                         [ 1.此包文件过大，无法上传，已被移除，需要可自行下载，文件在最下方 ]
│  ├─allatori  代码混淆打包工具
│  ├─rabbitmq-plugins  mq创建docker脚本 及 延迟队列插件 3.9.0
│  └─skywalking-agent  微服务链路追踪 agent 相关jar
├─.run   idea 远程docker部署脚本
├─spring-boot-business  项目具体业务模块
│  ├─spring-boot-activiti  工作流底层模块
│  │  └─start liunx项目运行相关脚本
│  ├─spring-boot-bean 项目基础业务模块 如：用户丶权限丶部门丶菜单丶角色等相关逻辑
│  │  └─start liunx项目运行相关脚本
│  ├─spring-boot-face 人脸识别模块 人脸模块底层为 人脸识别算法SeetaFace6（主要是免费） 已有liunx版本与Windows版本 
│  │  ├─conf  人脸识别模块所需第三方包：核心                                 [ 2.此包文件过大，无法上传，已被移除，需要可自行下载，文件在最下方 ]
│  │  └─start liunx项目运行相关脚本
│  ├─spring-boot-oa 工作流业务模块 用于工作流实际业务模块 目前只有：请假流程。此处只给举例，其他业务自行开发！
│  │  └─start liunx项目运行相关脚本
│  └─spring-boot-sms 本应是短信模块，因服务不足模块合并，模块包含 ：短信发送模块丶netty-sockit模块丶job定时器模块丶redis订阅模块
│     └─start liunx项目运行相关脚本
│  └─spring-boot-video 国标28181模块,模块功能 ：分屏调度丶国标设备实时播放丶历史回放丶云台控制丶流媒体管理丶国标级联丶拉流管理丶推流管理等模块
│     └─start liunx项目运行相关脚本
├─spring-boot-client 相关服务接口提供模块
│  ├─spring-boot-app 小程序端接口模块
│  │  └─start ....
│  └─spring-boot-web-api web端接口模块
│     └─start ....
├─spring-boot-common 公共底层数据结构模块
│  ├─spring-boot-comm 所有服务底层公共模块
│  └─spring-boot-entity 业务底层公共模块  如：vo丶model丶param丶entity等相关实体
├─spring-boot-feign 各个服务相互调用 feign 服务
│  ├─spring-boot-feign-activiti 工作流底层对外开放相关接口
│  ├─spring-boot-feign-bean 基础业务对外开放相关接口
│  ├─spring-boot-feign-face 人脸识别模块对外开放相关接口
│  ├─spring-boot-feign-oa 工作流业务模块对外开放相关接口
│  ├─spring-boot-feign-sms 短信发送模块对外开放相关接口
│  └─spring-boot-feign-sso 统一认证鉴权对外开放相关接口
├─spring-boot-service 第三方模块服务 此模块只用于本地启动，也可自行用官方服务启动。
│  ├─spring-boot-service-nacos nacos服务
│  ├─spring-boot-service-seata 分布式事务服务
│  ├─spring-boot-service-sentinel 熔断限流服务
│  └─spring-boot-service-xxl-job xxl-job服务
├─spring-boot-starter 相关模块底层依赖
│  ├─spring-boot-starter-autopoi POI导入导出功能底层 使用 easypoi-spring-boot-starter 包并进行二次封装，有使用举例。
│  ├─spring-boot-starter-cloud 所有运行模块公共加载配置模块 如：controller公共继承类，请求路径丶请求参数打印功能。
│  ├─spring-boot-starter-elasticsearch 引擎搜索elasticsearch加载配置模块 使用 easy-es-boot-starter 包
│  ├─spring-boot-starter-feign feign相关加载配置模块
│  │  ├─spring-boot-starter-feign-config 服务feign调用异常拦截
│  │  └─spring-boot-starter-feign-core  feign的调用拦截器 主要在请求前存放相关认证 header
│  ├─spring-boot-starter-logs 日志封装相关加载配置模块
│  │  ├─spring-boot-starter-logs-basic 日志注解 及 枚举
│  │  └─spring-boot-starter-logs-core  日志核心实现逻辑，使用AOP切面
│  ├─spring-boot-starter-minio minio静态资源 加载配置模块
│  ├─spring-boot-starter-mybatis mybatis加载配置模块  主要 1.统一字段自动填充 比如：创建人，修改人   2.租户相关配置
│  ├─spring-boot-starter-nacos nacos加载配置模块 naocs 负载均衡配置   现在默认走 NacosRule naocs权重策略
│  ├─spring-boot-starter-netty  对netty进行二次业务封装，更加方便简便的使用。项目中有举例
│  ├─spring-boot-starter-quartz  定时器加载配置模块 -------已弃用  改用 xxl-job
│  ├─spring-boot-starter-rabbitmq 消息队列加载配置模块 对mq相关操作进行封装
│  ├─spring-boot-starter-redis redis缓存加载配置模块 1.redis操作工具类  2.发布订阅模块封装  3.redis整合spring-cache配置
│  ├─spring-boot-starter-security-oauth 认证授权加载配置模块
│  │  ├─spring-boot-starter-security-oauth-basic 认证授权底层封装基础类
│  │  └─spring-boot-starter-security-oauth-core  认证授权配置 主要分布 1.授权服务相关配置  2.整合gateway统一认证配置
│  ├─spring-boot-starter-sentinel 熔断限流加载配置模块
│  ├─spring-boot-starter-sms 短信模块加载配置模块
│  │  ├─spring-boot-starter-sms-basic 短信模块底层基础类
│  │  └─spring-boot-starter-sms-core  短信模块相关配置
│  ├─spring-boot-starter-socket-io netty-socket加载配置模块  主要对netty-socket进行二次封装根据特殊要求更简便配置调用
│  ├─spring-boot-starter-swagger  swagger2加载配置模块
│  ├─spring-boot-starter-video  对接GBT 28181-2016 公共安全视频监控联网系统 相关模块实现 借鉴wvp平台代码进行整理改编
│  │   ├─spring-boot-starter-video-basic 国标28181相应实体基础类
│  │   └─spring-boot-starter-video-core  国标28181底层通讯 实现进行封装 核心包
│  └─spring-boot-starter-xxl-job  xxl-job加载配置模块
├─spring-boot-system 
│  ├─spring-boot-gateway 服务统一网关丶鉴权模块
│  │  └─start  ....
│  ├─spring-boot-pay 支付模块  ----暂未开发
│  │  └─start  ....
│  └─spring-boot-sso 统一认证模块
│     └─start  ....
└─sql 项目基础模块sql


~~~

## 预览
- [ 在线地址 ](https://www.nilongao.cn) https://www.nilongao.cn
- 测试账号: nilongao/nilongao


<p align="center">
    <img alt="NiLongAo Logo" width="100%" src="https://NiLongAo.github.io/static/images/home.jpg">
    <img alt="NiLongAo Logo" width="100%" src="https://anncwb.github.io/anncwb/images/preview2.png">
    <img alt="NiLongAo Logo" width="100%" src="https://anncwb.github.io/anncwb/images/preview3.png">
</p>


## 前端整合示例
- [vue3-admin-cloud](https://github.com/NiLongAo/vue-admin-cloud) - 基于 SpringCloud Alibaba 的微服务中后台快速开发平台

## 备注
- 由于项目文件过大，将部分大文件剔除，有需要者，自行下载
- 剔除文件：模块中已说明
- [大文件下载地址 ](https://pan.xunlei.com/s/VNdi9s7-6nE1U-Ht5U_xgKyWA1)  提取码：rapu，解压码：spring-boot-cloud
## 交流
`逆龙傲` 是完全开源免费的项目，在帮助开发者更方便地进行中大型管理系统开发，同时也提供 QQ 交流群使用问题欢迎在群内提问。
- QQ 群 `715528092`


