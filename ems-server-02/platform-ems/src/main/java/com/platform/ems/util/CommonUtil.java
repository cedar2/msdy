package com.platform.ems.util;

import cn.hutool.core.util.StrUtil;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * 常用工具类
 * @Author qhq
 * @create 2021/11/11 10:09
 */
@SuppressWarnings("all")
public class CommonUtil {

    private static String homePath = System.getProperty("user.home");

    public static String getDeskTopPath(){
        return homePath+"\\Desktop";
    }


    /**
     * @Description 成digits位数的 字母 大小随机 当做注册码
     * @Param int 位数
     * @return String 随机码
     * @Author chenkw
     **/
    public static String random(int digits){
        Random r = new Random();
        String code = "";
        for (int i = 0; i < digits; ++i) {
            int temp = r.nextInt(52);
            char x = (char) (temp < 26 ? temp + 97 : (temp % 26) + 65);
            code += x;
        }
        return code;
    }

    /**
     * @Description 获取字段名称
     * @Param Object 类名
     * @Param String 字段名
     * @return String 字段名中文注释
     * @Author chenkw
     **/
    public static String getFieldName(Object object1,String field) {
        String alias = "";
        Field[] fields = object1.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getName().equals(field)){
                ApiModelProperty attr = fields[i].getAnnotation(ApiModelProperty.class);
                if (attr != null && (StrUtil.isNotEmpty(attr.value()))) {
                    alias = attr.value();
                    break;
                }
            }
        }
        return alias;
    }

    /**
     * @Description 自定义List分页工具
     * @param List list
     * @param pageNum 页码
     * @param pageSize 每页多少条数据
     * @return List list
     * @author chenkw
     */
    public static List startPage(List list, Integer pageNum, Integer pageSize) {
        if (list == null) {
            return null;
        }
        if (list.size() == 0) {
            return null;
        }
        // 记录总数
        Integer count = list.size();
        // 页数
        Integer pageCount = 0;
        if (count % pageSize == 0) {
            pageCount = count / pageSize;
        } else {
            pageCount = count / pageSize + 1;
        }
        // 开始索引
        int fromIndex = 0;
        // 结束索引
        int toIndex = 0;
        if (pageNum - pageCount != 0) {
            fromIndex = (pageNum - 1) * pageSize;
            toIndex = fromIndex + pageSize;
        } else {
            fromIndex = (pageNum - 1) * pageSize;
            toIndex = count;
        }
        List pageList = list.subList(fromIndex, toIndex);
        return pageList;
    }

    /**
     * @Description 实体类转HashMap
     * @param Object 类名
     * @return HashMap <String,Object>
     * @author chenkw
     */
    public static HashMap<String,Object> transferEntityToMap(Object onClass){
        HashMap<String,Object> hashMap = new HashMap<String,Object>();
        Field[] fields = onClass.getClass().getDeclaredFields();
        for(Field field:fields){
            //反射时让私有变量变成可访问
            field.setAccessible(true);
            try {
                hashMap.put(field.getName(),field.get(onClass));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return hashMap;
    }

    /**
     * 根据PDF模版生成PDF文件  合同模板生成电子合同
     * @param templateFilePath PDF模版文件路径
     * @param object 实体数据
     * @param imageData 图片数据 VALUE为图片文件路径
     * @param formFlattening false：生成后的PDF文件表单域仍然可编辑 true：生成后的PDF文件表单域不可编辑
     * @param pdfFilePath 生成PDF的文件路径
     */
    public static MultipartFile createPDF(String templateFilePath, Object object, HashMap<String,String> imageData,
                                          boolean formFlattening, String pdfFilePath, String fileName) throws Exception{
        PdfReader reader = null;
        ByteArrayOutputStream bos = null;
        PdfStamper pdfStamper = null;
        // 创建一份空白文件，用来生成电子合同 后转为 MultipartFile
        File aimfile = new File("C:\\"+fileName+".pdf");
        boolean res = aimfile.createNewFile();
        FileOutputStream fos = new FileOutputStream(aimfile);
        //FileOutputStream fos = null;
        //fos = new FileOutputStream(pdfFilePath);
        try{
            HashMap<String,Object> data = transferEntityToMap(object);
            // 读取PDF模版文件
            reader = new PdfReader(templateFilePath);
            // 输出流
            bos = new ByteArrayOutputStream();
            // 构建PDF对象
            pdfStamper = new PdfStamper(reader, bos);
            // 获取表单数据
            AcroFields form = pdfStamper.getAcroFields();
            // 使用中文字体 使用 AcroFields填充值的不需要在程序中设置字体，在模板文件中设置字体为中文字体 Adobe 宋体 std L
            BaseFont bfChinese = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            form.addSubstitutionFont(bfChinese);
            // 表单赋值
            for(String key : data.keySet()){
                if (data.get(key) != null){
                    form.setField(key,data.get(key).toString());
                    // 也可以指定字体
                    form.setFieldProperty(key, "textfont", bfChinese, null);
                }
            }
            // 添加图片
            if(null != imageData && imageData.size() > 0){
                for(String key : imageData.keySet()){
                    int pageNo = form.getFieldPositions(key).get(0).page;
                    Rectangle signRect = form.getFieldPositions(key).get(0).position;
                    float x = signRect.getLeft();
                    float y = signRect.getBottom();
                    // 读图片
                    Image image = Image.getInstance(imageData.get(key));
                    // 获取操作的页面
                    PdfContentByte under = pdfStamper.getOverContent(pageNo);
                    // 根据域的大小缩放图片
                    image.scaleToFit(signRect.getWidth(), signRect.getHeight());
                    // 添加图片
                    image.setAbsolutePosition(x, y);
                    under.addImage(image);
                }
            }
            // 如果为false那么生成的PDF文件还能编辑，一定要设为true
            pdfStamper.setFormFlattening(formFlattening);
            pdfStamper.close();
            // 保存文件
            fos.write(bos.toByteArray());
            fos.flush();
            // 转为 MultipartFile
            return getFile(aimfile);
        }finally {
            // 删除临时文件
            aimfile.deleteOnExit();
            if(null != fos){
                try {fos.close(); }catch (Exception e){e.printStackTrace();}
            }
            if(null != bos){
                try {bos.close(); }catch (Exception e){e.printStackTrace();}
            }
            if(null != reader){
                try {reader.close(); }catch (Exception e){e.printStackTrace();}
            }
            aimfile.delete();
        }
    }

    /**
     * @Description
     * @param inputStream 输入流
     * @author yangqz
     */
    public static File asFile(InputStream inputStream,String pre,String suf) throws IOException {
        File tmp = File.createTempFile(pre, suf, new File("C:\\"));
        OutputStream os = new FileOutputStream(tmp);
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        inputStream.close();
        return tmp;
    }

    /**
     * @Description
     * @param inputStream 输入流
     * @author yangqz
     */
    public static File asFileTemp(InputStream inputStream, String pre,String suf) throws IOException {
        File tmp = File.createTempFile(pre, suf);
        OutputStream os = new FileOutputStream(tmp);
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        inputStream.close();
        return tmp;
    }

    /**
     * @Description File文件转为 MultipartFile
     * @param file
     * @author chenkw
     */
    public static MultipartFile getFile(File file) throws IOException {
        FileItem fileItem = new DiskFileItem("copyfile.txt", Files.probeContentType(file.toPath()),false,file.getName(),(int)file.length(),file.getParentFile());
        byte[] buffer = new byte[4096];
        int n;
        try (InputStream inputStream = new FileInputStream(file); OutputStream os = fileItem.getOutputStream()){
            while ( (n = inputStream.read(buffer,0,4096)) != -1){
                os.write(buffer,0,n);
            }
            //也可以用IOUtils.copy(inputStream,os);
            MultipartFile multipartFile = new CommonsMultipartFile(fileItem);
            return multipartFile;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

}
