import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SopaLetrasC {
	private JComboBox<String> combo = new JComboBox<>();;
	private JFrame frame;
	private JPanel panelSopa, rightPanel;
	JButton [][] botones;
	private int rows=16;
    private final int columns=16;

    private JTextField txt = new JTextField();

	private ObjectOutputStream oos;
	private ObjectInputStream ois;

	public SopaLetrasC() {
        try{
            Socket cl = new Socket("127.0.0.1",3500);
            System.out.println("Conexion con servidor establecida...");

            oos = new ObjectOutputStream(cl.getOutputStream());
            ois = new ObjectInputStream(cl.getInputStream());
            botones = new JButton[rows][columns];


            String []categ = (String[]) ois.readObject();

            for (String category: categ) // Añadir los items JComboBox.
               combo.addItem(category);

            // Accion a realizar cuando el JComboBox cambia de item seleccionado.
            combo.addActionListener(e -> {
                String elec = combo.getSelectedItem().toString();
                try {
                    oos.writeObject(elec);
                    oos.flush();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            });

            //Creacion de la ventana con los componentes
            panelSopa = new JPanel();
            panelSopa.setLayout(new GridLayout(rows,columns));
            rightPanel = new JPanel();
            rightPanel.add(combo);

            frame = new JFrame();
            frame.setLayout(new BorderLayout());

            hazBotones();
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

	public void hazBotones(){
        for(int x=0; x<rows; x++){
            for (int y=0; y<columns; y++){
                botones[x][y] = new JButton(x +","+ y);
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

	public static void main(String[] args) {
        new SopaLetrasC();
	}
}
