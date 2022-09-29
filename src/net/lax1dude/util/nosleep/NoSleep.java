package net.lax1dude.util.nosleep;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class NoSleep extends JFrame {
	
	private JLabel toggleLabel;
	private JButton toggleButton;
	
	private boolean enabled = true;
	
	private long lastMove;
	private final Robot robot;

	private static final Color enabledColor = new Color(0x007700);
	private static final Color disabledColor = new Color(0x990000);
	
	private static final Random rand = new Random();
	
	private NoSleep() throws AWTException {
		setTitle("NoSleep");
		setBounds(100, 100, 240, 130);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setAlwaysOnTop(true);
		setResizable(false);
		setType(Type.POPUP);
		getContentPane().setBackground(Color.WHITE);
		getContentPane().setLayout(null);
		
		toggleLabel = new JLabel("NoSleep is enabled");
		toggleLabel.setBounds(10, 13, 200, 22);
		toggleLabel.setFont(toggleLabel.getFont().deriveFont(15.0f));
		toggleLabel.setForeground(enabledColor);
		toggleLabel.setHorizontalAlignment(JLabel.CENTER);
		getContentPane().add(toggleLabel);
		
		toggleButton = new JButton("Disable");
		toggleButton.setBounds((215 - 70) / 2, 47, 70, 23);
		toggleButton.setFont(toggleButton.getFont().deriveFont(12.0f));
		toggleButton.setMargin(new Insets(0, 0, 0, 0));
		
		toggleButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				enabled = !enabled;
				toggleLabel.setText("NoSleep is " + (enabled ? "enabled" : "disabled"));
				toggleLabel.setForeground(enabled ? enabledColor : disabledColor);
				toggleButton.setText(enabled ? "Disable" : "Enable");
				lastMove = System.currentTimeMillis() - 10000l;
			}
			
		});
		
		getContentPane().add(toggleButton);
		
		lastMove = System.currentTimeMillis() - 10000l;
		robot = new Robot();
		
		Thread eventThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while(true) {
					try {
						if(enabled) {
							eventLoop();
						}
						Thread.sleep(200l);
					}catch(Throwable t) {
						t.printStackTrace();
					}
				}
			}
			
		}, "Mouse Event Loop");
		
		eventThread.setDaemon(true);
		eventThread.start();
	}
	
	private void eventLoop() throws Throwable {
		long millis = System.currentTimeMillis();
		
		if(millis - lastMove > 15000l) {
			lastMove = millis;
			
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Point mouseStart = MouseInfo.getPointerInfo().getLocation();
			
			float mouseDirectionX = (rand.nextFloat() * 220.0f - 110.0f) * screenSize.width / 1000.0f;
			float mouseDirectionY = (rand.nextFloat() * 220.0f - 110.0f) * screenSize.height / 1000.0f;
			
			int centerOffsetX = (int)(screenSize.getWidth() / 2 - mouseStart.x);
			int centerOffsetY = (int)(screenSize.getHeight() / 2 - mouseStart.y);
			float m = (float) Math.sqrt(centerOffsetX * centerOffsetX + centerOffsetY * centerOffsetY);
			m = (float) Math.pow(m, 0.7);
			centerOffsetX /= m;
			centerOffsetY /= m;
			
			mouseDirectionX += (centerOffsetX * (100.0f + rand.nextFloat() * 40.0f));
			mouseDirectionY += (centerOffsetY * (100.0f + rand.nextFloat() * 40.0f));
			
			float mouseCurveX = rand.nextFloat() * 2.6f + 0.15f;
			mouseCurveX = mouseCurveX * mouseCurveX;
			float mouseCurveY = rand.nextFloat() * 2.6f + 0.15f;
			mouseCurveY = mouseCurveY * mouseCurveY;
			
			for(float i = 0.0f; i < 1.0f; i += 0.01f) {
				float j = i * i;
				robot.mouseMove((int)(mouseStart.getX() + mouseDirectionX * Math.pow(j, mouseCurveX)),
						(int)(mouseStart.getY() + mouseDirectionY * Math.pow(j, mouseCurveY)));
				Thread.sleep(25l);
			}
			
		}
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
						| UnsupportedLookAndFeelException e) {
				}
				try {
					(new NoSleep()).setVisible(true);
				}catch(Throwable t) {
					JOptionPane.showMessageDialog(null, "NoSleep failed to start! " + t.toString(),
							"NoSleep", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

}
