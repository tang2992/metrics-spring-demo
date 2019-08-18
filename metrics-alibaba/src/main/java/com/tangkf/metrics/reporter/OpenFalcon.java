package com.tangkf.metrics.reporter;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.*;

import java.io.IOException;
import java.util.Set;


/**
 * Created by tangshangwen on 17-3-6.
 */
@Slf4j
public class OpenFalcon {

    public static final int DEFAULT_BATCH_SIZE_LIMIT = 10;
    public static final int CONN_TIMEOUT_DEFAULT_MS = 5000;
    public static final int READ_TIMEOUT_DEFAULT_MS = 5000;

    public static Builder forService(String baseUrl) {
        return new Builder(baseUrl);
    }

    private final BoundRequestBuilder requestBuilder;
    private ObjectMapper mapper = new ObjectMapper();
    private int batchSizeLimit = DEFAULT_BATCH_SIZE_LIMIT;
    private AsyncHttpClient ahc;

    public static class Builder {
        private Integer connectionTimeout = CONN_TIMEOUT_DEFAULT_MS;
        private Integer readTimeout = READ_TIMEOUT_DEFAULT_MS;
        private String baseUrl;

        public Builder(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public Builder withConnectTimeout(Integer connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        public Builder withReadTimeout(Integer readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public OpenFalcon create() {
            return new OpenFalcon(baseUrl, connectionTimeout, readTimeout);
        }
    }

    private OpenFalcon(String baseURL, Integer connectionTimeout, Integer readTimeout) {
        AsyncHttpClientConfig acc = new DefaultAsyncHttpClientConfig.Builder()
                .setConnectTimeout(connectionTimeout)
                .setReadTimeout(readTimeout)
                .build();
        ahc = new DefaultAsyncHttpClient(acc);
        this.requestBuilder = ahc.preparePost(baseURL + "/v1/push");
    }

    public void setBatchSizeLimit(int batchSizeLimit) {
        this.batchSizeLimit = batchSizeLimit;
    }

    public void send(Set<OpenFalconMetric> metrics) {
        sendHelper(metrics);
    }

    private void sendHelper(Set<OpenFalconMetric> metrics) {
        if (!metrics.isEmpty()) {
            try {
                String body = mapper.writeValueAsString(metrics);
                log.info("metrics set: {}", body);
                requestBuilder
                        .setBody(body)
                        .execute(new AsyncCompletionHandler<Void>() {
                            @Override
                            public Void onCompleted(Response response) throws Exception {
                                if (response.getStatusCode() != 200) {
                                    log.error("send to open falcon endpoint failed: ("
                                            + response.getStatusCode() + ") "
                                            + response.getResponseBody());
                                }
                                return null;
                            }
                        });
            } catch (Throwable ex) {
                log.error("send to open falcon endpoint failed", ex);
            }
        }
    }

    public void close() {
        log.debug("ahc isClosed {}", ahc.isClosed());
        if (!ahc.isClosed()) {
            try {
                ahc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
