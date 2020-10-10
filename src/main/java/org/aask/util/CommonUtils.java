package org.aask.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.digest.DigestUtils;
import org.aask.constant.HttpResponseCodes;

public class CommonUtils {
	
	public static String generateChecksum(String data) throws UnsupportedEncodingException {
		//MessageDigest md5Digest = DigestUtils.getMd5Digest();
		MessageDigest sha512Digest = DigestUtils.getSha512Digest();
		byte[] digest = sha512Digest.digest(data.getBytes("UTF-8"));
		String requestCheckSum = DatatypeConverter.printHexBinary(digest).toLowerCase();
		return requestCheckSum;
	}
	
	public static String stackTraceToString(Throwable e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
	
	public static String generatePrimaryKey(String soapAction,int statusCode, String requestCheckSum) {
		
		StringBuffer key = new StringBuffer();
		if(soapAction != null)
			key.append(soapAction).append("_");
		key.append(statusCode).append("_");
		key.append(requestCheckSum);
	
		return key.toString();
	}
	
	public static List<Integer> getHttpStatusCodes(){
		
		List<Integer> statusCodes = new ArrayList<>();
		
		Field[] declaredFields = HttpResponseCodes.class.getDeclaredFields();
		for (Field field : declaredFields) {
			try {
				statusCodes.add((Integer) field.get(null));
			} catch (Exception e) {	} 
		}
		
		return statusCodes;
	}
}
