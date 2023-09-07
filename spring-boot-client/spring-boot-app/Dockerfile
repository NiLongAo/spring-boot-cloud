FROM anapsix/alpine-java:8_server-jre_unlimited
MAINTAINER 18789432816@126.com
# 同步时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN mkdir -p /spring-boot-app/lib
RUN mkdir -p /spring-boot-app/skywalking-agent-lib
RUN mkdir -p /spring-boot-app/logs

WORKDIR /spring-boot-app
EXPOSE 9540
EXPOSE 13009
ADD ./target/spring-boot-app.jar ./
ADD ./target/lib/* ./lib/
ADD ./start/logback.xml ./
ADD ./target/tmp  ./skywalking-agent-lib/
# Djava.security.egd=file:/dev/./urandom 解决阿里云的tomcat启动慢的问题
ENTRYPOINT [ \
    "java", \
#    "-javaagent:/spring-boot-app/skywalking-agent-lib/skywalking-agent.jar", \
#    "-Dskywalking.agent.service_name=spring-boot-app", \
#    "-Dskywalking.collector.backend_service=1.15.9.228:12011", \
    "-Xmx256m","-Xms256m", \
    "-Dloader.path=/spring-boot-app/lib", \
    "-Dfile.encoding=utf-8", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-XX:+UnlockExperimentalVMOptions", \
    "-XX:+UseCGroupMemoryLimitForHeap", \
    "-jar","/spring-boot-app/spring-boot-app.jar", \
    "--logging.config=/spring-boot-app/logback.xml" \
]
CMD sleep 10;