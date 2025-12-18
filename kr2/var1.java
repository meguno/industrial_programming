public class var1 {
    public static void main(String[] args) {
        ClassBD classBD = new ClassBD();

        try {
            classBD.readStudentsFromFile("Student1.txt");
            classBD.readTeachersFromFile("Teacher.txt");
            classBD.populateTeachersMap();
            classBD.writeTeachersMapToFile("rezultmap.txt");
            classBD.findTeachersWithoutDebtors("rezultdebt.txt");
            classBD.writeTeachersToJson("rezjson.txt");
            classBD.archiveResultFiles(
                    new String[] { "rezultmap.txt", "rezultdebt.txt", "rezjson.txt" },
                    "results.zip");

            System.out.println("Готово!");

        } catch (Exception e) {
            System.err.println("Ошибка выполнения: " + e.getMessage());
        }
    }
}