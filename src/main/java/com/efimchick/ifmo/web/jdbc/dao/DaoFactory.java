package com.efimchick.ifmo.web.jdbc.dao;

import com.efimchick.ifmo.web.jdbc.ConnectionSource;
import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class DaoFactory {

    public Employee GetEmployeeByResult(ResultSet resultSet) throws SQLException {
        BigInteger id = resultSet.getBigDecimal("id").toBigInteger();
        FullName fullName = new FullName(resultSet.getString("firstName"), resultSet.getString("lastName"), resultSet.getString("middleName"));
        Position position = Position.valueOf(resultSet.getString("position"));
        LocalDate Hired = resultSet.getDate("hireDate").toLocalDate();
        BigDecimal salary = resultSet.getBigDecimal("salary");
        String ManagerID = resultSet.getString("manager");
        if (resultSet.wasNull()) {
            ManagerID = "0";
        }
        String DepartmentID = resultSet.getString("department");
        if (resultSet.wasNull()) {
            DepartmentID = "0";
        }
        return new Employee(id, fullName, position, Hired, salary, new BigInteger(ManagerID), new BigInteger(DepartmentID));
    }

    public Department GetDepartmentByResult(ResultSet resultSet) throws SQLException {
        BigInteger id = resultSet.getBigDecimal("id").toBigInteger();
        String Name = resultSet.getString("name");
        String Loc = resultSet.getString("location");
        return new Department(id, Name, Loc);
    }

    public EmployeeDao employeeDAO() {
        return new EmployeeDao() {
            @Override
            public List<Employee> getByDepartment(Department department) {
                try (Connection conn = ConnectionSource.instance().createConnection()) {
                    Statement stat = conn.createStatement();
                    ResultSet resultSet = stat.executeQuery("SELECT * FROM employee WHERE department = " + department.getId());
                    List<Employee> Employees = new ArrayList<>();
                    while (resultSet.next()) {
                        Employees.add(GetEmployeeByResult(resultSet));
                    }

                    return Employees;
                } catch (SQLException e) {
                    throw new UnsupportedOperationException();
                }
            }

            @Override
            public List<Employee> getByManager(Employee employee) {
                try (Connection conn = ConnectionSource.instance().createConnection()) {
                    Statement stat = conn.createStatement();
                    ResultSet resultSet = stat.executeQuery("SELECT * FROM employee WHERE manager = " + employee.getId());
                    List<Employee> Employees = new ArrayList<>();
                    while (resultSet.next()) {
                        Employees.add(GetEmployeeByResult(resultSet));
                    }

                    return Employees;
                } catch (SQLException e) {
                    throw new UnsupportedOperationException();
                }
            }

            @Override
            public List<Employee> getAll() {
                try (Connection conn = ConnectionSource.instance().createConnection()) {
                    Statement stat = conn.createStatement();
                    ResultSet resultSet = stat.executeQuery("SELECT * FROM employee");
                    List<Employee> Employees = new ArrayList<>();
                    while (resultSet.next()) {
                        Employees.add(GetEmployeeByResult(resultSet));
                    }

                    return Employees;
                } catch (SQLException e) {
                    throw new UnsupportedOperationException();
                }
            }

            @Override
            public Optional<Employee> getById(BigInteger Id) {
                try (Connection conn = ConnectionSource.instance().createConnection()) {
                    Statement stat = conn.createStatement();
                    ResultSet resultSet = stat.executeQuery("SELECT * FROM employee WHERE id =" + Id);

                    if (resultSet.next()) {
                        return Optional.of(GetEmployeeByResult(resultSet));
                    } else {
                        return Optional.empty();
                    }
                } catch (SQLException e) {
                    throw new UnsupportedOperationException();
                }
            }

            @Override
            public void delete(Employee employee) {
                try (Connection conn = ConnectionSource.instance().createConnection()) {
                    Statement stat = conn.createStatement();
                    stat.executeUpdate("DELETE FROM employee WHERE id = " + employee.getId());
                } catch (SQLException e) {
                    throw new UnsupportedOperationException();
                }
            }

            @Override
            public Employee save(Employee employee) {
                try (Connection con = ConnectionSource.instance().createConnection()) {
                    String request = "INSERT INTO employee VALUES (?,?,?,?,?,?,?,?,?)";
                    PreparedStatement preparedStat = con.prepareStatement(request);

                    preparedStat.setInt(1, employee.getId().intValue());
                    preparedStat.setString(2, employee.getFullName().getFirstName());
                    preparedStat.setString(3, employee.getFullName().getLastName());
                    preparedStat.setString(4, employee.getFullName().getMiddleName());
                    preparedStat.setString(5, employee.getPosition().toString());
                    preparedStat.setInt(6, employee.getManagerId().intValue());
                    preparedStat.setDate(7, Date.valueOf(employee.getHired()));
                    preparedStat.setDouble(8,
                            employee.getSalary().doubleValue());
                    preparedStat.setInt(9, employee.getDepartmentId().intValue());

                    preparedStat.executeUpdate();
                    return employee;
                } catch (SQLException e) {
                    throw new UnsupportedOperationException();
                }
            }
        };
    }

    ;

    public DepartmentDao departmentDAO() {
        return new DepartmentDao() {
            @Override
            public Optional<Department> getById(BigInteger Id) {
                try (Connection conn = ConnectionSource.instance().createConnection()) {
                    Statement stat = conn.createStatement();
                    ResultSet resultSet = stat.executeQuery("SELECT * FROM department WHERE id = " + Id);

                    if (resultSet.next()) {
                        return Optional.of(GetDepartmentByResult(resultSet));
                    } else {
                        return Optional.empty();
                    }
                } catch (SQLException e) {
                    throw new UnsupportedOperationException();
                }
            }

            @Override
            public List<Department> getAll() {
                try (Connection conn = ConnectionSource.instance().createConnection()) {
                    Statement stat = conn.createStatement();
                    ResultSet resultSet = stat.executeQuery("SELECT * FROM department");
                    List<Department> Deps = new ArrayList<>();

                    while (resultSet.next()) {
                        Deps.add(GetDepartmentByResult(resultSet));
                    }
                    return Deps;
                } catch (SQLException e) {
                    throw new UnsupportedOperationException();
                }
            }

            ;

            @Override
            public Department save(Department department) {
                try {
                    Connection conn = ConnectionSource.instance().createConnection();
                    PreparedStatement stat;
                    if (getById(department.getId()).equals(Optional.empty())) {
                        stat = conn.prepareStatement("INSERT INTO DEPARTMENT VALUES (?, ?, ?)");
                        stat.setString(1, department.getId().toString());
                        stat.setString(2, department.getName());
                        stat.setString(3, department.getLocation());
                    } else {
                        stat = conn.prepareStatement(
                                "UPDATE DEPARTMENT SET NAME = ?, LOCATION = ? " +
                                        "WHERE ID = ?"
                        );
                        stat.setString(1, department.getName());
                        stat.setString(2, department.getLocation());
                        stat.setString(3, department.getId().toString());
                    }
                    stat.executeUpdate();
                    return getById(department.getId()).get();
                } catch (SQLException e) {
                    throw new UnsupportedOperationException();
                }
            }


            @Override
            public void delete(Department department) {
                try (Connection conn = ConnectionSource.instance().createConnection()) {
                    Statement stat = conn.createStatement();
                    stat.executeUpdate("DELETE FROM department WHERE id = " + department.getId());
                } catch (SQLException e) {
                    throw new UnsupportedOperationException();
                }
            }
        };
    }
}
