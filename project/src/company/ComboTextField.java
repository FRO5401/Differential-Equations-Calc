package company;

import javax.swing.*;
import java.awt.*;

class ComboTextField {
    private JLabel label;
    private JComboBox<String> comboBox;
    private JTextField textField;
    ComboTextField(String text, String... items) {
        label = new JLabel(text);
        comboBox = new JComboBox<>();
        if (items == null)
            throw new NullPointerException("There has to be at least one item.");
        for (String item : items) {
            comboBox.addItem(item);
        }
        textField = new JTextField();
    }

    int getSelectedIndex() {
        return comboBox.getSelectedIndex();
    }

    Box get() {
        Box horizontalBox = Box.createHorizontalBox();
        Component rigidArea = Box.createRigidArea(new Dimension(20, 10));
        Component rigidArea2 = Box.createRigidArea(new Dimension(20, 10));
        horizontalBox.add(label);
        horizontalBox.add(rigidArea);
        horizontalBox.add(comboBox);
        horizontalBox.add(rigidArea2);
        horizontalBox.add(textField);
        return horizontalBox;
    }

    String getText() {
        return textField.getText();
    }
}