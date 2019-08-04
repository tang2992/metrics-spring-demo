package com.tangkf.metrics;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricRegistryListener;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.httpclient.HttpClientMetricNameStrategies;
import com.codahale.metrics.httpclient.InstrumentedHttpClients;
import com.codahale.metrics.jmx.JmxReporter;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.codahale.metrics.logback.InstrumentedAppender;
import com.codahale.metrics.servlets.AdminServlet;
import com.codahale.metrics.servlets.MetricsServlet;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@EnableMetrics
public class MetricsConfigurer extends MetricsConfigurerAdapter {

	private MetricRegistry registry;
	private HealthCheckRegistry healthCheckRegistry;

	@Bean
	public RestProvider getRestProvider() {
		return new RestProvider();
	}
	
	@Bean
	public HttpClient getHttpClient() {
		return InstrumentedHttpClients.createDefault(getMetricRegistry(),
			HttpClientMetricNameStrategies.QUERYLESS_URL_AND_METHOD
		);
	}
//	@Bean
//	public ServletRegistrationBean servletRegistrationBean(MetricRegistry metricRegistry) {
//		return new ServletRegistrationBean(new MetricsServlet(metricRegistry), "/monitor/metrics");
//	}
	@Bean
	public ServletRegistrationBean servletRegistrationBean(MetricRegistry metricRegistry) throws ServletException {
		AdminServlet adminServlet = new AdminServlet();
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(adminServlet, "/admin/*");
		return servletRegistrationBean;
	}
	
	@Override
	public MetricRegistry getMetricRegistry() {
		
		if (this.registry == null) {
			registry = new MetricRegistry();
		
			// register JVM metrics
			registry.registerAll(new GarbageCollectorMetricSet());
			registry.registerAll(new MemoryUsageGaugeSet());
			registry.registerAll(new ThreadStatesGaugeSet());

		}
		
		return registry;
	}

	@Override
	public HealthCheckRegistry getHealthCheckRegistry() {
		if (healthCheckRegistry == null) {
			healthCheckRegistry = new HealthCheckRegistry();
		}
		return healthCheckRegistry;
	}
	
	@Override
	public void configureReporters(MetricRegistry metricRegistry) {
		Slf4jReporter.forRegistry(metricRegistry).build().start(15, TimeUnit.SECONDS);
	}	
}
