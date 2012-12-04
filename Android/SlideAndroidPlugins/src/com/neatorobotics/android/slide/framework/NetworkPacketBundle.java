package com.neatorobotics.android.slide.framework;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.neatorobotics.android.slide.framework.logger.LogHelper;

@SuppressWarnings("unused")
public class NetworkPacketBundle {
	
	private static final String TAG = NetworkPacketBundle.class.getSimpleName();
	
	
	private static final int TYPE_NULL 									= 0;
	private static final int TYPE_INT 									= 1;
	private static final int TYPE_INTARRAY 								= 2;
	private static final int TYPE_BYTE 									= 3;
	private static final int TYPE_BYTEARRAY 							= 4;
	private static final int TYPE_BOOLEAN								= 5;
	private static final int TYPE_BOOLEANARRAY							= 6;
	private static final int TYPE_CHAR									= 7;
	private static final int TYPE_CHARARRAY								= 8;
	private static final int TYPE_FLOAT									= 9;
	private static final int TYPE_FLOATARRAY							= 10;
	private static final int TYPE_DOUBLE								= 11;
	private static final int TYPE_DOUBLEARRAY							= 12;
	private static final int TYPE_LONG									= 13;
	private static final int TYPE_LONGARRAY								= 14;
	private static final int TYPE_STRING								= 15;
	private static final int TYPE_STRINGARRAY							= 16;
	private static final int TYPE_SHORT									= 17;
	private static final int TYPE_SHORTARRAY							= 18;
	
	private HashMap<String, Object> mData;
	
	public NetworkPacketBundle()
	{
		 mData = new HashMap<String, Object>();
	}
	
	public NetworkPacketBundle(NetworkPacketBundle bundle)
	{
		mData = new HashMap<String, Object>(bundle.mData);
	}
	
	public void putInt(String key, int value)
	{
		mData.put(key, value);
	}
	
	public int getInt(String key)
	{
		return getInt(key, 0);
	}
	
	public int getInt(String key, int defaultValue)
	{
		Object value = mData.get(key);
		if (value == null) {
			return defaultValue;
		}
		else if (value instanceof Integer) {
			return (Integer)value;
		}
		else {
			logInvalidDataTypeWarning(key, value, Integer.class.getName());
		}
		
		return defaultValue;
	}
	
	
	public void putString(String key, String value)
	{
		mData.put(key, value);
	}
	
	public String getString(String key)
	{
		Object value = mData.get(key);
		if (value == null) {
			return null;
		}
		else if (value instanceof String) {
			return (String)value;
		}
		else {
			logInvalidDataTypeWarning(key, value, String.class.getName());
		}
		
		return null;
	}
	
	
	
	
	private void logInvalidDataTypeWarning(String key, Object value, String expectedType)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("*******WARNING*********\n");
		sb.append("Unexpected data found for key " + key);
		sb.append(" Expected data type = " + expectedType);
		sb.append(", Actual data type = " + value.getClass().getName());
		
		LogHelper.log(TAG, sb.toString());
	}
	

	
	
	public void putIntArray(String key, int[] value)
	{
		mData.put(key, value);
	}
	
	public int[] getIntArray(String key)
	{
		Object value = mData.get(key);
		if (value == null) {
			return null;
		}
		else if (value instanceof Integer []) {
			return (int[])value;
		}
		else {
			logInvalidDataTypeWarning(key, value, "Int[]");
		}
		
		return null;
	}
	
	private void writeMapData(DataOutputStream dos, Map<String, Object> data) throws IOException
	{
		if (dos == null) {
			LogHelper.log(TAG, "dos is null");
			return;
		}
		if (data == null) {
			LogHelper.logD(TAG, "data is null");
			dos.writeInt(-1);
			return;
		}
		
		Set<String> keys = data.keySet();
		dos.writeInt(keys.size());
		for (String key: keys) {
			writeValue(dos, key);
			writeValue(dos, mData.get(key));
		}
		
	}
	
	private void writeValue(DataOutputStream dos, Object value) throws IOException
	{
		if (value == null) {
			dos.writeInt(TYPE_NULL);
		}
		else if (value instanceof Integer) {
			dos.writeInt(TYPE_INT);
			dos.writeInt((Integer)value);
		}
		else if (value instanceof String) {
			dos.writeInt(TYPE_STRING);
			dos.writeUTF((String)value);
		}
		else {
			LogHelper.log(TAG, "ERROR - writeValue. value class type = " + value.getClass().getName());
		}
	}
	
	private void readData(DataInputStream dis, Map<String, Object> data) throws IOException
	{
		int mapSize = dis.readInt();
		LogHelper.logD(TAG, "Data Map size = " + mapSize);
		
		for (int i = 0; i < mapSize; i++) {
			Object key = readValue(dis);
    		Object value = readValue(dis);
    		data.put((String)key, value);
		}
	}
	
	private Object readValue(DataInputStream din) throws IOException
	{
		int type = din.readInt();
		
		switch(type) {
		case TYPE_NULL:
			return null;
		case TYPE_INT:
			return din.readInt();
		case TYPE_STRING:
			return din.readUTF();
		default:
			LogHelper.log(TAG, "WARNING: Invalid data type - datatype = " + type);
			break;
		}
		
		return null;
	}
	
	public byte[] toByteArray()
	{
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			
			writeMapData(dos, mData);
			
			return bos.toByteArray();
		}
		catch (IOException e) {
			LogHelper.log(TAG, "Exception in toByteArray", e);
		}
		
		return null;
	}
	
	public static NetworkPacketBundle createBundleFromByteArray(byte [] data)
	{
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
    	DataInputStream dis = new DataInputStream(bis);
    	
    	NetworkPacketBundle bundle = new NetworkPacketBundle();
    	try {
    		bundle.readBundle(dis);
    		return bundle;
    	}
    	catch (Exception e) {
    		LogHelper.log(TAG, "Exception in createBundleFromByteArray", e);
    	}
    	
    	return null;
	}
	
	public static NetworkPacketBundle createBundleFromByteArray(DataInputStream dis)
	{
    	NetworkPacketBundle bundle = new NetworkPacketBundle();
    	try {
    		bundle.readBundle(dis);
    		return bundle;
    	}
    	catch (Exception e) {
    		LogHelper.log(TAG, "Exception in createBundleFromByteArray", e);
    	}
    	
    	return null;
	}
	
	private void readBundle(DataInputStream dis) throws IOException
	{
		readData(dis, mData);
	}
	
	
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("***************NetworkPacketBundle************\n");
    	sb.append("******************************************\n");
    	Set<Map.Entry<String, Object>> entries = mData.entrySet();
    	for (Map.Entry<String, Object> entry : entries) {
    		Object key = entry.getKey();
    		sb.append("\t\tKey: ").append(key).append(" - ");
    		Object value = entry.getValue();
   			sb.append("Value: ").append(value);
    		sb.append("\n");
    	}
    	
    	sb.append("******************************************\n");
    	
    	return sb.toString();
    }
    
 }
