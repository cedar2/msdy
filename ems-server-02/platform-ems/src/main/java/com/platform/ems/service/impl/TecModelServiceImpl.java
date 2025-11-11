package com.platform.ems.service.impl;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.response.ModelSystemListResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.ITecModelService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 版型档案Service业务层处理
 *
 * @author olive
 * @date 2021-01-30
 */
@Service
@SuppressWarnings("all")
public class TecModelServiceImpl implements ITecModelService {

    @Autowired
    private TecModelMapper tecModelMapper;
    @Autowired
    private TecModelAttachmentMapper tecModelAttachmentMapper;
    @Autowired
    private TecModelPosInforMapper tecModelPosInforMapper;
    @Autowired
    private TecModelPosSizeMapper tecModelPosSizeMapper;
    @Autowired
    private TecModelPosInforDownMapper tecModelPosInforDownMapper;
    @Autowired
    private TecModelPosSizeDownMapper tecModelPosSizeDownMapper;
    @Autowired
    private BasSkuGroupMapper basSkuGroupMapper;
    @Autowired
    private TecModelLinePosMapper tecModelLinePosMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    RedissonClient redissonClient;

    private static final String LOCK_KEY = "MODEL_STOCK";

    private static final String TITLE = "版型档案";

    /**
     * 查询版型档案
     *
     * @param modelSid 版型档案ID
     * @return 版型档案
     */
    @Override
    public TecModel selectTecModelById(Long modelSid) {
        TecModel tecModel = tecModelMapper.selectTecModelById(modelSid);
        //版型档案
        if (tecModel == null) {
            return tecModel;
        }
        //附件清单
        TecModelAttachment modelAttachment = new TecModelAttachment();
        modelAttachment.setModelSid(modelSid);
        List<TecModelAttachment> attachmentResponses = tecModelAttachmentMapper.selectTecModelAttachmentList(modelAttachment);
        tecModel.setAttachmentList(attachmentResponses);

        //版型部位信息
        TecModelPosInfor modelPosInfo = new TecModelPosInfor();
        modelPosInfo.setModelSid(modelSid);
        List<TecModelPosInfor> infoResponses = tecModelPosInforMapper.selectTecModelPosInforList(modelPosInfo)
                .stream()
                .map(info -> {
                    //版型部位
                    String modelPositionSid = info.getModelPositionInforSid();
                    //版型部位尺寸
                    TecModelPosSize modelPosSize = new TecModelPosSize();
                    modelPosSize.setModelPositionInforSid(modelPositionSid);
                    List<TecModelPosSize> modelPosSizes = tecModelPosSizeMapper.selectTecModelPosSizeList(modelPosSize);
                    //排序
                    modelPosSizes.forEach(li->{
                        String skuName = li.getSkuName();
                        String[] nameSplit = skuName.split("/");
                        if(nameSplit.length==1){
                            li.setFirstSort(nameSplit[0]);
                        }else{
                            String[] name2split = nameSplit[1].split("\\(");
                            if(name2split.length==2){
                                li.setSecondSort(name2split[0].replaceAll("[a-zA-Z]","" ));

                                li.setThirdSort(name2split[1]);
                            }else{
                                li.setSecondSort(nameSplit[1].replaceAll("[a-zA-Z]","" ));
                            }
                            li.setFirstSort(nameSplit[0]);
                        }
                    });
                    List<TecModelPosSize> allList = new ArrayList<>();
                    List<TecModelPosSize> allThirdList = new ArrayList<>();
                    List<TecModelPosSize> sortThird = modelPosSizes.stream().filter(li -> li.getThirdSort() != null).collect(Collectors.toList());
                    List<TecModelPosSize> sortThirdNull = modelPosSizes.stream().filter(li -> li.getThirdSort() == null).collect(Collectors.toList());
                    sortThird=sortThird.stream().sorted(Comparator.comparing(li->li.getThirdSort())).collect(Collectors.toList());
                    allThirdList.addAll(sortThird);
                    allThirdList.addAll(sortThirdNull);

                    List<TecModelPosSize> sort = allThirdList.stream().filter(li -> li.getSecondSort() != null).collect(Collectors.toList());
                    sort=sort.stream().sorted(Comparator.comparing(li->Integer.valueOf(li.getSecondSort()))).collect(Collectors.toList());
                    List<TecModelPosSize> sortNull = allThirdList.stream().filter(li -> li.getSecondSort() == null).collect(Collectors.toList());
                    allList.addAll(sort);
                    allList.addAll(sortNull);
                    modelPosSizes= allList.stream().sorted(Comparator.comparing(item -> item.getFirstSort())
                    ).collect(Collectors.toList());


                    info.setPosSizeList(modelPosSizes);
                    return info;
                }).collect(Collectors.toList());
        //有序
        List<TecModelPosInfor> withSerial = infoResponses.stream().filter(item -> item.getSerialNum() != null).collect(Collectors.toList());
        //有序
        List<TecModelPosInfor> noSerial = infoResponses.stream().filter(item -> item.getSerialNum() == null).collect(Collectors.toList());
        ArrayList<TecModelPosInfor> newInfoResponses = new ArrayList<>();
        newInfoResponses.addAll(withSerial);
        newInfoResponses.addAll(noSerial);
        tecModel.setPosInforList(newInfoResponses);

        TecModelPosInforDown posInforDown = new TecModelPosInforDown();
        posInforDown.setModelSid(Long.valueOf(modelSid));
        List<TecModelPosInforDown> downList = tecModelPosInforDownMapper.selectTecModelPosInforDownList(posInforDown)
                .stream()
                .map(info -> {
                    //版型部位
                    Long modelPositionSid = info.getModelPositionInforSid();
                    //版型部位尺寸
                    TecModelPosSizeDown modelPosSizeDown = new TecModelPosSizeDown();
                    modelPosSizeDown.setModelPositionInforSid(modelPositionSid);
                    List<TecModelPosSizeDown> modelPosSizes = tecModelPosSizeDownMapper.selectTecModelPosSizeDownList(modelPosSizeDown);
                    info.setPosSizeDownList(modelPosSizes);
                    return info;
                }).collect(Collectors.toList());
        //有序
        List<TecModelPosInforDown> withSerialDown = downList.stream().filter(item -> item.getSerialNum() != null).collect(Collectors.toList());
        //有序
        List<TecModelPosInforDown> noSerialDown = downList.stream().filter(item -> item.getSerialNum() == null).collect(Collectors.toList());
        ArrayList<TecModelPosInforDown> newDownList = new ArrayList<>();
        newDownList.addAll(withSerialDown);
        newDownList.addAll(noSerialDown);
        tecModel.setPosInforDownList(newDownList);

        //查询日志信息
        MongodbUtil.find(tecModel);
        return tecModel;
    }

    @Override
    public List<ModelSystemListResponse> getList() {
        return tecModelMapper.getList(new TecModel().setStatus(ConstantsEms.ENABLE_STATUS).setHandleStatus(ConstantsEms.CHECK_STATUS));
    }

    @Override
    public List<ModelSystemListResponse> getList(TecModel tecModel) {
        return tecModelMapper.getList(tecModel);
    }

    @Override
    public TecModel getDetail(Long modelSid) {
        TecModel tecModel = new TecModel();
        List<TecModelPosInfor> tecModelPosInforList = tecModelMapper.getDetail(modelSid);
        tecModel.setPosInforList(tecModelPosInforList);
        List<TecModelPosInforDown> tecModelPosInforDownList = tecModelMapper.getDownDetail(modelSid);
        tecModel.setPosInforDownList(tecModelPosInforDownList);
        return tecModel;
    }


    /**
     * 查询版型档案列表
     *
     * @param request 版型档案
     * @return 版型档案
     */
    @Override
    public List<TecModel> selectTecModelList(TecModel request) {
        return tecModelMapper.selectTecModelList(request);
    }

    /**
     * 新增版型档案
     *
     * @param tecModel 版型档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertTecModel(TecModel tecModel) {
        RLock lock = redissonClient.getLock(LOCK_KEY);
        lock.lock(10L, TimeUnit.SECONDS);
        int i = 0;
        try {
            String creatorAccount = ApiThreadLocalUtil.get().getUsername();
            Date newDate = new Date();
            //版型档案
            Long modelSid = IdWorker.getId();
            tecModel.setCreateDate(newDate);
            tecModel.setModelSid(modelSid);
            tecModel.setCreatorAccount(creatorAccount);
            //验证版型名称是否已存在
            TecModel nameResult = tecModelMapper.selectOne(new QueryWrapper<TecModel>().lambda()
                    .eq(TecModel::getModelName, tecModel.getModelName()));
            if (nameResult != null) {
                throw new CustomException("版型名称已存在");
            }
            TecModel codeResult = tecModelMapper.selectOne(new QueryWrapper<TecModel>().lambda()
                    .eq(TecModel::getModelCode, tecModel.getModelCode()));
            if (codeResult != null) {
                throw new CustomException("版型编码已存在");
            }
            tecModel.setIsCreateModelLine(ConstantsEms.NO);
            if (ConstantsEms.CHECK_STATUS.equals(tecModel.getHandleStatus())){
                tecModel.setConfirmerAccount(creatorAccount).setConfirmDate(newDate);
            }
            i = tecModelMapper.insert(tecModel);
            List<TecModelAttachment> attachmentRequestList = tecModel.getAttachmentList();
            //版型-附件对象
            List<TecModelAttachment> tecModelAttachmentList = tecModel.getAttachmentList();
            if (CollectionUtils.isNotEmpty(tecModelAttachmentList)) {
                addTecModelAttachment(tecModel);
            }
            List<TecModelPosInfor> infoRequestList = tecModel.getPosInforList();
            //版型部位信息
            if (infoRequestList != null) {
                for (TecModelPosInfor modelPosInfor : infoRequestList) {
                    String infoSid = IdWorker.getIdStr();
                    modelPosInfor.setCreateDate(newDate);
                    modelPosInfor.setModelSid(modelSid);
                    modelPosInfor.setModelPositionInforSid(infoSid);
                    modelPosInfor.setCreatorAccount(creatorAccount);
                    i &= tecModelPosInforMapper.insert(modelPosInfor);
                    List<TecModelPosSize> sizeRequest = modelPosInfor.getPosSizeList();
                    for (TecModelPosSize size : sizeRequest) {
                        String sizeSid = IdWorker.getIdStr();
                        size.setModelPositionInforSid(infoSid);
                        size.setModelPositionSizeSid(sizeSid);
                        size.setCreateDate(newDate);
                        size.setCreatorAccount(creatorAccount);
                        i &= tecModelPosSizeMapper.insert(size);
                    }
                }
            }
            List<TecModelPosInforDown> inforDownList = tecModel.getPosInforDownList();
            if (inforDownList != null) {
                inforDownList.forEach(inforDown -> {
                    Long inforDownSid = IdWorker.getId();
                    inforDown.setModelPositionInforSid(inforDownSid);
                    inforDown.setModelSid(Long.valueOf(modelSid));
                    inforDown.setCreateDate(newDate);
                    inforDown.setCreatorAccount(creatorAccount);
                    tecModelPosInforDownMapper.insert(inforDown);
                    List<TecModelPosSizeDown> sizeRequest = inforDown.getPosSizeDownList();
                    if (sizeRequest != null) {
                        sizeRequest.forEach(size -> {
                            size.setModelPositionInforSid(inforDownSid);
                            size.setCreateDate(newDate);
                            size.setCreatorAccount(creatorAccount);
                            tecModelPosSizeDownMapper.insert(size);
                        });
                    }
                });
            }
            if (ConstantsEms.SAVA_STATUS.equals(tecModel.getHandleStatus())){
                //待办通知
                SysTodoTask sysTodoTask = new SysTodoTask();
                if (ConstantsEms.SAVA_STATUS.equals(tecModel.getHandleStatus())) {
                    sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                            .setTableName("s_tec_model")
                            .setDocumentSid(tecModel.getModelSid());
                    sysTodoTask.setTitle("版型档案: " + tecModel.getModelCode() + " 当前是保存状态，请及时处理！")
                            .setDocumentCode(String.valueOf(tecModel.getModelCode()))
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
            }
            MongodbDeal.insert(tecModel.getModelSid(), tecModel.getHandleStatus(), null, TITLE, null);
        }catch (CustomException e){
            throw new CustomException(e.getMessage());
        }finally {
            if(lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return i;
    }

    /**
     * 修改版型档案
     *
     * @param request 版型档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTecModel(TecModel tecModel) {
        RLock lock = redissonClient.getLock(LOCK_KEY);
        lock.lock(10L, TimeUnit.SECONDS);
        int row = 0;
        try {
            //版型原详情
            TecModel queryResult = tecModelMapper.selectTecModelById(tecModel.getModelSid());
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("model_name", tecModel.getModelName());
            List<TecModel> nameResult = tecModelMapper.selectByMap(queryParams);
            if (nameResult.size() > 0) {
                for (TecModel result : nameResult) {
                    if (!result.getModelSid().equals(tecModel.getModelSid())) {
                        throw new CustomException("名称重复,请查看");
                    }
                }
            }
            List<TecModel> codeResult = tecModelMapper.selectList(new QueryWrapper<TecModel>().lambda().eq(
                    TecModel::getModelCode, tecModel.getModelCode()));
            if (codeResult.size() > 0) {
                for (TecModel result : codeResult) {
                    if (!result.getModelSid().equals(tecModel.getModelSid())) {
                        throw new CustomException("编码重复,请查看");
                    }
                }
            }
            Long modelSid = tecModel.getModelSid();
            tecModel.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            tecModel.setUpdateDate(new Date());
            //确认
            if (HandleStatus.CONFIRMED.getCode().equals(tecModel.getHandleStatus())) {
                tecModel.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
                tecModel.setConfirmDate(new Date());
            }
            row = tecModelMapper.updateTecModel(tecModel);

            //删除关联信息重新插入
            Map<String, Object> params = new HashMap<>();
            params.put("model_sid", modelSid);
            //删除附件
            tecModelAttachmentMapper.deleteByMap(params);

            //删除尺寸表
            QueryWrapper<TecModelPosInfor> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("model_sid", modelSid);
            List<TecModelPosInfor> posInforList = tecModelPosInforMapper.selectList(queryWrapper);
            if (posInforList != null) {
                tecModelPosInforMapper.deleteByMap(params);
                for (TecModelPosInfor posInfor : posInforList) {
                    //删除尺码
                    params = new HashMap<>();
                    params.put("model_position_infor_sid", posInfor.getModelPositionInforSid());
                    tecModelPosSizeMapper.deleteByMap(params);
                }
            }
            //重新插入
            //版型-附件对象
            addTecModelAttachment(tecModel);
            List<TecModelPosInfor> infoRequestList = tecModel.getPosInforList();
            //版型部位信息
            if (infoRequestList != null) {
                for (TecModelPosInfor modelPosInfor : infoRequestList) {
                    modelPosInfor.setModelSid(tecModel.getModelSid());
                    tecModelPosInforMapper.insert(modelPosInfor);
                    //部位中的尺码
                    List<TecModelPosSize> sizeRequest = modelPosInfor.getPosSizeList();
                    for (TecModelPosSize modelSize : sizeRequest) {
                        modelSize.setModelPositionInforSid(modelPosInfor.getModelPositionInforSid());
                        tecModelPosSizeMapper.insert(modelSize);
                    }
                }
            }
            List<TecModelPosInforDown> inforDownRequestList = tecModel.getPosInforDownList();
            tecModelPosInforDownMapper.deleteTecModelPosInforDownByModelSid(modelSid);
            if (inforDownRequestList != null) {
                inforDownRequestList.forEach(down -> {
                    down.setModelSid(modelSid);
                    down.setUpdateDate(new Date());
                    down.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                    tecModelPosInforDownMapper.insert(down);
                    List<TecModelPosSizeDown> sizeDownRequest = down.getPosSizeDownList();
                    tecModelPosSizeDownMapper.deleteTecModPosSizeDownByModPosInfSid(down.getModelPositionInforSid());
                    if (sizeDownRequest != null) {
                        sizeDownRequest.forEach(size -> {
                            size.setModelPositionInforSid(down.getModelPositionInforSid());
                            size.setUpdateDate(new Date());
                            size.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                            tecModelPosSizeDownMapper.insert(size);
                        });
                    }
                });
            }
            //确认状态后删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(tecModel.getHandleStatus())){
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getDocumentSid, tecModel.getModelSid()));
            }
            //操纵日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(queryResult, tecModel);
            MongodbDeal.update(tecModel.getModelSid(), queryResult.getHandleStatus(), tecModel.getHandleStatus(), msgList, TITLE, null);
        }catch (CustomException e){
            throw new CustomException(e.getMessage());
        }finally {
            if(lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return row;
    }

    /**
     * 验证版型名称是否已存在
     */
    private void checkNameUnique(TecModel request) {
        if (tecModelMapper.checkNameUnique(request.getModelName()) > 0) {
            throw new BaseException("版型名称已存在，请确认！");
        }
    }

    /**
     * 批量删除版型档案
     *
     * @param tecModel 需要删除的版型档案ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTecModelByIds(TecModel tecModel) {
        //版型档案sids
        Long[] modelSids = tecModel.getModelSidList();
        if (ArrayUtil.isEmpty(modelSids)) {
            throw new BaseException("请选择行");
        }
        List sidList = Arrays.asList(modelSids);
        int row = tecModelMapper.deleteBatchIds(sidList);
        if (row > 0) {
            for (Long id : modelSids) {
                Map<String, Object> params = new HashMap<>();
                params.put("model_sid", id);
                //删除附件
                tecModelAttachmentMapper.deleteByMap(params);
                //删除尺寸表
                QueryWrapper<TecModelPosInfor> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("model_sid", id);
                List<TecModelPosInfor> posInforList = tecModelPosInforMapper.selectList(queryWrapper);
                if (posInforList != null) {
                    tecModelPosInforMapper.deleteByMap(params);
                    for (TecModelPosInfor posInfor : posInforList) {
                        params = new HashMap<>();
                        params.put("model_position_infor_sid", posInfor.getModelPositionInforSid());
                        tecModelPosSizeMapper.deleteByMap(params);
                    }
                }
                MongodbUtil.insertUserLog(id, BusinessType.DELETE.getValue(), TITLE);
            }
            //删除待办
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid, modelSids));
        }
        return row;
    }

    /**
     * 删除版型档案信息
     *
     * @param clientId 版型档案ID
     * @return 结果
     */
    @Override
    public int deleteTecModelById(Long clientId) {
        return tecModelMapper.deleteTecModelById(clientId);
    }

    @Override
    public String getHandleStatus(Long sId) {
        return tecModelMapper.getHandleStatus(sId);
    }

    @Override
    public String putHandleStatus(Long sId, String handleStatus) {
        return tecModelMapper.putHandleStatus(sId, handleStatus);
    }

    @Override
    public String getStatus(Long sId) {
        return tecModelMapper.getStatus(sId);
    }

    @Override
    public String putStatus(Long sId, String validStatus) {
        return tecModelMapper.putStatus(sId, validStatus);
    }

    @Override
    public int checkCodeUnique(String modelCode) {
        return tecModelMapper.checkCodeUnique(modelCode);
    }

    @Override
    public int checkNameUnique(String modelName) {
        return tecModelMapper.checkNameUnique(modelName);
    }

    /**
     * 变更版型档案
     *
     * @param tecModel 版型档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeTecModel(TecModel tecModel) {
        RLock lock = redissonClient.getLock(LOCK_KEY);
        lock.lock(10L, TimeUnit.SECONDS);
        int row = 0;
        try {
            //版型原详情
            TecModel queryResult = tecModelMapper.selectTecModelById(tecModel.getModelSid());
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("model_name", tecModel.getModelName());
            List<TecModel> nameResult = tecModelMapper.selectByMap(queryParams);
            if (nameResult.size() > 0) {
                for (TecModel result : nameResult) {
                    if (result.getModelName().equals(tecModel.getModelName()) && !result.getModelSid().equals(tecModel.getModelSid())) {
                        throw new CustomException("名称重复,请查看");
                    }
                }
            }
            List<TecModel> codeResult = tecModelMapper.selectList(new QueryWrapper<TecModel>().lambda().eq(
                    TecModel::getModelCode, tecModel.getModelCode()));
            if (codeResult.size() > 0) {
                for (TecModel result : codeResult) {
                    if (!result.getModelSid().equals(tecModel.getModelSid())) {
                        throw new CustomException("编码重复,请查看");
                    }
                }
            }
            Long modelSid = tecModel.getModelSid();
            tecModel.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            tecModel.setUpdateDate(new Date());
            //确认
            if (HandleStatus.CONFIRMED.getCode().equals(tecModel.getHandleStatus())) {
                tecModel.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
                tecModel.setConfirmDate(new Date());
            }
            row = tecModelMapper.updateTecModel(tecModel);
            //删除关联信息重新插入
            Map<String, Object> params = new HashMap<>();
            params.put("model_sid", modelSid);
            //删除附件
            tecModelAttachmentMapper.deleteByMap(params);
            //删除尺寸表
            QueryWrapper<TecModelPosInfor> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("model_sid", modelSid);
            List<TecModelPosInfor> posInforList = tecModelPosInforMapper.selectList(queryWrapper);
            if (posInforList != null) {
                tecModelPosInforMapper.deleteByMap(params);
                for (TecModelPosInfor posInfor : posInforList) {
                    //删除尺码
                    params = new HashMap<>();
                    params.put("model_position_infor_sid", posInfor.getModelPositionInforSid());
                    tecModelPosSizeMapper.deleteByMap(params);
                }
            }
            //删除下装尺寸表
            List<TecModelPosInforDown> posInforDownList = tecModelPosInforDownMapper.selectList(new QueryWrapper<TecModelPosInforDown>().lambda()
                    .eq(TecModelPosInforDown::getModelSid, modelSid));
            posInforDownList.forEach(down -> {
                tecModelPosSizeDownMapper.delete(new QueryWrapper<TecModelPosSizeDown>().lambda().eq(TecModelPosSizeDown::getModelPositionInforSid, down.getModelPositionInforSid()));
            });
            tecModelPosInforDownMapper.delete(new QueryWrapper<TecModelPosInforDown>().lambda()
                    .eq(TecModelPosInforDown::getModelSid, modelSid));
            //重新插入
            String creatorAccount = ApiThreadLocalUtil.get().getUsername();
            List<TecModelAttachment> attachmentRequestList = tecModel.getAttachmentList();
            //版型-附件对象
            addTecModelAttachment(tecModel);
            List<TecModelPosInfor> infoRequestList = tecModel.getPosInforList();
            //版型部位信息
            if (infoRequestList != null) {
                for (TecModelPosInfor modelPosInfor : infoRequestList) {
                    modelPosInfor.setModelSid(modelSid);
                    modelPosInfor.setUpdateDate(new Date());
                    modelPosInfor.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                    tecModelPosInforMapper.insert(modelPosInfor);
                    List<TecModelPosSize> sizeRequest = modelPosInfor.getPosSizeList();
                    for (TecModelPosSize modelSize : sizeRequest) {
                        modelSize.setModelPositionInforSid(modelPosInfor.getModelPositionInforSid());
                        modelSize.setUpdateDate(new Date());
                        modelSize.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                        tecModelPosSizeMapper.insert(modelSize);
                    }
                }
            }
            List<TecModelPosInforDown> inforDownRequestList = tecModel.getPosInforDownList();
            if (inforDownRequestList != null) {
                inforDownRequestList.forEach(down -> {
                    down.setModelSid(modelSid);
                    down.setUpdateDate(new Date());
                    down.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                    tecModelPosInforDownMapper.insert(down);
                    List<TecModelPosSizeDown> sizeDownRequest = down.getPosSizeDownList();
                    if (sizeDownRequest != null) {
                        sizeDownRequest.forEach(size -> {
                            size.setModelPositionSizeSid(down.getModelPositionInforSid());
                            size.setUpdateDate(new Date());
                            size.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                            tecModelPosSizeDownMapper.insert(size);
                        });
                    }
                });
            }
            MongodbUtil.insertUserLog(tecModel.getModelSid(), BusinessType.CHANGE.getValue(), tecModel, queryResult, TITLE, null);
        }catch (CustomException e){
            throw new CustomException(e.getMessage());
        }finally {
            if(lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return row;
    }

    /**
     * 启用/停用
     *
     * @param tecModel
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(TecModel tecModel) {
        int row = 0;
        Long[] sids = tecModel.getModelSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                tecModel.setModelSid(id);
                row = tecModelMapper.updateById(tecModel);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = StrUtil.isEmpty(tecModel.getDisableRemark()) ? null : tecModel.getDisableRemark();
                MongodbDeal.status(id, tecModel.getStatus(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param tecModel
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(TecModel tecModel) {
        int row = 0;
        Long[] sids = tecModel.getModelSidList();
        if (ConstantsEms.CHECK_STATUS.equals(tecModel.getHandleStatus())){
            tecModel.setConfirmDate(new Date());
            tecModel.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid, sids));
        }
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                tecModel.setModelSid(id);
                row = tecModelMapper.updateById(tecModel);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbDeal.check(tecModel.getModelSid(), tecModel.getHandleStatus(), msgList, TITLE, null);
            }
        }
        return row;
    }

    /**
     * 版型档案确认操作前校验相应附件是否上传
     *
     * @param tecModel
     * @return
     */
    @Override
    public AjaxResult checkAttach(TecModel tecModel) {
        //如果是查询页面批量确认走这里
        if (tecModel.getModelSidList() != null && tecModel.getModelSidList().length > 0){
            String codes = "";
            for (Long sid : tecModel.getModelSidList()) {
                TecModel one = tecModelMapper.selectOne(new QueryWrapper<TecModel>().lambda().eq(TecModel::getModelSid,sid));
                List<TecModelAttachment> attachmentList = tecModelAttachmentMapper.selectTecModelAttachmentList(
                        new TecModelAttachment().setModelSid(sid).setFileType(ConstantsEms.FILE_TYPE_BXCCB));
                if (CollectionUtils.isEmpty(attachmentList)){
                    codes = codes + one.getModelCode() + ",";
                }
            }
            if (StrUtil.isNotBlank(codes)){
                codes = codes.substring(0,codes.lastIndexOf(","));
                return AjaxResult.success("版型编码：" + codes + " 的版型尺寸表附件未上传，是否进行确认操作？",false);
            }
            else {
                return AjaxResult.success(true);
            }
        }
        //如果是新建编辑变更页面走这里
        if (ConstantsEms.CHECK_STATUS.equals(tecModel.getHandleStatus()) && CollectionUtils.isEmpty(tecModel.getAttachmentList())){
            return AjaxResult.success(false);
        }
        if (ConstantsEms.CHECK_STATUS.equals(tecModel.getHandleStatus()) && CollectionUtils.isNotEmpty(tecModel.getAttachmentList())){
            List<TecModelAttachment> list = tecModel.getAttachmentList();
            list = list.stream().filter(item -> ConstantsEms.FILE_TYPE_BXCCB.equals(item.getFileType())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(list)){
                return AjaxResult.success(false);
            }
        }
        return AjaxResult.success(true);
    }


    /**
     * 版型-附件对象
     */
    private void addTecModelAttachment(TecModel tecModel) {
        tecModelAttachmentMapper.delete(
                new UpdateWrapper<TecModelAttachment>()
                        .lambda()
                        .eq(TecModelAttachment::getModelSid, tecModel.getModelSid())
        );
        if (CollectionUtils.isNotEmpty(tecModel.getAttachmentList())) {
            tecModel.getAttachmentList().forEach(o -> {
                o.setModelSid(tecModel.getModelSid());
                tecModelAttachmentMapper.insert(o);
            });
        }
    }
}
