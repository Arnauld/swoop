package samples.quiz.app;

import static samples.quiz.infra.Funcs.continueWith;
import static samples.quiz.infra.Funcs.sendErrTo;
import static samples.quiz.infra.Funcs.sendOkTo;
import static samples.quiz.service.QuizServiceFuncs.saveE;
import static swoop.Swoop.delete;
import static swoop.Swoop.get;
import static swoop.Swoop.post;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samples.quiz.domain.Quiz;
import samples.quiz.domain.QuizCollection;
import samples.quiz.infra.Async;
import samples.quiz.infra.Flow;
import samples.quiz.service.QuizService;
import samples.quiz.service.QuizServiceInMemory;
import samples.quiz.service.QuizSpecification;
import swoop.Action;
import swoop.Request;
import swoop.Response;
import fj.Effect;

public class QuizApp {

    private static Logger console = LoggerFactory.getLogger(QuizApp.class);

    public static void main(String[] args) {
        asyncPolicy(Executors.newFixedThreadPool(4));
    }
    
    public static QuizServiceInMemory asyncPolicy(ExecutorService executor) {
        QuizServiceInMemory service = new QuizServiceInMemory();
        initRoutes(makeItAsync(service, executor));
        return service;
    }
    
    public static QuizServiceInMemory directPolicy() {
        QuizServiceInMemory service = new QuizServiceInMemory();
        initRoutes(service);
        return service;
    }

    public static void initRoutes(final QuizService service) {

        // @formatter:off
        /*
              app.get "/api/quizzes",
                (req, res) ->
                  Quiz.find {}, (err, docs) ->
                    throw err if err
                    sendJSON res, docs
         */
        //@formatter:on
        get(new Action("/api/quizzes") {
            @Override
            public void handle(Request request, final Response response) {
                console.info("get: /api/quizzes");
                service.find(QuizSpecification.all(), //
                        continueWith(response, sendOkTo(response, QuizCollection.class)));
            }
        });

        // @formatter:off
        /*
              app.delete "/api/quizzes/:id",
                (req, res) ->
                  console.log "Deleting quiz #{req.params.id}"
                  # very dangerous! Need to add some permissions checking
                  Quiz.remove { _id: req.params.id }, (err) ->
                    throw err if err
                    sendJSON res, { result: "ok" }
            */
        //@formatter:on
        delete(new Action("/api/quizzes/:id") {
            @Override
            public void handle(Request request, Response response) {
                String quizId = request.routeParam("id");
                console.info("Deleting quiz {}", quizId);
                // very dangerous! Need to add some permissions checking
                // e.g. through a filter declaration that applyOn 'delete'
                service.remove(QuizSpecification.byId(quizId), //
                        continueWith(response, sendOkTo(response)));
            }
        });

        // @formatter:off
        /*
              app.get "/api/create-test-data",
                (req, res) ->
                  remaining = 100
            
                  keepCount = (err) ->
                    throw err if err
                    remaining--
            
                    if (remaining == 0)
                      Quiz.find {}, (err, docs) ->
                        throw err if err
                        sendJSON res, docs
            
                  for i in [1..remaining]
                    new Quiz(title: "Test Quiz \# #{i}").save keepCount
         */
        //@formatter:on
        post(new Action("/api/create-test-data") {
            @Override
            public void handle(Request request, final Response response) {
                console.info("post: /api/create-test-data");

                final int COUNT = 100;
                final AtomicInteger remaining = new AtomicInteger(COUNT);

                Runnable keepCount = new Runnable() {
                    @Override
                    public void run() {
                        if (remaining.decrementAndGet() == 0) {
                            service.find(QuizSpecification.all(), //
                                    continueWith(response, sendOkTo(response, QuizCollection.class)));
                        }
                    }
                };
                Effect<Quiz> saveAndDecrement = invokeSave(service, response, keepCount);
                for (int i = 0; i < COUNT; i++)
                    service.create("Test Quizz #" + i, continueWith(response, saveAndDecrement));
            }
        });
        
        // @formatter:off
        /*
                app.get "/api/create-test-data",
                    (req, res) ->
                
                      flow = new Flow
                      for i in [1..100]
                        quiz = new Quiz
                          title: "Test Quiz \# #{i}"
                          location: "Undisclosed"
                
                        quiz.save flow.add (err) ->
                          throw err if err
                
                      flow.join ->
                        Quiz.find {}, (err, docs) ->
                          throw err if err
                          sendJSON res, docs
        
        */
        // @formatter:on
        post(new Action("/api/create-flow-data") {
            @Override
            public void handle(Request request, final Response response) {
                console.info("post: /api/create-flow-data");

                Flow flow = new Flow ();
                final int COUNT = 100;
                for (int i = 0; i < COUNT; i++) {
                    // save, and on error send err to response
                    Effect<Quiz> save = saveE(service, flow.addCallbackButOnError(sendErrTo(response)));
                    service.create("Test Quizz #" + i, continueWith(response, save));
                }
                
                flow.join(new Runnable() {
                    @Override
                    public void run() {
                    service.find(QuizSpecification.all(), //
                            continueWith(response, sendOkTo(response, QuizCollection.class)));
                    }
                });
            }

        });
        
        
    }

    private static QuizService makeItAsync(QuizService service, ExecutorService executor) {
        return Async.asyncProxy(QuizService.class, service, executor);
    }

    protected static Effect<Quiz> invokeSave(final QuizService service, final Response response, final Runnable next) {
        return new Effect<Quiz>() {
            @Override
            public void e(Quiz quiz) {
                service.save(quiz, continueWith(response, next));
            }
        };
    }

}
