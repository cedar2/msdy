package com.platform.ems.device.request;

import com.platform.ems.domain.BasStaff;
import lombok.Data;

import java.util.List;

/**
 * @author Straw
 * @since 2023/3/22
 */
@Data
public class StaffSyncRequest {

    String company_no;
    String factory_no;
    List<Staff> staffs;

    @Data
    public static class Staff {
        String name;
        String card_no;

        public static Staff wrap(BasStaff basStaff) {
            Staff staff = new Staff();
            staff.setCard_no(basStaff.getStaffCode());
            staff.setName(basStaff.getStaffName());
            return staff;
        }
    }
}
