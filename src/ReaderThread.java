import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class ReaderThread extends Thread{

    private InputStream is;

    private Program main;

    public ReaderThread(InputStream is, Program main) {
        this.is = is;
        this.main = main;
    }

    @Override
    public void run() {
        DataInputStream dis = new DataInputStream(is);
        byte[] data = new byte[8192];

        try {
            while (true) {
                int packetLen = dis.readInt();
                readn(dis,data,packetLen);
                Message message = new Message(new String(data, 0, packetLen));
                //System.out.println("recieve :: "+ message.getResult());
                main.onReceive(message.getProtocol(), message.getArgs());
            }
        }catch (EOFException ignored) {
            System.out.println("연결이 종료되었습니다.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readn(DataInputStream dis, byte[] data, int size) throws IOException {
        int left = size;
        int offset = 0;

        while (left > 0) {
            int len = dis.read(data, offset, left);
            left -= len;
            offset += len;
        }
    }

}
