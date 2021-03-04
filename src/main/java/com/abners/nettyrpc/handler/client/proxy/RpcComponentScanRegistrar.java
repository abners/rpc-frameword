package com.abners.nettyrpc.handler.client.proxy;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

/**
 * 类RpcComponentScan.java的实现描述：TODO 类实现描述
 *
 * @author baoxing.peng 2021年03月03日 16:43:51
 */
@Component
public class RpcComponentScanRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 获取扫描包路径
        Set<String> packagesToScan = getPackagesToScan(importingClassMetadata);
        // 注册@service解析的类
//        registerServiceAnnotationBeanPostProcessor(packagesToScan, registry);
        // 注册解析@Reference注解的bean
        registerReferenceAnnotationBeanPostProcessor(registry);

    }

    private void registerReferenceAnnotationBeanPostProcessor(BeanDefinitionRegistry registry) {
        String beanName = "rpcReferenceAnnotationBeanPostProcessor";
        if (!registry.containsBeanDefinition(beanName)) {
            RootBeanDefinition beanDefinition = new RootBeanDefinition(RpcReferenceAnnotationBeanPostProcessor.class);
            beanDefinition.setRole(2);
            registry.registerBeanDefinition(beanName, beanDefinition);
        }

    }

    private Set<String> getPackagesToScan(AnnotationMetadata metadata) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                metadata.getAnnotationAttributes(ComponentScan.class.getName()));
        String[] basePackages = attributes.getStringArray("basePackages");
        Class<?>[] basePackageClasses = attributes.getClassArray("basePackageClasses");
        String[] value = attributes.getStringArray("value");
        Set<String> packagesToScan = new LinkedHashSet(Arrays.asList(value));
        packagesToScan.addAll(Arrays.asList(basePackages));
        Class[] arr$ = basePackageClasses;
        int len$ = basePackageClasses.length;

        for (int i$ = 0; i$ < len$; ++i$) {
            Class<?> basePackageClass = arr$[i$];
            packagesToScan.add(ClassUtils.getPackageName(basePackageClass));
        }

        return (Set) (packagesToScan.isEmpty() ? Collections.singleton(
                ClassUtils.getPackageName(metadata.getClassName())) : packagesToScan);
    }

}
