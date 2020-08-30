package com.hmi.app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.util.List;
import java.util.TooManyListenersException;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class DropPane extends JPanel {

	private static final long serialVersionUID = 1L;
	private DropTarget dropTarget;
	private DropTargetHandler dropTargetHandler;
	private boolean dragOver = false;
	private JLabel message;
	private JPanel results;
	private JLabel result1;
	private JLabel result2;
	private JLabel result3;
	private List<?> files;
	private boolean flag = false;
	//private JFrame frame;

	public static void main(String [] args) {
		new DropPane();
	}

	public DropPane() {
		//		try {
		//			target = ImageIO.read(new File("/Users/omar_aldahrawy/Desktop/333.jpg"));
		//		} catch (IOException ex) {
		//			ex.printStackTrace();
		//		}

		//frame = new JFrame("Drop Pane");
		setVisible(true);
		setLayout(new GridBagLayout());
		message = new JLabel();
		result1 = new JLabel();
		result2 = new JLabel();
		result3 = new JLabel();
		result1.setHorizontalAlignment(JLabel.CENTER);
		result2.setHorizontalAlignment(JLabel.CENTER);
		result3.setHorizontalAlignment(JLabel.CENTER);
		results = new JPanel();
		results.setLayout(new GridLayout(3, 1));
		results.add(result1);
		results.add(result2);
		results.add(result3);
		message.setFont(message.getFont().deriveFont(Font.BOLD, 24));
		message.setHorizontalAlignment(JLabel.CENTER);
		add(message);
		//add(results);
//		frame.add(this);
//		frame.pack();
//		frame.setLocationRelativeTo(null);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setVisible(true);

	}

	public boolean getFlag() {
		System.out.print("");
		return flag;
	}

	public List<?> getFiles(){
		if(files == null) {
			System.out.println("bobo");
			return null;
		}
		else
		return files;
	}

	public void setResult1(String str) {
		result1.setText(str);
	}

	public void setResult2(String str) {
		result2.setText(str);
	}

	public void setResult3(String str) {
		result3.setText(str);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(400, 600);
	}

	protected DropTarget getMyDropTarget() {
		if (dropTarget == null) {
			dropTarget = new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, null);
		}
		return dropTarget;
	}

	protected DropTargetHandler getDropTargetHandler() {
		if (dropTargetHandler == null) {
			dropTargetHandler = new DropTargetHandler();
		}
		return dropTargetHandler;
	}

	@Override
	public void addNotify() {
		super.addNotify();
		try {
			getMyDropTarget().addDropTargetListener(getDropTargetHandler());
		} catch (TooManyListenersException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		getMyDropTarget().removeDropTargetListener(getDropTargetHandler());
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (dragOver) {
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setColor(new Color(0, 255, 0, 64));
			g2d.fill(new Rectangle(getWidth(), getHeight()));
			//			if (dragPoint != null && target != null) {
			//				int x = dragPoint.x - 25;
			//				int y = dragPoint.y - 25;
			//				g2d.drawImage(target, x, y, this);
			//			}
			g2d.dispose();
		}
	}

	protected void importFiles(final List<?> list) {
		files = list;
		Runnable run = new Runnable() {
			@Override
			public void run() {
//				try {
//					Image img = ImageIO.read((File) files.get(0));
//					message.setIcon(new ImageIcon(img.getScaledInstance(300, 300, 300)));
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
			}
		};
		SwingUtilities.invokeLater(run);
	}

	protected class DropTargetHandler implements DropTargetListener {

		protected void processDrag(DropTargetDragEvent dtde) {
			if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				dtde.acceptDrag(DnDConstants.ACTION_COPY);
			} else {
				dtde.rejectDrag();
				JOptionPane.showMessageDialog(null, "Drop files only", "Error", JOptionPane.PLAIN_MESSAGE);
			}
		}

		@Override
		public void dragEnter(DropTargetDragEvent dtde) {
			processDrag(dtde);
			SwingUtilities.invokeLater(new DragUpdate(true, dtde.getLocation()));
			repaint();
		}

		@Override
		public void dragOver(DropTargetDragEvent dtde) {
			processDrag(dtde);
			SwingUtilities.invokeLater(new DragUpdate(true, dtde.getLocation()));
			repaint();
		}

		@Override
		public void dropActionChanged(DropTargetDragEvent dtde) {
		}

		@Override
		public void dragExit(DropTargetEvent dte) {
			SwingUtilities.invokeLater(new DragUpdate(false, null));
			repaint();
		}

		@Override
		public void drop(DropTargetDropEvent dtde) {
			SwingUtilities.invokeLater(new DragUpdate(false, null));
			Transferable transferable = dtde.getTransferable();
			if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				dtde.acceptDrop(dtde.getDropAction());
				try {
					List<?> transferData = (List<?>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
					if (transferData != null && transferData.size() > 0) {
						flag = true;
						//System.out.println(flag);
						importFiles(transferData);
						dtde.dropComplete(true);
						flag = false;
					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else {
				dtde.rejectDrop();
			}
		}
	}

	public class DragUpdate implements Runnable {

		private boolean dragOver;

		public DragUpdate(boolean dragOver, Point dragPoint) {
			this.dragOver = dragOver;
		}

		@Override
		public void run() {
			DropPane.this.dragOver = dragOver;
			DropPane.this.repaint();
		}
	}

}