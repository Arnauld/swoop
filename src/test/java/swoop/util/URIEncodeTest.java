package swoop.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class URIEncodeTest {

    @Test(dataProvider = "data")
    public void urlEncoder(String entry, String expected) throws UnsupportedEncodingException {
        assertThat(URLEncoder.encode(entry, "UTF8"), equalTo(expected));
    }
    
    @Test(dataProvider = "data")
    public void uriEncode(String entry, String expected) throws UnsupportedEncodingException {
        assertThat(URIEncode.encode(entry), equalTo(expected));
    }

    
    @DataProvider(name="data")
    public Object[][] inputExpected() {
        return new Object[][] {
                {"a", "a"}, //
                {"b", "b"}, //
                {"*", "*"}, //
                {" ", "+"}, //
                {".", "."}, //
                {"(", "%28"}, //
                {")", "%29"}, //
                {"[", "%5B"}, //
                {"]", "%5D"}, //
                {"=", "%3D"}, //
                {"-", "-"}, //
                {"_", "_"}, //
                {"%", "%25"}, //
                {"%24", "%2524"}, //
                {"?", "%3F"}, //
                {"!", "%21"}, //
                {"#", "%23"}, //
                {"$", "%24"} //
        };
    }
}
