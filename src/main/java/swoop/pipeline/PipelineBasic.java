package swoop.pipeline;

import java.util.List;

import swoop.util.ContextBasic;

public class PipelineBasic implements Pipeline {

    private ContextBasic context;
    private List<HandlerAdapter> handlers;
    private PipelineExecutor pipelineExecutor;
    private int index;
    
    public PipelineBasic(PipelineExecutor pipelineExecutor, List<HandlerAdapter> handlers) {
        super();
        this.pipelineExecutor = pipelineExecutor;
        this.handlers = handlers;
        this.context = new ContextBasic();
    }
    
    @Override
    public void execute(Runnable runnable) {
        pipelineExecutor.execute(runnable);
    }

    @Override
    public <T> T get(Class<T> type) {
        return context.get(type);
    }
    
    @Override
    public <T> Pipeline with(Class<T> type, T value) {
        context.register(type, value);
        return this;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> Pipeline with(T value) {
        return with((Class<T>)value.getClass(), value);
    }
    
    @Override
    public void invokeNext() {
        if(index<handlers.size())
            pipelineExecutor.execute(handlers.get(index++), this);
    }
}
