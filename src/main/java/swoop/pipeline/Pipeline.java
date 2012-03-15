package swoop.pipeline;

import swoop.util.Context;
import swoop.util.HasDataParameters;

public interface Pipeline extends HasDataParameters, Context {
    
    /**
     * 
     */
    void invokeNext();
}
