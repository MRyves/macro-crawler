import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String LOCAL_TEMP_FOLDER = "C:\\temp\\";

    public static void main(String[] args) throws Exception {
        CrawlConfig config = new CrawlConfig();

        config.setCrawlStorageFolder(LOCAL_TEMP_FOLDER + "macro_crawler_temp\\");
        config.setPolitenessDelay(500);
        config.setMaxDepthOfCrawling(2);
        config.setIncludeBinaryContentInCrawling(false);
        config.setResumableCrawling(false);
        config.setMaxPagesToFetch(10);

        // Instantiate the controller for this crawl.
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        controller.addSeed("https://fooby.ch/en.html");


        Path mealsCsv = Path.of(LOCAL_TEMP_FOLDER, "meals.csv");


        try (MealCSVWriter mealCSVWriter = new MealCSVWriter(mealsCsv)) {
            mealCSVWriter.writeHeader();
            MealCrawlerFactory factory = new MealCrawlerFactory(mealCSVWriter, 3);

            controller.start(factory, 2);
            factory.flush();
        } catch (IOException e) {
            LOGGER.error("Error during opening csv file", e);
        }
    }
}
