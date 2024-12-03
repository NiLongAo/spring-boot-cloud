#!/bin/sh

# 项目在服务启动脚本(无docker)
# JDK的环境变量
export JAVA_HOME=/usr/local/program/jdk/jdk1.8.0_291
export PATH=$JAVA_HOME/bin:$PATH
# skywalking-ip地址
SKYWALKING_IP=1.82.217.118:12011
# 基础路径，由参数传入 $1 = /usr/local/program/spring-cloud
JAR_BATH=/usr/local/program/spring-cloud
# 多模块的时候，需要在路径中使用*统配一下多模块  /usr/local/program/spring-cloud/spring-boot-oa/spring-boot-oa.jar
# 比如/opt/ehang-spring-boot是多模块，下面由module1和module2
# 那么执行shell的时候使用：sh restart.sh /opt/ehang-spring-boot/\*  注意这里的*需要转义一下
JAR_PATH=${JAR_BATH}/spring-boot-*/*.jar

#JDK路径
JDK_PATH=/usr/local/program/jdk/jdk1.8.0_291/bin/java
#服务启动配置
PROFILES_ACTIVE=prod
# 获取所有的JAR 开始遍历
for JAR_FILE in $JAR_PATH;do
  if [ -f $JAR_FILE ];then
    echo ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
    echo "JAR路径:"$JAR_FILE
    # jar包名称
    JAR_FILE_NAME=`basename -s .jar $JAR_FILE`
    echo "JAR文件名:"$JAR_FILE_NAME
    # skywalking启动额外命令
    SKY_LIB="-javaagent:${JAR_BATH}/lib/skywalking-agent/skywalking-agent.jar -Dskywalking.agent.service_name=${JAR_FILE_NAME} -Dskywalking.collector.backend_service=${SKYWALKING_IP}"
    # lib包路径
    JAR_LIB=${JAR_BATH}/lib/${JAR_FILE_NAME}-lib
    # 日志路径
    LOG_PATH=${JAR_BATH}/${JAR_FILE_NAME}/logs/debug
    # 服务日志配置
    LOG4J2_PATH=file:${JAR_BATH}/${JAR_FILE_NAME}/logback.xml
    # 获取md5文件
    JAR_FILE_MD5=${JAR_FILE}.md5
    # 获取依赖md5文件
    JAR_TMP_FILE=${JAR_FILE}.tmp
    # 用于标记是否需要重启的标识
    RESTART=false
    # 判断MD5文件是否存在，存在就校验MD5值
    if [ -f $JAR_FILE_MD5 ]; then
      # 校验MD5
      md5sum --status -c $JAR_FILE_MD5
      # = 0表示校验成功 =1 表示校验失败
      if [ $? = 1 ];then
        echo "MD5校验失败,安装包已经更新！"
        RESTART=true
      else
        echo "安装包没有更新！"
      fi
    else
      echo "没有MD5值，说明是第一次启动"
      RESTART=true
    fi
    if [ -f $JAR_TMP_FILE ];then
      # 校验MD5
      md5sum --status -c $JAR_TMP_FILE
      # = 0表示校验成功 =1 表示校验失败
      if [ $? = 1 ];then
        echo "项目依赖MD5校验失败,安装包已经更新！"
        RESTART=true
      else
        echo "项目依赖安装包没有更新！"
      fi
    else
      RESTART=true
      echo "项目依赖安装包没有MD5值"
    fi
    # 获取进程号
    PROCESS_ID=`ps -ef | grep $JAR_FILE | grep -v grep | awk '{print $2}'`
    # 如果不需要重启，但是进程号没有，说明当前jar没有启动，同样也需要启动一下
    if [ $RESTART == false ] && [ ${#PROCESS_ID} == 0 ] ;then
       echo "没有发现进程，说明服务未启动,需要启动服务"
       RESTART=true
    fi

    # 如果是需要启动
    if [ $RESTART == true ]; then
        # kill掉原有的进程
        ps -ef | grep $JAR_FILE | grep -v grep | awk '{print $2}' | xargs kill -9

        #如果出现Jenins Job执行完之后，进程被jenkins杀死，可尝试放开此配置项
        #BUILD_ID=dontKillMe
        #启动Jar
        nohup $JDK_PATH $SKY_LIB -Dloader.path=$JAR_LIB -jar $JAR_FILE  > $LOG_PATH.log -server -Xmx256m -Xms128m -Xmn128m -Xss16m --logging.config=$LOG4J2_PATH --spring.profiles.active=$PROFILES_ACTIVE  2>&1 &
        # =0 启动成功 =1 启动失败
        if [ $? == 0 ];then
            echo "restart success!!! process id:" `ps -ef | grep $JAR_FILE | grep -v grep | awk '{print $2}'`
        else
            echo "启动失败！"
        fi

        # 将最新的MD5值写入到缓存文件
        echo `md5sum $JAR_FILE` > $JAR_FILE_MD5
        find $JAR_LIB -type f -print | xargs md5sum > $JAR_TMP_FILE
    fi
    echo "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
    echo ""
  fi
done