package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConPurchaseType;

/**
 * 采购类型Mapper接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConPurchaseTypeMapper  extends BaseMapper<ConPurchaseType> {


    ConPurchaseType selectConPurchaseTypeById(Long sid);

    List<ConPurchaseType> selectConPurchaseTypeList(ConPurchaseType conPurchaseType);

    /**
     * 添加多个
     * @param list List ConPurchaseType
     * @return int
     */
    int inserts(@Param("list") List<ConPurchaseType> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConPurchaseType
    * @return int
    */
    int updateAllById(ConPurchaseType entity);

    /**
     * 更新多个
     * @param list List ConPurchaseType
     * @return int
     */
    int updatesAllById(@Param("list") List<ConPurchaseType> list);

    /** 获取下拉列表 */
    List<ConPurchaseType> getConPurchaseTypeList();

    List<ConPurchaseType> getList(ConPurchaseType conPurchaseType);
}
