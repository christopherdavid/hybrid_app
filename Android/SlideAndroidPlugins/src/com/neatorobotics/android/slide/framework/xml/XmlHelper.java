package com.neatorobotics.android.slide.framework.xml;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
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
			LogHelper.log(TAG, "Exception in NodeToXmlString", e);
		}
		return null;
	}

	//Adds the given node to a document and sends. Nodes are added in the given order.
	public static String NodesToXmlString(ArrayList<Node> nodes) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;
			docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			for (int i=0; i<nodes.size(); i++) {

				Node adoptNode = doc.adoptNode(nodes.get(i));
				doc.appendChild(adoptNode);

			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformerfile = null;
			try {
				transformerfile = transformerFactory.newTransformer();
			} catch (TransformerConfigurationException e) {
				LogHelper.log(TAG, "Exception in NodesToXmlString", e);
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
			LogHelper.log(TAG, "Exception in NodeToXmlString", e);
		}
		return null;
	}

	public static String documentToString(Document doc) {
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformerfile;
			transformerfile = transformerFactory.newTransformer();
			DOMSource sourcefile = new DOMSource(doc);
			StringWriter write = new StringWriter();
			StreamResult resultfile = new StreamResult(write);
			transformerfile.transform(sourcefile, resultfile);
			LogHelper.log(TAG, write.toString());
			return write.toString();
		}
		catch (TransformerConfigurationException e) {
			LogHelper.log(TAG, "Exception in documentToString", e);
		}
		catch (TransformerException e) {
			LogHelper.log(TAG, "Exception in documentToString", e);
		}
		return null;
	}

	public static byte[] documentToBytes(Document doc) {
		try {
			Source source = new DOMSource(doc);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Result result = new StreamResult(out);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.transform(source, result);
			return out.toByteArray();
		}
		catch (TransformerConfigurationException e) {
			LogHelper.log(TAG, "Exception in documentToString", e);
		}
		catch (TransformerException e) {
			LogHelper.log(TAG, "Exception in documentToString", e);
		}
		return null;
	} 
}
