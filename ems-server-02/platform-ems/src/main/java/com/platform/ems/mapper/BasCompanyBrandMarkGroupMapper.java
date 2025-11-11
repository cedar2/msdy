package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasCompanyBrandMarkGroup;

/**
 * 公司-品牌-品标组合信息Mapper接口
 *
 * @author c
 * @date 2021-11-17
 */
public interface BasCompanyBrandMarkGroupMapper  extends BaseMapper<BasCompanyBrandMarkGroup> {


    BasCompanyBrandMarkGroup selectBasCompanyBrandMarkGroupById(Long cbbmGroupSid);

    List<BasCompanyBrandMarkGroup> selectBasCompanyBrandMarkGroupList(BasCompanyBrandMarkGroup basCompanyBrandMarkGroup);

    /**
     * 添加多个
     * @param list List BasCompanyBrandMarkGroup
     * @return int
     */
    int inserts(@Param("list") List<BasCompanyBrandMarkGroup> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity BasCompanyBrandMarkGroup
     * @return int
     */
    int updateAllById(BasCompanyBrandMarkGroup entity);

    /**
     * 更新多个
     * @param list List BasCompanyBrandMarkGroup
     * @return int
     */
    int updatesAllById(@Param("list") List<BasCompanyBrandMarkGroup> list);


}