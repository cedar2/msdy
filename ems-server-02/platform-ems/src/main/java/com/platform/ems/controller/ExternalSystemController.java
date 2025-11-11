package com.platform.ems.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.platform.common.constant.CacheConstants;
import com.platform.common.exception.CheckedException;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.core.redis.RedisCache;
import com.platform.ems.domain.dto.request.PurPurchaseOrderHandleRequest;
import com.platform.ems.domain.dto.response.DelDeliveryNoteOutResponse;
import com.platform.ems.domain.dto.response.PurPurchaseOrderOutResponse;
import com.platform.ems.domain.dto.response.external.BasMaterialBarcodeExternal;
import com.platform.ems.domain.dto.response.external.BasMaterialCertificateExternal;
import com.platform.ems.service.IBasMaterialBarcodeService;
import com.platform.ems.service.IBasMaterialCertificateService;
import com.platform.ems.service.IDelDeliveryNoteService;
import com.platform.ems.service.IPurPurchaseOrderService;
import com.platform.common.core.domain.entity.SysRole;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.core.domain.model.LoginUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 外部系统接口
 *  @author yangqz
 */
@Slf4j
@RestController
@RequestMapping("/external")
@Api(tags = "外部系统访问接口")
public class ExternalSystemController extends BaseController {
    @Autowired
    private IPurPurchaseOrderService purPurchaseOrderService;
    @Autowired
    private IDelDeliveryNoteService delDeliveryNoteService;
    @Autowired
    private IBasMaterialCertificateService basMaterialCertificateService;
    @Autowired
    private IBasMaterialBarcodeService basMaterialBarcodeService;

    @Autowired
    private RedisCache redisService;
    @Autowired
    public RedisTemplate redisTemplate;

    @ApiOperation(value = "云智算“用户+密码”登录获取token", notes = "云智算“用户+密码”登录获取token")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/api/yunzhisuan/getUserLogin")
    public AjaxResult yunzhisuanUserLogin(@RequestBody HashMap<String, String> request) {
        if (request == null){
            return AjaxResult.error("参数缺失");
        }
        SysUser sysUser = null;
        if (request.get("userName") != null && request.get("password") != null) {
            String ownerKey = CacheConstants.LOGIN_TOKEN_KEY + "*";
            Set<String> keys = redisTemplate.keys(ownerKey);
            //  遍历拿到值集
            for (String key : keys) {
                LoginUser loginUser = JSON.parseObject(JSON.toJSONString(redisTemplate.opsForValue().get(key)), LoginUser.class);
                if (request.get("userName").equals(loginUser.getUsername())) {
                    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    boolean matches = passwordEncoder.matches(request.get("password"), loginUser.getSysUser().getPassword());
                    if (matches) {
                        sysUser = loginUser.getSysUser();
                        break;
                    }
                }
            }
        }
        return AjaxResult.success(sysUser);
    }

    @ApiOperation(value = "云智算“用户+token”单点登录接口", notes = "云智算“用户+token”单点登录接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @GetMapping("/api/yunzhisuan/ssologin")
    public AjaxResult yunzhisuanSsoLogin(@RequestHeader HttpHeaders headers) {
        String accessToken = "accesstoken";
        SysUser sysUser = null;
        for(String key : headers.keySet()) {
            if (key.equals(accessToken)) {
                String tokenValue = String.valueOf(headers.get(key).get(0));
                String keyName = CacheConstants.LOGIN_TOKEN_KEY + tokenValue;
                LoginUser loginUser = JSON.parseObject(JSON.toJSONString(redisTemplate.opsForValue().get(keyName)), LoginUser.class);
                sysUser = loginUser.getSysUser();
                sysUser.setAccessToken(tokenValue);
            }
        }
        return AjaxResult.success(sysUser);
    }

    @ApiOperation(value = "云智算获取用户的角色集合接口", notes = "云智算获取用户的角色集合接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @GetMapping("/api/yunzhisuan/role")
    public AjaxResult yunzhisuanRole(@RequestHeader HttpHeaders headers, Long userID) {
        String accessToken = "accesstoken";
        SysUser sysUser = null;
        for(String key : headers.keySet()) {
            if (key.equals(accessToken)) {
                String tokenValue = String.valueOf(headers.get(key).get(0));
                String keyName = CacheConstants.LOGIN_TOKEN_KEY + tokenValue;
                LoginUser loginUser = JSON.parseObject(JSON.toJSONString(redisTemplate.opsForValue().get(keyName)), LoginUser.class);
                sysUser = loginUser.getSysUser();
                sysUser.setAccessToken(tokenValue);
            }
        }
        if (sysUser != null) {
            List<SysRole> roleList = sysUser.getRoles();
            if (roleList != null && roleList.size() != 0) {
                roleList = roleList.stream().filter(o->o.getRoleKey().startsWith("Visual_")).collect(Collectors.toList());
                List<Map<String, String>> roles = new ArrayList<>();
                if (CollectionUtil.isNotEmpty(roleList)) {
                    for (SysRole sysRole : roleList) {
                        Map<String, String> base = new HashMap<>();
                        base.put("roleId", sysRole.getRoleKey());
                        base.put("roleName", sysRole.getRoleName());
                        roles.add(base);
                    }
                }
                return AjaxResult.success(roles);
            }
        }
        return AjaxResult.success();
    }

    /**
     * SCM 发送面辅料档案信息给打印系统
     * 实际查询的是面辅料档案的商品条码
     */
    @Log(title = "SCM 发送面辅料档案信息给打印系统", businessType = BusinessType.QUERY)
    @ApiOperation(value = "SCM 发送面辅料档案信息给打印系统", notes = "SCM 发送面辅料档案信息给打印系统")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterialBarcodeExternal.class))
    @PostMapping("/material/getList")
    public AjaxResult getMaterialBarcodeInfoToExternal(Long materialSid) {
        if (materialSid == null){
            return AjaxResult.error("参数缺失");
        }
        return AjaxResult.success(basMaterialBarcodeService.selectForExternalById(materialSid));
    }

    /**
     * SCM 发送商品及其合格证洗唛字段信息给打印系统
     */
    @Log(title = "SCM 发送商品及其合格证洗唛字段信息给打印系统", businessType = BusinessType.QUERY)
    @ApiOperation(value = "SCM 发送商品及其合格证洗唛字段信息给打印系统", notes = "SCM 发送商品及其合格证洗唛字段信息给打印系统")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterialCertificateExternal.class))
    @PostMapping("/material/certificate/getInfo")
    public AjaxResult getInfoToExternal(Long materialCertificateSid) {
        if (materialCertificateSid == null){
            return AjaxResult.error("参数缺失");
        }
        return AjaxResult.success(basMaterialCertificateService.selectForExternalById(materialCertificateSid));
    }

    @ApiOperation(value = "外部接口-推送采购订单详细信息", notes = "外部接口-推送采购订单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseOrderOutResponse.class))
    @PostMapping("/purchase/order/getInfo")
    public AjaxResult getInfoOut(Long purchaseOrderSid) {
        if (purchaseOrderSid == null) {
            throw new CheckedException("参数缺失");
        }
        PurPurchaseOrderOutResponse purPurchaseOrder = purPurchaseOrderService.getOutOrder(purchaseOrderSid);
        return AjaxResult.success(purPurchaseOrder);
    }


    @ApiOperation(value = "外部接口-修改采购订单处理状态", notes = "外部接口-修改采购订单处理状态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/purchase/order/edit/handle")
    public AjaxResult changeHandleOut(@RequestBody List<PurPurchaseOrderHandleRequest> list) {
        if (CollectionUtil.isEmpty(list)) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success( purPurchaseOrderService.changeHandleOut(list));
    }

    /**
     * 易码通获取交货单详细信息
     */
    @ApiOperation(value = "外部接口-通获取交货单详细信息", notes = "外部接口-通获取交货单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DelDeliveryNoteOutResponse.class))
    @PostMapping("/delivery/note/getInfo")
    public AjaxResult getInfoDeliveryOut(Long purchaseOrderSid) {
        if (purchaseOrderSid == null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(delDeliveryNoteService.getOutDelDeliveryNote(purchaseOrderSid));
    }

}
