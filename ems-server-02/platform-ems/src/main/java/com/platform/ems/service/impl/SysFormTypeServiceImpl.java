package com.platform.ems.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.SysFormType;
import com.platform.ems.mapper.SysFormTypeMapper;
import com.platform.ems.service.ISysFormTypeService;
import com.platform.ems.util.MongodbUtil;

/**
 * 系统单据定义Service业务层处理
 *
 * @author qhq
 * @date 2021-09-06
 */
@Service
@SuppressWarnings("all")
public class SysFormTypeServiceImpl extends ServiceImpl<SysFormTypeMapper,SysFormType>  implements ISysFormTypeService {
    @Autowired
    private SysFormTypeMapper sysFormTypeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "系统单据定义";
    /**
     * 查询系统单据定义
     *
     * @param id 系统单据定义ID
     * @return 系统单据定义
     */
    @Override
    public SysFormType selectSysFormTypeById(Long id) {
        SysFormType sysFormType = sysFormTypeMapper.selectSysFormTypeById(id);
        MongodbUtil.find(sysFormType);
        return  sysFormType;
    }

    /**
     * 查询系统单据定义列表
     *
     * @param sysFormType 系统单据定义
     * @return 系统单据定义
     */
    @Override
    public List<SysFormType> selectSysFormTypeList(SysFormType sysFormType) {
        return sysFormTypeMapper.selectSysFormTypeList(sysFormType);
    }

    /**
     * 新增系统单据定义
     * 需要注意编码重复校验
     * @param sysFormType 系统单据定义
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSysFormType(SysFormType sysFormType) {
        int row= sysFormTypeMapper.insert(sysFormType);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(sysFormType.getId(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改系统单据定义
     *
     * @param sysFormType 系统单据定义
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSysFormType(SysFormType sysFormType) {
        SysFormType response = sysFormTypeMapper.selectSysFormTypeById(sysFormType.getId());
        int row=sysFormTypeMapper.updateById(sysFormType);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(sysFormType.getId(), BusinessType.UPDATE.ordinal(), response,sysFormType,TITLE);
        }
        return row;
    }

    /**
     * 变更系统单据定义
     *
     * @param sysFormType 系统单据定义
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeSysFormType(SysFormType sysFormType) {
        SysFormType response = sysFormTypeMapper.selectSysFormTypeById(sysFormType.getId());
                    int row=sysFormTypeMapper.updateAllById(sysFormType);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(sysFormType.getId(), BusinessType.CHANGE.ordinal(), response,sysFormType,TITLE);
        }
        return row;
    }

    /**
     * 批量删除系统单据定义
     *
     * @param ids 需要删除的系统单据定义ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysFormTypeByIds(List<Long> ids) {
        return sysFormTypeMapper.deleteBatchIds(ids);
    }

}
