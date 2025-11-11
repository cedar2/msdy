package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasVendorRecommend;

/**
 * 供应商推荐-基础Mapper接口
 * 
 * @author chenkw
 * @date 2022-02-21
 */
public interface BasVendorRecommendMapper  extends BaseMapper<BasVendorRecommend> {


    BasVendorRecommend selectBasVendorRecommendById(Long vendorRecommendSid);

    List<BasVendorRecommend> selectBasVendorRecommendList(BasVendorRecommend basVendorRecommend);

    /**
     * 添加多个
     * @param list List BasVendorRecommend
     * @return int
     */
    int inserts(@Param("list") List<BasVendorRecommend> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasVendorRecommend
    * @return int
    */
    int updateAllById(BasVendorRecommend entity);

    /**
     * 更新多个
     * @param list List BasVendorRecommend
     * @return int
     */
    int updatesAllById(@Param("list") List<BasVendorRecommend> list);


}
