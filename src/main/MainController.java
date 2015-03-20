package main;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;

import com.github.axet.vget.VGet;
import com.github.axet.vget.info.VGetParser;
import com.github.axet.vget.info.VideoInfo;
import com.github.axet.vget.vhs.VimeoInfo;
import com.github.axet.vget.vhs.YoutubeInfo;
import com.github.axet.wget.info.DownloadInfo;
import com.github.axet.wget.info.DownloadInfo.Part;
import com.github.axet.wget.info.URLInfo.States;

public class MainController extends AnchorPane implements Initializable {

	@FXML
	public Button searchButton;

	@FXML
	public Button downloadButton;

	@FXML
	public Button killDownload;

	@FXML
	public Button saveAs;

	@FXML
	public TextField searchText;

	@FXML
	public TextField downloadText;

	@FXML
	public TextField saveAsText;

	@FXML
	public ProgressBar progress;

	@FXML
	public ListView<Object> list;

	private Main main;

	private File path;
	private AtomicBoolean stop;
	private VideoInfo info;
	private long last;

	public void setApp(Main main) {
		this.main = main;
	}

	public void initialize(URL location, ResourceBundle resources) {
		stop = new AtomicBoolean();
		path = new File(System.getProperty("user.home"));
		saveAsText.setText(path.getAbsolutePath());
	}

	
	@FXML
	public void handleSearch(ActionEvent event) {
		System.out.println(searchText.getText());
	}
	
	@FXML
	public void handleDownload(ActionEvent event) {
		String url = downloadText.getText();
		stop.set(false);
		new Thread(() -> {

			try {
				Runnable notify = new Runnable() {
					@Override
					public void run() {
						VideoInfo i1 = info;
						DownloadInfo i2 = i1.getInfo();
						
						// notify app or save download state
						// you can extract information from DownloadInfo info;
						switch (i1.getState()) {
						case EXTRACTING:
						case EXTRACTING_DONE:
						case DONE:
							if (i1 instanceof YoutubeInfo) {
								YoutubeInfo i = (YoutubeInfo) i1;
								System.out.println(i1.getState() + " " + i.getVideoQuality());
							} else if (i1 instanceof VimeoInfo) {
								VimeoInfo i = (VimeoInfo) i1;
								System.out.println(i1.getState() + " " + i.getVideoQuality());
							} else {
								System.out.println("downloading unknown quality");
							}
							progress.setProgress(0.0);
							break;
						case RETRYING:
							System.out.println(i1.getState() + " " + i1.getDelay());
							break;
						case DOWNLOADING:
							long now = System.currentTimeMillis();
							if (now - 100 > last) {
								last = now;

								String parts = "";

								List<Part> pp = i2.getParts();
								if (pp != null) {
									// multipart download
									for (Part p : pp) {
										if (p.getState().equals(States.DOWNLOADING)) {
											parts += String.format("Part#%d(%.2f) ", p.getNumber(), p.getCount()
													/ (float) p.getLength());
										}
									}
								}
								
								progress.setProgress(i2.getCount() / (float) i2.getLength());
								System.out.println(String.format("%s %.2f %s", i1.getState(), i2.getCount()
										/ (float) i2.getLength(), parts));
							}
							break;
						default:
							break;
						}
					}
				};

				URL web = new URL(url);

				// [OPTIONAL] limit maximum quality, or do not call this
				// function if
				// you wish maximum quality available.
				//
				// if youtube does not have video with requested quality,
				// program
				// will raise en exception.
				VGetParser user = null;

				// create proper html parser depends on url
				user = VGet.parser(web);

				// download maximum video quality from youtube
				// user = new YouTubeQParser(YoutubeQuality.p480);

				// download mp4 format only, fail if non exist
				// user = new YouTubeMPGParser();

				// create proper videoinfo to keep specific video information
				info = user.info(web);

				VGet v = new VGet(info, path);

				// [OPTIONAL] call v.extract() only if you d like to get video
				// title
				// or download url link
				// before start download. or just skip it.
				v.extract(user, stop, notify);
				
				System.out.println("Title: " + info.getTitle());
				System.out.println("Download URL: " + info.getInfo().getSource());

				v.download(user, stop, notify);
			} catch (RuntimeException e) {
				System.err.println(e);
				progress.setProgress(0.0);
			} catch (Exception e) {
				System.err.println(e);
				progress.setProgress(0.0);
			}

		}).start();

	}



	@FXML
	public void handleKillDownload(ActionEvent event) {
		stop.set(true);
		progress.setProgress(0.0);
	}

	@FXML
	public void handleSaveAs(ActionEvent event) {
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Save as");
		
		chooser.setInitialDirectory(new File(System.getProperty("user.home"))); 
		File file = chooser.showDialog(main.getStage());
		if(file != null) 
			path = file;
		
		saveAsText.setText(path.getAbsolutePath());
	}

}
