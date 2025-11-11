package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.PurVendorMonthAccountBillZangu;
import com.platform.ems.mapper.PurVendorMonthAccountBillZanguMapper;
import com.platform.ems.service.IPurVendorMonthAccountBillZanguService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 供应商对账单-暂估明细Service业务层处理
 *
 * @author chenkw
 * @date 2021-09-22
 */
@Service
@SuppressWarnings("all")
public class PurVendorMonthAccountBillZanguServiceImpl extends ServiceImpl<PurVendorMonthAccountBillZanguMapper, PurVendorMonthAccountBillZangu> implements IPurVendorMonthAccountBillZanguService {
    @Autowired
    private PurVendorMonthAccountBillZanguMapper purVendorMonthAccountBillBillAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "供应商对账单-暂估明细";

    /**
     * 查询供应商对账单-暂估明细
     *
     * @param vendorMonthAccountBillZanguSid 供应商对账单-暂估明细ID
     * @return 供应商对账单-暂估明细
     */
    @Override
    public PurVendorMonthAccountBillZangu selectPurVendorMonthAccountBillZanguById(Long vendorMonthAccountBillZanguSid) {
        PurVendorMonthAccountBillZangu purVendorMonthAccountBillBillAttach = purVendorMonthAccountBillBillAttachMapper.selectPurVendorMonthAccountBillZanguById(vendorMonthAccountBillZanguSid);
        MongodbUtil.find(purVendorMonthAccountBillBillAttach);
        return purVendorMonthAccountBillBillAttach;
    }

    /**
     * 查询供应商对账单-暂估明细列表
     *
     * @param purVendorMonthAccountBillBillAttach 供应商对账单-暂估明细
     * @return 供应商对账单-暂估明细
     */
    @Override
    public List<PurVendorMonthAccountBillZangu> selectPurVendorMonthAccountBillZanguList(PurVendorMonthAccountBillZangu purVendorMonthAccountBillBillAttach) {
        return purVendorMonthAccountBillBillAttachMapper.selectPurVendorMonthAccountBillZanguList(purVendorMonthAccountBillBillAttach);
    }

    /**
     * 新增供应商对账单-暂估明细
     * 需要注意编码重复校验
     *
     * @param purVendorMonthAccountBillBillAttach 供应商对账单-暂估明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurVendorMonthAccountBillZangu(PurVendorMonthAccountBillZangu purVendorMonthAccountBillBillAttach) {
        int row = purVendorMonthAccountBillBillAttachMapper.insert(purVendorMonthAccountBillBillAttach);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(purVendorMonthAccountBillBillAttach.getVendorMonthAccountBillZanguSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改供应商对账单-暂估明细
     *
     * @param purVendorMonthAccountBillBillAttach 供应商对账单-暂估明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurVendorMonthAccountBillZangu(PurVendorMonthAccountBillZangu purVendorMonthAccountBillBillAttach) {
        PurVendorMonthAccountBillZangu response = purVendorMonthAccountBillBillAttachMapper.selectPurVendorMonthAccountBillZanguById(purVendorMonthAccountBillBillAttach.getVendorMonthAccountBillZanguSid());
        int row = purVendorMonthAccountBillBillAttachMapper.updateById(purVendorMonthAccountBillBillAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(purVendorMonthAccountBillBillAttach.getVendorMonthAccountBillZanguSid(), BusinessType.UPDATE.ordinal(), response, purVendorMonthAccountBillBillAttach, TITLE);
        }
        return row;
    }

    /**
     * 变更供应商对账单-暂估明细
     *
     * @param purVendorMonthAccountBillBillAttach 供应商对账单-暂估明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePurVendorMonthAccountBillZangu(PurVendorMonthAccountBillZangu purVendorMonthAccountBillBillAttach) {
        PurVendorMonthAccountBillZangu response = purVendorMonthAccountBillBillAttachMapper.selectPurVendorMonthAccountBillZanguById(purVendorMonthAccountBillBillAttach.getVendorMonthAccountBillZanguSid());
        int row = purVendorMonthAccountBillBillAttachMapper.updateAllById(purVendorMonthAccountBillBillAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(purVendorMonthAccountBillBillAttach.getVendorMonthAccountBillZanguSid(), BusinessType.CHANGE.ordinal(), response, purVendorMonthAccountBillBillAttach, TITLE);
        }
        return row;
    }

    /**
     * 批量删除供应商对账单-暂估明细
     *
     * @param vendorMonthAccountBillZanguSids 需要删除的供应商对账单-暂估明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurVendorMonthAccountBillZanguByIds(List<Long> vendorMonthAccountBillZanguSids) {
        return purVendorMonthAccountBillBillAttachMapper.deleteBatchIds(vendorMonthAccountBillZanguSids);
    }

}
