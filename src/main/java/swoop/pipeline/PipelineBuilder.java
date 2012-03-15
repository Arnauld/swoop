package swoop.pipeline;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swoop.util.Context;
import swoop.util.New;

public class PipelineBuilder {
    private Logger logger = LoggerFactory.getLogger(PipelineBuilder.class);
    
    private List<Object> handlers = New.arrayList();
    private Context context;
    private PipelineExecutor executor = Pipelines.callerExecutor();
    
    public PipelineBuilder handler(Object handler) {
        handlers.add(handler);
        return this;
    }
    
    public PipelineBuilder context(Context context) {
        this.context = context;
        return this;
    }
    
    public PipelineBuilder executor(PipelineExecutor executor) {
        this.executor = executor;
        return this;
    }

    public Pipeline buildPipeline() {
        return new PipelineBasic(executor, context, buildHandlers());
    }
    
    public List<Handler> buildHandlers() {
        List<Handler> downstreams = New.arrayList();
        List<Handler> upstreams = New.arrayList();
        Handler target = null;
        
        for(Object o : handlers) {
            if(o instanceof PipelineDownstreamHandler)
                downstreams.add(Pipelines.wrap((PipelineDownstreamHandler)o));
            if(o instanceof PipelineUpstreamHandler)
                upstreams.add(Pipelines.wrap((PipelineUpstreamHandler)o));
            if(o instanceof PipelineTargetHandler) {
                if(target==null)
                    target = Pipelines.wrap((PipelineTargetHandler)o);
                else
                    throw new IllegalStateException("Multiple target found at least <" + target + "> and <" + o + ">");
            }
        }
        if(target!=null)
            downstreams.add(target);
        else
            logger.info("No target defined");
        
        Collections.reverse(upstreams);
        downstreams.addAll(upstreams);
        return downstreams;
    }
}
