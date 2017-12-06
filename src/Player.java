public class Player {
    private String id;
    private String score;
    private String chat = "";
    private long now;

    public Player(String id, String score) {
        this.id = id;
        this.score = score;
        this.chat = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getChat() { return chat;  }

    public void setChat(String chat) {
        this.chat = chat;
        this.now = System.currentTimeMillis();
    }

    public void eraseChat() {
        if(System.currentTimeMillis()-now > 1200)
            this.chat = "";
    }
}
