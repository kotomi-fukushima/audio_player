package studiplayer.audio;
import studiplayer.basic.BasicPlayer;

public abstract class SampledFile extends AudioFile{
	protected long duration;
	// Default CTOR
	public SampledFile() {
		super();
	}
	
	//Main CTOR
	public SampledFile(String path) throws NotPlayableException {
		super(path);		
	}
	
	public void play() throws NotPlayableException {
		try {
			BasicPlayer.play(getPathname());
		} catch (Exception e) {
			throw new NotPlayableException(getPathname(), e);
		}
	}
	
	public void togglePause() {
		BasicPlayer.togglePause();
	}
	
	public void stop() {
		BasicPlayer.stop();
	}
	
	public String formatDuration() {
		return timeFormatter(getDuration());
	}
	
	public String formatPosition() {
		return timeFormatter(BasicPlayer.getPosition());
	}
	
	public static String timeFormatter(long timeInMicroSeconds) {
		if (timeInMicroSeconds < 0) {
			throw new RuntimeException("time is invalid!");
		}
		if (timeInMicroSeconds >= 6000000000L) {
			throw new RuntimeException("time is too long!");
		}
		
		//cast to seconds
		long duration = (timeInMicroSeconds / 1000000);
		
		int minutes = (int) (duration / 60);
		int seconds = (int) (duration % 60);
		
		return String.format("%02d:%02d", minutes, seconds);
	}
	
	public long getDuration() {
		return this.duration;
	}
}
