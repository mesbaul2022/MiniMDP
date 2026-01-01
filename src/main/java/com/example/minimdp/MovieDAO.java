package com.example.minimdp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {

    public static void addMovie(Movie movie) throws SQLException {
        String sql = "INSERT INTO movies (title,genre,year,description) VALUES (?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, movie.getTitle());
            ps.setString(2, movie.getGenre());
            if (movie.getYear() == null) ps.setNull(3, Types.INTEGER);
            else ps.setInt(3, movie.getYear());
            ps.setString(4, movie.getDescription());
            ps.executeUpdate();
        }
    }

    public static List<Movie> getAllMovies() throws SQLException {
        String sql = "SELECT * FROM movies ORDER BY title";
        List<Movie> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Movie(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("genre"),
                        (Integer) rs.getObject("year"),
                        rs.getString("description")
                ));
            }
        }
        return list;
    }

    public static List<String> getGenres() throws SQLException {
        String sql = "SELECT DISTINCT genre FROM movies ORDER BY genre";
        List<String> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(rs.getString(1));
        }
        return list;
    }

    public static List<Movie> search(String titleLike, String genre) throws SQLException {
        String sql = """
            SELECT * FROM movies
            WHERE (? = '' OR LOWER(title) LIKE '%' || LOWER(?) || '%')
              AND (? = 'ALL' OR genre = ?)
            ORDER BY title
            """;
        List<Movie> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, titleLike == null ? "" : titleLike.trim());
            ps.setString(2, titleLike == null ? "" : titleLike.trim());
            ps.setString(3, genre == null ? "ALL" : genre);
            ps.setString(4, genre == null ? "ALL" : genre);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Movie(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("genre"),
                            (Integer) rs.getObject("year"),
                            rs.getString("description")
                    ));
                }
            }
        }
        return list;
    }
}
