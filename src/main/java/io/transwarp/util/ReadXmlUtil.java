package io.transwarp.util;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ReadXmlUtil {

	private Vector<Element> elements = new Vector<Element>();
	
	public ReadXmlUtil(String path) throws Exception {
		super();
		File file = new File(path);
		if(!file.exists()) {
			throw new RuntimeException("no find this xml file " + path);
		}
		Document document = new SAXReader().read(file);
		Element rootElement = document.getRootElement();
		getChildElements(rootElement);
	}
	
	private void getChildElements(Element rootElement) {
		List<?> children = rootElement.elements();
		for(Object element : children) {
			elements.add((Element)element);
		}
	}
	
	public Element getElementByKeyValue(String key, String value) {
		for(Element element : elements) {
			String elementValue = element.elementText(key);
			if(elementValue != null && elementValue.equals(value)) {
				return element;
			}
		}
		throw new RuntimeException("no find element by this key-value \"" + key + " : " + value + "\"");
	}
	
	public List<Element> getAll() {
		return elements;
	}
}
