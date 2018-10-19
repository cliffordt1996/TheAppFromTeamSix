/* Author       : Thomas Clifford
 * Date Created : 04/07/2018
 * Last Modified: 04/07/2018
 * About        : A class used to manage issues.
 */
package OBJ;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * ClientForms are used to summarize Client details mainly for identification
 * purposes across a network.
 */
public class ClientForm implements java.io.Serializable {

    private String name, ipAddress;

    /*CONSTRUCTORS*************************************************************/
    public ClientForm() {
        name = "";
        ipAddress = "";
    }

    public ClientForm(String customName, String ipAddress) {
        this.name = customName;
        this.ipAddress = ipAddress;
    }

    /*SETTERS******************************************************************/
    public void setName(String customName) {
        this.name = customName;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /*GETTERS******************************************************************/
    public String getName() {
        return name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    /*UTILITY******************************************************************/
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ClientForm) {
            return (((ClientForm) o).getName().equals(name)
                    && ((ClientForm) o).getIpAddress().equals(ipAddress));
        } else {
            return false;
        }
    }
    
    public void save(PrintWriter outputStream) throws IOException {
        outputStream.println(name);
        outputStream.println(ipAddress);
    }
    
    public void load(BufferedReader inputStream) throws IOException {
        name = inputStream.readLine();
        ipAddress = inputStream.readLine();
    }

    /*PRIVATE******************************************************************/
}
