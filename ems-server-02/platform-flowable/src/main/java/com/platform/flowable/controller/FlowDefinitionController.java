package com.platform.flowable.controller;

import com.platform.common.core.domain.AjaxResult;
import com.platform.system.domain.dto.FlowProcDefDto;
import com.platform.flowable.domain.dto.FlowSaveXmlVo;
import com.platform.flowable.service.IFlowDefinitionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 工作流程定义
 * </p>
 *
 * @author c
 */
@Slf4j
@Api(tags = "流程定义")
@RestController
@RequestMapping("/flowable/definition")
@SuppressWarnings("all")
public class FlowDefinitionController {

    @Autowired
    private IFlowDefinitionService flowDefinitionService;


    @PostMapping(value = "/list")
    @ApiOperation(value = "流程定义列表",
                  response = FlowProcDefDto.class)
    public AjaxResult list(@RequestBody Map<String, String> map) {
        return AjaxResult.success(flowDefinitionService.list(map));
    }


    @ApiOperation(value = "导入流程文件",
                  notes = "上传bpmn20的xml文件")
    @PostMapping("/import")
    public AjaxResult importFile(@RequestParam(required = false) String name,
                                 @RequestParam(required = false) String category,
                                 MultipartFile file) {
        InputStream in = null;
        try {
            in = file.getInputStream();
            return flowDefinitionService.importFile(name, category, in);
        } catch (Exception e) {
            log.error("导入失败:", e);
            return AjaxResult.error("导入流程文件失败，原因: " + e.getStackTrace()[0]);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                log.error("关闭输入流出错", e);
            }
        }
    }


    @ApiOperation(value = "读取xml文件")
    @GetMapping("/readXml/{deployId}")
    public AjaxResult readXml(@ApiParam(value = "流程定义id") @PathVariable(value = "deployId") String deployId) {
        try {
            return flowDefinitionService.readXml(deployId);
        } catch (Exception e) {
            return AjaxResult.error("加载xml文件异常");
        }

    }

    @ApiOperation(value = "读取图片文件")
    @GetMapping("/readImage/{deployId}")
    public void readImage(@ApiParam(value = "流程定义id") @PathVariable(value = "deployId") String deployId,
                          HttpServletResponse response) {
        OutputStream os = null;
        BufferedImage image = null;
        try {
            image = ImageIO.read(flowDefinitionService.readImage(deployId));
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


    @ApiOperation(value = "保存流程设计器内的xml文件")
    @PostMapping("/save")
    public AjaxResult save(@RequestBody FlowSaveXmlVo vo) {
        InputStream in = null;
        try {
            in = new ByteArrayInputStream(vo.getXml().getBytes(StandardCharsets.UTF_8));
            flowDefinitionService.importFile(vo.getName(), vo.getCategory(), in);
        } catch (Exception e) {
            log.error("保存失败:", e);
            return AjaxResult.success(e.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                log.error("关闭输入流出错", e);
            }
        }

        return AjaxResult.success("保存成功");
    }


    @ApiOperation(value = "激活或挂起流程定义")
    @PutMapping(value = "/updateState")
    public AjaxResult updateState(@ApiParam(value = "1:激活,2:挂起",
                                            required = true) @RequestParam Integer state,
                                  @ApiParam(value = "流程部署ID",
                                            required = true) @RequestParam String deployId) {
        flowDefinitionService.updateState(state, deployId);
        return AjaxResult.success();
    }

    @ApiOperation(value = "删除流程")
    @DeleteMapping(value = "/delete")
    public AjaxResult delete(@ApiParam(value = "流程部署ID",
                                       required = true) @RequestParam String deployId) {
        flowDefinitionService.delete(deployId);
        return AjaxResult.success();
    }

    @GetMapping("/getProcessDefitionByKey/{defKey}/{businessKey}")
    public void getProcessDefitionByKey(@PathVariable String defKey, @PathVariable String businessKey) {
        flowDefinitionService.getProcessDefitionByKey(defKey, businessKey);
    }

    @PostMapping("/getProcessAllUserTask/{definitionId}")
    public List<Map<String, String>> getProcessAllUserTask(@PathVariable String definitionId) {
        return flowDefinitionService.getAllUserTask(definitionId);
    }


/*    @ApiOperation(value = "指定流程办理人员列表")
    @GetMapping("/userList")
    public AjaxResult userList(SysUser user) {
        List<SysUser> list = userService.selectUserList(user);
        return AjaxResult.success(list);
    }

    @ApiOperation(value = "指定流程办理组列表")
    @GetMapping("/roleList")
    public AjaxResult roleList(SysRole role) {
        List<SysRole> list = sysRoleService.selectRoleList(role);
        return AjaxResult.success(list);
    }*/

}
