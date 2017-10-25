/**
 * Created by a297 on 17/10/25.
 */
public class Token {
    private String type;
    private String code;
    private String error;

    Token(String type, String code) {
        this.type = type;
        this.code = code;
        this.error = null;
    }

    Token(int type, String code, String error) {
        this.type = "UNKNOWN     ";
        this.code = code;
        this.error = error;
    }
    // 忽略 空格、换行
    public boolean isValid() {return (!code.equals(" ") && !code.equals("\n"));}

    @Override
    public String toString(){
        return "Token: { " +
                "type = '" + type + "', " +
                "code = '" + code + "'" +
                (error != null ?(", " + "error = " + error) : "" ) + " }";
    }
}
