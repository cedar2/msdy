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
import com.platform.ems.plug.domain.ConMeasureUnit;
import com.platform.ems.plug.mapper.ConMeasureUnitMapper;
import com.platform.ems.plug.service.IConMeasureUnitService;
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
 * 计量单位Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-21
 */
@Service
@SuppressWarnings("all")
public class ConMeasureUnitServiceImpl extends ServiceImpl<ConMeasureUnitMapper,ConMeasureUnit>  implements IConMeasureUnitService {
    @Autowired
    private ConMeasureUnitMapper conMeasureUnitMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "计量单位";
    /**
     * 查询计量单位
     *
     * @param sid 计量单位ID
     * @return 计量单位
     */
    @Override
    public ConMeasureUnit selectConMeasureUnitById(Long sid) {
        ConMeasureUnit conMeasureUnit = conMeasureUnitMapper.selectConMeasureUnitById(sid);
        MongodbUtil.find(conMeasureUnit);
        return  conMeasureUnit;
    }

    /**
     * 查询计量单位列表
     *
     * @param conMeasureUnit 计量单位
     * @return 计量单位
     */
    @Override
    public List<ConMeasureUnit> selectConMeasureUnitList(ConMeasureUnit conMeasureUnit) {
        return conMeasureUnitMapper.selectConMeasureUnitList(conMeasureUnit);
    }

    /**
     * 新增计量单位
     * 需要注意编码重复校验
     * @param conMeasureUnit 计量单位
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConMeasureUnit(ConMeasureUnit conMeasureUnit) {
        List<ConMeasureUnit> codeList = conMeasureUnitMapper.selectList(new QueryWrapper<ConMeasureUnit>().lambda()
                .eq(ConMeasureUnit::getCode, conMeasureUnit.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConMeasureUnit> nameList = conMeasureUnitMapper.selectList(new QueryWrapper<ConMeasureUnit>().lambda()
                .eq(ConMeasureUnit::getName, conMeasureUnit.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conMeasureUnitMapper.insert(conMeasureUnit);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conMeasureUnit.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改计量单位
     *
     * @param conMeasureUnit 计量单位
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConMeasureUnit(ConMeasureUnit conMeasureUnit) {
        ConMeasureUnit response = conMeasureUnitMapper.selectConMeasureUnitById(conMeasureUnit.getSid());
        int row=conMeasureUnitMapper.updateById(conMeasureUnit);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conMeasureUnit.getSid(), BusinessType.UPDATE.getValue(), response,conMeasureUnit,TITLE);
        }
        return row;
    }

    /**
     * 变更计量单位
     *
     * @param conMeasureUnit 计量单位
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConMeasureUnit(ConMeasureUnit conMeasureUnit) {
        List<ConMeasureUnit> nameList = conMeasureUnitMapper.selectList(new QueryWrapper<ConMeasureUnit>().lambda()
                .eq(ConMeasureUnit::getName, conMeasureUnit.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conMeasureUnit.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conMeasureUnit.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConMeasureUnit response = conMeasureUnitMapper.selectConMeasureUnitById(conMeasureUnit.getSid());
        int row = conMeasureUnitMapper.updateAllById(conMeasureUnit);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conMeasureUnit.getSid(), BusinessType.CHANGE.getValue(), response, conMeasureUnit, TITLE);
        }
        return row;
    }

    /**
     * 批量删除计量单位
     *
     * @param sids 需要删除的计量单位ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConMeasureUnitByIds(List<Long> sids) {
        return conMeasureUnitMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conMeasureUnit
    * @return
    */
    @Override
    public int changeStatus(ConMeasureUnit conMeasureUnit){
        int row=0;
        Long[] sids=conMeasureUnit.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conMeasureUnit.setSid(id);
                row=conMeasureUnitMapper.updateById( conMeasureUnit);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conMeasureUnit.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conMeasureUnit.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conMeasureUnit
     * @return
     */
    @Override
    public int check(ConMeasureUnit conMeasureUnit){
        int row=0;
        Long[] sids=conMeasureUnit.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conMeasureUnit.setSid(id);
                row=conMeasureUnitMapper.updateById( conMeasureUnit);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conMeasureUnit.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }

    //获取下拉框
    @Override
    public List<ConMeasureUnit> getConMeasureUnitList() {
        return conMeasureUnitMapper.getConMeasureUnitList();
    }

}
