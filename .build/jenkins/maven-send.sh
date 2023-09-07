#!/bin/sh

# 此脚本是为了将包发送到运行服务器后执行脚本
# 进入文件夹
HOME_FILE=/usr/local/program/spring-cloud
HOME_TARGET_FILE=${HOME_FILE}/target
SPRING_CLOUD_FILE=${HOME_TARGET_FILE}/spring-cloud.tar.gz
MAVEN_SEND_FILE=${HOME_TARGET_FILE}/maven-send.sh
if [ ! -d $HOME_TARGET_FILE ];then
  mkdir -p target
fi

#解压文件
if [ ! -f $SPRING_CLOUD_FILE ];then
  echo "未获取jar包压缩文件，没有更新jar";
else
  tar pzxf $SPRING_CLOUD_FILE -C $HOME_TARGET_FILE
  #给脚本权限
  #if[ -f $MAVEN_SEND_FILE ]; then
  #  chmod 777 $MAVEN_SEND_FILE
  #  echo "执行脚本中....."
  #  sh $MAVEN_SEND_FILE
  #fi
  #正式执行脚本----------------------------------------------
  ALL_JAR=`find ${HOME_TARGET_FILE} -name *.jar|grep .jar`
  #开始循环所有jar包
  for JAR_FILE in $ALL_JAR;do
    #获取项目名
    PROJECT_NAME=`dirname  ${JAR_FILE#*/target/}`
    PROJECT_NAME=${PROJECT_NAME%%/*}
    echo '当前lib:'$JAR_FILE
    echo
    if `echo $JAR_FILE |grep -q "/lib"`;then
      MOVE_LOCATION=$HOME_FILE/lib/$PROJECT_NAME'-lib'/
    else
      MOVE_LOCATION=$HOME_FILE/$PROJECT_NAME/
    fi
     mv -f $JAR_FILE $MOVE_LOCATION
     echo '移动到:'$MOVE_LOCATION
  done
  rm -rf $HOME_TARGET_FILE
fi
