import java.io.*;
import java.util.zip.Deflater;

public class DemoLinker {
	private static final int Banks_LOAD_LAST_INDEX = 15;
	private static final int BANKS_IN_FIRST_PART = 5;

	private static final byte[] inputBuffer = new byte[65536];
	private static int inputIndex;

	private static final Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION, true);

	private static final byte[] outputBuffer = new byte[300000];
	private static int outputIndex;

	private static int inputLen;

	private static void link(String name, int skip, int len, boolean obx, boolean pack) throws IOException {
		InputStream is = new FileInputStream(name);
		if (skip > 0)
			is.read(new byte[skip]);
		inputIndex += is.read(inputBuffer, inputIndex, len);
		is.close();
		if (pack) {
			if (obx) {
				inputBuffer[inputIndex++] = 0;
				inputBuffer[inputIndex++] = 0;
			}
			deflater.reset();
			deflater.setInput(inputBuffer, 0, inputIndex);
			deflater.finish();
			inputLen += inputIndex;
			outputIndex += deflater.deflate(outputBuffer, outputIndex, outputBuffer.length - outputIndex);
			inputIndex = 0;
		}
	}

	private static void link(String name, int skip, boolean obx, boolean pack) throws IOException {
		link(name, skip, inputBuffer.length - inputIndex, obx, pack);
	}

	private static void link(String name, boolean obx, boolean pack) throws IOException {
		link(name, 0, obx, pack);
	}

	private static OutputStream outputStream;

	private static void writeWord(int word) throws IOException {
		outputStream.write(word & 0xff);
		outputStream.write((word >> 8) & 0xff);
	}

	public static void main(String[] args) {
		try {
			inputIndex = 0;
			outputIndex = 0;
			inputLen = 0;
			link("loader.obx", false, false);
			outputStream = new FileOutputStream("numen1.tqa");
			outputStream.write(inputBuffer, 0, inputIndex);
			inputIndex = 0;

			// name, obx, pack
			link("title/title.nex", true, true);
			link("flash/flash.nex", true, true);
			link("flash/tunnelz.ang", false, false);
			link("flash/tunnelz.ray", false, true);
			link("duke/1intro/duke.nex", true, true);
			link("duke/2corrid/duke.nex", true, true);
			link("tunnel56/tunnel56.nex", true, true);
			link("tunnel56/taba.dat", false, false);
			link("tunnel56/tabr.dat", false, true);
			link("env/env.nex", true, true);
			link("babka/babka.nex", true, true);
			link("hipbump/hipbump.nex", true, true);
			link("hipbump/bumpmap.gfx", false, true);
			link("duke/3window/duke.nex", true, true);
			link("tunnel34/tunnel34.nex", true, true);
			link("tunnel34/tun_tabs.dat", false, true);
			link("rzoom/rzoom.nex", true, true);
			link("duke/4exit/duke.nex", true, true);
			link("iftr/iftr.nex", true, true);
			link("iftr/statek.raw", false, false);
			link("iftr/einsrays.raw", false, true);
			link("plasma2/plasma2.nex", true, true);
			link("duke/5pool/duke.nex", true, true);
			if (false) {
				link("hiplazma/hiplazma.nex", true, true);
				link("hiplazma/hiplazma.msk", false, false);
				link("hiplazma/hiplazma.gfx", false, false);
				link("hiplazma/hiplazma.ray", false, true);
			}
			else {
				link("tiplazma/tiplazma.nex", true, true);
				link("tiplazma/tiplazma.agf", false, true);
			}
			if (false) {
				link("logo/logo.nex", true, true);
				link("logo/logo.gfx", 0x900, 0x4f5c, false, true);
			}
			else {
				link("logo256/logo256.nex", true, true);
				link("logo256/logo256.gfx", 21 * 390, 40 * 390, false, true);
				link("logo256/logo256.gfx", 61 * 390, 40 * 390, false, true);
			}
			link("duke/6box/duke.nex", true, true);
			link("tunnel46/tunnel46.nex", true, true);
			link("musk/musk.nex", true, true);
			link("rubik/rubik.nex", true, true);
			link("mocap/mocap.nex", true, true);
			link("tvectys/tvdisp.nex", true, false);
			link("tvectys/tvcalc.obx", 2, true, true);
			link("duke/7credits/duke.nex", true, true);
			link("bigbump/bigbump.nex", true, true);

			int bank = Banks_LOAD_LAST_INDEX;
			for (int i = 0; i < outputIndex; i += 0x3ffa) {
				int len = Math.min(outputIndex - i, 0x3ffa);
				if (bank == Banks_LOAD_LAST_INDEX - BANKS_IN_FIRST_PART) {
					outputStream.close();
					outputStream = new FileOutputStream("numen2.tqa");
				}
				writeWord(0x8000);
				writeWord(0x8006 + len - 1);
				writeWord(0x4006);
				writeWord(0x4006 + len - 1);
				outputStream.write(outputBuffer, i, len);
				writeWord(0x0000);
				writeWord(0x02e2);
				writeWord(0x02e3);
				writeWord(0x3e20);
				bank--;
			}
			System.out.println("; uncompressed: " + inputLen);
			System.out.println(";   compressed: " + outputIndex);
			outputIndex = 0;

			link("playinf.obx", true, true);
			link("inflate.obx", 2, true, false);
			writeWord(0x8000);
			writeWord(0x8000 + 4 + outputIndex + inputIndex + 2 + 2 - 1);
			writeWord(0x4006);
			writeWord(0x4006 + outputIndex - 1);
			outputStream.write(outputBuffer, 0, outputIndex);
			outputStream.write(inputBuffer, 0, inputIndex);
			writeWord(0x0000);
			writeWord(0x0000);
			writeWord(0x02e0);
			writeWord(0x02e1);
			writeWord(0x3e20);
			outputStream.close();
			System.out.println("Banks_LOAD_FIRST_INDEX equ " + bank);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(3);
		}
	}
}