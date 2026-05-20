/**
 * 海到尽头天作岸 山登绝顶我为峰
 *
 * @Author Administrator
 * @CreateTime 2026-05-15 10:23
 * @Description
 **/
package com.weiyj.admin.controller;

import com.weiyj.admin.db.CategoryEntity;
import com.weiyj.admin.service.CategoryService;
import com.weiyj.common.controller.BaseController;
import com.weiyj.common.response.Result;
import com.weiyj.common.response.ResultCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/api/categories")
@Tag(name = "分类", description = "包含分类的创建 删除  修改等") // 替代 @Api
public class ApiCategoriesController extends BaseController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新建分类
     * @param request
     * @param response
     * @param name
     * @param slug
     * @param description
     * @param sortOrder
     * @param status
     * @return
     */
    @Operation(summary = "新建分类信息",description = "")
    @PostMapping("/wb2db_create_category")
    @Transactional
    public Map wb2db_create_category(HttpServletRequest request, HttpServletResponse response,
                                     @RequestParam @NotNull(message = "name不得为空") String name,
                                     @RequestParam @NotNull(message = "slug不得为空") String slug,
                                     @RequestParam String description,
                                     @RequestParam @NotNull(message = "sordOrder不得为空" ) Integer sortOrder,
                                     @RequestParam @NotNull(message = "status启用状态不得为空") Integer status

                                     ){
        try{
            HashMap<String, Object> data = new HashMap<>();
            CategoryEntity categoryEntity = new CategoryEntity();
            categoryEntity.setName(name);
            categoryEntity.setSlug(slug);
            categoryEntity.setDescription(description);
            categoryEntity.setSortOrder(sortOrder);
            categoryEntity.setStatus(status);
            CategoryEntity category = categoryService.createCategory(categoryEntity);
            data.put("categoryInfos",category);
            return new Result(ResultCode.SUCCESS.code, ResultCode.SUCCESS.message,data);
        }catch (Exception e){
            log.error("获取标签汇总信息出错:"+e.getMessage());
            return new Result(ResultCode.FAIL.code,e.getMessage(),null);
        }
    }

    /**
     * 更新指定id 得分类
     * @param request
     * @param response
     * @param id
     * @param name
     * @param slug
     * @param description
     * @param sortOrder
     * @param status
     * @return
     */
    @Operation(summary = "更新分类信息",description = "")
    @PostMapping("/wb2db_update_category")
    @Transactional
    public Map wb2db_update_category(HttpServletRequest request, HttpServletResponse response,
                                     @RequestParam @NotNull(message = "id不得为空") Integer id,
                                     @RequestParam @NotNull(message = "name不得为空") String name,
                                     @RequestParam @NotNull(message = "slug不得为空") String slug,
                                     @RequestParam String description,
                                     @RequestParam @NotNull(message = "sordOrder不得为空" ) Integer sortOrder,
                                     @RequestParam @NotNull(message = "status启用状态不得为空") Integer status
                                     ){
        try{
            HashMap<String, Object> data = new HashMap<>();
            CategoryEntity categoryEntity = new CategoryEntity();
            categoryEntity.setId(id);
            categoryEntity.setName(name);
            categoryEntity.setSlug(slug);
            categoryEntity.setDescription(description);
            categoryEntity.setSortOrder(sortOrder);
            categoryEntity.setStatus(status);
            CategoryEntity category = categoryService.updateCategory(id,categoryEntity);
            data.put("categoryInfos",category);
            return new Result(ResultCode.SUCCESS.code, ResultCode.SUCCESS.message,data);
        }catch (Exception e){
            log.error("获取标签汇总信息出错:"+e.getMessage());
            return new Result(ResultCode.FAIL.code,e.getMessage(),null);
        }
    }


    /**
     * 查询分类列表
     * @param request
     * @param response
     * @return
     */
    @Operation(summary = "查询分类列表",description = "")
    @PostMapping("/wb2db_q_category_list")
    @Transactional
    public Map wb2db_q_category_list(HttpServletRequest request, HttpServletResponse response
                                     ){
        try{
            HashMap<String, Object> data = new HashMap<>();
            List<CategoryEntity> allCategories = categoryService.getAllCategories();
            data.put("details",allCategories);
            return new Result(ResultCode.SUCCESS.code, ResultCode.SUCCESS.message,data);
        }catch (Exception e){
            log.error("获取标签汇总信息出错:"+e.getMessage());
            return new Result(ResultCode.FAIL.code,e.getMessage(),null);
        }
    }


    /**
     * 删除指定id得分类
     * @param request
     * @param response
     * @param id
     * @return
     */
    @Operation(summary = "删除分类",description = "")
    @PostMapping("/wb2db_delete_category")
    @Transactional
    public Map wb2db_delete_category(HttpServletRequest request, HttpServletResponse response,
                                     @RequestParam @NotNull(message = "分类id不得为空") Integer id
                                     ){
        try{
            categoryService.deleteCategory(id);
            return new Result(ResultCode.SUCCESS.code, ResultCode.SUCCESS.message,null);
        }catch (Exception e){
            log.error("获取标签汇总信息出错:"+e.getMessage());
            return new Result(ResultCode.FAIL.code,e.getMessage(),null);
        }
    }

    /**
     * 批量删除指定id得分类
     * @param request
     * @param response
     * @param ids
     * @return
     */
    @Operation(summary = "批量删除指定id得分类",description = "")
    @PostMapping("/wb2db_batch_delete_category")
    @Transactional
    public Map wb2db_batch_delete_category(HttpServletRequest request, HttpServletResponse response,
                                     @RequestParam @NotNull(message = "分类id不得为空") List<Integer> ids
                                     ){
        try{
            categoryService.batchDeleteCategories(ids);
            return new Result(ResultCode.SUCCESS.code, ResultCode.SUCCESS.message,null);
        }catch (Exception e){
            log.error("获取标签汇总信息出错:"+e.getMessage());
            return new Result(ResultCode.FAIL.code,e.getMessage(),null);
        }
    }

}
