package model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FoobyMealFactory {

    private static final Pattern MACRO_PATTERN = Pattern.compile("^(?<number>\\d+).*$");

    public static Meal fromHtml(String id, String html) {
        Meal meal = new Meal(id);
        Document doc = Jsoup.parse(html);
        meal.setName(parseName(doc));

        Long carbs = parseMacro(doc, "#page-header-recipe__panel-detail > div.page-header-recipe__meta-container > div.meta-info.meta-info--big.meta-info--visible-breaks > span:nth-child(8)");
        Long protein = parseMacro(doc, "#page-header-recipe__panel-detail > div.page-header-recipe__meta-container > div.meta-info.meta-info--big.meta-info--visible-breaks > span:nth-child(11)");
        Long fat = parseMacro(doc, "#page-header-recipe__panel-detail > div.page-header-recipe__meta-container > div.meta-info.meta-info--big.meta-info--visible-breaks > span:nth-child(5)");


        meal.setCarbs(carbs);
        meal.setFat(fat);
        meal.setProtein(protein);

        return meal;
    }

    private static String parseName(Document doc) {
        Element element = doc.selectFirst("#page-header-recipe__panel-detail > h1");
        if (element.hasText()) {
            return element.text();
        }
        return "NO-NAME";
    }

    private static Long parseMacro(Document doc, String cssSelector) {
        Element element = doc.selectFirst(cssSelector);

        if (element == null || !element.hasText()) {
            return null;
        }

        Matcher matcher = MACRO_PATTERN.matcher(element.text());
        if (matcher.find()) {
            return Long.valueOf(matcher.group("number"));
        }

        return null;
    }

}
