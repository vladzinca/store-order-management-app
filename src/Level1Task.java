import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class Level1Task extends Thread {

    private Semaphore sem;
    private Semaphore readingSem1;
    private String folder_input;
    private int nr_max_threads;
    private BufferedReader orderBufferedReader;
    private BufferedWriter orderBufferedWriter;
    private BufferedWriter productBufferedWriter;

    /* constructor to obtain parameters from main call */
    public Level1Task(Semaphore sem, Semaphore readingSem1, String folder_input, int nr_max_threads,
            BufferedReader orderBufferedReader, BufferedWriter orderBufferedWriter, BufferedWriter productBufferedWriter) {
        this.sem = sem;
        this.readingSem1 = readingSem1;
        this.folder_input = folder_input;
        this.nr_max_threads = nr_max_threads;
        this.orderBufferedReader = orderBufferedReader;
        this.orderBufferedWriter = orderBufferedWriter;
        this.productBufferedWriter = productBufferedWriter;
    }

    public void run() {
        try {
            String ordersLine;

            /* make an attempt to read a line, will do if no other thread is reading */
            readingSem1.acquire();

            while ((ordersLine = orderBufferedReader.readLine()) != null) {

                /* read a line successfuly, allow other threads to read as well */
                readingSem1.release();

                /* parse line into order_id_comanda and order_nr_produse,
                   make order_nr_produse atomic */
                StringTokenizer orderStringTokenizer = new StringTokenizer(ordersLine, ",");
                String order_id_comanda = orderStringTokenizer.nextToken();
                int order_nr_produse = Integer.parseInt(orderStringTokenizer.nextToken());
                AtomicInteger atomic_order_nr_produse = new AtomicInteger(order_nr_produse);

                /* every level 1 thread opens products list and attempts to make
                   a full, separate reading in level 2 threads */
                BufferedReader productBufferedReader = new BufferedReader(
                        new FileReader(folder_input + "/order_products.txt"));

                /* create level 2 threads, and a semaphore for them to read correctly */
                Thread[] t = new Thread[Math.min(nr_max_threads, order_nr_produse)];
                Semaphore readingSem2 = new Semaphore(1);

                /* create nr_max_threads or order_nr_produse level 2 threads, whichever
                   is smaller, and activate threads such that only nr_max_threads level 2
                   threads exist at a time globally, using Semaphore sem */
                for (int i = 0; i < Math.min(nr_max_threads, order_nr_produse); i++) {
                    sem.acquire();
                    t[i] = new Level2Task(sem, readingSem2, order_id_comanda,
                            atomic_order_nr_produse, productBufferedReader, productBufferedWriter);
                    t[i].start();
                }

                /* join level 2 threads created by this level 1 thread */
                for (int i = 0; i < Math.min(nr_max_threads, order_nr_produse); i++) {
                    try {
                        t[i].join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                /* reading of order_products.txt is done by this
                   level 1 thread, close reading object */
                productBufferedReader.close();

                /* all products in the order were shipped, so ship the order */
                if (order_nr_produse > 0)
                    orderBufferedWriter.write(ordersLine + ",shipped\n");

                /* order is processed completely, so prepare to process another order and
                   read corresponding line with this level 1 thread */
                readingSem1.acquire();
            }

            /* after all orders were processed, release the just acquired permission */
            readingSem1.release();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
