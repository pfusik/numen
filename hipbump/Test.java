import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;

public class Test extends Canvas implements MouseMotionListener {
	private final static int W = 154;
	private final static int H = 98;
	private final static int R = 50 ;

	private BufferedImage image;
	private byte[][] map;
	private int[][] dx;
	private int[][] dy;
	private byte[] buf;

	private int cx = W / 2;

	public byte getLight(int x, int y) {
		x += cx - W;
		int r = (int) Math.sqrt((x * x + y * y) / 10);
		r = 29 - r;
		if (r < 0)
			r = 0;
		return (byte) (r * 256 / 30);
	}

	private int rep2 = 0;
	private int rep1 = 0;
	private String mode;
	private boolean[][] skip = new boolean[H][W];

	private void put(int x, int y, int cx, int cy, boolean both) {
		x += 3; cx += 3;
		y--; cy--;
		System.out.print("\tdta\ta(Screen_screen" + mode + "+" + (y * 80 + x / 4) + ",");
		if (both)
			System.out.println("[Screen_screen" + mode + "+" + (cy * 80 + cx / 4) + "]^$8000)");
		else
			System.out.println("Screen_screen" + mode + "+" + (cy * 80 + cx / 4) + ")");
/*		x -= 3; cx -= 3;
		y++; cy++;
 		System.out.print(" ; " + Integer.toHexString(adr(dx[y][x], dy[y][x]) + 48+56) + " dx=" + dx[y][x] + " dy=" + dy[y][x]);
 		System.out.println(" ; " + Integer.toHexString(adr(dx[cy][cx], dy[cy][cx]) + 48+56) + " dx=" + dx[cy][cx] + " dy=" + dy[cy][cx]);
 */
	}

	private void optPixel(int x, int y) {
		if (skip[y][x])
			return;
		int dx1 = dx[y][x];
		int dy1 = dy[y][x];
		int dx2 = dx[y][x + 2];
		int dy2 = dy[y][x + 2];
		int cx = x + 4;
		for (int cy = y; cy < H - 1; cy++) {
			for (; cx < W - 1; cx += 4) {
				if (!skip[cy][cx] && dx1 == dx[cy][cx] && dy1 == dy[cy][cx]
				 && dx2 == dx[cy][cx + 2] && dy2 == dy[cy][cx + 2]) {
					skip[cy][cx] = true;
					put(x, y, cx, cy, true);
					rep2++;
				}
			}
			cx &= 3;
		}
		cx = x + 4;
		for (int cy = y; cy < H - 1; cy++) {
			for (; cx < W - 1; cx += 4) {
				if (!skip[cy][cx] && dx1 == dx[cy][cx] && dy1 == dy[cy][cx]) {
					skip[cy][cx] = true;
					put(x, y, cx, cy, false);
					rep1++;
					optPixel(cx, cy);
					return;
				}
			}
			cx &= 3;
		}
	}

	private void optMode(int i, String mode) {
		this.mode = mode;
		System.out.println("\torg\tOpt_mode" + mode);
		boolean[][] skip = new boolean[H][W];
		for (int y = 1; y < H - 1; y++)
			for (int x = 1 + i; x < W - 1; x += 4)
				optPixel(x, y);
	}

	int adr(int dx, int dy) {
		dx /= 2;
		dy *= 2;
		dy += 2;
		if (dy >= 0x20)
			dy += 0x80 - 0x20;
		return (dy << 8) + dx;
	}

	public Test(boolean writeMap, boolean writeOpt) {
		try {
			PCXImage pcx = new PCXImage(new FileInputStream("tqa160_2.pcx"));
			map = new byte[H][W];
			for (int y = 0; y < H; y++)
				System.arraycopy(pcx.data[y + (pcx.height - H) / 2], (pcx.width - W) / 2, map[y], 0, W);
			if (writeMap) {
				OutputStream os = new FileOutputStream("bumpmap.gfx");
				for (int y = 0; y < H; y++)
					os.write(map[y]);
				os.close();
			}
		} catch (Exception e) {
			System.err.println(e);
			return;
		}
		dx = new int[H][W];
		dy = new int[H][W];
		for (int y = 1; y < H - 1; y++)
			for (int x = 1; x < W - 1; x++) {
				int t;
				t = x - 1 + (map[y][x + 1] & 0xff) - (map[y][x - 1] & 0xff);
				if (t < 0)
					t = 0;
				if (t >= 76 * 2)
					t = 76 * 2;
				dx[y][x] = t;
				t = y - H / 2 + (map[y + 1][x] & 0xff) - (map[y - 1][x] & 0xff);
				if (t < 0)
					t = -t;
				if (t > R)
					t = R; //10000;
				dy[y][x] = t;
			}

		if (false) {
			for (int y = 1; y < H - 1; y++)
				for (int x = 1; x < W - 1; x += 4) {
					System.out.println("LDA\t" + Integer.toHexString(adr(dx[y][x], dy[y][x]) + 48+56) + "\tdx=" + dx[y][x] + " dy=" + dy[y][x]);
					System.out.println("EOR\t" + Integer.toHexString(adr(dx[y][x + 2], dy[y][x + 2])) + "\tdx=" + dx[y][x + 2] + " dy=" + dy[y][x + 2]);
					System.out.println("STA\t" + Integer.toHexString(0x2100 + (x + 3) / 4 + (y - 1) * 80 - 64));
				}
			System.out.println("; mode A");
			for (int y = 1; y < H - 1; y++)
				for (int x = 2; x < W - 1; x += 4) {
					System.out.println("LDA\t" + Integer.toHexString(0x100 + adr(dx[y][x], dy[y][x]) + 48+56) + "\tdx=" + dx[y][x] + " dy=" + dy[y][x]);
					System.out.println("EOR\t" + Integer.toHexString(0x100 + adr(dx[y][x + 2], dy[y][x + 2])) + "\tdx=" + dx[y][x + 2] + " dy=" + dy[y][x + 2]);
					System.out.println("STA\t" + Integer.toHexString(0xe100 + (x + 3) / 4 + (y - 1) * 80 - 64));
				}
		}

		if (writeOpt) {
			optMode(0, "9");
			optMode(1, "A");
			System.out.println("; rep1=" + rep1);
			System.out.println("; rep2=" + rep2);
			int cn = 4 * (H - 2) * 5 + (W - 2) * (H - 2) * 13 / 2;
			int c = cn - 8 * rep2 - 2 * rep1;
			int bn = 4 * (H - 2) * 3 + (W - 2) * (H - 2) * 9 / 2;
			int b = bn - 6 * rep2 - 1 * rep1;
			System.out.println("; cbo=" + cn);
			System.out.println("; cykle=" + c);
			System.out.println("; cpu=" + 4 * (32760 - 43 * 2 * (H - 2)));
			System.out.println("; bbo=" + bn);
			System.out.println("; bajty=" + b);
		}
		buf = new byte[W * H];
		image = new BufferedImage(W, H, BufferedImage.TYPE_BYTE_GRAY);
		addMouseMotionListener(this);
	}

	public void update(Graphics g) {
		paint(g);
	}

	public void paint(Graphics g) {
		for (int y = 1; y < H - 1; y++)
			for (int x = 1; x < W - 1; x++)
				buf[y * W + x] = getLight(dx[y][x], dy[y][x]);
		image.setData(Raster.createPackedRaster(new DataBufferByte(buf, buf.length), W, H, 8, null));
		g.drawImage(image, 0, 0, W * 2, H * 2, this);
	}

	public void mouseDragged(MouseEvent e) {
		cx = W - e.getX() / 2;
		repaint();
	}

	public void mouseMoved(MouseEvent e) {
	}

	public Dimension getPreferredSize() {
		return new Dimension(W * 2, H * 2);
	}

	public static void main(String[] args) {
		if (args.length == 1 && args[0].equals("write"))
			new Test(true, true);
		else {
			Canvas c = new Test(false, false);
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