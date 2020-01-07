# config-server

## 部署
### jar包部署
1.通过gradle脚本编译可执行jar包
```shell
../gradlew bootRepackage
```



2.定义配置文件【application.yaml】
```yaml
spring:
  datasource:
    url: jdbc:mysql://
    username: 
    password: 

configcenter:
  redis:
    host: 
    port: 
    password:
```

3.将配置文件与jar包放在同一级目录

4.启动server
```shell
java -jar nadia-server.jar --server.port=8080
```