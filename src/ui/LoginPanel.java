package ui;

import javax.swing.*;
import exception.AuthException;
import data.DataStore;
import model.Account;

public class LoginPanel extends FormPanel {
    private final AuthFrame frame;
    private final JTextField txtUser = new JTextField();
    private final JPasswordField txtPass = new JPasswordField();

    public LoginPanel(AuthFrame frame) {
        super("LOG IN");
        this.frame = frame;

        UITheme.styleField(txtUser);
        UITheme.styleField(txtPass);
        addField("Username", txtUser);
        addField("Password", txtPass);

        addComponent(UITheme.link("FORGOT PASSWORD?", () -> frame.showCard(AuthFrame.FORGOT)));
        addGap(24);

        BlackButton btnLogin = new BlackButton("LOG IN");
        btnLogin.addActionListener(e -> doLogin());
        addComponent(btnLogin);
        txtPass.addActionListener(e -> doLogin());   // pressing Enter also logs in
        addGap(20);

        addComponent(UITheme.link("NEW HERE? CREATE AN ACCOUNT", () -> frame.showCard(AuthFrame.SIGNUP)));
    }

    private void doLogin() {
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());

        try {
            if (user.isEmpty() || pass.isEmpty()) {
                throw new AuthException("Enter your username and password.");
            }
            Account acc = DataStore.login(user, pass);
            JOptionPane.showMessageDialog(this, "Welcome, " + acc.getFullName() + "!");
            // TODO: open your MainFrame here, then close this window:
            // new MainFrame(acc).setVisible(true);
            // frame.dispose();
        } catch (AuthException ex) {
            showError(ex.getMessage());
        }
    }

    @Override
    public void clearFields() {
        txtUser.setText("");
        txtPass.setText("");
    }
}