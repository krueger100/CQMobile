package com.example.cq_mobile.ui.home.HomeFolder.TaskFolder;

// Data model for the API response
class Task {
    private Data data;

    public Data getData() {
        return data;
    }

    class Data {
        private int id;
        private String title;
        private String description;
        private String priority;
        private String status;
        private String start_date;
        private String end_date;
        private Assignee[] assignees;
        private boolean is_checked;

        // Getters
        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getPriority() {
            return priority;
        }

        public String getStatus() {
            return status;
        }

        public String getStartDate() {
            return start_date;
        }

        public String getEndDate() {
            return end_date;
        }

        public Assignee[] getAssignees() {
            return assignees;
        }

        public boolean isChecked() {
            return is_checked;
        }

        class Assignee {
            private int id;
            private int user_id;
            private String name;
            private String avatar;
            private String colleague_group_id;
            // Getters
            public int getId() {
                return id;
            }

            public int getUserId() {
                return user_id;
            }

            public String getName() {
                return name;
            }

            public String getAvatar() {
                return avatar;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getUser_id() {
                return user_id;
            }

            public void setUser_id(int user_id) {
                this.user_id = user_id;
            }

            public void setName(String name) {
                this.name = name;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            public String getColleague_group_id() {
                return colleague_group_id;
            }

            public void setColleague_group_id(String colleague_group_id) {
                this.colleague_group_id = colleague_group_id;
            }
        }

    }
}


