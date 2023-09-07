FROM anapsix/alpine-java:8_server-jre_unlimited
MAINTAINER 18789432816@126.com
# 同步时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN mkdir -p /spring-boot-web-api/lib
RUN mkdir -p /spring-boot-web-api/skywalking-agent-lib
RUN mkdir -p /spring-boot-web-api/logs

WORKDIR /spring-boot-web-api
EXPOSE 9530
EXPOSE 13004
EXPOSE 14001
ADD ./target/spring-boot-web-api.jar ./
ADD ./target/lib/* ./lib/
ADD ./start/logback.xml ./
ADD ./target/tmp  ./skywalking-agent-lib/
# Djava.security.egd=file:/dev/./urandom 解决阿里云的tomcat启动慢的问题
ENTRYPOINT [ \
    "java", \
#    "-javaagent:/spring-boot-web-api/skywalking-agent-lib/skywalking-agent.jar", \
#    "-Dskywalking.agent.service_name=spring-boot-web-api", \
#    "-Dskywalking.collector.backend_service=1.15.9.228:12011", \
    "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=14001", \
    "-Xmx512m","-Xms512m", \
    "-Dloader.path=/spring-boot-web-api/lib", \
    "-Dfile.encoding=utf-8", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-XX:+UnlockExperimentalVMOptions", \
    "-XX:+UseCGroupMemoryLimitForHeap", \
    "-jar","/spring-boot-web-api/spring-boot-web-api.jar", \
    "--logging.config=/spring-boot-web-api/logback.xml" \
]
CMD sleep 10;