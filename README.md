# tequila
龙舌兰项目

1启动
1.1启动命令
nohup java -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m -Xms1024m -Xmx1024m 
-Xmn256m -Xss256k -XX:SurvivorRatio=8 -XX:+UseConcMarkSweepGC -jar tequila-1.0.0.jar --spring.profiles.active=dev
> /Users/wangyudong/tequila/tomcat/logs/springBoot.log
1.2启动配置修改
/Users/wangyudong/tequila/tomcat/logs/springBoot.log 路径修改为具体的服务器路径
spring.profiles.active=dev 环境修改为线上环境pro

2数据库

3接口