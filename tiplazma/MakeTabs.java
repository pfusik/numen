import java.io.*;

public class MakeTabs {
	private static final int RAYS = 42;
	private static final int ANGLES = 84;

	private static final byte TRANSPARENT = (byte) 0x73; //0x47;
	private static final byte PLASMA_COLOR = 7;

	private static PCXImage pcx;
	private static double maxray;

	private static int ray(int x, int y) {
		x -= pcx.width / 2;
		y -= pcx.height / 2;
		return (int) (Math.sqrt(x * x + y * y) * RAYS / maxray);
	}

	private static int ang(int x, int y) {
		return (int) (ANGLES / 2 - Math.atan2(x - pcx.width / 2, y - pcx.height / 2) * ANGLES / 2 / Math.PI);
	}

	public static void main(String[] args) {
		try {
			pcx = new PCXImage(new FileInputStream(args[0]));
			maxray = Math.sqrt(pcx.width * pcx.width + pcx.height * pcx.height + 3) / 2;
			OutputStream os;
			os = new FileOutputStream("tiplazma.agf");
			// GR.10 even lines
			for (int y = 0; y < pcx.height; y += 2)
				for (int x = 1; x < pcx.width; x += 2) {
					if (pcx.data[y][x] == TRANSPARENT)
						os.write(ang(x, y));
					else
						os.write(0x80 | ((pcx.data[y][x] & 0x0f) >> 1));
				}
			// GR.9 even lines
			for (int y = 0; y < pcx.height; y += 2)
				for (int x = 0; x < pcx.width; x += 2) {
					if (pcx.data[y][x] == TRANSPARENT)
						os.write(ang(x, y));
					else
						os.write(0x80 | (pcx.data[y][x] & 0x0f));
				}
			// GR.9 odd lines
			for (int y = 1; y < pcx.height; y += 2)
				for (int x = 0; x < pcx.width; x += 2) {
					if (pcx.data[y][x] == TRANSPARENT)
						os.write(ang(x, y));
					else
						os.write(0x80 | (pcx.data[y][x] & 0x0f));
				}
			// GR.10 odd lines
			for (int y = 1; y < pcx.height; y += 2)
				for (int x = 1; x < pcx.width; x += 2) {
					if (pcx.data[y][x] == TRANSPARENT)
						os.write(ang(x, y));
					else
						os.write(0x80 | ((pcx.data[y][x] & 0x0f) >> 1));
				}
			os.close();
			os = new FileOutputStream("tiplazma.ray");
			// GR.10 even lines
			for (int y = 0; y < pcx.height; y += 2)
				for (int x = 1; x < pcx.width; x += 2)
					if (pcx.data[y][x] == TRANSPARENT)
						os.write(ray(x, y));
			// GR.9 even lines
			for (int y = 0; y < pcx.height; y += 2)
				for (int x = 0; x < pcx.width; x += 2)
					if (pcx.data[y][x] == TRANSPARENT)
						os.write(ray(x, y));
			// GR.9 odd lines
			for (int y = 1; y < pcx.height; y += 2)
				for (int x = 0; x < pcx.width; x += 2)
					if (pcx.data[y][x] == TRANSPARENT)
						os.write(ray(x, y));
			// GR.10 odd lines
			for (int y = 1; y < pcx.height; y += 2)
				for (int x = 1; x < pcx.width; x += 2)
					if (pcx.data[y][x] == TRANSPARENT)
						os.write(ray(x, y));
			os.close();
			os = new FileOutputStream("tiplazma.g10");
			for (int y = 0; y < pcx.height; y++)
				for (int x = 0; x < pcx.width; x += 4) {
					int hi = pcx.data[y][x] == TRANSPARENT ? PLASMA_COLOR << 4 : pcx.data[y][x] & 0xf0;
					int lo = pcx.data[y][x + 2] == TRANSPARENT ? PLASMA_COLOR : (pcx.data[y][x + 2] & 0xf0) >> 4;
					os.write(hi | lo);
				}
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
