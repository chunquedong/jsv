//
// Copyright (c) 2013, chunquedong
// Licensed under the MIT License
//
// History:
//	 2013-8-9	Jed Young	Creation
//

package chunquedong.jsv.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validate {

	public static class ValidateException extends RuntimeException {
		private static final long serialVersionUID = 2075908417197198740L;

		public ValidateException(String msg) {
			super(msg);
		}
	}

	public static boolean matches(String regex, String text,
			boolean caseSensitive) {
		Pattern pattern;
		if (!caseSensitive) {
			pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		} else {
			pattern = Pattern.compile(regex);
		}
		Matcher matcher = pattern.matcher(text);
		return (matcher.matches());
	}

	public static boolean isEmail(String text) {
		return matches("^(?:\\w+\\.?)*\\w+@(?:\\w+\\.?)*\\w+$", text, true);
	}

	public static boolean isDigit(String text) {
		for (int i = 0; i < text.length(); i++) {
			if (!Character.isDigit(text.charAt(i)))
				return false;
		}
		return true;
	}

	public static boolean isUri(String text) {
		return matches("(\\w+):\\/\\/([^\\/:]+)(:\\d*)?([^#]*)", text, true);
	}

	public static boolean isDate(String text) {
		return matches("^(\\d{4})\\-(\\d{2})\\-(\\d{2})$", text, true);
	}

	public static boolean isIdentifier(String text) {
		return matches("^[a-zA-Z][a-zA-Z0-9_]{3,35}$", text, true);
	}
	
	public static boolean isName(String text) {
		return matches("^[a-zA-Z0-9_-]{4,35}$", text, true);
	}

	// //////////////////////////////////////////////////////////////////////
	// check
	// //////////////////////////////////////////////////////////////////////

	public Validate email(String text, String msg) {
		if (text.isEmpty())
			return this;
		if (!isEmail(text))
			throw new ValidateException(msg != null ? msg
					: "Not a valid email: " + text);
		return this;
	}

	public Validate digit(String text, String msg) {
		if (text.isEmpty())
			return this;
		if (!isDigit(text))
			throw new ValidateException(msg != null ? msg
					: "Not a valid digit: " + text);
		return this;
	}

	public Validate date(String text, String msg) {
		if (text.isEmpty())
			return this;
		if (!isDate(text))
			throw new ValidateException(msg != null ? msg
					: "Not a valid date: " + text);
		return this;
	}

	public Validate uri(String text, String msg) {
		if (text.isEmpty())
			return this;
		if (!isUri(text))
			throw new ValidateException(msg != null ? msg : "Not a valid uri: "
					+ text);
		return this;
	}

	public Validate identifier(String text, String msg) {
		if (text.isEmpty())
			return this;
		if (!isIdentifier(text))
			throw new ValidateException(msg != null ? msg
					: "Not a valid identifier: " + text);
		return this;
	}
	
	public Validate name(String text, String msg) {
		if (text.isEmpty())
			return this;
		if (!isName(text))
			throw new ValidateException(msg != null ? msg
					: "Not a valid name: " + text);
		return this;
	}

	public Validate range(int i, int min, int max, String msg) {
		if (i > max || i < min)
			throw new ValidateException(msg != null ? msg : i + " out of ["
					+ min + "," + max + "]");
		return this;
	}

	public Validate length(String text, int min, int max, String msg) {
		if (text.length() > max || text.length() < min)
			throw new ValidateException(msg != null ? msg : "the " + text
					+ " length out of [" + min + "," + max + "]");
		return this;
	}

	public Validate required(String text) {
		return required(text, null);
	}

	public Validate required(String text, String msg) {
		if (text == null || text.isEmpty())
			throw new ValidateException(msg != null ? msg
					: "the text is required");
		return this;
	}
}
