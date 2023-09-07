#!/bin/sh

#跳转到脚本目录
# 构建镜像

cd ${WORKSPACE}/spring-boot-client/spring-boot-web-api

docker build -t ccr.ccs.tencentyun.com/spring_boot_cloud/spring_cloud_web_api:latest .
# 将镜像推送到埃利园
docker push ccr.ccs.tencentyun.com/spring_boot_cloud/spring_cloud_web_api:latest

cd ${WORKSPACE}