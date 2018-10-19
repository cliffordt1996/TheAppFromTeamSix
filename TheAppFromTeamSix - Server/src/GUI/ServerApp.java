package GUI;

import OBJ.ClientList;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;

public class ServerApp extends ClientList {

    public static final String TITLE = "The App From Team Six - Server";

    public static final Font FONT = new Font(Font.MONOSPACED, Font.PLAIN, 13);

    public static final Color[] COLOR_OPTION = {
        Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.ORANGE,
        Color.PINK, Color.RED, Color.WHITE, Color.YELLOW
    };

    public static final Color BACKGROUND_COLOR = Color.WHITE, FOREGROUND_COLOR = Color.BLACK;

    public static final int FRAME_WIDTH = 600;
    public static final int FRAME_HEIGHT = 500;

    private JFrame frame;
    private JTextArea textArea;
    private JTextField textField;
    private JScrollPane textScrollPane;
    
    private boolean autoScroll;

    /*CONSTRUCTORS*************************************************************/
    public ServerApp() {
        super();

        // Initialize attributes...
        autoScroll = false;

        // Initialize components...
        textArea = new JTextArea();
        textField = new JTextField();
        JMenuBar menuBar = new JMenuBar();
        frame = new JFrame();

        // Setup menu...
        JMenu menuSettings = new JMenu("Settings");
//        JMenu textColorOption = new JMenu("Text Color");
//
//        JMenuItem[] textColors = new JMenuItem[COLOR_OPTION.length];
//        for (int i = 0; i < textColors.length; i++) {
//            textColors[i] = new JMenuItem();
//            textColors[i].setBackground(COLOR_OPTION[i]);
//            textColors[i].addActionListener(e -> {
//                Color color = ((JMenuItem) e.getSource()).getBackground();
//                textArea.setForeground(color);
//                textField.setForeground(color);
//            });
//        }
//        for (JMenuItem color : textColors) {
//            textColorOption.add(color);
//        }
//        menuSettings.add(textColorOption); // Add color options to settings.

        JCheckBox autoScrollOption = new JCheckBox("Auto Scroll", autoScroll);
        autoScrollOption.setSelected(true);
        autoScrollOption.addChangeListener((ChangeEvent e) -> {
            autoScroll = !autoScroll;
        });
        menuSettings.add(autoScrollOption); // Add auto scroll option to settings.

        JMenuItem clearTextArea = new JMenuItem("Clear Screen");
        clearTextArea.addActionListener(e -> {
            textArea.setText("");
        });
        menuSettings.add(clearTextArea); // Add clear screen option to settings.

        menuSettings.addSeparator();

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> {
            frame.dispose();
            System.exit(0);
        });
        menuSettings.add(exit); // Add exit option to settings.

        menuBar.add(menuSettings);

        // Setup text area...
        textArea = new JTextArea();
        textArea.setBackground(BACKGROUND_COLOR);
        textArea.setForeground(FOREGROUND_COLOR);
        textArea.setFont(FONT);
        textArea.setLineWrap(true);
        textArea.setEditable(false);

        // Setup scroll pane...
        textScrollPane = new JScrollPane(textArea);

        // Setup text field...
        textField = new JTextField("");
        textField.setBackground(BACKGROUND_COLOR);
        textField.setForeground(FOREGROUND_COLOR);
        textField.setFont(FONT);
        textField.addKeyListener(new CommandListener());

        // Setup frame...
        frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setLocationRelativeTo(null);
//        frame.setIconImage(
//                new ImageIcon(this.getClass().getResource("/Icon.png")).getImage()
//        );
        frame.setTitle(TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set/Add components to frame...
        frame.setJMenuBar(menuBar);
        frame.add(textScrollPane, BorderLayout.CENTER);
        frame.add(textField, BorderLayout.SOUTH);
    }

    /*SETTERS******************************************************************/
    /*GETTERS******************************************************************/
    /*UTILITY******************************************************************/
    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }

    public void println(String s) {
        textArea.append(s + "\n");
        if (autoScroll) {
            JScrollBar sb = textScrollPane.getVerticalScrollBar();
            sb.setValue(sb.getMaximum());
        }
    }

    public void close() {
        frame.dispose();
    }

    /*PRIVATE******************************************************************/
    private class CommandListener implements KeyListener {

        public static final String 
                CMD_LIST_CMDS = "help",
                CMD_LIST_USERS = "list",
                CMD_KICK_USER = "kick";

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                String cmd = textField.getText().trim();

                if (cmd.toLowerCase().startsWith(CMD_LIST_CMDS)) {
                    printCommandList();
                } else if (cmd.toLowerCase().startsWith(CMD_LIST_USERS)) {
                    printClientInfo();
                } else if (cmd.toLowerCase().startsWith(CMD_KICK_USER)) {
                    kickClient(textField.getText().substring(CMD_KICK_USER.length()).trim());
                } else {
                    println(
                            "\"" + textField.getText()
                            + "\" is not a command. Type \""
                            + CMD_LIST_CMDS
                            + "\" for the list of commands.");
                }

                textField.setText("");
            }
        }

        private void printCommandList() {
            println(
                    "Available Commands:\n\t"
                    + CMD_LIST_CMDS + "\n\t"
                    + CMD_LIST_USERS + "\n\t"
                    + CMD_KICK_USER);
        }

        private void printClientInfo() {
            if (size() > 0) {
                println("Connected Users:");
                for (int i = 0; i < size(); i++) {
                    if (get(i).getSocket().isConnected()) {
                        textArea.append(
                                "\n\tIP Address: "
                                + get(i).getSocket().getInetAddress().getHostAddress()
                                + " | Name: "
                                + get(i).getCustomName());
                    }
                }
                println("");
            } else {
                println("There are no connected users.");
            }
        }

        private void kickClient(String address) {
            if (remove(address)) {
                println("Kicked user with address: " + address);
            } else {
                println("Failed to kick user with address: " + address);
            }
        }
    }
}
