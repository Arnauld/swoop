package samples.quiz.service;

import samples.quiz.domain.Quiz;
import samples.quiz.infra.Specification;

public class QuizSpecification {

    public static Specification<Quiz> all() {
        return new Specification<Quiz>() {
            @Override
            public boolean isSatisfiedBy(Quiz elem) {
                return true;
            }
        };
    }

    public static Specification<? super Quiz> byId(final String quizId) {
        return new Specification<Quiz>() {
            @Override
            public boolean isSatisfiedBy(Quiz quiz) {
                return quizId != null && quizId.equals(quiz.getId());
            }
        };
    }
}
