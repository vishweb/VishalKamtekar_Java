package com.vish.assignment.domain;

public class Position {
	
	private String instrument;
	private String account;
	private AccountType accountType;
	private Double initialQuantity;
	private Double latestQuantity;
	private Double delta = 0d;
	
	
	public Position(String instrument,String account,String typeCode,Double initialQuantity) {
		this.instrument = instrument;
		this.account = account;
		this.accountType = AccountType.getType(typeCode);
		this.initialQuantity = initialQuantity;
	}


    


	public Double getDelta() {
		return delta;
	}


	public void setDelta(Double delta) {
		this.delta = delta;
	}


	public String getInstrument() {
		return instrument;
	}


	public String getAccount() {
		return account;
	}


	public AccountType getAccountType() {
		return accountType;
	}


	public Double getInitialQuantity() {
		return initialQuantity;
	}

	public Double getLatestQuantity() {
		if(latestQuantity == null){
			return initialQuantity;
		}
		return latestQuantity;
	}
	
	public void setLatestQuantity(Double latestQuantity) {		
		this.latestQuantity = latestQuantity;
	}

	@Override
	public String toString() {
		return "Position [instrument=" + instrument + ", account=" + account + ", accountType=" + accountType
				+ ", initialQuantity=" + initialQuantity + ", finalQuantity=" + latestQuantity + ", delta=" + delta
				+ "]";
	}
	
	
	

}
