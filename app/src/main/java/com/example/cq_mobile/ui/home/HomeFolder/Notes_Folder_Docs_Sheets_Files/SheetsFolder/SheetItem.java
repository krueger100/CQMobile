package com.example.cq_mobile.ui.home.HomeFolder.Notes_Folder_Docs_Sheets_Files.SheetsFolder;
public class SheetItem {
    private String title;
    private String otherTitle;

    public SheetItem(String title, String otherTitle) {
        this.title = title;
        this.otherTitle = otherTitle;
    }

    public String getTitle() {
        return title;
    }

    public String getOtherTitle() {
        return otherTitle;
    }
}
