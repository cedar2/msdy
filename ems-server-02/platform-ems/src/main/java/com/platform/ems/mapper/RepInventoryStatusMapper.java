package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.RepInventoryStatus;
import org.apache.ibatis.annotations.Update;

/**
 * 库存状况Mapper接口
 *
 * @author linhongwei
 * @date 2022-02-25
 */
public interface RepInventoryStatusMapper extends BaseMapper<RepInventoryStatus> {


    //清空指定表
    @Update("truncate table s_rep_inventory_status")
    void deleteAll();

    RepInventoryStatus selectRepInventoryStatusById(Long dataRecordSid);

    List<RepInventoryStatus> selectRepInventoryStatusList(RepInventoryStatus repInventoryStatus);

    /**
     * 添加多个
     *
     * @param list List RepInventoryStatus
     * @return int
     */
    int inserts(@Param("list") List<RepInventoryStatus> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity RepInventoryStatus
     * @return int
     */
    int updateAllById(RepInventoryStatus entity);

    /**
     * 更新多个
     *
     * @param list List RepInventoryStatus
     * @return int
     */
    int updatesAllById(@Param("list") List<RepInventoryStatus> list);


}
