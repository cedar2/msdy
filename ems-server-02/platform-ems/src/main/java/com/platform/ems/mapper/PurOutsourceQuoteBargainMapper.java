package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurOutsourceQuoteBargain;

/**
 * 加工询报议价单主(询价/报价/核价/议价)Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-10
 */
public interface PurOutsourceQuoteBargainMapper extends BaseMapper<PurOutsourceQuoteBargain> {


    PurOutsourceQuoteBargain selectPurOutsourceRequestQuotationById(Long outsourceRequestQuotationSid);

    List<PurOutsourceQuoteBargain> selectPurOutsourceRequestQuotationList(PurOutsourceQuoteBargain purOutsourceQuoteBargain);

    /**
     * 添加多个
     * @param list List PurOutsourceQuoteBargain
     * @return int
     */
    int inserts(@Param("list") List<PurOutsourceQuoteBargain> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity PurOutsourceQuoteBargain
    * @return int
    */
    int updateAllById(PurOutsourceQuoteBargain entity);

    /**
     * 更新多个
     * @param list List PurOutsourceQuoteBargain
     * @return int
     */
    int updatesAllById(@Param("list") List<PurOutsourceQuoteBargain> list);


}
