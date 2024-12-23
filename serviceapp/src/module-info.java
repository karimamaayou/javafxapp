module serviceapp {
	requires javafx.controls;
	requires java.sql;
	requires javafx.fxml;
	requires javafx.base;
	requires javafx.graphics;	
	
	opens application to javafx.graphics, javafx.fxml,javafx.base; //add javafx.base
}
