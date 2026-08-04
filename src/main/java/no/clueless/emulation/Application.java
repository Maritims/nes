package no.clueless.emulation;

import no.clueless.emulation.gui.CpuPanel;
import no.clueless.emulation.gui.GamePanel;
import no.clueless.emulation.gui.GameWindow;
import no.clueless.emulation.gui.PpuPanel;
import no.clueless.emulation.impl.BusImpl;
import no.clueless.emulation.impl.apu.Apu2A03Impl;
import no.clueless.emulation.impl.cartridge.CartridgeImpl;
import no.clueless.emulation.impl.controller.NESControllerImpl;
import no.clueless.emulation.impl.cpu.Cpu6502Impl;
import no.clueless.emulation.impl.ppu.Ppu2C02Impl;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;

public class Application {
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

        var cpu         = new Cpu6502Impl(false);
        var controller  = new NESControllerImpl();
        var frameBuffer = new GamePanel();
        var ppu         = new Ppu2C02Impl(frameBuffer);
        var apu         = new Apu2A03Impl();
        var nes         = new BusImpl(cpu, ppu, apu, controller, null);
        nes.insertCartridge(cartridge);
        nes.reset();

        var gameLoop = new GameLoop(nes);
        var cpuPanel = new CpuPanel(cpu);
        var ppuPanel = new PpuPanel(ppu);

        var gameWindow = new GameWindow(frameBuffer, cpuPanel, ppuPanel, new KeyListener() {
            private void handleKeyEvent(KeyEvent e, boolean isPressed) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SPACE -> nes.getController1().setButtonState(NESControllerImpl.Button.SELECT, isPressed);
                    case KeyEvent.VK_ENTER -> nes.getController1().setButtonState(NESControllerImpl.Button.START, isPressed);
                    case KeyEvent.VK_UP -> nes.getController1().setButtonState(NESControllerImpl.Button.UP, isPressed);
                    case KeyEvent.VK_DOWN -> nes.getController1().setButtonState(NESControllerImpl.Button.DOWN, isPressed);
                    case KeyEvent.VK_LEFT -> nes.getController1().setButtonState(NESControllerImpl.Button.LEFT, isPressed);
                    case KeyEvent.VK_RIGHT -> nes.getController1().setButtonState(NESControllerImpl.Button.RIGHT, isPressed);
                    case KeyEvent.VK_A -> nes.getController1().setButtonState(NESControllerImpl.Button.A, isPressed);
                    case KeyEvent.VK_B -> nes.getController1().setButtonState(NESControllerImpl.Button.B, isPressed);
                }
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

        gameLoop.setFpsListener(event -> gameWindow.setFps(event.getFps()));
        gameLoop.setCpuMhzListener(event -> {
            gameWindow.setCpuMhz(event.getCpuMhz());
            cpuPanel.updateStatus();
            ppuPanel.updateStatus();
        });

        gameLoop.start();
    }
}
