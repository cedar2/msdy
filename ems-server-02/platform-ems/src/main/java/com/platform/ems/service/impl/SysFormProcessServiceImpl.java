package com.platform.ems.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.domain.SysFormProcess;
import com.platform.ems.mapper.SysFormProcessMapper;
import com.platform.ems.service.ISysFormProcessService;
import com.platform.ems.util.MongodbUtil;

/**
 * 单据关联流程实例Service业务层处理
 *
 * @author qhq
 * @date 2021-09-06
 */
@Service
@SuppressWarnings("all")
public class SysFormProcessServiceImpl extends ServiceImpl<SysFormProcessMapper,SysFormProcess>  implements ISysFormProcessService {
    @Autowired
    private SysFormProcessMapper sysFormProcessMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "单据关联流程实例";
    /**
     * 查询单据关联流程实例
     *
     * @param id 单据关联流程实例ID
     * @return 单据关联流程实例
     */
    @Override
    public SysFormProcess selectSysFormProcessById(Long id) {
        SysFormProcess sysFormProcess = sysFormProcessMapper.selectSysFormProcessById(id);
        MongodbUtil.find(sysFormProcess);
        return  sysFormProcess;
    }

    /**
     * 查询单据关联流程实例列表
     *
     * @param sysFormProcess 单据关联流程实例
     * @return 单据关联流程实例
     */
    @Override
    public List<SysFormProcess> selectSysFormProcessList(SysFormProcess sysFormProcess) {
        return sysFormProcessMapper.selectSysFormProcessList(sysFormProcess);
    }

    /**
     * 新增单据关联流程实例
     * 需要注意编码重复校验
     * ps:添加删除逻辑保证更新效果
     * @param sysFormProcess 单据关联流程实例
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSysFormProcess(SysFormProcess sysFormProcess) {
    	QueryWrapper<SysFormProcess> queryWrapper = new QueryWrapper<SysFormProcess>();
    	queryWrapper.eq("form_id", sysFormProcess.getFormId());
    	queryWrapper.eq("form_type", sysFormProcess.getFormType());
    	List<SysFormProcess> list = sysFormProcessMapper.selectList(queryWrapper);
    	if(list.size()>0) {
    		sysFormProcess.setProcessInstanceId(list.get(0).getProcessInstanceId());
    		sysFormProcess.setClientId(list.get(0).getClientId());
        	sysFormProcess.setCreateById(list.get(0).getCreateById());
        	sysFormProcess.setCreateDate(list.get(0).getCreateDate());
        	sysFormProcess.setCreatorAccount(list.get(0).getCreatorAccount());
    		sysFormProcessMapper.delete(queryWrapper);
    	}
        int row= sysFormProcessMapper.insertSysFormProcess(sysFormProcess);
//        if(row>0){
//            //插入日志
//            List<OperMsg> msgList=new ArrayList<>();
//            MongodbUtil.insertUserLog(sysFormProcess.getId(), BusinessType.INSERT.ordinal(), msgList,TITLE);
//        }
        return row;
    }

    /**
     * 修改单据关联流程实例
     *
     * @param sysFormProcess 单据关联流程实例
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSysFormProcess(SysFormProcess sysFormProcess) {
        SysFormProcess response = sysFormProcessMapper.selectSysFormProcessById(sysFormProcess.getId());
        int row=sysFormProcessMapper.updateById(sysFormProcess);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(sysFormProcess.getId(), BusinessType.UPDATE.ordinal(), response,sysFormProcess,TITLE);
        }
        return row;
    }

    /**
     * 变更单据关联流程实例
     *
     * @param sysFormProcess 单据关联流程实例
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeSysFormProcess(SysFormProcess sysFormProcess) {
        SysFormProcess response = sysFormProcessMapper.selectSysFormProcessById(sysFormProcess.getId());
                                                    int row=sysFormProcessMapper.updateAllById(sysFormProcess);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(sysFormProcess.getId(), BusinessType.CHANGE.ordinal(), response,sysFormProcess,TITLE);
        }
        return row;
    }

    /**
     * 批量删除单据关联流程实例
     *
     * @param ids 需要删除的单据关联流程实例ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysFormProcessByIds(List<Long> ids) {
        return sysFormProcessMapper.deleteBatchIds(ids);
    }


}
