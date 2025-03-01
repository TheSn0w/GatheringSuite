package net.Snow.Archaeology.Mysteries;

import java.util.ArrayList;
import java.util.List;

public class Mysteries {

    public static boolean doMysteries = false;

    public static int selectedMysteryIndex = 0;
    public static List<Mystery> mysteryList = new ArrayList<>();

    static {
        mysteryList.add(new Mystery("Breaking the Seal", 12));
        mysteryList.add(new Mystery("Mystery of the Bermuda Triangle", 20));
        mysteryList.add(new Mystery("Mystery of the Loch Ness Monster", 15));
        // Add more mysteries as needed
    }
}
