module com.dhh.divyanshu {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.dhh.divyanshu to javafx.fxml;
    exports com.dhh.divyanshu;
}