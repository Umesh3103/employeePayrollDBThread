package com.bl.employeepayrolldbthread;

import java.sql.Array;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.bl.employeepayrolldbthread.EmployeePayrollService.IOService;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.mapper.ObjectMapper;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class EmployeePayrollServiceTest {

	@Test
	public void Given3Employee_WhenWrittenToFile_ShouldMatchEmployeeEntries() {
		EmployeePayrollData[] arrayOfEmps = { new EmployeePayrollData(1, "Jeff Bezos", 100000.0),
				new EmployeePayrollData(2, "Bill Gates", 200000.0),
				new EmployeePayrollData(3, "Mark Zuckerberg", 300000.0) };
		EmployeePayrollService employeePayrollService;
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
		employeePayrollService.writeEmployeePayrollData(IOService.FILE_IO);
		employeePayrollService.printData(IOService.FILE_IO);
		long entries = employeePayrollService.countEntries(IOService.FILE_IO);
		Assert.assertEquals(3, entries);
	}

	@Test
	public void givenEmployees_WhenAddedToDB_ShouldMatchEmployeeEntries() {
		String[] deptArray = new String[4];
		deptArray[0] = "sales";
		deptArray[1] = "hr";
		deptArray[2] = "marketing";
		deptArray[3] = "hr,sales";
		EmployeePayrollData[] arrayOfEmps = {
				new EmployeePayrollData(0, "Jeff bezzos", "M", 100000.0, LocalDate.now(), 1, deptArray[0]),
				new EmployeePayrollData(0, "Bill Gates", "M", 200000.0, LocalDate.now(), 2, deptArray[1]),
				new EmployeePayrollData(0, "Mark Zuckerburg", "M", 300000.0, LocalDate.now(), 2, deptArray[2]),
				new EmployeePayrollData(0, "Sunder", "M", 600000.0, LocalDate.now(), 1, deptArray[1]),
				new EmployeePayrollData(0, "Mukesh", "M", 1000000.0, LocalDate.now(), 2, deptArray[2]),
				new EmployeePayrollData(0, "Anil", "M", 200000.0, LocalDate.now(), 1, deptArray[1]), };
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		try {
			employeePayrollService.readEmployeePayrollDataDB(IOService.DB_IO);
			Instant start = Instant.now();
			employeePayrollService.addEmployeesToPayrollWithoutThread(Arrays.asList(arrayOfEmps));
			Instant end = Instant.now();
			System.out.println("Duration without thread: " + Duration.between(start, end));
			Instant threadStart = Instant.now();
			employeePayrollService.addEmployeesToPayrollWithThread(Arrays.asList(arrayOfEmps));
			Instant threadEnd = Instant.now();
			System.out.println("Duration with thread: " + Duration.between(threadStart, threadEnd));
			Assert.assertEquals(15, employeePayrollService.countEntries(IOService.DB_IO));
		} catch (employeePayrollException e) {
		}
	}
	
	@Before
	public void setup(){
		RestAssured.baseURI = "http://localhost";
		RestAssured.port=3000;
	}
	private Response addEmployeeToJsonServer(EmployeePayrollData employeePayrollData) {
		String empJson = new Gson().toJson(employeePayrollData);
		RequestSpecification request =RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(empJson);
		return request.post("/employee_payroll");
	}

	private EmployeePayrollData[] getEmployeeList() {
		Response response = RestAssured.get("/employee_payroll");
		System.out.println("EMPLOYEE PAYROLL ENTRIES IN JSONserver: \n" +response.asString());
		EmployeePayrollData[] arrayOfEmps = new Gson().fromJson(response.asString(), EmployeePayrollData[].class);
		return arrayOfEmps;
	}
	
	@Test
	public void givenNewEmployee_WhenAdded_ShouldMatch201Response(){
		EmployeePayrollService employeePayrollService;
		EmployeePayrollData[] arrayOfEmps  = getEmployeeList();
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
		EmployeePayrollData employeePayrollData = null;
		employeePayrollData = new EmployeePayrollData(0,"Mark Zuckerberg", "M", 300000.00, LocalDate.now());
		Response response = addEmployeeToJsonServer(employeePayrollData);
		int statusCode = response.getStatusCode();
		Assert.assertEquals(201, statusCode);
		
		employeePayrollData = new Gson().fromJson(response.asString(), EmployeePayrollData.class);
		try {
			employeePayrollService.addEmployeeToPayroll(employeePayrollData, IOService.REST_IO);
			long entries = employeePayrollService.countEntries(IOService.REST_IO);
			Assert.assertEquals(3, entries);
		} catch (employeePayrollException e) {
		}
	}
	
	@Test
	public void givenListOfNewEmployee_WhenAdded_ShouldMatch201ResponseCount(){
		EmployeePayrollService employeePayrollService;
		EmployeePayrollData[] arrayOfEmps  = getEmployeeList();
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
		
		EmployeePayrollData[] arrayOfEmpPayrolls = {
				new EmployeePayrollData(0,"Sunder", "M", 600000.00, LocalDate.now()),
				new EmployeePayrollData(0,"Mukesh", "M", 1000000.00, LocalDate.now()),
				new EmployeePayrollData(0,"Anil", "M", 200000.00, LocalDate.now())
		};
		
		for(EmployeePayrollData employeePayrollData: arrayOfEmpPayrolls){
			Response response = addEmployeeToJsonServer(employeePayrollData);
			int statusCode = response.getStatusCode();
			Assert.assertEquals(201, statusCode);
			
			employeePayrollData = new Gson().fromJson(response.asString(), EmployeePayrollData.class);
			try {
				employeePayrollService.addEmployeeToPayroll(employeePayrollData, IOService.REST_IO);
			} catch (employeePayrollException e) {
			}
		}
		long entries = employeePayrollService.countEntries(IOService.REST_IO);
		Assert.assertEquals(6, entries);
	}
	
	@Test
	public void givenNewSalaryForEmployee_WhenUpdated_ShouldMatch200Response(){
		EmployeePayrollService employeePayrollService;
		EmployeePayrollData[] arrayOfEmps = getEmployeeList();
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
		
		try {
			employeePayrollService.updateEmployeeSalary("Anil", 3000000.00, IOService.REST_IO);
		} catch (employeePayrollException e) {
		}
		EmployeePayrollData employeePayrollData = employeePayrollService.getEmployeePayrollData("Anil");
		String empJson =new Gson().toJson(employeePayrollData);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(empJson);
		Response response = request.put("/employee_payroll/"+employeePayrollData.id);
		int statusCode = response.getStatusCode();
		Assert.assertEquals(200, statusCode);
	}
}
