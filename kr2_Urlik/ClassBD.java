
// ClassBD.java
import java.io.*;
import java.nio.file.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import java.util.zip.*;

public class ClassBD {
	private List<Student> students;
	private Map<String, TeacherInfo> teachers;

	public ClassBD() {
		students = new ArrayList<>();
		teachers = new HashMap<>();
	}

	private Student parseStudentLine(String line) {
		try {

			Pattern idPattern = Pattern.compile("^(\\d+)");
			Matcher idMatcher = idPattern.matcher(line);
			String id = "";
			if (idMatcher.find()) {
				id = idMatcher.group(1);
				line = line.substring(id.length());
			} else {
				return null;
			}

			line = line.replaceAll("[_;?]+", " ");

			Pattern namePattern = Pattern.compile("([a-zA-Zа-яА-Я]+)\\s+([a-zA-Zа-яА-Я]+)");
			Matcher nameMatcher = namePattern.matcher(line);
			String lastName = "";
			String firstName = "";

			if (nameMatcher.find()) {
				lastName = nameMatcher.group(1);
				firstName = nameMatcher.group(2);
				line = line.substring(nameMatcher.end());
			}

			Pattern datePattern = Pattern.compile("(\\d{1,2})[.,]\\s*(\\d{1,2})[.,]\\s*(\\d{4})");
			Matcher dateMatcher = datePattern.matcher(line);
			Date birthDate = null;

			if (dateMatcher.find()) {
				try {
					int day = Integer.parseInt(dateMatcher.group(1));
					int month = Integer.parseInt(dateMatcher.group(2));
					int year = Integer.parseInt(dateMatcher.group(3));

					if (month > 12 && day <= 12) {
						int temp = day;
						day = month;
						month = temp;
					}
					if (month > 12)
						month = 12;
					if (month < 1)
						month = 1;
					if (day > 31)
						day = 31;
					if (day < 1)
						day = 1;

					String dateStr = String.format("%02d.%02d.%d", day, month, year);
					SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
					sdf.setLenient(false);
					birthDate = sdf.parse(dateStr);

					line = line.substring(dateMatcher.end());
				} catch (Exception e) {
					System.err.println("Ошибка парсинга даты: " + dateMatcher.group());
				}
			}

			Pattern scorePattern = Pattern.compile("([\\d.]+[eE]?[-+]?\\d*)");
			Matcher scoreMatcher = scorePattern.matcher(line);
			double averageScore = 0.0;

			if (scoreMatcher.find()) {
				try {
					String scoreStr = scoreMatcher.group(1).toLowerCase().replace(',', '.');
					if (scoreStr.contains("e")) {
						String[] parts = scoreStr.split("e");
						double mantissa = Double.parseDouble(parts[0]);
						int exponent = Integer.parseInt(parts[1].replace("+", "").replace("-", ""));
						if (parts[1].contains("-")) {
							averageScore = mantissa * Math.pow(10, -exponent);
						} else {
							averageScore = mantissa * Math.pow(10, exponent);
						}
					} else {
						averageScore = Double.parseDouble(scoreStr);
					}
					line = line.substring(scoreMatcher.end());
				} catch (Exception e) {
					System.err.println("Ошибка парсинга балла: " + scoreMatcher.group(1));
				}
			}

			Pattern subjectPattern = Pattern.compile("\\d+");
			Matcher subjectMatcher = subjectPattern.matcher(line);
			List<Integer> subjects = new ArrayList<>();

			while (subjectMatcher.find()) {
				try {
					int subject = Integer.parseInt(subjectMatcher.group());
					subjects.add(subject);
				} catch (Exception e) {
				}
			}

			if (!id.isEmpty() && !lastName.isEmpty() && birthDate != null) {
				return new Student(id, lastName, firstName, birthDate, averageScore, subjects);
			}

		} catch (Exception e) {
			System.err.println("Ошибка при парсинге строки: " + line);
			e.printStackTrace();
		}

		return null;
	}

	public void readStudentsFromFile(String filename) throws IOException {
		List<String> lines = Files.readAllLines(Paths.get(filename));
		int successCount = 0;
		int errorCount = 0;

		System.out.println("\nЧтение студентов из файла " + filename + ":");
		System.out.println("Всего строк: " + lines.size());

		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i).trim();
			if (line.isEmpty())
				continue;

			System.out.printf("\nСтрока %2d: %s", i + 1, line);

			Student student = parseStudentLine(line);
			if (student != null) {
				students.add(student);
				successCount++;
				System.out.printf(" -> УСПЕХ: %s %s", student.getLastName(), student.getFirstName());
			} else {
				errorCount++;
				System.out.printf(" -> ОШИБКА парсинга");
			}
		}

		Collections.sort(students);

		System.out.println("\n\nИтого:");
		System.out.println("  Успешно прочитано: " + successCount);
		System.out.println("  Ошибок парсинга: " + errorCount);
		System.out.println("  Всего студентов в базе: " + students.size());
	}

	public void readTeachersFromFile(String filename) throws IOException {
		if (!Files.exists(Paths.get(filename))) {
			System.err.println("Файл " + filename + " не найден!");
			return;
		}

		List<String> lines = Files.readAllLines(Paths.get(filename));
		int count = 0;

		System.out.println("\nЧтение преподавателей из файла " + filename + ":");

		for (String line : lines) {
			line = line.trim();
			if (line.isEmpty())
				continue;

			String[] parts = line.split("\\s+");
			if (parts.length >= 2) {
				String lastName = parts[0];
				try {
					int subjectCode = Integer.parseInt(parts[1]);
					teachers.put(lastName, new TeacherInfo(lastName, subjectCode, 0));
					count++;
					System.out.printf("  Преподаватель: %-15s -> Предмет: %d%n", lastName, subjectCode);
				} catch (NumberFormatException e) {
					System.err.println("Ошибка в строке преподавателя: " + line);
				}
			}
		}

		System.out.println("Прочитано преподавателей: " + count);
	}

	public void populateTeachersMap() {
		Map<Integer, Integer> subjectStats = new HashMap<>();

		System.out.println("\nСтатистика по предметам:");
		for (Student student : students) {
			for (Integer subject : student.getSubjectNumbers()) {
				subjectStats.put(subject, subjectStats.getOrDefault(subject, 0) + 1);
			}
		}

		List<Integer> subjects = new ArrayList<>(subjectStats.keySet());
		Collections.sort(subjects);
		for (Integer subject : subjects) {
			System.out.printf("  Предмет %d: %d студентов%n", subject, subjectStats.get(subject));
		}

		for (Map.Entry<String, TeacherInfo> entry : teachers.entrySet()) {
			TeacherInfo teacher = entry.getValue();
			int subjectCode = teacher.getSubjectCode();
			int studentCount = subjectStats.getOrDefault(subjectCode, 0);
			teacher.setStudentsCount(studentCount);
		}

		System.out.println("\nОбновлена информация о " + teachers.size() + " преподавателях");
	}

	public void writeTeachersMapToFile(String filename) throws IOException {
		try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
			writer.println("ФИО преподавателя          | Код предмета | Количество студентов");
			writer.println("---------------------------+--------------+---------------------");

			List<Map.Entry<String, TeacherInfo>> sortedTeachers = new ArrayList<>(teachers.entrySet());
			sortedTeachers.sort(Map.Entry.comparingByKey());

			for (Map.Entry<String, TeacherInfo> entry : sortedTeachers) {
				TeacherInfo teacher = entry.getValue();
				writer.printf("%-25s | %-12d | %-19d%n",
						teacher.getLastName(),
						teacher.getSubjectCode(),
						teacher.getStudentsCount());
			}

			writer.println("\nВсего преподавателей: " + teachers.size());
			writer.println("Всего студентов в базе: " + students.size());
		}

		System.out.println("\nРезультаты записаны в файл: " + filename);
	}

	public void findTeachersWithoutDebtors(String filename) throws IOException {
		try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
			writer.println("ПРЕПОДАВАТЕЛИ БЕЗ СТУДЕНТОВ-ЗАДОЛЖНИКОВ");
			writer.println("=========================================\n");

			int totalStudents = students.size();
			if (totalStudents == 0) {
				writer.println("В базе нет студентов!");
				return;
			}

			int totalSubjectsPassed = 0;
			for (Student student : students) {
				totalSubjectsPassed += student.getSubjectNumbers().size();
			}

			writer.println("Общая статистика:");
			writer.println("  Всего студентов: " + totalStudents);
			writer.println("  Всего сдано предметов: " + totalSubjectsPassed);
			writer.printf("  Среднее количество предметов на студента: %.1f%n%n",
					(double) totalSubjectsPassed / totalStudents);

			writer.println("Преподаватели (отсортировано по количеству студентов):");
			writer.println("------------------------------------------------------");

			List<Map.Entry<String, TeacherInfo>> sortedTeachers = new ArrayList<>(teachers.entrySet());
			sortedTeachers.sort((a, b) -> Integer.compare(b.getValue().getStudentsCount(),
					a.getValue().getStudentsCount()));

			boolean foundWithoutDebtors = false;
			for (Map.Entry<String, TeacherInfo> entry : sortedTeachers) {
				TeacherInfo teacher = entry.getValue();
				int studentCount = teacher.getStudentsCount();
				double percentage = (double) studentCount / totalStudents * 100;

				if (percentage >= 70.0) {
					writer.printf("✓ %-15s (предмет %d): %d студентов (%.1f%%)%n",
							teacher.getLastName(),
							teacher.getSubjectCode(),
							studentCount,
							percentage);
					foundWithoutDebtors = true;
				}
			}

			if (!foundWithoutDebtors) {
				writer.println("\nНет преподавателей без задолжников (все имеют менее 70% сдавших)");
			}
		}

		System.out.println("Результаты записаны в файл: " + filename);
	}

	public void writeTeachersToJson(String filename) throws IOException {
		try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
			writer.println("{");
			writer.println("  \"metadata\": {");
			writer.println("    \"generated\": \"" + new Date() + "\",");
			writer.println("    \"totalStudents\": " + students.size() + ",");
			writer.println("    \"totalTeachers\": " + teachers.size());
			writer.println("  },");
			writer.println("  \"teachers\": [");

			List<Map.Entry<String, TeacherInfo>> sortedTeachers = new ArrayList<>(teachers.entrySet());
			sortedTeachers.sort(Map.Entry.comparingByKey());

			List<String> teacherEntries = new ArrayList<>();
			for (Map.Entry<String, TeacherInfo> entry : sortedTeachers) {
				TeacherInfo teacher = entry.getValue();
				String entryStr = String.format(
						"    {\n" +
								"      \"lastName\": \"%s\",\n" +
								"      \"subjectCode\": %d,\n" +
								"      \"studentsCount\": %d\n" +
								"    }",
						teacher.getLastName(), teacher.getSubjectCode(), teacher.getStudentsCount());
				teacherEntries.add(entryStr);
			}

			writer.println(String.join(",\n", teacherEntries));
			writer.println("  ]");
			writer.println("}");
		}

		System.out.println("JSON данные записаны в файл: " + filename);
	}

	public void archiveResultFiles(String[] filesToArchive, String zipFilename) throws IOException {
		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFilename))) {
			byte[] buffer = new byte[1024];

			System.out.println("\nСоздание архива " + zipFilename + ":");

			for (String filename : filesToArchive) {
				File file = new File(filename);
				if (file.exists()) {
					try (FileInputStream fis = new FileInputStream(file)) {
						ZipEntry zipEntry = new ZipEntry(filename);
						zos.putNextEntry(zipEntry);

						int length;
						while ((length = fis.read(buffer)) > 0) {
							zos.write(buffer, 0, length);
						}

						zos.closeEntry();
						System.out.println("  + " + filename + " добавлен в архив");
					}
				} else {
					System.out.println("  - Файл " + filename + " не найден (пропущен)");
				}
			}
		}

		System.out.println("Архив успешно создан: " + zipFilename);
	}

	public void printStudentSummary() {
		System.out.println("\n=== КРАТКАЯ ИНФОРМАЦИЯ О СТУДЕНТАХ ===");
		System.out.printf("Всего студентов: %d%n", students.size());

		Map<String, Integer> lastNameCount = new HashMap<>();
		for (Student student : students) {
			String lastName = student.getLastName();
			lastNameCount.put(lastName, lastNameCount.getOrDefault(lastName, 0) + 1);
		}

		System.out.println("\nРаспределение по фамилиям:");
		for (Map.Entry<String, Integer> entry : lastNameCount.entrySet()) {
			System.out.printf("  %-15s: %d студент(ов)%n", entry.getKey(), entry.getValue());
		}
	}

	public List<Student> getStudents() {
		return students;
	}

	public Map<String, TeacherInfo> getTeachers() {
		return teachers;
	}
}