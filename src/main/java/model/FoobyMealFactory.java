package model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FoobyMealFactory {

    private static final Pattern MACRO_PATTERN = Pattern.compile("^(?<number>\\d+).*$");
    private static final Pattern HAS_HEALTHY_TAG_PATTERN = Pattern.compile(".*(, healthy and balanced).*");

    // css selectors:
    public static final String FAT_SELECTOR = "#page-header-recipe__panel-detail > div.page-header-recipe__meta-container > div.meta-info.meta-info--big.meta-info--visible-breaks > span:nth-child(5)";
    public static final String NAME_SELECTOR = "#page-header-recipe__panel-detail > h1";
    public static final String CARB_SELECTOR = "#page-header-recipe__panel-detail > div.page-header-recipe__meta-container > div.meta-info.meta-info--big.meta-info--visible-breaks > span:nth-child(8)";
    public static final String PROTEIN_SELECTOR = "#page-header-recipe__panel-detail > div.page-header-recipe__meta-container > div.meta-info.meta-info--big.meta-info--visible-breaks > span:nth-child(11)";

    public static Meal fromHtml(String id, String html) {
        Meal meal = new Meal(id);
        Document doc = Jsoup.parse(html);
        meal.setName(parseString(doc, NAME_SELECTOR));

        long carbs = parseMacro(doc, CARB_SELECTOR);
        long protein = parseMacro(doc, PROTEIN_SELECTOR);
        long fat = parseMacro(doc, FAT_SELECTOR);
        boolean healthy = isMealHealthy(doc);

        meal.setCarbs(carbs);
        meal.setFat(fat);
        meal.setProtein(protein);
        meal.setHealthy(healthy);

        return meal;
    }

    private static boolean isMealHealthy(Document doc) {
        String tags = parseString(doc, "#page-header-recipe__panel-detail > div.page-header-recipe__meta-container > div:nth-child(2) > span");
        Matcher matcher = HAS_HEALTHY_TAG_PATTERN.matcher(tags);
        return matcher.matches();
    }

    private static String parseString(Document doc, String cssQuery) {
        Element element = doc.selectFirst(cssQuery);
        if (element.hasText()) {
            return element.text();
        }
        return "N / A";
    }

    private static long parseMacro(Document doc, String cssSelector) {
        Element element = doc.selectFirst(cssSelector);

        if (element == null || !element.hasText()) {
            return 0;
        }

        Matcher matcher = MACRO_PATTERN.matcher(element.text());
        if (matcher.find()) {
            return Long.parseLong(matcher.group("number"));
        }

        return 0;
    }

}
