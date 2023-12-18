/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java2ddrawingapplication;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.Paint;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author acv
 */
public class DrawingApplicationFrame extends JFrame
{

    // Create the panels for the top of the application. One panel for each
    // line and one to contain both of those panels.
    private final JPanel topPanel = new JPanel();
    private final JPanel line1 = new JPanel();
    private final JPanel line2 = new JPanel();
    private final DrawPanel drawPanel = new DrawPanel();

    // create the widgets for the firstLine Panel.
    public JLabel shape = new JLabel("Shape: ");
    public JButton color1 = new JButton("1st Color...");
    public JButton color2 = new JButton("2nd Color...");
    public JButton undo = new JButton("Undo");
    public JButton clear = new JButton("Clear");
    public JComboBox jcb = new JComboBox();

    //create the widgets for the secondLine Panel.
    public JLabel options = new JLabel("Opitions: ");
    public JLabel dashLength = new JLabel("Dash Length:");
    public JLabel  lineWidth = new JLabel("Line Width:");
    public JCheckBox gradient = new JCheckBox("Use Gradient");
    public JCheckBox dashed = new JCheckBox("Dashed");
    public JCheckBox fill = new JCheckBox("Filled");
    public JSpinner spin1 = new JSpinner(new SpinnerNumberModel(0,0,9,1));
    public JSpinner spin2 = new JSpinner(new SpinnerNumberModel(0,0,20,1));
    
    // Variables for drawPanel.
    public ArrayList<MyShapes> shapes = new ArrayList<>();;
    public Color c1 = Color.BLACK;
    public Color c2 = Color.BLACK;

    // add status label
    public JLabel statusLabel = new JLabel();
  
    // Constructor for DrawingApplicationFrame
    public DrawingApplicationFrame()
    {
        // add widgets to panels
        topPanel.setLayout(new BorderLayout());
        line1.setLayout(new FlowLayout());
        line2.setLayout(new FlowLayout());
        
        // firstLine widgets
        line1.add(shape);
        line1.add(jcb);
        jcb.addItem("Line");
        jcb.addItem("Oval");
        jcb.addItem("Rectangle");
        line1.add(color1);
        line1.add(color2);
        line1.add(undo);
        line1.add(clear);
        line1.setBackground(Color.CYAN);
        
        // secondLine widgets
        line2.add(options);
        line2.add(fill);
        line2.add(gradient);
        line2.add(dashed);
        line2.add(lineWidth);
        line2.add(spin1);
        line2.add(dashLength);
        line2.add(spin2);
        line2.setBackground(Color.CYAN);

        // add top panel of two panels
        topPanel.add(line1, BorderLayout.NORTH);
        topPanel.add(line2, BorderLayout.SOUTH);
        
        // add topPanel to North, drawPanel to Center, and statusLabel to South
        add(topPanel, BorderLayout.NORTH);
        add(drawPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        
        //add listeners and event handlers
        color1.addActionListener(listener -> {
            Color temp = c1;
            c1 = JColorChooser.showDialog(null, "Set Color 1", c1);
            if (c1 == null)
            {
                c1 = temp;
            }
        });
        color2.addActionListener(listener -> {
            Color temp = c2;
            c2 = JColorChooser.showDialog(null, "Set Color 2", c2);
            if (c2 == null)
            {
                c2 = temp;
            }
        });
        undo.addActionListener(listener -> {
            if(!shapes.isEmpty())
            {
                shapes.remove(shapes.size() - 1);
                drawPanel.repaint();
            }
        });
        clear.addActionListener(listener -> {
            shapes.clear();
            drawPanel.repaint();
        });
        
    }

    // Create event handlers, if needed

    // Create a private inner class for the DrawPanel.
    private class DrawPanel extends JPanel
    {
        Point startPoint;
        ArrayList<MyShapes> temp = new ArrayList<>();

        public DrawPanel()
        {
            setBackground(Color.WHITE);
            addMouseListener(new MouseHandler());
            addMouseMotionListener(new MouseHandler());
        }

        private MyShapes buildShape(Point start, Point end)
        {
            BasicStroke strk = dashed.isSelected() 
                ? new BasicStroke(Integer.parseInt(spin1.getValue().toString()), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, new float[]{Float.parseFloat(spin2.getValue().toString())}, 0) 
                : new BasicStroke(Integer.parseInt(spin1.getValue().toString()), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND); 
            Paint paint = gradient.isSelected() 
                ? new GradientPaint(0, 0, c1, 50, 50, c2, true) 
                : new GradientPaint(0, 0, c1, 50, 50, c1, true);

            switch(jcb.getSelectedItem().toString())
            {
                case "Line":
                    return new MyLine(start, end, paint, strk);
                case "Oval":
                    return new MyOval(start, end, paint, strk, fill.isSelected());
                case "Rectangle":
                    return new MyRectangle(start, end, paint, strk, fill.isSelected());
                default:
                    return null;
            }
        }

        @Override
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            //loop through and draw each shape in the shapes arraylist
            for (MyShapes shape : shapes) {
                shape.draw(g2d);
            }

            for (MyShapes shape : temp) {
                shape.draw(g2d);
            }
            temp.clear();
        }

        private class MouseHandler extends MouseAdapter implements MouseMotionListener
        {
            
            @Override
            public void mousePressed(MouseEvent event)
            {
                startPoint = event.getPoint();
            }
            
            @Override
            public void mouseReleased(MouseEvent event)
            {
                MyShapes currShape = buildShape(startPoint, event.getPoint());
                if (currShape != null)
                {
                    shapes.add(currShape);
                    drawPanel.repaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent event)
            {
                statusLabel.setText("(" + event.getX() + "," + event.getY() + ")");
                MyShapes currShape = buildShape(startPoint, event.getPoint());
                if (currShape != null)
                {
                    temp.add(currShape);
                    drawPanel.repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent event)
            {
                statusLabel.setText("(" + event.getX() + "," + event.getY() + ")");
            }
        }
    }
}