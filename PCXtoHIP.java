import java.io.*;

public class PCXtoHIP {
	public static void main(String[] args) {
		try {
			PCXImage pcx = new PCXImage(new FileInputStream(args[0]));
			OutputStream os = new FileOutputStream(args[1]);
			for (int y = 0; y < pcx.height; y++)
				for (int x = 0; x < pcx.width; x += 4)
					os.write((pcx.data[y][x] & 0xf0) | (pcx.data[y][x + 2] >> 4 & 0xf));
			os.close();
			os = new FileOutputStream(args[2]);
			for (int y = 0; y < pcx.height; y++)
				for (int x = 1; x < pcx.width; x += 4)
					os.write((pcx.data[y][x] >> 1 & 0x70) | (pcx.data[y][x + 2] >> 5 & 7));
			os.close();
		} catch (Exception e) {
			System.err.println(e);
			System.exit(3);
		}
	}
}
