package swoop.path;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIn.isOneOf;
import static swoop.path.Verb.Connect;
import static swoop.path.Verb.Delete;
import static swoop.path.Verb.Get;
import static swoop.path.Verb.Head;
import static swoop.path.Verb.Options;
import static swoop.path.Verb.Post;
import static swoop.path.Verb.Put;
import static swoop.path.Verb.Trace;
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
    }

    @Test(dataProvider = "allVerbs")
    public void isHttpMethod(Verb verb) {
        assertThat(verb.isHttpMethod(), equalTo(
                isOneOf(Connect, Delete, Get, Head, Options, Post, Put, Trace).matches(verb)));
    }

    @DataProvider(name = "allVerbs")
    public Object[][] matchAll() {
        Verb[] verbs = Verb.values();
        Object[][] list = new Object[verbs.length][];
        for (int i = 0; i < verbs.length; i++)
            list[i] = o(verbs[i]);
        return list;
    }

}
