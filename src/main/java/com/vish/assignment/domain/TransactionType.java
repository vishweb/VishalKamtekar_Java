package com.vish.assignment.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TransactionType {
	
	BUY("B"),SELL("S");
	
	private final String typeCode;
	
	private TransactionType(String code) {
		this.typeCode = code;
	}
	
	@JsonValue
	public String getTransactionTypeCode() {
		return this.typeCode;
	}
	
	public static TransactionType getType(String code) {
        for(TransactionType v : values())
            if(v.getTransactionTypeCode().equalsIgnoreCase(code)) return v;
        throw new IllegalArgumentException();
    }

}
