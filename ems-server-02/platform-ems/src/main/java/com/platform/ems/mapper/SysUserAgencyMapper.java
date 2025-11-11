package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.SysUserAgency;

/**
 * 账号代办设置Mapper接口
 * 
 * @author qhq
 * @date 2021-10-18
 */
public interface SysUserAgencyMapper  extends BaseMapper<SysUserAgency> {


    SysUserAgency selectSysUserAgencyById (Long userAgencySid);

    List<SysUserAgency> selectSysUserAgencyList (SysUserAgency sysUserAgency);

    /**
     * 添加多个
     * @param list List SysUserAgency
     * @return int
     */
    int inserts (@Param("list") List<SysUserAgency> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity SysUserAgency
    * @return int
    */
    int updateAllById (SysUserAgency entity);

    /**
     * 更新多个
     * @param list List SysUserAgency
     * @return int
     */
    int updatesAllById (@Param("list") List<SysUserAgency> list);


}
