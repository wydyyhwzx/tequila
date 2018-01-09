# tequila
龙舌兰项目

1。启动配置
nohup java -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m -Xms1024m -Xmx1024m 
-Xmn256m -Xss256k -XX:SurvivorRatio=8 -XX:+UseConcMarkSweepGC -jar tequila-1.0.0.jar 
> /Users/wangyudong/tequila/tomcat/logs/springBoot.log