import java.io.*;

public class AsciiToAtascii {
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Usage: AsciiToAtascii ascii_file atascii_file");
			System.exit(3);
		}
		try {
			Reader in = new FileReader(args[0]);
			OutputStream out = new FileOutputStream(args[1]);
			int c;
			while ((c = in.read()) >= 0)
				if (c == '\n')
					out.write(0x9b);
				else if (c != '\r') {
					if ((c >= ' ' && c <= '_') || (c >= 'a' && c <= 'z'))
						out.write(c);
					else
						throw new IllegalArgumentException("Illegal character: " + c);
				}
			out.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}