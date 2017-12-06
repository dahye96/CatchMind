public class Message {
    private String protocol;
    private String[] args;

    public Message(String receive){
        String[] arr = receive.split("#");
        this.protocol = arr[0];

        args = new String[arr.length-1];
        for (int i = 1; i < arr.length; i++) {
            args[i-1]=arr[i];
        }
    }

    public Message(String protocol, String args[]) {
        this.protocol = protocol;
        this.args = args;
    }

    public String getResult(){
        String res = protocol;

        for (int i = 0; i < args.length; i++) {
            res += "#";
            res += args[i];
        }

        return res;
    }

    public String getProtocol() {
        return protocol;
    }

    public String[] getArgs() {
        return args;
    }

}