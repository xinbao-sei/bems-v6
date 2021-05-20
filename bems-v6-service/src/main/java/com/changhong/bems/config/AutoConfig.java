package com.changhong.bems.config;

import com.changhong.bems.service.strategy.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 实现功能：
 *
 * @author 马超(Vision.Mac)
 * @version 1.0.00  2021-05-05 22:50
 */
@EnableAsync
@Configuration
public class AutoConfig {

    @Bean
    @ConditionalOnMissingBean
    public DimensionMatchStrategy equalStrategy() {
        return new DefaultEqualMatchStrategy();
    }

    @Bean
    @ConditionalOnMissingBean
    public DimensionMatchStrategy treeMatchStrategy() {
        return new DefaultOrgTreeMatchStrategy();
    }

    @Bean
    @ConditionalOnMissingBean
    public DimensionMatchStrategy periodMatchStrategy() {
        return new DefaultPeriodMatchStrategy();
    }
}
