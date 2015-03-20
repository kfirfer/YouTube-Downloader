package main;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import com.github.axet.vget.VGet;

public class MainController extends AnchorPane implements Initializable {

	@FXML
	public Button search;

	@FXML
	public Button download;

	@FXML
	public Button killDownload;

	@FXML
	public Button saveAs;

	@FXML
	public TextField name;

	@FXML
	public TextField artist;

	@FXML
	public TextField saveAsText;

	@FXML
	public ProgressBar progress;

	@FXML
	public ListView<Object> list;

	@SuppressWarnings("unused")
	private Main main;

	public void setApp(Main main) {
		this.main = main;
	}

	public void initialize(URL location, ResourceBundle resources) {
	}

	@FXML
	public void handleSearch(ActionEvent event) {
		new Thread(()->{
			String url = name.getText();
			try {
				VGet v = new VGet(new URL(url), new File("download"));
				v.download();
			} catch (Exception e) {
				System.err.println(e);
			}
		}).start();

		
		
	}

	@FXML
	public void handleDownload(ActionEvent event) {
		System.out.println(artist.getText());
	}

	@FXML
	public void handleKillDownload(ActionEvent event) {
		System.out.println("kill download");
	}

	@FXML
	public void handleSaveAs(ActionEvent event) {
		System.out.println("save as");
	}

}
