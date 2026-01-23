package pattern;

import Bicycle.Bicycle;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class HtmlDataWriterDecorator extends DataWriterDecorator {
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

	public HtmlDataWriterDecorator(DataWriter writer) {
		super(writer);
	}

	@Override
	public void write(List<Bicycle> bicycles, String filename) {
		String htmlFilename = filename.endsWith(".html") ? filename : filename + ".html";

		try (PrintWriter writer = new PrintWriter(new FileWriter(htmlFilename))) {
			writer.println("<!DOCTYPE html>");
			writer.println("<html lang=\"en\">");
			writer.println("<head>");
			writer.println("    <meta charset=\"UTF-8\">");
			writer.println("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
			writer.println("    <title>Bicycle Catalog</title>");
			writer.println("    <style>");
			writer.println("        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }");
			writer.println(
					"        .container { max-width: 1200px; margin: 0 auto; background: white; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }");
			writer.println(
					"        h1 { color: #2c3e50; text-align: center; border-bottom: 2px solid #3498db; padding-bottom: 10px; }");
			writer.println(
					"        .stats { background: #ecf0f1; padding: 15px; border-radius: 5px; margin-bottom: 20px; }");
			writer.println("        table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
			writer.println("        th { background-color: #3498db; color: white; padding: 12px; text-align: left; }");
			writer.println("        td { padding: 10px; border-bottom: 1px solid #ddd; }");
			writer.println("        tr:hover { background-color: #f9f9f9; }");
			writer.println(
					"        .footer { text-align: center; margin-top: 30px; color: #7f8c8d; font-size: 0.9em; }");
			writer.println("        .price { font-weight: bold; color: #27ae60; }");
			writer.println("    </style>");
			writer.println("</head>");
			writer.println("<body>");
			writer.println("    <div class=\"container\">");
			writer.println("        <h1>🚲 Bicycle Catalog</h1>");

			writer.println("        <div class=\"stats\">");
			writer.println("            <strong>Total bicycles:</strong> " + bicycles.size());
			writer.println("        </div>");

			writer.println("        <table>");
			writer.println("            <thead>");
			writer.println("                <tr>");
			writer.println("                    <th>ID</th>");
			writer.println("                    <th>Type</th>");
			writer.println("                    <th>Model</th>");
			writer.println("                    <th>Frame Material</th>");
			writer.println("                    <th>Wheel Size</th>");
			writer.println("                    <th>Gears</th>");
			writer.println("                    <th>Manufacture Date</th>");
			writer.println("                    <th>Price</th>");
			writer.println("                    <th>Accessories</th>");
			writer.println("                </tr>");
			writer.println("            </thead>");
			writer.println("            <tbody>");

			double totalPrice = 0;
			for (Bicycle bike : bicycles) {
				totalPrice += bike.getPrice();
				String dateStr = bike.getManufactureDate() != null ? dateFormat.format(bike.getManufactureDate())
						: "N/A";
				String accessories = bike.getAccessories().isEmpty() ? "None"
						: String.join(", ", bike.getAccessories());

				writer.println("                <tr>");
				writer.println("                    <td>" + bike.getId() + "</td>");
				writer.println("                    <td>" + escapeHtml(bike.getType()) + "</td>");
				writer.println("                    <td>" + escapeHtml(bike.getModel()) + "</td>");
				writer.println("                    <td>" + escapeHtml(bike.getFrameMaterial()) + "</td>");
				writer.println("                    <td>" + bike.getWheelSize() + "\"</td>");
				writer.println("                    <td>" + bike.getGears() + "</td>");
				writer.println("                    <td>" + dateStr + "</td>");
				writer.println(
						"                    <td class=\"price\">$" + String.format("%.2f", bike.getPrice()) + "</td>");
				writer.println("                    <td>" + escapeHtml(accessories) + "</td>");
				writer.println("                </tr>");
			}

			writer.println("            </tbody>");
			writer.println("        </table>");

			writer.println("        <div class=\"stats\">");
			writer.println("            <strong>Total value:</strong> $" + String.format("%.2f", totalPrice));
			writer.println("            <strong>Average price:</strong> $" +
					String.format("%.2f", bicycles.isEmpty() ? 0 : totalPrice / bicycles.size()));
			writer.println("        </div>");

			writer.println("        <div class=\"footer\">");
			writer.println("            <p>Generated on: " + new java.util.Date() + "</p>");
			writer.println("        </div>");
			writer.println("    </div>");
			writer.println("</body>");
			writer.println("</html>");

			System.out.println("✓ HTML report written to: " + htmlFilename);

			super.write(bicycles, filename.replace(".html", "_summary.txt"));

		} catch (IOException e) {
			System.err.println("✗ Error writing HTML: " + e.getMessage());
		}
	}

	private String escapeHtml(String value) {
		if (value == null)
			return "";
		return value.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&#39;");
	}
}