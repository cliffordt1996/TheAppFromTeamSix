/* Author       : Thomas Clifford
 * Date Created : 04/17/2018
 * Last Modified: 04/17/2018
 * About        : A class that renders issues for jList components.
 */
package GUI;

import OBJ.Issue;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 *
 * @author thomas
 */
public class IssueRenderer extends JLabel implements ListCellRenderer<Issue> {

    public IssueRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Issue> list, Issue issue, int index,
            boolean isSelected, boolean cellHasFocus) {

        setFont(list.getFont());
        setText(issue.toString());

        if (issue.getState() == Issue.State.ARCHIVED) {
            setBackground(MainApplicationFrame.GRAY);

            Map attributes = getFont().getAttributes();
            attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
            setFont(new Font(attributes));
        } else {
            switch (issue.getPriority()) {
                case LOW:
                    setBackground(MainApplicationFrame.GREEN);
                    break;
                case MEDIUM:
                    setBackground(MainApplicationFrame.YELLOW);
                    break;
                case HIGH:
                    setBackground(MainApplicationFrame.RED);
                    break;
            }
        }

        if (isSelected) {
            setBorder(new LineBorder(Color.BLACK, 2));
        } else {
            setBorder(new EmptyBorder(2, 2, 2, 2));
        }

        return this;
    }
}
