package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.PurVendorMonthAccountBillAttach;
import com.platform.ems.mapper.PurVendorMonthAccountBillAttachMapper;
import com.platform.ems.service.IPurVendorMonthAccountBillAttachService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 供应商对账单-附件Service业务层处理
 *
 * @author chenkw
 * @date 2021-09-22
 */
@Service
@SuppressWarnings("all")
public class PurVendorMonthAccountBillAttachServiceImpl extends ServiceImpl<PurVendorMonthAccountBillAttachMapper, PurVendorMonthAccountBillAttach> implements IPurVendorMonthAccountBillAttachService {
    @Autowired
    private PurVendorMonthAccountBillAttachMapper purVendorMonthAccountBillBillAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "供应商对账单-附件";

    /**
     * 查询供应商对账单-附件
     *
     * @param monthAccountBillAttachmentSid 供应商对账单-附件ID
     * @return 供应商对账单-附件
     */
    @Override
    public PurVendorMonthAccountBillAttach selectPurVendorMonthAccountBillAttachById(Long monthAccountBillAttachmentSid) {
        PurVendorMonthAccountBillAttach purVendorMonthAccountBillBillAttach = purVendorMonthAccountBillBillAttachMapper.selectPurVendorMonthAccountBillAttachById(monthAccountBillAttachmentSid);
        MongodbUtil.find(purVendorMonthAccountBillBillAttach);
        return purVendorMonthAccountBillBillAttach;
    }

    /**
     * 查询供应商对账单-附件列表
     *
     * @param purVendorMonthAccountBillBillAttach 供应商对账单-附件
     * @return 供应商对账单-附件
     */
    @Override
    public List<PurVendorMonthAccountBillAttach> selectPurVendorMonthAccountBillAttachList(PurVendorMonthAccountBillAttach purVendorMonthAccountBillBillAttach) {
        return purVendorMonthAccountBillBillAttachMapper.selectPurVendorMonthAccountBillAttachList(purVendorMonthAccountBillBillAttach);
    }

    /**
     * 新增供应商对账单-附件
     * 需要注意编码重复校验
     *
     * @param purVendorMonthAccountBillBillAttach 供应商对账单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurVendorMonthAccountBillAttach(PurVendorMonthAccountBillAttach purVendorMonthAccountBillBillAttach) {
        int row = purVendorMonthAccountBillBillAttachMapper.insert(purVendorMonthAccountBillBillAttach);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(purVendorMonthAccountBillBillAttach.getMonthAccountBillAttachmentSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改供应商对账单-附件
     *
     * @param purVendorMonthAccountBillBillAttach 供应商对账单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurVendorMonthAccountBillAttach(PurVendorMonthAccountBillAttach purVendorMonthAccountBillBillAttach) {
        PurVendorMonthAccountBillAttach response = purVendorMonthAccountBillBillAttachMapper.selectPurVendorMonthAccountBillAttachById(purVendorMonthAccountBillBillAttach.getMonthAccountBillAttachmentSid());
        int row = purVendorMonthAccountBillBillAttachMapper.updateById(purVendorMonthAccountBillBillAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(purVendorMonthAccountBillBillAttach.getMonthAccountBillAttachmentSid(), BusinessType.UPDATE.ordinal(), response, purVendorMonthAccountBillBillAttach, TITLE);
        }
        return row;
    }

    /**
     * 变更供应商对账单-附件
     *
     * @param purVendorMonthAccountBillBillAttach 供应商对账单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePurVendorMonthAccountBillAttach(PurVendorMonthAccountBillAttach purVendorMonthAccountBillBillAttach) {
        PurVendorMonthAccountBillAttach response = purVendorMonthAccountBillBillAttachMapper.selectPurVendorMonthAccountBillAttachById(purVendorMonthAccountBillBillAttach.getMonthAccountBillAttachmentSid());
        int row = purVendorMonthAccountBillBillAttachMapper.updateAllById(purVendorMonthAccountBillBillAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(purVendorMonthAccountBillBillAttach.getMonthAccountBillAttachmentSid(), BusinessType.CHANGE.ordinal(), response, purVendorMonthAccountBillBillAttach, TITLE);
        }
        return row;
    }

    /**
     * 批量删除供应商对账单-附件
     *
     * @param monthAccountBillAttachmentSids 需要删除的供应商对账单-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurVendorMonthAccountBillAttachByIds(List<Long> monthAccountBillAttachmentSids) {
        return purVendorMonthAccountBillBillAttachMapper.deleteBatchIds(monthAccountBillAttachmentSids);
    }

}
