package org.aask.bean;

import static com.google.common.base.Charsets.UTF_8;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Map;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.common.base.Optional;


public class ContentTypeHeader implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public static final String KEY = "Content-Type";
	public static final Charset DEFAULT_CHARSET = UTF_8;

	private String[] parts;
	
	public ContentTypeHeader(String stringValue) {
		parts = stringValue != null ? stringValue.split(";") : new String[0];
	}
	
	private ContentTypeHeader() {
		parts = new String[0];
    }
	
	public String mimeTypePart() {
		return parts != null ? parts[0] : null;
	}
	
	public Optional<String> encodingPart() {
		for (int i = 1; i < parts.length; i++) {
			if (parts[i].matches("\\s*charset\\s*=.*") ) {
				return Optional.of(parts[i].split("=")[1].replace("\"", ""));
			}
		}

		return Optional.absent();
	}

	public Charset charset() {
		if (isPresent() && encodingPart().isPresent()) {
			return Charset.forName(encodingPart().get());
		}

		return DEFAULT_CHARSET;
	}
	
	public boolean isPresent() {
        return parts.length > 0;
    }

	public static ContentTypeHeader getContentTypeHeader(Map<String, String> headers) {
		
		if(!CollectionUtils.isEmpty(headers)){
			String contentType = headers.get(KEY);
			if(StringUtils.isEmpty(contentType))
				contentType = "text/plain";
			return new ContentTypeHeader(contentType);
		}
		return new ContentTypeHeader();
	}

}
