package net.Snow.Smithing;

import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.vars.VarManager;



public enum SmithingTypes {


    DISABLED("Disabled", -1, -1, -1, new SmithingItem[]{
            SmithingItem.DISABLED
    }),

    BRONZE_BAR("Bronze Bar", 1490, 1, 2424884, new SmithingItem[]{
            SmithingItem.DISABLED,

            new SmithingItem("Bronze full Helm", 8333, 1155, 1, 2424935),
            new SmithingItem("Bronze med Helm", 8333, 1139, 3, 2424935),
            new SmithingItem("Bronze platelegs", 8333, 1075, 5, 2424935),
            new SmithingItem("Bronze plateskirt", 8333, 1087, 7, 2424935),
            new SmithingItem("Bronze platebody", 8333, 1117, 9, 2424935),
            new SmithingItem("Bronze chainbody", 8333, 1103, 11, 2424935),
            new SmithingItem("Bronze square shield", 8333, 1173, 13, 2424935),
            new SmithingItem("Bronze kiteshield", 8333, 1189, 15, 2424935),
            new SmithingItem("Bronze armoured boots", 8333, 4119, 17, 2424935),
            new SmithingItem("Bronze gauntlets", 8333, 45431, 19, 2424935),
            new SmithingItem("Bronze dagger", 8333, 1205, 1, 2424946),
            new SmithingItem("Bronze mace", 8333, 1422, 3, 2424946),
            new SmithingItem("Bronze sword", 8333, 1277, 5, 2424946),
            new SmithingItem("Bronze scimitar", 8333, 1321, 7, 2424946),
            new SmithingItem("Bronze longsword", 8333, 1291, 9, 2424946),
            new SmithingItem("Bronze warhammer", 8333, 1337, 11, 2424946),
            new SmithingItem("Bronze battleaxe", 8333, 1375, 13, 2424946),
            new SmithingItem("Bronze claws", 8333, 3095, 15, 2424946),
            new SmithingItem("Bronze 2h sword", 8333, 1307, 17, 2424946),
            new SmithingItem("Bronze hatchet", 8333, 1351, 19, 2424946),
    }),
    IRON_BAR("Iron Bar", 1491, 3, 2424884, new SmithingItem[]{
            SmithingItem.DISABLED,
            new SmithingItem("Iron full Helm", 8333, 1153, 1, 2424935),
            new SmithingItem("Iron med Helm", 8333, 1137, 3, 2424935),
            new SmithingItem("Iron platelegs", 8333, 1067, 5, 2424935),
            new SmithingItem("Iron plateskirt", 8333, 1081, 7, 2424935),
            new SmithingItem("Iron platebody", 8333, 1115, 9, 2424935),
            new SmithingItem("Iron chainbody", 8333, 1101, 11, 2424935),
            new SmithingItem("Iron square shield", 8333, 1175, 13, 2424935),
            new SmithingItem("Iron kiteshield", 8333, 1191, 15, 2424935),
            new SmithingItem("Iron armoured boots", 8333, 4121, 17, 2424935),
            new SmithingItem("Iron gauntlets", 8333, 45945, 19, 2424935),
            new SmithingItem("Iron dagger", 8333, 1203, 1, 2424946),
            new SmithingItem("Iron mace", 8333, 1420, 3, 2424946),
            new SmithingItem("Iron sword", 8333, 1279, 5, 2424946),
            new SmithingItem("Iron scimitar", 8333, 1323, 7, 2424946),
            new SmithingItem("Iron longsword", 8333, 1293, 9, 2424946),
            new SmithingItem("Iron warhammer", 8333, 1335, 11, 2424946),
            new SmithingItem("Iron battleaxe", 8333, 1363, 13, 2424946),
            new SmithingItem("Iron claws", 8333, 3096, 15, 2424946),
            new SmithingItem("Iron 2h sword", 8333, 1309, 17, 2424946),
            new SmithingItem("Iron hatchet", 8333, 1349, 19, 2424946),
            new SmithingItem("Iron arrowheads", 8333, 40, 1, 2424968),
            new SmithingItem("Iron dart tips", 8333, 820, 3, 2424968),
            new SmithingItem("Iron bolts (unf)", 8333, 9377, 5, 2424968),
            // Add iron items here with their respective component parameters
    }),
    RUNE_BAR("Rune Bar", 1495, 11, 2424884, new SmithingItem[]{
            SmithingItem.DISABLED,
            new SmithingItem("Rune full Helm", 8333, 45539, 1, 2424935),
            new SmithingItem("Rune med Helm", 8333, 45540, 3, 2424935),
            new SmithingItem("Rune platelegs", 8333, 45541, 5, 2424935),
            new SmithingItem("Rune plateskirt", 8333, 45542, 7, 2424935),
            new SmithingItem("Rune platebody", 8333, 45543, 9, 2424935),
            new SmithingItem("Rune chainbody", 8333, 45544, 11, 2424935),
            new SmithingItem("Rune square shield", 8333, 45545, 13, 2424935),
            new SmithingItem("Rune kiteshield", 8333, 45546, 15, 2424935),
            new SmithingItem("Rune armoured boots", 8333, 45547, 17, 2424935),
            new SmithingItem("Rune gauntlets", 8333, 46929, 19, 2424935),
            new SmithingItem("Rune dagger", 8333, 45522, 1, 2424946),
            new SmithingItem("Rune mace", 8333, 45524, 3, 2424946),
            new SmithingItem("Rune sword", 8333, 45526, 5, 2424946),
            new SmithingItem("Rune scimitar", 8333, 45528, 7, 2424946),
            new SmithingItem("Rune longsword", 8333, 45530, 9, 2424946),
            new SmithingItem("Rune warhammer", 8333, 45532, 11, 2424946),
            new SmithingItem("Rune battleaxe", 8333, 45534, 13, 2424946),
            new SmithingItem("Rune claws", 8333, 455536, 15, 2424946),
            new SmithingItem("Rune 2h sword", 8333, 45538, 17, 2424946),
            new SmithingItem("Rune hatchet", 8333, 1359, 19, 2424946),
            new SmithingItem("Rune full Helm + 1", 8333, 45539, 1, 2424935),
            new SmithingItem("Rune med Helm + 1", 8333, 45540, 3, 2424935),
            new SmithingItem("Rune platelegs + 1", 8333, 45541, 5, 2424935),
            new SmithingItem("Rune plateskirt + 1", 8333, 45542, 7, 2424935),
            new SmithingItem("Rune platebody + 1", 8333, 45543, 9, 2424935),
            new SmithingItem("Rune chainbody + 1", 8333, 45544, 11, 2424935),
            new SmithingItem("Rune square shield + 1", 8333, 45545, 13, 2424935),
            new SmithingItem("Rune kiteshield + 1", 8333, 45546, 15, 2424935),
            new SmithingItem("Rune armoured boots + 1", 8333, 45547, 17, 2424935),
            new SmithingItem("Rune arrows", 8333, 44, 1, 2424968),
    }),

    ELDER_RUNE_BAR("Elder Rune Bar", 1499, 11, 2424884, new SmithingItem[]{
            // Add elder rune items here with their respective component parameters
    }),
    PRIMAL_BAR("Primal Bar", 13879, 13, 2424884, new SmithingItem[]{
            // Add primal items here with their respective component parameters
    });

    private final String name;
    private final int varbit;
    private final int barComponentParameter;  // The second parameter for bar component
    private final int barComponentId;         // The third parameter for bar component
    private final SmithingItem[] items;

    SmithingTypes(String name, int varbit, int barComponentParameter, int barComponentId, SmithingItem[] items) {
        this.name = name;
        this.varbit = varbit;
        this.barComponentParameter = barComponentParameter;
        this.barComponentId = barComponentId;
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public int getVarbit() {
        return varbit;
    }

    public int getBarComponentParameter() {
        return barComponentParameter;
    }

    public int getBarComponentId() {
        return barComponentId;
    }

    public SmithingItem[] getItems() {
        return items;
    }

    public boolean isEnabled() {
        return VarManager.getVarValue(VarDomainType.PLAYER, varbit) == 1;
    }

    @Override
    public String toString() {
        return name; // This will be displayed in the ImGui combo box
    }

    public static class SmithingItem {
        public static final SmithingItem DISABLED = new SmithingItem("Disabled", -1, -1, -1, -1);

        private final String itemName;
        private final int varbit;
        private final int itemId;
        private final int itemComponentParameter;  // The second parameter for item component
        private final int itemComponentId;         // The third parameter for item component

        public SmithingItem(String itemName, int varbit, int itemId, int itemComponentParameter, int itemComponentId) {
            this.itemName = itemName;
            this.varbit = varbit;
            this.itemId = itemId;
            this.itemComponentParameter = itemComponentParameter;
            this.itemComponentId = itemComponentId;
        }

        public String getItemName() {
            return itemName;
        }

        public int getVarbit() {
            return varbit;
        }

        public int getItemId() {
            return itemId;
        }

        public int getItemComponentParameter() {
            return itemComponentParameter;
        }

        public int getItemComponentId() {
            return itemComponentId;
        }

        public boolean isEnabled() {
            return VarManager.getVarValue(VarDomainType.PLAYER, varbit) == itemId;
        }

        @Override
        public String toString() {
            return itemName; // This will be displayed in the ImGui combo box
        }
    }
}