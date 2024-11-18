package models.users;

public class Student extends User {
    public Student(int id, String firstName, String lastName, String username, String password, String role) {
        super(id, firstName, lastName, username, password, role);
    }
}
