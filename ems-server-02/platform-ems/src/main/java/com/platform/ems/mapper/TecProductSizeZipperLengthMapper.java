package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.BasSku;
import com.platform.ems.domain.TecBomItem;
import com.platform.ems.domain.dto.response.EstimateLineReportResponse;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.TecProductSizeZipperLength;

/**
 * 商品尺码拉链长度明细Mapper接口
 * 
 * @author c
 * @date 2021-08-03
 */
public interface TecProductSizeZipperLengthMapper  extends BaseMapper<TecProductSizeZipperLength> {


    TecProductSizeZipperLength selectTecProductSizeZipperLengthById(Long productZipperSid);

    List<TecProductSizeZipperLength> selectTecProductSizeZipperLengthList(TecProductSizeZipperLength tecProductSizeZipperLength);

    /**
     * 添加多个
     * @param list List TecProductSizeZipperLength
     * @return int
     */
    int inserts(@Param("list") List<TecProductSizeZipperLength> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity TecProductSizeZipperLength
    * @return int
    */
    int updateAllById(TecProductSizeZipperLength entity);

    /**
     * 更新多个
     * @param list List TecProductSizeZipperLength
     * @return int
     */
    int updatesAllById(@Param("list") List<TecProductSizeZipperLength> list);

    /**
     * 通过商品订单编码 sku2 信息 以及对应bom 的物料获取 拉链尺码
     */
    BasSku  getZipperSku2(TecBomItem ecBomItem);

    BasSku  getZipperEstSku2(EstimateLineReportResponse estimateLineReportResponse);
}
