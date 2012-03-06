package swoop.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Gsub {

    public interface Replacement {
        String replacement(String content, Matcher matcher);
    }
    
    public static String gsub(String regex, String content, Replacement replacement) {
        Pattern pattern = Pattern.compile(regex);
        return gsub(pattern, content, replacement);
    }
    
    public static String gsub(Pattern pattern, String content, Replacement replacement) {
        Matcher matcher = pattern.matcher(content);
        boolean result = matcher.find();
        if (result) {
            StringBuilder builder = new StringBuilder ();
            int index = 0;
            do {
                int start = matcher.start();
                int end = matcher.end();
                if(start>index) {
                    // first match does not start from beginning
                    builder.append(content, index, start);
                }
                builder.append(replacement.replacement(content, matcher));
                index = end;
                result = matcher.find();
            }
            while(result);
            
            // remaining data
            if(index<content.length())
                builder.append(content.substring(index));
            return builder.toString();
        }
        return content;
    }
       
    /**
     * <p>
     * If replacement returns a content that needs to be processed such as <code>\1\2</code>,
     * otherwise use {@link #gsub(Pattern, String, Replacement)}.
     * 
     * Rely on {@link Matcher#appendReplacement(StringBuffer, String)}.
     * </p>
     * 
     * <small>Watch out for Raiden! han han</small><br/>
     */
    public static String gsub0(Pattern pattern, String content, Replacement replacement) {
        Matcher matcher = pattern.matcher(content);
        boolean result = matcher.find();
        if (result) {
            StringBuffer sb = new StringBuffer();
            do {
                matcher.appendReplacement(sb, replacement.replacement(content, matcher));
                result = matcher.find();
            } while (result);
            matcher.appendTail(sb);
            return sb.toString();
        }
        return content;
    }
}
