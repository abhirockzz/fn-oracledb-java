package io.fnproject.example;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.util.Properties;

public class CreateEmployeeFunction {

    private Connection conn = null;

    public CreateEmployeeFunction() {
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

    public String handle(CreateEmployeeInfo empInfo) {
        return create(empInfo);
    }

    private String create(CreateEmployeeInfo empInfo) {
        String status = "Failed to insert employee " + empInfo;

        if (conn == null) {
            return status;
        }

        System.err.println("Inserting employee info into DB " + empInfo);

        int updated = 0;
        try (PreparedStatement st = conn.prepareStatement("INSERT INTO EMPLOYEES VALUES (?,?,?)")) {
            st.setString(1, empInfo.getEmp_email());
            st.setString(2, empInfo.getEmp_name());
            st.setString(3, empInfo.getEmp_dept());

            updated = st.executeUpdate();

            System.err.println(updated + " rows inserted");
            status = "Created employee " + empInfo;

        } catch (Exception se) {
            System.err.println("Unable to insert data into DB due to - " + se.getMessage());
        }

        return status;
    }

}
