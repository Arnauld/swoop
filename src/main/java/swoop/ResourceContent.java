package swoop;

import java.io.File;

import swoop.route.Route;

public abstract class ResourceContent extends Route {
    
    public static final String RESOURCE_PATH = "__resource.path";
    public static final String RESOURCE_DIR = "__resource.dir";

    private File baseDir;
    protected ResourceContent(String path, File baseDir) {
        super(path);
        this.baseDir = baseDir;
    }
    
    public File getBaseDir() {
        return baseDir;
    }
    
    @Override
    public final boolean isFilter() {
        return false;
    }
    
    @Override
    public final void handle(Request request, Response response, RouteChain routeChain) {
        File dir = (File)request.data(RESOURCE_DIR);
        if(dir==null)
            dir = getBaseDir();
        String path = (String)request.data(RESOURCE_PATH);
        if(path==null)
            path = request.uri();
        ResourceHandler resourceHandler = routeChain.context().get(ResourceHandler.class);
        resourceHandler.writeResource(path, dir, routeChain.context());
    }

}
