/* 
 ******************************************************************************
 * File: MascotMimeParser.java * * * Created on 11-13-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot.dat;

/**
 * Mascot will encode special characters such as '.', ';', '"' and so on into
 * symbols like "%2c" for ','. You may need this method to decode or encode
 * them.
 * 
 * @author Xinning
 * @version 0.1, 11-13-2008, 18:56:26
 */
public class MascotMimeParser {
	/**
	 * Encode specific character into mascot specific mime code.
	 * 
	 * @param c
	 *            character
	 * @return The mascot defined mime code which starts with '%' and is a
	 *         two-char-long string, e.g. "%2c"
	 */
	public static String encode(char c) {
		throw new NullPointerException("Has not desgined");
	}

	/**
	 * Mascot may decode special characters into specific mime string value.
	 * This method can be used to decode a mascot compiled string into normal 
	 * string. 
	 * 
	 * <p> The title of the query name
	 * 
	 * @param str_with_code
	 * @return
	 */
	public static String decodeString(String str_with_code) {
		int len = str_with_code.length();
		StringBuilder sb = new StringBuilder(len);

		for (int i = 0; i < len; i++) {
			char c = str_with_code.charAt(i);
			if (c == '%') {
				String code = str_with_code.substring(i + 1, i + 3);
				c = MascotMimeParser.decode(code);
				i += 2;
			}

			sb.append(c);
		}

		return sb.toString();
	}

	/**
	 * decode mascot specific mime code into readable character.
	 * 
	 * @param code
	 *            The mascot defined mime code. Both strings with or without '%'
	 *            character are accepted, e.g. 2c or %2c
	 * @return
	 */
	public static char decode(String code) {

		if (code == null)
			throw new NullPointerException("The input code must not be null.");

		int len = code.length();

		if (len != 2 && len != 3) {
			throw new IllegalArgumentException("The input code: \"" + code
			        + "\" is illegal. Excepted \"%xx\" or \"xx\"");
		}

		char c, d;

		if (len == 3) {
			if (code.charAt(0) != '%')
				throw new IllegalArgumentException("The input code: \"" + code
				        + "\" is illegal. Excepted \"%xx\" or \"xx\"");
			c = code.charAt(1);
			d = code.charAt(2);
		} else {
			c = code.charAt(0);
			d = code.charAt(1);
		}

		switch (c) {
		case '0': {
			switch (d) {
			case '9':
				return '\t';
			}
		}
		case '2': {
			switch (d) {
			case '0':
				return ' ';
			case '1':
				return '!';
			case '2':
				return '"';
			case '3':
				return '#';
			case '4':
				return '$';
			case '5':
				return '%';
			case '6':
				return '&';
			case '7':
				return '\'';
			case '8':
				return '(';
			case '9':
				return ')';
			case 'a':
				return '*';
			case 'b':
				return '+';
			case 'c':
				return ',';
			case 'd':
				return '-';
			case 'e':
				return '.';
			case 'f':
				return '/';
			}

			break;
		}

		case '3': {
			switch (d) {
			case 'a':
				return ':';
			case 'b':
				return ';';
			case 'c':
				return '<';
			case 'd':
				return '=';
			case 'e':
				return '>';
			}
			break;
		}

		case '4': {
			switch (d) {
			case '0':
				return '@';
			}
			break;
		}

		case '5': {
			switch (d) {
			case 'b':
				return '[';
			case 'c':
				return '\\';
			case 'd':
				return ']';
			case 'e':
				return '^';
			}
			break;
		}

		case '7': {
			switch (d) {
			case 'b':
				return '{';
			case 'c':
				return '|';
			case 'd':
				return '}';
			case 'e':
				return '~';
			}
			break;
		}
		}

		throw new IllegalArgumentException("Unkown input code: \"%" + code
		        + "\".");
	}

}
