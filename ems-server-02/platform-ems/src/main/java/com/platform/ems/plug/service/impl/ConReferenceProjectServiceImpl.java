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
import com.platform.ems.plug.domain.ConReferenceProject;
import com.platform.ems.plug.mapper.ConReferenceProjectMapper;
import com.platform.ems.plug.service.IConReferenceProjectService;
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
 * 业务归属项目Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-21
 */
@Service
@SuppressWarnings("all")
public class ConReferenceProjectServiceImpl extends ServiceImpl<ConReferenceProjectMapper,ConReferenceProject>  implements IConReferenceProjectService {
    @Autowired
    private ConReferenceProjectMapper conReferenceProjectMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "业务归属项目";
    /**
     * 查询业务归属项目
     *
     * @param sid 业务归属项目ID
     * @return 业务归属项目
     */
    @Override
    public ConReferenceProject selectConReferenceProjectById(Long sid) {
        ConReferenceProject conReferenceProject = conReferenceProjectMapper.selectConReferenceProjectById(sid);
        MongodbUtil.find(conReferenceProject);
        return  conReferenceProject;
    }

    /**
     * 查询业务归属项目列表
     *
     * @param conReferenceProject 业务归属项目
     * @return 业务归属项目
     */
    @Override
    public List<ConReferenceProject> selectConReferenceProjectList(ConReferenceProject conReferenceProject) {
        return conReferenceProjectMapper.selectConReferenceProjectList(conReferenceProject);
    }

    /**
     * 新增业务归属项目
     * 需要注意编码重复校验
     * @param conReferenceProject 业务归属项目
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConReferenceProject(ConReferenceProject conReferenceProject) {
        List<ConReferenceProject> codeList = conReferenceProjectMapper.selectList(new QueryWrapper<ConReferenceProject>().lambda()
                .eq(ConReferenceProject::getCode, conReferenceProject.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConReferenceProject> nameList = conReferenceProjectMapper.selectList(new QueryWrapper<ConReferenceProject>().lambda()
                .eq(ConReferenceProject::getName, conReferenceProject.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conReferenceProjectMapper.insert(conReferenceProject);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conReferenceProject.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改业务归属项目
     *
     * @param conReferenceProject 业务归属项目
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConReferenceProject(ConReferenceProject conReferenceProject) {
        ConReferenceProject response = conReferenceProjectMapper.selectConReferenceProjectById(conReferenceProject.getSid());
        int row=conReferenceProjectMapper.updateById(conReferenceProject);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conReferenceProject.getSid(), BusinessType.UPDATE.getValue(), response,conReferenceProject,TITLE);
        }
        return row;
    }

    /**
     * 变更业务归属项目
     *
     * @param conReferenceProject 业务归属项目
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConReferenceProject(ConReferenceProject conReferenceProject) {
        List<ConReferenceProject> nameList = conReferenceProjectMapper.selectList(new QueryWrapper<ConReferenceProject>().lambda()
                .eq(ConReferenceProject::getName, conReferenceProject.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conReferenceProject.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conReferenceProject.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConReferenceProject response = conReferenceProjectMapper.selectConReferenceProjectById(conReferenceProject.getSid());
        int row = conReferenceProjectMapper.updateAllById(conReferenceProject);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conReferenceProject.getSid(), BusinessType.CHANGE.getValue(), response, conReferenceProject, TITLE);
        }
        return row;
    }

    /**
     * 批量删除业务归属项目
     *
     * @param sids 需要删除的业务归属项目ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConReferenceProjectByIds(List<Long> sids) {
        return conReferenceProjectMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conReferenceProject
    * @return
    */
    @Override
    public int changeStatus(ConReferenceProject conReferenceProject){
        int row=0;
        Long[] sids=conReferenceProject.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conReferenceProject.setSid(id);
                row=conReferenceProjectMapper.updateById( conReferenceProject);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conReferenceProject.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conReferenceProject.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conReferenceProject
     * @return
     */
    @Override
    public int check(ConReferenceProject conReferenceProject){
        int row=0;
        Long[] sids=conReferenceProject.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conReferenceProject.setSid(id);
                row=conReferenceProjectMapper.updateById( conReferenceProject);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conReferenceProject.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
