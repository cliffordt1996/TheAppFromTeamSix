/* Author       : Thomas Clifford
 * Date Created : 04/12/2018
 * Last Modified: 04/17/2018
 * About        : A class used to manage comments.
 */
package OBJ;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class Comment implements java.io.Serializable {
    
    public static final int TEXT_MAX_CHAR_COUNT = 10000;
    
    private ClientForm author;
    private byte[] text; // stored as byte array for encryption purposes.
    private String dateCreated;

    private boolean isEncrypted;
    
    private int issueID; // The ID of the issue for which this comment belongs to.

    /*CONSTRUCTORS*************************************************************/
    public Comment() {
        author = new ClientForm();
        text = "".getBytes();
        dateCreated = new Date().toString();
        
        isEncrypted = false;
        
        issueID = Issue.NO_ISSUE_ID;
    }

    public Comment(ClientForm author, String text, int issueID) {
        this.author = author;
        this.text = text.getBytes();
        dateCreated = new Date().toString();
        isEncrypted = false;
        
        this.issueID = issueID;
    }

    /*SETTERS******************************************************************/
    public void setAuthor(ClientForm author) {
        this.author = author;
    }

    public void setText(String clientText) {
        this.text = clientText.getBytes();
    }
    
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated.toString();
    }

    public void setIsEncrypted(boolean isEncrypted) {
        this.isEncrypted = isEncrypted;
    }
    
    public void setIssueID(int issueID) {
        this.issueID = issueID;
    }

    /*GETTERS******************************************************************/
    public ClientForm getAuthor() {
        return author;
    }

    public String getText() {
        return new String(text);
    }
    
    public String getDateCreated() {
        return dateCreated;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }
    
    public int getIssueID() {
        return issueID;
    }

    /*UTILITY******************************************************************/
    public void encryptText(String encryptionKey) {
        TEA tea = new TEA(encryptionKey.getBytes());
        text = tea.encrypt(text);
        isEncrypted = true;
    }

    public void decryptText(String decryptionKey) {
        TEA tea = new TEA(decryptionKey.getBytes());
        text = tea.decrypt(text);
        isEncrypted = false;
    }
    
    public void save(PrintWriter outputStream) throws IOException {
        author.save(outputStream);
        
        String textAsString = getText();
        
        outputStream.println(textAsString.split("\r\n|\r|\n").length);
        outputStream.println(textAsString);
        
        outputStream.println(dateCreated);
        
        outputStream.println(isEncrypted);
    }
    
    public void load(BufferedReader inputStream) throws IOException {
        author.load(inputStream);
        
        StringBuilder textBuilder = new StringBuilder();
        
        int numLines = Integer.parseInt(inputStream.readLine());
        for(int i = 0; i < numLines; i++) {
            textBuilder.append(inputStream.readLine()).append("\n");
        }
        text = textBuilder.toString().getBytes();
        
        dateCreated = inputStream.readLine();
        
        isEncrypted = Boolean.parseBoolean(inputStream.readLine());
    }

    /*PRIVATE******************************************************************/
}
