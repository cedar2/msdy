package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConAccountCategoryReceivableBill;

/**
 * 款项类别_收款单Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface ConAccountCategoryReceivableBillMapper  extends BaseMapper<ConAccountCategoryReceivableBill> {


    ConAccountCategoryReceivableBill selectConAccountCategoryReceivableBillById(Long sid);

    List<ConAccountCategoryReceivableBill> selectConAccountCategoryReceivableBillList(ConAccountCategoryReceivableBill conAccountCategoryReceivableBill);

    /**
     * 添加多个
     * @param list List ConAccountCategoryReceivableBill
     * @return int
     */
    int inserts(@Param("list") List<ConAccountCategoryReceivableBill> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConAccountCategoryReceivableBill
    * @return int
    */
    int updateAllById(ConAccountCategoryReceivableBill entity);

    /**
     * 更新多个
     * @param list List ConAccountCategoryReceivableBill
     * @return int
     */
    int updatesAllById(@Param("list") List<ConAccountCategoryReceivableBill> list);


}
