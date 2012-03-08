package swoop.route;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.collection.IsEmptyIterable;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import swoop.util.Multimap;

public class RouteParametersTest {
    
    private RouteParameters params;
    private Multimap<String, String> underlying;
    
    @BeforeMethod
    public void setup () {
        underlying = new Multimap<String, String>();
        params = new RouteParameters();
        params.setUnderlying(underlying);
    }
    
    @Test
    public void routeParam_returnNull_ifUnderlyingIsNull() {
        params.setUnderlying(null);
        
        assertThat(params.routeParam("name"), nullValue());
    }
    
    @Test
    public void routeParams_returnEmpty_ifUnderlyingIsNull() {
        params.setUnderlying(null);
        assertThat(params.routeParams("name"), IsEmptyIterable.<String>emptyIterable());
    }

    @Test
    public void routeParamKeys_returnEmpty_ifUnderlyingIsNull() {
        params.setUnderlying(null);
        assertThat(params.routeParamKeys(), IsEmptyIterable.<String>emptyIterable());
    }

    @Test
    public void routeParam_parameterName_canAlsoStartsWith_colon_oneValue() {
        underlying.put("name", "McCallum");
        assertThat(params.routeParam("name"), equalTo("McCallum"));
        assertThat(params.routeParam(":name"), equalTo("McCallum"));
    }
    
    @Test
    public void routeParams_parameterName_canAlsoStartsWith_colon_oneValue() {
        underlying.put("name", "McCallum");
        assertThat(params.routeParams("name"), equalTo(asList("McCallum")));
        assertThat(params.routeParams(":name"), equalTo(asList("McCallum")));
    }
    
    @Test
    public void routeParam_parameterName_canAlsoStartsWith_colon_twoValues () {
        underlying.put("name", "McCallum");
        underlying.put("name", "Travis");
        assertThat(params.routeParam("name"), equalTo("McCallum"));
        assertThat(params.routeParam(":name"), equalTo("McCallum"));
    }
    
    @Test
    public void routeParams_parameterName_canAlsoStartsWith_colon_twoValues () {
        underlying.put("name", "McCallum");
        underlying.put("name", "Travis");
        assertThat(params.routeParams("name"), equalTo(asList("McCallum", "Travis")));
        assertThat(params.routeParams(":name"), equalTo(asList("McCallum", "Travis")));
    }
}
