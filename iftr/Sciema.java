import java.io.*;

public class Sciema {
	public static final boolean SZTUCZKA = true;	// inc / dec 15,x zawsze

	public static void main(String[] args) {
		byte[][] t = new byte[16][16];
		try {
			InputStream is = new FileInputStream("sciema");	// z playera TMC
			for (int j = 0; j < 16; j++)
				is.read(t[j]);
			is.close();
		} catch (Exception e) {
			System.err.println(e);
		}
		System.out.println("; bardzo zabawny kawalek kodu wygenerowany przez Sciema.java");
		System.out.println("; don't edit ;-)");
		for (int j = 1; j < 16; j++) {
			if (j == 8) {
				System.out.println("jasn");
				if (SZTUCZKA)
					System.out.println("\tinc\t15,x");
				System.out.println("\tmva\tjasnt,y\tjasnb-1");
				System.out.println("\tbne\t*");
				System.out.println("jasnb");
			}
			System.out.println("jasn" + j);
			for (int i = 0; i < (SZTUCZKA ? 15 : 16); i++)
				if (t[j - 1][i] < t[j][i])
					System.out.println("\tinc\t" + i + ",x");
			System.out.println("\trts");
		}
		for (int j = 0; j < 15; j++) {
			if (j == 7) {
				System.out.println("sciem");
				if (SZTUCZKA)
					System.out.println("\tdec\t15,x");
				System.out.println("\tmva\tsciemt,y\tsciemb-1");
				System.out.println("\tbne\t*");
				System.out.println("sciemb");
			}
			System.out.println("sciem" + j);
			for (int i = 0; i < (SZTUCZKA ? 15 : 16); i++)
				if (t[j + 1][i] > t[j][i])
					System.out.println("\tdec\t" + i + ",x");
			System.out.println("\trts");
		}
		System.out.println("sciemt");	// takie same
		System.out.print("jasnt\tdta\t");
		for (int j = 1; j < 15; j++)
			System.out.print("jasn" + j + "-jasnb,");
		System.out.println("jasn15-jasnb");
/*		System.out.print("sciemt\tdta\t");
		for (int j = 0; j < 14; j++)
			System.out.print("sciem" + j + "-sciemb,");
		System.out.println("sciem14-sciemb");
*/	}
}