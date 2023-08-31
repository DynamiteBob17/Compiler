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

    public static void main(String[] args) throws IOException, InterruptedException {
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

        String fileName = file.getName().split("\\.")[0];
        FileWriter fileWriter = new FileWriter(fileName + ".asm");
        fileWriter.write(asm);
        fileWriter.close();
        System.out.printf("Successfully created assembly file \'%s\'!\n", fileName + ".asm");

        String[] argsCmd1 = new String[] {"nasm", "-felf64", fileName + ".asm"};
        String[] argsCmd2 = new String[] {"ld", fileName + ".o", "-o", fileName};
        Process proc1 = new ProcessBuilder(argsCmd1).start();
        proc1.waitFor();
        proc1.destroy();
        System.out.printf("Successfully created binary file \'%s\'!\n", fileName + ".o");
        Process proc2 = new ProcessBuilder(argsCmd2).start();
        proc2.waitFor();
        proc2.destroy();
        System.out.printf("Successfully created executable file \'%s\'!\n", fileName);
    }

}