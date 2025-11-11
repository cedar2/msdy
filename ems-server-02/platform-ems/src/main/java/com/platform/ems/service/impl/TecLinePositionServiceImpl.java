package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.system.domain.SysTodoTask;
import com.platform.ems.domain.TecLinePosition;
import com.platform.ems.domain.TecModelLinePos;
import com.platform.ems.domain.TecProductLineposMat;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.enums.Status;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.ems.mapper.TecLinePositionMapper;
import com.platform.ems.mapper.TecModelLinePosMapper;
import com.platform.ems.mapper.TecProductLineposMatMapper;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.service.ITecLinePositionService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 线部位档案Service业务层处理
 *
 * @author hjj
 * @date 2021-08-19
 */
@Service
@SuppressWarnings("all")
public class TecLinePositionServiceImpl extends ServiceImpl<TecLinePositionMapper, TecLinePosition> implements ITecLinePositionService {
    @Autowired
    private TecLinePositionMapper tecLinePositionMapper;
    @Autowired
    private TecModelLinePosMapper tecModelLinePosMapper;
    @Autowired
    private TecProductLineposMatMapper tecProductLineposMatMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "线部位档案";

    /**
     * 查询线部位档案
     *
     * @param linePositionSid 线部位档案ID
     * @return 线部位档案
     */
    @Override
    public TecLinePosition selectTecLinePositionById(Long linePositionSid) {
        TecLinePosition tecLinePosition = tecLinePositionMapper.selectTecLinePositionById(linePositionSid);
        MongodbUtil.find(tecLinePosition);
        return tecLinePosition;
    }

    /**
     * 查询线部位档案列表
     *
     * @param tecLinePosition 线部位档案
     * @return 线部位档案
     */
    @Override
    public List<TecLinePosition> selectTecLinePositionList(TecLinePosition tecLinePosition) {
        return tecLinePositionMapper.selectTecLinePositionList(tecLinePosition);
    }

    /**
     * 新增线部位档案
     * 需要注意编码重复校验
     *
     * @param tecLinePosition 线部位档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertTecLinePosition(TecLinePosition tecLinePosition) {
        /*List<TecLinePosition> list1 = tecLinePositionMapper.selectList(new QueryWrapper<TecLinePosition>().lambda()
                .eq(TecLinePosition::getLinePositionCode, tecLinePosition.getLinePositionCode()));
        if (CollectionUtil.isNotEmpty(list1)) {
            throw new BaseException("线部位编码已存在！");
        }*/
        List<TecLinePosition> list2 = tecLinePositionMapper.selectList(new QueryWrapper<TecLinePosition>().lambda()
                .eq(TecLinePosition::getLinePositionName, tecLinePosition.getLinePositionName()));
        if (CollectionUtil.isNotEmpty(list2)) {
            throw new BaseException("线部位名称已存在！");
        }
        setConfirmInfo(tecLinePosition);
        int row = tecLinePositionMapper.insert(tecLinePosition);
        if (row > 0) {
            TecLinePosition linePosition = tecLinePositionMapper.selectTecLinePositionById(tecLinePosition.getLinePositionSid());
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(tecLinePosition.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsEms.TABLE_RECORD_TECHTRANSFER)
                        .setDocumentSid(tecLinePosition.getLinePositionSid());
                List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                    sysTodoTask.setTitle("线部位档案：" + linePosition.getLinePositionCode() + " 当前是保存状态，请及时处理！")
                            .setDocumentCode(String.valueOf(linePosition.getLinePositionCode()))
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
            } else {
                //校验是否存在待办
                checkTodoExist(tecLinePosition);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(tecLinePosition.getLinePositionSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(TecLinePosition tecLinePosition) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, tecLinePosition.getLinePositionSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, tecLinePosition.getLinePositionSid()));
        }
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(TecLinePosition o) {
        if (o == null) {
            return;
        }
        if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 修改线部位档案
     *
     * @param tecLinePosition 线部位档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTecLinePosition(TecLinePosition tecLinePosition) {
        //校验名称是否重复
        checkNameUnique(tecLinePosition);
        setConfirmInfo(tecLinePosition);
        TecLinePosition response = tecLinePositionMapper.selectTecLinePositionById(tecLinePosition.getLinePositionSid());
        int row = tecLinePositionMapper.updateById(tecLinePosition);
        if (row > 0) {
            if (!ConstantsEms.SAVA_STATUS.equals(tecLinePosition.getHandleStatus())) {
                //校验是否存在待办
                checkTodoExist(tecLinePosition);
            }
            //插入日志
            MongodbUtil.insertUserLog(tecLinePosition.getLinePositionSid(), BusinessType.UPDATE.getValue(), response, tecLinePosition, TITLE);
        }
        return row;
    }

    /**
     * 校验名称是否重复
     */
    private void checkNameUnique(TecLinePosition tecLinePosition) {
        List<TecLinePosition> list = tecLinePositionMapper.selectList(new QueryWrapper<TecLinePosition>().lambda()
                .eq(TecLinePosition::getLinePositionName, tecLinePosition.getLinePositionName()));
        if (CollectionUtil.isNotEmpty(list)){
            for (TecLinePosition linePosition : list) {
                if (!tecLinePosition.getLinePositionSid().equals(linePosition.getLinePositionSid())) {
                    throw new BaseException("线部位名称已存在！");
                }
            }
        }
    }

    /**
     * 变更线部位档案
     *
     * @param tecLinePosition 线部位档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeTecLinePosition(TecLinePosition tecLinePosition) {
        //校验名称是否重复
        checkNameUnique(tecLinePosition);
        setConfirmInfo(tecLinePosition);
        tecLinePosition.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername())
                .setUpdateDate(new Date());
        TecLinePosition response = tecLinePositionMapper.selectTecLinePositionById(tecLinePosition.getLinePositionSid());
        int row = tecLinePositionMapper.updateAllById(tecLinePosition);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(tecLinePosition.getLinePositionSid(), BusinessType.CHANGE.getValue(), response, tecLinePosition, TITLE);
        }
        return row;
    }

    /**
     * 批量删除线部位档案
     *
     * @param linePositionSids 需要删除的线部位档案ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTecLinePositionByIds(List<Long> linePositionSids) {
        Integer count = tecLinePositionMapper.selectCount(new QueryWrapper<TecLinePosition>().lambda()
                .eq(TecLinePosition::getHandleStatus, ConstantsEms.SAVA_STATUS)
                .in(TecLinePosition::getLinePositionSid, linePositionSids));
        if (count != linePositionSids.size()){
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT);
        }
        List<String> list = new ArrayList<>();
        List<TecModelLinePos> tecModelLinePosList =
                tecModelLinePosMapper.selectTecModelLinePosList(new TecModelLinePos().setLinePositionSids(linePositionSids));
        if (CollUtil.isNotEmpty(tecModelLinePosList)) {
            //线部位编码
            List<String> linePositionCodeList = tecModelLinePosList.stream().map(TecModelLinePos::getLinePositionCode).distinct().collect(Collectors.toList());
            //版型编码
//            List<String> modelCodeList = tecModelLinePosList.stream().map(TecModelLinePos::getModelCode).distinct().collect(Collectors.toList());
            list.addAll(linePositionCodeList);
        }

        List<TecProductLineposMat> tecProductLineposMatList =
                tecProductLineposMatMapper.selectTecProductLineposMatList(new TecProductLineposMat().setLinePositionSids(linePositionSids));
        if (CollUtil.isNotEmpty(tecProductLineposMatList)) {
            //线部位编码
            List<String> linePositionCodeList = tecProductLineposMatList.stream().map(TecProductLineposMat::getLinePositionCode).distinct().collect(Collectors.toList());
            //商品编码
//            List<String> materialCodeList = tecProductLineposMatList.stream().map(TecProductLineposMat::getMaterialCode).collect(Collectors.toList());
            list.addAll(linePositionCodeList);
        }
        if (CollUtil.isNotEmpty(list)) {
            throw new BaseException("线部位" + list.toString() + "已被版型/商品线用量引用，删除失败！");
        }
        TecLinePosition tecLinePosition = new TecLinePosition();
        linePositionSids.forEach(linePositionSid -> {
            tecLinePosition.setLinePositionSid(linePositionSid);
            //校验是否存在待办
            checkTodoExist(tecLinePosition);
        });

        return tecLinePositionMapper.deleteBatchIds(linePositionSids);
    }

    /**
     * 启用/停用
     *
     * @param tecLinePosition
     * @return
     */
    @Override
    public int changeStatus(TecLinePosition tecLinePosition) {
        int row = 0;
        Long[] sids = tecLinePosition.getLinePositionSidList();
        if (sids != null && sids.length > 0) {
            if (ConstantsEms.DISENABLE_STATUS.equals(tecLinePosition.getStatus())) {
                List<String> list = new ArrayList<>();
                List<TecModelLinePos> tecModelLinePosList =
                        tecModelLinePosMapper.selectTecModelLinePosList(new TecModelLinePos().setLinePositionSids(Arrays.asList(sids)));
                if (CollUtil.isNotEmpty(tecModelLinePosList)) {
                    //线部位编码
                    List<String> linePositionCodeList = tecModelLinePosList.stream().map(TecModelLinePos::getLinePositionCode).distinct().collect(Collectors.toList());
                    list.addAll(linePositionCodeList);
                }

                List<TecProductLineposMat> tecProductLineposMatList =
                        tecProductLineposMatMapper.selectTecProductLineposMatList(new TecProductLineposMat().setLinePositionSids(Arrays.asList(sids)));
                if (CollUtil.isNotEmpty(tecProductLineposMatList)) {
                    //线部位编码
                    List<String> linePositionCodeList = tecProductLineposMatList.stream().map(TecProductLineposMat::getLinePositionCode).distinct().collect(Collectors.toList());
                    list.addAll(linePositionCodeList);
                }
                if (CollUtil.isNotEmpty(list)) {
                    list = list.stream().distinct().collect(Collectors.toList());
                    throw new BaseException("线部位" + list.toString() + "已被版型/商品线用量引用，不能停用！");
                }
            }
            row = tecLinePositionMapper.update(null, new UpdateWrapper<TecLinePosition>().lambda().set(TecLinePosition::getStatus, tecLinePosition.getStatus())
                    .in(TecLinePosition::getLinePositionSid, sids));
            for (Long id : sids) {
                tecLinePosition.setLinePositionSid(id);
                row = tecLinePositionMapper.updateById(tecLinePosition);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = StrUtil.isEmpty(tecLinePosition.getDisableRemark()) ? null : tecLinePosition.getDisableRemark();
                MongodbDeal.status(tecLinePosition.getLinePositionSid(), tecLinePosition.getStatus(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param tecLinePosition
     * @return
     */
    @Override
    public int check(TecLinePosition tecLinePosition) {
        int row = 0;
        Long[] sids = tecLinePosition.getLinePositionSidList();
        if (sids != null && sids.length > 0) {
            Integer count = tecLinePositionMapper.selectCount(new QueryWrapper<TecLinePosition>().lambda()
                    .eq(TecLinePosition::getHandleStatus, ConstantsEms.SAVA_STATUS)
                    .in(TecLinePosition::getLinePositionSid, sids));
            if (count != sids.length){
                throw new BaseException(ConstantsEms.CHECK_PROMPT_STATEMENT);
            }
            row = tecLinePositionMapper.update(null, new UpdateWrapper<TecLinePosition>().lambda()
                    .set(TecLinePosition::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .set(TecLinePosition::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                    .set(TecLinePosition::getConfirmDate, new Date())
                    .in(TecLinePosition::getLinePositionSid, sids));
            for (Long id : sids) {
                tecLinePosition.setLinePositionSid(id);
                //校验是否存在待办
                checkTodoExist(tecLinePosition);
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 导入线部位档案
     */
    @Override
    public int importData(MultipartFile file) {
        List<TecLinePosition> linePositionList = new ArrayList<>();
        List<String> linePositionCodeList = new ArrayList<>(); //用于编码查重
        List<String> linePositionNameList = new ArrayList<>(); //用于名称查重
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
            List<DictData> upDownSuitList = sysDictDataService.selectDictData("s_up_down_suit"); //上下装/套装
            Map<String, String> upDownSuitMaps = upDownSuitList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            for (int i = 0; i < readAll.size(); i++) {
                if (i < 2) {
                    //前两行跳过
                    continue;
                }
                List<Object> objects = readAll.get(i);
                copy(objects, readAll);
                String upDownSuit = objects.get(3) == null ? null : objects.get(3).toString();
                if (StrUtil.isNotEmpty(upDownSuit)) {
                    String value = upDownSuitMaps.get(upDownSuit);
                    if (StrUtil.isEmpty(value)) {
                        throw new BaseException("上下装配置错误,请联系管理员");
                    }
                }
                /*String linePositionCode = objects.get(0) == null ? null : objects.get(0).toString();
                if (StrUtil.isEmpty(linePositionCode)) {
                    throw new BaseException("线部位编码不能为空");
                }*/
                String linePositionName = objects.get(1) == null ? null : objects.get(1).toString();
                if (StrUtil.isEmpty(linePositionName)) {
                    throw new BaseException("线部位名称不能为空");
                }
                String measureDescription = objects.get(2) == null ? null : objects.get(2).toString();
                if (StrUtil.isEmpty(measureDescription)) {
                    throw new BaseException("度量方法说明不能为空");
                }
                String remark = "";
                if (objects.size() > 4) {
                    remark = objects.get(4) == null ? null : objects.get(4).toString();
                }
                TecLinePosition linePosition = new TecLinePosition();
//                linePosition.setLinePositionCode(linePositionCode);
                linePosition.setLinePositionName(linePositionName);
                linePosition.setMeasureDescription(measureDescription);
                linePosition.setRemark(remark);
                linePosition.setStatus(Status.ENABLE.getCode());
                linePosition.setHandleStatus(HandleStatus.SAVE.getCode());
                linePosition.setClientId(ApiThreadLocalUtil.get().getClientId());
                linePosition.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                linePosition.setCreateDate(new Date());
                linePosition.setUpDownSuit(upDownSuitMaps.get(upDownSuit));
//                linePosition.setUpDownSuit(objects.get(3) == null ? null : objects.get(3).toString());

                linePositionList.add(linePosition);
//                linePositionCodeList.add(linePositionCode);
                linePositionNameList.add(linePositionName);
            }
            //编码查重
            TecLinePosition params = new TecLinePosition();
            params.setLinePositionCodeList(linePositionCodeList);
            List<TecLinePosition> queryList = tecLinePositionMapper.selectTecLinePositionList(params);
            if (CollectionUtils.isNotEmpty(queryList)) {
                linePositionCodeList = new ArrayList<>();
                for (int i = 0; i < queryList.size(); i++) {
                    linePositionCodeList.add(queryList.get(i).getLinePositionCode());
                }
                throw new BaseException(linePositionCodeList.toString() + "线部位编码重复,请检查后再试");
            }
            //名称查重
            params = new TecLinePosition();
            params.setLinePositionNameList(linePositionNameList);
            queryList = tecLinePositionMapper.selectTecLinePositionList(params);
            if (CollectionUtils.isNotEmpty(queryList)) {
                linePositionNameList = new ArrayList<>();
                for (int i = 0; i < queryList.size(); i++) {
                    linePositionNameList.add(queryList.get(i).getLinePositionName());
                }
                throw new BaseException(linePositionNameList.toString() + "线部位名称重复,请检查后再试");
            }
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
        return tecLinePositionMapper.inserts(linePositionList);
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
