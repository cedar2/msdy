package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.SamSampleLendreturn;
import com.platform.ems.domain.dto.response.SamSampleLendreturnReportResponse;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.SamSampleLendreturnItem;

/**
 * 样品借还单-明细Mapper接口
 * 
 * @author linhongwei
 * @date 2021-12-20
 */
public interface SamSampleLendreturnItemMapper  extends BaseMapper<SamSampleLendreturnItem> {


    List<SamSampleLendreturnItem> selectSamSampleLendreturnItemById(Long lendreturnSid);

    List<SamSampleLendreturnItem> getItemHandle(SamSampleLendreturnItem samSampleLendreturnItem);

    List<SamSampleLendreturnReportResponse> reportList(SamSampleLendreturn samSampleLendreturn);

    List<SamSampleLendreturnItem> selectSamSampleLendreturnItemList(SamSampleLendreturnItem samSampleLendreturnItem);

    /**
     * 添加多个
     * @param list List SamSampleLendreturnItem
     * @return int
     */
    int inserts(@Param("list") List<SamSampleLendreturnItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity SamSampleLendreturnItem
    * @return int
    */
    int updateAllById(SamSampleLendreturnItem entity);

    /**
     * 更新多个
     * @param list List SamSampleLendreturnItem
     * @return int
     */
    int updatesAllById(@Param("list") List<SamSampleLendreturnItem> list);


}
