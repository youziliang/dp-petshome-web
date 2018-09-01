package com.dp.petshome.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * @Description 变量操作工具类
 * @author DU
 */
public class PropertyUtil extends PropertyPlaceholderConfigurer {
    
	private static Map<String, String> ctxPropertiesMap;

	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props)
			throws BeansException {
		super.processProperties(beanFactoryToProcess, props);
		ctxPropertiesMap = new HashMap<String, String>(64);
		for (Object key : props.keySet()) {
			String keyStr = key.toString();
			String value = props.getProperty(keyStr);
			ctxPropertiesMap.put(keyStr, value);
		}
	}
    
    /**
     * 
    * @Title: getProperty 
    * @Description 根据键值获取Property文件中的值 
    * @param key 键值
    * @return
    * @throws
     */
	public static String getProperty(String key) {
		if (ctxPropertiesMap == null) {
			ctxPropertiesMap = new HashMap<String, String>(64);
		}
		return ctxPropertiesMap.get(key);
	}

    /**
     * 
    * @Title: getProperty 
    * @Description 根据键值获取Property文件中的值，若无对应属性值，则返回指定默认值 
    * @param key 键值
    * @param defaultValue 默认值
    * @return
    * @throws
     */
	public static String getProperty(String key, String defaultValue) {
		if (ctxPropertiesMap == null) {
			ctxPropertiesMap = new HashMap<String, String>(64);
		}
		String value = ctxPropertiesMap.get(key);
		return (value == null) ? defaultValue : value;
	}

}
