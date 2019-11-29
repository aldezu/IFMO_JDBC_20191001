package com.efimchick.ifmo.web.jdbc;

/**
 * Implement sql queries like described
 */
public class SqlQueries {
    //Select all employees sorted by last name in ascending order
    //language=HSQLDB
    String select01 = "SELECT * FROM employee ORDER BY lastname ASC";

    //Select employees having no more than 5 characters in last name sorted by last name in ascending order
    //language=HSQLDB
    String select02 = "SELECT * FROM employee WHERE LENGTH(lastname) <= 5 ORDER BY lastname ASC";

    //Select employees having salary no less than 2000 and no more than 3000
    //language=HSQLDB
    String select03 = "SELECT * FROM employee WHERE salary >= 2000 AND salary <= 3000";

    //Select employees having salary no more than 2000 or no less than 3000
    //language=HSQLDB
    String select04 = "SELECT * FROM employee WHERE salary <= 2000 OR salary >= 3000";

    //Select employees assigned to a department and corresponding department name
    //language=HSQLDB
    String select05 = "SELECT a.*, b.name FROM employee a JOIN department b ON (a.department = b.id)";

    //Select all employees and corresponding department name if there is one.
    //Name column containing name of the department "depname".
    //language=HSQLDB
    String select06 = "SELECT a.*, b.name AS depname FROM employee a LEFT OUTER JOIN department b ON (a.department = b.id)";

    //Select total salary pf all employees. Name it "total".
    //language=HSQLDB
    String select07 = "SELECT SUM(salary) AS total FROM employee";

    //Select all departments and amount of employees assigned per department
    //Name column containing name of the department "depname".
    //Name column containing employee amount "staff_size".
    //language=HSQLDB
    String select08 = "SELECT a.name AS depname, COUNT(b.department) AS staff_size FROM department a JOIN employee b ON (a.id = b.department) GROUP BY a.name";

    //Select all departments and values of total and average salary per department
    //Name column containing name of the department "depname".
    //language=HSQLDB
    String select09 = "SELECT a.name AS depname, SUM(b.salary) AS total, AVG(b.salary) AS average FROM department a JOIN employee b ON (a.id = b.department) GROUP BY a.name";

    //Select all employees and their managers if there is one.
    //Name column containing employee lastname "employee".
    //Name column containing manager lastname "manager".
    //language=HSQLDB
    String select10 = "SELECT a.lastname AS employee, b.lastname AS manager FROM employee a LEFT OUTER JOIN employee b ON (a.manager = b.id)";



}
