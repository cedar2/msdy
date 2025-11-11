package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.plug.domain.ConDiscountType;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDistributeChannelCategory;

/**
 * 分销渠道类别Mapper接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConDistributeChannelCategoryMapper  extends BaseMapper<ConDistributeChannelCategory> {


    ConDistributeChannelCategory selectConDistributeChannelCategoryById(Long sid);

    List<ConDistributeChannelCategory> selectConDistributeChannelCategoryList(ConDistributeChannelCategory conDistributeChannelCategory);

    /**
     * 添加多个
     * @param list List ConDistributeChannelCategory
     * @return int
     */
    int inserts(@Param("list") List<ConDistributeChannelCategory> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConDistributeChannelCategory
    * @return int
    */
    int updateAllById(ConDistributeChannelCategory entity);

    /**
     * 更新多个
     * @param list List ConDistributeChannelCategory
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDistributeChannelCategory> list);

    /** 获取下拉列表 */
    List<ConDistributeChannelCategory> getConDistributeChannelCategoryList();
}
