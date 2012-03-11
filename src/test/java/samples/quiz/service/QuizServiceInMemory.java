package samples.quiz.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samples.quiz.domain.Quiz;
import samples.quiz.domain.QuizCollection;
import samples.quiz.infra.Specification;
import swoop.util.New;
import fj.Effect;
import fj.data.Either;
import fj.data.Option;

public class QuizServiceInMemory implements QuizService {
    
    private Logger logger = LoggerFactory.getLogger(QuizServiceInMemory.class);
    private Map<String, Quiz> quizzes = New.concurrentHashMap();

    public QuizServiceInMemory() {
    }

    public void find(final Specification<? super Quiz> spec, final Effect<Either<Throwable, QuizCollection>> callback) {
        logger.info("Searching within #{} documents", quizzes.size());
        try {
            List<Quiz> found = New.arrayList();
            for (Quiz quiz : quizzes.values()) {
                if (spec.isSatisfiedBy(quiz)) {
                    found.add(quiz);
                }
            }
            logger.info("Found #{} documents", found.size());
            Either<Throwable, QuizCollection> res = Either.right(new QuizCollection(found));
            callback.e(res);
        } catch (Exception e) {
            Either<Throwable, QuizCollection> res = Either.left((Throwable) e);
            callback.e(res);
        }
    }

    public void remove(final Specification<? super Quiz> spec, final Effect<Option<Throwable>> callback) {
        logger.info("Deleting within #{} documents", quizzes.size());
        try {
            List<String> idsToRemove = New.arrayList();
            for (Quiz quiz : quizzes.values()) {
                if (spec.isSatisfiedBy(quiz)) {
                    idsToRemove.add(quiz.getId());
                }
            }

            for (String idToRemove : idsToRemove) {
                logger.info("Deleting quiz #{}", idToRemove);
                quizzes.remove(idToRemove);
            }

            Option<Throwable> res = Option.none();
            callback.e(res);
        } catch (Exception e) {
            Option<Throwable> res = Option.some((Throwable) e);
            callback.e(res);
        }
    }

    public void create(final String title, final Effect<Either<Throwable, Quiz>> callback) {
        logger.info("Creating quiz with title <{}>", title);
        try {
            Quiz quiz = new Quiz(UUID.randomUUID().toString(), title);
            Either<Throwable, Quiz> res = Either.right(quiz);
            callback.e(res);
        } catch (Exception e) {
            Either<Throwable, Quiz> res = Either.left((Throwable) e);
            callback.e(res);
        }
    }

    public void save(final Quiz quiz, final Effect<Option<Throwable>> callback) {
        logger.info("Saving quiz #{}", quiz.getId());
        try {
            quizzes.put(quiz.getId(), quiz);

            Option<Throwable> res = Option.none();
            callback.e(res);
        } catch (Exception e) {
            Option<Throwable> res = Option.some((Throwable) e);
            callback.e(res);
        }
    }

    public void clearContent() {
        quizzes.clear();
        quizzes = New.concurrentHashMap();
    }
    
    public void clearContent(int initialCapacity) {
        quizzes.clear();
        quizzes = New.concurrentHashMap(initialCapacity);
    }
}
