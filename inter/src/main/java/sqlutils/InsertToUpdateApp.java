package sqlutils;


import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InsertToUpdateApp {
    public static void main(String[] args) {
        System.out.println("Convert insert to update");
        //String sql = new Scanner(System.in).nextLine();
        String sql = "Insert into \"setup_windows\" (WINDOW_ID,WINDOW_NAME,WINDOW_SCRIPT,WINDOW_PARAMS,MENU_ID,HELP_TOPIC_ID,HELP_FILE_NAME,WINDOW_LOCK_TYPE_ID,WINDOW_WIDTH,WINDOW_HEIGHT,SEC_LANG_NAME,WINDOW_SCRIPT_ID,CONFIG_SCRIPT_SUPPORT,ENABLE_CUSTOM_HELP,CUSTOM_HELP,STAGED_EDIT_SUPPORT,FEAT_UID,EXTERNAL_HELP_FILE) values ('show_128','Classes Identity Bridge Data',null,null,null,null,null,1,'75%','65%','זיהוי סיווגי נתוני גשרים','GeneralMultiFormPopup',0,0, EMPTY_CLOB(),0,null,null)";
        System.out.println(convertInsertToUpdate(sql, "WINDOW_ID,WINDOW_NAME"));
    }

    public static String convertInsertToUpdate(String input, String conditionColumns) {
        String result = "UPDATE ";
        String whereClause = " WHERE ";

        Integer firstOpenParenthesisPosition = input.indexOf("(");
        Integer valuesPosition = indexOfRegex(input, "values\\s*\\(");
        String tableName = input.substring(input.indexOf("into") + 4, firstOpenParenthesisPosition).trim().replaceAll("\"", "");
        result += tableName + " SET ";

        String columns = trimString(input.substring(firstOpenParenthesisPosition, valuesPosition).trim(), "\\(|\\)");
        String[] columnsArray = columns.split("\\s*,\\s*");

        List<String> conditionColumnsList = Arrays.asList(conditionColumns.split(","));

        String values = trimString(input.substring(valuesPosition+6).trim(), "\\(|\\)");
        String[] valuesArray = values.split("\\s*,\\s*");

        for (int i = 0; i < columnsArray.length; i++) {
            String columnAndValue = columnsArray[i] + "=" + valuesArray[i] + ",";
            if(!conditionColumnsList.contains(columnsArray[i])){
                result += columnsArray[i] + "=" + valuesArray[i] + ",";
            } else {
                whereClause += columnsArray[i] + "=" + valuesArray[i]+ " and ";
            }
        }


        return trimString(result,",") + trimString(whereClause, "\\s+and\\s+");
    }

    public static Integer indexOfRegex(String s, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        return matcher.find() ? matcher.start() : -1;
    }

    public static String trimString(String s, String trim){
        String result = s.replaceAll("^" + trim, "");
        result = result.replaceAll(trim + "$","");
        return result;
    }
}
