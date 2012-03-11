package samples.quiz.service;

import samples.quiz.domain.Quiz;
import fj.Effect;
import fj.data.Option;

public class QuizServiceFuncs {
    public static Effect<Quiz> saveE(final QuizService service, final Effect<Option<Throwable>> onSave) {
        return new Effect<Quiz>() {
            @Override
            public void e(Quiz quiz) {
                service.save(quiz, onSave);
            }
        };
    }
}
