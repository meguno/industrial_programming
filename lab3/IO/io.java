package IO;

import abstractClasses.abstractStorage;
import pattern.BaseDataWriter;
import pattern.CsvDataWriterDecorator;
import pattern.DataWriter;
import pattern.JsonDataWriterDecorator;
import Bicycle.Bicycle;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.*;

public class io {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
	private static final String DELIMITER = ",";
	private static final String ACCESSORIES_DELIMITER = ";";
	private static final Logger LOGGER = Logger.getLogger(io.class.getName());

	static {
		try {
			FileHandler handler = new FileHandler("factory_errors.log", true);
			handler.setFormatter(new SimpleFormatter());
			LOGGER.addHandler(handler);
			LOGGER.setLevel(Level.WARNING);
		} catch (IOException e) {
			System.err.println("Failed to configure logger: " + e.getMessage());
		}
	}

	public void saveToFile(abstractStorage<Bicycle> storage, String filename) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
			writer.write(createHeader());
			writer.newLine();

			for (Bicycle bike : getAllBicycles(storage)) {
				writer.write(bicycleToCsv(bike));
				writer.newLine();
			}

			System.out.println("✓ Data saved successfully to CSV file: " + filename);

		} catch (IOException e) {
			System.err.println("✗ Error saving CSV: " + e.getMessage());
			LOGGER.severe("CSV save error: " + e.getMessage());
		}
	}

	public void saveToJsonFile(abstractStorage<Bicycle> storage, String filename) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
			writer.write("{\n");
			writer.write("  \"bicycles\": [\n");

			List<Bicycle> bicycles = storage.get_All();
			boolean first = true;

			for (Bicycle bike : bicycles) {
				if (!first) {
					writer.write(",\n");
				}
				writer.write(bicycleToJson(bike));
				first = false;
			}

			writer.write("\n  ]\n");
			writer.write("}\n");

			System.out.println("✓ Data saved to JSON file: " + filename);

		} catch (IOException e) {
			System.err.println("✗ Error saving JSON: " + e.getMessage());
			LOGGER.severe("JSON save error: " + e.getMessage());
		}
	}

	public void exportToHtml(abstractStorage<Bicycle> storage, String filename) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
			List<Bicycle> bicycles = storage.get_All();

			writer.write("<!DOCTYPE html>\n");
			writer.write("<html>\n");
			writer.write("<head>\n");
			writer.write("    <meta charset=\"UTF-8\">\n");
			writer.write("    <title>Bicycle Catalog</title>\n");
			writer.write("    <style>\n");
			writer.write("        body { font-family: Arial, sans-serif; margin: 20px; }\n");
			writer.write("        h1 { color: #2c3e50; text-align: center; }\n");
			writer.write("        table { border-collapse: collapse; width: 100%; margin: 20px 0; }\n");
			writer.write("        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n");
			writer.write("        th { background-color: #4CAF50; color: white; }\n");
			writer.write("        tr:nth-child(even) { background-color: #f2f2f2; }\n");
			writer.write("        .footer { text-align: center; margin-top: 30px; color: #7f8c8d; }\n");
			writer.write("    </style>\n");
			writer.write("</head>\n");
			writer.write("<body>\n");
			writer.write("    <h1>Bicycle Catalog</h1>\n");
			writer.write("    <p>Total bicycles: " + bicycles.size() + "</p>\n");

			writer.write("    <table>\n");
			writer.write("        <tr>\n");
			writer.write("            <th>ID</th>\n");
			writer.write("            <th>Type</th>\n");
			writer.write("            <th>Model</th>\n");
			writer.write("            <th>Frame Material</th>\n");
			writer.write("            <th>Wheel Size</th>\n");
			writer.write("            <th>Gears</th>\n");
			writer.write("            <th>Manufacture Date</th>\n");
			writer.write("            <th>Price</th>\n");
			writer.write("            <th>Accessories</th>\n");
			writer.write("        </tr>\n");

			for (Bicycle bike : bicycles) {
				writer.write("        <tr>\n");
				writer.write("            <td>" + bike.getId() + "</td>\n");
				writer.write("            <td>" + escapeHtml(bike.getType()) + "</td>\n");
				writer.write("            <td>" + escapeHtml(bike.getModel()) + "</td>\n");
				writer.write("            <td>" + escapeHtml(bike.getFrameMaterial()) + "</td>\n");
				writer.write("            <td>" + bike.getWheelSize() + "</td>\n");
				writer.write("            <td>" + bike.getGears() + "</td>\n");

				String dateStr = bike.getManufactureDate() != null
						? DATE_FORMAT.format(bike.getManufactureDate())
						: "not specified";
				writer.write("            <td>" + dateStr + "</td>\n");

				writer.write("            <td>$" + String.format("%.2f", bike.getPrice()) + "</td>\n");

				String accessoriesStr = String.join(", ", bike.getAccessories());
				if (accessoriesStr.isEmpty()) {
					accessoriesStr = "none";
				}
				writer.write("            <td>" + escapeHtml(accessoriesStr) + "</td>\n");
				writer.write("        </tr>\n");
			}

			writer.write("    </table>\n");

			writer.write("    <div class=\"footer\">\n");
			writer.write("        <p>Generated: " + new Date() + "</p>\n");
			writer.write("    </div>\n");

			writer.write("</body>\n");
			writer.write("</html>\n");

			System.out.println("✓ Data exported to HTML file: " + filename);

		} catch (IOException e) {
			System.err.println("✗ Error exporting to HTML: " + e.getMessage());
			LOGGER.severe("HTML export error: " + e.getMessage());
		}
	}

	public void generateReport(abstractStorage<Bicycle> storage, String filename) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
			List<Bicycle> bicycles = storage.get_All();

			writer.write("============================================================\n");
			writer.write("                 BICYCLE REPORT\n");
			writer.write("============================================================\n\n");

			writer.write("Generation date: " + new Date() + "\n");
			writer.write("Total bicycles: " + bicycles.size() + "\n\n");

			writer.write("STATISTICS:\n");
			writer.write("------------------------------------------------------------\n");

			Map<String, Integer> typeStats = new HashMap<>();
			double totalPrice = 0;
			Bicycle cheapest = null;
			Bicycle mostExpensive = null;

			for (Bicycle bike : bicycles) {
				String type = bike.getType();
				typeStats.put(type, typeStats.getOrDefault(type, 0) + 1);
				totalPrice += bike.getPrice();

				if (cheapest == null || bike.getPrice() < cheapest.getPrice()) {
					cheapest = bike;
				}
				if (mostExpensive == null || bike.getPrice() > mostExpensive.getPrice()) {
					mostExpensive = bike;
				}
			}

			writer.write("Distribution by type:\n");
			for (Map.Entry<String, Integer> entry : typeStats.entrySet()) {
				writer.write("  * " + entry.getKey() + ": " + entry.getValue() + " pcs.\n");
			}

			writer.write("\nFinancial information:\n");
			writer.write("  * Total cost: $" + String.format("%.2f", totalPrice) + "\n");
			writer.write("  * Average price: $" +
					String.format("%.2f", bicycles.isEmpty() ? 0 : totalPrice / bicycles.size()) + "\n");

			if (cheapest != null) {
				writer.write("  * Cheapest: " + cheapest.getModel() +
						" ($" + String.format("%.2f", cheapest.getPrice()) + ")\n");
			}
			if (mostExpensive != null) {
				writer.write("  * Most expensive: " + mostExpensive.getModel() +
						" ($" + String.format("%.2f", mostExpensive.getPrice()) + ")\n");
			}

			writer.write("\n\nDETAILED LIST:\n");
			writer.write("------------------------------------------------------------\n");

			for (Bicycle bike : bicycles) {
				writer.write("\nBicycle #" + bike.getId() + "\n");
				System.out.println("--------------------------------------------------");
				writer.write("Type: " + bike.getType() + "\n");
				writer.write("Model: " + bike.getModel() + "\n");
				writer.write("Frame material: " + bike.getFrameMaterial() + "\n");
				writer.write("Wheel size: " + bike.getWheelSize() + "\"\n");
				writer.write("Gears: " + bike.getGears() + "\n");

				String dateStr = bike.getManufactureDate() != null
						? DATE_FORMAT.format(bike.getManufactureDate())
						: "not specified";
				writer.write("Manufacture date: " + dateStr + "\n");

				writer.write("Price: $" + String.format("%.2f", bike.getPrice()) + "\n");

				if (!bike.getAccessories().isEmpty()) {
					writer.write("Accessories: " + String.join(", ", bike.getAccessories()) + "\n");
				}
			}

			System.out.println("--------------------------------------------------");
			writer.write("End of report\n");

			System.out.println("✓ Report generated to file: " + filename);

		} catch (IOException e) {
			System.err.println("✗ Error generating report: " + e.getMessage());
			LOGGER.severe("Report generation error: " + e.getMessage());
		}
	}

	public int loadFromFile(abstractStorage<Bicycle> storage, String filename) {
		int loadedCount = 0;
		int errorCount = 0;

		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			String line;
			int lineNumber = 0;

			while ((line = reader.readLine()) != null) {
				lineNumber++;

				if (lineNumber == 1) {
					if (!isValidHeader(line)) {
						logError("Invalid file header", line, lineNumber);
					}
					continue;
				}

				if (line.trim().isEmpty()) {
					continue;
				}

				Bicycle bike = parseAndValidateCsvLine(line, lineNumber);
				if (bike != null) {
					storage.add(bike);
					loadedCount++;
				} else {
					errorCount++;
				}
			}

			System.out.printf("✓ Loaded %d bicycles from file: %s (errors: %d)%n",
					loadedCount, filename, errorCount);

		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + filename + ". Creating new one.");
		} catch (IOException e) {
			System.err.println("✗ File reading error: " + e.getMessage());
		}

		return loadedCount;
	}

	private Iterable<Bicycle> getAllBicycles(abstractStorage<Bicycle> storage) {
		try {
			if (storage.getClass().getName().contains("BicycleListStorage")) {
				java.lang.reflect.Field field = storage.getClass()
						.getDeclaredField("bicycles");
				field.setAccessible(true);
				return (Iterable<Bicycle>) field.get(storage);
			} else if (storage.getClass().getName().contains("BicycleMapStorage")) {
				java.lang.reflect.Field field = storage.getClass()
						.getDeclaredField("bicycles");
				field.setAccessible(true);
				java.util.Map<Integer, Bicycle> map = (java.util.Map<Integer, Bicycle>) field.get(storage);
				return map.values();
			}
		} catch (Exception e) {
			LOGGER.warning("Failed to get bicycles from storage: " + e.getMessage());
		}
		return java.util.Collections.emptyList();
	}

	private Bicycle parseAndValidateCsvLine(String line, int lineNumber) {
		try {
			List<String> fields = parseCsvFields(line);

			if (fields.size() < 8) {
				logError("Not enough fields (" + fields.size() + " instead of 8)", line, lineNumber);
				return null;
			}

			String idStr = cleanNumber(fields.get(0));
			if (!isValidPositiveInteger(idStr)) {
				logError("Invalid ID: " + fields.get(0), line, lineNumber);
				return null;
			}
			int id = Integer.parseInt(idStr);

			String type = cleanString(fields.get(1));
			if (type.isEmpty()) {
				logError("Empty type", line, lineNumber);
				return null;
			}

			String model = cleanString(fields.get(2));
			if (model.isEmpty()) {
				logError("Empty model", line, lineNumber);
				return null;
			}

			String frameMaterial = cleanString(fields.get(3));

			String wheelSizeStr = cleanNumber(fields.get(4));
			if (!isValidWheelSize(wheelSizeStr)) {
				logError("Invalid wheel size: " + fields.get(4), line, lineNumber);
				return null;
			}
			int wheelSize = Integer.parseInt(wheelSizeStr);

			String gearsStr = cleanNumber(fields.get(5));
			if (!isValidGearsCount(gearsStr)) {
				logError("Invalid gear count: " + fields.get(5), line, lineNumber);
				return null;
			}
			int gears = Integer.parseInt(gearsStr);

			java.util.Date date = null;
			String dateStr = cleanString(fields.get(6));
			if (!dateStr.isEmpty()) {
				try {
					date = DATE_FORMAT.parse(dateStr);
				} catch (Exception e) {
					logError("Invalid date: " + fields.get(6), line, lineNumber);
				}
			}

			String priceStr = cleanNumber(fields.get(7));
			if (!isValidPrice(priceStr)) {
				logError("Invalid price: " + fields.get(7), line, lineNumber);
				return null;
			}
			double price = Double.parseDouble(priceStr);

			Bicycle bike = new Bicycle(id, type, model, frameMaterial,
					wheelSize, gears, date, price);

			if (fields.size() > 8) {
				String accessoriesStr = cleanString(fields.get(8));
				if (!accessoriesStr.isEmpty()) {
					String[] accessories = accessoriesStr.split(ACCESSORIES_DELIMITER);
					for (String accessory : accessories) {
						String cleanedAccessory = cleanString(accessory);
						if (!cleanedAccessory.isEmpty()) {
							bike.addAccessory(cleanedAccessory);
						}
					}
				}
			}

			return bike;

		} catch (Exception e) {
			logError("Parsing error: " + e.getMessage(), line, lineNumber);
			return null;
		}
	}

	private boolean isValidHeader(String line) {
		return line.contains("ID") && line.contains("Type") && line.contains("Model");
	}

	private boolean isValidPositiveInteger(String str) {
		try {
			int value = Integer.parseInt(str);
			return value > 0;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private boolean isValidWheelSize(String str) {
		try {
			int size = Integer.parseInt(str);
			return size >= 10 && size <= 30;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private boolean isValidGearsCount(String str) {
		try {
			int gears = Integer.parseInt(str);
			return gears >= 1 && gears <= 30;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private boolean isValidPrice(String str) {
		try {
			double price = Double.parseDouble(str);
			return price >= 0;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private String cleanString(String str) {
		if (str == null)
			return "";
		str = str.trim();
		str = str.replaceAll("[\\x00-\\x1F\\x7F]", "");
		return str;
	}

	private String cleanNumber(String str) {
		if (str == null)
			return "";
		str = str.replaceAll("[^0-9.,-]", "");
		str = str.replace(',', '.');
		return str.trim();
	}

	private void logError(String message, String line, int lineNumber) {
		String errorMsg = String.format("Line %d: %s | Data: %s",
				lineNumber, message, line);
		LOGGER.warning(errorMsg);
		System.err.println("ERROR: " + errorMsg);
	}

	private String createHeader() {
		return String.join(DELIMITER, "ID", "Type", "Model", "Frame Material", "Wheel Size", "Gears",
				"Manufacture Date", "Price", "Accessories");
	}

	private String bicycleToCsv(Bicycle bike) {
		String dateStr = bike.getManufactureDate() != null
				? DATE_FORMAT.format(bike.getManufactureDate())
				: "";

		String accessoriesStr = String.join(ACCESSORIES_DELIMITER, bike.getAccessories());

		accessoriesStr = escapeCsvField(accessoriesStr);
		String model = escapeCsvField(bike.getModel());
		String type = escapeCsvField(bike.getType());
		String frameMaterial = escapeCsvField(bike.getFrameMaterial());

		return String.join(DELIMITER,
				String.valueOf(bike.getId()),
				type,
				model,
				frameMaterial,
				String.valueOf(bike.getWheelSize()),
				String.valueOf(bike.getGears()),
				dateStr,
				String.format("%.2f", bike.getPrice()),
				accessoriesStr);
	}

	private String escapeCsvField(String field) {
		if (field == null) {
			return "";
		}
		if (field.contains(DELIMITER) || field.contains("\"") || field.contains("\n")) {
			return "\"" + field.replace("\"", "\"\"") + "\"";
		}
		return field;
	}

	private List<String> parseCsvFields(String line) {
		List<String> fields = new ArrayList<>();
		StringBuilder currentField = new StringBuilder();
		boolean inQuotes = false;

		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);

			if (c == '"') {
				if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
					currentField.append('"');
					i++;
				} else {
					inQuotes = !inQuotes;
				}
			} else if (c == ',' && !inQuotes) {
				fields.add(currentField.toString());
				currentField.setLength(0);
			} else {
				currentField.append(c);
			}
		}

		fields.add(currentField.toString());

		return fields;
	}

	private String bicycleToJson(Bicycle bike) {
		StringBuilder json = new StringBuilder();
		json.append("    {\n");
		json.append("      \"id\": ").append(bike.getId()).append(",\n");
		json.append("      \"type\": \"").append(escapeJson(bike.getType())).append("\",\n");
		json.append("      \"model\": \"").append(escapeJson(bike.getModel())).append("\",\n");
		json.append("      \"frameMaterial\": \"").append(escapeJson(bike.getFrameMaterial())).append("\",\n");
		json.append("      \"wheelSize\": ").append(bike.getWheelSize()).append(",\n");
		json.append("      \"gears\": ").append(bike.getGears()).append(",\n");

		String dateStr = bike.getManufactureDate() != null
				? DATE_FORMAT.format(bike.getManufactureDate())
				: "";
		json.append("      \"manufactureDate\": \"").append(dateStr).append("\",\n");

		json.append("      \"price\": ").append(String.format("%.2f", bike.getPrice())).append(",\n");
		json.append("      \"accessories\": [\n");

		List<String> accessories = bike.getAccessories();
		for (int i = 0; i < accessories.size(); i++) {
			json.append("        \"").append(escapeJson(accessories.get(i))).append("\"");
			if (i < accessories.size() - 1) {
				json.append(",");
			}
			json.append("\n");
		}

		json.append("      ]\n");
		json.append("    }");

		return json.toString();
	}

	private String escapeJson(String str) {
		if (str == null)
			return "";
		return str.replace("\\", "\\\\")
				.replace("\"", "\\\"")
				.replace("\n", "\\n")
				.replace("\r", "\\r")
				.replace("\t", "\\t");
	}

	private String escapeHtml(String str) {
		if (str == null)
			return "";
		return str.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&#39;");
	}

	public static class LoadResult {
		private final int loadedCount;
		private final int errorCount;
		private final boolean fileExists;
		private final List<String> errors;

		public LoadResult(int loadedCount, int errorCount, boolean fileExists, List<String> errors) {
			this.loadedCount = loadedCount;
			this.errorCount = errorCount;
			this.fileExists = fileExists;
			this.errors = errors;
		}

		public int getLoadedCount() {
			return loadedCount;
		}

		public int getErrorCount() {
			return errorCount;
		}

		public boolean isFileExists() {
			return fileExists;
		}

		public List<String> getErrors() {
			return errors;
		}

		public boolean hasErrors() {
			return !errors.isEmpty();
		}
	}

	public int loadFromJson(String filename, abstractStorage<Bicycle> storage) {
		int loadedCount = 0;

		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			StringBuilder jsonBuilder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				jsonBuilder.append(line);
			}

			String json = jsonBuilder.toString();
			parseJsonAndLoad(json, storage);

			System.out.println("✓ Loaded " + loadedCount + " bicycles from JSON file: " + filename);

		} catch (Exception e) {
			System.err.println("✗ Error loading from JSON: " + e.getMessage());
		}

		return loadedCount;
	}

	private void parseJsonAndLoad(String json, abstractStorage<Bicycle> storage) throws Exception {
		json = json.trim();

		if (!json.startsWith("{") || !json.endsWith("}")) {
			throw new IllegalArgumentException("Invalid JSON format");
		}

		int startIndex = json.indexOf("\"bicycles\":[");
		if (startIndex == -1)
			return;

		startIndex += 12;
		int endIndex = json.lastIndexOf("]");
		String bikesArray = json.substring(startIndex, endIndex);

		String[] bikeObjects = bikesArray.split("\\},\\s*\\{");

		for (String bikeJson : bikeObjects) {
			bikeJson = bikeJson.replaceFirst("\\{", "").replaceFirst("\\}$", "");
			if (!bikeJson.trim().isEmpty()) {
				Bicycle bike = parseBicycleJson("{" + bikeJson + "}");
				if (bike != null) {
					storage.add(bike);
				}
			}
		}
	}

	private Bicycle parseBicycleJson(String json) throws Exception {
		int id = extractIntFromJson(json, "id");
		String type = extractStringFromJson(json, "type");
		String model = extractStringFromJson(json, "model");
		String frameMaterial = extractStringFromJson(json, "frameMaterial");
		int wheelSize = extractIntFromJson(json, "wheelSize");
		int gears = extractIntFromJson(json, "gears");

		Date manufactureDate = null;
		String dateStr = extractStringFromJson(json, "manufactureDate");
		if (!dateStr.isEmpty()) {
			manufactureDate = DATE_FORMAT.parse(dateStr);
		}

		double price = extractDoubleFromJson(json, "price");

		return new Bicycle(id, type, model, frameMaterial, wheelSize, gears, manufactureDate, price);
	}

	private int extractIntFromJson(String json, String field) {
		String pattern = "\"" + field + "\":\\s*(\\d+)";
		java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
		java.util.regex.Matcher m = p.matcher(json);
		if (m.find()) {
			return Integer.parseInt(m.group(1));
		}
		return 0;
	}

	private double extractDoubleFromJson(String json, String field) {
		String pattern = "\"" + field + "\":\\s*(\\d+\\.\\d+)";
		java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
		java.util.regex.Matcher m = p.matcher(json);
		if (m.find()) {
			return Double.parseDouble(m.group(1));
		}
		return 0.0;
	}

	private String extractStringFromJson(String json, String field) {
		String pattern = "\"" + field + "\":\\s*\"([^\"]*)\"";
		java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
		java.util.regex.Matcher m = p.matcher(json);
		if (m.find()) {
			return m.group(1);
		}
		return "";
	}

	public void exportWithDecorator(abstractStorage<Bicycle> storage, String format, String filename) {
		List<Bicycle> bicycles = storage.get_All();

		DataWriter writer = new BaseDataWriter();

		if (format.equalsIgnoreCase("csv")) {
			writer = new CsvDataWriterDecorator(writer);
		} else if (format.equalsIgnoreCase("json")) {
			writer = new JsonDataWriterDecorator(writer);
		} else if (format.equalsIgnoreCase("all")) {
			writer = new CsvDataWriterDecorator(writer);
			writer = new JsonDataWriterDecorator(writer);
		}

		writer.write(bicycles, filename);
	}
}