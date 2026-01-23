package pattern;

import Bicycle.Bicycle;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class JsonDataWriterDecorator extends DataWriterDecorator {
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

	public JsonDataWriterDecorator(DataWriter writer) {
		super(writer);
	}

	@Override
	public void write(List<Bicycle> bicycles, String filename) {
		String jsonFilename = filename.endsWith(".json") ? filename : filename + ".json";

		try (PrintWriter writer = new PrintWriter(new FileWriter(jsonFilename))) {
			writer.println("{");
			writer.println("  \"bicycles\": [");

			for (int i = 0; i < bicycles.size(); i++) {
				Bicycle bike = bicycles.get(i);
				writer.println("    {");
				writer.println("      \"id\": " + bike.getId() + ",");
				writer.println("      \"type\": \"" + escapeJson(bike.getType()) + "\",");
				writer.println("      \"model\": \"" + escapeJson(bike.getModel()) + "\",");
				writer.println("      \"frameMaterial\": \"" + escapeJson(bike.getFrameMaterial()) + "\",");
				writer.println("      \"wheelSize\": " + bike.getWheelSize() + ",");
				writer.println("      \"gears\": " + bike.getGears() + ",");

				if (bike.getManufactureDate() != null) {
					writer.println(
							"      \"manufactureDate\": \"" + dateFormat.format(bike.getManufactureDate()) + "\",");
				} else {
					writer.println("      \"manufactureDate\": null,");
				}

				writer.println("      \"price\": " + String.format("%.2f", bike.getPrice()) + ",");

				writer.println("      \"accessories\": [");
				List<String> accessories = bike.getAccessories();
				for (int j = 0; j < accessories.size(); j++) {
					writer.print("        \"" + escapeJson(accessories.get(j)) + "\"");
					if (j < accessories.size() - 1)
						writer.print(",");
					writer.println();
				}
				writer.println("      ]");

				writer.print("    }");
				if (i < bicycles.size() - 1)
					writer.print(",");
				writer.println();
			}

			writer.println("  ]");
			writer.println("}");

			System.out.println("✓ JSON data written to: " + jsonFilename);

			super.write(bicycles, filename.replace(".json", "_summary.txt"));

		} catch (IOException e) {
			System.err.println("✗ Error writing JSON: " + e.getMessage());
		}
	}

	private String escapeJson(String value) {
		if (value == null)
			return "";
		return value.replace("\\", "\\\\")
				.replace("\"", "\\\"")
				.replace("\n", "\\n")
				.replace("\r", "\\r")
				.replace("\t", "\\t");
	}
}