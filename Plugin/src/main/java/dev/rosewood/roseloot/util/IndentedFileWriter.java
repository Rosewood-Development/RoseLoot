package dev.rosewood.roseloot.util;

import java.io.FileWriter;
import java.io.IOException;

public class IndentedFileWriter {

    private final FileWriter fileWriter;
    private int indentation;

    public IndentedFileWriter(FileWriter fileWriter) {
        this.fileWriter = fileWriter;
        this.indentation = 0;
    }

    /**
     * Writes a line with the current indentation, also appends a newline at the end
     *
     * @param line The line to write
     */
    public void write(String line) throws IOException {
        this.fileWriter.write(new String(new char[this.indentation]).replace('\0', ' ') + line + '\n');
    }

    public void increaseIndentation() {
        this.indentation += 2;
    }

    public void decreaseIndentation() {
        this.indentation = Math.max(0, this.indentation - 2);
    }

}
