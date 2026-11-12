package ui;

import javax.swing.*;
import exception.AuthException;
import data.DataStore;

/*
 * Resets a password by checking the security question chosen at sign-up.
 */
public class ForgotPasswordPanel extends FormPanel {
    private final AuthFrame frame;
    private final JTextField txtUser = new JTextField();
    private final JComboBox<String> cboQuestion = new JComboBox<>(DataStore.SECURITY_QUESTIONS);
    private final JTextField txtAnswer = new JTextField();
    private final JPasswordField txtNew = new JPasswordField();
    private final JPasswordField txtConfirm = new JPasswordField();

    public ForgotPasswordPanel(AuthFrame frame) {
        super("FORGOT PASSWORD");
        this.frame = frame;

        UITheme.styleField(txtUser);
        UITheme.styleField(txtAnswer);
        UITheme.styleField(txtNew);
        UITheme.styleField(txtConfirm);
        UITheme.styleCombo(cboQuestion);

        addField("Username", txtUser);
        addField("Security question", cboQuestion);
        addField("Answer", txtAnswer);
        addField("New password", txtNew);
        addField("Confirm new password", txtConfirm);
        addGap(8);

        BlackButton btn = new BlackButton("RESET PASSWORD");
        btn.addActionListener(e -> doReset());
        addComponent(btn);
        addGap(16);

        addComponent(UITheme.link("BACK TO LOG IN", () -> frame.showCard(AuthFrame.LOGIN)));
    }

    private void doReset() {
        try {
            String user = txtUser.getText().trim();
            String answer = txtAnswer.getText().trim();
            String newPass = new String(txtNew.getPassword());
            String confirm = new String(txtConfirm.getPassword());

            if (user.isEmpty() || answer.isEmpty() || newPass.isEmpty()) {
                throw new AuthException("Fill in all fields.");
            }
            if (newPass.length() < 6) {
                throw new AuthException("Password must be at least 6 characters.");
            }
            if (!newPass.equals(confirm)) {
                throw new AuthException("Passwords do not match.");
            }

            DataStore.resetPassword(user, (String) cboQuestion.getSelectedItem(), answer, newPass);

            JOptionPane.showMessageDialog(this, "Password updated. You can now log in.");
            frame.showCard(AuthFrame.LOGIN);
        } catch (AuthException ex) {
            showError(ex.getMessage());
        }
    }

    @Override
    public void clearFields() {
        txtUser.setText("");
        txtAnswer.setText("");
        txtNew.setText("");
        txtConfirm.setText("");
        cboQuestion.setSelectedIndex(0);
    }
}