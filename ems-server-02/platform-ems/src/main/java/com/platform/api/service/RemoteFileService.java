package com.platform.api.service;

import com.platform.common.core.domain.R;
import com.platform.framework.web.domain.server.SysFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务
 *
 * @author linhongwei
 */
public interface RemoteFileService {
    /**
     * 上传文件
     *
     * @param file 文件信息
     * @return 结果
     */
    public R<SysFile> upload(MultipartFile file, Long sid);

    R<SysFile> upload(MultipartFile multipartFile);

    R<SysFile> uploadTemplate(MultipartFile multipartFile, String fileName);
}
