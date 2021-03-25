import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SopaLetrasC {
    private JTextField tf;
    private JComboBox<String> combo = new JComboBox<>();;
    private JFrame frame;

    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public SopaLetrasC() {
       try{
           Socket cl = new Socket("127.0.0.1",3600);
           System.out.println("Conexion con servidor establecida...");

           oos = new ObjectOutputStream(cl.getOutputStream());
           ois = new ObjectInputStream(cl.getInputStream());
           oos.writeObject("");
           oos.flush();

           String []categ = (String[]) ois.readObject();

           // Creacion del JComboBox y añadir los items.
           for (String category: categ) {
               combo.addItem(category);
           }

           tf = new JTextField(20);
           // Accion a realizar cuando el JComboBox cambia de item seleccionado.
           combo.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                   String elec = combo.getSelectedItem().toString();
                   tf.setText(elec);
                   try {
                       System.out.println("A cont. intentará write: "+elec);
                       oos.writeObject(elec);
                       oos.flush();
                   } catch (IOException ioException) {
                       ioException.printStackTrace();
                   }
               }
           });

           oos.close();
           ois.close();
           cl.close();
       }catch(Exception e){
           e.printStackTrace();
       }

        // Creacion de la ventana con los componentes
        frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(combo);
        frame.getContentPane().add(tf);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        new SopaLetrasC();
    }
}
