package com.ebao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients //该注解表示启用@FeignClient注解，不加启用会报错
public class PCWebApp {

	public static void main(String[] args) {

		SpringApplication.run(PCWebApp.class, args);
	}

}
