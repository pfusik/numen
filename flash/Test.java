import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;

public class Test extends Canvas implements Runnable {
	private final static int W = 80;
	private final static int H = 56;
	private final static int S = 200;
	private final static double Z = 2.0;

	private	final static int R = 50;
	private final static int A = 128;

	private PCXImage pcx;
	private BufferedImage imghi;

	private BufferedImage imglo;
	private byte[][] scr;
	private byte[] buf;

	private byte[][] rtab;
	private byte[][] atab;
	private byte[][] ctab;
	private int maxr;

	public Test(boolean writeLookups) {
		try {
			pcx = new PCXImage(new FileInputStream("200x200.pcx"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		imghi = new BufferedImage(2 * W, 2 * H, BufferedImage.TYPE_BYTE_GRAY);
		byte[] bufhi = new byte[2 * W * 2 * H];
		for (int y = 0; y < 2 * H; y++)
			for (int x = 0; x < 2 * W; x++)
				bufhi[y * 2 * W + x] = (byte) (pcx.data[S / 2 - H + y][S / 2 - W + x] & 0xf0);
		imghi.setData(Raster.createPackedRaster(new DataBufferByte(bufhi, bufhi.length), 2 * W, 2 * H, 8, null));

		imglo = new BufferedImage(W, H, BufferedImage.TYPE_BYTE_GRAY);
		scr = new byte[H][W];
		buf = new byte[W * H];

		rtab = new byte[H][W];
		atab = new byte[H][W];
		for (int y = 0; y < H; y++)
			for (int x = 0; x < W; x++) {
				rtab[y][x] = (byte) Math.sqrt((x - W / 2) * (x - W / 2) + (y - H / 2) * (y - H / 2));
				atab[y][x] = (byte) (A / 2 - Math.atan2(x - W / 2, y - H / 2) * A / 2 / Math.PI);
			}
		ctab = new byte[R][A];
		for (int r = 0; r < R; r++)
			for (int a = 0; a < A; a++) {
				int x = (int) (S / 2 + Z * r * Math.sin(a * 2 * Math.PI / A));
				int y = (int) (S / 2 - Z * r * Math.cos(a * 2 * Math.PI / A));
				ctab[r][a] = (byte) (pcx.data[y][x] & 0xf0);
			}

		if (false) {
			byte la = -1;
			byte lr = -1;
			int opts = 0;
			for (int y = 0; y < H; y++)
				for (int x = 0; x < W; x++)
					if (atab[y][x] == la && rtab[y][x] == lr)
						opts++;
					else {
						la = atab[y][x];
						lr = rtab[y][x];
					}
			System.out.println("opts = " + opts);
		}

		if (writeLookups) {
			try {
				OutputStream os;
				os = new FileOutputStream("tunnelz.ray");
				for (int y = 0; y < H; y++)
					os.write(rtab[y]);
				os.close();
				os = new FileOutputStream("tunnelz.ang");
				for (int y = 0; y < H; y++)
					os.write(atab[y]);
				os.close();
				os = new FileOutputStream("flash.map");
				for (int r = 0; r < R; r++)
					os.write(ctab[r]);
				os.close();
				os = new FileOutputStream("flaship.gr9");
				for (int y = 0; y < 2 * H; y++)
					for (int x = 0; x < 2 * W; x += 4)
						os.write(bufhi[y * 2 * W + x]
						+ ((bufhi[y * 2 * W + x + 2] >> 4) & 0xf));
				os.close();
				os = new FileOutputStream("flaship.grA");
				for (int y = 0; y < 2 * H; y++)
					for (int x = 1; x < 2 * W; x += 4)
						os.write(((bufhi[y * 2 * W + x] >> 1) & 0x70)
						+ ((bufhi[y * 2 * W + x + 2] >> 5) & 0x7));
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		maxr = 0;
	}

	public void update(Graphics g) {
		paint(g);
	}

	public void paint(Graphics g) {
		if (maxr < 44) {
			int da = Math.max(4 * (40 - maxr), 0);
			for (int y = 0; y < H; y++)
				for (int x = 0; x < W; x++)
					scr[y][x] = ctab[Math.min(rtab[y][x] & 0xff, maxr)][(atab[y][x] + da) % A];
			for (int y = 0; y < H; y++)
				for (int x = 0; x < W; x++)
					buf[y * W + x] = scr[y][x];
			imglo.setData(Raster.createPackedRaster(new DataBufferByte(buf, buf.length), W, H, 8, null));
			g.drawImage(imglo, 0, 0, 4 * W, 4 * H, this);
		}
		else
			g.drawImage(imghi, 0, 0, 4 * W, 4 * H, this);
		maxr++;
		maxr &= 63;
	}

	public Dimension getPreferredSize() {
		return new Dimension(2 * W * 2, 2 * H * 2);
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