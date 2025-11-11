package com.platform.ems.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.common.core.domain.R;
import com.platform.common.exception.TokenException;
import com.platform.common.utils.StringUtils;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.domain.SysUserFavoriteMenu;
import com.platform.ems.mapper.SysUserFavoriteMenuMapper;
import com.platform.ems.service.ISysUserFavoriteMenuService;
import com.platform.ems.service.document.UserFavoriteMenuRepository;
import com.platform.api.service.RemoteMenuService;
import com.platform.common.core.domain.entity.SysMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 用户收藏菜单Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-29
 */
@Service
@SuppressWarnings("all")
public class SysUserFavoriteMenuServiceImpl implements ISysUserFavoriteMenuService {
    @Autowired
    private UserFavoriteMenuRepository userFavoriteMenuRepository;
    @Autowired
    private SysUserFavoriteMenuMapper sysUserFavoriteMenuMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private RemoteMenuService remoteMenuService;


    private static final String TITLE = "用户收藏菜单";

    /**
     * 查询用户收藏菜单
     *
     * @param id 用户收藏菜单ID
     * @return 用户收藏菜单
     */
    @Override
    public SysUserFavoriteMenu selectSysUserFavoriteMenuById(String id) {
        SysUserFavoriteMenu sysUserFavoriteMenu = new SysUserFavoriteMenu();
        sysUserFavoriteMenu.setId(id);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
                .withIgnoreCase(true) //改变默认大小写忽略方式：忽略大小写
                // .withMatcher("title", ExampleMatcher.GenericPropertyMatchers.contains()) //采用“包含匹配”的方式查询
                .withIgnorePaths("pageNum", "pageSize");  //忽略属性，不参与查询;
        Example<SysUserFavoriteMenu> example = Example.of(sysUserFavoriteMenu, matcher);
        Optional<SysUserFavoriteMenu> menuOptional = userFavoriteMenuRepository.findOne(example);
        menuOptional.ifPresent(o -> {
            BeanUtil.copyProperties(o, sysUserFavoriteMenu, true);
        });
        return sysUserFavoriteMenu;
    }

    /**
     * 查询用户收藏菜单列表
     *
     * @param sysUserFavoriteMenu 用户收藏菜单
     * @return 用户收藏菜单
     */
    @Override
    public List<SysUserFavoriteMenu> selectSysUserFavoriteMenuList(SysUserFavoriteMenu sysUserFavoriteMenu) {
        /*Query query = new Query();
        String clientId=ApiThreadLocalUtil.get().getClientId();
        if(null==clientId&&!ApiThreadLocalUtil.get().getUserid().equals(1L)){
            throw new TokenException();
        }
        if(!ApiThreadLocalUtil.get().getUserid().equals(1L)){
            query.addCriteria(Criteria.where("clientId").is(clientId));
            query.addCriteria(Criteria.where("userId").is(ApiThreadLocalUtil.get().getUserid()));
        }
        query.with(Sort.by(Sort.Direction.ASC, "serialNum"));
        PageRequest pageRequest = PageRequest.of(sysUserFavoriteMenu.getPageNum()!=null?sysUserFavoriteMenu.getPageNum() - 1:0
                , sysUserFavoriteMenu.getPageSize()!=null? sysUserFavoriteMenu.getPageSize():10);
        int count = (int) mongoTemplate.count(query, SysUserFavoriteMenu.class);
        query.with(pageRequest);
        List<SysUserFavoriteMenu> userFavoriteMenuList = mongoTemplate.find(query, SysUserFavoriteMenu.class);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(userFavoriteMenuList);
        rspData.setMsg("查询成功");
        rspData.setTotal(count);
        return rspData;*/
        sysUserFavoriteMenu.setUserId(ApiThreadLocalUtil.get().getUserid());
        List<SysUserFavoriteMenu> sysUserFavoriteMenuList = sysUserFavoriteMenuMapper.selectSysUserFavoriteMenuList(sysUserFavoriteMenu);
        if (CollectionUtil.isNotEmpty(sysUserFavoriteMenuList)){
            for (SysUserFavoriteMenu menu : sysUserFavoriteMenuList) {
                SysUserFavoriteMenu userFavoriteMenu = new SysUserFavoriteMenu();
                String path = menu.getPath();
                userFavoriteMenu.setMenuId(menu.getMenuId());
                getParent(menu, userFavoriteMenu, path);
                R<SysMenu> menuData = remoteMenuService.getInfo(userFavoriteMenu.getMenuId());
                SysMenu sysMenu = menuData.getData();
                path = sysMenu.getPath() + "/" + menu.getPath();
                menu.setPath(path.substring(0, path.lastIndexOf("/")));
            }
        }
        return sysUserFavoriteMenuList;
    }

    /**
     * 递归父节点
     */
    private String getParent(SysUserFavoriteMenu menu, SysUserFavoriteMenu userFavoriteMenu, String path) {
        if (StringUtils.isEmpty(path)){
            path = "";
        }
        R<SysMenu> menuData = remoteMenuService.getInfo(userFavoriteMenu.getMenuId());
        SysMenu sysMenu = menuData.getData();
        path = sysMenu.getPath() + "/" + path;
        if (sysMenu.getParentId() != null && sysMenu.getParentId() != 0){
            R<SysMenu> parentData = remoteMenuService.getInfo(sysMenu.getParentId());
            SysMenu parentMenu = parentData.getData();
            String parentPath = parentMenu.getPath();
            if (StringUtils.isEmpty(parentPath)){
                return path;
            }else {
                path = parentMenu.getPath() + "/"+ path;
            }
            userFavoriteMenu.setMenuId(parentMenu.getParentId());
            getParent(menu, userFavoriteMenu, path);
        }
        menu.setPath(path);
        return path;
    }

    /**
     * 新增用户收藏菜单
     * 需要注意编码重复校验
     *
     * @param sysUserFavoriteMenu 用户收藏菜单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSysUserFavoriteMenu(SysUserFavoriteMenu sysUserFavoriteMenu) {
        String clientId = ApiThreadLocalUtil.get().getClientId();
        if (null == clientId) {
            throw new TokenException();
        }
        sysUserFavoriteMenu.setClientId(clientId);
        sysUserFavoriteMenu.setUserId(sysUserFavoriteMenu.getUserId());
        sysUserFavoriteMenu.setCreateDate(new Date());
        sysUserFavoriteMenu.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
        userFavoriteMenuRepository.save(sysUserFavoriteMenu);
        return 1;
    }

    /**
     * 修改用户收藏菜单
     *
     * @param sysUserFavoriteMenu 用户收藏菜单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSysUserFavoriteMenu(SysUserFavoriteMenu sysUserFavoriteMenu) {
        sysUserFavoriteMenu.setUpdateDate(new Date());
        sysUserFavoriteMenu.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        userFavoriteMenuRepository.save(sysUserFavoriteMenu);
        return 1;
    }


    /**
     * 批量删除用户收藏菜单
     *
     * @param favoriteMenuSids 需要删除的用户收藏菜单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysUserFavoriteMenuByIds(List<String> favoriteMenuSids) {
        favoriteMenuSids.forEach(s -> {
            userFavoriteMenuRepository.deleteById(s);
        });
        return 1;
    }


}
