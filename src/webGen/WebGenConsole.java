package webGen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.time.LocalDateTime;
import java.time.format.TextStyle;

public class WebGenConsole {
	// ArrayList for keeping track of all posts
	private static ArrayList<Post> allPosts = new ArrayList<>();

	public static void main(String[] args) {
		// Creating a File object for directory
		File directoryPath = new File(BuildOptions.getInDir());

		iterateThroughFiles(directoryPath);

		createBlogPage();
		createAtomFeed();
	}

	private static void createBlogPage() {
		StringBuilder bld = new StringBuilder();
		List<Post> byDate = allPosts.stream().sorted((x, y) -> y.getPubDate().compareTo(x.getPubDate())).toList();
		System.out.println(byDate.size());

		for (int i = 0; i < byDate.size(); i++) {
			boolean isFirst = i == 0;
			Post cur = byDate.get(i);
			Post prev = null;
			if (!isFirst)
				prev = byDate.get(i - 1);

			if (isFirst
					|| prev.getPubDate().getYear() > cur.getPubDate().getYear()) {
				String year = Integer.toString(cur.getPubDate().getYear());
				bld.append("\n\n" + FormattingMethods.formatHeader(year + "'s posts", 3, year));
			}
			if (isFirst
					|| prev.getPubDate().getYear() < cur.getPubDate().getYear()
					|| prev.getPubDate().getMonthValue() > cur.getPubDate().getMonthValue()) {
				String year = Integer.toString(cur.getPubDate().getYear());
				String monthStr = cur.getPubDate().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
				bld.append("\n" + FormattingMethods.formatHeader(monthStr, 4, monthStr + "-" + year));
			}
			if (!isFirst
					&& prev.getPubDate().getYear() == cur.getPubDate().getYear()
					&& prev.getPubDate().getMonthValue() == cur.getPubDate().getMonthValue()) {
				bld.append(FormattingMethods.getNewLine());
			}
			
			bld.append(FormattingMethods.buildLink(cur.getTitle(), "\\" + cur.getPath(), cur.getDescription()));

		}

		String hdr = String.format(StaticVars.getCurrentTemplate().getHeaderTemplate(), "Blog", "A list of my blog posts, ordered by date");
		String ftr = StaticVars.getCurrentTemplate().getFooterTemplate();
		String outStr = hdr + bld.toString() + ftr;

		FileIO.writeToFile(outStr, BuildOptions.getOutDir() + "\\blog\\index.html");
	}
	
	private static void createAtomFeed() {
		String site = "https://mck.is/";
		StringBuilder bld = new StringBuilder();
		List<Post> byDate = allPosts.stream().sorted((x, y) -> y.getPubDate().compareTo(x.getPubDate())).toList();
		System.out.println(byDate.size());
		
		LocalDateTime lastUpdated = LocalDateTime.MIN;

		for (int i = 0; i < byDate.size(); i++) {
			Post post = byDate.get(i);
			if (post.getUpdateDate().compareTo(lastUpdated) > 0) lastUpdated = post.getUpdateDate();

			bld.append("\n<entry>\n");
			bld.append(String.format("<title type=\"html\">%1$s</title>", post.getTitle()));
			bld.append("\n");
			bld.append(String.format("<link href=\"%1$s\" rel=\"alternate\" type=\"text/html\" title=\"%2$s\"/>",
					site + post.getPath().replace("\\", "/"), post.getTitle()));
			bld.append("\n");
			bld.append(String.format("<published>%1$s</published>", post.getPubDate() + ":00Z"));
			bld.append("\n");
			bld.append(String.format("<updated>%1$s</updated>", post.getUpdateDate() + ":00Z"));
			bld.append("\n");
			bld.append(String.format("<id>%1$s</id>", site + post.getPath().replace("\\", "/")));
			bld.append("\n");
			
			bld.append(String.format("<content type=\"html\" xml:base=\"%1$s\">", site + post.getPath().replace("\\", "/")));
			bld.append("\n");
			bld.append(FormattingMethods.escapeHTML(post.getContent()));
			bld.append("\n");
			bld.append("</content>");
			bld.append("\n");
			
			bld.append("<author>");
			bld.append("\n");
			bld.append("<name>James McKee</name>");
			bld.append("\n");
			bld.append("</author>");
			bld.append("\n");
			
			for (int j = 0; j < post.getTags().length; j++) {
				bld.append(String.format("<category term=\"%1$s\"/>", post.getTags()[j]));
				bld.append("\n");
			}
			
			bld.append("<summary type=\"html\">\n");
			bld.append(post.getDescription() + "\n");
			bld.append("</summary>\n");
			
			
			bld.append("</entry>\n");
		}

		
		String header = String.format(FileIO.readFile(BuildOptions.getInDir() + "\\header.xml"), lastUpdated + ":00Z");
		bld.append("</feed>");

		FileIO.writeToFile(header + bld.toString(), BuildOptions.getOutDir() + "\\feed.xml");
	}

	private static void iterateThroughFiles(File file) {
		if (BuildOptions.getIgnoredFiles().contains(file.getName()))
			return;

		if (file.isDirectory()) {
			File[] filesList = file.listFiles();

			for (File subfile : filesList) {
				iterateThroughFiles(subfile);
			}
		} else {
			String fileName = file.getName();

			if (fileName.endsWith(BuildOptions.getPreFormattedFileType().getFormat())) {
				try {
					Post post = PostParsing.fileToPost(file);
					allPosts.add(post);
					String formatted = formatPost(post);
					// System.out.println(formatted);
					System.out.println("Path: " + post.getPath());
					FileIO.writeToFile(formatted, BuildOptions.getOutDir() + post.getPath());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private static String formatPost(Post post) {
		String header = createHeader(post);

		String body = createBody(post);

		String footer = createFooter(post);

		return header + body + footer;
	}

	private static String createHeader(Post post) {
		return String.format(StaticVars.getCurrentTemplate().getHeaderTemplate(), post.getTitle(), post.getDescription());
	}
	private static String createFooter(Post post) {
		return String.format(StaticVars.getCurrentTemplate().getFooterTemplate()); // Could put recent posts in the footer or something
	}
	
	private static String createBody(Post post) {
		StringBuilder bld = new StringBuilder();

		// Post title
		bld.append(FormattingMethods.formatHeader(post.getTitle(), 1, "title"));
		// Post description
		bld.append(FormattingMethods.emphasiseText(post.getDescription()));
		bld.append(FormattingMethods.getNewLine());
		bld.append(FormattingMethods.makeSmallText(getPostedUpdatedLength(post)));
		bld.append(FormattingMethods.getNewLine());
		bld.append(FormattingMethods.makeSmallText(getPostTags(post)));

		// Horizontal rule before content
		bld.append(FormattingMethods.getHorizontalRule());

		bld.append(post.getContent());

		return bld.toString();
	}
	
	private static String getPostTags(Post post) {
		StringBuilder bld = new StringBuilder();

		String[] tags = post.getTags();
		if (tags.length > 0) {
			bld.append("Tags: ");

			for (int i = 0; i < tags.length; i++) {
				bld.append(FormattingMethods.buildLink(tags[i], "/blog/tags/" + tags[i], ""));

				if (i < tags.length - 1) {
					bld.append(" | ");
				}
			}
		}

		return bld.toString();
	}
	

	private static int wordCount(String string) {
		int count = 0;
		char[] ch = string.toCharArray();
		for (int i = 0; i < string.length(); i++) {
			if (((i > 0) && (ch[i] != ' ') && (ch[i - 1] == ' ')) || ((ch[0] != ' ') && (i == 0))) {
				count++;
			}
		}
		return count;
	}


	private static String getPostedUpdatedLength(Post post) {
		String published = "Published on " + FormattingMethods.formatDate(post.getPubDate());

		String updated = "";

		if (!post.getPubDate().equals(post.getUpdateDate())) {
			updated = ", updated on " + FormattingMethods.formatDate(post.getUpdateDate());
		}

		int words = wordCount(post.getContent());
		String wordCount;

		if (words == 1)
			wordCount = words + " word";
		else
			wordCount = words + " words";

		// int ttr = Math.max(words / 238, 1);
		String readTime = "";// ", ~" + ttr + " min. read";

		return published + updated + ". " + wordCount + readTime + ".";
	}
}
