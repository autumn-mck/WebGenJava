package webGen;

public class PostTemplate {
	public PostTemplate() {
		// Get the header and footer
		headerTemplate = FileIO.readFile(headerPath);
		footerTemplate = FileIO.readFile(footerPath);
	}
	
	private String headerPath = BuildOptions.getInDir() + "header" + BuildOptions.getFormattedFileType().getFormat();
	private String headerTemplate;
	private String footerPath = BuildOptions.getInDir() + "footer" + BuildOptions.getFormattedFileType().getFormat();
	private String footerTemplate;
	
	public String getHeaderTemplate() {
		return headerTemplate;
	}
	public String getFooterTemplate() {
		return footerTemplate;
	}
}
