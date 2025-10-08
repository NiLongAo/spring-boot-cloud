# Spring Boot Cloud 项目 Cursor Rules

本目录包含了 Spring Boot Cloud 微服务项目的开发规范和最佳实践。

## 规则文件列表

### 1. [01-project-structure.mdc](mdc:01-project-structure.mdc)
**项目结构和架构总览**
- 项目技术栈说明
- 核心模块架构
- 模块依赖关系
- 开发原则

**应用范围**: 所有文件（alwaysApply: true）

### 2. [02-java-coding-standards.mdc](mdc:02-java-coding-standards.mdc)
**Java编码规范**
- Lombok使用规范
- 命名规范
- 注解使用规范
- MyBatis-Plus规范
- 异常处理规范
- 参数校验规范
- 日志规范
- 事务管理规范

**应用范围**: 所有 `.java` 文件

### 3. [03-microservice-development.mdc](mdc:03-microservice-development.mdc)
**Spring Cloud微服务开发规范**
- 服务配置规范
- Feign客户端开发
- Sentinel熔断限流
- Gateway网关配置
- Redis缓存使用
- RabbitMQ消息队列
- Seata分布式事务
- XXL-Job分布式任务

**应用范围**: 手动应用

### 4. [04-maven-dependency.mdc](mdc:04-maven-dependency.mdc)
**Maven依赖管理和构建规范**
- 版本管理
- 依赖声明规范
- 模块依赖规范
- 插件配置
- 构建命令

**应用范围**: 所有 `pom.xml` 文件

### 5. [05-database-mybatis.mdc](mdc:05-database-mybatis.mdc)
**数据库设计和MyBatis-Plus开发规范**
- 数据库设计规范
- 表设计规范
- MyBatis-Plus实体类规范
- Mapper开发规范
- QueryWrapper使用
- 分页查询
- 多租户配置

**应用范围**: 手动应用

### 6. [06-docker-deployment.mdc](mdc:06-docker-deployment.mdc)
**Docker容器化部署规范**
- Dockerfile编写规范
- Docker Compose编排
- 镜像构建脚本
- 启动脚本规范
- 环境变量规范
- 日志卷挂载
- 健康检查

**应用范围**: `Dockerfile`, `docker-*.yml`, `*.sh` 文件

### 7. [07-api-swagger.mdc](mdc:07-api-swagger.mdc)
**RESTful API设计和Swagger文档规范**
- RESTful API设计规范
- HTTP方法规范
- 请求参数规范
- 响应结构规范
- Swagger注解使用
- 接口版本控制
- 跨域配置

**应用范围**: 手动应用

### 8. [08-security-oauth.mdc](mdc:08-security-oauth.mdc)
**Spring Security + OAuth2 安全认证授权规范**
- OAuth2授权模式
- 认证授权服务配置
- JWT Token配置
- 资源服务器配置
- Gateway网关鉴权
- 权限注解使用
- 获取当前用户信息
- 密码加密

**应用范围**: 手动应用

### 9. [09-code-quality.mdc](mdc:09-code-quality.mdc)
**代码质量和开发最佳实践**
- 代码整洁原则
- 异常处理最佳实践
- 对象转换最佳实践
- 数据校验最佳实践
- 缓存使用最佳实践
- 异步处理最佳实践
- 日志记录最佳实践
- 性能优化建议
- 代码审查检查清单

**应用范围**: 手动应用

## 如何使用这些规则

### 自动应用的规则
以下规则会自动应用到相应的文件类型：
- **项目结构规范**: 所有文件
- **Java编码规范**: `.java` 文件
- **Maven规范**: `pom.xml` 文件
- **Docker规范**: `Dockerfile`, `docker-*.yml`, `*.sh` 文件

### 手动应用的规则
其他规则需要在需要时手动引用。在与AI对话时，可以：
1. 直接提及规则名称，如"参考微服务开发规范"
2. AI会根据上下文自动加载相关规则

## 项目技术栈

- **Spring Boot**: 2.3.2.RELEASE
- **Spring Cloud**: Hoxton.SR9
- **Spring Cloud Alibaba**: 2.2.6.RELEASE
- **Java**: 1.8
- **数据库**: MySQL 8.0.16
- **ORM**: MyBatis-Plus 3.4.3
- **缓存**: Redis
- **消息队列**: RabbitMQ
- **服务注册/配置**: Nacos
- **网关**: Spring Cloud Gateway
- **熔断限流**: Sentinel
- **分布式事务**: Seata
- **任务调度**: XXL-Job
- **链路追踪**: SkyWalking
- **对象存储**: MinIO

## 快速开始

1. **阅读项目架构**: 从 `01-project-structure.mdc` 开始
2. **遵循编码规范**: 开发时参考 `02-java-coding-standards.mdc`
3. **参考具体技术规范**: 根据开发内容查阅相应规则文件
4. **代码质量检查**: 提交前参考 `09-code-quality.mdc` 中的检查清单

## 贡献指南

如果需要更新或新增规则：
1. 确保规则文件使用 `.mdc` 扩展名
2. 正确设置 frontmatter 元数据（alwaysApply/description/globs）
3. 使用 Markdown 格式编写
4. 使用 `[filename.ext](mdc:filename.ext)` 格式引用项目文件

## 相关链接

- [项目主README](mdc:../README.md)
- [项目主POM](mdc:../pom.xml)
- [Docker编排配置](mdc:../docker-compose.yml)

