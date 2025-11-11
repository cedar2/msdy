package com.platform.common.security.utils.dingtalk;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.core.redis.RedisCache;
import com.platform.common.exception.base.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@SuppressWarnings("all")
public class GetDingtalkCode {

    public static String agentId = "1201699850";

    public static final String appkey = "dinge9rozhgtuishgt8i";

    public static final String appsecret = "PGYtV9Qh6KTg4HyIJFK-icPQ2KKq4Rys-wnj2CA6fzMdEK0jfyMOlOFIpLMfR-tn";

    public static final String ddCorpid = "ding441c00629444b8e324f2f5cc6abecb85";

    public static final String ddCorpsecret = "eDjtlby-X8uBVdudYe8eJcekvdhgHb-tc91uOh7i6_qGTNpb39KWv4Z7gDBVAvvn";

    public static final String dd_tokenKey = "ddapi_access_token";

    private static RedisCache redisService;

    private static Logger logger = LoggerFactory.getLogger(GetDingtalkCode.class);

    /**
     * 钉钉授权地址
     *
     * @param code
     * @param appid
     * @param SECRET
     * @return
     */
    public static String getDdurl(String code, String agentId, String SECRET) {
        String url = "https://oapi.dingtalk.com/user/getuserinfo/access_token?agentId=" + agentId + "&secret=" + SECRET + "&code=" + code + "&grant_type=authorization_code";
        return url;
    }

    /**
     * 钉钉获取access_token url
     *
     * @param corpid
     * @param SECRET
     * @return
     */
    public static String getDdapiAccessTokenUrl(String appkey, String appsecret) {
        String url = "https://oapi.dingtalk.com/gettoken?appkey=" + appkey + "&appsecret=" + appsecret;
        return url;
    }

    /**
     * 钉钉推送消息url
     *
     * @param corpid
     * @param SECRET
     * @return
     */
    public static String getDdapiSendUrl(String ACCESS_TOKEN) {
        String url = "https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2?access_token=" + ACCESS_TOKEN;
        return url;
    }

    /**
     * 钉钉用户信息 url
     *
     * @param token
     * @param code
     * @return
     */
    public static String getDdapiUserInfoUrl(String token, String code) {
        String url = "https://oapi.dingtalk.com/user/getuserinfo?access_token=" + token + "&code=" + code;
        return url;
    }

    /**
     * 获取钉钉access_token
     *
     * @return
     */
    public static String getDdAccessToken() {
        redisService = SpringUtil.getBean(RedisCache.class);
        String token = redisService.getCacheObject(dd_tokenKey);
        if (StrUtil.isEmpty(token)) {
            String url = getDdapiAccessTokenUrl(appkey, appsecret);
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
            redisService.setCacheObject(dd_tokenKey, accessToken, Long.valueOf(expiresIn), TimeUnit.SECONDS);
            token = accessToken;
        }
        return token;
    }

    /**
     * 获取钉钉access_token
     *
     * @return
     */
    public static String getDdAuthAccessToken(SysUser user) {
        redisService = SpringUtil.getBean(RedisCache.class);
        String token = redisService.getCacheObject(dd_tokenKey);
        if (StrUtil.isEmpty(token)) {
            String url = getDdapiAccessTokenUrl(user.getDingtalkAppkey(), user.getDingtalkAppsecret());
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
            redisService.setCacheObject(dd_tokenKey, accessToken, Long.valueOf(expiresIn), TimeUnit.SECONDS);
            token = accessToken;
        }
        return token;
    }

    /**
     * 获取钉钉用户信息
     *
     * @return
     */
    public static JSONObject getDdUserInfo(SysUser user) {
        String accessToken = getDdAuthAccessToken(user);
        String currentOpenIdurl = GetDingtalkCode.getDdapiUserInfoUrl(accessToken, user.getCode());
        log.info("GetDingtalkCode.getDdUserInfo.currentOpenIdurl，结果：{}", currentOpenIdurl);
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(currentOpenIdurl, String.class);
        log.info("GetDingtalkCode.getDdUserInfo.result，结果：{}", result);
        JSONObject obj = JSONObject.parseObject(result);
        String errcode = obj.getString("errcode");
        log.info("GetDingtalkCode.getDdUserInfo.result.obj，结果：{}", obj);
        log.info("GetDingtalkCode.getDdUserInfo.result.errcode，结果：{}", errcode);
        if (!"0".equals(errcode)) {
            throw new BaseException(obj.getString("errmsg"));
        }
        return obj;
    }

    /**
     * 获取钉钉用户信息
     *
     * @return
     */
    public static JSONObject getDdUserInfo(String code) {
        String accessToken = getDdAccessToken();
        String currentOpenIdurl = GetDingtalkCode.getDdapiUserInfoUrl(accessToken, code);
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(currentOpenIdurl, String.class);
        JSONObject obj = JSONObject.parseObject(result);
        String errcode = obj.getString("errcode");
        if (!"0".equals(errcode)) {
            throw new BaseException(obj.getString("errmsg"));
        }
        return obj;
    }

    public static String getDdAuthAccessTokenDirectly(SysUser user) {
        String url = getDdapiAccessTokenUrl(user.getDingtalkAppkey(), user.getDingtalkAppsecret());
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(url, String.class);
        JSONObject obj = JSONObject.parseObject(result);
        logger.info("解析code返回的obj：" + obj);
        String errcode = (String) obj.getString("errcode");
        if (!errcode.equals("0")) {
            String errmsg = (String) obj.get("errmsg");
            throw new BaseException("获取token失败:" + errmsg);
        }
        return (String) obj.get("access_token");
    }
}
