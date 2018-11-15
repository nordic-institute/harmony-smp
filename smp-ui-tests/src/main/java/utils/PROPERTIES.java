package utils;

public class PROPERTIES {

	public static final String UI_BASE_URL = System.getProperty("UI_BASE_URL");
	public static final int TIMEOUT = Integer.valueOf(System.getProperty("SHORT_TIMEOUT"));
	public static final int LONG_WAIT = Integer.valueOf(System.getProperty("LONG_TIMEOUT"));
	public static final String REPORTS_FOLDER = System.getProperty("reports.folder");
	public static final String TESTDATAFILE = System.getProperty("data.folder") + System.getProperty("testdata.file");


}
