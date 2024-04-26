import java.util.HashMap;
import java.util.Map;

public class InMemoryDB {
    private Map<String, Integer> data;
    private Map<String, Integer> transactionData;
    private boolean transactionInProgress;

    public InMemoryDB() {
        this.data = new HashMap<>();
        this.transactionData = new HashMap<>();
        this.transactionInProgress = false;
    }

    public Integer get(String key) {
        if (data.containsKey(key)) {
            return data.get(key);
        }
        return null;
    }

    public void put(String key, int val) {
        if (!transactionInProgress) {
            throw new RuntimeException("No transaction in progress");
        }
        transactionData.put(key, val);
    }

    public void beginTransaction() {
        if (transactionInProgress) {
            throw new RuntimeException("Transaction already in progress");
        }
        transactionInProgress = true;
        transactionData = new HashMap<>(data);
    }

    public void commit() {
        if (!transactionInProgress) {
            throw new RuntimeException("No transaction in progress");
        }
        data.putAll(transactionData);
        transactionInProgress = false;
        transactionData.clear();
    }

    public void rollback() {
        if (!transactionInProgress) {
            throw new RuntimeException("No transaction in progress");
        }
        transactionInProgress = false;
        transactionData.clear();
    }

    public static void main(String[] args) {
    InMemoryDB inMemoryDB = new InMemoryDB();

    // should return null, because A doesn’t exist in the DB yet
    System.out.println(inMemoryDB.get("A"));

    try {
        // should throw an error because a transaction is not in progress
        inMemoryDB.put("A", 5);
    } catch (RuntimeException e) {
        System.out.println("Error: " + e.getMessage());
    }

    // starts a new transaction
    inMemoryDB.beginTransaction();

    // set’s value of A to 5, but its not committed yet
    inMemoryDB.put("A", 5);

    // should return null, because updates to A are not committed yet
    System.out.println(inMemoryDB.get("A") + " should be null");

    // update A’s value to 6 within the transaction
    inMemoryDB.put("A", 6);

    // commits the open transaction
    inMemoryDB.commit();

    // should return 6, that was the last value of A to be committed
    System.out.println(inMemoryDB.get("A"));

    try {
        // throws an error, because there is no open transaction
        inMemoryDB.commit();
    } catch (RuntimeException e) {
        System.out.println("Error: " + e.getMessage());
    }

    try {
        // throws an error because there is no ongoing transaction
        inMemoryDB.rollback();
    } catch (RuntimeException e) {
        System.out.println("Error: " + e.getMessage());
    }

    // should return null because B does not exist in the database
    System.out.println(inMemoryDB.get("B"));

    // starts a new transaction
    inMemoryDB.beginTransaction();

    // Set key B’s value to 10 within the transaction
    inMemoryDB.put("B", 10);

    // Rollback the transaction - revert any changes made to B
    inMemoryDB.rollback();

    // Should return null because changes to B were rolled back
    System.out.println(inMemoryDB.get("B"));
}
}
