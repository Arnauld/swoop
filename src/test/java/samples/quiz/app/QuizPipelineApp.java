package samples.quiz.app;

import static samples.quiz.service.QuizServiceFuncs.saveE;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webbitserver.HttpResponse;

import samples.Json;
import samples.quiz.domain.Quiz;
import samples.quiz.domain.QuizCollection;
import samples.quiz.infra.Async;
import samples.quiz.infra.Flow;
import samples.quiz.infra.Result;
import samples.quiz.service.QuizService;
import samples.quiz.service.QuizServiceInMemory;
import samples.quiz.service.QuizSpecification;
import swoop.pipeline.Pipeline;
import swoop.pipeline.PipelineTargetHandler;
import swoop.pipeline.Swoop2Builder;
import swoop.route.RouteParameters;
import fj.Effect;
import fj.data.Either;
import fj.data.Option;

public class QuizPipelineApp extends Swoop2Builder {
    private static Logger console = LoggerFactory.getLogger(QuizApp.class);

    public static void main(String[] args) {
        QuizPipelineApp app = new QuizPipelineApp();
        app.asyncPolicy(Executors.newFixedThreadPool(4));
        app.initRoutes();
    }

    private QuizServiceInMemory memoryService;
    private QuizService service;

    public void asyncPolicy(ExecutorService executor) {
        memoryService = new QuizServiceInMemory();
        service = makeItAsync(memoryService, executor);
    }

    public void directPolicy() {
        memoryService = new QuizServiceInMemory();
        service = memoryService;
    }
    
    public QuizServiceInMemory getInMemoryService() {
        return memoryService;
    }

    public void initRoutes() {

        // @formatter:off
        /*
              app.get "/api/quizzes",
                (req, res) ->
                  Quiz.find {}, (err, docs) ->
                    throw err if err
                    sendJSON res, docs
         */
        //@formatter:on
        get("/api/quizzes", new PipelineTargetHandler() {
            @Override
            public void handleTarget(final Pipeline pipeline) {
                console.info("get: /api/quizzes");
                service.find(QuizSpecification.all(), //
                        sendError(pipeline).orContinueWith(sendOkTo(pipeline, QuizCollection.class)));
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
        delete("/api/quizzes/:id", new PipelineTargetHandler() {
            @Override
            public void handleTarget(final Pipeline pipeline) {
                String quizId = pipeline.get(RouteParameters.class).routeParam("id");
                console.info("Deleting quiz {}", quizId);
                // very dangerous! Need to add some permissions checking
                // e.g. through a filter declaration that applyOn 'delete'
                service.remove(QuizSpecification.byId(quizId), //
                        sendError(pipeline).orContinueWith(sendOkTo(pipeline)));
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
        post("/api/create-test-data", new PipelineTargetHandler() {
            @Override
            public void handleTarget(final Pipeline pipeline) {
                console.info("post: /api/create-test-data");

                final int COUNT = 100;
                final AtomicInteger remaining = new AtomicInteger(COUNT);

                Runnable keepCount = new Runnable() {
                    @Override
                    public void run() {
                        if (remaining.decrementAndGet() == 0) {
                            service.find(QuizSpecification.all(), //
                                    sendError(pipeline).orContinueWith(sendOkTo(pipeline, QuizCollection.class)));
                        }
                    }
                };
                Effect<Quiz> saveAndDecrement = invokeSave(service).sendError(pipeline).orContinueWith(keepCount);
                for (int i = 0; i < COUNT; i++)
                    service.create("Test Quizz #" + i, sendError(pipeline).orContinueWith(saveAndDecrement));
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
        post("/api/create-flow-data", new PipelineTargetHandler() {
            @Override
            public void handleTarget(final Pipeline pipeline) {
                console.info("post: /api/create-flow-data");

                Flow flow = new Flow();
                final int COUNT = 100;
                for (int i = 0; i < COUNT; i++) {
                    // save, and on error send err to response
                    Effect<Quiz> save = saveE(service, flow.addCallbackButOnError(sendErrTo(pipeline)));
                    service.create("Test Quizz #" + i, sendError(pipeline).orContinueWith(save));
                }

                flow.join(new Runnable() {
                    @Override
                    public void run() {
                        service.find(QuizSpecification.all(), //
                                sendError(pipeline).orContinueWith(sendOkTo(pipeline, QuizCollection.class)));
                    }
                });
            }

        });

    }

    //
    //
    //
    //

    protected static InvokeChain<Quiz> invokeSave(final QuizService service) {
        return new InvokeChain<Quiz>() {
            @Override
            public EffectBasedErrorOrContinueChain<Quiz> sendError(Pipeline pipeline) {
                return new EffectBasedErrorOrContinueChain<Quiz>(sendErrTo(pipeline)) {
                    @Override
                    protected <E> Effect<Quiz> funE(final Effect<Either<Throwable, E>> callback) {
                        return new Effect<Quiz>() {
                            @Override
                            public void e(Quiz quiz) {
                                service.save(quiz, asOptionEffect(callback, null));
                            }
                        };
                    }

                    @Override
                    protected Effect<Quiz> funO(final Effect<Option<Throwable>> callback) {
                        return new Effect<Quiz>() {
                            @Override
                            public void e(Quiz quiz) {
                                service.save(quiz, callback);
                            }
                        };
                    }
                };
            }
        };
    }

    protected static <E> Effect<Option<Throwable>> asOptionEffect(final Effect<Either<Throwable, E>> callback,
            final E rightValue) {
        return new Effect<Option<Throwable>>() {
            @Override
            public void e(Option<Throwable> a) {
                if (a.isSome())
                    callback.e(Either.<Throwable, E> left(a.some()));
                else
                    callback.e(Either.<Throwable, E> right(rightValue));

            }
        };
    }

    public interface InvokeChain<T> {
        public EffectBasedErrorOrContinueChain<T> sendError(Pipeline pipeline);
    }

    public abstract static class EffectBasedErrorOrContinueChain<T> {
        private final Effect<Throwable> onError;

        public EffectBasedErrorOrContinueChain(Effect<Throwable> onError) {
            this.onError = onError;
        }

        protected abstract <E> Effect<T> funE(Effect<Either<Throwable, E>> callback);

        protected abstract Effect<T> funO(Effect<Option<Throwable>> callback);

        public <E> Effect<T> orContinueWith(final Effect<E> onSuccess) {
            return funE(eitherEffect(onError, onSuccess));
        }

        public Effect<T> orContinueWith(Runnable onSuccess) {
            return funO(optionEffect(onError, onSuccess));
        }
    }

    protected static ErrorOrContinueChain sendError(Pipeline pipeline) {
        return new ErrorOrContinueChain(sendErrTo(pipeline));
    }

    public static class ErrorOrContinueChain {
        private final Effect<Throwable> onError;

        public ErrorOrContinueChain(Effect<Throwable> onError) {
            this.onError = onError;
        }

        public <T> Effect<Either<Throwable, T>> orContinueWith(final Effect<T> onSuccess) {
            return eitherEffect(onError, onSuccess);
        }

        public <T> Effect<Option<Throwable>> orContinueWith(final Runnable onSuccess) {
            return optionEffect(onError, onSuccess);
        }
    }

    public static <R, L> Effect<Either<L, R>> eitherEffect(final Effect<L> onLeft, final Effect<R> onRight) {
        return new Effect<Either<L, R>>() {
            @Override
            public void e(Either<L, R> a) {
                if (a.isLeft())
                    onLeft.e(a.left().value());
                else
                    onRight.e(a.right().value());
            }
        };
    }

    public static <E> Effect<Option<E>> optionEffect(final Effect<E> onSome, final Runnable otherwise) {
        return new Effect<Option<E>>() {
            @Override
            public void e(Option<E> a) {
                if (a.isSome())
                    onSome.e(a.some());
                else
                    otherwise.run();
            }
        };
    }

    public static <T> Effect<T> sendOkTo(final Pipeline pipeline, Class<T> type) {
        return new Effect<T>() {
            @Override
            public void e(T a) {
                sendJsonAndInvokeNext(pipeline, Result.ok(null, a));
            }
        };
    }

    public static Runnable sendOkTo(final Pipeline pipeline) {
        return new Runnable() {
            @Override
            public void run() {
                sendJsonAndInvokeNext(pipeline, Result.ok());
            }
        };
    }

    public static Effect<Throwable> sendErrTo(final Pipeline pipeline) {
        return new Effect<Throwable>() {
            @Override
            public void e(Throwable thr) {
                pipeline.get(HttpResponse.class).status(500); // internal server error
                sendJsonAndInvokeNext(pipeline, Result.err(thr));
            }
        };
    }

    public static void sendJsonAndInvokeNext(Pipeline pipeline, Object what) {
        sendJson(pipeline, what);
        pipeline.invokeNext();
    }
    
    public static void sendJson(Pipeline pipeline, Object what) {
        HttpResponse httpResponse = pipeline.get(HttpResponse.class);
        httpResponse.header("Content-type", "text/json");
        httpResponse.content(Json.toJson(what, true));
    }
    
    //
    //
    //
    //

    private static QuizService makeItAsync(QuizService service, ExecutorService executor) {
        return Async.asyncProxy(QuizService.class, service, executor);
    }

}
