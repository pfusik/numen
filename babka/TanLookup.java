import java.io.*;

public class TanLookup {
	public static final int ANGLES = 91;

	private static void dump(char c) {
		for (int i = 0; i < ANGLES; i++) {
			if (i % 8 == 0)
				System.out.print("\tdta\t" + c + "(");
			else
				System.out.print(",");
			System.out.print("$" + Integer.toHexString( (int)
				// twice $ff
				(Math.tan((i <= ANGLES / 2 ? i : i - 1) * Math.PI / 2 / (ANGLES - 1)) * 256)
			));
			if (i % 8 == 7 || i == ANGLES - 1)
				System.out.println(")");
		}
	}

	public static void main(String[] args) {
		System.out.println("ANGLES equ " + ANGLES);
		System.out.println("tan_lo");
		dump('l');
		System.out.println("tan_hi");
		dump('h');
	}
}