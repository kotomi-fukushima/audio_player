package studiplayer.audio;
import java.io.File;

public abstract class AudioFile {
	
	//Default CTOR
	public AudioFile() {
		this.author = "";
		this.title ="";
		this.filename ="";
		this.pathname ="";
	}
	
	//Main CTOR
	public AudioFile(String path) throws NotPlayableException {
		parsePathname(path); //sets pathname and filename
		parseFilename(filename); //sets author and title
		
		File file = new File(this.pathname); //sets file
		
		//check if file is readable
		if (!file.canRead()) {
			throw new NotPlayableException(this.pathname, "the file is not readable");
		}
	}
	
	
	//Attribute
		private String pathname, filename;  //private because of TaggedFile
		protected String author, title;
		
		public String getPathname() {
			return pathname;
		}
		
		public String getFilename() {
			return filename;
		}
		
		public String getAuthor() {
			return author.trim();
		}
		
		public String getTitle() {
			return title.trim();
		}	
	
	public void parsePathname(String s) {
		if (isWindows()) {
			pathname = s.trim();
			pathname = pathname.replace('/', '\\');   //change separators into Window's form
			do { 
				pathname = pathname.replace("\\\\", "\\");
				} while (pathname.indexOf("\\\\") >= 0);   //to eliminate separators in a row 
			int lastPathSep = pathname.lastIndexOf("\\");
			if (lastPathSep != -1) {   //if there is path separators
					filename = pathname.substring(lastPathSep+1);
			} else {
					filename = pathname;
			}
			filename = filename.trim();
		} else {
			//Linux or Mac
			pathname = s.trim();
			pathname = pathname.replace('\\', '/');   //change separators into Linux or Mac form
			do { 
				pathname = pathname.replace("//", "/");
				} while (pathname.indexOf("//") >= 0);   //to eliminate separators in a row
			int lastPathSep1 = pathname.lastIndexOf('/');
			int twoDotPos = pathname.indexOf(":");
			if (twoDotPos != -1) {
				pathname = pathname.replace(":", "");
				pathname = '/' + pathname;
			}
			if (lastPathSep1 != -1) {
				filename = pathname.substring(lastPathSep1+1);
			} else {
				filename = pathname;
			}
			filename = filename.trim();
			}
		}

	public void parseFilename(String s) {
		int dashPos = s.indexOf(" - ");
		int lastDotPos = s.lastIndexOf(".");
		
		if (dashPos != -1) {   //if there is author
			author = s.substring(0,dashPos); 
			author = author.trim();
			if (lastDotPos != -1) {
				title = s.substring(dashPos+3,lastDotPos); //if there is an extension
			} else {
				title = s.substring(dashPos+3);
			}
			title = title.trim();	
		} else {   //if there is no author
			if (lastDotPos != -1) {   //if there is a title 
				author = "";
				title = s.substring(0,lastDotPos);
				title = title.trim();
			} else if (s.equals("-")) {   //special case
				author = "";
				title = "-";
			} else {
				author = "";
				title = "";
			}
		}
	}
	
	private boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().indexOf("win") >= 0;
	} 
	
	public String toString() {
//		System.out.println(author);
//		System.out.println(title);
		if (author.isEmpty()) {
			return title;
		} else {
			return author + " - " + title;
		}
	}
	
	public abstract void play() throws NotPlayableException;
	
	public abstract void togglePause();
	
	public abstract void stop();

	public abstract String formatDuration();
	
	public abstract String formatPosition();
}

