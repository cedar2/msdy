package com.platform.file.controller;

import cn.hutool.core.bean.BeanUtil;
import com.platform.common.config.PlatformConfig;
import com.platform.common.core.domain.R;
import com.platform.common.core.domain.entity.FileType;
import com.platform.common.core.domain.model.LoginUser;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.file.FileUploadUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.utils.file.MimeTypeUtils;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.plug.domain.ConFileType;
import com.platform.ems.plug.service.IConFileTypeService;
import com.platform.file.service.ISysFileService;
import com.platform.framework.web.domain.server.SysFile;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

import static com.platform.common.utils.SecurityUtils.getLoginUser;

/**
 * 文件请求处理
 *
 * @author linhongwei
 */
@RestController
@SuppressWarnings("all")
public class SysFileController {
    private static final Logger log = LoggerFactory.getLogger(SysFileController.class);

    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient client;

    @Autowired
    private ISysFileService sysFileService;

    @Autowired
    private IConFileTypeService conFileTypeService;

    /**
     * 文件上传请求
     */
    @PostMapping("upload")
    public R<SysFile> upload(MultipartFile file, Long sid) {
        if(sid!=null){
            ConFileType conFileType = conFileTypeService.selectConFileTypeById(sid);
            FileType fileType=new FileType();
            BeanUtil.copyProperties(conFileType, fileType, true);
            Long fileSize=file.getSize();
            Long maxSize=fileType.getMaxSize();
            if((fileSize.doubleValue()/1024/1024)>maxSize.doubleValue()){
                throw new BaseException("上传文件超过限制,限制大小："+maxSize+"MB");
            }
        }
        try {
            // 上传并返回访问地址
            String url = sysFileService.uploadFile(file);
            SysFile sysFile = new SysFile();
            sysFile.setName(FilenameUtils.removeExtension(file.getOriginalFilename()) + "."+ FileUploadUtils.getExtension(file));
            sysFile.setUrl(url);
            return R.ok(sysFile);
        } catch (Exception e) {
            log.error("上传文件失败", e);
            return R.fail(e.getMessage());
        }
    }

    @PostMapping("downloadTemplate")
    public void download(String filePath, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String filepath = "/" + minioConfig.getBucketName();
        int size = filepath.length();
        filePath = filePath.substring(filePath.indexOf(filepath) + size);
        InputStream inputStream = client.getObject(GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(filePath).build());
        OutputStream outputStream = null;
        String suffix = filePath.substring(filePath.lastIndexOf("."));
//        fileName = ""fileName"" + suffix;  再多传一个参数文件名
        try {
            byte[] buffer = new byte[1024];
            int len = 0;
            response.reset();
//            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
            response.addHeader("Access-Control-Allow-Headers", "Content-Type");
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(filePath, "UTF-8"));
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("UTF-8");
            outputStream = response.getOutputStream();
            //输出文件
            while((len = inputStream.read(buffer)) > 0){
                outputStream.write(buffer, 0, len);
            }
        } catch (Exception e) {
            throw new BaseException("读取文件异常:" + e.getMessage());
        }finally {
            if(inputStream!=null){
                inputStream.close();
            }
            if(outputStream!=null){
                outputStream.close();
            }
        }
    }

    @PostMapping("uploadTemplate")
    public R<SysFile> uploadTemplate(MultipartFile file, String fileName) {
        try {
            String url = sysFileService.uploadTemplate(file, fileName);
            SysFile sysFile = new SysFile();
            sysFile.setName(FileUtils.getName(url) + "." + FileUploadUtils.getExtension(file));
            sysFile.setUrl(url);
            return R.ok(sysFile);
        } catch (Exception e) {
            log.error("上传文件失败", e);
            return R.fail(e.getMessage());
        }
    }
}

