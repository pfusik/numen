import java.io.*;

public class ReallocMPT {
	static void reallocMPT(String oldFile, int newAddr, String newFile)
	throws Exception {
		if (newAddr < 0 || newAddr > 0xfffe)
			throw new Exception("Bad address");
		InputStream is = new FileInputStream(oldFile);
		byte[] buffer = new byte[32768];
		int len = is.read(buffer);
		is.close();
		if (len < 0x1d0 || len >= 32768)
			throw new Exception("Bad file length: " + len);
		if (buffer[0] != -1 || buffer[1] != -1)
			throw new Exception("Not a binary file");
		int oldAddr = (buffer[2] & 0xff) | ((buffer[3] & 0xff) << 8);
		int endAddr = (buffer[4] & 0xff) | ((buffer[5] & 0xff) << 8);
		System.out.println(oldFile + ": " + oldAddr + "-" + endAddr);
		System.out.println("Pattern length = " + buffer[0x1ce]);
		System.out.println("Default tempo  = " + buffer[0x1cf]);
		buffer[2] = (byte) (newAddr & 0xff);
		buffer[3] = (byte) (newAddr >> 8);
		endAddr = newAddr + len - 6 - 1;
		buffer[4] = (byte) (endAddr & 0xff);
		buffer[5] = (byte) (endAddr >> 8);
		for (int i = 6; i < 0xc6; i += 2) {
			int addr = (buffer[i] & 0xff) | ((buffer[i + 1] & 0xff) << 8);
			if (addr != 0) {
				addr += newAddr - oldAddr;
				buffer[i] = (byte) (addr & 0xff);
				buffer[i + 1] = (byte) (addr >> 8);
			}
		}
		for (int i = 0; i < 4; i++) {
			int addr = (buffer[0x1c6 + i] & 0xff) | ((buffer[0x1ca + i] & 0xff) << 8);
			addr += newAddr - oldAddr;
			buffer[0x1c6 + i] = (byte) (addr & 0xff);
			buffer[0x1ca + i] = (byte) (addr >> 8);
		}
		System.out.println(newFile + ": " + newAddr + "-" + endAddr);
		OutputStream os = new FileOutputStream(newFile);
		os.write(buffer, 0, len);
		os.close();
	}

	public static void main(String[] args) {
		if (args.length != 3 || args[1].charAt(0) != '$') {
			System.err.println("Usage: ReallocMPT source_file $addr dest_file");
			System.exit(3);
		}
		try {
			reallocMPT(args[0], Integer.parseInt(args[1].substring(1), 16), args[2]);
			System.exit(0);
		} catch (Exception e) {
			System.err.println(e.toString());
			System.exit(3);
		}
	}
}
