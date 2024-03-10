FROM anapsix/alpine-java:8_server-jre_unlimited
MAINTAINER 18789432816@126.com
# 同步时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN mkdir -p /spring-boot-sso/lib
RUN mkdir -p /spring-boot-sso/skywalking-agent-lib
RUN mkdir -p /spring-boot-sso/logs

WORKDIR /spring-boot-sso
EXPOSE 9110
EXPOSE 13002
ADD ./target/spring-boot-sso.jar ./
ADD ./target/lib/* ./lib/
ADD ./start/logback.xml ./
 ADD ./target/tmp ./skywalking-agent-lib/
# Djava.security.egd=file:/dev/./urandom 解决阿里云的tomcat启动慢的问题
ENTRYPOINT [ \
    "java", \
    "-javaagent:/spring-boot-sso/skywalking-agent-lib/skywalking-agent.jar", \
    "-Dskywalking.agent.service_name=spring-boot-sso", \
    "-Dskywalking.collector.backend_service=1.15.9.228:12011", \
    "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=14005", \
    "-XX:MetaspaceSize=256M", "-XX:MaxMetaspaceSize=256M","-XX:+AlwaysPreTouch","-XX:ReservedCodeCacheSize=128m", "-Xms256m", "-Xmx256m", "-Xss512k", "-XX:+UseG1GC","-XX:G1HeapRegionSize=4M",\
    "-Dloader.path=/spring-boot-sso/lib", \
    "-Dfile.encoding=utf-8", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar","/spring-boot-sso/spring-boot-sso.jar", \
    "--logging.config=/spring-boot-sso/logback.xml" \
]
CMD sleep 10;