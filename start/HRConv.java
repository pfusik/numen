import java.io.*;

public class HRConv {
	public static void main(String[] args) {
		try {
			PCXImage pcx = new PCXImage(new FileInputStream(args[0]));
			if (pcx.width % 8 != 0)
				throw new IllegalArgumentException("width must be multiple of 8");
			OutputStream os = new FileOutputStream(args[1]);
			for (int y = 0; y < pcx.height; y++) {
				for (int x = 0; x < pcx.width; x += 8) {
					int r = 0;
					for (int i = 0; i < 8; i++)
						switch (pcx.data[y][x + i]) {
						case 0:
							break;
						case 1:
							if (((i ^ y) & 1) != 0)
								break;
						case 2:
							r |= 128 >> i;
							break;
						}
					os.write(r);
				}
				for (int x = 0; x < pcx.width; x += 8) {
					int r = 0;
					for (int i = 0; i < 8; i++)
						switch (pcx.data[y][x + i]) {
						case 0:
							break;
						case 1:
							if (((i ^ y) & 1) == 0)
								break;
						case 2:
							r |= 128 >> i;
							break;
						}
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
