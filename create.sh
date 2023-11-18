for i in `cat spellings`
do
	echo `grep -c $i /usr/dict/words` $i
done
