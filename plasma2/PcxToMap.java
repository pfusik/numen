import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;

public class PcxToMap {
	private static final int R = 51;
	private static final int A = 128;
	private static final double Z = 1.0;

	public static void main(String[] args) {
		try {
			PCXImage pcx = new PCXImage(new FileInputStream("tqa.pcx"));
			int w = pcx.width;
			int h = pcx.height;
			byte[][] ctab = new byte[R][A];
			for (int r = 0; r < R; r++)
				for (int a = 0; a < A; a++) {
					int x = (int) (w / 2 + Z * r * Math.sin(a * 2 * Math.PI / A));
					int y = (int) (h / 2 - Z * r * Math.cos(a * 2 * Math.PI / A));
					ctab[r][a] = x >= 0 && x < w && y >= 0 && y < h ? (byte) (pcx.data[y][x] & 0xf0) : 0;
				}
			OutputStream os = new FileOutputStream("plasma2.map");
			for (int r = 0; r < R; r++)
				os.write(ctab[r]);
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}