package com.dp.petshome.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;

import com.dp.petshome.utils.EhCacheUtil;
import com.dp.petshome.utils.ImageUtil;


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

	@Test
	public void testCompressPic() throws IOException {
		File file = new File("C:\\Users\\Desig\\Desktop\\" + "banner_1.png");
		Map<Integer, Map<String, Object>> params = new HashMap<>();
		Map<String, Object> param = new HashMap<>();
		param.put(ImageUtil.RATIO, 0.6);
		params.put(ImageUtil.SCALE, param);
		ImageUtil.createThumb(file, params);
	}

	@Test
	public void testFile() throws IOException {
		File file = new File("C:\\Users\\Desig\\Desktop\\" + "banner_1.png");
		System.out.println(file.getPath());
		System.out.println(file.getParent());
		System.out.println(file.getName());
	}

	@Test
	public void testExtractNumber() throws IOException {
		String str = "style='width: 338px; float: left;' class='note-float-left'";
		String regEx = "[^\\d]";
		Pattern patternp = Pattern.compile(regEx);
		Matcher matcher = patternp.matcher(str);
		System.out.println("截取出的數字為：" + matcher.replaceAll("").trim());
	}

	@Test
	public void testConcurrentHashMap() {
		AbstractMap<Object, Object> map = new ConcurrentHashMap<>();
		map.put("test", "test");
	}
}
