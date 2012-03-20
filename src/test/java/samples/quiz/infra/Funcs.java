package samples.quiz.infra;

import samples.Json;
import swoop.Response;
import fj.Effect;
import fj.data.Either;
import fj.data.Option;

public class Funcs {
    public static <T> Effect<Either<Throwable, T>> sendErrorOrContinueWith(final Response response, final Effect<T> onSucess) {
        return new Effect<Either<Throwable, T>>() {
            @Override
            public void e(Either<Throwable, T> res) {
                if (res.isLeft()) {
                    response.status(500); // internal server error
                    sendJson(response, Result.err(res.left().value()));
                } else {
                    onSucess.e(res.right().value());
                }
            }
        };
    }

    public static Effect<Option<Throwable>> sendErrorOrContinueWith(final Response response, final Runnable next) {
        return new Effect<Option<Throwable>>() {
            @Override
            public void e(Option<Throwable> res) {
                if (res.isSome()) {
                    response.status(500); // internal server error
                    sendJson(response, Result.err(res.some()));
                } else {
                    next.run();
                }
            }
        };
    }
    
    public static Effect<Throwable> sendErrTo(final Response response) {
        return  new Effect<Throwable>()  {
            @Override
            public void e(Throwable thr) {
                response.status(500); // internal server error
                sendJson(response, Result.err(thr));
            }
        };
    }

    public static Runnable sendOkTo(final Response response) {
        return new Runnable() {
            @Override
            public void run() {
                sendJson(response, Result.ok());
            }
        };
    }
    
    public static <T> Effect<T>  sendOkTo(final Response response, Class<T> type) {
        return new Effect<T>() {
            @Override
            public void e(T a) {
                sendJson(response, Result.ok(null, a));
            }
        };
    }

    public static void sendJson(Response response, Object what) {
        response.contentType("text/json");
        response.body(Json.toJson(what, true));
    }

}
