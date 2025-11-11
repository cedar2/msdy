package com.platform.ems.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.platform.common.core.domain.R;
import com.platform.common.exception.TokenException;
import com.platform.common.utils.StringUtils;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.SalSalesOrder;
import com.platform.system.domain.SysTodoTask;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.SalSalesOrderMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.ems.service.ISysTodoTaskService;
import com.platform.ems.service.document.TodoTaskListRepository;
import com.platform.api.service.RemoteMenuService;
import com.platform.common.core.domain.entity.SysMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 待办事项列Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-29
 */
@Service
@SuppressWarnings("all")
public class SysTodoTaskServiceImpl implements ISysTodoTaskService {
    @Autowired
    private TodoTaskListRepository todoTaskListRepository;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private RemoteMenuService remoteMenuService;
    @Autowired
    private SalSalesOrderMapper salSalesOrderMapper;

    private static final String TITLE = "待办事项列";

    /**
     * 查询待办事项列
     *
     * @param todoTaskSid 待办事项列ID
     * @return 待办事项列
     */
    @Override
    public SysTodoTask selectSysTodoTaskListById(String id) {
        SysTodoTask todoTask = new SysTodoTask();
        todoTask.setId(id);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
                .withIgnoreCase(true) //改变默认大小写忽略方式：忽略大小写
                // .withMatcher("title", ExampleMatcher.GenericPropertyMatchers.contains()) //采用“包含匹配”的方式查询
                .withIgnorePaths("pageNum", "pageSize");  //忽略属性，不参与查询;
        Example<SysTodoTask> example = Example.of(todoTask, matcher);
        Optional<SysTodoTask> menuOptional = todoTaskListRepository.findOne(example);
        menuOptional.ifPresent(o -> {
            BeanUtil.copyProperties(o, todoTask, true);
        });
        return todoTask;
    }

    /**
     * 查询待办事项列列表 (用户工作台)
     *
     * @param sysTodoTaskList 待办事项列
     * @return 待办事项列
     */
    @Override
    public List<SysTodoTask> selectSysTodoTaskListTable(SysTodoTask sysTodoTask) {
        if (!"10000".equals(sysTodoTask.getClientId()) && !"Y".equals(sysTodoTask.getNotAutoUser())){
            sysTodoTask.setUserId(ApiThreadLocalUtil.get().getUserid());
        }
        List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
        if (CollectionUtil.isNotEmpty(sysTodoTaskList)){
            for (SysTodoTask todoTask : sysTodoTaskList) {
                Long menuId = todoTask.getMenuId();
                String title = todoTask.getTitle();
                if(title.contains(ConstantsEms.TODO_SALE_ORDER)){
                    SalSalesOrder salSalesOrder = salSalesOrderMapper.selectById(todoTask.getDocumentSid());
                    if(salSalesOrder!=null){
                        String materialCategory = salSalesOrder.getMaterialCategory();
                        if(ConstantsEms.MATERIAL_CATEGORY_WL.equals(materialCategory)){
                            Long id = sysTodoTaskMapper.getMenuId(ConstantsEms.TODO_SALE_ORDER_MENU_WL);
                            todoTask.setMenuId(id);
                        }else{
                            Long id = sysTodoTaskMapper.getMenuId(ConstantsEms.TODO_SALE_ORDER_MENU_SP);
                            todoTask.setMenuId(id);
                        }
                    }
                }
                if(title.contains(ConstantsEms.TODO_SALE_CONTRACT)){
                    Long id = sysTodoTaskMapper.getMenuId(ConstantsEms.TODO_SALE_CONTRACT_MENU);
                    todoTask.setMenuId(id);
                }
                if(title.contains(ConstantsEms.TODO_SALE_PRICE)){
                    Long id = sysTodoTaskMapper.getMenuId(ConstantsEms.TODO_SALE_PRICE_MENU);
                    todoTask.setMenuId(id);
                }
                if (todoTask.getMenuId() != null){
                    SysTodoTask task = new SysTodoTask();
                    task.setMenuId(todoTask.getMenuId());
                    String path = todoTask.getPath();
                    path = getParent(todoTask, task, path);
                    R<SysMenu> menuData = remoteMenuService.getInfo(task.getMenuId());
                    SysMenu menu = menuData.getData();
                    if (menu != null) {
                        path = menu.getPath() + "/" + path;
                    }
                    todoTask.setPath(path.substring(0, path.lastIndexOf("/")));
                }
            }
        }
        return sysTodoTaskList;
    }

    /**
     * 查询待办事项列报表
     *
     * @param sysTodoTaskList 待办事项列
     * @return 待办事项列
     */
    @Override
    public List<SysTodoTask> selectSysTodoTaskReport(SysTodoTask sysTodoTask) {
        List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
        if (CollectionUtil.isNotEmpty(sysTodoTaskList)){
            for (SysTodoTask todoTask : sysTodoTaskList) {
                Long menuId = todoTask.getMenuId();
                if (menuId != null){
                    SysTodoTask task = new SysTodoTask();
                    task.setMenuId(todoTask.getMenuId());
                    String path = todoTask.getPath();
                    path = getParent(todoTask, task, path);
                    R<SysMenu> menuData = remoteMenuService.getInfo(task.getMenuId());
                    SysMenu menu = menuData.getData();
                    if (menu != null) {
                        path = menu.getPath() + "/" + path;
                    }
                    todoTask.setPath(path.substring(0, path.lastIndexOf("/")));
                }
            }
        }
        return sysTodoTaskList;
    }

    /**
     * 递归父节点
     */
    private String getParent(SysTodoTask todoTask, SysTodoTask task, String path) {
        if (StringUtils.isEmpty(path)){
            path = "";
        }
        R<SysMenu> menuData = remoteMenuService.getInfo(task.getMenuId());
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
                task.setMenuId(parentMenu.getParentId());
                getParent(todoTask, task, path);
            }
        }
//        todoTask.setPath(path);
        return path;
    }

    @Override
    public List<SysTodoTask> selectSysTodoTaskLists(SysTodoTask sysTodoTaskList) {
        Query query = new Query();
        String clientId = ApiThreadLocalUtil.get().getClientId();
        if (null==clientId) {
            throw new TokenException();
        }
        if(!ApiThreadLocalUtil.get().getUserid().equals(1L)){
            query.addCriteria(Criteria.where("clientId").is(clientId));
            query.addCriteria(Criteria.where("userId").is(ApiThreadLocalUtil.get().getUserid()));
        }
        List<SysTodoTask> todoTaskLists = mongoTemplate.find(query, SysTodoTask.class);
        return todoTaskLists;
    }

    /**
     * 新增待办事项列
     * 需要注意编码重复校验
     *
     * @param sysTodoTaskList 待办事项列
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSysTodoTaskList(SysTodoTask sysTodoTask) {
        String clientId=ApiThreadLocalUtil.get().getClientId();
        if(null==clientId){
            throw new TokenException();
        }
        sysTodoTask.setClientId(clientId);
        sysTodoTask.setUserId(ApiThreadLocalUtil.get().getUserid());
        sysTodoTask.setCreateDate(new Date());
        sysTodoTask.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
        todoTaskListRepository.save(sysTodoTask);
        return 1;
    }

    /**
     * 修改待办事项列
     *
     * @param sysTodoTaskList 待办事项列
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSysTodoTaskList(SysTodoTask sysTodoTask) {
        sysTodoTask.setUpdateDate(new Date());
        sysTodoTask.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        todoTaskListRepository.save(sysTodoTask);
        return 1;
    }


    /**
     * 批量删除待办事项列
     *
     * @param todoTaskSids 需要删除的待办事项列ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysTodoTaskListByIds(List<String> ids) {
        ids.forEach(s->{
            todoTaskListRepository.deleteById(s);
        });
        return 1;
    }

    /**
     * 新增待办事项
     *
     * @param sysTodoTask 待办事项
     * @return 结果
     */
    @Override
    public int insertSysTodoTask(SysTodoTask sysTodoTask) {
        int row = 0;
        if (sysTodoTask != null) {
            row = sysTodoTaskMapper.insert(sysTodoTask);
        }
        return row;
    }

    /**
     * 新增待办事项
     */
    @Override
    public int insertSysTodoTaskMenu(SysTodoTask sysTodoTask, String tableName) {
        // 获取菜单id
        SysMenu menu = new SysMenu();
        menu.setMenuName(tableName);
        menu = remoteMenuService.getInfoByName(menu).getData();
        if (menu != null && menu.getMenuId() != null) {
            sysTodoTask.setMenuId(menu.getMenuId());
        }
        return sysTodoTaskMapper.insert(sysTodoTask);
    }

    /**
     * 根据条件批量删除待办事项列
     *
     * @param sids， handleStatus， tableName
     * @return 结果
     */
    @Override
    public int deleteSysTodoTaskList(Long[] sids, String handleStatus, String tableName) {
        int row = 0;
        if (sids == null || sids.length == 0) {
            return row;
        }
        QueryWrapper<SysTodoTask> todoWrapper = new QueryWrapper<>();
        if (StrUtil.isNotBlank(tableName)) {
            todoWrapper.lambda().eq(SysTodoTask::getTableName, tableName);
        }
        todoWrapper.lambda().in(SysTodoTask::getDocumentSid, sids);
        if (ConstantsEms.CHECK_STATUS.equals(handleStatus) || HandleStatus.RETURNED.getCode().equals(handleStatus)
                || ConstantsEms.SUBMIT_STATUS.equals(handleStatus)) {
            if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
                // 清除 待批待办
                todoWrapper.lambda().in(SysTodoTask::getTaskCategory,
                        new String[]{ConstantsEms.TODO_TASK_DB, ConstantsEms.TODO_TASK_DP});
            } else if (ConstantsEms.SUBMIT_STATUS.equals(handleStatus)) {
                // 清除 待办
                todoWrapper.lambda().in(SysTodoTask::getTaskCategory,
                        new String[]{ConstantsEms.TODO_TASK_DB, ConstantsEms.TODO_TASK_DP});
            } else if (HandleStatus.RETURNED.getCode().equals(handleStatus)) {
                // 清除 待批
                todoWrapper.lambda().eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DP);
            }
        }
        row = sysTodoTaskMapper.delete(todoWrapper);
        return row;
    }

}
