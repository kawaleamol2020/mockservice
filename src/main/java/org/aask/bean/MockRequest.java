package org.aask.bean;

import java.io.Serializable;
import java.util.Map;

public class MockRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private Map<String, String> headers;

	private String reqeustBody;

	private String requestURI;

	private String requestMethod;

	private String soapAction;

	private String requestCheckSum;

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public String getReqeustBody() {
		return reqeustBody;
	}

	public void setReqeustBody(String reqeustBody) {
		this.reqeustBody = reqeustBody;
	}

	public String getRequestURI() {
		return requestURI;
	}

	public void setRequestURI(String requestURI) {
		this.requestURI = requestURI;
	}

	public String getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	public String getSoapAction() {
		return soapAction;
	}

	public void setSoapAction(String soapAction) {
		this.soapAction = soapAction;
	}

	public String getRequestCheckSum() {
		return requestCheckSum;
	}

	public void setRequestCheckSum(String requestCheckSum) {
		this.requestCheckSum = requestCheckSum;
	}

	@Override
	public String toString() {
		return "MockRequest [headers=" + headers + ", reqeustBody=" + reqeustBody + ", requestURI=" + requestURI
				+ ", requestMethod=" + requestMethod + ", soapAction=" + soapAction + ", requestCheckSum="
				+ requestCheckSum + "]";
	}
}
