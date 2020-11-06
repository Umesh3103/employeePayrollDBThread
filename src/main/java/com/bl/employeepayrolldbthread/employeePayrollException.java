package com.bl.employeepayrolldbthread;

public class employeePayrollException extends Exception{

	enum ExceptionType{
		DB_PROBLEM
	}
	
	public ExceptionType type;
	
	public employeePayrollException(String message, ExceptionType type){
		super(message);
		this.type=type;
	}
}
