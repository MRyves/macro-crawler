import edu.uci.ics.crawler4j.crawler.CrawlController;
import model.Meal;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class MealCrawlerFactory implements CrawlController.WebCrawlerFactory<FoobyCrawler> {


    private final Map<String, Meal> buffer = new ConcurrentHashMap<>();
    private final Consumer<Collection<Meal>> sink;
    private final int bufferSize;

    public MealCrawlerFactory(Consumer<Collection<Meal>> sink, int bufferSize) {
        this.bufferSize = bufferSize;
        this.sink = sink;
    }

    @Override
    public FoobyCrawler newInstance() {
        return new FoobyCrawler(this::addToBuffer);
    }

    public void flush() {
        if (!this.buffer.isEmpty()) {
            this.sink.accept(this.buffer.values());
            this.buffer.clear();
        }
    }

    private void addToBuffer(Meal meal) {
        this.buffer.putIfAbsent(meal.getId(), meal);

        if (this.buffer.size() >= bufferSize) {
            this.flush();
        }
    }
}
