package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.RepFinanceStatusDaixys;

/**
 * 财务状况-客户-待销已收Mapper接口
 *
 * @author chenkw
 * @date 2022-02-25
 */
public interface RepFinanceStatusDaixysMapper extends BaseMapper<RepFinanceStatusDaixys> {

    RepFinanceStatusDaixys selectRepFinanceStatusDaixysById(Long dataRecordSid);

    List<RepFinanceStatusDaixys> selectRepFinanceStatusDaixysList(RepFinanceStatusDaixys repFinanceStatusDaixys);

    /**
     * 从收款流水数据库表获取数据出来
     *
     * @param repFinanceStatusDaixys
     * @return int
     */
    List<RepFinanceStatusDaixys> getRepFinanceStatusDaixysList(RepFinanceStatusDaixys repFinanceStatusDaixys);

    /**
     * 添加多个
     *
     * @param list List RepFinanceStatusDaixys
     * @return int
     */
    int inserts(@Param("list") List<RepFinanceStatusDaixys> list);

}
