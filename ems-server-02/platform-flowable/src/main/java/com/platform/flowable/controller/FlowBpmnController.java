package com.platform.flowable.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.flowable.service.IFlowBpmnService;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

/**
 * 流程图相关
 * @author qhq
 *
 */
@Slf4j
@Api(tags = "流程图相关")
@RestController
@RequestMapping("/flowable/bpmn")
@SuppressWarnings("all")
public class FlowBpmnController {
	
	@Autowired
	private IFlowBpmnService bpmnService;

	/**
	 * 获取流程当前流转情况
	 * @param businesskey 业务单据key
	 * @param response
	 */
	@GetMapping("/getImage/{businesskey}/{definitionKey}")
	public void getImage(@PathVariable String businesskey ,@PathVariable String definitionKey , HttpServletResponse response) {
		OutputStream os = null;
        BufferedImage image = null;
        try {
            image = ImageIO.read(bpmnService.readImage(businesskey,definitionKey));
            response.setContentType("image/png");
            os = response.getOutputStream();
            if (image != null) {
                ImageIO.write(image, "png", os);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.flush();
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
	
}
