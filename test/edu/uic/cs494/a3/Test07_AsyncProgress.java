package edu.uic.cs494.a3;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class Test07_AsyncProgress {

    @Test
    public void asyncAddItemsWithoutRunningThreads() {
        Warehouse w = Warehouse.createWarehouse();
        int size = 100_000;
        int test = 200_000;
        Shelf s = w.createShelf(size); // Thread not started

        for (int i = 0; i < test; i++) {
            Item item = w.createItem(Test01_AddItems.ITEM_NAME + i);
            Set<Item> toAdd = new HashSet<>();
            toAdd.add(item);

            Assert.assertFalse(w.addItemsAsync(s, toAdd).isReady());
        }

        Assert.assertTrue(s.items.isEmpty());
    }

    @Test
    public void asyncAddItemsWithoutRunningThreadsThenWaitForCompletion() {
        Warehouse w = Warehouse.createWarehouse();
        int size = 1000;
        int test = 2000;
        Shelf s = w.createShelf(size); // Thread not started
        Result<Boolean>[] results = new Result[test];
        Item[] items = new Item[size];

        long start = System.nanoTime();
        for (int i = 0; i < test; i++) {
            Item item = w.createItem(Test01_AddItems.ITEM_NAME + i);
            Set<Item> toAdd = new HashSet<>();
            toAdd.add(item);

            if (i < size)
                items[i] = item;

            results[i] = w.addItemsAsync(s, toAdd);
            Assert.assertFalse(results[i].isReady());
        }
        long added = System.nanoTime() - start;

        Assert.assertTrue(s.items.isEmpty());

        s.startThread();

        start = System.nanoTime();
        for (int i = 0; i < test; i++) {
            if (i < size)
                Assert.assertTrue(results[i].getResult());
            else
                Assert.assertFalse(results[i].getResult());
        }
        long done = System.nanoTime() - start;

        System.out.println(added);
        System.out.println(done);

        Assert.assertTrue(done > added);

        Set<Item> expected = Set.of(items);
        Assert.assertEquals(expected, w.getContents());
        Assert.assertEquals(expected, w.getContents(s));
        Assert.assertEquals(expected, s.items);
    }

    @Test
    public void asyncRemoveItemsWithoutRunningThreads() {
        Warehouse w = Warehouse.createWarehouse();
        int test = 2_500_000;
        Shelf s = w.createShelf(1); // Thread not started

        Set<Item> items = Set.of(w.createItem(Test01_AddItems.ITEM_NAME));
        for (int i = 0; i < test; i++)
            Assert.assertFalse(w.removeItemsAsync(s, items).isReady());

        Assert.assertTrue(s.items.isEmpty());
    }

    @Test
    public void asyncRemoveItemsWithoutRunningThreadsThenWaitForCompletion() {
        Warehouse w = Warehouse.createWarehouse();
        int size = 1000;
        int test = 2000;
        Shelf s = w.createShelf(size); // Thread not started
        Result<Boolean>[] results = new Result[test];
        Item[] items = new Item[size];

        for (int i = 0; i < test; i++) {
            Item item = w.createItem(Test01_AddItems.ITEM_NAME + i);
            Set<Item> toAdd = new HashSet<>();
            toAdd.add(item);

            if (i < size)
                items[i] = item;

            Assert.assertFalse(w.addItemsAsync(s, toAdd).isReady());
        }


        long start = System.nanoTime();
        for (int i = 0; i < test; i++) {
            if (i < size)
                results[i] = w.removeItemsAsync(s, Set.of(items[i]));
            else
                results[i] = w.removeItemsAsync(s, Set.of(w.createItem(Test01_AddItems.ITEM_NAME + i)));

            Assert.assertFalse(results[i].isReady());
        }
        long submitted = System.nanoTime() - start;

        Assert.assertTrue(s.items.isEmpty());

        s.startThread();

        start = System.nanoTime();
        for (int i = 0; i < test; i++) {
            if (i < size)
                Assert.assertTrue(results[i].getResult());
            else
                Assert.assertFalse(results[i].getResult());
        }
        long removed = System.nanoTime() - start;

        System.out.println(submitted);
        System.out.println(removed);

        Assert.assertTrue(removed > submitted);

        Set<Item> expected = new HashSet<>();
        Assert.assertEquals(expected, w.getContents());
        Assert.assertEquals(expected, w.getContents(s));
        Assert.assertEquals(expected, s.items);
    }

    @Test
    public void asyncGetContentsWithoutRunningThreads() {
        Warehouse w = Warehouse.createWarehouse();
        int test = 1_250_000;
        Shelf s = w.createShelf(1); // Thread not started

        for (int i = 0; i < test; i++) {
            Assert.assertFalse(w.getContentsAsync().isReady());
            Assert.assertFalse(w.getContentsAsync(s).isReady());
        }
    }

    @Test
    public void asyncGetContentsWithoutRunningThreadsThenWaitForCompletion() {
        Warehouse w = Warehouse.createWarehouse();
        int test = 1_000_000;
        Shelf s = w.createShelf(1); // Thread not started

        Result<Set<Item>>[] results = new Result[test];

        Set<Item> expected = Set.of(w.createItem(Test01_AddItems.ITEM_NAME));
        w.addItemsAsync(s, expected);

        long start = System.nanoTime();
        for (int i = 0; i < test; i+=2) {
            results[i]   = w.getContentsAsync();
            results[i+1] = w.getContentsAsync(s);

            Assert.assertFalse(results[i].isReady());
            Assert.assertFalse(results[i+1].isReady());
        }
        long submitted = System.nanoTime() - start;

        s.startThread();

        start = System.nanoTime();
        for (int i = 0; i < test; i+=2) {
            Assert.assertEquals(expected, results[i].getResult());
            Assert.assertEquals(expected, results[i+1].getResult());
        }
        long done = System.nanoTime() - start;

        System.out.println(submitted);
        System.out.println(done);

        Assert.assertTrue(done > submitted);
    }
}
