/**
 * 海到尽头天作岸 山登绝顶我为峰
 *
 * @Author Administrator
 * @CreateTime 2026-05-15 13:57
 * @Description
 **/
package com.weiyj.admin.service;

import com.weiyj.admin.db.TagEntity;
import com.weiyj.admin.repository.TagRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@Transactional
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    // ==================== 创建操作 ====================

    /**
     * 创建标签
     *
     * @param tag 标签实体
     * @return 保存后的标签
     * @throws RuntimeException 如果标签名称或缩略名已存在
     */
    @Transactional
    public TagEntity createTag(TagEntity tag) {
        log.info("创建标签: name={}, slug={}", tag.getName(), tag.getSlug());

        // 检查名称唯一性
        if (tagRepository.existsByName(tag.getName())) {
            throw new RuntimeException("标签名称已存在: " + tag.getName());
        }

        // 检查缩略名唯一性
        if (tagRepository.findBySlug(tag.getSlug()).isPresent()) {
            throw new RuntimeException("缩略名已存在: " + tag.getSlug());
        }

        // 设置默认颜色（如果未设置）
        if (tag.getColor() == null || tag.getColor().isEmpty()) {
            tag.setColor("#409EFF");
        }

        TagEntity savedTag = tagRepository.save(tag);
        log.info("标签创建成功: id={}, name={}", savedTag.getId(), savedTag.getName());

        return savedTag;
    }

    /**
     * 批量创建标签
     *
     * @param tags 标签列表
     * @return 保存后的标签列表
     */
    @Transactional
    public List<TagEntity> batchCreateTags(List<TagEntity> tags) {
        log.info("批量创建标签, 数量: {}", tags.size());

        // 过滤掉重复的标签名称
        List<TagEntity> validTags = tags.stream()
                .filter(tag -> !tagRepository.existsByName(tag.getName()))
                .collect(Collectors.toList());

        // 设置默认颜色
        validTags.forEach(tag -> {
            if (tag.getColor() == null || tag.getColor().isEmpty()) {
                tag.setColor("#409EFF");
            }
        });

        List<TagEntity> savedTags = tagRepository.saveAll(validTags);
        log.info("批量创建标签成功, 实际创建: {}", savedTags.size());

        return savedTags;
    }

    // ==================== 更新操作 ====================

    /**
     * 更新标签
     *
     * @param id 标签ID
     * @param tag 标签更新信息
     * @return 更新后的标签
     * @throws RuntimeException 如果标签不存在或名称/缩略名冲突
     */
    @Transactional
    public TagEntity updateTag(Integer id, TagEntity tag) {
        log.info("更新标签: id={}, name={}", id, tag.getName());

        TagEntity existingTag = tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("标签不存在: " + id));

        // 检查名称唯一性（排除自身）
        if (!existingTag.getName().equals(tag.getName()) &&
                tagRepository.existsByNameAndIdNot(tag.getName(), id)) {
            throw new RuntimeException("标签名称已存在: " + tag.getName());
        }

        // 检查缩略名唯一性（排除自身）
        if (!existingTag.getSlug().equals(tag.getSlug()) &&
                tagRepository.existsBySlugAndIdNot(tag.getSlug(), id)) {
            throw new RuntimeException("缩略名已存在: " + tag.getSlug());
        }

        // 更新字段
        existingTag.setName(tag.getName());
        existingTag.setSlug(tag.getSlug());
        existingTag.setDescription(tag.getDescription());
        if (tag.getColor() != null && !tag.getColor().isEmpty()) {
            existingTag.setColor(tag.getColor());
        }

        TagEntity updatedTag = tagRepository.save(existingTag);
        log.info("标签更新成功: id={}, name={}", updatedTag.getId(), updatedTag.getName());

        return updatedTag;
    }

    /**
     * 批量更新标签颜色
     *
     * @param ids 标签ID列表
     * @param color 颜色代码
     * @return 更新数量
     */
    @Transactional
    public int batchUpdateColor(List<Integer> ids, String color) {
        log.info("批量更新标签颜色: ids={}, color={}", ids, color);
        int updatedCount = tagRepository.batchUpdateColor(ids, color);
        log.info("批量更新标签颜色完成, 更新数量: {}", updatedCount);
        return updatedCount;
    }

    // ==================== 删除操作 ====================

    /**
     * 删除标签
     *
     * @param id 标签ID
     * @throws RuntimeException 如果标签不存在
     */
    @Transactional
    public void deleteTag(Integer id) {
        log.info("删除标签: id={}", id);

        if (!tagRepository.existsById(id)) {
            throw new RuntimeException("标签不存在: " + id);
        }

        tagRepository.deleteById(id);
        log.info("标签删除成功: id={}", id);
    }

    /**
     * 批量删除标签
     *
     * @param ids 标签ID列表
     * @return 删除数量
     */
    @Transactional
    public int batchDeleteTags(List<Integer> ids) {
        log.info("批量删除标签: ids={}", ids);
        int deletedCount = tagRepository.batchDeleteByIds(ids);
        log.info("批量删除标签完成, 删除数量: {}", deletedCount);
        return deletedCount;
    }

    /**
     * 删除所有标签（慎用）
     *
     * @return 删除数量
     */
    @Transactional
    public int deleteAllTags() {
        log.warn("删除所有标签！");
        long count = tagRepository.count();
        tagRepository.deleteAll();
        log.info("已删除所有标签, 数量: {}", count);
        return (int) count;
    }

    // ==================== 查询操作 ====================

    /**
     * 根据ID查询标签
     *
     * @param id 标签ID
     * @return 标签信息
     * @throws RuntimeException 如果标签不存在
     */
    public TagEntity getTagById(Integer id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("标签不存在: " + id));
    }

    /**
     * 根据ID查询标签（可选）
     *
     * @param id 标签ID
     * @return 标签信息（可能为空）
     */
    public Optional<TagEntity> findTagById(Integer id) {
        return tagRepository.findById(id);
    }

    /**
     * 根据名称查询标签
     *
     * @param name 标签名称
     * @return 标签信息（可能为空）
     */
    public Optional<TagEntity> getTagByName(String name) {
        return tagRepository.findByName(name);
    }

    /**
     * 根据缩略名查询标签
     *
     * @param slug 缩略名
     * @return 标签信息（可能为空）
     */
    public Optional<TagEntity> getTagBySlug(String slug) {
        return tagRepository.findBySlug(slug);
    }

    /**
     * 查询所有标签
     *
     * @return 标签列表
     */
    public List<TagEntity> getAllTags() {
        log.debug("查询所有标签");
        return tagRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    /**
     * 分页查询所有标签
     *
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param direction 排序方向（asc/desc）
     * @return 分页结果
     */
    public Page<TagEntity> getAllTags(int page, int size, String sortBy, String direction) {
        log.debug("分页查询标签: page={}, size={}, sortBy={}, direction={}", page, size, sortBy, direction);

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(sortDirection, sortBy));

        return tagRepository.findAll(pageable);
    }

    /**
     * 分页查询标签（按创建时间倒序）
     *
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 分页结果
     */
    public Page<TagEntity> getTagsOrderByCreatedAtDesc(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return tagRepository.findAllOrderByCreatedAtDesc(pageable);
    }

    /**
     * 分页查询标签（按更新时间倒序）
     *
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 分页结果
     */
    public Page<TagEntity> getTagsOrderByUpdatedAtDesc(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return tagRepository.findAllOrderByUpdatedAtDesc(pageable);
    }

    // ==================== 搜索操作 ====================

    /**
     * 关键词搜索标签
     *
     * @param keyword 搜索关键词
     * @return 标签列表
     */
    public List<TagEntity> searchTags(String keyword) {
        log.debug("搜索标签: keyword={}", keyword);
        return tagRepository.searchByKeyword(keyword);
    }

    /**
     * 分页搜索标签
     *
     * @param keyword 搜索关键词
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 分页结果
     */
    public Page<TagEntity> searchTags(String keyword, int page, int size) {
        log.debug("分页搜索标签: keyword={}, page={}, size={}", keyword, page, size);
        Pageable pageable = PageRequest.of(page - 1, size);
        return tagRepository.searchByKeywordPage(keyword, pageable);
    }

    /**
     * 根据ID列表查询标签
     *
     * @param ids 标签ID列表
     * @return 标签列表
     */
    public List<TagEntity> getTagsByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return tagRepository.findByIdIn(ids);
    }

    /**
     * 根据名称列表查询标签
     *
     * @param names 标签名称列表
     * @return 标签列表
     */
    public List<TagEntity> getTagsByNames(List<String> names) {
        if (names == null || names.isEmpty()) {
            return List.of();
        }
        return tagRepository.findByNameIn(names);
    }

    // ==================== 统计操作 ====================

    /**
     * 获取标签总数
     *
     * @return 标签总数
     */
    public long getTotalTagCount() {
        return tagRepository.countAllTags();
    }

    /**
     * 获取最近N天新增标签数量
     *
     * @param days 天数
     * @return 新增数量
     */
    public long getRecentTagsCount(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return tagRepository.countRecentTags(since);
    }

    /**
     * 获取使用频率最高的标签
     *
     * @param limit 限制数量
     * @return 标签使用频率列表，每个元素包含 [id, name, slug, color, postCount]
     */
    public List<Object[]> getMostUsedTags(int limit) {
        log.debug("获取使用频率最高的 {} 个标签", limit);
        return tagRepository.findMostUsedTags(limit);
    }

    /**
     * 获取热门标签（封装为实体列表）
     *
     * @param limit 限制数量
     * @return 标签列表（包含使用次数）
     */
    public List<TagUsageVO> getHotTags(int limit) {
        List<Object[]> results = tagRepository.findMostUsedTags(limit);
        return results.stream()
                .map(row -> new TagUsageVO(
                        (Integer) row[0],
                        (String) row[1],
                        (String) row[2],
                        (String) row[3],
                        ((Number) row[4]).longValue()
                ))
                .collect(Collectors.toList());
    }

    // ==================== 关联查询操作 ====================

    /**
     * 根据文章ID查询标签列表
     *
     * @param postId 文章ID
     * @return 标签列表
     */
    public List<TagEntity> getTagsByPostId(Integer postId) {
        log.debug("根据文章ID查询标签: postId={}", postId);
        return tagRepository.findByPostId(postId);
    }

    /**
     * 获取孤儿标签（未被任何文章使用的标签）
     *
     * @return 孤儿标签列表
     */
    public List<TagEntity> getOrphanTags() {
        log.debug("获取孤儿标签");
        return tagRepository.findOrphanTags();
    }

    /**
     * 清理孤儿标签
     *
     * @return 清理数量
     */
    @Transactional
    public int cleanOrphanTags() {
        log.info("清理孤儿标签");
        List<TagEntity> orphanTags = tagRepository.findOrphanTags();
        if (orphanTags.isEmpty()) {
            log.info("没有孤儿标签需要清理");
            return 0;
        }

        List<Integer> orphanIds = orphanTags.stream()
                .map(TagEntity::getId)
                .collect(Collectors.toList());

        int deletedCount = tagRepository.batchDeleteByIds(orphanIds);
        log.info("清理孤儿标签完成, 清理数量: {}", deletedCount);
        return deletedCount;
    }

    // ==================== 验证操作 ====================

    /**
     * 检查标签名称是否存在
     *
     * @param name 标签名称
     * @return true-存在, false-不存在
     */
    public boolean checkNameExists(String name) {
        return tagRepository.existsByName(name);
    }

    /**
     * 检查缩略名是否存在
     *
     * @param slug 缩略名
     * @return true-存在, false-不存在
     */
    public boolean checkSlugExists(String slug) {
        return tagRepository.findBySlug(slug).isPresent();
    }

    /**
     * 检查标签名称是否存在（排除指定ID）
     *
     * @param name 标签名称
     * @param excludeId 排除的ID
     * @return true-存在, false-不存在
     */
    public boolean checkNameExistsExcludeId(String name, Integer excludeId) {
        return tagRepository.existsByNameAndIdNot(name, excludeId);
    }

    // ==================== 导出操作 ====================

    /**
     * 获取所有标签用于导出
     *
     * @return 标签列表（按名称排序）
     */
    public List<TagEntity> getAllTagsForExport() {
        log.info("获取所有标签用于导出");
        return tagRepository.findAllForExport();
    }

    // ==================== 内部类 ====================

    /**
     * 标签使用情况VO
     */
    @Getter
    @AllArgsConstructor
    public static class TagUsageVO {
        private final Integer id;
        private final String name;
        private final String slug;
        private final String color;
        private final Long postCount;

    }
}
