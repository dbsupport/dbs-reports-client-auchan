/**
 * 
 */
package pl.com.dbs.reports.client.auchan.support.db;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.junit.Test;

/**
 * TODO
 *
 * @author Krzysztof Kaziura | krzysztof.kaziura@gmail.com | http://www.lazydevelopers.pl
 * @coptyright (c) 2014
 */
public class EncodingTest {
	private static final String xmlstring = "Zarzuć gęślą jaźń";//"Здравей' хора";
	
	private void print(String result) {
		for (int i = 0; i < result.length(); ++i) {
			System.out.printf("%x ", (int) result.charAt(i));
		}
	}

	@Test
	public void test0() {
		System.out.println("Default Charset=" + Charset.defaultCharset());
	}
	
	@Test
	public void test1() {
		String result = convertToUTF8(convertFromUTF8(xmlstring));
		print(result);
		System.out.println(result);
	}

	
	@Test
	public void test2() {
		String result = ucs2ToUTF8(xmlstring.getBytes());
		print(result);
		System.out.println(result);
	}	
	
	
	
	
	
	//convert from UTF-8 -> internal Java String format
	String convertFromUTF8(String s) {
		String out = null;
		try {
			out = new String(s.getBytes("ISO-8859-1"), "UTF-8");
		} catch (java.io.UnsupportedEncodingException e) {
			return null;
		}
		return out;
	}
	// convert from internal Java String format -> UTF-8
	String convertToUTF8(String s) {
		String out = null;
		try {
			out = new String(s.getBytes("UTF-8"), "ISO-8859-1");
		} catch (java.io.UnsupportedEncodingException e) {
			return null;
		}
		return out;
	}	
	
	String ucs2ToUTF8(byte[] ucs2Bytes) {  
		try {
	    String unicode = new String(ucs2Bytes, "UTF-16");  
	    return new String(unicode.getBytes("UTF-8"), "Cp1252");
		} catch (UnsupportedEncodingException e) {}
	     
	    return null;  
	} 	
	
}
