import java.io.*;
import java.util.*;

public class Tema2 {
    public static void main(String[] args) {
        String folder_input = args[0];
        // int nr_max_threads = Integer.parseInt(args[1]);

        BufferedReader orderBufferedReader = null;
        BufferedReader productBufferedReader = null;
        BufferedWriter orderBufferedWriter = null;
        BufferedWriter productBufferedWriter = null;

        try {
            orderBufferedReader = new BufferedReader(new FileReader(folder_input + "/orders.txt"));
            String ordersLine;

            orderBufferedWriter = new BufferedWriter(new FileWriter("orders_out.txt"));
            productBufferedWriter = new BufferedWriter(new FileWriter("order_products_out.txt"));

            while ((ordersLine = orderBufferedReader.readLine()) != null) {
                StringTokenizer orderStringTokenizer = new StringTokenizer(ordersLine, ",");
                String order_id_comanda = orderStringTokenizer.nextToken();
                int order_nr_produse = Integer.parseInt(orderStringTokenizer.nextToken());
                int tmp_order_nr_produse = order_nr_produse;

                productBufferedReader = new BufferedReader(new FileReader(folder_input + "/order_products.txt"));
                String productsLine;

                while (((productsLine = productBufferedReader.readLine()) != null) && (tmp_order_nr_produse > 0)) {
                    StringTokenizer productStringTokenizer = new StringTokenizer(productsLine, ",");

                    String product_id_comanda = productStringTokenizer.nextToken();
                    // String product_id_produs = productStringTokenizer.nextToken();

                    if (order_id_comanda.equals(product_id_comanda)) {
                        productBufferedWriter.write(productsLine + ",shipped\n");
                        tmp_order_nr_produse--;
                    }
                }

                if (order_nr_produse > 0)
                    orderBufferedWriter.write(ordersLine + ",shipped\n");

                productBufferedReader.close();

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
