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
import com.platform.ems.plug.domain.ConShipmentMode;
import com.platform.ems.plug.mapper.ConShipmentModeMapper;
import com.platform.ems.plug.service.IConShipmentModeService;
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
 * 配送方式Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@Service
@SuppressWarnings("all")
public class ConShipmentModeServiceImpl extends ServiceImpl<ConShipmentModeMapper,ConShipmentMode>  implements IConShipmentModeService {
    @Autowired
    private ConShipmentModeMapper conShipmentModeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "配送方式";
    /**
     * 查询配送方式
     *
     * @param sid 配送方式ID
     * @return 配送方式
     */
    @Override
    public ConShipmentMode selectConShipmentModeById(Long sid) {
        ConShipmentMode conShipmentMode = conShipmentModeMapper.selectConShipmentModeById(sid);
        MongodbUtil.find(conShipmentMode);
        return  conShipmentMode;
    }

    /**
     * 查询配送方式列表
     *
     * @param conShipmentMode 配送方式
     * @return 配送方式
     */
    @Override
    public List<ConShipmentMode> selectConShipmentModeList(ConShipmentMode conShipmentMode) {
        return conShipmentModeMapper.selectConShipmentModeList(conShipmentMode);
    }

    /**
     * 新增配送方式
     * 需要注意编码重复校验
     * @param conShipmentMode 配送方式
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConShipmentMode(ConShipmentMode conShipmentMode) {
        List<ConShipmentMode> codeList = conShipmentModeMapper.selectList(new QueryWrapper<ConShipmentMode>().lambda()
                .eq(ConShipmentMode::getCode, conShipmentMode.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConShipmentMode> nameList = conShipmentModeMapper.selectList(new QueryWrapper<ConShipmentMode>().lambda()
                .eq(ConShipmentMode::getName, conShipmentMode.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conShipmentModeMapper.insert(conShipmentMode);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conShipmentMode.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改配送方式
     *
     * @param conShipmentMode 配送方式
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConShipmentMode(ConShipmentMode conShipmentMode) {
        ConShipmentMode response = conShipmentModeMapper.selectConShipmentModeById(conShipmentMode.getSid());
        int row=conShipmentModeMapper.updateById(conShipmentMode);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conShipmentMode.getSid(), BusinessType.UPDATE.getValue(), response,conShipmentMode,TITLE);
        }
        return row;
    }

    /**
     * 变更配送方式
     *
     * @param conShipmentMode 配送方式
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConShipmentMode(ConShipmentMode conShipmentMode) {
        List<ConShipmentMode> nameList = conShipmentModeMapper.selectList(new QueryWrapper<ConShipmentMode>().lambda()
                .eq(ConShipmentMode::getName, conShipmentMode.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conShipmentMode.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conShipmentMode.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConShipmentMode response = conShipmentModeMapper.selectConShipmentModeById(conShipmentMode.getSid());
        int row = conShipmentModeMapper.updateAllById(conShipmentMode);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conShipmentMode.getSid(), BusinessType.CHANGE.getValue(), response, conShipmentMode, TITLE);
        }
        return row;
    }

    /**
     * 批量删除配送方式
     *
     * @param sids 需要删除的配送方式ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConShipmentModeByIds(List<Long> sids) {
        return conShipmentModeMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conShipmentMode
    * @return
    */
    @Override
    public int changeStatus(ConShipmentMode conShipmentMode){
        int row=0;
        Long[] sids=conShipmentMode.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conShipmentMode.setSid(id);
                row=conShipmentModeMapper.updateById( conShipmentMode);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conShipmentMode.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conShipmentMode.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conShipmentMode
     * @return
     */
    @Override
    public int check(ConShipmentMode conShipmentMode){
        int row=0;
        Long[] sids=conShipmentMode.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conShipmentMode.setSid(id);
                row=conShipmentModeMapper.updateById( conShipmentMode);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conShipmentMode.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }

    /**
     * 配送方式下拉框
     */
    @Override
    public List<ConShipmentMode> getList() {
        return conShipmentModeMapper.getList();
    }
}
