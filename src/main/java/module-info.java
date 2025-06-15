module ru.labs.game {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires javatuples;
    requires spring.boot.autoconfigure;
    requires spring.boot;
    requires spring.context;
    requires spring.web;
    requires spring.beans;

    opens ru.labs.game;
    opens ru.labs.game.app to javafx.fxml;
    opens ru.labs.game.model to javafx.fxml;
    exports ru.labs.game.model;
    exports ru.labs.game.app;
    exports ru.labs.game.rest;
    exports ru.labs.game;
    exports ru.labs.game.service to spring.beans;
}