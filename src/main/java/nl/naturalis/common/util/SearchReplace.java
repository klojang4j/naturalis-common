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
import static nl.naturalis.common.ArrayMethods.inArray;
import static nl.naturalis.common.StringMethods.endsWith;
import static nl.naturalis.common.check.CommonChecks.directory;
import static nl.naturalis.common.check.CommonChecks.neverNull;
import static nl.naturalis.common.check.CommonChecks.yes;

/**
 * Global search-and-replace utility that can be used stand-alone (it has a main method) or from
 * within another program.
 *
 * @author Ayco Holleman
 */
public class SearchReplace {
  private static final String USAGE =
      "USAGE: SearchReplace <rootDir> <search> <replace> <fileExts...>\n\n"
          + "   rootDir       Full path to root directory for search/replace\n"
          + "   search        String to search for\n"
          + "   replace       Replacement string\n"
          + "   fileExts...   One or more file extensions to restrict search/replace to\n\n";

  public static void main(String[] args) {
    if (args.length < 4) {
      System.out.println(USAGE);
      return;
    }
    String rootDir = args[0];
    String search = args[1];
    String replace = args[2];
    String[] exts = Arrays.copyOfRange(args, 3, args.length);
    // TODO: use system properties to configure remaining options (like regexSearch)
    System.out.println();
    System.out.println("Root directory ...:  " + rootDir);
    System.out.println("Search for .......:  " + search);
    System.out.println("Replace with .....:  " + replace);
    System.out.println("File extensions ..:  " + List.of(exts));
    System.out.println();
    SearchReplace sr = new SearchReplace();
    sr.setRootDir(rootDir);
    sr.setSearch(search);
    sr.setReplace(replace);
    sr.setFileExtensions(exts);
    sr.searchAndReplace();
  }

  private String rootDir;
  private String search;
  private String replace;
  private boolean regexSearch = false;
  private boolean ignoreCase = false;
  private boolean wholeWords = true;
  private boolean excludeHidden = true;
  private String[] fileExts;
  private String[] excludeDirs;

  public SearchReplace() {}

  public void searchAndReplace(String search, String replace) {
    setSearch(search);
    setReplace(replace);
    searchAndReplace();
  }

  public void searchAndReplace() {
    Check.notNull(rootDir, "rootDir");
    Check.notNull(search, "regexSearch");
    Check.notNull(replace, "replace");
    Check.that(fileExts).is(neverNull(), "At least one file extension required");
    Path root = Path.of(rootDir);
    Check.that(root)
        .has(Path::isAbsolute, yes(), "rootDir must be absolute path")
        .has(Path::toFile, directory(), "No such directory", root);

    Pattern pattern;
    if (isRegexSearch()) {
      pattern = Pattern.compile(search);
    } else {
      search = Pattern.quote(search);
      if (wholeWords) {
        search = "\\b" + search + "\\b";
      }
      if (ignoreCase) {
        pattern = Pattern.compile(search, Pattern.CASE_INSENSITIVE);
      } else {
        pattern = Pattern.compile(search);
      }
    }
    MutableInt total = new MutableInt();
    try {
      Files.walkFileTree(
          root,
          new FileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                throws IOException {
              String fileName = file.getFileName().toString();
              if (excludeHidden && fileName.startsWith(".")) {
                return FileVisitResult.CONTINUE;
              }
              if (null != endsWith(fileName, false, fileExts)) {
                String contents = IOMethods.toString(file);
                Matcher matcher = pattern.matcher(contents);
                long cnt = matcher.results().count();
                if (cnt > 0) {
                  Path relative = root.relativize(file);
                  String es = cnt == 1 ? "" : "es";
                  System.out.println("+  " + relative + ": " + cnt + " match" + es);
                  contents = matcher.replaceAll(replace);
                  total.ipp();
                  Files.writeString(file, contents, StandardOpenOption.TRUNCATE_EXISTING);
                }
              }
              return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                throws IOException {
              String dirname = dir.getFileName().toString();
              if (excludeHidden && dirname.startsWith(".")) {
                return FileVisitResult.SKIP_SUBTREE;
              } else if (excludeDirs != null && inArray(dirname, excludeDirs)) {
                return FileVisitResult.SKIP_SUBTREE;
              }
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
    System.out.println("Number of changed files:  " + total);
  }

  public String getRootDir() {
    return rootDir;
  }

  public void setRootDir(String rootDir) {
    this.rootDir = rootDir;
  }

  public String getSearch() {
    return search;
  }

  public void setSearch(String search) {
    this.search = search;
  }

  public String getReplace() {
    return replace;
  }

  public void setReplace(String replace) {
    this.replace = replace;
  }

  public boolean isRegexSearch() {
    return regexSearch;
  }

  public void setRegexSearch(boolean regexSearch) {
    this.regexSearch = regexSearch;
  }

  public boolean isIgnoreCase() {
    return ignoreCase;
  }

  public void setIgnoreCase(boolean ignoreCase) {
    this.ignoreCase = ignoreCase;
  }

  public boolean isWholeWords() {
    return wholeWords;
  }

  public void setWholeWords(boolean wholeWords) {
    this.wholeWords = wholeWords;
  }

  public boolean isExcludeHidden() {
    return excludeHidden;
  }

  public void setExcludeHidden(boolean excludeHidden) {
    this.excludeHidden = excludeHidden;
  }

  public String[] getFileExtensions() {
    return fileExts;
  }

  public void setFileExtensions(String... fileExts) {
    this.fileExts = fileExts;
  }

  public String[] getExcludeDirs() {
    return excludeDirs;
  }

  public void setExcludeDirs(String... excludeDirs) {
    this.excludeDirs = excludeDirs;
  }
}
