import java.util.Optional;

public record Token(TokenType type, Optional<String> value) {
    public Token(TokenType type) {
        this(type, Optional.empty());
    }
}
