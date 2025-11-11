package com.platform.ems.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.platform.common.constant.HttpStatus;
import com.platform.common.core.domain.R;
import com.platform.common.exception.TokenException;
import com.platform.common.utils.StringUtils;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.SalSalesOrder;
import com.platform.system.domain.SysOverdueBusiness;
import com.platform.ems.mapper.SalSalesOrderMapper;
import com.platform.system.mapper.SysOverdueBusinessMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.ems.service.document.OverdueBusinessRepository;
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
import com.platform.ems.service.ISysOverdueBusinessService;

/**
 * 已逾期警示列Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-29
 */
@Service
@SuppressWarnings("all")
public class SysOverdueBusinessServiceImpl implements ISysOverdueBusinessService {
    @Autowired
    private OverdueBusinessRepository overdueBusinessRepository;
    @Autowired
    private SysOverdueBusinessMapper sysOverdueBusinessMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private RemoteMenuService remoteMenuService;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private SalSalesOrderMapper salSalesOrderMapper;


    private static final String TITLE = "已逾期警示";

    /**
     * 查询已逾期警示列
     *
     * @param overdueBusinessSid 已逾期警示列ID
     * @return 已逾期警示列
     */
    @Override
    public SysOverdueBusiness selectSysOverdueBusinessById(String id) {
        SysOverdueBusiness overdueBusiness = new SysOverdueBusiness();
        overdueBusiness.setId(id);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
                .withIgnoreCase(true) //改变默认大小写忽略方式：忽略大小写
                // .withMatcher("title", ExampleMatcher.GenericPropertyMatchers.contains()) //采用“包含匹配”的方式查询
                .withIgnorePaths("pageNum", "pageSize");  //忽略属性，不参与查询;
        Example<SysOverdueBusiness> example = Example.of(overdueBusiness, matcher);
        Optional<SysOverdueBusiness> menuOptional = overdueBusinessRepository.findOne(example);
        menuOptional.ifPresent(o -> {
            BeanUtil.copyProperties(o, overdueBusiness, true);
        });
        return overdueBusiness;
    }

    /**
     * 查询已逾期警示列列表（用户工作台）
     *
     * @param sysOverdueBusiness 已逾期警示列
     * @return 已逾期警示列
     */
    @Override
    public List<SysOverdueBusiness> selectSysOverdueBusinessList(SysOverdueBusiness sysOverdueBusiness) {
        /*Query query = new Query();
        String clientId = ApiThreadLocalUtil.get().getClientId();
        if (null==clientId) {
            throw new TokenException();
        }
        query.addCriteria(Criteria.where("clientId").is(clientId));
        query.addCriteria(Criteria.where("userId").is(ApiThreadLocalUtil.get().getUserid()));
        List<SysOverdueBusiness> overdueBusinessList = mongoTemplate.find(query, SysOverdueBusiness.class);
        return overdueBusinessList;*/
        if (!"10000".equals(sysOverdueBusiness.getClientId()) && !"Y".equals(sysOverdueBusiness.getNotAutoUser())){
            sysOverdueBusiness.setUserId(ApiThreadLocalUtil.get().getUserid());
        }
        List<SysOverdueBusiness> sysOverdueBusinessList = sysOverdueBusinessMapper.selectSysOverdueBusinessList(sysOverdueBusiness);
        if (CollectionUtil.isNotEmpty(sysOverdueBusinessList)){
            for (SysOverdueBusiness overdueBusiness : sysOverdueBusinessList) {
                String title = overdueBusiness.getTitle();
                if(title.contains(ConstantsEms.TODO_SALE_ORDER)){
                    SalSalesOrder salSalesOrder = salSalesOrderMapper.selectById(overdueBusiness.getDocumentSid());
                    String materialCategory = salSalesOrder.getMaterialCategory();
                    if(ConstantsEms.MATERIAL_CATEGORY_WL.equals(materialCategory)){
                        Long id = sysTodoTaskMapper.getMenuId(ConstantsEms.TODO_SALE_ORDER_MENU_WL);
                        overdueBusiness.setMenuId(id);
                    }else{
                        Long id = sysTodoTaskMapper.getMenuId(ConstantsEms.TODO_SALE_ORDER_MENU_SP);
                        overdueBusiness.setMenuId(id);
                    }
                }
                if(title.contains(ConstantsEms.TODO_SALE_CONTRACT)){
                    Long id = sysTodoTaskMapper.getMenuId(ConstantsEms.TODO_SALE_CONTRACT_MENU);
                    overdueBusiness.setMenuId(id);
                }
                if(title.contains(ConstantsEms.TODO_SALE_PRICE)){
                    Long id = sysTodoTaskMapper.getMenuId(ConstantsEms.TODO_SALE_PRICE_MENU);
                    overdueBusiness.setMenuId(id);
                }
                Long menuId = overdueBusiness.getMenuId();
                if (menuId != null){
                    SysOverdueBusiness business = new SysOverdueBusiness();
                    business.setMenuId(overdueBusiness.getMenuId());
                    String path = overdueBusiness.getPath();
                    getParent(overdueBusiness, business, path);
                    R<SysMenu> menuData = remoteMenuService.getInfo(business.getMenuId());
                    SysMenu menu = menuData.getData();
                    path = menu.getPath() + "/" + overdueBusiness.getPath();
                    overdueBusiness.setPath(path.substring(0, path.lastIndexOf("/")));
                }
            }
        }
        return sysOverdueBusinessList;
    }

    /**
     * 查询已逾期警示列报表
     *
     * @param sysOverdueBusiness 已逾期警示列
     * @return 已逾期警示列
     */
    @Override
    public List<SysOverdueBusiness> selectSysOverdueBusinessReport(SysOverdueBusiness sysOverdueBusiness) {
        List<SysOverdueBusiness> sysOverdueBusinessList = sysOverdueBusinessMapper.selectSysOverdueBusinessList(sysOverdueBusiness);
        if (CollectionUtil.isNotEmpty(sysOverdueBusinessList)){
            for (SysOverdueBusiness overdueBusiness : sysOverdueBusinessList) {
                Long menuId = overdueBusiness.getMenuId();
                if (menuId != null){
                    SysOverdueBusiness business = new SysOverdueBusiness();
                    business.setMenuId(overdueBusiness.getMenuId());
                    String path = overdueBusiness.getPath();
                    getParent(overdueBusiness, business, path);
                    R<SysMenu> menuData = remoteMenuService.getInfo(business.getMenuId());
                    SysMenu menu = menuData.getData();
                    path = menu.getPath() + "/" + overdueBusiness.getPath();
                    overdueBusiness.setPath(path.substring(0, path.lastIndexOf("/")));
                }
            }
        }
        return sysOverdueBusinessList;
    }

    /**
     * 递归父节点
     */
    private String getParent(SysOverdueBusiness overdueBusiness, SysOverdueBusiness business, String path) {
        if (StringUtils.isEmpty(path)){
            path = "";
        }
        R<SysMenu> menuData = remoteMenuService.getInfo(business.getMenuId());
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
                business.setMenuId(parentMenu.getParentId());
                getParent(overdueBusiness, business, path);
            }
            overdueBusiness.setPath(path);
        }
        return path;
    }


    @Override
    public TableDataInfo selectSysOverdueBusinessTable(SysOverdueBusiness sysOverdueBusiness) {
        Query query = new Query();
        String clientId = ApiThreadLocalUtil.get().getClientId();
        if (null==clientId) {
            throw new TokenException();
        }
        query.addCriteria(Criteria.where("clientId").is(clientId));
        query.addCriteria(Criteria.where("userId").is(ApiThreadLocalUtil.get().getUserid()));
        PageRequest pageRequest = PageRequest.of((sysOverdueBusiness.getPageNum()==null?1:sysOverdueBusiness.getPageNum()) - 1, sysOverdueBusiness.getPageSize()==null?10:sysOverdueBusiness.getPageSize());
        int count = (int) mongoTemplate.count(query, SysOverdueBusiness.class);
        query.with(pageRequest);
        List<SysOverdueBusiness> overdueBusinessList = mongoTemplate.find(query, SysOverdueBusiness.class);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(overdueBusinessList);
        rspData.setMsg("查询成功");
        rspData.setTotal(count);
        return rspData;
    }

    /**
     * 新增已逾期警示列
     * 需要注意编码重复校验
     *
     * @param sysOverdueBusiness 已逾期警示列
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSysOverdueBusiness(SysOverdueBusiness sysOverdueBusiness) {
        String clientId=ApiThreadLocalUtil.get().getClientId();
        if(null==clientId){
            throw new TokenException();
        }
        sysOverdueBusiness.setClientId(clientId);
        sysOverdueBusiness.setUserId(ApiThreadLocalUtil.get().getUserid());
        sysOverdueBusiness.setCreateDate(new Date());
        sysOverdueBusiness.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
        overdueBusinessRepository.save(sysOverdueBusiness);
        return 1;
    }

    /**
     * 修改已逾期警示列
     *
     * @param sysOverdueBusiness 已逾期警示列
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSysOverdueBusiness(SysOverdueBusiness sysOverdueBusiness) {
        sysOverdueBusiness.setUpdateDate(new Date());
        sysOverdueBusiness.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        overdueBusinessRepository.save(sysOverdueBusiness);
        return 1;
    }


    /**
     * 批量删除已逾期警示列
     *
     * @param ids 需要删除的已逾期警示列ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysOverdueBusinessByIds(List<String> ids) {
        ids.forEach(s->{
            overdueBusinessRepository.deleteById(s);
        });
        return 1;
    }



}
