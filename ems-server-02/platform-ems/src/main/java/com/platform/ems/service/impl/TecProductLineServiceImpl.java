package com.platform.ems.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.EstimateLineReportRequest;
import com.platform.ems.domain.dto.response.EstimateLineReportResponse;
import com.platform.ems.mapper.*;
import com.platform.ems.service.ITecProductLineService;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品线Service业务层处理
 *
 * @author linhongwei
 * @date 2021-10-21
 */
@Service
@SuppressWarnings("all")
public class TecProductLineServiceImpl extends ServiceImpl<TecProductLineMapper, TecProductLine> implements ITecProductLineService {
    @Autowired
    private TecProductLineMapper tecProductLineMapper;
    @Autowired
    private TecProductLineposMatMapper tecProductLineposMatMapper;
    @Autowired
    private TecProductLineposMatColorMapper tecProductLineposMatColorMapper;
    @Autowired
    private BasMaterialMapper basMaterialMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private TecLinePositionMapper tecLinePositionMapper;
    @Autowired
    private InvInventoryLocationMapper invInventoryLocationMapper;
    @Autowired
    private TecProductSizeZipperLengthMapper zipperLengthMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "商品线";

    /**
     * 查询商品线
     *
     * @param materialSid 商品ID
     * @return 商品线
     */
    @Override
    public TecProductLine selectTecProductLineById(Long materialSid) {
        TecProductLine tecProductLine = tecProductLineMapper.selectTecProductLineById(materialSid);
        if (tecProductLine == null) {
            return null;
        }
        //商品线部位-线料
        List<TecProductLineposMat> tecProductLineposMatList =
                tecProductLineposMatMapper.selectTecProductLineposMatList(new TecProductLineposMat().setProductSid(materialSid));
        if (CollectionUtil.isNotEmpty(tecProductLineposMatList)) {
            tecProductLine.setTecProductLineposMatList(tecProductLineposMatList);
            tecProductLineposMatList.forEach(o -> {
                //商品线部位-款色线色
                List<TecProductLineposMatColor> tecProductLineposMatColorList =
                        tecProductLineposMatColorMapper.selectTecProductLineposMatColorList(new TecProductLineposMatColor()
                                .setLineposMatSid(o.getLineposMatSid()));
                o.setTecProductLineposMatColorList(tecProductLineposMatColorList);
            });
        }
        MongodbUtil.find(tecProductLine);
        return tecProductLine;
    }

    /**
     * 查询商品线列表
     *
     * @param tecProductLine 商品线
     * @return 商品线
     */
    @Override
    public List<TecProductLine> selectTecProductLineList(TecProductLine tecProductLine) {
        return tecProductLineMapper.selectTecProductLineList(tecProductLine);
    }

    /**
     * 新增商品线
     * 需要注意编码重复校验
     *
     * @param tecProductLine 商品线
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertTecProductLine(TecProductLine tecProductLine) {
        setConfirmInfo(tecProductLine);
        int row = tecProductLineMapper.insert(tecProductLine);
        if (row > 0) {
            //商品线部位-线料
            List<TecProductLineposMat> tecProductLineposMatList = tecProductLine.getTecProductLineposMatList();
            if (CollectionUtil.isNotEmpty(tecProductLineposMatList)) {
                tecProductLineposMatList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                });
                addTecProductLineposMat(tecProductLine, tecProductLineposMatList);
                /*if (ConstantsEms.CHECK_STATUS.equals(tecProductLine.getHandleStatus())) {
                    //确认时回写线部位
                    collbackLinePosition(tecProductLineposMatList);
                }*/
                //回写线部位
                collbackLinePosition(tecProductLineposMatList);
            }
            //更新商品档案的是否创建商品线用量
            basMaterialMapper.update(null, new UpdateWrapper<BasMaterial>().lambda()
                    .set(BasMaterial::getIsHasCreatedProductLine, ConstantsEms.YES)
                    .in(BasMaterial::getMaterialSid, tecProductLine.getProductSid()));
            TecProductLine productLine = tecProductLineMapper.selectTecProductLineById(tecProductLine.getProductSid());
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(productLine.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsEms.PRODUCT_LINE)
                        .setDocumentSid(productLine.getProductLineSid());
                List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                    sysTodoTask.setTitle("商品线用量" + productLine.getMaterialCode() + "当前是保存状态，请及时处理！")
                            .setDocumentCode(String.valueOf(productLine.getMaterialCode()))
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
            } else {
                //校验是否存在待办
                checkTodoExist(tecProductLine);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(tecProductLine.getProductLineSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 回写线部位
     */
    private void collbackLinePosition(List<TecProductLineposMat> tecProductLineposMatList) {
        List<TecProductLineposMat> itemList = tecProductLineposMatList.stream().filter(item -> item.getLinePositionSid() == null).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(itemList)) {
            TecLinePosition tecLinePosition = new TecLinePosition();
            for (TecProductLineposMat item : itemList) {
                List<TecLinePosition> list = tecLinePositionMapper.selectList(new QueryWrapper<TecLinePosition>().lambda()
                        .eq(TecLinePosition::getLinePositionName, item.getLinePositionName()));
                if (CollectionUtil.isNotEmpty(list)) {
                    TecLinePosition linePosition = list.get(0);
                    //已存在相同线部位，则更新编码、sid、线部位类别、度量方法说明
                    BeanUtil.copyProperties(linePosition, item, new String[]{"creatorAccount", "createDate", "updaterAccount", "updateDate", "remark"});
                    tecProductLineposMatMapper.updateById(item);
                } else {
                    //不存在相同线部位，则插入一笔新数据，并回写编码及sid
                    BeanUtil.copyProperties(item, tecLinePosition, new String[]{"creatorAccount", "createDate", "updaterAccount", "updateDate"});
                    tecLinePosition.setConfirmDate(new Date())
                            .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername())
                            .setLinePositionCategory(ConstantsEms.ZY)
                            .setStatus(ConstantsEms.ENABLE_STATUS)
                            .setHandleStatus(ConstantsEms.CHECK_STATUS);
                    tecLinePositionMapper.insert(tecLinePosition);
                    TecLinePosition position = new TecLinePosition();
                    position.setLinePositionName(item.getLinePositionName());
                    List<TecLinePosition> linePositionList = tecLinePositionMapper.selectTecLinePositionList(position);
                    tecProductLineposMatMapper.updateById(new TecProductLineposMat().setLineposMatSid(item.getLineposMatSid())
                            .setLinePositionSid(linePositionList.get(0).getLinePositionSid())
                            .setLinePositionCode(linePositionList.get(0).getLinePositionCode()));
                }
            }
        }
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(TecProductLine tecProductLine) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, tecProductLine.getProductLineSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, tecProductLine.getProductLineSid()));
        }
    }

    /**
     * 商品线部位-线料
     */
    private void addTecProductLineposMat(TecProductLine tecProductLine, List<TecProductLineposMat> tecProductLineposMatList) {
        tecProductLineposMatMapper.delete(new UpdateWrapper<TecProductLineposMat>()
                .lambda()
                .eq(TecProductLineposMat::getProductLineSid, tecProductLine.getProductLineSid())
        );
        tecProductLineposMatList.forEach(o -> {
            o.setProductLineSid(tecProductLine.getProductLineSid());
            o.setProductSid(tecProductLine.getProductSid());
        });
        tecProductLineposMatMapper.inserts(tecProductLineposMatList);
        tecProductLineposMatList.forEach(o -> {
            //商品线部位-款色线色
            List<TecProductLineposMatColor> tecProductLineposMatColorList = o.getTecProductLineposMatColorList();
            if (CollectionUtil.isNotEmpty(tecProductLineposMatColorList)) {
                tecProductLineposMatColorMapper.delete(new UpdateWrapper<TecProductLineposMatColor>()
                        .lambda()
                        .eq(TecProductLineposMatColor::getLineposMatSid, o.getLineposMatSid()));
                tecProductLineposMatColorList.forEach(tecProductLineposMatColor -> {
                    tecProductLineposMatColor.setLineposMatSid(o.getLineposMatSid());
                });
                tecProductLineposMatColorMapper.inserts(tecProductLineposMatColorList);
            }
        });
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(TecProductLine o) {
        if (o == null) {
            return;
        }
        List<TecProductLineposMat> lineposMatList = o.getTecProductLineposMatList();
        if (CollectionUtil.isEmpty(lineposMatList)) {
            throw new BaseException("商品线用量明细不能为空");
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            List<TecProductLineposMat> itemList = lineposMatList.stream()
                    .filter(item -> item.getQuantity() != null && StrUtil.isNotBlank(item.getQuantityUnit())).collect(Collectors.toList());
            if (lineposMatList.size() != itemList.size()) {
                throw new BaseException("用量、BOM用量单位不能为空");
            }
            //物料sid
            List<Long> materialSids = lineposMatList.stream().map(TecProductLineposMat::getMaterialSid).collect(Collectors.toList());
            List<BasMaterial> basMaterialList = basMaterialMapper.selectList(new QueryWrapper<BasMaterial>().lambda()
                    .eq(BasMaterial::getStatus, ConstantsEms.DISENABLE_STATUS)
                    .in(BasMaterial::getMaterialSid, materialSids));
            if (CollectionUtil.isNotEmpty(basMaterialList)) {
                List<String> materialCode = basMaterialList.stream().map(BasMaterial::getMaterialCode).collect(Collectors.toList());
                throw new BaseException("存在停用的物料编码" + materialCode.toString() + "，请检查！");
            }
            lineposMatList.forEach(lineposMat -> {
                if (CollectionUtil.isNotEmpty(lineposMat.getTecProductLineposMatColorList())) {
                    List<TecProductLineposMatColor> colorList = lineposMat.getTecProductLineposMatColorList().stream()
                            .filter(color -> color.getMaterialSkuSid() != null).collect(Collectors.toList());
                    if (CollectionUtil.isEmpty(colorList)) {
                        throw new BaseException("存在物料的颜色未填写，无法确认！");
                    }
                }
            });
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 修改商品线
     *
     * @param tecProductLine 商品线
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTecProductLine(TecProductLine tecProductLine) {
        setConfirmInfo(tecProductLine);
        TecProductLine response = tecProductLineMapper.selectTecProductLineById(tecProductLine.getProductSid());
        int row = tecProductLineMapper.updateById(tecProductLine);
        if (row > 0) {
            //商品线部位-线料
            List<TecProductLineposMat> tecProductLineposMatList = tecProductLine.getTecProductLineposMatList();
            if (CollectionUtil.isNotEmpty(tecProductLineposMatList)) {
                addTecProductLineposMat(tecProductLine, tecProductLineposMatList);
                /*if (ConstantsEms.CHECK_STATUS.equals(tecProductLine.getHandleStatus())) {
                    //确认时回写线部位
                    collbackLinePosition(tecProductLineposMatList);
                }*/
                //回写线部位
                collbackLinePosition(tecProductLineposMatList);
            } else {
                throw new BaseException("商品线用量明细不能为空");
            }
            if (!ConstantsEms.SAVA_STATUS.equals(tecProductLine.getHandleStatus())) {
                //校验是否存在待办
                checkTodoExist(tecProductLine);
            }
            //插入日志
            MongodbUtil.insertUserLog(tecProductLine.getProductLineSid(), BusinessType.UPDATE.getValue(), response, tecProductLine, TITLE);
        }
        return row;
    }

    /**
     * 变更商品线
     *
     * @param tecProductLine 商品线
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeTecProductLine(TecProductLine tecProductLine) {
        setConfirmInfo(tecProductLine);
        tecProductLine.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        TecProductLine response = tecProductLineMapper.selectTecProductLineById(tecProductLine.getProductSid());
        int row = tecProductLineMapper.updateAllById(tecProductLine);
        if (row > 0) {
            //商品线部位-线料
            List<TecProductLineposMat> tecProductLineposMatList = tecProductLine.getTecProductLineposMatList();
            if (CollectionUtil.isNotEmpty(tecProductLineposMatList)) {
                tecProductLineposMatList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                });
                addTecProductLineposMat(tecProductLine, tecProductLineposMatList);
                /*if (ConstantsEms.CHECK_STATUS.equals(tecProductLine.getHandleStatus())) {
                    //确认时回写线部位
                    collbackLinePosition(tecProductLineposMatList);
                }*/
                //回写线部位
                collbackLinePosition(tecProductLineposMatList);
            } else {
                throw new BaseException("商品线用量明细不能为空");
            }
            //插入日志
            MongodbUtil.insertUserLog(tecProductLine.getProductLineSid(), BusinessType.CHANGE.getValue(), response, tecProductLine, TITLE);
        }
        return row;
    }

    /**
     * 批量删除商品线
     *
     * @param productLineSids 需要删除的商品线ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTecProductLineByIds(List<Long> productLineSids) {
        Integer count = tecProductLineMapper.selectCount(new QueryWrapper<TecProductLine>().lambda()
                .eq(TecProductLine::getHandleStatus, ConstantsEms.SAVA_STATUS)
                .in(TecProductLine::getProductLineSid, productLineSids));
        if (count != productLineSids.size()) {
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT);
        }
        //删除商品线部位-线料
        tecProductLineposMatMapper.delete(new UpdateWrapper<TecProductLineposMat>().lambda()
                .in(TecProductLineposMat::getProductLineSid, productLineSids));
        List<TecProductLineposMat> tecProductLineposMats = tecProductLineposMatMapper.selectList(new QueryWrapper<TecProductLineposMat>()
                .lambda().in(TecProductLineposMat::getProductLineSid, productLineSids));
        if (CollectionUtil.isNotEmpty(tecProductLineposMats)) {
            //删除商品线部位-款色线色
            List<Long> lineposMatSids = tecProductLineposMats.stream().map(TecProductLineposMat::getLineposMatSid).collect(Collectors.toList());
            tecProductLineposMatColorMapper.delete(new UpdateWrapper<TecProductLineposMatColor>().lambda()
                    .in(TecProductLineposMatColor::getLineposMatSid, lineposMatSids));
        }
        List<TecProductLine> tecProductLineList = tecProductLineMapper.selectList(new QueryWrapper<TecProductLine>().lambda()
                .in(TecProductLine::getProductLineSid, productLineSids));
        List<Long> productSids = tecProductLineList.stream().map(TecProductLine::getProductSid).collect(Collectors.toList());
        //更新商品档案的是否创建商品线用量
        basMaterialMapper.update(null, new UpdateWrapper<BasMaterial>().lambda()
                .set(BasMaterial::getIsHasCreatedProductLine, ConstantsEms.NO)
                .in(BasMaterial::getMaterialSid, productSids));
        TecProductLine tecProductLine = new TecProductLine();
        productLineSids.forEach(productLineSid ->{
            tecProductLine.setProductLineSid(productLineSid);
            //校验是否存在待办
            checkTodoExist(tecProductLine);
        });
        return tecProductLineMapper.deleteBatchIds(productLineSids);
    }

    /**
     * 更改确认状态
     *
     * @param tecProductLine
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(TecProductLine tecProductLine) {
        int row = 0;
        Long[] sids = tecProductLine.getProductLineSidList();
        if (sids != null && sids.length > 0) {
            Integer count = tecProductLineMapper.selectCount(new QueryWrapper<TecProductLine>().lambda()
                    .eq(TecProductLine::getHandleStatus, ConstantsEms.SAVA_STATUS)
                    .in(TecProductLine::getProductLineSid, sids));
            if (count != sids.length) {
                throw new BaseException(ConstantsEms.CHECK_PROMPT_STATEMENT);
            }
            row = tecProductLineMapper.update(null, new UpdateWrapper<TecProductLine>().lambda()
                    .set(TecProductLine::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .set(TecProductLine::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                    .set(TecProductLine::getConfirmDate, new Date())
                    .in(TecProductLine::getProductLineSid, sids));
            for (Long id : sids) {
                tecProductLine.setProductLineSid(id);
                //校验是否存在待办
                checkTodoExist(tecProductLine);
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 物料需求报表 线用量
     */
    @Override
    public List<EstimateLineReportResponse> getEstLine(List<EstimateLineReportRequest> requestList) {
        List<EstimateLineReportResponse> list = new ArrayList<>();
        requestList.stream().forEach(item -> {
            item.setHandleStatus(ConstantsEms.CHECK_STATUS);
            EstimateLineReportRequest estimateLineReportRequest = new EstimateLineReportRequest();
            estimateLineReportRequest.setHandleStatus(ConstantsEms.CHECK_STATUS)
                    .setSku1Sid(item.getSku1Sid())
                    .setMaterialSid(item.getMaterialSid());
            List<EstimateLineReportResponse> LineList = tecProductLineMapper.getEstimateLine(estimateLineReportRequest);
            if (CollectionUtils.isEmpty(LineList)) {
                throw new BaseException("商品"+item.getSaleMaterialCode()+"没有维护线用量信息，请核实！");
            }
            List<EstimateLineReportResponse> estimateLineList = tecProductLineMapper.getEstimateLine(item);
            //过滤 sku1为 null
            estimateLineList=estimateLineList.stream().filter(it->it.getMaterialSku1Sid()!=null).collect(Collectors.toList());
            estimateLineList.forEach(li->{
                if(li.getQuantity()==null){
                    throw new BaseException("存在用量未填写的明细行，无法测算物料需求");
                }
            });
            //赋值销售订单带过来的信息
            estimateLineList.forEach(tecBomItem -> {
                tecBomItem.setQuantityLossRate(tecBomItem.getQuantity().divide(new BigDecimal(tecBomItem.getUnitConversionRate()),4,BigDecimal.ROUND_HALF_DOWN));
                tecBomItem.setSaleMaterialName(item.getSaleMaterialName());//款名称
                tecBomItem.setSaleSku1Name(item.getSaleSku1Name());//款颜色
                tecBomItem.setSaleSku2Name(item.getSaleSku2Name());//款尺码
                tecBomItem.setSaleMaterialCode(item.getSaleMaterialCode());//款号
                tecBomItem.setSaleSku1Sid(item.getSku1Sid());
                tecBomItem.setSaleSku2Sid(item.getSku2Sid());
                tecBomItem.setSku2Sid(item.getSku2Sid());
                tecBomItem.setSaleMaterialSid(item.getMaterialSid());
                tecBomItem.setCommonSid(item.getCommonSid());
                tecBomItem.setCommonItemSid(item.getCommonItemSid());
                tecBomItem.setCommonItemNum(item.getCommonItemNum());
                tecBomItem.setCommonItemSidRemark(item.getCommonItemSid()!=null?item.getCommonItemSid().toString():null);//明细行行sid
                if(item.getSumDimension().equals("LS1")){
                    tecBomItem.setMaterialCodeRemark(item.getSaleMaterialCode());//款备注
                }
                if(item.getSumDimension().equals("KLS1")){
                    tecBomItem.setMaterialSkuRemark(item.getSaleSku1Name());//款颜色
                }
                if(item.getSumDimension().equals("KS1LS1")||item.getSumDimension().equals("DKS1LS1")){
                    tecBomItem.setMaterialSku2Remark(item.getSaleSku2Name());//款尺码
                }
                //生产订单
                if(item.getManufactureOrderCode()!=null){
                    tecBomItem.setManufactureOrderCodeRemark(item.getManufactureOrderCode().toString());
                    tecBomItem.setCommonCode(item.getManufactureOrderCode().toString());
                    tecBomItem.setManufactureOrderCode(item.getManufactureOrderCode());
                }
                //采购订单
                if(item.getPurchaseOrderCode()!=null){
                    tecBomItem.setPurchaseOrderCodeRemark(item.getPurchaseOrderCode().toString());
                    tecBomItem.setCommonCode(item.getPurchaseOrderCode().toString());
                    tecBomItem.setPurchaseOrderCode(item.getPurchaseOrderCode());
                }
                //销售订单
                if(item.getSalesOrderCode()!=null){
                    tecBomItem.setSalesOrderCodeRemark(item.getSalesOrderCode().toString());
                    tecBomItem.setCommonCode(item.getSalesOrderCode().toString());
                    tecBomItem.setSalesOrderCode(item.getSalesOrderCode());
                }
            });
            ArrayList<EstimateLineReportResponse> temporList = new ArrayList<>();
            estimateLineList.forEach(bomItem -> {
                //判断同种物料、同种sku1 合并 需求量值累加
                if (CollectionUtils.isNotEmpty(list)) {
                    Boolean exit = true;
                    for (EstimateLineReportResponse li : list) {
                        //料号+料SKU1
                        if(item.getSumDimension().equals("LS1")){
                            if (li.getMaterialSid().equals(bomItem.getMaterialSid())
                                    && li.getMaterialSku1Sid().equals(bomItem.getMaterialSku1Sid())) {
                                //计算重复物料的需求量
                                bomItem.setLossRequireQuantity(bomItem.getQuantityLossRate().multiply(item.getQuantity()));
                                li.setLossRequireQuantity(li.getLossRequireQuantity().add(bomItem.getLossRequireQuantity()));
                                //款数量
                                li.setProductQuantity(li.getProductQuantity().add(item.getQuantity()));
                                li.setQuantityLossRate(li.getQuantityLossRate().add(bomItem.getQuantityLossRate()));
                                //款备注
                                String materialCodeRemark = bomItem.getMaterialCodeRemark();//当前符合条件的款备注
                                String remark = li.getMaterialCodeRemark();//当前所有的跨备注
                                Boolean match = match(remark, materialCodeRemark);//重复校验
                                if(!match){
                                    String code = materialCodeRemark + ";" + li.getMaterialCodeRemark();
                                    li.setMaterialCodeRemark(code);
                                }
                                //订单号赋值
                                setCode(li, bomItem);
                                exit = false;
                                break;
                            }
                        }
                        //款号+料号+料SKU1
                        if(item.getSumDimension().equals("KLS1")){
                            if (li.getMaterialSid().equals(bomItem.getMaterialSid())
                                    && li.getMaterialSku1Sid().equals(bomItem.getMaterialSku1Sid())
                                    &&li.getSaleMaterialCode().equals(bomItem.getSaleMaterialCode())) {
                                //计算重复物料的需求量
                                bomItem.setLossRequireQuantity(bomItem.getQuantityLossRate().multiply(item.getQuantity()));
                                li.setLossRequireQuantity(li.getLossRequireQuantity().add(bomItem.getLossRequireQuantity()));
                                //款数量
                                li.setProductQuantity(li.getProductQuantity().add(item.getQuantity()));
                                li.setQuantityLossRate(li.getQuantityLossRate().add(bomItem.getQuantityLossRate()));
                                //款颜色
                                String materialSkuRemark = bomItem.getMaterialSkuRemark();//当前符合条件的款备注
                                String remark = li.getMaterialSkuRemark();//当前所有的跨备注
                                Boolean match = match(remark, materialSkuRemark);//重复校验
                                if(!match){
                                    String code = materialSkuRemark + ";" + li.getMaterialSkuRemark();
                                    li.setMaterialSkuRemark(code);
                                }
                                //订单号赋值
                                setCode( li, bomItem);
                                exit = false;
                                break;
                            }
                        }

                        //款号+款颜色+料号+料SKU1
                        if(item.getSumDimension().equals("KS1LS1")){
                            if (li.getMaterialSid().equals(bomItem.getMaterialSid())
                                    && li.getMaterialSku1Sid().equals(bomItem.getMaterialSku1Sid())
                                    &&li.getSaleMaterialCode().equals(bomItem.getSaleMaterialCode())
                                    &&li.getSaleSku1Name().equals(bomItem.getSaleSku1Name())) {
                                //计算重复物料的需求量
                                bomItem.setLossRequireQuantity(bomItem.getQuantityLossRate().multiply(item.getQuantity()));
                                li.setLossRequireQuantity(li.getLossRequireQuantity().add(bomItem.getLossRequireQuantity()));
                                //款数量
                                li.setProductQuantity(li.getProductQuantity().add(item.getQuantity()));
                                li.setQuantityLossRate(li.getQuantityLossRate().add(bomItem.getQuantityLossRate()));
                                //款尺码
                                String materialSku2Remark = bomItem.getMaterialSku2Remark();//当前符合条件的款备注
                                String remark = li.getMaterialSku2Remark();//当前所有的跨备注
                                Boolean match = match(remark, materialSku2Remark);//重复校验
                                if(!match){
                                    String code = materialSku2Remark + ";" + li.getMaterialSku2Remark();
                                    li.setMaterialSku2Remark(code);
                                }
                                //订单号赋值
                                setCode(li,bomItem);
                                exit = false;
                                break;
                            }
                        }

                        //商品订单号+款号+款颜色+料号+料SKU1
                        if(item.getSumDimension().equals("DKS1LS1")){
                            if (    li.getCommonCode()!=null?(
                                    li.getMaterialSid().equals(bomItem.getMaterialSid())
                                            && li.getMaterialSku1Sid().equals(bomItem.getMaterialSku1Sid())
                                            &&li.getSaleMaterialCode().equals(bomItem.getSaleMaterialCode())
                                            &&li.getSaleSku1Name().equals(bomItem.getSaleSku1Name())
                                            &&li.getCommonCode().equals(bomItem.getCommonCode())
                                    ):(
                                    li.getMaterialSid().equals(bomItem.getMaterialSid())
                                            && li.getMaterialSku1Sid().equals(bomItem.getMaterialSku1Sid())
                                            &&li.getSaleMaterialCode().equals(bomItem.getSaleMaterialCode())
                                            &&li.getSaleSku1Name().equals(bomItem.getSaleSku1Name())
                                    )

                            ) {
                                //计算重复物料的需求量
                                bomItem.setLossRequireQuantity(bomItem.getQuantityLossRate().multiply(item.getQuantity()));
                                li.setLossRequireQuantity(li.getLossRequireQuantity().add(bomItem.getLossRequireQuantity()));
                                //款数量
                                li.setProductQuantity(li.getProductQuantity().add(item.getQuantity()));
                                li.setQuantityLossRate(li.getQuantityLossRate().add(bomItem.getQuantityLossRate()));
                                //款尺码
                                String materialSku2Remark = bomItem.getMaterialSku2Remark();//当前符合条件的款备注
                                String remark = li.getMaterialSku2Remark();//当前所有的跨备注
                                Boolean match = match(remark, materialSku2Remark);//重复校验
                                if(!match){
                                    String code = materialSku2Remark + ";" + li.getMaterialSku2Remark();
                                    li.setMaterialSku2Remark(code);
                                }
                                //订单号赋值
                                setCodeItem(li,bomItem);
                                exit = false;
                                break;
                            }
                        }
                    }
                    if (exit) {
                        temporList.clear();
                        bomItem.setLossRequireQuantity(bomItem.getQuantityLossRate().multiply(item.getQuantity()));
                        //款数量
                        bomItem.setProductQuantity(item.getQuantity());
                        //添加明细行数量
                        if(bomItem.getCommonItemSidRemark()!=null){
                            HashMap<String, BigDecimal> quantityMap = bomItem.getQuantityMap();
                            quantityMap.put(bomItem.getCommonItemSidRemark(),bomItem.getLossRequireQuantity());
                        }
                        temporList.add(bomItem);
                    }

                } else {
                    bomItem.setLossRequireQuantity(bomItem.getQuantityLossRate().multiply(item.getQuantity()));
                    //款数量
                    bomItem.setProductQuantity(item.getQuantity());
                    //添加明细行数量
                    if(bomItem.getCommonItemSidRemark()!=null){
                        HashMap<String, BigDecimal> quantityMap = bomItem.getQuantityMap();
                        quantityMap.put(bomItem.getCommonItemSidRemark(),bomItem.getLossRequireQuantity());
                    }
                    temporList.add(bomItem);
                }
                list.addAll(temporList);
                temporList.clear();
            });
        });
        //过滤
        String sumDimension = requestList.get(0).getSumDimension();//汇总维度
        if(sumDimension.equals("LS1")){
            list.forEach(li->{
                li.setSaleMaterialCode(null)
                        .setSaleMaterialName(null)
                        .setSaleMaterialSid(null)
                        .setSaleSku2Sid(null)
                        .setSaleSku2Name(null)
                        .setSaleSku1Sid(null)
                        .setQuantityLossRate(null)
                        .setSku2Name(null)
                        .setSaleSku1Name(null);
            });
        }else if(sumDimension.equals("KLS1")){
            list.forEach(li->{
                li.setSaleSku2Name(null)
                        .setSaleSku2Sid(null)
                        .setSku2Name(null)
                        .setSaleSku1Sid(null)
                        .setQuantityLossRate(null)
                        .setSaleSku1Name(null);
            });
        }else if(sumDimension.equals("KS1LS1")||sumDimension.equals("DKS1LS1")) {
            list.forEach(li->{
                li.setSaleSku2Sid(null)
                        .setSku2Name(null)
                        .setSaleSku2Name(null);
            });
        }
        if(sumDimension.equals("DKS1LS1")||sumDimension.equals("DKS1S2LS1S2")){
            list.forEach(li->{
                li.setSalesOrderCodeRemark(null)
                        .setPurchaseOrderCodeRemark(null)
                        .setManufactureOrderCodeRemark(null);

            });
        }
        list.forEach(li->{
            if(li.getPurchaseOrderCodeRemark()!=null){
                li.setPurchaseOrderCode(null);
            }
            if(li.getSalesOrderCodeRemark()!=null){
                li.setSalesOrderCode(null);
            }
            if(li.getManufactureOrderCodeRemark()!=null){
                li.setManufactureOrderCode(null);
            }
        });
        //计算可用库存量
        list.forEach(bomItem -> {
            List<InvInventoryLocation> invInventoryLocations = invInventoryLocationMapper.selectList(new QueryWrapper<InvInventoryLocation>().lambda()
                    .eq(InvInventoryLocation::getMaterialSid, bomItem.getMaterialSid())
                    .eq(InvInventoryLocation::getSku1Sid, bomItem.getMaterialSku1Sid())
            );
            //计算该物料所有仓库库存量信息
            if (CollectionUtils.isNotEmpty(invInventoryLocations)) {
                int sum = invInventoryLocations.stream().mapToInt(o -> o.getUnlimitedQuantity().intValue()).sum();
                bomItem.setUnlimitedQuantity(sum);
            } else {
                bomItem.setUnlimitedQuantity(0);
            }
        });
        //需求量计算取整
        list.forEach(h->{
            if(ConstantsEms.YES.equals(h.getIsInteger())){

            }else{
//                h.setRequireQuantity(h.getRequireQuantity().setScale(4,BigDecimal.ROUND_HALF_UP))
//                        .setLossRequireQuantity(h.getLossRequireQuantity().setScale(4,BigDecimal.ROUND_HALF_UP));
//                DecimalFormat decimalFormat = new DecimalFormat("0.0000#");
//                String requireQuantity = decimalFormat.format(h.getRequireQuantity().setScale(4,BigDecimal.ROUND_HALF_UP));
//                String lossRequireQuantity = decimalFormat.format(h.getLossRequireQuantity().setScale(4,BigDecimal.ROUND_HALF_UP));
//                h.setRequireQuantityView(requireQuantity)
//                        .setLossRequireQuantityView(lossRequireQuantity);
            }
        });
        list.forEach(li->{
          li.setBomMaterialSid(li.getMaterialSid())
                  .setBomMaterialSku1Sid(li.getMaterialSku1Sid())
                  .setProductQuantity(null)
                  .setSku1Name(li.getMaterialSku1Name());
        });
        //按物料编码降序
        List<EstimateLineReportResponse> descList = list.stream()
                .sorted(Comparator.comparing(EstimateLineReportResponse::getMaterialCode).reversed()
                )
                .collect(Collectors.toList());
        return descList;
    }

    /**
     * 匹配值 是否重复
     */
    public Boolean match(String remark,String match){
        String[] remarkList = remark.split(";");
        List remarkListNow = Arrays.asList(remarkList);
        boolean exit = remarkListNow.stream().anyMatch(m -> m.equals(match));
        return exit;
    }
    /**
     * 订单号赋值去重
     */
    public void setCode(EstimateLineReportResponse li,EstimateLineReportResponse tecBomItem){
        //生产订单
        if(tecBomItem.getManufactureOrderCodeRemark()!=null){
            String man = tecBomItem.getManufactureOrderCodeRemark();
            String remark = li.getManufactureOrderCodeRemark();
            Boolean match = match(remark, man);
            if(!match){
                String code = man + ";" + li.getMaterialSku2Remark();
                li.setManufactureOrderCodeRemark(code);
            }
        }
        //采购订单
        if(tecBomItem.getPurchaseOrderCodeRemark()!=null){
            String man = tecBomItem.getPurchaseOrderCodeRemark();
            String remark = li.getPurchaseOrderCodeRemark();
            Boolean match = match(remark, man);
            if(!match){
                String code = man + ";" + li.getPurchaseOrderCodeRemark();
                li.setPurchaseOrderCodeRemark(code);
            }
        }
        //销售订单
        if(tecBomItem.getSalesOrderCodeRemark()!=null){
            String man = tecBomItem.getSalesOrderCodeRemark();
            String remark = li.getSalesOrderCodeRemark();
            Boolean match = match(remark, man);
            if(!match){
                String code = man + ";" + li.getSalesOrderCodeRemark();
                li.setSalesOrderCodeRemark(code);
            }
        }
        //来源数据明细行sid
        if(tecBomItem.getCommonItemSidRemark()!=null){
            String man = tecBomItem.getCommonItemSidRemark();
            String remark = li.getCommonItemSidRemark();
            Boolean match = match(remark, man);
            HashMap<String, BigDecimal> quantityMap = li.getQuantityMap();
            BigDecimal quantity = quantityMap.get(man);
            if(quantity==null){
                quantityMap.put(man,tecBomItem.getLossRequireQuantity());
            }else{
                quantityMap.put(man,quantity.add(tecBomItem.getLossRequireQuantity()));
            }
            if(!match){
                String code = man + ";" + li.getCommonItemSidRemark();
                li.setCommonItemSidRemark(code);
            }
        }
    }

    /**
     * 订单号行sid处理
     */
    public void setCodeItem(EstimateLineReportResponse li,EstimateLineReportResponse tecBomItem){
        //来源数据明细行sid
        if(tecBomItem.getCommonItemSidRemark()!=null){
            String man = tecBomItem.getCommonItemSidRemark();
            String remark = li.getCommonItemSidRemark();
            Boolean match = match(remark, man);
            HashMap<String, BigDecimal> quantityMap = li.getQuantityMap();
            BigDecimal quantity = quantityMap.get(man);
            if(quantity==null){
                quantityMap.put(man,tecBomItem.getLossRequireQuantity());
            }else{
                quantityMap.put(man,quantity.add(tecBomItem.getLossRequireQuantity()));
            }
            if(!match){
                String code = man + ";" + li.getCommonItemSidRemark();
                li.setCommonItemSidRemark(code);
            }
        }
    }

    /**
     * 添加线部位时校验名称是否重复
     */
    @Override
    public TecProductLineposMat verifyPosition(TecProductLineposMat tecProductLineposMat) {
        if (tecProductLineposMat == null) {
            return null;
        }
        List<TecLinePosition> list = tecLinePositionMapper.selectList(new QueryWrapper<TecLinePosition>().lambda()
                .eq(TecLinePosition::getLinePositionName, tecProductLineposMat.getLinePositionName()));
        //已存在相同线部位，则回写编码及sid
        if (CollectionUtil.isNotEmpty(list)) {
            TecLinePosition linePosition = list.get(0);
            BeanUtil.copyProperties(linePosition, tecProductLineposMat, new String[]{"creatorAccount", "createDate", "updaterAccount", "updateDate", "remark"});
            tecProductLineposMat.setLinePositionCategory(linePosition.getLinePositionCategory());
        }
        return tecProductLineposMat;
    }
}
