package pl.scene.taquart.coledit;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.event.*;
import pl.scene.taquart.PCXImage;

public class ColorEditor extends Component {
	private Preferences prefs;

	private String pcxName;
	private String grbName;
	private int width;
	private int height;
	private byte[] pixels;
	private boolean isTip;
	private boolean isModified;

	private byte[] palette;
	private IndexColorModel cm;
	private int color;
	int bw;
	int bh;

	private MemoryImageSource mis;
	private Image image;
	private JFrame frame;
	private JCheckBoxMenuItem protectBlack;
	private JToolBar colorBar;
	private int zoom;

	private class ZoomAction extends AbstractAction {
		private int z;
		public ZoomAction(int z) {
			super("Zoom " + z + "x");
			this.z = z;
		}
		public void actionPerformed(ActionEvent e) {
			zoom = z;
			frame.pack();
			repaint();
		}
	}

	private class BrushAction extends AbstractAction {
		private int w;
		public BrushAction(int w) {
			super(w + "x" + w);
			this.w = w;
		}
		public void actionPerformed(ActionEvent e) {
			bw = bh = w;
		}
	}

	private static class SolidIcon implements Icon {
		private static final int W = 16;
		private static final int H = 16;
		private byte[] palette;
		private int index;
		public SolidIcon(byte[] palette, int i) {
			this.palette = palette;
			index = (16 * i + 7) * 3;
		}
		public int getIconWidth() {
			return W;
		}
		public int getIconHeight() {
			return H;
		}
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Color sc = g.getColor();
			g.setColor(new Color(palette[index] & 0xff, palette[index + 1] & 0xff, palette[index + 2] & 0xff));
			g.fillRect(x, y, W, H);
			g.setColor(sc);
		}
	}

	private class ColorAction extends AbstractAction {
		private int i;
		private JRadioButtonMenuItem rbmi;
		public ColorAction(int i) {
			super(Integer.toString(i), new SolidIcon(palette, i));
			this.i = i;
			rbmi = new JRadioButtonMenuItem(this);
		}
		public void actionPerformed(ActionEvent e) {
			color = i;
			rbmi.setSelected(true);
		}
	}

	private JRadioButtonMenuItem[] colRadio;

	private class UIAction extends AbstractAction {
		private String className;
		public UIAction(UIManager.LookAndFeelInfo i) {
			super(i.getName());
			className = i.getClassName();
		}
		public void actionPerformed(ActionEvent e) {
			try {
				UIManager.setLookAndFeel(className);
				SwingUtilities.updateComponentTreeUI(frame);
				prefs.put("LookAndFeel", className);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null, ex);
			}
		}
	}

	private void title() {
		frame.setTitle("ColorEditor [" + pcxName + "," + grbName + (isModified ? "*] " : "] ")
		 + width + "x" + height + (isTip ? " (TIP)" : " (256)"));
	}

	public void openPicture(String name) throws Exception {
		InputStream is;
		is = new FileInputStream(name);
		PCXImage pcx = new PCXImage(is);
		is.close();
		if (pcx.width % 1 != 0)
			throw new Exception("Width must be even!");
		for (int i = 0; i < 256; i++)
			for (int j = 0; j < 3; j++)
				if (pcx.palette[i][j] != (byte) i)
					throw new Exception("Wrong palette! Must be grayscale!");
		checkbits:
		for (int y = 0; y < pcx.height; y++)
			for (int x = 0; x < pcx.width; x++)
				if ((pcx.data[y][x] & 0xf) != 0) {
					if (JOptionPane.showConfirmDialog(frame,
						"More than 16 shades! Strip to 16 shades?",
						"ColorEditor", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
						throw new Exception("Cancelled!");
					for (y = 0; y < pcx.height; y++)
						for (x = 0; x < pcx.width; x++)
							pcx.data[y][x] &= 0xf0;
					OutputStream os = new FileOutputStream(name);
					pcx.write(os);
					os.close();
					break checkbits;
				}
		String gn = name.substring(0, name.length() - 3) + "grB";
		File f = new File(gn);
		int len = (int) f.length();
		boolean isNew = len == 0;
		if (isNew) {
			final Object[] options = {"256", "TIP", "Cancel"};
			int r = JOptionPane.showOptionDialog(frame, gn + " does not exist! Choose format", "New colorization",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
			if (r == 0)
				isTip = false;
			else if (r == 1)
				isTip = true;
			else
				throw new Exception("Cancelled!");
			isModified = true;
		}
		else {
			if (len == pcx.width * pcx.height / 2) {
				isTip = false;
			}
			else if (len == pcx.width * pcx.height / 4) {
				isTip = true;
			}
			else
				throw new Exception("Wrong length of " + gn);
			isModified = false;
		}
		pcxName = name;
		grbName = gn;
		width = pcx.width;
		height = pcx.height;
		pixels = new byte[width * height];
		int i = 0;
		for (int y = 0; y < pcx.height; y++)
			for (int x = 0; x < pcx.width; x++)
				pixels[i++] = (byte) ((pcx.data[y][x] >> 4) & 0xf);
		if (isTip) {
			i = 0;
			for (int y = 0; y < pcx.height; y++) {
				int b = 0;
				for (int x = 0; x < pcx.width; x += 2) {
					int d = pixels[i];
					pixels[i++] = (byte) ((d + b) / 2);
					b = pixels[i] & 0x0e;
					pixels[i++] = (byte) ((d + b) / 2);
				}
			}
		}
		if (!isNew) {
			is = new FileInputStream(f);
			i = 0;
			for (int y = 0; y < pcx.height; y++)
				for (int x = 0; x < pcx.width; x+= isTip ? 4 : 2) {
					int c = is.read();
					pixels[i++] |= c & 0xf0;
					if (isTip)
						pixels[i++] |= c & 0xf0;
					pixels[i++] |= c << 4;
					if (isTip)
						pixels[i++] |= c << 4;
				}
			is.close();
		}
		mis = new MemoryImageSource(width, height, cm, pixels, 0, width);
		mis.setAnimated(true);
		image = createImage(mis);
		title();
		frame.pack();
	}

	private String chooseFile(final String ext, final String description) {
		JFileChooser fc = new JFileChooser(new File("."));
		fc.setFileFilter(new javax.swing.filechooser.FileFilter() {
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().endsWith(ext);
			}
			public String getDescription() {
				return description;
			}
		});
		if (fc.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION)
			return null;
		try {
			return fc.getSelectedFile().getCanonicalPath();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame, e, "Error!", JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}

	public void fileOpenPicture() {
		String name = chooseFile(".pcx", "pcx images");
		if (name != null)
			try {
				openPicture(name);
				repaint();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(frame, e, "Error!", JOptionPane.ERROR_MESSAGE);
			}
	}

	public void saveColors(String grbName) throws IOException {
		OutputStream os = new FileOutputStream(grbName);
		int i = 0;
		while (i < pixels.length)
			if (isTip) {
				os.write((pixels[i] & 0xf0) | ((pixels[i + 2] & 0xf0) >> 4));
				i += 4;
			}
			else {
				os.write((pixels[i] & 0xf0) | ((pixels[i + 1] & 0xf0) >> 4));
				i += 2;
			}
		os.close();
		isModified = false;
		title();
	}

	public void fileSaveColors() {
		if (width <= 0 || height <= 0)
			return;
		try {
			saveColors(grbName);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame, e, "Error!", JOptionPane.ERROR_MESSAGE);
		}
	}

	public boolean loadPalette(String name) {
		try {
			InputStream is = new FileInputStream(name);
			is.read(palette);
			is.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame,
				e, "Error loading " + name,
				JOptionPane.ERROR_MESSAGE
			);
			return false;
		}
		cm = new IndexColorModel(8, 256, palette, 0, false);
		return true;
	}

	public void fileLoadPalette() {
		String name = chooseFile(".act", "act palettes");
		if (name != null)
			try {
				loadPalette(name);
				colorBar.repaint();
				if (mis != null)
					mis.newPixels(pixels, cm, 0, width);
				repaint();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(frame, e, "Error!", JOptionPane.ERROR_MESSAGE);
			}
	}

	public ColorEditor() {
		palette = new byte[768];
		if (!loadPalette("real.act"))
			System.exit(3);
		prefs = Preferences.userNodeForPackage(ColorEditor.class);
		color = 1;
		zoom = 1;
		bw = 1;
		bh = 1;
		frame = new JFrame("ColorEditor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JMenuBar mb = new JMenuBar();
		JMenu menu = new JMenu("File");
		menu.add(new AbstractAction("Open picture...") {
			public void actionPerformed(ActionEvent e) {
				fileOpenPicture();
			}
		});
		menu.add(new AbstractAction("Save colors") {
			public void actionPerformed(ActionEvent e) {
				fileSaveColors();
			}
		});
		menu.addSeparator();
		menu.add(new AbstractAction("Load palette...") {
			public void actionPerformed(ActionEvent e) {
				fileLoadPalette();
			}
		});
		menu.addSeparator();
		menu.add(new AbstractAction("Exit") {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mb.add(menu);
		menu = new JMenu("View");
		ButtonGroup bg = new ButtonGroup();
		for (int i = 1; i <= 10; i++) {
			JRadioButtonMenuItem rbmi = new JRadioButtonMenuItem(new ZoomAction(i));
			if (i == 1)
				rbmi.setSelected(true);
			bg.add(rbmi);
			menu.add(rbmi);
		}
		menu.addSeparator();
		JCheckBoxMenuItem cbmi = new JCheckBoxMenuItem("Show palette", true);
		cbmi.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				colorBar.setVisible(((JCheckBoxMenuItem) e.getSource()).isSelected());
			}
		});
		menu.add(cbmi);
		boolean flatPalette = prefs.getBoolean("FlatPalette", false);
		cbmi = new JCheckBoxMenuItem("Flat palette", flatPalette);
		cbmi.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				boolean b = ((JCheckBoxMenuItem) e.getSource()).isSelected();
				colorBar.setRollover(b);
				prefs.putBoolean("FlatPalette", b);
			}
		});
		menu.add(cbmi);
		menu.addSeparator();
		UIManager.LookAndFeelInfo[] guis = UIManager.getInstalledLookAndFeels();
		String curgui = prefs.get("LookAndFeel", UIManager.getLookAndFeel().getClass().getName());
		bg = new ButtonGroup();
		for (int i = 0; i < guis.length; i++) {
			JRadioButtonMenuItem rbmi = new JRadioButtonMenuItem(new UIAction(guis[i]));
			if (guis[i].getClassName().equals(curgui))
				rbmi.setSelected(true);
			bg.add(rbmi);
			menu.add(rbmi);
		}
		mb.add(menu);
		menu = new JMenu("Brush");
		bg = new ButtonGroup();
		for (int i = 1; i <= 10; i++) {
			JRadioButtonMenuItem rbmi = new JRadioButtonMenuItem(new BrushAction(i));
			if (i == 1)
				rbmi.setSelected(true);
			bg.add(rbmi);
			menu.add(rbmi);
		}
		menu.addSeparator();
		protectBlack = new JCheckBoxMenuItem("Protect black");
		menu.add(protectBlack);
		mb.add(menu);
		menu = new JMenu("Color");
		colorBar = new JToolBar();
		colorBar.setRollover(flatPalette);
		bg = new ButtonGroup();
		colRadio = new JRadioButtonMenuItem[16];
		for (int i = 0; i < 16; i++) {
			ColorAction ca = new ColorAction(i);
			JRadioButtonMenuItem rbmi = ca.rbmi;
			if (i == 1)
				rbmi.setSelected(true);
			colRadio[i] = rbmi;
			bg.add(rbmi);
			menu.add(rbmi);
			colorBar.add(ca);
		}
		mb.add(menu);
		menu = new JMenu("Help");
		menu.add(new AbstractAction("About...") {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame,
				"256-color and Taquart Interlace Picture colorizer\n" +
				"14/06/2002 by Fox/TQA <fox@scene.pl>\n" +
				"17/06/2002: corrected Open in different dirs, added GUI selection (View menu).\n" +
				"21/06/2002: added Load Palette\n" +
				"01/07/2002: GUI selection saved to user preferences\n\n" +
				"Tip: Right-click to pick color under cursor.",
				"ColorEditor",
				JOptionPane.INFORMATION_MESSAGE);
			}
		});
		mb.add(menu);
		frame.setJMenuBar(mb);
		frame.getContentPane().add(colorBar, "North");
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				plot(e);
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				plot(e);
			}
		});
		frame.getContentPane().add(new JScrollPane(this));
		try {
			UIManager.setLookAndFeel(curgui);
			SwingUtilities.updateComponentTreeUI(frame);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex);
		}
		frame.pack();
		frame.show();
	}

	public void plot(MouseEvent e) {
		int x = e.getX() / zoom;
		int y = e.getY() / zoom;
		if (x < 0 || x >= width || y < 0 || y >= height)
			return;
		if ((e.getModifiers() & MouseEvent.META_MASK) != 0) {
			color = (pixels[y * width + x] & 0xf0) >> 4;
			colRadio[color].setSelected(true);
			return;
		}
		if (!isModified) {
			isModified = true;
			title();
		}
		int w = Math.min(bw, width - x);
		int h = Math.min(bh, height - y);
		boolean p = protectBlack.isSelected();
		if (isTip) {
			x &= ~1;
			w++;
			w &= ~1;
			int o = y * width + x;
			for (int j = 0; j < bh && (y + j) < height; j++) {
				for (int i = 0; i < w; i += 2, o += 2)
					if (!p || ((pixels[o] | pixels[o + 1]) != 0)) {
						pixels[o] = (byte) ((pixels[o] & 0x0f) | (color << 4));
						pixels[o + 1] = (byte) ((pixels[o + 1] & 0x0f) | (color << 4));
					}
				o += width - w;
			}
		}
		else {
			int o = y * width + x;
			for (int j = 0; j < h; j++) {
				for (int i = 0; i < w; i++, o++)
					if (!p || (pixels[o] & 0x0f) != 0)
						pixels[o] = (byte) ((pixels[o] & 0x0f) | (color << 4));
				o += width - w;
			}
		}
		mis.newPixels(x, y, w, h);
	}

	public Dimension getPreferredSize() {
		return image == null ? new Dimension(400, 200) : new Dimension(width * zoom, height * zoom);
	}

	public void paint(Graphics g) {
		if (image != null)
			g.drawImage(image, 0, 0, width * zoom, height * zoom, this);
	}

	public static void main(String[] args) {
		ColorEditor ce;
		ce = new ColorEditor();
	}
}
