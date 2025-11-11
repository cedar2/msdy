package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDiscountType;

/**
 * 折扣类型Mapper接口
 *
 * @author linhongwei
 * @date 2021-05-20
 */
public interface ConDiscountTypeMapper  extends BaseMapper<ConDiscountType> {


    ConDiscountType selectConDiscountTypeById(Long sid);

    List<ConDiscountType> selectConDiscountTypeList(ConDiscountType conDiscountType);

    /**
     * 添加多个
     * @param list List ConDiscountType
     * @return int
     */
    int inserts(@Param("list") List<ConDiscountType> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConDiscountType
    * @return int
    */
    int updateAllById(ConDiscountType entity);

    /**
     * 更新多个
     * @param list List ConDiscountType
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDiscountType> list);

    /** 获取下拉列表 */
    List<ConDiscountType> getConDiscountTypeList();
}
