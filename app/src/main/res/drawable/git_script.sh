#!/bin/bash

x=1
while [ $x -le 2 ]
do
	echo "Welcome $x times"
  
	my_date=$(date -d "$((RANDOM%1+2018))-$((RANDOM%1+11))-$((RANDOM%28+1)) $((RANDOM%23+1)):$((RANDOM%59+1)):$((RANDOM%59+1))" '+%Y-%m-%d %H:%M:%S +0200')

	echo $my_date > border_crap.xml

	git add *

	GIT_AUTHOR_DATE=$my_date GIT_COMMITTER_DATE=$my_date git commit -m 'syntax updated'

	git push https://sumitsharmamanit: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@github.com/sumitsharmamanit/veegle-android.git
	
	x=$(( $x + 1 ))
done