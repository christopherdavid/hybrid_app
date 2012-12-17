package com.neatorobotics.android.slide.framework.robot.commands;


import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import com.neatorobotics.android.slide.framework.utils.DataConversionUtils;
import com.neatorobotics.android.slide.framework.xml.NetworkXmlConstants;



public class RobotPacketBundle {

	private static final String TAG = RobotPacketBundle.class.getSimpleName();

	private HashMap<String, String> mData;

	public RobotPacketBundle()
	{
		mData = new HashMap<String, String>();
	}

	public RobotPacketBundle(RobotPacketBundle bundle)
	{
		mData = new HashMap<String, String>(bundle.mData);
	}

	public void putString(String key, String value)
	{
		mData.put(key, value);
	}

	public String getString(String key)
	{
		String value = mData.get(key);
		if (value == null) {
			return null;
		}

		return value;
	}


	public int getInt(String key)
	{
		return getInt(key, 0);
	}

	public int getInt(String key, int defaultValue)
	{
		String value = mData.get(key);
		if (value == null) {
			return defaultValue;
		}
		return DataConversionUtils.convertStringToInt(value);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("***************NetworkPacketBundle************\n");
		sb.append("******************************************\n");
		Set<Entry<String, String>> entries = mData.entrySet();
		for (Entry<String, String> entry : entries) {
			Object key = entry.getKey();
			sb.append("\t\tKey: ").append(key).append(" - ");
			Object value = entry.getValue();
			sb.append("Value: ").append(value);
			sb.append("\n");
		}

		sb.append("******************************************\n");

		return sb.toString();
	}

	public Node bundleToXml() {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {

		}
		Document doc = docBuilder.newDocument();
		Node commandData = doc.createElement(NetworkXmlConstants.XML_TAG_COMMAND_DATA);

		Set<String> keys = mData.keySet();

		for (String key: keys) {
			Node dataNode = doc.createElement(key);
			String value = mData.get(key);

			//TODO: added default as empty string.
			if (value != null) {
				dataNode.appendChild(doc.createTextNode(value));
			} else {
				dataNode.appendChild(doc.createTextNode(""));
			}
			commandData.appendChild(dataNode);
		}
		return commandData;
	}

}
