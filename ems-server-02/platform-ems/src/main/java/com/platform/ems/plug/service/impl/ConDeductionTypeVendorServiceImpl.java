package com.platform.ems.plug.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.plug.domain.ConDeductionTypeVendor;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.plug.mapper.ConDeductionTypeVendorMapper;
import com.platform.ems.plug.service.IConDeductionTypeVendorService;

/**
 * 扣款类型_采购Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConDeductionTypeVendorServiceImpl extends ServiceImpl<ConDeductionTypeVendorMapper, ConDeductionTypeVendor> implements IConDeductionTypeVendorService {
    @Autowired
    private ConDeductionTypeVendorMapper conDeductionTypeVendorMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "扣款类型_采购";

    /**
     * 查询扣款类型_采购
     *
     * @param sid 扣款类型_采购ID
     * @return 扣款类型_采购
     */
    @Override
    public ConDeductionTypeVendor selectConDeductionTypeVendorById(Long sid) {
        ConDeductionTypeVendor conDeductionTypeVendor = conDeductionTypeVendorMapper.selectConDeductionTypeVendorById(sid);
        MongodbUtil.find(conDeductionTypeVendor);
        return conDeductionTypeVendor;
    }

    /**
     * 查询扣款类型_采购列表
     *
     * @param conDeductionTypeVendor 扣款类型_采购
     * @return 扣款类型_采购
     */
    @Override
    public List<ConDeductionTypeVendor> selectConDeductionTypeVendorList(ConDeductionTypeVendor conDeductionTypeVendor) {
        return conDeductionTypeVendorMapper.selectConDeductionTypeVendorList(conDeductionTypeVendor);
    }

    /**
     * 新增扣款类型_采购
     * 需要注意编码重复校验
     *
     * @param conDeductionTypeVendor 扣款类型_采购
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDeductionTypeVendor(ConDeductionTypeVendor conDeductionTypeVendor) {
        List<ConDeductionTypeVendor> codeList = conDeductionTypeVendorMapper.selectList(new QueryWrapper<ConDeductionTypeVendor>().lambda()
                .eq(ConDeductionTypeVendor::getCode, conDeductionTypeVendor.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)){
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConDeductionTypeVendor> nameList = conDeductionTypeVendorMapper.selectList(new QueryWrapper<ConDeductionTypeVendor>().lambda()
                .eq(ConDeductionTypeVendor::getName, conDeductionTypeVendor.getName()));
        if (CollectionUtil.isNotEmpty(nameList)){
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row = conDeductionTypeVendorMapper.insert(conDeductionTypeVendor);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conDeductionTypeVendor.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改扣款类型_采购
     *
     * @param conDeductionTypeVendor 扣款类型_采购
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDeductionTypeVendor(ConDeductionTypeVendor conDeductionTypeVendor) {
        ConDeductionTypeVendor response = conDeductionTypeVendorMapper.selectConDeductionTypeVendorById(conDeductionTypeVendor.getSid());
        ConDeductionTypeVendor tempCode = conDeductionTypeVendorMapper.selectOne(new QueryWrapper<ConDeductionTypeVendor>().lambda().eq(ConDeductionTypeVendor::getCode, conDeductionTypeVendor.getCode()));
        if (tempCode != null && !conDeductionTypeVendor.getSid().equals(tempCode.getSid())) {
            throw new CustomException(conDeductionTypeVendor.getCode() + "：编码已存在");
        }
        ConDeductionTypeVendor tempName = conDeductionTypeVendorMapper.selectOne(new QueryWrapper<ConDeductionTypeVendor>().lambda().eq(ConDeductionTypeVendor::getName, conDeductionTypeVendor.getName()));
        if (tempName != null && !conDeductionTypeVendor.getSid().equals(tempName.getSid())) {
            throw new CustomException(conDeductionTypeVendor.getName() + "：名称已存在");
        }
        int row = conDeductionTypeVendorMapper.updateById(conDeductionTypeVendor);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDeductionTypeVendor.getSid(), BusinessType.UPDATE.getValue(), response, conDeductionTypeVendor, TITLE);
        }
        return row;
    }

    /**
     * 变更扣款类型_采购
     *
     * @param conDeductionTypeVendor 扣款类型_采购
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDeductionTypeVendor(ConDeductionTypeVendor conDeductionTypeVendor) {
        ConDeductionTypeVendor response = conDeductionTypeVendorMapper.selectConDeductionTypeVendorById(conDeductionTypeVendor.getSid());
        List<ConDeductionTypeVendor> nameList = conDeductionTypeVendorMapper.selectList(new QueryWrapper<ConDeductionTypeVendor>().lambda()
                .eq(ConDeductionTypeVendor::getName, conDeductionTypeVendor.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conDeductionTypeVendor.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conDeductionTypeVendor.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        int row = conDeductionTypeVendorMapper.updateAllById(conDeductionTypeVendor);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDeductionTypeVendor.getSid(), BusinessType.CHANGE.getValue(), response, conDeductionTypeVendor, TITLE);
        }
        return row;
    }

    /**
     * 批量删除扣款类型_采购
     *
     * @param sids 需要删除的扣款类型_采购ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDeductionTypeVendorByIds(List<Long> sids) {
        return conDeductionTypeVendorMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conDeductionTypeVendor
     * @return
     */
    @Override
    public int changeStatus(ConDeductionTypeVendor conDeductionTypeVendor) {
        int row = 0;
        Long[] sids = conDeductionTypeVendor.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conDeductionTypeVendor.setSid(id);
                row = conDeductionTypeVendorMapper.updateById(conDeductionTypeVendor);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conDeductionTypeVendor.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conDeductionTypeVendor.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conDeductionTypeVendor
     * @return
     */
    @Override
    public int check(ConDeductionTypeVendor conDeductionTypeVendor) {
        int row = 0;
        Long[] sids = conDeductionTypeVendor.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conDeductionTypeVendor.setSid(id);
                row = conDeductionTypeVendorMapper.updateById(conDeductionTypeVendor);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(conDeductionTypeVendor.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    //获取下拉框
    @Override
    public List<ConDeductionTypeVendor> getConDeductionTypeVendorList() {
        return conDeductionTypeVendorMapper.getConDeductionTypeVendorList();
    }
}
