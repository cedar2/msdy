package com.platform.ems.eSignApp.constant;

/**
 * e签宝API接口地址
 *
 * @author chenkw
 */
public class SignApiPath {

    /**
     * e签宝地址前缀
     * 模拟环境https: //smlopenapi.esign.cn
     * 正式环境https: //openapi.esign.cn
     */
    public static final String E_SIGN_HOST = "https://smlopenapi.esign.cn";

    /**
     * sign-flow 接口前缀
     */
    public static final String V3_SIGN_FLOW = "/v3/sign-flow/";

    /**
     * 查询机构认证信息
     */
    public static final String ORG_IDENTITY_INFO = "/v3/organizations/identity-info?orgName=";

    /**
     * 获取机构认证&授权页面链接
     */
    public static final String ORG_AUTH_URL = "/v3/org-auth-url";

    /**
     * 通过页面发起签署-获取发起签署可视化页面
     */
    public static final String SIGN_FLOW_INITIATE_URL_BY_FILE = "/v3/sign-flow/sign-flow-initiate-url/by-file";

    /**
     * 查询签署流程列表
     */
    public static final String SIGN_FLOW_LIST = "/v3/sign-flow/sign-flow-list";

    /**
     * 下载已签署文件及附属材料-后缀（前缀是 V3_SIGN_FLOW ）
     */
    public static final String FILE_DOWNLOAD_URL = "/file-download-url";

    /**
     * 获取文件上传地址
     */
    public static final String FILE_UPLOAD_URL = "/v3/files/file-upload-url";

    /**
     * 查询文件上传状态 /path文件ID
     */
    public static final String FILE_STATUS = "/v3/files/";

    /**
     * 基于文件发起签署
     */
    public static final String SIGN_FLOW_CREATE_BY_FILE = "/v3/sign-flow/create-by-file";
}
