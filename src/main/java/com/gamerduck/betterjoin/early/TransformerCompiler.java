package com.gamerduck.betterjoin.early;

import java.io.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class TransformerCompiler {

    public void createJarFromCompiledClass(String outputJarPath) throws Exception {
        try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(outputJarPath))) {
            String baseClassPath = "com/gamerduck/betterjoin/early/BetterJoinEarlyPlugin";
            copyClassAndInnerClasses(jos, baseClassPath);
            copyPackageResources(jos, "org/objectweb/asm");
            addServiceFile(jos, "com.gamerduck.betterjoin.early.BetterJoinEarlyPlugin");
            System.out.println("JAR created successfully at: " + outputJarPath);
        }
    }

    private void copyClassAndInnerClasses(JarOutputStream targetJar, String baseClassPath) throws IOException {
        String classLocation = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        File location = new File(classLocation);
        if (location.isDirectory()) {
            copyClassesFromDirectory(targetJar, location, baseClassPath);
        } else if (location.isFile() && location.getName().endsWith(".jar")) {
            copyClassesFromJar(targetJar, location, baseClassPath);
        }
    }

    private void copyClassesFromDirectory(JarOutputStream targetJar, File classesDir, String baseClassPath) throws IOException {
        String baseFileName = baseClassPath.substring(baseClassPath.lastIndexOf('/') + 1);
        File packageDir = new File(classesDir, baseClassPath.substring(0, baseClassPath.lastIndexOf('/')));
        if (!packageDir.exists()) {
            throw new RuntimeException("Package directory not found: " + packageDir.getAbsolutePath());
        }
        File[] files = packageDir.listFiles((dir, name) ->
                name.equals(baseFileName + ".class") || name.startsWith(baseFileName + "$")
        );
        if (files == null || files.length == 0) {
            throw new RuntimeException("No class files found for: " + baseClassPath);
        }
        for (File file : files) {
            String entryName = baseClassPath.substring(0, baseClassPath.lastIndexOf('/') + 1) + file.getName();
            JarEntry entry = new JarEntry(entryName);
            targetJar.putNextEntry(entry);

            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    targetJar.write(buffer, 0, bytesRead);
                }
            }

            targetJar.closeEntry();
            System.out.println("Copied class: " + entryName);
        }
    }

    private void copyClassesFromJar(JarOutputStream targetJar, File sourceJarFile, String baseClassPath) throws IOException {
        try (JarFile sourceJar = new JarFile(sourceJarFile)) {
            java.util.Enumeration<JarEntry> entries = sourceJar.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();
                if (entryName.equals(baseClassPath + ".class") ||
                        entryName.matches(baseClassPath.replace("/", "\\/") + "\\$.*\\.class")) {

                    JarEntry newEntry = new JarEntry(entryName);
                    targetJar.putNextEntry(newEntry);

                    try (InputStream is = sourceJar.getInputStream(entry)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            targetJar.write(buffer, 0, bytesRead);
                        }
                    }

                    targetJar.closeEntry();
                    System.out.println("Copied class: " + entryName);
                }
            }
        }
    }

    private void copyPackageResources(JarOutputStream targetJar, String packagePath) throws IOException {
        String classLocation = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        File location = new File(classLocation);
        if (location.isDirectory()) {
            copyFromDirectory(targetJar, location, packagePath);
        } else if (location.isFile() && location.getName().endsWith(".jar")) {
            copyFromJar(targetJar, location, packagePath);
        }
    }

    private void copyFromDirectory(JarOutputStream targetJar, File classesDir, String packagePath) throws IOException {
        File packageDir = new File(classesDir, packagePath);
        if (!packageDir.exists() || !packageDir.isDirectory()) {
            System.out.println("Warning: Package directory not found: " + packageDir.getAbsolutePath());
            return;
        }
        copyDirectoryToJar(targetJar, classesDir, packageDir);
    }

    private void copyDirectoryToJar(JarOutputStream targetJar, File baseDir, File currentDir) throws IOException {
        File[] files = currentDir.listFiles();
        if (files == null) return;
        for (File file : files) {
            String relativePath = baseDir.toPath().relativize(file.toPath()).toString().replace('\\', '/');

            if (file.isDirectory()) {
                copyDirectoryToJar(targetJar, baseDir, file);
            } else {
                JarEntry entry = new JarEntry(relativePath);
                targetJar.putNextEntry(entry);

                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        targetJar.write(buffer, 0, bytesRead);
                    }
                }

                targetJar.closeEntry();
            }
        }
    }

    private void copyFromJar(JarOutputStream targetJar, File sourceJarFile, String packagePath) throws IOException {
        try (JarFile sourceJar = new JarFile(sourceJarFile)) {
            java.util.Enumeration<JarEntry> entries = sourceJar.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();

                if (entryName.startsWith(packagePath)) {
                    JarEntry newEntry = new JarEntry(entryName);
                    targetJar.putNextEntry(newEntry);

                    if (!entry.isDirectory()) {
                        try (InputStream is = sourceJar.getInputStream(entry)) {
                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = is.read(buffer)) != -1) {
                                targetJar.write(buffer, 0, bytesRead);
                            }
                        }
                    }

                    targetJar.closeEntry();
                }
            }
        }
    }

    private void addServiceFile(JarOutputStream jos, String serviceImplementation) throws IOException {
        JarEntry servicesEntry = new JarEntry("META-INF/services/com.hypixel.hytale.plugin.early.ClassTransformer");
        jos.putNextEntry(servicesEntry);
        jos.write(serviceImplementation.getBytes());
        jos.closeEntry();
    }
}