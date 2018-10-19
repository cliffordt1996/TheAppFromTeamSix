/* Author       : Thomas Clifford, Nate Irwin, Troy Nance, Dylan Webb
 * Date Created : 04/08/2018
 * Last Modified: 04/22/2018
 * About        : The entry point for this application.
 */

import GUI.LoginPanel;
import GUI.MainApplicationFrame;
import OBJ.Client;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Application Entry Point.
 */
public class Launch {

    private static final int DEFAULT_PORT = 60100;

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainApplicationFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainApplicationFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainApplicationFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainApplicationFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        LoginPanel login = new LoginPanel();

        login.dialog.getContentPane().add(login);
        login.dialog.setTitle("Login");
        login.dialog.setModal(true);
        login.dialog.pack();
        login.dialog.setLocationRelativeTo(null);
        login.dialog.setVisible(true);

        if (!login.getOwner().isEmpty()) {
            try { // try to connect to server...
                Client client = new Client(login.getHostAddress(), DEFAULT_PORT, login.getOwner());
                
                client.writeObject(client.asForm());
                /* Create and display the form in online mode */
                MainApplicationFrame app = new MainApplicationFrame(client);
                app.setVisible(true);
                new Thread(app.new ObjectCollector()).start();
                
            } catch (IOException ex) {
                Logger.getLogger(Launch.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println("Failed to connect to host.");
                MainApplicationFrame.notifyFailedToConnectToHost();
                System.exit(-1);
            }
        } else {
            System.exit(0);
        }
    }
}
