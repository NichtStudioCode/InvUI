package xyz.xenondevs.inventoryaccess.version;

import xyz.xenondevs.inventoryaccess.util.VersionUtils;

public enum InventoryAccessRevision {
    
    // this order is required
    R25("r25", "1.21.9"),
    R24("r24", "1.21.6"),
    R23("r23", "1.21.5"),
    R22("r22", "1.21.4"),
    R21("r21", "1.21.2"),
    R20("r20", "1.21.0"),
    R19("r19", "1.20.5"),
    R18("r18", "1.20.3"),
    R17("r17", "1.20.2"),
    R16("r16", "1.20.0"),
    R15("r15", "1.19.4"),
    R14("r14", "1.19.3"),
    R13("r13", "1.19.1"),
    R12("r12", "1.19.0"),
    R11("r11", "1.18.2"),
    R10("r10", "1.18.0"),
    R9("r9", "1.17.1"),
    R8("r8", "1.17.0"),
    R7("r7", "1.16.4"),
    R6("r6", "1.16.2"),
    R5("r5", "1.16.0"),
    R4("r4", "1.15.0"),
    R3("r3", "1.14.4"),
    R2("r2", "1.14.1"),
    R1("r1", "1.14.0");
    
    public static final InventoryAccessRevision REQUIRED_REVISION = getRequiredRevision();
    
    private final String packageName;
    private final int[] since;
    
    InventoryAccessRevision(String packageName, String since) {
        this.packageName = packageName;
        this.since = VersionUtils.toMajorMinorPatch(since);
    }
    
    private static InventoryAccessRevision getRequiredRevision() {
        for (InventoryAccessRevision revision : values())
            if (VersionUtils.isServerHigherOrEqual(revision.getSince())) return revision;
        
        throw new UnsupportedOperationException("Your version of Minecraft is not supported by InventoryAccess");
    }
    
    public String getPackageName() {
        return packageName;
    }
    
    public int[] getSince() {
        return since;
    }
    
}
