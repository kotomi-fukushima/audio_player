package studiplayer.audio;

public class AudioFileFactory {
	private String fileExtension;
	
	public static AudioFile createAudioFile(String path) throws NotPlayableException {
		int lastDotPos = path.lastIndexOf(".");
		
		// if there is "." (=extension)
		if (lastDotPos != -1) {
			// get file extension from path
			String fileExtension = path.substring(lastDotPos + 1).toLowerCase();

			if (fileExtension.contains("wav")) {
				return new WavFile(path);
			} else if (fileExtension.contains("ogg") || fileExtension.contains("mp3")) {
				return new TaggedFile(path);
			} else {
				throw new NotPlayableException(path, "Unknown suffix for AudioFile \"" + path + "\"");
			}
		} else {
			throw new NotPlayableException(path, "Unknown suffix for AudioFile \"" + path + "\"");
		}
		
	}
}
