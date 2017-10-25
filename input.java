
class Token {
    /**
     * this is a block note
     */
    public static void test(int state) {
        switch (state) {
            case 7:
                // TODO: 2017/10/24 this is a line note
                break;
            case 8:
                if (c == '/') {
                    analyse(9, word.add(c), index + 1);
                } else {
                    tokens.add(new Token(0, word.toString(), "Unable to Recognize!"));
                    analyse(0, new Word(), index);
                }
                break;
        }
    }
}
