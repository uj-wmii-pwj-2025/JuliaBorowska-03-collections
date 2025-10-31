package uj.wmii.pwj.collections;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.ListIterator;

public class BrainfuckInterpreter implements Brainfuck {
    private final ListIterator<Character> programIt;

    private final int[] stack;
    private int stackPos;

    private final PrintStream outStream;
    private final InputStream inStream;

    public BrainfuckInterpreter(String program, PrintStream out, InputStream in, int stackSize) {
        LinkedList<Character> programList = new LinkedList<>();
        for (char c : program.toCharArray()) {
            programList.add(c);
        }
        this.programIt = programList.listIterator();

        this.outStream = out;
        this.inStream = in;

        this.stack = new int[stackSize];
        this.stackPos = 0;
    }

    void jumpForward() {
        int opened = 1;
        while (opened>0) {
            Character curr = programIt.next();
            if (curr=='[') opened++;
            if (curr==']') opened--;
        }
    }

    void jumpBack() {
        int closed = 1;
        programIt.previous();
        while (closed>0) {
            Character curr = programIt.previous();
            if (curr==']') closed++;
            if (curr=='[') closed--;
        }
    }

    @Override
    public void execute() {
        while (programIt.hasNext()) {
            Character curr = programIt.next();
            switch (curr) {
                case '>' :
                    stackPos++;
                    break;

                case '<' :
                    stackPos--;
                    break;

                case '+' :
                    stack[stackPos]++;
                    break;

                case '-' :
                    stack[stackPos]--;
                    break;

                case '.' :
                    outStream.print((char)stack[stackPos]);
                    break;

                case ',' :
                    try {
                        stack[stackPos] = inStream.read();
                    } catch (IOException e) {
                        outStream.print("IOException");
                    }
                    break;

                case '[' :
                    if (stack[stackPos]==0) {
                        jumpForward();
                    }
                    break;

                case ']' :
                    if (stack[stackPos]!=0) {
                        jumpBack();
                    }
                    break;
            }
        }
    }
}
