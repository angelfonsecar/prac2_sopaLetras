import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class SopaLetrasC {
    private final JComboBox<String> combo = new JComboBox<>();
    private JPanel panelSopa, rightPanel, labelPanel;
    private JLabel [] palabrasPorEncontrar;
    JButton [][] botones;
    private final int rows=16;
    private final int columns=16;
    private final char[][] matrix = new char[16][16];
    private ArrayList<DatosPalabra> palabrasArrayList = new ArrayList<>();

    int palabrasFaltantes;
    long fInicio;
    boolean terminar = false;

    private JButton tempButton = new JButton();

    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public SopaLetrasC() {
        //Creacion de la ventana con los componentes
        panelSopa = new JPanel();
        panelSopa.setLayout(new GridLayout(rows,columns));
        rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.add(combo,BorderLayout.NORTH);
        labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel,BoxLayout.Y_AXIS));


        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());

        try{
            Socket cl = new Socket("127.0.0.1",3500);
            System.out.println("Conexion con servidor establecida...");

            oos = new ObjectOutputStream(cl.getOutputStream());
            ois = new ObjectInputStream(cl.getInputStream());
            botones = new JButton[rows][columns];


            String []categ = (String[]) ois.readObject();

            for (String category: categ) // Añadir los items JComboBox.
                combo.addItem(category);
            inicLabels();


            // Accion a realizar cuando el usuario cambia de categoría seleccionado.
            combo.addActionListener(e -> {
                //iniciar el cronometro
                fInicio = System.currentTimeMillis();

                palabrasFaltantes = 10;
                for(int i=0; i<16; i++){        //leemos la matriz de letras
                    for (int j=0; j<16; j++){
                        botones[i][j].setEnabled(true);
                        botones[i][j].setBackground(null);
                    }
                }
                String elec = combo.getSelectedItem().toString();
                try {
                    oos.writeObject(elec);
                    oos.flush();

                    for(int i=0; i<16; i++){        //leemos la matriz de letras
                        for (int j=0; j<16; j++){
                            char charTemp = (char) ois.readObject();
                            matrix[i][j] = charTemp;
                        }
                    }
                    rellenaBotones();

                    palabrasArrayList = (ArrayList<DatosPalabra>) ois.readObject();
                    int i=0;
                    for (DatosPalabra datoPalabra: palabrasArrayList) { //imiprime la lista de palabras con sus propiedades
                        palabrasPorEncontrar[i++].setText(datoPalabra.getPalabra());
                        //palabrasPorEncontrar[i++].setText(datoPalabra.getPalabra());
                        System.out.println("\nPalabra: "+datoPalabra.getPalabra()+
                                "\nCoord inicio:"+datoPalabra.getxInicio()+","+datoPalabra.getyInicio()+
                                "\nCoord fin:"+datoPalabra.getxFin()+","+datoPalabra.getyFin());
                    }
/*                    for (int x=0; x < matrix.length; x++) {     //imprimimos la matriz recibida
                        for (int y=0; y < matrix[x].length; y++) {
                            System.out.print(" | ");System.out.print (matrix[x][y]); System.out.print(" | ");
                        }
                        System.out.println();
                    }
                    System.out.println();*/

                } catch (IOException | ClassNotFoundException ioException) {
                    ioException.printStackTrace();
                }
            });


            hazBotones();
            for(int i=0; i<10; i++){
                labelPanel.add(palabrasPorEncontrar[i]);
            }
            rightPanel.add(labelPanel,BorderLayout.CENTER);
            frame.add(panelSopa,BorderLayout.WEST);
            frame.add(rightPanel,BorderLayout.EAST);
            frame.pack();
            frame.setVisible(true);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            if(!frame.isActive())
                System.out.println("Marco no activo");

            while(!terminar){
                Thread.sleep(10);
            }
            oos.close();
            ois.close();
            cl.close();
        }catch(Exception e){
            e.printStackTrace();
        }
	}

    public void inicLabels(){
        palabrasPorEncontrar = new JLabel[10];
        for (int i=0; i<10; i++)
            palabrasPorEncontrar[i] = new JLabel("");
    }

    public void hazBotones(){
        for(int y=0; y<rows; y++){
            for (int x=0; x<columns; x++){
                botones[x][y] = new JButton(x+","+y);
                botones[x][y].setMargin(new Insets(0,0,0,0));
                botones[x][y].setPreferredSize(new Dimension(30, 30));
                botones[x][y].setName(x+","+y);

                botones[x][y].addActionListener(e -> verificaPalabra((JButton) e.getSource()));

                panelSopa.add(botones[x][y]);
            }
        }
    }
    public void rellenaBotones(){
        for(int y=0; y<rows; y++) {
            for (int x = 0; x < columns; x++) {
                botones[y][x].setText(String.valueOf(matrix[x][y]));
            }
        }
    }

    public void verificaPalabra(JButton presionado) {//string inicio string final
        String tempButtonName = tempButton.getName();
        boolean isInCoordInicio = false;
        if (tempButtonName == null) {
            System.out.println("El botón es el primero en ser presionado");
            presionado.setEnabled(false);
            presionado.setBackground(Color.red);
            tempButton = presionado;
        } else {
            System.out.println("segundo en ser presionado\nEl primero fue " + tempButtonName);
            //boton1 = tempButton; boton2 = presionado;
            int index = 0;
            for (DatosPalabra datoPalabra : palabrasArrayList) {
                index++;
                if (tempButtonName.equals(datoPalabra.getxInicio() + "," + datoPalabra.getyInicio())) {
                    isInCoordInicio = true;
                    break;
                }
            }

            DatosPalabra posibleCoincidencia = palabrasArrayList.get(index - 1);
            System.out.println("CoordFin de posiblCoinc " + posibleCoincidencia.getxFin() + "," + posibleCoincidencia.getyFin());
            if (isInCoordInicio && presionado.getName().equals(posibleCoincidencia.getxFin() + "," + posibleCoincidencia.getyFin())) {
                System.out.println("Felicidades, palabra encontrada: " + posibleCoincidencia.getPalabra());
                colorearCeldas(posibleCoincidencia);
                palabrasFaltantes--;
                System.out.println("Faltan "+palabrasFaltantes+" palabras");
                for(int i=0; i<10; i++){
                    if(palabrasPorEncontrar[i].getText().equals(posibleCoincidencia.getPalabra()))
                        palabrasPorEncontrar[i].setText("-----");
                }
                if(palabrasFaltantes<1){
                    System.out.println("Tiempo: " + ((System.currentTimeMillis()-fInicio)/1000) );
                    System.out.println("Felicidades!!!\nHaz Ganado, ingresa tu nombre:");
                    Scanner reader = new Scanner(System.in);
                    String nickname = reader.nextLine();

                    registro(nickname, String.valueOf(((System.currentTimeMillis()-fInicio)/1000)));
                    System.out.println("Saliendo...");
                    terminar = true;
                }

            } else {
                tempButton.setEnabled(true);
                tempButton.setBackground(null);
                System.out.println("\nIntenta de nuevo");
            }
            tempButton = new JButton();
        }
    }

    public void colorearCeldas(DatosPalabra acierto){
        int auxP= (acierto.getPalabra()).length();
        int difX=acierto.getxFin()- acierto.getxInicio();
        int difY= acierto.getyFin()- acierto.getyInicio();

        if(difX>0 && difY==0)  //modo 0
            for(int i=0;i<auxP;i++) {
                botones[acierto.getxInicio() + i][acierto.getyInicio()].setBackground(Color.pink);
                botones[acierto.getxInicio()+i][acierto.getyInicio()].setEnabled(false);
            }
        if(difX>0 && difY>0)  //modo 1
            for(int i=0;i<auxP;i++) {
                botones[acierto.getxInicio() + i][acierto.getyInicio() + i].setBackground(Color.pink);
                botones[acierto.getxInicio() + i][acierto.getyInicio() + i].setEnabled(false);
            }
        if(difX==0 && difY>0) //modo 2
            for(int i=0;i<auxP;i++) {
                botones[acierto.getxInicio()][acierto.getyInicio() + i].setBackground(Color.pink);
                botones[acierto.getxInicio()][acierto.getyInicio() + i].setEnabled(false);
            }
        if(difX<0 && difY>0)  //modo 3
            for(int i=0;i<auxP;i++) {
                botones[acierto.getxInicio() - i][acierto.getyInicio() + i].setBackground(Color.pink);
                botones[acierto.getxInicio() - i][acierto.getyInicio() + i].setEnabled(false);
            }
        if(difX<0 && difY==0) //modo 4
            for(int i=0;i<auxP;i++) {
                botones[acierto.getxInicio() - i][acierto.getyInicio()].setBackground(Color.pink);
                botones[acierto.getxInicio() - i][acierto.getyInicio()].setEnabled(false);
            }
        if(difX<0 && difY<0)  //modo 5
            for(int i=0;i<auxP;i++) {
                botones[acierto.getxInicio() - i][acierto.getyInicio() - i].setBackground(Color.pink);
                botones[acierto.getxInicio() - i][acierto.getyInicio() - i].setEnabled(false);
            }
        if(difX==0 && difY<0)  //modo 6
            for(int i=0;i<auxP;i++) {
                botones[acierto.getxInicio()][acierto.getyInicio() - i].setBackground(Color.pink);
                botones[acierto.getxInicio()][acierto.getyInicio() - i].setEnabled(false);
            }
        if(difX>0 && difY<0)  //modo 7
            for(int i=0;i<auxP;i++) {
                botones[acierto.getxInicio() + i][acierto.getyInicio() - i].setBackground(Color.pink);
                botones[acierto.getxInicio() + i][acierto.getyInicio() - i].setEnabled(false);
            }
    }


    public static void main (String[]args){
        new SopaLetrasC();
    }
}