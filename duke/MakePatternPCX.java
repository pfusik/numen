import java.io.*;

public class MakePatternPCX {
	private static void write(String name, PCXImage p) throws IOException {
		OutputStream os = new FileOutputStream(name);
		p.write(os);
		os.close();
	}

	private static void writeSolid(int name, PCXImage p, int c) throws IOException {
		for (int y = 0; y < 16; y++)
			for (int x = 0; x < 16; x++)
				p.data[y][x] = (byte) c;
		write(name + ".pcx", p);
	}

	private static void writeMix(int name, PCXImage p, int c1, int c2) throws IOException {
		for (int y = 0; y < 16; y++)
			for (int x = 0; x < 16; x++)
				p.data[y][x] = (byte) (((x ^ y) & 4) != 0 ? c2 : c1);
		write(name + ".pcx", p);
	}

	public static void main(String[] args) {
		int cols;
		int[] colorCodes = new int[25];
		try {
			switch (args.length) {
			case 2:
				int base = Integer.parseInt(args[1], 16);
				if (base < 0 || base > 0xf)
					throw new IllegalArgumentException();
				for (int i = 0; i < 16; i++)
					colorCodes[i] = (base << 4) + i;
				cols = 16;
				break;
			case 10:
				for (int i = 0; i < 9; i++) {
					int code = Integer.parseInt(args[1 + i], 16);
					if (code < 0 || code > 0xff || (code & 1) != 0)
						throw new IllegalArgumentException();
					colorCodes[i] = code;
				}
				cols = 9;
				break;
			case 11:
				int base2 = Integer.parseInt(args[1], 16);
				if (base2 < 0 || base2 > 0xf)
					throw new IllegalArgumentException();
				for (int i = 0; i < 16; i++)
					colorCodes[i] = (base2 << 4) + i;
				for (int i = 0; i < 9; i++) {
					int code = Integer.parseInt(args[2 + i], 16);
					if (code < 0 || code > 0xff || (code & 1) != 0)
						throw new IllegalArgumentException();
					colorCodes[16 + i] = code;
				}
				cols = 25;
				break;
			default:
				throw new IllegalArgumentException();
			}
		} catch (IllegalArgumentException e) {
			System.err.println("Usage for GR9 patterns:");
			System.err.println("MakePatternPCX real.act one_hex_digit_color_code");
			System.err.println("Usage for GR10 patterns:");
			System.err.println("MakePatternPCX real.act 9x:two_hex_digits_color_code");
			System.err.println("Usage for GR9+GR10 patterns:");
			System.err.println("MakePatternPCX real.act one_hex_digit_color_code 9x:two_hex_digits_color_code");
			System.exit(3);
			return;
		}
		try {
			InputStream is = new FileInputStream(args[0]);
			byte[] pal = new byte[768];
			is.read(pal);
			is.close();
			PCXImage p = new PCXImage(16, 16);
			for (int i = 0; i < cols; i++) {
				int j = 3 * colorCodes[i];
				p.setColor(i, pal[j], pal[j + 1], pal[j + 2]);
			}
			p.setColor(0xff, 0xff, 0x00, 0xff);
			for (int y = 0; y < 16; y++)
				for (int x = 0; x < 16; x++)
					p.data[y][x] = (byte) (((x + y) & 8) != 0 ? 0x03 : 0x08);
			write("000.pcx", p);
			int pic;
			for (pic = 0; pic < (cols == 9 ? 9 : 16); pic++)
				writeSolid(100 + pic, p, pic);
			pic = 116;
			for (int c1 = 0; c1 < 9; c1++)
				for (int c2 = c1 + 1; c2 < 9; c2++)
					writeMix(pic++, p, c1, c2);
			if (cols == 25) {
				for (pic = 0; pic < 9; pic++)
					writeSolid(200 + pic, p, pic + 16);
				pic = 216;
				for (int c1 = 16; c1 < 25; c1++)
					for (int c2 = c1 + 1; c2 < 25; c2++)
						writeMix(pic++, p, c1, c2);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
