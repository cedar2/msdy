package com.platform.ems.device.api;

import com.platform.ems.device.request.ManSchedulingInfoSyncRequest;
import com.platform.ems.device.request.StationsSyncRequest;
import com.platform.ems.device.response.DeviceResp;
import com.platform.ems.device.request.StaffSyncRequest;
import com.platform.ems.domain.BasStaff;
import com.platform.ems.domain.ManWorkstation;
import com.platform.ems.service.impl.BasStaffServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static com.platform.ems.device.api.DeviceAPI.*;

/**
 * @author Straw
 * @since 2023/3/22
 */

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Service
@Slf4j(topic = "物联设备推送")
public class DevicePushService {

    @Autowired
    BasStaffServiceImpl staffService;

    @Autowired
    DeviceTokenService tokenService;

    /**
     * 推送员工档案时，需要分组，[公司code+工厂code]相同的为一组。
     * 每次只能推送一组
     */
    public String staffSync(HashMap<String[], List<BasStaff>> basStaffGroup) throws Exception {
        return pushSync(API_STAFF_SYNC, staffToJson(basStaffGroup), "员工档案");
    }


    public String stationsSync(HashMap<String[], List<ManWorkstation>> basStaffGroup) throws Exception {
        return pushSync(API_STATIONS_SYNC, stationsToJson(basStaffGroup), "工位档案");
    }

    public String schedulingSync(List<ManSchedulingInfoSyncRequest> requestList) throws Exception {
        List<String> param = new ArrayList<>();
        HashMap<String, List<ManSchedulingInfoSyncRequest>> map = new HashMap<>();
        map.put("production_info", requestList);
        param.add(G.toJson(map));
        return pushSync(API_SCHEDULING_SYNC, param, "生产排程同步接口");
    }

    /**
     * 推送物联设备档案的统一请求入口
     *
     * @param api          请求的接口路径
     * @param postJsonList 推送的请求体参数列表
     * @param topic        推送的信息，debug用
     */
    private String pushSync(String api, List<String> postJsonList, String topic) throws Exception {
        // joiner是用来收集所有响应的请求体，作为返回值给前端
        StringJoiner joiner = new StringJoiner("\n");

        // 遍历分组，以此推送
        for (String json : postJsonList) {
            log.info("推送【" + topic + "】，请求参数: {}", json);
            DeviceResp resp = DeviceAPI.post(json, this.tokenService.getTokenOfLoginClient(), api);

            resp.requireSuccess(); // 判断响应成功，如果响应失败了，抛出异常

            String respBody = resp.getBody();
            log.info("推送【" + topic + "】，响应内容: {}", respBody);
            joiner.add(respBody);
        }
        return joiner.toString();
    }

    private List<String> staffToJson(HashMap<String[], List<BasStaff>> map) {
        List<String> ret = new ArrayList<>();
        map.forEach((strArr, basStaffs) -> {
            StaffSyncRequest request = new StaffSyncRequest();
            List<StaffSyncRequest.Staff> staffs = basStaffs.stream()
                                                           .map(StaffSyncRequest.Staff::wrap)
                                                           .collect(Collectors.toList());
            request.setStaffs(staffs);
            request.setCompany_no(strArr[0]);
            request.setFactory_no(strArr[1]);
            ret.add(G.toJson(request));
        });
        return ret;
    }

    private List<String> stationsToJson(HashMap<String[], List<ManWorkstation>> map) {
        List<String> ret = new ArrayList<>();
        map.forEach((strArr, stationList) -> {
            List<String> station_nos = stationList.stream()
                                                  .map(ManWorkstation::getWorkstationCode)
                                                  .collect(Collectors.toList());
            StationsSyncRequest request = new StationsSyncRequest();
            request.setCompany_no(strArr[0]);
            request.setFactory_no(strArr[1]);
            request.setStation_nos(station_nos);
            ret.add(G.toJson(request));
        });
        return ret;
    }
}
