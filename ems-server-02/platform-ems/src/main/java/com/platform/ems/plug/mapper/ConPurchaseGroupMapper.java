package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConPurchaseGroup;

/**
 * 采购组Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConPurchaseGroupMapper  extends BaseMapper<ConPurchaseGroup> {


    ConPurchaseGroup selectConPurchaseGroupById(Long sid);

    List<ConPurchaseGroup> selectConPurchaseGroupList(ConPurchaseGroup conPurchaseGroup);

    /**
     * 添加多个
     * @param list List ConPurchaseGroup
     * @return int
     */
    int inserts(@Param("list") List<ConPurchaseGroup> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConPurchaseGroup
    * @return int
    */
    int updateAllById(ConPurchaseGroup entity);

    /**
     * 更新多个
     * @param list List ConPurchaseGroup
     * @return int
     */
    int updatesAllById(@Param("list") List<ConPurchaseGroup> list);


    /**
     * 采购组下拉框
     */
    List<ConPurchaseGroup> getList();

    /**
     * 采购组下拉框
     */
    List<ConPurchaseGroup> getPurchaseGroupList(ConPurchaseGroup entity);
}
