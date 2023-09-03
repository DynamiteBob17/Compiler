package analysis;

import token.Token;
import token.TokenType;

import java.util.List;
import java.util.Optional;

public class Parser extends Consumer<Token> {

    public record NodeExpr(Token integerLiteral) {}
    public record NodeExit(NodeExpr expr) {}

    public Parser(List<Token> items) {
        super(items);
    }

    private Optional<NodeExpr> parseExpr() {
        if (peek().isPresent() && peek().get().type() == TokenType.INTEGER_LITERAL) {
            return Optional.of(new NodeExpr(consume()));
        } else {
            return Optional.empty();
        }
    }

    public Optional<NodeExit> parse() {
        index = 0;
        Optional<NodeExit> nodeExit = Optional.empty();
        Optional<Token> tokenOptional;

        while ((tokenOptional = peek()).isPresent()) {
            Token token = tokenOptional.get();

            if (token.type() == TokenType.EXIT) {
                consume();
                NodeExpr nodeExpr = parseExpr().orElseThrow(() -> new RuntimeException("Invalid expression for 'exit' token."));
                nodeExit = Optional.of(new NodeExit(nodeExpr));

                if ((tokenOptional = peek()).isPresent()) {
                    if (tokenOptional.get().type() != TokenType.SEMICOLON) {
                        throw new RuntimeException("Missing semicolon.");
                    } else {
                        consume();
                    }
                } else {
                    throw new RuntimeException("Missing semicolon.");
                }
            } else {
                consume();
            }
        }

        return nodeExit;
    }

}
