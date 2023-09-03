package analysis;

import token.Token;
import token.TokenType;

import java.util.*;
import java.util.function.Function;

public class Tokenizer extends Consumer<Character> {

    private final Map<String, TokenType> tokenDictionary;

    public Tokenizer(String src) {
        super(src.trim()
                .replaceAll("[\\n\\r]", "")
                .replaceAll("\\s{2,}", " ")
                .chars()
                .mapToObj(e -> (char)e)
                .toList()
        );
        tokenDictionary = createTokenDictionary();
    }

    public static Map<String, TokenType> createTokenDictionary() {
        Map<String, TokenType> tokenDictionary = new HashMap<>();

        tokenDictionary.put("exit", TokenType.EXIT);
        tokenDictionary.put(";", TokenType.SEMICOLON);

        return tokenDictionary;
    }

    private void feedBuffer(
            Function<Character, Boolean> function,
            StringBuilder buffer) {
        buffer.append(consume());

        Optional<Character> opt;
        while ((opt = peek()).isPresent() && function.apply(opt.get())) {
            buffer.append(consume());
        }
    }

    public List<Token> tokenize() {
        index = 0;
        StringBuilder buffer = new StringBuilder();
        List<Token> tokens = new ArrayList<>();
        Optional<Character> characterOptional;

        while ((characterOptional = peek()).isPresent()) {
            char c = characterOptional.get();

            if (Character.isWhitespace(c)) {
                consume();
            } else if (Character.isAlphabetic(c)) {
                feedBuffer(ch -> Character.isAlphabetic(ch) || Character.isDigit(ch), buffer);
                tokens.add(new Token(tokenDictionary.get(buffer.toString())));
            } else if (Character.isDigit(c)) {
                feedBuffer(Character::isDigit, buffer);
                tokens.add(new Token(TokenType.INTEGER_LITERAL, Optional.of(buffer.toString())));
            } else if (c == ';') {
                consume();
                tokens.add(new Token(TokenType.SEMICOLON));
            } else {
                throw new RuntimeException("Token " + characterOptional.get() + " is not allowed.");
            }

            if (tokens.get(tokens.size() - 1).type() == null) {
                throw new RuntimeException("Unrecognized token " + buffer);
            }

            if (!buffer.isEmpty()) {
                buffer.delete(0, buffer.length());
            }
        }

        return tokens;
    }

}
