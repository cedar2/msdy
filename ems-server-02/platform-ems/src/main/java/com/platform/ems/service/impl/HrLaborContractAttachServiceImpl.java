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
import com.platform.ems.domain.HrLaborContractAttach;
import com.platform.ems.mapper.HrLaborContractAttachMapper;
import com.platform.ems.plug.domain.ConFileType;
import com.platform.ems.plug.mapper.ConFileTypeMapper;
import com.platform.ems.service.IHrLaborContractAttachService;
import com.platform.ems.util.MongodbUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 劳动合同-附件Service业务层处理
 *
 * @author xfzz
 * @date 2024/5/9
 */
@Service
public class HrLaborContractAttachServiceImpl  extends ServiceImpl<HrLaborContractAttachMapper, HrLaborContractAttach> implements IHrLaborContractAttachService {
    @Resource
    private HrLaborContractAttachMapper hrLaborContractAttachMapper;
    @Resource
    private ConFileTypeMapper conFileTypeMapper;


    private static final String TITLE = "劳动合同-附件";
    private static final String Type = "LDHTDQS";


    /**
     * 查询劳动合同-附件
     *
     * @param laborContractAttachSid 劳动合同-附件ID
     * @return 劳动合同-附件
     */
    @Override
    public HrLaborContractAttach selectHrLaborContractAttachById(Long laborContractAttachSid) {
        HrLaborContractAttach hrLaborContractAttach = hrLaborContractAttachMapper.selectHrLaborContractAttachById(laborContractAttachSid);
        MongodbUtil.find(hrLaborContractAttach);
        return hrLaborContractAttach;
    }

    /**
     * 查询劳动合同-附件列表
     *
     * @param hrLaborContractAttach 劳动合同-附件
     * @return 劳动合同-附件
     */
    @Override
    public List<HrLaborContractAttach> selectHrLaborContractAttachList(HrLaborContractAttach hrLaborContractAttach) {
        return hrLaborContractAttachMapper.selectHrLaborContractAttachList(hrLaborContractAttach);
    }

    /**
     * 新增劳动合同-附件
     * 需要注意编码重复校验
     *
     * @param hrLaborContractAttach 劳动合同-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertHrLaborContractAttach(HrLaborContractAttach hrLaborContractAttach) {
        int row = hrLaborContractAttachMapper.insert(hrLaborContractAttach);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new HrLaborContractAttach(), hrLaborContractAttach);
            MongodbUtil.insertUserLog(hrLaborContractAttach.getLaborContractAttachSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改劳动合同-附件
     *
     * @param hrLaborContractAttach 劳动合同-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateHrLaborContractAttach(HrLaborContractAttach hrLaborContractAttach) {
        HrLaborContractAttach response = hrLaborContractAttachMapper.selectHrLaborContractAttachById(hrLaborContractAttach.getLaborContractAttachSid());
        int row = hrLaborContractAttachMapper.updateById(hrLaborContractAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(hrLaborContractAttach.getLaborContractAttachSid(), BusinessType.UPDATE.getValue(), response, hrLaborContractAttach, TITLE);
        }
        return row;
    }

    /**
     * 变更劳动合同-附件
     *
     * @param hrLaborContractAttach 劳动合同-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeHrLaborContractAttach(HrLaborContractAttach hrLaborContractAttach) {
        HrLaborContractAttach response = hrLaborContractAttachMapper.selectHrLaborContractAttachById(hrLaborContractAttach.getLaborContractAttachSid());
        int row = hrLaborContractAttachMapper.updateAllById(hrLaborContractAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(hrLaborContractAttach.getLaborContractAttachSid(), BusinessType.CHANGE.getValue(), response, hrLaborContractAttach, TITLE);
        }
        return row;
    }

    /**
     * 批量删除劳动合同-附件
     *
     * @param laborContractAttachSids 需要删除的劳动合同-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteHrLaborContractAttachByIds(List<Long> laborContractAttachSids) {
        laborContractAttachSids.forEach(sid -> {
            HrLaborContractAttach hrLaborContractAttach = hrLaborContractAttachMapper.selectById(sid);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(hrLaborContractAttach, new HrLaborContractAttach());
            MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
        });
        return hrLaborContractAttachMapper.deleteBatchIds(laborContractAttachSids);
    }

    /**
     * 发起签署前校验
     *
     * @param hrLaborContractAttach
     * @return
     */
    @Override
    public AjaxResult check(HrLaborContractAttach hrLaborContractAttach) {
        if (hrLaborContractAttach.getLaborContractSid() == null){
            throw new BaseException("请先选择劳动合同！");
        }
        if (StrUtil.isBlank(hrLaborContractAttach.getFileType())){
            return AjaxResult.success(true);
        }else {
            ConFileType conFileType = conFileTypeMapper.selectOne(new QueryWrapper<ConFileType>().lambda().eq(ConFileType::getCode,hrLaborContractAttach.getFileType())
                    .eq(ConFileType::getDataobjectCategoryCode, Type));
            if (conFileType != null && ConstantsEms.NO.equals(conFileType.getIsUploadMultifile())){
                List<HrLaborContractAttach> list = hrLaborContractAttachMapper.selectList(new QueryWrapper<HrLaborContractAttach>().lambda()
                        .eq(HrLaborContractAttach::getLaborContractSid,hrLaborContractAttach.getLaborContractSid())
                        .eq(HrLaborContractAttach::getFileType,hrLaborContractAttach.getFileType()));
                if (CollectionUtils.isEmpty(list)){
                    return AjaxResult.success("不存在待签署文件，请检查！",false);
                }
            }
        }
        return AjaxResult.success(true);
    }

}
