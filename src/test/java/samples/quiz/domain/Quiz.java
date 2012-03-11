package samples.quiz.domain;


public class Quiz {
    
    private String quizId;
    private String title;
    
    public Quiz() {
    }
    
    public Quiz(String quizId, String title) {
        this.quizId = quizId;
        this.title = title;
    }
    
    public String getTitle() {
        return title;
    }

    public String getId() {
        return quizId;
    }

}
