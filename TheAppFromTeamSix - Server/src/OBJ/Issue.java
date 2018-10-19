/* Author       : Thomas Clifford
 * Date Created : 04/12/2018
 * Last Modified: 04/17/2018
 * About        : A class used to manage issues.
 */
package OBJ;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

/**
 * Used for managing all data about an issue.
 * Basic Information Includes:
 *     - Title
 *     - Topic
 *     - State (priority, open, archived)
 *     - (who has access to the issue?)
 *     - Comments
 *     - Date Created
 *     - Date Last Modified
 * 
 * This class is designed to be used by an IssueLog object. If you need to use
 * this object, first make sure you need this object and not an issue through an
 * issueLog object. 
 * 
 * @author Thomas Clifford
 */
public class Issue implements java.io.Serializable {
    
    public static final int NO_ISSUE_ID = -1; // Used by server to determine whether issue needs an ID.
    
    //private static final int TITLE_MAX_CHAR_COUNT = 32;
    
    private ClientForm author;
    
    public enum State { PRIORITY, OPEN, ARCHIVED }
    
    private State state;
    
    public enum VisibilityType { ANYONE, WHITELIST }
    
    private VisibilityType visibilityType;

    public enum Priority { LOW, MEDIUM, HIGH}
    
    private Priority priority;
    
    private String title;
    private String topic;
    
    private ArrayList<ClientForm> whitelist;
    private ArrayList<Comment> comments;
    
    private Date dateCreated;
    private Date lastModified;
    
    private boolean hasCode;
    
    private String code;
    private String codeFileName;
    
    private int ID; // Assigned by the server.
    
    private boolean hasUnreadComments;
    
    private int numUnreadComments;

    /*CONSTRUCTORS*************************************************************/
    public Issue() {
        author = new ClientForm();
        
        state = State.OPEN;
        
        visibilityType = VisibilityType.ANYONE;
        
        title = "";
        topic = "";
        
        whitelist = new ArrayList();
        comments = new ArrayList();
        
        dateCreated = new Date();
        lastModified = new Date();
        
        ID = NO_ISSUE_ID;
        
        hasCode = false;
        
        code = "";
        codeFileName = "";
        
        hasUnreadComments = false;
        
        numUnreadComments = 0;
    }

    public Issue(ClientForm author, String title, String topic, State state, VisibilityType visibilityType) {
        this.author = author;
        
        this.state = state;
        
        this.visibilityType = visibilityType;
        
        this.title = title;
        this.topic = topic;
        
        whitelist = new ArrayList();
        comments = new ArrayList();
        
        dateCreated = new Date();
        lastModified = new Date();
        
        ID = NO_ISSUE_ID;
        
        hasCode = false;
        
        code = "";
        codeFileName = "";
        
        hasUnreadComments = false;
        
        numUnreadComments = 0;
    }
    
    /*SETTERS******************************************************************/
    public void setAuthor(ClientForm author) {
        this.author = author;
    }
    
    public void setState(State state) {
        this.state = state;
    }

    public void setVisibilityType(VisibilityType visibilityType) {
        this.visibilityType = visibilityType;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
    
    public void setWhitelist(ArrayList<ClientForm> whitelist) {
        this.whitelist = whitelist;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
    
    public void setID(int ID) {
        this.ID = ID;
    }

    public void setCode(String code) {
        hasCode = !code.isEmpty();
        this.code = code;
    }
    
    public void setCodeFileName(String codeFileName) {
        this.codeFileName = codeFileName;
    }
    
    public void setPriority(Priority priority) {
        this.priority = priority;
    }
    
    /*GETTERS******************************************************************/
    public ClientForm getAuthor() {
        return author;
    }
    
    public State getState() {
        return state;
    }

    public VisibilityType getVisibilityType() {
        return visibilityType;
    }

    public String getTitle() {
        return title;
    }

    public String getTopic() {
        return topic;
    }

    public ArrayList<ClientForm> getWhitelist() {
        return whitelist;
    }
    
    public ArrayList<Comment> getComments() {
        return comments;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public Date getLastModified() {
        return lastModified;
    }
    
    public int getID() {
        return ID;
    }

    public String getCode() {
        return code;
    }
    
    public String getCodeFileName() {
        return codeFileName;
    }
    
    public Priority getPriority() {
        return priority;
    }
    
    /*UTILITY******************************************************************/
    public boolean isValid() {
        return (!title.isEmpty() && !topic.isEmpty());
    }
    
    public void addComment(Comment comment) {
        comments.add(comment);
        numUnreadComments++;
        hasUnreadComments = true;
    }
    
    public boolean hasCode() {
        return hasCode;
    }
    
    /**
     * Call this method to reset the unread comments counter.
     */
    public void hasReadComments() {
        numUnreadComments = 0;
        hasUnreadComments = false;
    }
    
    public void save(PrintWriter outputStream) throws IOException {
        author.save(outputStream);
        
        switch(state) {
            case PRIORITY: 
                outputStream.println("PRIORITY"); 
                break;
            case OPEN: 
                outputStream.println("OPEN"); 
                break;
            case ARCHIVED: 
                outputStream.println("ARCHIVED"); 
                break;
        }
        
        switch(visibilityType) {
            case ANYONE: 
                outputStream.println("ANYONE"); 
                break;
            case WHITELIST: 
                outputStream.println("WHITELIST"); 
                outputStream.println(whitelist.size());
                for(ClientForm c : whitelist) {
                    c.save(outputStream);
                }
                break;
        }
        
        outputStream.println(title);
        
        outputStream.println(topic.split("\r\n|\r|\n").length);
        outputStream.println(topic);
        
        for(Comment c : comments) {
            c.save(outputStream);
        }
        
        outputStream.println(dateCreated);
        outputStream.println(lastModified);
        
        outputStream.println(ID);
    }
    
    public void load(BufferedReader inputStream) throws IOException {
        author.load(inputStream);
        
        String stateAsString = inputStream.readLine();
        switch(stateAsString) {
            case "PRIORITY":
                state = State.PRIORITY;
                break;
            case "OPEN":
                state = State.OPEN;
                break;
            case "ARCHIVED":
                state = State.ARCHIVED;
                break;
        }
        
        String visibilityTypeAsString = inputStream.readLine();
        switch(visibilityTypeAsString) {
            case "ANYONE":
                visibilityType = VisibilityType.ANYONE;
                break;
            case "WHITELIST":
                int numClientForms = Integer.parseInt(inputStream.readLine());
                for(int i = 0; i < numClientForms; i++) {
                    ClientForm c = new ClientForm();
                    c.load(inputStream);
                    whitelist.add(c);
                }
                break;
        }
        
        title = inputStream.readLine();
        
        StringBuilder textBuilder = new StringBuilder();
        
        int numLines = Integer.parseInt(inputStream.readLine());
        for(int i = 0; i < numLines; i++) {
            textBuilder.append(inputStream.readLine()).append("\n");
        }
        topic = textBuilder.toString();
        
        dateCreated = new Date(inputStream.readLine());
        lastModified = new Date(inputStream.readLine());
        
        ID = Integer.parseInt(inputStream.readLine());
    }
    
    
    /*PRIVATE******************************************************************/

    @Override
    public String toString() {
        String thisIssueAsString = (hasUnreadComments) ? "(!)" : "";
        
        switch(priority) {
            case HIGH:
                thisIssueAsString += ("(Hi) " + title);
                break;
            case MEDIUM:
                thisIssueAsString += ("(Md) " + title);
                break;
            case LOW:
                thisIssueAsString += ("(Lo) " + title);
                break;
        }
        
        return thisIssueAsString;
    }

    @Override
    public int hashCode() { // Da faq. Generated with equals just.. Don't use it OK.
        int hash = 7;
        hash = 71 * hash + this.ID;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Issue other = (Issue) obj;
        return this.ID == other.ID;
    }

}
