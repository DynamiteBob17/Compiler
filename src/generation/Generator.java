package generation;

import static analysis.Parser.NodeExit;

public class Generator {

    private final NodeExit root;

    public Generator(NodeExit root) {
        this.root = root;
    }

    public String generate() {
        return "global _start\n_start:\n" +
                "    mov rax, 60\n" +
                "    mov rdi, " + root.expr().integerLiteral().value().orElseThrow() + "\n" +
                "    syscall";
    }

}
