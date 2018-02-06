# tequila 龙舌兰项目

1启动
1.1启动命令
dev
nohup java -server -Xms2g -Xmx2g -XX:PermSize=256m -XX:MaxPermSize=256m -Xmn800m -XX:MaxDirectMemorySize=512m -XX:SurvivorRatio=8 -XX:+UseConcMarkSweepGC -XX:+UseCMSCompactAtFullCollection -XX:+UseCMSCompactAtFullCollection -XX:CMSMaxAbortablePrecleanTime=5000 -XX:+CMSClassUnloadingEnabled -XX:CMSInitiatingOccupancyFraction=80 -XX:+UseCMSInitiatingOccupancyOnly -XX:+ExplicitGCInvokesConcurrent -XX:ParallelGCThreads=40 -Xloggc:/Users/wangyudong/tequila/logs/gc.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/Users/wangyudong/tequila/logs/java.hprof -Dfile.encoding=UTF-8 -jar tequila-1.0.0.jar --spring.profiles.active=dev >/Users/wangyudong/tequila/logs/start.log 2>&1 &
pro
nohup java -server -Xms2g -Xmx2g -XX:PermSize=256m -XX:MaxPermSize=256m -Xmn800m -XX:MaxDirectMemorySize=512m -XX:SurvivorRatio=8 -XX:+UseConcMarkSweepGC -XX:+UseCMSCompactAtFullCollection -XX:+UseCMSCompactAtFullCollection -XX:CMSMaxAbortablePrecleanTime=5000 -XX:+CMSClassUnloadingEnabled -XX:CMSInitiatingOccupancyFraction=80 -XX:+UseCMSInitiatingOccupancyOnly -XX:+ExplicitGCInvokesConcurrent -XX:ParallelGCThreads=40 -Xloggc:/home/tequila/logs/gc.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/home/tequila/logs/java.hprof -Dfile.encoding=UTF-8 -jar /home/tequila/deploy/tequila-1.0.0.jar --spring.profiles.active=pro >/home/tequila/logs/start.log 2>&1 &
1.2启动配置修改
-Xloggc:/home/tequila/logs/gc.log  gc日志文件路径修改为服务器具体日志路径
-XX:HeapDumpPath=/home/tequila/logs/java.hprof  jvm进程宕机时内存状态文件路径修改为服务器具体日志路径
--spring.profiles.active=pro 环境修改为线上环境pro
/home/tequila/logs/start.log 启动日志修改为服务器具体日志路径
tequila-1.0.0.jar 修改为jar文件具体路径/home/tequila/deploy/tequila-1.0.0.jar
1.3启动脚本
/home/tequila/deploy/tequila.sh

2数据库
2.1数据库名称
tequila
2.2用户表建表sql
CCREATE TABLE `tequila_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `name` varchar(100) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `mail` varchar(50) NOT NULL,
  `token` varchar(100) NOT NULL,
  `token_expire` datetime NOT NULL,
  `password` varchar(12) NOT NULL,
  `extend` varchar(500) DEFAULT NULL,
  `verify_status` tinyint(4) NOT NULL,
  `media_id` varchar(50) DEFAULT NULL,
  `media_type` tinyint(4) DEFAULT NULL,
  `real_name` varchar(50) DEFAULT NULL,
  `card_type` tinyint(4) DEFAULT NULL,
  `card_id` varchar(50) DEFAULT NULL,
  `member_type` tinyint(4) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  UNIQUE KEY `uk_phone` (`phone`),
  UNIQUE KEY `uk_mail` (`mail`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8

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
UserDO

3.4登录接口
url:/user/login
method:POST
参数:
phone:手机号
password:密码
verifyCode:验证码
返回结果result：
UserDO

3.5登出接口
url:/user/logout
method:GET
参数:无
返回结果result：无

3.6密码重置接口
url:/user/resetPassword
method:POST
参数:
password:原密码
newPassword:新密码
confirm:重复确认新密码
verifyCode:验证码
返回结果result：无

3.7密码找回接口
url:/user/findPassword
method:POST
参数:
mail:邮箱
verifyCode:验证码
返回结果result：无

3.8获取用户信息接口
url:/user/getUserInfo
method:GET
参数:无
返回结果result：
UserDO

3.9获取验证码接口
url:/user/getVerifyCode
method:GET
参数:
type:验证码类型，0 注册，1 登录，2 密码重置， 3 密码找回
width:图片的宽度,不传默认160
height:图片的高度，不传默认40
返回结果result：
异常或参数错误返回错误码和具体描述，正常返回验证码图片

3.10关键词搜索列表接口
url:/search/keyword/list
method:GET
参数:
query:关键词
page:页数，不传默认1
返回结果result：
list:[WechartArticle]

3.11关键词搜索详情接口
url:/search/keyword/info
method:GET
参数:
url:详情页url地址
返回结果result：
抓取是否返回错误码 1:系统错误
成功返回详情html 设置响应头：Content-Type: text/html; charset=UTF-8