#!/bin/sh

# 获取docker 构建镜像脚本
BUILD_SHELL_PATH=`find ${WORKSPACE} -name docker-image-build.sh | grep .docker-image-build.sh`


# 获取所有的JAR 开始遍历
handle_jar(){
  # 用于标记是否需要重启的标识
  RESTART=0
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
        # 将最新的MD5值写入到缓存文件
        find $JAR_TME -type f -print | xargs md5sum > $JAR_TMP_FILE
        echo `md5sum $JAR_FILE` > $JAR_MD5_FILE
        echo "jar包解压文件没有MD5值:"$COPY_PROJECT_FILE/$JAR_FILE_NAME
        RESTART=1
        break
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
           # 将最新的MD5值写入到缓存文件
           find $JAR_TME -type f -print | xargs md5sum > $JAR_TMP_FILE
           echo `md5sum $JAR_FILE` > $JAR_MD5_FILE
           echo "jar包解压文件MD5校验失败:"$COPY_PROJECT_FILE/$JAR_FILE_NAME
           RESTART=1
           break
         fi
      fi
    fi
  done
  return $RESTART
}

# 获取所有的JAR 开始遍历
for BUILD_SHELL in $BUILD_SHELL_PATH;do
  if [ -f $BUILD_SHELL ];then
    echo ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
    #获取项目路径
    PROJECT_URL=`dirname  $BUILD_SHELL`
    #获取项目所有jar包
    BUSINESS_ALL=`find $PROJECT_URL/target -name *.jar|grep .jar`
    #标记是否需要重新构建
    BUSINESS_RESTART=0
    for JAR_FILE in $BUSINESS_ALL;do
      handle_jar $JAR_FILE
      if [ $? -eq "1" ];then
        echo "项目需要重新构建:"$PROJECT_URL
        BUSINESS_RESTART=1
      fi
    done
    echo "BUSINESS_RESTART:"$BUSINESS_RESTART
    if [ $BUSINESS_RESTART -eq "0" ]; then
      echo "无需构建镜像:"$PROJECT_URL
    else
      echo "构建项目镜像中:"$PROJECT_URL
      sh $BUILD_SHELL
      echo "构建项目镜像完成:"$PROJECT_URL
    fi
    echo "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
    echo ""
  fi
done