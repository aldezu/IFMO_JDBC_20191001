package com.efimchick.ifmo.web.jdbc;

import java.util.Set;

import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.time.LocalDate;

public class SetMapperFactory {

    public SetMapper<Set<Employee>> employeesSetMapper() {
        return new SetMapper<Set<Employee>>() {
            @Override
            public Set<Employee> mapSet(ResultSet resultSet) {
                Set<Employee> employees = new HashSet<>();
                try {
                    while (resultSet.next()) {
                        employees.add(getSlaves(resultSet));
                    }
                } catch (SQLException ignored) {}
                return employees;
            }
        };
    }

            private Employee getSlaves(ResultSet resultSet) throws SQLException {

                try{
                    BigInteger id = new BigInteger(resultSet.getString("ID"));
                    FullName fullName = new FullName(resultSet.getString("FIRSTNAME"), resultSet.getString("LASTNAME"), resultSet.getString("MIDDLENAME"));
                    Position position = Position.valueOf(resultSet.getString("POSITION"));
                    LocalDate hireDate = LocalDate.parse(resultSet.getString("HIREDATE"));
                    BigDecimal salary = resultSet.getBigDecimal("SALARY");

                    Employee manager = null;
                    resultSet.getString("manager");
                    if (!resultSet.wasNull()) {
                        int currentCursor = resultSet.getRow();
                        BigInteger managerId = new BigInteger(resultSet.getString("MANAGER"));
                        resultSet.beforeFirst();
                        while (resultSet.next()) {
                            if (managerId.equals(new BigInteger(resultSet.getString("ID")))) {
                                manager = getSlaves(resultSet);
                            }
                        }
                        resultSet.absolute(currentCursor);
                    }
                return new Employee(id, fullName, position, hireDate, salary, manager);
            } catch (SQLException e) {
                    return null;
                }
    }
};