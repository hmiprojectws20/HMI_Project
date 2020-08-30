package com.hmi.app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jnativehook.NativeHookException;
import org.tensorflow.DataType;
import org.tensorflow.Graph;
import org.tensorflow.Output;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

public class Controller extends JFrame {
	
	private String targetPath = "C:/Users/Sujatha/Desktop/Test";

	private static final long serialVersionUID = 1L;

	private String dataPath;
	private byte[] graphDef;

	private DropPane dp3;
	private String result = "Image contains:\n";
	private ArrayList<ArrayList<String>> categories = new ArrayList<ArrayList<String>>();
	private ArrayList<ArrayList<String>> methods = new ArrayList<ArrayList<String>>();

	private JPanel tray = new JPanel();
	private JPanel tray2 = new JPanel();
	JFrame trayFrame = new JFrame("Images of Different Categories");
	JFrame trayFrame2 = new JFrame("Selected category images");

	private InputStream inStream = null;
	private OutputStream outStream = null;


	private MouseInputListener mouseInputListener; 
	private KeyListener keyListener; 

	private List<String> labels;

	private MouseListener mouseListener1;
	private MouseListener mouseListener2;


	public Controller() throws IOException, InterruptedException, NativeHookException {
		createMouseListener1();
		createMouseListener2();

		mouseInputListener = new MouseInputListener();
		keyListener = new KeyListener();

		dataPath = "Data";
		graphDef = readAllBytesOrExit(Paths.get(dataPath, "graph.pb"));
		labels = readAllLinesOrExit(Paths.get(dataPath, "labels.txt"));
		
		trayFrame.add(new JScrollPane(tray));
		trayFrame2.add(new JScrollPane(tray2));

		setSize(300, 300);
		setLayout(new GridLayout(1, 1));
		setAlwaysOnTop(true);
		setTitle("Welcome");

		
		JLabel copyRec = new JLabel("<html>Drag only images here...</html>");
		copyRec.setHorizontalAlignment(JLabel.CENTER);

	
		dp3 = new DropPane();
		dp3.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		dp3.add(copyRec);

		add(dp3);

		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		while(true) {
			if(keyListener.getShortcut()) {
				tray2.removeAll();
				tray2.updateUI();
				if(trayFrame2.isVisible()) {
					trayFrame2.setVisible(false);
				}
				if(!trayFrame.isVisible()) {
					showContainter();
				}
			}

			while(trayFrame.isVisible()){
				setVisible(false);
			}

			while(trayFrame2.isVisible()) {
				setVisible(false);
				if(keyListener.getShortcut()) {
					tray2.removeAll();
					tray2.updateUI();
					trayFrame2.setVisible(false);
					if(!trayFrame.isVisible()) {
						showContainter();
					}
				}
			}

			if((getLocation().x != (int)mouseInputListener.mouseX-100) || (getLocation().y != (int)mouseInputListener.mouseY-100)) {
				setLocation((int)mouseInputListener.mouseX-100, (int)mouseInputListener.mouseY-100);
			}

			if(!mouseInputListener.released && !isVisible()) {
				setVisible(true);
			}

			
			else if(dp3.getFlag()) {
				setVisible(false);
				copyRec(dp3);
			}
		}
	}

	public void checkArrays() {
		for(int i = 0 ; i < categories.size() ; i++) {
			for(int j = 0 ; j < categories.get(i).size() ; j++) {
				if(categories.get(i).get(j).equals("")) {
					categories.get(i).remove(j);
					j--;
				}
			}
			if(categories.get(i).size() == 1) {
				categories.remove(i);
				i--;
			}
		}
	}
	
	public void showContainter() throws IOException {
		tray2.removeAll();
		tray2.updateUI();
		checkArrays();
		if(categories.isEmpty()) {
			JOptionPane.showMessageDialog(null, "tray is empty. Drag files to the tray\nto view them in the tray.", "Warning", JOptionPane.PLAIN_MESSAGE);
		}
		else {
			tray.removeAll();
			tray.updateUI();
			tray.setLayout(new GridLayout(1, categories.size()));
			for(int i = 0 ; i < categories.size() ; i++) {
				JPanel panel = new JPanel();
				panel.setLayout(new GridLayout(2, 3));
				panel.add(new JLabel(""));
				JLabel className = new JLabel(categories.get(i).get(0));
				className.setHorizontalAlignment(JLabel.CENTER);
				panel.add(className);
				panel.add(new JLabel(""));
				getClassImages(panel, i);
				panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
				panel.addMouseListener(mouseListener1);
				tray.add(panel);
				panel.updateUI();
				tray.updateUI();
			}
			trayFrame.setSize(700, 200);
			trayFrame.setLocationRelativeTo(null);
			trayFrame.setVisible(true);
		}
	}

	public void getClassImages(JPanel panel , int i) throws IOException {
		int j = 1;
		if(categories.get(i).size() == 2) {
			panel.add(new JLabel(""));
		}
		for( ; j <= 3 && j < categories.get(i).size() ; j++) {
			if(!categories.get(i).get(j).equals("")) {
				File file = new File(categories.get(i).get(j));
				Image img = ImageIO.read(file);
				JLabel imglbl = new JLabel();
				int length = 0;
				int width = 0;
				if(categories.size() >= 3) {
					length = 700/(3*3);
					width = 700/(3*3);
				}
				else if(categories.size() < 3) {
					if(700/(categories.size()*3) > 80) {
						length = 80;
						width = 80;
					}
					else {
						length = 700/(categories.size()*3);
						width = 700/(categories.size()*3);
					}
				}
				imglbl.setIcon(new ImageIcon(img.getScaledInstance(length,width,100)));
				imglbl.setHorizontalAlignment(JLabel.CENTER);
				panel.add(imglbl);
			}
		}
		if(categories.get(i).size() == 3 || categories.get(i).size() == 2) {
			panel.add(new JLabel(""));
		}
	}

	public void createContainer2(String className) throws IOException {
		tray2.removeAll();
		tray2.updateUI();
		for(int i = 0 ; i < categories.size() ; i++) {
			int rows = 0;
			if(categories.get(i).get(0).equals(className)) {
				if(categories.get(i).size() <= 9) {
					rows = 3;
				}
				else {
					if((categories.get(i).size()-1) % 3 == 0) {
						rows = ((int)(categories.get(i).size()-1)/3);
					}
					else{
						rows = (((int)(categories.get(i).size()-1)/3)+1);
					}
				}
				tray2.setLayout(new GridLayout(rows, 3));
				int j = 1;
				for( ; j < categories.get(i).size() ; j++) {
					if(!categories.get(i).get(j).equals("")) {
						File file = new File(categories.get(i).get(j));
						Image img = ImageIO.read(file);
						JLabel imglbl = new JLabel();
						imglbl.setIcon(new ImageIcon(img.getScaledInstance(80,80,300)));
						imglbl.setHorizontalAlignment(JLabel.CENTER);
						imglbl.addMouseListener(mouseListener2);
						imglbl.setToolTipText(categories.get(i).get(j));
						imglbl.setBorder(BorderFactory.createLineBorder(Color.BLACK));
						tray2.add(imglbl);
					}
				}
				for( ; j <= rows*3 ; j++) {
					JLabel tmp = new JLabel("");
					tmp.setBorder(BorderFactory.createLineBorder(Color.BLACK));
					tray2.add(tmp);
				}
				break;
			}
		}
		tray2.updateUI();
		trayFrame2.setSize(new Dimension(300, 300));
		trayFrame2.setResizable(false);
		trayFrame2.setLocationRelativeTo(null);
		trayFrame.setVisible(true);
		trayFrame2.setVisible(true);
	}

	public void completeOperation(File file , String operation) throws IOException {
		inStream = new FileInputStream(file);
		outStream = new FileOutputStream(new File(targetPath + file.getName()));
		byte[] buffer = new byte[1024];
		int length;
		while ((length = inStream.read(buffer)) > 0){
			outStream.write(buffer, 0, length);
		}
		inStream.close();
		outStream.close();

		if(operation.equals("cut")) {
			file.delete();
		}

	}

	public void createMouseListener1() {
		mouseListener1 = new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				//container.removeAll();
				tray.updateUI();
				String className = ((JLabel)(((JPanel)arg0.getSource()).getComponent(1))).getText();
				try {
					createContainer2(className);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};
	}

	public void createMouseListener2() {
		mouseListener2 = new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				String path = ((JLabel)arg0.getSource()).getToolTipText();
				int tmp = 0;
				for(int i = 0 ; i < categories.size() ; i++) {
					if(categories.get(i).contains(path)) {
						tmp = categories.get(i).indexOf(path)-1;
						break;
					}
				}
				((JLabel)tray2.getComponent(tmp)).setIcon(null);;
				tray2.updateUI();
				String operation = "";
				for(int i = 0 ; i < categories.size() ; i++) {
					if(categories.get(i).contains(path)) {
						operation = methods.get(i).get(tmp+1);
						categories.get(i).set(categories.get(i).indexOf(path), "");
						break;
					}
				}
				try {
					completeOperation(new File(path) , operation);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	}


	public void copyRec(DropPane dp) throws IOException {
		List<?> files = ((List<?>)(dp.getFiles()));
		for(int x = 0 ; x < files.size() ; x++) {
			File file = (File)(files.get(x));
			byte[] imageBytes = readAllBytesOrExit(Paths.get(file.getAbsolutePath()));
			try (Tensor<?> image = Tensor.create(imageBytes)) {
				float[] labelProbabilities = executeInceptionGraph(graphDef, image);
				for(int i = 0 ; i < 3 ; i++) {
					int bestLabelIdx = maxIndex(labelProbabilities);							
					//String order = "";
					switch(i){
					case 0:
						//order = "(most likely)";
						if(categories.isEmpty()) {
							categories.add(new ArrayList<String>());
							categories.get(0).add(labels.get(bestLabelIdx));
							categories.get(0).add(file.getAbsolutePath());
							methods.add(new ArrayList<String>());
							methods.get(0).add(labels.get(bestLabelIdx));
							methods.get(0).add("copy");
						}
						else {
							for(int j = 0 ; j < categories.size() ; j++) {
								if(categories.get(j).get(0).equals(labels.get(bestLabelIdx))) {
									categories.get(j).add(file.getAbsolutePath());
									methods.get(j).add("copy");
									break;
								}
								else if(j == categories.size()-1) {
									categories.add(new ArrayList<String>());
									categories.get(categories.size()-1).add(labels.get(bestLabelIdx));
									categories.get(categories.size()-1).add(file.getAbsolutePath());
									methods.add(new ArrayList<String>());
									methods.get(categories.size()-1).add(labels.get(bestLabelIdx));
									methods.get(categories.size()-1).add("copy");
									break;
								}
							}
						}
						break;
					case 1:
						//order = "2nd";
						break;
					case 2:
						//order = "3rd";
					}
					//result += String.format("%s "+order, labels.get(bestLabelIdx), labelProbabilities[bestLabelIdx] * 100f);
					//result += "\n";
					labelProbabilities[bestLabelIdx] = 0;
				}
				//JOptionPane.showMessageDialog(null, result, file.getName(), JOptionPane.PLAIN_MESSAGE);
				//result = "";
			}
		}
	}


	public void dp(DropPane dp) {
		List<?> files = ((List<?>)(dp.getFiles()));
		if(files.size() == 1) {
			File file = (File)(files.get(0));
			byte[] imageBytes = readAllBytesOrExit(Paths.get(file.getAbsolutePath()));
			try (Tensor<?> image = Tensor.create(imageBytes)) {
				float[] labelProbabilities = executeInceptionGraph(graphDef, image);
				for(int i = 0 ; i < 3 ; i++) {
					int bestLabelIdx = maxIndex(labelProbabilities);							
					String order = "";
					switch(i){
					case 0:
						order = "(most likely)";
						if(categories.isEmpty()) {
							categories.add(new ArrayList<String>());
							categories.get(0).add(labels.get(bestLabelIdx));
							categories.get(0).add(file.getName());
						}
						else {
							for(int j = 0 ; j < categories.size() ; j++) {
								if(categories.get(j).get(0).equals(labels.get(bestLabelIdx))) {
									categories.get(j).add(file.getName());
									break;
								}
								else if(j == categories.size()-1) {
									categories.add(new ArrayList<String>());
									categories.get(categories.size()-1).add(labels.get(bestLabelIdx));
									categories.get(categories.size()-1).add(file.getName());
								}
							}
						}
						break;
					case 1:
						order = "";
						break;
					case 2:
						order = "";
					}
					result += String.format("%s "+order, labels.get(bestLabelIdx), labelProbabilities[bestLabelIdx] * 100f);
					result += "\n";
					labelProbabilities[bestLabelIdx] = 0;
				}
				JOptionPane.showMessageDialog(null, result, file.getName(), JOptionPane.PLAIN_MESSAGE);
				result = "";
			}
		}
		else if(files.size() > 1) {
			int response = JOptionPane.showConfirmDialog(null, "You selected " + files.size() + " files.\nDo you want to classify them?", "" + "Files Select", JOptionPane.YES_NO_CANCEL_OPTION);
			if(response == JOptionPane.YES_OPTION) {
				for(int x = 0 ; x < files.size() ; x++) {
					File file = (File)(files.get(x));
					byte[] imageBytes = readAllBytesOrExit(Paths.get(file.getAbsolutePath()));
					try (Tensor<?> image = Tensor.create(imageBytes)) {
						float[] labelProbabilities = executeInceptionGraph(graphDef, image);
						for(int i = 0 ; i < 3 ; i++) {
							int bestLabelIdx = maxIndex(labelProbabilities);							
							String order = "";
							switch(i){
							case 0:
								order = "(most likely)";
								if(categories.isEmpty()) {
									categories.add(new ArrayList<String>());
									categories.get(0).add(labels.get(bestLabelIdx));
									categories.get(0).add(file.getName());
								}
								else {
									for(int j = 0 ; j < categories.size() ; j++) {
										if(categories.get(j).get(0).equals(labels.get(bestLabelIdx))) {
											categories.get(j).add(file.getName());
											break;
										}
										else if(j == categories.size()-1) {
											categories.add(new ArrayList<String>());
											categories.get(categories.size()-1).add(labels.get(bestLabelIdx));
											categories.get(categories.size()-1).add(file.getName());
											break;
										}
									}
								}
								break;
							case 1:
								order = "2nd";
								break;
							case 2:
								order = "3rd";
							}
							result += String.format("%s "+order, labels.get(bestLabelIdx), labelProbabilities[bestLabelIdx] * 100f);
							result += "\n";
							labelProbabilities[bestLabelIdx] = 0;
						}
						JOptionPane.showMessageDialog(null, result, file.getName(), JOptionPane.PLAIN_MESSAGE);
						result = "";
					}
				}
			}
			else if(response == JOptionPane.NO_OPTION) {
				for(int x = 0 ; x < files.size() ; x++) {
					File file = (File)(files.get(x));
					if(categories.isEmpty()) {
						categories.add(new ArrayList<String>());
						categories.get(0).add("Not Classified");
						categories.get(0).add(file.getName());
					}
					else {
						for(int j = 0 ; j < categories.size() ; j++) {
							if(categories.get(j).get(0).equals("Not Classified")) {
								categories.get(j).add(file.getName());
								break;
							}
							else if(j == categories.size()-1) {
								categories.add(new ArrayList<String>());
								categories.get(categories.size()-1).add("Not Classified");
								categories.get(categories.size()-1).add(file.getName());
								break;
							}
						}
					}			
				}
			}
			else if(response == JOptionPane.CANCEL_OPTION) {
				//
			}
		}
	}

	private static float[] executeInceptionGraph(byte[] graphDef, Tensor<?> image) {
		try (Graph g = new Graph()) {
			g.importGraphDef(graphDef);
			try (Session s = new Session(g);
					Tensor<?> result = s.runner().feed("DecodeJpeg/contents", image).fetch("softmax").run().get(0)) {
				final long[] rshape = result.shape();
				if (result.numDimensions() != 2 || rshape[0] != 1) {
					throw new RuntimeException(
							String.format(
									"Expected model to produce a [1 N] shaped tensor where N is the number of labels, instead it produced one with shape %s",
									Arrays.toString(rshape)));
				}
				int nlabels = (int) rshape[1];
				return result.copyTo(new float[1][nlabels])[0];
			}
		}
	}

	private static int maxIndex(float[] probabilities) {
		int best = 0;
		for (int i = 1; i < probabilities.length; ++i) {
			if (probabilities[i] > probabilities[best]) {
				best = i;
			}
		}
		return best;
	}

	private static byte[] readAllBytesOrExit(Path path) {
		try {
			return Files.readAllBytes(path);
		} catch (IOException e) {
			System.err.println("Failed to read [" + path + "]: " + e.getMessage());
			System.exit(1);
		}
		return null;
	}

	private static List<String> readAllLinesOrExit(Path path) {
		try {
			return Files.readAllLines(path, Charset.forName("UTF-8"));
		} catch (IOException e) {
			System.err.println("Failed to read [" + path + "]: " + e.getMessage());
			System.exit(0);
		}
		return null;
	}

	static class GraphBuilder {

		GraphBuilder(Graph g) {
			this.g = g;
		}

		Output<?> div(Output<?> x, Output<?> y) {
			return binaryOp("Div", x, y);
		}

		Output<?> sub(Output<?> x, Output<?> y) {
			return binaryOp("Sub", x, y);
		}

		Output<?> resizeBilinear(Output<?> images, Output<?> size) {
			return binaryOp("ResizeBilinear", images, size);
		}

		Output<?> expandDims(Output<?> input, Output<?> dim) {
			return binaryOp("ExpandDims", input, dim);
		}

		Output<?>  cast(Output<?> value, DataType dtype) {
			return g.opBuilder("Cast", "Cast").addInput(value).setAttr("DstT", dtype).build().output(0);
		}

		Output<?>  decodeJpeg(Output<?> contents, long channels) {
			return g.opBuilder("DecodeJpeg", "DecodeJpeg")
					.addInput(contents)
					.setAttr("channels", channels)
					.build()
					.output(0);
		}

		Output<?> constant(String name, Object value) {
			try (Tensor<?> t = Tensor.create(value)) {
				return g.opBuilder("Const", name)
						.setAttr("dtype", t.dataType())
						.setAttr("value", t)
						.build()
						.output(0);
			}
		}

		private Output<?> binaryOp(String type, Output<?> in1, Output<?> in2) {
			return g.opBuilder(type, type).addInput(in1).addInput(in2).build().output(0);
		}

		private Graph g;
	}

	public static void main(String[] args) throws IOException, InterruptedException, NativeHookException {
		new Controller();
	}


}