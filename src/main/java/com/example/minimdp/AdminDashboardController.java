package com.example.minimdp;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AdminDashboardController {

    @FXML private Label adminNameLabel;

    @FXML private TextField titleField;
    @FXML private TextField genreField;
    @FXML private TextField yearField;
    @FXML private TextArea descArea;
    @FXML private Label movieMsgLabel;

    @FXML private ComboBox<String> genreFilterCombo;
    @FXML private TableView<Movie> movieTable;
    @FXML private TableColumn<Movie, String> colTitle;
    @FXML private TableColumn<Movie, String> colGenre;
    @FXML private TableColumn<Movie, Integer> colYear;

    @FXML private TableView<Review> reviewTable;
    @FXML private TableColumn<Review, String> colMovie;
    @FXML private TableColumn<Review, String> colUser;
    @FXML private TableColumn<Review, Integer> colRating;
    @FXML private TableColumn<Review, String> colComment;
    @FXML private TableColumn<Review, String> colCreated;
    @FXML private Label reviewMsgLabel;

    @FXML
    public void initialize() {
        User admin = Session.getCurrentUser();
        adminNameLabel.setText("Admin: " + (admin != null ? admin.getName() : "Unknown"));

        colTitle.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTitle()));
        colGenre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getGenre()));
        colYear.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getYear()));

        colMovie.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMovieTitle()));
        colUser.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getUserEmail()));
        colRating.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getRating()));
        colComment.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getComment()));
        colCreated.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCreatedAt()));

        loadMovies();
        loadReviews();
    }

    @FXML
    public void loadMovies() {
        try {
            var genres = MovieDAO.getGenres();
            genreFilterCombo.setItems(FXCollections.observableArrayList());
            genreFilterCombo.getItems().add("ALL");
            genreFilterCombo.getItems().addAll(genres);
            if (genreFilterCombo.getValue() == null) genreFilterCombo.setValue("ALL");

            String selectedGenre = genreFilterCombo.getValue();
            movieTable.setItems(FXCollections.observableArrayList(MovieDAO.search("", selectedGenre)));
            movieMsgLabel.setText("");
        } catch (Exception e) {
            movieMsgLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleAddMovie() {
        try {
            String title = titleField.getText().trim();
            String genre = genreField.getText().trim();
            String yearText = yearField.getText().trim();
            String desc = descArea.getText().trim();

            if (title.isEmpty() || genre.isEmpty()) {
                movieMsgLabel.setText("Title and genre are required.");
                return;
            }

            Integer year = null;
            if (!yearText.isEmpty()) year = Integer.parseInt(yearText);

            MovieDAO.addMovie(new Movie(title, genre, year, desc));

            titleField.clear();
            genreField.clear();
            yearField.clear();
            descArea.clear();

            movieMsgLabel.setText("Movie added.");
            loadMovies();
        } catch (Exception e) {
            movieMsgLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void loadReviews() {
        try {
            reviewTable.setItems(FXCollections.observableArrayList(ReviewDAO.getAllReviews()));
            reviewMsgLabel.setText("");
        } catch (Exception e) {
            reviewMsgLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDeleteReview() {
        Review selected = reviewTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            reviewMsgLabel.setText("Select a review first.");
            return;
        }
        try {
            ReviewDAO.deleteReview(selected.getId());
            reviewMsgLabel.setText("Review deleted.");
            loadReviews();
        } catch (Exception e) {
            reviewMsgLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
