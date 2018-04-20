package com.ap.api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexpUtils {

    public static String getDateFromStatus(String status){
        System.out.println(status);
        for (MatchResult match : allMatches(Pattern.compile("\\[2018.*?0\\]"), status)) {
            System.out.println(match.group() + " at " + match.start());
            LocalDate parse = LocalDate.parse(match.group().replace("[", "").replace("]", "").replace(" 00:05:16.0", ""));
            System.out.println(parse);
        }
        return "";
    }

    public static void main(String[] args) {
        System.out.println(getDateFromStatus("<a href='/statuses'>index</a><br/><span style='color:red'>1(5035[[comp:andrii]])) [player2:4ERROR] [live stream: yes]Tennis. ATP. Acapulco. Hard[2018-02-28 00:05:16.0] - {{Ebden Matthew - Querrey Sam}}><b>ZPLAYER2_CROSS_LIMIT:ZRETURS_TO_BET_BOUND</b> [3.9, 1.25] 1-1(6-3,1-6,6-6) 6:5[lastupdate=2018-02-28 01:56:40.0]</span><br/> <br/>"));

    }

    public static Iterable<MatchResult> allMatches(
            final Pattern p, final CharSequence input) {
        return new Iterable<MatchResult>() {
            public Iterator<MatchResult> iterator() {
                return new Iterator<MatchResult>() {
                    // Use a matcher internally.
                    final Matcher matcher = p.matcher(input);
                    // Keep a match around that supports any interleaving of hasNext/next calls.
                    MatchResult pending;

                    public boolean hasNext() {
                        // Lazily fill pending, and avoid calling find() multiple times if the
                        // clients call hasNext() repeatedly before sampling via next().
                        if (pending == null && matcher.find()) {
                            pending = matcher.toMatchResult();
                        }
                        return pending != null;
                    }

                    public MatchResult next() {
                        // Fill pending if necessary (as when clients call next() without
                        // checking hasNext()), throw if not possible.
                        if (!hasNext()) { throw new NoSuchElementException(); }
                        // Consume pending so next call to hasNext() does a find().
                        MatchResult next = pending;
                        pending = null;
                        return next;
                    }

                    /** Required to satisfy the interface, but unsupported. */
                    public void remove() { throw new UnsupportedOperationException(); }
                };
            }
        };
    }

}
