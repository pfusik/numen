import java.io.*;

public class Test {
	public static void main(String[] args) {
		char[] ct = {'0', '2', '4', '6', '8', 'A', 'C', 'E'};
		for (int j = 0; j < 8; j++) {
			System.out.println("FADE_table" + ct[j]);
			System.out.print("\tdta\t$0");
			for (int i = 1; i < 16; i++) {
				int x = ((i + 2) * j + 4) / 8;
				if (x < 2)
					x = 0;
				else
					x = (x - 2) | 1;
				System.out.print(",$" + Integer.toHexString(x));
			}
			System.out.println();
		}
	}
}