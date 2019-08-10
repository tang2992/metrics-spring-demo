package com.tangkf.metrics.config;

import com.tangkf.metrics.RestProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpRequestExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class BeanConfig {


    @Bean
    public RestProvider restProvider() {
        return new RestProvider();
    }

    @Bean
    public HttpClient getHttpClient() {
        return HttpClientBuilder.create()
                .setRequestExecutor(new HttpRequestExecutor())
                .setConnectionManager(new PoolingHttpClientConnectionManager()).build();
    }
}
