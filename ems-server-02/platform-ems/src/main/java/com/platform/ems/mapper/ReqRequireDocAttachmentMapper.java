package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ReqRequireDocAttachment;

/**
 * 需求单附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-02
 */
public interface ReqRequireDocAttachmentMapper  extends BaseMapper<ReqRequireDocAttachment> {


    ReqRequireDocAttachment selectReqRequireDocAttachmentById(Long requireDocAttachmentSid);

    List<ReqRequireDocAttachment> selectReqRequireDocAttachmentList(ReqRequireDocAttachment reqRequireDocAttachment);

    /**
     * 添加多个
     * @param list List ReqRequireDocAttachment
     * @return int
     */
    int inserts(@Param("list") List<ReqRequireDocAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ReqRequireDocAttachment
    * @return int
    */
    int updateAllById(ReqRequireDocAttachment entity);

    /**
     * 更新多个
     * @param list List ReqRequireDocAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<ReqRequireDocAttachment> list);


    void deleteRequireDocAttachmentByIds(@Param("array")Long[] requireDocSids);
}
