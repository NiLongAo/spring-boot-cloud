#!/bin/sh

#跳转到脚本目录
# 构建镜像

cd ${WORKSPACE}/spring-boot-system/spring-boot-gateway

docker build -t ccr.ccs.tencentyun.com/spring_boot_cloud/spring_cloud_gateway:latest .
# 将镜像推送到埃利园
docker push ccr.ccs.tencentyun.com/spring_boot_cloud/spring_cloud_gateway:latest

cd ${WORKSPACE}