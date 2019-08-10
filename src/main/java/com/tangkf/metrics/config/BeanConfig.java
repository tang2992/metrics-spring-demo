package com.tangkf.metrics.config;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.httpclient.HttpClientMetricNameStrategies;
import com.codahale.metrics.httpclient.InstrumentedHttpClients;
import com.tangkf.metrics.RestProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

@Slf4j
@Configuration
public class BeanConfig {

    @Autowired
    private MetricRegistry registry;

    @Bean
    public RestProvider restProvider() {
        return new RestProvider();
    }

    @Bean
    public HttpClient getHttpClient() {
        return InstrumentedHttpClients.createDefault(registry,
                HttpClientMetricNameStrategies.QUERYLESS_URL_AND_METHOD
        );
    }
}
