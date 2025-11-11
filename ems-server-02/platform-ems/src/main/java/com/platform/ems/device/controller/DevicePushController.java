package com.platform.ems.device.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.platform.common.exception.CheckedException;
import com.platform.ems.device.api.CompanyFactoryInfo;
import com.platform.ems.device.api.DeviceLogService;
import com.platform.ems.device.api.DevicePushService;
import com.platform.ems.device.api.DeviceTokenService;
import com.platform.ems.device.request.ManSchedulingInfoSyncRequest;
import com.platform.ems.domain.BasStaff;
import com.platform.ems.domain.ManSchedulingInfo;
import com.platform.ems.domain.ManWorkstation;
import com.platform.ems.service.impl.BasStaffServiceImpl;
import com.platform.ems.service.impl.ManWorkstationServiceImpl;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Straw
 * @since 2023/3/22
 */
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@RestController
@RequestMapping("/device/push")
@Api(tags = "物联设备")
@Slf4j
public class DevicePushController {

    @Autowired
    DeviceTokenService tokenService;

    @Autowired
    DevicePushService pushService;

    @Autowired
    DeviceLogService logService;

    /**
     * 按公司code+工厂code分组
     */
    static <T extends CompanyFactoryInfo> HashMap<String[], List<T>> groupByCompanyFactoryCode(List<T> basStaffList) {
        if (basStaffList.isEmpty()) {
            throw new CheckedException("不能推送空的员工列表");
        }
        HashMap<String[], List<T>> map = new HashMap<>();

        for (T staff : basStaffList) {
            if (StrUtil.isEmpty(staff.getCompanyCode()))
                throw new CheckedException(String.format("[%s] 未配置公司信息，公司的code为空", staff.getName()));
            if (StrUtil.isEmpty(staff.getPlantCode())) {
                throw new CheckedException(String.format("[%s] 未配置工厂信息，工厂的code为空", staff.getName()));
            }

            String[] key = new String[]{staff.getCompanyCode(), staff.getPlantCode()};
            map.computeIfAbsent(key, (ignore) -> new ArrayList<>()).add(staff);
        }
        return map;
    }

    static <T> T ofService(Class<T> clazz) {
        return SpringUtil.getBean(clazz);
    }

    @PostMapping("/staff")
    public Object pushStaff(@RequestBody BasStaff basStaff) throws Exception {
        // 根据sid查实体类
        List<BasStaff> basStaffList = ofService(BasStaffServiceImpl.class).selectBasStaffList(basStaff);

        // 分组
        HashMap<String[], List<BasStaff>> group = groupByCompanyFactoryCode(basStaffList);

        // 推送
        String apiResponse = pushService.staffSync(group);

        // 记录日志
        tryLog((logService) -> logService.logStaffSync(group));

        return apiResponse;
    }

    private void tryLog(Consumer<DeviceLogService> consumer) {
        try {
            consumer.accept(this.logService);
        } catch (Exception e) {
            throw new CheckedException("员工信息推送成功，系统记录日志失败:" + e, e);
        }
    }

    @PostMapping("/stations")
    public Object pushStations(@RequestBody ManWorkstation station) throws Exception {
        // 根据sid查实体类
        List<ManWorkstation> stationList = ofService(ManWorkstationServiceImpl.class).selectManWorkstationList(station);

        // 分组
        HashMap<String[], List<ManWorkstation>> group = groupByCompanyFactoryCode(stationList);

        // 推送
        String apiResponse = pushService.stationsSync(group);

        // 记录日志
        tryLog((logService) -> logService.logStationsSync(group));

        return apiResponse;
    }

    @PostMapping("/scheduling")
    public Object pushScheduling(@RequestBody List<ManSchedulingInfo> infoList) throws Exception {
        List<ManSchedulingInfoSyncRequest> requestList = infoList.stream()
                .map(ManSchedulingInfoSyncRequest::wrap)
                .collect(Collectors.toList());

        // 推送
        String apiResponse = pushService.schedulingSync(requestList);

        // 记录日志
        tryLog((logService) -> logService.logScheduling(requestList));

        return apiResponse;
    }

    @GetMapping("/getToken")
    public Object getToken() {
        return tokenService.getTokenOfLoginClient();
    }

}
