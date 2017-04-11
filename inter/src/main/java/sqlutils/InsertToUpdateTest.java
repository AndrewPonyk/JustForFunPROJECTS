package sqlutils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by andrii on 08.04.17.
 */

public class InsertToUpdateTest {
    String sql = "Insert into \"setup_windows\" (WINDOW_ID,WINDOW_NAME,WINDOW_SCRIPT,WINDOW_PARAMS,MENU_ID,HELP_TOPIC_ID,HELP_FILE_NAME,WINDOW_LOCK_TYPE_ID,WINDOW_WIDTH,WINDOW_HEIGHT,SEC_LANG_NAME,WINDOW_SCRIPT_ID,CONFIG_SCRIPT_SUPPORT,ENABLE_CUSTOM_HELP,CUSTOM_HELP,STAGED_EDIT_SUPPORT,FEAT_UID,EXTERNAL_HELP_FILE) values ('show_128','Classes Identity Bridge Data',null,null,null,null,null,1,'75%','65%','זיהוי סיווגי נתוני גשרים','GeneralMultiFormPopup',0,0, EMPTY_CLOB(),0,null,null)";
    String expectedSql = "UPDATE setup_windows SET WINDOW_NAME='Classes Identity Bridge Data',WINDOW_SCRIPT=null,WINDOW_PARAMS=null,MENU_ID=null,HELP_TOPIC_ID=null,HELP_FILE_NAME=null,WINDOW_LOCK_TYPE_ID=1,WINDOW_WIDTH='75%',WINDOW_HEIGHT='65%',SEC_LANG_NAME='זיהוי סיווגי נתוני גשרים',WINDOW_SCRIPT_ID='GeneralMultiFormPopup',CONFIG_SCRIPT_SUPPORT=0,ENABLE_CUSTOM_HELP=0,CUSTOM_HELP=EMPTY_CLOB,STAGED_EDIT_SUPPORT=0,FEAT_UID=null,EXTERNAL_HELP_FILE=null WHERE WINDOW_ID='show_128'";

    @Test
    public void testConvertFromInsertToUpdate(){
        assertEquals(expectedSql, InsertToUpdateApp.convertInsertToUpdate(sql, "WINDOW_ID"));
    }

    @Test
    public void testConvertFromInsertToUpdateWith2Conditions(){
        assertEquals(expectedSql, InsertToUpdateApp.convertInsertToUpdate(sql, "WINDOW_ID,WINDOW_NAME"));
    }


}
