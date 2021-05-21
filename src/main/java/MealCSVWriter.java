import model.Meal;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

public class MealCSVWriter implements AutoCloseable, Consumer<Meal[]> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MealCSVWriter.class);

    private final Path csvFile;

    private CSVPrinter csvPrinter;
    private BufferedWriter bufferedWriter;

    public MealCSVWriter(Path csvFile) throws IOException {
        this.csvFile = csvFile;
    }

    public void open() throws Exception {
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
    public void accept(Meal[] meals) {
        try {
            this.csvPrinter.printRecords(meals);
        } catch (IOException e) {
            LOGGER.error("Exception during writing into CSV file", e);
        }
    }
}
