package samples.quiz.infra;

public interface Specification<T> {
    boolean isSatisfiedBy(T elem);
}
