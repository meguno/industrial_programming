public class TeacherInfo {
	private String lastName;
	private int subjectCode;
	private int studentsCount;

	public TeacherInfo(String lastName, int subjectCode, int studentsCount) {
		this.lastName = lastName;
		this.subjectCode = subjectCode;
		this.studentsCount = studentsCount;
	}

	public String getLastName() {
		return lastName;
	}

	public int getSubjectCode() {
		return subjectCode;
	}

	public int getStudentsCount() {
		return studentsCount;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setSubjectCode(int subjectCode) {
		this.subjectCode = subjectCode;
	}

	public void setStudentsCount(int studentsCount) {
		this.studentsCount = studentsCount;
	}

	@Override
	public String toString() {
		return String.format("TeacherInfo{LastName='%s', Subject=%d, Students=%d}",
				lastName, subjectCode, studentsCount);
	}
}