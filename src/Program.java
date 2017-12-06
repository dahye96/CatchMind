import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

public class Program extends PApplet{
    private String myName ="WON-MIN";

    private int posX;
    private int posY;

    private boolean erase = false;

    private String text = "";

    private ArrayList<Point> points;

    private final ArrayList<Point> receivedPoints = new ArrayList<>();
    private ArrayList<Player> players;

    private String myAnswer = "";

    private String answer = "";

    private int tick;

    private Socket socket;
    private DataOutputStream dos;

    public static void main(String[] args) {
        PApplet.main("Program");
    }

    @Override
    public void settings() {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress("192.168.11.12", 8080));
            dos = new DataOutputStream(socket.getOutputStream());

            ReaderThread reader = new ReaderThread(socket.getInputStream(), this);
            reader.start();

            sendMessage(new Message("CONNECT", new String[]{myName} ));

            points = new ArrayList<>();



        } catch (IOException e) {
            e.printStackTrace();
        }


        this.size(1000, 600);
    }

    @Override
    public void setup() {
        this.noStroke();
        this.background(110, 181, 244);

        this.fill(255);
        this.rect(0,0, 800, 550); // 그리는 부분 600X550

    }

    @Override
    public void draw() {

        if(erase) {
            this.fill(255);
            this.rect(0,0, 800, 550); // 그리는 부분 600X550
            erase = false;
        }

        this.noStroke();
        this.fill(255);
        this.rect(300, 550, 200, 50); // 단어 나타나는 부분
        if(!myAnswer.equals("")) {
            this.fill(0);
            this.text(myAnswer, 380-myAnswer.length(), 580);
        }

        if(!answer.equals("") && tick < 90) {
            this.fill(255, 0, 0);
            this.text(answer, 380-answer.length(), 580);
        }

        this.fill(27, 146, 252);
        this.rect(800, 0, 200, 600); // 플레이어 목록
        int i = 0;
        if(players != null) {
            for (Player p : players) {
                this.fill(255, 0, 0);
                this.text(p.getChat(), 800, 30 * i + 30);
                this.fill(0);
                this.text(p.getId() + "    " + p.getScore(), 850, 30 * i + 30);
                i++;
                p.eraseChat();

            }
        }

        this.fill(255);
        this.rect(810, 500, 180, 50);
        this.fill(0);
        this.text(text, 900-text.length(), 525);

        this.stroke(0);
        this.strokeWeight(3);
        this.fill(0);
        for(int j = 0 ; j < receivedPoints.size() ; j++) {
            this.point(receivedPoints.get(j).getPosX(), receivedPoints.get(j).getPosY());
        }


        if(mousePressed) {
            linkPoint();
            posX = mouseX;
            posY = mouseY;
        }
        tick++;


        receivedPoints.clear();

    }

    @Override
    public void mousePressed(MouseEvent event) {
        posX = event.getX();
        posY = event.getY();

    }

    @Override
    public void keyPressed(KeyEvent event) {
        char input = event.getKey();

        if (input == '\n') {
            Message message = new Message("ANSWER", new String[]{myName,text});
            sendMessage(message);
            text = "";
        }else if(input==BACKSPACE ) {
            if (text.length()==0)
                text ="";
            else
                text = new String(text.getBytes(), 0, text.length()-1);

        }else
            text += input;
    }




    public void linkPoint() {
        int deltaX = mouseX - posX;
        int deltaY = mouseY - posY;
        float length = (float) Math.sqrt(deltaX*deltaX + deltaY*deltaY);

        for(int i = 0; i < length; i+=1f){
            Point p = new Point();

            p.setPosX((int)(deltaX / length * i + posX));
            p.setPosY((int)(deltaY / length * i + posY));

            sendMessage(new Message("DRAW",new String[]{myName, String.valueOf(p.getPosX()), String.valueOf(p.getPosY())}));
            points.add(p);
        }
    }

    public void onReceive(String protocol, String[] args) {
        switch (protocol) {
            case "TURN" :
                myAnswer = args[0];
                break;
            case "USERS" :
                players = new ArrayList<>();
                String[] player = args[0].split("/");
                for(int i = 0 ; i < player.length ; i++) {
                    String[] playerInfo = player[i].split(",");
                    Player p = new Player(playerInfo[0], playerInfo[1]);
                    players.add(p);
                }
                break;
            case "DRAW" :
                receivedPoints.add(new Point(Integer.parseInt(args[1]), Integer.parseInt(args[2])));
                break;
            case "SUCCESS" :
                myAnswer = "";
                tick = 0;
                for(Player p : players) {
                    if(p.getId().equals(args[0])) {
                        p.setScore(args[2]);
                    }
                }
                answer = args[1];
                break;
            case "FAIL" :
                for(int i = 0 ; i < players.size() ; i++) {
                    if(players.get(i).getId().equals(args[0])) {
                        players.get(i).setChat(args[1]);
                    }
                }
                break;
            case "DISCONNECT" :
                for(int i = 0 ; i < players.size() ; i++) {
                    if(players.get(i).getId().equals(args[0])) {
                        players.remove(players.get(i));
                    }
                }
                break;
            case "REFRESH" :
                erase = true;
                break;
        }
    }

    public void sendMessage(Message message) {

        try {
            dos.writeInt(message.getResult().length());
            dos.write(message.getResult().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
