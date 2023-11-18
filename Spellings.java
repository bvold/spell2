import java.util.*;
import java.io.*;

class Spellings {
	public static void main (String argv[]) {
	  Properties Spellings = new Properties();
	  Properties Diphthongs = new Properties();
		String diphthongs;
	  StringTokenizer st;
	  StringTokenizer spell_try;
		String test_word = "laff";
		Enumeration keys;
		TernaryTree key_trie = new TernaryTree();
		TernaryTree diph_trie = new TernaryTree();
		String diph_array[] = new String[50];  // Shouldn't ever go this high.
																					 // Know of any 50 char words?
	  Vector corrections = new Vector();  // List of potential corrections
		int d_count = 0;
    try {
			FileInputStream s_prop  = new FileInputStream("spellings.properties");
			Spellings.load(s_prop);
     }
     catch (FileNotFoundException e) {
       System.err.println(e.getMessage() + "\nThe stack trace is:");
       e.printStackTrace();
     }
     catch (IOException e) {
       System.err.println(e.getMessage() + "\nThe stack trace is:");
       e.printStackTrace();
     }

		//Spellings.list(System.out);

		keys = Spellings.propertyNames();

		String temp;

		while (keys.hasMoreElements()) {
			key_trie.insert((String)keys.nextElement());
			/* Original code
			temp = (String)keys.nextElement();
			key_trie.insert(temp);
			*/
		}
    //key_trie.traverse();  // works!

    try {
			FileInputStream d_prop = new FileInputStream("diphthong.properties");
			Diphthongs.load(d_prop);
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
			temp = (String)keys.nextElement();
			diph_trie.insert(temp);
		}
		// make backup copy, so we don't destroy original	
    String test_copy = new String(test_word);
		int offset = 0;
		int size = 0;
		do {
			// Next loop finds largest matching diphthong
			while (true) {
			  if (((offset+size) < test_copy.length()) && key_trie.rsearch(test_copy.substring(offset, offset+size+1)))
			        {
			    size++;
				}
				else {
				  break;
				}
			}
			d_count++; // increment the number of diphthongs we've seen
			diph_array[d_count-1] = test_copy.substring(offset, offset+size);
			System.out.println("diphthong = " + test_copy.substring(offset, offset+size));
//			System.out.println("offset = " + offset);
//			System.out.println("size = " + size);
//			System.out.println("size + offset =" + (size + offset));
//			System.out.println("test_word.length()" + test_word.length());
			offset += size; 
			size = 0; // reset for next iteration
		} while (offset+size < test_word.length());

//		System.out.println("Number of diphthongs = " + d_count);

    String word = "";  // will be used to assemble word to check.
		String diph_try = "";
		String diph_names = ""; // (my own) Names of diphthongs
		String poss_spellings = ""; // Diphthongs possible spelling
		String next_diph = "";
		// This is single diphthong replacement (each diphthong changed separately)
		for (int i = 0; i < d_count; i++) {
//		System.out.println("diph_array[" + i + "] = " + diph_array[i]);
		  diph_names = Spellings.getProperty(diph_array[i]);
System.out.println("diph_names = " + diph_names);
			st = new StringTokenizer(diph_names, ","); // diphthongs separated w/ ","
			while (st.hasMoreElements()) {
			  diph_try = (String)st.nextElement(); // FIXME -- casting efficiency?
				// Assemble word to be checked
				// get possible ways of spelling each named diphthong
				poss_spellings = Diphthongs.getProperty(diph_try);
System.out.println("poss_spellings = " + poss_spellings);
				spell_try = new StringTokenizer(poss_spellings, ",");
				next_word:
				while (spell_try.hasMoreElements()) {
					word = "";  // reset for next word
          next_diph = (String)spell_try.nextElement();  
//System.out.println("next_diph = " + next_diph);
					for (int j = 0; j < d_count; j++) {
						// FIXME -- this can probably be more efficient.
						// if the next diphthong is the same as the original, don't
						// bother to create a word, skip to next.
						if (next_diph.equals(diph_array[i])) {
						  continue next_word; // Skip this word, it's the original spelling
						}
						if (j == i) { // If this is the diphthong we're working on
							word += next_diph; // add the new spelling
						}
						else {
							word += diph_array[j];  //otherwise, use the original
						}
					}
System.out.println("checking ---> " + word);
				}
			}
		}


		 /*
		 System.out.println("Contents of spellings.properties");
		 System.out.println("f = " + Spellings.getProperty("f"));
		 diphthongs = Spellings.getProperty("f");

		 st = new StringTokenizer(diphthongs, ",");

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
