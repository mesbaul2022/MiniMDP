package com.example.minimdp;

import java.sql.*;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:minimdp.db";
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL);

            // Enable FK per connection (SQLite default is OFF)
            try (Statement st = connection.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON;");
            }
        }
        return connection;
    }

    public static void initializeDatabase() {

        String createUsers = """
        CREATE TABLE IF NOT EXISTS users (
          id INTEGER PRIMARY KEY AUTOINCREMENT,
          name TEXT NOT NULL,
          email TEXT NOT NULL UNIQUE,
          password TEXT NOT NULL
        )
        """;

        String createMovies = """
        CREATE TABLE IF NOT EXISTS movies (
          id INTEGER PRIMARY KEY AUTOINCREMENT,
          title TEXT NOT NULL,
          genre TEXT NOT NULL,
          year INTEGER,
          description TEXT
        )
        """;

        String createReviews = """
        CREATE TABLE IF NOT EXISTS reviews (
          id INTEGER PRIMARY KEY AUTOINCREMENT,
          movie_id INTEGER NOT NULL,
          user_id INTEGER NOT NULL,
          rating INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 10),
          comment TEXT,
          created_at TEXT DEFAULT (datetime('now')),
          FOREIGN KEY(movie_id) REFERENCES movies(id) ON DELETE CASCADE,
          FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
        )
        """;

        try (Connection conn = getConnection();
             Statement st = conn.createStatement()) {

            st.execute(createUsers);

            // ---- MIGRATION: ensure role column exists ----
            if (!columnExists(conn, "users", "role")) {
                // Add role with default USER
                st.execute("ALTER TABLE users ADD COLUMN role TEXT NOT NULL DEFAULT 'USER';");
            }

            // Create other tables
            st.execute(createMovies);
            st.execute(createReviews);

            // Ensure default admin exists and has ADMIN role
            createDefaultAdmin(conn);
            st.executeUpdate("UPDATE users SET role='ADMIN' WHERE email='admin@minimdp.com';");

        } catch (SQLException e) {
            System.err.println("DB init error: " + e.getMessage());
        }
    }

    // Uses PRAGMA table_info to check columns
    private static boolean columnExists(Connection conn, String table, String column) throws SQLException {
        String sql = "PRAGMA table_info(" + table + ")";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                if (column.equalsIgnoreCase(rs.getString("name"))) return true;
            }
        }
        return false;
    }


    private static void createDefaultAdmin(Connection conn) throws SQLException {
        String check = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(check)) {
            ps.setString(1, "admin@minimdp.com");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String insert = "INSERT INTO users (name,email,password,role) VALUES (?,?,?,?)";
                    try (PreparedStatement ins = conn.prepareStatement(insert)) {
                        ins.setString(1, "Admin");
                        ins.setString(2, "admin@minimdp.com");
                        ins.setString(3, "admin123");
                        ins.setString(4, "ADMIN");
                        ins.executeUpdate();
                        System.out.println("Default admin: admin@minimdp.com / admin123");
                    }
                }
            }
        }
    }

    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException e) {
            System.err.println("DB close error: " + e.getMessage());
        }
    }
}
