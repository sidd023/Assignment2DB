# Assignment2DB

Database Systems Assignment 2

Loading Syntax: (adds into heap and tree)
java dbload -p pagesize datafile
eg:
java dbload -p 4096 Data.csv

Querying Syntax:
Heapfile:
java dbquery -h text pagesize
eg:
Java dbquery -h 33 4096

range search: (id)
java dbquery -hid range1—-range2 pagesize
eg:
Java dbquery -hid 01--05 4096

range search: (date format MM/dd/yyyy)
java dbquery -hdate range1—-range2 pagesize
eg:
Java dbquery -hdate “02/28/2012”—-“03/28/2012” 4096


Querying Syntax:
B+ tree:
java dbquery -b text pagesize
eg:
Java dbquery -b 33 4096

range search: (id)
java dbquery -bid range1—-range2 pagesize
eg:
Java dbquery -bid 01--05 4096

range search: (date format MM/dd/yyyy)
java dbquery -bdate range1—-range2 pagesize
eg:
Java dbquery -bdate “02/28/2012”—-“03/28/2012” 4096

