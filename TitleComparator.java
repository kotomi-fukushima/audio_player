package studiplayer.audio;

import java.util.Comparator;

public class TitleComparator implements Comparator<AudioFile>{

	@Override
	public int compare(AudioFile af1, AudioFile af2) {
		
		String title1 = af1.getTitle();
		String title2 = af2.getTitle();
		
		// if both null, the sorting order is same
		if (title1 == null && title2 == null) {
			return 0;
		} else if (title1 == null || title1.isEmpty()) {  	// if title1 is null, title1 should be before album2
			return -1;
		} else if (title2 == null || title2.isEmpty()) {	// if title2 is null, title2 should be before album1 
			return 1;
		}
 
        return title1.compareTo(title2);
	}
}
