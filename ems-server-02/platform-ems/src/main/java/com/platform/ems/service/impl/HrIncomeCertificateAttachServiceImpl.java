package com.platform.ems.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.HrIncomeCertificateAttach;
import com.platform.ems.mapper.HrIncomeCertificateAttachMapper;
import com.platform.ems.plug.domain.ConFileType;
import com.platform.ems.plug.mapper.ConFileTypeMapper;
import com.platform.ems.service.IHrIncomeCertificateAttachService;
import com.platform.ems.util.MongodbUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 收入证明-附件Service业务层处理
 *
 * @author xfzz
 * @date 2024/5/9
 */
@Service
public class HrIncomeCertificateAttachServiceImpl extends ServiceImpl<HrIncomeCertificateAttachMapper, HrIncomeCertificateAttach> implements IHrIncomeCertificateAttachService {
    @Autowired
    private HrIncomeCertificateAttachMapper hrIncomeCertificateMapper;
    @Autowired
    private ConFileTypeMapper conFileTypeMapper;

    private static final String TITLE = "收入证明-附件";
    private static final String Type = "SRZMDQS";


    /**
     * 查询收入证明-附件
     *
     * @param incomeCertificateSid 收入证明-附件ID
     * @return 收入证明-附件
     */
    @Override
    public HrIncomeCertificateAttach selectHrIncomeCertificateAttachById(Long incomeCertificateSid) {
        HrIncomeCertificateAttach hrIncomeCertificate = hrIncomeCertificateMapper.selectHrIncomeCertificateAttachById(incomeCertificateSid);
        MongodbUtil.find(hrIncomeCertificate);
        return hrIncomeCertificate;
    }

    /**
     * 查询收入证明-附件列表
     *
     * @param hrIncomeCertificate 收入证明-附件
     * @return 收入证明-附件
     */
    @Override
    public List<HrIncomeCertificateAttach> selectHrIncomeCertificateAttachList(HrIncomeCertificateAttach hrIncomeCertificate) {
        return hrIncomeCertificateMapper.selectHrIncomeCertificateAttachList(hrIncomeCertificate);
    }

    /**
     * 新增收入证明-附件
     * 需要注意编码重复校验
     *
     * @param hrIncomeCertificate 收入证明-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertHrIncomeCertificateAttach(HrIncomeCertificateAttach hrIncomeCertificate) {
        int row = hrIncomeCertificateMapper.insert(hrIncomeCertificate);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new HrIncomeCertificateAttach(), hrIncomeCertificate);
            MongodbUtil.insertUserLog(hrIncomeCertificate.getIncomeCertificateAttachSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改收入证明-附件
     *
     * @param hrIncomeCertificate 收入证明-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateHrIncomeCertificateAttach(HrIncomeCertificateAttach hrIncomeCertificate) {
        HrIncomeCertificateAttach response = hrIncomeCertificateMapper.selectHrIncomeCertificateAttachById(hrIncomeCertificate.getIncomeCertificateAttachSid());
        int row = hrIncomeCertificateMapper.updateById(hrIncomeCertificate);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(hrIncomeCertificate.getIncomeCertificateAttachSid(), BusinessType.UPDATE.getValue(), response, hrIncomeCertificate, TITLE);
        }
        return row;
    }

    /**
     * 变更收入证明-附件
     *
     * @param hrIncomeCertificate 收入证明-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeHrIncomeCertificateAttach(HrIncomeCertificateAttach hrIncomeCertificate) {
        HrIncomeCertificateAttach response = hrIncomeCertificateMapper.selectHrIncomeCertificateAttachById(hrIncomeCertificate.getIncomeCertificateAttachSid());
        int row = hrIncomeCertificateMapper.updateAllById(hrIncomeCertificate);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(hrIncomeCertificate.getIncomeCertificateAttachSid(), BusinessType.CHANGE.getValue(), response, hrIncomeCertificate, TITLE);
        }
        return row;
    }

    /**
     * 批量删除收入证明-附件
     *
     * @param incomeCertificateSids 需要删除的收入证明-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteHrIncomeCertificateAttachByIds(List<Long> incomeCertificateSids) {
        incomeCertificateSids.forEach(sid -> {
            HrIncomeCertificateAttach hrIncomeCertificate = hrIncomeCertificateMapper.selectById(sid);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(hrIncomeCertificate, new HrIncomeCertificateAttach());
            MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
        });
        return hrIncomeCertificateMapper.deleteBatchIds(incomeCertificateSids);
    }

    /**
     * 发起签署前校验
     *
     * @param hrIncomeCertificate
     * @return
     */
    @Override
    public AjaxResult check(HrIncomeCertificateAttach hrIncomeCertificate) {
        if (hrIncomeCertificate.getIncomeCertificateSid() == null){
            throw new BaseException("请先选择收入证明！");
        }
        if (StrUtil.isBlank(hrIncomeCertificate.getFileType())){
            return AjaxResult.success(true);
        }else {
            ConFileType conFileType = conFileTypeMapper.selectOne(new QueryWrapper<ConFileType>().lambda().eq(ConFileType::getCode,hrIncomeCertificate.getFileType())
                    .eq(ConFileType::getDataobjectCategoryCode, Type));
            if (conFileType != null && ConstantsEms.NO.equals(conFileType.getIsUploadMultifile())){
                List<HrIncomeCertificateAttach> list = hrIncomeCertificateMapper.selectList(new QueryWrapper<HrIncomeCertificateAttach>().lambda()
                        .eq(HrIncomeCertificateAttach::getIncomeCertificateSid,hrIncomeCertificate.getIncomeCertificateSid())
                        .eq(HrIncomeCertificateAttach::getFileType,hrIncomeCertificate.getFileType()));
                if (CollectionUtils.isEmpty(list)){
                    return AjaxResult.success("不存在待签署文件，请检查！",false);
                }
            }
        }
        return AjaxResult.success(true);
    }

}
