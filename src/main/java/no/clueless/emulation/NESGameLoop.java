package no.clueless.emulation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NESGameLoop implements Runnable {
    public static final  double MASTER_CLOCK_FREQUENCY_MHZ = 21.477272;
    public static final  double CLOCK_DIVISOR              = 12;
    public static final  double CPU_CLOCK_FREQUENCY_MHZ    = MASTER_CLOCK_FREQUENCY_MHZ / CLOCK_DIVISOR;
    public static final  double SECONDS_PER_NANOSECOND     = 1.0 / 1_000_000_000.0;
    private static final Logger log                        = LoggerFactory.getLogger(NESGameLoop.class);

    private final Bus     nes;
    private       boolean isRunning;
    private       Thread  gameThread;

    public NESGameLoop(Bus nes) {
        this.nes = nes;
    }

    public synchronized void start() {
        if (isRunning) {
            return;
        }

        log.info("Starting NESGameLoop");

        isRunning  = true;
        gameThread = new Thread(this);

        gameThread.start();
    }

    public synchronized void stop() {
        if (!isRunning) {
            return;
        }

        isRunning = false;

        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void run() {
        log.info("Running NESGameLoop");

        var lastTime          = System.nanoTime();
        var accumulatedCycles = 0.0;

        // Verification variables
        long totalCyclesExecuted = 0;
        long lastLogTime         = lastTime;
        long secondCounter       = 0;

        while (isRunning) {
            var now           = System.nanoTime();
            var elapsedTimeNs = now - lastTime;
            lastTime = now;

            accumulatedCycles += (elapsedTimeNs * CPU_CLOCK_FREQUENCY_MHZ) / 1000.0;

            var cyclesExecuted        = 0;
            var maxCyclesPerIteration = 30_000;

            while (accumulatedCycles >= 1.0 && cyclesExecuted < maxCyclesPerIteration) {
                nes.clock();
                accumulatedCycles -= 1.0;
                cyclesExecuted++;
                totalCyclesExecuted++;

                if (nes.getPpu().isFrameComplete()) {
                    nes.getPpu().getFrameBuffer().render();
                    nes.getPpu().setFrameComplete(false);
                }
            }

            if (accumulatedCycles >= maxCyclesPerIteration) {
                accumulatedCycles = 0.0;
            }

            // --- Verification & Metrics Logging ---
            secondCounter += (now - lastLogTime);
            lastLogTime = now;

            // Print statistics every 1 second (1,000,000,000 nanoseconds)
            if (secondCounter >= 1_000_000_000L) {
                var mhz = ((double) totalCyclesExecuted / secondCounter) * 1000.0; // cycles/ns = MHz
                totalCyclesExecuted = 0;
                secondCounter %= 1_000_000_000L; // retain remainder for accuracy
                nes.getPpu().getFrameBuffer().setStatus(0.0, mhz);
            }
            // --------------------------------------

            if (cyclesExecuted == 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error(e.getMessage(), e);
                    break;
                }
            }
        }

        log.info("No longer running");
    }
}
