package swoop.pipeline;

import java.util.List;
import java.util.Map;

import swoop.util.Context;
import swoop.util.New;

public class PipelineBasic implements Pipeline {

    private Map<String,Object> data = New.hashMap();
    private Context delegateContext;
    private List<Handler> handlers;
    private PipelineExecutor pipelineExecutor;
    private int index;
    
    public PipelineBasic(PipelineExecutor pipelineExecutor, Context delegateContext, List<Handler> handlers) {
        super();
        this.pipelineExecutor = pipelineExecutor;
        this.delegateContext = delegateContext;
        this.handlers = handlers;
    }

    @Override
    public Object data(String key) {
        return data.get(key);
    }
    
    @Override
    public void data(String key, Object value) {
        data.put(key, value);
    }
    
    @Override
    public java.util.Set<String> dataKeys() {
        return data.keySet();
    }
    
    @Override
    public <T> T get(Class<T> type) {
        return delegateContext.get(type);
    }
    
    @Override
    public void invokeNext() {
        if(index<handlers.size())
            pipelineExecutor.execute(handlers.get(index++), this);
    }
}
