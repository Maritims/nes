package no.clueless.emulation.impl.cpu;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class CpuHistory {
    private static final int           MAX_SIZE = 100;
    private final        Deque<String> history  = new ArrayDeque<>(MAX_SIZE);

    public synchronized void logInstruction(String instruction) {
        if (history.size() >= MAX_SIZE) {
            history.pollFirst();
        }
        history.addLast(instruction);
    }

    public synchronized List<String> dumpInstructions() {
        return new ArrayList<>(history);
    }
}
