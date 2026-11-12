package ui;

import javax.swing.*;
import exception.AuthException;
import data.DataStore;
import model.Account;

public class SignUpPanel extends FormPanel {
    private final AuthFrame frame;
    private final JTextField txtName = new JTextField();
    private final JTextField txtUser = new JTextField();
    private final JPasswordField txtPass = new JPasswordField();
    private final JPasswordField txtConfirm = new JPasswordField();
    private final JComboBox<String> cboQuestion = new JComboBox<>(DataStore.SECURITY_QUESTIONS);
    private final JTextField txtAnswer = new JTextField();

    public SignUpPanel(AuthFrame frame) {
        super("SIGN UP");
        this.frame = frame;

        UITheme.styleField(txtName);
        UITheme.styleField(txtUser);
        UITheme.styleField(txtPass);
        UITheme.styleField(txtConfirm);
        UITheme.styleField(txtAnswer);
        UITheme.styleCombo(cboQuestion);

        addField("Full name", txtName);
        addField("Username", txtUser);
        addField("Password", txtPass);
        addField("Confirm password", txtConfirm);
        addField("Security question", cboQuestion);
        addField("Answer", txtAnswer);
        addGap(8);

        BlackButton btn = new BlackButton("CREATE ACCOUNT");
        btn.addActionListener(e -> doSignUp());
        addComponent(btn);
        addGap(16);

        addComponent(UITheme.link("ALREADY HAVE AN ACCOUNT? LOG IN", () -> frame.showCard(AuthFrame.LOGIN)));
    }

    private void doSignUp() {
        try {
            String name = txtName.getText().trim();
            String user = txtUser.getText().trim();
            String pass = new String(txtPass.getPassword());
            String confirm = new String(txtConfirm.getPassword());
            String answer = txtAnswer.getText().trim();

            /* Validation: each failed rule throws an AuthException with a clear message. */
            if (name.isEmpty() || user.isEmpty() || pass.isEmpty() || answer.isEmpty()) {
                throw new AuthException("Fill in all fields.");
            }
            if (!user.matches("[A-Za-z0-9_]{4,15}")) {
                throw new AuthException("Username must be 4-15 characters: letters, numbers or underscore.");
            }
            if (pass.length() < 6) {
                throw new AuthException("Password must be at least 6 characters.");
            }
            if (!pass.equals(confirm)) {
                throw new AuthException("Passwords do not match.");
            }

            DataStore.register(new Account(user, pass, name,
                    (String) cboQuestion.getSelectedItem(), answer));

            JOptionPane.showMessageDialog(this, "Account created. You can now log in.");
            frame.showCard(AuthFrame.LOGIN);
        } catch (AuthException ex) {
            showError(ex.getMessage());
        }
    }

    @Override
    public void clearFields() {
        txtName.setText("");
        txtUser.setText("");
        txtPass.setText("");
        txtConfirm.setText("");
        txtAnswer.setText("");
        cboQuestion.setSelectedIndex(0);
    }
}