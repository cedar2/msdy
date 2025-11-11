package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurPriceInfor;

/**
 * 采购价格记录主(报价/核价/议价)Mapper接口
 *
 * @author linhongwei
 * @date 2021-04-26
 */
public interface PurPriceInforMapper  extends BaseMapper<PurPriceInfor> {


    PurPriceInfor selectPurPriceInforById(Long priceInforSid);

    List<PurPriceInfor> selectPurPriceInforList(PurPriceInfor purPriceInfor);

    /**
     * 添加多个
     * @param list List PurPriceInfor
     * @return int
     */
    int inserts(@Param("list") List<PurPriceInfor> list);

    /**
    * 全量更新(通过barcodeSid)
    * null字段也会进行更新，慎用
    * @param entity PurPriceInfor
    * @return int
    */
    int updateAllById(PurPriceInfor entity);

    /**
     * 更新多个
     * @param list List PurPriceInfor
     * @return int
     */
    int updatesAllById(@Param("list") List<PurPriceInfor> list);


    List<Long> selectPriceByMaterialIds(@Param("array")Long[] materialSids);

}
