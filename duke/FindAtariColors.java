import java.io.*;

public class FindAtariColors {
	public static final boolean EVEN_ONLY = true;

	private static String hex(int x) {
		return x < 16 ? "0" + Integer.toHexString(x) : Integer.toHexString(x);
	}

	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("Usage: FindAtariColors real.act pic.pcx");
			return;
		}
		try {
			InputStream is = new FileInputStream(args[0]);
			byte[][] pal = new byte[256][];
			for (int i = 0; i < 256; i++)
				is.read(pal[i] = new byte[3]);
			is.close();
			is = new FileInputStream(args[1]);
			PCXImage pcx = new PCXImage(is);
			is.close();
			for (int i = 0; i < 9; i++) {
				int p0 = pcx.palette[i][0] & 0xff;
				int p1 = pcx.palette[i][1] & 0xff;
				int p2 = pcx.palette[i][2] & 0xff;
				int b = -1;
				int m = 3 * 256 * 256;
				for (int j = 0; j < 256; j += (EVEN_ONLY ? 2 : 1)) {
					int q0 = pal[j][0] & 0xff;
					int q1 = pal[j][1] & 0xff;
					int q2 = pal[j][2] & 0xff;
					int d = (p0 - q0) * (p0 - q0)
					+ (p1 - q1) * (p1 - q1)
					+ (p2 - q2) * (p2 - q2);
					if (d < m) {
						b = j;
						m = d;
					}
				}
				int q0 = pal[b][0] & 0xff;
				int q1 = pal[b][1] & 0xff;
				int q2 = pal[b][2] & 0xff;
				System.out.println(i + " (" + hex(p0) + "," + hex(p1) + "," + hex(p2) + ") ~= #"
				+ hex(b) + " (" + hex(q0) + "," + hex(q1) + "," + hex(q2) + ")");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}