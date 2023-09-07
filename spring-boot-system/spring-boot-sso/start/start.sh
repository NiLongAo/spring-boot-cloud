#!/bin/sh

APP_NAME=debug
#skywalking包路径
SKY_LIB='-javaagent:/usr/local/program/spring-cloud/lib/skywalking-agent/skywalking-agent.jar -Dskywalking.agent.service_name=spring-boot-sso -Dskywalking.collector.backend_service=1.15.9.228:12011'
#lib包路径
JAR_LIB='/usr/local/program/spring-cloud/lib/sso-lib'
#jar包路径
JAR_PATH='/usr/local/program/spring-cloud/spring-boot-sso'
#jar名称
JAR_NAME=spring-boot-sso.jar
#日志路径
LOG_PATH=/usr/local/program/spring-cloud/spring-boot-sso/logs
#PID  代表是PID文件
PID=$JAR_NAME\.pid
#JDK路径
JDK_PATH=/usr/local/program/jdk/jdk1.8.0_291/bin/java
#JDK路径
LOG4J2_PATH=file:/usr/local/program/spring-cloud/spring-boot-sso/logback.xml

#使用说明，用来提示输入参数
usage() {
    echo "Usage: sh 执行脚本.sh [start|stop|restart|status]"
    exit 1
}

#检查程序是否在运行
is_exist(){
  pid=`ps -ef|grep $JAR_NAME|grep -v grep|awk '{print $2}' `
  #如果不存在返回1，存在返回0
  if [ -z "${pid}" ]; then
   return 1
  else
    return 0
  fi
}

#启动方法
# skywalking 启动关闭 系统内存不足
# nohup $JDK_PATH $SKY_LIB -Dloader.path=$JAR_LIB -jar $JAR_PATH/$JAR_NAME >> $LOG_PATH/$APP_NAME.log -server -Xmx256m -Xms128m -Xmn128m -Xss16m --logging.config=$LOG4J2_PATH --spring.profiles.active=prod  2>&1 &
start(){
  is_exist
  if [ $? -eq "0" ]; then
    echo ">>> $APP_NAME is already running PID=${pid} <<<"
  else
	nohup  $JDK_PATH -Dloader.path=$JAR_LIB -jar $JAR_PATH/$JAR_NAME >> $LOG_PATH/$APP_NAME.log -server -Xmx256m -Xms128m -Xmn128m -Xss16m --logging.config=$LOG4J2_PATH --spring.profiles.active=prod  2>&1 &
	echo $! > $PID
    echo ">>> start $APP_NAME successed PID=$! <<<"
	  # tail -f $LOG_PATH/$APP_NAME.log
   fi
  }

#停止方法
stop(){
  #is_exist
  pidf=$(cat $PID)
  #echo "$pidf"
  echo ">>> PID = $pidf begin kill $pidf <<<"
  kill $pidf
  rm -rf $PID
  sleep 2
  is_exist
  if [ $? -eq "0" ]; then
    echo ">>> PID = $pid begin kill -9 $pid  <<<"
    kill -9  $pid
    sleep 2
    echo ">>> $APP_NAME process stopped <<<"
  else
    echo ">>> $APP_NAME is not running <<<"
  fi
}

#输出运行状态
status(){
  is_exist
  if [ $? -eq "0" ]; then
    echo ">>> $APP_NAME is running PID is ${pid} <<<"
  else
    echo ">>> $APP_NAME is not running <<<"
  fi
}

#重启
restart(){
  stop
  start
}

#根据输入参数，选择执行对应方法，不输入则执行使用说明
case "$1" in
  "start")
    start
    ;;
  "stop")
    stop
    ;;
  "status")
    status
    ;;
  "restart")
    restart
    ;;
  *)
    ;;
esac
exit 0
