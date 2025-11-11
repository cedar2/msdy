package com.platform.ems.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.HrOtherPersonnelCertificateAttach;
import com.platform.ems.mapper.HrOtherPersonnelCertificateAttachMapper;
import com.platform.ems.plug.domain.ConFileType;
import com.platform.ems.plug.mapper.ConFileTypeMapper;
import com.platform.ems.service.IHrOtherPersonnelCertificateAttachService;
import com.platform.ems.util.MongodbUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 其它人事证明-附件Service业务层处理
 *
 * @author xfzz
 * @date 2024/5/9
 */
@Service
public class HrOtherPersonnelCertificateAttachServiceImpl extends ServiceImpl<HrOtherPersonnelCertificateAttachMapper, HrOtherPersonnelCertificateAttach> implements IHrOtherPersonnelCertificateAttachService {
    @Autowired
    private HrOtherPersonnelCertificateAttachMapper hrOtherPersonnelCertificateMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ConFileTypeMapper conFileTypeMapper;


    private static final String TITLE = "其它人事证明-附件";
    private static final String Type = "QTRSZMDQS";


    /**
     * 查询其它人事证明-附件
     *
     * @param otherPersonnelCertificateSid 其它人事证明-附件ID
     * @return 其它人事证明-附件
     */
    @Override
    public HrOtherPersonnelCertificateAttach selectHrOtherPersonnelCertificateAttachById(Long otherPersonnelCertificateSid) {
        HrOtherPersonnelCertificateAttach hrOtherPersonnelCertificate = hrOtherPersonnelCertificateMapper.selectHrOtherPersonnelCertificateAttachById(otherPersonnelCertificateSid);
        MongodbUtil.find(hrOtherPersonnelCertificate);
        return hrOtherPersonnelCertificate;
    }

    /**
     * 查询其它人事证明-附件列表
     *
     * @param hrOtherPersonnelCertificate 其它人事证明-附件
     * @return 其它人事证明-附件
     */
    @Override
    public List<HrOtherPersonnelCertificateAttach> selectHrOtherPersonnelCertificateAttachList(HrOtherPersonnelCertificateAttach hrOtherPersonnelCertificate) {
        return hrOtherPersonnelCertificateMapper.selectHrOtherPersonnelCertificateAttachList(hrOtherPersonnelCertificate);
    }

    /**
     * 新增其它人事证明-附件
     * 需要注意编码重复校验
     *
     * @param hrOtherPersonnelCertificate 其它人事证明-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertHrOtherPersonnelCertificateAttach(HrOtherPersonnelCertificateAttach hrOtherPersonnelCertificate) {
        int row = hrOtherPersonnelCertificateMapper.insert(hrOtherPersonnelCertificate);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new HrOtherPersonnelCertificateAttach(), hrOtherPersonnelCertificate);
            MongodbUtil.insertUserLog(hrOtherPersonnelCertificate.getOtherPersonnelCertificateAttachSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改其它人事证明-附件
     *
     * @param hrOtherPersonnelCertificate 其它人事证明-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateHrOtherPersonnelCertificateAttach(HrOtherPersonnelCertificateAttach hrOtherPersonnelCertificate) {
        HrOtherPersonnelCertificateAttach response = hrOtherPersonnelCertificateMapper.selectHrOtherPersonnelCertificateAttachById(hrOtherPersonnelCertificate.getOtherPersonnelCertificateAttachSid());
        int row = hrOtherPersonnelCertificateMapper.updateById(hrOtherPersonnelCertificate);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(hrOtherPersonnelCertificate.getOtherPersonnelCertificateAttachSid(), BusinessType.UPDATE.getValue(), response, hrOtherPersonnelCertificate, TITLE);
        }
        return row;
    }

    /**
     * 变更其它人事证明-附件
     *
     * @param hrOtherPersonnelCertificate 其它人事证明-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeHrOtherPersonnelCertificateAttach(HrOtherPersonnelCertificateAttach hrOtherPersonnelCertificate) {
        HrOtherPersonnelCertificateAttach response = hrOtherPersonnelCertificateMapper.selectHrOtherPersonnelCertificateAttachById(hrOtherPersonnelCertificate.getOtherPersonnelCertificateAttachSid());
        int row = hrOtherPersonnelCertificateMapper.updateAllById(hrOtherPersonnelCertificate);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(hrOtherPersonnelCertificate.getOtherPersonnelCertificateAttachSid(), BusinessType.CHANGE.getValue(), response, hrOtherPersonnelCertificate, TITLE);
        }
        return row;
    }

    /**
     * 批量删除其它人事证明-附件
     *
     * @param otherPersonnelCertificateSids 需要删除的其它人事证明-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteHrOtherPersonnelCertificateAttachByIds(List<Long> otherPersonnelCertificateSids) {
        otherPersonnelCertificateSids.forEach(sid -> {
            HrOtherPersonnelCertificateAttach hrOtherPersonnelCertificate = hrOtherPersonnelCertificateMapper.selectById(sid);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(hrOtherPersonnelCertificate, new HrOtherPersonnelCertificateAttach());
            MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
        });
        return hrOtherPersonnelCertificateMapper.deleteBatchIds(otherPersonnelCertificateSids);
    }

    /**
     * 发起签署前校验
     *
     * @param hrOtherPersonnelCertificate
     * @return
     */
    @Override
    public AjaxResult check(HrOtherPersonnelCertificateAttach hrOtherPersonnelCertificate) {
        if (hrOtherPersonnelCertificate.getOtherPersonnelCertificateSid() == null){
            throw new BaseException("请先选择其它人事证明！");
        }
        if (StrUtil.isBlank(hrOtherPersonnelCertificate.getFileType())){
            return AjaxResult.success(true);
        }else {
            ConFileType conFileType = conFileTypeMapper.selectOne(new QueryWrapper<ConFileType>().lambda().eq(ConFileType::getCode,hrOtherPersonnelCertificate.getFileType())
                    .eq(ConFileType::getDataobjectCategoryCode, Type));
            if (conFileType != null && ConstantsEms.NO.equals(conFileType.getIsUploadMultifile())){
                List<HrOtherPersonnelCertificateAttach> list = hrOtherPersonnelCertificateMapper.selectList(new QueryWrapper<HrOtherPersonnelCertificateAttach>().lambda()
                        .eq(HrOtherPersonnelCertificateAttach::getOtherPersonnelCertificateSid,hrOtherPersonnelCertificate.getOtherPersonnelCertificateSid())
                        .eq(HrOtherPersonnelCertificateAttach::getFileType,hrOtherPersonnelCertificate.getFileType()));
                if (CollectionUtils.isEmpty(list)){
                    return AjaxResult.success("不存在待签署文件，请检查！",false);
                }
            }
        }
        return AjaxResult.success(true);
    }

}
