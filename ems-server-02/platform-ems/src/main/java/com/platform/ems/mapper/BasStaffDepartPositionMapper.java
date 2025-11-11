package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasStaffDepartPosition;

/**
 * 员工所属部门岗位信息Mapper接口
 * 
 * @author qhq
 * @date 2021-03-18
 */
public interface BasStaffDepartPositionMapper  extends BaseMapper<BasStaffDepartPosition> {


    BasStaffDepartPosition selectBasStaffDepartPositionById(String clientId);

    List<BasStaffDepartPosition> selectBasStaffDepartPositionList(BasStaffDepartPosition basStaffDepartPosition);

    /**
     * 添加多个
     * @param list List BasStaffDepartPosition
     * @return int
     */
    int inserts(@Param("list") List<BasStaffDepartPosition> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasStaffDepartPosition
    * @return int
    */
    int updateAllById(BasStaffDepartPosition entity);

    /**
     * 更新多个
     * @param list List BasStaffDepartPosition
     * @return int
     */
    int updatesAllById(@Param("list") List<BasStaffDepartPosition> list);


}
