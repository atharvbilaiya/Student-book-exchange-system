package bookexchange;

public class Student {
    private String studentId;
    private String name;
    private String branch;
    private int semester;
    private String contact;

    public Student(String studentId, String name, String branch, int semester, String contact) {
        this.studentId = studentId;
        this.name = name;
        this.branch = branch;
        this.semester = semester;
        this.contact = contact;
    }

    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public String getBranch() { return branch; }
    public int getSemester() { return semester; }
    public String getContact() { return contact; }

    public void setName(String name) { this.name = name; }
    public void setBranch(String branch) { this.branch = branch; }
    public void setSemester(int semester) { this.semester = semester; }
    public void setContact(String contact) { this.contact = contact; }

    @Override
    public String toString() {
        return studentId + " | " + name + " | " + branch +
               " | Sem " + semester + " | " + contact;
    }
}
