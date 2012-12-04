package com.neatorobotics.android.slide.framework.xml;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.neatorobotics.android.slide.framework.logger.LogHelper;

public class XmlHelper {
	public static final String TAG = XmlHelper.class.getSimpleName();

	public static String NodeToXmlString(Node node) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;
			docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Node adoptNode = doc.adoptNode(node);
			doc.appendChild(adoptNode);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformerfile = null;
			try {
				transformerfile = transformerFactory.newTransformer();
			} catch (TransformerConfigurationException e) {
				LogHelper.log(TAG, "Exception in NodeToXmlString", e);
			}
			DOMSource sourcefile = new DOMSource(doc);
			StringWriter write = new StringWriter();
			StreamResult resultfile = new StreamResult(write);
			transformerfile.transform(sourcefile, resultfile);
			LogHelper.log(TAG, write.toString());
			return write.toString();
		} catch (ParserConfigurationException e) {
			LogHelper.log(TAG, "Exception in NodeToXmlString", e);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			LogHelper.log(TAG, "Exception in NodeToXmlString", e);
		}
		return null;
	}

}
