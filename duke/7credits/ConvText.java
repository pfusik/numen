import java.io.*;

public class ConvText {
	public static void main(String[] args) {
		try {
			PCXImage pcx = new PCXImage(new FileInputStream(args[0]));
			if (pcx.width % 8 != 0)
				throw new IllegalArgumentException();
			OutputStream os = new FileOutputStream(args[1]);
			if (false) {
				// liniami
				int buf = 1;
				for (int y = 0; y < pcx.height; y++)
					for (int x = 0; x < pcx.width; x++) {
						buf <<= 1;
						if (pcx.data[y][x] < 0)
							buf++;
						if (buf >= 0x100) {
							os.write(buf);
							buf = 1;
						}
					}
			}
			else if (false) {
				// kolumnami
				for (int x = 0; x < pcx.width; x += 8)
					for (int y = 0; y < pcx.height; y++) {
						int buf = 0;
						for (int i = 0; i < 8; i++)
							if (pcx.data[y][x + i] < 0)
								buf |= 0x80 >> i;
						os.write(buf);
					}
			}
			else if (false) {
				// kazdy napis kolumnami
				for (int n = 0; n < pcx.height; n += 20)
					for (int x = 0; x < pcx.width; x += 8)
						for (int y = 0; y < 20; y++) {
							int buf = 0;
							for (int i = 0; i < 8; i++)
								if (pcx.data[n + y][x + i] < 0)
									buf |= 0x80 >> i;
							os.write(buf);
						}
			}
			else {
				// napisy obok siebie liniami
				for (int y = 0; y < 20; y++)
					for (int n = 0; n < pcx.height; n += 20)
						for (int x = 0; x < pcx.width; x += 8) {
							int buf = 0;
							for (int i = 0; i < 8; i++)
								if (pcx.data[n + y][x + i] < 0)
									buf |= 0x80 >> i;
							os.write(~buf);
						}
			}
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(3);
		}
	}
}