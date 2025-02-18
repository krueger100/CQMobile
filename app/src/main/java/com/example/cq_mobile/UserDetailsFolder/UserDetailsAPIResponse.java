package com.example.cq_mobile.UserDetailsFolder;



import java.util.List;

public class UserDetailsAPIResponse {
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private List<UseDetails> user;

        public List<UseDetails> getUser() {
            return user;
        }

        public void setUser(List<UseDetails> user) {
            this.user = user;
        }
    }
}
