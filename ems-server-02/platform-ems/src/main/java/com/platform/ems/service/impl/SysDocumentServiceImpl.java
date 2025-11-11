package com.platform.ems.service.impl;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.SysDocumentMapper;
import com.platform.ems.domain.SysDocument;
import com.platform.ems.service.ISysDocumentService;

/**
 * 文档管理Service业务层处理
 *
 * @author chenkw
 * @date 2023-02-13
 */
@Service
@SuppressWarnings("all")
public class SysDocumentServiceImpl extends ServiceImpl<SysDocumentMapper, SysDocument> implements ISysDocumentService {
    @Autowired
    private SysDocumentMapper sysDocumentMapper;

    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static final String TITLE = "文档管理";

    /**
     * 查询文档管理
     *
     * @param documentSid 文档管理ID
     * @return 文档管理
     */
    @Override
    public SysDocument selectSysDocumentById(Long documentSid) {
        SysDocument sysDocument = sysDocumentMapper.selectSysDocumentById(documentSid);
        MongodbUtil.find(sysDocument);
        return sysDocument;
    }

    /**
     * 查询文档管理列表
     *
     * @param sysDocument 文档管理
     * @return 文档管理
     */
    @Override
    public List<SysDocument> selectSysDocumentList(SysDocument sysDocument) {
        return sysDocumentMapper.selectSysDocumentList(sysDocument);
    }

    /**
     * 校验唯一性
     * @param sysDocument
     */
    private void verifyUnique(SysDocument sysDocument) {
        if (StrUtil.isBlank(sysDocument.getDocumentCategory())) {
            throw new BaseException("文档类别不能为空！");
        }
        QueryWrapper<SysDocument> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysDocument::getDocumentCategory, sysDocument.getDocumentCategory());
        if (sysDocument.getDocumentSid() != null) {
            queryWrapper.lambda().ne(SysDocument::getDocumentSid, sysDocument.getDocumentSid());
        }
        List<SysDocument> documents = sysDocumentMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(documents)) {
            List<DictData> catList=sysDictDataService.selectDictData("s_file_category");
            catList = catList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String,String> catMaps = catList.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel,(key1, key2)->key2));
            String categoryName = catMaps.get(sysDocument.getDocumentCategory()) == null ? "" : "“"+catMaps.get(sysDocument.getDocumentCategory()).toString()+"“";
            throw new BaseException("文档类别为" + categoryName + "的文件已存在，是否继续操作？");
        }
    }

    /**
     * 新增文档管理
     * 需要注意编码重复校验
     *
     * @param sysDocument 文档管理
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity insertSysDocument(SysDocument sysDocument) {
        // 校验唯一性并带有忽略并继续
        try {
            if (sysDocument.getIsContinue() == null || true != sysDocument.getIsContinue()) {
                this.verifyUnique(sysDocument);
            }
        } catch (BaseException e) {
            List<CommonErrMsgResponse> msgList = new ArrayList<>();
            msgList.add(new CommonErrMsgResponse().setMsg(e.getDefaultMessage()));
            return EmsResultEntity.warning(msgList);
        }
        int row = sysDocumentMapper.insert(sysDocument);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new SysDocument(), sysDocument);
            MongodbUtil.insertUserLog(sysDocument.getDocumentSid(), BusinessType.INSERT.getValue(), msgList, TITLE, null);
        }
        return EmsResultEntity.success(row);
    }

    /**
     * 修改文档管理
     *
     * @param sysDocument 文档管理
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity updateSysDocument(SysDocument sysDocument) {
        // 校验唯一性并带有忽略并继续
        try {
            if (sysDocument.getIsContinue() == null || true != sysDocument.getIsContinue()) {
                this.verifyUnique(sysDocument);
            }
        } catch (BaseException e) {
            List<CommonErrMsgResponse> msgList = new ArrayList<>();
            msgList.add(new CommonErrMsgResponse().setMsg(e.getDefaultMessage()));
            return EmsResultEntity.warning(msgList);
        }
        SysDocument original = sysDocumentMapper.selectSysDocumentById(sysDocument.getDocumentSid());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, sysDocument);
        if (CollectionUtil.isNotEmpty(msgList)) {
            sysDocument.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = sysDocumentMapper.updateAllById(sysDocument);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(sysDocument.getDocumentSid(), BusinessType.UPDATE.getValue(), msgList, TITLE, null);
        }
        return EmsResultEntity.success(row);
    }

    /**
     * 变更文档管理
     *
     * @param sysDocument 文档管理
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeSysDocument(SysDocument sysDocument) {
        SysDocument response = sysDocumentMapper.selectSysDocumentById(sysDocument.getDocumentSid());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, sysDocument);
        if (CollectionUtil.isNotEmpty(msgList)) {
            sysDocument.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = sysDocumentMapper.updateAllById(sysDocument);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(sysDocument.getDocumentSid(), BusinessType.CHANGE.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 批量删除文档管理
     *
     * @param documentSids 需要删除的文档管理ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysDocumentByIds(List<Long> documentSids) {
        List<SysDocument> list = sysDocumentMapper.selectList(new QueryWrapper<SysDocument>()
                .lambda().in(SysDocument::getDocumentSid, documentSids));
        int row = sysDocumentMapper.deleteBatchIds(documentSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new SysDocument());
                MongodbUtil.insertUserLog(o.getDocumentSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

}
