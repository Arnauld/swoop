package swoop.util;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultThreadFactory implements ThreadFactory {
    
    private Logger logger = LoggerFactory.getLogger(DefaultThreadFactory.class);
    
    private ThreadGroup group;
    private UncaughtExceptionHandler uncaughtExceptionHandler;
    private String name;
    private AtomicInteger idGen = new AtomicInteger();
    
    public DefaultThreadFactory(String name) {
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread (safeGetGroup(), r, name + "Thread" + idGen.incrementAndGet());
        thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
        return thread;
    }
    
    public void setGroup(ThreadGroup group) {
        this.group = group;
    }
    
    public void setUncaughtExceptionHandler(UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
    }
    
    protected synchronized ThreadGroup safeGetGroup() {
        if(group==null) {
            group = new ThreadGroup(name + "ThreadGroup") {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    logger.error("Uncaught exception on thread [" + t + "]", e);
                }
            };
        }
        return group;
    }
    
    protected synchronized UncaughtExceptionHandler safeGetUncaughtExceptionHandler() {
        if(uncaughtExceptionHandler==null)
            uncaughtExceptionHandler = new UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    logger.error("Uncaught exception on thread [" + t + "]", e);
                }
            };
        return uncaughtExceptionHandler;
    }

}
