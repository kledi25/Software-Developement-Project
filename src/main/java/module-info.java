module com.example.sew_projekt_varianteb {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;


    opens com.example.sew_projekt_varianteb to javafx.fxml;
    exports com.example.sew_projekt_varianteb;
}