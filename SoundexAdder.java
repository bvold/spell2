/* This is a modified soundex.  Basicially the only thing different is
   That this algorithm doesn't chop off at 3 numerics. */

import java.util.*;
import java.io.*;


class SoundexAdder {
  public String collapse(String s) {
    StringBuffer t = new StringBuffer(s); 
    String right = new String();
    if (t.length() == 1)
      return t.toString();
    right = collapse(t.toString().substring(1));
    if (t.charAt(0)==right.charAt(0)) {
      return t.charAt(0) + right.substring(1);
    }
    return t.charAt(0) + right;
  }

  public String translate(String s) {
    StringBuffer t = new StringBuffer(s.toUpperCase());
    for (int i = 0; i < t.length(); i++) {
      switch (t.charAt(i)) {
        case 'A': case 'E': case 'H': case 'I': case 'O': 
	case 'U': case 'W': case 'Y': 
	  t.setCharAt(i, '0');
	  break;
	case 'B': case 'F': case 'P': case 'V': 
	  t.setCharAt(i, '1');
	  break;
	case 'C': case 'G': case 'J': case 'K': case 'Q':
	case 'S': case 'X': case 'Z': 
	  t.setCharAt(i, '2');
	  break;
	case 'D': case 'T': 
	  t.setCharAt(i, '3');
	  break; 
	case 'L': 
	  t.setCharAt(i, '4');
	  break;
	case 'M': case 'N': 
	  t.setCharAt(i, '5');
	  break;
	case 'R' : 
	  t.setCharAt(i, '6');
	  break;
	default:
		t.setCharAt(i, '0'); // Handle punctuation
	  // Should probably throw some kind of invalid word exception 
	  //System.err.println("Invalid word!  Please check input!");
      }
    }
    t = new StringBuffer(collapse(t.toString()));
    // Note: use t for translated soundex #, s for orig char
    char f = t.charAt(0);  // Save first char (must be _after_ collapse)
    t = new StringBuffer(t.toString().substring(1));
    //int count=0;
    //StringBuffer temp = new StringBuffer("");  // orig soundex code
    StringBuffer temp = new StringBuffer("");
    //for (int i=0; i < t.length() && count < 3; i++) {  // orig soundex code
    // This code eliminates the '0' (number) from string
    for (int i=0; i < t.length() ; i++) {
          if (t.charAt(i) > '0') {
	    temp = temp.append(t.charAt(i));
    //	    count++;  // orig soundex code
	  }
    }
    
    // Code to pad to 3 numbers in orig soundex code.
    //for (; count < 3; count++) {
    //  temp = temp.append("0");
    //}

    return f + temp.toString(); 
  }

  public static void main(String argv[]) {
    SoundexAdder s = new SoundexAdder();
     FileReader r;
     BufferedReader br;
     StreamTokenizer in;
     String token;
     try {
       r = new FileReader("word");
       br = new BufferedReader(r);  // For efficiency!
       in = new StreamTokenizer(br);
       in.wordChars('&', '&'); // Add & for things like AT&T
       while (in.nextToken() != StreamTokenizer.TT_EOF) {
    		 System.out.println(s.translate(in.sval) + " " + in.sval);
       }
     }
     catch (FileNotFoundException e) {
       System.err.println(e.getMessage() + "\nThe stack trace is:");
       e.printStackTrace();
     }
     catch (IOException e) {
       System.err.println(e.getMessage() + "\nThe stack trace is:");
       e.printStackTrace();
     }

  }
}
