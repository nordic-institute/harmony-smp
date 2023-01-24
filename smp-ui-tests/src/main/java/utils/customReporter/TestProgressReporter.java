package utils.customReporter;

import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestProgressReporter  implements ITestListener {


	static int test_count = 0;
	static int passed_count = 0;
	static int failed_count = 0;
	static int skipped_count = 0;
	static int total_test_count = 0;
	Logger log = Logger.getLogger(this.getClass());

	@Override
	public void onStart(ITestContext context) {
		total_test_count = context.getSuite().getAllMethods().size();
		log.info("Tests methods to run - " + total_test_count);
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		test_count++;
		passed_count++;
		logTestCounts();
	}

	@Override
	public void onTestFailure(ITestResult result) {
		test_count++;
		failed_count++;
		logTestCounts();
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		test_count++;
		skipped_count++;
		logTestCounts();
	}

	private void logTestCounts() {
		log.info(String.format("-------- Passed - %s --------", passed_count));
		log.info(String.format("-------- Failed - %s --------", failed_count));
		log.info(String.format("-------- Skipped - %s --------", skipped_count));
		log.info(String.format("-------- Ran %s tests out of %s --------", test_count, total_test_count));
	}


}
