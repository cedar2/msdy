package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.ManDayManufacturePlanItem;
import com.platform.ems.domain.ManWeekManufacturePlanItem;
import com.platform.ems.domain.PurPurchaseOrder;
import com.platform.ems.domain.PurPurchaseOrderItem;
import com.platform.ems.mapper.ManDayManufacturePlanItemMapper;
import com.platform.ems.mapper.ManWeekManufacturePlanItemMapper;
import com.platform.ems.service.IManWeekManufacturePlanItemService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.util.ExcelStyleUtil;
import com.platform.ems.util.MongodbUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 生产周计划-明细Service业务层处理
 *
 * @author hjj
 * @date 2021-07-16
 */
@Service
@SuppressWarnings("all")
public class ManWeekManufacturePlanItemServiceImpl extends ServiceImpl<ManWeekManufacturePlanItemMapper, ManWeekManufacturePlanItem> implements IManWeekManufacturePlanItemService {
    @Autowired
    private ManWeekManufacturePlanItemMapper manWeekManufacturePlanItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ManDayManufacturePlanItemMapper manDayManufacturePlanItemMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;


    private static final String TITLE = "生产周计划-明细";

    /**
     * 查询生产周计划-明细
     *
     * @param weekManufacturePlanItemSid 生产周计划-明细ID
     * @return 生产周计划-明细
     */
    @Override
    public ManWeekManufacturePlanItem selectManWeekManufacturePlanItemById(Long weekManufacturePlanItemSid) {
        ManWeekManufacturePlanItem manWeekManufacturePlanItem = manWeekManufacturePlanItemMapper.selectManWeekManufacturePlanItemById(weekManufacturePlanItemSid);
        MongodbUtil.find(manWeekManufacturePlanItem);
        return manWeekManufacturePlanItem;
    }

    /**
     * 查询生产周计划-明细列表
     *
     * @param manWeekManufacturePlanItem 生产周计划-明细
     * @return 生产周计划-明细
     */
    @Override
    public List<ManWeekManufacturePlanItem> selectManWeekManufacturePlanItemList(ManWeekManufacturePlanItem manWeekManufacturePlanItem) {
        return manWeekManufacturePlanItemMapper.selectManWeekManufacturePlanItemList(manWeekManufacturePlanItem);
    }

    /**
     * 新增生产周计划-明细
     * 需要注意编码重复校验
     *
     * @param manWeekManufacturePlanItem 生产周计划-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManWeekManufacturePlanItem(ManWeekManufacturePlanItem manWeekManufacturePlanItem) {
        int row = manWeekManufacturePlanItemMapper.insert(manWeekManufacturePlanItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(manWeekManufacturePlanItem.getWeekManufacturePlanItemSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改生产周计划-明细
     *
     * @param manWeekManufacturePlanItem 生产周计划-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManWeekManufacturePlanItem(ManWeekManufacturePlanItem manWeekManufacturePlanItem) {
        ManWeekManufacturePlanItem response = manWeekManufacturePlanItemMapper.selectManWeekManufacturePlanItemById(manWeekManufacturePlanItem.getWeekManufacturePlanItemSid());
        int row = manWeekManufacturePlanItemMapper.updateById(manWeekManufacturePlanItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(manWeekManufacturePlanItem.getWeekManufacturePlanItemSid(), BusinessType.UPDATE.getValue(), response, manWeekManufacturePlanItem, TITLE);
        }
        return row;
    }

    /**
     * 变更生产周计划-明细
     *
     * @param manWeekManufacturePlanItem 生产周计划-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeManWeekManufacturePlanItem(ManWeekManufacturePlanItem manWeekManufacturePlanItem) {
        ManWeekManufacturePlanItem response = manWeekManufacturePlanItemMapper.selectManWeekManufacturePlanItemById(manWeekManufacturePlanItem.getWeekManufacturePlanItemSid());
        int row = manWeekManufacturePlanItemMapper.updateAllById(manWeekManufacturePlanItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(manWeekManufacturePlanItem.getWeekManufacturePlanItemSid(), BusinessType.CHANGE.getValue(), response, manWeekManufacturePlanItem, TITLE);
        }
        return row;
    }

    /**
     * 批量删除生产周计划-明细
     *
     * @param weekManufacturePlanItemSids 需要删除的生产周计划-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManWeekManufacturePlanItemByIds(List<Long> weekManufacturePlanItemSids) {
        return manWeekManufacturePlanItemMapper.deleteBatchIds(weekManufacturePlanItemSids);
    }
    /**
     * 导出生产周计划-明细
     *
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void exportReport(HttpServletResponse response, List<ManWeekManufacturePlanItem> list , Date dateStart){
        try {
            //所属生产环节
            List<DictData> handle=sysDictDataService.selectDictData("s_handle_status");
            Map<String,String> handleMaps=handle.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel,(key1, key2)->key2));
            XSSFWorkbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("生产周计划明细报表");
            sheet.setDefaultColumnWidth(18);
            String[] titlesBe = {
                    "周计划日期",
                    "工厂(工序)",
                    "操作部门",
                    "班组",
                    "商品编码(款号)",
                    "排产批次号",
                    "工序名称"
                   };
            List<String> dateList = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
               DateTime dateTime = DateUtil.offsetDay(dateStart, i);
               String format = DateUtil.format(dateTime, "yyyy/MM/dd");
               String[] arr = format.split("/");
               dateList.add(arr[1]+"/"+arr[2]);
            }
            String[] titlesAf = {
                    "待完成量",
                    "已完成量",
                    "计划产量",
                    "计划开始日期(工序)",
                    "计划完成日期(工序)",
                    "计量单位",
                    "备注",
                    "处理状态",
                    "创建人",
                    "创建日期",
                    "生产周计划编号",
                    "商品名称",
                    "生产订单号",
            };
            //第一行数据
            Row head = sheet.createRow(0);
            //第一行样式
            CellStyle cellStyle = ExcelStyleUtil.getStyle(workbook);
            //第一行数据
            ExcelStyleUtil.setCellStyleLime(cellStyle);
            ExcelStyleUtil.setBorderStyle(cellStyle);
            for (int i = 0; i < titlesBe.length; i++) {
                Cell cell = head.createCell(i);
                cell.setCellValue(titlesBe[i]);
                cell.setCellStyle(cellStyle);
            }
            //日期
            for (int i = 0; i < dateList.size(); i++) {
                Cell cell = head.createCell(i+titlesBe.length);
                cell.setCellValue(dateList.get(i));
                cell.setCellStyle(cellStyle);
            }
            for (int i = 0; i < titlesAf.length; i++) {
                Cell cell = head.createCell(i+titlesBe.length+dateList.size());
                cell.setCellValue(titlesAf[i]);
                cell.setCellStyle(cellStyle);
            }
            CellStyle defaultCellStyle = ExcelStyleUtil.getDefaultCellStyle(workbook);
            for (int i = 0; i <list.size(); i++) {
                //第二行数据
                Row rowItem= sheet.createRow(i+1);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String start = sdf.format(list.get(i).getDateStart());
                String end = sdf.format(list.get(i).getDateEnd());
                //周计划日期
                Cell cell0 = rowItem.createCell(0);
                cell0.setCellValue(start+"~"+end);
                cell0.setCellStyle(defaultCellStyle);
                //工厂(工序)
                Cell cell1 = rowItem.createCell(1);
                cell1.setCellValue(list.get(i).getPlantName());
                cell1.setCellStyle(defaultCellStyle);
                //生产阶段
                Cell cell2 = rowItem.createCell(2);
                cell2.setCellValue(list.get(i).getDepartmentName());
                cell2.setCellStyle(defaultCellStyle);
                //班组
                Cell cell3 = rowItem.createCell(3);
                cell3.setCellValue(list.get(i).getWorkCenterName());
                cell3.setCellStyle(defaultCellStyle);
                //商品编码(款号)
                Cell cell4 = rowItem.createCell(4);
                cell4.setCellValue(list.get(i).getMaterialCode());
                cell4.setCellStyle(defaultCellStyle);
                //排产批次号
                Cell cell5 = rowItem.createCell(5);
                Long paichanBatch= list.get(i).getPaichanBatch()==null?null:list.get(i).getPaichanBatch();
                if(paichanBatch!=null){
                    cell5.setCellValue(paichanBatch);
                }
                cell5.setCellStyle(defaultCellStyle);
                //工序名称
                Cell cell6 = rowItem.createCell(6);
                cell6.setCellValue(list.get(i).getProcessName());
                cell6.setCellStyle(defaultCellStyle);
                List<ManDayManufacturePlanItem> manDayManufacturePlanItemList = list.get(i).getManDayManufacturePlanItemList();
                Boolean exit=false;
                if(CollectionUtil.isNotEmpty(manDayManufacturePlanItemList)){
                    exit=true;
                }
                for (int j = 0; j < 7; j++) {
                    Cell cellDate = rowItem.createCell(6+j+1);
                    if(exit){
                        cellDate.setCellValue(manDayManufacturePlanItemList.get(j).getPlanQuantity()==null?null:manDayManufacturePlanItemList.get(j).getPlanQuantity().toString());
                    }
                    cellDate.setCellStyle(defaultCellStyle);
                }
                BigDecimal partQuantity=new BigDecimal("0");
                BigDecimal currentCompleteQuantity=new BigDecimal("0");
                if(list.get(i).getCurrentCompleteQuantity()!=null){
                    currentCompleteQuantity=list.get(i).getCurrentCompleteQuantity();
                }
                partQuantity = list.get(i).getQuantity().subtract(currentCompleteQuantity);
                //待完成量
                Cell cell13 = rowItem.createCell(14);
                cell13.setCellValue(partQuantity.toString());
                cell13.setCellStyle(defaultCellStyle);
                //已完成量
                Cell cell14 = rowItem.createCell(15);
                cell14.setCellValue(list.get(i).getCurrentCompleteQuantity()==null?null:list.get(i).getCurrentCompleteQuantity().toString());
                cell14.setCellStyle(defaultCellStyle);
                //计划产量
                Cell cell15 = rowItem.createCell(16);
                cell15.setCellValue(list.get(i).getQuantity()==null?null:list.get(i).getQuantity().toString());
                cell15.setCellStyle(defaultCellStyle);
                //计划开始日期(工序)
                Cell cell16 = rowItem.createCell(17);
                cell16.setCellValue(list.get(i).getPlanStartDate()==null?null:sdf.format(list.get(i).getPlanStartDate()));
                cell16.setCellStyle(defaultCellStyle);
                //计划完成日期(工序)
                Cell cell17 = rowItem.createCell(18);
                cell17.setCellValue(list.get(i).getPlanEndDate()==null?null:sdf.format(list.get(i).getPlanEndDate()));
                cell17.setCellStyle(defaultCellStyle);
                //计量单位
                Cell cell18 = rowItem.createCell(19);
                cell18.setCellValue(list.get(i).getUnitBaseName());
                cell18.setCellStyle(defaultCellStyle);
                //备注
                Cell cell19 = rowItem.createCell(20);
                cell19.setCellValue(list.get(i).getRemark());
                cell19.setCellStyle(defaultCellStyle);
                //处理状态
                Cell cell190 = rowItem.createCell(21);
                cell190.setCellValue(list.get(i).getHandleStatus()==null?null:handleMaps.get(list.get(i).getHandleStatus()));
                cell190.setCellStyle(defaultCellStyle);
                //创建人
                Cell cell20 = rowItem.createCell(22);
                cell20.setCellValue(list.get(i).getCreatorAccountName());
                cell20.setCellStyle(defaultCellStyle);
                //创建日期
                Cell cell21 = rowItem.createCell(23);
                cell21.setCellValue(sdf.format(list.get(i).getCreateDate()));
                cell21.setCellStyle(defaultCellStyle);
                //生产周计划编号
                Cell cell22 = rowItem.createCell(24);
                cell22.setCellValue(list.get(i).getWeekManufacturePlanCode());
                cell22.setCellStyle(defaultCellStyle);
                //商品名称
                Cell cell23 = rowItem.createCell(25);
                cell23.setCellValue(list.get(i).getMaterialName());
                cell23.setCellStyle(defaultCellStyle);
                //生产订单号
                Cell cell24 = rowItem.createCell(26);
                cell24.setCellValue(list.get(i).getManufactureOrderCode());
                cell24.setCellStyle(defaultCellStyle);
            }
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            workbook.write(response.getOutputStream());
        }catch (Exception e){
            throw new CustomException("导出失败");
        }}

    /**
     * 生产周计划明细报表
     */
    @Override
    public List<ManWeekManufacturePlanItem> getItemList(ManWeekManufacturePlanItem manWeekManufacturePlanItem) {
        List<ManWeekManufacturePlanItem> itemList = manWeekManufacturePlanItemMapper.getItemList(manWeekManufacturePlanItem);
        if(CollectionUtil.isNotEmpty(itemList)){
            itemList.forEach(li->{
                List<ManDayManufacturePlanItem> manDayManufacturePlanItems = manDayManufacturePlanItemMapper.selectList(new QueryWrapper<ManDayManufacturePlanItem>().lambda()
                        .eq(ManDayManufacturePlanItem::getManufacturePlanItemSid, li.getWeekManufacturePlanItemSid())
                );
                li.setManDayManufacturePlanItemList(manDayManufacturePlanItems);
            });
        }
        return itemList;
    }
}
