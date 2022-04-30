package webGen;

import java.util.Arrays;
import java.util.List;

public class BuildOptions {
	private BuildOptions() {}
	
	private static FileType preFormattedFileType = FileType.MARKDOWN;
	private static FileType formattedFileType = FileType.HTML;
	
	private static String inDir = "C:\\Users\\James\\Desktop\\Website\\james-mck.github.io\\";
	private static String outDir = "C:\\Users\\James\\Desktop\\Website\\james-mck.github.io\\";
	
	// List of files to ignore
	// Note: Currently only very basic pattern matching, eg will ignore all files/folders containing `.git`
	private static List<String> ignoredFiles = Arrays.asList(
			".git", "README.md");
	
	// The line used to mark the boundary between information about the post and the post itself
	private static String postStartSeperator = "-----";

	// Getters for everything in this class
	
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
