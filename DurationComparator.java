package studiplayer.audio;

import java.util.Comparator;

public class DurationComparator implements Comparator<AudioFile>{

	@Override
	public int compare(AudioFile af1, AudioFile af2) {
		
		// initialize to 0
		long duration1 = 0;
		long duration2 = 0;
		
		if (af1 instanceof SampledFile) {
			duration1 = ((SampledFile) af1).getDuration();
		}
		
		if (af2 instanceof SampledFile) {
			duration2 = ((SampledFile) af2).getDuration();
		}
		
		// if both 0, the sorting order is same
		if (duration1 == 0 && duration2 == 0) {
			return 0;
		} else if (duration1 == 0) {  		// if duration1 is 0, duration1 should be before album2
			return -1;
		} else if (duration2 == 0) {	    // if duration2 is 0, duration2 should be before album1 
			return 1;
		}
	    
        return Long.compare(duration1,duration2);
	}
}
