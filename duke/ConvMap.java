import java.io.*;

// Wejściowy strumień danych do odczytywania danych o kolejności bajtów Little Endian
// (DataInputStream zakłada Big Endian)
class LittleEndianDataInputStream extends FilterInputStream {
	// konstruktor
	public LittleEndianDataInputStream(InputStream is) {
		super(is);
	}

	// pominięcie n bajtów
	// zapewnia, że rzeczywiście n bajtów zostanie pominiętych
	public long skip(long n) throws IOException {
		long i = n - super.skip(n);
		while (--i >= 0)
			if (read() == -1)
				throw new EOFException();
		return n;
	}

	// odczytanie liczby 32-bitowej
	public int readInt() throws IOException {
		byte[] b = new byte[4];
		if (read(b) != 4)
			throw new EOFException();
		return ((b[3] & 0xff) << 24) + ((b[2] & 0xff) << 16) + ((b[1] & 0xff) << 8) + (b[0] & 0xff);
	}

	// odczytanie liczby 16-bitowej
	public short readShort() throws IOException {
		int l;
		int h;
		if ((l = read()) == -1 || (h = read()) == -1)
			throw new EOFException();
		return (short) ((h << 8) + l);
	}
}

public class ConvMap {
	static final int Gfx_SOLID = 1;
	static final int Gfx_SPRITE = 2;
	static final int Gfx_SKY = 3;
	static final int Gfx_WEAPON = 4;
	static final String[] Gfx_TYPE = {"none", "solid", "sprite", "sky", "weapon"};
	static int[] Gfx_type;
	static int[] Gfx_data;
	static int[] Gfx_width;
	static int[] Gfx_height;

	static void readGfxScript(String name) throws Exception {
		Gfx_type = new int[256];
		Gfx_data = new int[256];
		Gfx_width = new int[256];
		Gfx_height = new int[256];
		int spriteNo = 0;
		InputStreamReader isr = new FileReader(name);
		StreamTokenizer st = new StreamTokenizer(isr);
		st.commentChar('#');
		st.slashSlashComments(true);
		st.slashStarComments(true);
		System.out.println("; Gfx scipt ----------------");
		while (st.nextToken() != st.TT_EOF) {
			if (st.ttype != st.TT_NUMBER)
				throw new Exception(name + " (" + st.lineno() + "): Expected tile number");
			int tile = (int) st.nval;
			if (st.nextToken() != st.TT_WORD)
				throw new Exception(name + " (" + st.lineno() + "): Expected tile type");
			int type;
			if (st.sval.equalsIgnoreCase("solid")) {
				type = Gfx_SOLID;
				if (st.nextToken() != st.TT_NUMBER)
					throw new Exception(name + " (" + st.lineno() + "): Expected solid no");
				Gfx_data[tile] = (int) st.nval;
			}
			else if (st.sval.equalsIgnoreCase("sprite")) {
				type = Gfx_SPRITE;
				if (st.nextToken() != st.TT_NUMBER)
					throw new Exception(name + " (" + st.lineno() + "): Expected sprite width");
				Gfx_width[tile] = (int) st.nval;
				if (st.nextToken() != st.TT_NUMBER)
					throw new Exception(name + " (" + st.lineno() + "): Expected sprite height");
				Gfx_height[tile] = (int) st.nval;
				Gfx_data[tile] = spriteNo++;
			}
			else if (st.sval.equalsIgnoreCase("sky"))
				type = Gfx_SKY;
			else if (st.sval.equalsIgnoreCase("weapon"))
				type = Gfx_WEAPON;
			else
				throw new Exception(name + " (" + st.lineno() + "): Wrong tile type: " + st.sval);
			Gfx_type[tile] = type;
			System.out.println("; tile " + tile + ": " + Gfx_TYPE[type] /*+ " #" + Gfx_data[tile]*/);
		}
		isr.close();
	}

	static final int World_SECTOR_LIFT_LOW = 1;
	static final int World_SECTOR_PARALLAXING = 8;

	static final int World_EFFECT_WATER = 1;
	static final int World_EFFECT_UNDERWATER = 2;
	static final int World_EFFECT_DOOR_DOOM_UP = 3;
	static final int World_EFFECT_DOOR_DOOM_DOWN = 4;
	static final int World_EFFECT_DOOR_DOOM_SPLIT = 5;
	static final int World_EFFECT_DOOR_SWING_CCW = 6;
	static final int World_EFFECT_DOOR_SWING_CW = 7;
	static final int World_EFFECT_LIFT_FLOOR_ONLY = 8;
	static final int World_EFFECT_LIFT_CEIL_FLOOR = 9;

	static final int World_WALL_SWAP_BOTTOM = 1;
	static final int World_WALL_HORIZON = 2;

	static final int World_SPRITE_CENTER_VERT = 2;

	static int convertX(int x, String name) {
		if (x < 0 || x > 65535) {
			System.err.println(name + ": wrong X coordinate: " + x);
			System.exit(3);
		}
		return x >> 1;
	}

	static int convertY(int x, String name) {
		if (x < 0 || x > 65535) {
			System.err.println(name + ": wrong Y coordinate: " + x);
			System.exit(3);
		}
		return x >> 1;
	}

	static int convertZ(int x, String name) {
		if (x < -32768) {
			System.err.println(name + ": Z too high: " + x);
			System.exit(3);
		}
		else if (x >= 32768) {
			System.err.println(name + ": Z too low: " + x);
			System.exit(3);
		}
		return (x - -32768) >> 8;
	}

	static void printNumber(String name, int x) {
		System.out.println("\torg\t" + name);
		System.out.print("\tdta\t$");
		if (x < 16)
			System.out.print("0");
		System.out.println(Integer.toHexString(x));
	}

	static void printWord(String name, int x) {
		System.out.println("\torg\t" + name);
		String s = "000" + Integer.toHexString(x);
		System.out.println("\tdta\ta($" + s.substring(s.length() - 4) + ")");
	}

	static void printTable(String name, int[] t, int n) {
		System.out.println("\torg\t" + name);
		for (int i = 0; i < n; i++) {
			if (i % 16 == 0)
				System.out.print("\tdta\t");
			System.out.print(t[i] < 16 ? "$0" : "$");
			System.out.print(Integer.toHexString(t[i]));
			if (i % 16 == 15 || i == n - 1)
				System.out.println();
			else
				System.out.print(",");
		}
	}

	static void printTable(String name, int[] t) {
		printTable(name, t, t.length);
	}

	static void printTableWord(String name, int[] t, int n) {
		System.out.println("\torg\t" + name + "_lo");
		for (int i = 0; i < n; i++) {
			if (i % 16 == 0)
				System.out.print("\tdta\tl(");
			System.out.print(t[i] < 16 ? "$000" : t[i] < 256 ? "$00" : t[i] < 4096 ? "$0" : "$");
			System.out.print(Integer.toHexString(t[i]));
			if (i % 16 == 15 || i == n - 1)
				System.out.println(")");
			else
				System.out.print(",");
		}
		System.out.println("\torg\t" + name + "_hi");
		for (int i = 0; i < n; i++) {
			if (i % 16 == 0)
				System.out.print("\tdta\th(");
			System.out.print(t[i] < 16 ? "$000" : t[i] < 256 ? "$00" : t[i] < 4096 ? "$0" : "$");
			System.out.print(Integer.toHexString(t[i]));
			if (i % 16 == 15 || i == n - 1)
				System.out.println(")");
			else
				System.out.print(",");
		}
	}

	static void printTableWord(String name, int[] t) {
		printTableWord(name, t, t.length);
	}

	public static void main(String[] args) {
		try {
			int[] World_colors = new int[10];
			switch (args.length) {
			case 2+1:	// GR.9
				int x = Integer.parseInt(args[2], 16);
				if (x < 0 || x > 15)
					throw new IllegalArgumentException("GR.9 color must be 0..F");
				World_colors[8] = x << 4;
				World_colors[9] = 0x41;
				break;
			case 2+9:	// GR.10
				for (int i = 0; i < 9; i++)
					World_colors[i] = Integer.parseInt(args[2 + i], 16);
				World_colors[9] = 0x81;
				break;
			case 2+1+9:	// GR.9 then GR.10; last GR.10 color is ignored! must be set later!
				int y = Integer.parseInt(args[2], 16);
				if (y < 0 || y > 15)
					throw new IllegalArgumentException("GR.9 color must be 0..F");
				World_colors[8] = y << 4;
				for (int i = 0; i < 8; i++)
					World_colors[i] = Integer.parseInt(args[3 + i], 16);
				World_colors[9] = 0x41;
				break;
			default:
				throw new Exception("Usage: ConvMap level.map gfx.txt colors >level.asx");
			}
			System.out.println("* Generated automatically by ConvMap");
			System.out.println("* from " + args[0] + " and " + args[1]);
			System.out.println();
			readGfxScript(args[1]);
			System.out.println();
			//System.out.println("\ticl\t'../world.equ'");
			System.out.println("; Map file -----------------");

			LittleEndianDataInputStream dis = new LittleEndianDataInputStream(new FileInputStream(args[0]));
			dis.readInt();
			int Player_x = convertX(dis.readInt(), "Player");
			int Player_y = convertY(dis.readInt(), "Player");
			int Player_z = convertZ(dis.readInt(), "Player");
			int Player_angle = dis.readShort() / (2048 / 256);
			int Player_sector = dis.readShort();
			/*
					-------------------------------------------------------------
					| @@@@@@@ @@@@@@@ @@@@@@@ @@@@@@@@ @@@@@@@ @@@@@@@  @@@@@@@ |
					| @@      @@      @@         @@    @@   @@ @@   @@@ @@      |
					| @@@@@@@ @@@@@   @@         @@    @@   @@ @@@@@@@  @@@@@@@ |
					|      @@ @@      @@         @@    @@   @@ @@   @@@      @@ |
					| @@@@@@@ @@@@@@@ @@@@@@@    @@    @@@@@@@ @@    @@ @@@@@@@ |
					-------------------------------------------------------------
			*/
			int World_sectors = dis.readShort();
			if (World_sectors > 64) {
				System.err.println("Too many sectors: " + World_sectors);
				System.exit(3);
			}
			int[] World_sectorFlags = new int[World_sectors];
			int[] World_sectorEffect = new int[World_sectors];
			int[] World_sectorParameter = new int[World_sectors];
			int[] World_sectorWall = new int[World_sectors + 1];
			int[] World_ceilingZ = new int[World_sectors];
			int[] World_floorZ = new int[World_sectors];
			int[] World_ceilingPicture = new int[World_sectors];
			int[] World_floorPicture = new int[World_sectors];
			int[] Temp_effectorX = new int[World_sectors];
			int[] Temp_effectorY = new int[World_sectors];
			int[] Temp_sectorPair = new int[World_sectors];
			for (int s = 0; s < World_sectors; s++)
				Temp_sectorPair[s] = -1;
			for (int s = 0; s < World_sectors; s++) {
				int x;
				World_sectorWall[s] = dis.readShort();	// wallptr
				dis.readShort();	// wallnum
				World_ceilingZ[s] = convertZ(dis.readInt(), "Sector " + s + " ceiling");
				World_floorZ[s] = convertZ(dis.readInt(), "Sector " + s + " floor");
				int cstat = dis.readShort();	// ceilingstat
				if ((cstat & 1) != 0)
					World_sectorFlags[s] |= World_SECTOR_PARALLAXING;
				dis.readShort();	// floorstat
				x = dis.readShort();	// ceilingpicnum
				if ((cstat & 1) != 0) {
					x = 8;
				}
				else if (Gfx_type[x] != Gfx_SOLID) {
					System.err.println("Sector " + s + ": wrong ceiling picture: " + x);
					System.exit(3);
				}
				else
					x = Gfx_data[x];
				World_ceilingPicture[s] = x;
				dis.readShort();	// ceilingheinum
				dis.readShort();	// ceilingshade, ceilingpal
				dis.readShort();	// ceilingxpanning, ceilingypanning
				x = dis.readShort();	// floorpicnum
				if (Gfx_type[x] != Gfx_SOLID) {
					System.err.println("Sector " + s + ": wrong floor picture " + x);
					System.exit(3);
				}
				else
					x = Gfx_data[x];
				World_floorPicture[s] = x;
				dis.readShort();	// floorheinum
				dis.readShort();	// floorshade, floorpal
				dis.readShort();	// floorxpanning, floorypanning
				dis.readShort();	// visibility, filler
				int lotag = dis.readShort();	// lotag
				int hitag = dis.readShort();	// hitag
				dis.readShort();	// extra
				switch (lotag) {
				case 0:
					break;
				case 1:	// over water
					World_sectorEffect[s] = World_EFFECT_WATER;
					break;
				case 2:	// under water
					World_sectorEffect[s] = World_EFFECT_UNDERWATER;
					World_sectorParameter[s] = -1;
					break;
				case 17:	// floor only lift, lowered
					World_sectorFlags[s] |= World_SECTOR_LIFT_LOW;
				case 16:	// floor only lift, raised
					World_sectorEffect[s] = World_EFFECT_LIFT_FLOOR_ONLY;
					break;
				case 19:	// ceiling & floor lift, lowered
					World_sectorFlags[s] |= World_SECTOR_LIFT_LOW;
				case 18:	// ceiling & floor lift, raised
					World_sectorEffect[s] = World_EFFECT_LIFT_CEIL_FLOOR;
					break;
				case 20:	// doom doors
					World_sectorEffect[s] = World_EFFECT_DOOR_DOOM_UP;
					break;
				case 21:	// reversed doom doors
					World_sectorEffect[s] = World_EFFECT_DOOR_DOOM_DOWN;
					break;
				case 22:	// splitting doom doors
					World_sectorEffect[s] = World_EFFECT_DOOR_DOOM_SPLIT;
					break;
				case 23:	// swinging doors
					World_sectorEffect[s] = World_EFFECT_DOOR_SWING_CCW;
					break;
				default:
					System.err.println("Sector " + s + ": unknown lotag " + lotag);
					System.exit(3);
					break;
				}
			}

			/*			 -----------------------------------------------
						 | @@      @@ @@@@@@@@ @@      @@      @@@@@@@ |
						 | @@      @@ @@    @@ @@      @@      @@      |
						 | @@  @@  @@ @@@@@@@@ @@      @@      @@@@@@@ |
						 | @@ @@@@ @@ @@    @@ @@      @@           @@ |
						 |  @@@ @@@@  @@    @@ @@@@@@@ @@@@@@@ @@@@@@@ |
						 ----------------------------------------------|
			*/
			int World_walls = dis.readShort();
			if (World_walls > 255) {
				System.err.println("Too many walls: " + World_walls);
				System.exit(3);
			}
			World_sectorWall[World_sectors] = World_walls;
			int[] World_wallFlags = new int[World_walls];
			int[] World_wallX = new int[World_walls];
			int[] World_wallY = new int[World_walls];
			int[] World_wallNext = new int[World_walls];
			int[] World_wallReverse = new int[World_walls];
			int[] World_wallSector = new int[World_walls];
			int[] World_wallPicture = new int[World_walls];
			for (int w = 0; w < World_walls; w++) {
				int x;
				World_wallX[w] = convertX(dis.readInt(), "Wall " + w);
				World_wallY[w] = convertY(dis.readInt(), "Wall " + w);
				World_wallNext[w] = dis.readShort();	// point2
				x = dis.readShort();	// nextwall
				if (x < 0) x = 255;
				World_wallReverse[w] = x;
				x = dis.readShort();	// nextsector
				if (x < 0) x = 255;
				World_wallSector[w] = x;
				int cstat = dis.readShort();	// cstat
				if ((cstat & 2) != 0)
					World_wallFlags[w] |= World_WALL_SWAP_BOTTOM;
				x = dis.readShort();	// picnum
				switch (Gfx_type[x]) {
				case Gfx_SOLID:
					x = Gfx_data[x];
					break;
				case Gfx_SKY:
					if (World_wallSector[w] == 255)
						World_wallFlags[w] |= World_WALL_HORIZON;
					x = 0;
					break;
				default:
					if (World_wallSector[w] == 255) {
						System.err.println("Wall " + w + ": wrong picture: " + x);
						System.exit(3);
					}
					else
						x = 0;
					break;
				}
				World_wallPicture[w] = x;
				dis.readShort();	// overpicnum
				dis.readShort();	// shade, pal
				dis.readShort();	// xrepeat, yrepeat
				dis.readShort();	// xpanning, ypanning
				dis.readShort();	// lotag
				dis.readShort();	// hitag
				dis.readShort();	// extra
			}
			/*
					-------------------------------------------------------------
					| @@@@@@@ @@@@@@@ @@@@@@@   @@@@@@ @@@@@@@@ @@@@@@@ @@@@@@@ |
					| @@      @@   @@ @@   @@@    @@      @@    @@      @@      |
					| @@@@@@@ @@@@@@@ @@@@@@@     @@      @@    @@@@@   @@@@@@@ |
					|      @@ @@      @@    @@    @@      @@    @@           @@ |
					| @@@@@@@ @@      @@    @@  @@@@@@    @@    @@@@@@@ @@@@@@@ |
					-------------------------------------------------------------
			*/
			int World_sprites = dis.readShort();
			int[] World_spriteFlags = new int[World_sprites];
			int[] World_spriteX = new int[World_sprites];
			int[] World_spriteY = new int[World_sprites];
			int[] World_spriteZ = new int[World_sprites];
			int[] World_spriteSector = new int[World_sprites];
			int[] World_spritePicture = new int[World_sprites];
			int[] World_spriteWidth = new int[World_sprites];
			int[] World_spriteHeight = new int[World_sprites];
			int s = 0;
			for (int i = 0; i < World_sprites; i++) {
				boolean addIt = false;
				World_spriteX[s] = convertX(dis.readInt(), "Sprite " + i);
				World_spriteY[s] = convertY(dis.readInt(), "Sprite " + i);
				World_spriteZ[s] = convertZ(dis.readInt(), "Sprite " + i);
				int cstat = dis.readShort();	// cstat
				World_spriteFlags[s] = 0;
				if ((cstat & 0x80) != 0)
					World_spriteFlags[s] |= World_SPRITE_CENTER_VERT;
				int picnum = dis.readShort();	// picnum
				dis.readShort();	// shade, pal
				dis.readShort();	// clipdist, filler
				dis.readShort();	// xrepeat, yrepeat
				dis.readShort();	// xoffset, yoffset
				World_spriteSector[s] = dis.readShort();	// sectnum
				dis.readShort();	// statnum
				int ang = dis.readShort();	// ang
				dis.readShort();	// owner
				dis.readShort();	// xvel
				dis.readShort();	// yvel
				dis.readShort();	// zvel
				int lotag = dis.readShort();	// lotag
				int hitag = dis.readShort();	// hitag
				dis.readShort();	// extra
				switch (picnum) {
				case 1:	// sector effector
					switch (lotag) {
					case 7:	// submergible water
						if (Temp_sectorPair[hitag] == -1) {
							Temp_effectorX[hitag] = World_spriteX[s];
							Temp_effectorY[hitag] = World_spriteY[s];
							Temp_sectorPair[hitag] = World_spriteSector[s];
						}
						else if (Temp_sectorPair[hitag] == -2) {
							System.err.println("Too many sector effectors of lotag 7 with same hitag " + hitag);
							System.exit(3);
						}
						else {
							int s1 = Temp_sectorPair[hitag];
							int s2 = World_spriteSector[s];
							World_sectorParameter[s1] = s2;
							World_sectorParameter[s2] = s1;
							int w1 = World_sectorWall[s1];
							int w2 = World_sectorWall[s2];
							if (Temp_effectorX[hitag] - World_wallX[w1] != World_spriteX[s] - World_wallX[w2]
							 || Temp_effectorY[hitag] - World_wallY[w1] != World_spriteY[s] - World_wallY[w2]) {
								System.err.println("Water sectors " + s1 + " and " + s2 + " don't match");
								System.err.println("Sector " + s1 + ": wx=" + World_wallX[w1] + " wy=" + World_wallY[w1] +
									" sx=" + Temp_effectorX[hitag] + " sy=" + Temp_effectorY[hitag]);
								System.err.println("Sector " + s2 + ": wx=" + World_wallX[w2] + " wy=" + World_wallY[w2] +
									" sx=" + World_spriteX[s] + " sy=" + World_spriteY[s]);
								System.exit(3);
							}
							Temp_sectorPair[hitag] = -2;
						}
						break;
					case 10:	// autoclose door
						{
							int s1 = World_spriteSector[s];
							switch (World_sectorEffect[s1]) {
							case World_EFFECT_DOOR_DOOM_UP:
							case World_EFFECT_DOOR_DOOM_DOWN:
							case World_EFFECT_DOOR_DOOM_SPLIT:
							case World_EFFECT_DOOR_SWING_CCW:
							case World_EFFECT_DOOR_SWING_CW:
								// 32 is 1 second in Duke
								// 1 is 8 frames (8/50 of a second) in 8-bit
								World_sectorParameter[s1] = hitag * 50 / (32 * 8);
								break;
							default:
								System.err.println("Sector effector 10 (autoclose) in sector " + s1 +
								", which is not a door sector");
								System.exit(3);
								break;
							}
						}
						break;
					case 11:	// swinging door
						{
							int s1 = World_spriteSector[s];
							if (World_sectorEffect[s1] != World_EFFECT_DOOR_SWING_CCW) {
								System.err.println("Sector effector 11 in sector " + s1 + " (not a swinging door sector!)");
								System.exit(3);
							}
							else {
								if ((ang & 1024) == 0)
									World_sectorEffect[s1] = World_EFFECT_DOOR_SWING_CW;
							}
						}
						break;
					default:
						System.err.println("Unknown sector effector lotag " + lotag);
						System.exit(3);
						break;
					}
					break;
				default:
					if (Gfx_type[picnum] != Gfx_SPRITE) {
						System.err.println("Sprite " + i + ": unknown picture: " + picnum);
						System.exit(3);
					}
					World_spritePicture[s] = Gfx_data[picnum];
					World_spriteWidth[s] = Gfx_width[picnum];
					World_spriteHeight[s] = Gfx_height[picnum];
					s++;
					break;
				}
			}
			World_sprites = s;

			for (s = 0; s < World_sectors; s++) {
				if (World_sectorEffect[s] == World_EFFECT_UNDERWATER
				 && World_sectorParameter[s] < 0)
					World_sectorEffect[s] = World_sectorParameter[s] = 0;
			}

			System.out.println("* Sectors");
			printTable("World_sectorFlags", World_sectorFlags);
			printTable("World_sectorEffect", World_sectorEffect);
			printTable("World_sectorParameter", World_sectorParameter);
			printTable("World_sectorWall", World_sectorWall);
			printTable("World_ceilingZ", World_ceilingZ);
			printTable("World_floorZ", World_floorZ);
			printTable("World_ceilingPicture", World_ceilingPicture);
			printTable("World_floorPicture", World_floorPicture);
			System.out.println("* Walls");
			printTable("World_wallFlags", World_wallFlags);
			printTableWord("World_wallX", World_wallX);
			printTableWord("World_wallY", World_wallY);
			printTable("World_wallNext", World_wallNext);
			printTable("World_wallReverse", World_wallReverse);
			printTable("World_wallSector", World_wallSector);
			printTable("World_wallPicture", World_wallPicture);
			System.out.println("* Sprites");
			printTable("World_spriteFlags", World_spriteFlags, World_sprites);
			printTableWord("World_spriteX", World_spriteX, World_sprites);
			printTableWord("World_spriteY", World_spriteY, World_sprites);
			printTable("World_spriteZ", World_spriteZ, World_sprites);
			printTable("World_spriteSector", World_spriteSector, World_sprites);
			printTable("World_spritePicture", World_spritePicture, World_sprites);
			printTable("World_spriteWidth", World_spriteWidth, World_sprites);
			printTable("World_spriteHeight", World_spriteHeight, World_sprites);
			System.out.println("* Total");
			printNumber("World_sectors", World_sectors);
			printNumber("World_walls", World_walls);
			printNumber("World_sprites", World_sprites);
			printWord("World_beginX", Player_x);
			printWord("World_beginY", Player_y);
			printNumber("World_beginZ", Player_z);
			printNumber("World_beginAngle", Player_angle);
			printNumber("World_beginSector", Player_sector);
			printTable("World_colors", World_colors);
			System.err.println("Sectors: " + World_sectors);
			System.err.println("Walls:   " + World_walls);
			System.err.println("Sprites: " + World_sprites);
			System.out.println("; Bytes: " + (World_sectors * 8 + 1 + World_walls * 9 + World_sprites * 10));
		} catch (Exception e) {
				System.err.println(e.toString());
				System.exit(3);
		}
	}
}
