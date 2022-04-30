package webGen;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class PostParsing {
	private PostParsing() {}
	
	public static Post fileToPost(File file) {
		initValues();
		String rawIn = FileIO.readFile(file.getPath());
		// Replace CRLF with LF (Line endings)
		rawIn = rawIn.replace("\r\n", "\n");
		String[] fileArr = rawIn.split("\n");
		
		String oldPath = file.getPath().substring(BuildOptions.getInDir().length());
		String newPath = oldPath.substring(0, oldPath.length() - BuildOptions.getPreFormattedFileType().getFormat().length())
				+ BuildOptions.getFormattedFileType().getFormat();
		System.out.println("New path: " + newPath);

		int index = 0;
		
		String title = fileArr[index];
		index++;
		
		String description = fileArr[index];
		index++;

		LocalDateTime publishDate = LocalDateTime.MIN;
		if (!fileArr[index].equals(BuildOptions.getPostStartSeperator())) {
			try {
				publishDate = parseDateTime(fileArr[index]);
				index++;
			}
			catch (Exception ex) { }
		}

		LocalDateTime updateDate = LocalDateTime.MIN;
		if (!fileArr[index].equals(BuildOptions.getPostStartSeperator())) {
			try {
				updateDate = parseDateTime(fileArr[index]);
				index++;
			}
			catch (Exception ex) { }
		}

		String[] tags = new String[] { "untagged" };
		if (!fileArr[index].equals(BuildOptions.getPostStartSeperator())) {
			tags = fileArr[index].toLowerCase().split(", ");
		}

		if (!fileArr[index].equals(BuildOptions.getPostStartSeperator())) {
			index++;
		}
		
		String content = parseBody(fileArr, index);

		return new Post(newPath, title, description, publishDate, updateDate, tags, content);
	}
	
	private static StringBuilder bld = new StringBuilder();
	
	private static String prevLine = "";
	private static String currentLine = "";
	private static String nextLine = "";
	private static String unmodifiedLine = "";
	private static String unmodifiedPrevLine = "";
	
	private static boolean isInParagraph = false;
	private static boolean isPreformatted = false;
	private static boolean shouldParseByChar = true;
	
	private static boolean isLineHeading = false;
	private static boolean wasPrevLineHeading = false;
	
	private static int unorderedListLayer = 0;
	
	private static boolean nextLineExists = true;
	
	private static void initValues() {
		bld = new StringBuilder();
		
		prevLine = "";
		currentLine = "";
		nextLine = "";
		unmodifiedLine = "";
		unmodifiedPrevLine = "";
		
		isInParagraph = false;
		isPreformatted = false;
		
		isLineHeading = false;
		wasPrevLineHeading = false;
		
		unorderedListLayer = 0;
		
		nextLineExists = true;
	}
	
	private static String parseBody(String[] body, int index) {
		index++;
		nextLine = body[index];
		index++;

		while (nextLineExists) {
			shouldParseByChar = true;
			wasPrevLineHeading = isLineHeading;
			isLineHeading = false;

			prevLine = currentLine;
			currentLine = nextLine;
			unmodifiedPrevLine = unmodifiedLine;
			unmodifiedLine = currentLine;

			nextLineExists = index < body.length;
			if (nextLineExists) {
				nextLine = body[index];
				index++;
			}

			// Escape characters if preformatted
			if (isPreformatted && BuildOptions.getFormattedFileType() == FileType.HTML) {
				currentLine = FormattingMethods.escapeHTML(currentLine);
			}
			
			if (currentLine.equals("```")) {
				if (!isPreformatted) endIfParagraph();
					
				currentLine = FormattingMethods.getPreformattedToggle(isPreformatted);
				isPreformatted = !isPreformatted;
				shouldParseByChar = false;
				
			}
			
			if (isPreformatted) shouldParseByChar = false;

			if (shouldParseByChar) {
				LineByCharParser parser = new LineByCharParser(currentLine);
				currentLine = parser.parseCharByChar();
			}
			
			if (isLineUnorderedList()) {
				if (BuildOptions.getFormattedFileType() == FileType.HTML) {
					if (unorderedListLayer == 0) {
						endIfParagraph();
						unorderedListLayer = 1;
						bld.append("<ul>");
					}
					bld.append("\n");
					currentLine = currentLine.stripLeading().substring(1).stripLeading();
					currentLine = "\t<li>" + currentLine + "</li>";
				}
			} else if (shouldEndUnorderedList() && BuildOptions.getFormattedFileType() == FileType.HTML) {
				unorderedListLayer = 0;
				bld.append("\n</ul>\n\n");
			}

			// If preformatted
			if (isPreformatted && !unmodifiedPrevLine.equals("```")) {
				bld.append("\n");
			}
			// If line is a header
			else if (currentLine.startsWith("#")) {
				endIfParagraph();
				isLineHeading = true;
				int headingLevel = getHeadingLevel(currentLine);
				currentLine = FormattingMethods.formatHeader(currentLine.substring(headingLevel), headingLevel);
			}
			// If line break needed
			else if (prevLine.endsWith("  ") && !currentLine.isBlank()) {
				bld.append(FormattingMethods.getNewLine());
			}
			// If new line but no line break needed
			else if (!wasPrevLineHeading && !prevLine.isBlank() && !currentLine.isBlank() && !unmodifiedPrevLine.equals("```")) {
				bld.append(" ");
			}
			// If current line is blank
			else if (currentLine.isBlank()) {
				endIfParagraph();
			}
			
			if (prevLine.isBlank() && currentLine.isBlank() && nextLine.isBlank()) bld.append(FormattingMethods.getNewLine());

			startParagraphIfNeeded();

			bld.append(currentLine);
		}
		if (unorderedListLayer > 0 && BuildOptions.getFormattedFileType() == FileType.HTML) {
			bld.append("\n</ul>\n\n	");
		}
		endIfParagraph();

		return bld.toString();
	}
	
	private static boolean isLineUnorderedList() {
		// TODO: Rework to support indented lists!
		return !isPreformatted && currentLine.startsWith("-") || currentLine.startsWith(" -");
	}
	
	private static boolean shouldEndUnorderedList() {
		return !isPreformatted && unorderedListLayer > 0;
	}
	
	private static void startParagraphIfNeeded() {
		if (!isInParagraph && !isLineHeading && !currentLine.isBlank() && unorderedListLayer == 0 && !isPreformatted && shouldParseByChar) {
			bld.append(FormattingMethods.getStartParagraph());
			isInParagraph = true;
		}
	}
	
	private static void endIfParagraph() {
		if (isInParagraph) {
			bld.append(FormattingMethods.getEndParagraph());
			isInParagraph = false;
		}
	}

	private static int getHeadingLevel(String nextLine) {
		for (int i = 0; i < nextLine.length(); i++) {
			if (nextLine.charAt(i) != '#')
				return i;
		}
		return 0;
	}

	private static LocalDateTime parseDateTime(String toParse) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy[ HH:mm]");
		TemporalAccessor temporalAccessor = formatter.parseBest(toParse, LocalDateTime::from, LocalDate::from);
		if (temporalAccessor instanceof LocalDateTime dateTime) {
			return dateTime;
		} else {
			return ((LocalDate) temporalAccessor).atStartOfDay();
		}
	}
}
