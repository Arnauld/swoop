package samples.quiz.app;

import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.testng.annotations.Test;

import samples.quiz.infra.Result;
import samples.quiz.service.QuizServiceInMemory;

public class QuizPipelineAppITest extends QuizAppITestDefinition {

    private QuizPipelineApp app;

    @Override
    protected QuizServiceInMemory startApplication() throws Exception {
        app = new QuizPipelineApp();
        app.port(port);
        app.asyncPolicy(Executors.newFixedThreadPool(4));
        app.initRoutes();
        app.server().awaitServerStarted();
        return app.getInMemoryService();
    }

    protected void stopApplication() throws Exception {
        app.server().shutdown();
    }

    @Test
    public void dummyForEclipseLauncher() {
        // otherwise eclipse launcher does not provided the Run as>... since
        // tests are defined in super class
    }
    
    @Test
    public void createTestDataUsingFlow() throws Exception {
        HttpResponse response = httpClient.execute(new HttpPost(baseUrl + "/api/create-flow-data"));
        Result result = readResultAndAssertIsOk(response);
        assertResultContainsValidQuizzes(result, NUMBER_CREATED_THROUGH_TEST_API);
    }
}
