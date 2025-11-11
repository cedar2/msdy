package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.SysAuthorityObjectField;
import com.platform.ems.mapper.SysAuthorityObjectFieldMapper;
import com.platform.ems.service.ISysAuthorityObjectFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 权限对象-字段明细Service业务层处理
 *
 * @author chenkw
 * @date 2021-12-28
 */
@Service
@SuppressWarnings("all")
public class SysAuthorityObjectFieldServiceImpl extends ServiceImpl<SysAuthorityObjectFieldMapper, SysAuthorityObjectField> implements ISysAuthorityObjectFieldService {
    @Autowired
    private SysAuthorityObjectFieldMapper sysAuthorityObjectFieldMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "权限对象-字段明细";

    /**
     * 查询权限对象-字段明细
     *
     * @param sid 权限对象-字段明细ID
     * @return 权限对象-字段明细
     */
    @Override
    public SysAuthorityObjectField selectSysAuthorityObjectFieldById(Long sid) {
        return sysAuthorityObjectFieldMapper.selectSysAuthorityObjectFieldById(sid);
    }

    /**
     * 查询权限对象-字段明细列表
     *
     * @param sysAuthorityObjectField 权限对象-字段明细
     * @return 权限对象-字段明细
     */
    @Override
    public List<SysAuthorityObjectField> selectSysAuthorityObjectFieldList(SysAuthorityObjectField sysAuthorityObjectField) {
        return sysAuthorityObjectFieldMapper.selectSysAuthorityObjectFieldList(sysAuthorityObjectField);
    }

    /**
     * 新增权限对象-字段明细
     * 需要注意编码重复校验
     *
     * @param sysAuthorityObjectField 权限对象-字段明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSysAuthorityObjectField(SysAuthorityObjectField sysAuthorityObjectField) {
        int row = sysAuthorityObjectFieldMapper.insert(sysAuthorityObjectField);
        return row;
    }

    /**
     * 修改权限对象-字段明细
     *
     * @param sysAuthorityObjectField 权限对象-字段明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSysAuthorityObjectField(SysAuthorityObjectField sysAuthorityObjectField) {
        SysAuthorityObjectField response = sysAuthorityObjectFieldMapper.selectSysAuthorityObjectFieldById(
                sysAuthorityObjectField.getAuthorityObjectFieldSid());
        int row = sysAuthorityObjectFieldMapper.updateById(sysAuthorityObjectField);
        return row;
    }

    /**
     * 变更权限对象-字段明细
     *
     * @param sysAuthorityObjectField 权限对象-字段明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeSysAuthorityObjectField(SysAuthorityObjectField sysAuthorityObjectField) {
        SysAuthorityObjectField response = sysAuthorityObjectFieldMapper.selectSysAuthorityObjectFieldById(
                sysAuthorityObjectField.getAuthorityObjectFieldSid());
        int row = sysAuthorityObjectFieldMapper.updateAllById(sysAuthorityObjectField);
        return row;
    }

    /**
     * 批量删除权限对象-字段明细
     *
     * @param authorityObjectFieldSids 需要删除的权限对象-字段明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysAuthorityObjectFieldByIds(List<Long> authorityObjectFieldSids) {
        return sysAuthorityObjectFieldMapper.deleteBatchIds(authorityObjectFieldSids);
    }

}
