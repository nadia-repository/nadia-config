# nadia-client

## 安装
maven
```xml
<dependency>
    <groupId>com.nadia.config</groupId>
    <artifactId>nadia-client</artifactId>
    <version>${latest-release-version}</version>
</dependency>
```
gradle
```groovy
compile group: 'com.nadia.config', name: 'nadia-client', version: '${latest-release-version}'
```

## 基本用法
### 方式1
在springboot项目中yml配置文件里直接写需要的配置
```yaml
nadia:
  integer: 1
  string: test
  boolean: true
  double: 7.7
```
然后就可以在任意的spring bean中通过@Value注解直接使用
```java
import com.nadia.config.annotation.NadiaConfig;
import com.nadia.config.callback.CallbackDemo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigValueDemo {

    @Value("${nadia.integer}")
    private Integer someIntegerConfig;

    @Value("${nadia.string}")
    private String someStringConfig;

    @Value("${nadia.boolean}")
    private boolean someBooleanConfig;
    
    @Value("${nadia.double}")
    private boolean someDoubleConfig;
}
```
### 方式2
在springboot项目中yml配置文件里直接写需要的配置
```yaml
prefix:
  age: 1
  name: test
  flag: true
  money: 7.7
```
然后声明一个@ConfigurationProperties的类即可
```java
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "prefix")
@Data
public class ConfigConfigurationPropertiesDemo {
    
    private Integer age;
    
    private String name;
    
    private boolean flag;
    
    private Double money;
}
```
## 高级用法
### 自定义回调方法
在需要回调的属性上声明@NadiaConfig，并指定一个class
```java
@Component
public class ConfigValueDemo {

    @NadiaConfig(clazz = CallbackDemo.class)
    @Value("${nadia.integer}")
    private Integer someIntegerConfig;
    ...
}
```
CallbackDemo需要是一个spring bean并实现了Callback接口
```java
import org.springframework.stereotype.Component;
import com.nadia.config.callback.Callback;

@Component
public class CallbackDemo implements Callback {
    @Override
    public void callback(String key, Object oldValue, Object newValue) {
        // do your business
    }
}
```