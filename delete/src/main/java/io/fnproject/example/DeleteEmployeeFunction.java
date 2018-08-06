package io.fnproject.example;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.util.Properties;

public class DeleteEmployeeFunction {

    private Connection conn = null;

    public DeleteEmployeeFunction() {
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

    public String handle(String empEmail) {
        if (empEmail == null || empEmail.equals("")) {
            return "Employee email null/empty";
        }
        return delete(empEmail);
    }

    private String delete(String empEmail) {
        String status = "Failed to delete employee " + empEmail;

        if (conn == null) {
            return status;
        }

        System.err.println("Deleting employee from DB " + empEmail);
        int updated = 0;
        try (PreparedStatement st = conn.prepareStatement("delete from EMPLOYEES where EMP_EMAIL=?")) {
            st.setString(1, empEmail);
            updated = st.executeUpdate();

            System.err.println(updated + " rows updated");
            status = "Deleted employee " + empEmail;

        } catch (Exception se) {
            System.err.println("Unable to delete from DB due to - " + se.getMessage());
        }

        return status;
    }

}
