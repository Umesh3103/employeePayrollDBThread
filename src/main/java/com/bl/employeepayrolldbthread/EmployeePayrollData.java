package com.bl.employeepayrolldbthread;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class EmployeePayrollData {

	int id;
	String name;
	double salary;
	public LocalDate startDate;
	public int companyId;
	public String department;
	public String gender;

	public EmployeePayrollData(int id, String name, double salary) {
		super();
		this.id = id;
		this.name = name;
		this.salary = salary;
	}

	public EmployeePayrollData(int id, String name, double salary, LocalDate startDate) {
		this(id, name, salary);
		this.startDate = startDate;
	}

	public EmployeePayrollData(int id, String name, double salary, LocalDate startDate, int companyId, String department){
		this(id,name,salary,startDate);
		this.companyId = companyId;
		this.department = department;
	}
	
	public EmployeePayrollData(int id, String name, String gender,double salary, LocalDate startDate, int companyId,
			String department) {
		this(id, name, salary, startDate,companyId,department);
		this.gender=gender;
	}

	@Override
	public String toString() {
		return "EmployeePayrollData [id=" + id + ", name=" + name + ", salary=" + salary + ", startDate=" + startDate
				+ ", companyId=" + companyId + ", department=" + department + "]";
	}

	@Override
	public int hashCode(){
		return Objects.hash(name,gender,salary,startDate, companyId, department);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmployeePayrollData other = (EmployeePayrollData) obj;
		if (companyId != other.companyId)
			return false;
		if (department == null) {
			if (other.department != null)
				return false;
		} else if (!department.equals(other.department))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (Double.doubleToLongBits(salary) != Double.doubleToLongBits(other.salary))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}

}
