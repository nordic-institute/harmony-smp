package utils;

@SuppressWarnings("SpellCheckingInspection")
public class PROPERTIES {

	public static final String UI_BASE_URL = System.getProperty("UI_BASE_URL");
	public static final int SHORT_UI_TIMEOUT = 5;
	public static final int TIMEOUT = Integer.valueOf(System.getProperty("SHORT_TIMEOUT"));

	public static final String HEADLESS = "true";
	public static final String REPORTS_FOLDER = System.getProperty("reports.folder");
	public static final String TESTDATAFILE = System.getProperty("data.folder") + System.getProperty("testdata.file");


}
