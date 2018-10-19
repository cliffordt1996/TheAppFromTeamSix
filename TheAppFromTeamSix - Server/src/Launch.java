/* Author       : Thomas Clifford, Nate Irwin, Troy Nance, Dylan Webb
 * Date Created : 04/21/2018
 * Last Modified: 04/21/2018
 * About        : The entry point for this application.
 */

import GUI.ServerApp;
import OBJ.Client;
import OBJ.ClientForm;
import OBJ.Issue;
import OBJ.IssueLog;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Date;

public class Launch {

    public static final int DEFAULT_PORT = 60100;

    private static ServerApp app;

    private static int counterID;

    private static IssueLog issueLog;

    public static void main(String[] args) {

        counterID = 0;

        issueLog = new IssueLog();

        app = new ServerApp();
        app.setVisible(true);
        try { // Setup the server...
            ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT);
            app.println(
                    "Starting server at: " + new Date() + '\n'
                    + "Server IP: "
                    + serverSocket.getInetAddress().getHostAddress()
                    + ':' + serverSocket.getLocalPort());
            do {
                try {
                    // Listen for new clients; put each new client on its own 
                    // thread for handling and register them to the server.
                    while (app != null) {
                        Client client = new Client(serverSocket.accept());
                        client.setCustomName(
                                ((ClientForm) client.readObject()).getName());
                        app.sendAllClientForm(client.asForm()); // Notify other users of new client.
                        app.add(client);
                        app.println("Client detected. Attempting to setup thread.");
                        new Thread(new HandleAClient(client)).start();
                    }
                } catch (IOException ex) {
                    app.println("Failed to start thread for client.");
                    ex.printStackTrace();
                }
            } while (app != null);
        } catch (IOException ex) {
            System.err.println("Failed to setup server socket.");
            System.exit(1);
        }
    }

    public static class HandleAClient implements Runnable {

        private Client client;

        public HandleAClient(Client client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                // Thread for client successfully starts running; notify server.
                app.println(
                        "Thread started for client:\n\t"
                        + "IP Address: "
                        + client.getSocket().getInetAddress().getHostAddress()
                        + " | Name: "
                        + client.getCustomName());

                // Notify client of all other currently connected users.
                ClientForm[] currForms = app.getListasClientForms();
                for (ClientForm c : currForms) {
                    if (!(c.equals(client.asForm()))) {
                        client.writeObject(c);
                    }
                }

                // Send client the current state of the issue log.
                client.writeObject(issueLog);
                
                // Begin handling incoming data from client.
                while (client.getSocket().isConnected() && app.isClientListed(client)) {
                    Object o = client.readObject();
                    if (o instanceof Issue) {
                        Issue issue = (Issue) o;
                        if (app.isClientListed(client)) { // Check if status changed during wait.
                            // Make sure the issue has been assigned an ID.
                            if (issue.getID() == Issue.NO_ISSUE_ID) {
                                issue.setID(counterID);
                                ++counterID;
                            }

                            if (issueLog.getIssues().contains(issue)) {
                                // This works for updates because issues are equal if their ID's match.
                                issueLog.setIssue(issue, issueLog.getIssues().indexOf(issue));
                            } else {
                                issueLog.addIssue(issue);
                            }

                            if (issue.getVisibilityType() == Issue.VisibilityType.ANYONE) { // Issue is for all clients.
                                app.sendAllIssue(issue);
                                app.println(
                                        "Sent all clients issue with ID: "
                                        + issue.getID());
                            } else {
                                // TODO send whitelisted issue to correct clients.
                            }
                        }
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                // Losing the client; notify server and other clients; perform cleanup.
                app.println(
                        "Lost client:\n\t"
                        + "IP Address: "
                        + client.getSocket().getInetAddress().getHostAddress()
                        + " | Name: "
                        + client.getCustomName()
                        + "\nPerforming cleanup...");
                app.remove(client);
                client.closeStreams(); // In case streams are open.
                app.sendAllClientForm(client.asForm()); // For client-side cleanup.
                app.println("Cleanup complete.");
            }
        }
    }
}
