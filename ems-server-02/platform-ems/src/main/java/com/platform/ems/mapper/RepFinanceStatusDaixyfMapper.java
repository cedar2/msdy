package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.RepFinanceStatusDaixyf;

/**
 * 财务状况-供应商-待销已付Mapper接口
 *
 * @author chenkw
 * @date 2022-02-25
 */
public interface RepFinanceStatusDaixyfMapper extends BaseMapper<RepFinanceStatusDaixyf> {

    RepFinanceStatusDaixyf selectRepFinanceStatusDaixyfById(Long dataRecordSid);

    List<RepFinanceStatusDaixyf> selectRepFinanceStatusDaixyfList(RepFinanceStatusDaixyf repFinanceStatusDaixyf);

    /**
     * 从付款流水数据库表获取数据出来
     *
     * @param repFinanceStatusDaixyf
     * @return int
     */
    List<RepFinanceStatusDaixyf> getRepFinanceStatusDaixyfList(RepFinanceStatusDaixyf repFinanceStatusDaixyf);

    /**
     * 添加多个
     *
     * @param list List RepFinanceStatusDaixyf
     * @return int
     */
    int inserts(@Param("list") List<RepFinanceStatusDaixyf> list);


}
