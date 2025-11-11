package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PrjProject;
import com.platform.ems.domain.base.EmsResultEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * 项目档案导入接口
 */
public interface IPrjProjectImportService extends IService<PrjProject> {

    /**
     * 导入试销项目
     * @param file 文件
     * @return 返回
     */
    EmsResultEntity importShix(MultipartFile file, PrjProject project);

    /**
     * 导入开发项目
     * @param file 文件
     * @return 返回
     */
    EmsResultEntity importKaif(MultipartFile file, PrjProject project);
}
