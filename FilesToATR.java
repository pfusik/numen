import java.io.*;

public class FilesToATR {
	private static final int VTOC_SECTOR = 0x168;
	private static final int DIR_SECTOR = 0x169;
	private static final int DIR_SECS = 8;

	private static final int SECTORS_INDEX = 0;
	private static final int BYTES_INDEX = 1;
	private static final int ATR_INDEX = 2;
	private static final int BOOT_INDEX = 3;
	private static final int FILE_INDEX = 4;

	private static void putWord(byte[] b, int o, int x) {
		b[o] = (byte) x;
		b[o + 1] = (byte) (x >> 8);
	}

	public static void main(String[] args) {
		if (args.length < 3) {
			System.err.println("Usage: FilesToATR <sectors> <bytesPerSector> file.atr bootsecs.raw file1 file2 file3 ...");
			System.exit(3);
		}
		int sectors = Integer.parseInt(args[SECTORS_INDEX]);
		if (sectors < DIR_SECTOR + DIR_SECS - 1)
			sectors = DIR_SECTOR + DIR_SECS - 1;
		int sectorSize = Integer.parseInt(args[BYTES_INDEX]);
		byte[][] atr = new byte[2000][];
		atr[0] = new byte[16];
		atr[0][0] = (byte) 0x96;
		atr[0][1] = (byte) 0x02;
		atr[0][4] = (byte) sectorSize;
		atr[0][5] = (byte) (sectorSize >> 8);
		//atr[0][15] = (byte) 0x01;
		try {
			InputStream is = new FileInputStream(args[BOOT_INDEX]);
			for (int i = 1; i <= 3; i++) {
				atr[i] = new byte[128];
				is.read(atr[i]);
			}
			is.close();
			for (int i = 4; i <= sectors; i++)
				atr[i] = new byte[sectorSize];
			atr[VTOC_SECTOR][0] = 2;
			int sec = 4;
			for (int i = FILE_INDEX; i < args.length; i++) {
				String name = args[i];
				is = new FileInputStream(name);
				if (name.lastIndexOf('/') >= 0)
					name = name.substring(name.lastIndexOf('/') + 1);
				byte[] dirs = atr[DIR_SECTOR + (i - FILE_INDEX) / 8];
				int diro = ((i - FILE_INDEX) & 7) << 4;
				dirs[diro] = 0x42;
				putWord(dirs, diro + 3, sec);
				int j = 0;
				for (int k = 5; k < 16; k++) {
					char c = ' ';
					get_char:
					if (j < name.length()) {
						if (name.charAt(j) == '.') {
							if (k == 5 + 8)
								j++;
							else
								break get_char;
						}
						c = Character.toUpperCase(name.charAt(j++));
					}
					dirs[diro + k] = (byte) c;
				}
				int len = 0;
				byte[] prev = null;
				for (;;) {
					if (sec > sectors)
						atr[sec] = new byte[sectorSize];
					int b = is.read(atr[sec], 0, sectorSize - 3);
					if (prev != null) {
						if (b <= 0)
							break;
						prev[sectorSize - 3] = (byte) ((sec >> 8) + ((i - FILE_INDEX) << 2));
						prev[sectorSize - 2] = (byte) sec;
					}
					prev = atr[sec];
					prev[sectorSize - 3] = (byte) ((i - FILE_INDEX) << 2);
					prev[sectorSize - 2] = 0;
					prev[sectorSize - 1] = b < 0 ? 0 : (byte) b;
					len++;
					sec++;
					if (sec == VTOC_SECTOR)
						sec = DIR_SECTOR + DIR_SECS;
				}
				putWord(dirs, diro + 1, len);
				is.close();
			}
			System.out.println("Last used sector: " + (sec - 1));
			int last = Math.max(sectors, sec - 1);
			putWord(atr[VTOC_SECTOR], 1, last - 4 - 1 - DIR_SECS);
			switch (sectorSize) {
			case 128:
				putWord(atr[0], 2, last << 3);
				break;
			case 256:
				putWord(atr[0], 2, (last << 4) - 0x18);
				break;
			default:
				throw new IllegalArgumentException();
			}
			OutputStream os = new FileOutputStream(args[ATR_INDEX]);
			for (int i = 0; i <= last; i++)
				os.write(atr[i]);
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(3);
		}
		
	}
}