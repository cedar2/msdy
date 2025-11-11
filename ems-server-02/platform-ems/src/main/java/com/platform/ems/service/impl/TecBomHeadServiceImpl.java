package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.SecurityUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.*;
import com.platform.ems.domain.dto.response.*;
import com.platform.ems.mapper.*;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.service.ISysFormProcessService;
import com.platform.ems.service.ISystemUserService;
import com.platform.ems.service.ITecBomHeadService;
import com.platform.ems.util.ExcelStyleUtil;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbUtil;
import com.platform.api.service.RemoteFlowableService;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 物料清单（BOM）主Service业务层处理
 *
 * @author qhq
 * @date 2021-03-15
 */
@Service
@SuppressWarnings("all")
public class TecBomHeadServiceImpl extends ServiceImpl<TecBomHeadMapper,TecBomHead>  implements ITecBomHeadService {
	@Autowired
	private TecBomHeadMapper tecBomHeadMapper;
	@Autowired
	private TecBomAttachmentMapper tecBomAttachmentMapper;
	@Autowired
	private TecBomItemMapper tecBomItemMapper;
	@Autowired
	private BasSkuMapper basSkuMapper;
	@Autowired
	private TecBomSizeQuantityMapper tecBomSizeQuantityMapper;
	@Autowired
	private BasMaterialMapper basMaterialMapper;
	@Autowired
	private BasMaterialSkuMapper basMaterialSkuMapper;
	@Autowired
	RedissonClient redissonClient;
	@Autowired
	private BasMaterialServiceImpl basMaterialServiceImpl;
	@Autowired
	private ISystemDictDataService sysDictDataService;
	@Autowired
	private ISysFormProcessService formProcessService;
	@Autowired
	private ISystemUserService userService;
	@Autowired
	private SysTodoTaskMapper sysTodoTaskMapper;
	@Autowired
	private BasMaterialAttachmentMapper basMaterialAttachmentMapper;
	@Autowired
	private RemoteFlowableService flowableService;


	private static final String LOCK_KEY = "BOM_STOCK";
	private static final String TITLE = "BOM";

	private static final String TABLE = "s_tec_bom_head";

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss");
	/**
	 * 查询物料清单（BOM）主
	 *
	 * @param clientId 物料清单（BOM）主ID-旧
	 * @return 物料清单（BOM）主
	 */
	@Override
	public List<TecBomHead> selectTecBomHeadById(TecBomHead tecBomHead) {
		List<BasMaterialSku> colorSort=new ArrayList<>();
		List<TecBomHead> bomHead = tecBomHeadMapper.selectTecBomHeadList(tecBomHead);
		MongodbUtil.find(bomHead.get(0));
		if(CollectionUtils.isEmpty(bomHead)) {
			throw new CheckedException("未查询到所需数据，请重试！");
		}
		//过滤停用状态的bom
		bomHead=bomHead.stream().filter(bom->bom.getStatus().equals(ConstantsEms.ENABLE_STATUS)).collect(Collectors.toList());
		BasMaterial material = basMaterialMapper.selectBasMaterialById(tecBomHead.getMaterialSid());
		bomHead.stream().forEach(bom->{
			if(material!=null){
				bom.setMaterialCode(material.getMaterialCode());
				//物料&商品-SKU明细对象
				BasMaterialSku basMaterialSku = new BasMaterialSku();
				basMaterialSku.setMaterialSid(tecBomHead.getMaterialSid());
				List<BasMaterialSku> basMaterialSkuList = basMaterialSkuMapper.selectBasMaterialSkuList(basMaterialSku);
				//不必要减少字段的回显
//				List<BasMaterialSku> basMaterialSkuList = basMaterialSkuMapper.selectBomBasMaterialSkuList(Long.valueOf(tecBomHead.getMaterialSid()));
				if(!CollectionUtils.isEmpty(basMaterialSkuList)) {
					basMaterialSkuList=basMaterialSkuList.stream().filter(li->li.getStatus().equals(ConstantsEms.ENABLE_STATUS)).collect(Collectors.toList());
					basMaterialSkuList=basMaterialSkuList.stream()
							.sorted(Comparator.comparing(item -> item.getItemNum())).collect(Collectors.toList());
					if(CollectionUtil.isEmpty(colorSort)){
						colorSort.addAll(basMaterialSkuList);
					}
					material.setBasMaterialSkuList(basMaterialSkuList);
				}
				bom.setMaterial(material);
			}
			List<TecBomAttachment> bomAttachmentList = tecBomAttachmentMapper.selectAttachmentByBomSid(bom.getBomSid());
			if(!CollectionUtils.isEmpty(bomAttachmentList)){
				bom.setAttachmentList(tecBomAttachmentMapper.selectAttachmentByBomSid(bom.getBomSid()));
			}
			List<TecBomItem> itemList = tecBomItemMapper.selectBomItemByBomSid(bom.getBomSid());
			itemList.stream().forEach(item->{
				BasMaterial materialItem = basMaterialMapper.selectBasMaterialBomById(item.getBomMaterialSid());
				if(materialItem!=null) {
					//物料&商品-SKU明细对象
					BasMaterialSku basMaterialSku = new BasMaterialSku();
					basMaterialSku.setMaterialSid(item.getBomMaterialSid());
					List<BasMaterialSku> basMaterialSkuList = basMaterialSkuMapper.selectBasMaterialSkuList(basMaterialSku);
					//减少不必要的字段返回
//					List<BasMaterialSku> basMaterialSkuList = basMaterialSkuMapper.selectBomBasMaterialSkuList(Long.valueOf(tecBomHead.getMaterialSid()));
					if(!CollectionUtils.isEmpty(basMaterialSkuList)) {
						materialItem.setBasMaterialSkuList(basMaterialSkuList);
					}
					item.setMaterial(materialItem);
				}
			});
			if(CollectionUtil.isNotEmpty(itemList)){
				List<TecBomItem> tecBomItems = new ArrayList<>();
				List<TecBomItem> tecBomItemsSort = new ArrayList<>();
				//无序面料
				List<TecBomItem> tecBomItemSortNoM=itemList.stream().filter(item->item.getSort()==null&&item.getMaterialType().equals("9")).collect(Collectors.toList());
				//无序辅料
				List<TecBomItem> tecBomItemSortNoF=itemList.stream().filter(item->item.getSort()==null&&item.getMaterialType().equals("11")).collect(Collectors.toList());;
				//有序其他
				List<TecBomItem> tecBomItemSort = itemList.stream().filter(item->item.getSort()!=null&&!item.getMaterialType().equals("9")&&!item.getMaterialType().equals("11")).collect(Collectors.toList());
				//有序面料
				List<TecBomItem> tecBomItemSortM = itemList.stream().filter(item->item.getSort()!=null&&item.getMaterialType().equals("9")).collect(Collectors.toList());
				//有序辅料
				List<TecBomItem> tecBomItemSortF = itemList.stream().filter(item->item.getSort()!=null&&item.getMaterialType().equals("11")).collect(Collectors.toList());
				//无序其他
				List<TecBomItem>tecBomItemSortNo =  itemList.stream().filter(item->item.getSort()==null&&!item.getMaterialType().equals("9")&&!item.getMaterialType().equals("11")).collect(Collectors.toList());
				if(tecBomItemSort!=null){
					tecBomItemSort = tecBomItemSort.stream()
							.sorted(Comparator.comparing(TecBomItem::getMaterialCode))
							.collect(Collectors.toList());
				}
				if(tecBomItemSortM!=null){
					tecBomItemSortM = tecBomItemSortM.stream()
							.sorted(Comparator.comparing(TecBomItem::getMaterialCode))
							.collect(Collectors.toList());
				}
				if(tecBomItemSortF!=null){
					tecBomItemSortF = tecBomItemSortF.stream()
							.sorted(Comparator.comparing(TecBomItem::getMaterialCode))
							.collect(Collectors.toList());
				}
				if(tecBomItemSortNoM!=null){
					tecBomItemSortNoM=tecBomItemSortNoM.stream()
							.sorted(Comparator.comparing(TecBomItem::getMaterialCode)).collect(Collectors.toList());
				}
				if(tecBomItemSortNoF!=null){
					tecBomItemSortNoF=tecBomItemSortNoF.stream()
							.sorted(Comparator.comparing(TecBomItem::getMaterialCode)).collect(Collectors.toList());
				}
				if(tecBomItemSortNo!=null){
					tecBomItemSortNo=tecBomItemSortNo.stream()
							.sorted(Comparator.comparing(TecBomItem::getMaterialCode))
							.collect(Collectors.toList());
				}
				//有序
				tecBomItemsSort.addAll(tecBomItemSortM);
				tecBomItemsSort.addAll(tecBomItemSortF);
				tecBomItemsSort.addAll(tecBomItemSort);
				if(CollectionUtil.isNotEmpty(tecBomItemsSort)){
					tecBomItemsSort= tecBomItemsSort.stream().sorted(Comparator.comparing(item -> Double.parseDouble(item.getSort()))).collect(Collectors.toList());
				}
				tecBomItems.addAll(tecBomItemsSort);
				tecBomItems.addAll(tecBomItemSortNoM);
				tecBomItems.addAll(tecBomItemSortNoF);
				tecBomItems.addAll(tecBomItemSortNo);
				bom.setItemList(tecBomItems);
			}
		});
		return bomHead;
	}

	/**
	 * 行号赋值
	 */
	public void  setItemNums(List<TecBomItem> list){
		int size = list.size();
		if(size>0){
			for (int i=1;i<=size;i++){
				list.get(i-1).setItemNum(i);
			}
		}
	}
	/**
	 * 查询物料清单（BOM）主-新
	 *
	 * @param clientId 物料清单（BOM）主ID
	 * @return 物料清单（BOM）主
	 */
	@Override
	public TecBomHead getBom(TecBomHead tecBomHead){
		//主表sku排序
		BasMaterialSku basMaterialSku = new BasMaterialSku();
		basMaterialSku.setMaterialSid(tecBomHead.getMaterialSid());
		List<BasMaterialSku> basMaterialSkuList = basMaterialSkuMapper.selectBasMaterialSkuList(basMaterialSku);
        List<BasMaterialSku> skuList=null;
		if(ConstantsEms.YES.equals(tecBomHead.getIsSkipSatus())){
            //启用且按num升序
            skuList = basMaterialSkuList.stream().filter(li -> li.getSkuType().equals(ConstantsEms.SKUTYP_YS)).
                    sorted(Comparator.comparing(item -> item.getItemNum())).collect(Collectors.toList());
        }else{
            skuList = basMaterialSkuList.stream().filter(li -> li.getStatus().equals(ConstantsEms.ENABLE_STATUS)&&li.getSkuType().equals(ConstantsEms.SKUTYP_YS)).
                    sorted(Comparator.comparing(item -> item.getItemNum())).collect(Collectors.toList());
        }
		List<TecBomHead> bomHead = tecBomHeadMapper.selectTecBomHeadList(tecBomHead);
		ArrayList<TecBomHead> bomHeads = new ArrayList<>();
		skuList.forEach(li->{
			List<TecBomHead> list = bomHead.stream().filter(bom -> bom.getSku1Sid().toString().equals(li.getSkuSid().toString())).collect(Collectors.toList());
			if(CollectionUtil.isNotEmpty(list)){
				bomHeads.add(list.get(0));
			}
		});
		List<BasBomColorRequest> colorSort = new ArrayList<>();
		bomHeads.forEach(li->{
			List<TecBomItem> itemList = tecBomItemMapper.selectBomItemByBomSid(li.getBomSid());
			li.setItemList(itemList);
			BasBomColorRequest color = new BasBomColorRequest();
			color.setSku1Name(li.getSku1Name())
					.setSku1Sid(li.getSku1Sid())
					.setBomSid(li.getBomSid());
			colorSort.add(color);
		});
		TecBomHead head = bomHeads.get(0);
		//主表颜色赋值
		head.setSkuSidList(colorSort);
		List<TecBomItem> itemList = tecBomItemMapper.selectBomItemByBomSid(bomHeads.get(0).getBomSid());
		//颜色行转列
		for (int n=0;n<itemList.size();n++) {
			List<BasBomColorRequest> basBomColorRequests = new ArrayList<>();
			for (int i = 0; i < bomHeads.size(); i++) {
				BasBomColorRequest bomItemSku = new BasBomColorRequest();
				bomItemSku.setBomMaterialSku1Sid(bomHeads.get(i).getItemList().get(n).getBomMaterialSku1Sid())
						.setBomMaterialSku1Name(bomHeads.get(i).getItemList().get(n).getSku1Name())
						.setBomSid(bomHeads.get(i).getItemList().get(n).getBomSid())
						.setBomItemSid(bomHeads.get(i).getItemList().get(n).getBomItemSid());
				basBomColorRequests.add(bomItemSku);
			}
			itemList.get(n).setSkuSidList(basBomColorRequests);
		}
		//损耗零展示转化
		itemList.forEach(item->{
			if(item.getLossRate()!=null){
				item.setLossRate(item.getLossRate().multiply(new BigDecimal(100)));
			}
			if(item.getQuoteLossRate()!=null){
				item.setQuoteLossRate(item.getQuoteLossRate().multiply(new BigDecimal(100)));
			}
			if(item.getConfirmLossRate()!=null){
				item.setConfirmLossRate(item.getConfirmLossRate().multiply(new BigDecimal(100)));
			}
			if(item.getCheckLossRate()!=null){
				item.setCheckLossRate(item.getCheckLossRate().multiply(new BigDecimal(100)));
			}
		});
		if(CollectionUtil.isNotEmpty(itemList)){
			List<TecBomItem> tecBomItems = new ArrayList<>();
			List<TecBomItem> tecBomItemsSort = new ArrayList<>();
			//无序面料
			List<TecBomItem> tecBomItemSortNoM=itemList.stream().filter(item->item.getSort()==null&&item.getMaterialType().equals(ConstantsEms.MATERIAL_M)).collect(Collectors.toList());
			//无序辅料
			List<TecBomItem> tecBomItemSortNoF=itemList.stream().filter(item->item.getSort()==null&&item.getMaterialType().equals(ConstantsEms.MATERIAL_F)).collect(Collectors.toList());;
			//有序其他
			List<TecBomItem> tecBomItemSort = itemList.stream().filter(item->item.getSort()!=null&&!item.getMaterialType().equals(ConstantsEms.MATERIAL_M)&&!item.getMaterialType().equals(ConstantsEms.MATERIAL_F)).collect(Collectors.toList());
			//有序面料
			List<TecBomItem> tecBomItemSortM = itemList.stream().filter(item->item.getSort()!=null&&item.getMaterialType().equals(ConstantsEms.MATERIAL_M)).collect(Collectors.toList());
			//有序辅料
			List<TecBomItem> tecBomItemSortF = itemList.stream().filter(item->item.getSort()!=null&&item.getMaterialType().equals(ConstantsEms.MATERIAL_F)).collect(Collectors.toList());
			//无序其他
			List<TecBomItem>tecBomItemSortNo =  itemList.stream().filter(item->item.getSort()==null&&!item.getMaterialType().equals(ConstantsEms.MATERIAL_M)&&!item.getMaterialType().equals(ConstantsEms.MATERIAL_F)).collect(Collectors.toList());
			if(tecBomItemSort!=null){
				tecBomItemSort = tecBomItemSort.stream()
						.sorted(Comparator.comparing(TecBomItem::getMaterialCode))
						.collect(Collectors.toList());
			}
			if(tecBomItemSortM!=null){
				tecBomItemSortM = tecBomItemSortM.stream()
						.sorted(Comparator.comparing(TecBomItem::getMaterialCode))
						.collect(Collectors.toList());
			}
			if(tecBomItemSortF!=null){
				tecBomItemSortF = tecBomItemSortF.stream()
						.sorted(Comparator.comparing(TecBomItem::getMaterialCode))
						.collect(Collectors.toList());
			}
			if(tecBomItemSortNoM!=null){
				tecBomItemSortNoM=tecBomItemSortNoM.stream()
						.sorted(Comparator.comparing(TecBomItem::getMaterialCode)).collect(Collectors.toList());
			}
			if(tecBomItemSortNoF!=null){
				tecBomItemSortNoF=tecBomItemSortNoF.stream()
						.sorted(Comparator.comparing(TecBomItem::getMaterialCode)).collect(Collectors.toList());
			}
			if(tecBomItemSortNo!=null){
				tecBomItemSortNo=tecBomItemSortNo.stream()
						.sorted(Comparator.comparing(TecBomItem::getMaterialCode))
						.collect(Collectors.toList());
			}
			//有序
			tecBomItemsSort.addAll(tecBomItemSortM);
			tecBomItemsSort.addAll(tecBomItemSortF);
			tecBomItemsSort.addAll(tecBomItemSort);
			if(CollectionUtil.isNotEmpty(tecBomItemsSort)){
				tecBomItemsSort= tecBomItemsSort.stream().sorted(Comparator.comparing(item -> Double.parseDouble(item.getSort()))).collect(Collectors.toList());
			}
			tecBomItems.addAll(tecBomItemsSort);
			tecBomItems.addAll(tecBomItemSortNoM);
			tecBomItems.addAll(tecBomItemSortNoF);
			tecBomItems.addAll(tecBomItemSortNo);
			head.setItemList(tecBomItems);
		}
		//附件
		List<TecBomAttachment> tecBomAttachments = tecBomAttachmentMapper.selectTecBomAttachmentList(new TecBomAttachment().setBomSid(head.getBomSid()));
		//物料&商品-附件对象
		BasMaterialAttachment basMaterialAttachment = new BasMaterialAttachment();
		basMaterialAttachment.setMaterialSid(head.getMaterialSid());
		List<BasMaterialAttachment> basMaterialAttachmentList = basMaterialAttachmentMapper.selectBasMaterialAttachmentList(basMaterialAttachment);
		head.setAttachmentList(tecBomAttachments);
		head.setAttachmentMaterialList(basMaterialAttachmentList);
		MongodbUtil.find(head);
		return head;
	}

	/**
	 * 行号赋值
	 */
	public void  setItemNum(List<TecBomItem> list){
		int size = list.size();
		if(size>0){
			for (int i=1;i<=size;i++){
				list.get(i-1).setSort(String.valueOf(i));
			}
		}
	}
	/**
	 * bom 复制
	 */
    @Override
	public List<TecBomItem>  getBomItemM(TecBomHead request){
		TecBomHead head = tecBomHeadMapper.selectOne(new QueryWrapper<TecBomHead>().lambda()
				.eq(TecBomHead::getMaterialSid, request.getMaterialSid())
				.eq(TecBomHead::getSku1Sid, request.getSku1Sid())
		);
		if(head==null){
			throw new CustomException("没有对应的bom信息");
		}else{
			List<TecBomItem> tecBomItems = tecBomItemMapper.selectBomItemByBomSid(head.getBomSid());
			if(CollectionUtil.isEmpty(tecBomItems)){
				throw new CustomException("未查找到商品编码"+request.getMaterialCode()+"，颜色"+request.getSku1Name()+"的物料清单，请检查！");
			}else{
				tecBomItems.forEach(item->{
					if(item.getLossRate()!=null){
						item.setLossRate(item.getLossRate().multiply(new BigDecimal(100)));
					}
					if(item.getQuoteLossRate()!=null){
						item.setQuoteLossRate(item.getQuoteLossRate().multiply(new BigDecimal(100)));
					}
					if(item.getConfirmLossRate()!=null){
						item.setConfirmLossRate(item.getConfirmLossRate().multiply(new BigDecimal(100)));
					}
					if(item.getCheckLossRate()!=null){
						item.setCheckLossRate(item.getCheckLossRate().multiply(new BigDecimal(100)));
					}
					item.setBomSid(null);
					item.setBomItemSid(null);
					item.setBomMaterialSku1Sid(null);
					item.setItemNum(null);
				});
				return tecBomItems;
			}
		}
	}
	@Override
	public void export(HttpServletResponse response, TecBomHead tecBomHead)throws IOException{
		Long[] materialSids = tecBomHead.getMaterialSids();
//		List<File>  fileList=new ArrayList<File>();
			//response 输出流
			ServletOutputStream out = response.getOutputStream();
			//压缩输出流---将response输出流填入压缩输出流
			ZipOutputStream zipOutputStream = new ZipOutputStream(out);
			try{
				for (int m=0;m< materialSids.length ;m++) {
					TecBomHead head = new TecBomHead();
					head.setMaterialSid(materialSids[m]);
					TecBomHead tecBom = getBom(head);
					List<BasBomColorRequest> skuList = tecBom.getSkuSidList();
					List<TecBomItem> itemList = tecBom.getItemList();
					//是否
					List<DictData> isType=sysDictDataService.selectDictData("sys_yes_no");
					Map<String,String> isTypeMaps=isType.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
					//取整方式
					List<DictData> roundType=sysDictDataService.selectDictData("s_rounding_type");
					Map<String,String> roundTypeMaps=roundType.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel,(key1, key2)->key2));
					//所属生产环节
					List<DictData> touse=sysDictDataService.selectDictData("s_touse_produce_stage");
					Map<String,String> touseMaps=touse.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel,(key1, key2)->key2));
					HSSFWorkbook workbook = new HSSFWorkbook();
					Sheet sheet = workbook.createSheet("bom明细");
					sheet.setDefaultColumnWidth(18);
					String[] titles={"商品编码(款号)",
							"商品名称",
							"我司样衣号",
							"产品季",
							"设计师",
							"客户",
							"客方商品编码(款号)"};
					//第一行数据
					Row rowBOMHead = sheet.createRow(0);
					//第一行样式
					CellStyle cellStyle= ExcelStyleUtil.getStyleX(workbook);
					//第一行数据
					ExcelStyleUtil.setCellStyleLime(cellStyle);
					ExcelStyleUtil.setBorderStyle(cellStyle);
					for (int i=0;i<titles.length;i++) {
						Cell cell = rowBOMHead.createCell(i);
						cell.setCellValue(titles[i]);
						cell.setCellStyle(cellStyle);
					}
					CellStyle defaultCellStyle = ExcelStyleUtil.getDefaultCellStyleX(workbook);
					//第二行数据
					Row rowBOM = sheet.createRow(1);
					//商品款号
					Cell cell0 = rowBOM.createCell(0);
					cell0.setCellValue(tecBom.getMaterialCode());
					cell0.setCellStyle(defaultCellStyle);
					//商品名称
					Cell cell1 = rowBOM.createCell(1);
					cell1.setCellValue(tecBom.getMaterialName());
					cell1.setCellStyle(defaultCellStyle);
					//我司样衣号
					Cell cell2 = rowBOM.createCell(2);
					cell2.setCellValue(tecBom.getSampleCodeSelf());
					cell2.setCellStyle(defaultCellStyle);
					//产品季
					Cell cell3 = rowBOM.createCell(3);
					cell3.setCellValue(tecBom.getProductSeasonName());
					cell3.setCellStyle(defaultCellStyle);
					//设计师
					Cell cell4 = rowBOM.createCell(4);
					cell4.setCellValue(tecBom.getDesignerAccountName());
					cell4.setCellStyle(defaultCellStyle);
					//客户
					Cell cell5 = rowBOM.createCell(5);
					cell5.setCellValue(tecBom.getCustomerName());
					cell5.setCellStyle(defaultCellStyle);
					//客方商品编码
					Cell cell6 = rowBOM.createCell(6);
					cell6.setCellValue(tecBom.getCustomerProductCode());
					cell6.setCellStyle(defaultCellStyle);

					//第三行数据
					Row rowBomItm = sheet.createRow(2);
					String[] titleItem={"序号","物料编码",	"物料名称","供方编码","采购类型","物料类型","主面料",
							"部位","用量","损耗率(%)","损耗取整方式","基本计量单位","BOM用量单位","计价量","计价单位"  ,
							"所用生产环节",
							"物料分类",
							"供应商",
							"幅宽(厘米)",
							"克重",
							"纱支",
							"密度",
							"成分",
							"规格",
							"材质",
							"启停",
							"备注",
							"创建人",
							"创建日期",
							"行号"
					};
					for (int i=0;i<7;i++) {
						Cell cell = rowBomItm.createCell(i);
						cell.setCellValue(titleItem[i]);
						cell.setCellStyle(cellStyle);
					}
					//颜色显示动态
					for (int i=7;i<skuList.size()+7;i++) {
						Cell cell = rowBomItm.createCell(i);
						cell.setCellValue(skuList.get(i-7).getSku1Name());
						cell.setCellStyle(cellStyle);
					}
					int num=7+skuList.size();
					//颜色后面字段
					for (int i=num;i<titleItem.length+skuList.size();i++) {
						Cell cell = rowBomItm.createCell(i);
						cell.setCellValue(titleItem[i-skuList.size()]);
						cell.setCellStyle(cellStyle);
					}
					//数据部分
					for (int i=3;i<itemList.size()+3;i++) {
						Row row = sheet.createRow(i);
						//序号
						Cell cell01 = row.createCell(0);
						cell01.setCellValue(itemList.get(i-3).getSort());
						cell01.setCellStyle(defaultCellStyle);
						//物料编码
						Cell cell02 = row.createCell(1);
						cell02.setCellValue(itemList.get(i-3).getMaterialCode());
						cell02.setCellStyle(defaultCellStyle);
						//物料名称
						Cell cell03 = row.createCell(2);
						cell03.setCellValue(itemList.get(i-3).getMaterialName());
						cell03.setCellStyle(defaultCellStyle);
						//供方编码
						Cell cell03Add1 = row.createCell(3);
						cell03Add1.setCellValue(itemList.get(i-3).getSupplierProductCode());
						cell03Add1.setCellStyle(defaultCellStyle);
						//采购类型
						Cell cell03Add2 = row.createCell(4);
						cell03Add2.setCellValue(itemList.get(i-3).getPurchaseTypeName());
						cell03Add2.setCellStyle(defaultCellStyle);
						//物料类型
						Cell cell03Add3 = row.createCell(5);
						cell03Add3.setCellValue(itemList.get(i-3).getMaterialTypeName());
						cell03Add3.setCellStyle(defaultCellStyle);
						//主面料
						Cell cell04 = row.createCell(6);
						String isM=null;
						if(itemList.get(i - 3).getIsMainFabric()!=null){
							if(itemList.get(i - 3).getIsMainFabric().equals("1")){
								isM = "是";
							}
						}
						cell04.setCellValue(isM);
						cell04.setCellStyle(defaultCellStyle);
						for (int n=1;n<skuList.size()+1;n++) {
							Cell cell = row.createCell(6+n);
							cell.setCellValue(itemList.get(i-3).getSkuSidList().get(n-1).getBomMaterialSku1Name());
							cell.setCellStyle(defaultCellStyle);
						}
						int skuStart=skuList.size()+6;
						//部位
						Cell cell05 = row.createCell(skuStart+1);
						cell05.setCellValue(itemList.get(i-3).getPositionName());
						cell05.setCellStyle(defaultCellStyle);
						//用量
						Cell cell06 = row.createCell(skuStart+2);
						cell06.setCellValue(itemList.get(i-3).getQuantity()==null?null:removeZero(itemList.get(i-3).getQuantity().toString()));
						cell06.setCellStyle(defaultCellStyle);
						//损耗率(%)
						Cell cell07 = row.createCell(skuStart+3);
						cell07.setCellValue(itemList.get(i-3).getLossRate()==null?null:removeZero(itemList.get(i-3).getLossRate().toString()));
						cell07.setCellStyle(defaultCellStyle);
						//损耗取整方式
						Cell cell12 = row.createCell(skuStart+4);
						String round=null;
						if(itemList.get(i-3).getRoundingType()!=null){
							round=roundTypeMaps.get(itemList.get(i-3).getRoundingType());
						}
						cell12.setCellValue(round);
						cell12.setCellStyle(defaultCellStyle);
						//基本计量单位
						Cell cell13 = row.createCell(skuStart+5);
						cell13.setCellValue(itemList.get(i-3).getUnitBaseName());
						cell13.setCellStyle(defaultCellStyle);
						//BOM用量单位
						Cell cell14 = row.createCell(skuStart+6);
						cell14.setCellValue(itemList.get(i-3).getUnitQuantityName());
						cell14.setCellStyle(defaultCellStyle);
						//计价量
						Cell cell15 = row.createCell(skuStart+7);
						cell15.setCellValue(itemList.get(i-3).getPriceQuantity()==null?null:removeZero(itemList.get(i-3).getPriceQuantity().toString()));
						cell15.setCellStyle(defaultCellStyle);
						//计价单位
						Cell cell16 = row.createCell(skuStart+8);
						cell16.setCellValue(itemList.get(i-3).getUnitPriceName());
						cell16.setCellStyle(defaultCellStyle);
						//所用生产环节
						Cell cell27 = row.createCell(skuStart+9);
						cell27.setCellValue(itemList.get(i-3).getTouseProduceStage()==null?null:touseMaps.get(itemList.get(i-3).getTouseProduceStage()));
						cell27.setCellStyle(defaultCellStyle);
                       //物料分类
						Cell cell28 = row.createCell(skuStart+10);
						cell28.setCellValue(itemList.get(i-3).getNodeName());
						cell28.setCellStyle(defaultCellStyle);
						//供应商
						Cell cell18 = row.createCell(skuStart+11);
						cell18.setCellValue(itemList.get(i-3).getVendorName());
						cell18.setCellStyle(defaultCellStyle);
						//幅宽(厘米)
						Cell cell19 = row.createCell(skuStart+12);
						cell19.setCellValue(itemList.get(i-3).getWidth());
						cell19.setCellStyle(defaultCellStyle);
						//克重
						Cell cell20 = row.createCell(skuStart+13);
						cell20.setCellValue(itemList.get(i-3).getGramWeight());
						cell20.setCellStyle(defaultCellStyle);
						//纱支
						Cell cell21 = row.createCell(skuStart+14);
						cell21.setCellValue(itemList.get(i-3).getYarnCount());
						cell21.setCellStyle(defaultCellStyle);
						//密度
						Cell cell22 = row.createCell(skuStart+15);
						cell22.setCellValue(itemList.get(i-3).getDensity());
						cell22.setCellStyle(defaultCellStyle);
						//成分
						Cell cell23 = row.createCell(skuStart+16);
						cell23.setCellValue(itemList.get(i-3).getComposition());
						cell23.setCellStyle(defaultCellStyle);
						//规格
						Cell cell24 = row.createCell(skuStart+17);
						cell24.setCellValue(itemList.get(i-3).getSpecificationSize());
						cell24.setCellStyle(defaultCellStyle);
						//材质
						Cell cell25 = row.createCell(skuStart+18);
						cell25.setCellValue(itemList.get(i-3).getMaterialComposition());
						cell25.setCellStyle(defaultCellStyle);
						//启停
						Cell cell26 = row.createCell(skuStart+19);
						cell26.setCellValue(itemList.get(i-3).getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用");
						cell26.setCellStyle(defaultCellStyle);
						//备注
						Cell cell29 = row.createCell(skuStart+20);
						cell29.setCellValue(itemList.get(i-3).getRemark());
						cell29.setCellStyle(defaultCellStyle);
						//创建人
						Cell cell30 = row.createCell(skuStart+21);
						cell30.setCellValue(itemList.get(i-3).getCreatorAccountName());
						cell30.setCellStyle(defaultCellStyle);
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						//创建日期
						Cell cell31 = row.createCell(skuStart+22);
						cell31.setCellValue(itemList.get(i-3).getCreateDate()==null?null:sdf.format(itemList.get(i-3).getCreateDate()));
						cell31.setCellStyle(defaultCellStyle);
//创建日期
						Cell cell32 = row.createCell(skuStart+23);
						cell32.setCellValue(itemList.get(i-3).getItemNum()==null?null:itemList.get(i-3).getItemNum());
						cell32.setCellStyle(defaultCellStyle);
//						BufferedImage bufferImg = null;
//						//先把读进来的图片放到一个ByteArrayOutputStream中，以便产生ByteArray
//						ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
//						String url = itemList.get(i - 3).getPicturePath();

//						if(url!=null) {
//							try {
//								URL urls = new URL(url);
//								//String fileName=url.substring(21);
//								//String basepath= URLDecoder.decode(url,"utf-8");
//								// test="\\minio\\platform\\2021\\10\\20\\b04b6759-0afd-403e-9713-019ceb42ffcf.jpg";
//								bufferImg = ImageIO.read(urls);
//								try {
//									ImageIO.write(bufferImg, "jpg", byteArrayOut);
//								} catch (IOException es) {
//									es.printStackTrace();
//								}
//								Drawing<?> patriarch = sheet.createDrawingPatriarch();
//								//画图的顶级管理器，一个sheet只能获取一个（一定要注意这点）
//								//anchor主要用于设置图片的属性
//								HSSFClientAnchor anchor =
//										new HSSFClientAnchor
//												(0, //x缩放
//														0, // y缩放
//														255, //最大1023
//														255, //最大255
//														(short) 4,  //宽度占几格 0开始
//														i, //在第几行
//														(short) 4, //宽度占几格 0开始
//														skuStart + 28 //第几列
//												);
//								//插入图片
//								patriarch.createPicture(anchor, workbook.addPicture(byteArrayOut.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG));
//							} catch (IOException ex) {
//								ex.printStackTrace();
//							}
//						}



					}
					String msg=tecBom.getMaterialName()+tecBom.getMaterialCode();
//					File file = getEmptyExcelFile(workbook,msg);
//					fileList.add(file);
					response.setContentType("application/octet-stream; charset=utf-8");
					response.setHeader("Content-Disposition", "attachment; filename=test.zip");
					//重点开始,创建压缩文件,并进行打包
					String name=tecBom.getMaterialCode()+"_BOM明细导出_"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss");
					ZipEntry z = new ZipEntry(name + ".xls");
					zipOutputStream.putNextEntry(z);
					//写入一个压缩文件---最后写文件
					workbook.write(zipOutputStream);
				}
				zipOutputStream.flush();
			}catch (IOException e){
				e.printStackTrace();
			}finally {
				//注意关闭顺序，否则可能文件错误，先开后关
				if (zipOutputStream != null) {
					zipOutputStream.close();
				}
				if (out != null) {
					out.close();
				}
			}
	}
	/**
	 * 组合拉链物料 插入
	 */
	@Override
	public int insertZipper(TecBomHead tecBomHead){
		int row = 1;
		Long sid = tecBomHead.getBomSid();
		List<TecBomItem> itemList = tecBomHead.getItemList();
		if(CollectionUtil.isEmpty(itemList) && sid != null){
			List<Long> sidList = new ArrayList<>();
			sidList.add(tecBomHead.getBomSid());
			tecBomHeadMapper.deleteBatchIds(sidList);
			row = tecBomItemMapper.delete(new QueryWrapper<TecBomItem>().lambda()
					.in(TecBomItem::getBomSid,sidList));
			return row;
		}
		if(CollectionUtil.isNotEmpty(itemList)){
			int size = itemList.size();
			itemList = itemList.stream().filter(o->o.getBomMaterialSku1Sid() != null).collect(Collectors.toList());
			if (itemList.size() != size) {
				throw new BaseException("配件颜色不能为空");
			}
			itemList = itemList.stream().filter(o->o.getQuantity() != null).collect(Collectors.toList());
			if (itemList.size() != size) {
				throw new BaseException("数量不能为空");
			}
		}
		if(sid!=null){
			List<Long> sidList = tecBomHeadMapper.selectList(new QueryWrapper<TecBomHead>().lambda()
					.eq(TecBomHead::getMaterialSid, tecBomHead.getMaterialSid())).stream().map(li -> li.getBomSid()).collect(Collectors.toList());
			tecBomHeadMapper.deleteBatchIds(sidList);
			tecBomItemMapper.delete(new QueryWrapper<TecBomItem>().lambda()
					.in(TecBomItem::getBomSid,sidList));
		}
		if(CollectionUtil.isNotEmpty(itemList)){
			row = tecBomHeadMapper.insert(tecBomHead);
		}
		if(row>0){
			MongodbUtil.insertUserLog(tecBomHead.getBomSid(), BusinessType.INSERT.getValue(),TITLE);
			if(CollectionUtil.isNotEmpty(itemList)){
				Long bomSid = tecBomHead.getBomSid();
				itemList.forEach(li->{
					li.setBomSid(bomSid);
					tecBomItemMapper.insert(li);
				});
			}
		}
		return row;
	}
	/**
	 * 组合拉链物料 详情
	 */
	@Override
	public TecBomHead getZipper(Long materialSid){
		TecBomHead bom = tecBomHeadMapper.selectOne(new QueryWrapper<TecBomHead>().lambda()
				.eq(TecBomHead::getMaterialSid, materialSid));
		if(bom!=null){
			List<TecBomItem> tecBomItems = tecBomItemMapper.selectBomItemByBomSid(bom.getBomSid());
			bom.setItemList(tecBomItems);
		}else{
			bom= new TecBomHead();
		}
		return bom;
	}

	/**
	 * 组合拉链物料 修改
	 */
	@Override
	public int editZipper(TecBomHead tecBomHead){
		int row=0;
		List<TecBomItem> itemList = tecBomHead.getItemList();
		Long bomSid = tecBomHead.getBomSid();
		if(bomSid!=null){
			tecBomItemMapper.delete(new QueryWrapper<TecBomItem>().lambda()
					.eq(TecBomItem::getBomSid,bomSid));
			tecBomHeadMapper.insert(tecBomHead);
		}
		if(itemList.size()>0){
			itemList.forEach(li->{
				li.setBomSid(bomSid);
				tecBomItemMapper.insert(li);
			});
		}else{
			row=tecBomHeadMapper.deleteById(bomSid);
		}
		return row;
	}



	/**
	 * 查询物料清单（BOM）主列表
	 *
	 * @param tecBomHead 物料清单（BOM）主
	 * @return 物料清单（BOM）主
	 */
	@Override
	public List<TecBomHead> selectTecBomHeadList(TecBomHead tecBomHead) {
		List<TecBomHead> tecBomHeads = tecBomHeadMapper.selectList(new QueryWrapper<TecBomHead>().lambda()
				.eq(TecBomHead::getMaterialSid, tecBomHead.getMaterialSid())
		);
		return tecBomHeads;
	}

	/**
	 * 查询物料清单（BOM）主列表
	 *
	 * @param tecBomHead 物料清单（BOM）主
	 * @return 物料清单（BOM）主
	 */
	@Override
	public List<TecBomHead> selectTecBomHeadListNew(TecBomHead tecBomHead) {
		List<TecBomHead> tecBomHeads = tecBomHeadMapper.selectTecBomHeadNewList(tecBomHead);
		return tecBomHeads;
	}


	/**
	 * bom报表
	 *
	 */
	@Override
	public List<TecBomHeadReportResponse> report(TecBomHeadReportRequest tecBomHead) {
		List<TecBomHeadReportResponse> list = tecBomItemMapper.report(tecBomHead);
        list.forEach(li->{
            if(li.getLossRate()!=null){
                li.setLossRate(li.getLossRate().multiply(new BigDecimal(100)));
            }
            if(li.getQuoteLossRate()!=null){
                li.setQuoteLossRate(li.getQuoteLossRate().multiply(new BigDecimal(100)));
            }
        });
		return list;
	}

	/**
	 * 更改采购类型
	 *
	 */
	@Override
	public int changePurchaseType(TecBomHeadReportPurchaseRequest request){
		List<TecBomHeadReportSidRequest> sidList = request.getSidList();
		sidList.stream().forEach(item->{
			TecBomHead tecBomHead = tecBomHeadMapper.selectById(item.getBomSid());
			List<TecBomHead> tecBomHeads = tecBomHeadMapper.selectList(new QueryWrapper<TecBomHead>().lambda()
					.eq(TecBomHead::getMaterialSid, tecBomHead.getMaterialSid())
			);
			tecBomHeads.forEach(bom->{
				tecBomItemMapper.update(new TecBomItem(),new UpdateWrapper<TecBomItem>().lambda()
						.eq(TecBomItem::getBomSid,bom.getBomSid())
						.eq(TecBomItem::getBomMaterialSid,item.getBomMaterialSid())
						.set(TecBomItem::getPurchaseType,request.getPurchaseType())
				);
			});
		});
		return 1;
	}

	/**
	 * BOM物料报表
	 *
	 */
	@Override
	public List<TecBomHeadMaterialReportResponse> reportMaterial(TecBomHeadReportRequest tecBomHead) {
		List<TecBomHeadMaterialReportResponse> list = tecBomItemMapper.reportMaterial(tecBomHead);
		return list;
	}
	/**
	 * BOM序号排序 刷新序号
	 *
	 */
	@Override
	public List<TecBomItem> sortItem(List<TecBomItem> itemList){
		List<TecBomItem> All = new ArrayList<>();
		List<TecBomItem> notNullList = itemList.stream().filter(li -> li.getSort() != null).collect(Collectors.toList());
		notNullList=notNullList.stream().sorted(Comparator.comparing(item->Double.parseDouble(item.getSort())))
				.collect(Collectors.toList());
		List<TecBomItem> nullList = itemList.stream().filter(li -> li.getSort() == null).collect(Collectors.toList());
		All.addAll(notNullList);
		All.addAll(nullList);
		return All;
	}

	/**
	 * BOM序号排序 刷新序号 新建
	 *
	 */
	@Override
	public List<BomSortResponse> sortItemAdd(List<BomSortResponse> itemList){
		List<BomSortResponse> All = new ArrayList<>();
		List<BomSortResponse> notNullList = itemList.stream().filter(li -> li.getSort() != null).collect(Collectors.toList());
		notNullList=notNullList.stream().sorted(Comparator.comparing(item->Double.parseDouble(item.getSort())))
				.collect(Collectors.toList());
		List<BomSortResponse> nullList = itemList.stream().filter(li -> li.getSort() == null).collect(Collectors.toList());
		All.addAll(notNullList);
		All.addAll(nullList);
		return All;
	}

	@Override
	public void setIsM(List<TecBomHeadReportResponse> list){
		if(CollectionUtil.isNotEmpty(list)){
			list.forEach(item->{
				if(ConstantsEms.ENABLE_STATUS.equals(item.getIsMainFabric())){
					item.setIsMainFabric("是");
				}
			});
		}
	}

    @Override
    public List<Long>  test(List<Long> sids){
		List<Long> materialSidList = new ArrayList<>();
		if(CollectionUtil.isEmpty(sids)){
			List<TecBomHead> tecBomHeads = tecBomHeadMapper.selectList(new QueryWrapper<TecBomHead>().lambda()
					.isNotNull(TecBomHead::getSku1Sid)
			);
			Set<Long> longs = tecBomHeads.stream().map(li -> li.getMaterialSid()).collect(Collectors.toSet());
			longs.forEach(id->{
				try{
					TecBomHead tecBomHead = new TecBomHead();
					tecBomHead.setMaterialSid(id);
                    tecBomHead.setIsSkipSatus(ConstantsEms.YES);
					TecBomHead bom = getBom(tecBomHead);
					List<TecBomItem> itemList = bom.getItemList();
					for (int i = 0; i < itemList.size(); i++) {
						int num=i+1;
						List<BasBomColorRequest> skuSidList = itemList.get(i).getSkuSidList();
						skuSidList.forEach(item->{
							TecBomItem tecBomItem = new TecBomItem();
							tecBomItem.setBomItemSid(item.getBomItemSid())
									.setItemNum(num);
							tecBomItemMapper.updateById(tecBomItem);
						});
					}
				}catch (Exception e){
					materialSidList.add(id);
				}
			});
		}else{
			sids.forEach(id->{
				TecBomHead tecBomHead = new TecBomHead();
				tecBomHead.setMaterialSid(id);
                tecBomHead.setIsSkipSatus(ConstantsEms.YES);
				TecBomHead bom = getBom(tecBomHead);
				List<TecBomItem> itemList = bom.getItemList();
				for (int i = 0; i < itemList.size(); i++) {
					int num=i+1;
					List<BasBomColorRequest> skuSidList = itemList.get(i).getSkuSidList();
					skuSidList.forEach(item->{
						TecBomItem tecBomItem = new TecBomItem();
						tecBomItem.setBomItemSid(item.getBomItemSid())
								.setItemNum(num);
						tecBomItemMapper.updateById(tecBomItem);
					});
				}
			});
		}
		return materialSidList;
    }

	/**
	 * 新增物料清单（BOM）主
	 * 需要注意编码重复校验
	 * @param tecBomHead 物料清单（BOM）主
	 * @return 结果
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int insertTecBomHead(List<TecBomHead> bomList) {
		RLock lock = redissonClient.getLock(LOCK_KEY);
		lock.lock(10L, TimeUnit.SECONDS);
		try{
			List<Integer> ints = new ArrayList<>();
			TecBomHead tecBomHead = bomList.get(0);
			Long materialSid = tecBomHead.getMaterialSid();
			List<TecBomHead> tecBomHeads = tecBomHeadMapper.selectList(new QueryWrapper<TecBomHead>().lambda()
					.eq(TecBomHead::getMaterialSid, materialSid)
			);
			if(CollectionUtil.isNotEmpty(tecBomHeads)){
				throw new CustomException("已存在该商品的bom，不允许重复新建");
			}else{
				//明细行颜色数量校验
				List<TecBomItem> tecBomItems = bomList.get(0).getItemList();
//				if(CollectionUtil.isNotEmpty(tecBomItems)){
//					tecBomItems.forEach(m->{
//						if(m.getPriceQuantity()!=null&&m.getUnitRecursion()==null){
//							throw new CustomException("”计价量、递增减价单位”必须同时有值");
//						}
//						if(m.getUnitRecursion()!=null&&m.getPriceQuantity()==null){
//							throw new CustomException("“计价量、递增减价单位”必须同时有值");
//						}
//					});
//				}
				for(TecBomHead bom : bomList) {
					Long bomSid = IdWorker.getId();
					bom.setBomSid(bomSid);
					if(ConstantsEms.CHECK_STATUS.equals(bom.getHandleStatus())){
						bom.setConfirmDate(new Date());
						bom.setConfirmerAccount(SecurityUtils.getUsername());
					}
					bom.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
					int row = tecBomHeadMapper.insert(bom);
					if(row>0){
						LambdaUpdateWrapper<BasMaterial> updateWrapper = new LambdaUpdateWrapper<>();
						updateWrapper.eq(BasMaterial::getMaterialSid,bom.getMaterialSid()).set(BasMaterial::getIsHasCreatedBom, ConstantsEms.YES);
						int i = basMaterialMapper.update(new BasMaterial(), updateWrapper);
						//插入日志
						List<OperMsg> msgList=new ArrayList<>();
						MongodbUtil.insertUserLog(bom.getBomSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
					}
					if(row>0){
						List<TecBomAttachment> attachmentList =  bom.getAttachmentList();
						if(!CollectionUtils.isEmpty(attachmentList)){
							for (TecBomAttachment attachment : attachmentList){
								attachment.setBomSid(bomSid);
								attachment.setBomAttachmentSid(IdWorker.getId());
								attachment.setClientId(SecurityUtils.getClientId());
								tecBomAttachmentMapper.insert(attachment);
							}
						}
						List<TecBomItem> itemList = bom.getItemList();
						if(!CollectionUtils.isEmpty(itemList)){
							setItemNums(itemList);
							for (TecBomItem item : itemList){
								if(ConstantsEms.CHECK_STATUS.equals(bom.getHandleStatus())){
									//组合拉链 sku 回写
									insertZipperSku(item);
								}
								item.setBomSid(bomSid);
								if(item.getLossRate()!=null){
									item.setLossRate(item.getLossRate().divide(new BigDecimal(100)));
								}
								if(item.getQuoteLossRate()!=null){
									item.setQuoteLossRate(item.getQuoteLossRate().divide(new BigDecimal(100)));
								}
								if(item.getConfirmLossRate()!=null){
									item.setConfirmLossRate(item.getConfirmLossRate().divide(new BigDecimal(100)));
								}
								if(item.getCheckLossRate()!=null){
									item.setCheckLossRate(item.getCheckLossRate().divide(new BigDecimal(100)));
								}
								tecBomItemMapper.insert(item);
							}
						}
					}
				}
				TecBomHead head = bomList.get(0);
				//待办通知
				TecBomHead o = tecBomHeadMapper.selectById(head.getBomSid());
				SysTodoTask sysTodoTask = new SysTodoTask();
				if (ConstantsEms.SAVA_STATUS.equals(o.getHandleStatus())) {
					sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
							.setTableName(TABLE)
							.setDocumentSid(o.getBomSid());
					String msg=tecBomHead.getMaterialCode()!=null?"BOM款编码为" + tecBomHead.getMaterialCode() + "当前是保存状态，请及时处理！":"BOM款样衣号为" + tecBomHead.getSampleCodeSelf() + "当前是保存状态，请及时处理！";
					List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
					if (CollectionUtil.isEmpty(sysTodoTaskList)) {
						sysTodoTask.setTitle(msg)
								.setDocumentCode(tecBomHead.getMaterialCode())
								.setNoticeDate(new Date())
								.setUserId(ApiThreadLocalUtil.get().getUserid());
						sysTodoTaskMapper.insert(sysTodoTask);
					}
				} else {
					//校验是否存在待办
					checkTodoExist(head);
				}
			}
		}catch (CustomException e){
			throw new CustomException(e.getMessage());
		}finally {
			lock.unlock();
		}
		return 1;
	}

	/**
	 * 校验是否存在待办
	 */
	private void checkTodoExist(TecBomHead head) {
		List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
				.eq(SysTodoTask::getDocumentSid, head.getBomSid()));
		if (CollectionUtil.isNotEmpty(todoTaskList)) {
			sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
					.eq(SysTodoTask::getDocumentSid, head.getBomSid()));
		}
	}

	//组合拉链协议 颜色回写
	public void insertZipperSku(TecBomItem item){
		String zipperFlag = item.getZipperFlag();
		if(ConstantsEms.ZIPPER_ZH.equals(zipperFlag)||ConstantsEms.ZIPPER_ZT.equals(zipperFlag)){
			if(item.getBomMaterialSku1Sid()!=null){
				List<BasMaterialSku> basMaterialSkus = basMaterialSkuMapper.selectList(new QueryWrapper<BasMaterialSku>().lambda()
						.eq(BasMaterialSku::getMaterialSid, item.getBomMaterialSid())
						.eq(BasMaterialSku::getSkuType,ConstantsEms.SKUTYP_YS)
				);
				boolean exit = basMaterialSkus.stream().anyMatch(li -> li.getSkuSid().equals(item.getBomMaterialSku1Sid()));
				if(!exit){
					BasMaterialSku basMaterialSku = new BasMaterialSku();
					basMaterialSku.setHandleStatus(ConstantsEms.CHECK_STATUS)
							.setStatus(ConstantsEms.SAVA_STATUS)
							.setSkuSid(item.getBomMaterialSku1Sid())
							.setSkuType(ConstantsEms.SKUTYP_YS)
							.setMaterialSid(item.getBomMaterialSid());
					BasMaterial basMaterial = basMaterialServiceImpl.selectBasMaterialById(item.getBomMaterialSid());
					List<BasMaterialSku> basMaterialSkuList = basMaterial.getBasMaterialSkuList();
					basMaterialSkuList.add(basMaterialSku);
					basMaterial.setBasMaterialSkuList(basMaterialSkuList);
					//新增sku1 sku2 商品条码 刷新
					basMaterialServiceImpl.change(basMaterial);
				}
			}
		}
	}

	/**
	 * 修改物料清单（BOM）主
	 *
	 * @param tecBomHead 物料清单（BOM）主
	 * @return 结果
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateTecBomHead(List<TecBomHead> bomList) {
		return 1;
	}
	/**
	 * 修改物料清单（BOM）主-新
	 *
	 * @param clientIds 需要删除的物料清单（BOM）主ID
	 * @return 结果
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int editTecBomHead(TecBomHead bomHead) {
		Long mongDbsid=bomHead.getBomSid();
		//主表颜色信息
		List<BasBomColorRequest> skuSidList = bomHead.getSkuSidList();
		List<TecBomItem> itemList = bomHead.getItemList();
//		if(CollectionUtil.isNotEmpty(itemList)){
//			itemList.forEach(m->{
//				if(m.getPriceQuantity()!=null&&m.getUnitRecursion()==null){
//					throw new CustomException("”计价量、递增减价单位”必须同时有值");
//				}
//				if(m.getUnitRecursion()!=null&&m.getPriceQuantity()==null){
//					throw new CustomException("“计价量、递增减价单位”必须同时有值");
//				}
//			});
//		}
		if(!ConstantsEms.SAVA_STATUS.equals(bomHead.getHandleStatus())){
			//校验
			judgeBom(bomHead,null);
		}
		skuSidList.forEach(bom->{
			tecBomHeadMapper.deleteById(bom.getBomSid());
			bomHead.setBomSid(bom.getBomSid());
			bomHead.setSku1Sid(bom.getSku1Sid());
			tecBomHeadMapper.insert(bomHead);
			if(!ConstantsEms.SAVA_STATUS.equals(bomHead.getHandleStatus())){
				//校验是否存在待办
				checkTodoExist(bomHead);
			}
		});
		//删除原有的明细
		List<Long> longs = itemList.get(0).getSkuSidList().stream().map(o -> o.getBomSid()).collect(Collectors.toList());
		//行号赋值
		setItemNums(itemList);
		tecBomItemMapper.delete(new QueryWrapper<TecBomItem>().lambda()
				.in(TecBomItem::getBomSid,longs));
		//单位会写
//		itemList.stream().forEach(li->{
//			BasMaterial material = new BasMaterial();
//			material.setMaterialSid(li.getBomMaterialSid());
////			material.setUnitRecursion(li.getUnitRecursion());
//			material.setis
//			basMaterialMapper.updateById(material);
//		});
		//新建当前明细
		itemList.stream().forEach(item->{
			List<BasBomColorRequest> iteBomList = item.getSkuSidList();
			List<TecBomItem> tecBomItems = new ArrayList<>();
			iteBomList.stream().forEach(li->{
				item.setBomSid(li.getBomSid())
						.setBomItemSid(null)
						.setBomMaterialSku1Sid(li.getBomMaterialSku1Sid());
				TecBomItem tecBomItem = new TecBomItem();
				if(ConstantsEms.CHECK_STATUS.equals(bomHead.getHandleStatus())){
					tecBomItems.forEach(o->{
						//组合拉链 sku 回写
						insertZipperSku(item);
					});
				}
				BeanCopyUtils.copyProperties(item,tecBomItem);
				tecBomItems.add(tecBomItem);
			});
			tecBomItems.forEach(li->{
				if(li.getLossRate()!=null){
					li.setLossRate(li.getLossRate().divide(new BigDecimal(100)));
				}
				if(li.getQuoteLossRate()!=null){
					li.setQuoteLossRate(li.getQuoteLossRate().divide(new BigDecimal(100)));
				}
				if(li.getConfirmLossRate()!=null){
					li.setConfirmLossRate(li.getConfirmLossRate().divide(new BigDecimal(100)));
				}
				if(li.getCheckLossRate()!=null){
					li.setCheckLossRate(li.getCheckLossRate().divide(new BigDecimal(100)));
				}
			});
			tecBomItemMapper.inserts(tecBomItems);
		});
		Long materialSid = bomHead.getMaterialSid();
		List<Long> sidList = tecBomHeadMapper.selectList(new QueryWrapper<TecBomHead>().lambda()
				.eq(TecBomHead::getMaterialSid, materialSid)
		).stream().map(li -> li.getBomSid()).collect(Collectors.toList());
		//停用的bom添加明细
		if(skuSidList.size()!=sidList.size()){
			List<Long> nowSidList = skuSidList.stream().map(li -> li.getBomSid()).collect(Collectors.toList());
			//两个集合取差集
			List<Long> reduce = sidList.stream().filter(item -> !nowSidList.contains(item)).collect(Collectors.toList());
			reduce.forEach(sid->{
				tecBomItemMapper.deleteItemByBomId(sid);
				itemList.forEach(item->{
					item.setBomSid(sid);
					item.setBomItemSid(null);
					item.setBomMaterialSku1Sid(null);
					if(item.getLossRate()!=null){
						item.setLossRate(item.getLossRate().divide(new BigDecimal(100)));
					}
					if(item.getQuoteLossRate()!=null){
						item.setQuoteLossRate(item.getQuoteLossRate().divide(new BigDecimal(100)));
					}
					if(item.getConfirmLossRate()!=null){
						item.setConfirmLossRate(item.getConfirmLossRate().divide(new BigDecimal(100)));
					}
					if(item.getCheckLossRate()!=null){
						item.setCheckLossRate(item.getCheckLossRate().divide(new BigDecimal(100)));
					}
					tecBomItemMapper.insert(item);
				});
			});
		}
		//附件
		List<TecBomAttachment> attachmentList = bomHead.getAttachmentList();
		skuSidList.forEach(li->{
			tecBomAttachmentMapper.delete(new QueryWrapper<TecBomAttachment>().lambda()
					.eq(TecBomAttachment::getBomSid,li.getBomSid())
			);
			if(CollectionUtil.isNotEmpty(attachmentList)){
				attachmentList.forEach(o->{
					o.setBomSid(li.getBomSid());
					o.setBomAttachmentSid(null);
				});
				tecBomAttachmentMapper.inserts(attachmentList);
			}
		});
		TecBomHead response = tecBomHeadMapper.selectById(bomHead.getBomSid());
		if(ConstantsEms.CHECK_STATUS.equals(response.getHandleStatus())){
			//插入日志
			sidList.forEach(li->{
				MongodbUtil.insertUserLog(li, BusinessType.CHANGE.getValue(), TITLE);
			});
		}else{
			//插入日志
			sidList.forEach(li->{
				MongodbUtil.insertUserLog(li, BusinessType.UPDATE.getValue(), TITLE);
			});
		}
		return 1;
}
	//bom 物料替换
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int exChange(TecBomHeadExchangeRequest request) {
		Long bomMaterialSidOld = request.getBomMaterialSidOld();
		Long bomMaterialSidNew = request.getBomMaterialSidNew();
		Long bomMaterialSku1SidNew = request.getBomMaterialSku1SidNew();
		Long bomMaterialSku1SidOld = request.getBomMaterialSku1SidOld();
		String before=null;
		String after=null;
		if(bomMaterialSku1SidNew!=null){
			BasSku basSkuterbefore = basSkuMapper.selectById(bomMaterialSku1SidOld);
			BasSku basSkuterAfter = basSkuMapper.selectById(bomMaterialSku1SidNew);
			before=request.getBomMaterialCodeOld()+"/"+basSkuterbefore.getSkuName();
			after=request.getBomMaterialCodeNew()+"/"+basSkuterAfter.getSkuName();
		}else{
			before=request.getBomMaterialCodeOld();
			after=request.getBomMaterialCodeNew();
		}
		List<TecBomHeadReportExSidRequest> sidList = request.getSidList();
		if(ConstantsEms.YES.equals(request.getIsExchangeCode())){
			//只替物料
			sidList.stream().forEach(item->{
				List<Long> sids = basMaterialSkuMapper.selectList(new QueryWrapper<BasMaterialSku>().lambda()
						.eq(BasMaterialSku::getSkuType, ConstantsEms.SKUTYP_YS)
						.eq(BasMaterialSku::getStatus,ConstantsEms.ENABLE_STATUS)
						.eq(BasMaterialSku::getMaterialSid, bomMaterialSidNew)
				).stream().map(li -> li.getSkuSid()).collect(Collectors.toList());
				List<TecBomHead> tecBomHeads = tecBomHeadMapper.selectList(new QueryWrapper<TecBomHead>().lambda()
						.eq(TecBomHead::getMaterialSid, item.getMaterialSid())
				);
				List<Long> bomSidList = tecBomHeads.stream().map(li -> li.getBomSid()).collect(Collectors.toList());
				List<Long> bomskusidList = tecBomItemMapper.selectList(new QueryWrapper<TecBomItem>().lambda()
						.in(TecBomItem::getBomSid, bomSidList)
						.eq(TecBomItem::getItemNum,item.getItemNum())
				).stream().map(li -> li.getBomMaterialSku1Sid()).collect(Collectors.toList());
				//现有的sku是否都包含旧的sku
				bomskusidList.stream().forEach(li->{
					if(li!=null){
						boolean contains = sids.contains(li);
						if(!contains){
							throw new CustomException("物料编码(旧)在BOM中所用到的颜色未全部包含在物料编码(新)档案中，无法替换，请检查！");
						}
					}
				});
				BasMaterial basMaterial = basMaterialMapper.selectById(bomMaterialSidNew);
				tecBomHeads.forEach(bom->{
					tecBomItemMapper.update(new TecBomItem(),new UpdateWrapper<TecBomItem>().lambda()
							.eq(TecBomItem::getBomSid,bom.getBomSid())
							.eq(TecBomItem::getItemNum,item.getItemNum())
							.eq(TecBomItem::getBomMaterialSid,bomMaterialSidOld)
							.set(TecBomItem::getBomMaterialSid,bomMaterialSidNew)
							.set(TecBomItem::getUnitBase,basMaterial.getUnitBase())
//							.set(TecBomItem::getUnitRecursion,basMaterial.getUnitRecursion())
							.set(TecBomItem::getUnitQuantity,basMaterial.getUnitQuantity())
							.set(TecBomItem::getPurchaseType,basMaterial.getPurchaseType())
							);
				});
			});

		}else{
			//替换物料和颜色
			sidList.stream().forEach(item->{
				TecBomItem bomItem = new TecBomItem();
				bomItem.setBomMaterialSid(bomMaterialSidOld)
						.setItemNum(item.getItemNum())
						.setMaterialSid(item.getMaterialSid());
				List<TecBomItem> tecBomItems = tecBomItemMapper.selectTecBomItemList(bomItem);
				Set<Long> setSize = tecBomItems.stream().filter(li -> li.getBomMaterialSku1Sid() != null && ConstantsEms.ENABLE_STATUS.equals(li.getStatus())).map(li -> li.getBomMaterialSku1Sid()).collect(Collectors.toSet());
				//被替换的颜色数量不可以超过一种
				if(setSize.size()==1){
					List<Long> sidsDelete = tecBomItems.stream().map(li -> li.getBomItemSid()).collect(Collectors.toList());
					tecBomItemMapper.deleteBatchIds(sidsDelete);
				}else{
					throw  new  CustomException("该清单列颜色超过一种");
				}
				for (int i = 0; i < tecBomItems.size(); i++) {
					tecBomItems.get(i).setBomMaterialSku1Sid(bomMaterialSku1SidNew);
					tecBomItems.get(i).setBomMaterialSid(bomMaterialSidNew);
					tecBomItems.get(i).setBomItemSid(null);
				}
				if(!bomMaterialSidOld.toString().equals(bomMaterialSidNew.toString())){
					tecBomItems.forEach(li->{
						BasMaterial material = basMaterialMapper.selectById(bomMaterialSidNew);
						li.setUnitBase(material.getUnitBase())
//								.setUnitRecursion(material.getUnitRecursion())
								.setUnitQuantity(material.getUnitQuantity())
								.setPurchaseType(material.getPurchaseType());
					});
				}
				tecBomItemMapper.inserts(tecBomItems);
			});
		}
		String be=before;
		String af=after;
		Set<Long> materialSidList = sidList.stream().map(li -> li.getMaterialSid()).collect(Collectors.toSet());
		materialSidList.stream().forEach(id->{
			List<TecBomHead> tecBomHeads = tecBomHeadMapper.selectList(new QueryWrapper<TecBomHead>().lambda()
					.eq(TecBomHead::getMaterialSid, id)
			);
           String msg="物料替换，替换前："+
				   be+"，替换后："+af+"；替换说明："+ request.getExplain();
			tecBomHeads.forEach(tecBom->{
				MongodbUtil.insertApprovalLog(tecBom.getBomSid(), BusinessType.WLTH.getValue(),msg);
			});
		});
		return 1;
	}

	public void judgeNull(List<TecBomItem> itemList){
		if(CollectionUtils.isEmpty(itemList)){
			throw new CustomException("确认时，明细行不允许为空");
		}else{
			Boolean exit=false;
			for (int i=0;i<itemList.size();i++){
				if(ConstantsEms.IS_MATERIAL.equals(itemList.get(i).getIsMainFabric())){
					exit=true;
					break;
				}
			}
			if(exit){
				itemList.forEach(item->{
					if(item.getQuantity()==null){
						throw new CustomException("存在用量未填写的明细行，请检查！");
					}
				});
			}else{
				throw new CustomException("确认时，明细行至少有一条主面料,请勾选主面料");
			}
			//组合拉链 颜色回写
			itemList.forEach(li->{
				insertZipperSku(li);
			});
		}
	}
	//物料颜色替换校验
    @Override
	public int changeJudge(List<TecBomHeadReportExSidRequest> list) {
		HashSet<Long> sidsSet = new HashSet<>();
		list.forEach(it -> {
			List<Long> sids = tecBomHeadMapper.selectList(new QueryWrapper<TecBomHead>().lambda()
					.eq(TecBomHead::getMaterialSid, it.getMaterialSid())
			).stream().map(li -> li.getBomSid()).collect(Collectors.toList());
			tecBomItemMapper.selectList(new QueryWrapper<TecBomItem>().lambda()
					.in(TecBomItem::getBomSid, sids)
					.eq(TecBomItem::getItemNum, it.getItemNum())
			).stream().forEach(li -> {
						if (li.getBomMaterialSku1Sid() == null) {
							throw new CustomException("该物料编码在BOM中所用到的颜色不一致，无法进行颜色替换，请检查！");
						}else{
							sidsSet.add(li.getBomMaterialSku1Sid());
							if(sidsSet.size()>1){
								throw new CustomException("该物料编码在BOM中所用到的颜色不一致，无法进行颜色替换，请检查！");
							}
						}
					}
			);
		});
		return 1;
	}
	//物料替换 校验
	@Override
	public AjaxResult Judge(TecBomHeadExChangeJudgeRequest request){
		String bomMaterialCode = request.getBomMaterialCode();
		Long bomMaterialSid = request.getBomMaterialSid();
		Long bomSid = request.getBomSid();
		List<Long> materialSidList = request.getMaterialSidList();
		String msg=null;
		Map<Long, List<Long>> listMap = materialSidList.stream().collect(Collectors.groupingBy(li -> li));
		String code=null;
		String selfCode=null;
		HashMap<String, String> hashMap = new HashMap<>();
		for (Long li : listMap.keySet()) {
			List<TecBomHead> tecBomHeads = tecBomHeadMapper.selectList(new QueryWrapper<TecBomHead>().lambda()
					.eq(TecBomHead::getMaterialSid, li)
			);
			TecBomItem tecBomItem = new TecBomItem();
			tecBomItem.setMaterialSid(li)
					.setBomSid(tecBomHeads.get(0).getBomSid())
					.setBomMaterialSid(bomMaterialSid);
			List<TecBomItem> tecBomItems = tecBomItemMapper.exChangeMaterial(tecBomItem);
			if(tecBomItems.size()>listMap.get(li).size()){
				String materialCode = tecBomItems.get(0).getMaterialCode();
				String sampleCodeSelf = tecBomItems.get(0).getSampleCodeSelf();
				if(materialCode!=null){
					if(code==null){
						code="["+materialCode+"]";
					}else{
						code=code+"、["+materialCode+"]";
					}
				}else{
					if(selfCode==null){
						selfCode="["+sampleCodeSelf+"]";
					}else{
						selfCode=selfCode+"、["+sampleCodeSelf+"]";
					}

				}
			}
		}
		if(code!=null){
			msg="<p>物料编码"+bomMaterialCode+"，在款号"+code+"的BOM明细中存在多行，未全部勾选，是否继续？</p>";
		}
		if(selfCode!=null){
			if(msg==null){
				msg="<p>物料编码"+bomMaterialCode+"，在我司样衣号"+selfCode+"的BOM明细中存在多行，未全部勾选，是否继续？</p>";
			}else{
				msg=msg+"<p>物料编码"+bomMaterialCode+"，在我司样衣号"+selfCode+"的BOM明细中存在多行，未全部勾选，是否继续？</p>";
			}
		}
		if(msg!=null){
			return AjaxResult.success(msg,500);
		}
		return AjaxResult.success(200);
	}
	/**
	 * 批量删除物料清单（BOM）主
	 *
	 * @param clientIds 需要删除的物料清单（BOM）主ID
	 * @return 结果
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int deleteTecBomHeadByIds(List<Long> materialSids) {
		int i = 0;
		for (Long materialSid : materialSids) {
			LambdaUpdateWrapper<BasMaterial> updateWrapper = new LambdaUpdateWrapper<>();
			updateWrapper.in(BasMaterial::getMaterialSid,materialSid).set(BasMaterial::getIsHasCreatedBom, ConstantsEms.NO);
			i = i + basMaterialMapper.update(null, updateWrapper);
		}
		//删除工作流额外记录流程信息的表
		formProcessService.deleteSysFormProcessByIds(materialSids);
		for (Long materialSid : materialSids){
			List<TecBomHead> tecBomHead = tecBomHeadMapper.selectTecBomHeadByMaterialSid(materialSid);
			if(!CollectionUtils.isEmpty(tecBomHead)) {
				for(TecBomHead bom : tecBomHead) {
                    //校验是否存在待办
                    checkTodoExist(bom);
					tecBomAttachmentMapper.deleteAttachmentByBomId(bom.getBomSid());
					tecBomItemMapper.deleteItemByBomId(bom.getBomSid());
					tecBomHeadMapper.deleteById(bom.getBomSid());
					sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
							.eq(SysTodoTask::getDocumentCode, bom.getMaterialCode()));
					//插入日志
					MongodbUtil.insertUserLog(bom.getBomSid(),BusinessType.DELETE.getValue(), null, TITLE);
				}
			}
		}
		return 1;
	}

	@Override
	public int deleteTecBomHeadById(Long clientId) {
		return 0;
	}

	@Override
	public int editHandleStatus(TecBomHead tecBomHead) {
		TecBomHead bom = new TecBomHead();
		List<Long> bomSids = tecBomHead.getBomSids();
		for(Long sid:tecBomHead.getBomSids()) {
			bom = new TecBomHead();
			bom.setBomSid(sid);
			bom.setHandleStatus(tecBomHead.getHandleStatus());
			TecBomHead tecBom = new TecBomHead();
			TecBomHead head = tecBomHeadMapper.selectById(sid);
			//颜色一致性校验
			List<TecBomHead> tecBomHeads = tecBomHeadMapper.selectList(new QueryWrapper<TecBomHead>().lambda().eq(TecBomHead::getMaterialSid, head.getMaterialSid()));
			tecBomHeads=tecBomHeads.stream().filter(li->li.getStatus().equals(ConstantsEms.ENABLE_STATUS)).collect(Collectors.toList());
			List<Integer> color=new ArrayList<>();
			List<TecBomItem> bomItems = new ArrayList<>();
			tecBomHeads.forEach(item->{
				//校验是否存在待办
				checkTodoExist(item);
				List<TecBomItem> tecBomItems =  tecBomItemMapper.selectBomItemByBomSid(item.getBomSid());
				if(CollectionUtil.isEmpty(bomItems)){
					bomItems.addAll(tecBomItems);
				}
				//确认校验以及颜色回写
				judgeNull(tecBomItems);
				if(CollectionUtil.isNotEmpty(tecBomItems)){
					List<TecBomItem> items = tecBomItems.stream().filter(li -> li.getBomMaterialSku1Sid() != null).collect(Collectors.toList());
					color.add(items.size());
				}else{
					color.add(0);
				}
			});
//			if(CollectionUtil.isNotEmpty(bomItems)){
//				bomItems.forEach(li->{
//					BasMaterial material = new BasMaterial();
//					material.setMaterialSid(li.getBomMaterialSid());
////					material.setUnitRecursion(li.getUnitRecursion());
//					basMaterialMapper.updateById(material);
//				});
//			}
			for (int i = 0; i < color.size() - 1; i++) {
				if (color.get(i) != color.get(i + 1)) {
					throw new CustomException("物料信息不完整，请检查！");
				}
			}
			tecBomHeadMapper.update(tecBom,new UpdateWrapper<TecBomHead>().lambda()
					.eq(TecBomHead::getMaterialSid,head.getMaterialSid())
					.set(TecBomHead::getHandleStatus,ConstantsEms.CHECK_STATUS));
			//插入日志
			List<OperMsg> msgList=new ArrayList<>();
			MongodbUtil.insertUserLog(sid, BusinessType.CHECK.getValue(), msgList,TITLE);
		}
		return 1;
	}

	@Override
	public int editStatus(TecBomHead tecBomHead) {
		TecBomHead bom = new TecBomHead();
		for(Long sid:tecBomHead.getBomSids()) {
			bom = new TecBomHead();
			bom.setBomSid(sid);
			bom.setStatus(tecBomHead.getStatus());
			tecBomHeadMapper.updateStatus(bom);
		}
		return 1;
	}

	@Override
	public int updateBomStatus(TecBomHead tecBomHead) {
		TecBomHead bom = new TecBomHead();
		for(Long sid:tecBomHead.getBomSids()) {
			bom = new TecBomHead();
			bom.setBomSid(sid);
			bom.setBomStatus(tecBomHead.getBomStatus());
			tecBomHeadMapper.updateBomStatus(bom);
		}
		return 1;
	}

	/**
	 * 获取Bom明细列表
	 * @return
	 */
	@Override
	public List<TecBomItem> selectTecBomItemList(TecBomHead tecBomHead) {
		return tecBomHeadMapper.selectTecBomItemList(tecBomHead);
	}

	@Override
	public List<TecBomHead> getListByMaterialSid(Long materialSid) {
		TecBomHead tecBomHead = new TecBomHead();
		tecBomHead.setMaterialSid(materialSid);
		List<TecBomHead> list = tecBomHeadMapper.selectTecBomHeadList(tecBomHead);
		return list;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public AjaxResult processCheck(TecBomHead bomHead){
		TecBomHead bom = getBom(bomHead);
		return judgeBom(bom,ConstantsEms.SAVA_STATUS);
	}

	public AjaxResult judgeBom(TecBomHead bom,String type){
		Long materialSid = bom.getMaterialSid();
		BasMaterial basMaterial = basMaterialMapper.selectById(materialSid);
		if(!basMaterial.getStatus().equals(ConstantsEms.ENABLE_STATUS)){
			String msg=basMaterial.getMaterialCode()!=null?"商品编码"+basMaterial.getMaterialCode()+"已停用，请检查！":"我司样衣号"+basMaterial.getSampleCodeSelf()+"已停用，请检查！";
			throw  new CustomException(msg);
		}
		List<TecBomItem> itemList = bom.getItemList();
		List<TecBomItem> isMacList = itemList.stream().filter(li -> ConstantsEms.IS_MATERIAL.equals(li.getIsMainFabric())).collect(Collectors.toList());
		if(CollectionUtil.isEmpty(isMacList)){
			throw  new CustomException("主面料未勾选，无法提交！");
		}else if(isMacList.size() ==1){
			itemList.forEach(li->{
				List<BasBomColorRequest> skuSidList = li.getSkuSidList();
				skuSidList.forEach(i->{
					if(ConstantsEms.IS_MATERIAL.equals(li.getIsMainFabric())&&i.getBomMaterialSku1Sid()==null){
						throw  new CustomException("存在主面料的颜色未填写，无法提交！");
					}
				});
			});
		}else{
			List<TecBomItem> items = itemList.stream().filter(li -> ConstantsEms.IS_MATERIAL.equals(li.getIsMainFabric())).collect(Collectors.toList());
			items.forEach(li->{
				List<BasBomColorRequest> skuSidList = li.getSkuSidList();
				List<BasBomColorRequest> colorList = skuSidList.stream().filter(color -> color.getBomMaterialSku1Sid() != null).collect(Collectors.toList());
				if(CollectionUtil.isEmpty(colorList)){
					throw  new CustomException("存在主面料的颜色未填写，无法提交！");
				}
			});
			List<BasBomColorRequest> skuSidList = items.get(0).getSkuSidList();
			Boolean[] isExitList =new Boolean[skuSidList.size()];
			for (int i=0;i<skuSidList.size();i++){
				isExitList[i]=false;
			}
			items.forEach(li->{
				List<BasBomColorRequest> liSkuSidList = li.getSkuSidList();
				for (int i = 0; i <liSkuSidList.size(); i++) {
					if(liSkuSidList.get(i).getBomMaterialSku1Sid()!=null){
						isExitList[i]=true;
					}
				}
			});
			for (Boolean exit : isExitList) {
				if(!exit){
					throw  new CustomException("存在主面料的颜色未填写，无法提交！");
				}
			}
		}
		itemList.forEach(li->{
			List<BasBomColorRequest> skuSidList = li.getSkuSidList();
			List<BasBomColorRequest> colorList = skuSidList.stream().filter(i -> i.getBomMaterialSku1Sid() != null).collect(Collectors.toList());
			if(CollectionUtil.isEmpty(colorList)){
				throw  new CustomException("存在物料颜色未填写，无法提交！");
			}

		});
		if(ConstantsEms.SAVA_STATUS.equals(type)){
			//提交校验
			List<TecBomHead> tecBomHeads = tecBomHeadMapper.selectList(new QueryWrapper<TecBomHead>().lambda().eq(TecBomHead::getMaterialSid, bom.getMaterialSid()));
			//过滤掉停用
			tecBomHeads=tecBomHeads.stream().filter(li->li.getStatus().equals(ConstantsEms.ENABLE_STATUS)).collect(Collectors.toList());
			//颜色一致性校验
			List<Integer> color = new ArrayList<>();

			for(TecBomHead item : tecBomHeads ){
				//确认校验以及颜色回写
				List<TecBomItem> tecBomItems =  tecBomItemMapper.selectBomItemByBomSid(item.getBomSid());
				tecBomItems.forEach(o->{
					if(!o.getStatus().equals(ConstantsEms.ENABLE_STATUS)){
						String msg=basMaterial.getMaterialCode()!=null?"款号"+basMaterial.getMaterialCode()+"，存在停用的物料编码"+o.getMaterialCode()+"，请检查！":"我司样衣号"+basMaterial.getSampleCodeSelf()+"，存在停用的物料编码"+o.getMaterialCode()+"，请检查！";
						throw  new CustomException(msg);
					}
				});

				if(CollectionUtil.isNotEmpty(tecBomItems)){
					int size=0;
					HashSet<Long> bomsidItemList = new HashSet<>();
					for (int i = 0; i <tecBomItems.size(); i++) {
						if(tecBomItems.get(i).getBomMaterialSku1Sid()!=null){
							int old = bomsidItemList.size();
							bomsidItemList.add(tecBomItems.get(i).getBomMaterialSid());
							if(old<bomsidItemList.size()){
								size++;
							}
						}
					}
					color.add(size);
				}else{
					color.add(0);
				}
			}
			if(color.size()>1){
				for (int i = 0; i < color.size() - 1; i++) {
					if (color.get(i) != color.get(i + 1)) {
						throw  new CustomException("物料信息不完整，请检查！");
					}
				}
			}
			for(TecBomHead item : tecBomHeads ){
				//删除待办
				sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
						.eq(SysTodoTask::getDocumentSid, item.getBomSid()));
			}
		}else{
			List<Long> materialSidList = itemList.stream().map(li -> li.getBomMaterialSid()).collect(Collectors.toList());
			List<BasMaterial> basMaterials = basMaterialMapper.selectBatchIds(materialSidList);
			basMaterials.forEach(o->{
				if(!o.getStatus().equals(ConstantsEms.ENABLE_STATUS)){
					String msg=basMaterial.getMaterialCode()!=null?"款号"+basMaterial.getMaterialCode()+"，存在停用的物料编码"+o.getMaterialCode()+"，请检查！":"我司样衣号"+basMaterial.getSampleCodeSelf()+"，存在停用的物料编码"+o.getMaterialCode()+"，请检查！";
					throw  new CustomException(msg);
				}
			});
			if(ConstantsEms.CHECK_STATUS.equals(bom.getHandleStatus())){
				itemList.forEach(o->{
					List<BasBomColorRequest> skuSidList = o.getSkuSidList();
					skuSidList.forEach(li->{
						if(li.getBomMaterialSku1Sid()!=null){
							BasMaterialSku basMaterialSku = basMaterialSkuMapper.selectOne(new QueryWrapper<BasMaterialSku>().lambda()
									.eq(BasMaterialSku::getSkuType, ConstantsEms.SKUTYP_YS)
									.eq(BasMaterialSku::getMaterialSid, o.getBomMaterialSid())
									.eq(BasMaterialSku::getSkuSid, li.getBomMaterialSku1Sid())
							);
							if(!ConstantsEms.ENABLE_STATUS.equals(basMaterialSku.getStatus())){
								String msg=basMaterial.getMaterialCode()!=null?"款号"+basMaterial.getMaterialCode()+"，物料编码"+o.getMaterialCode()+"的"+li.getBomMaterialSku1Name()+"已被停用，请检查！":"我司样衣号"+basMaterial.getSampleCodeSelf()+"，物料编码"+o.getMaterialCode()+"的"+li.getBomMaterialSku1Name()+"已被停用，请检查！";
								throw  new CustomException(msg);
							}
						}
					});
				});
			}
			List<BasBomColorRequest> skuSidList = bom.getSkuSidList();
			List<Integer> exitColor=new ArrayList<>();
			for (int i=0;i<skuSidList.size();i++){
				int colorCount=new Integer(0);
				HashSet<Long> bomsidItemList = new HashSet<>();
				for (int j=0;j<itemList.size();j++){
					BasBomColorRequest colorList = itemList.get(j).getSkuSidList().get(i);;
					if(colorList.getBomMaterialSku1Sid()!=null){
						int old = bomsidItemList.size();
						bomsidItemList.add(itemList.get(j).getBomMaterialSid());
						if(old<bomsidItemList.size()){
							colorCount=colorCount+1;
						}
					}
				}
				exitColor.add(colorCount);
			}
			if(exitColor.size()>1){
				for (int m=0;m<exitColor.size()-1;m++){
					if(exitColor.get(m)!=exitColor.get(m+1)){
						throw new CustomException("物料信息不完整，请检查！");
					}
				}
			}
			if(CollectionUtil.isNotEmpty(itemList)){
				itemList.forEach(li->{
					String zipperFlag = li.getZipperFlag();
					if(!ConstantsEms.YES.equals(bom.getIsSkipJudge())){
						if(ConstantsEms.ZIPPER_ZH.equals(zipperFlag)||ConstantsEms.ZIPPER_ZT.equals(zipperFlag)){
							if(li.getPriceQuantity()==null){
								throw new CustomException("拉链"+li.getMaterialCode()+"，计价量需填写，请检查！");
							}
						}
                        if(li.getQuantity()==null){
                            throw new CustomException("存在用量未填写的明细行，请检查!");
                        }
					}
				});
			}
		}
		return AjaxResult.success(true);
	}
	@Override
	public void insertZipperSku (List<TecBomHead> bomList) {
		bomList.forEach(bom->{
			List<TecBomItem> tecBomItems =  tecBomItemMapper.selectBomItemByBomSid(bom.getBomSid());
			tecBomItems.forEach(item->{
				insertZipperSku(item);
			});
		});
	}

	public File getEmptyExcelFile(XSSFWorkbook workbook, String msg) throws IOException {
		File outputFile = File.createTempFile(msg, ".xlsx");
		try (FileOutputStream fos = new FileOutputStream(outputFile)) {
			workbook.write(fos);
			return outputFile;
		}
	}

	/**
	 * 压缩文件
	 *
	 * @param srcfile File[] 需要压缩的文件列表
	 * @param zipfile File 压缩后的文件
	 */
	public static void zipFiles(List<File> srcfile, File zipfile) {
		byte[] buf = new byte[1024];
		try {
			// Create the ZIP file
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));
			// Compress the files
			for (int i = 0; i < srcfile.size(); i++) {
				File file = srcfile.get(i);
				FileInputStream in = new FileInputStream(file);
				// Add ZIP entry to output stream.
				out.putNextEntry(new ZipEntry(file.getName()));
				// Transfer bytes from the file to the ZIP file
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				// Complete the entry
				out.closeEntry();
				in.close();
			}
			// Complete the ZIP file
			out.close();
		} catch (IOException e) {

		}
	}

	/**
	 * bom导入-编辑页面
	 */
	@Override
	public AjaxResult importBOM(MultipartFile file, String materialCode, String sampleCodeSelf) {
		try {
			File toFile = null;
			try {
				toFile = FileUtils.multipartFileToFile(file);
			} catch (Exception e) {
				e.getMessage();
				throw new BaseException("文件转换失败");
			}
			ExcelReader reader = ExcelUtil.getReader(toFile);
			FileUtils.delteTempFile(toFile);
			List<List<Object>> readAll = reader.read();
			InvInventoryDocument invInventoryDocument = new InvInventoryDocument();
			String materialCodeFile = null;
			String sampleCodeSelfFile = null;
			Long materialSid = null;
			List<CommonErrMsgResponse> msgList = new ArrayList<>();
			List<BasBomColorRequest> skuSidHeadList = new ArrayList<>();
			ArrayList<TecBomItem> tecBomItems = new ArrayList<>();
			for (int i = 1; i < readAll.size(); i++) {
				if(i == 4||i==1){
					continue;
				}
				if (i == 2) {
					List<Object> objects = readAll.get(i);
					copy(objects, readAll);
					int size = objects.size();
					if ((objects.get(0) != null && objects.get(0) != "") || (objects.get(1) != null && objects.get(1) != "")) {
						if (objects.get(0) != null && objects.get(0) != "") {
							String code = objects.get(0).toString();
							BasMaterial basMaterial = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
									.eq(BasMaterial::getMaterialCode, code)
							);
							if (materialCode != null) {
								if (!code.equals(materialCode)) {
									CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
									errMsgResponse.setItemNum(3);
									errMsgResponse.setMsg("表格中的商品编码，与系统中的商品编码不一致，导入失败！");
									msgList.add(errMsgResponse);
								}
							}
							if (CollectionUtil.isNotEmpty(msgList)) {
								return AjaxResult.error("报错信息", msgList);
							}
							if (basMaterial == null) {
								CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
								errMsgResponse.setItemNum(3);
								errMsgResponse.setMsg("商品编码" + code + "不存在，导入失败");
								msgList.add(errMsgResponse);
							} else {
								if (!ConstantsEms.CHECK_STATUS.equals(basMaterial.getHandleStatus())
										|| !ConstantsEms.ENABLE_STATUS.equals(basMaterial.getStatus())) {
									CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
									errMsgResponse.setItemNum(3);
									errMsgResponse.setMsg("商品编码，必须是已确认且启用状态，导入失败");
									msgList.add(errMsgResponse);
								} else {
									materialCodeFile = code;
									materialSid = basMaterial.getMaterialSid();
								}
							}
						} else {
							String code = objects.get(1).toString();
							BasMaterial basMaterial = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
									.eq(BasMaterial::getSampleCodeSelf, code)
							);
							if (sampleCodeSelf != null) {
								if (!sampleCodeSelf.equals(code)) {
									CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
									errMsgResponse.setItemNum(3);
									errMsgResponse.setMsg("表格中的我司样衣号，与系统中的我司样衣号不一致，导入失败！");
									msgList.add(errMsgResponse);
								}
							}
							if (CollectionUtil.isNotEmpty(msgList)) {
								return AjaxResult.error("报错信息", msgList);
							}
							if (basMaterial == null) {
								CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
								errMsgResponse.setItemNum(3);
								errMsgResponse.setMsg("我司样衣号" + code + "不存在，导入失败");
								msgList.add(errMsgResponse);
							} else {
								if (!ConstantsEms.CHECK_STATUS.equals(basMaterial.getHandleStatus())
										||!ConstantsEms.ENABLE_STATUS.equals(basMaterial.getStatus())) {
									CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
									errMsgResponse.setItemNum(3);
									errMsgResponse.setMsg("我司样衣号，必须是已确认且启用状态，导入失败");
									msgList.add(errMsgResponse);
								} else {
									sampleCodeSelfFile = code;
									materialSid = basMaterial.getMaterialSid();
								}
							}
						}

					} else {
						CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
						errMsgResponse.setItemNum(3);
						errMsgResponse.setMsg("商品编码、我司样衣号，不能同时为空，导入失败");
						msgList.add(errMsgResponse);
					}
					if (CollectionUtil.isNotEmpty(msgList)) {
						return AjaxResult.error("报错信息", msgList);
					}
					continue;
				}
				if (i == 3) {
					List<Object> objects = readAll.get(i);
					int size = objects.size();
					BasMaterialSku basMaterialSku = new BasMaterialSku();
					basMaterialSku.setMaterialSid(materialSid);
					List<BasMaterialSku> basMaterialSkuList = basMaterialSkuMapper.selectBasMaterialSkuList(basMaterialSku);
					List<BasMaterialSku> skuList = basMaterialSkuList.stream().filter(li -> li.getStatus().equals(ConstantsEms.ENABLE_STATUS) && li.getSkuType().equals(ConstantsEms.SKUTYP_YS)).
							sorted(Comparator.comparing(item -> item.getItemNum())).collect(Collectors.toList());
					if (skuList.size() != size - 5) {
						CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
						errMsgResponse.setItemNum(4);
						errMsgResponse.setMsg("表格中的成衣颜色的数量，与系统中的成衣颜色的数量不一致，导入失败！");
						msgList.add(errMsgResponse);
					} else {
						for (int m = 0; m < skuList.size(); m++) {
							BasBomColorRequest basBomColorRequest = new BasBomColorRequest();
							String o = objects.get(m + 5).toString();
							BasSku basSku = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
									.eq(BasSku::getSkuName, o)
							);
							if(basSku!=null){
								TecBomHead tecBomHead = tecBomHeadMapper.selectOne(new QueryWrapper<TecBomHead>().lambda()
										.eq(TecBomHead::getMaterialSid, materialSid)
										.eq(TecBomHead::getSku1Sid, basSku.getSkuSid())
								);
								if(tecBomHead!=null){
									basBomColorRequest.setBomSid(tecBomHead.getBomSid());
								}
							}else{
								CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
								errMsgResponse.setItemNum(4);
								errMsgResponse.setMsg("成衣颜色“"+o+"”不存在，导入失败");
								msgList.add(errMsgResponse);
								break;
							}
							basBomColorRequest.setSku1Sid(basSku.getSkuSid())
									.setSku1Name(o);
							skuSidHeadList.add(basBomColorRequest);
							if (!skuList.get(m).getSkuName().equals(o)) {
								CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
								errMsgResponse.setItemNum(4);
								errMsgResponse.setMsg("表格中的成衣颜色的排序，与系统中的成衣颜色的排序不一致，导入失败！");
								msgList.add(errMsgResponse);
								break;
							}
						}
					}
					if (CollectionUtil.isNotEmpty(msgList)) {
						return AjaxResult.error("报错信息", msgList);
					}
					continue;
				}
				int num = i + 1;
				List<Object> objects = readAll.get(i);
				int length = readAll.get(3).size();
				Long materialSidItem = null;
				String materialName = null;
                String materialCodeItem = null;
				copyItem(objects, readAll);
				List<BasBomColorRequest> skuSidItemList = new ArrayList<>();
				TecBomItem tecBomItem = new TecBomItem();
				if (objects.get(0) != null && objects.get(0) != "") {
					String itemNum = objects.get(0).toString();
					boolean match = JudgeFormat.isValidDouble(itemNum);
					if (!match) {
						CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
						errMsgResponse.setItemNum(num);
						errMsgResponse.setMsg("序号，数据格式错误，导入失败");
						msgList.add(errMsgResponse);
					}
				}
				if (objects.get(1) == null || objects.get(1) == "") {
					CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
					errMsgResponse.setItemNum(num);
					errMsgResponse.setMsg("物料编码不能为空，导入失败");
					msgList.add(errMsgResponse);
				} else {
					String code = objects.get(1).toString();
					BasMaterial basMaterial = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
							.eq(BasMaterial::getMaterialCode, code)
					);
					if (basMaterial == null) {
						CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
						errMsgResponse.setItemNum(num);
						errMsgResponse.setMsg("物料编码" + code + "不存在，导入失败");
						msgList.add(errMsgResponse);
					} else {
						if (!ConstantsEms.CHECK_STATUS.equals(basMaterial.getHandleStatus())
								|| !ConstantsEms.ENABLE_STATUS.equals(basMaterial.getStatus())) {
							CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
							errMsgResponse.setItemNum(num);
							errMsgResponse.setMsg("物料编码，必须是已确认且启用状态，导入失败");
							msgList.add(errMsgResponse);
						} else {
							materialSidItem = basMaterial.getMaterialSid();
							materialName = basMaterial.getMaterialName();
							materialCodeItem=basMaterial.getMaterialCode();
						}
					}
				}
				if (objects.get(3) != null && objects.get(3) != "") {
					String isMac = objects.get(3).toString();
					if (!isMac.equals("是")) {
						CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
						errMsgResponse.setItemNum(num);
						errMsgResponse.setMsg("是否主面料，配置错误，导入失败");
						msgList.add(errMsgResponse);
					}
				}

				for (int m = 0; m < length - 5; m++) {
					BasBomColorRequest basBomColorRequest = new BasBomColorRequest();
					//获取对应成衣的颜色
					basBomColorRequest.setSku1Sid(skuSidHeadList.get(m).getSku1Sid())
							.setBomSid(skuSidHeadList.get(m).getBomSid())
							.setSku1Name(skuSidHeadList.get(m).getSku1Name());
					Object sku = objects.get(m + 5);
					if(materialCodeItem!=null){
						if (sku != null && sku != "") {
							String skuName = sku.toString();
							BasSku basSku = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
									.eq(BasSku::getSkuName, skuName)
							);
							if (basSku == null) {
								CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
								errMsgResponse.setItemNum(num);
								errMsgResponse.setMsg("物料编码" + materialCodeItem + "不存在物料颜色“" + skuName + "”，导入失败");
								msgList.add(errMsgResponse);
							} else {
								BasMaterialSku basMaterialSku = basMaterialSkuMapper.selectOne(new QueryWrapper<BasMaterialSku>().lambda()
										.eq(BasMaterialSku::getSkuSid, basSku.getSkuSid())
										.eq(BasMaterialSku::getMaterialSid,materialSidItem)
										.eq(BasMaterialSku::getSkuType, ConstantsEms.SKUTYP_YS)
								);
								if (basMaterialSku == null) {
									CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
									errMsgResponse.setItemNum(num);
									errMsgResponse.setMsg("物料编码" + materialCodeItem + "不存在物料颜色“" + skuName + "”，导入失败");
									msgList.add(errMsgResponse);
								} else {
									if (!ConstantsEms.ENABLE_STATUS.equals(basMaterialSku.getStatus())) {
										CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
										errMsgResponse.setItemNum(num);
										errMsgResponse.setMsg("物料编码" + materialCodeItem + "，物料颜色“" + skuName + "”不是启用状态，导入失败");
										msgList.add(errMsgResponse);
									} else {
										basBomColorRequest.setBomMaterialSku1Sid(basSku.getSkuSid())
												.setBomMaterialSku1Name(basSku.getSkuName());
									}
								}
							}
						}
							skuSidItemList.add(basBomColorRequest);

					}
				}
				if(materialSidItem!=null){
					BasMaterial basMaterial = basMaterialMapper.selectBasMaterialBomById(materialSidItem);
					BeanCopyUtils.copyProperties(basMaterial,tecBomItem);
				}
				tecBomItem.setSkuSidList(skuSidItemList);
				tecBomItem.setMaterialSid(materialSidItem)
						.setBomMaterialSid(materialSidItem)
                       .setRoundingType("SSWR")
						.setSort(objects.get(0)==null||objects.get(0)==""?null:objects.get(0).toString())
						.setIsMainFabric(objects.get(3)==null||objects.get(3)==""?null:"1")
						.setPositionName(objects.get(4)==null||objects.get(4)==""?null:objects.get(4).toString());
				tecBomItems.add(tecBomItem);
			}
			if (CollectionUtil.isNotEmpty(msgList)) {
				return AjaxResult.error("报错信息", msgList);
			}
			return AjaxResult.success(tecBomItems);
		} catch (BaseException e) {
			throw new BaseException(e.getDefaultMessage());
		}
	}

	/**
	 * bom导入-新建页面
	 */
	@Override
	public AjaxResult importBOMAdd(MultipartFile file, String materialCode, String sampleCodeSelf) {
		try {
			File toFile = null;
			try {
				toFile = FileUtils.multipartFileToFile(file);
			} catch (Exception e) {
				e.getMessage();
				throw new BaseException("文件转换失败");
			}
			ExcelReader reader = ExcelUtil.getReader(toFile);
			FileUtils.delteTempFile(toFile);
			List<List<Object>> readAll = reader.read();
			InvInventoryDocument invInventoryDocument = new InvInventoryDocument();
			String materialCodeFile = null;
			String sampleCodeSelfFile = null;
			Long materialSid = null;
			List<CommonErrMsgResponse> msgList = new ArrayList<>();
			List<BasBomColorRequest> skuSidHeadList = new ArrayList<>();
			List<BomSortResponse> tecBomItems = new ArrayList<>();
			for (int i = 1; i < readAll.size(); i++) {
				if(i == 4||i==1){
					continue;
				}
				if (i == 2) {
					List<Object> objects = readAll.get(i);
					copy(objects, readAll);
					int size = objects.size();
					if ((objects.get(0) != null && objects.get(0) != "") || (objects.get(1) != null && objects.get(1) != "")) {
						if (objects.get(0) != null && objects.get(0) != "") {
							String code = objects.get(0).toString();
							BasMaterial basMaterial = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
									.eq(BasMaterial::getMaterialCode, code)
							);
							if (materialCode != null) {
								if (!code.equals(materialCode)) {
									CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
									errMsgResponse.setItemNum(3);
									errMsgResponse.setMsg("表格中的商品编码，与系统中的商品编码不一致，导入失败！");
									msgList.add(errMsgResponse);
								}
							}
							if (CollectionUtil.isNotEmpty(msgList)) {
								return AjaxResult.error("报错信息", msgList);
							}
							if (basMaterial == null) {
								CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
								errMsgResponse.setItemNum(3);
								errMsgResponse.setMsg("商品编码" + code + "不存在，导入失败");
								msgList.add(errMsgResponse);
							} else {
								if (!ConstantsEms.CHECK_STATUS.equals(basMaterial.getHandleStatus())
										|| !ConstantsEms.ENABLE_STATUS.equals(basMaterial.getStatus())) {
									CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
									errMsgResponse.setItemNum(3);
									errMsgResponse.setMsg("商品编码，必须是已确认且启用状态，导入失败");
									msgList.add(errMsgResponse);
								} else {
									materialCodeFile = code;
									materialSid = basMaterial.getMaterialSid();
								}
							}
						} else {
							String code = objects.get(1).toString();
							BasMaterial basMaterial = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
									.eq(BasMaterial::getSampleCodeSelf, code)
							);
							if (sampleCodeSelf != null) {
								if (!sampleCodeSelf.equals(code)) {
									CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
									errMsgResponse.setItemNum(3);
									errMsgResponse.setMsg("表格中的我司样衣号，与系统中的我司样衣号不一致，导入失败！");
									msgList.add(errMsgResponse);
								}
							}
							if (CollectionUtil.isNotEmpty(msgList)) {
								return AjaxResult.error("报错信息", msgList);
							}
							if (basMaterial == null) {
								CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
								errMsgResponse.setItemNum(3);
								errMsgResponse.setMsg("我司样衣号" + code + "不存在，导入失败");
								msgList.add(errMsgResponse);
							} else {
								if (!ConstantsEms.CHECK_STATUS.equals(basMaterial.getHandleStatus())
										||!ConstantsEms.ENABLE_STATUS.equals(basMaterial.getStatus())) {
									CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
									errMsgResponse.setItemNum(3);
									errMsgResponse.setMsg("我司样衣号，必须是已确认且启用状态，导入失败");
									msgList.add(errMsgResponse);
								} else {
									sampleCodeSelfFile = code;
									materialSid = basMaterial.getMaterialSid();
								}
							}
						}

					} else {
						CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
						errMsgResponse.setItemNum(3);
						errMsgResponse.setMsg("商品编码、我司样衣号，不能同时为空，导入失败");
						msgList.add(errMsgResponse);
					}
					if (CollectionUtil.isNotEmpty(msgList)) {
						return AjaxResult.error("报错信息", msgList);
					}
					continue;
				}
				if (i == 3) {
					List<Object> objects = readAll.get(i);
					int size = objects.size();
					BasMaterialSku basMaterialSku = new BasMaterialSku();
					basMaterialSku.setMaterialSid(materialSid);
					List<BasMaterialSku> basMaterialSkuList = basMaterialSkuMapper.selectBasMaterialSkuList(basMaterialSku);
					List<BasMaterialSku> skuList = basMaterialSkuList.stream().filter(li -> li.getStatus().equals(ConstantsEms.ENABLE_STATUS) && li.getSkuType().equals(ConstantsEms.SKUTYP_YS)).
							sorted(Comparator.comparing(item -> item.getItemNum())).collect(Collectors.toList());
					if (skuList.size() != size - 5) {
						CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
						errMsgResponse.setItemNum(4);
						errMsgResponse.setMsg("表格中的成衣颜色的数量，与系统中的成衣颜色的数量不一致，导入失败！");
						msgList.add(errMsgResponse);
					} else {
						for (int m = 0; m < skuList.size(); m++) {
							BasBomColorRequest basBomColorRequest = new BasBomColorRequest();
							String o = objects.get(m + 5).toString();
							BasSku basSku = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
									.eq(BasSku::getSkuName, o)
							);
							if(basSku!=null){
								TecBomHead tecBomHead = tecBomHeadMapper.selectOne(new QueryWrapper<TecBomHead>().lambda()
										.eq(TecBomHead::getMaterialSid, materialSid)
										.eq(TecBomHead::getSku1Sid, basSku.getSkuSid())
								);
								if(tecBomHead!=null){
									basBomColorRequest.setBomSid(tecBomHead.getBomSid());
								}
							}else{
								CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
								errMsgResponse.setItemNum(4);
								errMsgResponse.setMsg("成衣颜色“"+o+"”不存在，导入失败");
								msgList.add(errMsgResponse);
								break;
							}
							basBomColorRequest.setSku1Sid(basSku.getSkuSid())
									.setSku1Name(o);
							skuSidHeadList.add(basBomColorRequest);
							if (!skuList.get(m).getSkuName().equals(o)) {
								CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
								errMsgResponse.setItemNum(4);
								errMsgResponse.setMsg("表格中的成衣颜色的排序，与系统中的成衣颜色的排序不一致，导入失败！");
								msgList.add(errMsgResponse);
								break;
							}
						}
					}
					if (CollectionUtil.isNotEmpty(msgList)) {
						return AjaxResult.error("报错信息", msgList);
					}
					continue;
				}
				int num = i + 1;
				List<Object> objects = readAll.get(i);
				int length = readAll.get(3).size();
				Long materialSidItem = null;
				String materialName = null;
				String materialCodeItem = null;
				copyItem(objects, readAll);
				List<BasMaterialSkuResponse> skuSidItemList = new ArrayList<>();
				BomSortResponse tecBomItem = new BomSortResponse();
				if (objects.get(0) != null && objects.get(0) != "") {
					String itemNum = objects.get(0).toString();
					boolean match = JudgeFormat.isValidDouble(itemNum);
					if (!match) {
						CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
						errMsgResponse.setItemNum(num);
						errMsgResponse.setMsg("序号，数据格式错误，导入失败");
						msgList.add(errMsgResponse);
					}
				}
				if (objects.get(1) == null || objects.get(1) == "") {
					CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
					errMsgResponse.setItemNum(num);
					errMsgResponse.setMsg("物料编码不能为空，导入失败");
					msgList.add(errMsgResponse);
				} else {
					String code = objects.get(1).toString();
					BasMaterial basMaterial = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
							.eq(BasMaterial::getMaterialCode, code)
					);
					if (basMaterial == null) {
						CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
						errMsgResponse.setItemNum(num);
						errMsgResponse.setMsg("物料编码" + code + "不存在，导入失败");
						msgList.add(errMsgResponse);
					} else {
						if (!ConstantsEms.CHECK_STATUS.equals(basMaterial.getHandleStatus())
								|| !ConstantsEms.ENABLE_STATUS.equals(basMaterial.getStatus())) {
							CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
							errMsgResponse.setItemNum(num);
							errMsgResponse.setMsg("物料编码，必须是已确认且启用状态，导入失败");
							msgList.add(errMsgResponse);
						} else {
							materialSidItem = basMaterial.getMaterialSid();
							materialName = basMaterial.getMaterialName();
							materialCodeItem=basMaterial.getMaterialCode();
						}
					}
				}
				if (objects.get(3) != null && objects.get(3) != "") {
					String isMac = objects.get(3).toString();
					if (!isMac.equals("是")) {
						CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
						errMsgResponse.setItemNum(num);
						errMsgResponse.setMsg("是否主面料，配置错误，导入失败");
						msgList.add(errMsgResponse);
					}
				}

				for (int m = 0; m < length - 5; m++) {
					BasMaterialSkuResponse basBomColorRequest = new BasMaterialSkuResponse();
					//获取对应成衣的颜色
					basBomColorRequest.setSkuSid(skuSidHeadList.get(m).getSku1Sid());
					Object sku = objects.get(m + 5);
					if(materialCodeItem!=null){
						if (sku != null && sku != "") {
							String skuName = sku.toString();
							BasSku basSku = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
									.eq(BasSku::getSkuName, skuName)
							);
							if (basSku == null) {
								CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
								errMsgResponse.setItemNum(num);
								errMsgResponse.setMsg("物料编码" + materialCodeItem + "不存在物料颜色“" + skuName + "”，导入失败");
								msgList.add(errMsgResponse);
							} else {
								BasMaterialSku basMaterialSku = basMaterialSkuMapper.selectOne(new QueryWrapper<BasMaterialSku>().lambda()
										.eq(BasMaterialSku::getSkuSid, basSku.getSkuSid())
										.eq(BasMaterialSku::getMaterialSid,materialSidItem)
										.eq(BasMaterialSku::getSkuType, ConstantsEms.SKUTYP_YS)
								);
								if (basMaterialSku == null) {
									CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
									errMsgResponse.setItemNum(num);
									errMsgResponse.setMsg("物料编码" + materialCodeItem + "不存在物料颜色“" + skuName + "”，导入失败");
									msgList.add(errMsgResponse);
								} else {
									if (!ConstantsEms.ENABLE_STATUS.equals(basMaterialSku.getStatus())) {
										CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
										errMsgResponse.setItemNum(num);
										errMsgResponse.setMsg("物料编码" + materialCodeItem + "，物料颜色“" + skuName + "”不是启用状态，导入失败");
										msgList.add(errMsgResponse);
									} else {
										basBomColorRequest.setBomMaterialSku1Sid(basSku.getSkuSid());
									}
								}
							}
						}
						skuSidItemList.add(basBomColorRequest);

					}
				}
				if(materialSidItem!=null){
					BasMaterial basMaterial = basMaterialMapper.selectBasMaterialBomById(materialSidItem);
					BeanCopyUtils.copyProperties(basMaterial,tecBomItem);
				}
				tecBomItem.setBasMaterialSkuList1(skuSidItemList);
				tecBomItem.setMaterialSid(materialSidItem)
						.setBomMaterialSid(materialSidItem)
						.setRoundingType("SSWR")
						.setSort(objects.get(0)==null||objects.get(0)==""?null:objects.get(0).toString())
						.setIsMainFabric(objects.get(3)==null||objects.get(3)==""?null:"1")
						.setPositionName(objects.get(4)==null||objects.get(4)==""?null:objects.get(4).toString());
				tecBomItems.add(tecBomItem);
			}
			if (CollectionUtil.isNotEmpty(msgList)) {
				return AjaxResult.error("报错信息", msgList);
			}
			return AjaxResult.success(tecBomItems);
		} catch (BaseException e) {
			throw new BaseException(e.getDefaultMessage());
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
	//填充-明细表
	public void copyItem(List<Object> objects,List<List<Object>> readAll){
		//获取第三行的列数
		int size = readAll.get(3).size();
		//当前行的列数
		int lineSize = objects.size();
		ArrayList<Object> all = new ArrayList<>();
		for (int i=lineSize;i<size;i++){
			Object o = new Object();
			o=null;
			objects.add(o);
		}
	}
	public String removeZero(String s){
		if(s.indexOf(".") > 0){
			//正则表达
			s = s.replaceAll("0+?$", "");//去掉后面无用的零
			s = s.replaceAll("[.]$", "");//如小数点后面全是零则去掉小数点
		}
		return s;
	}
}
