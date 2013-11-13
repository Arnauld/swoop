package swoop.util;

import org.apache.http.entity.ContentType;

import java.nio.charset.Charset;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class ContentTypes {
    public static final Charset UTF8 = Charset.forName("utf8");
    //
    public static final String JSON = "application/json";
    public static final String TEXT_PLAIN = "text/plain";
    public static final String TEXT_HTML = "text/html";
    public static final String TEXT_XML = "text/xml";

    public static String create(String mimeType, Charset charset) {
        return ContentType.create(mimeType, charset).toString();
    }

    public static String json() {
        return ContentType.create(JSON, UTF8).toString();
    }
}
