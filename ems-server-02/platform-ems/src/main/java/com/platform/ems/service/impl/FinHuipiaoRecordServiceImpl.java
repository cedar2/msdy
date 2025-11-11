package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.utils.file.FileUtils;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IFinHuipiaoRecordUseRecordService;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.service.ISysDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.service.IFinHuipiaoRecordService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 汇票台账表Service业务层处理
 *
 * @author platform
 * @date 2024-03-12
 */
@Service
@SuppressWarnings("all")
public class FinHuipiaoRecordServiceImpl extends ServiceImpl<FinHuipiaoRecordMapper, FinHuipiaoRecord> implements IFinHuipiaoRecordService {
    @Autowired
    private FinHuipiaoRecordMapper finHuipiaoRecordMapper;
    @Autowired
    private IFinHuipiaoRecordUseRecordService useRecordService;
    @Autowired
    private BasCustomerMapper customerMapper;

    @Autowired
    private FinHuipiaoRecordAttachMapper finHuipiaoRecordAttachMapper;

    @Autowired
    private BasCompanyMapper companyMapper;

    @Autowired
    private BasCompanyMapper basCompanyMapper;

    @Autowired
    private FunFundAccountMapper funFundAccountMapper;

    @Autowired
    private ISysDictDataService sysDictDataService;

    private static final String TITLE = "汇票台账表";

    /**
     * 查询汇票台账表
     *
     * @param huipiaoRecordSid 汇票台账表ID
     * @return 汇票台账表
     */
    @Override
    public FinHuipiaoRecord selectFinHuipiaoRecordById(Long huipiaoRecordSid) {
        FinHuipiaoRecord finHuipiaoRecord = finHuipiaoRecordMapper.selectFinHuipiaoRecordById(huipiaoRecordSid);
        // 汇票台账-使用记录表对象
        // 特殊字段处理
        getData(finHuipiaoRecord);
        // 附件清单
        finHuipiaoRecord.setAttachmentList(new ArrayList<>());
        List<FinHuipiaoRecordAttach> attachList = finHuipiaoRecordAttachMapper.selectFinHuipiaoRecordAttachList(new FinHuipiaoRecordAttach()
                .setHuipiaoRecordSid(huipiaoRecordSid));
        if (CollectionUtil.isNotEmpty(attachList)) {
            finHuipiaoRecord.setAttachmentList(attachList);
        }
        finHuipiaoRecord.setUseRecordList(new ArrayList<>());
        List<FinHuipiaoRecordUseRecord> recordList = useRecordService.selectFinHuipiaoRecordUseRecordList
                (new FinHuipiaoRecordUseRecord().setHuipiaoRecordSid(huipiaoRecordSid));
        if (CollectionUtil.isNotEmpty(recordList)) {
            finHuipiaoRecord.setUseRecordList(recordList);
        }
        // 操作日志
        MongodbUtil.find(finHuipiaoRecord);
        return finHuipiaoRecord;
    }

    /**
     * 查询汇票台账表列表
     *
     * @param finHuipiaoRecord 汇票台账表
     * @return 汇票台账表
     */
    @Override
    public List<FinHuipiaoRecord> selectFinHuipiaoRecordList(FinHuipiaoRecord finHuipiaoRecord) {
        return finHuipiaoRecordMapper.selectFinHuipiaoRecordList(finHuipiaoRecord);
    }

    /**
     * 新增汇票台账表
     * 需要注意编码重复校验
     *
     * @param finHuipiaoRecord 汇票台账表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinHuipiaoRecord(FinHuipiaoRecord finHuipiaoRecord) {
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(finHuipiaoRecord.getHandleStatus())) {
            finHuipiaoRecord.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        setData(finHuipiaoRecord);
        int row = finHuipiaoRecordMapper.insert(finHuipiaoRecord);
        if (row > 0) {
            // 附件清单
            if (CollectionUtil.isNotEmpty(finHuipiaoRecord.getAttachmentList())) {
                finHuipiaoRecord.getAttachmentList().forEach(item->{
                    item.setHuipiaoRecordSid(finHuipiaoRecord.getHuipiaoRecordSid());
                });
                finHuipiaoRecordAttachMapper.inserts(finHuipiaoRecord.getAttachmentList());
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FinHuipiaoRecord(), finHuipiaoRecord);
            MongodbDeal.insert(finHuipiaoRecord.getHuipiaoRecordSid(), finHuipiaoRecord.getHandleStatus(), msgList, TITLE, null,
                    finHuipiaoRecord.getImportStatus());

        }
        return row;
    }

    /**
     * 写值
     */
    public void setData(FinHuipiaoRecord finHuipiaoRecord) {
        // 图片
        String picture = null;
        if (ArrayUtil.isNotEmpty(finHuipiaoRecord.getPicturePathList())) {
            picture = "";
            for (int i = 0; i < finHuipiaoRecord.getPicturePathList().length; i++) {
                picture = picture + finHuipiaoRecord.getPicturePathList()[i] + ";";
            }
        }
        finHuipiaoRecord.setPicturePath(picture);
        // 客户编码
        finHuipiaoRecord.setCustomerCode(null);
        if (finHuipiaoRecord.getCustomerSid() != null) {
            BasCustomer customer = customerMapper.selectById(finHuipiaoRecord.getCustomerSid());
            if (customer != null) {
                finHuipiaoRecord.setCustomerCode(customer.getCustomerCode());
            }
        }
        // 公司(初始)编码
        finHuipiaoRecord.setCompanyCodeInitial(null);
        if (finHuipiaoRecord.getCompanySidInitial() != null) {
            BasCompany company = companyMapper.selectById(finHuipiaoRecord.getCompanySidInitial());
            if (company != null) {
                finHuipiaoRecord.setCompanyCodeInitial(company.getCompanyCode());
            }
        }
        // 新建时
        if (finHuipiaoRecord.getHuipiaoRecordSid() == null) {
            finHuipiaoRecord.setCompanySidNew(finHuipiaoRecord.getCompanySidInitial());
        }
        // 公司(当前)编码
        finHuipiaoRecord.setCompanyCodeNew(null);
        if (finHuipiaoRecord.getCompanySidNew() != null) {
            BasCompany company = companyMapper.selectById(finHuipiaoRecord.getCompanySidNew());
            if (company != null) {
                finHuipiaoRecord.setCompanyCodeNew(company.getCompanyCode());
            }
        }
    }

    /**
     * 修改汇票台账表
     *
     * @param finHuipiaoRecord 汇票台账表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinHuipiaoRecord(FinHuipiaoRecord finHuipiaoRecord) {
        FinHuipiaoRecord original = finHuipiaoRecordMapper.selectFinHuipiaoRecordById(finHuipiaoRecord.getHuipiaoRecordSid());
        // 写值
        setData(finHuipiaoRecord);
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(finHuipiaoRecord.getHandleStatus())) {
            finHuipiaoRecord.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, finHuipiaoRecord);
        if (CollectionUtil.isNotEmpty(msgList)) {
            finHuipiaoRecord.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = finHuipiaoRecordMapper.updateAllById(finHuipiaoRecord);
        if (row > 0) {
            addAttach(finHuipiaoRecord);
            //插入日志
            MongodbDeal.update(finHuipiaoRecord.getHuipiaoRecordSid(), original.getHandleStatus(),
                    finHuipiaoRecord.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 更改状态信息
     *
     * @param finHuipiaoRecord 汇票台账表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setDateStatusByid(FinHuipiaoRecord finHuipiaoRecord) {
        FinHuipiaoRecord response = finHuipiaoRecordMapper.selectFinHuipiaoRecordById(finHuipiaoRecord.getHuipiaoRecordSid());
        // 写值
        setData(finHuipiaoRecord);
        //编写日志详细
        //原值：XXX，新值：XXX；
        StringBuilder changeInfo = new StringBuilder();
        changeInfo.append("更改状态信息。\n");
        //数据字典Map
        List<DictData> yseNoTypeDict = sysDictDataService.selectDictData("s_yesno_flag"); //是否更改
        yseNoTypeDict = yseNoTypeDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
        Map<String, String> yseNoTypeMaps = yseNoTypeDict.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));
        //数据字典Map
        List<DictData> liutongFlagTypeDict = sysDictDataService.selectDictData("s_liutong_flag"); //流通标志
        liutongFlagTypeDict = liutongFlagTypeDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
        Map<String, String> liutongFlagTypeMaps = liutongFlagTypeDict.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));

        //数据字典Map
        List<DictData> huipiaoStatusTypeDict = sysDictDataService.selectDictData("s_huipiao_status"); //票据状态
        huipiaoStatusTypeDict = huipiaoStatusTypeDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
        Map<String, String> huipiaoStatusTypeMaps = huipiaoStatusTypeDict.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));

        if (finHuipiaoRecord.getPlanLiutongFlag() != null && finHuipiaoRecord.getPlanLiutongFlag().equals("Y")){
            changeInfo.append("流通标志，原值：").append(liutongFlagTypeMaps.get(response.getLiutongFlag())).append("，新值：")
                    .append(liutongFlagTypeMaps.get(finHuipiaoRecord.getLiutongFlag())).append("；\n");
            response.setLiutongFlag(finHuipiaoRecord.getLiutongFlag());
        }

        if (finHuipiaoRecord.getPlanHuipiaoStatus() != null && finHuipiaoRecord.getPlanHuipiaoStatus().equals("Y")) {
            changeInfo.append("票据状态，原值：").append(huipiaoStatusTypeMaps.get(response.getHuipiaoStatus()))
                    .append("，新值：").append(huipiaoStatusTypeMaps.get(finHuipiaoRecord.getHuipiaoStatus())).append("；\n");
            response.setHuipiaoStatus(finHuipiaoRecord.getHuipiaoStatus());
        }

        if (finHuipiaoRecord.getPlanIsFenbao() != null && finHuipiaoRecord.getPlanIsFenbao().equals("Y")) {
            response.setIsFenbao(finHuipiaoRecord.getIsFenbao());
        }

        if (finHuipiaoRecord.getPlanIsZhuanrang() != null && finHuipiaoRecord.getPlanIsZhuanrang().equals("Y")) {
            response.setIsZhuanrang(finHuipiaoRecord.getIsZhuanrang());
        }

        int row = finHuipiaoRecordMapper.updateAllById(response);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finHuipiaoRecord.getHuipiaoRecordSid(), BusinessType.QITA.getValue(),
                    null, TITLE,  changeInfo.toString());
        }
        return row;
    }

    /**
     * 变更汇票台账表
     *
     * @param finHuipiaoRecord 汇票台账表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinHuipiaoRecord(FinHuipiaoRecord finHuipiaoRecord) {
        FinHuipiaoRecord response = finHuipiaoRecordMapper.selectFinHuipiaoRecordById(finHuipiaoRecord.getHuipiaoRecordSid());
        // 写值
        setData(finHuipiaoRecord);

        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, finHuipiaoRecord);
        if (CollectionUtil.isNotEmpty(msgList)) {
            finHuipiaoRecord.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = finHuipiaoRecordMapper.updateAllById(finHuipiaoRecord);
        if (row > 0) {
            addAttach(finHuipiaoRecord);
            //插入日志
            MongodbUtil.insertUserLog(finHuipiaoRecord.getHuipiaoRecordSid(), BusinessType.CHANGE.getValue(), response, finHuipiaoRecord, TITLE);
        }
        return row;
    }

    /**
     * 批量删除汇票台账表
     *
     * @param huipiaoRecordSids 需要删除的汇票台账表ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinHuipiaoRecordByIds(List<Long> huipiaoRecordSids) {
        finHuipiaoRecordAttachMapper.delete(new QueryWrapper<FinHuipiaoRecordAttach>().lambda()
                .in(FinHuipiaoRecordAttach::getHuipiaoRecordSid, huipiaoRecordSids));
        List<FinHuipiaoRecord> list = finHuipiaoRecordMapper.selectList(new QueryWrapper<FinHuipiaoRecord>()
                .lambda().in(FinHuipiaoRecord::getHuipiaoRecordSid, huipiaoRecordSids));
        int row = finHuipiaoRecordMapper.deleteBatchIds(huipiaoRecordSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new FinHuipiaoRecord());
                MongodbUtil.insertUserLog(o.getHuipiaoRecordSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param finHuipiaoRecord
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(FinHuipiaoRecord finHuipiaoRecord) {
        Long[] sids = finHuipiaoRecord.getHuipiaoRecordSidList();
        if (ArrayUtil.isEmpty(sids)) {
            return 0;
        }
        LambdaUpdateWrapper<FinHuipiaoRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(FinHuipiaoRecord::getHuipiaoRecordSid, sids);
        updateWrapper.set(FinHuipiaoRecord::getHandleStatus, finHuipiaoRecord.getHandleStatus());
        if (ConstantsEms.CHECK_STATUS.equals(finHuipiaoRecord.getHandleStatus())) {
            updateWrapper.set(FinHuipiaoRecord::getConfirmDate, new Date());
            updateWrapper.set(FinHuipiaoRecord::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
        }
        int row = finHuipiaoRecordMapper.update(null, updateWrapper);
        if (row > 0) {
            for (Long id : sids) {
                //插入日志
                MongodbDeal.check(id, finHuipiaoRecord.getHandleStatus(), null, TITLE, null);
            }
        }
        return row;
    }

    /**
     * 修改汇票台账表
     *
     * @param finHuipiaoRecord 汇票台账表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRecordCompanyNew(FinHuipiaoRecord finHuipiaoRecord) {
        int row = 0;
        Long[] sids = finHuipiaoRecord.getHuipiaoRecordSidList();
        if (ArrayUtil.isEmpty(sids)) {
            return 0;
        }
        LambdaUpdateWrapper<FinHuipiaoRecord> updateWrapper = new LambdaUpdateWrapper<>();
        // 变更所属公司
        if (finHuipiaoRecord.getCompanySidNew() != null) {
            // 找到不是该公司的流水
            List<FinHuipiaoRecord> list = finHuipiaoRecordMapper.selectList(new QueryWrapper<FinHuipiaoRecord>().lambda()
                    .in(FinHuipiaoRecord::getHuipiaoRecordSid, sids)
                    .and(wrap->wrap.ne(FinHuipiaoRecord::getCompanySidNew, finHuipiaoRecord.getCompanySidNew()).or()
                            .isNull(FinHuipiaoRecord::getCompanySidNew)));
            if (CollectionUtil.isNotEmpty(list)) {
                Long[] huipiaoRecordSids = list.stream().map(FinHuipiaoRecord::getHuipiaoRecordSid).toArray(Long[]::new);
                // 找公司编码
                BasCompany company = companyMapper.selectById(finHuipiaoRecord.getCompanySidNew());
                if (company == null) {
                    throw new BaseException("找不到该公司");
                }
                updateWrapper.in(FinHuipiaoRecord::getHuipiaoRecordSid, huipiaoRecordSids);
                updateWrapper.set(FinHuipiaoRecord::getCompanySidNew, company.getCompanySid());
                updateWrapper.set(FinHuipiaoRecord::getCompanyCodeNew, company.getCompanyCode());
                row = finHuipiaoRecordMapper.update(null, updateWrapper);
                for (FinHuipiaoRecord record : list) {
                    //插入日志
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.setDiff(record, "companySidNew", record.getCompanySidNew(), finHuipiaoRecord.getCompanySidNew(), msgList);
                    msgList = BeanUtils.setDiff(record, "companyCodeNew", record.getCompanyCodeNew(), company.getCompanyCode(), msgList);
                    MongodbUtil.insertUserLog(record.getHuipiaoRecordSid(), BusinessType.CHANGE.getValue(), msgList, TITLE, "变更所属公司");
                }
            }
        }
        else {
            throw new BaseException("公司不能为空！");
        }
        return row;
    }

    /**
     * 修改汇票台账表
     *
     * @param finHuipiaoRecord 汇票台账表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRecordUseStatus(FinHuipiaoRecord finHuipiaoRecord) {
        int row = 0;
        Long[] sids = finHuipiaoRecord.getHuipiaoRecordSidList();
        if (ArrayUtil.isEmpty(sids)) {
            return 0;
        }
        LambdaUpdateWrapper<FinHuipiaoRecord> updateWrapper = new LambdaUpdateWrapper<>();
        // 变更使用状态
        if (StrUtil.isNotBlank(finHuipiaoRecord.getHuipiaoUseStatus())) {
            // 找到不是该使用状态的流水
            List<FinHuipiaoRecord> list = finHuipiaoRecordMapper.selectList(new QueryWrapper<FinHuipiaoRecord>().lambda()
                    .in(FinHuipiaoRecord::getHuipiaoRecordSid, sids)
                    .and(wrap-> wrap.ne(FinHuipiaoRecord::getHuipiaoUseStatus, finHuipiaoRecord.getHuipiaoUseStatus()).or()
                            .isNull(FinHuipiaoRecord::getHuipiaoUseStatus)));
            if (CollectionUtil.isNotEmpty(list)) {
                Long[] huipiaoRecordSids = list.stream().map(FinHuipiaoRecord::getHuipiaoRecordSid).toArray(Long[]::new);
                updateWrapper.in(FinHuipiaoRecord::getHuipiaoRecordSid, huipiaoRecordSids);
                updateWrapper.set(FinHuipiaoRecord::getHuipiaoUseStatus, finHuipiaoRecord.getHuipiaoUseStatus());
                row = finHuipiaoRecordMapper.update(null, updateWrapper);
                for (FinHuipiaoRecord record : list) {
                    //插入日志
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.setDiff(record, "huipiaoUseStatus", record.getHuipiaoUseStatus(), finHuipiaoRecord.getHuipiaoUseStatus(), msgList);
                    MongodbUtil.insertUserLog(record.getHuipiaoRecordSid(), BusinessType.CHANGE.getValue(), msgList, TITLE, "设置使用状态");
                }
            }
        }
        else {
            throw new BaseException("使用状态不能为空！");
        }
        return row;
    }

    /**
     * 修改汇票台账表
     *
     * @param finHuipiaoRecord 汇票台账表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRecordUseRecord(FinHuipiaoRecord finHuipiaoRecord) {
        int row = 0;
        if (finHuipiaoRecord.getHuipiaoRecordSid() != null) {
            row = useRecordService.updateByList(finHuipiaoRecord);
            if (row > 0) {
                //插入日志
                MongodbUtil.insertUserLog(finHuipiaoRecord.getHuipiaoRecordSid(), BusinessType.CHANGE.getValue(), null, TITLE, "更新汇票使用记录");
            }
        }
        return row;
    }

    @Override
    public Object importData(MultipartFile file) {
        int num = 0;
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
            //数据字典Map
            List<DictData> yearTypeDict = sysDictDataService.selectDictData("s_year"); //年度
            yearTypeDict = yearTypeDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> yearTypeMaps = yearTypeDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            List<DictData> seasonTypeDict = sysDictDataService.selectDictData("s_season"); //季节
            seasonTypeDict = seasonTypeDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> seasonTypeMaps = seasonTypeDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            List<DictData> isTypeDict = sysDictDataService.selectDictData("s_yesno_flag"); //是否
            isTypeDict = isTypeDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> isTypeMaps = isTypeDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            List<DictData> huipiaoPurposeTypeDict = sysDictDataService.selectDictData("s_huipiao_purpose"); //汇票用途
            huipiaoPurposeTypeDict = huipiaoPurposeTypeDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> huipiaoPurposeTypeMaps = huipiaoPurposeTypeDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            List<DictData> liutongFlagTypeDict = sysDictDataService.selectDictData("s_liutong_flag"); //流通标志
            liutongFlagTypeDict = liutongFlagTypeDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> liutongFlagTypeMaps = liutongFlagTypeDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            List<DictData> huipiaoStatusTypeDict = sysDictDataService.selectDictData("s_huipiao_status"); //票据状态
            huipiaoStatusTypeDict = huipiaoStatusTypeDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> huipiaoStatusTypeMaps = huipiaoStatusTypeDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            //每行对象
            List<FinHuipiaoRecord> recordList = new ArrayList<>();
            CommonErrMsgResponse errMsg = null;
            //错误信息
            List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
            //读excel行和列
            for (int i = 0; i < readAll.size(); i++) {
                if (i < 2) {
                    //前两行跳过
                    continue;
                }
                List<Object> objects = readAll.get(i);
//                if (objects == null) {
//                    //空行
//                    num = i + 1;
//                    continue;
//                }
                //填充总列数
                copy(objects, readAll);
                num = i + 1;

                /**
                 * 出票日期 必填
                 */
                String huipiaoDate_s = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                Date huipiaoDate = null;
                if (StrUtil.isBlank(huipiaoDate_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("出票日期不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (!JudgeFormat.isValidDate(huipiaoDate_s)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("出票日期格式错误，导入失败");
                        errMsgList.add(errMsg);
                    } else {
                        huipiaoDate = DateUtil.parse(huipiaoDate_s);
                    }
                }

                /**
                 * 汇票到期日 必填
                 */
                String terminateDate_s = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                Date terminateDate = null;
                if (StrUtil.isBlank(terminateDate_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("汇票到期日不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (!JudgeFormat.isValidDate(terminateDate_s)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("汇票到期日格式错误，导入失败");
                        errMsgList.add(errMsg);
                    } else {
                        terminateDate = DateUtil.parse(terminateDate_s);
                    }
                }


                /**
                 * 当前归属公司 必填 （配置档案）
                 */
                Long companySidInitial = null; // 公司
                String companyName = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                if (StrUtil.isBlank(companyName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("当前归属公司不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    //获取档案信息校验 公司XXXX不存在 、公司XXXX必须为确认且启用的数据
                    BasCompany basCompany = basCompanyMapper.selectOne(new QueryWrapper<BasCompany>().lambda()
                            .eq(BasCompany::getShortName, companyName));
                    if (basCompany == null || basCompany.getCompanySid() == null) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("当前归属公司" + companyName + "不存在，导入失败！");
                        errMsgList.add(errMsg);
                    } else if (!basCompany.getStatus().equals("1") || !basCompany.getHandleStatus().equals("5")) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("当前归属公司" + companyName + "必须为确认且启用的数据，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        companySidInitial = basCompany.getCompanySid();
                    }
                }

                /**
                 * 票据号码 必填
                 */

                String huipiaoNum = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString();
                if (StrUtil.isBlank(huipiaoNum)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("票据号码不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }

                /**
                 * 票面金额(元) 必填
                 */
                String currencyAmountTax_s = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString();
                BigDecimal currencyAmountTax = null;
                if (StrUtil.isBlank(currencyAmountTax_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("票面金额(元)不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (!JudgeFormat.isValidDouble(currencyAmountTax_s, 10, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("票面金额(元)数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        currencyAmountTax = new BigDecimal(currencyAmountTax_s);
                        currencyAmountTax = currencyAmountTax.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP);
                    }
                }
                if (currencyAmountTax != null && BigDecimal.ZERO.compareTo(currencyAmountTax) >= 0) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("票面金额(元)必须大于0，导入失败！");
                    errMsgList.add(errMsg);
                }

                /**
                 * 客户简称 必填 （数据字典）
                 */
                Long customerSid = null; // 客户
                String customerShortName = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString();
                if (StrUtil.isBlank(customerShortName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("客户简称不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    BasCustomer basCustomer = customerMapper.selectOne(new QueryWrapper<BasCustomer>().lambda()
                            .eq(BasCustomer::getShortName, customerShortName));
                    if (basCustomer == null || basCustomer.getCustomerSid() == null) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("客户简称" + customerShortName + "不存在，导入失败！");
                        errMsgList.add(errMsg);
                    } else if (!basCustomer.getStatus().equals("1") || !basCustomer.getHandleStatus().equals("5")) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("客户简称" + customerShortName + "必须为确认且启用的数据，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        customerSid = basCustomer.getCustomerSid();
                    }
                }

                /**
                 * 年度 非必填 （数据字典）
                 */
                String year = objects.get(6) == null || objects.get(6) == "" ? null : objects.get(6).toString();
                if (StrUtil.isNotBlank(year)) {
                    year = yearTypeMaps.get(year); //通过数据字典标签获取数据字典的值
                    if (StrUtil.isBlank(year)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("年度填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 季节 非必填 （数据字典）
                 */
                String season = objects.get(7) == null || objects.get(7) == "" ? null : objects.get(7).toString();
                if (StrUtil.isNotBlank(season)) {
                    season = seasonTypeMaps.get(season); //通过数据字典标签获取数据字典的值
                    if (StrUtil.isBlank(season)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("季节填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 资金账户 非必填
                 */
                String fundAccount_s = objects.get(8) == null || objects.get(8) == "" ? null : objects.get(8).toString();
                String fundAccount = null;
                if (StrUtil.isNotBlank(fundAccount_s)) {
                    FunFundAccount funFundAccount =null;
                    List<FunFundAccount> funFundAccountList = funFundAccountMapper.selectList(new QueryWrapper<FunFundAccount>().lambda()
                            .eq(FunFundAccount::getCompanySid,companySidInitial)
                            .eq(FunFundAccount::getAccountNumber,fundAccount_s));
                    if(funFundAccountList != null && funFundAccountList.size() > 0){
                        funFundAccount = funFundAccountList.get(0);
                    }
                    if(funFundAccount == null){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("公司"+companyName+"下不存在账号为"+fundAccount_s+"的资金账户，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        if(!funFundAccount.getHandleStatus().equals("5")){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("账号为"+fundAccount_s+"的资金账户必须为确认的数据，导入失败！");
                            errMsgList.add(errMsg);
                        }else{
                            fundAccount = fundAccount_s;
                        }
                    }
                }

                /**
                 * 是否可以分包 非必填 （数据字典）
                 */
                String isFenbao = objects.get(9) == null || objects.get(9) == "" ? null : objects.get(9).toString();
                if (StrUtil.isNotBlank(isFenbao)) {
                    isFenbao = isTypeMaps.get(isFenbao); //通过数据字典标签获取数据字典的值
                    if (StrUtil.isBlank(isFenbao)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("是否可以分包填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 是否可以转让 非必填 （数据字典）
                 */
                String isZhuanrang = objects.get(10) == null || objects.get(10) == "" ? null : objects.get(10).toString();
                if (StrUtil.isNotBlank(isZhuanrang)) {
                    isZhuanrang = isTypeMaps.get(isZhuanrang); //通过数据字典标签获取数据字典的值
                    if (StrUtil.isBlank(isZhuanrang)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("是否可以转让填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 子票区间 非必填
                 */
                String zipiaoQujian = objects.get(11) == null || objects.get(11) == "" ? null : objects.get(11).toString();

                /**
                 * 出票人-开户银行 非必填
                 */
                String drawerName = objects.get(12) == null || objects.get(12) == "" ? null : objects.get(12).toString();

                /**
                 * 出票人-账号 非必填
                 */
                String drawerBankAccount = objects.get(13) == null || objects.get(13) == "" ? null : objects.get(13).toString();

                /**
                 * 出票人-全称 非必填
                 */
                String drawer = objects.get(14) == null || objects.get(14) == "" ? null : objects.get(14).toString();

                /**
                 * 收款人-开户银行 非必填
                 */
                String shoukuanrenBankname = objects.get(15) == null || objects.get(15) == "" ? null : objects.get(15).toString();

                /**
                 * 收款人-账号 非必填
                 */
                String shoukuanrenBankcode = objects.get(16) == null || objects.get(16) == "" ? null : objects.get(16).toString();

                /**
                 * 收款人-全称 非必填
                 */
                String shoukuanrenName = objects.get(17) == null || objects.get(17) == "" ? null : objects.get(17).toString();

                /**
                 * 承兑人-开户银行 非必填
                 */
                String chengduirenBankname = objects.get(18) == null || objects.get(18) == "" ? null : objects.get(18).toString();

                /**
                 * 承兑人-全称 非必填
                 */
                String chengduirenName = objects.get(19) == null || objects.get(19) == "" ? null : objects.get(19).toString();

                /**
                 * 汇票用途 非必填 （数据字典）
                 */
                String huipiaoPurpose = objects.get(20) == null || objects.get(20) == "" ? null : objects.get(20).toString();
                if (StrUtil.isNotBlank(huipiaoPurpose)) {
                    huipiaoPurpose = huipiaoPurposeTypeMaps.get(huipiaoPurpose); //通过数据字典标签获取数据字典的值
                    if (StrUtil.isBlank(huipiaoPurpose)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("汇票用途填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 承兑日期 选填
                 */
                String chengduiDate_s = objects.get(21) == null || objects.get(21) == "" ? null : objects.get(21).toString();
                Date chengduiDate = null;
                if (StrUtil.isNotBlank(chengduiDate_s)) {
                    if (!JudgeFormat.isValidDate(chengduiDate_s)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("承兑日期格式错误，导入失败");
                        errMsgList.add(errMsg);
                    } else {
                        chengduiDate = DateUtil.parse(chengduiDate_s);
                    }
                }

                /**
                 * 流通标志 非必填 （数据字典）
                 */
                String liutongFlag = objects.get(22) == null || objects.get(22) == "" ? null : objects.get(22).toString();
                if (StrUtil.isNotBlank(liutongFlag)) {
                    liutongFlag = liutongFlagTypeMaps.get(liutongFlag); //通过数据字典标签获取数据字典的值
                    if (StrUtil.isBlank(liutongFlag)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("流通标志填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 票据状态 非必填 （数据字典）
                 */
                String huipiaoStatus = objects.get(23) == null || objects.get(23) == "" ? null : objects.get(23).toString();
                if (StrUtil.isNotBlank(huipiaoStatus)) {
                    huipiaoStatus = huipiaoStatusTypeMaps.get(huipiaoStatus); //通过数据字典标签获取数据字典的值
                    if (StrUtil.isBlank(huipiaoStatus)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("票据状态填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 收付款摘要 非必填
                 */
                String shoufukuanRemark = objects.get(24) == null || objects.get(24) == "" ? null : objects.get(24).toString();

                /**
                 * 使用说明 非必填
                 */
                String useRemark = objects.get(25) == null || objects.get(25) == "" ? null : objects.get(25).toString();

                /**
                 * 备注 选填
                 */
                String remark = objects.get(26) == null || objects.get(26) == "" ? null : objects.get(26).toString();
                if (StrUtil.isNotBlank(remark)) {
                    if (remark.length() > 600) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("备注不能超过600个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                FinHuipiaoRecord record = new FinHuipiaoRecord();
                record.setHuipiaoDate(huipiaoDate).setTerminateDate(terminateDate).setCompanySidInitial(companySidInitial)
                        .setHuipiaoNum(huipiaoNum)
                        .setCurrencyAmountTax(currencyAmountTax).setCustomerSid(customerSid).setYear(year).setSeason(season)
                        .setFundAccount(fundAccount).setIsFenbao(isFenbao).setIsZhuanrang(isZhuanrang).setZipiaoQujian(zipiaoQujian)
                        .setDrawerName(drawerName).setDrawerBankAccount(drawerBankAccount).setDrawer(drawer)
                        .setShoukuanrenBankname(shoukuanrenBankname).setShoukuanrenBankcode(shoukuanrenBankcode)
                        .setShoukuanrenName(shoukuanrenName).setChengduirenBankname(chengduirenBankname).setChengduirenName(chengduirenName)
                        .setHuipiaoPurpose(huipiaoPurpose).setChengduiDate(chengduiDate).setLiutongFlag(liutongFlag)
                        .setHuipiaoStatus(huipiaoStatus).setShoufukuanRemark(shoufukuanRemark).setUseRemark(useRemark)
                        .setImportStatus(BusinessType.IMPORT.getValue()).setRemark(remark);
                record.setCurrencyUnit(ConstantsFinance.CURRENCY_UNIT_YUAN).setCurrency(ConstantsFinance.CURRENCY_CNY)
                        .setHandleStatus(ConstantsEms.CHECK_STATUS).setShoufukuanType("SK").setHuipiaoUseStatus("WSY");
                recordList.add(record);
            }
            //检查有没有报错
            if (CollectionUtil.isNotEmpty(errMsgList)) {
                return errMsgList;
            }
            //调用新增方法写入
            if (CollectionUtil.isNotEmpty(recordList)) {
                recordList.forEach(item -> {
                    insertFinHuipiaoRecord(item);
                });
            }
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
        return num - 2;
    }

    //填充-主表
    public void copy(List<Object> objects, List<List<Object>> readAll) {
        //获取第一行的列数
        int size = readAll.get(0).size();
        //当前行的列数
        int lineSize = objects.size();
        ArrayList<Object> all = new ArrayList<>();
        for (int i = lineSize; i < size; i++) {
            Object o = new Object();
            o = null;
            objects.add(o);
        }
    }

    /**
     * 读取数据字段处理
     */
    private void getData(FinHuipiaoRecord record) {
        if (record == null) {
            return;
        }
        // 图片
        if (StrUtil.isNotBlank(record.getPicturePath())) {
            record.setPicturePathList(record.getPicturePath().split(";"));
        }
    }

    /**
     * 处理附件
     *
     * @param finHuipiaoRecord
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void addAttach(FinHuipiaoRecord finHuipiaoRecord) {
        finHuipiaoRecordAttachMapper.delete(new QueryWrapper<FinHuipiaoRecordAttach>().lambda()
                .eq(FinHuipiaoRecordAttach::getHuipiaoRecordSid, finHuipiaoRecord.getHuipiaoRecordSid()));
        if (CollectionUtil.isNotEmpty(finHuipiaoRecord.getAttachmentList())) {
            finHuipiaoRecord.getAttachmentList().forEach(item -> {
                item.setHuipiaoRecordSid(finHuipiaoRecord.getHuipiaoRecordSid());
            });
            finHuipiaoRecordAttachMapper.inserts(finHuipiaoRecord.getAttachmentList());
        }
    }
}
