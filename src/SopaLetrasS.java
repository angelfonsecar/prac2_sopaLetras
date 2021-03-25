import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SopaLetrasS {
    private String[] categ = {"Animales","Flores","Colores","Instrumentos musicales","Películas"};

    public SopaLetrasS() {
        try {
            ServerSocket s = new ServerSocket(3600);
            s.setReuseAddress(true);
            System.out.println("Servidor iniciado, esperando clientes...");

            for (;;){
                Socket cl = s.accept();
                System.out.println("Cliente conectado desde  "+cl.getInetAddress()+" : "+cl.getPort());

                ObjectOutputStream oos = new ObjectOutputStream(cl.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(cl.getInputStream());
                oos.writeObject(categ);
                oos.flush();

                while (true){
                    String elec = (String) ois.readObject();
                    System.out.println("elec = " + elec);
                    if(elec.equals("salir")) break;
                }

                oos.close();
                ois.close();
                cl.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new SopaLetrasS();
    }
}
