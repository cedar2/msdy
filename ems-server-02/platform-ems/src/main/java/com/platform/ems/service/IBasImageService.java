package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasImage;
import com.platform.ems.domain.base.EmsResultEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图案档案Service接口
 *
 * @author chenkw
 * @date 2022-12-14
 */
public interface IBasImageService extends IService<BasImage> {
    /**
     * 查询图案档案
     *
     * @param imageSid 图案档案ID
     * @return 图案档案
     */
    public BasImage selectBasImageById(Long imageSid);

    /**
     * 查询图案档案列表
     *
     * @param basImage 图案档案
     * @return 图案档案集合
     */
    public List<BasImage> selectBasImageList(BasImage basImage);

    /**
     * 新增图案档案
     *
     * @param basImage 图案档案
     * @return 结果
     */
    public int insertBasImage(BasImage basImage);

    /**
     * 修改图案档案
     *
     * @param basImage 图案档案
     * @return 结果
     */
    public int updateBasImage(BasImage basImage);

    /**
     * 变更图案档案
     *
     * @param basImage 图案档案
     * @return 结果
     */
    public int changeBasImage(BasImage basImage);

    /**
     * 批量删除图案档案
     *
     * @param imageSids 需要删除的图案档案ID
     * @return 结果
     */
    public int deleteBasImageByIds(List<Long> imageSids);

    /**
     * 启用/停用
     *
     * @param basImage
     * @return
     */
    int changeStatus(BasImage basImage);

    /**
     * 更改确认状态
     *
     * @param basImage
     * @return
     */
    int check(BasImage basImage);

    /**
     * 导入
     * @param file
     * @return
     */
    EmsResultEntity importData(MultipartFile file);
}
