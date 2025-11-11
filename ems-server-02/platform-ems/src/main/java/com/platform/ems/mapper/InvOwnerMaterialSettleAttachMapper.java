package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvOwnerMaterialSettleAttach;

/**
 * 甲供料结算单-附件Mapper接口
 * 
 * @author c
 * @date 2021-09-13
 */
public interface InvOwnerMaterialSettleAttachMapper  extends BaseMapper<InvOwnerMaterialSettleAttach> {


    List<InvOwnerMaterialSettleAttach> selectInvOwnerMaterialSettleAttachById(Long settleSid);

    List<InvOwnerMaterialSettleAttach> selectInvOwnerMaterialSettleAttachList(InvOwnerMaterialSettleAttach invOwnerMaterialSettleAttach);

    /**
     * 添加多个
     * @param list List InvOwnerMaterialSettleAttach
     * @return int
     */
    int inserts(@Param("list") List<InvOwnerMaterialSettleAttach> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvOwnerMaterialSettleAttach
    * @return int
    */
    int updateAllById(InvOwnerMaterialSettleAttach entity);

    /**
     * 更新多个
     * @param list List InvOwnerMaterialSettleAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<InvOwnerMaterialSettleAttach> list);


}
