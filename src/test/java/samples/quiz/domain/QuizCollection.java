package samples.quiz.domain;

import java.util.List;


public class QuizCollection {

    private List<Quiz> elements;
    public QuizCollection() {
    }
    public QuizCollection(List<Quiz> elements) {
        this.elements = elements;
    }
    public List<Quiz> getElements() {
        return elements;
    }

}
