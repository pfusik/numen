import java.io.*;

public class PCXtoGR8 {
	public static void main(String[] args) {
		try {
			PCXImage pcx = new PCXImage(new FileInputStream(args[0]));
			OutputStream os = new FileOutputStream(args[1]);
			for (int y = 0; y < pcx.height; y++)
				for (int x = 0; x < pcx.width; x += 8) {
					int r = 0;
					for (int i = 0; i < 8; i++)
						if (pcx.data[y][x + i] != 0)
							r |= 128 >> i;
					os.write(r);
				}
			os.close();
		} catch (Exception e) {
			System.err.println(e);
			System.exit(3);
		}
	}
}
