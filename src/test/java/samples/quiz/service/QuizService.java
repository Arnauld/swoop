package samples.quiz.service;

import samples.quiz.domain.Quiz;
import samples.quiz.domain.QuizCollection;
import samples.quiz.infra.Specification;
import fj.Effect;
import fj.data.Either;
import fj.data.Option;

/**
 * QuizService is an interface to make it easier to turn into an asynchronous implementation
 * using java proxy.
 * @see QuizServiceInMemory
 * @see Async#
 */
public interface QuizService {
    void find(final Specification<? super Quiz> spec, final Effect<Either<Throwable, QuizCollection>> callback);
    void remove(final Specification<? super Quiz> spec, final Effect<Option<Throwable>> callback);
    void create(final String title, final Effect<Either<Throwable, Quiz>> callback);
    void save(final Quiz quiz, final Effect<Option<Throwable>> callback);
}
