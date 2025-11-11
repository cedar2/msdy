package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasCustomerBrandMark;

/**
 * 客户-客方品标信息Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-24
 */
public interface BasCustomerBrandMarkMapper  extends BaseMapper<BasCustomerBrandMark> {


    BasCustomerBrandMark selectBasCustomerBrandMarkById(Long brandMarkSid);

    List<BasCustomerBrandMark> selectBasCustomerBrandMarkList(BasCustomerBrandMark basCustomerBrandMark);

    /**
     * 添加多个
     * @param list List BasCustomerBrandMark
     * @return int
     */
    int inserts(@Param("list") List<BasCustomerBrandMark> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasCustomerBrandMark
    * @return int
    */
    int updateAllById(BasCustomerBrandMark entity);

    /**
     * 更新多个
     * @param list List BasCustomerBrandMark
     * @return int
     */
    int updatesAllById(@Param("list") List<BasCustomerBrandMark> list);


}
