import java.awt.Color;
import java.io.*;

/**
 * The class representing an image with 8-bit palette.
 * Available are methods for loading and storing the image
 * in PCX format.
 * @author Piotr Fusik
 */

public class PCXImage {
	/**
	 * width of image
	 */
	public int width;
	/**
	 * height of image
	 */
	public int height;
	/**
	 * array of pixels.
	 * <code>data[y][x]</code> is the color of pixel at (x,y)
	 */
	public byte[][] data;
	/**
	 * color table: 256 entries of R, G and B. <code>palette[i][0]</code>
	 * is the Red component, <code>palette[i][1]</code> is the Green component
	 * and <code>palette[i][2]</code> is the Blue component.
	 * Each component is treated as unsigned value 0-255.
	 */
	public byte[][] palette;

	/**
	 * Gets a color from palette.
	 * @param index index of the color in palette (in the range 0 to 255)
	 * @return the color from palette
	 */
	public Color getColor(int index) {
		byte[] p = palette[index];
		return new Color(p[0] & 0xff, p[1] & 0xff, p[2] & 0xff);
	}

	/**
	 * Sets a color in palette.
	 * @param index index of the color in palette (in the range 0 to 255)
	 * @param red red component (0-255)
	 * @param green green component (0-255)
	 * @param blue blue component (0-255)
	 */
	public void setColor(int index, int red, int green, int blue) {
		byte[] p = palette[index];
		p[0] = (byte) red;
		p[1] = (byte) green;
		p[2] = (byte) blue;
	}

	/**
	 * Sets a color in palette.
	 * @param index index of the color in palette (in the range 0 to 255)
	 * @param color the new color
	 */
	public void setColor(int index, Color color) {
		setColor(index, color.getRed(), color.getGreen(), color.getBlue());
	}

	/**
	 * Constructs new blank image of given size.
	 * @param width the width of the image
	 * @param height the height of the image
	 */
	public PCXImage(int width, int height) {
		this.width = width;
		this.height = height;
		data = new byte[height][width];
		palette = new byte[256][3];
	}

	/**
	 * Reads a byte from stream <code>is</code>.
	 * @param is the stream to read a byte from
	 * @return the byte read (0-255)
	 * @exception java.io.EOFException reached end of file
	 * @exception java.io.IOException I/O error
	 */
	private static int readByte(InputStream is) throws IOException {
		int b = is.read();
		if (b < 0)
			throw new EOFException("Unexpected EOF in PCX file");
		return b;
	}

	/**
	 * Reads an unsigned short (little endian) from stream <code>is</code>.
	 * @param is the stream to read a short from
	 * @return the value read (0-65535)
	 * @exception java.io.EOFException reached end of file
	 * @exception java.io.IOException I/O error
	 */
	private static int readShort(InputStream is) throws IOException {
		int l = readByte(is);
		return l | (readByte(is) << 8);
	}

	/**
	 * Skips n bytes from stream <code>is</code>.
	 * @param is the stream to skip bytes from
	 * @param n number of bytes to skip
	 * @exception java.io.IOException I/O error
	 */
	static void skip(InputStream is, int n) throws IOException {
		while (true) {
			n -= is.skip(n);
			if (n <= 0)
				return;
			readByte(is);
		}
	}

	/**
	 * Constructs new image from PCX format data read from <code>is</code>.
	 * @param is the stream containing PCX format data
	 * @exception java.io.IOException I/O error
	 * @exception java.lang.Exception invalid format (must be PCX with 8-bit palette)
	 */
	public PCXImage(InputStream is) throws Exception {
		if (is.read() != 10	/* ZSoft .PCX */
		 || is.read() != 5	/* version 3.0 */
		 || is.read() != 1	/* RLE encoding */
		 || is.read() != 8	/* 8 bits per pixel */
		)
			throw new Exception("Invalid PCX header");
		width = readShort(is);
		height = readShort(is);
		width = 1 + readShort(is) - width;
		height = 1 + readShort(is) - height;
		if (width <= 0 || height <= 0)
			throw new Exception("Invalid size of PCX image");
		skip(is, 4			/* DPI */
				+ 48		/* EGA palette */
				+ 1			/* reserved */
		);
		if (is.read() != 1)	/* 1 bitplane */
			throw new Exception("Must be one bitplane in PCX file");
		int bytesPerLine = readShort(is);
		if (bytesPerLine != width && bytesPerLine != width + 1)
			throw new Exception("Invalid bytes per line");
		skip(is, 2			/* palette info */
				+ 58		/* filler */
		);
		data = new byte[height][width];
		int count = 1;
		int value = 0;
		for (int y = 0; y < height; y++)
			for (int x = 0; x < bytesPerLine; x++) {
				if (--count == 0) {
					value = readByte(is);
					if (value < 0xc0)
						count = 1;
					else {
						count = value - 0xc0;
						value = readByte(is);
					}
				}
				if (x < width)
					data[y][x] = (byte) value;
			}
		if (is.read() != 0xc)
			throw new Exception("Expected 0x0c before palette");
		palette = new byte[256][3];
		for (int i = 0; i < 256; i++) {
			palette[i][0] = (byte) readByte(is);
			palette[i][1] = (byte) readByte(is);
			palette[i][2] = (byte) readByte(is);
		}
	}

	/**
	 * Writes an unsigned short value (little endian) to <code>os</code>.
	 * @param os the stream to write to
	 * @param x the value to be written
	 * @exception java.io.IOException I/O error
	 */
	private static void writeShort(OutputStream os, int x) throws IOException {
		os.write(x & 0xff);
		os.write((x >> 8) & 0xff);
	}

	/**
	 * Writes the image in PCX format to <code>os</code>.
	 * @param os the stream to write to
	 * @exception java.io.IOException I/O error
	 */
	public void write(OutputStream os) throws IOException {
		os.write(10);	/* ZSoft .PCX */
		os.write(5);	/* version 3.0 */
		os.write(1);	/* RLE encoding */
		os.write(8);	/* 8 bits per pixel */
		writeShort(os, 0);
		writeShort(os, 0);
		writeShort(os, width - 1);
		writeShort(os, height - 1);
		for (int i = 0; i < 4 + 48 + 1; i++)
			os.write(0);
		os.write(1);	/* 1 bitplane */
		writeShort(os, width);	/* bytes per line */
		for (int i = 0; i < 2 + 58; i++)
			os.write(0);
		for (int y = 0; y < height; y++) {
			int x = 0;
			do {
				int count = 0xc0;
				int value = data[y][x];
				do {
					count++;
					x++;
				} while (x < width && value == data[y][x] && count < 0xff);
				if (count > 0xc1 || (value & 0xc0) == 0xc0)
					os.write(count);
				os.write(value);
			} while (x < width);
		}
		os.write(0xc);
		for (int i = 0; i < 256; i++)
			os.write(palette[i]);
	}
}
