import java.util.*;
import java.io.*;

class Diphthong {
	public static void main (String argv[]) {
	  Properties Diphthongs = new Properties();
		String spellings;
	  StringTokenizer st;
		String test_word = "notorious";
		Enumeration keys;
    try {
			FileInputStream fis = new FileInputStream("diphthong.properties");
			Diphthongs.load(fis);
     }
     catch (FileNotFoundException e) {
       System.err.println(e.getMessage() + "\nThe stack trace is:");
       e.printStackTrace();
     }
     catch (IOException e) {
       System.err.println(e.getMessage() + "\nThe stack trace is:");
       e.printStackTrace();
     }

		 //Diphthongs.list(System.out);

		 keys = Diphthongs.propertyNames();

		 while (keys.hasMoreElements()) {
		   System.out.println("key = " + keys.nextElement());
		 }
		 
		 /*
		 System.out.println("Contents of diphthong.properties");
		 System.out.println("f = " + Diphthongs.getProperty("f"));
		 spellings = Diphthongs.getProperty("f");

		 st = new StringTokenizer(spellings, ",");

	   while (true) {
		   try {
		     System.out.println("token = " + st.nextToken());
			 }
			 catch (java.util.NoSuchElementException e) {
			   break;
			 }
		 }
		 */

	}
}
