# 人脸识别 特制运行环境镜像
FROM ccr.ccs.tencentyun.com/spring_boot_cloud/seetaface_face_work:latest
MAINTAINER 18789432816@126.com
# 同步时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN mkdir -p /spring-boot-face/lib
RUN mkdir -p /spring-boot-face/skywalking-agent-lib
RUN mkdir -p /spring-boot-face/logs
# RUN mkdir -p /spring-boot-face/conf

WORKDIR /spring-boot-face
EXPOSE 8640
EXPOSE 13008
ADD ./target/spring-boot-face.jar ./
ADD ./target/lib/* ./lib/
ADD ./start/logback.xml ./
# ADD ./target/tmp ./skywalking-agent-lib/
# ADD ./conf ./conf/ 已封装到镜像 无需单独加载
# Djava.security.egd=file:/dev/./urandom 解决阿里云的tomcat启动慢的问题
ENTRYPOINT [ \
    "java", \
#    "-javaagent:/spring-boot-face/skywalking-agent-lib/skywalking-agent.jar", \
#    "-Dskywalking.agent.service_name=spring-boot-face", \
#    "-Dskywalking.collector.backend_service=1.15.9.228:12011", \
    "-XX:MetaspaceSize=256M", "-XX:MaxMetaspaceSize=256M","-XX:+AlwaysPreTouch","-XX:ReservedCodeCacheSize=128m", "-Xms256m", "-Xmx256m", "-Xss512k", "-XX:+UseG1GC","-XX:G1HeapRegionSize=4M",\
    "-Dloader.path=/spring-boot-face/lib", \
    "-Dfile.encoding=utf-8", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar","/spring-boot-face/spring-boot-face.jar", \
    "--logging.config=/spring-boot-face/logback.xml" \
]
CMD sleep 10;