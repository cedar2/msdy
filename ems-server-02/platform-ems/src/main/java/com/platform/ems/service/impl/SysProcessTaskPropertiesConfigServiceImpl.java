package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.service.ISysProcessTaskPropertiesConfigService;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysProcessTaskPropertiesConfig;
import com.platform.system.mapper.SysProcessTaskPropertiesConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 流程节点属性配置Service业务层处理
 *
 * @author qhq
 * @date 2021-10-11
 */
@Service
@SuppressWarnings("all")
public class SysProcessTaskPropertiesConfigServiceImpl extends ServiceImpl<SysProcessTaskPropertiesConfigMapper,SysProcessTaskPropertiesConfig>  implements ISysProcessTaskPropertiesConfigService {
    @Autowired
    private SysProcessTaskPropertiesConfigMapper sysProcessTaskPropertiesConfigMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "【请填写功能名称】";
    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public SysProcessTaskPropertiesConfig selectSysProcessTaskPropertiesConfigById(Long id) {
        SysProcessTaskPropertiesConfig sysProcessTaskPropertiesConfig = sysProcessTaskPropertiesConfigMapper.selectSysProcessTaskPropertiesConfigById(id);
        MongodbUtil.find(sysProcessTaskPropertiesConfig);
        return  sysProcessTaskPropertiesConfig;
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param sysProcessTaskPropertiesConfig 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<SysProcessTaskPropertiesConfig> selectSysProcessTaskPropertiesConfigList(SysProcessTaskPropertiesConfig sysProcessTaskPropertiesConfig) {
        return sysProcessTaskPropertiesConfigMapper.selectSysProcessTaskPropertiesConfigList(sysProcessTaskPropertiesConfig);
    }

    /**
     * 新增【请填写功能名称】
     * 需要注意编码重复校验
     * @param sysProcessTaskPropertiesConfig 【请填写功能名称】
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSysProcessTaskPropertiesConfig(SysProcessTaskPropertiesConfig sysProcessTaskPropertiesConfig) {
        int row= sysProcessTaskPropertiesConfigMapper.insert(sysProcessTaskPropertiesConfig);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(sysProcessTaskPropertiesConfig.getId(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param sysProcessTaskPropertiesConfig 【请填写功能名称】
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSysProcessTaskPropertiesConfig(SysProcessTaskPropertiesConfig sysProcessTaskPropertiesConfig) {
        SysProcessTaskPropertiesConfig response = sysProcessTaskPropertiesConfigMapper.selectSysProcessTaskPropertiesConfigById(sysProcessTaskPropertiesConfig.getId());
        int row=sysProcessTaskPropertiesConfigMapper.updateById(sysProcessTaskPropertiesConfig);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(sysProcessTaskPropertiesConfig.getId(), BusinessType.UPDATE.ordinal(), response,sysProcessTaskPropertiesConfig,TITLE);
        }
        return row;
    }

    /**
     * 变更【请填写功能名称】
     *
     * @param sysProcessTaskPropertiesConfig 【请填写功能名称】
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeSysProcessTaskPropertiesConfig(SysProcessTaskPropertiesConfig sysProcessTaskPropertiesConfig) {
        SysProcessTaskPropertiesConfig response = sysProcessTaskPropertiesConfigMapper.selectSysProcessTaskPropertiesConfigById(sysProcessTaskPropertiesConfig.getId());
                                                    int row=sysProcessTaskPropertiesConfigMapper.updateAllById(sysProcessTaskPropertiesConfig);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(sysProcessTaskPropertiesConfig.getId(), BusinessType.CHANGE.ordinal(), response,sysProcessTaskPropertiesConfig,TITLE);
        }
        return row;
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysProcessTaskPropertiesConfigByIds(List<Long> ids) {
        return sysProcessTaskPropertiesConfigMapper.deleteBatchIds(ids);
    }

    /**
    * 启用/停用
    * @param sysProcessTaskPropertiesConfig
    * @return
    */
    @Override
    public int changeStatus(SysProcessTaskPropertiesConfig sysProcessTaskPropertiesConfig){
        int row=0;
        Long[] sids=sysProcessTaskPropertiesConfig.getIdList();
        if(sids!=null&&sids.length>0){
            row=sysProcessTaskPropertiesConfigMapper.update(null, new UpdateWrapper<SysProcessTaskPropertiesConfig>().lambda().set(SysProcessTaskPropertiesConfig::getStatus ,sysProcessTaskPropertiesConfig.getStatus() )
                    .in(SysProcessTaskPropertiesConfig::getId,sids));
            for(Long id:sids){
                sysProcessTaskPropertiesConfig.setId(id);
                row=sysProcessTaskPropertiesConfigMapper.updateById( sysProcessTaskPropertiesConfig);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=sysProcessTaskPropertiesConfig.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(sysProcessTaskPropertiesConfig.getId(), BusinessType.CHECK.ordinal(), msgList,TITLE,remark);
            }
        }
        return row;
    }

}
