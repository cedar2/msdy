package com.platform.ems.plug.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConProject;
import com.platform.ems.plug.mapper.ConProjectMapper;
import com.platform.ems.plug.service.IConProjectService;
import com.platform.ems.util.MongodbUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 项目Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-21
 */
@Service
@SuppressWarnings("all")
public class ConProjectServiceImpl extends ServiceImpl<ConProjectMapper,ConProject>  implements IConProjectService {
    @Autowired
    private ConProjectMapper conProjectMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "项目";
    /**
     * 查询项目
     *
     * @param sid 项目ID
     * @return 项目
     */
    @Override
    public ConProject selectConProjectById(Long sid) {
        ConProject conProject = conProjectMapper.selectConProjectById(sid);
        MongodbUtil.find(conProject);
        return  conProject;
    }

    /**
     * 查询项目列表
     *
     * @param conProject 项目
     * @return 项目
     */
    @Override
    public List<ConProject> selectConProjectList(ConProject conProject) {
        return conProjectMapper.selectConProjectList(conProject);
    }

    /**
     * 新增项目
     * 需要注意编码重复校验
     * @param conProject 项目
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConProject(ConProject conProject) {
        List<ConProject> codeList = conProjectMapper.selectList(new QueryWrapper<ConProject>().lambda()
                .eq(ConProject::getCode, conProject.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConProject> nameList = conProjectMapper.selectList(new QueryWrapper<ConProject>().lambda()
                .eq(ConProject::getName, conProject.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conProjectMapper.insert(conProject);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conProject.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改项目
     *
     * @param conProject 项目
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConProject(ConProject conProject) {
        ConProject response = conProjectMapper.selectConProjectById(conProject.getSid());
        int row=conProjectMapper.updateById(conProject);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conProject.getSid(), BusinessType.UPDATE.getValue(), response,conProject,TITLE);
        }
        return row;
    }

    /**
     * 变更项目
     *
     * @param conProject 项目
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConProject(ConProject conProject) {
        List<ConProject> nameList = conProjectMapper.selectList(new QueryWrapper<ConProject>().lambda()
                .eq(ConProject::getName, conProject.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conProject.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conProject.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConProject response = conProjectMapper.selectConProjectById(conProject.getSid());
        int row = conProjectMapper.updateAllById(conProject);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conProject.getSid(), BusinessType.CHANGE.getValue(), response, conProject, TITLE);
        }
        return row;
    }

    /**
     * 批量删除项目
     *
     * @param sids 需要删除的项目ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConProjectByIds(List<Long> sids) {
        return conProjectMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conProject
    * @return
    */
    @Override
    public int changeStatus(ConProject conProject){
        int row=0;
        Long[] sids=conProject.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conProject.setSid(id);
                row=conProjectMapper.updateById( conProject);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conProject.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conProject.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conProject
     * @return
     */
    @Override
    public int check(ConProject conProject){
        int row=0;
        Long[] sids=conProject.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conProject.setSid(id);
                row=conProjectMapper.updateById( conProject);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conProject.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
