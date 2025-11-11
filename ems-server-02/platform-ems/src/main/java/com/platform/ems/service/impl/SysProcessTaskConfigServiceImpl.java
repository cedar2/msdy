package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.service.ISysProcessTaskConfigService;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysProcessTaskConfig;
import com.platform.system.mapper.SysProcessTaskConfigMapper;
import com.platform.system.mapper.SysProcessTaskPropertiesConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 流程任务节点个性化配置参数Service业务层处理
 *
 * @author qhq
 * @date 2021-10-11
 */
@Service
@SuppressWarnings("all")
public class SysProcessTaskConfigServiceImpl extends ServiceImpl<SysProcessTaskConfigMapper,SysProcessTaskConfig>  implements ISysProcessTaskConfigService {
    @Autowired
    private SysProcessTaskConfigMapper sysProcessTaskConfigMapper;
    @Autowired
    private SysProcessTaskPropertiesConfigMapper propertiesConfigMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "流程任务节点个性化配置参数";
    /**
     * 查询流程任务节点个性化配置参数
     *
     * @param id 流程任务节点个性化配置参数ID
     * @return 流程任务节点个性化配置参数
     */
    @Override
    public SysProcessTaskConfig selectSysProcessTaskConfigById(Long id) {
        SysProcessTaskConfig sysProcessTaskConfig = sysProcessTaskConfigMapper.selectSysProcessTaskConfigById(id);
        MongodbUtil.find(sysProcessTaskConfig);
        return  sysProcessTaskConfig;
    }

    /**
     * 查询流程任务节点个性化配置参数列表
     *
     * @param sysProcessTaskConfig 流程任务节点个性化配置参数
     * @return 流程任务节点个性化配置参数
     */
    @Override
    public List<SysProcessTaskConfig> selectSysProcessTaskConfigList(SysProcessTaskConfig sysProcessTaskConfig) {
        return sysProcessTaskConfigMapper.selectSysProcessTaskConfigList(sysProcessTaskConfig);
    }

    /**
     * 新增流程任务节点个性化配置参数
     * 需要注意编码重复校验
     * @param sysProcessTaskConfig 流程任务节点个性化配置参数
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSysProcessTaskConfig(SysProcessTaskConfig sysProcessTaskConfig) {
        int row= sysProcessTaskConfigMapper.insert(sysProcessTaskConfig);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(sysProcessTaskConfig.getId(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改流程任务节点个性化配置参数
     *
     * @param sysProcessTaskConfig 流程任务节点个性化配置参数
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSysProcessTaskConfig(SysProcessTaskConfig sysProcessTaskConfig) {
        SysProcessTaskConfig response = sysProcessTaskConfigMapper.selectSysProcessTaskConfigById(sysProcessTaskConfig.getId());
        int row=sysProcessTaskConfigMapper.updateById(sysProcessTaskConfig);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(sysProcessTaskConfig.getId(), BusinessType.UPDATE.ordinal(), response,sysProcessTaskConfig,TITLE);
        }
        return row;
    }

    /**
     * 变更流程任务节点个性化配置参数
     *
     * @param sysProcessTaskConfig 流程任务节点个性化配置参数
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeSysProcessTaskConfig(SysProcessTaskConfig sysProcessTaskConfig) {
        SysProcessTaskConfig response = sysProcessTaskConfigMapper.selectSysProcessTaskConfigById(sysProcessTaskConfig.getId());
                                                                int row=sysProcessTaskConfigMapper.updateAllById(sysProcessTaskConfig);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(sysProcessTaskConfig.getId(), BusinessType.CHANGE.ordinal(), response,sysProcessTaskConfig,TITLE);
        }
        return row;
    }

    /**
     * 批量删除流程任务节点个性化配置参数
     *
     * @param ids 需要删除的流程任务节点个性化配置参数ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysProcessTaskConfigByIds(List<Long> ids) {
        return sysProcessTaskConfigMapper.deleteBatchIds(ids);
    }

    /**
    * 启用/停用
    * @param sysProcessTaskConfig
    * @return
    */
    @Override
    public int changeStatus(SysProcessTaskConfig sysProcessTaskConfig){
        int row=0;
        Long[] sids=sysProcessTaskConfig.getIdList();
        if(sids!=null&&sids.length>0){
            row=sysProcessTaskConfigMapper.update(null, new UpdateWrapper<SysProcessTaskConfig>().lambda().set(SysProcessTaskConfig::getStatus ,sysProcessTaskConfig.getStatus() )
                    .in(SysProcessTaskConfig::getId,sids));
            for(Long id:sids){
                sysProcessTaskConfig.setId(id);
                row=sysProcessTaskConfigMapper.updateById( sysProcessTaskConfig);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=sysProcessTaskConfig.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(sysProcessTaskConfig.getId(), BusinessType.CHECK.ordinal(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param sysProcessTaskConfig
     * @return
     */
    @Override
    public int check(SysProcessTaskConfig sysProcessTaskConfig){
        int row=0;
        /*Long[] sids=sysProcessTaskConfig.getIdList();
        if(sids!=null&&sids.length>0){
            row=sysProcessTaskConfigMapper.update(null,new UpdateWrapper<SysProcessTaskConfig>().lambda().set(SysProcessTaskConfig::getHandleStatus ,ConstantsEms.CHECK_STATUS)
                    .in(SysProcessTaskConfig::getId,sids));
            for(Long id:sids){
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }*/
        return row;
    }


}
