import java.io.*;

public class ConvTrack {
	private static String[] names = {"x", "y", "z", "a", "h"};
	private static int[] fracts = {8, 8, 4, 4, 4};
	private static boolean[] isWord = {true, true, false, false, false, true};

	private static int parseHex(String s, int fractbits) throws NumberFormatException {
		if (fractbits % 4 != 0)
			throw new IllegalArgumentException();
		int i = 0;
		boolean negative = false;
		switch (s.charAt(0)) {
		case '-':
			negative = true;
		case '+':
			i++;
			break;
		}
		if (s.charAt(i) == '$')
			i++;
		int r = 0;
		while (i < s.length()) {
			int c = s.charAt(i++);
			if (c == '.') {
				while (fractbits > 0 && i < s.length()) {
					c = Character.digit(s.charAt(i++), 16);
					if (c < 0)
						throw new NumberFormatException();
					r <<= 4;
					r += c;
					fractbits -= 4;
				}
				break;
			}
			c = Character.digit((char) c, 16);
			if (c < 0)
				throw new NumberFormatException();
			r <<= 4;
			r += c;
		}
		r <<= fractbits;
		return negative ? -r : r;
	}

	private static int getNumber(StreamTokenizer st, int fractbits) throws IOException, NumberFormatException {
		while (st.ttype == '+')
			st.nextToken();
		switch (st.ttype) {
		case StreamTokenizer.TT_NUMBER:
			return (int) (st.nval * (1 << fractbits));
		case StreamTokenizer.TT_WORD:
			return parseHex(st.sval, fractbits);
		case '-':
			if (st.nextToken() == st.TT_WORD)
				return -parseHex(st.sval, fractbits);
		default:
			throw new NumberFormatException();
		}
	}

	private static int nextNumber(StreamTokenizer st, int fractbits) throws IOException, NumberFormatException {
		while (st.nextToken() == '+');
		return getNumber(st, fractbits);
	}

	private static String toHexString(int x, int digits) {
		String s = "000" + Integer.toHexString(x);
		int l = s.length();
		return s.substring(l - digits, l);
	}

	private static String hexByte(int x) {
		return "$" + toHexString(x, 2);
	}

	private static String hexWord(int x) {
		return "a($" + toHexString(x, 4) + ")";
	}

	public static void main(String[] args) {
		StreamTokenizer st = new StreamTokenizer(new InputStreamReader(System.in));
		st.commentChar('#');
		st.slashSlashComments(true);
		st.slashStarComments(true);
		st.lowerCaseMode(true);
		st.ordinaryChar('=');
		st.ordinaryChar('@');
		st.wordChars('$', '$');
		st.wordChars('-', '-');
		st.wordChars('_', '_');
		try {
			if (st.nextToken() != st.TT_WORD || !st.sval.equals("start"))
				throw new Exception("Expected 'start'");
			int[] current = new int[5];
			for (int i = 0; i < 5; i++) {
				if (st.nextToken() != st.TT_WORD || !st.sval.equals(names[i]))
					throw new Exception("Expected '" + names[i] + "'");
				if (st.nextToken() != '=')
					throw new Exception("Expected '='");
				current[i] = nextNumber(st, 0);
			}
			System.out.println("* Generated automatically by ConvTrack");
			System.out.print("; Starting from");
			for (int i = 0; i < 5; i++)
				System.out.print(" " + names[i] + "=$" + toHexString(current[i], isWord[i] ? 4 : 2));
			System.out.println();
			System.out.println("\torg\tWorld_beginHorizon");
			System.out.println("\tdta\t" + hexByte(current[4]));
			System.out.println("\torg\tWorld_track");
			int totalFrames = 0;
			int[] params = new int[5];
			String runCode = null;
			st.nextToken();
			while (st.ttype != st.TT_EOF) {
				if (st.ttype != st.TT_WORD)
					throw new Exception("Expected 'after' or 'wait'");
				int flags;
				if (st.sval.equals("after"))
					flags = 0;
				else if (st.sval.equals("wait"))
					flags = 0x40;
				else
					throw new Exception("Expected 'after' or 'wait'");
				if (st.nextToken() != st.TT_NUMBER)
					throw new Exception("Expected time in seconds");
				if (st.nval > 5.10)
					throw new Exception("Max time is 5.10");
				if ((st.nval + 0.00005) % 0.02 >= 0.0001)
					throw new Exception("Time must be multiple of 0.02");
				int frames = (int) (st.nval * 50);
				totalFrames += frames;
				param:
				while (st.nextToken() != st.TT_EOF) {
					if (st.ttype != st.TT_WORD)
						throw new Exception("Expected a word");
					if (st.sval.equals("go")) {
						if ((flags & 0x40) != 0)
							throw new Exception("Unexpected 'go' in 'wait'");
						if ((flags & 3) != 0)
							throw new Exception("Already got x or y");
						flags |= 3;
						int speed;
						if (st.nextToken() == '@')
							speed = nextNumber(st, 8);
						else
							speed = (getNumber(st, 0) << 8) / frames;
						double angle = 2 * Math.PI / 256 * current[3];
						params[0] = (int) (speed * Math.cos(angle));
						params[1] = (int) (speed * Math.sin(angle));
					}
					else if (st.sval.equals("code")) {
						if ((flags & 0x20) != 0)
							throw new Exception("Already got code");
						flags |= 0x20;
						if (st.nextToken() != st.TT_WORD)
							throw new Exception();
						runCode = st.sval;
					}
					else {
						for (int i = 0; i < names.length; i++)
							if (st.sval.equals(names[i])) {
								if ((flags & 0x40) != 0)
									throw new Exception("Unexpected '" + st.sval + "' in 'wait'");
								if ((flags & (1 << i)) != 0)
									throw new Exception("Already got " + st.sval);
								flags |= 1 << i;
								int d;
								switch (st.nextToken()) {
								case '=':
									d = nextNumber(st, 0) - current[i];
									// if angle, turn left or right, whichever is closer
									if (i == 3 && Math.abs(d) > 0x80)
										d += d < 0 ? 0x100 : -0x100;
									break;
								case '@':
									params[i] = nextNumber(st, fracts[i]);
									continue param;
								default:
									d = getNumber(st, 0);
									break;
								}
								int p = (d << fracts[i]) / frames;
								if (p * frames >> fracts[i] == d)
									params[i] = p;
								else if ((p - 1) * frames >> fracts[i] == d)
									params[i] = p - 1;
								else if ((p + 1) * frames >> fracts[i] == d)
									params[i] = p + 1;
								else {
									System.err.println("Warning: " + names[i] + " at " + st);
									params[i] = p;
								}
								continue param;
							}
						break;
					}
				}
				System.out.print("\tdta\t" + frames + "," + hexByte(flags));
				for (int i = 0; i < 5; i++)
					if ((flags & (1 << i)) != 0) {
						System.out.print("," + (isWord[i] ?
								hexWord(params[i] ^ 0x8000) : hexByte(params[i] ^ 0x80)));
						current[i] += (params[i] * frames) >> fracts[i];
					}
				if ((flags & 0x20) != 0)
					System.out.print(",h(" + runCode + "-1),l(" + runCode + "-1)");
				current[3] &= 0xff;	// angle
				if ((flags & 0x1f) == 0)
					System.out.println((flags & 0x40) != 0 ? "  ; wait but animate" : "  ; wait");
				else {
					System.out.print("  ; until");
					for (int i = 0; i < 5; i++)
						if ((flags & (1 << i)) != 0)
							System.out.print(" " + names[i] + "=" + toHexString(current[i], isWord[i] ? 4 : 2));
					System.out.println();
				}
			}
			System.out.println("\tdta\t0");
			System.out.println("; total time: " + totalFrames * 0.02 + " sec");
		} catch (Exception e) {
			System.err.println("At " + st + ":");
			e.printStackTrace();
			System.exit(3);
		}
	}
}