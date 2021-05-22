import model.Meal;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MealCSVWriter implements AutoCloseable, Consumer<Collection<Meal>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MealCSVWriter.class);

    private final Path csvFile;
    private final CSVPrinter csvPrinter;
    private final BufferedWriter bufferedWriter;

    public MealCSVWriter(Path csvFile) throws IOException {
        this.csvFile = csvFile;
        bufferedWriter = Files.newBufferedWriter(this.csvFile, StandardCharsets.UTF_8);
        csvPrinter = new CSVPrinter(bufferedWriter, CSVFormat.DEFAULT);
    }

    public void writeHeader() throws IOException {
        csvPrinter.printRecord(
                "ID",
                "mealName",
                "carbs",
                "protein",
                "fat",
                "kcal"
        );
    }

    @Override
    public void close() throws Exception {
        if (this.csvPrinter != null && this.bufferedWriter != null) {
            this.csvPrinter.close();
            this.bufferedWriter.close();
        }
    }

    @Override
    public void accept(Collection<Meal> meals) {
        try {
            LOGGER.info("Writing {} meals to {}", meals.size(), this.csvFile);
            this.csvPrinter.printRecords(meals.stream().map(this::toMealRecord).collect(Collectors.toUnmodifiableList()));
        } catch (IOException e) {
            LOGGER.error("Exception during writing into CSV file", e);
        }
    }

    @NotNull
    private List<String> toMealRecord(Meal meal) {
        return Arrays.asList(meal.getId(),
                meal.getName(),
                String.valueOf(meal.getCarbs()),
                String.valueOf(meal.getProtein()),
                String.valueOf(meal.getFat()),
                String.valueOf(meal.getKcal()));
    }
}
