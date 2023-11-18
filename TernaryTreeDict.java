// Ternary Tree implementation

import java.util.*;
import java.io.*;
import TernaryTree;

public class TernaryTreeDict extends TernaryTree {
   public void loadWords(String file) {
     FileReader r;
     BufferedReader br;
     StreamTokenizer in;
     String token;
     TernaryLeaf temp;
     try {
       r = new FileReader(file);
       br = new BufferedReader(r);  // For efficiency!

     // read tokens by StreamTokenizer class
       r = new FileReader(file);
       br = new BufferedReader(r);  // For efficiency!
       in = new StreamTokenizer(br);
       in.wordChars('&', '&'); // Add & for things like AT&T
       while (in.nextToken() != StreamTokenizer.TT_EOF) {
	       insert(in.sval); // actually do insert, in.sval will change,
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

   public static void main (String argv[]) {
     TernaryTreeDict ttb = new TernaryTreeDict();
     //ttb.loadWords("word");  // testing only
     //ttb.loadWords("words");  // testing only
     ttb.loadWords("/usr/dict/words");  // The real thing!
     //System.out.println("It's there? : " + ttb.rsearch("abandon"));
     //System.out.println("It's there? : " + ttb.rsearch("abdoning"));
     //ttb.traverse();  // works!
     //ttb.rtraverse();  // works!
     //ttb.pmsearch("a.an.on");  // works!
     //ttb.nearsearch("abandom", 2);  // works!
   }
}
