package swoop.route;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static swoop.util.Objects.o;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class VerbTest {

    @Test
    public void lookup_get_ignoringcase() {
        assertThat(Verb.lookup("get"), equalTo(Verb.Get));
        assertThat(Verb.lookup("gEt"), equalTo(Verb.Get));
        assertThat(Verb.lookup("geT"), equalTo(Verb.Get));
        assertThat(Verb.lookup("GEt"), equalTo(Verb.Get));
        assertThat(Verb.lookup("GET"), equalTo(Verb.Get));
    }

    @Test
    public void lookup_get() {
        assertThat(Verb.lookup("get"), equalTo(Verb.Get));
    }

    @Test
    public void lookup_post() {
        assertThat(Verb.lookup("post"), equalTo(Verb.Post));
    }
    
    @Test
    public void lookup_post_ignoringcase() {
        assertThat(Verb.lookup("post"), equalTo(Verb.Post));
        assertThat(Verb.lookup("Post"), equalTo(Verb.Post));
        assertThat(Verb.lookup("pOst"), equalTo(Verb.Post));
        assertThat(Verb.lookup("poST"), equalTo(Verb.Post));
        assertThat(Verb.lookup("POST"), equalTo(Verb.Post));
    }

    @Test
    public void lookup_delete() {
        assertThat(Verb.lookup("delete"), equalTo(Verb.Delete));
    }

    @Test
    public void lookup_put() {
        assertThat(Verb.lookup("put"), equalTo(Verb.Put));
    }

    @Test
    public void lookup_others() {
        assertThat(Verb.lookup("head"), equalTo(Verb.Head));
        assertThat(Verb.lookup("connect"), equalTo(Verb.Connect));
        assertThat(Verb.lookup("options"), equalTo(Verb.Options));
        assertThat(Verb.lookup("trace"), equalTo(Verb.Trace));
        //
        assertThat(Verb.lookup("any"), equalTo(Verb.Any));
    }

    @Test(dataProvider="allVerbs")
    public void isFilter(Verb verb) {
        assertThat(verb.isAny(), is(verb==Verb.Any));
    }
    
    @Test(dataProvider="allVerbs")
    public void isHttpMethod(Verb verb) {
        assertThat(verb.isHttpMethod(), is(verb!=Verb.Any));
    }
    
    @DataProvider(name = "allVerbs")
    public Object[][] matchAll() {
        Verb[] verbs = Verb.values();
        Object[][] list = new Object[verbs.length][];
        for(int i=0;i<verbs.length;i++)
           list[i] = o(verbs[i]);
        return list;
    }
    
    @Test
    public void matches_any() {
        for(Verb v : Verb.values()) {
            assertThat(Verb.Any.matches(v), is(true));
        }
    }
    
    @Test
    public void matches_get() {
        assertThat(Verb.Get.matches(Verb.Any), is(false));
        assertThat(Verb.Get.matches(Verb.Post), is(false));
        assertThat(Verb.Get.matches(Verb.Get), is(true));
    }
    
    @Test(dataProvider="allVerbsCombi")
    public void matches(Verb verb1, Verb verb2, boolean match) {
        assertThat(verb1.matches(verb2), equalTo(match));
    }
    
    @DataProvider(name = "allVerbsCombi")
    public Object[][] allVerbsCombi() {
        Verb[] verbs = Verb.values();
        Object[][] list = new Object[verbs.length*verbs.length][];
        int count = 0;
        for(int i=0;i<verbs.length;i++) {
            for(int j=0;j<verbs.length;j++) {
                Verb verb1 = verbs[i];
                Verb verb2 = verbs[j];
                list[count++] = o(verb1, verb2, verb1==Verb.Any || verb1==verb2);
             }
        }
        return list;
    }
}
