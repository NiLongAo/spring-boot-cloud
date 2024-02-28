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
#-XX:MetaspaceSize=128m (元空间默认大小)
#-XX:MaxMetaspaceSize=128m (元空间最大大小)
#-Xms1024m (堆最大大小)
#-Xmx1024m (堆默认大小)
#-Xmn256m (新生代大小)
#-Xss256k (棧最大深度大小)
#-XX:SurvivorRatio=8 (新生代分区比例 8:2)
#-XX:+UseConcMarkSweepGC (指定使用的垃圾收集器，这里使用CMS收集器)
#-XX:+PrintGCDetails (打印详细的GC日志)
#知识点：
#JDK8之后把-XX:PermSize 和 -XX:MaxPermGen移除了，取而代之的是
#-XX:MetaspaceSize=128m (元空间默认大小)
#-XX:MaxMetaspaceSize=128m (元空间最大大小)
ENTRYPOINT [ \
    "java", \
#    "-javaagent:/spring-boot-web-api/skywalking-agent-lib/skywalking-agent.jar", \
#    "-Dskywalking.agent.service_name=spring-boot-web-api", \
#    "-Dskywalking.collector.backend_service=1.15.9.228:12011", \
    "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=14001", \
    "-XX:MetaspaceSize=64m", "-XX:MaxMetaspaceSize=128m","-XX:+AlwaysPreTouch", "-Xms128m", "-Xmx256m", "-Xmn128m", "-Xss256k", "-XX:SurvivorRatio=8",\
    "-Dloader.path=/spring-boot-web-api/lib", \
    "-Dfile.encoding=utf-8", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar","/spring-boot-web-api/spring-boot-web-api.jar", \
    "--logging.config=/spring-boot-web-api/logback.xml" \
]
CMD sleep 10;