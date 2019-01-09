package botsfinder.porohs;

import org.apache.commons.lang3.StringUtils;

public class PorohsCommentChecker {

    public boolean isBotsText(String text){
        if(StringUtils.isNotBlank(text) && text.contains("зрад")){
            return true;
        }
        return false;
    }
}
