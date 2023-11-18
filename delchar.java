  public StringBuffer delete (StringBuffer sbuf,
                              int begin,
                              int len)
  {
    int length;
    int current = 0;
    <B>char</B>[] temp;
        
   	if (sbuf == null)
      throw new IllegalArgumentException(&quot;null StringBuffer
argument&quot;);
    if (len == 0)
      throw new IllegalArgumentException (&quot;length must be greater than
0&quot;);
      
    // call this guy once, we use it in several places
    length = sbuf.length();	

    if (start &gt;= length)
      throw new IndexOutOfBoundsException (&quot;starting index is greater
than available length&quot;);

    // trim len if it's too big
    if (start+len &gt; length)
    {
      len = length - start;
    }

    // create a temp buffer big enough to hold the result        
    temp = new <B>char</B>[length-len];
        
    // copy the front into the temp buffer
    if (start != 0)
    {
      sbuf.getChars(0,start,temp,0);
      current = start;
    }

    // copy the tail into the temp buffer
    if (start+len &lt; length)
    {
      sbuf.getChars(start+len,length,temp,current);
    }

    //zero and append the temp buffer back into sbuf
    sbuf.setLength(0);
    sbuf.append(temp);
    return(sbuf);
  }
