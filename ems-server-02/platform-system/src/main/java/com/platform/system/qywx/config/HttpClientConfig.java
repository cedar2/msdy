package com.platform.system.qywx.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@ConfigurationProperties(prefix = "httpclient")
public class HttpClientConfig {

    private int connectionTimeToLive = 2;

    private boolean connectionManagerShared = true;

    private int maxConnTotal = 5;

    private int maxConnPerRoute = 5;

    private int socketTimeout = 60000;

    private int connectTimeout = 60000;

    private int connectionRequestTimeout = 60000;

    @Bean(name = "httpClient")
    public CloseableHttpClient httpClient(HttpClientConfig config) {
        log.info("-----httpClient-init-----，{}", config.toString());
        HttpClientBuilder builder = HttpClientBuilder.create();
        //8秒钟无响应则自动关闭
        builder.setConnectionTimeToLive(config.getConnectionTimeToLive(), TimeUnit.SECONDS);
        //链接共享
        builder.setConnectionManagerShared(config.isConnectionManagerShared());
        //总线程数
        builder.setMaxConnTotal(config.getMaxConnTotal());
        //每次请求数量
        builder.setMaxConnPerRoute(config.getMaxConnPerRoute());
        RequestConfig rconfig = RequestConfig.custom()
                .setSocketTimeout(config.getSocketTimeout())
                .setConnectTimeout(config.getConnectTimeout())
                .setConnectionRequestTimeout(config.getConnectionRequestTimeout()).build();
        CloseableHttpClient client = builder.setDefaultRequestConfig(rconfig).build();
        return client;
    }


    public int getConnectionTimeToLive() {
        return connectionTimeToLive;
    }

    public void setConnectionTimeToLive(int connectionTimeToLive) {
        this.connectionTimeToLive = connectionTimeToLive;
    }

    public boolean isConnectionManagerShared() {
        return connectionManagerShared;
    }

    public void setConnectionManagerShared(boolean connectionManagerShared) {
        this.connectionManagerShared = connectionManagerShared;
    }

    public int getMaxConnTotal() {
        return maxConnTotal;
    }

    public void setMaxConnTotal(int maxConnTotal) {
        this.maxConnTotal = maxConnTotal;
    }

    public int getMaxConnPerRoute() {
        return maxConnPerRoute;
    }

    public void setMaxConnPerRoute(int maxConnPerRoute) {
        this.maxConnPerRoute = maxConnPerRoute;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    @Override
    public String toString() {
        return "HttpClientConfig{" +
                "connectionTimeToLive=" + connectionTimeToLive +
                ", connectionManagerShared=" + connectionManagerShared +
                ", maxConnTotal=" + maxConnTotal +
                ", maxConnPerRoute=" + maxConnPerRoute +
                ", socketTimeout=" + socketTimeout +
                ", connectTimeout=" + connectTimeout +
                ", connectionRequestTimeout=" + connectionRequestTimeout +
                '}';
    }
}
