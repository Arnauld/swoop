package swoop;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public interface ResponseProcessor {
    void process(Object model, Request request, Response response);
}
