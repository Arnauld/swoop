package samples.quiz.app;

import static swoop.Swoop.listener;
import static swoop.Swoop.setPort;
import static swoop.Swoop.stop;

import java.util.concurrent.CountDownLatch;

import samples.quiz.service.QuizServiceInMemory;
import swoop.it.support.SwoopServerCountDownOnceStartedListener;

public class QuizAppITest extends QuizAppITestDefinition {

    @Override
    protected QuizServiceInMemory startApplication() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        setPort(port);
        listener(new SwoopServerCountDownOnceStartedListener(latch));
        QuizServiceInMemory quizService = initApplication();
        latch.await();
        return quizService;
    }

    protected QuizServiceInMemory initApplication() throws Exception {
        return QuizApp.directPolicy();
    }

    protected void stopApplication() throws Exception {
        stop();
    }

}
