import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

class TastyMealFetcherTest {

    @Test
    void test() throws IOException, URISyntaxException {
        TastyMealFetcher tastyMealFetcher = new TastyMealFetcher(meals -> meals.forEach(System.out::println));
//        tastyMealFetcher.consume(1, true);
        tastyMealFetcher.consume(2, false);
    }
}
