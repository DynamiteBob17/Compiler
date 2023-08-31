import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class SingleFileCompiler {

    private static File getFileFromPathname(String pathname) throws IllegalArgumentException {
        File file = new File(pathname);
        String filename = file.getName();
        String[] split = filename.split("\\.");

        if (!split[split.length - 1].equals("lol")) {
            throw new IllegalArgumentException("." + split[split.length - 1] + " is an invalid file type.");
        }
        String filenameNoExtension = String.join(".", Arrays.copyOfRange(split, 0, split.length - 1));
        if (filenameNoExtension.contains(".")) {
            throw new IllegalArgumentException(filenameNoExtension + " is not a valid file name.");
        }

        return file;
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            throw new IllegalArgumentException("Please provide a relative path to the file to be compiled as an argument.");
        }
        if (args.length > 1) {
            throw new IllegalArgumentException("Too many arguments.");
        }

        File file = getFileFromPathname(args[0]);
        String contents = Files.readString(Path.of(file.getPath()));
        List<Token> tokens = TokenFactory.tokenize(contents);
        String asm = TokenFactory.tokensToAsm(tokens);

        FileWriter fileWriter = new FileWriter(file.getName().replace(".lol", ".asm"));
        fileWriter.write(asm);
        fileWriter.close();

        System.out.println("Compilation successful - generated assembly file!");
    }

}