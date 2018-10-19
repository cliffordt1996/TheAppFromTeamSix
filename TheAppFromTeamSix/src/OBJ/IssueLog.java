/* Author       : Thomas Clifford
 * Date Created : 04/08/2018
 * Last Modified: 04/08/2018
 * About        : A class used to manage issues.
 */
package OBJ;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Issue logs are used to manage a grouping of issues and their comments 
 * either belonging to or permitted for use by a specific user. There can only
 * be one owner per log, so each user has unique log.
 * 
 * IssueLog -> Issue(s) -> Comment(s)
 */
public class IssueLog implements java.io.Serializable {
    
    private ClientForm owner;
    
    private ArrayList<Issue> issues;
    
    /*CONSTRUCTORS*************************************************************/
    public IssueLog() {
        owner = new ClientForm();
        issues = new ArrayList();
    }
    
    public IssueLog(ClientForm owner) {
        this.owner = owner;
        issues= new ArrayList();
    }
    
    public IssueLog(ClientForm owner, ArrayList<Issue> issues) {
        this.owner = owner;
        this.issues = issues;
    }

    /*SETTERS******************************************************************/
    public void setOwner(ClientForm owner) {
        this.owner = owner;
    }

    public void setIssues(ArrayList<Issue> issues) {
        this.issues = issues;
    }
    
    /*GETTERS******************************************************************/
    public ClientForm getOwner() {
        return owner;
    }

    public ArrayList<Issue> getIssues() {
        return issues;
    }

    /*UTILITY******************************************************************/
    public void addIssue(Issue issue) {
        issues.add(issue);
    }
    
    public void setIssue(Issue issue, int index) {
        issues.set(index, issue);
    }
    
    public void save(String filepath) throws IOException {
        try (PrintWriter outputStream = new PrintWriter(new FileWriter(filepath))) {
            // Write IssueLog to file.
            owner.save(outputStream);
            
            outputStream.println(issues.size());
            for(Issue i : issues) {
                i.save(outputStream);
            }
        }
    }
    
    public void load(String filepath) throws IOException {
        try (BufferedReader inputStream = new BufferedReader(new FileReader(filepath))) {
            // Read IssueLog from file.
            owner.load(inputStream);
            
            int numIssues = Integer.parseInt(inputStream.readLine());
            for(int i = 0; i < numIssues; i++) {
                Issue issue = new Issue();
                issue.load(inputStream);
                issues.add(issue);
            }
        }
    }

    /*PRIVATE******************************************************************/
}
