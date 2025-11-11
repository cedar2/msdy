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
import com.platform.ems.mapper.SalSalesOrderMapper;
import com.platform.system.domain.SysToexpireBusiness;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.ems.service.document.ToExpireBusinessRepository;
import com.platform.api.service.RemoteMenuService;
import com.platform.common.core.domain.entity.SysMenu;
import com.platform.system.mapper.SysToexpireBusinessMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.service.ISysToexpireBusinessService;

/**
 * 即将到期预警Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-29
 */
@Service
@SuppressWarnings("all")
public class SysToexpireBusinessServiceImpl   implements ISysToexpireBusinessService {
    @Autowired
    private ToExpireBusinessRepository toExpireBusinessRepository;
    @Autowired
    private SysToexpireBusinessMapper sysToexpireBusinessMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private RemoteMenuService remoteMenuService;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private SalSalesOrderMapper salSalesOrderMapper;


    private static final String TITLE = "即将到期预警";
    /**
     * 查询即将到期预警
     *
     * @param toexpireBusinessSid 即将到期预警ID
     * @return 即将到期预警
     */
    @Override
    public SysToexpireBusiness selectSysToexpireBusinessById(String id) {
        SysToexpireBusiness toexpireBusiness = new SysToexpireBusiness();
        toexpireBusiness.setId(id);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
                .withIgnoreCase(true) //改变默认大小写忽略方式：忽略大小写
                // .withMatcher("title", ExampleMatcher.GenericPropertyMatchers.contains()) //采用“包含匹配”的方式查询
                .withIgnorePaths("pageNum", "pageSize");  //忽略属性，不参与查询;
        Example<SysToexpireBusiness> example = Example.of(toexpireBusiness, matcher);
        Optional<SysToexpireBusiness> menuOptional = toExpireBusinessRepository.findOne(example);
        menuOptional.ifPresent(o -> {
            BeanUtil.copyProperties(o, toexpireBusiness, true);
        });
        return toexpireBusiness;
    }

    /**
     * 查询即将到期预警列表 (用户工作台)
     *
     * @param sysToexpireBusiness 即将到期预警
     * @return 即将到期预警
     */
    @Override
    public List<SysToexpireBusiness> selectSysToexpireBusinessList(SysToexpireBusiness sysToexpireBusiness) {
        /*Query query = new Query();
        String clientId = ApiThreadLocalUtil.get().getClientId();
        if (clientId==null) {
            throw new TokenException();
        }
        if(!ApiThreadLocalUtil.get().getUserid().equals(1L)){
            query.addCriteria(Criteria.where("clientId").is(clientId));
            query.addCriteria(Criteria.where("userId").is(ApiThreadLocalUtil.get().getUserid()));
        }
        List<SysToexpireBusiness> toexpireBusinessList = mongoTemplate.find(query, SysToexpireBusiness.class);
        return toexpireBusinessList;*/
        if (!"10000".equals(sysToexpireBusiness.getClientId()) && !"Y".equals(sysToexpireBusiness.getNotAutoUser())){
            sysToexpireBusiness.setUserId(ApiThreadLocalUtil.get().getUserid());
        }
        List<SysToexpireBusiness> sysToexpireBusinessList = sysToexpireBusinessMapper.selectSysToexpireBusinessList(sysToexpireBusiness);
        if (CollectionUtil.isNotEmpty(sysToexpireBusinessList)){
            for (SysToexpireBusiness toexpireBusiness : sysToexpireBusinessList) {
                String title = toexpireBusiness.getTitle();
                if(title.contains(ConstantsEms.TODO_SALE_ORDER)){
                    SalSalesOrder salSalesOrder = salSalesOrderMapper.selectById(toexpireBusiness.getDocumentSid());
                    String materialCategory = salSalesOrder.getMaterialCategory();
                    if(ConstantsEms.MATERIAL_CATEGORY_WL.equals(materialCategory)){
                        Long id = sysTodoTaskMapper.getMenuId(ConstantsEms.TODO_SALE_ORDER_MENU_WL);
                        toexpireBusiness.setMenuId(id);
                    }else{
                        Long id = sysTodoTaskMapper.getMenuId(ConstantsEms.TODO_SALE_ORDER_MENU_SP);
                        toexpireBusiness.setMenuId(id);
                    }
                }
                if(title.contains(ConstantsEms.TODO_SALE_CONTRACT)){
                    Long id = sysTodoTaskMapper.getMenuId(ConstantsEms.TODO_SALE_CONTRACT_MENU);
                    toexpireBusiness.setMenuId(id);
                }
                if(title.contains(ConstantsEms.TODO_SALE_PRICE)){
                    Long id = sysTodoTaskMapper.getMenuId(ConstantsEms.TODO_SALE_PRICE_MENU);
                    toexpireBusiness.setMenuId(id);
                }
                Long menuId = toexpireBusiness.getMenuId();
                if (menuId != null){
                    SysToexpireBusiness business = new SysToexpireBusiness();
                    business.setMenuId(toexpireBusiness.getMenuId());
                    String path = toexpireBusiness.getPath();
                    getParent(toexpireBusiness, business, path);
                    R<SysMenu> menuData = remoteMenuService.getInfo(business.getMenuId());
                    SysMenu menu = menuData.getData();
                    path = menu.getPath() + "/" + toexpireBusiness.getPath();
                    toexpireBusiness.setPath(path.substring(0, path.lastIndexOf("/")));
                }
            }
        }
        return sysToexpireBusinessList;
    }


    /**
     * 查询即将到期预警报表
     *
     * @param sysToexpireBusiness 即将到期预警
     * @return 即将到期预警
     */
    @Override
    public List<SysToexpireBusiness> selectSysToexpireBusinessReport(SysToexpireBusiness sysToexpireBusiness) {
        List<SysToexpireBusiness> sysToexpireBusinessList = sysToexpireBusinessMapper.selectSysToexpireBusinessList(sysToexpireBusiness);
        if (CollectionUtil.isNotEmpty(sysToexpireBusinessList)){
            for (SysToexpireBusiness toexpireBusiness : sysToexpireBusinessList) {
                Long menuId = toexpireBusiness.getMenuId();
                if (menuId != null){
                    SysToexpireBusiness business = new SysToexpireBusiness();
                    business.setMenuId(toexpireBusiness.getMenuId());
                    String path = toexpireBusiness.getPath();
                    getParent(toexpireBusiness, business, path);
                    R<SysMenu> menuData = remoteMenuService.getInfo(business.getMenuId());
                    SysMenu menu = menuData.getData();
                    path = menu.getPath() + "/" + toexpireBusiness.getPath();
                    toexpireBusiness.setPath(path.substring(0, path.lastIndexOf("/")));
                }
            }
        }
        return sysToexpireBusinessList;
    }

    /**
     * 递归父节点
     */
    private String getParent(SysToexpireBusiness toexpireBusiness, SysToexpireBusiness business, String path) {
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
                getParent(toexpireBusiness, business, path);
            }
            toexpireBusiness.setPath(path);
        }
        return path;
    }

    @Override
    public TableDataInfo selectSysToexpireBusinessTable(SysToexpireBusiness sysToexpireBusiness) {
        Query query = new Query();
        String clientId = ApiThreadLocalUtil.get().getClientId();
        if (clientId==null) {
            throw new TokenException();
        }
        if(!ApiThreadLocalUtil.get().getUserid().equals(1L)){
            query.addCriteria(Criteria.where("clientId").is(clientId));
            query.addCriteria(Criteria.where("userId").is(ApiThreadLocalUtil.get().getUserid()));
        }
        PageRequest pageRequest = PageRequest.of(sysToexpireBusiness.getPageNum() - 1, sysToexpireBusiness.getPageSize());
        int count = (int) mongoTemplate.count(query, SysToexpireBusiness.class);
        query.with(pageRequest);
        List<SysToexpireBusiness> toexpireBusinessList = mongoTemplate.find(query, SysToexpireBusiness.class);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(toexpireBusinessList);
        rspData.setMsg("查询成功");
        rspData.setTotal(count);
        return rspData;
    }

    /**
     * 新增即将到期预警
     * 需要注意编码重复校验
     * @param sysToexpireBusiness 即将到期预警
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSysToexpireBusiness(SysToexpireBusiness sysToexpireBusiness) {
        String clientId=ApiThreadLocalUtil.get().getClientId();
        if(null==clientId){
            throw new TokenException();
        }
        sysToexpireBusiness.setClientId(clientId);
        sysToexpireBusiness.setUserId(ApiThreadLocalUtil.get().getUserid());
        sysToexpireBusiness.setCreateDate(new Date());
        sysToexpireBusiness.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
        toExpireBusinessRepository.save(sysToexpireBusiness);
        return 1;
    }

    /**
     * 修改即将到期预警
     *
     * @param sysToexpireBusiness 即将到期预警
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSysToexpireBusiness(SysToexpireBusiness sysToexpireBusiness) {
        sysToexpireBusiness.setUpdateDate(new Date());
        sysToexpireBusiness.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        toExpireBusinessRepository.save(sysToexpireBusiness);
        return 1;
    }

    /**
     * 批量删除即将到期预警
     *
     * @param ids 需要删除的即将到期预警ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysToexpireBusinessByIds(List<String> ids) {
        ids.forEach(s->{
            toExpireBusinessRepository.deleteById(s);
        });
        return 1;
    }



}
