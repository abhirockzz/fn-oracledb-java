package io.fnproject.example;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.util.Properties;

public class UpdateEmployeeFunction {

    private Connection conn = null;

    public UpdateEmployeeFunction() {
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

    public String handle(UpdateEmployeeInfo empInfo) {
        return update(empInfo);
    }

    private String update(UpdateEmployeeInfo empInfo) {
        String status = "Failed to update employee " + empInfo;

        if (conn == null) {
            return status;
        }

        System.err.println("Updating employee info " + empInfo);

        int updated = 0;
        try (PreparedStatement st = conn.prepareStatement("update EMPLOYEES set EMP_DEPT=? where EMP_EMAIL=?")) {
            st.setString(1, empInfo.getEmp_dept());
            st.setString(2, empInfo.getEmp_email());

            updated = st.executeUpdate();

            System.err.println(updated + " rows updated");
            if (updated > 0) {
                status = "Updated employee " + empInfo;
            }

        } catch (Exception se) {
            System.err.println("Unable to update data in DB due to - " + se.getMessage());
        }
        return status;
    }

}
