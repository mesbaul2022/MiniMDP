package com.example.minimdp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {

    public static void addReview(int movieId, int userId, int rating, String comment) throws SQLException {
        String sql = "INSERT INTO reviews (movie_id,user_id,rating,comment) VALUES (?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, movieId);
            ps.setInt(2, userId);
            ps.setInt(3, rating);
            ps.setString(4, comment);
            ps.executeUpdate();
        }
    }

    public static List<Review> getReviewsByMovie(int movieId) throws SQLException {
        String sql = """
            SELECT r.*, u.email as user_email
            FROM reviews r
            JOIN users u ON u.id = r.user_id
            WHERE r.movie_id = ?
            ORDER BY r.id DESC
            """;
        List<Review> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, movieId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Review r = new Review(
                            rs.getInt("id"),
                            rs.getInt("movie_id"),
                            rs.getInt("user_id"),
                            rs.getInt("rating"),
                            rs.getString("comment"),
                            rs.getString("created_at")
                    );
                    r.setUserEmail(rs.getString("user_email"));
                    list.add(r);
                }
            }
        }
        return list;
    }

    public static double getAverageRating(int movieId) throws SQLException {
        String sql = "SELECT AVG(rating) FROM reviews WHERE movie_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, movieId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        }
        return 0.0;
    }


    public static List<Review> getAllReviews() throws SQLException {
        String sql = """
            SELECT r.*, m.title as movie_title, u.email as user_email
            FROM reviews r
            JOIN movies m ON m.id = r.movie_id
            JOIN users u ON u.id = r.user_id
            ORDER BY r.id DESC
            """;
        List<Review> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Review r = new Review(
                        rs.getInt("id"),
                        rs.getInt("movie_id"),
                        rs.getInt("user_id"),
                        rs.getInt("rating"),
                        rs.getString("comment"),
                        rs.getString("created_at")
                );
                r.setMovieTitle(rs.getString("movie_title"));
                r.setUserEmail(rs.getString("user_email"));
                list.add(r);
            }
        }
        return list;
    }

    public static void deleteReview(int reviewId) throws SQLException {
        String sql = "DELETE FROM reviews WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reviewId);
            ps.executeUpdate();
        }
    }
}
