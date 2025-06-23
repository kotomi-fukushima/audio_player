package studiplayer.ui;

import java.io.File;
import java.net.URL;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import studiplayer.audio.AudioFile;
import studiplayer.audio.NotPlayableException;
import studiplayer.audio.PlayList;
import studiplayer.audio.SampledFile;
import studiplayer.audio.SortCriterion;
import studiplayer.audio.TaggedFile;

public class Player extends Application{
	private boolean useCertPlayList = false;
	private static final String PLAYLIST_DIRECTORY = "playlists/";
	public static final String DEFAULT_PLAYLIST = PLAYLIST_DIRECTORY + "DefaultPlayList.m3u";
	public static final String CERT_PLAYLIST = PLAYLIST_DIRECTORY + "playList.cert.m3u";
	private PlayList playList;
	private static final String INITIAL_PLAY_TIME_LABEL = "00:00";
	private static final String NO_CURRENT_SONG = "-";
	private SongTable songTable;
	private Button playButton;
	private Button pauseButton;
	private Button stopButton;
	private Button nextButton;
	private Label playListLabel;
	private Label playTimeLabel;
	private Label currentSongLabel;
	private Button filterButton;
	private TextField searchTextField;
	private ChoiceBox<SortCriterion> sortChoiceBox;
	private boolean isPaused = false;
	private TimerThread timerThread;
	private PlayerThread playerThread;
	
//	private void startThreads(boolean onlyTimer) {
//		// create and start TimerThread
//		timerThread = new TimerThread();
//        timerThread.start();
//        
//		// if !onlyTimer: create and start PlayerThread 
//        if (!onlyTimer) {
//        	playerThread = new PlayerThread();
//        	playerThread.start();
//        }
//		}
//	
//	private void terminateThreads(boolean onlyTimer) {
//		// terminate TimerThread, set thread reference to null
//		timerThread.terminate();
//		timerThread = null;
//		
//		// if !onlyTimer: terminate PlayerThread, 
//		// set thread reference to null
//		if (!onlyTimer) {
//			playerThread.terminate();
//			playerThread = null;
//		}
//		}
		
	private class PlayerThread extends Thread {
		private volatile boolean stopped = false;
		
		public void terminate() {
			SampledFile af = (SampledFile) playList.currentAudioFile();
			af.stop();
			stopped = true;
//			System.out.println("Terminate is working");
		}
		
		public void togglePaused() {
			SampledFile af = (SampledFile) playList.currentAudioFile();
			af.togglePause();
//			System.out.println("paused is working");

		}
		
		public void run() {
			stopped = false;
            SampledFile currentSong = (SampledFile) playList.currentAudioFile();
//            System.out.println("run is working");
            
			try {
				currentSong.play();
			} catch (NotPlayableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while(!stopped) {
				continue;
            }
			
		}
		}
	
	
	private class TimerThread extends Thread {
		private boolean stopped = false;
		
		public void terminate() {
			stopped = true;
		}
		
		public void run() {
			while(!stopped) {
                try {
                	SampledFile currentSong = (SampledFile) playList.currentAudioFile();
                    Platform.runLater(() -> playTimeLabel.setText(currentSong.formatPosition()));
                	Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}
	
	public void setUseCertPlayList(boolean value) {
		this.useCertPlayList = value;
	}
	
	public void loadPlayList(String pathname) {
		try {
			if (pathname == null || pathname.isEmpty()) {
			this.playList = new PlayList(DEFAULT_PLAYLIST);
		} else {
			this.playList = new PlayList(pathname);
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Button createButton(String iconfile) {
		 Button button = null;
		 try {
		 URL url = getClass().getResource(""
		 		+ "/icons/" + iconfile);
		 Image icon = new Image(url.toString());
		 ImageView imageView = new ImageView(icon);
		 imageView.setFitHeight(20);
		 imageView.setFitWidth(20);
		 button = new Button("", imageView);
		 } catch (Exception e) {
		 System.out.println("Image " + "icons/" 
		 + iconfile + " not found!");
		 System.exit(-1);
		 }
		 return button;
		}
	
	public void start (Stage stage) throws Exception{
		String playListLabelString = null;
		
		// initialize playList
		if (this.useCertPlayList) {
			this.playList = new PlayList(CERT_PLAYLIST);
		} else {
			FileChooser fileChooser = new FileChooser();
			 fileChooser.setTitle("Choose the File :)");
			 fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("M3U files", "*.m3u"));
             File selectedFile = fileChooser.showOpenDialog(null);
             if (selectedFile != null) {
            	 String selectedPathname = selectedFile.getAbsolutePath();
            	 this.loadPlayList(selectedPathname);
            	 playListLabelString = selectedPathname;
             } else {
            	 this.loadPlayList(null);
            	 playListLabelString = DEFAULT_PLAYLIST;
             }
         
		}
		
		// set title
		stage.setTitle("APA Player");

        // create the main layout
		BorderPane borderPane = new BorderPane();

        // create a main pane with a size of 600x400 pixels
        Scene scene = new Scene(borderPane, 600, 400);
        
		// Top content
		TitledPane filterTitlePane = new TitledPane();
		GridPane gridTop = new GridPane();
		
		// text field
		searchTextField = new TextField();
		
		// choice box
		sortChoiceBox = new ChoiceBox<>();
		sortChoiceBox.getItems().addAll(SortCriterion.values());
		sortChoiceBox.setValue(SortCriterion.DEFAULT);
		sortChoiceBox.prefWidthProperty().bind(searchTextField.widthProperty());   //same width as the text field
		filterButton = new Button("display");
		filterButton.setOnAction(e -> {
            String searchText = searchTextField.getText();
            SortCriterion sortCriterion = sortChoiceBox.getValue();
            playList.setSearch(searchText);
            playList.setSortCriterion(sortCriterion);
            songTable.refreshSongs();  
        });
		
		gridTop.add(new Label("Search text"), 0, 0);
		gridTop.add(searchTextField, 1, 0);
		gridTop.add(new Label("Sort by"), 0, 1);
		gridTop.add(sortChoiceBox, 1, 1);
		gridTop.add(filterButton, 2, 1);
		
		filterTitlePane.setText("filter");
		filterTitlePane.setContent(gridTop);
		
		borderPane.setTop(filterTitlePane);
		
		// Center
		songTable = new SongTable(this.playList);
		borderPane.setCenter(songTable);
		
		// Bottom
		VBox bottomVbox = new VBox();
		
		// first area
		GridPane gridBottom = new GridPane();
		playListLabel = new Label(playListLabelString);
		currentSongLabel = new Label(NO_CURRENT_SONG);
		playTimeLabel = new Label(INITIAL_PLAY_TIME_LABEL);
		
		gridBottom.add(new Label("Playlist"), 0, 0);
		gridBottom.add(playListLabel, 1, 0);
		gridBottom.add(new Label("Current Song"), 0, 1);
		gridBottom.add(currentSongLabel, 1, 1);
		gridBottom.add(new Label("Playtime"), 0, 2);
		gridBottom.add(playTimeLabel, 1, 2);
	
		// second area
		HBox buttonHbox = new HBox();
		buttonHbox.setAlignment(Pos.CENTER);
		playButton = createButton("play.jpg");
		pauseButton = createButton("pause.jpg");
		stopButton = createButton("stop.jpg");
		nextButton = createButton("next.jpg");
		
//		playButton.setOnAction(e -> {try {
//			playCurrentSong();
//		} catch (NotPlayableException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}});
//		pauseButton.setOnAction(e -> {try {
//			pauseCurrentSong();
//		} catch (NotPlayableException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}});
//		stopButton.setOnAction(e -> {stopCurrentSong();});
//		nextButton.setOnAction(e -> {playNextSong();});
		

		buttonHbox.getChildren().addAll(playButton, pauseButton, stopButton, nextButton);
		
		bottomVbox.getChildren().addAll(gridBottom, buttonHbox);
		borderPane.setBottom(bottomVbox);
		
		
		
		
		
		
		
		
		
		
		
		
	
		
		initializeButton();
		if (playList.size() == 0) {
			setButtonStates(true, true, true, true);
		} else {
			setButtonStates(false, true, true, false);
		}
        // set the scene on the stage
		stage.setScene(scene);
        
        // show the stage
        stage.show();
	}
	
	private void initializeButton() throws NotPlayableException{
		playButton.setOnAction(e -> {
			try {
				playCurrentSong();
			} catch (NotPlayableException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});	
		pauseButton.setOnAction(e -> {try {
			pauseCurrentSong();
		} catch (NotPlayableException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}});
		stopButton.setOnAction(e -> {stopCurrentSong();});
		nextButton.setOnAction(e -> {
			try {
				playNextSong();
			} catch (NotPlayableException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
	}
	
	private void setButtonStates(boolean playButtonState, boolean pauseButtonState, boolean stopButtonState, boolean nextButtonState) {
		playButton.setDisable(playButtonState);
        pauseButton.setDisable(pauseButtonState);
        stopButton.setDisable(stopButtonState);
        nextButton.setDisable(nextButtonState);
	}
	
	// for console outputs
	private void printSongInfo(String action, AudioFile song) {
        String songInfo = action + " " + song.getAuthor() + " - "  + song.getTitle();
        if (song instanceof TaggedFile) {
            TaggedFile taggedFile = (TaggedFile) song;
            songInfo += " - " + taggedFile.getAlbum();
        }
        if (song instanceof SampledFile) {
            SampledFile sampledFile = (SampledFile) song;
            songInfo += " - " + sampledFile.formatDuration();
        }
        System.out.println(songInfo);
    }
	
	private void updateSongInfo(AudioFile af) {
		 Platform.runLater(() -> {
		 if (af == null) {
		 // set currentSongLabel and playTimeLabel
			 playTimeLabel.setText(INITIAL_PLAY_TIME_LABEL);
			 currentSongLabel.setText(NO_CURRENT_SONG);
		 } else {
		 // set currentSongLabel and playTimeLabel
			 playTimeLabel.setText("00:00");   // set the playback time to "00:00"
			 currentSongLabel.setText(af.getTitle());
		 }
		 });
		}
	
	private void playCurrentSong() throws NotPlayableException {
        SampledFile currentSong = (SampledFile) playList.currentAudioFile();
        playerThread = new PlayerThread();
        playerThread.start();
        timerThread = new TimerThread();
        timerThread.start();
    	songTable.selectSong(currentSong);

        updateSongInfo(currentSong);
            
    	setButtonStates(true, false, false, false);   // adapt button states
                        
        printSongInfo("Playing", currentSong);
        System.out.println("Filename is " + currentSong.getFilename());
        
	}
	
	private void pauseCurrentSong() throws NotPlayableException {
		playerThread.togglePaused();
        SampledFile currentSong = (SampledFile) playList.currentAudioFile();

//		if (isPaused == false) {
//			isPaused = true;
//		} else {
//			isPaused = false;
//		}
		setButtonStates(true, false, false, false);   // adapt button states
		
        printSongInfo("Pausing", currentSong);
        System.out.println("Filename is " + currentSong.getFilename());
	}
	
	private void stopCurrentSong() {
		AudioFile currentSong = playList.currentAudioFile();
		
		playerThread.terminate();
		timerThread.terminate();
		setButtonStates(false, true, true, false);   // adapt button states
		
		updateSongInfo(currentSong);
		
		printSongInfo("Stopping", currentSong);
        System.out.println("Filename is " + currentSong.getFilename());
	}
	
	private void playNextSong() throws NotPlayableException {
		System.out.println("Switching to next audio file: stopped = false, paused = true");
	    this.stopCurrentSong();
		this.playList.nextSong();
        this.playCurrentSong();
		
		setButtonStates(true, false, false, false);  // adapt button states
		
        System.out.println("Switched to next audio file: stopped = false, paused = true");
        
	}
	
	public static void main (String[] args) {
		launch();
	}
}