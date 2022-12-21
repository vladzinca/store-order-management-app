import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Level1Task extends Thread {

    private int id;
    private Semaphore sem;
    private String folder_input;
    private int nr_max_threads;
    private BufferedReader orderBufferedReader;
    private BufferedWriter orderBufferedWriter;
    private BufferedWriter productBufferedWriter;

    public Level1Task(int id, Semaphore sem, String folder_input, int nr_max_threads,
            BufferedReader orderBufferedReader,
            BufferedWriter orderBufferedWriter, BufferedWriter productBufferedWriter) {
        this.id = id;
        this.sem = sem;
        this.folder_input = folder_input;
        this.nr_max_threads = nr_max_threads;
        this.orderBufferedReader = orderBufferedReader;
        this.orderBufferedWriter = orderBufferedWriter;
        this.productBufferedWriter = productBufferedWriter;
    }

    public void run() {
        String ordersLine;
        try {
            while ((ordersLine = orderBufferedReader.readLine()) != null) {
                StringTokenizer orderStringTokenizer = new StringTokenizer(ordersLine, ",");
                String order_id_comanda = orderStringTokenizer.nextToken();
                int order_nr_produse = Integer.parseInt(orderStringTokenizer.nextToken());
                // System.out.println("From thread " + id + ": " + order_id_comanda + "," +
                // order_nr_produse + ",shipped");

                BufferedReader productBufferedReader = new BufferedReader(
                        new FileReader(folder_input + "/order_products.txt"));

                Thread[] t = new Thread[Math.min(nr_max_threads, order_nr_produse)];

                for (int i = 0; i < Math.min(nr_max_threads, order_nr_produse); i++) {
                    sem.acquire();
                    t[i] = new Level2Task(i, sem, order_id_comanda, order_nr_produse, productBufferedReader, productBufferedWriter);
                    t[i].start();
                }

                for (int i = 0; i < Math.min(nr_max_threads, order_nr_produse); i++) {
                    try {
                        t[i].join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                productBufferedReader.close();

                if (order_nr_produse > 0)
                    orderBufferedWriter.write(ordersLine + ",shipped\n");
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
