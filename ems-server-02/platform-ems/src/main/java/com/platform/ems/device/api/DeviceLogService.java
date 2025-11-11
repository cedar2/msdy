package com.platform.ems.device.api;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.device.log.domain.ManSchedulingInfoLog;
import com.platform.ems.device.log.domain.SysLogStaff;
import com.platform.ems.device.log.domain.SysLogWorkstation;
import com.platform.ems.device.log.service.ManSchedulingInfoLogService;
import com.platform.ems.device.log.service.SysLogStaffService;
import com.platform.ems.device.log.service.SysLogWorkstationService;
import com.platform.ems.device.request.ManSchedulingInfoSyncRequest;
import com.platform.ems.domain.BasStaff;
import com.platform.ems.domain.ManWorkstation;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

/**
 * @author Straw
 * @since 2023/3/22
 */
@Service
public class DeviceLogService {
    /**
     * @param <E>              实体类
     * @param <LE>             日志实体类
     * @param serviceImplClass 日志实体类的ServiceImpl
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static <E, LE> void createLogEntityAndSaveBatch(HashMap<String[], List<E>> map,
                                                            Class<? extends ServiceImpl> serviceImplClass,
                                                            Function<E, LE> copyMethod
    ) {
        ArrayList<LE> list = new ArrayList<>();

        map.forEach((strArr, basStaffs) -> {
            for (E staff : basStaffs) {
                LE logStaff = copyMethod.apply(staff);
                list.add(logStaff);
            }
        });

        ofService(serviceImplClass).saveBatch(list);
    }

    private static <T> T ofService(Class<T> clazz) {
        return SpringUtil.getBean(clazz);
    }

    public void logStaffSync(HashMap<String[], List<BasStaff>> map) {
        createLogEntityAndSaveBatch(map, SysLogStaffService.class, this::copyLogStaff);
    }

    public void logStationsSync(HashMap<String[], List<ManWorkstation>> map) {
        createLogEntityAndSaveBatch(map, SysLogWorkstationService.class, this::copyLogStations);
    }

    public void logScheduling(List<ManSchedulingInfoSyncRequest> requestList) {
        List<ManSchedulingInfoLog> logs = BeanCopyUtils.copyListProperties(requestList, ManSchedulingInfoLog::new);
        ofService(ManSchedulingInfoLogService.class).saveBatch(logs);
    }

    private SysLogWorkstation copyLogStations(ManWorkstation entity) {
        SysLogWorkstation logWorkstation = new SysLogWorkstation();
        BeanUtils.copyProperties(entity, logWorkstation);
        return logWorkstation;
    }

    private SysLogStaff copyLogStaff(BasStaff staff) {
        SysLogStaff logStaff = new SysLogStaff();
        logStaff.setDefaultCompanyCode(staff.getCompanyCode());
        logStaff.setDefaultPlantCode(staff.getPlantCode());
        BeanUtils.copyProperties(staff, logStaff);
        return logStaff;
    }
}
