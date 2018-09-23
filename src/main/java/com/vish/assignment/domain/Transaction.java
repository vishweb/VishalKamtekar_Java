package com.vish.assignment.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Transaction {
	
	private String transactionId;
	private String instrument;
	private TransactionType transactionType;
	private double quantity;
	private boolean isProcessed;
	
	@JsonCreator
	public Transaction(@JsonProperty("TransactionId") String transactionId, 
					   @JsonProperty("Instrument") String instrument, 
					   @JsonProperty("TransactionType") TransactionType transactionType, 
					   @JsonProperty("TransactionQuantity") double quantity) {
		this.transactionId = transactionId;
		this.instrument = instrument;
		this.transactionType = transactionType;
		this.quantity = quantity;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public String getInstrument() {
		return instrument;
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public double getQuantity() {
		return quantity;
	}
	
	public boolean isProcessed() {
		return isProcessed;
	}
	
	public void setProcessed(boolean isProcessed) {
		this.isProcessed = isProcessed;
	}

	@Override
	public String toString() {
		return "Transaction [transactionId=" + transactionId + ", instrument=" + instrument + ", transactionType="
				+ transactionType + ", quantity=" + quantity + "]";
	}	
	
	

}
