import java.io.*;

public class PCXtoGR9 {
	public static void main(String[] args) {
		try {
			PCXImage pcx = new PCXImage(new FileInputStream(args[0]));
			OutputStream os = new FileOutputStream(args[1]);
			for (int y = 0; y < pcx.height; y++)
				for (int x = 0; x < pcx.width; x += 2)
					os.write((pcx.data[y][x] & 0xf0) | ((pcx.data[y][x + 1] >> 4) & 0xf));
			os.close();
		} catch (Exception e) {
			System.err.println(e);
			System.exit(3);
		}
	}
}
