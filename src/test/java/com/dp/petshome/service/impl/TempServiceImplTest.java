package com.dp.petshome.service.impl;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;

import com.dp.petshome.utils.EhCacheUtil;

public class TempServiceImplTest extends BaseJunitTest {

	@Autowired
	EhCacheUtil ehCacheUtil;

	@Test
	public void testEhCache() {
		ehCacheUtil.set("userCache", "a", "1");
		ehCacheUtil.set("userCache", "b", "2");
		ehCacheUtil.set("userCache", "c", "3");

		Cache cache = ehCacheUtil.get("userCache");
		ValueWrapper value = cache.get("a");
		String a = (String) value.get();
		System.out.println("a的缓存值为：" + a);

		String b = (String) ehCacheUtil.get("userCache", "b");
		System.out.println("b的缓存值为：" + b);
	}

}
