package com.changhong.bems.service.mq;

import com.changhong.bems.commons.Constants;
import com.changhong.bems.dto.OrderStatus;
import com.changhong.bems.service.OrderService;
import com.changhong.sei.core.context.SessionUser;
import com.changhong.sei.core.context.mock.LocalMockUser;
import com.changhong.sei.core.context.mock.MockUser;
import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.core.util.JsonUtils;
import com.changhong.sei.util.thread.ThreadLocalHolder;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 实现功能：
 *
 * @author 马超(Vision.Mac)
 * @version 1.0.00  2021-05-15 17:34
 */
@Component
public class EffectiveOrderConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(EffectiveOrderConsumer.class);

    private final OrderService orderService;

    public EffectiveOrderConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 处理收到的监听消息
     *
     * @param record 消息纪录
     */
    @KafkaListener(topics = "${sei.mq.topic}")
    public void processMessage(ConsumerRecord<String, String> record) {
        if (Objects.isNull(record)) {
            return;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("received key='{}' message = '{}'", record.key(), record.value());
        }
        // 执行业务处理逻辑
        String orderId = null;
        // 操作类型
        String operation = null;
        try {
            ThreadLocalHolder.begin();

            String message = record.value();
            EffectiveOrderMessage orderMessage = JsonUtils.fromJson(message, EffectiveOrderMessage.class);

            // 模拟用户
            MockUser mockUser = new LocalMockUser();
            SessionUser sessionUser = new SessionUser();
            sessionUser.setTenantCode(orderMessage.getTenantCode());
            sessionUser.setUserId(orderMessage.getUserId());
            sessionUser.setAccount(orderMessage.getAccount());
            sessionUser.setUserName(orderMessage.getUserName());
            mockUser.mock(sessionUser);
//            MockUserHelper.mockUser(orderMessage.getTenantCode(), orderMessage.getAccount());

            orderId = orderMessage.getOrderId();
            operation = orderMessage.getOperation();
            ResultData<Void> resultData;
            if (Constants.ORDER_OPERATION_EFFECTIVE.equals(operation)) {
                resultData = orderService.effective(orderId);
                if (LOG.isInfoEnabled()) {
                    LOG.info("预算申请单生效结果: {}", resultData);
                }
            } else if (Constants.ORDER_OPERATION_COMPLETE.equals(operation)) {
                resultData = orderService.completeProcess(orderId);
                if (LOG.isInfoEnabled()) {
                    LOG.info("预算申请单流程完成处理结果: {}", resultData);
                }
            }
        } catch (Exception e) {
            LOG.error("预算申请单生效处理异常.", e);
            if (Constants.ORDER_OPERATION_EFFECTIVE.equals(operation)) {
                // 异常时,回滚状态为:草稿
                orderService.updateStatus(orderId, OrderStatus.DRAFT);
            }
        } finally {
            // 释放资源
            ThreadLocalHolder.end();
        }
    }
}
