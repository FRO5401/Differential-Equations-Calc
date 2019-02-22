package company;
import javax.swing.*;
import java.text.DecimalFormat;
import java.awt.EventQueue;
import java.awt.BorderLayout;

public class Driver extends JFrame {

    private static final long serialVersionUID = 3141743659276338823L;

    public static void main(String[] args) {
        //
        EventQueue.invokeLater(() -> {
            try {
                Driver driver = new Driver();
                driver.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    Driver() {
        JPanel contentPane = new JPanel();
        setTitle("Differential Equations Calculator");
        setSize(800,500);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Box box = Box.createVerticalBox();

        JLabel lblCredits = new JLabel("You can thank Patrick for this calculator.");
        box.add(lblCredits);

        JLabel lblNote1 = new JLabel("Note that you have to provide both initial conditions in order to get the particular solution.");
        box.add(lblNote1);

        JLabel lblNote2 = new JLabel("Example: If you want the condition y(0) = 3, then put the setting at (x, y) and put down \"(0, 3)\"");
        box.add(lblNote2);

        //Label and TextBox 1
        LabelTextField field1 = new LabelTextField("Mass/Capacitor Voltage (A)");
        Box box1 = field1.get();
        box.add(box1);

        //Label and TextBox 2
        LabelTextField field2 = new LabelTextField("Damping Coefficient/Resistor Voltage (B)");
        Box box2 = field2.get();
        box.add(box2);

        //Label and TextBox 3
        LabelTextField field3 = new LabelTextField("Spring Constant/Inductor Voltage (C))");
        Box box3 = field3.get();
        box.add(box3);

        ComboTextField comboField1 = new ComboTextField("Initial Condition 1:", "(x, y) =", "(x, dy/dx) = ", "<html>(x, d<sup>2</sup>y/dx<sup>2</sup>) =</html>");
        Box box4 = comboField1.get();
        box.add(box4);

        ComboTextField comboField2 = new ComboTextField("Initial Condition 2:", "(x, y) =", "(x, dy/dx) = ", "<html>(x, d<sup>2</sup>y/dx<sup>2</sup>) =</html>");
        Box box5 = comboField2.get();
        box.add(box5);

        String equation = "<html>Equation: a * (d<sup>   2</sup>y/dx<sup>  2</sup>) + b * (dy/dx) + c * y = 0</html>";
        JLabel equationLabel = new JLabel(equation);
        box.add(equationLabel);

        JButton button = new JButton("Submit");

        button.addActionListener(e -> {
            //Parse the doubles.
            DecimalFormat format = new DecimalFormat("#.#####");
            double a, b, c;

            try {
                a = Double.parseDouble(field1.getText());
                b = Double.parseDouble(field2.getText());
                c = Double.parseDouble(field3.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please try again and enter the numbers correctly.");
                return;
            }

            //Discriminant and the vertex
            double discriminant = b * b - 4 * a * c;
            double reducedDiscriminant = Math.sqrt(Math.abs(discriminant)) / (2 * a);
            double vertex = -b / (2 * a);
            double[] roots = new double[]{vertex + reducedDiscriminant, vertex - reducedDiscriminant};

            String strVertex = format.format(vertex);
            if (strVertex.equals("1")) {
                strVertex = "";
            } else if (strVertex.equals("-1")) {
                strVertex = "-";
            }
            String strReducedDiscriminant = format.format(Math.sqrt(Math.abs(discriminant)) / (2 * a));
            String[] strRoots = new String[] {roots[0] + "", roots[1] + ""};
            for (int i = 0; i < 2; i++) {
                String str = strRoots[i];
                if (str.equals("1")) {
                    str = "";
                } else if (str.equals("-1")) {
                    str = "-";
                }
                strRoots[i] = str;
            }
            String answer = "<html><u>Generic Answer</u><br><br>";
            int sign = (int) Math.signum(discriminant);
            switch (sign) {
                case -1:
                    answer += ("C <sub>1</sub>cos(" + strReducedDiscriminant + "x)e<sup>" + strVertex + "x</sup> + C <sub>2</sub>sin("
                            + strReducedDiscriminant + "x)e<sup>" + strVertex + "x</sup>"); break;
                case 0:
                    answer += ("C <sub>1</sub>e<sup>" + strVertex + "x</sup> + C <sub>2</sub>xe<sup>" + strVertex + "x</sup>"); break;
                default:
                    answer += ("C <sub>1</sub>e<sup>" + strRoots[0] + "x</sup> + C <sub>2</sub>e<sup>" + strRoots[1] + "x</sup>"); break;
            }
            double[][] orderedPairs = new double[2][2];
            String text1 = comboField1.getText();
            String text2 = comboField2.getText();

            if (text1.equals("") || text2.equals("")) {
                answer += "<br>Would you like an explanation?</html>";
                int result = JOptionPane.showConfirmDialog(null, answer, "Here is your solution", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    ExplanationFrame frame = new ExplanationFrame(a, b, c);
                    frame.setVisible(true);
                    setVisible(false);
                }
                return;
            }

            String[][] rawValues = new String[][] {text1.substring(1, text1.length() - 1).split(","),
                    text2.substring(1, text2.length() - 1).split(",")};
            try {
                for (int i1 = 0; i1 < rawValues.length; i1++) {
                    for (int i2 = 0; i2 < rawValues[i1].length; i2++) {
                        orderedPairs[i1][i2] = Double.parseDouble(rawValues[i1][i2]);
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please try again and enter the numbers correctly.");
                return;
            }

            int index1 = comboField1.getSelectedIndex();
            int index2 = comboField2.getSelectedIndex();
            int[] indices = {index1, index2};
            //Functions - Real Roots

            //To store initial condition
            double x;

            //To store the system of equations.
            double[][] coefficients = new double[2][3];

            //Then, apply for the initial conditions.
            if (discriminant > 0) {
                for (int i = 0; i < 2; i++) {
                    x = orderedPairs[i][0];
                    coefficients[i][0] = Math.exp(roots[0] * x) * Math.pow(roots[0], indices[i]);
                    coefficients[i][1] = Math.exp(roots[1] * x) * Math.pow(roots[1], indices[i]);
                    coefficients[i][2] = orderedPairs[i][1];
                }
            } else if (discriminant == 0) {
                /*
                Derivatives

                y(x)   = C1 exp(ax) + C2 * x exp(ax)
                y'(x)  = C1 * a exp(ax) + C2 * (ax + 1) * exp(ax)
                y''(x) = C1 * a^2 exp(ax) + C2 * (a^2 * x + 2a) exp(ax)
                 */
                double root = roots[0];
                for (int i = 0; i < 2; i++) {
                    x = orderedPairs[i][0];
                    switch (indices[i]) {
                        case 0:
                            coefficients[i][0] = Math.exp(root * x);
                            coefficients[i][1] = x * Math.exp(root * x);
                            break;
                        case 1:
                            coefficients[i][0] = root * Math.exp(root * x);
                            coefficients[i][1] = (root * x + 1) * Math.exp(root * x);
                            break;
                        case 2:
                            coefficients[i][0] = Math.pow(root, 2) * Math.exp(root * x);
                            coefficients[i][1] = (Math.pow(root, 2) * x + 2 * root) * Math.exp(root * x);
                            break;
                    }
                    coefficients[i][2] = orderedPairs[i][1];
                }
            } else {
                //Discriminant < 0

                /*
                Derivatives

                Yes, I am pretty sure that those are correct.

                y(x) = exp(ax) (C1 cos(bx) + C2 sin(bx)) = C1 cos(bx) exp(ax) + C2 sin(bx) exp(ax)
                y'(x) = C1 exp(ax) (a cos(bx) - b sin(bx)) + C2 exp(ax) (a sin(bx) + b cos(bx))
                y''(x) = C1 exp(ax) ((a^2 - b^2) cos(bx) - 2ab sin(bx))
                        + C2 exp(ax) ((a^2 - b^2) sin(bx) + 2ab cos(bx)
                 */
                double[] numbers = {vertex, reducedDiscriminant};
                for (int i = 0; i < 2; i++) {
                    x = orderedPairs[i][0];

                    //Discriminant
                    switch (indices[i]) {
                        case 0:
                            coefficients[i][0] = Math.exp(numbers[0] * x) * Math.cos(numbers[1] * x);
                            coefficients[i][1] = Math.exp(numbers[0] * x) * Math.sin(numbers[1] * x);
                            break;
                        case 1:
                            coefficients[i][0] = Math.exp(numbers[0] * x) * (numbers[0] * Math.cos(numbers[1] * x)
                                    - numbers[1] * Math.sin(numbers[1] * x));
                            coefficients[i][1] = Math.exp(numbers[0] * x) * (numbers[0] * Math.sin(numbers[1] * x)
                                    + numbers[1] * Math.cos(numbers[1] * x));
                            break;
                        case 2:
                            coefficients[i][0] = Math.exp(numbers[0] * x) * ((Math.pow(numbers[0], 2) - Math.pow(numbers[1], 2)) * Math.cos(numbers[1] * x)
                                    - 2 * numbers[0] * numbers[1] * Math.sin(numbers[1] * x));
                            coefficients[i][1] = Math.exp(numbers[0] * x) * ((Math.pow(numbers[0], 2) - Math.pow(numbers[1], 2)) * Math.sin(numbers[1] * x)
                                    + 2 * numbers[0] * numbers[1] * Math.cos(numbers[1] * x));
                            break;
                    }
                    coefficients[i][2] = orderedPairs[i][1];
                }
            }
            //Then, use those coefficients to solve the equation.

            //a1 * C1 + a2 * C2 = a3
            //b1 * C1 + b2 * C2 = b3

            //Solution:
            //C1 = (a3 * b2 - a2 * b3) / (a1 * b2 - a2 * b1)
            //C2 = (a1 * b3 - a3 * b1) / (a1 * b2 - a2 * b1)

            double determinant = coefficients[0][0] * coefficients[1][1] - coefficients[1][0] * coefficients[0][1];

            if (determinant == 0) {
                if (coefficients[0][2] != coefficients[1][2]) {
                    answer += "<br><br><u>Particular Solution</u><br>No solution</html>";
                    answer += "<br>Would you like an explanation?</html>";
                    int result = JOptionPane.showConfirmDialog(null, answer, "Here is your solution", JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        ExplanationFrame frame = new ExplanationFrame(a, b, c, coefficients, orderedPairs, new int[] {index1, index2});
                        frame.setVisible(true);
                        setVisible(false);
                    }
                } else {
                    answer += String.format("<br><br><u>Particular Solution</u><br>Infinitely many solutions" +
                            "<br>C<sub>1</sub> = %.5fC<sub>2</sub> + (%.5f)", -coefficients[0][1] / coefficients[0][0], coefficients[0][2] / coefficients[0][0]);
                    answer += "<br>Would you like an explanation?</html>";
                    int result = JOptionPane.showConfirmDialog(null, answer, "Here is your solution", JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        ExplanationFrame frame = new ExplanationFrame(a, b, c, coefficients, orderedPairs, new int[] {index1, index2});
                        frame.setVisible(true);
                        setVisible(false);
                    }
                }

                return;
            }

            String[] particularSolutions = {
                    "%.5fe<sup>%.5fx</sup> + (%.5f)e<sup>%.5fx</sup>",
                    "%1$.5fe<sup>%2$.5fx</sup> + (%3$.5f)xe<sup>%2$.5fx</sup>",
                    "%1$.5fe<sup>%2$.5fx</sup>cos(%3$.5fx) + (%4$.5f)e<sup>%2$.5fx</sup>sin(%3$.5fx)"
            };

            double c1 = (coefficients[0][2] * coefficients[1][1]
                    - coefficients[0][1] * coefficients[1][2]) / determinant;

            if (c1 == -0.0)
                c1 = 0;

            double c2 = (coefficients[0][0] * coefficients[1][2]
                    - coefficients[0][2] * coefficients[1][0]) / determinant;

            if (c2 == -0.0)
                c2 = 0;

            answer += "<br><br><u>Particular Solution</u>";
            answer += "<br>C<sub>1</sub> = " + format.format(c1);
            answer += "<br>C<sub>2</sub> = " + format.format(c2);
            answer += "<br>y(x) = " + ((discriminant > 0) ? String.format(particularSolutions[0], c1, roots[0], c2, roots[1]) :
                    (discriminant == 0) ? String.format(particularSolutions[1], c1, roots[0], c2) :
                            String.format(particularSolutions[2], c1, -b / (2 * a), Math.sqrt(-discriminant) / (2 * a), c2));

            answer += "<br>Would you like an explanation?</html>";
            int result = JOptionPane.showConfirmDialog(null, answer, "Here is your solution", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                ExplanationFrame frame = new ExplanationFrame(a, b, c, coefficients, orderedPairs, new int[] {index1, index2});
                frame.setVisible(true);
                setVisible(false);
            }
        });

        box.add(button);
        contentPane.add(box);
        setContentPane(contentPane);
    }
}