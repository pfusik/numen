import java.io.*;

public class MakeTan {
	private static final boolean NEW = true;

	private static final int A = 142;

	private static String toHex(int i) {
		String r = Integer.toHexString(i >> 12);
		r += Integer.toHexString((i >> 8) & 0xf);
		r += Integer.toHexString((i >> 4) & 0xf);
		return r + Integer.toHexString(i & 0xf);
	}

	public static void main(String[] args) {
		int[] t = new int[A];
		if (NEW) {
			int angs = 0;
			int maxdelta = 0;
			for (int y = 50; y >= 1; y--) {
				int x1 = (maxdelta * y) >> 8;
				int x2 = (int) Math.sqrt(50*50 - y*y);
				while (maxdelta < x2 * 0x100 / y) {
					t[angs++] = maxdelta;
					maxdelta += 0x182 / y;
				}
			}
			//System.out.println("angles=" + angs);
		}
		else
			for (int i = 0; i < A; i++)
				t[i] = (int) (0x100 * Math.tan(Math.PI * i / A / 2));
		for (char l = 'l'; l == 'l' || l == 'h'; l += 'h' - 'l') {
			System.out.print("LINE_" + l + 't');
			for (int i = 0; i < A; i++) {
				if (i % 8 == 0)
					System.out.print("\tdta\t" + l + '(');
				System.out.print("$" + toHex(t[i]));
				if (i % 8 < 7 && i < A - 1)
					System.out.print(',');
				else
					System.out.println(')');
			}
		}
	}
}