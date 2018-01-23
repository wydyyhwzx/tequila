# tequila 龙舌兰项目

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
3.1返回结果格式
{
    "code":0, //状态码,0为成功，其他为失败
    "message":"", //状态信息,开发人员使用
    "description":"", //描述信息, 展示给外部用户
    "result":{}  //业务处理结果，json格式
}

3.2通用状态码
0:成功
1:系统错误
2:参数错误
3:未登录

3.3注册接口
url:/user/register
method:POST
参数:
name:用户名
phone:手机号
mail:邮箱
password:密码
confirm:重复确认密码
verifyCode:验证码
返回结果result：
无

3.4获取验证码接口
url:/user/getVerifyCode
method:GET
参数:
type:验证码类型，0 注册，1 登录，2 密码重置， 3 密码找回
返回结果result：
{"code":"Q69PGF"}
