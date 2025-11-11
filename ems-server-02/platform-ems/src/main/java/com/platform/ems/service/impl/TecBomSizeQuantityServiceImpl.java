package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.BasMaterial;
import com.platform.ems.domain.BasMaterialSku;
import com.platform.ems.domain.TecBomSizeQuantity;
import com.platform.ems.domain.dto.request.TecBomSizeSkuRequest;
import com.platform.ems.mapper.TecBomSizeQuantityMapper;
import com.platform.ems.service.ITecBomSizeQuantityService;
import com.platform.common.utils.bean.BeanCopyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 物料清单（BOM）组件具体尺码用量Service业务层处理
 *
 * @author qhq
 * @date 2021-03-15
 */
@Service
@SuppressWarnings("all")
public class TecBomSizeQuantityServiceImpl extends ServiceImpl<TecBomSizeQuantityMapper,TecBomSizeQuantity>  implements ITecBomSizeQuantityService {
    @Autowired
    private TecBomSizeQuantityMapper tecBomSizeQuantityMapper;
    @Autowired
    private BasMaterialServiceImpl basMaterialServiceImpl;

    /**
     * 查询物料清单（BOM）组件具体尺码用量
     *
     * @param clientId 物料清单（BOM）组件具体尺码用量ID
     * @return 物料清单（BOM）组件具体尺码用量
     */
    @Override
    public List<TecBomSizeQuantity> selectTecBomSizeQuantityById(List<TecBomSizeQuantity>  request) {
        List<TecBomSizeQuantity> tecBomSizelist = new ArrayList<>();
        request.forEach(o->{
            //获取该物料的尺码
            List<BasMaterialSku> MaterialSkuList = basMaterialServiceImpl.selectBasMaterialById(o.getMaterialSid()).getBasMaterialSkuList()
                    .stream().filter(m -> m.getSkuType().equals(ConstantsEms.SKUTYP_CM)).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(MaterialSkuList)){
                throw new CustomException("该物料没有尺码");
            }
            List<TecBomSizeQuantity> bomSizelist = tecBomSizeQuantityMapper.selectList(new QueryWrapper<TecBomSizeQuantity>().lambda()
                    .eq(TecBomSizeQuantity::getBomItemSid, o.getBomItemSid()));
            //判断是否存在已有的尺码拉链
            if(CollectionUtils.isNotEmpty(bomSizelist)){
                List<TecBomSizeSkuRequest> skuList = new ArrayList<>();
                List<TecBomSizeQuantity> list = tecBomSizeQuantityMapper.selectTecBomSizeQuantityById(o.getMaterialSid());
                int size = MaterialSkuList.size();
                for (int i=0;i<size;i++) {
                    TecBomSizeSkuRequest sku = new TecBomSizeSkuRequest();
                    sku.setSkuBomSid(Long.valueOf(MaterialSkuList.get(i).getSkuSid()));
                    sku.setSkuBomName(MaterialSkuList.get(i).getSkuName());
                    sku.setSkuType(MaterialSkuList.get(i).getSkuType());
                    sku.setBomItemSid(list.get(i).getBomItemSid());
                    sku.setSkuName(list.get(i).getSkuName());
                    sku.setSkuSid(list.get(i).getSkuSid());
                    skuList.add(sku);
                }
                TecBomSizeQuantity tecBomSizeQuantity = list.get(0).setSkuList(skuList);
                tecBomSizelist.add(tecBomSizeQuantity);
            }else{
                BasMaterial basMaterial = basMaterialServiceImpl.selectBasMaterialById((o.getMaterialSid()));
                TecBomSizeQuantity tecBom= new TecBomSizeQuantity();
                BeanCopyUtils.copyProperties(basMaterial, tecBom);
                tecBomSizelist.add(tecBom);
            }
        });
        return tecBomSizelist;
    }

    /**
     * 查询物料清单（BOM）组件具体尺码用量列表
     *
     * @param tecBomSizeQuantity 物料清单（BOM）组件具体尺码用量
     * @return 物料清单（BOM）组件具体尺码用量
     */
    @Override
    public List<TecBomSizeQuantity> selectTecBomSizeQuantityList(TecBomSizeQuantity tecBomSizeQuantity) {
        return tecBomSizeQuantityMapper.selectTecBomSizeQuantityList(tecBomSizeQuantity);
    }

    /**
     * 新增物料清单（BOM）组件具体尺码用量
     * 需要注意编码重复校验
     * @param tecBomSizeQuantity 物料清单（BOM）组件具体尺码用量
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertTecBomSizeQuantity(List<TecBomSizeQuantity> request) {
        request.forEach(item -> {
            List<TecBomSizeSkuRequest> skuList = item.getSkuList();
            List<TecBomSizeQuantity> list = new ArrayList<>();
            skuList.forEach(o -> {
                        TecBomSizeQuantity tecBomSize = new TecBomSizeQuantity();
                        BeanCopyUtils.copyProperties(item, tecBomSize);
                        tecBomSize.setBomItemSid(o.getBomItemSid());
                        tecBomSize.setSkuSid(o.getSkuSid());
//                        tecBomSize.setBomSizeQuantitySid(IdWorker.getId());
//                        tecBomSize.setCreateDate(new Date());
//                        tecBomSize.setClientId(ApiThreadLocalUtil.get().getClientId());
//                        tecBomSize.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
//                        list.add(tecBomSize);
                        tecBomSizeQuantityMapper.insert(tecBomSize);
                    }
            );
//             tecBomSizeQuantityMapper.inserts(list);
        });
      return 1;
    }

    /**
     * 修改物料清单（BOM）组件具体尺码用量
     *
     * @param tecBomSizeQuantity 物料清单（BOM）组件具体尺码用量
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTecBomSizeQuantity(List<TecBomSizeQuantity> request) {
        request.forEach(item -> {
            List<TecBomSizeSkuRequest> skuList = item.getSkuList();
            List<TecBomSizeQuantity> list = new ArrayList<>();
            skuList.forEach(o -> {
                        TecBomSizeQuantity tecBomSize = new TecBomSizeQuantity();
                        BeanCopyUtils.copyProperties(item, tecBomSize);
                        tecBomSize.setBomItemSid(o.getBomItemSid());
                        tecBomSize.setSkuSid(o.getSkuSid());
                        tecBomSize.setClientId(ApiThreadLocalUtil.get().getClientId());
                        tecBomSize.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                        tecBomSize.setUpdateDate(new Date());
                        tecBomSizeQuantityMapper.updateAllById(tecBomSize);
//                        list.add(tecBomSize);
                    }
            );
//            tecBomSizeQuantityMapper.updatesAllById(list);
        });
        return 1;
    }


    /**
     * 批量删除物料清单（BOM）组件具体尺码用量
     *
     * @param clientIds 需要删除的物料清单（BOM）组件具体尺码用量ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTecBomSizeQuantityByIds(List<String> clientIds) {
        return tecBomSizeQuantityMapper.deleteBatchIds(clientIds);
    }

    @Override
    public int deleteTecBomSizeQuantityById(String clientId) {
        return 0;
    }


}
