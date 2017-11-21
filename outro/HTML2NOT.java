import java.io.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;

public class HTML2NOT extends Parser {
	private static DTD dtd;
	static {
		try {
			dtd = DTD.getDTD("");
		} catch (IOException e) {
		}
		dtd.defEntity("lt", GENERAL, '<');
		dtd.defEntity("gt", GENERAL, '>');
		dtd.defEntity("amp", GENERAL, '&');
		dtd.defEntity("nbsp", GENERAL, ' ');
	}
	private byte[] sizes;
	private OutputStream os;

	public HTML2NOT(byte[] sizes, OutputStream os) throws IOException {
		super(dtd);
		this.sizes = sizes;
		this.os = os;
	}

	private static final int WIDTH = 255;
	private static final int SPACE = 4;

	private static final int LEFT = 0;
	private static final int CENTER = 1;
	private static final int RIGHT = 2;
	private static final int JUSTIFY = 3;
	private int align = LEFT;

	public void handleEmptyTag(TagElement tag) {
		//System.out.println("empty " + tag);
		if (HTML.Tag.BR.equals(tag.getHTMLTag()))
			write(0);
		flushAttributes();
	}

	public void handleStartTag(TagElement tag) {
		//System.out.println("start " + tag);
		HTML.Tag htag = tag.getHTMLTag();
		if (HTML.Tag.P.equals(htag)) {
			align = LEFT;
			String a = (String) getAttributes().getAttribute(HTML.Attribute.ALIGN);
			if ("center".equalsIgnoreCase(a))
				align = CENTER;
			else if ("right".equalsIgnoreCase(a))
				align = RIGHT;
			else if ("justify".equalsIgnoreCase(a))
				align = JUSTIFY;
			write(0);
			flushAttributes();
		}
	}

	private int translate(char c) {
		switch (c) {
		case '~': return '|';
		case 'ł': return 'l' & 0x1f;
		case 'ó': return 'o' & 0x1f;
		case 'ś': return 's' & 0x1f;
		default:
			if (!(c >= ' ' && c <= '_' || c >= 'a' && c <= 'z'))
				throw new IllegalArgumentException("Character " + c);
			return c;
		}
	}

	private int left;
	private int words;

	private int fitWordsInLine(char[] text, int offset) {
		left = WIDTH + SPACE;
		words = 0;
		while (offset < text.length) {
			if (text[offset] == ' ')
				offset++;
			/*else*/ {
				int wordLeft = left - SPACE;
				int wordOffset;
				for (wordOffset = offset; wordOffset < text.length && text[wordOffset] != ' '; wordOffset++) {
					wordLeft -= sizes[translate(text[wordOffset])];
					if (wordLeft < 0)
						return offset;
				}
				offset = wordOffset;
				left = wordLeft;
				words++;
			}
		}
		return offset;
	}

	private void write(int b) {
		try {
			os.write(b);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void emitSpace(int width) {
		if (width == 0)
			return;
		if (width < 0)
			throw new IllegalArgumentException();
		if (width >= 0x80) {
			write(0xff);
			width -= 0x7f;
		}
		write(width | 0x80);
	}

	private void emit(char c) {
		write(translate(c));
	}

	private void emit(char[] text, int offset, int end, boolean justify) {
		if (justify) {
			final int spaces = words - 1;
			final int i = SPACE + left / spaces;
			final int f = left % spaces;
			int e = spaces / 2;
			while (offset < end) {
				if (text[offset] == ' ') {
					e -= f;
					if (e < 0) {
						e += spaces;
						emitSpace(i + 1);
					}
					else
						emitSpace(i);
				}
				else
					emit(text[offset]);
				offset++;
			}
		}
		else
			while (offset < end) {
				if (text[offset] == ' ')
					emitSpace(SPACE);
				else
					emit(text[offset]);
				offset++;
			}
	}

	public void handleText(char[] text) {
		/*System.err.println("text @@" + new String(text) + "@@");*/
		for (int offset = 0; offset < text.length; ) {
			int end = fitWordsInLine(text, offset);
			if (words > 0) {
				boolean justify = false;
				switch (align) {
				case LEFT:
					break;
				case CENTER:
					emitSpace(left / 2);
					break;
				case RIGHT:
					emitSpace(left);
					break;
				case JUSTIFY:
					if (end < text.length && words > 1)
						justify = true;
					break;
				default:
					throw new IllegalStateException();
				}
				emit(text, offset, end, justify);
			}
			offset = end;
			if (offset < text.length)
				write(0);
		}
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 3) {
			System.err.println("Usage: HTML2NOT eed22c.siz outro.html outro.not");
			System.exit(3);
		}
		InputStream is = new FileInputStream(args[0]);
		byte[] sizes = new byte[128];
		is.read(sizes, 0x20, 0x40);
		is.read(sizes, 0x00, 0x20);
		is.read(sizes, 0x60, 0x20);
		is.close();
		Reader r = new InputStreamReader(new FileInputStream(args[1]), "UTF8");
		OutputStream os = new FileOutputStream(args[2]);
		new HTML2NOT(sizes, os).parse(r);
		r.close();
		os.close();
	}
}
