import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class Level2Task extends Thread {

    private Semaphore sem;
    private Semaphore readingSem2;
    private String order_id_comanda;
    private AtomicInteger atomic_order_nr_produse;
    private BufferedReader productBufferedReader;
    private BufferedWriter productBufferedWriter;

    /* constructor to obtain parameters from level 1 thread call */
    public Level2Task(Semaphore sem, Semaphore readingSem2, String order_id_comanda,
            AtomicInteger atomic_order_nr_produse, BufferedReader productBufferedReader,
            BufferedWriter productBufferedWriter) {
        this.sem = sem;
        this.readingSem2 = readingSem2;
        this.order_id_comanda = order_id_comanda;
        this.atomic_order_nr_produse = atomic_order_nr_produse;
        this.productBufferedReader = productBufferedReader;
        this.productBufferedWriter = productBufferedWriter;
    }

    public void run() {
        try {
            String productsLine;

            /* make an attempt to read a line, will do if no other thread generated
               at the corresponding level 1 thread is reading */
            readingSem2.acquire();

            /* will only read lines until order_nr_produse products are found */
            while (((productsLine = productBufferedReader.readLine()) != null) && !atomic_order_nr_produse.compareAndSet(0, 0)) {

                /* read a line successfuly, allow other threads to read as well */
                readingSem2.release();

                /* parse line and get the product_id_comanda */
                StringTokenizer productStringTokenizer = new StringTokenizer(productsLine, ",");
                String product_id_comanda = productStringTokenizer.nextToken();

                /* check if the product is part of the desired order */
                if (order_id_comanda.equals(product_id_comanda)) {

                    /* if it is, ship the product and decrease number of
                       products searched for in said order */
                    productBufferedWriter.write(productsLine + ",shipped\n");
                    int order_nr_produse = atomic_order_nr_produse.get();
                    atomic_order_nr_produse.compareAndSet(order_nr_produse, order_nr_produse - 1);

                }

                /* productLine is processed completely, so prepare to read another line */
                readingSem2.acquire();
            }

            /* after all products were processed, release the just acquired permission */
            readingSem2.release();

            /* level 2 thread finished job, can start another level 2 thread */
            sem.release();

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
