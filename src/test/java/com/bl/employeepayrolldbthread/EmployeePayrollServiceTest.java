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
import org.junit.Test;

import com.bl.employeepayrolldbthread.EmployeePayrollService.IOService;

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
	public void  givenEmployees_WhenAddedToDB_ShouldMatchEmployeeEntries(){
		String[] deptArray = new String[4];
		deptArray[0] = "sales";
		deptArray[1] = "hr";
		deptArray[2] = "marketing";
		deptArray[3] = "hr,sales";
		EmployeePayrollData[] arrayOfEmps = {
			new EmployeePayrollData(0, "Jeff bezzos", "M", 100000.0, LocalDate.now(),1,deptArray[0]),
			new EmployeePayrollData(0, "Bill Gates", "M", 200000.0, LocalDate.now(),2,deptArray[1]),
			new EmployeePayrollData(0, "Mark Zuckerburg", "M", 300000.0, LocalDate.now(),2,deptArray[2]),
			new EmployeePayrollData(0, "Sunder", "M", 600000.0, LocalDate.now(),1,deptArray[1]),
			new EmployeePayrollData(0, "Mukesh", "M", 1000000.0, LocalDate.now(),2,deptArray[2]),
			new EmployeePayrollData(0, "Anil", "M", 200000.0, LocalDate.now(),1,deptArray[1]),
		};
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		try {
			employeePayrollService.readEmployeePayrollDataDB(IOService.DB_IO);
			Instant start =Instant.now();
			employeePayrollService.addEmployeesToPayrollThread(Arrays.asList(arrayOfEmps));
			Instant end =Instant.now();
			System.out.println("Duration without thread: "+Duration.between(start, end));
			Assert.assertEquals(9, employeePayrollService.countEntries(IOService.DB_IO));
		} catch (employeePayrollException e) {
		}
	}
}
