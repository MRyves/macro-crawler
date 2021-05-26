import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import model.Meal;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.roaringbitmap.RoaringBitmap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Consumer;

public class TastyMealFetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(TastyMealFetcher.class);
    private static final int PAGE_SIZE = 200;
    private static final String BASE_API_URL = "https://tasty.p.rapidapi.com/recipes/list";

    private final JsonFactory jFactory = new JsonFactory();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RoaringBitmap fetchedMeals = new RoaringBitmap();

    private final Consumer<Collection<Meal>> mealConsumer;

    public TastyMealFetcher(Consumer<Collection<Meal>> mealConsumer) {
        this.mealConsumer = mealConsumer;
    }

    public void consume(int pages, boolean onlyHealthyRecipes) throws IOException, URISyntaxException {
        for (int currentPageCount = 0; currentPageCount < pages; currentPageCount++) {
            HttpUriRequest request = this.buildRequest(currentPageCount, onlyHealthyRecipes);
            LOGGER.info("Consuming result of {} api call", request);
            Collection<Meal> parsedResults = this.fetchResults(request);
            this.mealConsumer.accept(parsedResults);
            if (parsedResults.isEmpty()) {
                LOGGER.warn("Last api call resulted in zero meals, exiting fetching...");
                break;
            }
        }
    }

    private HttpGet buildRequest(int currentPageCount, boolean onlyHealthyRecipes) throws URISyntaxException {
        URI uri = this.buildUri(currentPageCount, onlyHealthyRecipes);
        HttpGet request = new HttpGet(uri);
        request.addHeader("x-rapidapi-host", "tasty.p.rapidapi.com");
        request.addHeader("x-rapidapi-key", "de3f729651mshe52fa21f8e7b725p1eb7bejsn521b8762fdf8");
        return request;
    }

    private URI buildUri(int pageCount, boolean onlyHealthyRecipes) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(BASE_API_URL)
                .addParameter("from", String.valueOf(pageCount * PAGE_SIZE))
                .addParameter("size", String.valueOf(PAGE_SIZE));
        if (onlyHealthyRecipes) {
            builder.addParameter("tags", "healthy");
        }

        return builder.build();
    }

    private Collection<Meal> fetchResults(HttpUriRequest request) throws IOException {
        try (CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
             CloseableHttpResponse execute = closeableHttpClient.execute(request);
             JsonParser jParser = this.jFactory.createParser(new BufferedInputStream(execute.getEntity().getContent()))) {
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                if ("results".equals(jParser.getCurrentName())) {
                    LOGGER.info("Results found. Starting to parse meals...");
                    this.skipTokens(jParser, 2);
                    return this.parseResults(jParser);
                }
            }
        }
        return Collections.emptyList();
    }

    private void skipTokens(JsonParser jParser, int tokenCount) throws IOException {
        for (int i = 0; i < tokenCount; i++) {
            jParser.nextToken();
        }
    }

    private Collection<Meal> parseResults(JsonParser jParser) throws IOException {
        Collection<Meal> meals = new ArrayList<>();
        while (true) {
            JsonNode recipeNode = objectMapper.readTree(jParser);
            if (recipeNode == null || !recipeNode.isObject()) break;

            Meal meal = this.parseResult((ObjectNode) recipeNode);
            if (meal != null) {
                ingestMeal(meals, meal);
            }
        }

        LOGGER.info("Finished parsing results. Parsed a total of {} meals", meals.size());
        return meals;
    }

    private void ingestMeal(Collection<Meal> meals, Meal meal) {
        if (!this.fetchedMeals.contains(meal.hashCode())) {
            meals.add(meal);
            this.fetchedMeals.add(meal.hashCode());
        } else {
            LOGGER.warn("Parsed already persisted meal: {}", meal);
        }
    }

    private Meal parseResult(ObjectNode recipeNode) {
        JsonNode name = recipeNode.path("name");
        JsonNode carbNode = recipeNode.path("nutrition").path("carbohydrates");
        JsonNode fatNode = recipeNode.path("nutrition").path("fat");
        JsonNode proteinNode = recipeNode.path("nutrition").path("protein");

        Meal returningMeal = null;

        if (!name.isMissingNode() &&
                !carbNode.isMissingNode() &&
                !fatNode.isMissingNode() &&
                !proteinNode.isMissingNode()) {
            returningMeal = new Meal("Tasty" + name.hashCode());
            returningMeal.setName(name.asText());
            returningMeal.setCarbs(carbNode.asLong());
            returningMeal.setFat(fatNode.asLong());
            returningMeal.setProtein(proteinNode.asLong());
            returningMeal.setHealthy(this.hasHealthyTag(recipeNode));
        } else {
            LOGGER.debug("Unable to parse meal with nodes: name={}, carb={}, fat={}, protein={}", name, carbNode, fatNode, proteinNode);
        }
        return returningMeal;
    }

    private boolean hasHealthyTag(ObjectNode recipeNode) {
        ArrayNode tagsNode = (ArrayNode) recipeNode.get("tags");
        if (tagsNode == null || tagsNode.isEmpty()) return false;

        List<JsonNode> name = tagsNode.findValues("name");
        JsonNode jsonNode = name.stream().filter(JsonNode::isValueNode)
                .filter(node -> "healthy".equals(node.asText()))
                .findFirst().orElse(null);
        return jsonNode != null;
    }

}
