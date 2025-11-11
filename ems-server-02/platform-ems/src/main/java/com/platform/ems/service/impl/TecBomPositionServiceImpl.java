package com.platform.ems.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.DateUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.ems.mapper.TecBomPositionMapper;
import com.platform.ems.plug.domain.ConMeasureUnit;
import com.platform.ems.plug.domain.ConTaxRate;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.util.JudgeFormat;
import com.platform.common.core.domain.model.LoginUser;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.ems.service.ITecBomPositionService;
import org.springframework.web.multipart.MultipartFile;

/**
 * BOM部位档案Service业务层处理
 *
 * @author linhongwei
 * @date 2022-07-07
 */
@Service
@SuppressWarnings("all")
public class TecBomPositionServiceImpl extends ServiceImpl<TecBomPositionMapper,TecBomPosition>  implements ITecBomPositionService {
    @Autowired
    private TecBomPositionMapper tecBomPositionMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static final String TITLE = "BOM部位档案";
    /**
     * 查询BOM部位档案
     *
     * @param clientId BOM部位档案ID
     * @return BOM部位档案
     */
    @Override
    public TecBomPosition selectTecBomPositionById(Long bomPositionSid) {
        TecBomPosition tecBomPosition = tecBomPositionMapper.selectTecBomPositionById(bomPositionSid);
        MongodbUtil.find(tecBomPosition);
        return  tecBomPosition;
    }

    /**
     * 查询BOM部位档案列表
     *
     * @param tecBomPosition BOM部位档案
     * @return BOM部位档案
     */
    @Override
    public List<TecBomPosition> selectTecBomPositionList(TecBomPosition tecBomPosition) {
        return tecBomPositionMapper.selectTecBomPositionList(tecBomPosition);
    }


    /**
     * 新增BOM部位档案
     * 需要注意编码重复校验
     * @param tecBomPosition BOM部位档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertTecBomPosition(TecBomPosition tecBomPosition) {
        List<TecBomPosition> tecBomPositions = tecBomPositionMapper.selectList(new QueryWrapper<TecBomPosition>().lambda()
                                                                                   .eq(TecBomPosition::getBomPositionName,
                                                                                       tecBomPosition.getBomPositionName()));
        if (ObjectUtil.isNotEmpty(tecBomPositions)) {
            throw new BaseException("BOM部位名称已存在！");
        }
        LoginUser loginUser = ApiThreadLocalUtil.get();
        tecBomPosition.setUpdateDate(new Date()).setUpdaterAccount(loginUser.getUsername());
        if (StrUtil.equals(tecBomPosition.getHandleStatus() , "5")) {
            tecBomPosition.setConfirmerAccount(loginUser.getUsername()).setConfirmDate(new Date());
        }
        int row= tecBomPositionMapper.insert(tecBomPosition);
        if (row > 0) {
            TecBomPosition tecBomPosition1 = tecBomPositionMapper.selectTecBomPositionById(tecBomPosition.getBomPositionSid());
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(tecBomPosition1.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsEms.TABLE_RECORD_TECHTRANSFER)
                        .setDocumentSid(tecBomPosition1.getBomPositionSid());
                List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                    sysTodoTask.setTitle("Bom部位档案：" + tecBomPosition1.getBomPositionCode() + " 当前是保存状态，请及时处理！")
                            .setDocumentCode(String.valueOf(tecBomPosition1.getBomPositionCode()))
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
            } else {
                //校验是否存在待办
                checkTodoExist(tecBomPosition);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(tecBomPosition.getBomPositionSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(tecBomPosition.getBomPositionSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }


    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(TecBomPosition tecBomPosition) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, tecBomPosition.getBomPositionSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, tecBomPosition.getBomPositionSid()));
        }
    }

    /**
     * 修改BOM部位档案
     *
     * @param tecBomPosition BOM部位档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTecBomPosition(TecBomPosition tecBomPosition) {
        LoginUser loginUser = ApiThreadLocalUtil.get();
        tecBomPosition.setUpdaterAccount(loginUser.getUsername())
                      .setUpdateDate(new Date());
        TecBomPosition response = tecBomPositionMapper.selectTecBomPositionById(tecBomPosition.getBomPositionSid());
        // BOM部位名称已存在
        if (StrUtil.isNotBlank(tecBomPosition.getBomPositionName()) &&
                !tecBomPosition.getBomPositionName().equals(response.getBomPositionName())) {
            List<TecBomPosition> tecBomPositions = tecBomPositionMapper.selectList(new QueryWrapper<TecBomPosition>().lambda()
                    .eq(TecBomPosition::getBomPositionName, tecBomPosition.getBomPositionName())
                    .ne(TecBomPosition::getBomPositionSid, tecBomPosition.getBomPositionSid()));
            if (ObjectUtil.isNotEmpty(tecBomPositions)) {
                throw new BaseException("BOM部位名称已存在！");
            }
        }
        if ((tecBomPosition.getHandleStatus() != "" || tecBomPosition.getHandleStatus() != null) &&
             StrUtil.equals(tecBomPosition.getHandleStatus() , "5")) {
            //设置确认人和确认时间
            tecBomPosition.setConfirmerAccount(loginUser.getUsername()).setConfirmDate(new Date());

            List<Long> sids = new ArrayList<>();
            sids.add(tecBomPosition.getBomPositionSid());
            //删除代办
            Integer deleteSysTodoTask = deleteSysTodoTask(sids);
            if (deleteSysTodoTask <= 0) {
                throw new BaseException("Bom部位删除代办异常~");
            }
        }
        int row=tecBomPositionMapper.updateById(tecBomPosition);
        if(row>0){

            //插入日志
            MongodbUtil.insertUserLog(tecBomPosition.getBomPositionSid(), BusinessType.UPDATE.getValue(), response,tecBomPosition,TITLE);
        }
        return row;
    }

    /**
     * 变更BOM部位档案
     *
     * @param tecBomPosition BOM部位档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeTecBomPosition(TecBomPosition tecBomPosition) {
        TecBomPosition response = tecBomPositionMapper.selectTecBomPositionById(tecBomPosition.getBomPositionSid());
        // BOM部位名称已存在
        if (StrUtil.isNotBlank(tecBomPosition.getBomPositionName()) &&
                !tecBomPosition.getBomPositionName().equals(response.getBomPositionName())) {
            List<TecBomPosition> tecBomPositions = tecBomPositionMapper.selectList(new QueryWrapper<TecBomPosition>().lambda()
                    .eq(TecBomPosition::getBomPositionName, tecBomPosition.getBomPositionName())
                    .ne(TecBomPosition::getBomPositionSid, tecBomPosition.getBomPositionSid()));
            if (ObjectUtil.isNotEmpty(tecBomPositions)) {
                throw new BaseException("BOM部位名称已存在！");
            }
        }
        LoginUser loginUser = ApiThreadLocalUtil.get();
        tecBomPosition.setUpdaterAccount(loginUser.getUsername()).setUpdateDate(new Date());
        int row=tecBomPositionMapper.updateAllById(tecBomPosition);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(tecBomPosition.getBomPositionSid(), BusinessType.CHANGE.ordinal(), response,tecBomPosition,TITLE);
        }
        return row;
    }

    /**
     * 批量删除BOM部位档案
     *
     * @param clientIds 需要删除的BOM部位档案ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTecBomPositionByIds(List<Long> tecBomPositionSids) {
        int affect = tecBomPositionMapper.deleteBatchIds(tecBomPositionSids);
        if (affect > 0) {
            Integer deleteSysTodoTask = deleteSysTodoTask(tecBomPositionSids);
            return deleteSysTodoTask;
        }
        return 0;
    }

    public Integer deleteSysTodoTask (List<Long> sidList) {

        List<SysTodoTask> sysTodoTasks = sysTodoTaskMapper.selectList(Wrappers.lambdaQuery(SysTodoTask.class)
                                                                              .in(SysTodoTask::getDocumentSid, sidList));
        if (CollectionUtil.isNotEmpty(sysTodoTasks)) {
            int delete = sysTodoTaskMapper.delete(Wrappers.lambdaQuery(SysTodoTask.class)
                                                          .in(SysTodoTask::getDocumentSid, sidList));
            return delete;
        }
        return 0;

    }

    /**
    * 启用/停用
    * @param tecBomPosition
    * @return
    */
    @Override
    public int changeStatus(TecBomPosition tecBomPosition){
        int row=0;
        Long[] sids=tecBomPosition.getBomPositionSidList();
        LoginUser loginUser = ApiThreadLocalUtil.get();
        if(sids!=null&&sids.length>0){
            row=tecBomPositionMapper.update(null, new UpdateWrapper<TecBomPosition>().lambda()
                    .set(TecBomPosition::getStatus ,tecBomPosition.getStatus())
                    .set(TecBomPosition::getUpdaterAccount , loginUser.getUsername())
                    .set(TecBomPosition::getUpdateDate , new Date())
                    .in(TecBomPosition::getBomPositionSid,sids));

                //插入日志
            if (row != sids.length) {
                throw new CustomException("更改状态失败,请联系管理员");
            }
            List<OperMsg> msgList=new ArrayList<>();
            String remark=tecBomPosition.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
            MongodbUtil.insertUserLog(tecBomPosition.getBomPositionSid(), BusinessType.CHECK.ordinal(), msgList,TITLE,remark);

        }
        return row;
    }


    /**
     *更改确认状态
     * @param tecBomPosition
     * @return
     */
    @Override
    public int check(TecBomPosition tecBomPosition){
        int row=0;
        Long[] sids=tecBomPosition.getBomPositionSidList();
        LoginUser loginUser = ApiThreadLocalUtil.get();
        if(sids != null&&sids.length>0){
            row = tecBomPositionMapper.update(null,new UpdateWrapper<TecBomPosition>().lambda()
                    .set(TecBomPosition::getHandleStatus ,ConstantsEms.CHECK_STATUS)
                    .set(TecBomPosition::getConfirmerAccount , loginUser.getUsername())
                    .set(TecBomPosition::getConfirmDate , new Date())
                    .in(TecBomPosition::getBomPositionSid,sids));
            if (row == sids.length) {
                List<Long> list = CollectionUtil.toList(sids);
                Integer deleteSysTodoTask = deleteSysTodoTask(list);
                if (deleteSysTodoTask != row) {
                    throw new BaseException("删除代办异常!");
                }
                return deleteSysTodoTask;
            }
            for(Long id:sids){
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }

    /**
     * BOM部位 导入
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult importDataPur(MultipartFile file) {
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
            //上下装
            List<DictData> upDownSuit = sysDictDataService.selectDictData("s_up_down_suit");
            Map<String, String> upDownSuitMaps = upDownSuit.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            List<CommonErrMsgResponse> msgList = new ArrayList<>();
            ArrayList<TecBomPosition> tecBomPositions = new ArrayList<>();
            HashSet<String> set = new HashSet<>();
            int size = readAll.size();
            if(size<2){
                throw new BaseException("表格数据不能为空");
            }
            for (int i = 0; i < readAll.size(); i++) {
                boolean isSkip=false;
                String suit=null;
                if (i < 2) {
                    //前两行跳过
                    continue;
                }
                List<Object> objects = readAll.get(i);
                copy(objects, readAll);
                int num = i + 1;
                if (objects.get(0) == null || objects.get(0) == "") {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("BOM部位名称，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    TecBomPosition tecBomPosition = tecBomPositionMapper.selectOne(new QueryWrapper<TecBomPosition>().lambda()
                            .eq(TecBomPosition::getBomPositionName, objects.get(0).toString())
                    );
                    if(!set.add(objects.get(0).toString())){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("表格中，BOM部位名称重复，导入失败！");
                        msgList.add(errMsgResponse);
                    }
                    if(tecBomPosition!=null){
                        isSkip=true;
                    }
                }
                if (objects.get(1) == null || objects.get(1) == "") {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("上下装/套装，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    suit = upDownSuitMaps.get(objects.get(1).toString());
                    if(suit==null){
                        //throw new BaseException("第"+num+"行,甲供料方式配置错误，请联系管理员，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("上下装/套装填写错误，导入失败！");
                        msgList.add(errMsgResponse);
                    }else{
                        String value=suit;
                        List<DictData> list = upDownSuit.stream()
                                .filter(m -> ConstantsEms.CHECK_STATUS.equals(m.getHandleStatus()) && "0".equals(m.getStatus()) && value.equals(m.getDictValue()))
                                .collect(Collectors.toList());
                        if(CollectionUtil.isEmpty(list)){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("上下装/套装填写错误，导入失败！");
                            msgList.add(errMsgResponse);
                        }
                    }
                }
                TecBomPosition tecBomPosition = new TecBomPosition();
                tecBomPosition.setBomPositionName((objects.get(0)==""||objects.get(0)==null)?null:objects.get(0).toString())
                        .setUpDownSuit(suit)
                        .setStatus(ConstantsEms.ENABLE_STATUS)
                        .setHandleStatus(ConstantsEms.CHECK_STATUS)
                        .setCreateDate(new Date())
                        .setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                        .setConfirmDate(new Date())
                        .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername())
                        .setPositionDescription((objects.get(2)==""||objects.get(2)==null)?null:objects.get(2).toString())
                        .setRemark((objects.get(3)==""||objects.get(3)==null)?null:objects.get(3).toString());
                if(!isSkip){
                    tecBomPositions.add(tecBomPosition);
                }
            }
            if(CollectionUtil.isNotEmpty(msgList)){
                return AjaxResult.error("导入失败",msgList);
            }
            if(CollectionUtil.isNotEmpty(tecBomPositions)){
                tecBomPositionMapper.inserts(tecBomPositions);
            }
            return AjaxResult.success("导入成功");
        }catch (BaseException e){
            throw new BaseException(e.getMessage());
        }
    }
    //填充-主表
    public void copy(List<Object> objects,List<List<Object>> readAll){
        //获取第一行的列数
        int size = readAll.get(0).size();
        //当前行的列数
        int lineSize = objects.size();
        ArrayList<Object> all = new ArrayList<>();
        for (int i=lineSize;i<size;i++){
            Object o = new Object();
            o=null;
            objects.add(o);
        }
    }

}
