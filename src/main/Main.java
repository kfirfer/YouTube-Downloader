package main;

import java.io.InputStream;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {

	private Stage stage;

	public static void main(String[] srg) {
		Application.launch(srg);
	}

	public void start(Stage primaryStage) {
		this.stage = primaryStage;

		stage.setTitle("Chat");
		stage.setMinHeight(400);
		stage.setMinWidth(400);

		load();
		stage.show();
	}

	private void load() {
		try {
			MainController login = (MainController) replaceSceneContent("Main.fxml");
			login.setApp(this);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private Initializable replaceSceneContent(String fxml) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		InputStream in = Main.class.getResourceAsStream(fxml);
		loader.setBuilderFactory(new JavaFXBuilderFactory());
		loader.setLocation(Main.class.getResource(fxml));
		AnchorPane page;
		try {
			page = (AnchorPane) loader.load(in);
		} finally {
			in.close();
		}
		int duration = 600;
		if (fxml.contains("StartScreen"))
			duration = 1000;

		FadeTransition ft = new FadeTransition(Duration.millis(duration), page);
		ft.setFromValue(0.1);
		ft.setToValue(1.0);
		ft.play();

		Scene scene = new Scene(page);
		stage.setScene(scene);
		stage.sizeToScene();
		return (Initializable) loader.getController();
	}
	
	public Stage getStage() {
		return this.stage;
	}
}
