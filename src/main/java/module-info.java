module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example to javafx.fxml;
    opens com.example.model to javafx.fxml;
    opens com.example.controller to javafx.fxml;
    opens com.example.vue to javafx.fxml;
    
    exports com.example;
    exports com.example.model;
    exports com.example.controller;
    exports com.example.vue;
}
