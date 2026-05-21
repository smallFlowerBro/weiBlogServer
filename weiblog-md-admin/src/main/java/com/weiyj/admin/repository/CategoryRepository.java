/**
 * 海到尽头天作岸 山登绝顶我为峰
 *
 * @Author Administrator
 * @CreateTime 2026-05-20 10:00
 * @Description
 **/
package com.weiyj.admin.repository;


import com.weiyj.admin.db.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 分类数据访问层
 *
 * @author Blog System
 * @date 2026-05-20
 */
@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer>, JpaSpecificationExecutor<CategoryEntity> {

    // ==================== 基础查询方法 ====================

    /**
     * 根据分类名称查询
     * @param name 分类名称
     * @return 分类信息
     */
    Optional<CategoryEntity> findByName(String name);

    /**
     * 根据缩略名查询
     * @param slug 缩略名
     * @return 分类信息
     */
    Optional<CategoryEntity> findBySlug(String slug);

    /**
     * 检查分类名称是否存在
     * @param name 分类名称
     * @return true-存在, false-不存在
     */
    boolean existsByName(String name);

    /**
     * 检查缩略名是否存在
     * @param slug 缩略名
     * @return true-存在, false-不存在
     */
    boolean existsBySlug(String slug);

    /**
     * 检查分类名称是否存在（排除指定ID）
     * @param name 分类名称
     * @param id 排除的ID
     * @return true-存在, false-不存在
     */
    @Query("SELECT COUNT(c) > 0 FROM CategoryEntity c WHERE c.name = :name AND c.id != :id")
    boolean existsByNameAndIdNot(@Param("name") String name, @Param("id") Integer id);

    /**
     * 检查缩略名是否存在（排除指定ID）
     * @param slug 缩略名
     * @param id 排除的ID
     * @return true-存在, false-不存在
     */
    @Query("SELECT COUNT(c) > 0 FROM CategoryEntity c WHERE c.slug = :slug AND c.id != :id")
    boolean existsBySlugAndIdNot(@Param("slug") String slug, @Param("id") Integer id);

    // ==================== 批量查询方法 ====================

    /**
     * 根据ID列表批量查询
     * @param ids ID列表
     * @return 分类列表
     */
    List<CategoryEntity> findByIdIn(List<Integer> ids);

    /**
     * 根据状态查询
     * @param status 状态（1-启用，0-禁用）
     * @return 分类列表
     */
    List<CategoryEntity> findByStatus(Integer status);

    /**
     * 查询所有启用的分类（按排序权重升序）
     * @return 分类列表
     */
    @Query("SELECT c FROM CategoryEntity c WHERE c.status = 1 ORDER BY c.sortOrder ASC, c.createdAt DESC")
    List<CategoryEntity> findAllEnabled();

    /**
     * 根据排序权重范围查询
     * @param minSort 最小排序权重
     * @param maxSort 最大排序权重
     * @return 分类列表
     */
    List<CategoryEntity> findBySortOrderBetween(Integer minSort, Integer maxSort);

    // ==================== 分页查询方法 ====================

    /**
     * 分页查询启用的分类
     * @param pageable 分页参数
     * @return 分页结果
     */
    @Query("SELECT c FROM CategoryEntity c WHERE c.status = 1 ORDER BY c.sortOrder ASC, c.createdAt DESC")
    Page<CategoryEntity> findAllEnabled(Pageable pageable);

    /**
     * 分页查询按排序权重排序
     * @param pageable 分页参数
     * @return 分页结果
     */
    @Query("SELECT c FROM CategoryEntity c ORDER BY c.sortOrder ASC, c.createdAt DESC")
    Page<CategoryEntity> findAllOrderBySortOrder(Pageable pageable);

    /**
     * 分页查询按创建时间倒序
     * @param pageable 分页参数
     * @return 分页结果
     */
    @Query("SELECT c FROM CategoryEntity c ORDER BY c.createdAt DESC")
    Page<CategoryEntity> findAllOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 分页查询按更新时间倒序
     * @param pageable 分页参数
     * @return 分页结果
     */
    @Query("SELECT c FROM CategoryEntity c ORDER BY c.updatedAt DESC")
    Page<CategoryEntity> findAllOrderByUpdatedAtDesc(Pageable pageable);

    // ==================== 搜索方法 ====================

    /**
     * 关键词模糊搜索（名称、缩略名、描述）
     * @param keyword 搜索关键词
     * @return 分类列表
     */
    @Query("SELECT c FROM CategoryEntity c WHERE c.name LIKE CONCAT('%', :keyword, '%') OR c.slug LIKE CONCAT('%', :keyword, '%') OR c.description LIKE CONCAT('%', :keyword, '%') ORDER BY c.sortOrder ASC")
    List<CategoryEntity> searchByKeyword(@Param("keyword") String keyword);

    /**
     * 分页关键词搜索
     * @param keyword 搜索关键词
     * @param pageable 分页参数
     * @return 分页结果
     */
    @Query("SELECT c FROM CategoryEntity c WHERE c.name LIKE CONCAT('%', :keyword, '%') OR c.slug LIKE CONCAT('%', :keyword, '%') OR c.description LIKE CONCAT('%', :keyword, '%') ORDER BY c.sortOrder ASC")
    Page<CategoryEntity> searchByKeywordPage(@Param("keyword") String keyword, Pageable pageable);

    // ==================== 统计查询方法 ====================

    /**
     * 统计分类总数
     * @return 分类总数
     */
    @Query("SELECT COUNT(c) FROM CategoryEntity c")
    long countAllCategories();

    /**
     * 统计启用的分类数量
     * @return 启用的分类数量
     */
    @Query("SELECT COUNT(c) FROM CategoryEntity c WHERE c.status = 1")
    long countEnabledCategories();

    /**
     * 统计禁用的分类数量
     * @return 禁用的分类数量
     */
    @Query("SELECT COUNT(c) FROM CategoryEntity c WHERE c.status = 0")
    long countDisabledCategories();

    /**
     * 统计最近N天新增的分类数量
     * @param since 起始时间
     * @return 新增数量
     */
    @Query("SELECT COUNT(c) FROM CategoryEntity c WHERE c.createdAt >= :since")
    long countRecentCategories(@Param("since") LocalDateTime since);

    /**
     * 统计最近N天新增的分类数量（使用 SPEL 表达式）
     * @param days 天数
     * @return 新增数量
     */
    @Query("SELECT COUNT(c) FROM CategoryEntity c WHERE c.createdAt >= :#{T(java.time.LocalDateTime).now().minusDays(#days)}")
    long countRecentCategoriesByDays(@Param("days") int days);

    /**
     * 统计最近N天新增的分类数量（原生SQL）
     * @param days 天数
     * @return 新增数量
     */
    @Query(value = "SELECT COUNT(*) FROM categories WHERE created_at >= DATE_SUB(NOW(), INTERVAL :days DAY)", nativeQuery = true)
    long countRecentCategoriesNative(@Param("days") int days);

    // ==================== 更新操作 ====================

    /**
     * 批量更新分类状态
     * @param ids 分类ID列表
     * @param status 状态（1-启用，0-禁用）
     * @return 更新数量
     */
    @Modifying
    @Transactional
    @Query("UPDATE CategoryEntity c SET c.status = :status, c.updatedAt = CURRENT_TIMESTAMP WHERE c.id IN :ids")
    int batchUpdateStatus(@Param("ids") List<Integer> ids, @Param("status") Integer status);

    /**
     * 批量更新分类排序权重
     * @param ids 分类ID列表
     * @param sortOrder 排序权重
     * @return 更新数量
     */
    @Modifying
    @Transactional
    @Query("UPDATE CategoryEntity c SET c.sortOrder = :sortOrder, c.updatedAt = CURRENT_TIMESTAMP WHERE c.id IN :ids")
    int batchUpdateSortOrder(@Param("ids") List<Integer> ids, @Param("sortOrder") Integer sortOrder);

    /**
     * 批量删除分类
     * @param ids 分类ID列表
     * @return 删除数量
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM CategoryEntity c WHERE c.id IN :ids")
    int batchDeleteByIds(@Param("ids") List<Integer> ids);

    /**
     * 根据状态批量更新
     * @param oldStatus 旧状态
     * @param newStatus 新状态
     * @return 更新数量
     */
    @Modifying
    @Transactional
    @Query("UPDATE CategoryEntity c SET c.status = :newStatus, c.updatedAt = CURRENT_TIMESTAMP WHERE c.status = :oldStatus")
    int batchUpdateStatusByCondition(@Param("oldStatus") Integer oldStatus, @Param("newStatus") Integer newStatus);

    // ==================== 排序相关方法 ====================

    /**
     * 获取最大排序权重
     * @return 最大排序权重
     */
    @Query("SELECT COALESCE(MAX(c.sortOrder), 0) FROM CategoryEntity c")
    Integer getMaxSortOrder();

    /**
     * 获取最小排序权重
     * @return 最小排序权重
     */
    @Query("SELECT COALESCE(MIN(c.sortOrder), 0) FROM CategoryEntity c")
    Integer getMinSortOrder();


    // ==================== 关联查询方法 ====================

    /**
     * 获取有文章的分类（非空分类）
     * @return 分类列表
     */
    @Query(value = "SELECT DISTINCT c.* FROM categories c " +
            "INNER JOIN articles a ON c.id = a.category_id " +
            "WHERE c.status = 1 " +
            "ORDER BY c.sort_order ASC", nativeQuery = true)
    List<CategoryEntity> findNonEmptyCategories();

    /**
     * 获取空分类（没有文章的分类）
     * @return 分类列表
     */
    @Query(value = "SELECT c.* FROM categories c " +
            "LEFT JOIN articles a ON c.id = a.category_id " +
            "WHERE a.id IS NULL " +
            "ORDER BY c.sort_order ASC", nativeQuery = true)
    List<CategoryEntity> findEmptyCategories();

    // ==================== 导出方法 ====================

    /**
     * 获取所有分类用于导出（按排序权重升序）
     * @return 分类列表
     */
    @Query("SELECT c FROM CategoryEntity c ORDER BY c.sortOrder ASC, c.createdAt DESC")
    List<CategoryEntity> findAllForExport();

    /**
     * 获取指定状态的所有分类用于导出
     * @param status 状态
     * @return 分类列表
     */
    @Query("SELECT c FROM CategoryEntity c WHERE c.status = :status ORDER BY c.sortOrder ASC")
    List<CategoryEntity> findAllByStatusForExport(@Param("status") Integer status);
}