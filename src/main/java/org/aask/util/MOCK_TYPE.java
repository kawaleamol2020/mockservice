package org.aask.util;

import java.util.Arrays;
import java.util.List;

public enum MOCK_TYPE {

	LIVE {
		@Override
		public void setId(Long id){
			this.id = id;
		}
	}, MOCK{
		@Override
		public void setId(Long id){
			this.id = id;
		}
	}, SOAP_ERROR{
		@Override
		public void setId(Long id){
			this.id = id;
		}
	}, HTTP_ERROR{
		@Override
		public void setId(Long id){
			this.id = id;
		}
	};
	
	Long id;	
	
	public abstract void setId(Long id);
	
	public Long getId(){
		return this.id;
	}

	public static boolean contains(String value) {
		for (MOCK_TYPE mockType : MOCK_TYPE.values()) {
			if (mockType.name().equalsIgnoreCase(value))
				return true;
		}
		return false;
	}

	public static List<MOCK_TYPE> asList() {
		return Arrays.asList(MOCK_TYPE.values());
	}
	
	public static boolean containsId(Long id) {
		for (MOCK_TYPE mockType : MOCK_TYPE.values()) {
			if (mockType.getId().equals(id))
				return true;
		}
		return false;
	}
	
	public static MOCK_TYPE getMockTypeById(Long id){
		for (MOCK_TYPE mockType : MOCK_TYPE.values()) {
			if (mockType.getId().equals(id))
				return mockType;
		}
		return null;
	}
}
