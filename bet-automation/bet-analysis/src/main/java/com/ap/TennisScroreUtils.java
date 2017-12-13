package com.ap;

import com.ap.model.MomentResult;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by andrii on 11.12.17.
 */
public class TennisScroreUtils {
    public static int getWinner(String score, int set) {
        if (!score.contains("(")) {
            return -1;
        }
        String[] setsArray = score.substring(score.indexOf("(") + 1, score.indexOf(")")).split(",");

        if (setsArray.length < 2) {
            return -1;
        }

        if(setsArray.length < set){
            return -1;
        }

        String currentSet = setsArray[set - 1];
        List<Integer> currentSetResults = Arrays.stream(currentSet.split("-")).map(Integer::parseInt).collect(Collectors.toList());


        if (currentSet.startsWith("6") || currentSet.startsWith("7")) {
            if (currentSetResults.get(0) > currentSetResults.get(1)) {
                return 1;
            }
        }

        if (currentSet.endsWith("6") || currentSet.endsWith("7")) {
            if (currentSetResults.get(0) < currentSetResults.get(1)) {
                return 2;
            }
        }

        // special case, not finished sed:
        //....
        // special case, not finished sed:
        //....
        if (set == setsArray.length && Math.max(currentSetResults.get(0), currentSetResults.get(1)) >= 5) {
            if (currentSetResults.get(0) > currentSetResults.get(1)) {
                return 1;
            } else if (currentSetResults.get(0) < currentSetResults.get(1)) {
                return 2;
            }
        }

        return -1;
    }

    public static boolean hasLeadershipInSet(List<MomentResult> results, int set, int player) {

        for (MomentResult result : results) {
            if (!result.toString().contains("(")) {
                continue;
            }
            String[] setsArray = result.getResult()
                    .substring(result.getResult().indexOf("(") + 1, result.getResult().indexOf(")")).split(",");
            if (setsArray.length < set) {
                continue;
            }
            String currentSet = setsArray[set - 1];
            List<Integer> currentSetResults = Arrays.stream(currentSet.split("-")).map(Integer::parseInt).collect(Collectors.toList());

            if (player == 1 && currentSetResults.get(0) > currentSetResults.get(1) && currentSetResults.get(0) > 0) {
                return true;
            }
            if (player == 2 && currentSetResults.get(0) < currentSetResults.get(1) && currentSetResults.get(1) > 0) {
                return true;
            }
        }
        return false;
    }
}
