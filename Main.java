import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {

	static class DayForecast {
		private LocalDate date;
		private int temperature;
		private String weather;

		public DayForecast(LocalDate date, int temperature, String weather) {
			this.date = date;
			this.temperature = temperature;
			this.weather = weather;
		}

		public LocalDate getDate() {
			return date;
		}

		public int getTemperature() {
			return temperature;
		}

		public String getWeather() {
			return weather;
		}

		@Override
		public String toString() {
			return "Data: " + date + ", Temperature: " + temperature + ", Weather: " + weather;
		}
	}

	private static Map<String, List<DayForecast>> weatherData = new HashMap<>();
	private static Set<String> citiesWithWarnings = new HashSet<>();

	public static void main(String[] args) {
		try {
			loadFromXml("weather.xml");

			String city = "Minsk";
			LocalDate start = LocalDate.parse("2026-01-21");
			LocalDate end = LocalDate.parse("2026-01-25");
			calculateAverageTemp(city, start, end);

			findWarmestDay();

			printWarnings();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void loadFromXml(String filePath) throws Exception {
		File inputFile = new File(filePath);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(inputFile);
		doc.getDocumentElement().normalize();

		NodeList cityList = doc.getElementsByTagName("city");

		for (int i = 0; i < cityList.getLength(); i++) {
			Node cityNode = cityList.item(i);

			if (cityNode.getNodeType() == Node.ELEMENT_NODE) {
				Element cityElement = (Element) cityNode;
				String cityName = cityElement.getAttribute("name");
				String hasWarning = cityElement.getAttribute("warnings");

				if ("yes".equalsIgnoreCase(hasWarning)) {
					citiesWithWarnings.add(cityName);
				}

				List<DayForecast> forecasts = new ArrayList<>();
				NodeList dayList = cityElement.getElementsByTagName("day");

				for (int j = 0; j < dayList.getLength(); j++) {
					Node dayNode = dayList.item(j);
					if (dayNode.getNodeType() == Node.ELEMENT_NODE) {
						Element dayElement = (Element) dayNode;

						String dateStr = dayElement.getAttribute("date");
						String tempStr = dayElement.getElementsByTagName("temp").item(0).getTextContent();
						String weatherStr = dayElement.getElementsByTagName("weather").item(0).getTextContent();

						LocalDate date = LocalDate.parse(dateStr);
						int temp = Integer.parseInt(tempStr);

						forecasts.add(new DayForecast(date, temp, weatherStr));
					}
				}
				weatherData.put(cityName, forecasts);
			}
		}
	}

	private static void calculateAverageTemp(String city, LocalDate start, LocalDate end) {
		System.out.println("\nAverage temperature for " + city + ": ");
		List<DayForecast> days = weatherData.get(city);

		if (days == null) {
			System.out.println("City not found.");
			return;
		}

		double sumTemp = 0;
		int count = 0;

		for (DayForecast day : days) {
			if (!day.getDate().isBefore(start) && !day.getDate().isAfter(end)) {
				sumTemp += day.getTemperature();
				count++;
			}
		}

		if (count > 0) {
			System.out.printf("Period: %s - %s. Average temperature: %.2f%n", start, end, (sumTemp / count));
		} else {
			System.out.println("No data for this period.");
		}
	}

	private static void findWarmestDay() {
		System.out.println("\nThe warmest day across all cities:");
		DayForecast warmestDay = null;
		String cityWithWarmestDay = "";

		for (Map.Entry<String, List<DayForecast>> entry : weatherData.entrySet()) {
			for (DayForecast day : entry.getValue()) {
				if (warmestDay == null || day.getTemperature() > warmestDay.getTemperature()) {
					warmestDay = day;
					cityWithWarmestDay = entry.getKey();
				}
			}
		}

		if (warmestDay != null) {
			System.out.println("City: " + cityWithWarmestDay + " | " + warmestDay);
		}
	}

	private static void printWarnings() {
		System.out.println("\nCities with weather warnings:");

		for (String city : citiesWithWarnings) {
			List<DayForecast> days = weatherData.get(city);
			if (days != null) {
				System.out.println("City: " + city + " (WARNING!)");
				for (DayForecast day : days) {
					System.out.println("  -> " + day.getDate());
				}
			}
		}
	}
}