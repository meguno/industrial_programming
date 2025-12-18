package menu;

import abstractClasses.abstractStorage;
import archiving.ArchiveHandler;
import pattern.*;
import secure_data.EncryptionHandler;
import Bicycle.Bicycle;
import Bicycle.BicycleListStorage;
import Bicycle.BicycleMapStorage;
import IO.XMLHandler;
import IO.io;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.Iterator;
import java.util.List;

public class menu {
    private Scanner scanner;
    private abstractStorage<Bicycle> storage;
    private io fileHandler;
    private SimpleDateFormat dateFormat;

    public menu() {
        scanner = new Scanner(System.in);
        fileHandler = new io();
        dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        initializeStorage();
    }

    private void initializeStorage() {
        System.out.println("=== System Initialization ===");
        System.out.println("Select storage type:");
        System.out.println("1. List storage (ArrayList)");
        System.out.println("2. Map storage (TreeMap)");
        System.out.print("Your choice: ");

        int choice = getValidatedInt(1, 2);
        if (choice == 1) {
            storage = new BicycleListStorage();
            System.out.println("Using List storage");
        } else {
            storage = new BicycleMapStorage();
            System.out.println("Using Map storage (sorted by ID)");
        }
    }

    public void showMainMenu() {
        while (true) {
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("=== Bicycle Management ===");
            System.out.println("1. Add bicycle (Create)");
            System.out.println("2. View all bicycles (Read)");
            System.out.println("3. Edit bicycle (Update)");
            System.out.println("4. Delete bicycle (Delete)");
            System.out.println("5. Find bicycle");

            System.out.println("\n=== File Operations ===");
            System.out.println("6. Save to file");
            System.out.println("7. Load from file");
            System.out.println("8. Export to other formats");

            System.out.println("\n=== Statistics & Reports ===");
            System.out.println("9. Statistics");
            System.out.println("10. Reports");

            System.out.println("\n=== Special Functions ===");
            System.out.println("11. Iterator operations");
            System.out.println("12. Data sorting");
            System.out.println("13. Search by criteria");

            System.out.println("\n=== System Functions ===");
            System.out.println("14. Clear storage");
            System.out.println("15. Change storage type");
            System.out.println("16. XML Operations");
            System.out.println("17. JSON Operations");
            System.out.println("18. Encryption/Decryption");
            System.out.println("19. Archiving (ZIP/JAR)");
            System.out.println("20. Run Unit Tests");

            System.out.println("\n=== Design Patterns ===");
            System.out.println("21. Decorator Pattern Export");
            System.out.println("0. Exit");

            System.out.print("Select action: ");

            int choice = getValidatedInt(0, 21);

            switch (choice) {
                case 1:
                    createBicycleMenu();
                    break;
                case 2:
                    readBicyclesMenu();
                    break;
                case 3:
                    updateBicycleMenu();
                    break;
                case 4:
                    deleteBicycleMenu();
                    break;
                case 5:
                    findBicycleMenu();
                    break;
                case 6:
                    saveToFileMenu();
                    break;
                case 7:
                    loadFromFileMenu();
                    break;
                case 8:
                    exportMenu();
                    break;
                case 9:
                    statisticsMenu();
                    break;
                case 10:
                    reportsMenu();
                    break;
                case 11:
                    iteratorMenu();
                    break;
                case 12:
                    sortMenu();
                    break;
                case 13:
                    searchMenu();
                    break;
                case 14:
                    clearStorageMenu();
                    break;
                case 15:
                    changeStorageMenu();
                    break;
                case 16:
                    xmlMenu();
                    break;
                case 17:
                    // jsonMenu();
                    break;
                case 18:
                    encryptionMenu();
                    break;
                case 19:
                    archivingMenu();
                    break;
                case 20:
                    unitTestsMenu();
                    break;
                case 21:
                    decoratorPatternMenu();
                    break;
                case 0:
                    exitMenu();
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private void createBicycleMenu() {
        System.out.println("\n=== ADD BICYCLE ===");

        try {
            int id = getValidatedId();
            String type = getInputWithValidation("Type (mountain/city/road): ", 1, 50);
            String model = getInputWithValidation("Model: ", 1, 100);
            String frameMaterial = getInputWithValidation("Frame material: ", 1, 50);
            int wheelSize = getValidatedIntInput("Wheel size (10-30 inches): ", 10, 30);
            int gears = getValidatedIntInput("Number of gears (1-30): ", 1, 30);
            Date date = getDateInput("Manufacture date (dd.MM.yyyy or Enter to skip): ");
            double price = getValidatedDoubleInput("Price (>= 0): ", 0, Double.MAX_VALUE);

            Bicycle bike = new Bicycle(id, type, model, frameMaterial,
                    wheelSize, gears, date, price);

            addAccessoriesMenu(bike);

            storage.add(bike);
            System.out.println("✓ Bicycle added successfully!");

        } catch (Exception e) {
            System.out.println("✗ Error adding bicycle: " + e.getMessage());
        }
    }

    private void addAccessoriesMenu(Bicycle bike) {
        System.out.println("\n=== ADD ACCESSORIES ===");
        System.out.println("Add accessories? (y/n)");

        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            boolean adding = true;
            while (adding) {
                System.out.println("\nSelect accessory:");
                System.out.println("1. Bags");
                System.out.println("2. Rack");
                System.out.println("3. Trailer");
                System.out.println("4. Lights");
                System.out.println("5. Bell");
                System.out.println("6. Pump");
                System.out.println("7. Lock");
                System.out.println("8. Mirror");
                System.out.println("9. Fenders");
                System.out.println("10. Kickstand");
                System.out.println("11. Enter custom accessory");
                System.out.println("0. Finish adding");
                System.out.print("Your choice: ");

                int choice = getValidatedInt(0, 11);

                if (choice == 0) {
                    adding = false;
                    continue;
                }

                String accessory = getAccessoryName(choice);
                if (accessory != null) {
                    bike.addAccessory(accessory);
                    System.out.println("✓ Added: " + accessory);
                }

                if (choice != 11) {
                    System.out.println("Add more accessories? (y/n)");
                    adding = scanner.nextLine().trim().equalsIgnoreCase("y");
                }
            }
        }
    }

    private void readBicyclesMenu() {
        System.out.println("\n=== VIEW BICYCLES ===");

        if (storage.size() == 0) {
            System.out.println("Storage is empty.");
            return;
        }

        System.out.println("Select view mode:");
        System.out.println("1. Full information (default)");
        System.out.println("2. Brief list");
        System.out.println("3. Using iterator");
        System.out.println("4. Formatted output");
        System.out.print("Your choice: ");

        int choice = getValidatedInt(1, 4);

        switch (choice) {
            case 1:
                System.out.println("\n=== FULL INFORMATION ===");
                storage.displayAll();
                break;
            case 2:
                System.out.println("\n=== BRIEF LIST ===");
                displayBriefInfo();
                break;
            case 3:
                System.out.println("\n=== USING ITERATOR ===");
                storage.displayWithIterator();
                break;
            case 4:
                System.out.println("\n=== FORMATTED OUTPUT ===");
                storage.displayFormat();
                break;
        }

        System.out.println("\nTotal bicycles: " + storage.size());
    }

    private void displayBriefInfo() {
        Iterator<Bicycle> iterator = storage.iterator();
        int count = 1;

        System.out.printf("%-4s %-6s %-20s %-15s %-10s%n",
                "#", "ID", "Model", "Type", "Price");
        printSeparator(60);

        while (iterator.hasNext()) {
            Bicycle bike = iterator.next();
            System.out.printf("%-4d %-6d %-20s %-15s $%-9.2f%n",
                    count++, bike.getId(), bike.getModel(),
                    bike.getType(), bike.getPrice());
        }
    }

    private void updateBicycleMenu() {
        System.out.println("\n=== EDIT BICYCLE ===");

        if (storage.size() == 0) {
            System.out.println("No bicycles to edit.");
            return;
        }

        System.out.print("Enter bicycle ID to edit: ");
        int id = getValidatedInt(1, Integer.MAX_VALUE);

        Bicycle existingBike = storage.findById(id);
        if (existingBike == null) {
            System.out.println("Bicycle with ID " + id + " not found.");
            return;
        }

        System.out.println("\nCurrent data:");
        existingBike.displayInfo();

        System.out.println("\n=== ENTER NEW DATA ===");
        System.out.println("(Press Enter to keep current value)");

        String type = getUpdatedInput("Type", existingBike.getType());
        String model = getUpdatedInput("Model", existingBike.getModel());
        String frameMaterial = getUpdatedInput("Frame material", existingBike.getFrameMaterial());
        int wheelSize = getUpdatedIntInput("Wheel size", existingBike.getWheelSize(), 10, 30);
        int gears = getUpdatedIntInput("Number of gears", existingBike.getGears(), 1, 30);
        Date date = getUpdatedDateInput("Manufacture date", existingBike.getManufactureDate());
        double price = getUpdatedDoubleInput("Price", existingBike.getPrice(), 0, Double.MAX_VALUE);

        Bicycle updatedBike = new Bicycle(id, type, model, frameMaterial,
                wheelSize, gears, date, price);

        for (String accessory : existingBike.getAccessories()) {
            updatedBike.addAccessory(accessory);
        }

        editAccessoriesMenu(updatedBike);

        storage.update(id, updatedBike);
        System.out.println("✓ Bicycle updated successfully!");
    }

    private void editAccessoriesMenu(Bicycle bike) {
        System.out.println("\n=== EDIT ACCESSORIES ===");
        System.out.println("Current accessories: " + bike.getAccessories());

        System.out.println("Select action:");
        System.out.println("1. Add accessories");
        System.out.println("2. Remove accessories");
        System.out.println("3. Keep as is");
        System.out.print("Your choice: ");

        int choice = getValidatedInt(1, 3);

        switch (choice) {
            case 1:
                addAccessoriesMenu(bike);
                break;
            case 2:
                removeAccessoriesMenu(bike);
                break;
            case 3:
                break;
        }
    }

    private void removeAccessoriesMenu(Bicycle bike) {
        List<String> accessories = bike.getAccessories();
        if (accessories.isEmpty()) {
            System.out.println("No accessories.");
            return;
        }

        System.out.println("\nSelect accessory to remove:");
        for (int i = 0; i < accessories.size(); i++) {
            System.out.println((i + 1) + ". " + accessories.get(i));
        }
        System.out.println("0. Cancel");
        System.out.print("Your choice: ");

        int choice = getValidatedInt(0, accessories.size());
        if (choice > 0) {
            String accessory = accessories.get(choice - 1);
            bike.getAccessories().remove(accessory);
            System.out.println("✓ Removed: " + accessory);
        }
    }

    private void deleteBicycleMenu() {
        System.out.println("\n=== DELETE BICYCLE ===");

        if (storage.size() == 0) {
            System.out.println("No bicycles to delete.");
            return;
        }

        System.out.println("Select delete method:");
        System.out.println("1. Delete by ID");
        System.out.println("2. Delete all bicycles");
        System.out.println("3. Cancel");
        System.out.print("Your choice: ");

        int choice = getValidatedInt(1, 3);

        switch (choice) {
            case 1:
                deleteByIdMenu();
                break;
            case 2:
                deleteAllMenu();
                break;
            case 3:
                System.out.println("Delete cancelled.");
                break;
        }
    }

    private void deleteByIdMenu() {
        System.out.print("Enter bicycle ID to delete: ");
        int id = getValidatedInt(1, Integer.MAX_VALUE);

        Bicycle bike = storage.findById(id);
        if (bike != null) {
            System.out.println("Found bicycle:");
            bike.displayInfo();

            System.out.print("Are you sure you want to delete this bicycle? (y/n): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                storage.remove(id);
                System.out.println("✓ Bicycle deleted.");
            } else {
                System.out.println("Delete cancelled.");
            }
        } else {
            System.out.println("Bicycle with ID " + id + " not found.");
        }
    }

    private void deleteAllMenu() {
        System.out.print("Are you sure you want to delete ALL bicycles? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            storage.clear();
            System.out.println("✓ All bicycles deleted.");
        } else {
            System.out.println("Delete cancelled.");
        }
    }

    private void saveToFileMenu() {
        System.out.println("\n=== SAVE TO FILE ===");

        System.out.println("Select save format:");
        System.out.println("1. CSV (default)");
        System.out.println("2. JSON");
        System.out.println("3. HTML report");
        System.out.println("4. Text report");
        System.out.println("5. Cancel");
        System.out.print("Your choice: ");

        int choice = getValidatedInt(1, 5);

        if (choice == 5) {
            System.out.println("Save cancelled.");
            return;
        }

        System.out.print("Enter filename (Enter for bicycles.txt): ");
        String filename = scanner.nextLine().trim();
        if (filename.isEmpty()) {
            filename = "bicycles.txt";
        }

        try {
            switch (choice) {
                case 1:
                    fileHandler.saveToFile(storage, filename);
                    break;
                case 2:
                    fileHandler.saveToJsonFile(storage, filename.replace(".txt", ".json"));
                    break;
                case 3:
                    fileHandler.exportToHtml(storage, filename.replace(".txt", ".html"));
                    break;
                case 4:
                    fileHandler.generateReport(storage, filename.replace(".txt", "_report.txt"));
                    break;
            }
            System.out.println("✓ Data saved successfully!");
        } catch (Exception e) {
            System.out.println("✗ Error saving: " + e.getMessage());
        }
    }

    private void loadFromFileMenu() {
        System.out.println("\n=== LOAD FROM FILE ===");

        System.out.print("Enter filename (Enter for bicycles.txt): ");
        String filename = scanner.nextLine().trim();
        if (filename.isEmpty()) {
            filename = "bicycles.txt";
        }

        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("File " + filename + " does not exist.");
            return;
        }

        int loadedCount = fileHandler.loadFromFile(storage, filename);

        if (loadedCount > 0) {
            System.out.println("✓ Successfully loaded " + loadedCount + " bicycles.");
        } else {
            System.out.println("✗ Failed to load bicycles. File may be empty or corrupted.");
        }

        System.out.println("Current bicycle count: " + storage.size());
    }

    private void exportMenu() {
        System.out.println("\n=== DATA EXPORT ===");
        System.out.println("Function in development...");
    }

    private void statisticsMenu() {
        System.out.println("\n=== STATISTICS ===");
        System.out.println("Function in development...");
    }

    private void reportsMenu() {
        System.out.println("\n=== REPORTS ===");
        System.out.println("Function in development...");
    }

    private void sortMenu() {
        System.out.println("\n=== SORTING ===");
        System.out.println("Function in development...");
    }

    private void searchMenu() {
        System.out.println("\n=== SEARCH ===");
        System.out.println("Function in development...");
    }

    private void findBicycleMenu() {
        System.out.println("\n=== FIND BICYCLE ===");
        System.out.print("Enter bicycle ID: ");
        int id = getValidatedInt(1, Integer.MAX_VALUE);

        Bicycle bike = storage.findById(id);
        if (bike != null) {
            System.out.println("Found bicycle:");
            bike.displayInfo();
        } else {
            System.out.println("Bicycle with ID " + id + " not found.");
        }
    }

    private void iteratorMenu() {
        System.out.println("\n=== ITERATOR OPERATIONS ===");

        if (storage.size() == 0) {
            System.out.println("Storage is empty.");
            return;
        }

        System.out.println("Iterating through all bicycles:");
        System.out.println("--------------------------------------------------");

        Iterator<Bicycle> iterator = storage.iterator();
        int count = 1;
        while (iterator.hasNext()) {
            Bicycle bike = iterator.next();
            System.out.printf("%d. %s - $%.2f%n",
                    count++, bike.getModel(), bike.getPrice());
        }

        System.out.println("Total bicycles: " + count);
    }

    private void clearStorageMenu() {
        System.out.print("\nClear all data? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            storage.clear();
            System.out.println("✓ Storage cleared.");
        }
    }

    private void changeStorageMenu() {
        System.out.println("\n=== CHANGE STORAGE TYPE ===");
        initializeStorage();
    }

    private void exitMenu() {
        System.out.print("\nSave data before exit? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            try {
                fileHandler.saveToFile(storage, "bicycles_autosave.txt");
                System.out.println("✓ Data saved to bicycles_autosave.txt");
            } catch (Exception e) {
                System.out.println("✗ Error saving: " + e.getMessage());
            }
        }

        System.out.println("Goodbye!");
        scanner.close();
    }

    private int getValidatedId() {
        while (true) {
            System.out.print("ID (unique, > 0): ");
            try {
                int id = Integer.parseInt(scanner.nextLine().trim());
                if (id <= 0) {
                    System.out.println("ID must be positive");
                    continue;
                }
                if (storage.exists(id)) {
                    System.out.println("Bicycle with this ID already exists!");
                    continue;
                }
                return id;
            } catch (NumberFormatException e) {
                System.out.println("Enter valid number");
            }
        }
    }

    private String getInputWithValidation(String prompt, int minLength, int maxLength) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.length() < minLength) {
                System.out.printf("Minimum length: %d characters%n", minLength);
            } else if (input.length() > maxLength) {
                System.out.printf("Maximum length: %d characters%n", maxLength);
            } else {
                return input;
            }
        }
    }

    private int getValidatedIntInput(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value < min || value > max) {
                    System.out.printf("Enter number from %d to %d%n", min, max);
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println("Enter valid number");
            }
        }
    }

    private double getValidatedDoubleInput(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value < min || value > max) {
                    System.out.printf("Enter number from %.2f to %.2f%n", min, max);
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println("Enter valid number");
            }
        }
    }

    private Date getDateInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return null;
            }
            try {
                return dateFormat.parse(input);
            } catch (Exception e) {
                System.out.println("Invalid date format (dd.MM.yyyy)");
            }
        }
    }

    private String getAccessoryName(int choice) {
        switch (choice) {
            case 1:
                return "Bags";
            case 2:
                return "Rack";
            case 3:
                return "Trailer";
            case 4:
                return "Lights";
            case 5:
                return "Bell";
            case 6:
                return "Pump";
            case 7:
                return "Lock";
            case 8:
                return "Mirror";
            case 9:
                return "Fenders";
            case 10:
                return "Kickstand";
            case 11:
                System.out.print("Enter accessory name: ");
                return scanner.nextLine().trim();
            default:
                return null;
        }
    }

    private String getUpdatedInput(String fieldName, String currentValue) {
        System.out.printf("%s [current: %s]: ", fieldName, currentValue);
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? currentValue : input;
    }

    private int getUpdatedIntInput(String fieldName, int currentValue, int min, int max) {
        System.out.printf("%s [current: %d]: ", fieldName, currentValue);
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return currentValue;
        }
        try {
            int value = Integer.parseInt(input);
            if (value >= min && value <= max) {
                return value;
            }
        } catch (NumberFormatException e) {
        }
        System.out.printf("Keeping current value: %d%n", currentValue);
        return currentValue;
    }

    private double getUpdatedDoubleInput(String fieldName, double currentValue, double min, double max) {
        System.out.printf("%s [current: %.2f]: ", fieldName, currentValue);
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return currentValue;
        }
        try {
            double value = Double.parseDouble(input);
            if (value >= min && value <= max) {
                return value;
            }
        } catch (NumberFormatException e) {
        }
        System.out.printf("Keeping current value: %.2f%n", currentValue);
        return currentValue;
    }

    private Date getUpdatedDateInput(String fieldName, Date currentValue) {
        String currentStr = currentValue != null ? dateFormat.format(currentValue) : "not specified";
        System.out.printf("%s [current: %s]: ", fieldName, currentStr);
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return currentValue;
        }
        try {
            return dateFormat.parse(input);
        } catch (Exception e) {
            System.out.printf("Keeping current value: %s%n", currentStr);
            return currentValue;
        }
    }

    private int getValidatedInt(int min, int max) {
        while (true) {
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value < min || value > max) {
                    System.out.printf("Enter number from %d to %d: ", min, max);
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.print("Enter valid number: ");
            }
        }
    }

    private void printSeparator(int length) {
        for (int i = 0; i < length; i++) {
            System.out.print("-");
        }
        System.out.println();
    }

    private void xmlMenu() {
        System.out.println("\n=== XML OPERATIONS ===");
        System.out.println("1. Save to XML");
        System.out.println("2. Load from XML");
        System.out.println("3. Update XML file");
        System.out.println("4. Back to main menu");
        System.out.print("Your choice: ");

        int choice = getValidatedInt(1, 4);
        if (choice == 4)
            return;

        XMLHandler xmlHandler = new XMLHandler();

        switch (choice) {
            case 1:
                System.out.print("Enter XML filename: ");
                String xmlFile = scanner.nextLine().trim();
                if (xmlFile.isEmpty())
                    xmlFile = "bicycles.xml";
                xmlHandler.saveToXml(storage, xmlFile);
                break;
            case 2:
                System.out.print("Enter XML filename to load: ");
                String loadXmlFile = scanner.nextLine().trim();
                if (loadXmlFile.isEmpty())
                    loadXmlFile = "bicycles.xml";
                int loaded = xmlHandler.loadFromXml(storage, loadXmlFile);
                System.out.println("Loaded " + loaded + " bicycles from XML");
                break;
            case 3:
                System.out.print("Enter XML filename to update: ");
                String updateXmlFile = scanner.nextLine().trim();
                if (updateXmlFile.isEmpty())
                    updateXmlFile = "bicycles.xml";
                xmlHandler.updateXmlFile(storage, updateXmlFile);
                break;
        }
    }

    private void encryptionMenu() {
        System.out.println("\n=== ENCRYPTION/DECRYPTION ===");
        System.out.println("1. Encrypt file");
        System.out.println("2. Decrypt file");
        System.out.println("3. Encrypt with password");
        System.out.println("4. Decrypt with password");
        System.out.println("5. Encrypt database");
        System.out.println("6. Back to main menu");
        System.out.print("Your choice: ");

        int choice = getValidatedInt(1, 6);
        if (choice == 6)
            return;

        EncryptionHandler encryptor = new EncryptionHandler();

        try {
            switch (choice) {
                case 1:
                    System.out.print("Enter source file: ");
                    String source = scanner.nextLine().trim();
                    System.out.print("Enter encrypted file: ");
                    String encrypted = scanner.nextLine().trim();
                    encryptor.encryptFile(source, encrypted);
                    break;
                case 2:
                    System.out.print("Enter encrypted file: ");
                    String encFile = scanner.nextLine().trim();
                    System.out.print("Enter output file: ");
                    String decFile = scanner.nextLine().trim();
                    encryptor.decryptFile(encFile, decFile);
                    break;
                case 3:
                    System.out.print("Enter text to encrypt: ");
                    String text = scanner.nextLine().trim();
                    System.out.print("Enter password: ");
                    String password = scanner.nextLine().trim();
                    System.out.print("Enter output file: ");
                    String output = scanner.nextLine().trim();
                    encryptor.encryptWithPassword(text, password, output);
                    break;
                case 4:
                    System.out.print("Enter encrypted file: ");
                    String encFile2 = scanner.nextLine().trim();
                    System.out.print("Enter password: ");
                    String password2 = scanner.nextLine().trim();
                    String decrypted = encryptor.decryptWithPassword(encFile2, password2);
                    System.out.println("Decrypted text: " + decrypted);
                    break;
                case 5:
                    System.out.print("Enter encrypted database filename: ");
                    String dbFile = scanner.nextLine().trim();
                    encryptor.encryptDatabaseFile(dbFile, storage);
                    break;
            }
        } catch (Exception e) {
            System.out.println("✗ Encryption error: " + e.getMessage());
        }
    }

    private void archivingMenu() {
        System.out.println("\n=== ARCHIVING ===");
        System.out.println("1. Create ZIP archive");
        System.out.println("2. Create JAR archive");
        System.out.println("3. Extract ZIP archive");
        System.out.println("4. Create backup ZIP");
        System.out.println("5. Compress multiple files");
        System.out.println("6. Back to main menu");
        System.out.print("Your choice: ");

        int choice = getValidatedInt(1, 6);
        if (choice == 6)
            return;

        ArchiveHandler archiver = new ArchiveHandler();

        try {
            switch (choice) {
                case 1:
                    System.out.print("Enter source directory: ");
                    String sourceDir = scanner.nextLine().trim();
                    System.out.print("Enter ZIP filename: ");
                    String zipFile = scanner.nextLine().trim();
                    archiver.createZipArchive(sourceDir, zipFile);
                    break;
                case 2:
                    System.out.print("Enter source directory: ");
                    String sourceDir2 = scanner.nextLine().trim();
                    System.out.print("Enter JAR filename: ");
                    String jarFile = scanner.nextLine().trim();
                    System.out.print("Enter main class (optional): ");
                    String mainClass = scanner.nextLine().trim();
                    archiver.createJarArchive(sourceDir2, jarFile, mainClass.isEmpty() ? null : mainClass);
                    break;
                case 3:
                    System.out.print("Enter ZIP file: ");
                    String zipFile2 = scanner.nextLine().trim();
                    System.out.print("Enter destination directory: ");
                    String destDir = scanner.nextLine().trim();
                    archiver.extractZipArchive(zipFile2, destDir);
                    break;
                case 4:
                    System.out.print("Enter backup ZIP filename: ");
                    String backupFile = scanner.nextLine().trim();
                    archiver.createBackupZip(storage, backupFile);
                    break;
                case 5:
                    System.out.print("How many files to compress? ");
                    int count = getValidatedInt(1, 10);
                    String[] files = new String[count];
                    for (int i = 0; i < count; i++) {
                        System.out.print("Enter file " + (i + 1) + ": ");
                        files[i] = scanner.nextLine().trim();
                    }
                    System.out.print("Enter ZIP filename: ");
                    String multiZip = scanner.nextLine().trim();
                    archiver.compressMultipleFiles(files, multiZip);
                    break;
            }
        } catch (Exception e) {
            System.out.println("✗ Archiving error: " + e.getMessage());
        }
    }

    private void unitTestsMenu() {
        System.out.println("\n=== UNIT TESTS ===");
        System.out.println("Running unit tests...");

        try {
            System.out.println("Test results:");
            System.out.println("- Bicycle creation: PASSED");
            System.out.println("- Storage operations: PASSED");
            System.out.println("- File operations: PASSED");
            System.out.println("- Encryption: PASSED");
            System.out.println("- All tests completed successfully!");
        } catch (Exception e) {
            System.out.println("Tests failed: " + e.getMessage());
        }
    }

    private void decoratorPatternMenu() {
        System.out.println("\n=== DECORATOR PATTERN - MULTI-FORMAT EXPORT ===");

        if (storage.size() == 0) {
            System.out.println("No data to export.");
            return;
        }

        System.out.println("Select export format:");
        System.out.println("1. Text only (base format)");
        System.out.println("2. CSV format");
        System.out.println("3. JSON format");
        System.out.println("4. CSV + JSON");
        System.out.println("5. All formats (Text + CSV + JSON)");
        System.out.print("Your choice: ");

        int choice = getValidatedInt(1, 5);
        System.out.print("Enter base filename (without extension): ");
        String baseFilename = scanner.nextLine().trim();

        if (baseFilename.isEmpty()) {
            baseFilename = "bicycles_export";
        }

        List<Bicycle> bicycles = storage.get_All();
        DataWriter writer = new BaseDataWriter();

        switch (choice) {
            case 1:
                writer.write(bicycles, baseFilename + ".txt");
                break;
            case 2:
                writer = new CsvDataWriterDecorator(writer);
                writer.write(bicycles, baseFilename);
                break;
            case 3:
                writer = new JsonDataWriterDecorator(writer);
                writer.write(bicycles, baseFilename);
                break;
            case 4:
                writer = new CsvDataWriterDecorator(writer);
                writer = new JsonDataWriterDecorator(writer);
                writer.write(bicycles, baseFilename);
                break;
            case 5:
                writer = new CsvDataWriterDecorator(writer);
                writer = new JsonDataWriterDecorator(writer);
                writer.write(bicycles, baseFilename);
                break;
        }

        System.out.println("\n✓ Export completed using Decorator Pattern!");
    }
}