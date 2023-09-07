#!/bin/sh

# 此脚本在maven构建完成后 将要发送的包进行整理 之后同期其他脚本推送到运行服务器
# 具体包路径 WORKSPACE: 基本路径
BUSINESS_ALL=`find ${WORKSPACE}/spring-boot-business -name *.jar|grep .jar`
WEB_API=`find ${WORKSPACE}/spring-boot-client/spring-boot-web-api -name *.jar|grep .jar`
SYSTEM=`find ${WORKSPACE}/spring-boot-system -name *.jar|grep .jar`
#拷贝需要上传的文件位置
COPY_FILE=${WORKSPACE}/compress
rm -rf ${COPY_FILE}/*
rm -rf ${WORKSPACE}/.build/send

# 获取所有的JAR 开始遍历
handle_jar(){
for JAR_FILE in $*; do
  if [ -f $JAR_FILE ]; then
    # jar包名称
    JAR_FILE_NAME=`basename -s .jar $JAR_FILE`
    # 获取target之前路径
    BEFORE_TARGET=${JAR_FILE%/target/*}
    # 项目名称
    PROJECT_NAME=`basename -s .jar $BEFORE_TARGET`
    # 获取md5文件目录
    JAR_MD5=${BEFORE_TARGET}/md5
    # 获取md5解压文件目录
    JAR_TME=${JAR_MD5}/tme
    # 获取md5文件
    JAR_MD5_FILE=${JAR_MD5}/${JAR_FILE_NAME}.md5
    # 获取md5解压文件
    JAR_TMP_FILE=${JAR_MD5}/${JAR_FILE_NAME}.tmp
    # md5目录不存在创建
    if [ ! -d $JAR_TME ]; then
       mkdir -p $JAR_TME
    fi
    #判断是否是依赖jar包  copy jar包位置
    if `echo $JAR_FILE |grep -q "/lib"`;then
        COPY_PROJECT_FILE=${COPY_FILE}/${PROJECT_NAME}/lib
    else
        COPY_PROJECT_FILE=${COPY_FILE}/${PROJECT_NAME}
    fi
    # 判断MD5文件是否存在，存在就校验MD5值
    if [ ! -f $JAR_MD5_FILE ]; then
      echo "jar包没有MD5值:"$COPY_PROJECT_FILE/$JAR_FILE_NAME
    else
      # 校验MD5
      md5sum --status -c $JAR_MD5_FILE
      # = 0表示校验成功 =1 表示校验失败
      if [ $? = 0 ];then
        #没有更新则删除不用上传
        rm -f $JAR_FILE
        continue
      else
        echo "jar包MD5校验失败,准备解压验证:"$COPY_PROJECT_FILE/$JAR_FILE_NAME
      fi
    fi
    # 删除解压目录文件
    rm -rf $JAR_TME/*
    #解压 文件到指定文件夹
    unzip -q $JAR_FILE -d $JAR_TME
    if [ ! -f $JAR_TMP_FILE ]; then
      # copy jar包位置不存在创建
      if [ ! -d $COPY_PROJECT_FILE ]; then
         mkdir -p $COPY_PROJECT_FILE
      fi
      # 将最新的MD5值写入到缓存文件
      cp $JAR_FILE $COPY_PROJECT_FILE
      find $JAR_TME -type f -print | xargs md5sum > $JAR_TMP_FILE
      echo `md5sum $JAR_FILE` > $JAR_MD5_FILE
      echo "jar包解压文件没有MD5值:"$COPY_PROJECT_FILE/$JAR_FILE_NAME
    else
       # 校验MD5
       md5sum --status -c $JAR_TMP_FILE
       # = 0表示校验成功 =1 表示校验失败
       if [ $? = 0 ];then
         #解压包验证成功无需发送
         rm -f $JAR_FILE
         echo "jar包解压文件MD5校验成功:"$COPY_PROJECT_FILE/$JAR_FILE_NAME
         continue
       else
         # copy jar包位置不存在创建
         if [ ! -d $COPY_PROJECT_FILE ]; then
            mkdir -p $COPY_PROJECT_FILE
         fi
         # 将最新的MD5值写入到缓存文件
         cp $JAR_FILE $COPY_PROJECT_FILE
         find $JAR_TME -type f -print | xargs md5sum > $JAR_TMP_FILE
         echo `md5sum $JAR_FILE` > $JAR_MD5_FILE
         echo "jar包解压文件MD5校验失败:"$COPY_PROJECT_FILE/$JAR_FILE_NAME
       fi
    fi
  fi
done
}

# 开始依次处理
handle_jar $BUSINESS_ALL
handle_jar $WEB_API
handle_jar $SYSTEM
#压缩 jar包准备发送
if [  -d $COPY_FILE ]; then
  mkdir -p ${WORKSPACE}/.build/send
  COPY_FILE_SEND=${WORKSPACE}/.build/send/spring-cloud.tar.gz
  cd $COPY_FILE
  tar -pzcf $COPY_FILE_SEND ./*
  rm -rf $COPY_FILE
fi
