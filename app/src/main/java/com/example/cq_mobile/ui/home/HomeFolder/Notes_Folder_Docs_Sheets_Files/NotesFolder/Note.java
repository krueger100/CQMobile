package com.example.cq_mobile.ui.home.HomeFolder.Notes_Folder_Docs_Sheets_Files.NotesFolder;


public class Note {
    private int id;
    private String note;
    private String date;
    private User user;

    public int getId() {
        return id;
    }

    public String getNote() {
        return note;
    }

    public String getDate() {
        return date;
    }

    public User getUser() {
        return user;
    }

    public static class User {
        private int id;
        private String name;
        private String avatar;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getAvatar() {
            return avatar;
        }
    }
}
