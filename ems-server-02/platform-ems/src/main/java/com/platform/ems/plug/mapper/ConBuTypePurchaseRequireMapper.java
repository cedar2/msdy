package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConBuTypePurchaseRequire;

/**
 * 业务类型_申购单Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConBuTypePurchaseRequireMapper  extends BaseMapper<ConBuTypePurchaseRequire> {


    ConBuTypePurchaseRequire selectConBuTypePurchaseRequireById(Long sid);

    List<ConBuTypePurchaseRequire> selectConBuTypePurchaseRequireList(ConBuTypePurchaseRequire conBuTypePurchaseRequire);

    /**
     * 添加多个
     * @param list List ConBuTypePurchaseRequire
     * @return int
     */
    int inserts(@Param("list") List<ConBuTypePurchaseRequire> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConBuTypePurchaseRequire
    * @return int
    */
    int updateAllById(ConBuTypePurchaseRequire entity);

    /**
     * 更新多个
     * @param list List ConBuTypePurchaseRequire
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBuTypePurchaseRequire> list);


}
