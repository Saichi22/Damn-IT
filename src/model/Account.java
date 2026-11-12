package model;

/*
 * A cashier / staff account. The security question and answer
 * are used by the Forgot Password screen.
 */
public class Account {
    private String username;
    private String password;
    private String fullName;
    private String securityQuestion;
    private String securityAnswer;

    public Account(String username, String password, String fullName,
                   String securityQuestion, String securityAnswer) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getSecurityQuestion() { return securityQuestion; }

    public void setPassword(String password) { this.password = password; }

    /* Case-insensitive, ignores extra spaces. */
    public boolean checkAnswer(String answer) {
        return securityAnswer.trim().equalsIgnoreCase(answer.trim());
    }
}