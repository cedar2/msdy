package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasCompanyBrand;

/**
 * 公司-品牌信息Mapper接口
 * 
 * @author linhongwei
 * @date 2021-03-22
 */
public interface BasCompanyBrandMapper  extends BaseMapper<BasCompanyBrand> {


    BasCompanyBrand selectBasCompanyBrandById(Long companyBrandSid);

    List<BasCompanyBrand> selectBasCompanyBrandList(BasCompanyBrand basCompanyBrand);

    /**
     * 添加多个
     * @param list List BasCompanyBrand
     * @return int
     */
    int  inserts(@Param("list") List<BasCompanyBrand> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasCompanyBrand
    * @return int
    */
    int updateAllById(BasCompanyBrand entity);

    /**
     * 更新多个
     * @param list List BasCompanyBrand
     * @return int
     */
    int updatesAllById(@Param("list") List<BasCompanyBrand> list);


}
