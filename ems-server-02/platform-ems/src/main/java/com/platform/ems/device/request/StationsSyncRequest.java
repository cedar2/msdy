package com.platform.ems.device.request;

import lombok.Data;

import java.util.List;

/**
 * @author Straw
 * @since 2023/3/31
 */
@Data
public class StationsSyncRequest {

    String company_no;
    String factory_no;

    List<String> station_nos;
}
