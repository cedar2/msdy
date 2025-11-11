package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasVendorRecommendAttach;

/**
 * 供应商推荐-附件Mapper接口
 * 
 * @author chenkw
 * @date 2022-02-21
 */
public interface BasVendorRecommendAttachMapper  extends BaseMapper<BasVendorRecommendAttach> {


    BasVendorRecommendAttach selectBasVendorRecommendAttachById(Long vendorRecommendAttachSid);

    List<BasVendorRecommendAttach> selectBasVendorRecommendAttachList(BasVendorRecommendAttach basVendorRecommendAttach);

    /**
     * 添加多个
     * @param list List BasVendorRecommendAttach
     * @return int
     */
    int inserts(@Param("list") List<BasVendorRecommendAttach> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasVendorRecommendAttach
    * @return int
    */
    int updateAllById(BasVendorRecommendAttach entity);

    /**
     * 更新多个
     * @param list List BasVendorRecommendAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<BasVendorRecommendAttach> list);


}
