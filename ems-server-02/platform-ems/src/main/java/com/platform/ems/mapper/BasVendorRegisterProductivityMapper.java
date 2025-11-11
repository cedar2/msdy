package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasVendorRegisterProductivity;

/**
 * 供应商注册-产能信息Mapper接口
 * 
 * @author chenkw
 * @date 2022-02-21
 */
public interface BasVendorRegisterProductivityMapper  extends BaseMapper<BasVendorRegisterProductivity> {


    BasVendorRegisterProductivity selectBasVendorRegisterProductivityById(Long vendorRegisterProductivitySid);

    List<BasVendorRegisterProductivity> selectBasVendorRegisterProductivityList(BasVendorRegisterProductivity basVendorRegisterProductivity);

    /**
     * 添加多个
     * @param list List BasVendorRegisterProductivity
     * @return int
     */
    int inserts(@Param("list") List<BasVendorRegisterProductivity> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasVendorRegisterProductivity
    * @return int
    */
    int updateAllById(BasVendorRegisterProductivity entity);

    /**
     * 更新多个
     * @param list List BasVendorRegisterProductivity
     * @return int
     */
    int updatesAllById(@Param("list") List<BasVendorRegisterProductivity> list);


}
