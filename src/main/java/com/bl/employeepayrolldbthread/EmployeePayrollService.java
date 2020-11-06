package com.bl.employeepayrolldbthread;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class EmployeePayrollService {
	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	}

	private static EmployeePayrollDBService employeePayrollDBService;

	private List<EmployeePayrollData> employeePayrollList;

	public EmployeePayrollService() {
		employeePayrollDBService = EmployeePayrollDBService.getInstance();
	}

	public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
		this();
		this.employeePayrollList = employeePayrollList;
	}

	public static void main(String[] args) {
		ArrayList<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		EmployeePayrollService employeePayrollService = new EmployeePayrollService(employeePayrollList);
		Scanner consoleInputReader = new Scanner(System.in);
		employeePayrollService.readEmployeePayrollData(consoleInputReader);
		employeePayrollService.writeEmployeePayrollData(IOService.CONSOLE_IO);
	}

	public void readEmployeePayrollData(Scanner consoleInputReader) {
		System.out.println("Enter Employee id: ");
		int id = consoleInputReader.nextInt();
		System.out.println("Enter Employee Name: ");
		String name = consoleInputReader.next();
		System.out.println("Enter Employee salary: ");
		double salary = consoleInputReader.nextDouble();
		employeePayrollList.add(new EmployeePayrollData(id, name, salary));
	}

	public void writeEmployeePayrollData(IOService ioService) {
		if (ioService.equals(IOService.CONSOLE_IO)) {
			System.out.println("Employee payroll list: " + employeePayrollList);
		} else if (ioService.equals(IOService.FILE_IO)) {
			new EmployeePayrollFileIOService().writeData(employeePayrollList);
		}
	}

	public void printData(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO)) {
			new EmployeePayrollFileIOService().printData();
		}
	}

	public long countEntries(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO)) {
			return new EmployeePayrollFileIOService().countEntries();
		}
		return employeePayrollList.size();
	}

	public long readEmployeePayrollData(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO)) {
			this.employeePayrollList = new EmployeePayrollFileIOService().readData();
		}
		return employeePayrollList.size();
	}

	public List<EmployeePayrollData> readEmployeePayrollDataDB(IOService ioService) throws employeePayrollException {
		if (ioService.equals(IOService.DB_IO)) {
			this.employeePayrollList = employeePayrollDBService.readData();
		}
		return this.employeePayrollList;
	}

	public void updateEmployeeSalary(String name, double salary) throws employeePayrollException {
		int result = employeePayrollDBService.updateEmployeeData(name, salary);
		if (result == 0)
			return;
		EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
		if (employeePayrollData != null)
			employeePayrollData.salary = salary;
	}

	private EmployeePayrollData getEmployeePayrollData(String name) {
		return this.employeePayrollList.stream()
				.filter(employeePayrollDataItem -> employeePayrollDataItem.name.equals(name)).findFirst().orElse(null);
	}

	public boolean checkEmployeePayrollInSyncWithDB(String name) throws employeePayrollException {
		List<EmployeePayrollData> employeePayrollDataList = employeePayrollDBService.getEmployeePayrollData(name);
		return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
	}

	public List<EmployeePayrollData> readEmployeePayrollForDateRange(IOService ioService, LocalDate startDate,
			LocalDate endDate) throws employeePayrollException {
		if (ioService.equals(IOService.DB_IO)) {
			return employeePayrollDBService.getEmployeePayrollForDateRange(startDate, endDate);
		}
		return null;
	}

	public Map<String, Double> readAverageSalaryByGender(IOService ioService) throws employeePayrollException {
		if (ioService.equals(IOService.DB_IO)) {
			return employeePayrollDBService.getAverageSalaryByGender();
		}
		return null;
	}

	public void addEmployeeToPayroll(String name, double salary, LocalDate startDate, String gender, int companyId,
			String[] deptList) throws employeePayrollException {
		employeePayrollList.add(
				employeePayrollDBService.addEmployeeToPayroll(name, salary, startDate, gender, companyId, deptList));
	}

	public int removeEmployeeFromPayroll(String name, int is_active) throws employeePayrollException {
		int result = employeePayrollDBService.removeEmployeeFromPayrollDB(name, is_active);
		if (result == 1) {
			for (EmployeePayrollData employee : employeePayrollList) {
				if (employee.name.equals(name)) {
					employeePayrollList.remove(employee);
					break;
				}
			}
		}
		return employeePayrollList.size();
	}

	public void addEmployeesToPayrollThread(List<EmployeePayrollData> employeePayrollDataList) {
		employeePayrollDataList.forEach(employeePayrollData -> {
			System.out.println("Employee being Added:" + employeePayrollData.name);
			try {
				this.addEmployeeToPayroll(employeePayrollData.name, employeePayrollData.salary,
						employeePayrollData.startDate, employeePayrollData.gender, employeePayrollData.companyId,
						employeePayrollData.department);
			} catch (employeePayrollException e) {
			}
			System.out.println("Employee Added:" + employeePayrollData.name);
		});
		System.out.println(this.employeePayrollList);
	}

	private void addEmployeeToPayroll(String name, double salary, LocalDate startDate, String gender, int companyId,
			String department) throws employeePayrollException {
		employeePayrollList.add(employeePayrollDBService.addEmployeeToPayrollThread(name, salary, startDate, gender,
				companyId, department));
	}
}
