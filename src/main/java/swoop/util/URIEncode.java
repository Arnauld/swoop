package swoop.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import swoop.SwoopException;

public class URIEncode {

    public static String encode(String c) {
        try {
            return URLEncoder.encode(c, "UTF8");
        } catch (UnsupportedEncodingException e) {
            throw new SwoopException("Damn it! no UTF8", e);
        }
    }
}
