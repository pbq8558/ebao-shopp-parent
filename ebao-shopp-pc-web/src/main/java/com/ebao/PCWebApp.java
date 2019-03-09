package com.ebao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients //��ע���ʾ����@FeignClientע�⣬�������ûᱨ��
public class PCWebApp {

	public static void main(String[] args) {

		SpringApplication.run(PCWebApp.class, args);
	}

}
