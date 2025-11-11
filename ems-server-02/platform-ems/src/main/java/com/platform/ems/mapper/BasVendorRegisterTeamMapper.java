package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasVendorRegisterTeam;

/**
 * 供应商注册-人员信息Mapper接口
 * 
 * @author chenkw
 * @date 2022-02-21
 */
public interface BasVendorRegisterTeamMapper  extends BaseMapper<BasVendorRegisterTeam> {


    BasVendorRegisterTeam selectBasVendorRegisterTeamById(Long vendorRegisterTeamSid);

    List<BasVendorRegisterTeam> selectBasVendorRegisterTeamList(BasVendorRegisterTeam basVendorRegisterTeam);

    /**
     * 添加多个
     * @param list List BasVendorRegisterTeam
     * @return int
     */
    int inserts(@Param("list") List<BasVendorRegisterTeam> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasVendorRegisterTeam
    * @return int
    */
    int updateAllById(BasVendorRegisterTeam entity);

    /**
     * 更新多个
     * @param list List BasVendorRegisterTeam
     * @return int
     */
    int updatesAllById(@Param("list") List<BasVendorRegisterTeam> list);


}
