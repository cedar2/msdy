package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.plug.domain.ConDocCategoryInventoryDocument;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConReasonTypeStorage;

/**
 * 原因类型(库存管理)Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-20
 */
public interface ConReasonTypeStorageMapper  extends BaseMapper<ConReasonTypeStorage> {


    ConReasonTypeStorage selectConReasonTypeStorageById(Long sid);

    List<ConReasonTypeStorage> selectConReasonTypeStorageList(ConReasonTypeStorage conReasonTypeStorage);
    List<ConReasonTypeStorage> getList();
    /**
     * 添加多个
     * @param list List ConReasonTypeStorage
     * @return int
     */
    int inserts(@Param("list") List<ConReasonTypeStorage> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConReasonTypeStorage
    * @return int
    */
    int updateAllById(ConReasonTypeStorage entity);

    /**
     * 更新多个
     * @param list List ConReasonTypeStorage
     * @return int
     */
    int updatesAllById(@Param("list") List<ConReasonTypeStorage> list);


}
