package studiplayer.audio;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class PlayList implements Iterable<AudioFile>{
	
    private LinkedList<AudioFile> list;
    private SortCriterion sort;
    private String search;
    private ControllablePlayListIterator iterator;
    private AudioFile currentsong;

	//Default CTOR
	public PlayList(){
		this.list = new LinkedList<>();
		this.sort = SortCriterion.DEFAULT;
		this.newIterator();
	}
	
	public void newIterator() {
		this.iterator = new ControllablePlayListIterator(list, search, sort);
	}
	
	public PlayList(String m3uPathname) {
		this();
		loadFromM3U(m3uPathname);
		this.newIterator();  // the playback sequence starts from the beginning when adding or deleting a song or loading a new M3U file
	}
	
	public void add(AudioFile file) {
		 this.list.add(file);
		 this.newIterator();
	}
	
	public void remove(AudioFile file) {
		 this.list.remove(file);
		 this.newIterator();
	}
	
	public int size() {
		 return this.list.size();
	}
	
	public AudioFile currentAudioFile() {
		if (this.size() == 0) {
			return null;  //there is no song in play list
		}
		if (!this.iterator.hasNext()) {
			this.iterator.reset();
		}
		currentsong = iterator.currentAudioFile();
		return currentsong;
	}

	public void nextSong() {
		if (this.size() > 0) {
			currentsong = iterator.next();
			if (!this.iterator.hasNext()) {
				this.iterator.reset();
			}
		}
	}
	
	public void loadFromM3U(String pathname){
		Scanner scanner = null;
		this.list = new LinkedList<>();
		
		try {
			// open the file for reading
			scanner = new Scanner(new File(pathname));
		} catch (Exception e) {
			throw new RuntimeException("File not found: " + pathname);
		} finally {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				
				// check if the line is not comment or blank
				if (line.trim().equals("") || line.startsWith("#")) {
					continue; // ignore do nothing
				} else {
					try {
						AudioFile audioFile = AudioFileFactory.createAudioFile(line);
						this.list.add(audioFile);
					} catch (Exception e) {
						// ignore
					}
				}
			}
			scanner.close();	
		}
	}
	
	public void saveAsM3U(String pathname){
		FileWriter writer = null;

		try {
			// create the file if it does not exist, otherwise reset the file and open it for writing
			writer = new FileWriter(pathname);
			
			// get pathname from list of AudioFile
			for (int i = 0; i < list.size(); i++) {
				writer.write(list.get(i).getPathname() + "\n");
			}
			
			// more simple code
//			for (AudioFile af : list) {
//				writer.write(af.getPathname());
//			}
			
		} catch (Exception e) {
			throw new RuntimeException("Unable to save file " + pathname + "!");
		} 
		finally {
			try {
				// close the file writing back all buffers
				writer.close();
			} catch (Exception e) {
				// ignore exception; probably because file could not be opened
			}
		}
	}
	
	public List<AudioFile> getList() {
		return this.list;
	}
	
	public void jumpToAudioFile(AudioFile file) {
		currentsong = iterator.jumpToAudioFile(file);
	}
	
	public String getSearch() {
		return search;
	}
	
	public void setSearch(String value) {
		this.search = value;
		this.iterator = new ControllablePlayListIterator(list, search, sort);
	}
	
	public SortCriterion getSortCriterion() {
		return sort;
	}
	
	public void setSortCriterion(SortCriterion sort) {
		this.sort = sort;
		this.iterator = new ControllablePlayListIterator(list, search, sort);
	}

	public ControllablePlayListIterator iterator() {
		return new ControllablePlayListIterator(this.list, this.search, this.sort);
	}
	
	public String toString() {
		if (list == null || list.isEmpty()) {
			return "[]";
		}
		StringBuilder playListStr = new StringBuilder();
		
		for (AudioFile af: list) {
			playListStr.append(af).append(", ");
		}
		int lastIndex = playListStr.lastIndexOf(", ");
		
		return "[" + playListStr.substring(0, lastIndex) + "]";
	}
	
}
