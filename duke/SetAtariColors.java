import java.io.*;

public class SetAtariColors {
	private static byte[][] pal;
	private static int[] code;

	private static void update(String name) throws Exception {
		InputStream is = new FileInputStream(name);
		PCXImage pcx = new PCXImage(is);
		is.close();
		boolean same = true;
		for (int i = 0; i < 9; i++)
			if (!java.util.Arrays.equals(pcx.palette[i], pal[code[i]])) {
				pcx.palette[i] = pal[code[i]];
				same = false;
			}
		if (same)
			System.out.println("Skipping " + name);
		else {
			System.out.println("Updating " + name);
			OutputStream os = new FileOutputStream(name);
			pcx.write(os);
			os.close();
		}
	}


	public static void main(String[] args) {
		if (args.length < 11) {
			System.err.println("Usage: SetAtariColors real.act c0 c1 ... c8 pic1.pcx pic2.pcx ...");
			System.err.println("or (reads fnames from stdin)");
			System.err.println("SetAtariColors real.act c0 c1 ... c8 -");
			return;
		}
		try {
			InputStream is = new FileInputStream(args[0]);
			pal = new byte[256][];
			for (int i = 0; i < 256; i++)
				is.read(pal[i] = new byte[3]);
			is.close();
			code = new int[9];
			for (int i = 0; i < 9; i++) {
				code[i] = Integer.parseInt(args[1 + i], 16);
				if (code[i] < 0 || code[i] > 0xff || (code[i] & 1) != 0)
					throw new IllegalArgumentException();
			}
			if (args[10].equals("-")) {
				LineNumberReader in = new LineNumberReader(new InputStreamReader(System.in));
				String name;
				while ((name = in.readLine()) != null && !name.equals(""))
					update(name);
			}
			else
				for (int j = 10; j < args.length; j++)
					update(args[j]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}