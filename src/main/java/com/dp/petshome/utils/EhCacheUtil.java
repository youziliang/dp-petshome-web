package com.dp.petshome.utils;

import javax.annotation.Resource;

import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Component;

@Component
public class EhCacheUtil {

	@Resource(name = "cacheManager")
	private EhCacheCacheManager cacheManager;

	public void set(String cacheName, String key, Object value) {
		Cache cache = cacheManager.getCache(cacheName);
		cache.put(key, value);
	}

	public Object get(String cacheName, String key) {
		Cache cache = cacheManager.getCache(cacheName);
		ValueWrapper value = cache.get(key);
		return value == null ? null : value.get();
	}

	public Cache get(String cacheName) {
		return cacheManager.getCache(cacheName);
	}

	public void remove(String cacheName, String key) {
		Cache cache = cacheManager.getCache(cacheName);
		cache.evict(key);
	}
}
