package no.clueless.emulation.impl.ppu;

public class NameTableManager {
    private final NameTable[] nameTables = new NameTable[]{new NameTable(), new NameTable()};
    private       Mirroring   mode       = Mirroring.VERTICAL;

    public void setMirroring(Mirroring mode) {
        this.mode = mode;
    }

    private NameTable resolvePhysicalTable(int tableIndex) {
        return switch (mode) {
            case VERTICAL -> (tableIndex == 0 || tableIndex == 2) ? nameTables[0] : nameTables[1];
            case HORIZONTAL -> (tableIndex == 0 || tableIndex == 1) ? nameTables[0] : nameTables[1];
            case SINGLE_SCREEN_LOWER -> nameTables[0];
            case SINGLE_SCREEN_UPPER -> nameTables[1];
        };
    }

    public int read(int address) {
        var calculation = NameTableCalculation.calculate(address);
        var table       = resolvePhysicalTable(calculation.tableIndex());
        return table.read(calculation.offset());
    }

    public void write(int address, int data) {
        var calculation = NameTableCalculation.calculate(address);
        var table       = resolvePhysicalTable(calculation.tableIndex());
        table.write(calculation.offset(), data);
    }

    record NameTableCalculation(int relativeAddress, int tableIndex, int offset) {
        public static NameTableCalculation calculate(int address) {
            var relativeAddress = (address - 0x2000) % 0x1000;
            var tableIndex      = relativeAddress / 0x0400;
            var offset          = relativeAddress % 0x0400;
            return new NameTableCalculation(relativeAddress, tableIndex, offset);
        }
    }
}
