package swoop.support;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swoop.Filter;
import swoop.Request;
import swoop.Response;
import swoop.path.Verb;
import swoop.route.RouteChain;
import swoop.util.CircularBuffer;

public class PerfFilter extends Filter {
    
    public static class Stats {
        public AtomicLong invokeCount = new AtomicLong();
        public AtomicLong maxProcessingTime = new AtomicLong();
        public AtomicLong minProcessingTime = new AtomicLong(Long.MAX_VALUE);
        public AtomicLong totalProcessingTime = new AtomicLong();
        public CircularBuffer<Long> latestProcessingTimes;
        
        public Stats() {
            this.latestProcessingTimes = new CircularBuffer<Long>(32);
        }
        
        public Stats(Stats stats) {
            this.invokeCount.set(stats.invokeCount.get());
            this.maxProcessingTime.set(stats.maxProcessingTime.get());
            this.minProcessingTime.set(stats.minProcessingTime.get());
            this.totalProcessingTime.set(stats.totalProcessingTime.get());
            this.latestProcessingTimes = stats.latestProcessingTimes.copy();
        }
        
        public void process(long elapsedTime) {
            invokeCount.incrementAndGet();
            totalProcessingTime.addAndGet(elapsedTime);
            while(minProcessingTime.get()>elapsedTime)
                minProcessingTime.set(elapsedTime);
            while(maxProcessingTime.get()<elapsedTime)
                maxProcessingTime.set(elapsedTime);
            latestProcessingTimes.add(elapsedTime);
        }
        
        public long getInvokeCount() {
            return invokeCount.get();
        }

        public long getMaxProcessingTime() {
            return maxProcessingTime.get();
        }

        public long getMinProcessingTime() {
            return minProcessingTime.get();
        }

        public long getTotalProcessingTime() {
            return totalProcessingTime.get();
        }

        public CircularBuffer<Long> getLatestProcessingTimes() {
            return latestProcessingTimes;
        }

        public Stats copy () {
            return new Stats(this);
        }
    }
    
    private Logger logger = LoggerFactory.getLogger(PerfFilter.class);
    private Stats stats = new Stats();

    public PerfFilter() {
        super();
    }

    public PerfFilter(String path) {
        super(path);
    }

    public PerfFilter(Verb applyOn, String path) {
        super(applyOn, path);
    }

    @Override
    public void handle(Request request, Response response, RouteChain routeChain) {
        long t0 = System.currentTimeMillis();
        try  {
            routeChain.invokeNext();
        }
        finally {
            long t1 = System.currentTimeMillis();
            stats.process(t1-t0);
            logger.info("Request " + request.logInfo() + " executed in " + (t1-t0) + "ms");
        }
    }
    
    public Stats grab() {
        return stats.copy();
    }
}
