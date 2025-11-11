package com.platform.ems.domain.dto.request;

import com.platform.ems.domain.TecBomItemReport;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * 物料需求测算报表-跳转出库
 *
 * @author yangqz
 * @date 2021-05-26
 */
@Data
@ApiModel
public class TecBomItemReportRequest {

   private List<TecBomItemReport> orderList;

   private  String movementType;

}
