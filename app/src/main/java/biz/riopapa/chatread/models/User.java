package biz.riopapa.chatread.models;
public class User {
    private String name;
    private String job;
    private int image;

    public User(String name, String job, int image) {
        this.name = name;
        this.job = job;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
