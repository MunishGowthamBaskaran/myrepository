package com.raremile.prd.inferlytics.exception;

@SuppressWarnings("serial")
public class CriticalException extends RuntimeException {

	public CriticalException(){
		super();
	}

	public CriticalException(String message){
		super(message);
	}

	public CriticalException(Exception ex) {
		super(ex);
	}

}

