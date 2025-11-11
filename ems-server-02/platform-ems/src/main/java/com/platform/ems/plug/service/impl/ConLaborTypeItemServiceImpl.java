package com.platform.ems.plug.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.ManProcess;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.mapper.ManProcessMapper;
import com.platform.ems.plug.domain.ConLaborType;
import com.platform.ems.plug.domain.ConLaborTypeItem;
import com.platform.ems.plug.mapper.ConLaborTypeItemMapper;
import com.platform.ems.plug.mapper.ConLaborTypeMapper;
import com.platform.ems.plug.service.IConLaborTypeItemService;
import com.platform.ems.plug.service.IConLaborTypeService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 工价类型/工价费用项对照Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-10
 */
@Service
@SuppressWarnings("all")
public class ConLaborTypeItemServiceImpl extends ServiceImpl<ConLaborTypeItemMapper, ConLaborTypeItem> implements IConLaborTypeItemService {
    @Autowired
    private ConLaborTypeItemMapper conLaborTypeItemMapper;
    @Autowired
    private ConLaborTypeMapper conLaborTypeMapper;
    @Autowired
    private IConLaborTypeService conLaborTypeService;
    @Autowired
    private ManProcessMapper manProcessMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "工价类型/工价费用项对照";

    /**
     * 查询工价类型/工价费用项对照
     *
     * @param laborTypeItemSid 工价类型/工价费用项对照ID
     * @return 工价类型/工价费用项对照
     */
    @Override
    public ConLaborTypeItem selectConLaborTypeItemById(Long laborTypeItemSid) {
        ConLaborTypeItem conLaborTypeItem = conLaborTypeItemMapper.selectConLaborTypeItemById(laborTypeItemSid);
        MongodbUtil.find(conLaborTypeItem);
        return conLaborTypeItem;
    }

    /**
     * 查询工价类型/工价费用项对照列表  (主表详情的明细页面按序号+名称排序)
     *
     * @param conLaborTypeItem 工价类型/工价费用项对照
     * @return 工价类型/工价费用项对照
     */
    @Override
    public List<ConLaborTypeItem> selectConLaborTypeItemList(ConLaborTypeItem conLaborTypeItem) {
        return conLaborTypeItemMapper.selectConLaborTypeItemList(conLaborTypeItem);
    }

    /**
     * 查询工价类型/工价费用项对照列表  (查询页面按工价类型+编码排序)
     * @author chenkw
     * @param conLaborTypeItem 工价类型/工价费用项对照
     * @return 工价类型/工价费用项对照
     */
    @Override
    public List<ConLaborTypeItem> selectTypeItemList(ConLaborTypeItem conLaborTypeItem) {
        return conLaborTypeItemMapper.selectTypeItemList(conLaborTypeItem);
    }

    /**
     * 新增工价类型/工价费用项对照
     * 需要注意编码重复校验
     *
     * @param conLaborTypeItem 工价类型/工价费用项对照
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConLaborTypeItem(ConLaborTypeItem conLaborTypeItem) {
        List<ConLaborTypeItem> codeList = conLaborTypeItemMapper.selectList(new QueryWrapper<ConLaborTypeItem>().lambda()
                .eq(ConLaborTypeItem::getItemCode, conLaborTypeItem.getItemCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConLaborTypeItem> nameList = conLaborTypeItemMapper.selectList(new QueryWrapper<ConLaborTypeItem>().lambda()
                .eq(ConLaborTypeItem::getItemName, conLaborTypeItem.getItemName())
                .eq(ConLaborTypeItem::getLaborTypeSid, conLaborTypeItem.getLaborTypeSid()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException("该工价类型的工价项名称已存在");
        }
        if (ConstantsEms.CHECK_STATUS.equals(conLaborTypeItem.getHandleStatus())) {
            conLaborTypeItem.setConfirmDate(new Date());
            conLaborTypeItem.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        conLaborTypeItem.setLaborTypeCode(conLaborTypeService.selectConLaborTypeCodeBySid(conLaborTypeItem.getLaborTypeSid()));
        int row = conLaborTypeItemMapper.insert(conLaborTypeItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conLaborTypeItem.getLaborTypeItemSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改工价类型/工价费用项对照
     *
     * @param conLaborTypeItem 工价类型/工价费用项对照
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConLaborTypeItem(ConLaborTypeItem conLaborTypeItem) {
        ConLaborTypeItem laborTypeItem = conLaborTypeItemMapper.selectOne(new QueryWrapper<ConLaborTypeItem>().lambda()
                .eq(ConLaborTypeItem::getItemName, conLaborTypeItem.getItemName())
                .eq(ConLaborTypeItem::getLaborTypeSid, conLaborTypeItem.getLaborTypeSid()));
        if (!conLaborTypeItem.getLaborTypeItemSid().equals(laborTypeItem.getLaborTypeItemSid())) {
            throw new CheckedException("工价项名称已存在！");
        }
        ConLaborTypeItem response = conLaborTypeItemMapper.selectConLaborTypeItemById(conLaborTypeItem.getLaborTypeItemSid());
        if (response.getLaborTypeSid() == null || !response.getLaborTypeSid().equals(conLaborTypeItem.getLaborTypeSid())){
            conLaborTypeItem.setLaborTypeCode(conLaborTypeService.selectConLaborTypeCodeBySid(conLaborTypeItem.getLaborTypeSid()));
        }
        int row = conLaborTypeItemMapper.updateById(conLaborTypeItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conLaborTypeItem.getLaborTypeItemSid(), BusinessType.UPDATE.getValue(), response, conLaborTypeItem, TITLE);
        }
        return row;
    }

    /**
     * 变更工价类型/工价费用项对照
     *
     * @param conLaborTypeItem 工价类型/工价费用项对照
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConLaborTypeItem(ConLaborTypeItem conLaborTypeItem) {
        List<ConLaborTypeItem> nameList = conLaborTypeItemMapper.selectList(new QueryWrapper<ConLaborTypeItem>().lambda()
                .eq(ConLaborTypeItem::getItemName, conLaborTypeItem.getItemName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getLaborTypeSid().equals(conLaborTypeItem.getLaborTypeSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conLaborTypeItem.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConLaborTypeItem response = conLaborTypeItemMapper.selectConLaborTypeItemById(conLaborTypeItem.getLaborTypeItemSid());
        if (response.getLaborTypeSid() == null || !response.getLaborTypeSid().equals(conLaborTypeItem.getLaborTypeSid())){
            conLaborTypeItem.setLaborTypeCode(conLaborTypeService.selectConLaborTypeCodeBySid(conLaborTypeItem.getLaborTypeSid()));
        }
        int row = conLaborTypeItemMapper.updateAllById(conLaborTypeItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conLaborTypeItem.getLaborTypeItemSid(), BusinessType.CHANGE.getValue(), response, conLaborTypeItem, TITLE);
        }
        return row;
    }

    /**
     * 批量删除工价类型/工价费用项对照
     *
     * @param laborTypeItemSids 需要删除的工价类型/工价费用项对照ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConLaborTypeItemByIds(List<Long> laborTypeItemSids) {
        List<ConLaborTypeItem> conLaborTypeItemList = conLaborTypeItemMapper.selectList(new QueryWrapper<ConLaborTypeItem>().lambda()
                .in(ConLaborTypeItem::getLaborTypeItemSid, laborTypeItemSids).eq(ConLaborTypeItem::getHandleStatus, ConstantsEms.CHECK_STATUS));
        if (conLaborTypeItemList.size() > 0) {
            throw new BaseException("已确认数据不可删除");
        }
        return conLaborTypeItemMapper.deleteBatchIds(laborTypeItemSids);
    }

    /**
     * 启用/停用
     *
     * @param conLaborTypeItem
     * @return
     */
    @Override
    public int changeStatus(ConLaborTypeItem conLaborTypeItem) {
        int row = 0;
        Long[] sids = conLaborTypeItem.getLaborTypeItemSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conLaborTypeItem.setLaborTypeItemSid(id);
                row = conLaborTypeItemMapper.updateById(conLaborTypeItem);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conLaborTypeItem.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conLaborTypeItem.getLaborTypeItemSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conLaborTypeItem
     * @return
     */
    @Override
    public int check(ConLaborTypeItem conLaborTypeItem) {
        int row = 0;
        Long[] sids = conLaborTypeItem.getLaborTypeItemSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conLaborTypeItem.setLaborTypeItemSid(id);
                row = conLaborTypeItemMapper.updateById(conLaborTypeItem);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(conLaborTypeItem.getLaborTypeItemSid(), BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    //获取下拉框
    @Override
    public List<ConLaborTypeItem> getConLaborTypeItemList() {
        return conLaborTypeItemMapper.getConLaborTypeItemList();
    }

    /**
     * 导入
     * @param file
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object importData(MultipartFile file) {
        //每行对象
        List<ConLaborTypeItem> typeItemList = new ArrayList<>();
        //错误信息
        CommonErrMsgResponse errMsg = null;
        List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
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
            //excel表里面编码和名称的缓存
            HashMap<String, String> codeMap = new HashMap<>();
            HashMap<String, String> nameMap = new HashMap<>();
            //读excel行和列
            for (int i = 0; i < readAll.size(); i++) {
                if (i < 1) {
                    //前一行跳过
                    continue;
                }
                List<Object> objects = readAll.get(i);
                //填充总列数
                copy(objects, readAll);
                num = i + 1;
                /**
                 * 工价项类型编码 必填
                 */
                String laborTypeCode = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                Long laborTypeSid = null;
                if (laborTypeCode != null){
                    try {
                        ConLaborType type = conLaborTypeMapper.selectOne(new QueryWrapper<ConLaborType>()
                                .lambda().eq(ConLaborType::getLaborTypeCode,laborTypeCode));
                        if (type != null){
                            laborTypeSid = type.getLaborTypeSid();
                        }else {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("工价项类型"+laborTypeCode + "不存在，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(laborTypeCode + "工价项类型编码存在重复，请先检查该工价项类型，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 工价项编码 必填
                 */
                String itemCode = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                if (itemCode == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("工价项编码不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    // 判断是否与表格内的编码重复
                    if (codeMap.get(itemCode) == null) {
                        codeMap.put(itemCode, String.valueOf(num));
                        List<ConLaborTypeItem> codeList = conLaborTypeItemMapper.selectList(new QueryWrapper<ConLaborTypeItem>().lambda()
                                .eq(ConLaborTypeItem::getItemCode, itemCode));
                        if (CollectionUtil.isNotEmpty(codeList)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("系统中，"+"工价项编码已存在，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }else {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("表格中，"+"工价项编码已存在，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 工价项名称 必填
                 */
                String itemName = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                if (itemName == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("工价项名称不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    // 判断是否与表格内的名称重复
                    if (codeMap.get(itemCode) == null) {
                        codeMap.put(itemCode, String.valueOf(num));
                        List<ConLaborTypeItem> nameList = conLaborTypeItemMapper.selectList(new QueryWrapper<ConLaborTypeItem>().lambda()
                                .eq(ConLaborTypeItem::getItemName, itemName));
                        if (CollectionUtil.isNotEmpty(nameList)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("系统中，"+"工价项名称已存在，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }else {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("表格中，"+"工价项名称已存在，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 工序名称 必填
                 */
                String processName = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString();
                Long processSid = null;
                if (processName != null){
                    try {
                        ManProcess process = manProcessMapper.selectOne(new QueryWrapper<ManProcess>()
                                .lambda().eq(ManProcess::getProcessName,processName));
                        if (process != null){
                            processSid = process.getProcessSid();
                        }else {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("工序"+processName + "不存在，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(processName + "工序档案存在重复，请先检查该工序，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                if (CollectionUtil.isEmpty(errMsgList)){
                    ConLaborTypeItem typeItem = new ConLaborTypeItem();
                    typeItem.setLaborTypeSid(laborTypeSid).setItemCode(itemCode).setItemName(itemName)
                            .setProcessSid(processSid).setHandleStatus(ConstantsEms.CHECK_STATUS).setStatus(ConstantsEms.SAVA_STATUS)
                            .setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
                    typeItemList.add(typeItem);
                }
            }
            if (CollectionUtil.isNotEmpty(errMsgList)){
                return errMsgList ;
            }
            if (CollectionUtil.isNotEmpty(typeItemList)){
                conLaborTypeItemMapper.inserts(typeItemList);
            }
        }catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
        return num-2;
    }

    //填充-主表
    public void copy(List<Object> objects, List<List<Object>> readAll){
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
