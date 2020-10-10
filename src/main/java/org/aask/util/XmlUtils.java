package org.aask.util;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class XmlUtils {

	static Logger logger = LoggerFactory.getLogger(XmlUtils.class);

	public static String removeNameSpaceFrom(String xml) {

		try {

			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

			Document document = documentBuilder.parse(new InputSource(new StringReader(xml)));

			InputStream xsltFile = XmlUtils.class.getClassLoader().getResourceAsStream("remove-namespace.xslt");
			Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(xsltFile));
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(document), new StreamResult(writer));
			return writer.toString();
		} catch (Exception e) {
			logger.error("removeNameSpaceFrom error" + CommonUtils.stackTraceToString(e));
			return replaceHederAndChildElements(removeNameSpaceAndHeaderFrom(xml));
		}
	}

	public static String removeNameSpaceAndHeaderFrom(String xml) {

		try {

			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = documentBuilder.parse(new InputSource(new StringReader(xml)));
			XPath xpath = XPathFactory.newInstance().newXPath();
			Node node = (Node) xpath.compile("//*[local-name()='Header']").evaluate(document, XPathConstants.NODE);
			if (node != null)
				node.getParentNode().removeChild(node);
			StringWriter writer = new StringWriter();
			InputStream xsltFile = XmlUtils.class.getClassLoader().getResourceAsStream("remove-namespace.xslt");
			Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(xsltFile));
			transformer.transform(new DOMSource(document), new StreamResult(writer));
			return writer.toString();
		} catch (Exception e) {
			logger.error("removeNameSpaceFrom error" + CommonUtils.stackTraceToString(e));
			return replaceHederAndChildElements(removeXmlNmaespaceAndPreamble(xml));
		}
	}

	public static String getElementValueFromXmlDocument(String xml, String elementPath, boolean isHeaderElement) {

		try {

			elementPath = elementPath.toLowerCase();
			xml = xml.toLowerCase();

			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = documentBuilder.parse(new InputSource(new StringReader(xml)));
			XPath xpath = XPathFactory.newInstance().newXPath();
			String expression = "";
			if (isHeaderElement)
				expression = "//*[local-name()='header']/" + elementPath + "/text()";
			else
				expression = "//*[local-name()='body']/" + elementPath + "/text()";
			String elementValue = (String) xpath.compile(expression).evaluate(document, XPathConstants.STRING);
			if (StringUtils.isEmpty(elementValue))
				elementValue = null;
			return elementValue;
		} catch (Exception e) {
			logger.error("removeNameSpaceFrom error" + e.getMessage());
			return null;
		}
	}

	public static String removeXmlNmaespaceAndPreamble(String xmlString) {
		return xmlString.replaceAll("(<\\?[^<]*\\?>)?", "").replaceAll("xmlns.*?(\"|\').*?(\\\"|\\')", "")
				.replaceAll("(<)(\\w+:)(.*?>)", "$1$3").replaceAll("(</)(\\w+:)(.*?>)", "$1$3");

	}

	public static String replaceHederAndChildElements(String xmlString) {
		return xmlString.replaceAll("<Header>(.*?)</Header>", "").replaceAll("(<Header>|</Header>|<Header/>)", "");

	}
}
