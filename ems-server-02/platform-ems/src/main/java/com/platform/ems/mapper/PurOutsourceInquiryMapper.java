package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurOutsourceInquiry;

/**
 * 加工询价单主Mapper接口
 * 
 * @author chenkw
 * @date 2022-01-11
 */
public interface PurOutsourceInquiryMapper  extends BaseMapper<PurOutsourceInquiry> {


    PurOutsourceInquiry selectPurOutsourceInquiryById(Long outsourceInquirySid);

    List<PurOutsourceInquiry> selectPurOutsourceInquiryList(PurOutsourceInquiry purOutsourceInquiry);

    /**
     * 添加多个
     * @param list List PurOutsourceInquiry
     * @return int
     */
    int inserts(@Param("list") List<PurOutsourceInquiry> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity PurOutsourceInquiry
    * @return int
    */
    int updateAllById(PurOutsourceInquiry entity);

    /**
     * 更新多个
     * @param list List PurOutsourceInquiry
     * @return int
     */
    int updatesAllById(@Param("list") List<PurOutsourceInquiry> list);


}
