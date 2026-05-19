/**
 * 海到尽头天作岸 山登绝顶我为峰
 *
 * @Author Administrator
 * @CreateTime 2026-05-15 10:53
 * @Description
 **/
package com.weiyj.admin.repository;
import com.weiyj.admin.db.TagEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 标签数据访问层
 *
 * @author Blog System
 * @date 2026-05-15
 */
@Repository
public interface TagRepository extends JpaRepository<TagEntity, Integer> {

    // ==================== 基础查询方法 ====================

    /**
     * 根据标签名称查询
     * @param name 标签名称
     * @return 标签信息
     */
    Optional<TagEntity> findByName(String name);

    /**
     * 根据缩略名查询
     * @param slug 缩略名
     * @return 标签信息
     */
    Optional<TagEntity> findBySlug(String slug);

    /**
     * 检查标签名称是否存在
     * @param name 标签名称
     * @return true-存在, false-不存在
     */
    boolean existsByName(String name);

    /**
     * 检查缩略名是否存在（排除指定ID）
     * @param slug 缩略名
     * @param id 排除的ID
     * @return true-存在, false-不存在
     */
    @Query(value = "SELECT COUNT(t) > 0 FROM TagEntity t WHERE t.slug = :slug AND t.id != :id")
    boolean existsBySlugAndIdNot(@Param("slug") String slug, @Param("id") Integer id);

    /**
     * 检查标签名称是否存在（排除指定ID）
     * @param name 标签名称
     * @param id 排除的ID
     * @return true-存在, false-不存在
     */
    @Query(value = "SELECT COUNT(t) > 0 FROM TagEntity t WHERE t.name = :name AND t.id != :id")
    boolean existsByNameAndIdNot(@Param("name") String name, @Param("id") Integer id);

    // ==================== 批量查询方法 ====================

    /**
     * 根据标签名称列表批量查询
     * @param names 标签名称列表
     * @return 标签列表
     */
    List<TagEntity> findByNameIn(List<String> names);

    /**
     * 根据ID列表批量查询
     * @param ids ID列表
     * @return 标签列表
     */
    List<TagEntity> findByIdIn(List<Integer> ids);

    /**
     * 根据标签名称模糊查询
     * @param keyword 关键词
     * @return 标签列表
     */
    @Query(value = "SELECT t FROM TagEntity t WHERE (:keyword IS NULL OR t.name LIKE CONCAT('%', :keyword, '%') )OR (:keyword IS NULL OR t.slug LIKE CONCAT('%', :keyword, '%')) ")
    List<TagEntity> searchByKeyword(@Param("keyword") String keyword);

    // ==================== 分页查询方法 ====================

    /**
     * 分页查询所有标签（按创建时间倒序）
     * @param pageable 分页参数
     * @return 分页结果
     */
    @Query(value = "SELECT t FROM TagEntity t ORDER BY t.createdAt DESC")
    Page<TagEntity> findAllOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 分页查询所有标签（按更新时间倒序）
     * @param pageable 分页参数
     * @return 分页结果
     */
    @Query(value = "SELECT t FROM TagEntity t ORDER BY t.updatedAt DESC")
    Page<TagEntity> findAllOrderByUpdatedAtDesc(Pageable pageable);

    /**
     * 分页搜索标签
     * @param keyword 搜索关键词
     * @param pageable 分页参数
     * @return 分页结果
     */
    @Query(value = "SELECT t FROM TagEntity t WHERE t.name LIKE CONCAT('%', :keyword, '%') OR t.slug LIKE CONCAT('%', :keyword, '%') ORDER BY t.createdAt DESC")
    Page<TagEntity> searchByKeywordPage(@Param("keyword") String keyword, Pageable pageable);

    // ==================== 统计查询方法 ====================

    /**
     * 统计标签总数
     * @return 标签总数
     */
    @Query(value = "SELECT COUNT(t) FROM TagEntity t")
    long countAllTags();

    /**
     * 统计最近N天新增的标签数量
     * @param since 时间
     * @return 新增数量
     */
    @Query(value = "SELECT COUNT(t) FROM TagEntity t WHERE t.createdAt >= :since")
    long countRecentTags(@Param("since") LocalDateTime since);


    /**
     * 获取使用频率最高的标签（关联文章表）
     * @param limit 限制数量
     * @return 标签使用频率列表
     */
    @Query(value = "SELECT t.id, t.name, t.slug, t.color, COUNT(pt.post_id) as post_count " +
            "FROM tags t " +
            "LEFT JOIN post_tags pt ON t.id = pt.tag_id " +
            "GROUP BY t.id " +
            "ORDER BY post_count DESC, t.name ASC " +
            "LIMIT :limit", nativeQuery = true)
    List<Object[]> findMostUsedTags(@Param("limit") int limit);

    // ==================== 更新操作 ====================

    /**
     * 批量更新标签颜色
     * @param ids 标签ID列表
     * @param color 颜色代码
     * @return 更新数量
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE TagEntity t SET t.color = :color, t.updatedAt = CURRENT_TIMESTAMP WHERE t.id IN :ids")
    int batchUpdateColor(@Param("ids") List<Integer> ids, @Param("color") String color);

    /**
     * 批量删除标签
     * @param ids 标签ID列表
     * @return 删除数量
     */
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM tags WHERE id IN :ids",nativeQuery = true)
    int batchDeleteByIds(@Param("ids") List<Integer> ids);

    // ==================== 关联查询方法 ====================

    /**
     * 根据文章ID查询标签列表
     * @param postId 文章ID
     * @return 标签列表
     */
    @Query(value = "SELECT * FROM tags t JOIN post_tags pt ON t.id = pt.tagId WHERE pt.postId = :postId ORDER BY t.name ASC",nativeQuery = true)
    List<TagEntity> findByPostId(@Param("postId") Integer postId);

    /**
     * 获取未被任何文章使用的标签（孤儿标签）
     * @return 标签列表
     */
    @Query(value = "SELECT t.* FROM tags t " +
            "LEFT JOIN post_tags pt ON t.id = pt.tag_id " +
            "WHERE pt.tag_id IS NULL", nativeQuery = true)
    List<TagEntity> findOrphanTags();

    // ==================== 导出相关方法 ====================

    /**
     * 获取所有标签用于导出（按名称排序）
     * @return 标签列表
     */
    @Query(value = "SELECT * FROM tags t ORDER BY t.name ASC",nativeQuery = true)
    List<TagEntity> findAllForExport();
}
