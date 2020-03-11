package com.efimchick.ifmo.web.jdbc.service;

import com.efimchick.ifmo.web.jdbc.ConnectionSource;
import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceFactory {
    public EmployeeService employeeService() {
        return new EmployeeService() {
            @Override
            public List<Employee> getAllSortByHireDate(Paging paging) {
                try {
                    return getWithPaging("select * from employee order by hireDate", paging);
                } catch (SQLException e){
                    return null;
                }
            }

            @Override
            public List<Employee> getAllSortByLastname(Paging paging) {
                try {
                    return getWithPaging("select * from employee order by lastName", paging);
                } catch (SQLException e){
                    return null;
                }
            }

            @Override
            public List<Employee> getAllSortBySalary(Paging paging) {
                try {
                    return getWithPaging("select * from employee order by salary", paging);
                } catch (SQLException e) {
                    return null;
                }
            }

            @Override
            public List<Employee> getAllSortByDepartmentNameAndLastname(Paging paging) {
                try {
                    return getWithPaging("select * from employee order by department, lastName", paging);
                } catch (SQLException e){
                    return null;
                }
            }

            @Override
            public List<Employee> getByDepartmentSortByHireDate(Department department, Paging paging) {
                try {
                    return getWithPaging("select * from employee where department=" + department.getId() + "order by hireDate", paging);
                } catch (SQLException e){
                    return null;
                }
            }

            @Override
            public List<Employee> getByDepartmentSortBySalary(Department department, Paging paging) {
                try {
                    return getWithPaging("select * from employee where department=" + department.getId() + "order by salary", paging);
                } catch (SQLException e){
                    return null;
                }
            }

            @Override
            public List<Employee> getByDepartmentSortByLastname(Department department, Paging paging) {
                try {
                    return getWithPaging("select * from employee where department=" + department.getId() + "order by lastName", paging);
                } catch (SQLException e){
                    return null;
                }
            }

            @Override
            public List<Employee> getByManagerSortByLastname(Employee manager, Paging paging) {
                try {
                    return getWithPaging("select * from employee where manager=" + manager.getId() + "order by lastName", paging);
                } catch (SQLException e) {
                    return null;
                }
            }

            @Override
            public List<Employee> getByManagerSortByHireDate(Employee manager, Paging paging) {
                try {
                    return getWithPaging("select * from employee where manager=" + manager.getId() + "order by hireDate", paging);
                } catch (SQLException e) {
                    return null;
                }
            }

            @Override
            public List<Employee> getByManagerSortBySalary(Employee manager, Paging paging) {
                try {
                    return getWithPaging("select * from employee where manager=" + manager.getId() + "order by salary", paging);
                } catch (SQLException e) {
                    return null;
                }
            }

            @Override
            public Employee getWithDepartmentAndFullManagerChain(Employee employee) {
                try {
                    return getEmployees(true, true, "select * from employee where id = " + employee.getId()).get(0);
                } catch (SQLException e) {
                    return null;
                }
            }

            @Override
            public Employee getTopNthBySalaryByDepartment(int salaryRank, Department department) {
                try {
                    return getEmployees(false, true, "select * from employee where department=" + department.getId() + " order by salary desc").get(salaryRank-1);
                } catch (SQLException e) {
                    return null;
                }
            }
        };
    }

    public Employee getEmployee(ResultSet rs, boolean managerFlag, boolean chainFlag) throws SQLException {
        Object rsManager = rs.getObject("manager");
        Object rsDepartment = rs.getObject("department");

        BigInteger id = new BigInteger(rs.getString("id"));
        FullName name = new FullName(
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("middleName")
        );
        Position position = Position.valueOf(rs.getString("position"));
        LocalDate hireDate = LocalDate.parse(rs.getString("hireDate"));
        BigDecimal salary = rs.getBigDecimal("salary");
        Employee manager = ((chainFlag || managerFlag) && rsManager != null) ? getEmployees(chainFlag, false, "select * from employee where id = " + new BigInteger(rs.getString("manager"))).get(0) : null;
        Department dep = (rsDepartment != null) ? getDepById(BigInteger.valueOf(rs.getInt("department"))) : null;

        return new Employee(id, name, position, hireDate, salary, manager, dep);
    }

    public List<Employee> getEmployees(boolean chainFlag, boolean managerFlag, String query) throws SQLException {
        List<Employee> Employees = new ArrayList<>();
        ResultSet rs = ConnectionSource.instance().createConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery(query);
        while (rs.next()) {
            Employees.add(getEmployee(rs, managerFlag, chainFlag));
        }
        return Employees;
    }

    private Department getDep(ResultSet rs) throws SQLException {
        return new Department(new BigInteger(rs.getString("ID")), rs.getString("name"), rs.getString("location"));
    }

    private Department getDepById(BigInteger id) throws SQLException {
        ResultSet rs = ConnectionSource.instance().createConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery("select * from department where id = " + id);
        rs.next();
        return getDep(rs);
    }

    public List<Employee> getWithPaging (String sql, Paging paging) throws SQLException {
        String pagingSql = " offset " + (paging.page - 1) * paging.itemPerPage + " limit " + paging.itemPerPage;
        String finalQuery = sql + pagingSql;
        return getEmployees(false, true, finalQuery);
    }
}