package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurInquiry;

/**
 * 物料询价单主Mapper接口
 * 
 * @author chenkw
 * @date 2022-01-11
 */
public interface PurInquiryMapper  extends BaseMapper<PurInquiry> {


    PurInquiry selectPurInquiryById(Long inquirySid);

    List<PurInquiry> selectPurInquiryList(PurInquiry purInquiry);

    /**
     * 添加多个
     * @param list List PurInquiry
     * @return int
     */
    int inserts(@Param("list") List<PurInquiry> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity PurInquiry
    * @return int
    */
    int updateAllById(PurInquiry entity);

    /**
     * 更新多个
     * @param list List PurInquiry
     * @return int
     */
    int updatesAllById(@Param("list") List<PurInquiry> list);


}
