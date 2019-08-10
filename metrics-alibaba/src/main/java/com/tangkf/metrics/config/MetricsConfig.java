package com.tangkf.metrics.config;

import com.alibaba.metrics.MetricRegistry;
import com.alibaba.metrics.NOPMetricManager;
import com.alibaba.metrics.reporter.Slf4jReporter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MetricsConfig  {

	private MetricRegistry registry;

	
	@Bean
	public MetricRegistry getMetricRegistry() {
		
		if (this.registry == null) {
			registry = new NOPMetricManager().getMetricRegistryByGroup("test");
		
			// register JVM metrics
//			registry.registerAll(new GarbageCollectorMetricSet());
//			registry.registerAll(new MemoryUsageGaugeSet());
//			registry.registerAll(new ThreadStatesGaugeSet());

		}
		
		return registry;
	}

	@Bean
	public Slf4jReporter reporter(MetricRegistry registry) {

		return Slf4jReporter.forRegistry(registry).build();
	}


}
