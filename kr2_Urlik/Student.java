import java.util.Date;
import java.util.List;

public class Student implements Comparable<Student> {
	private String id;
	private String lastName;
	private String firstName;
	private Date birthDate;
	private double averageScore;
	private List<Integer> subjectNumbers;

	public Student(String id, String lastName, String firstName, Date birthDate,
			double averageScore, List<Integer> subjectNumbers) {
		this.id = id;
		this.lastName = lastName;
		this.firstName = firstName;
		this.birthDate = birthDate;
		this.averageScore = averageScore;
		this.subjectNumbers = subjectNumbers;
	}

	public String getId() {
		return id;
	}

	public String getLastName() {
		return lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public double getAverageScore() {
		return averageScore;
	}

	public List<Integer> getSubjectNumbers() {
		return subjectNumbers;
	}

	@Override
	public String toString() {
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd.MM.yyyy");
		return String.format("Student{ID='%s', Name='%s %s', BirthDate=%s, Average=%.2f, Subjects=%s}",
				id, lastName, firstName, sdf.format(birthDate), averageScore, subjectNumbers);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Student student = (Student) obj;
		return id.equals(student.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public int compareTo(Student other) {
		int lastNameCompare = this.lastName.compareTo(other.lastName);
		if (lastNameCompare != 0)
			return lastNameCompare;
		return this.firstName.compareTo(other.firstName);
	}
}