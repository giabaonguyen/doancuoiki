package utils;

import java.io.File;

public class FileUtils {

	public final static String jpeg ="jepg";
	public final static String txt ="txt";
	public final static String png ="png";
	public final static String mp3 ="mp3";
	
	public static String getExtension(File f) {
		String ext = null;
		String fileName = f.getName();
		int i = fileName.lastIndexOf('.');
		
		if(i>0&&i<fileName.length()-1) {
			ext= fileName.substring(i+1).toLowerCase();
		}
		return ext;
	}
}
