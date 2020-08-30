package com.hmi.app;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class KeyListener implements NativeKeyListener {

	private boolean ctrl = false;
	private boolean shortcut = false;

	public KeyListener() throws NativeHookException {
		LogManager.getLogManager().reset();
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);

		GlobalScreen.registerNativeHook();
		GlobalScreen.addNativeKeyListener(this);
	}

	public static void main(String [] args) throws NativeHookException {
		new KeyListener();
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent arg0) {
		if(NativeKeyEvent.getKeyText(arg0.getKeyCode()).toString().equals("Ctrl")) {
			ctrl = true;
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent arg0) {
		if(NativeKeyEvent.getKeyText(arg0.getKeyCode()).toString().equals("Ctrl")){
			ctrl = false;
			setShortcut(false);
		}
		if(NativeKeyEvent.getKeyText(arg0.getKeyCode()).toString().equals("U") || NativeKeyEvent.getKeyText(arg0.getKeyCode()).toString().equals("u")){
			setShortcut(false);
		}
		if(NativeKeyEvent.getKeyText(arg0.getKeyCode()).toString().equals("u") || NativeKeyEvent.getKeyText(arg0.getKeyCode()).toString().equals("U") && ctrl) {
			setShortcut(true);
		}
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent arg0) {
		if((arg0.getKeyChar() == ('u') || arg0.getKeyChar() == ('U')) && ctrl) {
			setShortcut(true);
		}
	}

	public boolean getShortcut() {
		return shortcut;
	}

	public void setShortcut(boolean shortcut) {
		this.shortcut = shortcut;
	}

}
