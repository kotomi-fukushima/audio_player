package studiplayer.audio;
import java.util.Map;

import studiplayer.basic.TagReader;
import studiplayer.basic.WavParamReader;

public class TaggedFile extends SampledFile {
	
	//we only use album in this class
	private String album;
	
	//Default CTOR
	public TaggedFile() {
		super();
	}
	
	//Main CTOR
	public TaggedFile(String path) throws NotPlayableException {
		super(path);
		readAndStoreTags();
	}
	
	public String getAlbum() {
		return this.album;
	}
	
	public void readAndStoreTags() throws NotPlayableException {
		Map<String, Object> tagMap = null;
		
		//get map
		try {
			tagMap = TagReader.readTags(getPathname());
		} catch (Exception e) {
			throw new NotPlayableException(getPathname(), e);
		}
		//create objects
		Object tagtitle = tagMap.get("title");
		Object tagauthor = tagMap.get("author");
		Object tagalbum = tagMap.get("album");
		Object tagduration = tagMap.get("duration");
		
		//if the value of title or author is null, we need to get it from pathname
		if(tagtitle == null) {
			this.title = super.getTitle();
		} else {
			this.title = ((String) tagtitle).trim();
		}
		
		if(tagauthor == null) {
			this.author = super.getAuthor();
		} else {
			this.author = ((String) tagauthor).trim();
		}
		
		//if the value of album is null, return null
		if (tagalbum == null) {
			this.album = null;
		} else {
			this.album = ((String) tagalbum).trim();
		}
		
		//if the value of duration is null, we need to get it from getDuration()
		if (tagduration == null) {
			this.duration = super.getDuration();
		} else {
			this.duration = (long) tagduration;
		}
	}
	
	public String toString() {
		if (this.album == null) {
			return super.toString().trim() + " - " + formatDuration();
		} else {
			return super.toString().trim() + " - " + this.album.trim() + " - " + formatDuration();  		//author - title - album - duration
		}
	}
	
}
