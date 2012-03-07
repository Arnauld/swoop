package swoop.util;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CircularBufferTest {

    private CircularBuffer<Long> circularBuffer;

    @BeforeMethod
    public void setUp () {
        circularBuffer = new CircularBuffer<Long>(7);
    }
    
    @Test
    public void empty() {
        assertThat(circularBuffer.size(), equalTo(0));
    }
    
    @Test
    public void add_1() {
        circularBuffer.add(1L);
        assertThat(circularBuffer.size(), equalTo(1));
        assertThat(circularBuffer.toList(), equalTo(asList(1L)));
    }
    
    @Test
    public void add_2() {
        circularBuffer.add(1L);
        circularBuffer.add(2L);
        assertThat(circularBuffer.size(), equalTo(2));
        assertThat(circularBuffer.toList(), equalTo(asList(1L, 2L)));
    }

    @Test
    public void add_3() {
        circularBuffer.add(1L);
        circularBuffer.add(2L);
        circularBuffer.add(3L);
        assertThat(circularBuffer.size(), equalTo(3));
        assertThat(circularBuffer.toList(), equalTo(asList(1L, 2L, 3L)));
    }
    
    @Test
    public void add_4() {
        circularBuffer.add(1L);
        circularBuffer.add(2L);
        circularBuffer.add(3L);
        circularBuffer.add(5L);
        assertThat(circularBuffer.size(), equalTo(4));
        assertThat(circularBuffer.toList(), equalTo(asList(1L, 2L, 3L, 5L)));
    }
    
    @Test
    public void add_5() {
        circularBuffer.add(1L);
        circularBuffer.add(2L);
        circularBuffer.add(3L);
        circularBuffer.add(5L);
        circularBuffer.add(7L);
        assertThat(circularBuffer.size(), equalTo(5));
        assertThat(circularBuffer.toList(), equalTo(asList(1L, 2L, 3L, 5L, 7L)));
    }
    
    @Test
    public void add_6() {
        circularBuffer.add(1L);
        circularBuffer.add(2L);
        circularBuffer.add(3L);
        circularBuffer.add(5L);
        circularBuffer.add(7L);
        circularBuffer.add(11L);
        assertThat(circularBuffer.size(), equalTo(6));
        assertThat(circularBuffer.toList(), equalTo(asList(1L, 2L, 3L, 5L, 7L, 11L)));
    }
    
    @Test
    public void add_7() {
        circularBuffer.add(1L);
        circularBuffer.add(2L);
        circularBuffer.add(3L);
        circularBuffer.add(5L);
        circularBuffer.add(7L);
        circularBuffer.add(11L);
        circularBuffer.add(13L);
        assertThat(circularBuffer.size(), equalTo(7));
        assertThat(circularBuffer.toList(), equalTo(asList(1L, 2L, 3L, 5L, 7L, 11L, 13L)));
    }
    
    @Test
    public void add_8() {
        circularBuffer.add(1L);
        circularBuffer.add(2L);
        circularBuffer.add(3L);
        circularBuffer.add(5L);
        circularBuffer.add(7L);
        circularBuffer.add(11L);
        circularBuffer.add(13L);
        circularBuffer.add(17L);
        assertThat(circularBuffer.size(), equalTo(7));
        assertThat(circularBuffer.toList(), equalTo(asList(2L, 3L, 5L, 7L, 11L, 13L, 17L)));
    }
    
    @Test
    public void add_9() {
        circularBuffer.add(1L);
        circularBuffer.add(2L);
        circularBuffer.add(3L);
        circularBuffer.add(5L);
        circularBuffer.add(7L);
        circularBuffer.add(11L);
        circularBuffer.add(13L);
        circularBuffer.add(17L);
        circularBuffer.add(19L);
        assertThat(circularBuffer.size(), equalTo(7));
        assertThat(circularBuffer.toList(), equalTo(asList(3L, 5L, 7L, 11L, 13L, 17L, 19L)));
    }
}
