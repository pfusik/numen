import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;

public class Test extends Canvas {
	private static final int DY = 0;
	private static final int C1 = 16;
	private static final int C2 = -800;

	private	final static int R = 50;
	private final static int A = 128;
	private final static double Z = 2.0;

	private static final int SCREEN_WIDTH = 80;
	private static final int SCREEN_HEIGHT = 56;
	private static final int SCREEN_ZOOM = 4;

	private static final int MAP_WIDTH = (int) (2 * Z * (R + 1));
	private static final int MAP_HEIGHT = MAP_WIDTH;

	private BufferedImage image;
	private byte[][] scr;
	private byte[] buf;

	private byte[][] rtab;
	private byte[][] atab;
	private byte[][] ctab;

	public Test(boolean writeMap) {
		scr = new byte[SCREEN_HEIGHT][SCREEN_WIDTH];
		buf = new byte[SCREEN_WIDTH * SCREEN_HEIGHT];
		image = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_BYTE_GRAY);

		byte[][] map = new byte[MAP_HEIGHT][MAP_WIDTH];
		for (int y = 0; y < MAP_HEIGHT; y++) {
			double ay = Math.abs(y - MAP_HEIGHT / 2 + 0.5 - DY);
			if (ay < 3) {
				for (int x = 0; x < MAP_WIDTH; x++)
					map[y][x] = 0;
			}
			else {
				int iz = (int) (0x10000 / ay);
				for (int x = 0; x < MAP_WIDTH; x++) {
					int u = ((C1 * (x - MAP_WIDTH / 2) * iz) >> 16) & 15;
					int v = ((C2 * iz) >> 16) & 15;
					map[y][x] = (byte) ((v << 4) + u);
				}
			}
		}

		ctab = new byte[R][A];
		for (int r = 0; r < R; r++)
			for (int a = 0; a < A; a++) {
				int x = (int) (MAP_WIDTH / 2 + Z * r * Math.sin((a + 0.5) * 2 * Math.PI / A));
				int y = (int) (MAP_HEIGHT / 2 - Z * r * Math.cos((a + 0.5) * 2 * Math.PI / A));
				ctab[r][a] = map[y][x];
			}

		rtab = new byte[SCREEN_HEIGHT][SCREEN_WIDTH];
		atab = new byte[SCREEN_HEIGHT][SCREEN_WIDTH];
		for (int y = 0; y < SCREEN_HEIGHT; y++) {
			int y2 = y - SCREEN_HEIGHT / 2;
			for (int x = 0; x < SCREEN_WIDTH; x++) {
				int x2 = x - SCREEN_WIDTH / 2;
				rtab[y][x] = (byte) Math.sqrt(y2 * y2 + x2 * x2);
				atab[y][x] = (byte) (A / 2 - Math.atan2(x2, y2) * A / 2 / Math.PI);
				scr[y][x] = ctab[rtab[y][x]][atab[y][x]];
			}
		}

		if (writeMap) {
			OutputStream os;
			try {
					os = new FileOutputStream("tabr.dat");
				for (int i = 0; i < SCREEN_HEIGHT; i++)
					os.write(rtab[i]);
				os.close();
				os = new FileOutputStream("taba.dat");
				for (int i = 0; i < SCREEN_HEIGHT; i++)
					os.write(atab[i]);
				os.close();
				InputStream is = new FileInputStream("toruseru.dat");
				os = new FileOutputStream("tabc.dat");
				byte[] buf = new byte[A];
				for (int i = 0; i < R; i++) {
					os.write(ctab[i]);
					is.read(buf);
					os.write(buf);
				}
				is.close();
				os.close();
			} catch (Exception e) {
				System.err.println(e);
			}
		}
	}

	public void paint(Graphics g) {
		for (int y = 0; y < SCREEN_HEIGHT; y++)
			for (int x = 0; x < SCREEN_WIDTH; x++)
				buf[SCREEN_WIDTH * y + x] = scr[y][x];
		image.setData(Raster.createPackedRaster(new DataBufferByte(buf, buf.length), SCREEN_WIDTH, SCREEN_HEIGHT, 8, null));
		g.drawImage(image, 0, 0, SCREEN_ZOOM * SCREEN_WIDTH, SCREEN_ZOOM * SCREEN_HEIGHT, this);
	}

	public Dimension getPreferredSize() {
		return new Dimension(SCREEN_ZOOM * SCREEN_WIDTH, SCREEN_ZOOM * SCREEN_HEIGHT);
	}

	public static void main(String[] args) {
		if (args.length == 1 && args[0].equals("write"))
			new Test(true);
		else {
			Test c = new Test(false);
			Frame f = new Frame("Test");
			f.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});
			f.setResizable(false);
			f.add(c);
			f.pack();
			f.show();
		}
	}
}