import java.io.*;

public class FontConv {
	public static void main(String[] args) {
		try {
			PCXImage pcx = new PCXImage(new FileInputStream(args[0]));
			OutputStream os = new FileOutputStream(args[1]);
			for (int j = 0; j < 64; j += 16)
				for (int i = 0; i < 160; i += 16) {
					for (int y = 0; y < 16; y++) {
						int r = 0;
						for (int x = 0; x < 8; x++)
							if (pcx.data[j + y][i + x] == 2)
								r |= 128 >> x;
						os.write(r);
					}
					for (int y = 0; y < 16; y++) {
						int r = 0;
						for (int x = 0; x < 8; x++)
							if (pcx.data[j + y][i + x + 8] == 2)
								r |= 128 >> x;
						os.write(r);
					}
				}
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(3);
		}
	}
}