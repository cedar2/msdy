package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasVendorRecommendAddr;

/**
 * 供应商推荐-联系方式信息Mapper接口
 * 
 * @author chenkw
 * @date 2022-02-21
 */
public interface BasVendorRecommendAddrMapper  extends BaseMapper<BasVendorRecommendAddr> {


    BasVendorRecommendAddr selectBasVendorRecommendAddrById(Long vendorRecommendContactSid);

    List<BasVendorRecommendAddr> selectBasVendorRecommendAddrList(BasVendorRecommendAddr basVendorRecommendAddr);

    /**
     * 添加多个
     * @param list List BasVendorRecommendAddr
     * @return int
     */
    int inserts(@Param("list") List<BasVendorRecommendAddr> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasVendorRecommendAddr
    * @return int
    */
    int updateAllById(BasVendorRecommendAddr entity);

    /**
     * 更新多个
     * @param list List BasVendorRecommendAddr
     * @return int
     */
    int updatesAllById(@Param("list") List<BasVendorRecommendAddr> list);


}
