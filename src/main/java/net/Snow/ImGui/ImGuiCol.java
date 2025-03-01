package net.Snow.ImGui;

import net.botwithus.rs3.imgui.ImGui;

public class ImGuiCol {
    public static final int  Text = 0;
    public static final int  TextDisabled = 1;
    public static final int  WindowBg = 2;
    public static final int  ChildBg = 3;
    public static final int  PopupBg = 4;
    public static final int  Border = 5;
    public static final int  BorderShadow = 6;
    public static final int  FrameBg = 7;
    public static final int  FrameBgHovered = 8;
    public static final int  FrameBgActive = 9;
    public static final int  TitleBg = 10;
    public static final int  TitleBgActive = 11;
    public static final int  TitleBgCollapsed = 12;
    public static final int  MenuBarBg = 13;
    public static final int  ScrollbarBg = 14;
    public static final int  ScrollbarGrab = 15;
    public static final int  ScrollbarGrabHovered = 16;
    public static final int  ScrollbarGrabActive = 17;
    public static final int  CheckMark = 18;
    public static final int  SliderGrab = 19;
    public static final int  SliderGrabActive = 20;
    public static final int  Button = 21;
    public static final int  ButtonHovered = 22;
    public static final int  ButtonActive = 23;
    public static final int  Header = 24;
    public static final int  HeaderHovered = 25;
    public static final int  HeaderActive = 26;
    public static final int  Separator = 27;
    public static final int  SeparatorHovered = 28;
    public static final int  SeparatorActive = 29;
    public static final int  ResizeGrip = 30;
    public static final int  ResizeGripHovered = 31;
    public static final int  ResizeGripActive = 32;
    public static final int  Tab = 33;
    public static final int  TabHovered = 34;
    public static final int  TabActive = 35;
    public static final int  TabUnfocused = 36;
    public static final int  TabUnfocusedActive = 37;
    public static final int  DockingPreview = 38;
    public static final int  DockingEmptyBg = 39;
    public static final int  PlotLines = 40;
    public static final int  PlotLinesHovered = 41;
    public static final int  PlotHistogram = 42;
    public static final int  PlotHistogramHovered = 43;
    public static final int  TableHeaderBg = 44;
    public static final int  TableBorderStrong = 45;
    public static final int  TableBorderLight = 46;
    public static final int  TableRowBg = 47;
    public static final int  TableRowBgAlt = 48;
    public static final int  TextSelectedBg = 49;
    public static final int  DragDropTarget = 50;
    public static final int  NavHighlight = 51;
    public static final int  NavWindowingHighlight = 52;
    public static final int  NavWindowingDimBg = 53;
    public static final int  ModalWindowDimBg = 54;

    public static void setStyleColor(int colorEnum, int r, int g, int b, int a) {
        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));
        a = Math.max(0, Math.min(255, a));

        float floatColorR = r / 255.0f;
        float floatColorG = g / 255.0f;
        float floatColorB = b / 255.0f;
        float floatColorA = a / 255.0f;

        ImGui.PushStyleColor(colorEnum, floatColorR, floatColorG, floatColorB, floatColorA);
    }

    // Helper method to render centered text with a fixed window width
    public static void renderCenteredText(String text, int r, int g, int b, int a) {
        // Fixed window width
        float windowWidth = 280.0F;

        // Calculate the text width for centering
        float textWidth = ImGui.CalcTextSize(text).getX();

        // Calculate the position to center the text
        float positionX = (windowWidth - textWidth) / 2;

        // Set the style color for the text
        setStyleColor(ImGuiCol.Text, r, g, b, a);

        // Set cursor position and display the text centered
        ImGui.SetCursorPosX(positionX);
        ImGui.Text(text);

        // Pop the style color (reset to default)
        ImGui.PopStyleColor(1);
    }


}
