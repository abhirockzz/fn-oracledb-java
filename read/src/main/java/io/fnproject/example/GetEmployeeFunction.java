package io.fnproject.example;

import com.fnproject.fn.api.RuntimeContext;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class GetEmployeeFunction {

    private Connection conn = null;

    public GetEmployeeFunction(RuntimeContext ctx) {
        try {
            Class<Driver> driver = (Class<Driver>) Class.forName("oracle.jdbc.driver.OracleDriver");

            String dbUrl = System.getenv().getOrDefault("DB_URL", "localhost");
            String dbUser = System.getenv().getOrDefault("DB_USER", "scott");
            String dbPasswd = System.getenv().getOrDefault("DB_PASSWORD", "tiger");

            System.err.println("Connecting to DB...");
            System.err.println("URL " + dbUrl);
            System.err.println("User " + dbUser);

            Properties connInfo = new Properties();
            connInfo.setProperty("user", dbUser);
            connInfo.setProperty("password", dbPasswd);

            conn = driver.getDeclaredConstructor().newInstance().connect(dbUrl, connInfo);
            System.err.println("Connected to DB successfully");

        } catch (Throwable e) {
            System.err.println("DB connectivity failed due - " + e.getMessage());
        }
    }

    public List<Employee> handle(String empEmail) {
        return read(empEmail);
    }

    private static final String GET_ALL_EMPLOYEES = "select * from EMPLOYEES";
    private static final String GET_EMPLOYEE_INFO = "select * from EMPLOYEES where EMP_EMAIL=?";

    private List<Employee> read(String empEmail) {

        if (conn == null) {
            return Collections.emptyList();
        }

        String query = null;

        if (empEmail.equals("")) {
            System.err.println("Getting all employees...");
            query = GET_ALL_EMPLOYEES;
        } else {
            System.err.println("Fetching employee info for " + empEmail);
            query = GET_EMPLOYEE_INFO;
        }

        List<Employee> emps = new ArrayList<>();

        try (PreparedStatement st = conn.prepareStatement(query)) {
            if (!empEmail.equals("")) {
                st.setString(1, empEmail);
            }

            ResultSet empRSet = st.executeQuery();

            while (empRSet.next()) {
                emps.add(new Employee(empRSet.getString("EMP_EMAIL"), empRSet.getString("EMP_NAME"), empRSet.getString("EMP_DEPT")));
            }

        } catch (Exception se) {
            System.err.println("Unable to fetch employee info " + se.getMessage());
        }
        return emps;
    }

}
