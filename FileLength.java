import java.io.*;

public class FileLength {
	public static void main(String[] args) {
		try {
			int c;
			int i = 0;
			while ((c = System.in.read()) > 0) {
				if (c == '%')
					System.out.print(new File(args[i++]).length());
				else
					System.out.write(c);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(3);
		}
	}
}
