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
import com.platform.ems.domain.HrDimissionCertificateAttach;
import com.platform.ems.mapper.HrDimissionCertificateAttachMapper;
import com.platform.ems.plug.domain.ConFileType;
import com.platform.ems.plug.mapper.ConFileTypeMapper;
import com.platform.ems.service.IHrDimissionCertificateAttachService;
import com.platform.ems.util.MongodbUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 离职证明-附件Service业务层处理
 *
 * @author xfzz
 * @date 2024/5/9
 */
@Service
public class HrDimissionCertificateAttachServiceImpl extends ServiceImpl<HrDimissionCertificateAttachMapper, HrDimissionCertificateAttach>
        implements IHrDimissionCertificateAttachService {

    @Autowired
    private HrDimissionCertificateAttachMapper hrDimissionCertificateMapper;
    @Autowired
    private ConFileTypeMapper conFileTypeMapper;

    private static final String TITLE = "离职证明-附件";
    private static final String Type = "LZZMDQS";

    /**
     * 查询离职证明-附件
     *
     * @param dimissionCertificateSid 离职证明-附件ID
     * @return 离职证明-附件
     */
    @Override
    public HrDimissionCertificateAttach selectHrDimissionCertificateAttachById(Long dimissionCertificateSid) {
        HrDimissionCertificateAttach hrDimissionCertificate = hrDimissionCertificateMapper.selectHrDimissionCertificateAttachById(dimissionCertificateSid);
        MongodbUtil.find(hrDimissionCertificate);
        return hrDimissionCertificate;
    }

    /**
     * 查询离职证明-附件列表
     *
     * @param hrDimissionCertificate 离职证明-附件
     * @return 离职证明-附件
     */
    @Override
    public List<HrDimissionCertificateAttach> selectHrDimissionCertificateAttachList(HrDimissionCertificateAttach hrDimissionCertificate) {
        return hrDimissionCertificateMapper.selectHrDimissionCertificateAttachList(hrDimissionCertificate);
    }

    /**
     * 新增离职证明-附件
     * 需要注意编码重复校验
     *
     * @param hrDimissionCertificate 离职证明-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertHrDimissionCertificateAttach(HrDimissionCertificateAttach hrDimissionCertificate) {
        int row = hrDimissionCertificateMapper.insert(hrDimissionCertificate);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new HrDimissionCertificateAttach(), hrDimissionCertificate);
            MongodbUtil.insertUserLog(hrDimissionCertificate.getDimissionCertificateAttachSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改离职证明-附件
     *
     * @param hrDimissionCertificate 离职证明-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateHrDimissionCertificateAttach(HrDimissionCertificateAttach hrDimissionCertificate) {
        HrDimissionCertificateAttach response = hrDimissionCertificateMapper.selectHrDimissionCertificateAttachById(hrDimissionCertificate.getDimissionCertificateAttachSid());
        int row = hrDimissionCertificateMapper.updateById(hrDimissionCertificate);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(hrDimissionCertificate.getDimissionCertificateAttachSid(), BusinessType.UPDATE.getValue(), response, hrDimissionCertificate, TITLE);
        }
        return row;
    }

    /**
     * 变更离职证明-附件
     *
     * @param hrDimissionCertificate 离职证明-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeHrDimissionCertificateAttach(HrDimissionCertificateAttach hrDimissionCertificate) {
        HrDimissionCertificateAttach response = hrDimissionCertificateMapper.selectHrDimissionCertificateAttachById(hrDimissionCertificate.getDimissionCertificateAttachSid());
        int row = hrDimissionCertificateMapper.updateAllById(hrDimissionCertificate);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(hrDimissionCertificate.getDimissionCertificateAttachSid(), BusinessType.CHANGE.getValue(), response, hrDimissionCertificate, TITLE);
        }
        return row;
    }

    /**
     * 批量删除离职证明-附件
     *
     * @param dimissionCertificateSids 需要删除的离职证明-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteHrDimissionCertificateAttachByIds(List<Long> dimissionCertificateSids) {
        dimissionCertificateSids.forEach(sid -> {
            HrDimissionCertificateAttach hrDimissionCertificate = hrDimissionCertificateMapper.selectById(sid);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(hrDimissionCertificate, new HrDimissionCertificateAttach());
            MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
        });
        return hrDimissionCertificateMapper.deleteBatchIds(dimissionCertificateSids);
    }

    /**
     * 发起签署前校验
     *
     * @param hrDimissionCertificate
     * @return
     */
    @Override
    public AjaxResult check(HrDimissionCertificateAttach hrDimissionCertificate) {
        if (hrDimissionCertificate.getDimissionCertificateSid() == null){
            throw new BaseException("请先选择离职证明！");
        }
        if (StrUtil.isBlank(hrDimissionCertificate.getFileType())){
            return AjaxResult.success(true);
        }else {
            ConFileType conFileType = conFileTypeMapper.selectOne(new QueryWrapper<ConFileType>().lambda().eq(ConFileType::getCode,hrDimissionCertificate.getFileType())
                    .eq(ConFileType::getDataobjectCategoryCode, Type));
            if (conFileType != null && ConstantsEms.NO.equals(conFileType.getIsUploadMultifile())){
                List<HrDimissionCertificateAttach> list = hrDimissionCertificateMapper.selectList(new QueryWrapper<HrDimissionCertificateAttach>().lambda()
                        .eq(HrDimissionCertificateAttach::getDimissionCertificateSid,hrDimissionCertificate.getDimissionCertificateSid())
                        .eq(HrDimissionCertificateAttach::getFileType,hrDimissionCertificate.getFileType()));
                if (CollectionUtils.isEmpty(list)){
                    return AjaxResult.success("不存在待签署文件，请检查！",false);
                }
            }
        }
        return AjaxResult.success(true);
    }

}
