public class player {
    private String name;
    private boolean playing;
    private boolean waiting;
    private Server server;
    private int pos;
    private Object lock;

    public void frset(int p){
        playing = false;
        waiting = false;
        pos = p;
    }
    public String name(){
        return name;
    }
    public boolean playing(){
        return playing;
    }
    public boolean waiting(){
        return waiting;
    }
    public Server server(){
        return server;
    }
    public int pos(){
        return pos;
    }
    public Object lock(){
        return lock;
    }
    public void setlock(Object l){
        lock = l;
    }
    public void setname(String n){
        name = n;
    }
    public void setserver(Server s){
        server = s;
    }
    public void setpos(int p){
        pos = p;
    }
    public void setwait(boolean w){
        waiting = w;
    }
    public void setplay(boolean p){
        playing = p;
    }
    public void end(){
        playing = false;
        waiting = false;
    }
}
