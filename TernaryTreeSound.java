// Ternary Tree implementation

import java.util.*;
import java.io.*;

public class TernaryTree {
	class TernaryNode { 
		char splitchar;
     TernaryLeaf lokid;
     TernaryLeaf eqkid;
     TernaryLeaf hikid;
   }

   class TernaryLeaf extends TernaryNode {
     String s;
   }

   public synchronized TernaryLeaf insert(String s) {
     s = s + "\0";  // This data structure relies on a sentinel to term. str.s
     return insert1(root, s);
   }

	protected String insertstr;

  private synchronized TernaryLeaf insert1(TernaryLeaf p, String s) {
    if (p == null) {
      p = new TernaryLeaf();
      p.splitchar = s.charAt(0);
      p.lokid = p.eqkid = p.hikid = null;
    }
    if (s.charAt(0) < p.splitchar) 
      p.lokid = insert1(p.lokid, s);
    else if (s.charAt(0) == p.splitchar) {
      if (s.charAt(0) == 0)
        p.s = insertstr;
      else
        p.eqkid = insert1(p.eqkid, s.substring(1));
    } 
    else
      p.hikid = insert1(p.hikid, s);
    return p;
  }

  protected TernaryLeaf root = null;

  public void pmsearch(String s) {
    pmsearch(root, s + '\0');
  }

  private void pmsearch(TernaryLeaf p, String s) {
    if (p == null) return;
    // nodecnt++;  // uncomment to keep track of how many nodes touched
    if ((s.charAt(0) == '.') || (s.charAt(0) < p.splitchar))
      pmsearch(p.lokid, s);
    if ((s.charAt(0) == '.') || (s.charAt(0) == p.splitchar))
      if ((p.splitchar != 0) && (s.charAt(0) != 0))
        pmsearch(p.eqkid, s.substring(1));
    if ((s.charAt(0) == 0) && (p.splitchar == 0))
      System.out.println(p.s); // print it out
      //srcharr[srchtop++] = p.s;  // save it off
    if ((s.charAt(0) == '.') || (s.charAt(0) > p.splitchar))
      pmsearch(p.hikid, s);
  }
   

   public boolean rsearch(String s) {
     return rsearch(root, s + "\0");
   }


   public boolean contains(StringBuffer s) {
	   System.out.println("Trying to find: " + s);
     return rsearch(s.toString());
   }

   public boolean contains(String s) {
	   System.out.println("Trying to find: " + s);
     return rsearch(root, s + "\0");
   }

   private boolean rsearch(TernaryLeaf p, String s) {
     if  (p == null) return false;
     if (s.charAt(0) < p.splitchar)
       return rsearch(p.lokid, s);
     else if (s.charAt(0) > p.splitchar)
       return rsearch(p.hikid, s);
     else {
       if (s.charAt(0) == 0)
         return true;
       return rsearch(p.eqkid, s.substring(1)); 
     }
   }

   public void nearsearch(String s, int d) {
     s = s + '\0';
     nearsearch(root, s, d);
   }

   private void nearsearch(TernaryLeaf p, String s, int d) {
     if ((p == null) || (d < 0)) return;
     if ((d > 0) || (s.charAt(0) < p.splitchar))
       nearsearch(p.lokid, s, d);
     if (p.splitchar == 0) {
       if (s.length() <= d)
         // srcharr[srchtop++] = p.s; // to store in array
         System.out.println(p.s); // to print out
     } else
       nearsearch(p.eqkid, (s.charAt(0) == 0) ? s.substring(1) : s,
         (s.charAt(0) == p.splitchar) ? d : d-1);
     if ((d > 0) || (s.charAt(0) > p.splitchar))
       nearsearch(p.hikid, s, d);
   }

   public void traverse() {
     traverse(root);
   }

   public void traverse(TernaryLeaf p) {
     if (p == null) return;
     traverse(p.lokid);
     if (p.splitchar != '\0')
       traverse(p.eqkid);
     else
       System.out.println(p.s);
     traverse(p.hikid);
   }

   public void rtraverse() {
     rtraverse(root);
   }

   private void rtraverse(TernaryLeaf p) {
		 if (p == null) return;
		 rtraverse(p.hikid);
		 if (p.splitchar != '\0')
			 rtraverse(p.eqkid);
		 else
			 System.out.println(p.s);
		 rtraverse(p.lokid);
   }

   public static void main (String argv[]) {
	   String insertstr;
     TernaryTree ttb = new TernaryTree();
		 ttb.insertstr = "abandon";
     ttb.root = ttb.insert("abandon");
		 ttb.insertstr = "aback";
     ttb.root = ttb.insert("aback");
		 ttb.insertstr = "aba&ft";
     ttb.root = ttb.insert("aba&ft");
		 ttb.insertstr = "abandon";
     ttb.root = ttb.insert("abandon");
		 ttb.insertstr = "abandoned";
     ttb.root = ttb.insert("abandoned");
		 ttb.insertstr = "abandoning";
     ttb.root = ttb.insert("abandoning");
		 ttb.insertstr = "abandonment";
     ttb.root = ttb.insert("abandonment");
		 ttb.insertstr = "abandons";
     ttb.root = ttb.insert("abandons");
		 ttb.insertstr = "abase";
     ttb.root = ttb.insert("abase");
		 ttb.insertstr = "abased";
     ttb.root = ttb.insert("abased");
		 ttb.insertstr = "abasement";
     ttb.root = ttb.insert("abasement");
		 ttb.insertstr = "notorious";
     ttb.root = ttb.insert("notorious");
     ttb.traverse();  // works!
     ttb.rtraverse();  // works!
     ttb.pmsearch("a.an.on");  // works!
     ttb.nearsearch("abandom", 2);  // works!
     System.out.println("It's there? : " + ttb.rsearch("abandon"));
     System.out.println("It's there? : " + ttb.rsearch("abdoning"));
   }
}
