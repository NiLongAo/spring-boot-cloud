FROM anapsix/alpine-java:8_server-jre_unlimited
MAINTAINER 18789432816@126.com
# 同步时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN mkdir -p /spring-boot-fs/lib
RUN mkdir -p /spring-boot-fs/skywalking-agent-lib
RUN mkdir -p /spring-boot-fs/logs

WORKDIR /spring-boot-fs
EXPOSE 8630
EXPOSE 13007
ADD ./target/spring-boot-fs.jar ./
ADD ./target/lib/* ./lib/
ADD ./start/logback.xml ./
ADD ./target/tmp ./skywalking-agent-lib/
# Djava.security.egd=file:/dev/./urandom 解决阿里云的tomcat启动慢的问题
ENTRYPOINT [ \
    "java", \
    "-javaagent:/spring-boot-fs/skywalking-agent-lib/skywalking-agent.jar", \
    "-Dskywalking.agent.service_name=spring-boot-fs", \
    "-Dskywalking.collector.backend_service=1.15.9.228:12011", \
    "-XX:MetaspaceSize=256M", "-XX:MaxMetaspaceSize=256M","-XX:+AlwaysPreTouch","-XX:ReservedCodeCacheSize=128m", "-Xms256m", "-Xmx256m", "-Xss512k", "-XX:+UseG1GC","-XX:G1HeapRegionSize=4M",\
    "-Dloader.path=/spring-boot-fs/lib", \
    "-Dfile.encoding=utf-8", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar","/spring-boot-fs/spring-boot-fs.jar", \
    "--logging.config=/spring-boot-fs/logback.xml" \
]
CMD sleep 10;