package ru.ifmo.rain.Zhevtyak.walk;

import java.io.*;

public class Walk {

    public static void main(String[] args) {
        if (args == null || args.length < 2) {
            System.out.println("Usage: java Walk <input> <output>");
            return;
        } else if (args[0] == null || args[1] == null) {
            System.out.println("Error: input and output files should be different from null");
            return;
        }
        try (BufferedReader inputReader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "UTF-8"))) {
            try (BufferedWriter outputWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), "UTF-8"))) {
                String str;
                while ((str = inputReader.readLine()) != null) {
                    File f = new File(str);
                    int h = 0x811c9dc5;
                    try (InputStream fileReader = new FileInputStream(f)) {
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
                    }
                    try {
                        outputWriter.write(String.format("%08x %s%n", h, str));
                    } catch (IOException e){
                        System.out.println("Error while writing file " + str);
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("Incorrect path of output file " + args[1]);
            } catch (SecurityException e){
                System.out.println("Error: security violation in output file " + args[1]);
            } catch (IOException e) {
                System.out.println("Error while opening file " + args[1]);
            }
        } catch (FileNotFoundException e) {
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