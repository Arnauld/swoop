package swoop.path;

import static swoop.util.Gsub.gsub;
import static swoop.util.Objects.o;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webbitserver.helpers.Hex;

import swoop.SwoopException;
import swoop.util.Gsub.Replacement;
import swoop.util.URICodec;

public class PathMatcherSinatraCompiler implements PathMatcherCompiler {

    private static Logger logger = LoggerFactory.getLogger(PathMatcherSinatraCompiler.class);

    private static final Pattern toEncodePattern = Pattern.compile("[^\\?\\%\\\\\\/\\:\\*\\w]");
    private static final Pattern paramsPattern = Pattern.compile("(:(\\w+)|\\*)");

    // def encoded(char)
    // enc = URI.encode(char)
    // enc = "(?:#{Regexp.escape enc}|#{URI.encode char, /./})" if enc == char
    // enc = "(?:#{enc}|#{encoded('+')})" if char == " "
    // enc
    // end

    public static String encoded(char c) throws UnsupportedEncodingException {
        String str = String.valueOf(c);
        String enc;
        switch (c) {
            case '$':
            case '#':
            case '!':
            case '(':
            case ')':
                // special characters
                enc = "(?:\\" + str + "|%" + utf8Hex(str) + ")";
                break;
            case ' ':
                // inlined version of: enc = "(?:" + enc + "|" + encoded('+') + ")";
                enc = "(?:%20|\\+|%2B)";
                break;
            case '+':
                enc = "(?:\\+|%2B)";
                break;
            default:
                // default case
                enc = URICodec.encode(str);
                if (enc.equals(str)) {
                    enc = "(?:" + escape(str) + "|%" + utf8Hex(str) + ")";
                }
        }
        logger.debug("Char <{}> encoded to <{}>", c, enc);
        return enc;
    }

    protected static String utf8Hex(String str) throws UnsupportedEncodingException {
        return Hex.toHex(str.getBytes("UTF8"));
    }

    protected static String escape(String enc) {
        String escaped = Pattern.quote(enc);
        return escaped;
    }

    public static Replacement encoded() {
        return new Replacement() {
            @Override
            public String replacement(String content, Matcher matcher) {
                try {
                    final char c = content.charAt(matcher.start());
                    return encoded(c);
                } catch (UnsupportedEncodingException e) {
                    throw new SwoopException(e);
                }
            }
        };
    }

    // if path.respond_to? :to_str
    // pattern = path.to_str.gsub(/[^\?\%\\\/\:\*\w]/) { |c| encoded(c) }
    // pattern.gsub!(/((:\w+)|\*)/) do |match|
    // if match == "*"
    // keys << 'splat'
    // "(.*?)"
    // else
    // keys << $2[1..-1]
    // "([^/?#]+)"
    // end
    // end
    // [/^#{pattern}$/, keys]

    protected static Replacement keyCollector(final List<String> keys) {
        return new Replacement() {
            @Override
            public String replacement(String content, Matcher matcher) {
                if (matcher.group(1).equals("*")) {
                    keys.add("splat");
                    return "(.*?)";
                } else {
                    keys.add(matcher.group(2));
                    return "([^/?#]+)";
                }
            }
        };
    }

    @Override
    public PathMatcher compile(String routeExpr) {
        final List<String> keys = new ArrayList<String>();
        String encoded = gsub(toEncodePattern, routeExpr, encoded());
        String pattern = gsub(paramsPattern, encoded, keyCollector(keys));
        pattern = pattern.replace("/", "\\/");
        pattern = "^" + pattern + "$";

        logger.debug("Expression <{}> compiled to <{}> ({})", o(routeExpr, pattern, keys));
        return new RegexPathMatcher(Pattern.compile(pattern), keys);
    }

}