import java.util.*;
import java.util.function.Function;

public class TokenFactory {

    public static Map<String, TokenType> createTokenDictionary() {
        Map<String, TokenType> tokenDictionary = new HashMap<>();

        tokenDictionary.put("return", TokenType.RETURN);
        tokenDictionary.put(";", TokenType.SEMICOLON);

        return tokenDictionary;
    }

    private static void feedBuffer(
            Function<Character, Boolean> function,
            StringBuilder buffer,
            char[] chars,
            int idx) {
        buffer.append(chars[idx++]);

        while (idx < chars.length && function.apply(chars[idx])) {
            buffer.append(chars[idx++]);
        }
    }

    public static List<Token> tokenize(String str) {
        String truncatedString = str.trim()
                .replaceAll("[\\n\\r]", "")
                .replaceAll("\\s{2,}", " ");
        char[] chars = truncatedString.toCharArray();
        StringBuilder buffer = new StringBuilder();
        List<Token> tokens = new ArrayList<>();
        Map<String, TokenType> tokenDictionary = createTokenDictionary();

        for (int i = 0; i < chars.length; ++i) {
            char c = chars[i];

            if (Character.isWhitespace(c)) {
                continue;
            } else if (Character.isAlphabetic(c)) {
                feedBuffer(ch -> Character.isAlphabetic(ch) || Character.isDigit(ch), buffer, chars, i);
                tokens.add(new Token(tokenDictionary.get(buffer.toString())));
            } else if (Character.isDigit(c)) {
                feedBuffer(Character::isDigit, buffer, chars, i);
                tokens.add(new Token(TokenType.INTEGER_LITERAL, Optional.of(buffer.toString())));
            } else if (c == ';') {
                tokens.add(new Token(TokenType.SEMICOLON));
                continue;
            } else {
                throw new RuntimeException("Character " + c + " is not allowed.");
            }

            if (tokens.get(tokens.size() - 1).type() == null) {
                throw new RuntimeException("Unrecognized token " + buffer);
            }

            if (!buffer.isEmpty()) {
                i += buffer.length() - 1;
                buffer.delete(0, buffer.length());
            }
        }

        return tokens;
    }

    public static String tokensToAsm(List<Token> tokens) {
        StringBuilder asm = new StringBuilder("global _start\nstart:\n");

        for (Iterator<Token> it = tokens.iterator(); it.hasNext(); /*...*/) {
            Token token = it.next();

            if (token.type() == TokenType.RETURN) {
                if (it.hasNext()) {
                    Token next = it.next();

                    if (next.type() == TokenType.INTEGER_LITERAL) {
                        String value = next.value().orElseThrow(() -> new RuntimeException("???"));

                        if (it.hasNext() && it.next().type() == TokenType.SEMICOLON) {
                            asm.append("    mov rax, 60\n");
                            asm.append("    mov rdi, ").append(value).append("\n");
                            asm.append("    syscall");
                        }
                    }
                }
            }
        }

        return asm.toString();
    }

}
