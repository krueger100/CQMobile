package com.example.cq_mobile.ui.chat.ColleagueFolder;


import java.util.List;
public class ColleagueAPIResponse {
    private ColleagueAPIResponse.Data data;


    public ColleagueAPIResponse.Data getData() {
        return data;
    }

    public void setData(ColleagueAPIResponse.Data data) {
        this.data = data;
    }

    public static class Data {
        private List<ColleagueAPIItem> contacts;

        public List<ColleagueAPIItem> getContacts() {
            return contacts;
        }

        public void setContacts(List<ColleagueAPIItem> contacts) {
            this.contacts = contacts;
        }
    }

}
