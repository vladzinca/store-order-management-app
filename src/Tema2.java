import java.io.*;
import java.util.concurrent.*;

public class Tema2 {

    public static void main(String[] args) {
        /* save call arguments */
        String folder_input = args[0];
        int nr_max_threads = Integer.parseInt(args[1]);

        /* declare reading and writing objects */
        BufferedReader orderBufferedReader = null;
        BufferedWriter orderBufferedWriter = null;
        BufferedWriter productBufferedWriter = null;

        /* declare number of first level threads */
        Thread[] t = new Thread[nr_max_threads];

        /* initialize semaphores sem for limiting the number of level 2
           threads to args[1] and readingSem1 to work as a mutex for
           making sure two level 1 threads do not read the same line */
        Semaphore sem = new Semaphore(nr_max_threads);
        Semaphore readingSem1 = new Semaphore(1);

        try {
            /* initialize reading and writing objects to read and write
               from the correct files */
            orderBufferedReader = new BufferedReader(new FileReader(folder_input + "/orders.txt"));
            orderBufferedWriter = new BufferedWriter(new FileWriter("orders_out.txt"));
            productBufferedWriter = new BufferedWriter(new FileWriter("order_products_out.txt"));

            /* start nr_max_threads level 1 threads */
            for (int i = 0; i < nr_max_threads; i++) {
                t[i] = new Level1Task(sem, readingSem1, folder_input, nr_max_threads,
                        orderBufferedReader, orderBufferedWriter, productBufferedWriter);
                t[i].start();
            }

            /* join the nr_max_threads threads */
            for (int i = 0; i < nr_max_threads; i++) {
                try {
                    t[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            /* after join, close writing objects */
            orderBufferedWriter.close();
            productBufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (orderBufferedReader != null)
                    /* after join, close reading object */
                    orderBufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
