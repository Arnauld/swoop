package swoop.util;

import org.testng.annotations.Test;

import fj.F;
import fj.data.List;

public class FJTest {

    @Test
    public void filterReturnElementsInReverseOrder() {
       List<Integer> elements = List.list(1,2,3,4,5,6,7,8,9);
       System.out.println(elements.filter(greaterThan(5)).toCollection());
    }

    private F<Integer, Boolean> greaterThan(final int threshold) {
        return new F<Integer, Boolean> () {
            @Override
            public Boolean f(Integer a) {
                return a>threshold;
            }
        };
    }
}
