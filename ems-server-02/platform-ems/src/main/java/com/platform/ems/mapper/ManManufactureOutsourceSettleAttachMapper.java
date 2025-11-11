package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ManManufactureOutsourceSettleAttach;

/**
 * 外发加工费结算单-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-10
 */
public interface ManManufactureOutsourceSettleAttachMapper  extends BaseMapper<ManManufactureOutsourceSettleAttach> {


    ManManufactureOutsourceSettleAttach selectManManufactureOutsourceSettleAttachById(Long manufactureOutsourceSettleAttachSid);

    List<ManManufactureOutsourceSettleAttach> selectManManufactureOutsourceSettleAttachList(ManManufactureOutsourceSettleAttach manManufactureOutsourceSettleAttach);

    /**
     * 添加多个
     * @param list List ManManufactureOutsourceSettleAttach
     * @return int
     */
    int inserts(@Param("list") List<ManManufactureOutsourceSettleAttach> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ManManufactureOutsourceSettleAttach
    * @return int
    */
    int updateAllById(ManManufactureOutsourceSettleAttach entity);

    /**
     * 更新多个
     * @param list List ManManufactureOutsourceSettleAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<ManManufactureOutsourceSettleAttach> list);


    void deleteManManufactureOutsourceSettleAttachByIds(@Param("list") List<Long> manufactureOutsourceSettleSids);
}
