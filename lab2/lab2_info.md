# String Processor - Lab 2

A Java application for advanced string processing with date validation and file operations.

## Features

- **String Tokenization**: Splits input string into tokens using custom delimiters
- **Number Detection**: Identifies and extracts real numbers from tokens
- **Date Validation**: Finds and validates dates in DD\MM\YY or DD\MM\YYYY format
- **File Operations**: Read input from files and save results to files
- **String Manipulation**: Automatic random number insertion and substring removal

## How to Use

### Console Input:
1. Run the program: `java Main`
2. Choose input method (1 for console)
3. Enter your text with tokens
4. Enter delimiters
5. Choose to save results to file

### File Input:
1. Run the program: `java Main`
2. Choose input method (2 for file)
3. Enter input filename
4. Enter delimiters
5. Results will be processed and can be saved

## File Structure
- `Main.java` - Main program with user interface
- `StringProcessor.java` - Core processing logic with all operations

## Technologies Used
- Java String Processing
- StringTokenizer and Regex
- SimpleDateFormat for date validation
- File I/O with BufferedReader/Writer
- Random number generation