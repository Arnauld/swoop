package swoop;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class DefaultResponseProcessor implements ResponseProcessor {
    @Override
    public void process(Object model, Request request, Response response) {
        // TODO content negociation
        if (model instanceof byte[])
            response.body(model);
        else
            response.body(String.valueOf(model));

    }
}
