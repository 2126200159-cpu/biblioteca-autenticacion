module edu.utj.evnd.poo.bibliotecaproyec {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;                 
    requires org.mariadb.jdbc;         

    opens edu.utj.evnd.poo.bibliotecaproyec to javafx.fxml;
    opens edu.utj.evnd.poo.bibliotecaproyec.controller to javafx.fxml;
    opens edu.utj.evnd.poo.bibliotecaproyec.model to javafx.base;   // ← NUEVO (si no estaba)

    exports edu.utj.evnd.poo.bibliotecaproyec;
}
