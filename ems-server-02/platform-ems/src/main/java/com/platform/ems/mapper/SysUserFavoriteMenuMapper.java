package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.SysUserFavoriteMenu;

/**
 * 用户收藏菜单Mapper接口
 *
 * @author linhongwei
 * @date 2021-06-29
 */
public interface SysUserFavoriteMenuMapper extends BaseMapper<SysUserFavoriteMenu> {


    SysUserFavoriteMenu selectSysUserFavoriteMenuById(Long favoriteMenuSid);

    List<SysUserFavoriteMenu> selectSysUserFavoriteMenuList(SysUserFavoriteMenu sysUserFavoriteMenu);

    /**
     * 添加多个
     *
     * @param list List SysUserFavoriteMenu
     * @return int
     */
    int inserts(@Param("list") List<SysUserFavoriteMenu> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity SysUserFavoriteMenu
     * @return int
     */
    int updateAllById(SysUserFavoriteMenu entity);

    /**
     * 更新多个
     *
     * @param list List SysUserFavoriteMenu
     * @return int
     */
    int updatesAllById(@Param("list") List<SysUserFavoriteMenu> list);


}
