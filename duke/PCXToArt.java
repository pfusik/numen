import java.io.*;

public class PCXToArt {
	static PCXImage[] tile;

	static void readPCX(String directory) throws Exception {
		String[] fileList = new File(directory).list();
		if (fileList.length == 0)
			throw new Exception("No PCX files found in " + directory);
		tile = new PCXImage[256];
		nextFile:
		for (int i = 0; i < fileList.length; i++) {
			String name = fileList[i];
			if (name.length() != 7)
				continue;
			if (!name.regionMatches(true, 3, ".pcx", 0, 4))
				continue;
			int no = 0;
			for (int j = 0; j < 3; j++) {
				int digit = Character.digit(name.charAt(j), 10);
				if (digit < 0)
					continue nextFile;
				no = 10 * no + digit;
			}
			System.err.println("Reading " + directory + "\\" + name);
			InputStream is = new FileInputStream(new File(directory, name));
			tile[no] = new PCXImage(is);
			is.close();
		}
	}

	static void checkPalettes() {
		int i = 0;
		while (tile[i] == null)
			i++;
		byte[][] firstPalette = tile[i].palette;
		do {
			i++;
			if (tile[i] != null) {
				byte[][] currentPalette = tile[i].palette;
				for (int j = 0; j < 256; j++)
					if (currentPalette[j][0] != firstPalette[j][0]
					 || currentPalette[j][1] != firstPalette[j][1]
					 || currentPalette[j][1] != firstPalette[j][1]) {
						System.err.println("Picture " + i + " has different palette");
						System.exit(1);
					}
			}
		} while (i < 255);
	}

	static void writeShort(OutputStream os, int x) throws IOException {
		os.write(x & 0xff);
		os.write((x >> 8) & 0xff);
	}

	static void writeInt(OutputStream os, int x) throws IOException {
		os.write(x & 0xff);
		os.write((x >> 8) & 0xff);
		os.write((x >> 16) & 0xff);
		os.write((x >> 24) & 0xff);
	}

	static void writeArt(String name) throws IOException {
		System.err.println("Writing " + name);
		OutputStream os = new FileOutputStream(name);
		writeInt(os, 1);	// artversion
		writeInt(os, 256);	// numtiles
		writeInt(os, 0);	// localtilestart
		writeInt(os, 255);	// localtileend
		for (int i = 0; i < 256; i++)	// tilesizx
			writeShort(os, tile[i] == null ? 0 : tile[i].width);
		for (int i = 0; i < 256; i++)	// tilesizy
			writeShort(os, tile[i] == null ? 0 : tile[i].height);
		for (int i = 0; i < 256; i++)	// picanm
			writeInt(os, 0);
		for (int i = 0; i < 256; i++)
			if (tile[i] != null)
				for (int x = 0; x < tile[i].width; x++)
					for (int y = 0; y < tile[i].height; y++)
						os.write(tile[i].data[y][x]);
		os.close();
	}

	static void writePaletteDat(String name) throws IOException {
		System.err.println("Reading " + name);
		File f = new File(name);
		int len = (int) f.length();
		InputStream is = new FileInputStream(f);
		byte[] b = new byte[len];
		is.read(b);
		is.close();
		for (int i = 0; i < 256; i++) {
			b[3 * i] = (byte) ((tile[0].palette[i][0] >> 2) & 0x3f);
			b[3 * i + 1] = (byte) ((tile[0].palette[i][1] >> 2) & 0x3f);
			b[3 * i + 2] = (byte) ((tile[0].palette[i][2] >> 2) & 0x3f);
		}
		System.err.println("Writing " + name);
		OutputStream os = new FileOutputStream(f);
		os.write(b);
		os.close();
	}

	public static void main(String[] args) {
		if (args.length != 2 && args.length != 3) {
			System.err.println("Usage: PCXToArt pcxdir file.art [palette.dat]");
			System.exit(3);
		}
		try {
			readPCX(args[0]);
			checkPalettes();
			writeArt(args[1]);
			if (args.length == 3)
				writePaletteDat(args[2]);
		} catch (Exception e) {
			System.err.println(e.toString());
			System.exit(1);
		}
		System.exit(0);
	}
}
