package no.clueless.emulation;

import no.clueless.emulation.gui.*;
import no.clueless.emulation.impl.BusImpl;
import no.clueless.emulation.impl.apu.Apu2A03Impl;
import no.clueless.emulation.impl.cartridge.CartridgeImpl;
import no.clueless.emulation.impl.controller.NESControllerImpl;
import no.clueless.emulation.impl.cpu.Cpu6502Impl;
import no.clueless.emulation.impl.cpu.CpuHistory;
import no.clueless.emulation.impl.ppu.NESPalette;
import no.clueless.emulation.impl.ppu.Ppu2C02Impl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;

public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private static Cartridge loadCartridge(String filename) {
        try (var is = Application.class.getClassLoader().getResourceAsStream(filename)) {
            if (is == null) {
                throw new IllegalStateException("%s does not exist".formatted(filename));
            }
            var data = is.readAllBytes();
            return new CartridgeImpl(data);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load cartridge: " + filename, e);
        }
    }

    public static void main(String[] args) throws IOException {
        var filename = "Super Mario Bros. (Japan, USA).nes";
        //var       filename = "1.Branch_Basics.nes";
        //var       filename = "2.Backward_Branch.nes";
        //var       filename = "3.Forward_Branch.nes";
        //var       filename = "cpu_dummy_reads.nes";
        //var       filename = "cpu_dummy_writes_ppumem.nes";
        //var       filename = "ppu_vbl_nmi.nes";
        var cartridge = loadCartridge(filename);

        var cpuHistory = new CpuHistory();
        var cpu        = new Cpu6502Impl(cpuHistory, false);
        var controller = new NESControllerImpl();
        var gamePanel  = new GamePanel();
        var ppu        = new Ppu2C02Impl(gamePanel);
        var apu        = new Apu2A03Impl();
        var nes        = new BusImpl(cpu, ppu, apu, controller, null);
        nes.insertCartridge(cartridge);
        nes.reset();

        gamePanel.setKeyListener(new KeyListener() {
            private int setOrClear(int original, int bitmask, boolean isPressed) {
                return isPressed ? original | bitmask : original & ~bitmask;
            }

            private void handleKeyEvent(KeyEvent e, boolean isPressed) {
                var controller1 = nes.getController1();

                var button = switch (e.getKeyCode()) {
                    case KeyEvent.VK_SPACE -> NESControllerImpl.Button.SELECT;
                    case KeyEvent.VK_ENTER -> NESControllerImpl.Button.START;
                    case KeyEvent.VK_UP -> NESControllerImpl.Button.UP;
                    case KeyEvent.VK_DOWN -> NESControllerImpl.Button.DOWN;
                    case KeyEvent.VK_LEFT -> NESControllerImpl.Button.LEFT;
                    case KeyEvent.VK_RIGHT -> NESControllerImpl.Button.RIGHT;
                    case KeyEvent.VK_A -> NESControllerImpl.Button.A;
                    case KeyEvent.VK_B -> NESControllerImpl.Button.B;
                    case KeyEvent.VK_R -> {
                        nes.reset();
                        yield null;
                    }
                    default -> null;
                };

                if (button == null) {
                    return;
                }

                var newValue = setOrClear(controller1, button.getBitmask(), isPressed);
                nes.setController1(newValue);
            }

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyEvent(e, true);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                handleKeyEvent(e, false);
            }
        });

        var gameLoop = new GameLoop(nes, gamePanel);
        var cpuPanel = new CpuPanel(cpu);
        var ppuPanel = new PpuPanel(ppu);
        var paletteViewPanel = new PaletteViewPanel(new NESPalette(), null);

        var gameWindow = new GameWindow(gamePanel, cpuPanel, ppuPanel, paletteViewPanel);

        gameLoop.setFpsListener(event -> gameWindow.setFps(event.getFps()));
        gameLoop.setCpuMhzListener(event -> {
            gameWindow.setCpuMhz(event.getCpuMhz());
            cpuPanel.updateStatus();
            ppuPanel.updateStatus();

            log.debug(String.join("\n", cpuHistory.dumpInstructions()));
        });

        gameLoop.start();
    }
}
