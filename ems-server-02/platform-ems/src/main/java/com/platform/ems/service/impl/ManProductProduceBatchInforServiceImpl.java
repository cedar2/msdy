package com.platform.ems.service.impl;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.mapper.BasPlantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.ManProductProduceBatchInforMapper;
import com.platform.ems.domain.ManProductProduceBatchInfor;
import com.platform.ems.service.IManProductProduceBatchInforService;

/**
 * 商品生产批次信息Service业务层处理
 *
 * @author chenkw
 * @date 2022-09-30
 */
@Service
@SuppressWarnings("all")
public class ManProductProduceBatchInforServiceImpl extends ServiceImpl<ManProductProduceBatchInforMapper, ManProductProduceBatchInfor> implements IManProductProduceBatchInforService {
    @Autowired
    private ManProductProduceBatchInforMapper manProductProduceBatchInforMapper;

    private static final String TITLE = "商品生产批次信息";

    /**
     * 查询商品生产批次信息
     *
     * @param produceBatchInforSid 商品生产批次信息ID
     * @return 商品生产批次信息
     */
    @Override
    public ManProductProduceBatchInfor selectManProductProduceBatchInforById(Long produceBatchInforSid) {
        ManProductProduceBatchInfor manProductProduceBatchInfor = manProductProduceBatchInforMapper.selectManProductProduceBatchInforById(produceBatchInforSid);
        MongodbUtil.find(manProductProduceBatchInfor);
        return manProductProduceBatchInfor;
    }

    /**
     * 查询商品生产批次信息列表
     *
     * @param manProductProduceBatchInfor 商品生产批次信息
     * @return 商品生产批次信息
     */
    @Override
    public List<ManProductProduceBatchInfor> selectManProductProduceBatchInforList(ManProductProduceBatchInfor manProductProduceBatchInfor) {
        return manProductProduceBatchInforMapper.selectManProductProduceBatchInforList(manProductProduceBatchInfor);
    }

    /**
     * 新增商品生产批次信息
     * 需要注意编码重复校验
     *
     * @param manProductProduceBatchInfor 商品生产批次信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManProductProduceBatchInfor(ManProductProduceBatchInfor manProductProduceBatchInfor) {
        int row = manProductProduceBatchInforMapper.insert(manProductProduceBatchInfor);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new ManProductProduceBatchInfor(), manProductProduceBatchInfor);
            MongodbDeal.insert(manProductProduceBatchInfor.getProduceBatchInforSid(), manProductProduceBatchInfor.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改商品生产批次信息
     *
     * @param manProductProduceBatchInfor 商品生产批次信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManProductProduceBatchInfor(ManProductProduceBatchInfor manProductProduceBatchInfor) {
        ManProductProduceBatchInfor original = manProductProduceBatchInforMapper.selectManProductProduceBatchInforById(manProductProduceBatchInfor.getProduceBatchInforSid());
        int row = manProductProduceBatchInforMapper.updateById(manProductProduceBatchInfor);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(original, manProductProduceBatchInfor);
            MongodbDeal.update(manProductProduceBatchInfor.getProduceBatchInforSid(), original.getHandleStatus(), manProductProduceBatchInfor.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更商品生产批次信息
     *
     * @param manProductProduceBatchInfor 商品生产批次信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeManProductProduceBatchInfor(ManProductProduceBatchInfor manProductProduceBatchInfor) {
        ManProductProduceBatchInfor response = manProductProduceBatchInforMapper.selectManProductProduceBatchInforById(manProductProduceBatchInfor.getProduceBatchInforSid());
        int row = manProductProduceBatchInforMapper.updateAllById(manProductProduceBatchInfor);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(manProductProduceBatchInfor.getProduceBatchInforSid(), BusinessType.CHANGE.getValue(), response, manProductProduceBatchInfor, TITLE);
        }
        return row;
    }

    /**
     * 批量删除商品生产批次信息
     *
     * @param produceBatchInforSids 需要删除的商品生产批次信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManProductProduceBatchInforByIds(List<Long> produceBatchInforSids) {
        List<ManProductProduceBatchInfor> list = manProductProduceBatchInforMapper.selectList(new QueryWrapper<ManProductProduceBatchInfor>()
                .lambda().in(ManProductProduceBatchInfor::getProduceBatchInforSid, produceBatchInforSids));
        int row = manProductProduceBatchInforMapper.deleteBatchIds(produceBatchInforSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new ManProductProduceBatchInfor());
                MongodbUtil.insertUserLog(o.getProduceBatchInforSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param manProductProduceBatchInfor
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(ManProductProduceBatchInfor manProductProduceBatchInfor) {
        int row = 0;
        Long[] sids = manProductProduceBatchInfor.getProduceBatchInforSidList();
        if (sids != null && sids.length > 0) {
            LambdaUpdateWrapper<ManProductProduceBatchInfor> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(ManProductProduceBatchInfor::getProduceBatchInforSid, sids);
            updateWrapper.set(ManProductProduceBatchInfor::getHandleStatus, manProductProduceBatchInfor.getHandleStatus());
            if (ConstantsEms.CHECK_STATUS.equals(manProductProduceBatchInfor.getHandleStatus())) {
                updateWrapper.set(ManProductProduceBatchInfor::getConfirmDate, new Date());
                updateWrapper.set(ManProductProduceBatchInfor::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
            }
            row = manProductProduceBatchInforMapper.update(null, updateWrapper);
            if (row > 0) {
                for (Long id : sids) {
                    //插入日志
                    MongodbDeal.check(id, manProductProduceBatchInfor.getHandleStatus(), null, TITLE, null);
                }
            }
        }
        return row;
    }

    /**
     * 维护实裁数
     * @param manProductProduceBatchInfor
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int preserveShicai(ManProductProduceBatchInfor manProductProduceBatchInfor) {
        int row = 0;
        // 判断输入的数据是否是大于等于零的整数
        String shicaiQuantity = manProductProduceBatchInfor.getShicaiQuantity().toString();
        if (shicaiQuantity != null) {
            if (!Character.isDigit(shicaiQuantity.charAt(0))) {
                throw new BaseException("实裁数只能输入大于0的整数");
            }
            for (int i = shicaiQuantity.length(); --i >= 0;) {
                if (!Character.isDigit(shicaiQuantity.charAt(i))) {
                    throw new BaseException("实裁数只能输入大于0的整数");
                }
            }
        }
        // 若“商品生产批次信息表”（s_man_product_produce_batch_infor）中，
        // 按”工厂+商品编码(款号)+排产批次号“已存在数据，则将”实裁数“带入；否则”实裁数“字段默认显示为空
        QueryWrapper<ManProductProduceBatchInfor> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(ManProductProduceBatchInfor::getPlantSid, manProductProduceBatchInfor.getPlantSid())
                .eq(ManProductProduceBatchInfor::getProductSid, manProductProduceBatchInfor.getProductSid());
        if (manProductProduceBatchInfor.getPaichanBatch() == null) {
            queryWrapper.lambda().isNull(ManProductProduceBatchInfor::getPaichanBatch);
        }
        else {
            queryWrapper.lambda().eq(ManProductProduceBatchInfor::getPaichanBatch, manProductProduceBatchInfor.getPaichanBatch());
        }
        List<ManProductProduceBatchInfor> inforList = manProductProduceBatchInforMapper.selectList(queryWrapper);
        // 如果存在则更新
        if (CollectionUtil.isNotEmpty(inforList)) {
            List<Long> inforSidList = inforList.stream().map(ManProductProduceBatchInfor::getProduceBatchInforSid).collect(Collectors.toList());
            row = manProductProduceBatchInforMapper.update(null, new LambdaUpdateWrapper<ManProductProduceBatchInfor>()
                    .in(ManProductProduceBatchInfor::getProduceBatchInforSid, inforSidList)
                    .set(ManProductProduceBatchInfor::getUpdateDate, new Date())
                    .set(ManProductProduceBatchInfor::getUpdaterAccount, ApiThreadLocalUtil.get().getUsername())
                    .set(ManProductProduceBatchInfor::getShicaiQuantity, manProductProduceBatchInfor.getShicaiQuantity()));
        }
        else {
            manProductProduceBatchInfor.setUpdaterAccount(null).setUpdateDate(null).setConfirmDate(new Date())
                    .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            manProductProduceBatchInfor.setHandleStatus(ConstantsEms.CHECK_STATUS);
            row = manProductProduceBatchInforMapper.insert(manProductProduceBatchInfor);
        }
        return row;
    }

}
