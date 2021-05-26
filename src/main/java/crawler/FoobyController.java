package crawler;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class FoobyController extends CrawlController {

    public static CrawlController defaultController(String localTempFolder) throws Exception {
        CrawlConfig config = new CrawlConfig();

        config.setCrawlStorageFolder(localTempFolder + "macro_crawler_temp\\");
        config.setPolitenessDelay(500);
        config.setMaxDepthOfCrawling(10);
        config.setIncludeBinaryContentInCrawling(false);
        config.setResumableCrawling(false);
        config.setMaxPagesToFetch(1000);

        // Instantiate the controller for this crawl.
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new FoobyController(config, pageFetcher, robotstxtServer);

        controller.addSeed("https://fooby.ch/en.html");

        return controller;
    }

    private FoobyController(CrawlConfig config, PageFetcher pageFetcher, RobotstxtServer robotstxtServer) throws Exception {
        super(config, pageFetcher, robotstxtServer);
    }
}
