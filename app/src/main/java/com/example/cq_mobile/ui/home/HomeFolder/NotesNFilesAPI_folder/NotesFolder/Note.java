package com.example.cq_mobile.ui.home.HomeFolder.NotesNFilesAPI_folder.NotesFolder;

public class Note {
    private int id;
    private String note;
    private String date;
    private User user;

    public Note(int id, String note, String date, User user) {
        this.id = id;
        this.note = note;
        this.date = date;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static class User {
        private int id;
        private String name;
        private String avatar;

        public User(int id, String name, String avatar) {
            this.id = id;
            this.name = name;
            this.avatar = avatar;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }
}
