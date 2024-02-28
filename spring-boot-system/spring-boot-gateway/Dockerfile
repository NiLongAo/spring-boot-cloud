FROM anapsix/alpine-java:8_server-jre_unlimited
MAINTAINER 18789432816@126.com
# 同步时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN mkdir -p /spring-boot-gateway/lib
RUN mkdir -p /spring-boot-gateway/skywalking-agent-lib
RUN mkdir -p /spring-boot-gateway/logs

WORKDIR /spring-boot-gateway
EXPOSE 9190
EXPOSE 13001
EXPOSE 14002
ADD ./target/spring-boot-gateway.jar ./
ADD ./target/lib/* ./lib/
ADD ./start/logback.xml ./
ADD ./target/tmp  ./skywalking-agent-lib/
# Djava.security.egd=file:/dev/./urandom 解决阿里云的tomcat启动慢的问题
ENTRYPOINT [ \
    "java", \
#    "-javaagent:/spring-boot-gateway/skywalking-agent-lib/skywalking-agent.jar", \
#    "-Dskywalking.agent.service_name=spring-boot-gateway", \
#    "-Dskywalking.collector.backend_service=1.15.9.228:12011", \
    "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=14002", \
    "-XX:MetaspaceSize=64m", "-XX:MaxMetaspaceSize=128m","-XX:+AlwaysPreTouch", "-Xms128m", "-Xmx256m", "-Xmn128m", "-Xss256k", "-XX:SurvivorRatio=8",\
    "-Dloader.path=/spring-boot-gateway/lib", \
    "-Dfile.encoding=utf-8", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar","/spring-boot-gateway/spring-boot-gateway.jar", \
    "--logging.config=/spring-boot-gateway/logback.xml" \
]
CMD sleep 10;