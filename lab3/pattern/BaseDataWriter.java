// BaseDataWriter.java
package pattern;

import Bicycle.Bicycle;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class BaseDataWriter implements DataWriter {
	protected SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

	@Override
	public void write(List<Bicycle> bicycles, String filename) {
		try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
			writer.println("=== Bicycle Database ===");
			writer.println("Total: " + bicycles.size());
			writer.println();

			for (Bicycle bike : bicycles) {
				writer.println("ID: " + bike.getId());
				writer.println("Type: " + bike.getType());
				writer.println("Model: " + bike.getModel());
				writer.println("Price: $" + String.format("%.2f", bike.getPrice()));
				writer.println("---");
			}

			System.out.println("✓ Data written to: " + filename);
		} catch (IOException e) {
			System.err.println("✗ Error writing to file: " + e.getMessage());
		}
	}
}