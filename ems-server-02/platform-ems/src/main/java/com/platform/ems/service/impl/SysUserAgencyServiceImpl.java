package com.platform.ems.service.impl;

import java.util.List;
import java.util.ArrayList;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.ems.mapper.SysUserAgencyMapper;
import com.platform.ems.domain.SysUserAgency;
import com.platform.ems.service.ISysUserAgencyService;

/**
 * 账号代办设置Service业务层处理
 *
 * @author qhq
 * @date 2021-10-18
 */
@Service
@SuppressWarnings("all")
public class SysUserAgencyServiceImpl extends ServiceImpl<SysUserAgencyMapper,SysUserAgency>  implements ISysUserAgencyService {
    @Autowired
    private SysUserAgencyMapper sysUserAgencyMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "账号代办设置";
    /**
     * 查询账号代办设置
     *
     * @param userAgencySid 账号代办设置ID
     * @return 账号代办设置
     */
    @Override
    public SysUserAgency selectSysUserAgencyById(Long userAgencySid) {
        SysUserAgency sysUserAgency = sysUserAgencyMapper.selectSysUserAgencyById(userAgencySid);
        MongodbUtil.find(sysUserAgency);
        return  sysUserAgency;
    }

    /**
     * 查询账号代办设置列表
     *
     * @param sysUserAgency 账号代办设置
     * @return 账号代办设置
     */
    @Override
    public List<SysUserAgency> selectSysUserAgencyList(SysUserAgency sysUserAgency) {
        return sysUserAgencyMapper.selectSysUserAgencyList(sysUserAgency);
    }

    /**
     * 新增账号代办设置
     * 需要注意编码重复校验
     * @param sysUserAgency 账号代办设置
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSysUserAgency(SysUserAgency sysUserAgency) {
        int row = 0;
        row = sysUserAgencyMapper.insert(sysUserAgency);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(sysUserAgency.getUserAgencySid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改账号代办设置
     *
     * @param sysUserAgency 账号代办设置
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSysUserAgency(SysUserAgency sysUserAgency) {
        SysUserAgency response = sysUserAgencyMapper.selectSysUserAgencyById(sysUserAgency.getUserAgencySid());
        int row=sysUserAgencyMapper.updateById(sysUserAgency);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(sysUserAgency.getUserAgencySid(), BusinessType.UPDATE.ordinal(), response,sysUserAgency,TITLE);
        }
        return row;
    }

    /**
     * 变更账号代办设置
     *
     * @param sysUserAgency 账号代办设置
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeSysUserAgency(SysUserAgency sysUserAgency) {
        SysUserAgency response = sysUserAgencyMapper.selectSysUserAgencyById(sysUserAgency.getUserAgencySid());
                                                                                    int row=sysUserAgencyMapper.updateAllById(sysUserAgency);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(sysUserAgency.getUserAgencySid(), BusinessType.CHANGE.ordinal(), response,sysUserAgency,TITLE);
        }
        return row;
    }

    /**
     * 批量删除账号代办设置
     *
     * @param userAgencySids 需要删除的账号代办设置ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysUserAgencyByIds(List<Long> userAgencySids) {
        return sysUserAgencyMapper.deleteBatchIds(userAgencySids);
    }

    /**
    * 启用/停用
    * @param sysUserAgency
    * @return
    */
    @Override
    public int changeStatus(SysUserAgency sysUserAgency){
        int row=0;
        Long[] sids=sysUserAgency.getUserAgencySidList();
        if(sids!=null&&sids.length>0){
//            row=sysUserAgencyMapper.update(null, new UpdateWrapper<SysUserAgency>().lambda().set(SysUserAgency::getStatus ,sysUserAgency.getStatus() )
//                    .in(SysUserAgency::getUserAgencySid,sids));
            for(Long id:sids){
                sysUserAgency.setUserAgencySid(id);
                row=sysUserAgencyMapper.updateById( sysUserAgency);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
//                String remark=sysUserAgency.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
//                MongodbUtil.insertUserLog(sysUserAgency.getUserAgencySid(), BusinessType.CHECK.ordinal(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param sysUserAgency
     * @return
     */
    @Override
    public int check(SysUserAgency sysUserAgency){
        int row=0;
        Long[] sids=sysUserAgency.getUserAgencySidList();
        if(sids!=null&&sids.length>0){
            row=sysUserAgencyMapper.update(null,new UpdateWrapper<SysUserAgency>().lambda().set(SysUserAgency::getHandleStatus ,ConstantsEms.CHECK_STATUS)
                    .in(SysUserAgency::getUserAgencySid,sids));
            for(Long id:sids){
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }


}
