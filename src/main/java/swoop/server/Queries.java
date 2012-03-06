package swoop.server;

import static swoop.util.Objects.o;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swoop.util.Multimap;
import swoop.util.Tree;
import swoop.util.URICodec;

public class Queries {
    
    private static Logger logger = LoggerFactory.getLogger(Queries.class);
    
    static final Pattern NORMALIZE_PARAMETERS_PATTERN = Pattern.compile("\\A[\\[\\]]*([^\\[\\]]+)\\]*");
    static final Pattern AFTER_P1 = Pattern.compile("^\\[\\]\\[([^\\[\\]]+)\\]");
    static final Pattern AFTER_P2 = Pattern.compile("^\\[\\](.+)$");

    public static Multimap<String,String> parseQuery(String query) {
        Multimap<String,String> params = new Multimap<String, String>();
        String[] split = query.split("[&;]");
        for(String p : split) {
            String[] keyValue = p.split("=", 2);
            String key = keyValue[0];
            String val = (keyValue.length>1)?keyValue[1]:"";
            params.put(URICodec.decode(key), URICodec.decode(val));
        }
        return params;
    }
    
    public static Tree<String,String> normalizeParameters(Tree<String,String> tree, String name, String value) {
        Pattern p = NORMALIZE_PARAMETERS_PATTERN;
        Matcher matcher = p.matcher(name);
        if(!matcher.find()) {
            tree.put(name, value);
            return tree;
        }
        String key = matcher.group(1);
        String after = name.substring(matcher.end());
        if(StringUtils.isEmpty(after)) {
            tree.put(key, value);
        }
        else if("[]".equals(after)) {
            if(tree.hasValues()) {
                logger.warn("A node should not be a hash and a list at the same time! for key: <{}> value: <{}>, node values: <{}>", o(name, value, tree.getValues()));
            }
            tree.put(key, value);
        }
        else {
            boolean p1OrP2 = false;
            matcher = AFTER_P1.matcher(after);
            if(matcher.find()) {
                p1OrP2 = true; 
            }
            else {
                matcher = AFTER_P2.matcher(after);
                if(matcher.find())
                    p1OrP2 = true; 
            }
            if(p1OrP2) {
                String child_key = matcher.group(1);
                tree.getOrCreateChild(key).acceptValues();
                normalizeParameters(tree.getOrCreateChild(key), child_key, value);
            }
            else {
                normalizeParameters(tree.getOrCreateChild(key), after, value);
            }
        }
        return tree;
    }
}
