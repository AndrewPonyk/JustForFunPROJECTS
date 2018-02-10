package com.ap.utils;

import com.ap.model.BetItem;
import com.ap.model.MomentResult;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {
    public static final Pattern pattern = Pattern.compile(".*<a href[^>]*>(.*)<span class=\"score\">(.*?)<\\/span><\\/a>");

    public static LocalDateTime currentTime = LocalDateTime.now();

    public static void main(String[] args) {
        String html = "<colgroup width=\"3%\" span=\"1\"></colgroup><colgroup width=\"34%\" span=\"1\"></colgroup><colgroup width=\"7%\" span=\"3\"></colgroup><colgroup width=\"6%\" span=\"2\"></colgroup><colgroup width=\"6%\" span=\"2\"></colgroup><colgroup width=\"6%\" span=\"3\"></colgroup><tbody> <tr>  <td><input value=\"26333659\" class=\"ch_l\" type=\"checkbox\"></td>  <td class=\"td_n\"><a href=\"bet.html?hl=26333659\"><small>Duval Victoria/Kleybanova Alisa</small> - <small>Dart Harriet/Di Lorenzo Francesca</small> <span class=\"score\">0-0(0-1) 30:40</span></a></td>  <td><u><a id=\"r7199_170_q8dso17yMokMSbDM6fTm\"><i class=\"blank\">1.70</i></a></u></td>  <td>&nbsp;</td>  <td><u><a id=\"r7199_205_CfjeQbmD46dv3nFMV.Vm\"><i class=\"blank\">2.05</i></a></u></td>  <td>&nbsp;</td>  <td>&nbsp;</td>  <td>&nbsp;</td>  <td>&nbsp;</td>  <td><b>19.5</b></td>  <td><u><a id=\"r7199_190_eI0HuzrUe4oOfB_sCKO6\"><i class=\"blank\">1.90</i></a></u></td>  <td><u><a id=\"r7199_185_8oXRvySO8ULVyknEDv7o\"><i class=\"blank\">1.85</i></a></u></td> </tr></tbody>";
        html = html.replaceAll("\n","");
        final Matcher matcher = pattern.matcher(html);
        matcher.find();
        String title = matcher.group(1);

        System.out.println(matcher.group(1));

    }

    public static BetItem parseBetItem(String sport, String content, String coef1, String coef2, String link,
                                       String competition) throws Exception {
        content=content.replaceAll("\n","");
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

            Double coef1Double = 0.0;
            Double coef2Double = 0.0;
            try {
                coef1Double = Double.valueOf(coef1);
                coef2Double = Double.valueOf(coef2);
            }catch (Exception e){
                // no coefs

            }
            results.add(new MomentResult(coef1Double, coef2Double, currentScore, currentTime.toString()));
        }
        System.out.println(title + " ::: " + competition);
        return new BetItem(title, sport, results, "0", link, "", competition);
    }

}