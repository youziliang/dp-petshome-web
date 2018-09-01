package com.dp.petshome.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alibaba.fastjson.JSONObject;
import com.dp.petshome.enums.CharSets;

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
	public static Map<String, Object> xml2Map(String strXML) throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		InputStream stream = new ByteArrayInputStream(strXML.getBytes(CharSets.UTF8));
		org.w3c.dom.Document doc = documentBuilder.parse(stream);
		doc.getDocumentElement().normalize();
		NodeList nodeList = doc.getDocumentElement().getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				org.w3c.dom.Element element = (org.w3c.dom.Element) node;
				map.put(element.getNodeName(), element.getTextContent());
			}
		}
		stream.close();
		return map;
	}

	/**
	 * @Description Map TO XML
	 */
	public static String map2Xml(Map<String, Object> map) throws Exception {

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		org.w3c.dom.Document document = documentBuilder.newDocument();
		org.w3c.dom.Element root = document.createElement("xml");
		document.appendChild(root);
		for (String key : map.keySet()) {
			String value = map.get(key).toString();
			if (value == null) {
				value = "";
			}
			value = value.trim();
			org.w3c.dom.Element filed = document.createElement(key);
			filed.appendChild(document.createTextNode(value));
			root.appendChild(filed);
		}
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		DOMSource source = new DOMSource(document);
		transformer.setOutputProperty(OutputKeys.ENCODING, CharSets.UTF8);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		transformer.transform(source, result);
		String output = writer.getBuffer().toString(); // .replaceAll("\n|\r", "");
		writer.close();
		return output;
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
