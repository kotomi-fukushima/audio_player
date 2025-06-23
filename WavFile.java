package studiplayer.audio;
import studiplayer.basic.BasicPlayer;
import studiplayer.basic.WavParamReader;

public class WavFile extends SampledFile {

	//Default CTOR
	public WavFile() {
		super();
	}
	
	//Main CTOR
	public WavFile(String path) throws NotPlayableException {
		super(path);
		readAndSetDurationFromFile();
	}
	
	public void readAndSetDurationFromFile() throws NotPlayableException {
		//read params
		try {
			WavParamReader.readParams(getPathname());
			
			//get number of frames and frame per seconds
			float framPerSeconds = WavParamReader.getFrameRate();
			long framNum = WavParamReader.getNumberOfFrames();
			this.duration = computeDuration(framNum, framPerSeconds);
		} catch (Exception e) {
			throw new NotPlayableException(getPathname(), e);
		}
					
	}
	
	public String toString() {
		return super.toString() + " - " + formatDuration();
		}

	// cast to microseconds
	public static long computeDuration(long numberOfFrames, float frameRate) {
		return (long) (1000000 *(numberOfFrames/frameRate));
	}
	
}