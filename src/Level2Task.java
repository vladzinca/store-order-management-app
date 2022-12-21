import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Level2Task extends Thread {

    private int id;
    private Semaphore sem;
    private String order_id_comanda;
    private volatile int order_nr_produse;
    private BufferedReader productBufferedReader;
    private BufferedWriter productBufferedWriter;

    public Level2Task(int id, Semaphore sem, String order_id_comanda, int order_nr_produse, BufferedReader productBufferedReader,
            BufferedWriter productBufferedWriter) {
        this.id = id;
        this.sem = sem;
        this.order_id_comanda = order_id_comanda;
        this.order_nr_produse = order_nr_produse;
        this.productBufferedReader = productBufferedReader;
        this.productBufferedWriter = productBufferedWriter;
    }

    public void run() {
        try {
            String productsLine;

            while ((productsLine = productBufferedReader.readLine()) != null) {
                StringTokenizer productStringTokenizer = new StringTokenizer(productsLine, ",");

                String product_id_comanda = productStringTokenizer.nextToken();
                // String product_id_produs = productStringTokenizer.nextToken();

                if (order_id_comanda.equals(product_id_comanda)) {
                    productBufferedWriter.write(productsLine + ",shipped\n");
                    order_nr_produse--;
                }

            }

            sem.release();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
