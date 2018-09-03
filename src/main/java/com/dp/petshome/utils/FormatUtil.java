package com.dp.petshome.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

import com.alibaba.fastjson.JSONObject;

/**
 * @Description 格式轉換工具类
 */
public class FormatUtil {

	/**
	 * @Description Object转换成Map
	 * @param obj
	 */
	public static Map<String, Object> obj2Map(Object obj) throws Exception {
		if (obj == null) {
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();

		BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		for (PropertyDescriptor property : propertyDescriptors) {
			String key = property.getName();
			if (key.compareToIgnoreCase("class") == 0) {
				continue;
			}
			Method getter = property.getReadMethod();
			Object value = getter != null ? getter.invoke(obj) : null;
			if (null == value) {
				continue;
			}
			map.put(key, value);
		}
		return map;
	}

	/**
	 * @Description XML TO Map
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> xml2Map(String strXML) throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();
		Document document = DocumentHelper.parseText(strXML);
		Element root = document.getRootElement();
		List<Element> elements = root.elements();
		for (Element element : elements) {
			map.put(element.getName(), element.getTextTrim());
		}
		return map;
	}

	/**
	 * @Description Map TO XML
	 */
	public static String map2Xml(Map<String, Object> map) throws Exception {

		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("xml");
		Set<String> keys = map.keySet();
		for (String key : keys) {
			root.addElement(key).addText(map.get(key).toString());
		}
		StringWriter stringWriter = new StringWriter();
		XMLWriter xmlWriter = new XMLWriter(stringWriter);
		xmlWriter.setEscapeText(false);
		xmlWriter.write(document);
		return stringWriter.toString();
	}

	/**
	 * @Description request TO Map
	 */
	public static Map<String, Object> req2Map(HttpServletRequest request) {

		Map<String, Object> map = new HashMap<String, Object>();
		Enumeration<?> keys = request.getParameterNames();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = request.getParameter(key);
			if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
				map.put(key, value);
			}
		}
		return map;
	}

	/**
	 * @Description json TO Map
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> json2Map(String jsonStr) {

		return JSONObject.parseObject(jsonStr, HashMap.class);
	}

	/**
	 * @Description base64 To File
	 */
	public static File base64ToFile(String base64Str, String filePath, String fileName, Map<Integer, Map<String, Object>> transform) {
		// 创建文件目录
		File dir = new File(filePath);
		if (!dir.exists() && !dir.isDirectory()) {
			dir.mkdirs();
		}
		filePath = StringUtils.endsWith(filePath, "/") ? filePath : filePath + "/";
		File file = new File(filePath + fileName);

		try (FileOutputStream fos = new FileOutputStream(file, true); BufferedOutputStream bos = new BufferedOutputStream(fos);) {
			byte[] bytes = Base64.getDecoder().decode(base64Str);
			bos.write(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * @Description String match RegEx
	 */
	public static String strMatchRegex(String str, String pattern) {

		if (str.length() != pattern.length()) {
			return null;
		}
		char[] patternArray = pattern.toCharArray();
		char[] strArray = str.toCharArray();
		for (int i = 0; i < patternArray.length; i++) {
			if (Character.isUpperCase(patternArray[i])) {
				strArray[i] = Character.toUpperCase(strArray[i]);
			} else {
				strArray[i] = Character.toLowerCase(strArray[i]);
			}
		}
		String result = new String(strArray);
		return result;
	}

}
