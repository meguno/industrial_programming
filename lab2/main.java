import java.util.Scanner;
import java.util.List;

public class main {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		System.out.println("Choose input method:");
		System.out.println("1 - Console input");
		System.out.println("2 - File input");
		System.out.print("Your choice: ");
		int choice = scanner.nextInt();
		scanner.nextLine();

		StringProcessor processor;
		processor = new StringProcessor("", "");

		if (choice == 2) {
			System.out.print("Enter input filename: ");
			String inputFilename = scanner.nextLine();
			System.out.print("Enter delimiters: ");
			String delimiters = scanner.nextLine();

			processor = new StringProcessor("", delimiters);
			processor.processFromFile(inputFilename);
		} else if (choice == 1) {
			System.out.print("Enter tokens string: ");
			String tokensString = scanner.nextLine();
			System.out.print("Enter delimiters: ");
			String delimitersString = scanner.nextLine();

			processor = new StringProcessor(tokensString, delimitersString);
			processor.process();
		} else {
			System.out.print("Wrong input");
		}

		System.out.print("\nSave results to file? (y/n): ");
		String saveChoice = scanner.nextLine();
		if (saveChoice.equalsIgnoreCase("y")) {
			System.out.print("Enter output filename: ");
			String outputFilename = scanner.nextLine();

			List<String> tokens = processor.splitIntoTokens();
			List<Double> numbers = processor.findDoubleNums(tokens);
			List<String> dates = processor.findValidDates(tokens);
			String resultString = processor.processString();

			processor.saveResultsToFile(outputFilename, tokens, numbers, dates, resultString);
		}

		scanner.close();
	}
}