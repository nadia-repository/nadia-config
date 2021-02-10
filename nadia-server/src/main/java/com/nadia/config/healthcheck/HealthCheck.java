package com.nadia.config.healthcheck;

import com.nadia.config.common.rest.RestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author: Wally.Wang
 * @date: 2020/07/23
 * @description:
 */
@Slf4j
@RestController
public class HealthCheck {

	@Resource
	private ApplicationContext ctx;

	@RequestMapping(value = "/system/version", method = RequestMethod.GET)
	public RestBody<String> ping() {
		String version= "version.txt";
		RestBody<String> response= new RestBody<>();
		response.setData("No version.txt file found in classpath.");
		try(InputStream in= ctx.getClassLoader().getResourceAsStream(version)){
			if(in!= null){
				byte[] content= new byte[in.available()];
				in.read(content);
				response.setData(new String(content,"UTF-8"));
			}
		} catch (IOException e) {
			log.error("Failed to read version.txt file error.", e);
		}
		return response;
	}

}
