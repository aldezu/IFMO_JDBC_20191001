package com.efimchick.ifmo.web.jdbc;

import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.time.LocalDate;


public class RowMapperFactory {


    public RowMapper<Employee> employeeRowMapper() {

        throw new UnsupportedOperationException();
        RowMapper<Employee> Rowmapper = resultSet -> {
            try {
                return new Employee(
                    new BigInteger(String.valueOf(resultSet.getInt("ID"))),
                    new FullName(resultSet.getString("FIRSTNAME"), resultSet.getString("LASTNAME"), resultSet.getString("MIDDLENAME")),
                    Position.valueOf(resultSet.getString("POSITION")),
                    LocalDate.parse(resultSet.getString("HIREDATE")),
                    new BigDecimal(String.valueOf(resultSet.getInt("SALARY")))) ;
                } catch (SQLException e) {
                return null;
            }
        };
        return  Rowmapper;

    }

}
