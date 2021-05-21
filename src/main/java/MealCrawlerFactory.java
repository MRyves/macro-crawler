import edu.uci.ics.crawler4j.crawler.CrawlController;
import model.Meal;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;

public class MealCrawlerFactory implements CrawlController.WebCrawlerFactory<FoobyCrawler> {


    private final Queue<Meal> queue = new ConcurrentLinkedDeque<>();
    private final Consumer<Meal[]> sink;
    private final int bufferSize;

    public MealCrawlerFactory(Consumer<Meal[]> sink, int bufferSize) {
        this.bufferSize = bufferSize;
        this.sink = sink;
    }

    @Override
    public FoobyCrawler newInstance() {
        return new FoobyCrawler(this::addToBuffer);
    }

    private void addToBuffer(Meal meal){
        this.queue.add(meal);

        if(this.queue.size() >= bufferSize){
            this.sink.accept(queue.toArray(new Meal[0]));
            this.queue.clear();
        }
    }
}
