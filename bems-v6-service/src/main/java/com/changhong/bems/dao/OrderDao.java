package com.changhong.bems.dao;

import com.changhong.bems.dto.OrderCategory;
import com.changhong.bems.dto.OrderStatus;
import com.changhong.bems.entity.Order;
import com.changhong.sei.core.dao.BaseEntityDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 预算申请单(Order)数据库访问类
 *
 * @author sei
 * @since 2021-04-25 15:13:57
 */
@Repository
public interface OrderDao extends BaseEntityDao<Order> {

    /**
     * 检查是否存在指定类型的预制单
     *
     * @param creatorId 创建人
     * @param category  分类
     * @param status    预制状态
     * @return 返回检查结果
     */
    List<Order> findTop10ByCreatorIdAndOrderCategoryAndStatus(String creatorId, OrderCategory category, OrderStatus status);

    /**
     * 更新订单申请金额
     *
     * @param id     订单id
     * @param amount 金额
     */
    @Modifying
    @Query("update Order o set o.applyAmount = :amount where o.id = :id ")
    void updateAmount(@Param("id") String id, @Param("amount") double amount);

    /**
     * 更新订单申请金额
     *
     * @param id 订单id
     */
    @Modifying
    @Query("update Order o set o.applyAmount = (select sum(d.amount) from OrderDetail d where d.orderId = o.id) where o.id = :id ")
    void updateAmount(@Param("id") String id);

    /**
     * 更新订单状态
     *
     * @param id     订单id
     * @param status 状态
     */
    @Modifying
    @Query("update Order o set o.status = :status where o.id = :id ")
    void updateStatus(@Param("id") String id, @Param("status") OrderStatus status);

    /**
     * 更新订单是否手动生效
     *
     * @param id                订单id
     * @param manuallyEffective 是否手动生效
     */
    @Modifying
    @Query("update Order o set o.manuallyEffective = :manuallyEffective where o.id = :id ")
    void manuallyEffective(@Param("id") String id, @Param("manuallyEffective") boolean manuallyEffective);

    /**
     * 更新订单是否正在异步处理行项数据
     * 如果是,在编辑时进入socket状态显示页面
     *
     * @param id         订单id
     * @param processing 是否正在异步处理行项数据
     */
    @Modifying
    @Query("update Order o set o.processing = :processing where o.id = :id ")
    void setProcessStatus(@Param("id") String id, @Param("processing") boolean processing);

    /**
     * 更新订单是否正在异步处理行项数据
     * 如果是,在编辑时进入socket状态显示页面
     *
     * @param id         订单id
     * @param processing 是否正在异步处理行项数据
     */
    @Modifying
    @Query("update Order o set o.status = :status, o.processing = :processing where o.id = :id ")
    void updateOrderStatus(@Param("id") String id, @Param("status") OrderStatus status, @Param("processing") boolean processing);
}