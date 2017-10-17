-- 1) Select sports by the count of completed bets
SELECT count(*),SPORT FROM BET_HISTORY WHERE STAGE like '%5COMPLETED%' AND DATE
GROUP BY SPORT
ORDER BY count(*) desc;
-- 09.10.17
-- 95	Tennis
-- 49	Basketball
-- 33	Volleyball
-- 14	Handball
-- 6	Hockey
-- 3	Badminton

-- ------------------------------------------------------------

-- 2) Completed bets by days
SELECT count(*),DATE FROM BET_HISTORY WHERE STAGE like '%5COMPLETED%' AND DATE > '2017-09-23'
GROUP BY DATE
ORDER BY DATE desc;

/*
18	2017-10-08
20	2017-10-07
4	2017-10-06
21	2017-10-05
19	2017-10-04
11	2017-10-03
15	2017-10-02
16	2017-10-01
19	2017-09-30
10	2017-09-29
12	2017-09-28
17	2017-09-27
19	2017-09-26
7	2017-09-24
*/