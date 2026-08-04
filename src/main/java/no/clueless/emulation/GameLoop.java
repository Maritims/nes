package no.clueless.emulation;

import no.clueless.emulation.event.CpuMhzEvent;
import no.clueless.emulation.event.CpuMhzListener;
import no.clueless.emulation.event.FpsEvent;
import no.clueless.emulation.event.FpsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameLoop implements Runnable {
    public static final  double MASTER_CLOCK_FREQUENCY_MHZ = 21.477272;
    public static final  double CLOCK_DIVISOR              = 12;
    public static final  double CPU_CLOCK_FREQUENCY_MHZ    = MASTER_CLOCK_FREQUENCY_MHZ / CLOCK_DIVISOR;
    private static final Logger log                        = LoggerFactory.getLogger(GameLoop.class);

    private final Bus            nes;
    private       boolean        isRunning;
    private       Thread         gameThread;
    private       FpsListener    fpsListener;
    private       CpuMhzListener cpuMhzListener;

    public GameLoop(Bus nes) {
        this.nes = nes;
    }

    public void setFpsListener(FpsListener fpsListener) {
        this.fpsListener = fpsListener;
    }

    public void setCpuMhzListener(CpuMhzListener cpuMhzListener) {
        this.cpuMhzListener = cpuMhzListener;
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

    private void reportFps(double fps) {
        if (fpsListener != null) {
            fpsListener.fpsUpdated(new FpsEvent(this, fps));
        }
    }

    private void reportCpuMhz(double cpuMhz) {
        if (cpuMhzListener != null) {
            cpuMhzListener.cpuMhzUpdated(new CpuMhzEvent(this, cpuMhz));
        }
    }

    @Override
    public void run() {
        log.info("Running NESGameLoop");

        var lastTime          = System.nanoTime();
        var accumulatedCycles = 0.0;

        // Verification variables
        long totalCyclesExecuted = 0;
        long lastCpuMhzLogTime   = lastTime;
        long cpuMhzSecondCounter = 0;

        var totalFramesRendered = 0;

        while (isRunning) {
            var now           = System.nanoTime();
            var elapsedTimeNs = now - lastTime;
            lastTime = now;

            accumulatedCycles += (elapsedTimeNs * CPU_CLOCK_FREQUENCY_MHZ) / 1000.0;

            var cyclesExecuted = 0;

            while (accumulatedCycles >= 1.0) {
                nes.clock();
                accumulatedCycles -= 1.0;
                cyclesExecuted++;
                totalCyclesExecuted++;

                if (nes.getPpu().isFrameComplete()) {
                    nes.getPpu().getFrameBuffer().render();
                    nes.getPpu().setFrameComplete(false);

                    totalFramesRendered++;
                }
            }

            // region Metrics logging
            cpuMhzSecondCounter += (now - lastCpuMhzLogTime);
            lastCpuMhzLogTime = now;

            // Print statistics every 1 second (1,000,000,000 nanoseconds)
            if (cpuMhzSecondCounter >= 1_000_000_000L) {
                var mhz = ((double) totalCyclesExecuted / cpuMhzSecondCounter) * 1000.0; // cycles/ns = MHz
                totalCyclesExecuted = 0;

                cpuMhzSecondCounter %= 1_000_000_000L; // retain remainder for accuracy

                reportCpuMhz(mhz);
                reportFps(totalFramesRendered);
                totalFramesRendered = 0;
            }
            // endregion

            if (cyclesExecuted == 0) {
                try {
                    Thread.sleep(0, 100);
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
