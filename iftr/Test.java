import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;

public class Test extends Canvas implements Runnable {
	private static final int SCREEN_WIDTH = 80;
	private static final int SCREEN_HEIGHT = 56;
	private static final int SCREEN_ZOOM = 4;

	private static final int MAP_WIDTH = 160;
	private static final int MAP_HEIGHT = 120;

	private BufferedImage image;
	private byte[][] scr;
	private byte[] buf;

	private PCXImage pcx1;
	private PCXImage pcx2;

	private byte[][] rtab;

	private static final int PAL_SIZE = 32;
	private final int[][] px = {{0, 0x63}, {0x40, 0x76}};
	private static final int[][] ax = {{26, 12}, {34, 44}};
	private static final int[][] lx = {{8, 8}, {8, 16}};
	private byte[][] pal;

	private final int[][] sx = {{0, 0x33}, {0x51, 0xb9}};
	private final int[][] sy = {{0x1e, 0x70}, {0x30, 0xd0}};
	private static final int[][] dx = {{5, 2}, {4, 3}};
	private static final int[][] dy = {{2, 6}, {5, 7}};
	private final int[] cx;
	private final int[] cy;

	private int t;

	public Test(boolean writeMap) {
		scr = new byte[SCREEN_HEIGHT][SCREEN_WIDTH];
		buf = new byte[SCREEN_WIDTH * SCREEN_HEIGHT];
		try {
			pcx1 = new PCXImage(new FileInputStream("statek.pcx"));
			pcx2 = new PCXImage(new FileInputStream("maszt.pcx"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		cx = new int[2];
		cy = new int[2];
		image = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_BYTE_GRAY);

		rtab = new byte[MAP_HEIGHT][MAP_WIDTH];
		for (int y = 0; y < MAP_HEIGHT; y++) {
			int y2 = y - MAP_HEIGHT / 2;
			for (int x = 0; x < MAP_WIDTH; x++) {
				int x2 = x - MAP_WIDTH / 2;
				double a = (Math.atan2(y2, x2) * 64 / Math.PI);
				double r = Math.sqrt(y2 * y2 + x2 * x2) / 2;
				rtab[y][x] = (byte) ((int) (256 + a + r) & (PAL_SIZE - 1));
			}
		}
		pal = new byte[2][PAL_SIZE];

		if (writeMap) {
			byte[][] v = new byte[PAL_SIZE][PAL_SIZE];
			for (int i = 0; i < PAL_SIZE; i++)
				java.util.Arrays.fill(v[i], (byte) -1);
			byte[] m = new byte[MAP_WIDTH / 2 * MAP_HEIGHT];
			byte[] ht = new byte[202];
			byte[] lt = new byte[202];
			int mv = 0;
			int i = 0;
			for (int y = 0; y < MAP_HEIGHT; y++)
				for (int x = 0; x < MAP_WIDTH; x += 2) {
					byte h = rtab[y][x];
					byte l = rtab[y][x + 1];
					if (v[h][l] == -1) {
						ht[mv] = h;
						lt[mv] = l;
						v[h][l] = (byte) (mv++);
					}
					m[i++] = v[h][l];
				}
			try {
				OutputStream os;
				os = new FileOutputStream("wirhilo.map");
				os.write(m);
				os.close();
				os = new FileOutputStream("hi.dat");
				os.write(ht);
				os.close();
				os = new FileOutputStream("lo.dat");
				os.write(lt);
				os.close();
			} catch (Exception e) {
				System.err.println(e);
			}
		}

		if (false) {
			java.util.HashSet s = new java.util.HashSet();
			for (int y = 0; y < MAP_HEIGHT; y++)
				for (int x = 0; x < MAP_WIDTH; x += 2)
					s.add(new Short((short) ((rtab[y][x] << 8) + rtab[y][x + 1])));
			System.out.println("size=" + s.size());
		}

		if (false) {
			try {
				OutputStream os;
				os = new FileOutputStream("statek.raw");
				for (int y = 0; y < MAP_HEIGHT; y++)
					for (int x = 0; x < MAP_WIDTH; x += 2)
						os.write((pcx1.data[y][x] & 0xf0)
							+ ((pcx1.data[y][x + 1] >> 4) & 0xf));
				os.close();
				os = new FileOutputStream("maszt.raw");
				for (int y = 0; y < MAP_HEIGHT; y++)
					for (int x = 0; x < MAP_WIDTH; x += 2)
						os.write((pcx2.data[y][x] & 0xf0)
							+ ((pcx2.data[y][x + 1] >> 4) & 0xf));
				os.close();
			} catch (Exception e) {
				System.err.println(e);
			}
		}

		/*if (false) {
			try {
				OutputStream os = new FileOutputStream("wir.map");
				for (int y = 0; y < MAP_HEIGHT; y++)
					os.write(rtab[y]);
				os.close();
			} catch (Exception e) {
				System.err.println(e);
			}
		}*/
	}

	public void update(Graphics g) {
		paint(g);
	}

	// 1 + sin(0..255)
	private static double sin(int angle) {
		return 1 + Math.sin(angle * Math.PI / 128);
	}

	public void paint(Graphics g) {
		t++;
		for (int i = 0; i < 2; i++) {
			// ruszaj obrazkami
			cx[i] = (int) ((sin(sx[i][0]) + sin(sx[i][1])) * (MAP_WIDTH - SCREEN_WIDTH) / 4);
			cy[i] = (int) ((sin(sy[i][0]) + sin(sy[i][1])) * (MAP_HEIGHT - SCREEN_HEIGHT) / 4);
			cx[i] &= ~1;
			cy[i] &= ~1;
			for (int j = 0; j < 2; j++) {
				sx[i][j] += dx[i][j];
				sy[i][j] += dy[i][j];
			}
			// rób palety
			int t1 = px[i][0] += ax[i][0];
			int t2 = px[i][1] += ax[i][1];
			for (int j = 0; j < PAL_SIZE; j++)
				pal[i][j] = (byte) ((int) ((sin(t1 += lx[i][0]) + sin(t2 += lx[i][1])) * 7 / 4) << 4);
		}
		for (int y = 0; y < SCREEN_HEIGHT; y++)
			for (int x = 0; x < SCREEN_WIDTH; x++) {
				int c1 = pal[1][rtab[y + cy[0]][x + cx[0]]];
				int c2 = pal[1][rtab[y + cy[1]][x + cx[1]]];
//				int c1 = (pcx1.data[y + cy[0]][x + cx[0]] >> 1) & 0x70;
//				int c2 = (pcx2.data[y + cy[1]][x + cx[1]] >> 1) & 0x70;
				scr[y][x] = (byte) (c1 + c2);
			}

		for (int y = 0; y < SCREEN_HEIGHT; y++)
			for (int x = 0; x < SCREEN_WIDTH; x++)
				buf[SCREEN_WIDTH * y + x] = scr[y][x];
		image.setData(Raster.createPackedRaster(new DataBufferByte(buf, buf.length), SCREEN_WIDTH, SCREEN_HEIGHT, 8, null));
		g.drawImage(image, 0, 0, SCREEN_ZOOM * SCREEN_WIDTH, SCREEN_ZOOM * SCREEN_HEIGHT, this);
	}

	public Dimension getPreferredSize() {
		return new Dimension(SCREEN_ZOOM * SCREEN_WIDTH, SCREEN_ZOOM * SCREEN_HEIGHT);
	}

	public void run() {
		try {
			while (true) {
				repaint();
				Thread.sleep(40);
			}
		} catch (InterruptedException e) {
		}
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
			new Thread(c).start();
		}
	}
}