package eu.europa.ec.edelivery.smp.auth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class URLCsrfMatcherTest {

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection cookieWriterTestParameters() {
        return asList(new Object[][]{
                {"/test/", false, asList("/.*"), null},
                {"/ui/resource", true, asList("/!(ui/).*"), null},
                {"/test/resource", false, asList("^/(?!ui/).*"), null},
                {"/ui/resource", true, asList("^/(?!ui/).*"), null},

        });
    }

    @Parameterized.Parameter(0)
    public String patInfo;

    @Parameterized.Parameter(1)
    public boolean notMatchResult;

    @Parameterized.Parameter(2)
    public List<String> regExp;

    @Parameterized.Parameter(3)
    public List<HttpMethod> httpMethods;


    @Test
    public void matches() {
        URLCsrfIgnoreMatcher testInstance = new URLCsrfIgnoreMatcher(regExp, httpMethods);

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn(patInfo).when(request).getRequestURI();
        Mockito.doReturn("").when(request).getServletPath();

        boolean result = testInstance.matches(request);
        assertEquals(notMatchResult, result);


    }
}
