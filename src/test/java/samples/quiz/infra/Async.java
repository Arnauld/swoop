package samples.quiz.infra;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swoop.util.Objects;

public class Async {
    private static Logger logger = LoggerFactory.getLogger(Async.class);

    @SuppressWarnings("unchecked")
    public static <T> T asyncProxy(Class<T> type, final T impl, final ExecutorService executor) {
        return (T)Proxy.newProxyInstance(Async.class.getClassLoader(), new Class<?>[]{type}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
                logger.debug("Invoking proxy method <{}> with arguments <{}> returnin type <{}>", //
                        Objects.o(method, Arrays.toString(args), method.getReturnType()));
                Future<Object> future = executor.submit(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        return method.invoke(impl, args);
                    }
                });
                Class<?> returnType = method.getReturnType();
                if(returnType==null || returnType==Void.class)
                    return null;
                else
                    return future.get();
            }
        });
    }
}
