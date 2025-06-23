package studiplayer.audio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class ControllablePlayListIterator implements Iterator<AudioFile>{
	private List<AudioFile> audioFile;
	private int current;
	private int nextCalled = 0;  // if next() has been called, it's 1
	
	// Default CTOR
	public ControllablePlayListIterator(List<AudioFile> audioFile) {
		this.audioFile = audioFile;
		this.reset();   // iterate from first song
	}
	
	public void reset() {
		this.current = 0;
	}
	
	// new CTOR
	public ControllablePlayListIterator(List<AudioFile> list, String search, SortCriterion sort) {
		if (search == null || search.isEmpty()) {
			this.audioFile = new ArrayList<>(list);
		} else {
			// search author, title or album containing String search
			// make an empty list filterFile, set true if author, title or album containing String search,
			// and add filterFile at the end to avoid overlap
			List<AudioFile> filterFile = new ArrayList<AudioFile>();
			for (AudioFile af : list) {
				boolean contain = false;
				if (af.getAuthor().contains(search)) {
					contain = true;
				}
				if (af.getTitle().contains(search)) {
					contain = true;
				}
				if (af instanceof TaggedFile) {
					String albumName = ((TaggedFile) af).getAlbum();
					if (albumName != null && albumName.contains(search)) {
						contain = true;
					}
				}
				if (contain) {
					filterFile.add(af);
				}
			}
			
			this.audioFile = filterFile;
		}
		
		// sort filterFile using sortCriterion
//		if (sort != null && sort != SortCriterion.DEFAULT) {
//			if (sort == SortCriterion.AUTHOR) {
//				Collections.sort(audioFile, new AuthorComparator());
//			} else if (sort == SortCriterion.TITLE) {
//				Collections.sort(audioFile, new TitleComparator());
//			} else if (sort == SortCriterion.ALBUM) {
//				Collections.sort(audioFile, new AlbumComparator());
//			} else if (sort == SortCriterion.DURATION) {
//				Collections.sort(audioFile, new DurationComparator());
//			}
//		} 
		
//		switch (sort) {
//			case AUTHOR:
//				Collections.sort(audioFile, new AuthorComparator());
//				break;
//			case TITLE:
//				Collections.sort(audioFile, new TitleComparator());
//				break;
//			case ALBUM:
//				Collections.sort(audioFile, new AlbumComparator());
//				break;
//			case DURATION:
//				Collections.sort(audioFile, new DurationComparator());
//				break;
//			default:
//				break;
//		}
		
		Comparator<AudioFile> comparator = null;
		switch (sort) {
		case AUTHOR:
			comparator = new AuthorComparator();
			break;
		case TITLE:
			comparator = new TitleComparator();
			break;
		case ALBUM:
			comparator = new AlbumComparator();
			break;
		case DURATION:
			comparator = new DurationComparator();
			break;
		default:
			break;
		}
	
		if (comparator != null) {
			this.audioFile.sort(comparator);
		}
		
		this.reset();
		
	}
	
	public boolean hasNext() {
		return current < audioFile.size();
	}
	
	public AudioFile next() {
		if (!hasNext()) {
		this.reset();
		}
		AudioFile currentAF = this.currentAudioFile();
		this.current ++;
		this.nextCalled = 1;
		return currentAF;
		
	}
	
	public AudioFile jumpToAudioFile(AudioFile file) {
		int index = audioFile.indexOf(file);
		// if the file exist, return this file
		if(index != -1) {
			this.current = index + nextCalled;
			return file;
		} else {
			return null;
		}
	}
	
	public AudioFile currentAudioFile() {
		return audioFile.get(this.current);
	}
	
	public static void main(String[] args) throws NotPlayableException{
        // Create a list of AudioFile instances
        List<AudioFile> files = Arrays.asList(
                new TaggedFile("audiofiles/Rock 812.mp3"),
                new TaggedFile("audiofiles/Eisbach Deep Snow.ogg"),
                new TaggedFile("audiofiles/wellenmeister_awakening.ogg"));

        // Create a ControllablePlayListIterator with the list
//        ControllablePlayListIterator it = new ControllablePlayListIterator(files);

//        for (AudioFile af : files) {
//        	System.out.println(af);
//        }
        
    	System.out.println(files);

//        
        
        // Iterate over the list and print each element
//        while (it.hasNext()) {
//            System.out.println(it.next());
        ControllablePlayListIterator it = new ControllablePlayListIterator(files);
        it.jumpToAudioFile(files.get(1));
        
        while(it.hasNext()) {
        		 System.out.println(it.next());
        		}
        }
}
