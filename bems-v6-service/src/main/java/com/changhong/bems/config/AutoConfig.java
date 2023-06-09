package com.changhong.bems.config;

import com.changhong.bems.service.PoolService;
import com.changhong.bems.service.strategy.*;
import com.changhong.bems.service.strategy.impl.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 实现功能：
 *
 * @author 马超(Vision.Mac)
 * @version 1.0.00  2021-05-05 22:50
 */
@EnableAsync
@EnableScheduling
@Configuration
public class AutoConfig {
//    private final RedisConnectionFactory redisConnectionFactory;
//
//    //////////////////redis mq config start/////////////////////
//
//    @Autowired
//    public AutoConfig(RedisConnectionFactory redisConnectionFactory) {
//        this.redisConnectionFactory = redisConnectionFactory;
//    }
//
//    /**
//     * 配置消息监听器
//     */
//    @Bean
//    public OrderStateSubscribeListener orderStateListener(Cache<String, String> memoryCache) {
//        return new OrderStateSubscribeListener(memoryCache);
//    }
//
//    /**
//     * 将消息监听器绑定到消息容器
//     */
//    @Bean
//    public RedisMessageListenerContainer messageListenerContainer(Cache<String, String> memoryCache) {
//        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//        container.setConnectionFactory(redisConnectionFactory);
//
//        //  MessageListener 监听数据
//        container.addMessageListener(orderStateListener(memoryCache), ChannelTopic.of(Constants.TOPIC));
//        return container;
//    }
//
//    //////////////////redis mq config end/////////////////////
//
//    @Bean
//    public Cache<String, String> memoryCache() {
//        return CacheBuilder.newBuilder()
//                // 设置缓存最大容量为100，超过100之后就会按照LRU最近最少使用算法来移除缓存项
//                .maximumSize(512)
//                // 设置写缓存后3秒钟过期  最后一次写入后的一段时间移出
//                .expireAfterWrite(5, TimeUnit.SECONDS)
//
//                // 设置并发级别为8，并发级别是指可以同时写缓存的线程数
//                .concurrencyLevel(16)
//                // 设置缓存容器的初始容量为10
//                .initialCapacity(10)
//                .build();
//    }

    /**
     * 一致性维度匹配策略
     */
    @Bean
    @ConditionalOnMissingBean
    public EqualMatchStrategy equalMatchStrategy() {
        return new DefaultEqualMatchStrategy();
    }

    /**
     * 组织树路径维度匹配策略
     */
    @Bean
    @ConditionalOnMissingBean
    public OrgTreeMatchStrategy orgTreeMatchStrategy() {
        return new DefaultOrgTreeMatchStrategy();
    }

    /**
     * 期间关系维度匹配策略
     */
    @Bean
    @ConditionalOnMissingBean
    public PeriodMatchStrategy periodMatchStrategy() {
        return new DefaultPeriodMatchStrategy();
    }

    /**
     * 余额范围内强制控制策略
     */
    @Bean
    @ConditionalOnMissingBean
    public LimitExecutionStrategy limitExecutionStrategy(PoolService poolService) {
        return new DefaultLimitExecutionStrategy(poolService);
    }

    /**
     * 允许超额使用控制策略
     */
    @Bean
    @ConditionalOnMissingBean
    public ExcessExecutionStrategy excessExecutionStrategy(PoolService poolService) {
        return new DefaultExcessExecutionStrategy(poolService);
    }

    /**
     * 同期间范围内总额控制策略
     */
    @Bean
    @ConditionalOnMissingBean
    public SamePeriodTotalLimitExecutionStrategy samePeriodTotalLimitExecutionStrategy(PoolService poolService) {
        return new DefaultSamePeriodTotalLimitExecutionStrategy(poolService);
    }

}
