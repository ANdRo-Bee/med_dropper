package application;

import java.net.URL;

import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;

/**
 * use slider with keyboard keys -> or <-
 * 
 * @author ANdRo-Bee
 *
 */

public class SampleController implements Initializable {

	private int sec;
	private Timer myTimer;
	private MyTimerStartTask myTimerStartTask;
	private MyTimerResumeTask myTimerResumeTask;
	private long elapsedTime;

	private double count = 0;
	private int ml;
	private int dropsSpeed;

	@FXML
	private Button startButton;

	@FXML
	private Button stopButton;

	@FXML
	private Button pauseButton;

	@FXML
	private Button resumeButton;

	@FXML
	private ProgressBar prBar1;

	@FXML
	private Slider manipulatorSlider;

	@FXML
	private ComboBox<Integer> sizeCmbx;

	ObservableList<Integer> size = FXCollections.observableArrayList(250, 500, 1000);

	@FXML
	private Label sizeLbl;

	@FXML
	private ComboBox<Integer> dropSpeedCmbx;

	ObservableList<Integer> dropSpeed = FXCollections.observableArrayList(10, 20, 30, 40, 50, 60, 70);

	@FXML
	private Label dropSpeedLbl;

	@FXML
	private Label takesTimeLbl;

	@FXML
	private Button addBtn;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		initBottleSizeCmbx();
		initDropsSpeedCmbx();

		manipulatorSlider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				int sliderValue = (int) manipulatorSlider.getValue();
				System.out.println(sliderValue);
				switch (sliderValue) {
				case 0:
					manipulatorSlider.setStyle("-fx-control-inner-background: #778899;");
					stopDropper();
					break;
				case 1:
					manipulatorSlider.setStyle("-fx-control-inner-background: #95ff1f;");
					startDropper();
					break;
				case 2:
					manipulatorSlider.setStyle("-fx-control-inner-background: #ffce1f;");
					pauseDropper();
					break;
				case 3:
					manipulatorSlider.setStyle("-fx-control-inner-background: #1e90ff;");
					resumeDropper();
					break;
				}
			}

		});
	}

	private int getCalculatedTime() {
		dropsSpeed = dropSpeedCmbx.getValue();
		ml = sizeCmbx.getValue();
		int drops = ml * 20;
		int min = drops / dropsSpeed;
		System.out.println("minute " + min);
		sec = min * 60;
		return sec;
	}

	private void initDropsSpeedCmbx() {
		
		dropSpeedCmbx.setItems(dropSpeed);
		dropSpeedCmbx.setOnAction(value -> {
			int msec = getCalculatedTime();
			dropSpeedLbl.setText("with " + dropSpeedCmbx.getValue().toString() + " drops/minute");
			takesTimeLbl.setText(" takes " + msec/60 + " min or " + msec/60/60 +" h "+ ((msec/60)%60) +" min");
		});
	}

	private void initBottleSizeCmbx() {
		sizeCmbx.setItems(size);
		sizeCmbx.setOnAction(value -> {
			sizeLbl.setText(sizeCmbx.getValue().toString() + " ml");
		});
	}

	public void startDropper() {
		elapsedTime = 0;
		myTimer = new Timer();
		System.out.println("Start");
		prBar1.setProgress(count);
		myTimerStartTask = new MyTimerStartTask(prBar1);
		myTimer.scheduleAtFixedRate(myTimerStartTask, 0, 1000);
		getCalculatedTime();
	}

	public void stopDropper() {
		System.out.println("Stop");
		myTimer.cancel();
		elapsedTime = 0;
	}

	public void pauseDropper() {

		if (myTimer != null) {
			myTimer.cancel();
		}
		System.out.println("Pause");
		System.out.println("elapsedTime was" + elapsedTime);
		System.out.println(elapsedTime / 100);
	}

	public void resumeDropper() {
		if (myTimer != null) {
			myTimer.cancel();
		}
		System.out.println("Resume");
		System.out.println("elapsedTime was" + elapsedTime);
		System.out.println(elapsedTime / 100);
		myTimer = new Timer();
		myTimerResumeTask = new MyTimerResumeTask(prBar1);
		myTimer.scheduleAtFixedRate(myTimerResumeTask, 0, 1000);
	}

	class MyTimerStartTask extends TimerTask {

		ProgressBar bar;
		double count;

		public MyTimerStartTask(ProgressBar b) {
			bar = b;
			count = 0;
		}

		@Override
		public void run() {
			bar.setProgress(count++ / sec);
			elapsedTime++;
			System.out.println("elapsedTime" + elapsedTime);
			if (count >= sec) {
				myTimer.cancel();
			}
		}
	}

	class MyTimerResumeTask extends TimerTask {

		ProgressBar bar;

		public MyTimerResumeTask(ProgressBar b) {
			bar = b;
		}

		@Override
		public void run() {
			count = elapsedTime / sec + elapsedTime % sec;
			System.out.println("elapsed-count Time" + count);
			bar.setProgress(count++ / sec);
			elapsedTime++;
			System.out.println("elapsedTime" + elapsedTime);
			if (count >= sec) {
				myTimer.cancel();
			}
		}
	}
	

}