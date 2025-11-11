package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDocTypePurchaseRequire;

/**
 * 单据类型_申购单Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConDocTypePurchaseRequireMapper  extends BaseMapper<ConDocTypePurchaseRequire> {


    ConDocTypePurchaseRequire selectConDocTypePurchaseRequireById(Long sid);

    List<ConDocTypePurchaseRequire> selectConDocTypePurchaseRequireList(ConDocTypePurchaseRequire conDocTypePurchaseRequire);

    /**
     * 添加多个
     * @param list List ConDocTypePurchaseRequire
     * @return int
     */
    int inserts(@Param("list") List<ConDocTypePurchaseRequire> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConDocTypePurchaseRequire
    * @return int
    */
    int updateAllById(ConDocTypePurchaseRequire entity);

    /**
     * 更新多个
     * @param list List ConDocTypePurchaseRequire
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDocTypePurchaseRequire> list);


}
