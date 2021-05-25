//package com.atguigu.gulimall.product.config;
//
//
//import org.springframework.beans.factory.ObjectProvider;
//import org.springframework.boot.autoconfigure.AutoConfigureAfter;
//import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
//import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
//import org.springframework.boot.autoconfigure.cache.CacheProperties;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.boot.autoconfigure.couchbase.CouchbaseAutoConfiguration;
//import org.springframework.boot.autoconfigure.data.jpa.EntityManagerFactoryDependsOnPostProcessor;
//import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
//import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration;
//import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.cache.interceptor.CacheAspectSupport;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Import;
//
//import javax.cache.CacheManager;
//import java.util.stream.Collectors;
//
//// 缓存自动配置源码
//@Configuration(proxyBeanMethods = false)
//@ConditionalOnClass(CacheManager.class)
//@ConditionalOnBean(CacheAspectSupport.class)
//@ConditionalOnMissingBean(value = CacheManager.class, name = "cacheResolver")
//@EnableConfigurationProperties(CacheProperties.class)
//@AutoConfigureAfter({ CouchbaseAutoConfiguration.class, HazelcastAutoConfiguration.class,
//        HibernateJpaAutoConfiguration.class, RedisAutoConfiguration.class })
//@Import({CacheConfigurationImportSelector.class, // 看导入什么CacheConfiguration
//        CacheAutoConfiguration.CacheManagerEntityManagerFactoryDependsOnPostProcessor.class })
//public class CacheAutoConfiguration {
//
//    @Bean
//    @ConditionalOnMissingBean
//    public CacheManagerCustomizers cacheManagerCustomizers(ObjectProvider<CacheManagerCustomizer<?>> customizers) {
//        return new CacheManagerCustomizers(customizers.orderedStream().collect(Collectors.toList()));
//    }
//
//    @Bean
//    public CacheManagerValidator cacheAutoConfigurationValidator(CacheProperties cacheProperties,
//                                                                 ObjectProvider<CacheManager> cacheManager) {
//        return new CacheManagerValidator(cacheProperties, cacheManager);
//    }
//
//    @ConditionalOnClass(LocalContainerEntityManagerFactoryBean.class)
//    @ConditionalOnBean(AbstractEntityManagerFactoryBean.class)
//    static class CacheManagerEntityManagerFactoryDependsOnPostProcessor
//            extends EntityManagerFactoryDependsOnPostProcessor {
//
//        CacheManagerEntityManagerFactoryDependsOnPostProcessor() {
//            super("cacheManager");
//        }
//
//    }
//}
//
