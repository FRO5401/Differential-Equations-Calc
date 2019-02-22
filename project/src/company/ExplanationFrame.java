package company;

import javax.swing.*;
import java.util.List;
import java.awt.EventQueue;
import java.io.File;
import java.nio.file.Files;

public class ExplanationFrame extends JFrame {
    private static final long serialVersionUID = 1;
    private JPanel contentPane = new JPanel();
    private final int MAX_PAGES;
    private double a, b, c;
    private JTextPane textPane;
    private int pageNumber = 0;
    private double[][] coefficients, orderedPairs;
    private int[] indices;
    private String[] steps = {
            //Translate the equation to a characteristic equation.
            "Translate the equation a * (d<sup>2</sup>y/dx<sup>2</sup>) + b * dy/dx + c * y to a characteristic equation." +
                    "<br>In this problem, you will see how to translate the coefficients into a characteristic (or auxiliary)" +
                    "<br>equation. The characteristic equation is a quadratic that has the same coefficients as the" +
                    "<br>differential equation. (ar<sup>2</sup> + br + c = 0)" +
                    "<br>Therefore, the answer is equal to ",
            "We have to check if the polynomial is factorisable." +
                    "<br>That is, you will be able to see if |b<sup>2</sup> - 4ac| is a square number. ",
            "Apply the solutions you got from the roots into the generic equation.<br>In this case, the generic equation is ",
            "The initial condition can be obtained by taking derivatives if necessary.",
            "Now we have to solve the system of equations that were created by the initial conditions.<br>"
    };


    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            ExplanationFrame frame = new ExplanationFrame(1, 2, -1);
            frame.setVisible(true);
        });
    }


    ExplanationFrame(double a, double b, double c, double[][] coefficients, double[][] orderedPairs, int[] indices) {
        MAX_PAGES = 5;

        this.a = a;
        this.b = b;
        this.c = c;
        this.coefficients = coefficients;
        this.indices = indices;
        this.orderedPairs = orderedPairs;

        setTitle("Explanation");
        setSize(700,700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //This takes into account the initial conditions.

        Box verticalBox = Box.createVerticalBox();

        textPane = new JTextPane();
        textPane.setContentType("text/html");
        update();
        textPane.setEditable(false);

        verticalBox.add(textPane);

        Box horizontalBox = Box.createHorizontalBox();

        JButton btnPrevious = new JButton("Can you go back, please?");
        btnPrevious.addActionListener(e -> {
            if (pageNumber > 0) {
                pageNumber--;
                if (pageNumber == 0)
                    btnPrevious.setVisible(false);
                update();
            }
        });

        JButton btnGotIt = new JButton("Thank you. I got it.");
        btnGotIt.addActionListener(e -> {
            Driver driver = new Driver();
            driver.setVisible(true);
            dispose();
        });
        horizontalBox.add(btnGotIt);
        JButton btnNext = new JButton("Next step, please.");
        btnPrevious.addActionListener(e -> btnNext.setVisible(true));
        btnNext.addActionListener(e -> {
            if (pageNumber < MAX_PAGES) {
                pageNumber++;
                update();
                if (pageNumber > 0) {
                    btnPrevious.setVisible(true);
                }
                if (pageNumber == MAX_PAGES - 1)
                    btnNext.setVisible(false);
            }
        });
        btnPrevious.setVisible(false);
        horizontalBox.add(btnPrevious);
        horizontalBox.add(btnGotIt);
        horizontalBox.add(btnNext);
        verticalBox.add(horizontalBox);
        contentPane.add(verticalBox);
        add(contentPane);

    }
    ExplanationFrame(double a, double b, double c) {
        MAX_PAGES = 3;
        coefficients = null;
        setTitle("Explanation");
        setSize(700,700);

        this.a = a;
        this.b = b;
        this.c = c;

        Box verticalBox = Box.createVerticalBox();

        textPane = new JTextPane();
        textPane.setContentType("text/html");
        update();
        textPane.setEditable(false);

        verticalBox.add(textPane);

        Box horizontalBox = Box.createHorizontalBox();

        JButton btnPrevious = new JButton("Can you go back, please?");
        btnPrevious.addActionListener(e -> {
            if (pageNumber > 0) {
                pageNumber--;
                if (pageNumber == 0)
                    btnPrevious.setVisible(false);
                update();
            }
        });

        JButton btnGotIt = new JButton("Thank you. I got it.");
        btnGotIt.addActionListener(e -> {
            Driver driver = new Driver();
            driver.setVisible(true);
            dispose();
        });
        JButton btnNext = new JButton("Next step, please.");
        btnPrevious.addActionListener(e -> btnNext.setVisible(true));
        btnPrevious.setVisible(false);
        btnNext.addActionListener(e -> {
            if (pageNumber < MAX_PAGES) {
                pageNumber++;
                update();
                if (pageNumber > 0) {
                    btnPrevious.setVisible(true);
                }
                if (pageNumber == MAX_PAGES - 1)
                    btnNext.setVisible(false);
            }
        });
        btnPrevious.setVisible(false);

        horizontalBox.add(btnPrevious);
        horizontalBox.add(btnGotIt);
        horizontalBox.add(btnNext);
        verticalBox.add(horizontalBox);
        contentPane.add(verticalBox);
        add(contentPane);

    }

    private void update() {
        List<String> lines;
        String answer;
        boolean factorisable = (Math.sqrt(Math.abs(b * b - 4 * a * c)) % 1 == 0);
        boolean hasRealRoots = b * b >= 4 * a * c;
        double[] roots = {(-b - Math.sqrt(Math.abs(b * b - 4 * a * c))) / (2 * a), (-b + Math.sqrt(Math.abs(b * b - 4 * a * c))) / (2 * a)};
        double discriminant = b * b - 4 * a * c;
        double real = -b / (2 * a);
        double imaginary = 0;
        String[][] solutions =
                {
                        {"C<sub>1</sub>e<sup>%.5fx</sup> + C<sub>2</sub>e<sup>%.5fx</sup>", // Discriminant > 0 --> f(x)
                                "%1$.5fC<sub>1</sub>e<sup>%1$.5fx</sup> + (%2$.5f)C<sub>2</sub>e<sup>%2$.5fx</sup>", // Discriminant > 0 --> f'(x)
                                "%.5fC<sub>1</sub>e<sup>%.5fx</sup> + %.5fC<sub>2</sub>e<sup>%.5fx</sup>"}, //Discriminant > 0 --> f''(x)
                        {"C<sub>1</sub>e<sup>%1$.5fx</sup> + C<sub>2</sub>xe<sup>%1$.5fx</sup>", // Discriminant == 0 --> f(x)
                                "%1$.5fC<sub>1</sub>e<sup>%1$.5fx</sup> + C<sub>2</sub>(%1$.5fx + 1)e<sup>%1$.5fx</sup>", //Discriminant == 0 --> f'(x)
                                "%1$.5fC<sub>1</sub>e<sup>%2$.5fx</sup> + C<sub>2</sub>(%1$.5fx + (%3$.5f)e<sup>%2$.5fx</sup>"}, //Discriminant == 0 --> f''(x)
                        {"C<sub>1</sub>e<sup>%1$.5fx</sup>cos(%2$.5fx) + C<sub>2</sub>e<sup>%1$.5fx</sup>sin(%2$.5fx)", //Discriminant < 0 --> f(x)
                                "C<sub>1</sub>e<sup>%1$.5fx</sup>(%1$.5fcos(%2$.5fx) - (%2$.5f)sin(%2$.5fx)) +<br>C<sub>2</sub>e<sup>%1$.5fx</sup>(%2$.5fcos(%2$.5fx) + (%1$.5f)sin(%2$.5fx))", //Discriminant < 0 --> f'(x)
                                "C<sub>1</sub>e<sup>%1$.5fx</sup>(%3$.5fcos(%2$.5fx) - (%4$.5f)sin(%2$.5fx)) +<br>C<sub>2</sub>e<sup>%1$.5fx</sup>(%4$.5fcos(%2$.5fx) + (%3$.5f)sin(%2$.5fx))"} //Discriminant < 0 --> f''(x)
                };
        String[] particularSolutions = {
                "%.5fe<sup>%.5fx</sup> + (%.5f)e<sup>%.5fx</sup>",
                "%1$.5fe<sup>%2$.5fx</sup> + (%3$.5f)xe<sup>%2$.5fx</sup>",
                "%1$.5fe<sup>%2$.5fx</sup>cos(%3$.5fx) + (%4$.5f)e<sup>%2$.5fx</sup>sin(%3$.5fx)"
        };
        int highestOrder = 0;
        if (indices != null) {
            highestOrder = Math.max(indices[0], indices[1]);
        }
        switch (pageNumber) {
            case 0:
                //Equation.
                answer = String.format("%.5fr<sup>2</sup> + (%.5f)r + (%.5f)", a, b, c);
                break;
            case 1:
                //Checks whether or not the answer is factorisable or has real roots.
                answer = "<br>In this case, the polynomial is "+
                        (factorisable ? "" : "not") + " factorisable" +
                        ((factorisable && hasRealRoots || !factorisable && !hasRealRoots) ? " and " : " but ") +
                        (hasRealRoots ? "has real roots" : "has no real roots.") +
                        "<br>Therefore, the answer is calculated by using the formula " +
                        (factorisable && hasRealRoots ?
                                //Factorisable and has real roots.
                                String.format("%.5f(r - (%.5f))(r - (%.5f)) = 0<br>\u2234 r = %.5f, %.5f", a, roots[0], roots[1], roots[0], roots[1]) :
                                //Has real roots.
                                hasRealRoots ? String.format("r = (-(%.5f) \u00B1 \u221A(%.5f)) / (%.5f)<br>\u2234 r = %.5f, %.5f", b,
                                        discriminant, 2 * a, roots[0], roots[1]) :
                                //Factorisable.
                                factorisable ? String.format("r = (-(%.5f) \u00B1 (%.5f)i) / (%.5f)<br>\u2234 r = %.5f \u00B1 %.5fi", b,
                                        Math.sqrt(-discriminant), 2 * a, -b / (2 * a), Math.sqrt(-discriminant) / (2 * a)) :
                                //Neither.
                                String.format("<br>r = (-(%.5f) \u00B1 i\u221A%.5f) / (%.5f) = (%.5f) \u00B1 i\u221A%.5f", b, -discriminant, 2 * a,
                                        -b / (2 * a), -discriminant / (4 * a * a)));
                break;
            case 2:
                answer =
                        //Two Real Roots
                        ((discriminant > 0) ? String.format("C<sub>1</sub>e<sup>%.5fx</sup> + C<sub>2</sub>e<sup>%.5fx</sup>", roots[0], roots[1]) :

                                //Repeated Roots
                                (discriminant == 0) ? String.format("C<sub>1</sub>e<sup>%.5fx</sup> + C<sub>2</sub>xe<sup>%.5fx</sup>",
                                        roots[0], roots[0]) :
                                        //Two Complex Roots
                                        String.format("C<sub>1</sub>e<sup>%1$.5fx</sup>cos(%2$.5fx) + C<sub>2</sub>e<sup>%1$.5fx</sup>sin(%2$.5fx)",
                                                -b / (2 * a), Math.sqrt(-discriminant) / (2 * a))
                                );
                break;
            case 3:
                //So that way we won't get a NullPointerException.
                if (discriminant < 0)
                    imaginary = Math.sqrt(-discriminant) / (2 * a);
                if (indices != null) {

                    //Tells whether or not differentiation is necessary and if so to what level it is necessary.
                    answer = "<br>In this case, differentiation " +
                            ((highestOrder == 0) ? "is not necessary.<br>" : "is necessary to the " +
                                    ((highestOrder == 1) ? "first order.<br>" : "second order.<br>")) +

                            /*
                            Derivatives

                            y(x) = C1 * exp(ax) + C2 * exp(bx)
                            y'(x) = C1 * a * exp(ax) + C2 * b * exp(bx)
                            y''(x) = C1 * a^2 * exp(ax) + C2 * b^2 * exp(bx)
                             */

                            //Then, the answer differentiates if necessary.
                            "y(x) = " + (discriminant > 0 ?
                            //The discriminant is positive.
                            String.format(solutions[0][0], roots[0], roots[1]) +

                            //The highest order is first.
                            (((highestOrder >= 1) ? "<br>y\'(x) = " +
                            String.format(solutions[0][1], roots[0], roots[1]) : "") +

                            //The highest order is second.
                            ((highestOrder == 2) ? "<br>y\'\'(x) = " +
                            String.format(solutions[0][2], roots[0] * roots[0], roots[0], roots[1] * roots[1], roots[1]) : "")) :

                            /*

                            Derivatives
                            y(x)   = C1 exp(ax) + C2 * x exp(ax)
                            y'(x)  = C1 * a exp(ax) + C2 * (ax + 1) * exp(ax)
                            y''(x) = C1 * a^2 exp(ax) + C2 * (a^2 * x + 2a) exp(ax)
                            */

                            (discriminant == 0) ?

                                    String.format(solutions[1][0], roots[0]) +

                                    //The highest order is first.
                                    (((highestOrder >= 1) ? "<br>y\'(x) = " +
                                    String.format(solutions[1][1], roots[0]) : "") +

                                    //The highest order is second.
                                    ((highestOrder == 2) ? "<br>y\'\'(x) = " +
                                    String.format(solutions[1][2], roots[0] * roots[0], roots[0], 2 * roots[0]) : "")) :

                                    //The discriminant is negative.
                            /*
                            Derivatives

                            Yes, I am pretty sure that those are correct.

                            y(x) = exp(ax) (C1 cos(bx) + C2 sin(bx)) = C1 cos(bx) exp(ax) + C2 sin(bx) exp(ax)
                            y'(x) = C1 exp(ax) (a cos(bx) - b sin(bx)) + C2 exp(ax) (a sin(bx) + b cos(bx))
                            y''(x) = C1 exp(ax) ((a^2 - b^2) cos(bx) - 2ab sin(bx))
                            + C2 exp(ax) (2ab cos(bx) + (a^2 - b^2) sin(bx))
                             */

                            String.format(solutions[2][0], real, imaginary) +
                            (((highestOrder >= 1) ? "<br>y\'(x) = " +
                            String.format(solutions[2][1], real, imaginary) : "") +
                            ((highestOrder == 2) ? "<br>y\'\'(x) = " +
                            String.format(solutions[2][2], real, imaginary, real * real - imaginary * imaginary, 2 * real * imaginary) : ""))) +

                            //Plug in the initial conditions
                            "<br><br>If we plug in the initial conditions that y" +
                            ((indices[0] == 2) ? "\'\'" : (indices[0] == 1) ? "\'" : "") +
                            String.format("(%.5f) = %.5f", orderedPairs[0][0], orderedPairs[0][1]) +

                            //Then get the following system of equations.
                            " and " + String.format("<br>y%s(%.5f) = %.5f, then we get the following systems of equations below" +
                                    "<br>by substituting what is known and finding the coefficients for the systems of equations:",
                            ((indices[1] == 2) ? "\'\'" : (indices[1] == 1) ? "\'" : ""),
                            orderedPairs[1][0], orderedPairs[1][1]) +
                            "<br><br>" +
                            String.format("%.5fC<sub>1</sub> + (%.5f)C<sub>2</sub> = %.5f<br>", coefficients[0][0], coefficients[0][1], coefficients[0][2]) +
                            String.format("<br>%.5fC<sub>1</sub> + (%.5f)C<sub>2</sub> = %.5f<br>", coefficients[1][0], coefficients[1][1], coefficients[1][2]);
                    break;
                }
            case 4:
                //Solves the equation for C1 and C2 using Cramer's rule.
                //The sweat, blood, and tears for the programmer himself if he
                //has to use programming to explain.

                double a1 = coefficients[0][0];
                double a2 = coefficients[0][1];
                double b1 = coefficients[1][0];
                double b2 = coefficients[1][1];
                double c1 = coefficients[0][2];
                double c2 = coefficients[1][2];
                String equation1 = "%.5fC<sub>1</sub> + (%.5f)C<sub>2</sub> = %.5f";
                String equation2 = "%.5fC<sub>1</sub> + (%.5f)C<sub>2</sub> = %.5f";
                answer = String.format("To do that, we must use Cramer\'s rule if necessary." +
                        "<br>" + equation1 + "<br>" + equation2, a1, a2, orderedPairs[0][1], b1, b2, orderedPairs[1][1]);

                //Multiplies where necessary.
                //If any one coefficient is equal to one:
                if (a1 * b2 - a2 * b1 == 0) {
                    if (c1 == c2) {
                        answer += String.format("<br>There are infinitely many solutions." +
                                "<br>For this example, C<sub>1</sub> = (%.5f)C<sub>2</sub> + %.5f", -a2 / a1, c1 / a1);
                    }
                    else {
                        a1 /= b1;
                        a2 /= b1;
                        c1 /= b1;

                        answer += String.format("<br>The system of equations has no solution because" +
                                "<br>%.5fC<sub>1</sub> + %.5fC<sub>2</sub> cannot equal %.5f and %.5f at the same time", a1, a2, c1, c2);
                    }
                } else {
                    answer += String.format("<br>Use Cramer\'s rule for matrices to get both answers." +
                                    "<br>C<sub>2</sub> = det([[c<sub>1</sub>, b<sub>1</sub>], [c<sub>2</sub>, b<sub>2</sub>]]) / det([[a<sub>1</sub>, b<sub>1</sub>], [a<sub>2</sub>, b<sub>2</sub>]])" +
                                    "<br>C<sub>1</sub> = det([[%3$.5f, %2$.5f], [%6$.5f, %5$.5f]]) / det([[%1$.5f, %2$.5f], [%4$.5f, %5$.5f]])" +
                                    "<br>C<sub>1</sub> = (%3$.5f * %5$.5f - %2$.5f * %6$.5f) / (%1$.5f * %5$.5f - %2$.5f * %4$.5f)" +
                                    "<br>C<sub>1</sub> = (%7$.5f - %8$.5f) / (%9$.5f - %10$.5f)" +
                                    "<br>C<sub>1</sub> = %11$.5f / %12$.5f" +
                                    "<br>C<sub>1</sub> = %13$.5f"
                            , a1, a2, c1, b1, b2, c2, c1 * b2, a2 * c2, a1 * b2, a2 * b1, c1 * b2 - a2 * c2, a1 * b2 - a2 * b1,
                            (c1 * b2 != a2 * c2) ? (c1 * b2 - a2 * c2) / (a1 * b2 - a2 * b1) : 0);
                    answer += String.format(
                                    "<br>C<sub>2</sub> = det([[a<sub>1</sub>, c<sub>1</sub>], [a<sub>2</sub>, c<sub>1</sub>]]) / det([[a<sub>1</sub>, b<sub>1</sub>], [a<sub>2</sub>, b<sub>2</sub>]])" +
                                    "<br>C<sub>2</sub> = det([[%1$.5f, %3$.5f], [%4$.5f, %6$.5f]]) / det([[%1$.5f, %2$.5f], [%4$.5f, %5$.5f]])" +
                                    "<br>C<sub>2</sub> = (%1$.5f * %6$.5f - %3$.5f * %4$.5f) / (%1$.5f * %5$.5f - %2$.5f * %4$.5f)" +
                                    "<br>C<sub>2</sub> = (%7$.5f - %8$.5f) / (%9$.5f - %10$.5f)" +
                                    "<br>C<sub>2</sub> = %11$.5f / %12$.5f" +
                                    "<br>C<sub>2</sub> = %13$.5f"
                            , a1, a2, c1, b1, b2, c2, a1 * c2, c1 * b1, a1 * b2, a2 * b1, a1 * c2 - c1 * b1, a1 * b2 - a2 * b1,
                            (a1 * c2 != c1 * b1) ? (a1 * c2 - c1 * b1) / (a1 * b2 - a2 * b1) : 0);

                    double constant1 = (c1 * b2 != a2 * c2) ? (c1 * b2 - a2 * c2) / (a1 * b2 - a2 * b1) : 0,
                            constant2 = (a1 * c2 != c1 * b1) ? (a1 * c2 - c1 * b1) / (a1 * b2 - a2 * b1) : 0;
                    answer += String.format("<br><br>\u2234 C<sub>1</sub> = %.5f, C<sub>2</sub> = %.5f, and y(x) = "
                                    + ((discriminant > 0) ?
                                    String.format(particularSolutions[0], constant1, roots[0], constant2, roots[1]) :
                                    (discriminant == 0) ? String.format(particularSolutions[1], constant1, roots[0], constant2) :
                                    String.format(particularSolutions[2], constant1, real, imaginary, constant2)), constant1, constant2);
                }
                break;
            default:
                answer = "";
                break;
        }
        try {
            lines = Files.readAllLines(new File("Explanation").toPath());
        }  catch (Exception e) {
            e.printStackTrace();
            return;
        }
        textPane.setText("<html><u>" + lines.get(pageNumber) + "</u><br><br>" + steps[pageNumber] + answer + ".</html>");
    }
}