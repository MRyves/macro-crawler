import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import model.FoobyMealFactory;
import model.Meal;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Macro Crawler for <a href="www.fooby.ch">Fooby</a>
 *
 * @author yves
 */
public class FoobyCrawler extends WebCrawler {

    public static final String ROOT_URL = "https://fooby.ch";

    public static final Pattern RECIPE_PAGE_URL_PATTERN = Pattern.compile("/recipes/(?<recipeId>\\d+)/");

    public static final Pattern IGNORED_FILE_ENDINGS = Pattern.compile(".*(\\.css|\\.js)$");

    private static final AtomicInteger mealCounter = new AtomicInteger();

    private final Consumer<Meal> mealConsumer;

    public FoobyCrawler(Consumer<Meal> mealConsumer) {
        this.mealConsumer = mealConsumer;
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();

        return href.startsWith(ROOT_URL) &&
                !IGNORED_FILE_ENDINGS.matcher(href).matches() &&
                RECIPE_PAGE_URL_PATTERN.matcher(href).find();
    }

    @Override
    public void visit(Page page) {
        logger.info("Visiting page: {}", page.getWebURL());
        Matcher matcher = RECIPE_PAGE_URL_PATTERN.matcher(page.getWebURL().toString());
        if (matcher.find()) {
            long urlRecipeId = Long.parseLong(matcher.group("recipeId"));
            handleRecipePage(page, "fooby-" + urlRecipeId);
        }
    }

    private void handleRecipePage(Page page, String id) {
        logger.info("This is a Recipe page with id: {}", id);
        Meal meal = FoobyMealFactory.fromHtml(id, ((HtmlParseData) page.getParseData()).getHtml());
        logger.info("Parsed meal nr. {}: {}", mealCounter.getAndIncrement(), meal);
        this.mealConsumer.accept(meal);
    }

    @Override
    public void onBeforeExit() {
        logger.warn("EXITING FOOBY CRAWLER");
    }
}
