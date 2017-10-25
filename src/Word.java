/**
 * Created by a297 on 17/10/25.
 */
public class Word {
    private static int MAX_LENGTH = 50;
    private static int INITIAL_LENGTH = 0;
    private char[] value;
    private int length;

    Word() {
        this.value = new char[MAX_LENGTH];
        this.length = INITIAL_LENGTH;
    }

    Word add(char c) {
        if (length < MAX_LENGTH) {
            value[length++] = c;
        } else {
            System.out.println("Length exceeds the maximum. Please check the length of the word.");
        }
        return this;
    }

    @Override
    public String toString() {
        return String.valueOf(value).substring(0, length);
    }

}
