package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class BasMaterialPicture {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialSid;

    @ApiModelProperty(value = "物料（商品/服务）编码")
    private String materialCode;

    @ApiModelProperty(value = "物料（商品/服务）名称")
    private String materialName;

    @ApiModelProperty(value = "主图片路径")
    private String picturePath;

    @ApiModelProperty(value = "副图片路径（多图）")
    private String picturePathSecond;

    @ApiModelProperty(value = "图片路径（副图）")
    private String[] picturePathSecondList;

    @ApiModelProperty(value = "取视频附件")
    private List<BasMaterialVideo> attachmentList;

    @Data
    @Accessors(chain = true)
    public static class BasMaterialVideo {

        @ApiModelProperty(value = "系统ID-版型附件信息")
        @JsonSerialize(using = ToStringSerializer.class)
        private Long materialAttachmentSid;

        @ApiModelProperty(value = "类型")
        private String fileType;

        @ApiModelProperty(value = "文件名")
        private String fileName;

        @ApiModelProperty(value = "附件路径")
        private String filePath;

    }

}
