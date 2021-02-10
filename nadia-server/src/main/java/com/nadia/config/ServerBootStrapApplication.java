package com.nadia.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan(value = {"com.nadia"})
@ImportResource(locations = { "classpath:mybatis/mapper/**/*.xml" })
@EnableAsync
@MapperScan({"com.nadia.*.*.dao"})
public class ServerBootStrapApplication {

	public static void main(String[] args) {
		ApplicationContext context = new SpringApplicationBuilder(ServerBootStrapApplication.class).run(args);
	}
}
