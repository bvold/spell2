/* 	 Class Spell.  A spelling checker based on some ideas from
     Automatic Spelling Correction in Scientific and Scholarly Text
		 by Joseph J. Polluck and Antonio Zamora
		 Communications of the ACM, April 1984, Volume 27, Number 4
		 Pages 358-368.
*/

import java.util.*;
import TernaryTree;
import Soundex;
import java.io.*;
//import TernaryTreeExtended;

/** @author Bryan T. Vold
*/
final class Spell6 {
	// Do this the hard, brute-force way first.
	// Later reimplent functionality as a function of the double trie.

	// Note '&' in the following decl. to handle e.g. AT&T
	// Note: " " needs to be handled separately as a special case, because it
	// could be two words (probably also need to check two words to see if they
	// should be one!

	private final String ALPHABET="abcdefghijklmnopqrstuvwxyz&";
	private final int MAX_WORDSIZE = 50;
	private static Hashtable corrections = new Hashtable();  // List of potential corrections;  This is a Hashtable(), so it handles duplicates automatically
	private TernaryTreeDict Dictionary = new TernaryTreeDict();
	//private TernaryTreeExtended Soundex = new TernaryTreeExtended();
	protected Properties Spellings = new Properties();  // Spellings w/ diphthong names
	protected Properties Diphthongs = new Properties();  // Diphthong names w/ spellings
  protected int d_count = 0;

	
	public Spell6() {  // Constructor
		// Load the tree with the dictionary
		//Dictionary.loadWords("/usr/dict/words");  // Include this file in dist?
		Dictionary.loadWords("word1");  // testing only
		// Probably using a double trie?
		// creating either a skeleton key offset, omission key, or soundex key
	}

  protected boolean loadSpellings(String file) {
	  try {
      FileInputStream s_prop  = new FileInputStream(file);
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
    return true;
	}

	protected boolean loadDiphthongs(String file) {
	  try {
      FileInputStream d_prop  = new FileInputStream(file);
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
    return true;
	}

	// Checks a word phonetically, by systematically breaking it apart and then
	// substituting all possible spellings and check those final spelling
	// against a dictionary.
	// FIXME: possibility of two different ways of getting to correct spelling.
	//				possibly add to Hashtable, and will be automatically eliminated?
	// Note to myself: words that will be possible corrections will be found
	// (and could be added) when the prefix is in the dictionary and we are
	// curr_max == d_count-1!
	public boolean phonetic_check(StringBuffer oword) { // 'o' for original word
	  if (Dictionary.contains(oword)) {
		  return true;
		}
		else {
			Enumeration keys; // generic for getting property names
			loadDiphthongs("diphthong.properties");
			loadSpellings("spellings.properties");
			keys = Spellings.propertyNames();
			StringTokenizer st;
			StringTokenizer spell_try;

			String diph_array[] = new String[MAX_WORDSIZE];  
			String curr_diph[] = new String[MAX_WORDSIZE];  // Pieces to build current word
			Object pos4poss[] = new Object[MAX_WORDSIZE]; // Will store Enumeration states
			TernaryTree key_trie = new TernaryTree();
			// Insert into ternary trie for quick search (HashTable better?) FIXME
			while (keys.hasMoreElements()) {
				key_trie.insert((String)keys.nextElement());
			}
			// make backup copy, so we don't destroy original
			String test_copy = new String(oword);
			int offset = 0; // Offset from start of word where diphthong starts
			int size = 0; // Current size of longest diphthong match.
			do {
				// Next loop finds largest matching diphthong
				while (true) {
					if (((offset+size) < test_copy.length()) &&
							key_trie.rsearch(test_copy.substring(offset, offset+size+1))) {
						size++;
					}
					else {
						break;
					}
				}
				d_count++; // increment the number of diphthongs we've seen
				// store the diphthong in a 0-based array
				diph_array[d_count-1] = test_copy.substring(offset, offset+size);
				offset += size; // Update for start of next diphthong
				size = 0; // reset for next iteration
			} while (offset+size < oword.length());
			//---------------------------------------------------
			// At this point, we have the word broken up into 
			// 'd_count' longest diphthong matches, stored in
			// the diph_array (could this be the curr_word array?)
			//---------------------------------------------------

			for (int i = 0; i < d_count; i++) {
			  System.out.println("diph_array["+i+"] = "+diph_array[i]);
			}

			String word = "";  // will be used to assemble word to check.
			String diph_try = "";  // current diphthong we are trying
			String diph_names = ""; // (my own) Names of diphthongs
			String poss_spellings = ""; // Diphthongs possible spelling
			String next_diph = "";  // Temporary placeholder
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

// Sanity check, make sure we know what's in the buckets (testing only)
for (int i = 0; i < d_count; i++) {
	System.out.println("diph_bucket["+i+"] = " + diph_bucket[i]);
}

			// Start the Enumeration sequence
			// An enum seq for each diphthong position
			Enumeration poss4pos[] = new Enumeration[d_count]; 
			for (int i = 0; i < d_count; i++) {
				poss4pos[i] = diph_bucket[i].keys();  // FIXME or use values; NULL=""
			}

			Hashtable unused_prefix = new Hashtable();

			// This part works.
			// Pre-screen the first bucket, so we don't do unnecessary checking
//			String temp;
//			while (poss4pos[0].hasMoreElements()) {
//				temp = (String)poss4pos[0].nextElement();
//	System.out.println("temp = " + temp);
//				if (!Dictionary.sssearch(temp)) {
//System.out.println("Removing ----> " + temp);
//					unused_prefix.addElement(temp);
//				}	
//			}

			// This part works.
			// Have to do this, because Java Hashtable gets confused if we delete
			// while looping through a key enumeration  FIXME: Investigate, is this
			// really true?
			
			Enumeration unused_diphthongs = unused_prefix.elements();
			while (unused_diphthongs.hasMoreElements()) {
				diph_bucket[0].remove((String)unused_diphthongs.nextElement());
			}
//			while (
//			for (int i=0; i < unused_prefix.size(); i++) {
//				diph_bucket[0].remove(unused_prefix.elementAt(i));  // Remove it, it can't start any words
//			}

			// Reset the first bucket that we just checked.
			poss4pos[0] = diph_bucket[0].keys();

			String curr_word[] = new String[d_count + 1];  // +1 for silent letters FIXME
			
		  // Make sure we check this word against dictionary!	
			for (int i = 0; i < d_count; i++) {
				curr_word[i] = (String)poss4pos[i].nextElement();  // Has to be something there
//				System.out.println("curr_word[" + i + "] = " + curr_word[i]);
				// FIXME Check this word against dictionary, otherwise it will get missed.
			}
			short curr_max = 0;  // start at the beginning
			short curr_pos = 0;	 // Current diphthong position we're looking at
			boolean done = false;
			boolean diph_used = false;  // If it's possible for diphthong to be here
			boolean finished = false;
			unused_prefix = new Hashtable();

			// Will be done when poss4pos[dcount-1] wraps around.
			// This may need to be broken up into smaller chunks / routines FIXME
			for (int i = 0; i < d_count; i++) {
				poss4pos[i] = diph_bucket[i].keys();  // FIXME or use values; NULL=""
			}
			screening_loop:
			while (!done) {
				diph_used = false;
				if (poss4pos[curr_pos].hasMoreElements()) {
					curr_word[curr_pos] = (String)poss4pos[curr_pos].nextElement();
					word = "";  // reset for this iter.

					// create the word to check
					for (int i = 0; i < curr_max+1; i++) {
						word += curr_word[i];
					}

//System.out.println("checking --> " + word);
					if (Dictionary.sssearch(word)) {
System.out.println("prefix is used --> " + word);
						diph_used = true;
// Testing FIXME
System.out.println("undelete the unused prefix, since it is in a word");
System.out.println("undeleting ---------------->" + curr_word[curr_max]);
						//unused_prefix.remove(curr_word[curr_max]);
					}
					else {
System.out.println("prefix is _NOT_ used --> " + word);
System.out.println("curr_word[curr_max] = " + curr_word[curr_max]);
System.out.println("adding to unused diphthong table --------------> " + curr_word[curr_max]);
					  unused_prefix.put(curr_word[curr_max],"");
					}
				}
				else {  // No more elements in this position
System.out.println("No more elements in position " + curr_pos);
System.out.println("curr_max = " + curr_max);
System.out.println("curr_pos = " + curr_pos);
System.out.println("curr_word[curr_max] --> " + curr_word[curr_max]);
System.out.println("curr_word[curr_pos] --> " + curr_word[curr_pos]);
					if (curr_pos != d_count-1) { // Not at the end
						poss4pos[curr_pos] = diph_bucket[curr_pos].keys(); // Reset (wrapped)
						finished = false;
						// This not the right place, for first position (I don't think)
						if (!diph_used) {
								System.out.println("removing ----> " + curr_word[curr_max]);
//							  diph_bucket[curr_max].remove(curr_word[curr_max]);
						}
						//curr_max++;  // I don't think this is the right place for this
						try {
							while (!finished) {
								curr_pos++;
								if (curr_max < curr_pos) {
System.out.println("unused_prefixes = " + unused_prefix);
			//						int upsz = unused_prefix.size();


			unused_diphthongs = unused_prefix.elements();
			while (unused_diphthongs.hasMoreElements()) {
			  String temp_ele = (String)unused_diphthongs.nextElement();
System.out.println("really removing! ----> " + temp_ele);

				diph_bucket[curr_max].remove(temp_ele);
										poss4pos[curr_max] = diph_bucket[curr_max].keys();
			}
//									for (int p = 0; p < upsz; p++) {
//System.out.println("really removing! ----> " + unused_prefix.elementAt(p));
//										diph_bucket[curr_max].remove(unused_prefix.elementAt(p));
//										poss4pos[curr_max] = diph_bucket[curr_max].keys();
//									}
// Sanity check
for (int i = 0; i < d_count; i++) {
	System.out.println("diph_bucket["+i+"] = " + diph_bucket[i]);
}
									unused_prefix = new Hashtable(); // is this fastest way to do this
									curr_max = curr_pos;
								}
//		System.out.println("curr_pos = " + curr_pos);
								if (poss4pos[curr_pos].hasMoreElements()) {
		System.out.println("Getting here! if");
									curr_word[curr_pos] = (String)poss4pos[curr_pos].nextElement();
//System.out.println("curr_word[curr_pos] --> " + curr_word[curr_pos]);
									finished = true;
								}
								else {
		System.out.println("Getting here! else");
									poss4pos[curr_pos] = diph_bucket[curr_pos].keys(); // reset
									curr_word[curr_pos] = (String)poss4pos[curr_pos].nextElement();
//System.out.println("curr_word[curr_pos] --> " + curr_word[curr_pos]);
								}
							}
						}
						catch (ArrayIndexOutOfBoundsException e) {
						  done = true;
						}
System.out.println("curr_pos =" + curr_pos);
System.out.println("curr_max =" + curr_pos);
						curr_pos = 0;  // reset for next iter.
					}
					else {
						done = true;
					}
				}
			}

			curr_max = 0;  // start at the beginning
			curr_pos = 0;	 // Current diphthong position we're looking at
			done = false;

			// Will be done when poss4pos[dcount-1] wraps around.
			// This may need to be broken up into smaller chunks / routines FIXME
			for (int i = 0; i < d_count; i++) {
				poss4pos[i] = diph_bucket[i].keys();  // FIXME or use values; NULL=""
			}
			screening_loop2:
			while (!done) { // Could a sentinel be used?
				diph_used = false;
				if (poss4pos[curr_pos].hasMoreElements()) {
					curr_word[curr_pos] = (String)poss4pos[curr_pos].nextElement();
//System.out.println("curr_word[curr_pos]"+curr_word[curr_pos]);	
					word = "";  // reset for this iter.

					// create the word to check
					for (int i = 0; i < curr_max; i++) {
						word += curr_word[i];
					}

System.out.println("checking --> " + word);
					if (Dictionary.sssearch(word)) {
						diph_used = true;
					}
				}
				else {  // No more elements in this position
					if (curr_pos != d_count-1) { // Not at the end
						poss4pos[curr_pos] = diph_bucket[curr_pos].keys(); // Reset (wrapped)
						finished = false;
						//curr_max++;
						try {
							while (!finished) {
								curr_pos++;
								if (curr_max < curr_pos) {
									curr_max = curr_pos;
								}
								if (curr_max == d_count) {
									finished = true;
									continue;
								}
//		System.out.println("curr_pos = " + curr_pos);
								if (poss4pos[curr_pos].hasMoreElements()) {
									curr_word[curr_pos] = (String)poss4pos[curr_pos].nextElement();
//System.out.println("curr_word[curr_pos]"+curr_word[curr_pos]);	
									finished = true;
								}
								else {
									poss4pos[curr_pos] = diph_bucket[curr_pos].keys(); // reset
									curr_word[curr_pos] = (String)poss4pos[curr_pos].nextElement();
System.out.println("curr_word[curr_pos]"+curr_word[curr_pos]);	
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
			curr_max = 0;  // start at the beginning
			curr_pos = 0;	 // Current diphthong position we're looking at
			done = false;

			// Will be done when poss4pos[dcount-1] wraps around.
			// This may need to be broken up into smaller chunks / routines FIXME
			check_loop:
			while (!done) { // Could a sentinel be used?
				if (poss4pos[curr_pos].hasMoreElements()) {
					curr_word[curr_pos] = (String)poss4pos[curr_pos].nextElement();
//System.out.println("curr_word[curr_pos]"+curr_word[curr_pos]);	
					word = "";  // reset for this iter.

					// create the word to check
					for (int i = 0; i < d_count; i++) {
						word += curr_word[i];
					}

//System.out.println("checking --> " + word);
					if (Dictionary.contains(word)) {
						corrections.put(word,"");
System.out.println("adding --> " + word);
					}
				}
				else {  // No more elements in this position
					if (curr_pos != d_count-1) { // Not at the end
						poss4pos[curr_pos] = diph_bucket[curr_pos].keys(); // Reset (wrapped)
						finished = false;
						curr_max++;
						try {
							while (!finished && (curr_pos != curr_max)) {
								curr_pos++;
//System.out.println("curr_pos = " + curr_pos);
								if (poss4pos[curr_pos].hasMoreElements()) {
									curr_word[curr_pos] = (String)poss4pos[curr_pos].nextElement();
//System.out.println("curr_word[curr_pos]"+curr_word[curr_pos]);	
									finished = true;
								}
								else {
									poss4pos[curr_pos] = diph_bucket[curr_pos].keys(); // reset
									curr_word[curr_pos] = (String)poss4pos[curr_pos].nextElement();
//System.out.println("curr_word[curr_pos]"+curr_word[curr_pos]);	
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
		return false;
	}

	// This won't be needed in JDK1.2, because they implement this function.
  StringBuffer delchar(int index, StringBuffer o) {
		int l = o.length();
		if (index > l) {
			System.err.println("Index out of range");
			System.exit(-1); // Don't continue
		}
		if (index == 0) {
			return new StringBuffer(o.toString().substring(1));
		}
		else if (index == l) {
			return new StringBuffer(o.toString().substring(0,l-1)); 
		}
		else {
			return new StringBuffer(o.toString().substring(0,index) + o.toString().substring(index+1,l));
		}
	}

  public boolean check(StringBuffer o) {
	  return check(o.toString());
  }

  public boolean check(String o) {  // word
    int P;
		int l;
		int m;
    
		StringBuffer w = new StringBuffer(o);  // Create StringBuffer for checks
    if (Dictionary.contains(w)) {
      return true;
		}
    else {
      // Get list of words within 50 of collating distance.
			// Handles omission case
      l = w.length();  // Save recalculation
			m = ALPHABET.length();  // Save recalculation
			// Handle omissions before last character
      for (int i = 0; i < l-1; i++) {
        for (int j = 0; j < m; j++) {
				  // FIXME look at, is it cheaper to undo insert or start over?
			    w = new StringBuffer(o);  // Reset to original word
				  w.insert(i,ALPHABET.charAt(j));	
					if (Dictionary.contains(w))
						corrections.put(w,"");
				}
			}

			// Handle omissions at last character
			for (int j = 0; j < m; j++) {
			  w = new StringBuffer(o);
				w.append(ALPHABET.charAt(j));
				if (Dictionary.contains(w))
					corrections.put(w,"");
			}

			// Handles transposition case
			char tc; // temp char
			for (int i = 0; i < l-2; i++) {  // Step through entire word
			  w = new StringBuffer(o);  // Start from known value  FIXME?(use prev.work)
			  // Swap two adjacent chars
			  tc = w.charAt(i+1);
				w.setCharAt(i+1,w.charAt(i));
				w.setCharAt(i,tc);
				if (Dictionary.contains(w))
					corrections.put(w,"");
				else { // Reverse the transpose and continue
				  // or is it faster to set w to o?
				  tc = w.charAt(i+1);
					w.setCharAt(i+1, w.charAt(i));
					w.setCharAt(i, tc);
				  continue;
				}
			}
			// Handles insertion
			w = new StringBuffer(o); // Set word to original 
			StringBuffer temp;
			for (int i = 0; i < l-1; i++) {
			  // FIXME, is it cheaper to undo or start over?
			  temp = delchar(i, w);
				if (Dictionary.contains(temp))
					corrections.put(temp,"");
			}
			// Handles substitution
			for (int i = 0; i < l-1; i++) {
				for (int j = 0; j < m; j++)  {
				  // Note, if you ever change the _new_ below, need to new the add
					// to the correction table.
			    w = new StringBuffer(o);  // Set word to original
				  w.setCharAt(i, ALPHABET.charAt(j));
					if (Dictionary.contains(w)) {
						corrections.put(w,"");
					}
					else
						continue;
				}
			}
		}
		return false;
	}

  public static void main (String argv[]) {
	  Spell6 s = new Spell6();
//		StringBuffer w = new StringBuffer("laff");
		StringBuffer w = new StringBuffer("kumputer");

		if (s.phonetic_check(w)) {
//		if (s.check(w)) {
		  System.out.println("The word : \""+w+"\" is spelled correctly");
		}
		else {
		  System.out.println("The word : \""+w+"\" is spelled incorrectly");
			System.out.println("Possible corrections are:");
			System.out.println(corrections);
			if (!s.check(w)) {
				System.out.println("Possible corrections are:");
				System.out.println(corrections);
			}
		}
  }
}
