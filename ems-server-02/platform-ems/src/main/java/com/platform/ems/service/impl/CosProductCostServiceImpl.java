package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.DateUtils;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.CheckUniqueCommonRequest;
import com.platform.ems.domain.dto.request.CosProductCostLaborRequest;
import com.platform.ems.domain.dto.request.CosProductCostMaterialRequest;
import com.platform.ems.domain.dto.request.OrderErrRequest;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.domain.dto.response.CosProductCostLaborResponse;
import com.platform.ems.domain.dto.response.CosProductCostMaterialResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.ConMeasureUnit;
import com.platform.ems.plug.domain.ConTaxRate;
import com.platform.ems.service.*;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * 商品成本核算主Service业务层处理
 *
 * @author qhq
 * @date 2021-04-02
 */
@Service
@SuppressWarnings("all")
public class CosProductCostServiceImpl extends ServiceImpl<CosProductCostMapper,CosProductCost>  implements ICosProductCostService {
    @Autowired
    private CosProductCostMapper cosProductCostMapper;
    @Autowired
    private CosProductCostMaterialMapper cosProductCostMaterialMapper;
    @Autowired
    private CosProductCostLaborMapper cosProductCostLaborMapper;
    @Autowired
    private CosProductCostAttachmentMapper cosProductCostAttachmentMapper;
    @Autowired
    private TecBomHeadMapper tecBomHeadMapper;
    @Autowired
    private TecBomItemMapper tecBomItemMapper;
    @Autowired
    private BasMaterialMapper basMaterialMapper;
    @Autowired
    private BasMaterialSkuMapper basMaterialSkuMapper;
    @Autowired
    private CosCostLaborTemplateMapper cosCostLaborTemplateMapper;
    @Autowired
    private CosCostLaborTemplateItemMapper cosCostLaborTemplateItemMapper;
    @Autowired
    private CosProductCostBomMapper cosProductCostBomMapper;
    @Autowired
    private PurPriceInforItemMapper purPriceInforItemMapper;
    @Autowired
	private CosProductCostLaborOtherMapper cosProductCostLaborOtherMapper;
    @Autowired
    private ISalSalePriceService salePriceService;
	@Autowired
	private  IPurPurchasePriceService purPurchasePriceService;
	@Autowired
	private SalSalePriceMapper salSalePriceMapper;
	@Autowired
	private SalSalePriceItemMapper salSalePriceItemMapper;
	@Autowired
	private PurPurchasePriceMapper  purPurchasePriceMapper;
	@Autowired
	private PurPurchasePriceItemMapper purPurchasePriceItemMapper;
	@Autowired
	private ITecBomHeadService iTecBomHeadService;
	@Autowired
	private PurPriceInforMapper purPriceInforMapper;
	@Autowired
	private BasCustomerMapper basCustomerMapper;
	@Autowired
	private BasVendorMapper basVendorMapper;
	@Autowired
	private PurOutsourcePurchasePriceMapper purOutsourcePurchasePriceMapper;
	@Autowired
	RedissonClient redissonClient;
	private static final String LOCK_KEY = "COST_PRODUCT";
	private static final String TITLE = "成本核算";
	private static final String TABLE = "s_cos_product_cost";
	@Autowired
	private ISysFormProcessService formProcessService;
	@Autowired
	private ISystemUserService userService;
	@Autowired
	private BasSkuMapper basSkuMapper;
	@Autowired
	private PayProductProcessStepItemMapper  payProductProcessStepItemMapper;
	@Autowired
	private PayProductProcessStepMapper  payProductProcessStepMapper;
	@Autowired
	private SysTodoTaskMapper sysTodoTaskMapper;
	@Autowired
	private PurOutsourcePriceInforMapper purOutsourcePriceInforMapper;
	@Autowired
	private PurOutsourcePriceInforItemMapper purOutsourcePriceInforItemMapper;
    /**
     * 根据商品SID查询商品成本核算主
     *
     * @param materialSid 商品sid
     * @return 商品成本核算主
     */
    @Override
	public CosProductCost selectCosProductCostById(Long productCostSid) {
		CosProductCost cost = cosProductCostMapper.selectCosProductCostById(productCostSid);
		//商品bom信息
		List<CosProductCostMaterial> bomList = cosProductCostMaterialMapper.selectCosProductCostMaterialById(productCostSid);
		bomList.parallelStream().forEach(bom -> {
			if (bom.getLossRate() != null) {
				bom.setLossRate(bom.getLossRate().multiply(new BigDecimal(100)));
			}
			if (bom.getQuoteLossRate() != null) {
				bom.setQuoteLossRate(bom.getQuoteLossRate().multiply(new BigDecimal(100)));
			}
			if (bom.getConfirmLossRate() != null) {
				bom.setConfirmLossRate(bom.getConfirmLossRate().multiply(new BigDecimal(100)));
			}
			if (bom.getCheckLossRate() != null) {
				bom.setCheckLossRate(bom.getCheckLossRate().multiply(new BigDecimal(100)));
			}
		});
		cost.setCostMaterialList(bomList);
		//工价成本明细
		CosProductCostLabor cosProductCostLabor = new CosProductCostLabor();
		cosProductCostLabor.setProductCostSid(cost.getProductCostSid());
		List<CosProductCostLabor> cosProductCostLabors = cosProductCostLaborMapper.selectCosProductCostLaborList(cosProductCostLabor);
		if (CollectionUtil.isNotEmpty(cosProductCostLabors)) {
			cosProductCostLabors.forEach(li -> {
				List<CosProductCostLaborOther> listOther = cosProductCostLaborOtherMapper.getByProductCostSid(li.getProductCostLaborSid());
				li.setLaborItemOtherList(listOther);
			});
		}
		cost.setCostLaborList(cosProductCostLabors);
		//附件
		CosProductCostAttachment cosProductCostAttachment = new CosProductCostAttachment();
		cosProductCostAttachment.setProductCostSid(cost.getProductCostSid());
		List<CosProductCostAttachment> cosProductCostAttachments = cosProductCostAttachmentMapper.selectCosProductCostAttachmentList(cosProductCostAttachment);
		if (CollectionUtil.isNotEmpty(cosProductCostAttachments)) {
			cost.setCostAttachmentList(cosProductCostAttachments);
		}
		MongodbUtil.find(cost);
		return cost;
	}
	/**
	 * 查询商品成本核算主列表
	 *
	 * @param cosProductCost 商品成本核算主
	 * @return 商品成本核算主
	 */
	@Override
	public List<CosProductCost> selectCosProductCostList(CosProductCost cosProductCost) {
		List<CosProductCost> list = cosProductCostMapper.selectCosProductCostList(cosProductCost);
		return list;
	}

	@Override
	public List<CosProductCostMaterialResponse> reportMaterialList(CosProductCostMaterialRequest cosProductCostMaterialRequest){
		List<CosProductCostMaterialResponse> list = cosProductCostMaterialMapper.reportMaterialList(cosProductCostMaterialRequest);
		return list;
	}
	@Override
	public List<CosProductCostLaborResponse> reportProductCostLabor(CosProductCostLaborRequest cosProductCostLaborRequest){
		return cosProductCostLaborMapper.reportProductCostLabor(cosProductCostLaborRequest);
	}
    /**
     * 新增商品成本核算主
     * 需要注意编码重复校验
     * @param cosProductCost 商品成本核算主
     * @return 结果
     */
    @Override
	@Transactional(rollbackFor = Exception.class)
	public int insertCosProductCost(CosProductCost cost) {
		int code = 0;
		RLock lock = redissonClient.getLock(LOCK_KEY);
		lock.lock(15L, TimeUnit.SECONDS);
		try {
			Long materialSid = cost.getMaterialSid();
			BasMaterial basMaterial = basMaterialMapper.selectById(materialSid);
			judgeExit(cost);
			if(basMaterial.getCustomerSid()!=null&&cost.getCustomerSid()!=null){
					if(cost.getCustomerSid().toString().equals(basMaterial.getCustomerSid().toString())){
						basMaterial.setIsHasCreatedProductcost(ConstantsEms.YES);
						basMaterialMapper.updateById(basMaterial);
					}
			}
			if (ConstantsEms.PRICE_K.equals(cost.getPriceDimension())) {
				cost.setSku1Sid(null);
			}
			int row = code = cosProductCostMapper.insert(cost);
			//BOM物料清单
			CosProductCostBom cosProductCostBom = new CosProductCostBom();
			cosProductCostBom.
					 setBomSid(cost.getBomSid())
					.setProductCostSid(cost.getProductCostSid())
					.setSku1Sid(cost.getBomHeadSkuSid())
					.setMaterialSid(cost.getMaterialSid());
			cosProductCostBomMapper.insert(cosProductCostBom);
			cost.getCostMaterialList().forEach(bomitem -> {
				if (bomitem.getLossRate() != null) {
					bomitem.setLossRate(bomitem.getLossRate().divide(new BigDecimal(100)));
				}
				if (bomitem.getQuoteLossRate() != null) {
					bomitem.setQuoteLossRate(bomitem.getQuoteLossRate().divide(new BigDecimal(100)));
				}
				if (bomitem.getConfirmLossRate() != null) {
					bomitem.setConfirmLossRate(bomitem.getConfirmLossRate().divide(new BigDecimal(100)));
				}
				if (bomitem.getCheckLossRate() != null) {
					bomitem.setCheckLossRate(bomitem.getCheckLossRate().divide(new BigDecimal(100)));
				}
				bomitem.setProductCostBomSid(cosProductCostBom.getProductCostBomSid());
				cosProductCostMaterialMapper.insert(bomitem);
			});

			//工价费用
			if (CollectionUtils.isNotEmpty(cost.getCostLaborList())) {
				cost.getCostLaborList().forEach(labor -> {
					labor.setProductCostSid(cost.getProductCostSid());
					labor.setProductCostLaborSid(IdWorker.getId());
					cosProductCostLaborMapper.insert(labor);
					List<CosProductCostLaborOther> laborItemOtherList = labor.getLaborItemOtherList();
					//工价项其他
					if (CollectionUtils.isNotEmpty(laborItemOtherList)) {
						laborItemOtherList.forEach(laborOther -> {
							laborOther.setProductCostLaborOtherSid(null);
							laborOther.setProductCostLaborSid(labor.getProductCostLaborSid());
							laborOther.setProductCostSid(cost.getProductCostSid());
							cosProductCostLaborOtherMapper.insert(laborOther);
						});
					}
				});
			}
			//附件列表
			if (CollectionUtils.isNotEmpty(cost.getCostAttachmentList())) {
				cost.getCostAttachmentList().forEach(att -> {
					att.setProductCostSid(cost.getProductCostSid());
					cosProductCostAttachmentMapper.insert(att);
				});
			}
			SysTodoTask sysTodoTask = new SysTodoTask();
			if (ConstantsEms.SAVA_STATUS.equals(cost.getHandleStatus())) {
				sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
						.setTableName(TABLE)
						.setTitle(TITLE)
						.setDocumentSid(cost.getProductCostSid());
				List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
				String co=cost.getMaterialCode()!=null?cost.getMaterialCode():cost.getSampleCodeSelf();
				if (CollectionUtil.isEmpty(sysTodoTaskList)) {
					sysTodoTask.setTitle("商品/样品为"+co+"的成本核算当前是保存状态，请及时处理！")
							.setNoticeDate(new Date())
							.setUserId(ApiThreadLocalUtil.get().getUserid());
					sysTodoTaskMapper.insert(sysTodoTask);
				}
			}
			if (row > 0) {
				MongodbUtil.insertUserLog(cost.getProductCostSid(), BusinessType.INSERT.getValue(), TITLE);
			}
		} catch (CustomException e) {
			throw new CustomException(e.getMessage());
		} finally {
			lock.unlock();
		}
		return code;
	}
	/**
	 * 协议价回写到销售价
	 */
	public void insertSale(CosProductCost cost){
		String skipInsert = cost.getSkipInsert();
		String handleStatus = cost.getHandleStatus();
	 	 Boolean exit=true;
		 SalSalePrice salSalePrice = null;
		 List<SalSalePrice> salSalePrices=null;
		 List<Long>   salSalePriceSids=null;
		 String priceDimension = cost.getPriceDimension();
		 //按款
		 if (ConstantsEms.PRICE_K.equals(priceDimension)) {
			 salSalePrices = salSalePriceMapper.selectList(new QueryWrapper<SalSalePrice>().lambda()
					 .eq(SalSalePrice::getMaterialSid, cost.getMaterialSid())
					 .eq(SalSalePrice::getRawMaterialMode, cost.getRawMaterialMode())
					 .eq(SalSalePrice::getCustomerSid, cost.getCustomerSid())
					 .eq(SalSalePrice::getHandleStatus,ConstantsEms.CHECK_STATUS)
					 .eq(SalSalePrice::getSaleMode, ConstantsEms.DOCUMNET_TYPE_ZG)
			 );
		 }else{
		 	//按色
			 salSalePrices = salSalePriceMapper.selectList(new QueryWrapper<SalSalePrice>().lambda()
					 .eq(SalSalePrice::getMaterialSid, cost.getMaterialSid())
					 .eq(SalSalePrice::getRawMaterialMode, cost.getRawMaterialMode())
					 .eq(SalSalePrice::getCustomerSid, cost.getCustomerSid())
					 .eq(SalSalePrice::getHandleStatus,ConstantsEms.CHECK_STATUS)
					 .eq(SalSalePrice::getPriceDimension, cost.getPriceDimension())
					 .eq(SalSalePrice::getSaleMode, ConstantsEms.DOCUMNET_TYPE_ZG)
					 .eq(SalSalePrice::getSku1Sid,cost.getSku1Sid())
			 );
			 if(CollectionUtil.isEmpty(salSalePrices)){
				 salSalePrices = salSalePriceMapper.selectList(new QueryWrapper<SalSalePrice>().lambda()
						 .eq(SalSalePrice::getMaterialSid, cost.getMaterialSid())
						 .eq(SalSalePrice::getHandleStatus,ConstantsEms.CHECK_STATUS)
						 .eq(SalSalePrice::getRawMaterialMode, cost.getRawMaterialMode())
						 .eq(SalSalePrice::getCustomerSid, cost.getCustomerSid())
						 .eq(SalSalePrice::getSaleMode, ConstantsEms.DOCUMNET_TYPE_ZG)
						 .eq(SalSalePrice::getPriceDimension, ConstantsEms.PRICE_K)
						);
			 }
		 }
		 if (CollectionUtils.isNotEmpty(salSalePrices)) {
			 salSalePriceSids= salSalePrices.stream().map(o -> o.getSalePriceSid()).collect(Collectors.toList());
		 }
		 Date date = new Date();
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		 String start = sdf.format(date);
		 if (CollectionUtil.isNotEmpty(salSalePriceSids)) {
			 List<SalSalePriceItem> salSalePriceItems = salSalePriceItemMapper.selectList(new QueryWrapper<SalSalePriceItem>()
					 .lambda().in(SalSalePriceItem::getSalePriceSid, salSalePriceSids)
					 .eq(SalSalePriceItem::getHandleStatus,ConstantsEms.CHECK_STATUS)
			 );
			 if(CollectionUtil.isNotEmpty(salSalePriceItems)){
				 int max = -1;
				 //设置有效期至
				 String dateStr = "9999-12-31";
				 Date endTime = DateUtil.parse(dateStr);
				 //设置有效期 起
				 DateTime startTime = DateUtil.offsetDay(DateUtil.parse(start), 1);
				 Optional<SalSalePriceItem> optiona = salSalePriceItems.stream().max(Comparator.comparingLong(li -> li.getEndDate().getTime()));
				 //最大的有效期明细
				 SalSalePriceItem item = optiona.get();
				 SalSalePrice salePriceMax= salSalePriceMapper.selectById(item.getSalePriceSid());
				 //判断新价格信息的“有效期（起）”是否大于旧的价格信息的最大的“有效期（至）”，如是，则直接写入新的价格
				 if (item.getEndDate().getTime() < startTime.getTime()) {
					 if(!ConstantsEms.YES.equals(skipInsert)){
						 if(salePriceMax.getPriceDimension().equals(priceDimension)){
							 List<SalSalePriceItem> salSalePriceItemMaxs = salSalePriceItemMapper.selectList(new QueryWrapper<SalSalePriceItem>()
									 .lambda().eq(SalSalePriceItem::getSalePriceSid, item.getSalePriceSid())
							 );
							 int maxItem = salSalePriceItemMaxs.stream().mapToInt(li -> li.getItemNum()).max().getAsInt();
							 max=maxItem+1;
							 SalSalePriceItem salSalePriceItem = new SalSalePriceItem();
							 salSalePriceItem.setSalePriceSid(item.getSalePriceSid())
									 .setSalePriceTax(cost.getPriceTax())
									 .setTaxRate(cost.getTaxRate())
									 .setUnitBase(cost.getUnitBase())
									 .setUnitPrice(cost.getUnitPrice())
									 .setStartDate(startTime)
									 .setItemNum(max)
									 .setIsRecursionPrice(ConstantsEms.NO)
									 .setUnitConversionRate(BigDecimal.ONE)
									 .setSalePrice(cost.getPriceTax().divide(BigDecimal.ONE.add(cost.getTaxRate()),6,BigDecimal.ROUND_HALF_UP))
									 .setHandleStatus(ConstantsEms.CHECK_STATUS)
									 .setPriceEnterMode(ConstantsEms.ENTER_MODEL_TAX)
									 .setCurrency(cost.getCurrency())
									 .setCurrencyUnit(cost.getCurrencyUnit())
									 .setEndDate(endTime);
							 MongodbUtil.insertApprovalLogAddNum(item.getSalePriceSid(), BusinessType.CHECK.getValue(), "来自销售成本核算",maxItem+1);
							 salSalePriceItemMapper.insert(salSalePriceItem);
							 SalSalePrice price = new SalSalePrice();
							 price.setSalePriceSid(item.getSalePriceSid())
									 .setHandleStatus(ConstantsEms.CHECK_STATUS);
							 salSalePriceMapper.updateById(price);
							 return;
						 }else{
						 	if(!ConstantsEms.YES.equals(skipInsert)){
								//插入新的销售价
								insertCostSale(cost);
							}
							 return;
						 }
					 }
				 }
				 //判断系统中是否存在跟新价格信息的“有效期（起）”和“有效期（至）”一样的有效期
				 for (int i = 0; i < salSalePriceItems.size(); i++) {
					 if (salSalePriceItems.get(i).getStartDate().getTime() == startTime.getTime() && salSalePriceItems.get(i).getEndDate().getTime() == endTime.getTime()) {
						 salSalePriceItems.get(i).setSalePriceTax(cost.getPriceTax());
						 salSalePriceItems.get(i).setSalePrice(cost.getPriceTax().divide(BigDecimal.ONE.add(cost.getTaxRate()),6,BigDecimal.ROUND_HALF_UP));
                         salSalePriceItems.get(i).setTaxRate(cost.getTaxRate());
						 SalSalePrice salePrice = salSalePriceMapper.selectById(salSalePriceItems.get(i).getSalePriceSid());
						 String dimension =salePrice.getPriceDimension();
						 if(dimension.equals(priceDimension)){
							 if(!ConstantsEms.YES.equals(skipInsert)){
								 salSalePriceItemMapper.updateById(salSalePriceItems.get(i));
								 MongodbUtil.insertApprovalLogAddNum(salSalePriceItems.get(i).getSalePriceSid(), BusinessType.PRICE.getValue(), "来自销售成本核算",salSalePriceItems.get(i).getItemNum());
							 }
							 return;
						 }else{
							 String dimensionMsg=dimension.equals(ConstantsEms.PRICE_K1)?"按色的":"按款的";
							 throw new CustomException(cost.getMaterialName() + "当前存在" +  dimensionMsg + "销售价"+ salePrice.getSalePriceCode() + "的有效期与此成本核算单的有效期区间存在交集，请先手工更新旧的有效期后，再进行此操作。");
						 }
					 }
				 }
				 String dimensionMsg=salePriceMax.getPriceDimension().equals(ConstantsEms.PRICE_K1)?"按色的":"按款的";
				 //判断新价格信息的“有效期（起）”是否比旧的价格的最大的“有效期（起）”大且新的价格信息的“有效期（至）”是否比旧的价格的最大的“有效期（至）”大或是相等，如是，将最大的旧的价格的“有效期（至）”改成“新的有效期（起）-1”，
				 if (item.getStartDate().getTime() < startTime.getTime() && item.getEndDate().getTime() <= endTime.getTime()) {
					 if(salePriceMax.getPriceDimension().equals(priceDimension)){
						 item.setEndDate(DateUtil.offsetDay(startTime, -1));
						 if(!ConstantsEms.YES.equals(skipInsert)){
							 SalSalePrice price = new SalSalePrice();
							 price.setSalePriceSid(item.getSalePriceSid())
									 .setHandleStatus(ConstantsEms.CHECK_STATUS);
							 salSalePriceMapper.updateById(price);
							 salSalePriceItemMapper.updateById(item);
							 MongodbUtil.insertApprovalLogAddNum(item.getSalePriceSid(), BusinessType.CHANGE.getValue(), "更新有效期至",item.getItemNum());
							 //插入一笔新的明细
							 List<SalSalePriceItem> salSalePriceItemMaxs = salSalePriceItemMapper.selectList(new QueryWrapper<SalSalePriceItem>()
									 .lambda().eq(SalSalePriceItem::getSalePriceSid, item.getSalePriceSid())
							 );
							 int maxItem = salSalePriceItemMaxs.stream().mapToInt(li -> li.getItemNum()).max().getAsInt();
							 max=maxItem+1;
							 SalSalePriceItem salSalePriceItem = new SalSalePriceItem();
							 salSalePriceItem.setSalePriceSid(item.getSalePriceSid())
									 .setSalePriceTax(cost.getPriceTax())
									 .setTaxRate(cost.getTaxRate())
									 .setUnitBase(cost.getUnitBase())
									 .setUnitPrice(cost.getUnitPrice())
									 .setStartDate(startTime)
									 .setItemNum(max)
									 .setIsRecursionPrice(ConstantsEms.NO)
									 .setUnitConversionRate(BigDecimal.ONE)
									 .setSalePrice(cost.getPriceTax().divide(BigDecimal.ONE.add(cost.getTaxRate()),6,BigDecimal.ROUND_HALF_UP))
									 .setHandleStatus(ConstantsEms.CHECK_STATUS)
									 .setPriceEnterMode(ConstantsEms.ENTER_MODEL_TAX)
									 .setCurrency(cost.getCurrency())
									 .setCurrencyUnit(cost.getCurrencyUnit())
									 .setEndDate(endTime);
							 MongodbUtil.insertApprovalLogAddNum(item.getSalePriceSid(), BusinessType.CHECK.getValue(), "来自销售成本核算",maxItem+1);
							 salSalePriceItemMapper.insert(salSalePriceItem);
						 }
						 return;
					 }else{
						 throw new CustomException(cost.getMaterialName() + "当前存在" +  dimensionMsg + "销售价"+ salePriceMax.getSalePriceCode() + "的有效期与此成本核算单的有效期区间存在交集，请先手工更新旧的有效期后，再进行此操作。");
					 }
				 }else{
					 throw new CustomException(cost.getMaterialName() + "当前存在" +  dimensionMsg + "销售价"+ salePriceMax.getSalePriceCode() + "的有效期与此成本核算单的有效期区间存在交集，请先手工更新旧的有效期后，再进行此操作。");
				 }
			 }else{
				 if(!ConstantsEms.YES.equals(skipInsert)){
					 //插入新的销售价
					 insertCostSale(cost);
				 }
			 }
		 } else {
			 if(!ConstantsEms.YES.equals(skipInsert)){
			 	//插入新的销售价
				 insertCostSale(cost);
			 }
		 }
	}

	//插入到采购价 主表明细表
	public void insertCostSale(CosProductCost cost){
		SalSalePrice salSale = new SalSalePrice();
		salSale.setMaterialSid(cost.getMaterialSid())
				.setMaterialCode(cost.getMaterialCode())
				.setHandleStatus(ConstantsEms.CHECK_STATUS)
				.setCompanySid(cost.getCompanySid())
				.setRawMaterialMode(cost.getRawMaterialMode())
				.setCustomerSid(cost.getCustomerSid())
				.setPriceDimension(cost.getPriceDimension())
				.setSaleMode(cost.getBusinessMode())
				.setStatus(ConstantsEms.SAVA_STATUS)
				.setSku1Sid(cost.getSku1Sid());
		SalSalePriceItem salSalePriceItem = new SalSalePriceItem();
		Calendar calendar = Calendar.getInstance();
		//设置有效期至
		String dateStr = "9999-12-31";
		Date endTime = DateUtil.parse(dateStr);
		//设置有效期 起
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String start = sdf.format(date);
		DateTime startTime = DateUtil.offsetDay(DateUtil.parse(start), 1);
		salSalePriceItem .setSalePriceTax(cost.getPriceTax())
				.setTaxRate(cost.getTaxRate())
				.setStartDate(startTime)
				.setUnitBase(cost.getUnitBase())
				.setUnitPrice(cost.getUnitPrice())
				.setIsRecursionPrice(ConstantsEms.NO)
				.setUnitConversionRate(BigDecimal.ONE)
				.setSalePrice(cost.getPriceTax().divide(BigDecimal.ONE.add(cost.getTaxRate()),6,BigDecimal.ROUND_HALF_UP))
				.setItemNum(1)
				.setHandleStatus(ConstantsEms.CHECK_STATUS)
				.setCurrency(cost.getCurrency())
				.setPriceEnterMode(ConstantsEms.ENTER_MODEL_TAX)
				.setCurrencyUnit(cost.getCurrencyUnit())
				.setEndDate(endTime);
		salSalePriceMapper.insert(salSale);
		salSalePriceItem.setSalePriceSid(salSale.getSalePriceSid());
		salSalePriceItemMapper.insert(salSalePriceItem);
		MongodbUtil.insertApprovalLogAddNum(salSale.getSalePriceSid(), BusinessType.CHECK.getValue(), "来自销售成本核算",1);
	}
	/**
	 * 协议价回写到采购价
	 *
	 */
	public void insertPurchase(CosProductCost cost){
		String skipInsert = cost.getSkipInsert();
		String handleStatus = cost.getHandleStatus();
		Boolean exit=true;
		List<PurPurchasePrice> purPurchasePrices=null;
		List<Long>   purPurchasePricesids=null;
		String priceDimension = cost.getPriceDimension();
		//按款
		if (ConstantsEms.PRICE_K.equals(priceDimension)) {
			purPurchasePrices = purPurchasePriceMapper.selectList(new QueryWrapper<PurPurchasePrice>().lambda()
					.eq(PurPurchasePrice::getMaterialSid, cost.getMaterialSid())
					.eq(PurPurchasePrice::getRawMaterialMode, cost.getRawMaterialMode())
					.eq(PurPurchasePrice::getVendorSid, cost.getVendorSid())
					.eq(PurPurchasePrice::getHandleStatus,ConstantsEms.CHECK_STATUS)
					.eq(PurPurchasePrice::getPurchaseMode, ConstantsEms.DOCUMNET_TYPE_ZG)
			);
		}else{
			//按色
			purPurchasePrices = purPurchasePriceMapper.selectList(new QueryWrapper<PurPurchasePrice>().lambda()
					.eq(PurPurchasePrice::getMaterialSid, cost.getMaterialSid())
					.eq(PurPurchasePrice::getRawMaterialMode, cost.getRawMaterialMode())
					.eq(PurPurchasePrice::getPurchaseMode, ConstantsEms.DOCUMNET_TYPE_ZG)
					.eq(PurPurchasePrice::getHandleStatus,ConstantsEms.CHECK_STATUS)
					.eq(PurPurchasePrice::getPriceDimension, cost.getPriceDimension())
					.eq(PurPurchasePrice::getVendorSid, cost.getVendorSid())
					.eq(PurPurchasePrice::getSku1Sid,cost.getSku1Sid())
			);
			if(CollectionUtil.isEmpty(purPurchasePrices)){
				purPurchasePrices = purPurchasePriceMapper.selectList(new QueryWrapper<PurPurchasePrice>().lambda()
						.eq(PurPurchasePrice::getMaterialSid, cost.getMaterialSid())
						.eq(PurPurchasePrice::getHandleStatus,ConstantsEms.CHECK_STATUS)
						.eq(PurPurchasePrice::getRawMaterialMode, cost.getRawMaterialMode())
						.eq(PurPurchasePrice::getVendorSid, cost.getVendorSid())
						.eq(PurPurchasePrice::getPurchaseMode, ConstantsEms.DOCUMNET_TYPE_ZG)
						.eq(PurPurchasePrice::getPriceDimension, ConstantsEms.PRICE_K)
				);
			}
		}
		if (CollectionUtils.isNotEmpty(purPurchasePrices)) {
			purPurchasePricesids= purPurchasePrices.stream().map(o -> o.getPurchasePriceSid()).collect(Collectors.toList());
		}
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String start = sdf.format(date);
		if (CollectionUtil.isNotEmpty(purPurchasePricesids)) {
			List<PurPurchasePriceItem> purPurchasePriceItems = purPurchasePriceItemMapper.selectList(new QueryWrapper<PurPurchasePriceItem>()
					.lambda().in(PurPurchasePriceItem::getPurchasePriceSid, purPurchasePricesids)
					.eq(PurPurchasePriceItem::getHandleStatus,ConstantsEms.CHECK_STATUS)
			);
			if(CollectionUtil.isNotEmpty(purPurchasePriceItems)){
				int max = -1;
				//设置有效期至
				String dateStr = "9999-12-31";
				Date endTime = DateUtil.parse(dateStr);
				//设置有效期 起
				DateTime startTime = DateUtil.offsetDay(DateUtil.parse(start), 1);
				Optional<PurPurchasePriceItem> optiona = purPurchasePriceItems.stream().max(Comparator.comparingLong(li -> li.getEndDate().getTime()));
				//最大的有效期明细
				PurPurchasePriceItem item = optiona.get();
				PurPurchasePrice purPurchasePriceMax= purPurchasePriceMapper.selectById(item.getPurchasePriceSid());
				//判断新价格信息的“有效期（起）”是否大于旧的价格信息的最大的“有效期（至）”，如是，则直接写入新的价格
				if (item.getEndDate().getTime() < startTime.getTime()) {
					if(!ConstantsEms.YES.equals(skipInsert)){
						if(purPurchasePriceMax.getPriceDimension().equals(priceDimension)){
							List<PurPurchasePriceItem> purPurchasePriceItemMaxs = purPurchasePriceItemMapper.selectList(new QueryWrapper<PurPurchasePriceItem>()
									.lambda().eq(PurPurchasePriceItem::getPurchasePriceSid, item.getPurchasePriceSid())
							);
							int maxItem = purPurchasePriceItemMaxs.stream().mapToInt(li -> li.getItemNum()).max().getAsInt();
							max=maxItem+1;
							PurPurchasePriceItem purPurchasePriceItem = new PurPurchasePriceItem();
							purPurchasePriceItem.setPurchasePriceSid(item.getPurchasePriceSid())
									.setItemNum(max)
									.setPurchasePrice(cost.getPriceTax().divide(BigDecimal.ONE.add(cost.getTaxRate()),6,BigDecimal.ROUND_HALF_UP))
									.setPurchasePriceTax(cost.getPriceTax())
									.setHandleStatus(ConstantsEms.CHECK_STATUS)
									.setTaxRate(cost.getTaxRate())
									.setUnitBase(cost.getUnitBase())
									.setUnitPrice(cost.getUnitPrice())
									.setStartDate(startTime)
									.setIsRecursionPrice(ConstantsEms.NO)
									.setUnitConversionRate(BigDecimal.ONE)
									.setPriceEnterMode(ConstantsEms.ENTER_MODEL_TAX)
									.setCurrency(cost.getCurrency())
									.setCurrencyUnit(cost.getCurrencyUnit())
									.setEndDate(endTime);
							MongodbUtil.insertApprovalLogAddNum(item.getPurchasePriceSid(), BusinessType.CHECK.getValue(), "来自采购价",maxItem+1);
							purPurchasePriceItemMapper.insert(purPurchasePriceItem);
							PurPurchasePrice price = new PurPurchasePrice();
							price.setPurchasePriceSid(item.getPurchasePriceSid())
									.setHandleStatus(ConstantsEms.CHECK_STATUS);
							purPurchasePriceMapper.updateById(price);
							return;
						}else{
							//插入采购价
							insertCostPurchase(cost);
							return;
						}
					}
				}
				//判断系统中是否存在跟新价格信息的“有效期（起）”和“有效期（至）”一样的有效期
				for (int i = 0; i < purPurchasePriceItems.size(); i++) {
					if (purPurchasePriceItems.get(i).getStartDate().getTime() == startTime.getTime() && purPurchasePriceItems.get(i).getEndDate().getTime() == endTime.getTime()) {
						purPurchasePriceItems.get(i).setPurchasePriceTax(cost.getPriceTax());
						purPurchasePriceItems.get(i).setTaxRate(cost.getTaxRate());
						purPurchasePriceItems.get(i).setPurchasePrice(cost.getPriceTax().divide(BigDecimal.ONE.add(cost.getTaxRate()),6,BigDecimal.ROUND_HALF_UP));
						PurPurchasePrice purchasePrice = purPurchasePriceMapper.selectById(purPurchasePriceItems.get(i).getPurchasePriceSid());
						String dimension =purchasePrice.getPriceDimension();
						if(dimension.equals(priceDimension)){
							if(!ConstantsEms.YES.equals(skipInsert)){
								purPurchasePriceItemMapper.updateById(purPurchasePriceItems.get(i));
								MongodbUtil.insertApprovalLogAddNum(purPurchasePriceItems.get(i).getPurchasePriceSid(), BusinessType.PRICE.getValue(), "来自采购成本核算",purPurchasePriceItems.get(i).getItemNum());
							}
							return;
						}else{
							String dimensionMsg=dimension.equals(ConstantsEms.PRICE_K1)?"按色的":"按款的";
							throw new CustomException(cost.getMaterialName() + "当前存在" +  dimensionMsg + "采购价"+ purchasePrice.getPurchasePriceCode() + "的有效期与此成本核算单的有效期区间存在交集，请先手工更新旧的有效期后，再进行此操作。");
						}
					}
				}
				String dimensionMsg=purPurchasePriceMax.getPriceDimension().equals(ConstantsEms.PRICE_K1)?"按色的":"按款的";
				//判断新价格信息的“有效期（起）”是否比旧的价格的最大的“有效期（起）”大且新的价格信息的“有效期（至）”是否比旧的价格的最大的“有效期（至）”大或是相等，如是，将最大的旧的价格的“有效期（至）”改成“新的有效期（起）-1”，
				if (item.getStartDate().getTime() < startTime.getTime() && item.getEndDate().getTime() <= endTime.getTime()) {
					if(purPurchasePriceMax.getPriceDimension().equals(priceDimension)){
						item.setEndDate(DateUtil.offsetDay(startTime, -1));
						if(!ConstantsEms.YES.equals(skipInsert)){
							PurPurchasePrice price = new PurPurchasePrice();
							price.setPurchasePriceSid(item.getPurchasePriceSid())
									.setHandleStatus(ConstantsEms.CHECK_STATUS);
							purPurchasePriceMapper.updateById(price);
							purPurchasePriceItemMapper.updateById(item);
							MongodbUtil.insertApprovalLogAddNum(item.getPurchasePriceSid(), BusinessType.CHANGE.getValue(), "更新有效期至",item.getItemNum());
							//插入一笔新的明细
							List<PurPurchasePriceItem> purPurchasePriceItemMaxs = purPurchasePriceItemMapper.selectList(new QueryWrapper<PurPurchasePriceItem>()
									.lambda().eq(PurPurchasePriceItem::getPurchasePriceSid, item.getPurchasePriceSid())
							);
							int maxItem = purPurchasePriceItemMaxs.stream().mapToInt(li -> li.getItemNum()).max().getAsInt();
							max=maxItem+1;
							PurPurchasePriceItem purPurchasePriceItem = new PurPurchasePriceItem();
							purPurchasePriceItem.setPurchasePriceSid(item.getPurchasePriceSid())
									.setItemNum(max)
									.setPurchasePrice(cost.getPriceTax().divide(BigDecimal.ONE.add(cost.getTaxRate()),6,BigDecimal.ROUND_HALF_UP))
									.setHandleStatus(ConstantsEms.CHECK_STATUS)
									.setPurchasePriceTax(cost.getPriceTax())
									.setTaxRate(cost.getTaxRate())
									.setUnitBase(cost.getUnitBase())
									.setUnitPrice(cost.getUnitPrice())
									.setStartDate(startTime)
									.setIsRecursionPrice(ConstantsEms.NO)
									.setUnitConversionRate(BigDecimal.ONE)
									.setPriceEnterMode(ConstantsEms.ENTER_MODEL_TAX)
									.setCurrency(cost.getCurrency())
									.setCurrencyUnit(cost.getCurrencyUnit())
									.setEndDate(endTime);
							MongodbUtil.insertApprovalLogAddNum(item.getPurchasePriceSid(), BusinessType.CHECK.getValue(), "来自采购成本核算",maxItem+1);
							purPurchasePriceItemMapper.insert(purPurchasePriceItem);
						}
						return;
					}else{
						throw new CustomException(cost.getMaterialName() + "当前存在" +  dimensionMsg + "采购价"+ purPurchasePriceMax.getPurchasePriceCode() + "的有效期与此成本核算单的有效期区间存在交集，请先手工更新旧的有效期后，再进行此操作。");
					}
				}else{
					throw new CustomException(cost.getMaterialName() + "当前存在" +  dimensionMsg + "采购价"+ purPurchasePriceMax.getPurchasePriceCode() + "的有效期与此成本核算单的有效期区间存在交集，请先手工更新旧的有效期后，再进行此操作。");
				}
			}else{
				if(!ConstantsEms.YES.equals(skipInsert)){
					insertCostPurchase(cost);
				}
			}
		} else {
			if(!ConstantsEms.YES.equals(skipInsert)){
				insertCostPurchase(cost);
			}
		}
	}
   //插入采购价
	public void insertCostPurchase(CosProductCost cost){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String start = sdf.format(date);
		PurPurchasePrice purchasePrice = new PurPurchasePrice();
		purchasePrice.setMaterialSid(cost.getMaterialSid())
				.setMaterialCode(cost.getMaterialCode())
				.setHandleStatus(ConstantsEms.CHECK_STATUS)
				.setCompanySid(cost.getCompanySid())
				.setRawMaterialMode(cost.getRawMaterialMode())
				.setVendorSid(cost.getVendorSid())
				.setPriceDimension(cost.getPriceDimension())
				.setPurchaseMode(cost.getBusinessMode())
				.setStatus(ConstantsEms.ENABLE_STATUS)
				.setSku1Sid(cost.getSku1Sid());
		Calendar calendar = Calendar.getInstance();
		//设置有效期至
		String dateStr = "9999-12-31";
		Date endTime = DateUtil.parse(dateStr);
		//设置有效期 起
		DateTime startTime = DateUtil.offsetDay(DateUtil.parse(start), 1);
		PurPurchasePriceItem purPurchasePriceItem = new PurPurchasePriceItem();
		purPurchasePriceItem
				.setItemNum(1)
				.setPurchasePrice(cost.getPriceTax().divide(BigDecimal.ONE.add(cost.getTaxRate()),6,BigDecimal.ROUND_HALF_UP))
				.setHandleStatus(ConstantsEms.CHECK_STATUS)
				.setPurchasePriceTax(cost.getPriceTax())
				.setTaxRate(cost.getTaxRate())
				.setUnitBase(cost.getUnitBase())
				.setUnitPrice(cost.getUnitPrice())
				.setStartDate(startTime)
				.setIsRecursionPrice(ConstantsEms.NO)
				.setUnitConversionRate(BigDecimal.ONE)
				.setPriceEnterMode(ConstantsEms.ENTER_MODEL_TAX)
				.setCurrency(cost.getCurrency())
				.setCurrencyUnit(cost.getCurrencyUnit())
				.setEndDate(endTime);
		//插入采购价信息
		purPurchasePriceMapper.insert(purchasePrice);
		purPurchasePriceItem.setPurchasePriceSid(purchasePrice.getPurchasePriceSid());
		purPurchasePriceItemMapper.insert(purPurchasePriceItem);
		MongodbUtil.insertApprovalLogAddNum(purchasePrice.getPurchasePriceSid(), BusinessType.CHECK.getValue(), "来自采购成本核算",1);
	}
	//设置年月日
	 public void setDate(Calendar time,int year,int month,int day){
		 time.set(Calendar.YEAR,year);
		 time.set(Calendar.MONTH, month);
		 time.set(Calendar.DATE, day);
		 time.set(Calendar.HOUR, 0);
		 time.set(Calendar.MINUTE, 0);
	}
	//当前时间 增加
	public void addTime(Calendar time,int year,int month,int day){
		time.add(Calendar.YEAR,year);
		time.add(Calendar.MONTH, month);
		time.add(Calendar.DATE, day);
	}

    /**
     * 修改商品成本核算主
     *
     * @param cosProductCost 商品成本核算主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
	public int updateCosProductCost(CosProductCost cost) {
		Long sid = cost.getProductCostSid();
		String handleStatus=null;
		if (ConstantsEms.CHECK_STATUS.equals(cost.getHandleStatus())) {
			CosProductCost oldCost = cosProductCostMapper.selectById(cost.getProductCostSid());
			handleStatus=oldCost.getHandleStatus();
			if (ConstantsEms.CHECK_STATUS.equals(oldCost.getHandleStatus())) {
				if (!cost.getTaxRate().equals(oldCost.getTaxRate()) || !(cost.getPriceTax().compareTo(oldCost.getPriceTax()) == 0)) {
					String businessMode = cost.getBusinessType();
					if (ConstantsEms.COST_BUSINESS_TYPE_SA.equals(businessMode)) {
						//协议价回写销售价
						insertSale(cost);
					} else {
						insertPurchase(cost);
					}
				}
			}else{
				String businessMode = cost.getBusinessType();
				if (ConstantsEms.COST_BUSINESS_TYPE_SA.equals(businessMode)) {
					//协议价回写销售价
					insertSale(cost);
				} else {
					insertPurchase(cost);
				}
			}
		}
		cost.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername())
				.setUpdateDate(new Date());
		cosProductCostMapper.updateAllById(cost);
		//工价成本其他
		cosProductCostLaborOtherMapper.delete(new QueryWrapper<CosProductCostLaborOther>().lambda()
				.eq(CosProductCostLaborOther::getProductCostSid, sid)
		);
		List<CosProductCostMaterial> costMaterialList = cost.getCostMaterialList();
        CosProductCostBom cosProductCostBom = cosProductCostBomMapper.selectOne(new QueryWrapper<CosProductCostBom>().lambda()
                .eq(CosProductCostBom::getProductCostSid, cost.getProductCostSid())
        );
        cosProductCostMaterialMapper.delete(new QueryWrapper<CosProductCostMaterial>().lambda()
		.eq(CosProductCostMaterial::getProductCostBomSid,cosProductCostBom.getProductCostBomSid())
		);
		costMaterialList.forEach(bomitem->{
            if (bomitem.getLossRate() != null) {
                bomitem.setLossRate(bomitem.getLossRate().divide(new BigDecimal(100)));
            }
            if (bomitem.getQuoteLossRate() != null) {
                bomitem.setQuoteLossRate(bomitem.getQuoteLossRate().divide(new BigDecimal(100)));
            }
            if (bomitem.getConfirmLossRate() != null) {
                bomitem.setConfirmLossRate(bomitem.getConfirmLossRate().divide(new BigDecimal(100)));
            }
            if (bomitem.getCheckLossRate() != null) {
                bomitem.setCheckLossRate(bomitem.getCheckLossRate().divide(new BigDecimal(100)));
            }
            bomitem.setProductCostBomSid(cosProductCostBom.getProductCostBomSid());
		});
		cosProductCostMaterialMapper.inserts(costMaterialList);
		//附件
		QueryWrapper<CosProductCostAttachment> attWrapper = new QueryWrapper<CosProductCostAttachment>();
		attWrapper.eq("product_cost_sid", sid);
		cosProductCostAttachmentMapper.delete(attWrapper);
		//写入子表数据
		//BOM物料清单
		if (CollectionUtils.isNotEmpty(cost.getCostLaborList())) {
			cost.getCostLaborList().forEach(labor -> {
				labor.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername())
						.setUpdateDate(new Date());
				cosProductCostLaborMapper.updateById(labor);
				List<CosProductCostLaborOther> laborItemOtherList = labor.getLaborItemOtherList();
				//工价项其他
				if (CollectionUtils.isNotEmpty(laborItemOtherList)) {
					laborItemOtherList.forEach(laborOther -> {
						laborOther.setProductCostLaborOtherSid(null);
						laborOther.setProductCostLaborSid(labor.getProductCostLaborSid());
						laborOther.setProductCostSid(cost.getProductCostSid());
					});
					cosProductCostLaborOtherMapper.inserts(laborItemOtherList);
				}
			});
		}
		if (CollectionUtils.isNotEmpty(cost.getCostAttachmentList())) {
				cost.getCostAttachmentList().forEach(att->{
					att.setProductCostSid(cost.getProductCostSid());
					cosProductCostAttachmentMapper.insert(att);
				});
		}
		if(ConstantsEms.CHECK_STATUS.equals(handleStatus)){
			//插入日志
			MongodbUtil.insertUserLog(sid, BusinessType.CHANGE.getValue(), TITLE);
			//插入日志
			MongodbUtil.insertUserLog(sid, BusinessType.CHECK.getValue(), TITLE);
		}else{
			//插入日志
			MongodbUtil.insertUserLog(sid, BusinessType.UPDATE.getValue(), TITLE);
		}
		return 1;

	}

    /**
     * 批量删除商品成本核算主
     *
     * @param productCostSids 需要删除的商品成本核算主ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteCosProductCostByIds(List<Long> productCostSids) {
		int row = 0;
		List<CosProductCost> cosProductCosts = cosProductCostMapper.selectBatchIds(productCostSids);
		cosProductCosts.forEach(li->{
			BasMaterial basMaterial = basMaterialMapper.selectById(li.getMaterialSid());
			if(basMaterial.getCustomerSid()!=null&&li.getCustomerSid()!=null){
				if(li.getCustomerSid().toString().equals(basMaterial.getCustomerSid().toString())){
					basMaterial.setIsHasCreatedProductcost(ConstantsEms.NO);
					basMaterialMapper.updateById(basMaterial);
				}
			}
		});
		sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
		.in(SysTodoTask::getDocumentSid,productCostSids)
		);
		//主表
		row = cosProductCostMapper.delete(new QueryWrapper<CosProductCost>().lambda()
				.in(CosProductCost::getProductCostSid, productCostSids));
		List<CosProductCostBom> cosProductCostBoms = cosProductCostBomMapper.selectList(new QueryWrapper<CosProductCostBom>().lambda()
				.in(CosProductCostBom::getProductCostSid, productCostSids)
		);
		List<Long> sids = cosProductCostBoms.stream().map(li -> li.getProductCostBomSid()).collect(Collectors.toList());
		cosProductCostMaterialMapper.delete(new QueryWrapper<CosProductCostMaterial>().lambda()
		.in(CosProductCostMaterial::getProductCostBomSid,sids)
		);
		//bom子表及物料子表
		cosProductCostBomMapper.delete(new QueryWrapper<CosProductCostBom>().lambda()
				.in(CosProductCostBom::getProductCostSid, productCostSids));
		//工价成本明细
		cosProductCostLaborMapper.delete(new QueryWrapper<CosProductCostLabor>().lambda()
				.in(CosProductCostLabor::getProductCostSid, productCostSids));
		//附件
		cosProductCostAttachmentMapper.delete(new QueryWrapper<CosProductCostAttachment>().lambda()
				.in(CosProductCostAttachment::getProductCostSid, productCostSids));
		cosProductCostLaborOtherMapper.delete(new QueryWrapper<CosProductCostLaborOther>().lambda()
		.eq(CosProductCostLaborOther::getProductCostSid,productCostSids)
		);
		//插入日志
		productCostSids.forEach(li -> {
			MongodbUtil.insertUserLog(li, BusinessType.DELETE.getValue(), TITLE);
		});
		return row;
    }

    public void judgeExit(CosProductCost cost){
		List<CosProductCost> costList=null;
     	 String priceDimension = cost.getPriceDimension();
		//按款
		if(ConstantsEms.PRICE_K.equals(priceDimension)){
			QueryWrapper<CosProductCost> wrapper = new QueryWrapper<>();
			wrapper.eq("material_sid",cost.getMaterialSid())
					.eq("price_dimension",priceDimension)
					.eq("raw_material_mode",cost.getRawMaterialMode())
					.eq("business_type",cost.getBusinessType())
					.eq("customer_sid",cost.getCustomerSid());

			costList = cosProductCostMapper.selectList(wrapper);
		}else{
			QueryWrapper<CosProductCost> wrapper = new QueryWrapper<>();
			wrapper.eq("material_sid",cost.getMaterialSid())
					.eq("price_dimension",priceDimension)
					.eq("sku1_sid",cost)
					.eq("raw_material_mode",cost.getRawMaterialMode())
					.eq("business_type",cost.getBusinessType())
					.eq("customer_sid",cost.getCustomerSid());
			costList = cosProductCostMapper.selectList(wrapper);
		}
		if (CollectionUtils.isNotEmpty(costList)){
			throw new BaseException("请不要重复创建成本核算！");
		}
	}

	@Override
	public OrderErrRequest processCheck(OrderErrRequest request){
		List<Long> sidList = request.getSidList();
		List<CommonErrMsgResponse> msgList = new ArrayList<>();
		for (Long o : sidList) {
			CosProductCost cost = cosProductCostMapper.selectCosProductCostById(o);
			Long materialSid = cost.getMaterialSid();
			String materialCode = cost.getMaterialCode();
			if(materialCode==null){
				CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
				errMsgResponse.setMsg("商品款号不能为空");
				msgList.add(errMsgResponse);
			}
			List<TecBomHead> tecBomHeads = tecBomHeadMapper.selectList(new QueryWrapper<TecBomHead>().lambda()
					.eq(TecBomHead::getMaterialSid, materialSid)
			);
			String handleStatus = tecBomHeads.get(0).getHandleStatus();
			if(!ConstantsEms.CHECK_STATUS.equals(handleStatus)){
				CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
				errMsgResponse.setMsg("对应的bom必须是确认状态");
				msgList.add(errMsgResponse);
			}
			if (cost.getTaxRate() == null) {
				CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
				errMsgResponse.setMsg("税率不允许为空");
				msgList.add(errMsgResponse);
			}
			if (cost.getPriceTax() == null||cost.getPriceTax().compareTo(BigDecimal.ZERO)==0) {
				CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
				errMsgResponse.setMsg("协议价不允许为空");
				msgList.add(errMsgResponse);
			}
			List<CosProductCostMaterial> cosProductCostMaterials = cosProductCostMaterialMapper.selectCosProductCostMaterialById(cost.getProductCostSid());
			//跳过插入，只校验逻辑
			cost.setSkipInsert(ConstantsEms.YES);
			String businessMode = cost.getBusinessType();
			if(ConstantsEms.COST_BUSINESS_TYPE_SA.equals(businessMode)){
				List<CosProductCostMaterial> materials = cosProductCostMaterials.stream().filter(li -> li.getQuantity() == null || li.getQuoteQuantity() == null || li.getConfirmQuantity() == null).collect(Collectors.toList());
				if(CollectionUtil.isNotEmpty(materials)){
					CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
					errMsgResponse.setMsg("存在用量/报价用量/客方确认用量未填写的明细行，请检查!");
					msgList.add(errMsgResponse);
				}
				if(CollectionUtils.isNotEmpty(msgList)){
					request.setMsgList(msgList);
					return request;
				}
				try{
					//校验互斥
					checkSaleQunie(cost);
					//协议价回写销售价
					insertSale(cost);
				}catch (CustomException e){
					CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
					errMsgResponse.setMsg(e.getMessage());
					msgList.add(errMsgResponse);
				}
				if(CollectionUtils.isNotEmpty(msgList)){
					request.setMsgList(msgList);
					return request;
				}
				if(!ConstantsEms.YES.equals(request.getIsPriceNull())){
					List<CosProductCostMaterial> list = cosProductCostMaterials.stream().filter(li -> !ConstantsEms.PURCHASE_TYPE_KGLL.equals(li.getPurchaseType()) && (li.getPriceTax() == null || li.getQuotePriceTax() == null)
					).collect(Collectors.toList());
					if(CollectionUtils.isNotEmpty(list)){
						CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
						errMsgResponse.setMsg("”物料清单“页签存在明细行的报价或采购价为空");
						msgList.add(errMsgResponse);
						request.setIsPriceNull(ConstantsEms.YES);
					}
				}else{
					request.setIsPriceNull(null);
				}
			}else{
				List<CosProductCostMaterial> materials = cosProductCostMaterials.stream().filter(li -> li.getQuoteQuantity() == null || li.getCheckQuantity() == null || li.getConfirmQuantity() == null).collect(Collectors.toList());
				if(CollectionUtil.isNotEmpty(materials)){
					CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
					errMsgResponse.setMsg("存在报价用量/核定用量/供方确认用量未填写的明细行，请检查！");
					msgList.add(errMsgResponse);
				}
				List<CosProductCostMaterial> materialsPrice = cosProductCostMaterials.stream().filter(li -> li.getPriceTax() == null ).collect(Collectors.toList());
				if(CollectionUtils.isNotEmpty(materialsPrice)){
					if(msgList.size()==0){
						CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
						errMsgResponse.setMsg("”物料清单“页签存在明细行的采购价为空");
						msgList.add(errMsgResponse);
						request.setMsgList(msgList);
						request.setIsPriceNull(ConstantsEms.YES);
						return request;
					}else{
						CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
						errMsgResponse.setMsg("”物料清单“页签存在明细行的采购价为空");
						msgList.add(errMsgResponse);
					}
				}
				if(CollectionUtils.isNotEmpty(msgList)){
					request.setMsgList(msgList);
					return request;
				}
				try{
					CheckUniqueCommonRequest checkUniqueCommonRequest = new CheckUniqueCommonRequest();
					BeanCopyUtils.copyProperties(cost,checkUniqueCommonRequest);
					checkUniqueCommonRequest.setPurchaseMode(cost.getBusinessMode())
							.setCode(cost.getMaterialCode()!=null?cost.getMaterialCode():cost.getSampleCodeSelf())
							.setId(cost.getProductCostSid());
					//校验互斥
					purPurchasePriceService.checkUnique(checkUniqueCommonRequest);
					insertPurchase(cost);
				}catch (CustomException e){
					CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
					errMsgResponse.setMsg(e.getMessage());
					msgList.add(errMsgResponse);
				}
			}
		}
		request.setMsgList(msgList);
		return request;
	}
	//校验销售价互斥
	public void checkSaleQunie(CosProductCost cost){
		//按款
		if(ConstantsEms.PRICE_K.equals(cost.getPriceDimension())){
			List<CosProductCost> cosProductCosts = cosProductCostMapper.selectList(new QueryWrapper<CosProductCost>().lambda()
					.eq(CosProductCost::getRawMaterialMode, cost.getRawMaterialMode())
					.eq(CosProductCost::getMaterialSid, cost.getMaterialSid())
					.eq(CosProductCost::getCustomerSid, cost.getCustomerSid())
					.eq(CosProductCost::getBusinessMode, cost.getBusinessMode())
					.in(CosProductCost::getHandleStatus, new String[]{HandleStatus.SUBMIT.getCode(), HandleStatus.CHANGEAPPROVAL.getCode()})
			);
			List<CosProductCost> costs = cosProductCosts.stream().filter(li -> !li.getProductCostSid().toString().equals(cost.getProductCostSid().toString())).collect(Collectors.toList());
			if(CollectionUtil.isNotEmpty(costs)){
				throw new CustomException(cost.getMaterialCode()+cost.getMaterialName()+"存在相应的审批中的销售成本核算信息，请先处理此销售成本核算信息");
			}
			SalSalePrice salSalePrice = new SalSalePrice();
			BeanCopyUtils.copyProperties(cost,salSalePrice);
			salSalePrice.setHandleStatuses(new String[]{HandleStatus.SUBMIT.getCode(), HandleStatus.CHANGEAPPROVAL.getCode()})
					.setPriceDimension(null)
					.setSaleMode(cost.getBusinessMode());
			List<SalSalePrice> costList = salSalePriceMapper.getCostList(salSalePrice);
			if(CollectionUtil.isNotEmpty(costList)){
				String salePriceCode = costList.get(0).getSalePriceCode();
				throw new CustomException(cost.getMaterialCode()+cost.getMaterialName()+"存在相应的审批中的销售价"+salePriceCode+"，请先处理此销售价信息");
			}
		}else{
			List<CosProductCost> cosProductCosts = cosProductCostMapper.selectList(new QueryWrapper<CosProductCost>().lambda()
					.eq(CosProductCost::getRawMaterialMode, cost.getRawMaterialMode())
					.eq(CosProductCost::getMaterialSid, cost.getMaterialSid())
					.eq(CosProductCost::getCustomerSid, cost.getCustomerSid())
					.eq(CosProductCost::getPriceDimension,ConstantsEms.PRICE_K)
					.eq(CosProductCost::getBusinessMode, cost.getBusinessMode())
					.in(CosProductCost::getHandleStatus, new String[]{HandleStatus.SUBMIT.getCode(), HandleStatus.CHANGEAPPROVAL.getCode()})
			);
			if(CollectionUtil.isNotEmpty(cosProductCosts)){
				throw new CustomException(cost.getMaterialCode()+cost.getMaterialName()+"存在相应的审批中的销售成本核算信息，请先处理此销售成本核算信息");
			}
			SalSalePrice salSalePrice = new SalSalePrice();
			BeanCopyUtils.copyProperties(cost,salSalePrice);
			salSalePrice.setHandleStatuses(new String[]{HandleStatus.SUBMIT.getCode(), HandleStatus.CHANGEAPPROVAL.getCode()})
					.setPriceDimension(ConstantsEms.PRICE_K)
					.setSku1Sid(null)
					.setSaleMode(cost.getBusinessMode());
			List<SalSalePrice> costList = salSalePriceMapper.getCostList(salSalePrice);
			if(CollectionUtil.isNotEmpty(costList)){
				String salePriceCode = costList.get(0).getSalePriceCode();
				throw new CustomException(cost.getMaterialCode()+cost.getMaterialName()+"存在相应的审批中的销售价"+salePriceCode+"，请先处理此销售价信息");
			}
			List<CosProductCost> cosProductCostsK1 = cosProductCostMapper.selectList(new QueryWrapper<CosProductCost>().lambda()
					.eq(CosProductCost::getRawMaterialMode, cost.getRawMaterialMode())
					.eq(CosProductCost::getMaterialSid, cost.getMaterialSid())
					.eq(CosProductCost::getCustomerSid, cost.getCustomerSid())
					.eq(CosProductCost::getSku1Sid,cost.getSku1Sid())
					.eq(CosProductCost::getBusinessMode, cost.getBusinessMode())
					.in(CosProductCost::getHandleStatus, new String[]{HandleStatus.SUBMIT.getCode(), HandleStatus.CHANGEAPPROVAL.getCode()})
			);
			List<CosProductCost> costs = cosProductCostsK1.stream().filter(li -> !li.getProductCostSid().toString().equals(cost.getProductCostSid().toString())).collect(Collectors.toList());
			if(CollectionUtil.isNotEmpty(costs)){
				throw new CustomException(cost.getMaterialCode()+cost.getMaterialName()+"存在相应的审批中的销售成本核算信息，请先处理此销售成本核算信息");
			}
			SalSalePrice salSalePriceK1 = new SalSalePrice();
			BeanCopyUtils.copyProperties(cost,salSalePrice);
			salSalePrice.setHandleStatuses(new String[]{HandleStatus.SUBMIT.getCode(), HandleStatus.CHANGEAPPROVAL.getCode()})
					.setPriceDimension(ConstantsEms.PRICE_K1)
					.setSaleMode(cost.getBusinessMode());
			List<SalSalePrice> costListK1 = salSalePriceMapper.getCostList(salSalePrice);
			if(CollectionUtil.isNotEmpty(costListK1)){
				String salePriceCode = costListK1.get(0).getSalePriceCode();
				throw new CustomException(cost.getMaterialCode()+cost.getMaterialName()+"存在相应的审批中的销售价"+salePriceCode+"，请先处理此销售价信息");
			}
		}
	}

    /**
     * 确认商品成本核算主
     *
     * @param productCostSids 商品成本核算主ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int handleStatus(List<Long> productCostSids) {
        int row;
		productCostSids.forEach(o -> {
			CosProductCost cost = cosProductCostMapper.selectCosProductCostById(o);
			if (cost.getTaxRate() == null) {
				throw new CustomException("确认时，税率不允许为空");
			}
			if (cost.getPriceTax() == null) {
				throw new CustomException("确认时，协议价不允许为空");
			}
			String businessMode = cost.getBusinessType();
			if(ConstantsEms.COST_BUSINESS_TYPE_SA.equals(businessMode)){
				//协议价回写销售价
				insertSale(cost);
			}else{
				insertPurchase(cost);
			}
		});
		CosProductCost cosProductCost = new CosProductCost();
		row =cosProductCostMapper.update(cosProductCost,new UpdateWrapper<CosProductCost>().lambda()
				.set(CosProductCost::getConfirmerAccount,ApiThreadLocalUtil.get().getUsername())
				.set(CosProductCost::getConfirmDate,new Date())
				.set(CosProductCost::getHandleStatus,ConstantsEms.CHECK_STATUS)
				.in(CosProductCost::getProductCostSid,productCostSids));

		return row;
    }

	public void copySale(Long id){
		CosProductCost cost = selectCosProductCostById(id);
		SalSalePrice sale = new SalSalePrice();
		BeanCopyUtils.copyProperties(cost,sale);
		List<SalSalePriceItem> saleItemList = new ArrayList<SalSalePriceItem>();
		SalSalePriceItem saleItem = new SalSalePriceItem();
		BeanCopyUtils.copyProperties(cost,saleItem);
		saleItem.setStartDate(new Date());
		Calendar calendar = Calendar.getInstance();
		calendar.set(2099, 12, 31, 23, 59, 59);
		Date end = calendar.getTime();
		saleItem.setEndDate(end);
		saleItem.setSalePriceTax(cost.getPriceTax());
		saleItemList.add(saleItem);
		sale.setListSalSalePriceItem(saleItemList);
		salePriceService.insertSalSalePrice(sale);
	}

	public void copyPurchase(Long id){
		CosProductCost cost = selectCosProductCostById(id);
		PurPurchasePrice purPurchasePrice = new PurPurchasePrice();
		BeanCopyUtils.copyProperties(cost,purPurchasePrice);
		List<PurPurchasePriceItem> purPurchaseItemList = new ArrayList<PurPurchasePriceItem>();
		PurPurchasePriceItem purPurchaseItem = new PurPurchasePriceItem();
		BeanCopyUtils.copyProperties(cost,purPurchaseItem);
		purPurchaseItem.setStartDate(new Date());
		Calendar calendar = Calendar.getInstance();
		calendar.set(2099, 12, 31, 23, 59, 59);
		Date end = calendar.getTime();
		purPurchaseItem.setEndDate(end);
		purPurchaseItem.setPurchasePriceTax(cost.getPriceTax());
		purPurchaseItemList.add(purPurchaseItem);
		purPurchasePrice.setListPurPurchasePriceItem(purPurchaseItemList);
		purPurchasePriceService.insertPurPurchasePrice(purPurchasePrice);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int status(List<Long> materialSids , String status) {
		for(Long materialSid : materialSids) {
    		QueryWrapper<CosProductCost> costWrapper = new QueryWrapper<CosProductCost>();
    		costWrapper.eq("material_sid", materialSid);
    		List<CosProductCost> costList = cosProductCostMapper.selectList(costWrapper);
    		costList.forEach(cost ->{
    			cost.setStatus(status);
                cost.setConfirmDate(new Date());
                cost.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            	cosProductCostMapper.updateById(cost);
    		});
    	}
		return 1;
	}
	/**
	 * 获取成本价格
	 *
	 */
	public BigDecimal getCostPrice(Long materialSid,Long processSid,String  productionMode){
		if(ConstantsEms.PROCEE_ZC.equals(productionMode)){
			PayProductProcessStep payProductProcessStep = new PayProductProcessStep();
			payProductProcessStep.setProductSid(materialSid)
					.setHandleStatus(ConstantsEms.CHECK_STATUS)
					.setProcessSid(processSid);
			List<PayProductProcessStepItem> list = payProductProcessStepItemMapper.getCostPrice(payProductProcessStep);
			if(CollectionUtil.isNotEmpty(list)){
				//根据主表sid任取一笔符合条件的
				Long productProcessStepSid = list.get(0).getProductProcessStepSid();
				Map<Long, List<PayProductProcessStepItem>> listMap = list.stream().collect(Collectors.groupingBy(v -> v.getProductProcessStepSid()));
				List<PayProductProcessStepItem> payProductProcessStepItems = listMap.get(productProcessStepSid);
				BigDecimal sum = payProductProcessStepItems.stream().map(li -> li.getPrice().multiply(li.getPriceRate())).reduce(BigDecimal.ZERO, BigDecimal::add);
				return sum.divide(BigDecimal.ONE,5,BigDecimal.ROUND_HALF_UP);
			}
		}else{
			PurOutsourcePurchasePrice purOutsourcePurchasePrice = new PurOutsourcePurchasePrice();
			purOutsourcePurchasePrice.setMaterialSid(materialSid)
					.setProcessSid(processSid)
					.setCreateDate(new Date())
					.setHandleStatus(ConstantsEms.CHECK_STATUS);
			List<PurOutsourcePurchasePriceItem> list = purOutsourcePurchasePriceMapper.getCostPrice(purOutsourcePurchasePrice);
			if(CollectionUtil.isNotEmpty(list)){
				return list.get(0).getPurchasePriceTax();
            }
		}
		return null;
	}

	/**
	 * 获取报价
	 *
	 */
	public BigDecimal getQut(Long materialSid,Long processSid,String  productionMode){
		if(ConstantsEms.PROCEE_ZC.equals(productionMode)){
			PayProductProcessStep payProductProcessStep = new PayProductProcessStep();
			payProductProcessStep.setProductSid(materialSid)
					.setHandleStatus(ConstantsEms.CHECK_STATUS)
					.setProcessSid(processSid);
			List<PayProductProcessStepItem> list = payProductProcessStepItemMapper.getCostPrice(payProductProcessStep);
			if(CollectionUtil.isNotEmpty(list)){
				//根据主表sid任取一笔符合条件的
				Long productProcessStepSid = list.get(0).getProductProcessStepSid();
				Map<Long, List<PayProductProcessStepItem>> listMap = list.stream().collect(Collectors.groupingBy(v -> v.getProductProcessStepSid()));
				List<PayProductProcessStepItem> payProductProcessStepItems = listMap.get(productProcessStepSid);
				BigDecimal sum = payProductProcessStepItems.stream().map(li -> li.getPrice().multiply(li.getPriceRate())).reduce(BigDecimal.ZERO, BigDecimal::add);
				return sum.divide(BigDecimal.ONE,5,BigDecimal.ROUND_HALF_UP);
			}
		}else{
			List<PurOutsourcePriceInfor> purOutsourcePriceInfors = purOutsourcePriceInforMapper.selectList(new QueryWrapper<PurOutsourcePriceInfor>().lambda()
					.eq(PurOutsourcePriceInfor::getProcessSid, processSid)
					.eq(PurOutsourcePriceInfor::getMaterialSid, materialSid)
			);
			if(CollectionUtils.isNotEmpty(purOutsourcePriceInfors)){
				PurOutsourcePriceInfor purOutsourcePriceInfor = purOutsourcePriceInfors.get(0);
				List<PurOutsourcePriceInforItem> purOutsourcePriceInforItems = purOutsourcePriceInforItemMapper.selectList(new QueryWrapper<PurOutsourcePriceInforItem>().lambda()
						.eq(PurOutsourcePriceInforItem::getOutsourcePriceInforSid, purOutsourcePriceInfor.getOutsourcePriceInforSid())
				);
				return purOutsourcePriceInforItems.get(0).getQuotePriceTax();
			}

		}
		return null;
	}
	/**
	 * 更新成本价格
	 *
	 */
	@Override
	public List<CosProductCostLabor> updateCostPrice(CosProductCost cost){
		Long materialSid = cost.getMaterialSid();
		List<CosProductCostLabor> costLaborList = cost.getCostLaborList();
		costLaborList.forEach(li->{
			if(li.getProcessSid()!=null&&ConstantsEms.NO.equals(li.getIsOther())){
				BigDecimal price=getCostPrice(materialSid,li.getProcessSid(),li.getProductionMode());
				BigDecimal quotePriceTax = getQut(materialSid, li.getProcessSid(), li.getProductionMode());
				if(price!=null){
					li.setInnerPriceTax(price);
				}
				if(quotePriceTax!=null){
					li.setQuotePriceTax(quotePriceTax);
				}
			}
		});
		return costLaborList;
	}
	/**
	 * 更新清单列
	 *
	 */
	@Override
	public List<CosProductCostMaterial> updateBom(Long bomSid){
		List<TecBomItem> itemList = tecBomItemMapper.selectBomItemByBomSid(bomSid);
		if (CollectionUtils.isNotEmpty(itemList)) {
			itemList.forEach(item->{
				PurPurchasePriceItem purchase = getAllPurchase(item);
				if(purchase!=null){
					item.setPriceTax(purchase.getPurchasePriceTax());
					item.setUnitConversionRatePrice(purchase.getUnitConversionRate());
				}
				item.setQuotePriceTax(getAllQuoterPrice(item));
				if (item.getLossRate() != null) {
					item.setLossRate(item.getLossRate().multiply(new BigDecimal(100)));
				}
				if (item.getQuoteLossRate() != null) {
					item.setQuoteLossRate(item.getQuoteLossRate().multiply(new BigDecimal(100)));
				}
				if (item.getConfirmLossRate() != null) {
					item.setConfirmLossRate(item.getConfirmLossRate().multiply(new BigDecimal(100)));
				}
				if (item.getCheckLossRate() != null) {
					item.setCheckLossRate(item.getCheckLossRate().multiply(new BigDecimal(100)));
				}
			});
		}
		List<CosProductCostMaterial> cosProductCostMaterials = BeanCopyUtils.copyListProperties(itemList, CosProductCostMaterial::new);
		return cosProductCostMaterials;
	}
	/**
	 * 价格
	 *
	 */
	@Override
	public List<CosProductCostMaterial> updatePrice(Long productCostSid){
		List<CosProductCostMaterial> bomList = cosProductCostMaterialMapper.selectCosProductCostMaterialById(productCostSid);
		bomList.parallelStream().forEach(bom -> {
			TecBomItem tecBomItem = new TecBomItem();
			BeanCopyUtils.copyProperties(bom,tecBomItem);
			PurPurchasePriceItem purchase = getAllPurchase(tecBomItem);
			bom.setQuotePriceTax(getAllQuoterPrice(tecBomItem));
			if(purchase!=null){
				bom.setPriceTax(purchase.getPurchasePriceTax())
						.setUnitConversionRatePrice(purchase.getUnitConversionRate());
			}
			if (bom.getLossRate() != null) {
				bom.setLossRate(bom.getLossRate().multiply(new BigDecimal(100)));
			}
			if (bom.getQuoteLossRate() != null) {
				bom.setQuoteLossRate(bom.getQuoteLossRate().multiply(new BigDecimal(100)));
			}
			if (bom.getConfirmLossRate() != null) {
				bom.setConfirmLossRate(bom.getConfirmLossRate().multiply(new BigDecimal(100)));
			}
			if (bom.getCheckLossRate() != null) {
				bom.setCheckLossRate(bom.getCheckLossRate().multiply(new BigDecimal(100)));
			}
		});
		return bomList;
	}
	@Override
	@Transactional(rollbackFor = Exception.class)
	public CosProductCost getInsertInfo(BasMaterial request) {
		Long sku1Sid = request.getSku1Sid();
		String priceDimension = request.getPriceDimension();
		CosProductCost cost = new CosProductCost();
		BasMaterial basMaterial = basMaterialMapper.selectBasMaterialById(request.getMaterialSid());
		if (basMaterial == null) {
			throw new BaseException("输入的商品编码/我司样衣号 不存在，请检查！");
		}
		String isCreatedProductcost = basMaterial.getIsCreateProductcost();
		if(ConstantsEms.NO.equals(isCreatedProductcost)){
			throw new CustomException("该商品/样品不允许创建成本核算");
		}
		List<TecBomHead> bomList = null;
		String dimensionCode = "K";
		//按款 任选一条sku1
		if (dimensionCode.equals(priceDimension)) {
			QueryWrapper<TecBomHead> bomWrapper = new QueryWrapper<TecBomHead>();
			bomWrapper.eq("material_sid", basMaterial.getMaterialSid());
			bomList = tecBomHeadMapper.selectList(bomWrapper);
			if (CollectionUtils.isNotEmpty(bomList)) {
				TecBomHead tecBomHead = bomList.get(0);
				//清空原有的数据
				bomList = new ArrayList<TecBomHead>();
				bomList.add(tecBomHead);
			}
		} else {
			QueryWrapper<TecBomHead> bomWrapper = new QueryWrapper<TecBomHead>();
			bomWrapper.eq("material_sid", basMaterial.getMaterialSid());
			bomWrapper.eq("sku1_sid", sku1Sid);
			bomList = tecBomHeadMapper.selectList(bomWrapper);
		}

//        List<TecBomHead> bomList = tecBomHeadMapper.selectTecBomHeadByMaterialSid(basMaterial.getMaterialSid());
		if (CollectionUtils.isEmpty(bomList)) {
			throw new BaseException("该商品未创建BOM档案，请检查！");
		}
		List<CosProductCost> costList = null;
		String businessType = request.getBusinessType();
		if (ConstantsEms.COST_BUSINESS_TYPE_SA.equals(businessType)) {
			//按款
			if (dimensionCode.equals(priceDimension)) {
				QueryWrapper<CosProductCost> wrapper = new QueryWrapper<>();
				wrapper.eq("material_sid", basMaterial.getMaterialSid())
						.eq("price_dimension", priceDimension)
						.eq("raw_material_mode", request.getRawMaterialMode())
						.eq("customer_sid", request.getCustomerSid());
				costList = cosProductCostMapper.selectList(wrapper);
			} else {
				QueryWrapper<CosProductCost> wrapper = new QueryWrapper<>();
				wrapper.eq("material_sid", basMaterial.getMaterialSid())
						.eq("price_dimension", priceDimension)
						.eq("sku1_sid", sku1Sid)
						.eq("raw_material_mode", request.getRawMaterialMode())
						.eq("customer_sid", request.getCustomerSid());
				costList = cosProductCostMapper.selectList(wrapper);
			}
		} else {
			//按款
			if (dimensionCode.equals(priceDimension)) {
				QueryWrapper<CosProductCost> wrapper = new QueryWrapper<>();
				wrapper.eq("material_sid", basMaterial.getMaterialSid())
						.eq("price_dimension", priceDimension)
						.eq("raw_material_mode", request.getRawMaterialMode())
						.eq("vendor_sid", request.getVendorSid());
				costList = cosProductCostMapper.selectList(wrapper);
			} else {
				QueryWrapper<CosProductCost> wrapper = new QueryWrapper<>();
				wrapper.eq("material_sid", basMaterial.getMaterialSid())
						.eq("price_dimension", priceDimension)
						.eq("sku1_sid", sku1Sid)
						.eq("raw_material_mode", request.getRawMaterialMode())
						.eq("vendor_sid", request.getVendorSid());
				costList = cosProductCostMapper.selectList(wrapper);
			}
		}
		if (CollectionUtils.isNotEmpty(costList)) {
			CosProductCost cosProductCost = costList.get(0);
			return cost.setProductCostSid(cosProductCost.getProductCostSid());
		}
		TecBomHead bom = bomList.get(0);
		BeanCopyUtils.copyProperties(basMaterial,cost);
		BeanCopyUtils.copyProperties(request,cost);
		cost.setCreateDate(null)
				.setCreatorAccount(null)
				.setCreatorAccountName(null)
				.setUpdaterAccount(null)
				.setUpdateDate(null)
				.setUpdaterAccountName(null)
				.setStatus(null)
				.setRawMaterialMode(request.getRawMaterialMode())
				.setCustomerSid(request.getCustomerSid())
				.setVendorSid(request.getVendorSid())
				.setHandleStatus(null)
				.setConfirmDate(null)
				.setConfirmerAccount(null);
		cost.setSku1Sid(bom.getSku1Sid());
		cost.setBomHeadSkuSid(bom.getSku1Sid());
		BasSku basSku = basSkuMapper.selectById(bom.getSku1Sid());
		if (basSku != null){
			cost.setSku1Name(basSku.getSkuName());
			cost.setBomHeadSkuName(basSku.getSkuName());
		}
		if (ConstantsEms.COST_BUSINESS_TYPE_SA.equals(businessType)) {
			BasCustomer basCustomer = basCustomerMapper.selectById(request.getCustomerSid());
			if (basCustomer != null){
				cost.setCustomerName(basCustomer.getCustomerName());
			}
		}
		else {
			BasVendor basVendor = basVendorMapper.selectById(request.getVendorSid());
			if (basVendor != null){
				cost.setVendorName(basVendor.getVendorName());
			}
		}
		//bom明细
		List<TecBomItem> itemList = tecBomItemMapper.selectBomItemByBomSid(bom.getBomSid());
		cost.setBomSid((bom.getBomSid()));
		if (CollectionUtils.isNotEmpty(itemList)) {
				itemList.forEach(item->{
					if(ConstantsEms.COST_BUSINESS_TYPE_SA.equals(businessType)){
						item.setQuotePriceTax(getAllQuoterPrice(item));
					}
					PurPurchasePriceItem purchase = getAllPurchase(item);
					if(purchase!=null){
						item.setPriceTax(purchase.getPurchasePriceTax());
						item.setUnitConversionRatePrice(purchase.getUnitConversionRate());
					}
					if (item.getLossRate() != null) {
						item.setLossRate(item.getLossRate().multiply(new BigDecimal(100)));
					}
					if (item.getQuoteLossRate() != null) {
						item.setQuoteLossRate(item.getQuoteLossRate().multiply(new BigDecimal(100)));
					}
					if (item.getConfirmLossRate() != null) {
						item.setConfirmLossRate(item.getConfirmLossRate().multiply(new BigDecimal(100)));
					}
					if (item.getCheckLossRate() != null) {
						item.setCheckLossRate(item.getCheckLossRate().multiply(new BigDecimal(100)));
					}
				});
		}
		//添加工价成本模板数据
		CosCostLaborTemplate cosCostLaborTemplate = new CosCostLaborTemplate();
		cosCostLaborTemplate.setMaterialType(basMaterial.getMaterialType())
				.setProductTechniqueType(basMaterial.getProductTechniqueType())
				.setBusinessType(businessType);
		List<CosCostLaborTemplate> templateList = cosCostLaborTemplateMapper.selectCosCostLaborTemplateList(cosCostLaborTemplate);
		cosCostLaborTemplate = new CosCostLaborTemplate();
		if (CollectionUtils.isNotEmpty(templateList)) {
			cosCostLaborTemplate = templateList.get(0);
			CosCostLaborTemplateItem laborItem = new CosCostLaborTemplateItem();
			laborItem.setCostLaborTemplateSid(cosCostLaborTemplate.getCostLaborTemplateSid());
			List<CosCostLaborTemplateItem> laborItemList = cosCostLaborTemplateItemMapper.selectCosCostLaborTemplateItemList(laborItem);
			if (CollectionUtils.isNotEmpty(laborItemList)) {
				List<CosProductCostLabor> cosProductCostLabors = BeanCopyUtils.copyListProperties(laborItemList, CosProductCostLabor::new);
				cosProductCostLabors.forEach(li->{
					if(li.getProcessSid()!=null&&ConstantsEms.NO.equals(li.getIsOther())){
						li.setInnerPriceTax(getCostPrice(cost.getMaterialSid(),li.getProcessSid(),li.getProductionMode()));
						li.setQuotePriceTax(getQut(cost.getMaterialSid(),li.getProcessSid(),li.getProductionMode()));
					}
					li.setCreateDate(null)
							.setCreatorAccount(null)
							.setCreatorAccountName(null)
							.setUpdaterAccountName(null)
							.setUpdateDate(null)
							.setUpdaterAccount(null);
				});
				cost.setCostLaborList(cosProductCostLabors);
			}
		}
//		cost.setCosCostLaborTemplate(cosCostLaborTemplate);
		List<CosProductCostMaterial> cosProductCostMaterials = BeanCopyUtils.copyListProperties(itemList, CosProductCostMaterial::new);
		cost.setCostMaterialList(cosProductCostMaterials);
		return cost;
	}
   //获取报价信息
   public PurPriceInforItem getquotePriceTax(TecBomItem item) {
	   //按照“默认供应商+物料编码+甲供料方式【无】+采购模式【常规】+价格维度【按款】“
	   PurPriceInfor purPriceInfor = purPriceInforMapper.selectOne(new QueryWrapper<PurPriceInfor>().lambda()
			   .eq(PurPriceInfor::getVendorSid, item.getVendorSid())
			   .eq(PurPriceInfor::getMaterialSid, item.getBomMaterialSid())
			   .eq(PurPriceInfor::getPriceDimension, ConstantsEms.PRICE_K)
			   .eq(PurPriceInfor::getRawMaterialMode, ConstantsEms.RAW_w)
			   .eq(PurPriceInfor::getPurchaseMode, ConstantsEms.DOCUMNET_TYPE_ZG)
	   );
	   if (purPriceInfor != null) {
		   PurPriceInforItem purPriceInforItem = purPriceInforItemMapper.selectOne(new QueryWrapper<PurPriceInforItem>().lambda()
				   .eq(PurPriceInforItem::getPriceInforSid, purPriceInfor.getPriceInforSid())
		   );
		   if (purPriceInforItem != null) {
			   return purPriceInforItem;
		   }
	   } else {
		   //	按照“默认供应商+物料编码+甲供料方式【无】+采购模式【常规】+价格维度【按色】
		   List<PurPriceInfor> purPriceInfors = purPriceInforMapper.selectList(new QueryWrapper<PurPriceInfor>().lambda()
				   .eq(PurPriceInfor::getVendorSid, item.getVendorSid())
				   .eq(PurPriceInfor::getMaterialSid, item.getBomMaterialSid())
				   .eq(PurPriceInfor::getPriceDimension, ConstantsEms.PRICE_K1)
				   .eq(PurPriceInfor::getRawMaterialMode, ConstantsEms.RAW_w)
				   .eq(PurPriceInfor::getPurchaseMode, ConstantsEms.DOCUMNET_TYPE_ZG)
		   );
		   if (CollectionUtil.isNotEmpty(purPriceInfors)) {
			   PurPriceInfor priceInfor = purPriceInfors.get(0);
			   PurPriceInforItem purPriceInforItem = purPriceInforItemMapper.selectOne(new QueryWrapper<PurPriceInforItem>().lambda()
					   .eq(PurPriceInforItem::getPriceInforSid, priceInfor.getPriceInforSid())
			   );
			   if (purPriceInforItem != null) {
				   return purPriceInforItem;
			   }
		   } else {
			   //按照“物料编码+甲供料方式【无】+采购模式【常规】+价格维度【按款】
			   List<PurPriceInfor> purPriceInforVenNot = purPriceInforMapper.selectList(new QueryWrapper<PurPriceInfor>().lambda()
					   .eq(PurPriceInfor::getMaterialSid, item.getBomMaterialSid())
					   .eq(PurPriceInfor::getPriceDimension, ConstantsEms.PRICE_K)
					   .eq(PurPriceInfor::getRawMaterialMode, ConstantsEms.RAW_w)
					   .eq(PurPriceInfor::getPurchaseMode, ConstantsEms.DOCUMNET_TYPE_ZG)
			   );
			   if (CollectionUtil.isNotEmpty(purPriceInforVenNot)) {
				   PurPriceInfor priceInfor = purPriceInforVenNot.get(0);
				   PurPriceInforItem purPriceInforItem = purPriceInforItemMapper.selectOne(new QueryWrapper<PurPriceInforItem>().lambda()
						   .eq(PurPriceInforItem::getPriceInforSid, priceInfor.getPriceInforSid())
				   );
				   if (purPriceInforItem != null) {
					   return purPriceInforItem;
				   }
			   } else {
				   //按照“物料编码+甲供料方式【无】+采购模式【常规】+价格维度【按色】
				   List<PurPriceInfor> purPriceInforVenNotK1 = purPriceInforMapper.selectList(new QueryWrapper<PurPriceInfor>().lambda()
						   .eq(PurPriceInfor::getMaterialSid, item.getBomMaterialSid())
						   .eq(PurPriceInfor::getPriceDimension, ConstantsEms.PRICE_K1)
						   .eq(PurPriceInfor::getRawMaterialMode, ConstantsEms.RAW_w)
						   .eq(PurPriceInfor::getPurchaseMode, ConstantsEms.DOCUMNET_TYPE_ZG)
				   );
				   if (CollectionUtil.isNotEmpty(purPriceInforVenNotK1)) {
					   PurPriceInfor priceInfor = purPriceInforVenNotK1.get(0);
					   PurPriceInforItem purPriceInforItem = purPriceInforItemMapper.selectOne(new QueryWrapper<PurPriceInforItem>().lambda()
							   .eq(PurPriceInforItem::getPriceInforSid, priceInfor.getPriceInforSid())
					   );
					   if (purPriceInforItem != null) {
						   return purPriceInforItem;
					   }
				   }
			   }
		   }
	   }
	   return null;
   }


	/**
	 * 获取清单列采购价
	 */
	public PurPurchasePriceItem getAllPurchase(TecBomItem bomItem){
		PurPurchasePriceItem item=null;
		String zipperFlag = bomItem.getZipperFlag();
		if(ConstantsEms.ZIPPER_ZH.equals(zipperFlag)){
			item = getZipperPurchase(bomItem);
		}else{
			item = zipperPriceZT(bomItem);
		}
		if(item!=null){
		   return item;
		}
		return null;
	}
	/**
	 * 获取清单列报价
	 */
	public BigDecimal getAllQuoterPrice(TecBomItem bomItem){
		String zipperFlag = bomItem.getZipperFlag();
		BigDecimal priceTax = null;
		if (ConstantsEms.ZIPPER_ZH.equals(zipperFlag)) {
			Long materialSid = bomItem.getMaterialSid();
			TecBomHead zipper = iTecBomHeadService.getZipper(materialSid);
			BigDecimal purchaseZippe=null;
			List<TecBomItem> itemList = zipper.getItemList();
			List<BigDecimal> prices = new ArrayList<>();
			if(CollectionUtils.isNotEmpty(itemList)){
				itemList.forEach(li->{
					String zipperFlagItem = li.getZipperFlag();
					bomItem.setMaterialSid(li.getBomMaterialSid());
					//获取组件清单中的报价
					PurPriceInforItem purchasePrice = getquotePriceTax(bomItem);
					BigDecimal price;
					if(purchasePrice!=null){
						String isRecursionPrice = purchasePrice.getIsRecursionPrice();
						//链胚
						if(ConstantsEms.ZIPPER_LP.equals(zipperFlagItem)&&ConstantsEms.YES.equals(isRecursionPrice)){
							//最小起算量
							BigDecimal priceMinQuantity = purchasePrice.getPriceMinQuantity();
							//取整方式
							String roundingType = purchasePrice.getRoundingType();
							//获取链胚长度->等于整合拉链sku2的长度
							BigDecimal lenth =bomItem.getPriceQuantity();
							if(lenth==null){
								return ;
							}
							if(priceMinQuantity!=null){
								if(lenth.subtract(priceMinQuantity).compareTo(BigDecimal.ZERO)==-1){
									lenth=priceMinQuantity;
								}
							}
							//差异量
							BigDecimal diver = lenth.subtract(purchasePrice.getReferQuantity());
							if(diver.compareTo(new BigDecimal(0))==1){
								//递增
								BigDecimal value = getVale(diver.abs(), purchasePrice.getIncreQuantity(), roundingType);
								price=purchasePrice.getQuotePriceTax().add(value.multiply(purchasePrice.getIncreQuoPriceTax()));
								price=price.multiply(li.getInnerQuantity());
							}else if(diver.compareTo(new BigDecimal(0))==-1){
								//递减少
								BigDecimal value = getVale(diver.abs(),purchasePrice.getDecreQuantity(), roundingType);
								price=purchasePrice.getQuotePriceTax().subtract(value.multiply(purchasePrice.getDecreQuoPriceTax()));
								price=price.multiply(li.getInnerQuantity());
							}else{
								//相等
								price=purchasePrice.getQuotePriceTax();
								price=price.multiply(li.getInnerQuantity());
							}
						}else{
							price=purchasePrice.getQuotePriceTax().multiply(li.getInnerQuantity());
						}
						prices.add(price);
					}

				});
				if(prices.size()>0){
					if(prices.size()==itemList.size()){
						//求和
						BigDecimal totalPrice = prices.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
						purchaseZippe=totalPrice.divide(BigDecimal.ONE,4,BigDecimal.ROUND_UP);
					}else{
						return null;
					}
				}
				if(purchaseZippe==null){
					return null;
				}else{
					return purchaseZippe;
				}
			}
			return null;
		}else if(ConstantsEms.ZIPPER_ZT.equals(zipperFlag)){
			//获取整条拉链采购价
			PurPriceInforItem purchasePrice = getquotePriceTax(bomItem);
			BigDecimal price=null;
			if(purchasePrice != null && purchasePrice.getReferQuantity() != null
					&& purchasePrice.getPriceMinQuantity() != null && purchasePrice.getIncreQuantity() != null &&
					purchasePrice.getIncreQuoPriceTax() != null && purchasePrice.getDecreQuantity() != null &&
					purchasePrice.getDecreQuoPriceTax() != null)
			{
				String isRecursionPrice = purchasePrice.getIsRecursionPrice();
				if(ConstantsEms.YES.equals(isRecursionPrice)){
					//最小起算量
					BigDecimal priceMinQuantity = purchasePrice.getPriceMinQuantity();
					//取整方式
					String roundingType = purchasePrice.getRoundingType();
					//获取链胚长度->等于整合拉链sku2的长度
					BigDecimal lenth =bomItem.getPriceQuantity();
					if(lenth==null){
						return null;
					}
					if(priceMinQuantity!=null){
						if(lenth.subtract(priceMinQuantity).compareTo(BigDecimal.ZERO)==-1){
							lenth=priceMinQuantity;
						}
					}
					//差异量
					BigDecimal diver = lenth.subtract(purchasePrice.getReferQuantity());
					if(diver.compareTo(new BigDecimal(0))==1){
						//递增
						BigDecimal value = getVale(diver.abs(), purchasePrice.getIncreQuantity(), roundingType);
						price=purchasePrice.getQuotePriceTax().add(value.multiply(purchasePrice.getIncreQuoPriceTax()));
					}else if(diver.compareTo(new BigDecimal(0))==-1){
						//递减少
						BigDecimal value = getVale(diver.abs(),purchasePrice.getDecreQuantity(), roundingType);
						price=purchasePrice.getQuotePriceTax().subtract(value.multiply(purchasePrice.getDecreQuoPriceTax()));
					}else{
						//相等
						price=purchasePrice.getQuotePriceTax();
					}
				}else{
					price=purchasePrice.getQuotePriceTax();
				}
				return price;
			}
			return null;
		}
		else {
			PurPriceInforItem purPriceInforItem = getquotePriceTax(bomItem);
			if(purPriceInforItem!=null){
				return purPriceInforItem.getQuotePriceTax();
			}
		}
		return null;
	}
   public PurPurchasePriceItem getPriceTax(TecBomItem item){
	   List<PurPurchasePrice> result = null;
	   //   1.1 按照“默认供应商+物料编码+甲供料方式【无】+采购模式【常规】“
	   result = purPurchasePriceMapper.selectList(new QueryWrapper<PurPurchasePrice>()
			   .lambda()
			   .eq(PurPurchasePrice::getVendorSid, item.getVendorSid())
			   .eq(PurPurchasePrice::getPurchaseMode, ConstantsEms.DOCUMNET_TYPE_ZG)
			   .eq(PurPurchasePrice::getRawMaterialMode, ConstantsEms.RAW_w)
			   .eq(PurPurchasePrice::getMaterialSid, item.getBomMaterialSid())
			  );
	   if (CollectionUtil.isNotEmpty(result)) {
		   List<Long> sids = result.stream().map(li -> li.getPurchasePriceSid()).collect(Collectors.toList());
		   Date date = new Date();
		   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		   String now = sdf.format(date);
		   //直接截取到日
		   Date nowDate= DateUtils.parseDate(now);
		   List<PurPurchasePriceItem> priceItemList = purPurchasePriceItemMapper.selectList(new QueryWrapper<PurPurchasePriceItem>()
				   .lambda()
				   .le(PurPurchasePriceItem::getStartDate, nowDate)
				   .ge(PurPurchasePriceItem::getEndDate, nowDate)
				   .eq(PurPurchasePriceItem::getHandleStatus,ConstantsEms.CHECK_STATUS)
				   .in(PurPurchasePriceItem::getPurchasePriceSid,sids));
		   if(CollectionUtil.isNotEmpty(priceItemList)){
			   PurPurchasePriceItem purPurchasePriceItem = priceItemList.get(0);
			   return purPurchasePriceItem;
		   }
	      }
		   // 按照“物料编码+甲供料方式【无】+采购模式【常规】“
		   result = purPurchasePriceMapper.selectList(new QueryWrapper<PurPurchasePrice>()
				   .lambda()
				   .eq(PurPurchasePrice::getPurchaseMode, ConstantsEms.DOCUMNET_TYPE_ZG)
				   .eq(PurPurchasePrice::getRawMaterialMode, ConstantsEms.RAW_w)
				   .eq(PurPurchasePrice::getMaterialSid, item.getBomMaterialSid())
		   );
		   if (CollectionUtil.isNotEmpty(result)) {
			   List<Long> sids = result.stream().map(li -> li.getPurchasePriceSid()).collect(Collectors.toList());
			   Date date = new Date();
			   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			   String now = sdf.format(date);
			   //直接截取到日
			   Date nowDate=DateUtils.parseDate(now);
			   List<PurPurchasePriceItem> priceItemList = purPurchasePriceItemMapper.selectList(new QueryWrapper<PurPurchasePriceItem>()
					   .lambda()
					   .le(PurPurchasePriceItem::getStartDate, nowDate)
					   .ge(PurPurchasePriceItem::getEndDate, nowDate)
					   .eq(PurPurchasePriceItem::getHandleStatus,ConstantsEms.CHECK_STATUS)
					   .in(PurPurchasePriceItem::getPurchasePriceSid,sids));
			   if(CollectionUtil.isNotEmpty(priceItemList)){
				   PurPurchasePriceItem purPurchasePriceItem = priceItemList.get(0);
				   return purPurchasePriceItem;
			   }
		   }
	   return null;
   }

	//获取组合拉链的采购价
	public PurPurchasePriceItem getZipperPurchase(TecBomItem bomItem){
		Long materialSid = bomItem.getMaterialSid();
		TecBomHead zipper = iTecBomHeadService.getZipper(materialSid);
		BigDecimal purchaseZippe=null;
		List<TecBomItem> itemList = zipper.getItemList();
		List<BigDecimal> prices = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(itemList)){
			itemList.forEach(li->{
				String zipperFlag = li.getZipperFlag();
				bomItem.setMaterialSid(li.getBomMaterialSid());
				//获取组件清单中的采购价
				PurPurchasePriceItem purchasePrice = getPriceTax(bomItem);
				BigDecimal price;
				if(purchasePrice!=null){
					String isRecursionPrice = purchasePrice.getIsRecursionPrice();
					//链胚
					if(ConstantsEms.ZIPPER_LP.equals(zipperFlag)&&ConstantsEms.YES.equals(isRecursionPrice)){
						//最小起算量
						BigDecimal priceMinQuantity = purchasePrice.getPriceMinQuantity();
						//取整方式
						String roundingType = purchasePrice.getRoundingType();
						//获取链胚长度->等于整合拉链sku2的长度
						BigDecimal lenth =bomItem.getPriceQuantity();
						if(lenth==null){
							return ;
						}
						if(priceMinQuantity!=null){
							if(lenth.subtract(priceMinQuantity).compareTo(BigDecimal.ZERO)==-1){
								lenth=priceMinQuantity;
							}
						}
						//差异量
						BigDecimal diver = lenth.subtract(purchasePrice.getReferQuantity());
						if(diver.compareTo(new BigDecimal(0))==1){
							//递增
							BigDecimal value = getVale(diver.abs(), purchasePrice.getIncreQuantity(), roundingType);
							price=purchasePrice.getPurchasePriceTax().add(value.multiply(purchasePrice.getIncrePurPriceTax()));
							price=price.multiply(li.getInnerQuantity());
						}else if(diver.compareTo(new BigDecimal(0))==-1){
							//递减少
							BigDecimal value = getVale(diver.abs(),purchasePrice.getDecreQuantity(), roundingType);
							price=purchasePrice.getPurchasePriceTax().subtract(value.multiply(purchasePrice.getDecPurPriceTax()));
							price=price.multiply(li.getInnerQuantity());
						}else{
							//相等
							price=purchasePrice.getPurchasePriceTax();
							price=price.multiply(li.getInnerQuantity());
						}
					}else{
						price=purchasePrice.getPurchasePriceTax().multiply(li.getInnerQuantity());
					}
					prices.add(price);
				}

			});
			if(prices.size()>0){
				if(prices.size()==itemList.size()){
					//求和
					BigDecimal totalPrice = prices.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
					purchaseZippe=totalPrice.divide(BigDecimal.ONE,4,BigDecimal.ROUND_UP);
				}else{
					return null;
				}
			}
			if(purchaseZippe==null){
				return null;
			}else{
				PurPurchasePriceItem purPurchasePriceItem = new PurPurchasePriceItem();
				purPurchasePriceItem.setPurchasePriceTax(purchaseZippe);
				return purPurchasePriceItem;
			}
		}
		return null;
	}

	//获取整条拉链采购价
	public PurPurchasePriceItem zipperPriceZT(TecBomItem item){
		//获取整条拉链采购价
		PurPurchasePriceItem purchasePrice = getPriceTax(item);
		BigDecimal price=null;
		if(purchasePrice!=null){
			String isRecursionPrice = purchasePrice.getIsRecursionPrice();
			if(ConstantsEms.YES.equals(isRecursionPrice)){
				//最小起算量
				BigDecimal priceMinQuantity = purchasePrice.getPriceMinQuantity();
				//取整方式
				String roundingType = purchasePrice.getRoundingType();
				//获取链胚长度->等于整合拉链sku2的长度
				BigDecimal lenth =item.getPriceQuantity();
				if(lenth==null){
					return null;
				}
				if(priceMinQuantity!=null){
					if(lenth.subtract(priceMinQuantity).compareTo(BigDecimal.ZERO)==-1){
						lenth=priceMinQuantity;
					}
				}
				//差异量
				BigDecimal diver = lenth.subtract(purchasePrice.getReferQuantity());
				if(diver.compareTo(new BigDecimal(0))==1){
					//递增
					BigDecimal value = getVale(diver.abs(), purchasePrice.getIncreQuantity(), roundingType);
					price=purchasePrice.getPurchasePriceTax().add(value.multiply(purchasePrice.getIncrePurPriceTax()));
				}else if(diver.compareTo(new BigDecimal(0))==-1){
					//递减少
					BigDecimal value = getVale(diver.abs(),purchasePrice.getDecreQuantity(), roundingType);
					price=purchasePrice.getPurchasePriceTax().subtract(value.multiply(purchasePrice.getDecPurPriceTax()));
				}else{
					//相等
					price=purchasePrice.getPurchasePriceTax();
				}
			}else{
				price=purchasePrice.getPurchasePriceTax();
			}
			purchasePrice.setPurchasePriceTax(price);
		}
		return purchasePrice;
	}

	public BigDecimal getVale(BigDecimal diver,BigDecimal quaily,String roundingType){
		BigDecimal treal=null;
		if(ConstantsEms.QZFS_UP.equals(roundingType)){
			treal=diver.divide(quaily,0,BigDecimal.ROUND_UP);
		}else if(ConstantsEms.QZFS_DOWN.equals(roundingType)){
			treal=diver.divide(quaily,0,BigDecimal.ROUND_DOWN);
		}else{
			treal=diver.divide(quaily,0,BigDecimal.ROUND_HALF_UP);
		}
		return treal;
	}
	/**
	 * 通过序列号查询出成本核算其他项
	 *
	 */
	@Override
	public  List<CosProductCostLaborOther> getByNum(int num){
		List<CosProductCostLaborOther> list = cosProductCostLaborOtherMapper.selectCosProductCostLaborOtherById(num);
         return list;
	}
}
