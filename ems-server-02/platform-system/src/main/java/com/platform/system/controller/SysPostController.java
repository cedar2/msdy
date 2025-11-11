package com.platform.system.controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.platform.common.constant.UserConstants;
import com.platform.common.core.domain.model.LoginUser;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.system.domain.SysPost;
import com.platform.system.service.ISysPostService;

/**
 * 岗位信息操作处理
 *
 * @author platform
 */
@RestController
@RequestMapping("/post")
public class SysPostController extends BaseController {
    @Autowired
    private ISysPostService postService;

    /**
     * 获取岗位列表
     */
    @GetMapping("/list")
    public TableDataInfo list(SysPost post) {
        LoginUser loginUser = ApiThreadLocalUtil.get();
        startPage();
        if (loginUser != null && loginUser.getUserid() != null && loginUser.getUserid() != 1L) {
            post.setClientId(loginUser.getClientId());
        }
        List<SysPost> list = postService.selectPostList(post);
        return getDataTable(list);
    }

    @PostMapping("/export")
    public void export(HttpServletResponse response, SysPost post) throws IOException {
        LoginUser loginUser = ApiThreadLocalUtil.get();
        if (loginUser != null && loginUser.getUserid() != null && loginUser.getUserid() != 1L) {
            post.setClientId(loginUser.getClientId());
        }
        List<SysPost> list = postService.selectPostList(post);
        ExcelUtil<SysPost> util = new ExcelUtil<SysPost>(SysPost.class);
        util.exportExcel(response, list, "岗位数据");
    }

    /**
     * 根据岗位编号获取详细信息
     */
    @GetMapping(value = "/{postId}")
    public AjaxResult getInfo(@PathVariable Long postId) {
        return AjaxResult.success(postService.selectPostById(postId));
    }

    /**
     * 新增岗位
     */
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysPost post) {
        LoginUser loginUser = ApiThreadLocalUtil.get();
        post.setClientId(loginUser.getClientId());
        if (UserConstants.NOT_UNIQUE_NUM.equals(postService.checkPostNameUnique(post))) {
            return AjaxResult.error("新增岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        } else if (UserConstants.NOT_UNIQUE_NUM.equals(postService.checkPostCodeUnique(post))) {
            return AjaxResult.error("新增岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        post.setCreateBy(loginUser.getUsername());
        return toAjax(postService.insertPost(post));
    }

    /**
     * 修改岗位
     */
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysPost post) {
        if (UserConstants.NOT_UNIQUE_NUM.equals(postService.checkPostNameUnique(post))) {
            return AjaxResult.error("修改岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        } else if (UserConstants.NOT_UNIQUE_NUM.equals(postService.checkPostCodeUnique(post))) {
            return AjaxResult.error("修改岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        post.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(postService.updatePost(post));
    }

    /**
     * 删除岗位
     */
    @DeleteMapping("/{postIds}")
    public AjaxResult remove(@PathVariable Long[] postIds) {
        return toAjax(postService.deletePostByIds(postIds));
    }

    /**
     * 获取岗位选择框列表
     */
    @GetMapping("/optionselect")
    public AjaxResult optionselect() {
        List<SysPost> posts;
        LoginUser loginUser = ApiThreadLocalUtil.get();
        if (loginUser != null && loginUser.getUserid() != null && loginUser.getUserid() != 1L) {
            posts = postService.selectPostAll(loginUser.getClientId());
        } else {
            posts = postService.selectPostAll(null);
        }
        return AjaxResult.success(posts);
    }
}
