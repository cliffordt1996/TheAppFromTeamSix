package OBJ;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client extends Socket {

    private String customName, encryptionKey;
    private ObjectInputStream fromServer;
    private ObjectOutputStream toServer;

    /*CONSTRUCTORS*************************************************************/
    public Client() {
        super();
        customName = encryptionKey = "";
    }

    public Client(String host, int port) throws IOException {
        super(host, port);

        customName = this.getInetAddress().getHostAddress();
        encryptionKey = "";
        setupStreams();
    }

    public Client(String host, int port, String customName) throws IOException {
        this(host, port);
        this.customName = customName;
    }

    public Client(String host, int port, String customName, String encryptionKey) throws IOException {
        this(host, port, customName);
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
        return new ClientForm(customName, this.getInetAddress().getHostAddress());
    }

    public void closeStreams() {
        try {
            toServer.close();
            fromServer.close();
        } catch (IOException ex) {
            System.err.println(
                    "Client.closeStreams() failed to close streams.");
        }
    }

    /*PRIVATE******************************************************************/
    private void setupStreams() {
        try {
            toServer = new ObjectOutputStream(this.getOutputStream());
            fromServer = new ObjectInputStream(this.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("Client.setupStreams() Failed.");
        }
    }
}
