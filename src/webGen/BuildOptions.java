package webGen;

import java.util.Arrays;
import java.util.List;

public class BuildOptions {
	private BuildOptions() {}
	
	private static FileType preFormattedFileType = FileType.MARKDOWN;
	private static FileType formattedFileType = FileType.HTML;
	
	private static String inDir = "C:\\Users\\James\\Desktop\\Website\\james-mck.github.io\\";
	private static String outDir = "C:\\Users\\James\\Desktop\\Website\\james-mck.github.io\\";
	
	private static List<String> ignoredFiles = Arrays.asList(
			".git");
	
	private static String postStartSeperator = "-----";

	public static FileType getPreFormattedFileType() {
		return preFormattedFileType;
	}

	public static FileType getFormattedFileType() {
		return formattedFileType;
	}

	public static String getInDir() {
		return inDir;
	}

	public static String getOutDir() {
		return outDir;
	}

	public static List<String> getIgnoredFiles() {
		return ignoredFiles;
	}

	public static String getPostStartSeperator() {
		return postStartSeperator;
	}
}
