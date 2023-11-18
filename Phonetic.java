// Program comments are dedicated to Frankowski.
// Silent characters (at the end only?) will be handled separately (I think)
import java.util.*;
import java.io.*;

class Phonetic {
  private static boolean check (String o) { // Temporary for testing
	  return true;
	}
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
			temp = (String)keys.nextElement();
			key_trie.insert(temp);
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
		// why am I doing this?
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
		// FIXME Hashtable might not be the best data structure here, but I know
		// that it will work!
		Hashtable diph_bucket[] = new Hashtable[d_count]; 

		for (int i = 0; i < d_count; i++) {
		  diph_bucket[i] = new Hashtable();
		}

		// Cycle through all diphthong positions (that we've just parsed)
		for (int i = 0; i < d_count; i++) {
//		System.out.println("diph_array[" + i + "] = " + diph_array[i]);
		  diph_names = Spellings.getProperty(diph_array[i]);
//System.out.println("diph_names = " + diph_names);
			st = new StringTokenizer(diph_names, ","); // diphthongs separated w/ ","
			while (st.hasMoreElements()) {
			  diph_try = (String)st.nextElement(); // FIXME -- casting efficiency?
				// get possible ways of spelling each named diphthong
				poss_spellings = Diphthongs.getProperty(diph_try);
//System.out.println("poss_spellings = " + poss_spellings);
				spell_try = new StringTokenizer(poss_spellings, ",");
				while (spell_try.hasMoreElements()) {
          diph_bucket[i].put((String)spell_try.nextElement(), "");
//System.out.println("next_diph = " + next_diph);
				}
			}
		}
//System.out.println(diph_bucket[1]);

		// diph_bucket should contain minimal set of diphthongs that are possible
		// for each position that we've parsed.

    // Start the Enumeration sequence
		Enumeration poss4pos[] = new Enumeration[d_count];
		for (int i = 0; i < d_count; i++) {
		  poss4pos[i] = diph_bucket[i].keys();  // FIXME or values NULL=""
		}

		String curr_word[] = new String[d_count + 1];  // +1 for silent letters FIXME
		
		
		for (int i = 0; i < d_count; i++) {
		  curr_word[i] = (String)poss4pos[i].nextElement();  // Has to be something there
			System.out.println("curr_word[" + i + "] = " + curr_word[i]);
		}
		short curr_max = 0;  // start at the beginning
		short curr_pos = 0;
		boolean done = false;

		// Will be done when poss4pos[dcount-1] wraps around.
		// This may need to be broken up into smaller chunks / routines FIXME
		outer_loop:
		while (!done) { // Could a sentinel be used?
		  if (poss4pos[curr_pos].hasMoreElements()) {
			  curr_word[curr_pos] = (String)poss4pos[curr_pos].nextElement();
				word = "";  // reset for next iter.
				for (int i = 0; i < d_count; i++) {
				  word += curr_word[i];
				}
System.out.println("checking --> " + word);
				if (check(word)) {
				  corrections.addElement(word);
				}
 				else {
					continue;  // Do next iter in this position
				}
			}
			else {  // No more elements in this position
				if (curr_pos != d_count-1) { // Not at the end
				  poss4pos[curr_pos] = diph_bucket[curr_pos].keys(); // Reset (wrapped)
				  boolean finished = false;
					curr_max++;
					try {
						while (!finished && (curr_pos != curr_max)) {
							curr_pos++;
	System.out.println("curr_pos = " + curr_pos);
							if (poss4pos[curr_pos].hasMoreElements()) {
								curr_word[curr_pos] = (String)poss4pos[curr_pos].nextElement();
								finished = true;
							}
							else {
								poss4pos[curr_pos] = diph_bucket[curr_pos].keys(); // reset
								curr_word[curr_pos] = (String)poss4pos[curr_pos].nextElement();
							}
						}
					}
					catch (ArrayIndexOutOfBoundsException e) {
					done = true;
					}
					curr_pos = 0;  // reset for next iter.
				}
				else {
				  done = true;
				}
			}
		}
	}
}
