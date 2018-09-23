package com.vish.assignment.domain;

public enum AccountType {
	
	INTERNAL("I"),EXTERNAL("E");
	
	private final String typeCode;
	
	private AccountType(String code) {
		this.typeCode = code;
	}
	
	public String getAccountTypeCode() {
		return this.typeCode;
	}
	
	public static AccountType getType(String code) {
        for(AccountType v : values())
            if(v.getAccountTypeCode().equalsIgnoreCase(code)) return v;
        throw new IllegalArgumentException();
    }

}
