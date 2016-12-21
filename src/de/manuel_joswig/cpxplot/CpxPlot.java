package de.manuel_joswig.cpxplot;

import java.io.File;
import java.io.IOException;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Container;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.UIManager;

import javax.imageio.ImageIO;

import org.nfunk.jep.JEP;
import org.nfunk.jep.type.Complex;

/**
 * Main class that manages the window handling
 * 
 * @author		Manuel Joswig
 * @copyright	2012 Manuel Joswig
 */
public class CpxPlot extends JPanel implements ActionListener, KeyListener {
	private BufferedImage graph;
	private String projectTitle = "CpxPlot", functionalEquation = "", lastFunctionalEquation = "";
	private boolean isPlotted = false;
	private int height, width;
	private double borderX = 0, borderY = 0, zoom = 0.01;
	private long startTime, elapsedTime;
	
	private JEP mathParser = new JEP();
	private Complex z = new Complex();
	
	private JFrame appFrame;
	private JLabel function;
	private JTextField functionInputField;
	private JButton plotButton, saveButton;
	private Graphics g2d;

	public CpxPlot() {
		createWindow();
		initParser();
	}
	
	public void createWindow() {
		appFrame = new JFrame();
		appFrame.setTitle(projectTitle);
		appFrame.setSize(850, 525);
		
		try {
			/* customize the user interface for each operating system
			   UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); */
			
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		}
		catch (Exception e) {
			System.out.println("Error setting nimbus user interface: " + e);
		}
		
		appFrame.setContentPane(this);
		
		Container content = appFrame.getContentPane();
		
		function = new JLabel("f(z) =");
		function.setFont(new Font("Verdana", Font.BOLD, 12));
		function.setSize(50, 50);
		function.setLocation(245, 410);
		
		functionInputField = new JTextField(20);
		functionInputField.setSize(300, 30);
		functionInputField.setLocation(290, 420);
		functionInputField.addKeyListener(this);
		
		plotButton = new JButton("Graph zeichnen");
		plotButton.setSize(165, 25);
		plotButton.setLocation(245, 460);
		plotButton.addActionListener(this);
		plotButton.addKeyListener(this);
		
		saveButton = new JButton("Bild speichern");
		saveButton.setSize(165, 25);
		saveButton.setLocation(425, 460);
		saveButton.setEnabled(false);
		saveButton.addActionListener(this);
		saveButton.addKeyListener(this);
		
		content.setLayout(null);
		content.add(function);
		content.add(functionInputField);
		content.add(plotButton);
		content.add(saveButton);
		
		appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// TODO: setResizable(true) + component listener to adjust the location of the components
		appFrame.setResizable(false);
		appFrame.setLocationRelativeTo(null);
		appFrame.setVisible(true);
	}
	
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == plotButton) {
        	if (!functionInputField.getText().equals("")) {
	            functionalEquation = functionInputField.getText();
	            saveButton.setEnabled(true);
	            
    			if (equationChanged()) {
    				borderX = 0;
			    	borderY = 0;
			    	zoom = 0.01;
    			}
    			else {
    				if (borderX != 0 || borderY != 0 || zoom != 0.01) {
	    				borderX = 0;
				    	borderY = 0;
				    	zoom = 0.01;
    				}
    			}
    			
    			isPlotted = true;
    			repaint();
        	}
        }
        else if (e.getSource() == saveButton) {
        	saveImage("png");
        }
    }
    
    public void keyPressed(KeyEvent e) {
    	int keyCode = e.getKeyCode();
    	
    	if (!functionInputField.getText().equals("")) {
	    	switch (keyCode) {
		    	case KeyEvent.VK_ENTER:
		            functionalEquation = functionInputField.getText();
		            saveButton.setEnabled(true);
		            
		    		if (functionInputField.isFocusOwner()) {
		    			if (equationChanged()) {
		    				borderX = 0;
					    	borderY = 0;
					    	zoom = 0.01;
		    			}
		    			else {
		    				if (borderX != 0 || borderY != 0 || zoom != 0.01) {
			    				borderX = 0;
						    	borderY = 0;
						    	zoom = 0.01;
		    				}
		    			}
		    			
		    			isPlotted = true;
		    			repaint();
		    		}
		    		
		    		break;
	    	}
    	}
    	
    	if (isPlotted || !functionalEquation.equals("")) {
    		switch (keyCode) {
    			case KeyEvent.VK_LEFT:
    				borderX += 100;
    				isPlotted = true;
    				repaint();
    				
    				break;
    			
    			case KeyEvent.VK_RIGHT:
    				borderX -= 100;
    				isPlotted = true;
    				repaint();
    				
    				break;
    			
    			case KeyEvent.VK_UP:
    				borderY += 100;
    				isPlotted = true;
    				repaint();
    				
    				break;
    			
    			case KeyEvent.VK_DOWN:
    				borderY -= 100;
    				isPlotted = true;
    				repaint();
    				
    				break;
    		}
    	}
    }
    
    public void keyTyped(KeyEvent e) {
    }
    
    public void keyReleased(KeyEvent e) {
    }
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g2d = (Graphics2D) g;
		
		g2d.setColor(Color.GRAY);
		g2d.drawLine(0, getHeight() - 100, getWidth(), getHeight() - 100);
		
		if (isPlotted) {
			mathParser.parseExpression(functionalEquation);
			
			if (!mathParser.hasError()) {
				functionInputField.setBackground(Color.WHITE);
				graph = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
					
				appFrame.setTitle(projectTitle + " [.:: RENDERING ::.]");
				startTime = System.currentTimeMillis();
				height = getHeight();
				width = getWidth();
				
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						z.set((x - width / 2 - borderX) * zoom, (y - height / 2 - borderY) * -zoom);
								
						mathParser.setVarValue("z", z);
						z = mathParser.getComplexValue();
						
						graph.setRGB(x, y, getHue(z, 2));
					}
				}
				
				g2d.drawImage(graph, 0, 0, width, height - 100, this);
						
				elapsedTime = System.currentTimeMillis() - startTime;
						
				appFrame.setTitle(projectTitle + " [" + elapsedTime + "ms]");
					
				lastFunctionalEquation = functionalEquation;
			}
			else {
				appFrame.setTitle(projectTitle + " [.:: PARSER ERROR ::.]");
				functionInputField.setBackground(Color.PINK);
				
				System.out.println("Error parsing expression: " + mathParser.getErrorInfo());
			}
			
			isPlotted = false;
		}
		
		g2d.setColor(Color.GRAY);
		g2d.drawLine(0, getHeight() - 100, getWidth(), getHeight() - 100);
	}
	
	private void saveImage(String type) {
    	try {
    		File imageFile = new File("cpxplot-" + getUniqueID() + "." + type);
    		ImageIO.write(graph, type, imageFile);
    	}
    	catch (IOException e2) {
    		System.out.println("Error saving png image: " + e2);
    	}
	}
	
	private void initParser() {
		mathParser.addComplex();
		mathParser.addStandardConstants();
		mathParser.addStandardFunctions();
		mathParser.addVariable("z", 0);
		
		// why do powers (a^b) not works for custom functions... ?
		mathParser.addFunction("tan", new Tangent());
		mathParser.addFunction("erf", new ErrorFunction());
		
		/* mathParser.addFunction("zeta", new Zeta());
		   mathParser.addFunction("gamma", new Gamma()); */
		
		mathParser.setImplicitMul(true);
	}
	
	private boolean equationChanged() {
		if (!functionalEquation.equals(lastFunctionalEquation)) return true;
		return false;
	}
	
	private int getHue(Complex w, int algorithm) {
		double magnitude = w.abs();
		double argument = w.arg();
		
		double[] hsb = {argument / (2 * Math.PI), 0, 0};
		
		algorithm--;
		
		// start calculating the saturation and the brightness
		switch (algorithm) {
			case 0:
				if (magnitude > 1) {
					hsb[2] = 1;
					
					if (magnitude == Double.POSITIVE_INFINITY) {
						hsb[1] = 0;
					}
					else {
						hsb[1] = 1 / (Math.log(magnitude) / 5 + 1);
					}
				}
				else {
					hsb[1] = 1;
					
					if (magnitude == 0) {
						hsb[2] = 0;
					}
					else {
						hsb[2] = 1 / (1 - Math.log(magnitude) / 5);
					}
				}
				
				break;
				
			default:
				double rangeStart = 0;
				double rangeEnd = 1;
				
				if (magnitude == Double.POSITIVE_INFINITY) {
					hsb[1] = 0;
					hsb[2] = 0;
				}
				else {
					while (magnitude > rangeEnd) {
						rangeStart = rangeEnd;
						rangeEnd *= Math.E;
					}
					
					magnitude = (magnitude - rangeStart) / (rangeEnd - rangeStart);
					magnitude = (magnitude < 0.5) ? 2 * magnitude : 2 * (1 - magnitude);
					
					hsb[2] = 1 - 0.6 * magnitude * magnitude * magnitude;
					magnitude = 1 - magnitude;
					hsb[1] = 1 - 0.4 * magnitude * magnitude * magnitude;
					
					if (algorithm != 1) {
						switch (algorithm) {
							case 2:
								hsb[2] = 1 - hsb[2];
								break;
								
							case 3:
								hsb[1] = 1 - hsb[1];
								break;
								
							case 4:
								hsb[2] = 1 - hsb[2];
								hsb[1] = 1 - hsb[1];
								
								break;
						}
					}
				}
				
				break;
		}
		
		int rgb = Color.HSBtoRGB((float) hsb[0], (float) hsb[1], (float) hsb[2]);
		
		return rgb;
	}
	
	private long getUniqueID() {
		// the generated numbers are not very unique but the algorithm is acceptable
		return System.currentTimeMillis() * (int) (Math.random() * 7 + 2);
	}
	
	public static void main(String[] args) {
		new CpxPlot();
	}
}