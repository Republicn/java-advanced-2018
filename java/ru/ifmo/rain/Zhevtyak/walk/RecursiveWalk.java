package ru.ifmo.rain.Zhevtyak.walk;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class RecursiveWalk {

    private static int hash(String str) {
        int h = 0x811c9dc5;
        try (InputStream fileReader = new FileInputStream(str)) {
            int len;
            byte[] bytes = new byte[4096];
            try {
                while ((len = fileReader.read(bytes)) >= 0) {
                    for (int i = 0; i < len; i++) {
                        h = (h * 0x01000193) ^ (bytes[i] & 0xff);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error while reading file " + str);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Incorrect path of file " + str);
            h = 0;
        } catch (SecurityException e) {
            System.out.println("Error: security violation in file " + str);
            h = 0;
        } catch (IOException e) {
            System.out.println();
        }
        return h;
    }

    private static String res(int hash, String file) {
        return String.format("%08x %s%n", hash, file);
    }

    public static void main(String[] args) {
        if (args == null || args.length < 2) {
            System.out.println("Usage: java Walk <input> <output>");
            return;
        } else if (args[0] == null || args[1] == null) {
            System.out.println("Error: input and output files should be different from null");
            return;
        }
        try (BufferedReader inputReader = Files.newBufferedReader(Paths.get(args[0]))) {
            try (BufferedWriter outputWriter = Files.newBufferedWriter(Paths.get(args[1]))) {
                String str;
                while ((str = inputReader.readLine()) != null) {
                    try {
                        Path path = Paths.get(str);
                        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                            @Override
                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                String fileName = file.toString();
                                outputWriter.write(res(hash(fileName), fileName));
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                                outputWriter.write(res(0, file.toString()));
                                return FileVisitResult.CONTINUE;
                            }
                        });
                    } catch (InvalidPathException e) {
                        outputWriter.write(res(0, str));
                    }
                }
            } catch (InvalidPathException e) {
                System.out.println("Incorrect path of output file " + args[1]);
            } catch (SecurityException e) {
                System.out.println("Error: security violation in output file " + args[1]);
            } catch (IOException e) {
                System.out.println("Error while opening file " + args[1]);
            }
        } catch (InvalidPathException e) {
            System.out.println("Incorrect path of input file " + args[0]);
        } catch (SecurityException e) {
            System.out.println("Error: security violation in input file " + args[0]);
        } catch (UnsupportedEncodingException e) {
            System.out.println("Incorrect charset of input file " + args[0] + ". It must be UTF-8");
        } catch (IOException e) {
            System.out.println("Error while opening file " + args[0]);
        }
    }
}