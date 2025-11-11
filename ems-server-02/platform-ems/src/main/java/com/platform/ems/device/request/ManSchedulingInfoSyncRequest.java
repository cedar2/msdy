package com.platform.ems.device.request;

import com.platform.ems.domain.ManSchedulingInfo;
import lombok.Data;

/**
 * 生产排程信息推送对象
 *
 * @author chenkw
 * @date 2023-06-02
 */
@Data
public class ManSchedulingInfoSyncRequest {

    private Long manufacture_schedule_sid;

    private String product_code;

    private String product_name;

    private String sku1_code;

    private String sku1_name;

    private String sku2_code;

    private String sku2_name;

    private String process_step_code;

    private String process_step_name;

    private String station_no;

    private String company_no;

    private String factory_no;

    private Integer program_output;

    public static ManSchedulingInfoSyncRequest wrap(ManSchedulingInfo schedulingInfo) {
        ManSchedulingInfoSyncRequest request = new ManSchedulingInfoSyncRequest();
        request.setManufacture_schedule_sid(schedulingInfo.getSchedulingInfoSid());
        request.setProduct_code(schedulingInfo.getProductCode());
        request.setProduct_name(schedulingInfo.getProductName());
        request.setSku1_code(schedulingInfo.getSku1Code());
        request.setSku1_name(schedulingInfo.getSku1Name());
        request.setSku2_code(schedulingInfo.getSku2Code());
        request.setSku2_name(schedulingInfo.getSku2Name());
        request.setProcess_step_code(schedulingInfo.getProcessStepCode());
        request.setProcess_step_name(schedulingInfo.getProcessStepName());
        request.setStation_no(schedulingInfo.getWorkstationCode());
        request.setCompany_no(schedulingInfo.getCompanyCode());
        request.setFactory_no(schedulingInfo.getPlantCode());
        request.setProgram_output(schedulingInfo.getFenpeiQuantity());
        return request;
    }

}
