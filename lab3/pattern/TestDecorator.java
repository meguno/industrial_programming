package pattern;

import Bicycle.Bicycle;
import java.util.ArrayList;
import java.util.List;

public class TestDecorator {
	public static void main(String[] args) {
		System.out.println("Testing Decorator Pattern...\n");

		List<Bicycle> bicycles = new ArrayList<>();

		System.out.println("1. Base writer:");
		DataWriter baseWriter = new BaseDataWriter();
		baseWriter.write(bicycles, "test_base.txt");

		System.out.println("\n2. CSV decorator:");
		DataWriter csvWriter = new CsvDataWriterDecorator(new BaseDataWriter());
		csvWriter.write(bicycles, "test_csv");

		System.out.println("\n3. JSON decorator:");
		DataWriter jsonWriter = new JsonDataWriterDecorator(new BaseDataWriter());
		jsonWriter.write(bicycles, "test_json");

		System.out.println("\n4. Chain of decorators (CSV -> JSON):");
		DataWriter chainWriter = new BaseDataWriter();
		chainWriter = new CsvDataWriterDecorator(chainWriter);
		chainWriter = new JsonDataWriterDecorator(chainWriter);
		chainWriter.write(bicycles, "test_chain");

		System.out.println("\n✓ Decorator Pattern tested successfully!");
	}
}