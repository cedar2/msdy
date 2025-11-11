package com.platform.ems.plug.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConDocTypeSaleDeduction;
import com.platform.ems.plug.mapper.ConDocTypeSaleDeductionMapper;
import com.platform.ems.plug.service.IConDocTypeSaleDeductionService;
import com.platform.ems.util.MongodbUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 单据类型_销售扣款单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConDocTypeSaleDeductionServiceImpl extends ServiceImpl<ConDocTypeSaleDeductionMapper,ConDocTypeSaleDeduction>  implements IConDocTypeSaleDeductionService {
    @Autowired
    private ConDocTypeSaleDeductionMapper conDocTypeSaleDeductionMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "单据类型_销售扣款单";
    /**
     * 查询单据类型_销售扣款单
     *
     * @param sid 单据类型_销售扣款单ID
     * @return 单据类型_销售扣款单
     */
    @Override
    public ConDocTypeSaleDeduction selectConDocTypeSaleDeductionById(Long sid) {
        ConDocTypeSaleDeduction conDocTypeSaleDeduction = conDocTypeSaleDeductionMapper.selectConDocTypeSaleDeductionById(sid);
        MongodbUtil.find(conDocTypeSaleDeduction);
        return  conDocTypeSaleDeduction;
    }

    /**
     * 查询单据类型_销售扣款单列表
     *
     * @param conDocTypeSaleDeduction 单据类型_销售扣款单
     * @return 单据类型_销售扣款单
     */
    @Override
    public List<ConDocTypeSaleDeduction> selectConDocTypeSaleDeductionList(ConDocTypeSaleDeduction conDocTypeSaleDeduction) {
        return conDocTypeSaleDeductionMapper.selectConDocTypeSaleDeductionList(conDocTypeSaleDeduction);
    }

    /**
     * 新增单据类型_销售扣款单
     * 需要注意编码重复校验
     * @param conDocTypeSaleDeduction 单据类型_销售扣款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDocTypeSaleDeduction(ConDocTypeSaleDeduction conDocTypeSaleDeduction) {
        String name = conDocTypeSaleDeduction.getName();
        String code = conDocTypeSaleDeduction.getCode();
        List<ConDocTypeSaleDeduction> list = conDocTypeSaleDeductionMapper.selectList(new QueryWrapper<ConDocTypeSaleDeduction>().lambda()
                .or().eq(ConDocTypeSaleDeduction::getName, name)
                .or().eq(ConDocTypeSaleDeduction::getCode, code)
        );
        if(CollectionUtils.isNotEmpty(list)){
            throw new CustomException("配置档案已存在相同的名称或编码，不允许重复");
        }
        int row= conDocTypeSaleDeductionMapper.insert(conDocTypeSaleDeduction);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conDocTypeSaleDeduction.getSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改单据类型_销售扣款单
     *
     * @param conDocTypeSaleDeduction 单据类型_销售扣款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDocTypeSaleDeduction(ConDocTypeSaleDeduction conDocTypeSaleDeduction) {
        ConDocTypeSaleDeduction response = conDocTypeSaleDeductionMapper.selectConDocTypeSaleDeductionById(conDocTypeSaleDeduction.getSid());
        int row=conDocTypeSaleDeductionMapper.updateById(conDocTypeSaleDeduction);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeSaleDeduction.getSid(), BusinessType.UPDATE.ordinal(), response,conDocTypeSaleDeduction,TITLE);
        }
        return row;
    }

    /**
     * 变更单据类型_销售扣款单
     *
     * @param conDocTypeSaleDeduction 单据类型_销售扣款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDocTypeSaleDeduction(ConDocTypeSaleDeduction conDocTypeSaleDeduction) {
        String name = conDocTypeSaleDeduction.getName();
        ConDocTypeSaleDeduction item = conDocTypeSaleDeductionMapper.selectOne(new QueryWrapper<ConDocTypeSaleDeduction>().lambda()
                .eq(ConDocTypeSaleDeduction::getName, name)
        );
        if (item != null && !item.getSid().equals(conDocTypeSaleDeduction.getSid())) {
            throw new CustomException("配置档案已存在相同的名称，不允许重复");
        }
        ConDocTypeSaleDeduction response = conDocTypeSaleDeductionMapper.selectConDocTypeSaleDeductionById(conDocTypeSaleDeduction.getSid());
        int row = conDocTypeSaleDeductionMapper.updateAllById(conDocTypeSaleDeduction);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeSaleDeduction.getSid(), BusinessType.CHANGE.ordinal(), response, conDocTypeSaleDeduction, TITLE);
        }
        return row;
    }

    /**
     * 批量删除单据类型_销售扣款单
     *
     * @param sids 需要删除的单据类型_销售扣款单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDocTypeSaleDeductionByIds(List<Long> sids) {
        return conDocTypeSaleDeductionMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conDocTypeSaleDeduction
    * @return
    */
    @Override
    public int changeStatus(ConDocTypeSaleDeduction conDocTypeSaleDeduction){
        int row=0;
        Long[] sids=conDocTypeSaleDeduction.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocTypeSaleDeduction.setSid(id);
                row=conDocTypeSaleDeductionMapper.updateById( conDocTypeSaleDeduction);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conDocTypeSaleDeduction.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conDocTypeSaleDeduction.getSid(), BusinessType.CHECK.ordinal(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conDocTypeSaleDeduction
     * @return
     */
    @Override
    public int check(ConDocTypeSaleDeduction conDocTypeSaleDeduction){
        int row=0;
        Long[] sids=conDocTypeSaleDeduction.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocTypeSaleDeduction.setSid(id);
                row=conDocTypeSaleDeductionMapper.updateById( conDocTypeSaleDeduction);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conDocTypeSaleDeduction.getSid(), BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }


}
