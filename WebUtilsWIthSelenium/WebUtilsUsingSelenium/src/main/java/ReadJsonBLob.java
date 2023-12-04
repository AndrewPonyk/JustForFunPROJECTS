import java.io.File;
import java.io.IOException;

public class ReadJsonBLob {
    public static void main(String[] args) throws IOException {
        byte[] bytes = org.apache.commons.io.FileUtils.readFileToByteArray(new File("C:\\Users\\Andrii_Ponyk\\Downloads\\SP_DATA"));
        String data = new String(bytes);
        System.out.println(data);
    }
}
