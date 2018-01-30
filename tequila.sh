#!/bin/sh

#应用名
PROJECT_NAME=tequila
#执行程序启动所使用的系统用户
RUNNING_USER=root
#jar文件路径
JAR_HOME=/home/tequila/deploy/tequila-1.0.0.jar
#启动日志文件
START_FILE=/home/tequila/logs/start.log
#gc日志文件
GC_FILE=/home/tequila/logs/gc.log
#jvm进程宕机时内存状态文件
HPROF_FILE=/home/tequila/logs/java.hprof

#java虚拟机启动参数
JAVA_OPTS="-server -Xms2g -Xmx2g -XX:PermSize=256m -XX:MaxPermSize=256m -Xmn800m -XX:MaxDirectMemorySize=512m -XX:SurvivorRatio=8 -XX:+UseConcMarkSweepGC -XX:+UseCMSCompactAtFullCollection -XX:+UseCMSCompactAtFullCollection -XX:CMSMaxAbortablePrecleanTime=5000 -XX:+CMSClassUnloadingEnabled -XX:CMSInitiatingOccupancyFraction=80 -XX:+UseCMSInitiatingOccupancyOnly -XX:+ExplicitGCInvokesConcurrent -XX:ParallelGCThreads=40 -Xloggc:$GC_FILE -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$HPROF_FILE -Dfile.encoding=UTF-8 -Dproject.name=$PROJECT_NAME"

#初始化psid变量（全局）
psid=0

#(函数)判断程序是否已启动
checkpid() {
   javaps=`jps -l | grep $PROJECT_NAME`

   if [ -n "$javaps" ]; then
      psid=`echo $javaps | awk '{print $1}'`
   else
      psid=0
   fi
}

#(函数)启动程序
start() {
   checkpid

   if [ $psid -ne 0 ]; then
      echo "================================"
      echo "warn: $PROJECT_NAME already started! pid=$psid"
      echo "================================"
   else
      echo -n "Starting $PROJECT_NAME ..."
      JAVA_CMD="nohup java $JAVA_OPTS -jar $JAR_HOME --spring.profiles.active=pro >$START_FILE 2>&1 &"
      su - $RUNNING_USER -c "$JAVA_CMD"
      checkpid
      if [ $psid -ne 0 ]; then
         echo "pid=$psid [OK]"
      else
         echo "[Failed]"
      fi
   fi
}

#(函数)停止程序
stop() {
   checkpid

   if [ $psid -ne 0 ]; then
      echo -n "Stopping $PROJECT_NAME ... pid=$psid"
      su - $RUNNING_USER -c "kill -9 $psid"
      if [ $? -eq 0 ]; then
         echo "[OK]"
      else
         echo "[Failed]"
      fi

      checkpid
      if [ $psid -ne 0 ]; then
         stop
      fi
   else
      echo "================================"
      echo "warn: $PROJECT_NAME is not running"
      echo "================================"
   fi
}

#(函数)检查程序运行状态
status() {
   checkpid

   if [ $psid -ne 0 ];  then
      echo "$PROJECT_NAME is running! pid=$psid"
   else
      echo "$PROJECT_NAME is not running"
   fi
}

###################################
#读取脚本的第一个参数($1)，进行判断
#参数取值范围：{start|stop|restart|status}
#如参数不在指定范围之内，则打印帮助信息
###################################
case "$1" in
   'start')
      start
      ;;
   'stop')
     stop
     ;;
   'restart')
     stop
     start
     ;;
   'status')
     status
     ;;
   *)
     echo "Usage: $0  start|stop|restart|status"
     ;;
esac
exit 0

