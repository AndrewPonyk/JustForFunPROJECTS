package com.ap.utils;

import com.ap.model.BetItem;
import com.ap.model.MomentResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {
    public static final Pattern pattern = Pattern.compile("<a href[^>]+>(.*?)<span class=\"score\">(.*?)</span><\\/a>");

    public static void main(String[] args) {
    }

    public static BetItem parseBetItem(String sport, String content, String coef1, String coef2) throws Exception {
        final Matcher matcher = pattern.matcher(content);
        matcher.find();
        String title = matcher.group(1)
                .replace("<small>", "")
                .replace("</small>", "")
                .replaceAll("\\<[^>]*>","")
                .replaceAll("'", "")
                .trim();
        LinkedList<MomentResult> results = new LinkedList<>();
        if(matcher.groupCount() > 1){
            String currentScore = matcher.group(2);

            if(coef1.trim().isEmpty() || coef2.trim().isEmpty()){
                throw new Exception("No coefs in:" + title);
            }
            Double coef1Double =  Double.valueOf(coef1),
                    coef2Double =  Double.valueOf(coef2);

            results.add(new MomentResult(coef1Double, coef2Double, currentScore));
        }
        return new BetItem(title, sport, results, "0");
    }
}
