import java.util.*;
import java.util.regex.*;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;
import java.io.*;

class StringProcessor {
	private String inputString;
	private String delimiters;
	private Random random;
	private SimpleDateFormat dateFormat;

	public StringProcessor(String inputString, String delimiters) {
		this.inputString = inputString;
		this.delimiters = delimiters;
		this.random = new Random();
		this.dateFormat = new SimpleDateFormat("dd\\MM\\yyyy");
		this.dateFormat.setLenient(false);
	}

	public void process() {
		System.out.println("\n=== PROCESSING ===");
		System.out.println("Input: " + inputString);
		System.out.println("Delimiters: " + delimiters);

		List<String> tokens = splitIntoTokens();
		List<Double> doubleNums = findDoubleNums(tokens);
		List<String> dates = findValidDates(tokens);
		String resultString = processString();

		showResults(tokens, doubleNums, dates, resultString);
	}

	public void processFromFile(String inputFilename) {
		try {
			String fileContent = readFromFile(inputFilename);
			this.inputString = fileContent;
			process();
		} catch (IOException e) {
			System.out.println("Error reading from file: " + e.getMessage());
		}
	}

	public void saveResultsToFile(String outputFilename, List<String> tokens, List<Double> numbers, List<String> dates,
			String resultString) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename));

			writer.write("=== PROCESSING RESULTS ===\n");
			writer.write("Input: " + inputString + "\n");
			writer.write("Delimiters: " + delimiters + "\n\n");

			writer.write("Tokens: " + tokens + "\n");
			writer.write("Numbers: " + numbers + "\n");
			writer.write("Dates: " + dates + "\n");
			writer.write("Final string: " + resultString + "\n\n");

			if (!dates.isEmpty()) {
				writer.write("Date formats:\n");
				try {
					String dateStr = dates.get(0);
					if (dateStr.matches("\\d{2}\\\\\\d{2}\\\\\\d{2}")) {
						dateStr = dateStr.substring(0, 6) + "20" + dateStr.substring(6);
					}
					Date date = dateFormat.parse(dateStr);
					DateFormat shortFormat = DateFormat.getDateInstance(DateFormat.SHORT);
					writer.write("Short: " + shortFormat.format(date) + "\n");
					writer.write("Custom: " + new SimpleDateFormat("dd.MM.yyyy").format(date) + "\n");
					writer.write(String.format("Formatter: %td.%<tm.%<tY\n", date));
				} catch (ParseException e) {
					writer.write("Error formatting date: " + e.getMessage() + "\n");
				}
			}

			writer.close();

			System.out.println("Results saved to: " + outputFilename);
		} catch (IOException e) {
			System.out.println("Error writing to file: " + e.getMessage());
		}
	}

	public String readFromFile(String filename) throws IOException {
		StringBuilder content = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String line;

		while ((line = reader.readLine()) != null) {
			content.append(line).append("\n");
		}
		reader.close();

		return content.toString().trim();
	}

	public void processWithFileOutput(String outputFilename) {
		System.out.println("\n=== PROCESSING ===");
		System.out.println("Input: " + inputString);
		System.out.println("Delimiters: " + delimiters);

		List<String> tokens = splitIntoTokens();
		List<Double> doubleNums = findDoubleNums(tokens);
		List<String> dates = findValidDates(tokens);
		String resultString = processString();

		showResults(tokens, doubleNums, dates, resultString);
		saveResultsToFile(outputFilename, tokens, doubleNums, dates, resultString);
	}

	public List<String> splitIntoTokens() {
		List<String> tokens = new ArrayList<>();
		StringTokenizer tokenizer = new StringTokenizer(inputString, delimiters);
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken().trim();
			if (!token.isEmpty()) {
				tokens.add(token);
			}
		}
		return tokens;
	}

	public List<Double> findDoubleNums(List<String> tokens) {
		List<Double> numbers = new ArrayList<>();
		for (String token : tokens) {
			try {
				numbers.add(Double.parseDouble(token));
			} catch (NumberFormatException e) {
			}
		}
		return numbers;
	}

	public List<String> findValidDates(List<String> tokens) {
		List<String> dates = new ArrayList<>();
		for (String token : tokens) {
			if (isValidDate(token)) {
				dates.add(token);
			}
		}
		return dates;
	}

	public boolean isValidDate(String dateStr) {
		if (!dateStr.matches("\\d{2}\\\\\\d{2}\\\\\\d{2,4}")) {
			return false;
		}
		try {
			String dateToParse = dateStr;
			if (dateStr.matches("\\d{2}\\\\\\d{2}\\\\\\d{2}")) {
				dateToParse = dateStr.substring(0, 6) + "20" + dateStr.substring(6);
			}
			Date date = dateFormat.parse(dateToParse);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	public String processString() {
		String withRandom = addRandomNumber();
		return removeSubstrings(withRandom);
	}

	public String addRandomNumber() {
		double randomValue = random.nextDouble() * 100;
		String randomNum = String.format("%.2f", randomValue);

		for (int i = 0; i < inputString.length(); i++) {
			if (inputString.charAt(i) == '.') {
				int start = i;
				while (start > 0 && Character.isDigit(inputString.charAt(start - 1))) {
					start--;
				}

				if (i + 1 < inputString.length() && Character.isDigit(inputString.charAt(i + 1))) {
					return inputString.substring(0, start) + randomNum + " " + inputString.substring(start);
				}
			}
		}

		return inputString + " " + randomNum;
	}

	public String removeSubstrings(String str) {
		String[] words = str.split("\\s+");
		String toRemove = null;

		for (String word : words) {
			if (isValidForRemoval(word)) {
				if (toRemove == null || word.length() < toRemove.length()) {
					toRemove = word;
				}
			}
		}

		if (toRemove != null) {
			String result = str.replace(toRemove, "");
			result = result.replaceAll("\\s+", " ").trim();
			return result;
		} else {
			return str;
		}
	}

	public boolean isValidForRemoval(String word) {
		if (word.length() == 0) {
			return false;
		}
		boolean startsWithDigit = Character.isDigit(word.charAt(0));
		boolean endsWithPunctuation = ".?!,;:".indexOf(word.charAt(word.length() - 1)) != -1;
		return startsWithDigit && endsWithPunctuation;
	}

	public void showResults(List<String> tokens, List<Double> numbers, List<String> dates, String resultString) {
		System.out.println("\n--- RESULTS ---");
		System.out.println("Tokens: " + tokens);
		System.out.println("Numbers: " + numbers);
		System.out.println("Dates: " + dates);

		if (!dates.isEmpty()) {
			System.out.println("\nDate formats:");
			try {
				String dateStr = dates.get(0);
				if (dateStr.matches("\\d{2}\\\\\\d{2}\\\\\\d{2}")) {
					dateStr = dateStr.substring(0, 6) + "20" + dateStr.substring(6);
				}
				Date date = dateFormat.parse(dateStr);
				DateFormat shortFormat = DateFormat.getDateInstance(DateFormat.SHORT);
				System.out.println("Short: " + shortFormat.format(date));
				System.out.println("Custom: " + new SimpleDateFormat("dd.MM.yyyy").format(date));
				System.out.printf("Formatter: %td.%<tm.%<tY\n", date);
			} catch (ParseException e) {
				System.out.println("Error formatting date: " + e.getMessage());
			}
		}

		System.out.println("\nFinal string: " + resultString);
	}
}