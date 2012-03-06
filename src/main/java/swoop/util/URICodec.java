package swoop.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import swoop.SwoopException;

public class URICodec {

    public static String encode(String c) {
        try {
            return URLEncoder.encode(c, "UTF8");
        } catch (UnsupportedEncodingException e) {
            throw new SwoopException("Damn it! no UTF8", e);
        }
    }

    public static String decode(String val) {
        try {
            return URLDecoder.decode(val, "UTF8");
        } catch (UnsupportedEncodingException e) {
            throw new SwoopException("Damn it! no UTF8", e);
        }
    }
}
