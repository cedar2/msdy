package com.platform.system.qywx.util;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class HttpClientUtil {

    private static Logger LOG = LoggerFactory.getLogger(HttpClientUtil.class);

    @Autowired(required = true)
    private CloseableHttpClient httpClient;

    /**
     * post请求(application/json方式)
     *
     * @param url
     * @param jsonParam
     * @param header
     * @return
     */
    public String doPostByHeader(String url, String jsonParam, Map<String, String> header) {
        long start = System.currentTimeMillis();
        LOG.info("请求参数url:{}, reqParam:{},请求头：{}, 当前时间戳:{}", url, jsonParam, JSON.toJSONString(header), start);
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse res = null;
        try {
            httpPost.addHeader("Content-Type", "application/json");
//            httpPost.setHeader("Connection", "close");
            if (header != null) {
                for (Map.Entry<String, String> entity : header.entrySet()) {
                    httpPost.setHeader(entity.getKey(), entity.getValue());
                }
            }
            if (StrUtil.isNotBlank(jsonParam)) {
                StringEntity entity = new StringEntity(jsonParam, "utf-8");
                httpPost.setEntity(entity);
            }
            res = httpClient.execute(httpPost);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String content = EntityUtils.toString(res.getEntity(), "UTF-8");
                LOG.info("响应信息content:{},t:{}ms", content, System.currentTimeMillis() - start);
                return content;
            } else {
                httpPost.abort();
                String reasonPhrase = res.getStatusLine().getReasonPhrase();
                LOG.error("请求失败 Http Code:{},reqParam:{},t:{}ms", reasonPhrase, jsonParam, System.currentTimeMillis() - start);
            }
        } catch (Exception e) {
            LOG.error("HTTP请求异常,jsonParam:{},t:{}ms", jsonParam, System.currentTimeMillis() - start);
            LOG.error("HTTP请求异常", e);
        } finally {
            try {
                if (null != res) {
                    res.close();
                }
            } catch (Exception e2) {
            }
            LOG.info("url {},time:{} s , time:{}ms", url, (System.currentTimeMillis() - start) / 1000, (System.currentTimeMillis() - start));
        }
        return null;
    }

    /**
     * post请求(application/json方式)
     *
     * @param url
     * @param jsonParam
     * @return String
     */
    public String doPost(String url, String jsonParam) {
        return doPostByHeader(url, jsonParam, null);
    }

    /**
     * get
     *
     * @param url
     * @param header
     * @return
     */
    public String getByHeader(String url, Map<String, String> header) {
        long start = System.currentTimeMillis();
        String body = null;
        CloseableHttpResponse res = null;
        HttpGet httpGet = null;
        try {
            LOG.info("[med:get,mes:URL,url:" + url + "]");
            httpGet = new HttpGet(url);
            httpGet.setHeader("Connection", "close");
            if (header != null) {
                for (Map.Entry<String, String> entity : header.entrySet()) {
                    httpGet.setHeader(entity.getKey(), entity.getValue());
                }
            }
            res = httpClient.execute(httpGet);
            HttpEntity entity = res.getEntity();
            if (null != entity) {
                body = EntityUtils.toString(entity, "UTF-8");
            }
            LOG.info("[med:get,mes:响应消息,result:" + body + "], 耗时：" + (System.currentTimeMillis() - start) + "ms");
        } catch (Exception e) {
            LOG.error("[med:get,mes:请求异常,url:" + url + ", result:" + body + "], 耗时：" + (System.currentTimeMillis() - start) + "ms", e);
        } finally {
            if (null != res) {
                try {
                    res.close();
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
            if (null != httpGet) {
                try {
                    httpGet.abort();
                    httpGet.releaseConnection();
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            }
            LOG.info("url {},time:{} s , time:{}ms", url, (System.currentTimeMillis() - start) / 1000, (System.currentTimeMillis() - start));
        }
        return body;
    }

    /**
     * get
     *
     * @param url
     * @return
     */
    public String get(String url) {
        return getByHeader(url, null);
    }


    /**
     * http from表单提交请求
     *
     * @param url
     * @param params
     * @param header
     * @return
     */
    public String postForm(String url, List<NameValuePair> params, Map<String, String> header) {
        return postForm(url, params, header, true);
    }

    /**
     * http from表单提交请求
     *
     * @param url
     * @param params
     * @return String
     */
    public String postForm(String url, List<NameValuePair> params, Map<String, String> header, boolean isLogs) {
        long start = System.currentTimeMillis();
        CloseableHttpResponse res = null;
        try {
            if (isLogs) {
                LOG.info("postForm,url:" + url + ", params:" + params.toString());
            } else {
                LOG.info("postForm,url:" + url);
            }

            HttpPost httpPost = new HttpPost(url);
            HttpEntity entity;
            if (header != null) {
                for (Map.Entry<String, String> v : header.entrySet()) {
                    httpPost.setHeader(v.getKey(), v.getValue());
                }
            }

            if (params != null && !params.isEmpty()) {
                httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            }

            res = httpClient.execute(httpPost);
            entity = res.getEntity();
            LOG.info("postForm,status ：{}", res.getStatusLine().getStatusCode());
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK && entity != null) {
                String content = EntityUtils.toString(entity, "UTF-8");
                LOG.info("postForm,响应信息content:" + content + ", 耗时：" + (System.currentTimeMillis() - start) + "ms");
                return content;
            } else {
                httpPost.abort();
                entity.consumeContent();
                String reasonPhrase = res.getStatusLine().getReasonPhrase();
                if (isLogs) {
                    LOG.info("postForm,请求失败 Http Code：" + reasonPhrase + ",url" + url + ",params:" + params.toString() + ", 耗时：" + (System.currentTimeMillis() - start) + "ms");
                } else {
                    LOG.info("postForm,请求失败 Http Code：" + reasonPhrase + ",url" + url + ", 耗时：" + (System.currentTimeMillis() - start) + "ms");
                }

            }
        } catch (Exception e) {
            if (isLogs) {
                LOG.error("postForm,mes:请求异常,url:" + url + ",params:" + params.toString() + ", 耗时：" + (System.currentTimeMillis() - start) + "ms", e);
            } else {
                LOG.error("postForm,mes:请求异常,url:" + url + ", 耗时：" + (System.currentTimeMillis() - start) + "ms", e);
            }
        } finally {
            if (null != res) {
                try {
                    res.close();
                } catch (IOException e) {
                }
            }
            LOG.info("url {},time:{} s , time:{}ms", url, (System.currentTimeMillis() - start) / 1000, (System.currentTimeMillis() - start));
        }
        return null;
    }


}
