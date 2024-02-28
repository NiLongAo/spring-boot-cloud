FROM anapsix/alpine-java:8_server-jre_unlimited
# 同步时区
ENV TZ=Asia/Shanghai
ENV LANG=C.UTF-8
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
# 解决 图片 中文乱码 路径在 C:\Windows\Fonts\simsun.ttc
ADD ./start/simsun.ttc /opt/jdk1.8.0_202/jre/lib/fonts/simsun.ttc
# ADD ./target/tmp ./skywalking-agent-lib/
# Djava.security.egd=file:/dev/./urandom 解决阿里云的tomcat启动慢的问题
ENTRYPOINT [ \
    "java", \
#    "-javaagent:/spring-boot-activiti/skywalking-agent-lib/skywalking-agent.jar", \
#    "-Dskywalking.agent.service_name=spring-boot-activiti", \
#    "-Dskywalking.collector.backend_service=1.15.9.228:12011", \
    "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=14006", \
    "-XX:MetaspaceSize=64m", "-XX:MaxMetaspaceSize=128m","-XX:+AlwaysPreTouch", "-Xms128m", "-Xmx256m", "-Xmn128m", "-Xss256k", "-XX:SurvivorRatio=8",\
    "-Dloader.path=/spring-boot-activiti/lib", \
    "-Dfile.encoding=utf-8", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar","/spring-boot-activiti/spring-boot-activiti.jar", \
    "--logging.config=/spring-boot-activiti/logback.xml" \
]
CMD sleep 10;