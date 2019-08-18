package com.tangkf.metrics;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication(scanBasePackages = {"com.tangkf.metrics", "com.alibaba.metrics.annotation"})
public class AlibabaMetricsApplication implements ApplicationRunner {


	public static void main(String[] args) {
		SpringApplication.run(AlibabaMetricsApplication.class, args);

	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		log.info("启动http");
//		Bootstrap.init();
	}
}
