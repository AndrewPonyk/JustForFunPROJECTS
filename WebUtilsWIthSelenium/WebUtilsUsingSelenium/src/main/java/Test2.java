import java.text.SimpleDateFormat;
import java.util.Optional;

public class Test2 {
    public static void main(String[] args) {
        String test = null;
        String str_audit_date_started = Optional.ofNullable(test).orElse("a");
        System.out.println(str_audit_date_started);
    }
}
