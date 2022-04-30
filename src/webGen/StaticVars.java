package webGen;

public class StaticVars {
	private StaticVars() {}
	private static PostTemplate currentTemplate = new PostTemplate();
	
	public static PostTemplate getCurrentTemplate() {
		return currentTemplate;
	}
	
	public static void setCurrentTemplate(PostTemplate _currentTemplate) {
		currentTemplate = _currentTemplate;
	}
}
