package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.RepFinanceStatusDaiys;

/**
 * 财务状况-客户-待预收Mapper接口
 *
 * @author chenkw
 * @date 2022-02-25
 */
public interface RepFinanceStatusDaiysMapper extends BaseMapper<RepFinanceStatusDaiys> {


    RepFinanceStatusDaiys selectRepFinanceStatusDaiysById(Long dataRecordSid);

    List<RepFinanceStatusDaiys> selectRepFinanceStatusDaiysList(RepFinanceStatusDaiys repFinanceStatusDaiys);

    /**
     * 从预收台账数据库表获取数据出来
     *
     * @param repFinanceStatusDaiys
     * @return int
     */
    List<RepFinanceStatusDaiys> getRepFinanceStatusDaiysList(RepFinanceStatusDaiys repFinanceStatusDaiys);

    /**
     * 添加多个
     *
     * @param list List RepFinanceStatusDaiys
     * @return int
     */
    int inserts(@Param("list") List<RepFinanceStatusDaiys> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity RepFinanceStatusDaiys
     * @return int
     */
    int updateAllById(RepFinanceStatusDaiys entity);

    /**
     * 更新多个
     *
     * @param list List RepFinanceStatusDaiys
     * @return int
     */
    int updatesAllById(@Param("list") List<RepFinanceStatusDaiys> list);


}
