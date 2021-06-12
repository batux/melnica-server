package melnica.server.connector.http.util;

import java.text.FieldPosition;
import java.util.Date;

import javax.servlet.http.Cookie;

public class CookieTools {

	private static final String tspecials = "()<>@,;:\\\"/[]?={} \t";
	
	public static String getCookieHeaderName(Cookie cookie) {
		int version = cookie.getVersion();

		if (version == 1) {
			return "Set-Cookie2";
		} else {
			return "Set-Cookie";
		}
	}

	public static String getCookieHeaderValue(Cookie cookie) {
		
		StringBuffer buffer = new StringBuffer();
		getCookieHeaderValue(cookie, buffer);
		return buffer.toString();
	}

	public static void getCookieHeaderValue(Cookie cookie, StringBuffer buffer) {
		
		int version = cookie.getVersion();

		String name = cookie.getName();
		if (name == null) {
			name = "";
		}
			
		String value = cookie.getValue();
		if (value == null) {
			value = "";
		}

		buffer.append(name);
		buffer.append("=");
		appendQuoteIfRequired(version, buffer, value);

		if (version == 1) {
			buffer.append(";Version=1");

			if (cookie.getComment() != null) {
				buffer.append(";Comment=");
				appendQuoteIfRequired(version, buffer, cookie.getComment());
			}
		}

		if (cookie.getDomain() != null) {
			buffer.append(";Domain=");
			appendQuoteIfRequired(version, buffer, cookie.getDomain());
		}

		if (cookie.getMaxAge() >= 0) {
			if (version == 0) {
				buffer.append(";Expires=");
				if (cookie.getMaxAge() == 0) {
					DateTool.oldCookieDateFormat.format(new Date(10000), buffer, new FieldPosition(0));
				}
				else {
					DateTool.oldCookieDateFormat.format(new Date(System.currentTimeMillis() + cookie.getMaxAge() * 1000L), buffer, new FieldPosition(0));
				}
			} 
			else {
				buffer.append(";Max-Age=");
				buffer.append(cookie.getMaxAge());
			}
		} 
		else if (version == 1) {
			buffer.append(";Discard");
		}

		if (cookie.getPath() != null) {
			buffer.append(";Path=");
			appendQuoteIfRequired(version, buffer, cookie.getPath());
		}

		if (cookie.getSecure()) {
			buffer.append(";Secure");
		}
	}

	static void appendQuoteIfRequired(int version, StringBuffer buf, String value) {
		
		if (version == 0 || isToken(value)) {
			buf.append(value);
		}
		else {
			buf.append('"');
			buf.append(value);
			buf.append('"');
		}
	}

	private static boolean isToken(String value) {
		
		int len = value.length();
		
		for (int i = 0; i < len; i++) {
			char character = value.charAt(i);
			if (character < 0x20 || character >= 0x7f || tspecials.indexOf(character) != -1) {
				return false;
			}
		}
		return true;
	}
}
