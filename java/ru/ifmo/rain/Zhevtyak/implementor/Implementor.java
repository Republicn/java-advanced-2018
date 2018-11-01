package ru.ifmo.rain.Zhevtyak.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

/**
 * @author Nina Zhevtyak
 */

/**
 * Provides implementation for interfaces {@link Impler} and {@link JarImpler}.
 */
public class Implementor implements Impler, JarImpler {

    /**
     * Creates a string of method parameters with names like param[i], where i is a number of current parameter.
     * If no parameters found, returns an empty line.
     *
     * @param method method, which parameters are needed.
     * @return a string of parameters.
     */
    private String param(Method method) {
        StringBuilder ourStr = new StringBuilder();
        Class<?>[] paramTypes = method.getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            ourStr.append(paramTypes[i].getCanonicalName()).append(" ").append("param").append(Integer.toString(i + 1)).append(", ");
        }
        if (ourStr.length() > 0) {
            ourStr = new StringBuilder(ourStr.substring(0, ourStr.length() - 2));
        }
        return ourStr.toString();
    }

    /**
     * Creates a string of <tt>method</tt> annotations.
     * If no annotations found, returns an empty line.
     *
     * @param method current method of implementing file
     * @return a string of annotations.
     */
    private String annotations(Method method) {
        StringBuilder str = new StringBuilder();
        Annotation[] annotations = method.getAnnotations();
        for (Annotation ann : annotations) {
            str.append(ann.toString()).append("\n");
        }
        return str.toString();
    }

    /**
     * Returns what should be basically returned by a given <tt>method</tt>: an empty line,
     * if the <code>method.getReturnType()</code> is {@link java.lang.Void}, <code>false</code>,
     * if it's {@link java.lang.Boolean}, <tt>0</tt>, if it's primitive, and <tt>null</tt> otherwise.
     *
     * @param method current method of implementing file.
     * @return a return type as a string.
     */
    private String whatReturn(Method method) {
        Class<?> cl = method.getReturnType();
        if (cl.equals(void.class)) {
            return "";
        } else if (cl.equals(boolean.class)) {
            return " false";
        } else {
            return cl.isPrimitive() ? " 0" : " null";
        }
    }

    /**
     * Creates a string of throwing exceptions of current <tt>method</tt>. If <tt>method</tt>
     * throws no exceptions, returns 0.
     *
     * @param method current method of implementing file.
     * @return a string of exceptions.
     */

    private String exceptions(Method method) {
        StringBuilder str = new StringBuilder();
        Class<?>[] exc = method.getExceptionTypes();
        if (exc.length != 0) {
            str.append("throws ");
        }
        for (Class<?> cl: exc) {
            str.append(cl.getCanonicalName()).append(", ");
        }
        if (str.length() != 0) {
            str.deleteCharAt(str.length() - 2);
        }
        return str.toString();
    }

    /**
     * Creates and prints a full method by using {@link Writer}.
     *
     * @param method current method of implementing file.
     * @param outputWriter output destination.
     */
    private void createMethod(Method method, Writer outputWriter) {
        try {
            outputWriter.write("\t"
                    + annotations(method)
                    + Modifier.toString(method.getModifiers() & ~(Modifier.ABSTRACT | Modifier.TRANSIENT)) + " "
                    + method.getReturnType().getCanonicalName() + " " + method.getName()
                    + "(" + param(method) + ") " + exceptions(method) + "{\n");
            outputWriter.write("\t\treturn" + whatReturn(method) + ";\n\t}\n\n");

        } catch (IOException e) {
            System.err.println("Exception while working with file");
        }
    }

    /**
     * Produces code implementing class or interface specified by provided <tt>token</tt>.
     * <p>
     * Generated class full name should be same as full name of the type token with <tt>Impl</tt> suffix
     * added. Generated source code should be placed in the correct subdirectory of the specified
     * <tt>root</tt> directory and have correct file name. For example, the implementation of the
     * interface {@link java.util.List} should go to <tt>$root/java/util/ListImpl.java</tt>
     *
     * @param token type token to create implementation for.
     * @param root root directory.
     * @throws info.kgeorgiy.java.advanced.implementor.ImplerException when implementation cannot be generated.
     */
    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {
        Path path = getFullPath(token, root);
        if (!token.isInterface()) {
            throw new ImplerException("Interface expected, but didn't found");
        }
        try {
            Files.createDirectories(path.getParent());
            Writer outputWriter = new UnicodeFilter(Files.newBufferedWriter(path, Charset.forName("UTF-8")));
            if (token.getPackage() != null) {
                outputWriter.write("package " + token.getPackage().getName() + ";\n\n");
            }
            outputWriter.write("public class " + token.getSimpleName() + "Impl implements " + token.getSimpleName() + " {\n\n");
            Method[] methods = token.getMethods();
            for (Method method : methods) {
                createMethod(method, outputWriter);
            }
            outputWriter.write("}");
            outputWriter.close();
        } catch (IOException e) {
            System.err.println("Exception while working with file");
        }
    }

    /**
     * Creates a new Path to the file by concatenating the given root and a root to a token.
     * The file, which Path will be created, should have the same name as full name of the type token with <tt>Impl</tt>
     * suffix and extension ".java" added.
     *
     * @param token type token to create a {@link java.nio.file.Path}for.
     * @param root  a root where a new file should be created.
     * @return a generated Path.
     */
    private Path getFullPath(Class<?> token, Path root) {
        return root.resolve(token.getCanonicalName().replace(".", "/") + "Impl.java");
    }

    /**
     * Entry point of the program for command line arguments.
     * <p>
     * Usage:
     * <ul>
     * <li>{@code java -jar Implementor.jar -jar class-to-implement path-to-jar}</li>
     * <li>{@code java -jar Implementor.jar class-to-implement path-to-class}</li>
     * </ul>
     *
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        if (args.length < 1 || args[0] == null) {
            System.err.println("Usage: Interface Path");
            return;
        }
        try {
            if (args[0].equals("-jar")) {
                if (args.length != 3 || args[1] == null || args[2] == null) {
                    System.err.println("Usage: -jar Interface file.jar");
                    return;
                }
                new Implementor().implementJar(Class.forName(args[1]), Paths.get(args[2]));
            } else {
                if (args.length != 2 || args[1] == null) {
                    System.err.println("Usage: Interface, Path");
                }
                new Implementor().implement(Class.forName(args[0]), Paths.get(args[1]));
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Interface wasn't found");
        } catch (ImplerException e) {
            System.err.println("Impler Exception " + e.getMessage());
        }
    }

    /**
     * Produces <tt>.jar</tt> file implementing class or interface specified by provided <tt>token</tt>.
     * <p>
     * Generated class full name should be same as full name of the type token with <tt>Impl</tt> suffix
     * added.
     *
     * @param token type token to create implementation for.
     * @param jarFile target <tt>.jar</tt> file.
     * @throws info.kgeorgiy.java.advanced.implementor.ImplerException when implementation cannot be generated.
     */
    @Override
    public void implementJar(Class<?> token, Path jarFile) throws ImplerException {
        Path root = Paths.get("temp");
        implement(token, root);
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        if (javaCompiler == null) {
            throw new ImplerException("Compilation exception: compiler wasn't found");
        }
        int res = javaCompiler.run(null, null, null,
                getFullPath(token, root).toString(),
                "-encoding", "utf-8");
        if (res != 0) {
            throw new ImplerException("Error implementing " + token + "\n Compiler exit code" + res);
        }
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        try (JarOutputStream jarWriter = new JarOutputStream(Files.newOutputStream(jarFile), manifest)) {
            String className = token.getCanonicalName().replace(".", "/") + "Impl.class";
            jarWriter.putNextEntry(new ZipEntry(className));
            Files.copy(root.resolve(className), jarWriter);
        } catch (IOException e) {
            throw new ImplerException(e.getMessage());
        }
    }

    /**
     *  Class for correct output.
     */
    private class UnicodeFilter extends FilterWriter {

        /**
         * Constructor for our class.
         *
         * @param out <tt>Writer</tt>, whose parent constructor is created.
         */
        UnicodeFilter(Writer out) {
            super(out);
        }

        /**
         * Writes a symbol in a correct charset.
         *
         * @param c number of the symbol.
         * @throws IOException when <tt>error</tt> while writing is occured.
         */
        @Override
        public void write(int c) throws IOException {
            if (c > 127) {
                out.write(String.format("\\u%04X", c));
            } else {
                out.write(c);
            }
        }

        /**
         * Writes a substring of a given <tt>String</tt>.
         *
         * @param string basic String to write.
         * @param off position of first symbol to write.
         * @param len length of string to write.
         * @throws IOException when <tt>error</tt> while writing is occured.
         */
        @Override
        public void write(String string, int off, int len) throws IOException {
            for (char c : string.substring(off, off + len).toCharArray()) {
                write(c);
            }
        }

    }

}
