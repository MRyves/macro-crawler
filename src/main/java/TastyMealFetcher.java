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
            this.mealConsumer.accept(this.fetchResults(request).values());
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

    private Map<String, Meal> fetchResults(HttpUriRequest request) throws IOException {
        try (CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
             CloseableHttpResponse execute = closeableHttpClient.execute(request);
             JsonParser jParser = this.jFactory.createParser(new BufferedInputStream(execute.getEntity().getContent()))) {
            while (jParser.nextToken() != JsonToken.END_OBJECT) {
                if ("results".equals(jParser.getCurrentName())) {
                    LOGGER.info("Results found. Starting to parse meals...");
                    this.moveToStartOfObject(jParser);
                    return this.parseResults(jParser);
                }
            }
        }
        return Collections.emptyMap();
    }

    private void moveToStartOfObject(JsonParser jParser) throws IOException {
        while (jParser.nextToken() != JsonToken.START_OBJECT) {
            // do nothing
        }
    }

    private Map<String, Meal> parseResults(JsonParser jParser) throws IOException {
        Map<String, Meal> meals = new HashMap<>();
        while (jParser.nextToken() != JsonToken.END_ARRAY) {
            ObjectNode recipeNode = objectMapper.readTree(jParser);
            Meal meal = this.parseResult(recipeNode);
            if (meal != null && !this.fetchedMeals.contains(meal.hashCode())) {
                meals.put(meal.getId(), meal);
                this.fetchedMeals.add(meal.hashCode());
            }
        }

        LOGGER.info("Finished parsing results. Parsed a total of {} meals", meals.size());
        return meals;
    }

    private Meal parseResult(ObjectNode recipeNode) {
        JsonNode name = recipeNode.path("name");
        JsonNode carbNode = recipeNode.path("nutrition").path("carbohydrates");
        JsonNode fatNode = recipeNode.path("nutrition").path("fat");
        JsonNode proteinNode = recipeNode.path("nutrition").path("protein");

        Meal returningMeal = null;

        if (!recipeNode.isObject() &&
                !name.isMissingNode() &&
                !carbNode.isMissingNode() &&
                !fatNode.isMissingNode() &&
                !proteinNode.isMissingNode()) {
            returningMeal = new Meal("Tasty" + name.hashCode());
            returningMeal.setName(name.asText());
            returningMeal.setCarbs(carbNode.asLong());
            returningMeal.setFat(fatNode.asLong());
            returningMeal.setProtein(proteinNode.asLong());
            returningMeal.setHealthy(this.hasHealthyTag(recipeNode));
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
