# Store Order Management App

## 🛍️ What is it?

It's a Java program that iterates through an order list, as one can find in an online store, and ships its associated products using multithreading capabilities.

To do this, it uses two threading levels, with the main program spawning `P` level 1 threads that spawn level 2 threads so that the maximum number of level 2 threads present at a time is also `P`.

I coded it in less than a day during mid-December 2022 and it helped me review the knowledge of parallel programming that I have been building upon in the last few months.

## ⚙️ How to run it?

### Method 1)

1.  Clone this repository.
2.  Run the automated testing script using `./checker.sh` inside the `checker/` directory (I did not make the script myself). It will run the tests present in `input/input_0/` - `input/input_9/` and compare the results to those present in the `output/` directories.
3.  Enjoy!

### Method 2)

1.  Clone this repository.
2.  Create two files called `orders.txt` and `order_products.txt` with your own list of orders and products. If you want, you can use examples from `checker/sample_data/`.
3.  Compile the code found in `src/` manually using `javac Tema2.java && javac Level1Task.java && javac Level2Task.java` and run it using `java Tema2 input_location P`, where `input_location` is the location of the input files *relative* to the location `.java` files, and `P` is the maximum number of level 1 threads and level 2 threads present at one point during execution.
4.  The program will create two files named `orders_out.txt` and `order_products_out.txt` where it will mark as `shipped` the parsed orders and products.
5.  Enjoy!

## 🧩 What does it do?

Regarding its functionality, I will try to be as concise and clear as I can.

The program starts by spawning `P` level 1 threads, whose job is to read orders that are defined as lines inside the file `orders.txt`.

For every order they read, the level 1 threads attempt to spawn as many level 2 threads as `P` to read products from `order_products.txt`, check if they belong to the order the level 1 thread that spawned them handles, and if it they do ship them.

After all the products from `order_products.txt` have been read and the correct one have been shipped, the level 1 thread marks the whole order as shipped.

This will eventually mark as shipped all the orders that have 1 or more products inside them.

## 🚦 How did I do it?

The main challenge was making sure there are only `P` level 2 threads running at the same time, even though each level 1 thread almost always attempts to spawn `P` threads to process its order.

To do that, I used a Semaphore with `P` permissions, and each time a level 1 thread attempts to start a level 2 thread, it must acquire a permission first, and then, just before the level 2 thread ends execution, release the permission.

This only allows `P` level 2 threads to execute at the same, and works as intended.

## 🤔 Did you know?

The level 2 threads know from the level 1 thread that spawned them how many products there are in the order they search for, `order_nr_produse`, and so I wanted to make them stop reading products when all the products related to the order were shipped.

This means that we have to successfuly modify the `order_nr_produse` variable without creating a race condition.

To do that, I parsed the variable into an AtomicInteger and modified it using its predefined methods.
