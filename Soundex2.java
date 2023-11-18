/* This is a modified soundex.  Basicially the only thing different is
   That this algorithm doesn't chop off at 3 numerics. */

class Soundex2 {
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
	  // Should probably throw some kind of invalid word exception 
	  System.err.println("Invalid word!  Please check input!");
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
    Soundex2 s = new Soundex2();
    System.out.println("Should be E460 :");
    System.out.println(s.translate("Euler"));
    System.out.println(s.translate("Ellery"));
    System.out.println("Should be G200 :");
    System.out.println(s.translate("Gauss"));
    System.out.println(s.translate("Ghosh"));
    System.out.println("Should be H416 :");
    System.out.println(s.translate("Hilbert"));
    System.out.println(s.translate("Heilbronn"));
    System.out.println("Should be K530 :");
    System.out.println(s.translate("Knuth"));
    System.out.println(s.translate("Kant"));
    System.out.println("Should be L300 :");
    System.out.println(s.translate("Lloyd"));
    System.out.println(s.translate("Ladd"));
    System.out.println("Should be L222 :");
    System.out.println(s.translate("Lukasiewicz"));
    System.out.println(s.translate("Lissajous"));
    System.out.println("Should be B650");
    System.out.println(s.translate("Bryan"));
    System.out.println("Should be V430");
    System.out.println(s.translate("Vold"));

    System.out.println("Should be M322");
    System.out.println(s.translate("METSEKER"));
    System.out.println("Should be M326");
    System.out.println(s.translate("METSKER"));
    System.out.println(s.translate("philosophy"));
    System.out.println(s.translate("filosofy"));
    System.out.println(s.translate("philosophie"));
    System.out.println(s.translate("filosophie"));
    System.out.println(s.translate("philosofie"));
  }
}
