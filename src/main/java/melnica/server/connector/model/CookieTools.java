package melnica.server.connector.model;

import java.text.FieldPosition;
import java.util.Date;

import javax.servlet.http.Cookie;

public class CookieTools {

	/**
	 * Return the header name to set the cookie, based on cookie version
	 */
	public static String getCookieHeaderName(Cookie cookie) {
		int version = cookie.getVersion();

		if (version == 1) {
			return "Set-Cookie2";
		} else {
			return "Set-Cookie";
		}
	}

	/**
	 * Return the header value used to set this cookie
	 * 
	 * @deprecated Use StringBuffer version
	 */
	public static String getCookieHeaderValue(Cookie cookie) {
		StringBuffer buf = new StringBuffer();
		getCookieHeaderValue(cookie, buf);
		return buf.toString();
	}

	/**
	 * Return the header value used to set this cookie
	 */
	public static void getCookieHeaderValue(Cookie cookie, StringBuffer buf) {
		int version = cookie.getVersion();

		// this part is the same for all cookies

		String name = cookie.getName(); // Avoid NPE on malformed cookies
		if (name == null)
			name = "";
		String value = cookie.getValue();
		if (value == null)
			value = "";

		buf.append(name);
		buf.append("=");
		maybeQuote(version, buf, value);

		// add version 1 specific information
		if (version == 1) {
			// Version=1 ... required
			buf.append(";Version=1");

			// Comment=comment
			if (cookie.getComment() != null) {
				buf.append(";Comment=");
				maybeQuote(version, buf, cookie.getComment());
			}
		}

		// add domain information, if present

		if (cookie.getDomain() != null) {
			buf.append(";Domain=");
			maybeQuote(version, buf, cookie.getDomain());
		}

		// Max-Age=secs/Discard ... or use old "Expires" format
		if (cookie.getMaxAge() >= 0) {
			if (version == 0) {
				buf.append(";Expires=");
				if (cookie.getMaxAge() == 0)
					DateTool.oldCookieFormat.format(new Date(10000), buf, new FieldPosition(0));
				else
					DateTool.oldCookieFormat.format(new Date(System.currentTimeMillis() + cookie.getMaxAge() * 1000L),
							buf, new FieldPosition(0));
			} else {
				buf.append(";Max-Age=");
				buf.append(cookie.getMaxAge());
			}
		} else if (version == 1)
			buf.append(";Discard");

		// Path=path
		if (cookie.getPath() != null) {
			buf.append(";Path=");
			maybeQuote(version, buf, cookie.getPath());
		}

		// Secure
		if (cookie.getSecure()) {
			buf.append(";Secure");
		}
	}

	static void maybeQuote(int version, StringBuffer buf, String value) {
		if (version == 0 || isToken(value))
			buf.append(value);
		else {
			buf.append('"');
			buf.append(value);
			buf.append('"');
		}
	}

	//
	// from RFC 2068, token special case characters
	//
	private static final String tspecials = "()<>@,;:\\\"/[]?={} \t";

	/*
	 * Return true iff the string counts as an HTTP/1.1 "token".
	 */
	private static boolean isToken(String value) {
		int len = value.length();

		for (int i = 0; i < len; i++) {
			char c = value.charAt(i);

			if (c < 0x20 || c >= 0x7f || tspecials.indexOf(c) != -1)
				return false;
		}
		return true;
	}

}
