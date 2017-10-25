import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by a297 on 17/10/25.
 */
public class Analyser {

    private static final String FAIL_TO_ANALYSE = "FAIL TO ANALYSE";

    private static final String[] RESERVED_WORDS = {
            "class", "this", "new",
            "public", "protected", "private",
            "static", "void", "main",
            "if", "else", "for", "while", "do",
            "switch", "case", "break",
            "int", "double", "char", "boolean", "String",
            "try", "catch",  "return"

    };

    private static final String[] OPERATORS = {
            "+", "-", "*", "/", "=", "!",
            ">", "<", "==", ">=", "<=", "+=", "-=", "*+", "/=", "!=",
            "&&", "||", "&", "|"
    };

    private static final String[] PUNCUATIONS = {
            "{", "}", ";", ",", ".", "(", ")", "[", "]", ":", "\""
    };

    // token 分类
    private final String ID             = "ID          ";// ID
    private final String NUMBER         = "Number      ";// 数字
    private final String NOTE           = "Note        ";// 行注释
    private final String BLOCK_NOTE     = "BlockNote   ";// 块注释
    private final String RESERVED       = "ReservedWord";// 保留字
    private final String OPERATOR       = "Operator    ";// 操作符
    private final String PUNCTUATION    = "Punctuation ";// 标点
    private final String OTHER          = "Other       ";// 其他

    private String inputPath;
    private String outputPath;

    // 代码输入
    private char[] inputCode = new char[1000];
    private int inputCodeLength;

    // 记号流输出
    private List<Token> outputToken = new ArrayList<>();


    Analyser(String inputPath, String outputPath) {
        this.inputPath = inputPath;
        this.outputPath = outputPath;
    }

    public void start(){
        readInput();
        analyse(0, new Word(), 0);
        saveOutput();
    }

    /**
     * 将分析所得记号流保存到输出文件
     */
    private void saveOutput() {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outputPath))));
            for (Token eachToke : outputToken) {
                if (eachToke.isValid()) {
                    bw.write(eachToke.toString());
                    bw.newLine();
                }
            }
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("===== FAIL TO WRITE OUTPUT FILE =====");
        }

    }

    private void analyse(int state, Word word, int index) {
        /**
         *
         * 当前状态-->遇到-->下一个状态？ or 完成一个token，下一个token？
         */
        if (index >= inputCodeLength) {
            return;
        } else {
         char c = inputCode[index];
            switch (state) {
                case 0:
                    if (c == '-') {
                        analyse(1, word.add(c), index + 1);
                    } else if (isNumber(c)) {
                        analyse(2, word.add(c), index + 1);
                    } else if (isLetter(c)) {
                        analyse(4, word.add(c), index + 1);
                    } else if (c == '/') {
                        analyse(8, word.add(c), index + 1);
                    } else {
                        analyse(15, word.add(c), index + 1);
                    }
                    break;
                case 1:
                    if (isNumber(c)) {
                        analyse(2, word.add(c), index + 1);
                    } else {
                        outputToken.add(new Token(0, word.add(c).toString(), FAIL_TO_ANALYSE));
                        analyse(0, new Word(), index);
                    }
                    break;
                case 2:
                    if (isNumber(c)) {
                        analyse(2, word.add(c), index + 1);
                    } else if (c == '.') {
                        analyse(3, word.add(c), index + 1);
                    } else {
                        analyse(11, word.add(c), index + 1);
                    }
                    break;
                case 3:
                    if (isNumber(c)) {
                        analyse(10, word.add(c), index + 1);
                    } else {
                        outputToken.add(new Token(0, word.add(c).toString(), FAIL_TO_ANALYSE));
                        analyse(0, new Word(), index);
                    }
                    break;
                case 4:
                    if (isLetter(c) || isNumber(c)) {
                        analyse(4, word.add(c), index + 1);
                    } else {
                        analyse(12, word.add(c), index + 1);
                    }
                    break;
                case 5:
                    if (c == '*') {
                        if (inputCode[index + 1] == '/') {// TODO: 17/10/25 考虑溢出？
                            analyse(13, word.add(c).add(inputCode[index + 1]), index + 2);
                        } else {
                            analyse(6, word.add(c), index + 1);
                        }
                    } else if (c == '/') {
                        analyse(7, word.add(c), index + 1);
                    } else {
                        analyse(5, word.add(c), index + 1);
                    }
                    break;
                case 6:
                    if (c == '/') {
                        analyse(13, word.add(c), index + 1);
                    } else {
                        analyse(6, word.add(c), index + 1);
                    }
                    break;
                case 7:
                    if (c != '*') {
                        analyse(7, word.add(c), index + 1);
                    } else {
                        if (inputCode[index + 1] == '/') {// TODO: 17/10/25 溢出？
                            analyse(13, word.add(c).add(inputCode[index + 1]), index + 2);
                        } else {
                            outputToken.add(new Token(0, word.add(c).toString(), FAIL_TO_ANALYSE));
                            analyse(0, new Word(), index);
                        }
                    }
                    break;
                case 8:
                    if (c == '*') {
                        analyse(5, word.add(c), index + 1);
                    } else if (c == '/') {
                        analyse(9, word.add(c), index + 1);
                    } else {
                        outputToken.add(new Token(0, word.add(c).toString(), FAIL_TO_ANALYSE));
                        analyse(0, new Word(), index);
                    }
                    break;
                case 9:
                    if (c == '\n') {
                        analyse(14, word.add(c), index + 1);
                    } else {
                        analyse(9, word.add(c), index + 1);
                    }
                    break;
                case 10:
                    if (isNumber(c)) {
                        analyse(10, word.add(c), index + 1);
                    } else {
                        analyse(11, word.add(c), index + 1);
                    }
                    break;
                case 11:// NUMBER
                    outputToken.add(new Token(NUMBER, word.toString()));
                    analyse(0, new Word(), index);
                    // TODO: 17/10/25 above id index? or index+1?
                    break;
                case 12:// RESERVED? ID？
                    if (isReservedWord(word.toString())) {
                        outputToken.add(new Token(RESERVED, word.toString()));
                    } else {
                        outputToken.add(new Token(ID, word.toString()));
                    }
                    analyse(0, new Word(), index);
                    break;
                case 13:// BLOCK NOTE
                    outputToken.add(new Token(BLOCK_NOTE, word.toString()));
                    analyse(0, new Word(), index);
                    break;
                case 14:// NOTE
                    outputToken.add(new Token(NOTE, word.toString()));
                    analyse(0, new Word(), index);
                    break;
                case 15:// OPERATOR
                    if (isOperator(word.toString())) {
                        outputToken.add(new Token(OPERATOR, word.toString()));
                    } else if (isPunctuation(word.toString())) {
                        outputToken.add(new Token(PUNCTUATION, word.toString()));
                    } else {
                        outputToken.add(new Token(OTHER, word.toString()));
                    }
                    analyse(0, new Word(), index);
                    break;
            }
        }
    }

    /**
     * 读取文件输入，放入char数组
     */
    private void readInput() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputPath))));
            inputCodeLength = 0;
            String eachLine;
            while (( eachLine = br.readLine()) != null) {
                char[] temp = eachLine.toCharArray();
                for (char eachChar: temp) {
                    inputCode[inputCodeLength++] = eachChar;
                }
                inputCode[inputCodeLength++] = '\n';
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("===== FAIL TO READ INPUT FILE =====");
        }
    }

    private boolean isLetter(char c) {
        return ((c >= 'a' && c <= 'z') || (c >= 'A' && c <='Z'));
    }

    private boolean isNumber(char c) {
        return (c >= '0' && c <= '9');
    }

    private boolean isOperator(String s) {
        for (String operator : OPERATORS) {
            if (s.equals(operator)) {
                return true;
            }
        }
        return false;
    }

    private boolean isReservedWord(String s) {
        for (String reservedWord : RESERVED_WORDS) {
            if (s.equals(reservedWord)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPunctuation(String s) {
        for (String punctuaiton: PUNCUATIONS) {
            if (s.equals(punctuaiton)) {
                return true;
            }
        }
        return false;
    }
}
