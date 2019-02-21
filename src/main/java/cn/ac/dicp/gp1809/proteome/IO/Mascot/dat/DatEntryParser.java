/* 
 ******************************************************************************
 * File: DatEntryParser.java * * * Created on 11-12-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot.dat;

/**
 * Parser of mascot dat entry.
 * 
 * @author Xinning
 * @version 0.1, 11-12-2008, 15:32:51
 */
public class DatEntryParser {

	/**
	 * Parse the line with the pattern of "KEY=VALUE" into key entry. If a line
	 * is null or blank, null will be returned.
	 * 
	 * <li> If the input string contains no value (KEY= ), the value will be set
	 * as null.
	 * 
	 * <li> Notice: the first '=' will be considered as the spliter of the key
	 * and value even though there may be other '='
	 * 
	 * <li> The key and the value is the original pair, that is the quotation
	 * mark around the key and the value will <b>NOT</b> be removed. Use the
	 * {@link #trim_quotation_mark(String)} to remove the enclosing quotation
	 * marks
	 * 
	 * @param line
	 * @return
	 */
	public static Entry parseEntry(String line) {

		if (line == null || line.length() == 0)
			return null;

		int idx = line.indexOf('=');
		if (idx == -1)
			throw new IllegalArgumentException("\"" + line + "\""
			        + " doesn't seem like a KEY=VALUE string");

		String key = line.substring(0, idx);
		idx++;
		String value = idx < line.length() ? line.substring(idx) : null;

		return new Entry(key, value);
	}

	/**
	 * Trim the quotation mark ("") if the str is enclosed by them. Otherwise,
	 * direct return it after the calling of trim() method to remove the end or
	 * begin blank character.
	 * 
	 * <p>
	 * <b>Note: if the string is only ended or started with single quotation
	 * mark (e.g. "AAAA or AAAAA"), the end or start quotation mark will not be
	 * removed (return the origin string).
	 * 
	 * @param str
	 * @return
	 */
	public static String trim_quotation_mark(String str) {
		String found = str.trim();
		// Trim away opening and closing '"'.
		int len = found.length() - 1;
		if (len > 0) {
			if (found.charAt(0) == '"' && found.charAt(len) == '"') {
				found = found.substring(1, len);

				return found.trim();
			}
		}
		return found;
	}

	/**
	 * Entry of mascot dat file which is commonly represented in a line as
	 * KEY=VALUE
	 * 
	 * @author Xinning
	 * @version 0.1, 11-12-2008, 15:33:25
	 */
	public static class Entry {
		private String key;
		private String value;

		/**
		 * @param key
		 * @param value
		 */
		private Entry(String key, String value) {
			super();
			this.key = key;
			this.value = value;
		}

		/**
		 * @return the key
		 */
		public final String getKey() {
			return key;
		}

		/**
		 * @return the value
		 */
		public final String getValue() {
			return value;
		}

		/**
		 * key=value
		 */
		@Override
		public String toString() {
			return key + "=" + value;
		}
	}

	public static void main(String[] args) {
		System.out
		        .println(parseEntry("\"REVERSED_IPI:IPI00553236.1\"=87843.02,\"Hypothetical protein\""));
	}

}
