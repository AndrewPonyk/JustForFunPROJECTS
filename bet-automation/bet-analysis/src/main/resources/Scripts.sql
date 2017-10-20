SELECT COUNT(*),DATE FROM BET_HISTORY
WHERE STAGE LIKE '%:5COMPLETED%' AND DATE >= '2017-09-23'
GROUP BY DATE ORDER BY DATE DESC;

----------------

SELECT COUNT(*),SPORT FROM BET_HISTORY
WHERE STAGE LIKE '%:5COMPLETED%' AND DATE >= '2017-09-23'
GROUP BY SPORT ORDER BY COUNT(*) DESC;

163	Tennis
83	Basketball
63	Volleyball
22	Handball
9	Hockey
8	Badminton
6	Billiards
5	Football
3	Beach volleyball
2	Bandy
2	American football
1	Rugby
1	Futsal
1	Waterpolo

-------------------------------------------