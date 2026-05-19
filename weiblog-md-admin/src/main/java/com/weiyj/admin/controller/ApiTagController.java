/**
 * 海到尽头天作岸 山登绝顶我为峰
 *
 * @Author Administrator
 * @CreateTime 2026-05-15 10:22
 * @Description
 **/
package com.weiyj.admin.controller;

import com.weiyj.admin.db.TagEntity;
import com.weiyj.admin.service.TagService;
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
@RequestMapping("/api/tags")
@Tag(name = "标签", description = "包含博客标签的创建 删除  修改等") // 替代 @Api
public class ApiTagController extends BaseController  {

    @Autowired
    private TagService tagService;


    @Operation(summary = "查询最近标签的汇总信息",description = "")
    @PostMapping("/wb2db_q_tags_info")
    @Transactional
    public Map wb2db_q_tags_info(HttpServletRequest request, HttpServletResponse response){
        try{
            HashMap<String, Object> data = new HashMap<>();

            //查询最近7天新增的标签
            long recentTagsCount = tagService.getRecentTagsCount(7);
            //标签总数
            long totalTagCount = tagService.getTotalTagCount();
            //查询有效的标签数
            long usableTagCount = totalTagCount;

            data.put("recent_tags_count",recentTagsCount);
            data.put("total_tag_count",totalTagCount);
            data.put("usable_tag_count",usableTagCount);

            return new Result(ResultCode.SUCCESS.code, ResultCode.SUCCESS.message,data);
        }catch (Exception e){
            log.error("获取标签汇总信息出错:"+e.getMessage());
            return new Result(ResultCode.FAIL.code,e.getMessage(),null);
        }
    }

    @Operation(summary = "按缩略名或者标签名检索标签",description = "")
    @PostMapping("/wb2db_q_tags_list")
    @Transactional
    public Map wb2db_q_tags_list(HttpServletRequest request, HttpServletResponse response, @RequestParam String keyword){
        try{
            HashMap<String, Object> data = new HashMap<>();

            //根据关键词检索tag列表
            List<TagEntity> tagEntities = tagService.searchTags(keyword);

            data.put("detail",tagEntities);

            return new Result(ResultCode.SUCCESS.code, ResultCode.SUCCESS.message,data);
        }catch (Exception e){
            log.error("获取标签汇总信息出错:"+e.getMessage());
            return new Result(ResultCode.FAIL.code,e.getMessage(),null);
        }
    }


    @Operation(summary = "创建新标签",description = "")
    @PostMapping("/wb2db_create_tag")
    @Transactional
    public Map wb2db_create_tag(HttpServletRequest request, HttpServletResponse response,
                                @RequestParam @NotNull(message = "tag名字不能为空") String name,
                                @RequestParam @NotNull(message = "tag缩略名不能为空") String slug,
                                @RequestParam String color,
                                @RequestParam String description
                                ){
        try{
            HashMap<String, Object> data = new HashMap<>();

            //根据关键词检索tag列表
            TagEntity tagEntity = new TagEntity();
            tagEntity.setName(name);
            tagEntity.setSlug(slug);
            tagEntity.setColor(color);
            tagEntity.setDescription(description);
            TagEntity tag = tagService.createTag(tagEntity);
            data.put("tagInfos",tag);

            return new Result(ResultCode.SUCCESS.code, ResultCode.SUCCESS.message,data);
        }catch (Exception e){
            log.error("新建标签出错:"+e.getMessage());
            return new Result(ResultCode.FAIL.code,e.getMessage(),null);
        }
    }

    @Operation(summary = "修改标签",description = "")
    @PostMapping("/wb2db_update_tag")
    @Transactional
    public Map wb2db_update_tag(HttpServletRequest request, HttpServletResponse response,
                                @RequestParam @NotNull(message = "tag id不能为空") int id,
                                @RequestParam @NotNull(message = "tag名字不能为空") String name,
                                @RequestParam @NotNull(message = "tag缩略名不能为空") String slug,
                                @RequestParam String color,
                                @RequestParam String description
                                ){
        try{
            HashMap<String, Object> data = new HashMap<>();

            //根据关键词检索tag列表
            TagEntity tagEntity = new TagEntity();
            tagEntity.setId(id);
            tagEntity.setName(name);
            tagEntity.setSlug(slug);
            tagEntity.setColor(color);
            tagEntity.setDescription(description);
            TagEntity tag = tagService.updateTag(tagEntity.getId(),tagEntity);
            data.put("tagInfos",tag);

            return new Result(ResultCode.SUCCESS.code, ResultCode.SUCCESS.message,data);
        }catch (Exception e){
            log.error("更新标签信息出错:"+e.getMessage());
            return new Result(ResultCode.FAIL.code,e.getMessage(),null);
        }
    }
    @Operation(summary = "删除标签",description = "")
    @PostMapping("/wb2db_delete_tag")
    @Transactional
    public Map wb2db_delete_tag(HttpServletRequest request, HttpServletResponse response,
                                @RequestParam @NotNull(message = "tag id不能为空") int id
                                ){
        try{
            HashMap<String, Object> data = new HashMap<>();

            tagService.deleteTag(id);

            return new Result(ResultCode.SUCCESS.code, ResultCode.SUCCESS.message,null);
        }catch (Exception e){
            log.error("删除标签id:{}信息出错: {}",id,e.getMessage());
            return new Result(ResultCode.FAIL.code,e.getMessage(),null);
        }
    }
    @Operation(summary = "批量删除标签",description = "")
    @PostMapping("/wb2db_batch_delete_tag")
    @Transactional
    public Map wb2db_batch_delete_tag(HttpServletRequest request, HttpServletResponse response,
                                @RequestParam @NotNull(message = "tag id集合不能为空") List<Integer> ids
                                ){
        try{
            System.out.println(ids);
            tagService.batchDeleteTags(ids);

            return new Result(ResultCode.SUCCESS.code, ResultCode.SUCCESS.message,null);
        }catch (Exception e){
            log.error("批量删除标签id:{}信息出错: {}",ids,e.getMessage());
            return new Result(ResultCode.FAIL.code,e.getMessage(),null);
        }
    }







}
