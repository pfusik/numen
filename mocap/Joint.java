import java.io.*;
import java.util.*;

/* Reads MotView 0.8 Motion Data Dump, calculates the scene & writes asx */
public class Joint {
	static final boolean SHADOW = true;
	static final boolean BIN = false;	// binary (for testing the compression)
	static final boolean COORDS = true;	// group by: true = coords, false = frames
	static final boolean DIFFS = true;	// true = diffs from previous frame, false = values

	String name;
	Joint parent;
	double[] x;
	double[] y;
	double[] z;

	Joint(String name, Joint parent, int frames) {
		this.name = name;
		this.parent = parent;
		x = new double[frames];
		y = new double[frames];
		z = new double[frames];
	}

	static final char[] hexDigits = new char[2];

	static String hex(int x) {
		hexDigits[0] = Character.forDigit((x >> 4) & 0xf, 16);
		hexDigits[1] = Character.forDigit(x & 0xf, 16);
		return new String(hexDigits);
	}

	static void println(String s) {
		if (!BIN)
			System.out.println(s);
	}

	static void out(int[] buf, int off, int len) {
		if (BIN) {
			while (--len >= 0)
				System.out.write(buf[off++]);
		}
		else {
			System.out.print("\tdta\t");
			while (--len > 0)
				System.out.print("$" + hex(buf[off++]) + ",");
			System.out.println("$" + hex(buf[off]));
		}
	}

	static void out(int x, int y) {
		if (BIN) {
			System.out.write(x);
			System.out.write(y);
		}
		else
			System.out.println("\tdta\t$" + hex(x) + ",$" + hex(y));
	}

	static Joint find(Collection where, String name) {
		Joint r = null;
		if (!name.equals("")) {
			Iterator i = where.iterator();
			do
				r = (Joint) i.next();
			while (!r.name.equals(name));
		}
		return r;
	}

	static void remove(Collection from, String what) {
		Joint me = find(from, what);
		Iterator i = from.iterator();
		do {
			Joint j = (Joint) i.next();
			if (j.parent == me)
				j.parent = me.parent;
		} while (i.hasNext());
		from.remove(me);
	}

	static void setParent(Collection from, String what, String newParent) {
		System.err.println(find(from, what).parent.name);
		find(from, what).parent = find(from, newParent);
		System.err.println(find(from, what).parent.name);
	}

	public static void main(String[] args) {
		Vector v = new Vector();
		try {
			LineNumberReader r = new LineNumberReader(new InputStreamReader(System.in));
			String line;
			while ((line = r.readLine()) != null)
				if (line.equals("**************** Dumping Joint *****************")) {
					String name = r.readLine().substring(13);	// Joint name = %s
					r.readLine();	// Joint type
					Joint parent = find(v, r.readLine().substring(15));	// Joint parent = %s
					while (!(line = r.readLine()).startsWith("ORIGINAL MARKER data "));
					int frames = Integer.parseInt(line.substring(21));
					Joint j = new Joint(name, parent, frames);
					for (int i = 0; i < frames; i++) {
						StringTokenizer st = new StringTokenizer(r.readLine());
						j.x[i] = Double.parseDouble(st.nextToken());
						j.y[i] = Double.parseDouble(st.nextToken());
						j.z[i] = Double.parseDouble(st.nextToken());
					}
					v.add(j);
				}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (true) {
			remove(v, "LeftAnkle");
			remove(v, "RightAnkle");
			remove(v, "LeftWrist");
			remove(v, "RightWrist");
			//setParent(v, "LeftCollar", "Neck");
			//setParent(v, "RightCollar", "Neck");
			//remove(v, "LeftCollar");
			remove(v, "Chest");
			remove(v, "Head");
		}
		Object[] t = v.toArray();

		if (SHADOW) {
			double floorY = 0.0;
			double lightX = 23;
			double lightY = 150;
			double lightZ = 140;
			Object[] ot = t;
			t = new Object[t.length * 2];
			for (int i = 0; i < ot.length; i++) {
				Joint oj = (Joint) ot[i];
				Joint parent = null;
				if (oj.parent != null) {
					String pname = "Shadow" + oj.parent.name;
					int j = 0;
					do
						parent = (Joint) t[j++];
					while (!parent.name.equals(pname));
				}
				Joint j = new Joint("Shadow" + oj.name, parent, oj.x.length);
				for (int k = 0; k < oj.x.length; k++) {
					double ox = oj.x[k];
					double oy = oj.y[k];
					double oz = oj.z[k];
					j.x[k] = (ox * (lightY - floorY) - lightX * (oy - floorY)) / (lightY - oy);
					j.y[k] = floorY;
					j.z[k] = (oz * (lightY - floorY) - lightZ * (oy - floorY)) / (lightY - oy);
				}
				t[i] = j;
				t[i + ot.length] = oj;
			}
		}

		println("Joint_NUMBER\tequ\t" + t.length);
		for (int i = 0; i < t.length; i++) {
			Joint j = (Joint) t[i];
			println("Joint_" + j.name + "_OFFSET\tequ\t" + i * 2);
		}
		println("Joint_draw");
		Joint a1 = null;
		Joint a2 = null;
		for (int i = 0; i < t.length; i++) {
			Joint j = (Joint) t[i];
			if (SHADOW && i == t.length / 2)
				println("\tjsr\tLine_primaryColor");
			if (j.parent != null) {
				if (j.parent == a1) {
					println("\tmwa\tJoint_x+Joint_" + j.name + "_OFFSET\tLine_x2");
					a2 = j;
				}
				else if (j.parent == a2) {
					println("\tmwa\tJoint_x+Joint_" + j.name + "_OFFSET\tLine_x1");
					a1 = j;
				}
				else {
					println("\tmwa\tJoint_x+Joint_" + j.parent.name + "_OFFSET\tLine_x1");
					a1 = j.parent;
					println("\tmwa\tJoint_x+Joint_" + j.name + "_OFFSET\tLine_x2");
					a2 = j;
				}
				println("\tjsr\tLine");
			}
		}
		println("\trts");
		int f = ((Joint) t[0]).x.length;

/*		// N first frames every S frame
		final int N = 338;
		final int S = 2;
		f = N / S;
		for (int i = 0; i < t.length; i++) {
			Joint j = (Joint) t[i];
			double[] ox = j.x;
			double[] oy = j.y;
			double[] oz = j.z;
			j.x = new double[f];
			j.y = new double[f];
			j.z = new double[f];
			for (int k = 0; k < f; k++) {
				j.x[k] = ox[S * k];
				j.y[k] = oy[S * k];
				j.z[k] = oz[S * k];
			}
		}
*/
		// project to screen
		int[][] sx = new int[t.length][f];
		int[][] sy = new int[t.length][f];
		int minX = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxY = Integer.MIN_VALUE;
		double cameraX = 23;
		double cameraY = 38;
		double cameraZ = 150;
/*		double cameraX = 0;
		double cameraY = 0;
		double cameraZ = 250;
*/		for (int k = 0; k < t.length; k++) {
			Joint j = (Joint) t[k];
			for (int i = 0; i < f; i++) {
				double x = j.x[i] - cameraX;
				double y = j.y[i] - cameraY;
				double z = j.z[i] - cameraZ;
				int cx = (int) (64 - 163 * x / z);
				int cy = (int) (59 + 163 * y / z);
				// no perspective
				// int cx = (int) ((x + 43) * 128 / 148);
				// int cy = (int) (95 - y * 128 / 148);
				if (cx < minX)
					minX = cx;
				if (cx > maxX)
					maxX = cx;
				if (cy < minY)
					minY = cy;
				if (cy > maxY)
					maxY = cy;
				sx[k][i] = cx;
				sy[k][i] = cy;
			}
		}
		System.err.println(minX + " <= x <= " + maxX + "; " + minY + " <= y <= " + maxY);
		if (minX < 0 || maxX > 127 || minY < 0 || maxY > 117) {
			System.err.println("OUT OF RANGE!!!");
			System.exit(3);
		}

		// zapisz
		if (DIFFS) {
			for (int k = 0; k < t.length; k++)
				for (int i = f; --i > 0; ) {
					sx[k][i] -= sx[k][i - 1];
					sy[k][i] -= sy[k][i - 1];
				}
		}

		println("Frame_NUMBER\tequ\t" + f);
		if (COORDS)
			println("\torg\t[*+$ff]&$ff00\t; align $100");
		println("Frame_data");
		if (COORDS) {
			for (int k = 0; k < t.length; k++) {
				println("; " + ((Joint) t[k]).name + ".x");
				for (int i = 0; i < f; i += 16)
					out(sx[k], i, Math.min(f - i, 16));
				println("; " + ((Joint) t[k]).name + ".y");
				for (int i = 0; i < f; i += 16)
					out(sy[k], i, Math.min(f - i, 16));
			}
		}
		else
			for (int i = 0; i < f; i++) {
				println("; frame " + i + "/" + f);
				for (int k = 0; k < t.length; k++)
					out(sx[k][i], sy[k][i]);
			}
		System.out.flush();
	}
}