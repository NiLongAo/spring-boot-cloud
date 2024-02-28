FROM anapsix/alpine-java:8_server-jre_unlimited
MAINTAINER 18789432816@126.com
# 同步时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN mkdir -p /spring-boot-sms/lib
RUN mkdir -p /spring-boot-sms/skywalking-agent-lib
RUN mkdir -p /spring-boot-sms/logs

WORKDIR /spring-boot-sms
EXPOSE 9120
EXPOSE 9121
EXPOSE 13005
EXPOSE 14003
ADD ./target/spring-boot-sms.jar ./
ADD ./target/lib/* ./lib/
ADD ./start/logback.xml ./
# ADD ./target/tmp ./skywalking-agent-lib/
# Djava.security.egd=file:/dev/./urandom 解决阿里云的tomcat启动慢的问题
ENTRYPOINT [ \
    "java", \
#    "-javaagent:/spring-boot-sms/skywalking-agent-lib/skywalking-agent.jar", \
#    "-Dskywalking.agent.service_name=spring-boot-sms", \
#    "-Dskywalking.collector.backend_service=1.15.9.228:12011", \
    "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=14003", \
    "-XX:MetaspaceSize=64m", "-XX:MaxMetaspaceSize=128m","-XX:+AlwaysPreTouch", "-Xms128m", "-Xmx256m", "-Xmn128m", "-Xss256k", "-XX:SurvivorRatio=8",\
    "-Dloader.path=/spring-boot-sms/lib", \
    "-Dfile.encoding=utf-8", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar","/spring-boot-sms/spring-boot-sms.jar", \
    "--logging.config=/spring-boot-sms/logback.xml" \
]
CMD sleep 10;