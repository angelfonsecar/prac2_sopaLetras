import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class SopaLetrasS {
    private final String[] categ = {"animales","flores","colores","instrumentos musicales","peliculas"};
    private final String[] animales = {"PERRO","GATO","CABALLO","ZORRRO","DELFIN","SERPIENTE","ORNITORRINCO","IGUANA","JIRAFA","CAMELLO","OVEJA","TORO","RANA","ELEFANTE","HIPOPOTAMO","ZURICATA","OSO","NUTRIA","CABRA","COCHINILLA"};
    private final String[] colores = {"VERDE","ROJO","AZUL","MORADO","NARANJA","GUINDA","BLANCO","NEGRO","BEIGE","AMARILLO","MAGENTA","CARMESI","CORAL","GRIS","CAFE","AQUA","ROSA","VIOLETA","DURAZNO","TURQUESA"};
    private final String[] peliculas = {"LALALAND","CASABLANCA","AVATAR","MADMAX","INCEPTION","INTERESTELAR","DOGISLAND","ET","JUMANGI","BEETLEJUICE","VERTIGO","FRAGMENTADO","METROPOLIS","TIBURON","ALIEN","MATRIX","PARASITE","STARWARS","THEARRIVAL","MOTHER"};
    private final String[] instrumentos = {"UKULELE","MARIMBA","KALIMBA","GUITARRA","BATERIA","TOLOLOCHE","BAJO","CONTRABAJO","VIOLIN","VIOLONCHELO","CHELO","ARPA","VIOLA","CLARINETE","FLAUTA","XILOFONO","PANDERO","BOMBO","PIANO","ACORDEON"};
    private final String[] flores = {"VIOLA","CRISANTEMO","ROSA","JAZMIN","GARDENIA","CAMELIA","BEGONIA","AZUCENA","TULIPAN","VIOLETA","ORQUIDEA","PETUNIA","NARCISO","MARGARITA","HORTENCIA","LIRIO","GIRASOL","TUBEROSA","DALIA","LAVANDA"};

    private char[][] matrix = new char[16][16];
    private ArrayList<DatosPalabra> palabrasArrayList = new ArrayList<>();

    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public SopaLetrasS() {
        try {
            ServerSocket s = new ServerSocket(3500);
            s.setReuseAddress(true);
            System.out.println("Servidor iniciado, esperando clientes...");

            for (;;){
                Socket cl = s.accept();
                System.out.println("Cliente conectado desde  "+cl.getInetAddress()+" : "+cl.getPort());

                oos = new ObjectOutputStream(cl.getOutputStream());
                ois = new ObjectInputStream(cl.getInputStream());
                oos.writeObject(categ);
                oos.flush();

                while (true){
                    String elec = (String) ois.readObject();
                    System.out.println("elec = " + elec);

                    for(int i=0;i<16;i++)
                        for(int j=0;j<16;j++)
                            matrix[i][j]='-';

                    String[] palabrasElegidas = escogeArreglo(elec);    //se seleccionan 10 palabras al azar

                    palabrasArrayList.clear();

                    for(int j=0; j<10; j++) {
                        colocarPalabra(palabrasElegidas[j]);
                    }

                    for (int x=0; x < matrix.length; x++) {
                        for (int y=0; y < matrix[x].length; y++) {
                            System.out.print(" | ");System.out.print (matrix[x][y]); System.out.print(" | ");
                        }
                        System.out.println();
                    }
                    completaMatrix();
                    for (int x=0; x < matrix.length; x++) {
                        for (int y=0; y < matrix[x].length; y++) {
                            System.out.print(" | ");System.out.print (matrix[x][y]); System.out.print(" | ");
                        }
                        System.out.println();
                    }

                    for (DatosPalabra datoPalabra: palabrasArrayList) { //imiprime la lista de palabras con sus propiedades
                        System.out.println("\nPalabra: "+datoPalabra.getPalabra()+
                                "\nCoord inicio:"+datoPalabra.getxInicio()+","+datoPalabra.getyInicio()+
                                "\nCoord fin:"+datoPalabra.getxFin()+","+datoPalabra.getyFin());
                    }

                    for(int i=0; i<16; i++){    //envia la matriz
                        for (int j=0; j<16; j++){
                            char charTemp = matrix[i][j];
                            oos.writeObject(charTemp);
                            oos.flush();
                        }
                    }

                    oos.writeObject(palabrasArrayList.clone());
                    oos.flush();

                    //enviar al cliente el array con: la lista de palabras, las coordenadas de inicio y fin.
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

    public String[] escogeArreglo(String elec) {
        String[] categElegida = new String[20];
        String[] palabrasElegidas = new String[10];
        if (elec.equals("animales"))
            categElegida = animales;
        if (elec.equals("flores"))
            categElegida = flores;
        if (elec.equals("peliculas"))
            categElegida = peliculas;
        if (elec.equals("instrumentos musicales"))
            categElegida = instrumentos;
        if (elec.equals("colores"))
            categElegida = colores;
        else
            System.out.println("no hubo match");

        Collections.shuffle(Arrays.asList(categElegida));

        for (int i = 0; i < 10; i++) {
            palabrasElegidas[i] = categElegida[i];
            System.out.println("palabraElegidas = " + palabrasElegidas[i]+" tam: "+palabrasElegidas[i].length());
        }

        return palabrasElegidas;
    }

    public void colocarPalabra(String palabra){
        int xRand;
        int yRand;

        do{   //obtener un origen vacío
            xRand = (int) (Math.random()*15);
            yRand = (int) (Math.random()*15);
        }
        while(matrix[xRand][yRand]!='-');

        int modo;
        boolean correctOrient = false;
        for(modo=0; modo<8; modo++){

            switch (modo) {
                case 0 -> correctOrient = encuentraOrient(palabra, xRand, yRand, 1, 0);// las 3
                case 1 -> correctOrient = encuentraOrient(palabra, xRand, yRand, 1, 1);// las 4 y media
                case 2 -> correctOrient = encuentraOrient(palabra, xRand, yRand, 0, 1);// las 6
                case 3 -> correctOrient = encuentraOrient(palabra, xRand, yRand, -1, 1);// las 7 y media
                case 4 -> correctOrient = encuentraOrient(palabra, xRand, yRand, -1, 0);// las 9
                case 5 -> correctOrient = encuentraOrient(palabra, xRand, yRand, 1, -1);// las 10 y media
                case 6 -> correctOrient = encuentraOrient(palabra, xRand, yRand, 0, -1);// las 12
                case 7 -> correctOrient = encuentraOrient(palabra, xRand, yRand, -1, -1);//la 1 y media
            }

            if(correctOrient) {
                registraCoordenadas(palabra,xRand,yRand,modo);
                break;
            }
        }
        if(!correctOrient)
            colocarPalabra(palabra);
    }

    boolean encuentraOrient(String palabra, int x, int y, int difFila, int difCol){

        boolean cabePalabra = false;

        if((x+palabra.length()*difFila)>15 || (y+palabra.length()*difCol)>15 || (x+palabra.length()*difFila)<0 || (y+palabra.length()*difCol)<0)
            return false;

        int i;
        for(i=0; i<palabra.length(); i++){
            if(matrix[ x + i*difFila ][ y + i*difCol ]=='-'){
                cabePalabra = true;
            }else{
                cabePalabra = false;
                break;
            }
        }

        if(cabePalabra){
            for(int j=0; j<palabra.length(); j++)
                matrix[ x + j*difFila ][ y + j*difCol ] = palabra.charAt(j);
            return true;
        }
        else
            return false;
    }

        if(cabePalabra){
            for(int j=0; j<palabra.length(); j++)
                matrix[ x + j*difX ][ y + j*difY ] = palabra.charAt(j);
            return true;
        }
        else
            return false;
    }

    public void registraCoordenadas(String palabra, int filaInicio, int columnaInicio, int modo){
        //System.out.println("ejecutando registraCoord palabra: "+palabra+" caso: "+modo);
        int filaFin=0,columnaFin=0;
        switch(modo){
            case 0 ->{
                filaFin= filaInicio+palabra.length()-1;
                columnaFin= columnaInicio;
            }
            case 1 ->{
                filaFin= filaInicio+palabra.length()-1;
                columnaFin= columnaInicio+palabra.length()-1;
            }
            case 2 ->{
                filaFin= filaInicio;
                columnaFin= columnaInicio+palabra.length()-1;
            }
            case 3 ->{
                filaFin= filaInicio-palabra.length()+1;
                columnaFin= columnaInicio+palabra.length()-1;
            }
            case 4 ->{
                filaFin= filaInicio-palabra.length()+1;
                columnaFin= columnaInicio;
            }
            case 5 -> {
                filaFin = filaInicio - palabra.length() + 1;
                columnaFin = columnaInicio - palabra.length() + 1;
            }
            case 6 ->{
                filaFin= filaInicio;
                columnaFin= columnaInicio-palabra.length()+1;
            }
            case 7 ->{
                filaFin= filaInicio+palabra.length()-1;
                columnaFin= columnaInicio-palabra.length()+1;
            }
        }
        DatosPalabra datosPalabra = new DatosPalabra(columnaInicio,filaInicio,columnaFin,filaFin,palabra);
        palabrasArrayList.add(datosPalabra);
    }

    public void completaMatrix(){
        for(int i=0;i<16;i++)
            for(int j=0;j<16;j++)
                if(matrix[i][j]=='-')
                    matrix[i][j] = (char) (Math.random()*(90-65+1)+65);
    }

    public static void main(String[] args) {
        new SopaLetrasS();
    }
}