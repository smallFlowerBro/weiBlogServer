/**
 * 海到尽头天作岸 山登绝顶我为峰
 *
 * @Author Administrator
 * @CreateTime 2026-05-20 9:53
 * @Description
 **/
package com.weiyj.admin.db;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 文章分类实体类
 *
 * @author Blog System
 * @date 2026-05-20
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categories", indexes = {
        @Index(name = "idx_sort_order", columnList = "sort_order"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_created_at", columnList = "created_at")
})
public class CategoryEntity {

    /**
     * 分类ID（主键，自增）
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * 分类名称（唯一，不能为空）
     */
    @Column(name = "name", nullable = false, length = 50, unique = true)
    private String name;

    /**
     * 分类缩略名（用于URL，唯一，不能为空）
     */
    @Column(name = "slug", nullable = false, length = 100, unique = true)
    private String slug;

    /**
     * 分类描述
     */
    @Column(name = "description", length = 255)
    private String description;

    /**
     * 排序权重（数值越小越靠前）
     */
    @Column(name = "sort_order", nullable = false, columnDefinition = "smallint unsigned default 0")
    private Integer sortOrder;

    /**
     * 状态：1-启用，0-禁用
     */
    @Column(name = "status", nullable = false, columnDefinition = "tinyint unsigned default 1")
    private Integer status;

    /**
     * 创建时间（自动生成，不可更新）
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间（自动更新）
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ==================== 常量定义 ====================

    /**
     * 状态常量：启用
     */
    public static final int STATUS_ENABLED = 1;

    /**
     * 状态常量：禁用
     */
    public static final int STATUS_DISABLED = 0;

    /**
     * 默认排序权重
     */
    public static final int DEFAULT_SORT_ORDER = 0;

    // ==================== 辅助方法 ====================

    /**
     * 判断分类是否启用
     *
     * @return true-启用, false-禁用
     */
    public boolean isEnabled() {
        return STATUS_ENABLED == this.status;
    }


    /**
     * 系统默认分类
     * @return
     */
    public boolean isDefault(){
        return this.id==1;
    }

    /**
     * 启用分类
     */
    public void enable() {
        this.status = STATUS_ENABLED;
    }

    /**
     * 禁用分类
     */
    public void disable() {
        this.status = STATUS_DISABLED;
    }

    /**
     * 获取状态描述
     *
     * @return 状态描述字符串
     */
    public String getStatusText() {
        return isEnabled() ? "启用" : "禁用";
    }

    @Override
    public String toString() {
        return "CategoryEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                ", sortOrder=" + sortOrder +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
