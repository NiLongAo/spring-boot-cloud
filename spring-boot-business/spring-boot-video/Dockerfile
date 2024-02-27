FROM anapsix/alpine-java:8_server-jre_unlimited
MAINTAINER 18789432816@126.com
# 同步时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone &&\
    mkdir -p /spring-boot-video/lib &&\
    mkdir -p /spring-boot-video/skywalking-agent-lib &&\
    mkdir -p /spring-boot-video/logs

WORKDIR /spring-boot-video
EXPOSE 8660
EXPOSE 6081
EXPOSE 13010
EXPOSE 14004
ADD ./target/spring-boot-video.jar ./
ADD ./target/lib/* ./lib/
ADD ./start/logback.xml ./
# ADD ./target/tmp ./skywalking-agent-lib/
# Djava.security.egd=file:/dev/./urandom 解决阿里云的tomcat启动慢的问题
ENTRYPOINT [ \
    "java", \
#    "-javaagent:/spring-boot-video/skywalking-agent-lib/skywalking-agent.jar", \
#    "-Dskywalking.agent.service_name=spring-boot-video", \
#    "-Dskywalking.collector.backend_service=1.15.9.228:12011", \
    "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=14004", \
    "-XX:MetaspaceSize=64m", "-XX:MaxMetaspaceSize=128m", "-Xms128m", "-Xmx256m", "-Xmn128m", "-Xss256k", "-XX:SurvivorRatio=8",\
    "-Dloader.path=/spring-boot-video/lib", \
    "-Dfile.encoding=utf-8", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar","/spring-boot-video/spring-boot-video.jar", \
    "--logging.config=/spring-boot-video/logback.xml" \
]
CMD sleep 10;