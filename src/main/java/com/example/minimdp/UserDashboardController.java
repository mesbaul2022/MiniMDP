package com.example.minimdp;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class UserDashboardController {

    @FXML private Label userNameLabel;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> genreCombo;

    @FXML private TableView<Movie> movieTable;
    @FXML private TableColumn<Movie, String> colTitle;
    @FXML private TableColumn<Movie, String> colGenre;
    @FXML private TableColumn<Movie, Integer> colYear;

    @FXML private Label ratingLabel;
    @FXML private ListView<String> reviewList;
    @FXML private Label movieInfoLabel;

    @FXML private ComboBox<Integer> ratingCombo;
    @FXML private TextArea commentArea;
    @FXML private Label msgLabel;

    @FXML
    public void initialize() {
        User user = Session.getCurrentUser();
        userNameLabel.setText("User: " + (user != null ? user.getName() : "Unknown"));

        colTitle.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTitle()));
        colGenre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getGenre()));
        colYear.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getYear()));

        ratingCombo.setItems(FXCollections.observableArrayList(1,2,3,4,5,6,7,8,9,10));
        ratingCombo.setValue(8);

        movieTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) loadMovieDetails(newV);
        });

        loadMovies();
    }

    @FXML
    public void loadMovies() {
        try {
            var genres = MovieDAO.getGenres();
            genreCombo.setItems(FXCollections.observableArrayList());
            genreCombo.getItems().add("ALL");
            genreCombo.getItems().addAll(genres);
            if (genreCombo.getValue() == null) genreCombo.setValue("ALL");

            movieTable.setItems(FXCollections.observableArrayList(MovieDAO.getAllMovies()));
            msgLabel.setText("");
        } catch (Exception e) {
            msgLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSearch() {
        try {
            String q = searchField.getText();
            String g = genreCombo.getValue() == null ? "ALL" : genreCombo.getValue();
            movieTable.setItems(FXCollections.observableArrayList(MovieDAO.search(q, g)));
        } catch (Exception e) {
            msgLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadMovieDetails(Movie movie) {
        try {
            movieInfoLabel.setText("Selected: " + movie.getTitle() + " (" + movie.getGenre() + ")");
            double avg = ReviewDAO.getAverageRating(movie.getId());
            ratingLabel.setText(String.format("Average rating: %.2f / 10", avg));

            var reviews = ReviewDAO.getReviewsByMovie(movie.getId());
            var items = FXCollections.<String>observableArrayList();
            for (Review r : reviews) {
                String line = r.getUserEmail() + " | " + r.getRating() + "/10 | " + (r.getComment() == null ? "" : r.getComment());
                items.add(line);
            }
            reviewList.setItems(items);
        } catch (Exception e) {
            msgLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSubmitReview() {
        Movie selected = movieTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            msgLabel.setText("Select a movie first.");
            return;
        }

        try {
            int rating = ratingCombo.getValue();
            String comment = commentArea.getText().trim();
            User user = Session.getCurrentUser();

            ReviewDAO.addReview(selected.getId(), user.getId(), rating, comment);

            commentArea.clear();
            msgLabel.setText("Review submitted.");
            loadMovieDetails(selected);
        } catch (Exception e) {
            msgLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
