/**
 * 海到尽头天作岸 山登绝顶我为峰
 *
 * @Author Administrator
 * @CreateTime 2026-05-20 10:27
 * @Description
 **/
package com.weiyj.admin.service;

import com.weiyj.admin.db.CategoryEntity;
import com.weiyj.admin.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 分类服务层
 *
 * @author Blog System
 * @date 2026-05-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // ==================== 创建操作 ====================

    /**
     * 创建分类
     *
     * @param categoryEntity 创建信息
     * @return 创建后的分类VO
     * @throws RuntimeException 如果分类名称或缩略名已存在
     */
    @Transactional
    public CategoryEntity createCategory(CategoryEntity categoryEntity) {
        log.info("创建分类: name={}, slug={}", categoryEntity.getName(), categoryEntity.getSlug());

        // 检查名称唯一性
        if (categoryRepository.existsByName(categoryEntity.getName())) {
            throw new RuntimeException("分类名称已存在: " + categoryEntity.getName());
        }

        // 检查缩略名唯一性
        if (categoryRepository.existsBySlug(categoryEntity.getSlug())) {
            throw new RuntimeException("缩略名已存在: " + categoryEntity.getSlug());
        }


        CategoryEntity savedEntity = categoryRepository.save(categoryEntity);
        log.info("分类创建成功: id={}, name={}", savedEntity.getId(), savedEntity.getName());

        return savedEntity;
    }


    // ==================== 更新操作 ====================

    /**
     * 更新分类
     *
     * @param id 分类ID
     * @param updateDTO 更新信息
     * @return 更新后的分类VO
     * @throws RuntimeException 如果分类不存在或名称/缩略名冲突
     */
    @Transactional
    public CategoryEntity updateCategory(Integer id, CategoryEntity updateDTO) {
        log.info("更新分类: id={}, name={}", id, updateDTO.getName());

        CategoryEntity existingEntity = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在: " + id));

        // 检查名称唯一性（排除自身）
        if (!existingEntity.getName().equals(updateDTO.getName()) &&
                categoryRepository.existsByNameAndIdNot(updateDTO.getName(), id)) {
            throw new RuntimeException("分类名称已存在: " + updateDTO.getName());
        }

        // 检查缩略名唯一性（排除自身）
        if (!existingEntity.getSlug().equals(updateDTO.getSlug()) &&
                categoryRepository.existsBySlugAndIdNot(updateDTO.getSlug(), id)) {
            throw new RuntimeException("缩略名已存在: " + updateDTO.getSlug());
        }

        // 更新字段
        existingEntity.setName(updateDTO.getName());
        existingEntity.setSlug(updateDTO.getSlug());
        if (updateDTO.getDescription() != null) {
            existingEntity.setDescription(updateDTO.getDescription());
        }
        if (updateDTO.getSortOrder() != null) {
            existingEntity.setSortOrder(updateDTO.getSortOrder());
        }
        if (updateDTO.getStatus() != null) {
            existingEntity.setStatus(updateDTO.getStatus());
        }

        CategoryEntity updatedEntity = categoryRepository.save(existingEntity);
        log.info("分类更新成功: id={}, name={}", updatedEntity.getId(), updatedEntity.getName());

        return updatedEntity;
    }

    /**
     * 批量更新分类状态
     *
     * @param ids 分类ID列表
     * @param status 状态（1-启用，0-禁用）
     * @return 更新数量
     */
    @Transactional
    public int batchUpdateStatus(List<Integer> ids, Integer status) {
        log.info("批量更新分类状态: ids={}, status={}", ids, status);
        int updatedCount = categoryRepository.batchUpdateStatus(ids, status);
        log.info("批量更新分类状态完成, 更新数量: {}", updatedCount);
        return updatedCount;
    }

    /**
     * 批量更新分类排序权重
     *
     * @param ids 分类ID列表
     * @param sortOrder 排序权重
     * @return 更新数量
     */
    @Transactional
    public int batchUpdateSortOrder(List<Integer> ids, Integer sortOrder) {
        log.info("批量更新分类排序权重: ids={}, sortOrder={}", ids, sortOrder);
        int updatedCount = categoryRepository.batchUpdateSortOrder(ids, sortOrder);
        log.info("批量更新分类排序权重完成, 更新数量: {}", updatedCount);
        return updatedCount;
    }

    /**
     * 交换两个分类的排序权重
     *
     * @param id1 分类ID1
     * @param id2 分类ID2
     */
    @Transactional
    public void swapSortOrder(Integer id1, Integer id2) {
        Optional<CategoryEntity> byId1 = categoryRepository.findById(id1);
        Optional<CategoryEntity> byId2 = categoryRepository.findById(id2);
        if (byId1==null){
            throw new RuntimeException("未找到"+id1+"分类");
        }
        if (byId2==null){
            throw new RuntimeException("未找到"+id2+"分类");
        }
        CategoryEntity categoryEntity1 = byId1.get();
        CategoryEntity categoryEntity2 = byId2.get();

        int temp_sort = categoryEntity1.getSortOrder();
        categoryEntity1.setSortOrder(categoryEntity2.getSortOrder());
        categoryEntity2.setSortOrder(temp_sort);
        categoryRepository.save(categoryEntity1);
        categoryRepository.save(categoryEntity2);
    }

    /**
     * 启用分类
     *
     * @param id 分类ID
     */
    @Transactional
    public void enableCategory(Integer id) {
        log.info("启用分类: id={}", id);
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在: " + id));
        entity.enable();
        categoryRepository.save(entity);
        log.info("分类启用成功: id={}", id);
    }

    /**
     * 禁用分类
     *
     * @param id 分类ID
     */
    @Transactional
    public void disableCategory(Integer id) {
        log.info("禁用分类: id={}", id);
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在: " + id));
        entity.disable();
        categoryRepository.save(entity);
        log.info("分类禁用成功: id={}", id);
    }

    // ==================== 删除操作 ====================

    /**
     * 删除分类
     *
     * @param id 分类ID
     * @throws RuntimeException 如果分类不存在
     */
    @Transactional
    public void deleteCategory(Integer id) {
        log.info("删除分类: id={}", id);

        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("分类不存在: " + id);
        }

        categoryRepository.deleteById(id);
        log.info("分类删除成功: id={}", id);
    }

    /**
     * 批量删除分类
     *
     * @param ids 分类ID列表
     * @return 删除数量
     */
    @Transactional
    public int batchDeleteCategories(List<Integer> ids) {
        log.info("批量删除分类: ids={}", ids);
        int deletedCount = categoryRepository.batchDeleteByIds(ids);
        log.info("批量删除分类完成, 删除数量: {}", deletedCount);
        return deletedCount;
    }

    /**
     * 删除所有分类（慎用）
     *
     * @return 删除数量
     */
    @Transactional
    public int deleteAllCategories() {
        log.warn("删除所有分类！");
        long count = categoryRepository.count();
        categoryRepository.deleteAll();
        log.info("已删除所有分类, 数量: {}", count);
        return (int) count;
    }

    // ==================== 查询操作 ====================

    /**
     * 根据ID查询分类
     *
     * @param id 分类ID
     * @return 分类VO
     * @throws RuntimeException 如果分类不存在
     */
    public CategoryEntity getCategoryById(Integer id) {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在: " + id));
        return entity;
    }

    /**
     * 根据ID查询分类（可选）
     *
     * @param id 分类ID
     * @return 分类信息（可能为空）
     */
    public Optional<CategoryEntity> findCategoryById(Integer id) {
        return categoryRepository.findById(id);
    }

    /**
     * 根据名称查询分类
     *
     * @param name 分类名称
     * @return 分类信息（可能为空）
     */
    public Optional<CategoryEntity> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    /**
     * 根据缩略名查询分类
     *
     * @param slug 缩略名
     * @return 分类信息（可能为空）
     */
    public Optional<CategoryEntity> getCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug);
    }

    /**
     * 查询所有分类
     *
     * @return 分类VO列表
     */
    public List<CategoryEntity> getAllCategories() {
        log.debug("查询所有分类");
        List<CategoryEntity> entities = categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "sortOrder"));
        return entities;
    }

    /**
     * 查询所有启用的分类
     *
     * @return 分类VO列表
     */
    public List<CategoryEntity> getAllEnabledCategories() {
        log.debug("查询所有启用的分类");
        List<CategoryEntity> entities = categoryRepository.findAllEnabled();
        return entities;
    }

    /**
     * 分页查询所有分类
     *
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param direction 排序方向（asc/desc）
     * @return 分页结果
     */
    public Page<CategoryEntity> getAllCategories(int page, int size, String sortBy, String direction) {
        log.debug("分页查询分类: page={}, size={}, sortBy={}, direction={}", page, size, sortBy, direction);

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(sortDirection, sortBy));

        Page<CategoryEntity> entityPage = categoryRepository.findAll(pageable);
        return entityPage;
    }

    /**
     * 分页查询分类（按排序权重升序）
     *
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 分页结果
     */
    public Page<CategoryEntity> getCategoriesOrderBySortOrder(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<CategoryEntity> entityPage = categoryRepository.findAllOrderBySortOrder(pageable);
        return entityPage;
    }

    /**
     * 分页查询启用的分类
     *
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 分页结果
     */
    public Page<CategoryEntity> getEnabledCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<CategoryEntity> entityPage = categoryRepository.findAllEnabled(pageable);
        return entityPage;
    }

    // ==================== 搜索操作 ====================

    /**
     * 关键词搜索分类
     *
     * @param keyword 搜索关键词
     * @return 分类VO列表
     */
    public List<CategoryEntity> searchCategories(String keyword) {
        log.debug("搜索分类: keyword={}", keyword);
        if (!StringUtils.hasText(keyword)) {
            return getAllCategories();
        }
        List<CategoryEntity> entities = categoryRepository.searchByKeyword(keyword);
        return  entities;
    }

    /**
     * 分页搜索分类
     *
     * @param keyword 搜索关键词
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 分页结果
     */
    public Page<CategoryEntity> searchCategories(String keyword, int page, int size) {
        log.debug("分页搜索分类: keyword={}, page={}, size={}", keyword, page, size);

        if (!StringUtils.hasText(keyword)) {
            return getCategoriesOrderBySortOrder(page, size);
        }

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<CategoryEntity> entityPage = categoryRepository.searchByKeywordPage(keyword, pageable);
        return entityPage;
    }

    /**
     * 根据ID列表查询分类
     *
     * @param ids 分类ID列表
     * @return 分类VO列表
     */
    public List<CategoryEntity> getCategoriesByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        List<CategoryEntity> entities = categoryRepository.findByIdIn(ids);
        return entities;
    }

    // ==================== 统计操作 ====================

    /**
     * 获取分类总数
     *
     * @return 分类总数
     */
    public long getTotalCategoryCount() {
        return categoryRepository.countAllCategories();
    }

    /**
     * 获取启用的分类数量
     *
     * @return 启用的分类数量
     */
    public long getEnabledCategoryCount() {
        return categoryRepository.countEnabledCategories();
    }

    /**
     * 获取禁用的分类数量
     *
     * @return 禁用的分类数量
     */
    public long getDisabledCategoryCount() {
        return categoryRepository.countDisabledCategories();
    }

    /**
     * 获取最近N天新增分类数量
     *
     * @param days 天数
     * @return 新增数量
     */
    public long getRecentCategoriesCount(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return categoryRepository.countRecentCategories(since);
    }

    /**
     * 获取最大排序权重
     *
     * @return 最大排序权重
     */
    public Integer getMaxSortOrder() {
        return categoryRepository.getMaxSortOrder();
    }

    /**
     * 获取最小排序权重
     *
     * @return 最小排序权重
     */
    public Integer getMinSortOrder() {
        return categoryRepository.getMinSortOrder();
    }

    // ==================== 关联查询操作 ====================

    // ==================== 验证操作 ====================

    /**
     * 检查分类名称是否存在
     *
     * @param name 分类名称
     * @return true-存在, false-不存在
     */
    public boolean checkNameExists(String name) {
        return categoryRepository.existsByName(name);
    }

    /**
     * 检查缩略名是否存在
     *
     * @param slug 缩略名
     * @return true-存在, false-不存在
     */
    public boolean checkSlugExists(String slug) {
        return categoryRepository.existsBySlug(slug);
    }

    /**
     * 检查分类名称是否存在（排除指定ID）
     *
     * @param name 分类名称
     * @param excludeId 排除的ID
     * @return true-存在, false-不存在
     */
    public boolean checkNameExistsExcludeId(String name, Integer excludeId) {
        return categoryRepository.existsByNameAndIdNot(name, excludeId);
    }

    /**
     * 检查缩略名是否存在（排除指定ID）
     *
     * @param slug 缩略名
     * @param excludeId 排除的ID
     * @return true-存在, false-不存在
     */
    public boolean checkSlugExistsExcludeId(String slug, Integer excludeId) {
        return categoryRepository.existsBySlugAndIdNot(slug, excludeId);
    }

    // ==================== 导出操作 ====================

    /**
     * 获取所有分类用于导出
     *
     * @return 分类VO列表
     */
    public List<CategoryEntity> getAllCategoriesForExport() {
        log.info("获取所有分类用于导出");
        List<CategoryEntity> entities = categoryRepository.findAllForExport();
        return entities;
    }

    /**
     * 获取指定状态的分类用于导出
     *
     * @param status 状态
     * @return 分类VO列表
     */
    public List<CategoryEntity> getCategoriesByStatusForExport(Integer status) {
        log.info("获取指定状态的分类用于导出: status={}", status);
        List<CategoryEntity> entities = categoryRepository.findAllByStatusForExport(status);
        return entities;
    }

    // ==================== 内部类 ====================

    /**
     * 分类文章统计VO
     */
    public static class CategoryArticleStatsVO {
        private final Integer id;
        private final String name;
        private final String slug;
        private final String description;
        private final Integer sortOrder;
        private final Integer status;
        private final Long articleCount;

        public CategoryArticleStatsVO(Integer id, String name, String slug, String description,
                                      Integer sortOrder, Integer status, Long articleCount) {
            this.id = id;
            this.name = name;
            this.slug = slug;
            this.description = description;
            this.sortOrder = sortOrder;
            this.status = status;
            this.articleCount = articleCount;
        }

        public Integer getId() { return id; }
        public String getName() { return name; }
        public String getSlug() { return slug; }
        public String getDescription() { return description; }
        public Integer getSortOrder() { return sortOrder; }
        public Integer getStatus() { return status; }
        public Long getArticleCount() { return articleCount; }
    }
}