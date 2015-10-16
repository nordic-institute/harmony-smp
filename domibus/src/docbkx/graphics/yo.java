package eu.domibus.submission;

import eu.domibus.submission.transformer.MessageRetrievalTransformer;
import eu.domibus.submission.transformer.MessageSubmissionTransformer;
import eu.domibus.submission.transformer.exception.TransformationException;
import eu.domibus.submission.validation.exception.ValidationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.concurrent.Future;

public class ExampleBackendConnector extends AbstractBackendConnector<MyObjectIn, MyObjectOut>
                                     implements BackendConnector<MyObjectIn, MyObjectOut>{
									 
}
