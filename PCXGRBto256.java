import java.io.*;

public class PCXGRBto256 {
	public static void main(String[] args) {
		try {
			PCXImage pcx = new PCXImage(new FileInputStream(args[0]));
			InputStream is = new FileInputStream(args[1]);
			OutputStream os = new FileOutputStream(args[2]);
			byte[] buf = new byte[pcx.width / 2];
			for (int y = 0; y < pcx.height; y++) {
				is.read(buf);
				os.write(buf);
				for (int x = 0; x < pcx.width; x += 2)
					os.write((pcx.data[y][x] & 0xf0) | ((pcx.data[y][x + 1] >> 4) & 0xf));
			}
			is.close();
			os.close();
		} catch (Exception e) {
			System.err.println(e);
			System.exit(3);
		}
	}
}
