import java.io.*;

public class ChangeMapColors {
	private static InputStream is;
	private static OutputStream os;

	private static int readShort() throws IOException {
		int r = is.read();
		return r + (is.read() << 8);
	}

	private static void writeShort(int x) throws IOException {
		os.write(x);
		os.write(x >> 8);
	}

	private static int copyShort() throws IOException {
		int r = readShort();
		writeShort(r);
		return r;
	}

	private static void copy(int bytes) throws IOException {
		byte[] buf = new byte[bytes];
		is.read(buf);
		os.write(buf);
	}

	private static void color() throws IOException {
		int x = readShort();
		if (x >= 100 && x <= 151)
			x += 100;
		writeShort(x);
	}

	public static void main(String[] args) {
		try {
			is = new FileInputStream(args[0]);
			os = new FileOutputStream(args[1]);
			copy(20);
			int sectors = copyShort();
			for (int i = 0; i < sectors; i++) {
				copy(16);
				color();
				copy(6);
				color();
				copy(14);
			}
			int walls = copyShort();
			for (int i = 0; i < walls; i++) {
				copy(16);
				color();
				copy(14);
			}
			int sprites = copyShort();
			for (int i = 0; i < sprites; i++) {
				copy(44);
			}
			is.close();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(3);
		}
	}
}