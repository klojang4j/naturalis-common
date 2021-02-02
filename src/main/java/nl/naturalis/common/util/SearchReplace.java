package nl.naturalis.common.util;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.common.IOMethods;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.StringMethods.endsWith;
import static nl.naturalis.common.check.CommonChecks.directory;
import static nl.naturalis.common.check.CommonChecks.noneNull;
import static nl.naturalis.common.check.CommonChecks.yes;

public class SearchReplace {
  private static final String USAGE =
      "USAGE: SearchReplace <rootDir> <search> <replace> <fileExts...>\n\n"
          + "   rootDir       Full path to root directory for search/replace\n"
          + "   search        Regular expression\n"
          + "   replace       Replacement string\n"
          + "   fileExts...   One or more file extensions to restrict search/replace to\n\n"
          + "   NOTE: Prefix the regular expression with \"(?i)\" to execute a\n"
          + "   case-insensitive search/replace\n";

  public static void main(String[] args) {
    if (args.length < 4) {
      System.out.println(USAGE);
      return;
    }
    String rootDir = args[0];
    String search = args[1];
    String replace = args[2];
    String[] exts = Arrays.copyOfRange(args, 3, args.length);
    SearchReplace sr = new SearchReplace();
    sr.searchReplace(rootDir, search, replace, exts);
  }

  public SearchReplace() {}

  public void searchReplace(String rootDir, String search, String replace, String... fileExts) {
    Check.notNull(rootDir, "rootDir");
    Check.notNull(search, "regexSearch");
    Check.notNull(replace, "replace");
    Check.that(fileExts).is(noneNull(), "At least one file extension required");
    Path root = Path.of(rootDir);
    Check.that(root)
        .has(Path::isAbsolute, yes(), "rootDir must be absolute path")
        .has(Path::toFile, directory(), "No such directory", root);
    Pattern pattern = Pattern.compile(search);
    MutableInt total = new MutableInt();
    System.out.println();
    System.out.println("Root directory ...:  " + root);
    System.out.println("Search for .......:  \"" + search + "\"");
    System.out.println("Replace with .....:  \"" + replace + "\"");
    System.out.println("File extensions ..:  " + List.of(fileExts));
    System.out.println();
    try {
      Files.walkFileTree(
          root,
          new FileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                throws IOException {
              if (null != endsWith(file.getFileName().toString(), false, fileExts)) {
                String contents = IOMethods.toString(file);
                Matcher matcher = pattern.matcher(contents);
                long cnt = matcher.results().count();
                if (cnt > 0) {
                  String es = cnt == 1 ? "" : "es";
                  System.out.println("+  " + file.getFileName() + ": " + cnt + " match" + es);
                  contents = matcher.replaceAll(replace);
                  total.plusplus();
                  Files.writeString(file, contents, StandardOpenOption.TRUNCATE_EXISTING);
                }
              }
              return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                throws IOException {
              if (dir.getFileName().toString().startsWith(".")) {
                return FileVisitResult.SKIP_SUBTREE;
              }
              System.out.println(dir);
              return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
              throw exc;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                throws IOException {
              return FileVisitResult.CONTINUE;
            }
          });
    } catch (IOException e) {
      throw ExceptionMethods.uncheck(e);
    }
    System.out.println();
    System.out.println("Number of changed files:  " + total);
    System.out.println();
  }
}
