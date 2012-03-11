package samples.quiz.infra;

import java.util.concurrent.atomic.AtomicInteger;

import fj.Effect;
import fj.data.Option;

public class Flow {

    private volatile boolean frozen;
    private AtomicInteger spawned;
    private Runnable joinCallback;
    
    public Flow() {
        this.spawned = new AtomicInteger();
        this.frozen = false;
    }
    
    public synchronized Effect<Option<Throwable>> addCallbackButOnError(final Effect<Throwable> errorCallback) {
        if(frozen)
            throw new IllegalStateException("Flow is frozen: cannot add once join has been invoked");
        spawned.incrementAndGet();
        return new Effect<Option<Throwable>>() {
            @Override
            public void e(Option<Throwable> opt) {
                if(opt.isSome())
                    errorCallback.e(opt.some());
                else
                    taskDone();
            }
        };
    }
    
    public synchronized void join(Runnable joinCallback) {
        this.frozen = true;
        this.joinCallback = joinCallback;
        if(spawned.get()==0)
            joinCallback.run();
    }

    protected void taskDone() {
        if(spawned.decrementAndGet()==0)
            joinCallback.run();
    }
    
    
}
