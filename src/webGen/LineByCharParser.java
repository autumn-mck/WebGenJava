package webGen;

public class LineByCharParser {
	public LineByCharParser(String line) {
		this.line = line;
	}
	
	private String line;
	
	public String parseCharByChar() {
		boolean isParsingLink = false;
		int linkParsingStage = 0;
		boolean isParsingImage = false;
		int imageParsingStage = 0;
		
		String linkText = "";
		String linkUrl = "";
		String linkTooltip = "";
		String imageAlt = "";
		String imageUrl = "";
		String imageTitle = "";
		
		boolean isBold = false;
		boolean isItalic = false;
		
		boolean isInlineCode = false;

		StringBuilder lineBld = new StringBuilder();
		int len = line.length();
		
		for (int charIndex = 0; charIndex < len; charIndex++) {
			// Get the current character for easy reference
			Character c = line.charAt(charIndex);
			
			if (c == '`') {
				lineBld.append(FormattingMethods.getInlineCodeToggle(isInlineCode));
				isInlineCode = !isInlineCode;
			} else if ((c == '[' || c == '<') && !isInlineCode) {
				isParsingLink = true;
				linkParsingStage = 0;
				linkText = "";
				linkUrl = "";
				linkTooltip = "";
			} else if (isParsingLink) {
				switch (linkParsingStage) {
					case 0:
						if (c == ']') {
							linkParsingStage = 1;
							charIndex += 1;
						} else if (c == '>') {
							isParsingLink = false;
							linkUrl = linkText;
						} else linkText += c;
						break;
					case 1:
						if (c == ' ') {
							linkParsingStage = 2;
							charIndex += 1;
						} else if (c == ')')
							isParsingLink = false;
						else
							linkUrl += c;
						break;
					case 2:
						if (c == '"') {
							isParsingLink = false;
							charIndex += 1;
						} else
							linkTooltip += c;
						break;
					default:
						break;
				}

				if (!isParsingLink)
					lineBld.append(FormattingMethods.buildLink(linkText, linkUrl, linkTooltip));
			} else if (charIndex < len - 2 && line.substring(charIndex, charIndex + 3).equals("***") && !isInlineCode
					&& isBold && isItalic) {
				lineBld.append(FormattingMethods.getEmToggle(isItalic));
				lineBld.append(FormattingMethods.getBoldToggle(isBold));
				isBold = false;
				isItalic = false;
				charIndex += 2;
			} else if (charIndex < len - 1 && line.substring(charIndex, charIndex + 2).equals("**") && !isInlineCode) {
				lineBld.append(FormattingMethods.getBoldToggle(isBold));
				isBold = !isBold;
				charIndex += 1;
			} else if (c == '*' && !isInlineCode) {
				lineBld.append(FormattingMethods.getEmToggle(isItalic));
				isItalic = !isItalic;
			} else if (charIndex < len - 1 && line.substring(charIndex, charIndex + 2).equals("![") && !isInlineCode) {
				isParsingImage = true;
				charIndex += 1;
				imageParsingStage = 0;
				imageAlt = "";
				imageUrl = "";
				imageTitle = "";
			} else if (isParsingImage) {
				switch (imageParsingStage) {
					case 0:
						if (c == ']') {
							imageParsingStage = 1;
							charIndex += 1;
						} else
							imageAlt += c;
						break;
					case 1:
						if (c == ' ') {
							imageParsingStage = 2;
							charIndex += 1;
						} else if (c == ')')
							isParsingImage = false;
						else
							imageUrl += c;
						break;
					case 2:
						if (c == '"') {
							isParsingImage = false;
						} else
							imageTitle += c;
						break;
					default:
						break;
				}
				if (!isParsingImage)
					lineBld.append(FormattingMethods.buildImage(imageAlt, imageUrl, imageTitle));
			} else {
				if (isInlineCode)
					lineBld.append(FormattingMethods.escapeHTML(c.toString()));

				else
					lineBld.append(c);
			}
		}

		return lineBld.toString();
	}
	
}
