package webGen;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class FormattingMethods {
	private FormattingMethods() {}
	
	public static String formatDate(LocalDateTime dateTime) {
		DateTimeFormatter formatterWithTime = DateTimeFormatter.ofPattern("EEE dd MMM yyyy[ 'at' HH:mm]");
		DateTimeFormatter formatterWithoutTime = DateTimeFormatter.ofPattern("EEE dd MMM yyyy");

		if (dateTime.getHour() == 0 && dateTime.getMinute() == 0) {
			return dateTime.format(formatterWithoutTime);
		} else
			return dateTime.format(formatterWithTime);
	}

	public static String getHorizontalRule() {
		switch (BuildOptions.getFormattedFileType()) {
			case HTML:
				return "\n<hr/>\n";

			case GEMINI, MARKDOWN:
				return "\n---\n";

			default:
				return "";
		}
	}

	public static String getNewLine() {
		switch (BuildOptions.getFormattedFileType()) {
			case HTML:
				return "<br/>\n";

			case GEMINI, MARKDOWN:
				return "  \n";

			default:
				return "";
		}
	}

	public static String emphasiseText(String text) {
		switch (BuildOptions.getFormattedFileType()) {
			case HTML:
				return String.format("<em>%1$s</em>", text);

			case GEMINI, MARKDOWN:
				return String.format("*%1$s*", text);

			default:
				return "";
		}
	}

	public static String makeSmallText(String text) {
		switch (BuildOptions.getFormattedFileType()) {
			case HTML:
				return String.format("<small>%1$s</small>", text);

			case MARKDOWN:
				return String.format("~%1$s~", text);

			case GEMINI:
				return text;

			default:
				return "";
		}
	}
	
	public static Object buildImage(String imageAlt, String imageUrl, String imageTitle) {
		switch (BuildOptions.getFormattedFileType()) {
			case HTML:
				// TODO: figcaption? lazy loading?
				if (imageTitle.isBlank())
					return String.format("<img src=\"%2$s\" alt=\"%1$s\" loading=\"lazy\" />", imageAlt, imageUrl);
				else
					return String.format("<img src=\"%2$s\" alt=\"%1$s\" title=\"%3$s\" loading=\"lazy\" />", imageAlt, imageUrl,
							imageTitle);

			case MARKDOWN:
				return "TODO";

			case GEMINI:
				return "TODO";

			default:
				return "";
		}
	}
	
	public static String buildLink(String linkText, String linkUrl, String linkTooltip) {
		String indexPage = "index" + BuildOptions.getFormattedFileType().getFormat();
		if (linkUrl.endsWith(indexPage)) {
			linkUrl = linkUrl.substring(0, linkUrl.length() - indexPage.length());
		}

		switch (BuildOptions.getFormattedFileType()) {
			case HTML:
				linkUrl = linkUrl.replace(" ", "%20");
				if (linkTooltip.isBlank())
					return String.format("<a href=\"%1$s\">%2$s</a>", linkUrl, linkText);
				else
					return String.format("<a href=\"%1$s\" title=\"%3$s\">%2$s</a>", linkUrl, linkText, linkTooltip);

			case MARKDOWN:
				return "TODO";

			case GEMINI:
				return "TODO";

			default:
				return "";
		}
	}

	public static String escapeHTML(String str) {
		return str.codePoints()
				.mapToObj(c -> c > 127 || "\"'<>&".indexOf(c) != -1 ? "&#" + c + ";" : new String(Character.toChars(c)))
				.collect(Collectors.joining());
	}
	
	public static String getPreformattedToggle(boolean isPreformatted) {
		switch (BuildOptions.getFormattedFileType()) {
			case HTML:
				if (isPreformatted)
					return "</code></pre>\n\n";
				else
					return "<pre><code>";

			case GEMINI, MARKDOWN:
				return "```";

			default:
				return "";
		}
	}

	public static String getInlineCodeToggle(boolean isInlineCode) {
		switch (BuildOptions.getFormattedFileType()) {
			case HTML:
				if (isInlineCode)
					return "</code>";
				else
					return "<code>";

			case GEMINI, MARKDOWN:
				return "`";

			default:
				return "";
		}
	}

	// TODO: Change over to accept boolean isInParagraph like other methods
	public static String getStartParagraph() {
		switch (BuildOptions.getFormattedFileType()) {
			case HTML:
				return "<p>";

			case GEMINI, MARKDOWN:
				return "  \n";

			default:
				return "";
		}
	}

	public static String getEndParagraph() {
		switch (BuildOptions.getFormattedFileType()) {
			case HTML:
				return "</p>\n\n";
				
			case GEMINI, MARKDOWN:
				return "";

			default:
				return "";
		}
	}
	
	public static String formatHeader(String text, int level) {
		String id = text.toLowerCase().strip().replace(" ", "-").replace("<", "").replace(">", "");
		return formatHeader(text, level, id);
	}

	public static String formatHeader(String text, int level, String id) {
		switch (BuildOptions.getFormattedFileType()) {
			case HTML:
				return String.format("<h%1$s id=\"%3$s\">%2$s</h%1$s>%n", level, text, id);

			case GEMINI, MARKDOWN:
				return "#".repeat(level) + " " + text + "\n";

			default:
				return "";
		}
	}
	
	public static String getBoldToggle(boolean isBold) {
		switch (BuildOptions.getFormattedFileType()) {
			case HTML:
				if (isBold) return "</strong>";
				else return "<strong>";
	
			case GEMINI, MARKDOWN:
				return "**";
	
			default:
				return "";
		}
	}
	
	public static String getEmToggle(boolean isEm) {
		switch (BuildOptions.getFormattedFileType()) {
			case HTML:
				if (isEm) return "</em>";
				else return "<em>";
	
			case GEMINI, MARKDOWN:
				return "*";
	
			default:
				return "";
		}
	}
}
