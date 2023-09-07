#!/bin/sh

echo "更新最新的镜像:"
docker-compose -f '/usr/local/program/spring-cloud/docker/docker-compose.yml' build spring-boot-bean
echo "docker compose 配置路径:"$DOCKER_COMPOSE_FILE
docker-compose -f '/usr/local/program/spring-cloud/docker/docker-compose.yml' up -d
