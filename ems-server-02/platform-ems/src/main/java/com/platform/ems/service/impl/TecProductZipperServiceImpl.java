package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.response.TecProductSizeZipperLengthResponse;
import com.platform.ems.domain.dto.response.TecProductZipperSkuResponse;
import com.platform.ems.mapper.*;
import com.platform.ems.service.ITecProductZipperService;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品所用拉链Service业务层处理
 *
 * @author c
 * @date 2021-08-03
 */
@Service
@SuppressWarnings("all")
public class TecProductZipperServiceImpl extends ServiceImpl<TecProductZipperMapper,TecProductZipper>  implements ITecProductZipperService {
    @Autowired
    private TecProductZipperMapper tecProductZipperMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private BasMaterialSkuMapper basMaterialSkuMapper;
    @Autowired
    private BasMaterialMapper basMaterialMapper;
    @Autowired
    private TecProductSizeZipperLengthMapper tecProductSizeZipperLengthMapper;
    @Autowired
    private BasMaterialServiceImpl basMaterialServiceImpl;
    @Autowired
    private TecBomHeadMapper tecBomHeadMapper;
    private static final String TABLE = "s_tec_bom_head_task";
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;



    private static final String TITLE = "商品所用拉链";
    /**
     * 查询商品所用拉链
     *
     * @param productZipperSid 商品所用拉链ID
     * @return 商品所用拉链
     */
    @Override
    public TecProductZipper selectTecProductZipperById(String bomMaterialCode, List<Long> materialSids) {
        BasMaterialSku basMaterialSku = new BasMaterialSku();
        basMaterialSku.setMaterialSid(Long.valueOf(bomMaterialCode));
        basMaterialSku.setSkuType(ConstantsEms.SKUTYP_CM);
        List<BasMaterialSku> basMaterialSkus = basMaterialSkuMapper.selectBasMaterialSkuList(basMaterialSku);
        if (CollectionUtils.isEmpty(basMaterialSkus)) {
            throw new BaseException("该bom下的商品，没有尺码类型");
        }
        basMaterialSkus=basMaterialSkus.stream().filter(li->ConstantsEms.ENABLE_STATUS.equals(li.getStatus())).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(basMaterialSkus)){
            // 如果无序号再排序
            List<BasMaterialSku> haveSort = basMaterialSkus.stream().filter(item -> item.getSort() != null).collect(Collectors.toList());
            basMaterialSkus = basMaterialSkus.stream().filter(item -> item.getSort() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(basMaterialSkus)) {
                basMaterialSkus.forEach(li -> {
                    String skuName = li.getSkuName();
                    String[] nameSplit = skuName.split("/");
                    if (nameSplit.length == 1) {
                        li.setFirstSort(nameSplit[0]);
                    } else {
                        String[] name2split = nameSplit[1].split("\\(");
                        if (name2split.length == 2) {
                            li.setSecondSort(name2split[0].replaceAll("[a-zA-Z]", ""));

                            li.setThirdSort(name2split[1]);
                        } else {
                            li.setSecondSort(nameSplit[1].replaceAll("[a-zA-Z]", ""));
                        }
                        li.setFirstSort(nameSplit[0]);
                    }
                });
                List<BasMaterialSku> allList = new ArrayList<>();
                List<BasMaterialSku> allThirdList = new ArrayList<>();
                List<BasMaterialSku> sortThird = basMaterialSkus.stream().filter(li -> li.getThirdSort() != null).collect(Collectors.toList());
                List<BasMaterialSku> sortThirdNull = basMaterialSkus.stream().filter(li -> li.getThirdSort() == null).collect(Collectors.toList());
                sortThird=sortThird.stream().sorted(Comparator.comparing(li->li.getThirdSort())).collect(Collectors.toList());
                allThirdList.addAll(sortThird);
                allThirdList.addAll(sortThirdNull);
                List<BasMaterialSku> sort = allThirdList.stream().filter(li -> li.getSecondSort() != null).collect(Collectors.toList());
                sort=sort.stream().sorted(Comparator.comparing(li->Integer.valueOf(li.getSecondSort()))).collect(Collectors.toList());
                List<BasMaterialSku> sortNull = allThirdList.stream().filter(li -> li.getSecondSort() == null).collect(Collectors.toList());
                allList.addAll(sort);
                allList.addAll(sortNull);
                basMaterialSkus= allList.stream().sorted(Comparator.comparing(item -> item.getFirstSort())
                ).collect(Collectors.toList());
            }
            else {
                basMaterialSkus = new ArrayList<>();
            }
            if (CollectionUtil.isNotEmpty(haveSort)) {
                haveSort = haveSort.stream().sorted(Comparator.comparing(BasMaterialSku::getSort)).collect(Collectors.toList());
                basMaterialSkus.addAll(haveSort);
            }
        }else{
            throw new BaseException("该bom下的商品，没有启用的尺码类型");
        }
        //获取 bom 对应的尺码
        List<TecProductZipperSkuResponse> skuList = new ArrayList<>();
        basMaterialSkus.forEach(o -> {
            TecProductZipperSkuResponse sku = new TecProductZipperSkuResponse();
            sku.setSkuSid(o.getSkuSid());
            sku.setSkuName(o.getSkuName());
            skuList.add(sku);
        });
        Long bomMaterialSid = basMaterialSkus.get(0).getMaterialSid();
        List<TecProductZipper> listTecProduct = new ArrayList<>();
        for (Long o : materialSids) {
//            materialSids.forEach(o -> {
            List<BasMaterialSku> BasMaterialSkuList = basMaterialSkuMapper.selectList(new QueryWrapper<BasMaterialSku>().lambda()
                    .eq(BasMaterialSku::getMaterialSid, o)
                    .eq(BasMaterialSku::getSkuType, ConstantsEms.SKUTYPE_LE)
            );
            if (CollectionUtils.isEmpty(BasMaterialSkuList)) {
                String materialName = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
                        .eq(BasMaterial::getMaterialSid, o)
                ).getMaterialName();
                throw new BaseException("所选择的" + materialName + "，没有长度类型");
            }
            TecProductZipper tecProductZipper = tecProductZipperMapper.selectOne(new QueryWrapper<TecProductZipper>().lambda()
                    .eq(TecProductZipper::getMaterialSid, o)
                    .eq(TecProductZipper::getProductSid, bomMaterialSid)

            );
            //判断该物料是否有尺码拉链
            if (tecProductZipper != null) {
                TecProductZipper tecProduct = new TecProductZipper();
                List<TecProductSizeZipperLengthResponse> zipperLengthResponseList = new ArrayList<>();
                //获取现有的尺码拉链
                basMaterialSkus.forEach(p -> {
                    TecProductSizeZipperLength item = tecProductSizeZipperLengthMapper.selectOne(new QueryWrapper<TecProductSizeZipperLength>().lambda()
                            .eq(TecProductSizeZipperLength::getProductZipperSid, tecProductZipper.getProductZipperSid())
                            .eq(TecProductSizeZipperLength::getProductSkuSid, p.getSkuSid())
                    );
                    TecProductSizeZipperLengthResponse tecProductSizeZipperLength = new TecProductSizeZipperLengthResponse();
                    tecProductSizeZipperLength.setProductSkuSid(p.getSkuSid());
                    if(item!=null){
                        tecProductSizeZipperLength.setMaterialSkuSid(item.getMaterialSkuSid());
                    }else{
                        tecProductSizeZipperLength.setMaterialSkuSid(null);
                    }
                    zipperLengthResponseList.add(tecProductSizeZipperLength);
                });
                tecProduct.setProductSid(bomMaterialSid);
                tecProduct.setMaterialSid(o);
                tecProduct.setProductZipperSid(tecProductZipper.getProductZipperSid());
                tecProduct.setSizeZipperList(zipperLengthResponseList);
                listTecProduct.add(tecProduct);
            } else {
                TecProductZipper tecProduct = new TecProductZipper();
                List<TecProductSizeZipperLengthResponse> zipperLengthResponseList = new ArrayList<>();
                basMaterialSkus.forEach(p -> {
                    TecProductSizeZipperLengthResponse tecProductSizeZipperLength = new TecProductSizeZipperLengthResponse();
                    tecProductSizeZipperLength.setMaterialSkuSid(null);
                    tecProductSizeZipperLength.setProductSkuSid(p.getSkuSid());
                    zipperLengthResponseList.add(tecProductSizeZipperLength);
                });
                tecProduct.setProductSid(bomMaterialSid);
                tecProduct.setMaterialSid(o);
                tecProduct.setSizeZipperList(zipperLengthResponseList);
                listTecProduct.add(tecProduct);
            }
//            });
        }
        TecProductZipper tecProductZipper = new TecProductZipper();
        tecProductZipper.setListMaterial(listTecProduct);
        tecProductZipper.setListSku(skuList);
        return tecProductZipper;
    }

    /**
     * 查询商品所用拉链列表
     *
     * @param tecProductZipper 商品所用拉链
     * @return 商品所用拉链
     */
    @Override
    public List<TecProductZipper> selectTecProductZipperList(TecProductZipper tecProductZipper) {
        return tecProductZipperMapper.selectTecProductZipperList(tecProductZipper);
    }

    /**
     * 新增商品所用拉链
     * 需要注意编码重复校验
     * @param tecProductZipper 商品所用拉链
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertTecProductZipper(TecProductZipper request){
        int row=1;
        List<TecProductZipper> list = request.getListMaterial();
        list.forEach(m->{
            delete(m);
        });
         row= tecProductZipperMapper.inserts(list);
        list.forEach(item->{
            List<TecProductSizeZipperLengthResponse> sizeZipperList = item.getSizeZipperList();
            ArrayList<TecProductSizeZipperLength> tecProductSizeZipperLengths = new ArrayList<>();
            sizeZipperList.forEach(o->{
                TecProductSizeZipperLength tecProductSizeZipperLength = new TecProductSizeZipperLength();
                tecProductSizeZipperLength.setMaterialSkuSid(o.getMaterialSkuSid());
                tecProductSizeZipperLength.setProductSkuSid(o.getProductSkuSid());
                tecProductSizeZipperLength.setProductZipperSid(item.getProductZipperSid());
                tecProductSizeZipperLengths.add(tecProductSizeZipperLength);
                updateLength(item.getMaterialSid(),o.getMaterialSkuSid());
            });
            tecProductSizeZipperLengthMapper.inserts(tecProductSizeZipperLengths);
        });
        List<TecProductSizeZipperLengthResponse> sizeZipperList = list.get(0).getSizeZipperList();
        List<TecProductSizeZipperLengthResponse> sizeZipperListNull = sizeZipperList.stream().filter(li -> li.getMaterialSkuSid() == null).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(sizeZipperListNull)){
            //删除待办
            Long materialSid = list.get(0).getProductSid();
            List<TecBomHead> tecBomHeads = tecBomHeadMapper.selectList(new QueryWrapper<TecBomHead>().lambda()
                    .eq(TecBomHead::getMaterialSid, materialSid)
            );
            if(CollectionUtils.isNotEmpty(tecBomHeads)){
                List<Long> longs = tecBomHeads.stream().map(li -> li.getBomSid()).collect(Collectors.toList());
                int delete = sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getTableName, TABLE)
                        .eq(SysTodoTask::getDocumentItemSid,list.get(0).getMaterialSid())
                        .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                        .in(SysTodoTask::getDocumentSid, longs)
                );
            }


        }
        return row;
    }
    /**
     * 删除原因的尺码拉链
     */
    public void delete(TecProductZipper tecProductZipper){
        Long sid = tecProductZipper.getProductZipperSid();
        if(sid!=null){
            tecProductZipperMapper.delete(new QueryWrapper<TecProductZipper>().lambda()
            .eq(TecProductZipper::getProductZipperSid,sid)
            );
            List<TecProductSizeZipperLengthResponse> sizeZipperList = tecProductZipper.getSizeZipperList();
            if(CollectionUtils.isNotEmpty(sizeZipperList)){
                sizeZipperList.forEach(li->{
                    if(li.getProductSkuSid()!=null){
                        tecProductSizeZipperLengthMapper.delete(new QueryWrapper<TecProductSizeZipperLength>().lambda()
                                .eq(TecProductSizeZipperLength::getProductZipperSid,sid)
                                .eq(TecProductSizeZipperLength::getProductSkuSid,li.getProductSkuSid())
                        );
                    }
                });
            }
        }
    }
    /**
     * 修改物料对应的长度
     */
    public void updateLength(Long materialSid,Long skuSid){
        BasMaterialSku basMaterialSku = basMaterialSkuMapper.selectOne(new QueryWrapper<BasMaterialSku>().lambda()
                .eq(BasMaterialSku::getMaterialSid, materialSid)
                .eq(BasMaterialSku::getSkuSid, skuSid)
        );
        if(basMaterialSku==null&&skuSid!=null){
            BasMaterialSku materialSku = new BasMaterialSku();
            materialSku.setSkuSid(skuSid);
            materialSku.setSkuType(ConstantsEms.SKUTYPE_LE);
            materialSku.setMaterialSid(materialSid);
            materialSku.setStatus(ConstantsEms.SAVA_STATUS);
            materialSku.setHandleStatus(ConstantsEms.CHECK_STATUS);
            BasMaterial basMaterial = basMaterialServiceImpl.selectBasMaterialById(materialSid);
            List<BasMaterialSku> basMaterialSkuList = basMaterial.getBasMaterialSkuList();
            basMaterialSkuList.add(materialSku);
            basMaterial.setBasMaterialSkuList(basMaterialSkuList);
            basMaterialServiceImpl.change(basMaterial);
        }
    }

    /**
     * 修改商品所用拉链
     *
     * @param tecProductZipper 商品所用拉链
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTecProductZipper(TecProductZipper tecProductZipper) {
        TecProductZipper response = tecProductZipperMapper.selectTecProductZipperById(tecProductZipper.getProductZipperSid());
        int row=tecProductZipperMapper.updateById(tecProductZipper);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(tecProductZipper.getProductZipperSid(), BusinessType.UPDATE.ordinal(), response,tecProductZipper,TITLE);
        }
        return row;
    }

    /**
     * 变更商品所用拉链
     *
     * @param tecProductZipper 商品所用拉链
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeTecProductZipper(TecProductZipper tecProductZipper) {
        TecProductZipper response = tecProductZipperMapper.selectTecProductZipperById(tecProductZipper.getProductZipperSid());
                                                            int row=tecProductZipperMapper.updateAllById(tecProductZipper);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(tecProductZipper.getProductZipperSid(), BusinessType.CHANGE.ordinal(), response,tecProductZipper,TITLE);
        }
        return row;
    }

    /**
     * 批量删除商品所用拉链
     *
     * @param productZipperSids 需要删除的商品所用拉链ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTecProductZipperByIds(List<Long> productZipperSids) {
        return tecProductZipperMapper.deleteBatchIds(productZipperSids);
    }




    /**
     *更改确认状态
     * @param tecProductZipper
     * @return
     */
    @Override
    public int check(TecProductZipper tecProductZipper){
        int row=0;
        Long[] sids=tecProductZipper.getProductZipperSidList();
        if(sids!=null&&sids.length>0){
            row=tecProductZipperMapper.update(null,new UpdateWrapper<TecProductZipper>().lambda().set(TecProductZipper::getHandleStatus ,ConstantsEms.CHECK_STATUS)
                    .in(TecProductZipper::getProductZipperSid,sids));
            for(Long id:sids){
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }


}
