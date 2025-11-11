package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ReqRequireDocItem;

/**
 * 需求单明细Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-02
 */
public interface ReqRequireDocItemMapper  extends BaseMapper<ReqRequireDocItem> {


    ReqRequireDocItem selectReqRequireDocItemById(Long requireDocItemSid);

    List<ReqRequireDocItem> selectReqRequireDocItemList(ReqRequireDocItem reqRequireDocItem);

    /**
     * 添加多个
     * @param list List ReqRequireDocItem
     * @return int
     */
    int inserts(@Param("list") List<ReqRequireDocItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ReqRequireDocItem
    * @return int
    */
    int updateAllById(ReqRequireDocItem entity);

    /**
     * 更新多个
     * @param list List ReqRequireDocItem
     * @return int
     */
    int updatesAllById(@Param("list") List<ReqRequireDocItem> list);


    void deleteRequireDocItemByIds(@Param("array")Long[] requireDocSids);
}
