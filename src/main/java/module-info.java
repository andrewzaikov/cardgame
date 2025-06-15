module ru.labs.game {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires javatuples;

    opens ru.labs.game to javafx.fxml;
    exports ru.labs.game.model;
    exports ru.labs.game.app;
    opens ru.labs.game.app to javafx.fxml;
    opens ru.labs.game.model to javafx.fxml;
    exports ru.labs.game;
}