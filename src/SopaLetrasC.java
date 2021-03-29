import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class SopaLetrasC {
    private JComboBox<String> combo = new JComboBox<>();;
    private JFrame frame;
    private JPanel panelSopa, rightPanel, labelPanel;
    private JLabel [] palabrasPorEncontrar;
    JButton [][] botones;
    private final int rows=16;
    private final int columns=16;
    private char[][] matrix = new char[16][16];
    private ArrayList<DatosPalabra> palabrasArrayList = new ArrayList<>();

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


        frame = new JFrame();
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
            //oos.close();
            //ois.close();
            //cl.close();
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
                //botones[x][y].setBackground(Color.WHITE);
                botones[x][y].setPreferredSize(new Dimension(30, 30));
                botones[x][y].setName(x+","+y);

                int finalX = x;
                int finalY = y;
                botones[x][y].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Botón: "+botones[finalX][finalY].getName());
                        //JButton first = (JButton) e.getSource();
                        verificaPalabra((JButton) e.getSource());

                    }
                });

//                botones[x][y].putClientProperty("xpos", y);
//                botones[x][y].putClientProperty("ypos", x);
//                botones[x][y].putClientProperty("result", false);
                /*botones[x][y].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        System.out.println(actionEvent.getActionCommand());
                        if(!gamestatus) return;
                        JButton actioner = (JButton) actionEvent.getSource();
                        if(!selectedButtons.contains(actioner)){
                            selectedButtons.add(actioner);
                            actioner.setBackground(Color.YELLOW);
                            System.out.println(actioner.getClientProperty("xpos") + " . " + actioner.getClientProperty("ypos"));
                        }else{
                            selectedButtons.remove(actioner);
                            actioner.setBackground(Color.WHITE);
                            if((boolean) actioner.getClientProperty("result")){
                                actioner.setBackground(Color.GREEN);
                            }
                        }
                        if (checkIfOk()){
                            System.out.println("Checar si es alguna palabra");
                            String word = getWordInOrder();
                            if(actualWords.contains(word.toLowerCase(Locale.ROOT)) || actualWords.contains(StringUtils.reverse(word.toLowerCase(Locale.ROOT)))){
                                System.out.println("Encontraste la palabra :  " + word);
                                disableButtons();
                                markInPanel(word);
                                //actualWords.get();
                                int posInOrder = actualWords.indexOf(word.toLowerCase(Locale.ROOT));
                                int posInDisorder = actualWords.indexOf(StringUtils.reverse(word.toLowerCase(Locale.ROOT)));
                                if(posInOrder>=0) actualWords.remove(posInOrder);
                                else{
                                    actualWords.remove(posInDisorder);
                                }
                                if(actualWords.size()==0){
                                    endGame();
                                }
                            }
                        }
                    }
                });*/
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
                    System.out.println("La coord inicio es correcta con el indice: " + index);
                    break;
                }
            }

            DatosPalabra posibleCoincidencia = palabrasArrayList.get(index - 1);
            System.out.println("CoordFin de posiblCoinc " + posibleCoincidencia.getxFin() + "," + posibleCoincidencia.getyFin());
            if (isInCoordInicio && presionado.getName().equals(posibleCoincidencia.getxFin() + "," + posibleCoincidencia.getyFin())) {
                System.out.println("Felicidades, palabra encontrada: " + posibleCoincidencia.getPalabra());
                colorearCeldas(posibleCoincidencia);

            } else {
                System.out.println("Intenta de nuevo");
            }

            tempButton.setEnabled(true);
            tempButton = new JButton();
        }
    }

    public void colorearCeldas(DatosPalabra acierto){
        int auxP= (acierto.getPalabra()).length();
        int difX=acierto.getxFin()- acierto.getxInicio();
        int difY= acierto.getyFin()- acierto.getyInicio();

        if(difX>0 && difY==0) { //modo 0
            for(int i=0;i<auxP;i++)
                botones[acierto.getxInicio()+i][acierto.getyInicio()].setBackground(Color.BLUE);
        }
        if(difX>0 && difY>0) { //modo 1
            for(int i=0;i<auxP;i++)
                botones[acierto.getxInicio()+i][acierto.getyInicio()+i].setBackground(Color.BLUE);
        }
        if(difX==0 && difY>0) { //modo 2
            for(int i=0;i<auxP;i++)
                botones[acierto.getxInicio()][acierto.getyInicio()+i].setBackground(Color.BLUE);
        }
        if(difX<0 && difY>0) { //modo 3

        }
        if(difX<0 && difY==0) { //modo 4
            for(int i=0;i<auxP;i++)
                botones[acierto.getxInicio()-i][acierto.getyInicio()].setBackground(Color.BLUE);
        }
        if(difX<0 && difY<0) { //modo 5

        }
        if(difX==0 && difY<0) { //modo 6
            for(int i=0;i<auxP;i++)
                botones[acierto.getxInicio()][acierto.getyInicio()-i].setBackground(Color.BLUE);
        }
        if(difX>0 && difY<0) { //modo 7

        }
        //Stella pega tu chingada funcion aqui
    }


    public static void main (String[]args){
        new SopaLetrasC();
    }
}