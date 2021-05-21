import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String LOCAL_TEMP_FOLDER = "C:\\temp\\";

    public static void main(String[] args) throws Exception {
        CrawlConfig config = new CrawlConfig();

        config.setCrawlStorageFolder(LOCAL_TEMP_FOLDER + "macro_crawler_temp\\");
        config.setPolitenessDelay(1000);
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
            mealCSVWriter.open();
            mealCSVWriter.writeHeader();
            CrawlController.WebCrawlerFactory<FoobyCrawler> factory = new MealCrawlerFactory(mealCSVWriter, 3);

            controller.start(factory, 2);
        } catch (IOException e) {
            LOGGER.error("Error during opening csv file", e);
        }
    }
}
