package swoop.pipeline;

import static swoop.pipeline.HandlerAdapters.adjustRouteParametersPreProcess;
import static swoop.pipeline.HandlerAdapters.wrap;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swoop.util.New;

public class PipelineBuilder {
    private Logger logger = LoggerFactory.getLogger(PipelineBuilder.class);
    
    private List<HandlerEntry> entries = New.arrayList();
    private PipelineExecutor executor = Pipelines.callerExecutor();

    private boolean ensureFlushIsCalled;
    
    public PipelineBuilder handlers(HandlerEntry... entries) {
        this.entries.addAll(Arrays.asList(entries));
        return this;
    }
    
    public PipelineBuilder handlers(Iterable<HandlerEntry> entries) {
        for(HandlerEntry entry : entries)
            this.entries.add(entry);
        return this;
    }
    
    public PipelineBuilder handler(HandlerEntry entry) {
        this.entries.add(entry);
        return this;
    }
    
    public PipelineBuilder executor(PipelineExecutor executor) {
        this.executor = executor;
        return this;
    }

    public Pipeline buildPipeline() {
        return new PipelineBasic(executor, buildHandlers());
    }
    
    public List<HandlerAdapter> buildHandlers() {
        List<HandlerAdapter> downstreams = New.arrayList();
        List<HandlerAdapter> upstreams = New.arrayList();
        HandlerAdapter target = null;
        
        for(HandlerEntry entry : entries) {
            Handler handler = entry.getHandler();
            if(handler instanceof PipelineDownstreamHandler)
                downstreams.add(adapt(entry, (PipelineDownstreamHandler)handler));
            if(handler instanceof PipelineUpstreamHandler)
                upstreams.add(adapt(entry, (PipelineUpstreamHandler)handler));
            if(handler instanceof PipelineTargetHandler) {
                if(target==null)
                    target = adapt(entry, (PipelineTargetHandler)handler);
                else
                    throw new IllegalStateException("Multiple target found at least <" + target + "> and <" + handler + ">");
            }
        }
        if(target!=null)
            downstreams.add(target);
        else
            logger.info("No target defined");
        
        Collections.reverse(upstreams);
        downstreams.addAll(upstreams);
        if(ensureFlushIsCalled) {
            downstreams.add(new HandlerAdapter() {
                @Override
                public void handle(Pipeline pipeline) {
                    pipeline.get(Flusher.class).flush(pipeline);
                }
            });
        }
        return downstreams;
    }
    
    protected HandlerAdapter adapt(HandlerEntry entry, PipelineTargetHandler handler) {
        return adjustRouteParametersPreProcess(entry, wrap(handler));
    }

    protected HandlerAdapter adapt(HandlerEntry entry, PipelineUpstreamHandler handler) {
        return adjustRouteParametersPreProcess(entry, wrap(handler));
    }

    protected HandlerAdapter adapt(HandlerEntry entry, PipelineDownstreamHandler handler) {
        return adjustRouteParametersPreProcess(entry, wrap(handler));
    }

    public PipelineBuilder ensureFlushIsCalled() {
        this.ensureFlushIsCalled = true;
        return this;
    }
}
