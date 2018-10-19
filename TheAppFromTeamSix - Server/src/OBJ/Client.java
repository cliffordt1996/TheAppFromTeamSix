package OBJ;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {

    private Socket socket;

    private String customName, encryptionKey;
    private ObjectInputStream fromServer;
    private ObjectOutputStream toServer;

    /*CONSTRUCTORS*************************************************************/
    public Client() {
        super();
        customName = encryptionKey = "";
    }

    public Client(Socket socket) throws IOException {
        this.socket = socket;
        setupStreams();
    }

    public Client(Socket socket, String customName) throws IOException {
        this(socket);
        this.customName = customName;
    }

    public Client(Socket socket, String customName, String encryptionKey) throws IOException {
        this(socket, customName);
        this.encryptionKey = encryptionKey;
    }

    /*SETTERS******************************************************************/
    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public void setFromServer(ObjectInputStream fromServer) {
        this.fromServer = fromServer;
    }

    public void setToServer(ObjectOutputStream toServer) {
        this.toServer = toServer;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    /*GETTERS******************************************************************/
    public String getCustomName() {
        return customName;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public ObjectInputStream getFromServer() {
        return fromServer;
    }

    public ObjectOutputStream getToServer() {
        return toServer;
    }

    public Socket getSocket() {
        return socket;
    }

    /*UTILITY******************************************************************/
    public Object readObject() throws IOException {
        try {
            return fromServer.readObject();
        } catch (ClassNotFoundException ex) {
            System.err.println(
                    "Client.readObject() failed to read object from host.");
        }
        return new Object();
    }

    public void writeObject(Object o) {
        try {
            toServer.writeObject(o);
        } catch (IOException ex) {
            System.err.println(
                    "Client.writeObject() failed to write object to host.");
        }
    }

    public ClientForm asForm() {
        return new ClientForm(customName, socket.getInetAddress().getHostAddress());
    }

    public void closeStreams() {
        try {
            fromServer.close();
            toServer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("Client.closeStreams() Failed.");
        }
    }

    /*PRIVATE******************************************************************/
    private void setupStreams() {
        try {
            fromServer = new ObjectInputStream(getSocket().getInputStream());
            toServer = new ObjectOutputStream(getSocket().getOutputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("Client.setupStreams() Failed.");
        }
    }
}
