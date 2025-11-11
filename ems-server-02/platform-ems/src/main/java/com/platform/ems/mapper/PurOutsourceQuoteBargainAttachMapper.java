package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.PurOutsourceQuoteBargainAttach;
import org.apache.ibatis.annotations.Param;

/**
 * 加工询报议价单-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-10
 */
public interface PurOutsourceQuoteBargainAttachMapper extends BaseMapper<PurOutsourceQuoteBargainAttach> {


    PurOutsourceQuoteBargainAttach selectPurOutsourceRequestQuotationAttachmentById(Long outsourceRequestQuotationAttachmentSid);

    List<PurOutsourceQuoteBargainAttach> selectPurOutsourceRequestQuotationAttachmentList(PurOutsourceQuoteBargainAttach purOutsourceQuoteBargainAttach);

    /**
     * 添加多个
     * @param list List PurOutsourceQuoteBargainAttach
     * @return int
     */
    int inserts(@Param("list") List<PurOutsourceQuoteBargainAttach> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity PurOutsourceQuoteBargainAttach
    * @return int
    */
    int updateAllById(PurOutsourceQuoteBargainAttach entity);

    /**
     * 更新多个
     * @param list List PurOutsourceQuoteBargainAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<PurOutsourceQuoteBargainAttach> list);


}
