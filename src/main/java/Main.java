import crawler.FoobyController;
import crawler.MealCrawlerFactory;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String LOCAL_TEMP_FOLDER = "C:\\temp\\";

    public static void main(String[] args) throws Exception {
        Path mealsCsv = Path.of(LOCAL_TEMP_FOLDER, "meals.csv");

        try (MealCSVWriter mealCSVWriter = new MealCSVWriter(mealsCsv)) {
            mealCSVWriter.writeHeader();

            // fooby scraping:
            CrawlController foobyCrawlController = FoobyController.defaultController(LOCAL_TEMP_FOLDER);
            MealCrawlerFactory factory = new MealCrawlerFactory(mealCSVWriter, 100);
            foobyCrawlController.start(factory, 2);
            factory.flush();

            // tasty api access:
            TastyMealFetcher tastyMealFetcher = new TastyMealFetcher(mealCSVWriter);
            tastyMealFetcher.consume(5, true);
            tastyMealFetcher.consume(10, false);
        } catch (IOException e) {
            LOGGER.error("Error during opening csv file", e);
        }
    }

}
