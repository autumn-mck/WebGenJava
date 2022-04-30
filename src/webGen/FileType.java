package webGen;

/**
 * Enum for different file types
 */
public enum FileType {
	HTML(".html"), GEMINI(".gmi"), MARKDOWN(".md");
	
	// The extension used for the given file format
	private String format;
	
	private FileType(String format) {
		this.format = format;
	}
	
	public String getFormat() {
		return format;
	}
}
