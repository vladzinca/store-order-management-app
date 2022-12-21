import java.io.*;
import java.util.concurrent.*;

public class Tema2 {
    public static void main(String[] args) {
        String folder_input = args[0];
        int nr_max_threads = Integer.parseInt(args[1]);

        BufferedReader orderBufferedReader = null;
        BufferedWriter orderBufferedWriter = null;
        BufferedWriter productBufferedWriter = null;

        Thread[] t = new Thread[nr_max_threads];

        Semaphore sem = new Semaphore(nr_max_threads);

        try {
            orderBufferedReader = new BufferedReader(new FileReader(folder_input + "/orders.txt"));
            orderBufferedWriter = new BufferedWriter(new FileWriter("orders_out.txt"));
            productBufferedWriter = new BufferedWriter(new FileWriter("order_products_out.txt"));

            for (int i = 0; i < nr_max_threads; i++) {
                t[i] = new Level1Task(i, sem, folder_input, nr_max_threads, orderBufferedReader, orderBufferedWriter,
                        productBufferedWriter);
                t[i].start();
            }

            for (int i = 0; i < nr_max_threads; i++) {
                try {
                    t[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            orderBufferedWriter.close();
            productBufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (orderBufferedReader != null)
                    orderBufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
