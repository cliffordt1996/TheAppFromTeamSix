/* Author       : Nate Irwin
 * Date Created : 04/15/2018
 * Last Modified: 04/22/2018
 * About        : A simple panel for loging in.
 */

package GUI;

import javax.swing.JDialog;

/**
 *
 * @author Nate Irwin
 */
public class LoginPanel extends javax.swing.JPanel {

    private String owner;
    private String hostAddress;
    
    private static final String MAIN_SERVER_IP = "75.132.131.224";
    private static final String MAIN_SERVER_MASK = "team6server";
    
    public JDialog dialog;
    /** Creates new form LoginPanel */
    public LoginPanel() {
        initComponents();
        
        owner = "";
        hostAddress = "";
        
        dialog = new JDialog();
    }
    
    public String getOwner() {
        return owner;
    }

     public String getHostAddress() {
        return hostAddress;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        usernameLabel = new javax.swing.JLabel();
        passwordLabel = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();
        usernameField = new javax.swing.JTextField();
        loginButton = new javax.swing.JButton();
        hostLabel = new javax.swing.JLabel();
        hostAddressField = new javax.swing.JTextField();

        usernameLabel.setText("Username");

        passwordLabel.setText("Password");

        passwordField.setFont(new java.awt.Font("Courier", 0, 13)); // NOI18N
        passwordField.setMinimumSize(new java.awt.Dimension(6, 200));

        usernameField.setFont(new java.awt.Font("Courier", 0, 13)); // NOI18N

        loginButton.setText("Login");
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });

        hostLabel.setText("Host Address");

        hostAddressField.setFont(new java.awt.Font("Courier", 0, 13)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(usernameField)
                    .addComponent(hostAddressField)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(hostLabel)
                            .addComponent(usernameLabel)
                            .addComponent(passwordLabel)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(121, 121, 121)
                                .addComponent(loginButton)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(passwordField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(hostLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hostAddressField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(usernameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(passwordLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(loginButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginButtonActionPerformed
        // TODO add your handling code here:
        
        char[] tempPass = passwordField.getPassword();
        String tempUser = usernameField.getText();
        String tempHostAddress = hostAddressField.getText();
        
        boolean isFormValid = (tempPass.length != 0 && !tempUser.isEmpty());
        
        if(isFormValid) {
            hostAddress = (tempHostAddress.equals(MAIN_SERVER_MASK)) ? MAIN_SERVER_IP : tempHostAddress;
            owner = tempUser;
            dialog.dispose();
        }
        
    }//GEN-LAST:event_loginButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField hostAddressField;
    private javax.swing.JLabel hostLabel;
    private javax.swing.JButton loginButton;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JTextField usernameField;
    private javax.swing.JLabel usernameLabel;
    // End of variables declaration//GEN-END:variables

}
