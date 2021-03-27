import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SopaLetrasS {
    private final String[] categ = {"Animales","Flores","Colores","Instrumentos musicales","Películas"};
    private final String[] animales = {"PERRO","GATO","CABALLO","ZORRRO","DELFIN","SERPIENTE","ORNITORRINCO","IGUANA","JIRAFA","CAMELLO","OVEJA","TORO","RANA","ELEFANTE","HIPOPOTAMO","ZURICATA","OSO","NUTRIA","CABRA","COCHINILLA"};
    private final String[] colores = {"VERDE","ROJO","AZUL","MORADO","NARANJA","GUINDA","BLANCO","NEGRO","BEIGE","AMARILLO","MAGENTA","CARMESI","CORAL","GRIS","CAFE","AQUA","ROSA","VIOLETA","DURAZNO","TURQUESA"};
    private final String[] peliculas = {"LALALAND","CASABLANCA","AVATAR","MADMAX","INCEPTION","INTERESTELAR","DOGISLAND","ET","JUMANGI","BEETLEJUICE","VERTIGO","FRAGMENTADO","METROPOLIS","TIBURON","ALIEN","MATRIX","PARASITE","STARWARS","THEARRIVAL","MOTHER"};
    private final String[] instrumentos = {"UKULELE","MARIMBA","KALIMBA","GUITARRA","BATERIA","TOLOLOCHE","BAJO","CONTRABAJO","VIOLIN","VIOLONCHELO","CHELO","ARPA","VIOLA","CLARINETE","FLAUTA","XILOFONO","PANDERO","BOMBO","PIANO","ACORDEON"};
    private final String[] flores = {"VIOLA","CRISANTEMO","ROSA","JAZMIN","GARDENIA","CAMELIA","BEGONIA","AZUCENA","TULIPAN","VIOLETA","ORQUIDEA","PETUNIA","NARCISO","MARGARITA","HORTENCIA","LIRIO","GIRASOL","TUBEROSA","DALIA","LAVANDA"};


    public SopaLetrasS() {
        try {
            ServerSocket s = new ServerSocket(3500);
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

                    //agarrar aleatoriamente unas 10 palabras de una lista total de 20

                    //guardar en una lista las palabras elegidas
                    //colocar las palabras en una matriz de 16*16
                    //registrar por cada palabra, su coordenada de inicio, su coordenada de fin

                    //rellenar aleatoriamente el resto de la matriz

                    //enviar al cliente la lista de palabras
                    //enviar al cliente las coordenadas de inicio y fin
                    //enviar al cliente la matriz

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
