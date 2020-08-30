package com.hmi.app;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;

public class MouseListener implements NativeMouseListener {

	
	static JFrame frame = new JFrame("Drag");
	MouseInputListener mouseInputListener;
	
	public MouseListener(MouseInputListener mouseInputListener) {
		this.mouseInputListener = mouseInputListener;
		LogManager.getLogManager().reset();
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
	}

	public static void main(String [] args) throws NativeHookException {
		
	}

	@Override
	public void nativeMouseClicked(NativeMouseEvent arg0) {
		
		
	}

	@Override
	public void nativeMousePressed(NativeMouseEvent arg0) {
		
		
	}

	@Override
	public void nativeMouseReleased(NativeMouseEvent arg0) {
		mouseInputListener.released = true;
	}


}
