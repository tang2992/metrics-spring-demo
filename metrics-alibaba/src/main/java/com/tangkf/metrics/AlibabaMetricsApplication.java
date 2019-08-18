package com.tangkf.metrics;

import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricRegistry;
import com.alibaba.metrics.reporter.Slf4jReporter;
import com.tangkf.metrics.reporter.OpenFalcon;
import com.tangkf.metrics.reporter.OpenFalconReporter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootApplication(scanBasePackages = {"com.tangkf.metrics", "com.alibaba.metrics.annotation"})
public class AlibabaMetricsApplication implements ApplicationRunner {


	public static void main(String[] args) {
		SpringApplication.run(AlibabaMetricsApplication.class, args);

	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		MetricRegistry registry = MetricManager.getIMetricManager().getMetricRegistryByGroup("test");
		Slf4jReporter.forRegistry(registry).build().start(20, TimeUnit.SECONDS);

		OpenFalcon openFalcon = new OpenFalcon.Builder("http://47.106.137.38:1988").create();
		OpenFalconReporter.forRegistry(registry).build(openFalcon, "test").start(20, TimeUnit.SECONDS);

//		Bootstrap.init();
	}
}
