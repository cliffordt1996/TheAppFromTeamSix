/* Author       : Thomas Clifford, Nate Irwin, Troy Nance, Dylan Webb
 * Date Created : 04/12/2018
 * Last Modified: 04/21/2018
 * About        : The main window for this application.
 */
package GUI;

import OBJ.Client;
import OBJ.ClientForm;
import OBJ.Comment;
import OBJ.Issue;
import OBJ.IssueLog;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

/**
 * The main application window.
 *
 * @author Thomas Clifford, Nate Irwin, Troy Nance, Dylan Webb
 */
public class MainApplicationFrame extends javax.swing.JFrame {

    public static final Color GRAY = new Color((float) 0.5, (float) 0.5, (float) 0.5, (float) 0.5);
    public static final Color GREEN = new Color((float) 0.5, (float) 1.0, (float) 0.5, (float) 0.5);
    public static final Color YELLOW = new Color((float) 1.0, (float) 1.0, (float) 0.5, (float) 0.5);
    public static final Color RED = new Color((float) 1.0, (float) 0.5, (float) 0.5, (float) 0.5);

    // Our added variables.
    private HTMLEditorKit feedbackArea_kit;
    private HTMLDocument feedbackArea_doc;

    private DefaultListModel issueListModel;

    private Client client;
    private ArrayList<ClientForm> clientList;

    // End our added variables.
    public IssueLog issueLog;

    /**
     * Creates new form MainApplicationFrame. The default constructor is not
     * very useful. See other constructors to be sure you need the default.
     */
    public MainApplicationFrame() {
        feedbackArea_kit = new HTMLEditorKit();
        feedbackArea_doc = new HTMLDocument();

        issueListModel = new DefaultListModel();

        initComponents();

        currentLog_jList.getSelectionModel().addListSelectionListener(new currentLog_ListSelectionHandler());

        issue_jPopupMenu.add(options_jMenuItem);
        issue_jPopupMenu.add(archive_jMenuItem);

        
        issueLog = new IssueLog();
        clientList = new ArrayList<>();

        setLocationRelativeTo(null);
    }

    /**
     * Creates a new form MainApplicationFrame using the specified IssueLog.
     * This constructor is intended to be used whenever the client connects to a
     * server, where this form receives the IssueLog from the server.
     *
     * @param issueLog The IssueLog received from a server to be loaded by this
     * form.
     */
    public MainApplicationFrame(Client client/*, IssueLog issueLog*/) {
        this();
        this.client = client;
        this.issueLog.setOwner(client.asForm());
        //this.issueLog = issueLog;
    }

    /**
     * Creates a new form MainApplicationFrame using the specified owner. This
     * constructor is intended to be used whenever the client has no server to
     * connect to, in which case the owner is used for identification purposes
     * when creating/using issues.
     *
     * @param owner The owner of this new IssueLog (a username).
     */
    public MainApplicationFrame(String owner) {
        this();

        this.issueLog.setOwner(new ClientForm(owner, ""));
    }

    private void switchFocusToIssue(Issue issue) {
        // TODO switch content focus to parameterized issue.
        issueName_jLabel.setText(issue.getTitle());
        issueDatePosted_jLabel.setText("Posted " + issue.getDateCreated().toString());
        issueNumComments_jLabel.setText("Comments: " + issue.getComments().size());

        if (issue.getState() == Issue.State.ARCHIVED) {
            top_jPanel.setBackground(MainApplicationFrame.GRAY);
            commentBox_jTextArea.setEnabled(false);
            submitComment_jButton.setEnabled(false);
        } else {
            switch (issue.getPriority()) {
                case HIGH:
                    top_jPanel.setBackground(MainApplicationFrame.RED);
                    break;
                case MEDIUM:
                    top_jPanel.setBackground(MainApplicationFrame.YELLOW);
                    break;
                case LOW:
                    top_jPanel.setBackground(MainApplicationFrame.GREEN);
                    break;
            }
            commentBox_jTextArea.setEnabled(true);
            submitComment_jButton.setEnabled(true);
        }

        issueDownloadCode_jButton.setEnabled(issue.hasCode());

        try {
            // Get the frame's html document.
            //issueFeedback_jTextPane.setDocument(new HTMLDocument());
            // The following code block thanks to contributors on:
            // https://stackoverflow.com/questions/3470683/insert-html-into-the-body-of-an-htmldocument?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
            feedbackArea_doc = (HTMLDocument) issueFeedback_jTextPane.getStyledDocument(); // without this line... the feedback area is broken!

            // Get the body of the document. Head is reserved for issue topic.
            Element[] roots = feedbackArea_doc.getRootElements(); // #0 is the HTML element, #1 the bidi-root
            Element body = null;
            for (int i = 0; i < roots[0].getElementCount(); i++) {
                Element element = roots[0].getElement(i);
                if (element.getAttributes().getAttribute(StyleConstants.NameAttribute) == HTML.Tag.BODY) {
                    body = element;
                    break;
                }
            }

            // Reset the body.
            feedbackArea_doc.setInnerHTML(body, "<h2>Original Post</h2>");

            String issueTopicToHTML = issue.getTopic();
            // Replace < with html element to prevent html encodings and document generation errors.
            issueTopicToHTML = issueTopicToHTML.replaceAll("[<]", "&lt;");
            // Replace tabs in text with spaces in html to maintain formatting.
            issueTopicToHTML = issueTopicToHTML.replaceAll("[\\t]", "&nbsp;&nbsp;&nbsp;&nbsp;");
            // Replace spaces in text with spaces in html to maintain formatting.
            issueTopicToHTML = issueTopicToHTML.replaceAll("[ ]", "&nbsp;");
            // Replace new lines in text with new line in html to maintain formatting
            issueTopicToHTML = issueTopicToHTML.replaceAll("[\\n]", "<br>");

            issueTopicToHTML = ""
                    + "<p><b>" + issue.getAuthor().getName() + ","
                    + "<br>"
                    + "<blockquote><font face=\"Courier\">"
                    + issueTopicToHTML
                    + "</font></blockquote>"
                    + "<br>"
                    + "<font size =\"2\">Posted " + issue.getDateCreated().toString() + "</font>"
                    + "<br></b></p><hr>";

            // Add the topic to the body of the document
            feedbackArea_doc.insertBeforeEnd(body, issueTopicToHTML);

            // Add the issue's comments to the body of the document.
            for (Comment c : issue.getComments()) {
                addCommentToDisplay(c);
            }

            issue.hasReadComments();

        } catch (BadLocationException | IOException ex) {
            Logger.getLogger(MainApplicationFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        currentLog_jList.repaint();
        top_jPanel.repaint();

        this.setMinimumSize(this.getSize());
        this.pack();
        this.setMinimumSize(null);

    }

    private void addCommentToDisplay(Comment comment) {
        try {
            // The following code block thanks to contributors on:
            // https://stackoverflow.com/questions/3470683/insert-html-into-the-body-of-an-htmldocument?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
            feedbackArea_doc = (HTMLDocument) issueFeedback_jTextPane.getStyledDocument(); // without this line... the feedback area is broken!
            // Get the body of the document.
            Element[] roots = feedbackArea_doc.getRootElements(); // #0 is the HTML element, #1 the bidi-root
            Element body = null;
            for (int i = 0; i < roots[0].getElementCount(); i++) {
                Element element = roots[0].getElement(i);
                if (element.getAttributes().getAttribute(StyleConstants.NameAttribute) == HTML.Tag.BODY) {
                    body = element;
                    break;
                }
            }

            String commentTextToHTML = comment.getText();
            // Replace < with html element to prevent html encodings and document generation errors.
            commentTextToHTML = commentTextToHTML.replaceAll("[<]", "&lt;");
            // Replace tabs in text with spaces in html to maintain formatting.
            commentTextToHTML = commentTextToHTML.replaceAll("[\\t]", "&nbsp;&nbsp;&nbsp;&nbsp;");
            // Replace spaces in text with spaces in html to maintain formatting.
            commentTextToHTML = commentTextToHTML.replaceAll("[ ]", "&nbsp;");
            // Replace new lines in text with new line in html to maintain formatting
            commentTextToHTML = commentTextToHTML.replaceAll("[\\n]", "<br>");

            commentTextToHTML = "<p><b>" + comment.getAuthor().getName() + ",</b>"
                    + "<br>"
                    + "<blockquote>"
                    + commentTextToHTML
                    + "</blockquote>"
                    + "<br>"
                    + "<font size =\"2\">Posted " + comment.getDateCreated() + "</font>"
                    + "<br></p><hr>";

            feedbackArea_doc.insertBeforeEnd(body, commentTextToHTML);

        } catch (BadLocationException | IOException ex) {
            Logger.getLogger(MainApplicationFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        issue_jPopupMenu = new javax.swing.JPopupMenu();
        options_jMenuItem = new javax.swing.JMenuItem();
        archive_jMenuItem = new javax.swing.JMenuItem();
        left_jPanel = new javax.swing.JPanel();
        currentLog_jScrollPane = new javax.swing.JScrollPane();
        currentLog_jList = new javax.swing.JList<>();
        center_jPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        issueFeedback_jTextPane = new javax.swing.JTextPane();
        bottom_jPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        commentBox_jTextArea = new javax.swing.JTextArea();
        submitComment_jButton = new javax.swing.JButton();
        commentBoxInfo_jLabel = new javax.swing.JLabel();
        top_jPanel = new javax.swing.JPanel() {
            protected void paintComponent(java.awt.Graphics g)
            {
                g.setColor( getBackground() );
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        issueDownloadCode_jButton = new javax.swing.JButton();
        issueDatePosted_jLabel = new javax.swing.JLabel();
        issueName_jLabel = new javax.swing.JLabel();
        issueNumComments_jLabel = new javax.swing.JLabel();
        main_jMenuBar = new javax.swing.JMenuBar();
        file_jMenu = new javax.swing.JMenu();
        newIssue_jMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        changeServer_jMenuItem = new javax.swing.JMenuItem();
        profile_jMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        exit_jMenuItem = new javax.swing.JMenuItem();

        options_jMenuItem.setFont(new java.awt.Font("Lucida Grande", 0, 13)); // NOI18N
        options_jMenuItem.setText("Options...");
        options_jMenuItem.setEnabled(false);
        options_jMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                options_jMenuItemActionPerformed(evt);
            }
        });

        archive_jMenuItem.setFont(new java.awt.Font("Lucida Grande", 0, 13)); // NOI18N
        archive_jMenuItem.setText("Archive");
        archive_jMenuItem.setEnabled(false);
        archive_jMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                archive_jMenuItemActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("The App from Team Six");
        setMinimumSize(new java.awt.Dimension(550, 450));
        setName("mainApplicationFrame"); // NOI18N
        setSize(new java.awt.Dimension(800, 700));

        currentLog_jScrollPane.setPreferredSize(new java.awt.Dimension(71, 172));

        currentLog_jList.setBorder(javax.swing.BorderFactory.createTitledBorder("Current Log"));
        currentLog_jList.setFont(new java.awt.Font("Courier", 0, 15)); // NOI18N
        currentLog_jList.setModel(issueListModel);
        currentLog_jList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        currentLog_jList.setCellRenderer(new IssueRenderer());
        currentLog_jList.setComponentPopupMenu(issue_jPopupMenu);
        currentLog_jList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                issueDoubleClicked(evt);
            }
        });
        currentLog_jScrollPane.setViewportView(currentLog_jList);

        javax.swing.GroupLayout left_jPanelLayout = new javax.swing.GroupLayout(left_jPanel);
        left_jPanel.setLayout(left_jPanelLayout);
        left_jPanelLayout.setHorizontalGroup(
            left_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(currentLog_jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
        );
        left_jPanelLayout.setVerticalGroup(
            left_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(currentLog_jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 666, Short.MAX_VALUE)
        );

        center_jPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        issueFeedback_jTextPane.setEditable(false);
        issueFeedback_jTextPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        issueFeedback_jTextPane.setDocument(feedbackArea_doc);
        issueFeedback_jTextPane.setEditorKit(feedbackArea_kit);
        issueFeedback_jTextPane.setFont(new java.awt.Font("Courier", 0, 15)); // NOI18N
        issueFeedback_jTextPane.setToolTipText("");
        jScrollPane3.setViewportView(issueFeedback_jTextPane);

        javax.swing.GroupLayout center_jPanelLayout = new javax.swing.GroupLayout(center_jPanel);
        center_jPanel.setLayout(center_jPanelLayout);
        center_jPanelLayout.setHorizontalGroup(
            center_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 603, Short.MAX_VALUE)
        );
        center_jPanelLayout.setVerticalGroup(
            center_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3)
        );

        commentBox_jTextArea.setColumns(80);
        commentBox_jTextArea.setFont(new java.awt.Font("Courier", 0, 15)); // NOI18N
        commentBox_jTextArea.setLineWrap(true);
        commentBox_jTextArea.setRows(5);
        commentBox_jTextArea.setTabSize(4);
        commentBox_jTextArea.setWrapStyleWord(true);
        commentBox_jTextArea.setEnabled(false);
        commentBox_jTextArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                commentBox_jTextAreaKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(commentBox_jTextArea);

        submitComment_jButton.setText("Submit");
        submitComment_jButton.setEnabled(false);
        submitComment_jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitComment_jButtonActionPerformed(evt);
            }
        });

        commentBoxInfo_jLabel.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N
        commentBoxInfo_jLabel.setText("character count (" + commentBox_jTextArea.getText().length() + "/" + Comment.TEXT_MAX_CHAR_COUNT + ")");

        javax.swing.GroupLayout bottom_jPanelLayout = new javax.swing.GroupLayout(bottom_jPanel);
        bottom_jPanel.setLayout(bottom_jPanelLayout);
        bottom_jPanelLayout.setHorizontalGroup(
            bottom_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 607, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bottom_jPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(commentBoxInfo_jLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(submitComment_jButton))
        );
        bottom_jPanelLayout.setVerticalGroup(
            bottom_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bottom_jPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(bottom_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(submitComment_jButton)
                    .addComponent(commentBoxInfo_jLabel)))
        );

        top_jPanel.setOpaque(false);

        issueDownloadCode_jButton.setText("View Code");
        issueDownloadCode_jButton.setEnabled(false);
        issueDownloadCode_jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                issueDownloadCode_jButtonActionPerformed(evt);
            }
        });

        issueDatePosted_jLabel.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N

        issueName_jLabel.setFont(new java.awt.Font("Courier", 1, 15)); // NOI18N

        issueNumComments_jLabel.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N

        javax.swing.GroupLayout top_jPanelLayout = new javax.swing.GroupLayout(top_jPanel);
        top_jPanel.setLayout(top_jPanelLayout);
        top_jPanelLayout.setHorizontalGroup(
            top_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, top_jPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(issueName_jLabel)
                .addGap(18, 18, 18)
                .addComponent(issueDatePosted_jLabel)
                .addGap(18, 18, 18)
                .addComponent(issueNumComments_jLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(issueDownloadCode_jButton, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        top_jPanelLayout.setVerticalGroup(
            top_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(top_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(issueDownloadCode_jButton)
                .addComponent(issueName_jLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(issueDatePosted_jLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(issueNumComments_jLabel))
        );

        file_jMenu.setText("File");
        file_jMenu.setFont(new java.awt.Font("Lucida Grande", 0, 13)); // NOI18N

        newIssue_jMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        newIssue_jMenuItem.setFont(new java.awt.Font("Lucida Grande", 0, 13)); // NOI18N
        newIssue_jMenuItem.setText("New Issue...");
        newIssue_jMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newIssue_jMenuItemActionPerformed(evt);
            }
        });
        file_jMenu.add(newIssue_jMenuItem);
        file_jMenu.add(jSeparator1);

        changeServer_jMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        changeServer_jMenuItem.setFont(new java.awt.Font("Lucida Grande", 0, 13)); // NOI18N
        changeServer_jMenuItem.setText("Change Server...");
        file_jMenu.add(changeServer_jMenuItem);

        profile_jMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        profile_jMenuItem.setFont(new java.awt.Font("Lucida Grande", 0, 13)); // NOI18N
        profile_jMenuItem.setText("Profile...");
        file_jMenu.add(profile_jMenuItem);
        file_jMenu.add(jSeparator3);

        exit_jMenuItem.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        exit_jMenuItem.setText("Exit");
        exit_jMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exit_jMenuItemActionPerformed(evt);
            }
        });
        file_jMenu.add(exit_jMenuItem);

        main_jMenuBar.add(file_jMenu);

        setJMenuBar(main_jMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(left_jPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(center_jPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(top_jPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bottom_jPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(top_jPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(center_jPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bottom_jPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(left_jPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void newIssue_jMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newIssue_jMenuItemActionPerformed
        IssuePanel issuePanel = new IssuePanel();
        issuePanel.dialog.getContentPane().add(issuePanel);
        issuePanel.dialog.setTitle("Create New Issue");
        issuePanel.dialog.setModal(true);
        issuePanel.dialog.pack();
        issuePanel.dialog.setLocationRelativeTo(this);
        issuePanel.dialog.setVisible(true);

        if (issuePanel.getIssue().isValid() && !issuePanel.isCancelled) {
            issuePanel.getIssue().setAuthor(issueLog.getOwner());

            if (client.isConnected()) {
                client.writeObject(issuePanel.getIssue());
            } else {
                issueListModel.addElement(issuePanel.getIssue());
                currentLog_jList.setSelectedIndex(issueListModel.getSize() - 1);
                switchFocusToIssue(issuePanel.getIssue());
            }
        }
    }//GEN-LAST:event_newIssue_jMenuItemActionPerformed

    private void submitComment_jButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitComment_jButtonActionPerformed
        if (!commentBox_jTextArea.getText().isEmpty() && currentLog_jList.getSelectedIndex() != -1) {
            Issue selectedIssue = (Issue) (issueListModel.get(currentLog_jList.getSelectedIndex()));
            Comment comment = new Comment(issueLog.getOwner(), commentBox_jTextArea.getText(), selectedIssue.getID());

            selectedIssue.addComment(comment);
            if (client.isConnected()) {
                client.writeObject(selectedIssue);
            } else {
                switchFocusToIssue(selectedIssue);
            }
            commentBox_jTextArea.setText("");
            commentBoxInfo_jLabel.setText("character count (0/" + Comment.TEXT_MAX_CHAR_COUNT + ")");
        }
    }//GEN-LAST:event_submitComment_jButtonActionPerformed

    private void issueDoubleClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_issueDoubleClicked
        
        if (evt.getClickCount() == 2 && currentLog_jList.getSelectedIndex() != -1) {
            Issue selectedIssue = (Issue) issueListModel.getElementAt(currentLog_jList.getSelectedIndex());
            boolean isUserOwner = selectedIssue.getAuthor().getName().equals(issueLog.getOwner().getName());
            
            if (selectedIssue.getState() != Issue.State.ARCHIVED && isUserOwner) {
                IssuePanel issuePanel = new IssuePanel(selectedIssue);

                issuePanel.dialog.getContentPane().add(issuePanel);
                issuePanel.dialog.setTitle("Update Issue");
                issuePanel.dialog.setModal(true);
                issuePanel.dialog.pack();
                issuePanel.dialog.setLocationRelativeTo(this);
                issuePanel.dialog.setVisible(true);

                if (issuePanel.getIssue().isValid() && !issuePanel.isCancelled) {

                    if (client.isConnected()) {
                        client.writeObject(issuePanel.getIssue());
                    } else {
                        issueListModel.setElementAt(issuePanel.getIssue(), currentLog_jList.getSelectedIndex());

                        selectedIssue = (Issue) issueListModel.getElementAt(currentLog_jList.getSelectedIndex());
                        switchFocusToIssue(selectedIssue);
                    }
                }
            }
        }
    }//GEN-LAST:event_issueDoubleClicked

    private void issueDownloadCode_jButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_issueDownloadCode_jButtonActionPerformed
        Issue selectedIssue = (Issue) (issueListModel.get(currentLog_jList.getSelectedIndex()));

        CodeWindow cw = new CodeWindow(selectedIssue);
        cw.setTitle("Code for issue \"" + selectedIssue.getTitle() + "\"");
        cw.pack();
        cw.setLocationRelativeTo(this);
        cw.setVisible(true);
    }//GEN-LAST:event_issueDownloadCode_jButtonActionPerformed

    private void commentBox_jTextAreaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_commentBox_jTextAreaKeyReleased
        if (commentBox_jTextArea.getText().length() >= Comment.TEXT_MAX_CHAR_COUNT) {
            commentBox_jTextArea.setText(commentBox_jTextArea.getText().substring(0, Comment.TEXT_MAX_CHAR_COUNT - 1));
        }
        commentBoxInfo_jLabel.setText("character count (" + commentBox_jTextArea.getText().length() + "/" + Comment.TEXT_MAX_CHAR_COUNT + ")");
    }//GEN-LAST:event_commentBox_jTextAreaKeyReleased

    private void exit_jMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exit_jMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exit_jMenuItemActionPerformed

    private void archive_jMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_archive_jMenuItemActionPerformed
        if (currentLog_jList.getSelectedIndex() != -1) {
            Issue selectedIssue = (Issue) issueListModel.getElementAt(currentLog_jList.getSelectedIndex());
            selectedIssue.setState(Issue.State.ARCHIVED);
            if (client.isConnected()) {
                client.writeObject(selectedIssue);
            } else {
                switchFocusToIssue(selectedIssue);
            }
        }
    }//GEN-LAST:event_archive_jMenuItemActionPerformed

    private void options_jMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_options_jMenuItemActionPerformed
        if (currentLog_jList.getSelectedIndex() != -1) {
            Issue selectedIssue = (Issue) issueListModel.getElementAt(currentLog_jList.getSelectedIndex());

            if (selectedIssue.getState() != Issue.State.ARCHIVED) {
                IssuePanel issuePanel = new IssuePanel(selectedIssue);

                issuePanel.dialog.getContentPane().add(issuePanel);
                issuePanel.dialog.setTitle("Update Issue");
                issuePanel.dialog.setModal(true);
                issuePanel.dialog.pack();
                issuePanel.dialog.setLocationRelativeTo(this);
                issuePanel.dialog.setVisible(true);

                if (issuePanel.getIssue().isValid() && !issuePanel.isCancelled) {

                    if (client.isConnected()) {
                        client.writeObject(issuePanel.getIssue());
                    } else {
                        issueListModel.setElementAt(issuePanel.getIssue(), currentLog_jList.getSelectedIndex());

                        selectedIssue = (Issue) issueListModel.getElementAt(currentLog_jList.getSelectedIndex());
                        switchFocusToIssue(selectedIssue);
                    }
                }
            }
        }
    }//GEN-LAST:event_options_jMenuItemActionPerformed

    class currentLog_ListSelectionHandler implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (currentLog_jList.getSelectedIndex() != -1) {
                Issue selectedIssue = (Issue) issueListModel.getElementAt(currentLog_jList.getSelectedIndex());
                
                boolean isIssueArchived = selectedIssue.getState() == Issue.State.ARCHIVED;
                boolean isUserOwner = selectedIssue.getAuthor().getName().equals(issueLog.getOwner().getName());
                
                options_jMenuItem.setEnabled(!isIssueArchived && isUserOwner);
                archive_jMenuItem.setEnabled(!isIssueArchived && isUserOwner);
                
                switchFocusToIssue(selectedIssue);
            } else {
                options_jMenuItem.setEnabled(false);
                archive_jMenuItem.setEnabled(false);
            }
        }
    }

    public class ObjectCollector implements Runnable {

        @Override
        public void run() { // Handle data sent in from the server.
            try {
                while (client.isConnected()) {
                    Object o = client.readObject();
                    if (o instanceof Issue) {
                        // Check if issue is new or existing.
                        Issue issue = (Issue) o;
                        if (issueListModel.contains(issue)) {
                            // This works for updates because issues are equal if their ID's match.
                            issueListModel.setElementAt(issue, issueListModel.indexOf(issue));
                            if (currentLog_jList.getSelectedIndex() == issueListModel.indexOf(issue)) {
                                switchFocusToIssue((Issue) issueListModel.getElementAt(issueListModel.indexOf(issue)));
                            }
                        } else {
                            issueListModel.addElement(issue);
                            if (issue.getAuthor().getName().equals(issueLog.getOwner().getName())) { // User just created the issue, switch focus to it.
                                currentLog_jList.setSelectedIndex(issueListModel.getSize() - 1);
                                switchFocusToIssue((Issue) issueListModel.getElementAt(issueListModel.indexOf(issue)));
                            }
                        }
                        currentLog_jList.repaint();
                    } else if (o instanceof ClientForm) { // For whitelist purposes.
                        ClientForm cf = (ClientForm) o;
                        if (clientList.contains(cf)) { // User left.
                            clientList.remove(cf);
//                            if (userNotifications) {
//                                userDisconnected.play();
//                            }
                        } else { // User joined
                            clientList.add(cf);
//                            if (userNotifications) {
//                                userConnected.play();
//                            }
                        }
                    } else if (o instanceof IssueLog) {
                        IssueLog il = (IssueLog) o;
                        issueListModel.removeAllElements();
                        for (Issue i : il.getIssues()) {
                            issueListModel.addElement(i);
                        }
                        currentLog_jList.repaint();
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                notifyDisconnectedFromHost();
                client.closeStreams();
            }
        }
    }

    public static void notifyDisconnectedFromHost() {
        JOptionPane.showMessageDialog(
                null, "Disconnected From Host.", "The App From Team Six", JOptionPane.ERROR_MESSAGE);
    }
    
    public static void notifyFailedToConnectToHost() {
        JOptionPane.showMessageDialog(
                null, "Failed to Connect to Host.\nThis application will now close.", "The App From Team Six", JOptionPane.ERROR_MESSAGE);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem archive_jMenuItem;
    private javax.swing.JPanel bottom_jPanel;
    private javax.swing.JPanel center_jPanel;
    private javax.swing.JMenuItem changeServer_jMenuItem;
    private javax.swing.JLabel commentBoxInfo_jLabel;
    private javax.swing.JTextArea commentBox_jTextArea;
    private javax.swing.JList<Issue> currentLog_jList;
    private javax.swing.JScrollPane currentLog_jScrollPane;
    private javax.swing.JMenuItem exit_jMenuItem;
    private javax.swing.JMenu file_jMenu;
    private javax.swing.JLabel issueDatePosted_jLabel;
    private javax.swing.JButton issueDownloadCode_jButton;
    private javax.swing.JTextPane issueFeedback_jTextPane;
    private javax.swing.JLabel issueName_jLabel;
    private javax.swing.JLabel issueNumComments_jLabel;
    private javax.swing.JPopupMenu issue_jPopupMenu;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPanel left_jPanel;
    private javax.swing.JMenuBar main_jMenuBar;
    private javax.swing.JMenuItem newIssue_jMenuItem;
    private javax.swing.JMenuItem options_jMenuItem;
    private javax.swing.JMenuItem profile_jMenuItem;
    private javax.swing.JButton submitComment_jButton;
    private javax.swing.JPanel top_jPanel;
    // End of variables declaration//GEN-END:variables
}
