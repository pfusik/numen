import java.io.*;

public class Make3D1 {
	public static final int ENV_R = 31;

	// --- torus, triangle
	private static double[] xc;
	private static double[] yc;
	private static double[] an;

	private static void init(int segs) {
		xc = new double[segs];
		yc = new double[segs];
		an = new double[segs];
	}

	private static void ngon(int n, int segs, int r1, int r2) throws IOException {
		init(segs);
		for (int i = 0; i < n; i++) {
			double a = i * Math.PI * 2 / n;
			double x = 32 + r1 * Math.cos(a);
			double y = 32 + r1 * Math.sin(a);
			for (int j = 0; j < segs / n; j++) {
				double b = j * Math.PI * 2 / (segs - n) - Math.PI / n;
				xc[i * segs / n + j] = x + r2 * Math.cos(a + b);
				yc[i * segs / n + j] = y + r2 * Math.sin(a + b);
				an[i * segs / n + j] = a + b;
			}
		}
	}

	private static void write(String name, int sides, int r) throws IOException {
		int segs = xc.length;
		OutputStream os = new FileOutputStream(name);
		os.write(segs * sides);
		for (int j = 0; j < sides; j++) {
			double b = j * Math.PI * 2 / sides;
			for (int i = 0; i < segs; i++) {
				os.write((int) (xc[i] - r * Math.cos(an[i]) * Math.sin(b)));
				os.write((int) (yc[i] - r * Math.sin(an[i]) * Math.sin(b)));
				os.write((int) (32 - r * Math.cos(b)));
				os.write((int) (32 - ENV_R * Math.cos(an[i]) * Math.sin(b)));
				os.write((int) (32 - ENV_R * Math.sin(an[i]) * Math.sin(b)));
				os.write((int) (32 - ENV_R * Math.cos(b)));
			}
		}
		for (int j = 0; j < sides; j++) {
			int j2 = (j + 1) % sides;
			for (int i = 0; i < segs; i++) {
				int i2 = (i + 1) % segs;
				os.write(1 + i + segs * j);
				os.write(1 + i2 + segs * j);
				os.write(1 + i2 + segs * j2);
				os.write(1 + i + segs * j2);
				os.write(0);
			}
		}
		os.write(0);
		os.close();
	}

	// --- torus
	// RADIUS1,RADIUS2,SEGMENTS,SIDES -> vide 3D Studio MAX
	public static final int TORUS_RADIUS1 = 21;
	public static final int TORUS_RADIUS2 = 10;
	public static final int TORUS_SEGMENTS = 14;
	public static final int TORUS_SIDES = 8;

	private static void torus() throws IOException {
		init(TORUS_SEGMENTS);
		for (int i = 0; i < TORUS_SEGMENTS; i++) {
			double a = i * Math.PI * 2 / TORUS_SEGMENTS;
			xc[i] = 32 + TORUS_RADIUS1 * Math.cos(a);
			yc[i] = 32 + TORUS_RADIUS1 * Math.sin(a);
			an[i] = a;
		}
		write("torus.3d1", TORUS_SIDES, TORUS_RADIUS2);
	}

	// --- triangle
	public static final int TRIANGLE_RADIUS1 = 14;
	public static final int TRIANGLE_RADIUS2 = 10;
	public static final int TRIANGLE_RADIUS3 = 7;
	public static final int TRIANGLE_SEGMENTS = 12;
	public static final int TRIANGLE_SIDES = 6;

	private static void triangle() throws IOException {
		ngon(3, TRIANGLE_SEGMENTS, TRIANGLE_RADIUS1, TRIANGLE_RADIUS2);
		write("triangle.3d1", TRIANGLE_SIDES, TRIANGLE_RADIUS3);
	}

	// --- hammer
	/*   1
	     /\
	   5/  \2
	   |\6 /
	   | \/|9----------------+10
	   | | |11---------------+12
	  7\ |3|...               ...
	    \+-+
	     8  4
	*/
	public static final int HAMMER_METAL_WIDTH = 12;
	public static final int HAMMER_METAL_HEIGHT = 26;
	public static final int HAMMER_METAL_LENGTH = 8;
	public static final int HAMMER_METAL_HEIGHT1 = 15;
	public static final int HAMMER_WOOD_LENGTH = 30;
	public static final int HAMMER_WOOD_RADIUS = 4;
	public static final int HAMMER_WOOD_SIDES = 6;
	private static final int HAMMER_X1 = 32 - (HAMMER_METAL_LENGTH + HAMMER_WOOD_LENGTH) / 2;
	private static final int HAMMER_X2 = HAMMER_X1 + HAMMER_METAL_LENGTH;
	private static final int HAMMER_X3 = HAMMER_X2 + HAMMER_WOOD_LENGTH;
	private static final int HAMMER_Y1 = 32 - HAMMER_METAL_HEIGHT / 2;
	private static final int HAMMER_Y2 = HAMMER_Y1 + HAMMER_METAL_HEIGHT - HAMMER_METAL_HEIGHT1;
	private static final int HAMMER_Y3 = HAMMER_Y1 + HAMMER_METAL_HEIGHT;
	private static final int HAMMER_Z1 = 32 - HAMMER_METAL_WIDTH / 2;
	private static final int HAMMER_Z2 = HAMMER_Z1 + HAMMER_METAL_WIDTH;
	// normalne œcian metalu
	private static final double[] n_bok = {0, 0, -1};
	private static final double[] n_bok2 = {0, 0, 1};
	private static final double[] n_tyl = {1, 0, 0};
	private static final double alfa = Math.atan2(HAMMER_METAL_LENGTH, HAMMER_METAL_HEIGHT - HAMMER_METAL_HEIGHT1);
	private static final double[] n_skos = {-Math.cos(alfa), -Math.sin(alfa), 0};
	private static final double[] n_przod = {-1, 0, 0};
	private static final double[] n_spod = {0, 1, 0};

	private static void norm(OutputStream os, double[] n1, double[] n2, double[] n3) throws IOException {
		os.write((int) (32 + (n1[0] + n2[0] + n3[0]) * ENV_R / 3));
		os.write((int) (32 + (n1[1] + n2[1] + n3[1]) * ENV_R / 3));
		os.write((int) (32 + (n1[2] + n2[2] + n3[2]) * ENV_R / 3));
	}

	private static void mepo(OutputStream os, int x, int y, double[] n1, double[] n2) throws IOException {
		os.write(x);
		os.write(y);
		os.write(HAMMER_Z2);
		norm(os, n1, n2, n_bok2);
		os.write(x);
		os.write(y);
		os.write(HAMMER_Z1);
		norm(os, n1, n2, n_bok);
	}

	private static void hammer() throws IOException {
		OutputStream os = new FileOutputStream("hammer.3d1");
		os.write(8 + 2 * HAMMER_WOOD_SIDES);
		// punkty metalu
		mepo(os, HAMMER_X2, HAMMER_Y1, n_skos, n_tyl);	// 1,2
		mepo(os, HAMMER_X2, HAMMER_Y3, n_spod, n_tyl);	// 3,4
		mepo(os, HAMMER_X1, HAMMER_Y2, n_skos, n_przod);	// 5,6
		mepo(os, HAMMER_X1, HAMMER_Y3, n_przod, n_spod);	// 7,8
		// punkty drewna
		for (int i = 0; i < HAMMER_WOOD_SIDES; i++) {
			double a = i * Math.PI * 2 / HAMMER_WOOD_SIDES;
			int y = (int) (32 - HAMMER_WOOD_RADIUS * Math.cos(a));
			int z = (int) (32 - HAMMER_WOOD_RADIUS * Math.sin(a));
			os.write(HAMMER_X2); os.write(y); os.write(z);
			os.write(32);
			os.write((int) (32 - ENV_R * Math.cos(a)));
			os.write((int) (32 - ENV_R * Math.sin(a)));
			os.write(HAMMER_X3); os.write(y); os.write(z);
			os.write((int) (32 + 31 * 0.7));
			os.write((int) (32 - ENV_R * 0.7 * Math.cos(a)));
			os.write((int) (32 - ENV_R * 0.7 * Math.sin(a)));
		}
		// tylna œcianka metalu
		os.write(new byte[] {2, 1, 3, 4, 0});
		// boczne drewno
		for (int i = 0; i < HAMMER_WOOD_SIDES; i++) {
			os.write(9 + 2 * i);
			os.write(10 + 2 * i);
			os.write(10 + 2 * ((i + 1) % HAMMER_WOOD_SIDES));
			os.write(9 + 2 * ((i + 1) % HAMMER_WOOD_SIDES));
			os.write(0);
		}
		// ty³ drewna
		for (int i = 0; i < HAMMER_WOOD_SIDES; i++)
			os.write(8 + 2 * (HAMMER_WOOD_SIDES - i));
		os.write(0);
		// boki metalu
		os.write(new byte[] {2, 4, 8, 6, 0});
		os.write(new byte[] {1, 5, 7, 3, 0});
		// przód metalu
		os.write(new byte[] {1, 2, 6, 5, 0});
		os.write(new byte[] {5, 6, 8, 7, 0});
		// spód metalu
		os.write(new byte[] {4, 3, 7, 8, 0});
		os.write(0);
		os.close();
	}

	public static void main(String[] args) {
		try {
			torus();
			triangle();
			//hammer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}