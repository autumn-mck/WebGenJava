package webGen;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileIO {
	private FileIO() {}
	
	public static String readFile(String pathStr) {
		Path path = Paths.get(pathStr);
		try {
			return Files.readString(path);
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public static void writeToFile(String str, String filePath) {
		try {
			Files.writeString(Path.of(filePath), str, StandardCharsets.UTF_8);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
