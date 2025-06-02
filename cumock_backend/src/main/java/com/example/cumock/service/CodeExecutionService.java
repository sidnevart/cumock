package com.example.cumock.service;

import com.example.cumock.dto.code_sandbox.CodeResult;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

@Service
public class CodeExecutionService {
    public CodeResult execute(String code, String input, String language) throws IOException, InterruptedException {
        File tempDir = Files.createTempDirectory("exec").toFile();
        File codeFile;
        Process process = null;

        switch (language.toLowerCase()){
            case "java":
                codeFile = new File(tempDir, "Solution.java");
                Files.writeString(codeFile.toPath(), code);
                Process compile = new ProcessBuilder("javac", codeFile.getAbsolutePath())
                        .directory(tempDir)
                        .start();
                if (compile.waitFor() != 0) {
                    String err = new String(compile.getErrorStream().readAllBytes());
                    return new CodeResult("", err, compile.exitValue(), null);
                }

                process = new ProcessBuilder("java", "Solution")
                        .directory(tempDir)
                        .redirectErrorStream(true)
                        .start();{
                }
                break;
            case "python":
                codeFile = new File(tempDir, "Solution.py");
                Files.writeString(codeFile.toPath(), code);
                process = new ProcessBuilder("python3", codeFile.getAbsolutePath())
                        .directory(tempDir)
                        .start();
                break;

            case "cpp":
                codeFile = new File(tempDir, "main.cpp");
                Files.writeString(codeFile.toPath(), code);
                Process gpp = new ProcessBuilder("g++", codeFile.getAbsolutePath(), "-o", "main")
                        .directory(tempDir)
                        .start();
                if (gpp.waitFor() != 0) {
                    String err = new String(gpp.getErrorStream().readAllBytes());
                    return new CodeResult("", err, 1, 0L);
                }
                process = new ProcessBuilder("./main")
                        .directory(tempDir)
                        .start();
                break;

            default:
                throw new IllegalArgumentException("Unsupported language: " + language);
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
            writer.write(input);
            writer.newLine();  // Добавляет \n
            writer.flush();
        }

        long start = System.currentTimeMillis();
        boolean finished = process.waitFor(5, TimeUnit.SECONDS);
        long end = System.currentTimeMillis();

        if(!finished){
            process.destroyForcibly();
            return new CodeResult("", "Time limit exceeded", 1, end - start);
        }

        String output = new String(process.getInputStream().readAllBytes());
        String err = new String(process.getErrorStream().readAllBytes());

        System.out.println("=== RAW OUTPUT ===");
        System.out.println(output);
        System.out.println("=== RAW ERR ===");
        System.out.println(err);
        return new CodeResult(output, err, process.exitValue(), end - start);
    }
}
