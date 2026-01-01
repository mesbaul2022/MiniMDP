module com.example.minimdp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.minimdp to javafx.fxml;
    exports com.example.minimdp;
}
