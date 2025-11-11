package com.platform.system.qywx.service;

import com.alibaba.fastjson.JSON;
import com.platform.system.qywx.TokenCache;
import com.platform.system.qywx.config.QywxConfig;
import com.platform.system.qywx.dto.AccessTokenDTO;
import com.platform.system.qywx.dto.UserInfoDTO;
import com.platform.system.qywx.util.HttpClientUtil;
import com.platform.system.qywx.util.JsonConvertUtil;
import com.platform.system.qywx.vo.AccessTokenVo;
import com.platform.system.qywx.vo.UserInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Objects;

@Slf4j
@Service
public class QywxServer {

    private static Logger logger = LoggerFactory.getLogger(QywxServer.class);


    @Resource
    private QywxConfig qywxConfig;

    @Resource
    private HttpClientUtil httpClientUtil;

    private TokenCache tokenCache;

    private final int OVERDUE = 42001;

    /**
     * 获取访问用户身份
     *
     * @param vo
     * @return
     */
    public UserInfoDTO getUserInfo(UserInfoVo vo) {
        if (vo == null) {
            vo = new UserInfoVo();
        }
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        try {
            StringBuffer urlParam = new StringBuffer();
            urlParam.append(qywxConfig.getUserInfoApi());
            vo.setAccess_token(getToken());

            classToUrlParam(vo, UserInfoVo.class, urlParam);
            String result = httpClientUtil.get(urlParam.toString());
            if (StringUtils.isNotEmpty(result)) {
                if (JSON.parseObject(result).getIntValue("errcode") == OVERDUE) {
                    logger.error("getUserInfo企业微信Token失效：{}", vo.toString());
                    tokenCache = null;
                    getUserInfo(vo);
                }
                userInfoDTO = JsonConvertUtil.jsonToObject(result, UserInfoDTO.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userInfoDTO;

    }


    /**
     * 企业微信获取Token
     *
     * @return
     */
    public String getToken() {
        try {
            if (tokenCache != null && tokenCache.isOverdue() && StringUtils.isNotEmpty(tokenCache.getToken())) {
                return tokenCache.getToken();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accessToken();
    }


    private String accessToken() {
        AccessTokenDTO accessTokenDTO;
        if (tokenCache != null && tokenCache.isOverdue() && StringUtils.isNotEmpty(tokenCache.getToken())) {
            return tokenCache.getToken();
        }

        AccessTokenVo accessTokenVo = new AccessTokenVo();
        accessTokenVo.setCorpid(qywxConfig.getAppId());
        accessTokenVo.setCorpsecret(qywxConfig.getSecret());

        StringBuffer urlParam = new StringBuffer();
        urlParam.append(qywxConfig.getTokenApi());
        classToUrlParam(accessTokenVo, AccessTokenVo.class, urlParam);

        String result = httpClientUtil.get(urlParam.toString());

        log.info("QywxServer.accessToken.result，结果：{}，参数：{}，地址：{}", result, accessTokenVo, urlParam);

        if (StringUtils.isNotEmpty(result)) {
            accessTokenDTO = JsonConvertUtil.jsonToObject(result, AccessTokenDTO.class);
            if (accessTokenDTO == null || StringUtils.isEmpty(accessTokenDTO.getAccess_token())) {
                return null;
            }
            tokenCache = new TokenCache(accessTokenDTO.getAccess_token(), accessTokenDTO.getExpires_in());
        }
        return tokenCache.getToken();
    }


    private <T> void classToUrlParam(T t, Class clazz, StringBuffer urlParam) {
        if (!clazz.equals(Object.class)) {
            urlParam.append("?");
            Arrays.stream(clazz.getDeclaredFields()).forEach(field -> {
                field.setAccessible(true);
                try {
                    Object value = field.get(t);
                    if (Objects.nonNull(value)) {
                        urlParam.append(field.getName()).append("=").append(value).append("&");
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
            urlParam.deleteCharAt(urlParam.length() - 1);
        }
    }

}

