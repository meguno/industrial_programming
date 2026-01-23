package pattern;

import Bicycle.Bicycle;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class CsvDataWriterDecorator extends DataWriterDecorator {
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

	public CsvDataWriterDecorator(DataWriter writer) {
		super(writer);
	}

	@Override
	public void write(List<Bicycle> bicycles, String filename) {
		String csvFilename = filename.endsWith(".csv") ? filename : filename + ".csv";

		try (PrintWriter writer = new PrintWriter(new FileWriter(csvFilename))) {
			writer.println("ID,Type,Model,Frame Material,Wheel Size,Gears,Manufacture Date,Price,Accessories");

			for (Bicycle bike : bicycles) {
				String dateStr = bike.getManufactureDate() != null ? dateFormat.format(bike.getManufactureDate()) : "";
				String accessories = String.join(";", bike.getAccessories());

				writer.printf("%d,%s,%s,%s,%d,%d,%s,%.2f,%s%n",
						bike.getId(),
						escapeCsv(bike.getType()),
						escapeCsv(bike.getModel()),
						escapeCsv(bike.getFrameMaterial()),
						bike.getWheelSize(),
						bike.getGears(),
						dateStr,
						bike.getPrice(),
						escapeCsv(accessories));
			}

			System.out.println("✓ CSV data written to: " + csvFilename);

			super.write(bicycles, filename.replace(".csv", "_summary.txt"));

		} catch (IOException e) {
			System.err.println("✗ Error writing CSV: " + e.getMessage());
		}
	}

	private String escapeCsv(String value) {
		if (value == null)
			return "";
		if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
			return "\"" + value.replace("\"", "\"\"") + "\"";
		}
		return value;
	}
}