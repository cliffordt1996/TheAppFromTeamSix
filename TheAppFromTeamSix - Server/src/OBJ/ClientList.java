package OBJ;

import java.util.ArrayList;

public class ClientList extends ArrayList<Client> {

    /*CONSTRUCTORS*************************************************************/
    public ClientList() {
        super();
    }

    public ClientList(ArrayList<Client> clientList) {
        super(clientList);
    }

    /*SETTERS******************************************************************/
    /*GETTERS******************************************************************/
    /*UTILITY******************************************************************/
    public ClientForm[] getListasClientForms() {
        ClientForm[] cfs = new ClientForm[this.size()];
        for (int i = 0; i < cfs.length; i++) {
            cfs[i] = this.get(i).asForm();
        }
        return cfs;
    }

    public boolean isClientListed(Client client) {
        return this.contains(client);
    }

    public boolean remove(String address) {
        for (int i = 0; i < this.size(); i++) {
            if (this.get(i).getSocket().getInetAddress().getHostAddress().equalsIgnoreCase(address)) { // ¯\_(ツ)_/¯
                return this.remove(this.get(i));
            }
        }
        return false;
    }

    public void sendAllIssue(Issue issue) {
        for (Client c : this) {
            c.writeObject(issue);
        }
    }

    public void sendIssue(ClientForm clientForm, Issue issue) {
        for (Client c : this) {
            if (c.asForm().equals(clientForm)) {
                c.writeObject(issue);
            }
        }
    }

    public void sendAllClientForm(ClientForm clientForm) {
        for (Client c : this) {
            c.writeObject(clientForm);
        }
    }

    /*PRIVATE******************************************************************/
}
