package company;

import javax.swing.*;
import java.awt.*;

class LabelTextField {
    private JLabel label;
    private JTextField textField;

    //Horizontal Box containing a JLabel and a JTextField.
    LabelTextField(String labelText) {
        label = new JLabel(labelText);
        textField = new JTextField();
    }

    Box get() {
        Box box = Box.createHorizontalBox();
        box.add(label);
        Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
        box.add(rigidArea);
        box.add(textField);
        return box;
    }

    String getText() {
        return textField.getText();
    }
}
