package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.RepPurchaseStatusOutsourceProcess;

/**
 * 采购状况-外发加工结算Mapper接口
 *
 * @author linhongwei
 * @date 2022-02-25
 */
public interface RepPurchaseStatusOutsourceProcessMapper extends BaseMapper<RepPurchaseStatusOutsourceProcess> {


    RepPurchaseStatusOutsourceProcess selectRepPurchaseStatusOutsourceProcessById(Long dataRecordSid);

    List<RepPurchaseStatusOutsourceProcess> selectRepPurchaseStatusOutsourceProcessList(RepPurchaseStatusOutsourceProcess repPurchaseStatusOutsourceProcess);

    /**
     * 添加多个
     *
     * @param list List RepPurchaseStatusOutsourceProcess
     * @return int
     */
    int inserts(@Param("list") List<RepPurchaseStatusOutsourceProcess> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity RepPurchaseStatusOutsourceProcess
     * @return int
     */
    int updateAllById(RepPurchaseStatusOutsourceProcess entity);

    /**
     * 更新多个
     *
     * @param list List RepPurchaseStatusOutsourceProcess
     * @return int
     */
    int updatesAllById(@Param("list") List<RepPurchaseStatusOutsourceProcess> list);


}
