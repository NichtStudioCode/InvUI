package de.studiocode.inventoryaccess.version;

import de.studiocode.inventoryaccess.util.VersionUtils;

public enum InventoryAccessRevision {
    
    // this order is required
    R12("r12", "1.19.3"),
    R11("r11", "1.19.1"),
    R10("r10", "1.19.0"),
    R9("r9", "1.18.2"),
    R8("r8", "1.18.0"),
    R7("r7", "1.17.1"),
    R6("r6", "1.17.0"),
    R5("r5", "1.16.4"),
    R4("r4", "1.16.2"),
    R3("r3", "1.16.0"),
    R2("r2", "1.15.0"),
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
