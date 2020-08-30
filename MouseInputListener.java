package com.hmi.app;

import java.awt.MouseInfo;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

public class MouseInputListener implements NativeMouseInputListener {
	
	static JFrame frame = new JFrame("Drag");
	boolean released = true;
	double mouseX = 0;
	double mouseY = 0;
	MouseListener mouseListener = new MouseListener(this);
	int num = 0;
	
	public MouseInputListener() throws NativeHookException {
		LogManager.getLogManager().reset();
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
		
		GlobalScreen.registerNativeHook();
		GlobalScreen.addNativeMouseMotionListener(this);
		GlobalScreen.addNativeMouseListener(mouseListener);
		
		frame.setSize(150, 150);
	}

	public static void main(String [] args) throws NativeHookException {
		new MouseInputListener();
	}

	@Override
	public void nativeMouseClicked(NativeMouseEvent arg0) {
		
		
	}

	@Override
	public void nativeMousePressed(NativeMouseEvent arg0) {
		
		
	}

	@Override
	public void nativeMouseReleased(NativeMouseEvent arg0) {
		
	}

	@Override
	public void nativeMouseDragged(NativeMouseEvent arg0) {
		if(released) {
			mouseX = MouseInfo.getPointerInfo().getLocation().getX();
			mouseY = MouseInfo.getPointerInfo().getLocation().getY();
			released = false;
		}
//		frame.setLocation((int)mouseX, (int)mouseY);
//		frame.setResizable(false);
//		frame.setVisible(true);
	}

	@Override
	public void nativeMouseMoved(NativeMouseEvent arg0) {
		
	}

}
