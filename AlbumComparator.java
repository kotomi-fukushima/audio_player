package studiplayer.audio;

import java.util.Comparator;

public class AlbumComparator implements Comparator<AudioFile>{
	
	@Override
	public int compare(AudioFile af1, AudioFile af2) {
		
		if (!(af1 instanceof TaggedFile) && !(af2 instanceof TaggedFile)) {
			return 0;
		}
		
		// initialize to null
		String album1 = null;
		String album2 = null;
		
		if (af1 instanceof TaggedFile) {
			album1 = ((TaggedFile) af1).getAlbum();
		} else {
			album1 = "";
		}
		
		if (af2 instanceof TaggedFile) {
			album2 = ((TaggedFile) af2).getAlbum();
		} else {
			album2 = "";
		}
		
//		 if both null, the sorting order is same
		if ((album1 == null) && (album2 == null)) {
			return 0;
		} else if (album1 == null || album1.isEmpty()) {   // if album1 is null, album1 should be before album2
			return -1;
		} else if (album2 == null || album2.isEmpty()) {   // if album2 is null, album2 should be before album1
			return 1;      	     
		} else {
	        return album1.compareTo(album2);
		}

	}
}
