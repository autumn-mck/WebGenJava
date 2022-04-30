package webGen;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Static class used for file handling
 */
public class FileIO {
	private FileIO() {}
	
	/**
	 * Get the contents of the file with the given path
	 */
	public static String readFile(String pathStr) {
		Path path = Paths.get(pathStr);
		try {
			return Files.readString(path);
		} catch (IOException e) {
			// Print out any errors
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * Write the given string to the given file path
	 */
	public static void writeToFile(String str, String filePath) {
		try {
			Files.writeString(Path.of(filePath), str, StandardCharsets.UTF_8);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
