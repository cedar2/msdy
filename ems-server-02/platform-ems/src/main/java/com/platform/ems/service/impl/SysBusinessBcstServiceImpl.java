package com.platform.ems.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.constant.HttpStatus;
import com.platform.common.core.domain.R;
import com.platform.common.exception.TokenException;
import com.platform.common.utils.StringUtils;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.SalSalesOrder;
import com.platform.system.domain.SysTodoTask;
import com.platform.ems.mapper.SalSalesOrderMapper;
import com.platform.system.mapper.SysBusinessBcstMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.ems.service.document.BusinessBcstRepository;
import com.platform.api.service.RemoteMenuService;
import com.platform.common.core.domain.entity.SysMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.system.domain.SysBusinessBcst;
import com.platform.ems.service.ISysBusinessBcstService;

/**
 * 业务动态Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-30
 */
@Service
@SuppressWarnings("all")
public class SysBusinessBcstServiceImpl extends ServiceImpl<SysBusinessBcstMapper,SysBusinessBcst>  implements ISysBusinessBcstService {
    @Autowired
    private BusinessBcstRepository businessBcstRepository;
    @Autowired
    private SysBusinessBcstMapper sysBusinessBcstMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private RemoteMenuService remoteMenuService;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private SalSalesOrderMapper salSalesOrderMapper;

    private static final String TITLE = "业务动态";
    /**
     * 查询业务动态
     *
     * @param businessBcstSid 业务动态ID
     * @return 业务动态
     */
    @Override
    public SysBusinessBcst selectSysBusinessBcstById(Long businessBcstSid) {
        /*SysBusinessBcst businessBcst = new SysBusinessBcst();
        businessBcst.setId(id);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
                .withIgnoreCase(true) //改变默认大小写忽略方式：忽略大小写
                // .withMatcher("title", ExampleMatcher.GenericPropertyMatchers.contains()) //采用“包含匹配”的方式查询
                .withIgnorePaths("pageNum", "pageSize");  //忽略属性，不参与查询;
        Example<SysBusinessBcst> example = Example.of(businessBcst, matcher);
        Optional<SysBusinessBcst> menuOptional = businessBcstRepository.findOne(example);
        menuOptional.ifPresent(o -> {
            BeanUtil.copyProperties(o, businessBcst, true);
        });*/
        return sysBusinessBcstMapper.selectSysBusinessBcstById(businessBcstSid);
    }

    /**
     * 查询业务动态列表 (用户工作台)
     *
     * @param sysBusinessBcst 业务动态
     * @return 业务动态
     */
    @Override
    public List<SysBusinessBcst> selectSysBusinessBcstList(SysBusinessBcst sysBusinessBcst) {
        /*Query query = new Query();
        String clientId = ApiThreadLocalUtil.get().getClientId();
        if (null==clientId) {
            throw new TokenException();
        }
        if(!ApiThreadLocalUtil.get().getUserid().equals(1L)){
            query.addCriteria(Criteria.where("clientId").is(clientId));
            query.addCriteria(Criteria.where("userId").is(ApiThreadLocalUtil.get().getUserid()));
        }
        List<SysBusinessBcst> businessBcstList = mongoTemplate.find(query, SysBusinessBcst.class);
        return businessBcstList;*/
        if (!"10000".equals(sysBusinessBcst.getClientId())){
            sysBusinessBcst.setUserId(ApiThreadLocalUtil.get().getUserid());
        }
        List<SysBusinessBcst> sysBusinessBcstList = sysBusinessBcstMapper.selectSysBusinessBcstList(sysBusinessBcst);
        if (CollectionUtil.isNotEmpty(sysBusinessBcstList)){
            for (SysBusinessBcst businessBcst : sysBusinessBcstList) {
                String title = businessBcst.getTitle();
                if(title.contains(ConstantsEms.TODO_SALE_ORDER)){
                    SalSalesOrder salSalesOrder = salSalesOrderMapper.selectById(businessBcst.getDocumentSid());
                    String materialCategory = salSalesOrder.getMaterialCategory();
                    if(ConstantsEms.MATERIAL_CATEGORY_WL.equals(materialCategory)){
                        Long id = sysTodoTaskMapper.getMenuId(ConstantsEms.TODO_SALE_ORDER_MENU_WL);
                        businessBcst.setMenuId(id);
                    }else{
                        Long id = sysTodoTaskMapper.getMenuId(ConstantsEms.TODO_SALE_ORDER_MENU_SP);
                        businessBcst.setMenuId(id);
                    }
                }
                if(title.contains(ConstantsEms.TODO_SALE_CONTRACT)){
                    Long id = sysTodoTaskMapper.getMenuId(ConstantsEms.TODO_SALE_CONTRACT_MENU);
                    businessBcst.setMenuId(id);
                }
                if(title.contains(ConstantsEms.TODO_SALE_PRICE)){
                    Long id = sysTodoTaskMapper.getMenuId(ConstantsEms.TODO_SALE_PRICE_MENU);
                    businessBcst.setMenuId(id);
                }
                Long menuId = businessBcst.getMenuId();
                if (menuId != null){
                    SysBusinessBcst bcst = new SysBusinessBcst();
                    bcst.setMenuId(businessBcst.getMenuId());
                    String path = businessBcst.getPath();
                    getParent(businessBcst, bcst, path);
                    R<SysMenu> menuData = remoteMenuService.getInfo(bcst.getMenuId());
                    SysMenu menu = menuData.getData();
                    path = menu.getPath() + "/" + businessBcst.getPath();
                    businessBcst.setPath(path.substring(0, path.lastIndexOf("/")));
                }
            }
        }
        return sysBusinessBcstList;
    }

    /**
     * 查询业务动态报表
     *
     * @param sysBusinessBcst 业务动态
     * @return 业务动态
     */
    @Override
    public List<SysBusinessBcst> selectSysBusinessBcstReport(SysBusinessBcst sysBusinessBcst) {
        List<SysBusinessBcst> sysBusinessBcstList = sysBusinessBcstMapper.selectSysBusinessBcstList(sysBusinessBcst);
        if (CollectionUtil.isNotEmpty(sysBusinessBcstList)){
            for (SysBusinessBcst businessBcst : sysBusinessBcstList) {
                Long menuId = businessBcst.getMenuId();
                if (menuId != null){
                    SysBusinessBcst bcst = new SysBusinessBcst();
                    bcst.setMenuId(businessBcst.getMenuId());
                    String path = businessBcst.getPath();
                    getParent(businessBcst, bcst, path);
                    R<SysMenu> menuData = remoteMenuService.getInfo(bcst.getMenuId());
                    SysMenu menu = menuData.getData();
                    path = menu.getPath() + "/" + businessBcst.getPath();
                    businessBcst.setPath(path.substring(0, path.lastIndexOf("/")));
                }
            }
        }
        return sysBusinessBcstList;
    }

    /**
     * 递归父节点
     */
    private String getParent(SysBusinessBcst businessBcst, SysBusinessBcst bcst, String path) {
        if (StringUtils.isEmpty(path)){
            path = "";
        }
        R<SysMenu> menuData = remoteMenuService.getInfo(bcst.getMenuId());
        SysMenu sysMenu = menuData.getData();
        if (sysMenu != null){
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
                bcst.setMenuId(parentMenu.getParentId());
                getParent(businessBcst, bcst, path);
            }
            businessBcst.setPath(path);
        }
        return path;
    }

    @Override
    public TableDataInfo selectSysBusinessBcstTable(SysBusinessBcst sysBusinessBcst) {
        Query query = new Query();
        String clientId = ApiThreadLocalUtil.get().getClientId();
        if (null==clientId) {
            throw new TokenException();
        }
        if(!ApiThreadLocalUtil.get().getUserid().equals(1L)){
            query.addCriteria(Criteria.where("clientId").is(clientId));
            query.addCriteria(Criteria.where("userId").is(ApiThreadLocalUtil.get().getUserid()));
        }
        PageRequest pageRequest = PageRequest.of((sysBusinessBcst.getPageNum()==null?1:sysBusinessBcst.getPageNum()) - 1
                , sysBusinessBcst.getPageSize()==null?10:sysBusinessBcst.getPageSize());
        int count = (int) mongoTemplate.count(query, SysBusinessBcst.class);
        query.with(pageRequest);
        List<SysBusinessBcst> businessBcstList = mongoTemplate.find(query, SysBusinessBcst.class);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(businessBcstList);
        rspData.setMsg("查询成功");
        rspData.setTotal(count);
        return rspData;
    }

    /**
     * 新增业务动态
     * 需要注意编码重复校验
     * @param sysBusinessBcst 业务动态
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSysBusinessBcst(SysBusinessBcst sysBusinessBcst) {
        String clientId=ApiThreadLocalUtil.get().getClientId();
        if(null==clientId){
            throw new TokenException();
        }
        sysBusinessBcst.setClientId(clientId);
        sysBusinessBcst.setUserId(ApiThreadLocalUtil.get().getUserid());
        sysBusinessBcst.setCreateDate(new Date());
        sysBusinessBcst.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
        businessBcstRepository.save(sysBusinessBcst);
        return 1;
    }


    /**
     * 批量删除业务动态
     *
     * @param businessBcstSids 需要删除的业务动态ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysBusinessBcstByIds(List<String> ids) {
        ids.forEach(s->{
            businessBcstRepository.deleteById(s);
        });
        return 1;
    }



}
