package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasCompanyBrandMark;

/**
 * 公司-品标信息Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-24
 */
public interface BasCompanyBrandMarkMapper  extends BaseMapper<BasCompanyBrandMark> {


    BasCompanyBrandMark selectBasCompanyBrandMarkById(Long brandMarkSid);

    List<BasCompanyBrandMark> selectBasCompanyBrandMarkList(BasCompanyBrandMark basCompanyBrandMark);

    /**
     * 添加多个
     * @param list List BasCompanyBrandMark
     * @return int
     */
    int inserts(@Param("list") List<BasCompanyBrandMark> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasCompanyBrandMark
    * @return int
    */
    int updateAllById(BasCompanyBrandMark entity);

    /**
     * 更新多个
     * @param list List BasCompanyBrandMark
     * @return int
     */
    int updatesAllById(@Param("list") List<BasCompanyBrandMark> list);


}
