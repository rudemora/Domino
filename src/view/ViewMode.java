package view;

public enum ViewMode {
	CONSOLE, GUI;
	
	public static ViewMode fromString(String s) {
		return "console".equals(s.toLowerCase()) ? CONSOLE : GUI;
	}
}