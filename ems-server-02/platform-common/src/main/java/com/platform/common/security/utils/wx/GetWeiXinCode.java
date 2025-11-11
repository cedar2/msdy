package com.platform.common.security.utils.wx;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.core.redis.RedisCache;
import com.platform.common.exception.base.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
@SuppressWarnings("all")
public class GetWeiXinCode {

    //    public static String appId = "wx5c7a29f6b672c273";
    public static String appId = "wx1b2e09a27ceba291";// 公众号

    //    public static String appSecret = "e6f51b39a49f8305ba80c51627b74c70";
    public static String appSecret = "3dbcc0d09c42c56f621f256dabe02c81";// 公众号

    public static final String qyCorpid = "ww3e6804c2405881e1";

    public static final String qySecret = "9pgveoM02S86O3hqLtQGIJkimfEP0Rc6sWK_RDqNLX8";

    public static final String qiye_tokenKey = "qiyeapi_access_token";

    public static final String dd_tokenKey = "ddapi_access_token";

    public static final String wx_codeKey = "wxgzh_code";

    private static RedisCache redisService;

    private static Logger logger = LoggerFactory.getLogger(GetWeiXinCode.class);

    /**
     * 微信公众号授权地址
     *
     * @param code
     * @param appid
     * @param SECRET
     * @return
     */
    public static String getCurrentOpenId(String code, String appid, String SECRET) {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appid + "&secret=" + SECRET + "&code=" + code + "&grant_type=authorization_code";
        return url;
    }

    /**
     * 获取微信用户信息URL拼接
     *
     * @param accessToken
     * @param openid
     * @param lang
     * @return
     */
    public static String getUserInfoByWeiXinURL(String accessToken, String openid, String lang) {
        if (Objects.isNull(lang)) {
            lang = "zh_CN";
        }
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openid + "&lang=" + lang;
        return url;
    }

    /**
     * 获取微信用户信息
     *
     * @param accessToken
     * @param openid
     * @param lang
     * @return
     */
    public static JSONObject getWeiXinUserInfo(String accessToken, String openid, String lang) {
        String currentOpenIdurl = GetWeiXinCode.getUserInfoByWeiXinURL(accessToken, openid, lang);
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(currentOpenIdurl, String.class);
        JSONObject obj = JSONObject.parseObject(result);
        try {
            String errcode = obj.getString("errcode");
            if (!"0".equals(errcode)) {
                throw new BaseException(obj.getString("errmsg"));
            }
        } catch (Exception e) {

        }
        return obj;
    }

    /**
     * 企业微信获取access_token url
     *
     * @param corpid
     * @param SECRET
     * @return
     */
    public static String getqyapiAccessTokenUrl(String corpid, String SECRET) {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=" + corpid + "&corpsecret=" + SECRET;
        return url;
    }

    /**
     * 微信公众号获取access_token url
     *
     * @param corpid
     * @param SECRET
     * @return
     */
    public static String getWxgzhAccessTokenUrl(String appid, String appSecret) {
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appid + "&secret=" + appSecret;
        return url;
    }

    /**
     * 企业微信推送消息url
     *
     * @param corpid
     * @param SECRET
     * @return
     */
    public static String getqyapiSendUrl(String ACCESS_TOKEN) {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + ACCESS_TOKEN;
        return url;
    }

    /**
     * 微信公众号推送消息url
     *
     * @param corpid
     * @param SECRET
     * @return
     */
    public static String getWxgzhSendUrl(String ACCESS_TOKEN) {
        String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + ACCESS_TOKEN;
        return url;
    }

    /**
     * 微信公众号推送订阅消息url
     *
     * @param ACCESS_TOKEN
     * @return
     */
    public static String getWxgzhDYSendUrl(String ACCESS_TOKEN) {
        String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + ACCESS_TOKEN;
        return url;
    }

    /**
     * 企业微信用户信息 url
     *
     * @param token
     * @param code
     * @return
     */
    public static String getqyapiUserInfoUrl(String token, String code) {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/auth/getuserinfo?access_token=" + token + "&code=" + code;
        return url;
    }

    /**
     * 企业微信上传临时素材文件 url
     *
     * @param token
     * @param code
     * @return
     */
    public static String getqyapiUploadUrl(String token, String type) {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/media/upload?access_token=" + token + "&type=" + type;
        return url;
    }


    public static String uploadFile(File file, String type) {
        String accessToken = getQyAccessToken();
        if (accessToken != null) {
            String currentOpenIdurl = GetWeiXinCode.getqyapiUploadUrl(accessToken, type);
            // 设置请求体，注意是LinkedMultiValueMap
            MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
            FileSystemResource fileSystemResource = new FileSystemResource(file);
            form.add("media", fileSystemResource);
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            // 用HttpEntity封装整个请求报文
            HttpEntity<MultiValueMap<String, Object>> data = new HttpEntity<>(form, headers);
            try {
                RestTemplate restTemplate = new RestTemplate();
                // 这里RestTemplate请求返回的字符串直接转换成JSONObject会报异常,后续深入找一下原因
                String resultString = restTemplate.postForObject(currentOpenIdurl, data, String.class);
                System.out.println(resultString);
                if (!StrUtil.isEmpty(resultString)) {
                    JSONObject jsonObject = JSONObject.parseObject(resultString);
                    return jsonObject.getString("media_id");
                }
            } catch (Exception e) {
                throw new BaseException(e.getMessage());
            }

        }
        return null;
    }


    /**
     * 获取企业微信access_token
     *
     * @return
     */
    public static String getQyAccessToken() {
        redisService = SpringUtil.getBean(RedisCache.class);
        String token = redisService.getCacheObject(qiye_tokenKey);
        if (StrUtil.isEmpty(token)) {
            String url = getqyapiAccessTokenUrl(qyCorpid, qySecret);
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(url, String.class);
            JSONObject obj = JSONObject.parseObject(result);
            logger.info("解析code返回的obj：" + obj);
            String errcode = (String) obj.getString("errcode");
            if (!errcode.equals("0")) {
                String errmsg = (String) obj.get("errmsg");
                throw new BaseException("获取token失败:" + errmsg);
            }
            String accessToken = (String) obj.get("access_token");
            Integer expiresIn = (Integer) obj.get("expires_in");
            redisService.setCacheObject(qiye_tokenKey, accessToken, Long.valueOf(expiresIn), TimeUnit.SECONDS);
            token = accessToken;
        }
        return token;
    }

    public static String getQyAccessTokenDirectly() {
        String url = getqyapiAccessTokenUrl(qyCorpid, qySecret);
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(url, String.class);
        JSONObject obj = JSONObject.parseObject(result);
        logger.info("解析code返回的obj：" + obj);
        String errcode = (String) obj.getString("errcode");
        if (!errcode.equals("0")) {
            String errmsg = (String) obj.get("errmsg");
            throw new BaseException("获取token失败:" + errmsg);
        }
        String accessToken = (String) obj.get("access_token");
        return accessToken;
    }

    public static String getQyAuthAccessTokenDirectly(SysUser user) {
        String url = getqyapiAccessTokenUrl(user.getWorkWechatAppkey(), user.getWorkWechatAppsecret());
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(url, String.class);
        JSONObject obj = JSONObject.parseObject(result);
        logger.info("解析code返回的obj：" + obj);
        String errcode = (String) obj.getString("errcode");
        if (!errcode.equals("0")) {
            String errmsg = (String) obj.get("errmsg");
            throw new BaseException("获取token失败:" + errmsg);
        }
        String accessToken = (String) obj.get("access_token");
        return accessToken;
    }

    /**
     * 获取企业微信access_token
     *
     * @return
     */
    public static String getQyAuthAccessToken(SysUser user) {
        redisService = SpringUtil.getBean(RedisCache.class);
        String token = redisService.getCacheObject(qiye_tokenKey);
        if (StrUtil.isEmpty(token)) {
            String url = getqyapiAccessTokenUrl(user.getWorkWechatAppkey(), user.getWorkWechatAppsecret());
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(url, String.class);
            JSONObject obj = JSONObject.parseObject(result);
            logger.info("解析code返回的obj：" + obj);
            String errcode = (String) obj.getString("errcode");
            if (!errcode.equals("0")) {
                String errmsg = (String) obj.get("errmsg");
                throw new BaseException("获取token失败:" + errmsg);
            }
            String accessToken = (String) obj.get("access_token");
            Integer expiresIn = (Integer) obj.get("expires_in");
            redisService.setCacheObject(qiye_tokenKey, accessToken, Long.valueOf(expiresIn), TimeUnit.SECONDS);
            token = accessToken;
        }
        return token;
    }

    /**
     * 获取企业微信用户信息
     *
     * @return
     */
    public static JSONObject getQyUserInfo(SysUser user) {
        String accessToken = getQyAuthAccessToken(user);
        logger.info("日志打印：GetWeiXinCode.getQyUserInfo.code：" + user.getCode());
        String currentOpenIdurl = GetWeiXinCode.getqyapiUserInfoUrl(accessToken, user.getCode());
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(currentOpenIdurl, String.class);
        logger.info("日志打印：GetWeiXinCode.getQyUserInfo.resultn：" + result);
        JSONObject obj = JSONObject.parseObject(result);
        String errcode = obj.getString("errcode");
        if (!"0".equals(errcode)) {
            throw new BaseException(result);
        }
        return obj;
    }

    /**
     * 获取企业微信用户信息
     *
     * @return
     */
    public static JSONObject getQyUserInfo(String code) {
        String accessToken = getQyAccessToken();
        String currentOpenIdurl = GetWeiXinCode.getqyapiUserInfoUrl(accessToken, code);
        System.out.println("qy currentOpenIdurl:" + currentOpenIdurl);
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(currentOpenIdurl, String.class);
        System.out.println("qy result: " + result);
        JSONObject obj = JSONObject.parseObject(result);
        String errcode = obj.getString("errcode");
        if (!"0".equals(errcode)) {
            throw new BaseException(obj.getString("errmsg"));
        }
        return obj;
    }

    public static JSONObject getOpenId(String code) {
        String currentOpenIdurl = GetWeiXinCode.getCurrentOpenId(code, appId, appSecret);
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(currentOpenIdurl, String.class);
        JSONObject obj = JSONObject.parseObject(result);
        logger.info("解析code返回的obj：" + obj);
        String accessToken = (String) obj.get("access_token");
        logger.info("=============accessToken==========" + accessToken);
        Integer expiresIn = (Integer) obj.get("expires_in");
        logger.info("===========expiresIn============" + expiresIn);
        String openid = (String) obj.get("openid");
        logger.info("=============openid==========" + openid);
        return obj;
    }

    public static JSONObject getOpenId(SysUser user) {
        String currentOpenIdurl = GetWeiXinCode.getCurrentOpenId(user.getCode(),
                                                                 user.getWxGzhAppkey(),
                                                                 user.getWxGzhAppsecret());
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(currentOpenIdurl, String.class);
        JSONObject obj = JSONObject.parseObject(result);
        logger.info("解析code返回的obj：" + obj);
        String accessToken = (String) obj.get("access_token");
        logger.info("=============accessToken==========" + accessToken);
        Integer expiresIn = (Integer) obj.get("expires_in");
        logger.info("===========expiresIn============" + expiresIn);
        String openid = (String) obj.get("openid");
        logger.info("=============openid==========" + openid);
        return obj;
    }

    /**
     * 获取微信公众号access_token
     *
     * @return
     */
    public static String getWxgzhAccessToken() {
        redisService = SpringUtil.getBean(RedisCache.class);
        String token = redisService.getCacheObject(wx_codeKey);
        if (StrUtil.isEmpty(token)) {
            String url = getWxgzhAccessTokenUrl(appId, appSecret);
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(url, String.class);
            JSONObject obj = JSONObject.parseObject(result);
            logger.info("解析code返回的obj：" + obj);
            String errcode = (String) obj.getString("errcode");
            if (!"0".equals(errcode) && StrUtil.isNotBlank(errcode)) {
                String errmsg = (String) obj.get("errmsg");
                throw new BaseException("获取token失败:" + errmsg);
            }
            String accessToken = (String) obj.get("access_token");
            Integer expiresIn = (Integer) obj.get("expires_in");
            redisService.setCacheObject(wx_codeKey, accessToken, Long.valueOf(expiresIn), TimeUnit.SECONDS);
            token = accessToken;
        }
        return token;
    }

    public static String getWxgzhAccessTokenDirectly() {
        String url = getWxgzhAccessTokenUrl(appId, appSecret);
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(url, String.class);
        JSONObject obj = JSONObject.parseObject(result);
        logger.info("解析code返回的obj：" + obj);
        String errcode = (String) obj.getString("errcode");
        if (!"0".equals(errcode) && StrUtil.isNotBlank(errcode)) {
            String errmsg = (String) obj.get("errmsg");
            throw new BaseException("获取token失败:" + errmsg);
        }
        return (String) obj.get("access_token");
    }
}
