package webGen;

public enum FileType {
	HTML(".html"), GEMINI(".gmi"), MARKDOWN(".md");
	
	private String format;
	
	private FileType(String format) {
		this.format = format;
	}
	
	public String getFormat() {
		return format;
	}
}
