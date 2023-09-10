FROM anapsix/alpine-java:8_server-jre_unlimited
# 同步时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN mkdir -p /spring-boot-activiti/lib
RUN mkdir -p /spring-boot-activiti/skywalking-agent-lib
RUN mkdir -p /spring-boot-activiti/logs

WORKDIR /spring-boot-activiti
EXPOSE 8620
EXPOSE 13006
EXPOSE 14006
ADD ./target/spring-boot-activiti.jar ./
ADD ./target/lib/* ./lib/
ADD ./start/logback.xml ./
# ADD ./target/tmp ./skywalking-agent-lib/
# Djava.security.egd=file:/dev/./urandom 解决阿里云的tomcat启动慢的问题
ENTRYPOINT [ \
    "java", \
#    "-javaagent:/spring-boot-activiti/skywalking-agent-lib/skywalking-agent.jar", \
#    "-Dskywalking.agent.service_name=spring-boot-activiti", \
#    "-Dskywalking.collector.backend_service=1.15.9.228:12011", \
    "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=14006", \
    "-Xmx256m","-Xms256m", \
    "-Dloader.path=/spring-boot-activiti/lib", \
    "-Dfile.encoding=utf-8", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-XX:+UnlockExperimentalVMOptions", \
    "-XX:+UseCGroupMemoryLimitForHeap", \
    "-jar","/spring-boot-activiti/spring-boot-activiti.jar", \
    "--logging.config=/spring-boot-activiti/logback.xml" \
]
CMD sleep 10;