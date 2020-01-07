package com.nadia.config;

import com.nadia.config.annotation.EnableNadiaConfig;
import com.nadia.config.bean.ConfigConfigurationPropertiesDemo;
import com.nadia.config.bean.ConfigValueDemo;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

import java.io.IOException;


@SpringBootApplication()
@EnableNadiaConfig(basePackages = {"com.nadia.config"})
public class DemoBootSampleApplication {

  public static void main(String[] args) throws IOException {
    ApplicationContext context = new SpringApplicationBuilder(DemoBootSampleApplication.class).run(args);
    ConfigValueDemo bean = context.getBean(ConfigValueDemo.class);
    System.out.println(bean.getNadia_Integer());
    System.out.println(bean.getNadia_String());
    System.out.println(bean.isNadia_boolean());
    ConfigConfigurationPropertiesDemo bean1 = context.getBean(ConfigConfigurationPropertiesDemo.class);
    System.out.println(bean1.getName());
    System.out.println(bean1.getValue());
  }
}
