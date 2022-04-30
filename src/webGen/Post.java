package webGen;

import java.time.LocalDateTime;

public class Post {
	private String path;
	private String title;
	private String description;
	private LocalDateTime publishDate;
	private LocalDateTime updateDate;
	private String[] tags;
	private String content;
	
	public Post(String path, String title, String description, LocalDateTime publishDate, LocalDateTime updateDate, String[] tags,
			String content) {
		this.path = path;
		this.title = title;
		this.description = description;
		this.publishDate = publishDate;
		if (LocalDateTime.MIN.equals(updateDate)) this.updateDate = publishDate;
		else this.updateDate = updateDate;
		this.tags = tags;
		this.content = content;
	}
	
	public String getPath() {
		return path;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public LocalDateTime getPubDate() {
		return publishDate;
	}

	public LocalDateTime getUpdateDate() {
		return updateDate;
	}

	public String[] getTags() {
		return tags;
	}

	public String getContent() {
		return content;
	}
}
