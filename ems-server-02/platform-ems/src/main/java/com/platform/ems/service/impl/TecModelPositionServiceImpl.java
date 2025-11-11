package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.google.common.base.Joiner;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.StringUtils;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.SecurityUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.BasCustomer;
import com.platform.system.domain.SysTodoTask;
import com.platform.ems.domain.TecModelPosition;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.enums.Status;
import com.platform.ems.mapper.BasCustomerMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.ems.mapper.TecModelPositionMapper;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.service.ITecModelPositionService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 版型部位档案Service业务层处理
 *
 * @author ChenPinzhen
 * @date 2021-01-25
 */
@Service
public class TecModelPositionServiceImpl implements ITecModelPositionService {
    @Autowired
    private TecModelPositionMapper tecModelPositionMapper;
    @Autowired
    private BasCustomerMapper basCustomerMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static final String TITLE = "版型部位档案";

    /**
     * 查询版型部位
     *
     * @param modelPositionSid 版型部位SID
     * @return 版型部位
     */
    @Override
    public TecModelPosition selectTecModelPositionById(Long modelPositionSid) {
        TecModelPosition modelPosition = tecModelPositionMapper.selectTecModelPositionById(modelPositionSid);
        //查询日志信息
        MongodbUtil.find(modelPosition);
        return modelPosition;
    }

    /**
     * 查询版型部位列表
     *
     * @param tecModelPosition 版型部位
     * @return 版型部位
     */
    @Override
    public List<TecModelPosition> selectTecModelPositionList(TecModelPosition tecModelPosition) {
        return tecModelPositionMapper.selectTecModelPositionList(tecModelPosition);
    }

    /**
     * 新增版型部位
     *
     * @param tecModelPosition 版型部位
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertTecModelPosition(TecModelPosition tecModelPosition) {
        //验证版型部位编码、名称是否已存在
        checkCodeUnique(tecModelPosition);
        checkNameUnique(tecModelPosition);
        //属性赋值
        tecModelPosition.setClientId(SecurityUtils.getClientId());
        tecModelPosition.setModelPositionSid(IdWorker.getIdStr());
        tecModelPosition.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
        tecModelPosition.setCreateDate(new Date());
        //确认
        if (HandleStatus.CONFIRMED.getCode().equals(tecModelPosition.getHandleStatus())) {
            tecModelPosition.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            tecModelPosition.setConfirmDate(new Date());
        }
        //待办通知
        SysTodoTask sysTodoTask = new SysTodoTask();
        if (ConstantsEms.SAVA_STATUS.equals(tecModelPosition.getHandleStatus())) {
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName("s_tec_model_position")
                    .setDocumentSid(Long.parseLong(tecModelPosition.getModelPositionSid()));
            sysTodoTask.setTitle("版型部位档案: " + tecModelPosition.getModelPositionCode() + " 当前是保存状态，请及时处理！")
                    .setDocumentCode(String.valueOf(tecModelPosition.getModelPositionCode()))
                    .setNoticeDate(new Date())
                    .setUserId(ApiThreadLocalUtil.get().getUserid());
            sysTodoTaskMapper.insert(sysTodoTask);
        }
        //插入日志
        MongodbDeal.insert(Long.parseLong(tecModelPosition.getModelPositionSid()), tecModelPosition.getHandleStatus(), null, TITLE, null);
        return tecModelPositionMapper.insertTecModelPosition(tecModelPosition);
    }

    /**
     * 验证版型部位编码是否重复
     */
    private void checkCodeUnique(TecModelPosition tecModelPosition) {
        if (tecModelPositionMapper.checkCodeUnique(tecModelPosition.getModelPositionCode()) > 0) {
            throw new BaseException("版型部位编码已存在，请确认！");
        }
    }

    /**
     * 验证版型部位名称是否已存在
     */
    private void checkNameUnique(TecModelPosition tecModelPosition) {
        if (tecModelPositionMapper.checkNameUnique(tecModelPosition.getModelPositionName()) > 0) {
            throw new BaseException("版型部位名称已存在，请确认！");
        }
    }

    /**
     * 修改版型部位
     *
     * @param tecModelPosition 版型部位
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTecModelPosition(TecModelPosition tecModelPosition) {
        //版型档案sid
        String modelPositionSid = tecModelPosition.getModelPositionSid();
        TecModelPosition modelPosition = tecModelPositionMapper.selectTecModelPositionById(Long.parseLong(modelPositionSid));
        //验证版型部位编码是否修改
        if (!tecModelPosition.getModelPositionCode().equals(modelPosition.getModelPositionCode())) {
            //验证版型部位名称是否已存在
            checkCodeUnique(tecModelPosition);
        }
        //验证版型部位名称是否修改
        if (!tecModelPosition.getModelPositionName().equals(modelPosition.getModelPositionName())) {
            //验证版型部位名称是否已存在
            checkNameUnique(tecModelPosition);
        }
        tecModelPosition.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        tecModelPosition.setUpdateDate(new Date());
        //确认
        if (HandleStatus.CONFIRMED.getCode().equals(tecModelPosition.getHandleStatus())) {
            tecModelPosition.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            tecModelPosition.setConfirmDate(new Date());
        }
        if (!ConstantsEms.SAVA_STATUS.equals(tecModelPosition.getHandleStatus())){
            //确认状态后删除待办
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, tecModelPosition.getModelPositionSid()));
        }
        //插入日志
        List<OperMsg> msgList = new ArrayList<>();
        msgList = BeanUtils.eq(modelPosition, tecModelPosition);
        MongodbDeal.update(Long.parseLong(tecModelPosition.getModelPositionSid()), modelPosition.getHandleStatus(), tecModelPosition.getHandleStatus(), msgList,TITLE, null);
        return tecModelPositionMapper.updateTecModelPosition(tecModelPosition);
    }

    /**
     * 批量删除版型部位
     *
     * @param modelPositionSids 需要删除的版型部位ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTecModelPositionByIds(String[] modelPositionSids) {

        TecModelPosition params1 = new TecModelPosition();
        params1.setModelPositionSid(Joiner.on(';').skipNulls().join(modelPositionSids));
        params1.setHandleStatus(HandleStatus.SAVE.getCode());
        int count = tecModelPositionMapper.countByDomain(params1);
        if (count != modelPositionSids.length) {
            throw new BaseException("仅保存状态才允许删除");
        }
        //删除待办
        sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                .in(SysTodoTask::getDocumentSid, modelPositionSids));
        for (String id : modelPositionSids) {
            //插入日志
            MongodbUtil.insertUserLog(Long.valueOf(id), BusinessType.DELETE.getValue(), null, TITLE);
        }
        return tecModelPositionMapper.deleteTecModelPositionByIds(modelPositionSids);
    }

    /**
     * 版型部位批量确认
     *
     * @param tecModelPosition 版型部位sids、处理状态
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int confirm(TecModelPosition tecModelPosition) {
        //版型部位sids
        String[] modelPositionSidList = tecModelPosition.getModelPositionSidList();

        TecModelPosition params = new TecModelPosition();
        params.setModelPositionSid(Joiner.on(";").skipNulls().join(modelPositionSidList));
        params.setHandleStatus(HandleStatus.SAVE.getCode());
        int count = tecModelPositionMapper.countByDomain(params);
        if (count != modelPositionSidList.length) {
            throw new BaseException("仅保存状态才允许确认");
        }
        //确认状态后删除待办
        if (ConstantsEms.CHECK_STATUS.equals(tecModelPosition.getHandleStatus())){
            tecModelPosition.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            tecModelPosition.setConfirmDate(new Date());
        }
        if (!ConstantsEms.SAVA_STATUS.equals(tecModelPosition.getHandleStatus())){
            //确认状态后删除待办
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid, modelPositionSidList));
        }
        for (String id : modelPositionSidList) {
            //插入日志
            MongodbDeal.check(Long.parseLong(id), tecModelPosition.getHandleStatus(), null, TITLE, null);
        }
        return tecModelPositionMapper.confirm(tecModelPosition);
    }

    /**
     * 版型部位变更
     *
     * @param tecModelPosition 版型部位sid
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int change(TecModelPosition tecModelPosition) {
        String modelPositionSid = tecModelPosition.getModelPositionSid();
        TecModelPosition modelPosition = tecModelPositionMapper.selectTecModelPositionById(Long.parseLong(modelPositionSid));
        //验证是否确认状态
        if (!HandleStatus.CONFIRMED.getCode().equals(modelPosition.getHandleStatus())) {
            throw new BaseException("仅确认状态才允许变更");
        }
        //验证版型档案名称是否修改过
        if (!tecModelPosition.getModelPositionName().equals(modelPosition.getModelPositionName())) {
            //验证版型部位名称是否已存在
            checkNameUnique(tecModelPosition);
        }
        //插入日志
        List<OperMsg> msgList = new ArrayList<>();
        msgList = BeanUtils.eq(modelPosition, tecModelPosition);
        MongodbDeal.update(Long.parseLong(tecModelPosition.getModelPositionSid()), modelPosition.getHandleStatus(), tecModelPosition.getHandleStatus(), msgList, TITLE, null);
        return tecModelPositionMapper.updateTecModelPosition(tecModelPosition);
    }

    /**
     * 版型部位启用/停用
     *
     * @param tecModelPosition 版型部位sids、启用/停用状态
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int status(TecModelPosition tecModelPosition) {
        //版型档案sids
        String[] modelPositionSidList = tecModelPosition.getModelPositionSidList();

        //启用
        if (Status.ENABLE.getCode().equals(tecModelPosition.getStatus())) {
            TecModelPosition params = new TecModelPosition();
            params.setModelPositionSid(Joiner.on(";").skipNulls().join(modelPositionSidList));
            params.setHandleStatus(HandleStatus.CONFIRMED.getCode());
            int count = tecModelPositionMapper.countByDomain(params);
            if (count != modelPositionSidList.length) {
                throw new BaseException("仅确认状态才允许启用");
            }
        }
        for (String id : modelPositionSidList) {
            //插入日志
            String remark = StrUtil.isEmpty(tecModelPosition.getDisableRemark()) ? null : tecModelPosition.getDisableRemark();
            MongodbDeal.status(Long.parseLong(id), tecModelPosition.getStatus(), null, TITLE, remark);
        }
        return tecModelPositionMapper.confirm(tecModelPosition);
    }

    /**
     * 版型部位下拉框列表
     *
     * @return 结果
     */
    @Override
    public List<TecModelPosition> getModelPositionList() {
        return tecModelPositionMapper.getModelPositionList();
    }

    /**
     * 导入版型部位
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int importData(MultipartFile file) {
        List<TecModelPosition> modelPositionList = new ArrayList<>();
        List<String> modelPositionCodeList = new ArrayList<>(); //用于编码查重
        List<String> modelPositionNameList = new ArrayList<>(); //用于名称查重
        try {
            File toFile = null;
            try {
                toFile = FileUtils.multipartFileToFile(file);
            } catch (Exception e) {
                e.getMessage();
                throw new BaseException("文件转换失败");
            }
            ExcelReader reader = cn.hutool.poi.excel.ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();
            if (readAll.size() < 3 || CollectionUtil.isEmpty(readAll)) {
                throw new BaseException("请按模版填写数据后再导入！");
            }
            List<DictData> upDownSuitList = sysDictDataService.selectDictData("s_up_down_suit"); //上下装/套装
            Map<String, String> upDownSuitMaps = upDownSuitList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            for (int i = 0; i < readAll.size(); i++) {
                if (i < 2) {
                    //前两行跳过
                    continue;
                }
                List<Object> objects = readAll.get(i);
                copy(objects, readAll);
                String modelPositionCode = objects.get(0) == null ? null : objects.get(0).toString();
                if (StringUtils.isEmpty(modelPositionCode)) {
                    throw new BaseException("版型部位编码不能为空");
                }
                String modelPositionName = objects.get(1) == null ? null : objects.get(1).toString();
                if (StringUtils.isEmpty(modelPositionName)) {
                    throw new BaseException("版型部位名称不能为空");
                }
                String measureDescription = objects.get(2) == null ? null : objects.get(2).toString();
                if (StringUtils.isEmpty(measureDescription)) {
                    throw new BaseException("度量方法说明不能为空");
                }

                String upDownSuit = objects.get(5) == null ? null : objects.get(5).toString();
                if (StringUtils.isNotEmpty(upDownSuit)) {
                    String value = upDownSuitMaps.get(upDownSuit);
                    if (StringUtils.isEmpty(value)) {
                        throw new BaseException("上下装配置错误,请联系管理员");
                    }
                }

                String remark = "";
                if (objects.size() > 6) {
                    remark = objects.get(6) == null ? null : objects.get(6).toString();
                }
                TecModelPosition modelPosition = new TecModelPosition();
                modelPosition.setModelPositionCode(modelPositionCode);
                modelPosition.setModelPositionName(modelPositionName);
                modelPosition.setMeasureDescription(measureDescription);
                modelPosition.setRemark(remark);
                modelPosition.setStatus(Status.ENABLE.getCode());
                modelPosition.setHandleStatus(HandleStatus.SAVE.getCode());
                modelPosition.setClientId(ApiThreadLocalUtil.get().getClientId());
                modelPosition.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                modelPosition.setCreateDate(new Date());
                modelPosition.setCustomerPositionCode(objects.get(4) == null ? null : objects.get(4).toString());
                modelPosition.setUpDownSuit(upDownSuitMaps.get(upDownSuit));
//                modelPosition.setUpDownSuit(objects.get(5) == null ? null : objects.get(5).toString());
                String customerCode = objects.get(3) == null ? null : objects.get(3).toString();

                if (StringUtils.isNotEmpty(customerCode)) {
                    List<BasCustomer> list = basCustomerMapper.selectList(new QueryWrapper<BasCustomer>().lambda().eq(BasCustomer::getCustomerCode, customerCode));
                    if (CollectionUtils.isEmpty(list)) {
                        throw new BaseException(objects.get(3).toString() + "客户编码不存在");
                    }
                    list.forEach(basCustomer -> {
                        modelPosition.setCustomerSid(String.valueOf(basCustomer.getCustomerSid()));
                    });
                }
                modelPositionList.add(modelPosition);
                modelPositionCodeList.add(modelPositionCode);
                modelPositionNameList.add(modelPositionName);
            }
            //编码查重
            TecModelPosition params = new TecModelPosition();
            params.setModelPositionCodeList(modelPositionCodeList);
            List<TecModelPosition> queryList = tecModelPositionMapper.selectTecModelPositionList(params);
            if (CollectionUtils.isNotEmpty(queryList)) {
                modelPositionCodeList = new ArrayList<>();
                for (int i = 0; i < queryList.size(); i++) {
                    modelPositionCodeList.add(queryList.get(i).getModelPositionCode());
                }
                throw new BaseException(modelPositionCodeList.toString() + "版型部位编码重复,请检查后再试");
            }
            //名称查重
            params = new TecModelPosition();
            params.setModelPositionNameList(modelPositionNameList);
            queryList = tecModelPositionMapper.selectTecModelPositionList(params);
            if (CollectionUtils.isNotEmpty(queryList)) {
                modelPositionNameList = new ArrayList<>();
                for (int i = 0; i < queryList.size(); i++) {
                    modelPositionNameList.add(queryList.get(i).getModelPositionName());
                }
                throw new BaseException(modelPositionNameList.toString() + "版型部位名称重复,请检查后再试");
            }
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
        //待办通知
        List<SysTodoTask> sysTodoTaskList = new ArrayList<>();
        modelPositionList.forEach(item-> {
            SysTodoTask sysTodoTask = new SysTodoTask();
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName("s_tec_model_position")
                    .setDocumentSid(Long.parseLong(item.getModelPositionSid()));
            sysTodoTask.setTitle("版型部位档案: " + item.getModelPositionCode() + " 当前是保存状态，请及时处理！")
                    .setDocumentCode(String.valueOf(item.getModelPositionCode()))
                    .setNoticeDate(new Date())
                    .setUserId(ApiThreadLocalUtil.get().getUserid());
            sysTodoTaskList.add(sysTodoTask);
            //插入日志
            MongodbUtil.insertUserLog(Long.parseLong(item.getModelPositionSid()), BusinessType.INSERT.getValue(), null, TITLE);
        });
        sysTodoTaskMapper.inserts(sysTodoTaskList);
        return tecModelPositionMapper.inserts(modelPositionList);
    }

    private void copy(List<Object> objects, List<List<Object>> readAll) {
        //获取第一行的列数
        int size = readAll.get(0).size();
        //当前行的列数
        int lineSize = objects.size();
        for (int i = lineSize; i < size; i++) {
            Object o = null;
            objects.add(o);
        }
    }
}
