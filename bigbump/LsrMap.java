import java.io.*;

public class LsrMap {
	public static void main(String[] args) {
		try {
			PCXImage pcx = new PCXImage(new FileInputStream(args[0]));
			OutputStream os = new FileOutputStream(args[1]);
			for (int y = 0; y < pcx.height; y++)
				for (int x = 0; x < pcx.width; x++)
					os.write((pcx.data[y][x] >> 2) & 0x3f);
			os.close();
/*			InputStream is = new FileInputStream("theend.tif");
			is.skip(0x19);
			byte[] b = new byte[103 * 89];
			is.read(b);
			is.close();
			for (int i = 0; i < b.length; i++)
				b[i] = (byte) ((b[i] >> 2) & 0x3f);
			OutputStream os = new FileOutputStream("theend.raw");
			os.write(b);
			os.close();
*/		} catch (Exception e) {
			System.err.println(e);
		}
	}
}