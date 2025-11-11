package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ManProductProduceBatchInfor;

/**
 * 商品生产批次信息Mapper接口
 * 
 * @author chenkw
 * @date 2022-09-30
 */
public interface ManProductProduceBatchInforMapper  extends BaseMapper<ManProductProduceBatchInfor> {


    ManProductProduceBatchInfor selectManProductProduceBatchInforById(Long produceBatchInforSid);

    List<ManProductProduceBatchInfor> selectManProductProduceBatchInforList(ManProductProduceBatchInfor manProductProduceBatchInfor);

    /**
     * 添加多个
     * @param list List ManProductProduceBatchInfor
     * @return int
     */
    int inserts(@Param("list") List<ManProductProduceBatchInfor> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ManProductProduceBatchInfor
    * @return int
    */
    int updateAllById(ManProductProduceBatchInfor entity);

    /**
     * 更新多个
     * @param list List ManProductProduceBatchInfor
     * @return int
     */
    int updatesAllById(@Param("list") List<ManProductProduceBatchInfor> list);


}
