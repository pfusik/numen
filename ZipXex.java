import java.io.*;
import java.util.zip.*;

public class ZipXex {
	private static int putWord(byte[] buf, int index, int word) {
		buf[index++] = (byte) word;
		buf[index++] = (byte) (word >> 8);
		return index;
	}

	private static int putPoke(byte[] buf, int index, int adr, int val) {
		buf[index++] = (byte) 0xa9;
		buf[index++] = (byte) val;
		buf[index++] = (byte) 0x85;
		buf[index++] = (byte) adr;
		return index;
	}

	public static void main(String[] args) {
		if (args.length != 4 || args[1].charAt(0) != '$') {
			System.err.println("Usage: ZipXex zipxex.obx $b600 input.xex output.xex");
			System.exit(3);
		}
		final String zipXexFilename = args[0];
		final int inflateAddress = Integer.parseInt(args[1].substring(1), 16);
		final String inFilename = args[2];
		final String outFilename = args[3];
		final int initLen = 16;
		byte[] in = new byte[65536];
		int uncompressedLen = 0;
		// read source file
		try {
			InputStream is = new FileInputStream(inFilename);
			uncompressedLen = is.read(in);
			is.close();
		} catch (IOException e) {
			System.err.println(e.toString());
			System.exit(3);
		}
		int uncompressedAddress = (in[2] & 0xff) | ((in[3] & 0xff) << 8);
		int runAddress = (in[uncompressedLen - 2] & 0xff) | ((in[uncompressedLen - 1] & 0xff) << 8);
		System.out.println(inFilename + " summary:");
		System.out.println("File size:       " + uncompressedLen);
		System.out.println("Start address:   $" + Integer.toHexString(uncompressedAddress));
		System.out.println("Run address:     $" + Integer.toHexString(runAddress));
		// compress
		Deflater d = new Deflater(Deflater.BEST_COMPRESSION);
		d.setInput(in, 6, uncompressedLen - 12);
		d.finish();
		byte[] out = new byte[65536];
		int compressedLen = d.deflate(out, 4, 65530) - 6;
		int compressedAddress = inflateAddress - initLen - compressedLen;
		System.out.println("compression summary:");
		System.out.println("Compressed size: " + compressedLen);
		System.out.println("Start address:   $" + Integer.toHexString(compressedAddress));
		// read 'inflate'
		int inflateLen = 0;
		try {
			InputStream is = new FileInputStream(zipXexFilename);
			inflateLen = is.read(out, 6 + compressedLen + initLen - 6, 32768);
		} catch (IOException e) {
			System.err.println(e.toString());
			System.exit(3);
		}
		int endAddress = inflateAddress + inflateLen - 6 - 1;
		// set header
		putWord(out, 0, 0xffff);
		putWord(out, 2, compressedAddress);
		putWord(out, 4, endAddress);
		// set init for 'inflate'
		int ptr = 6 + compressedLen;
		ptr = putPoke(out, ptr, 0xf0, compressedAddress);
		ptr = putPoke(out, ptr, 0xf1, compressedAddress >> 8);
		ptr = putPoke(out, ptr, 0xf2, uncompressedAddress);
		ptr = putPoke(out, ptr, 0xf3, uncompressedAddress >> 8);
		// set init and run addresses
		ptr = 6 + compressedLen + initLen + inflateLen - 6;
		ptr = putWord(out, ptr, 0x2e0);
		ptr = putWord(out, ptr, 0x2e3);
		ptr = putWord(out, ptr, runAddress);
		ptr = putWord(out, ptr, inflateAddress - initLen);
		try {
			OutputStream os = new FileOutputStream(outFilename);
			os.write(out, 0, ptr);
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(3);
		}
		System.out.println(outFilename + " summary:");
		System.out.println("File size:       " + ptr);
	}
}
