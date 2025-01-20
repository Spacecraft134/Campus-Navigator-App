// == CS400 Fall 2024 File Header Information ==
// Name: Rohith Ravikumar
// Email: rravikumar3@wisc.edu 
// Group: P2.1708
// Lecturer: Gary dahl
// Notes to Grader: None

import java.util.LinkedList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import java.util.NoSuchElementException;

public class HashtableMap <KeyType, ValueType> implements MapADT<KeyType, ValueType> {

    protected LinkedList<Pair>[] table = null;
    
    protected class Pair {

	public KeyType key;
	public ValueType value;

	public Pair(KeyType key, ValueType value) {
	    this.key = key;
	    this.value = value;
	}
    }

    @SuppressWarnings("unchecked")
    public HashtableMap(int capacity) {
	table = (LinkedList<Pair>[]) new LinkedList[capacity];
    }

    @SuppressWarnings("unchecked")
    public HashtableMap() {
	table = (LinkedList<Pair>[]) new LinkedList[64];
    }

    /**
     * Adds a new key,value pair/mapping to this collection.
     * @param key the key of the key,value pair
     * @param value the value that key maps to
     * @throws IllegalArgumentException if key already maps to a value
     * @throws NullPointerException if key is null
     */
    @Override
    @SuppressWarnings("unchecked")
    public void put(KeyType key, ValueType value) throws IllegalArgumentException {
	if(key == null) {
	    throw new NullPointerException("The key is not valid");
	}

        int index = Math.abs(key.hashCode()) % getCapacity();

	//Since its chaining method we need to create linkedList for each index
	if(table[index] == null) {
	    table[index] = new LinkedList<>();
	}
	else {
	    //Check if the key is already in the table
	    for(int i = 0; i < table[index].size(); i++) {
		Pair pair = table[index].get(i);
		if(pair.key.equals(key)) {
		    throw new IllegalArgumentException("The key is already in the Hash Table");
		}
	    }
	}

	//add the key value pair in the Hash Table
	table[index].add(new Pair(key,value));

	//Check if the load factor is greater than or equal to 80%, if so resize
	if(((double) getSize() / getCapacity()) >= 0.8) {
	     LinkedList<Pair>[] tableWithData = table;
	     table = (LinkedList<Pair>[])new LinkedList[tableWithData.length * 2];

	     for(int i = 0; i < tableWithData.length; i++) {
		 if(tableWithData[i] != null) {
		     //Check the linkedList within the index since its chaining 
		     for(int j = 0; j < tableWithData[i].size(); j++) {
			 Pair keyValue = tableWithData[i].get(j);

			 //Get the new index for the keyValue pair in the new linkedList capacity
			 int newResizeIndex = Math.abs(keyValue.key.hashCode()) % getCapacity();

			 //Create a new linked list for each index if not done so yet
			 if(table[newResizeIndex] == null) {
			     table[newResizeIndex] = new LinkedList<>();
			 }

			 //add new keyValuePair in the new index
			 table[newResizeIndex].add(new Pair(keyValue.key, keyValue.value));
		     }
		 }
	     }
	}
    }

    /**
     * Checks whether a key maps to a value in this collection.
     * @param key the key to check
     * @return true if the key maps to a value, and false is the
     *         key doesn't map to a value
     */
    @Override
    public boolean containsKey(KeyType key) {
	//Checks if the key is null
	if(key == null) {
	    return false;
	}
	else {
	    //Gets index based on the hashCode
	    int index = Math.abs(key.hashCode()) % getCapacity();
	    if(table[index] != null) {
		for(int i = 0; i < table[index].size(); i++) {
		    Pair keyValue = table[index].get(i);
		    //If there is a match then returns true, else false
		    if(keyValue.key.equals(key)) {
			return true;
		    }
		}
	    }
	}
	return false;
    }

    /**
     * Retrieves the specific value that a key maps to.
     * @param key the key to look up
     * @return the value that key maps to
     * @throws NoSuchElementException when key is not stored in this
     *         collection
     */
    @Override
    public ValueType get(KeyType key) throws NoSuchElementException {
	//Checks if the key is null
	if(key == null) {
	    throw new NoSuchElementException("The key is not stored");   
	}
	//Gets index based on the hashCode
	int index = Math.abs(key.hashCode()) % getCapacity();
	if(table[index] != null) {
	    for(int i = 0; i < table[index].size(); i++) {
		Pair keyValue = table[index].get(i);
		 //If there is a match then returns its value, else throws exception
		if(keyValue.key.equals(key)) {
		    return keyValue.value;
		}
	    }
	}
	throw new NoSuchElementException("The key is not stored in the Hashtable");
    }

    /**
     * Remove the mapping for a key from this collection.
     * @param key the key whose mapping to remove
     * @return the value that the removed key mapped to
     * @throws NoSuchElementException when key is not stored in this
     *         collection
     */
    @Override
    public ValueType remove(KeyType key) throws NoSuchElementException {
	//Checks if the key is null
	if(key == null) {
            throw new NoSuchElementException("The key is not stored");
        }
	//Gets index based on the hashCode
        int index = Math.abs(key.hashCode()) % getCapacity();
        if(table[index] != null) {
            for(int i = 0; i < table[index].size(); i++) {
                Pair keyValue = table[index].get(i);
		//If there is a match then removes it keyValue pair then returns the value that is removed, else throws exception
                if(keyValue.key.equals(key)) {
		    table[index].remove(i);
		    return keyValue.value;
     
		}
	    }
	}
	throw new NoSuchElementException("The key is not stored in the Hashtable");
    }

     /**
     * Removes all key,value pairs from this collection.
     */
    @Override
    public void clear() {
	//Makes all indexs in the table null
	for(int i = 0; i < getCapacity(); i++) {
	    table[i] = null;
	}
    }

    /**
     * Retrieves the number of keys stored in this collection.
     * @return the number of keys stored in this collection
     */
    @Override
    public int getSize() {
	int size = 0;
	//Gets the size of each linkedlist per index
	for(int i = 0; i < getCapacity(); i++) {
	    if(table[i] != null) {
		size += table[i].size();
	    }
	}
	return size;
    }

    /**
     * Retrieves this collection's capacity.
     * @return the size of te underlying array for this collection
     */
    @Override
    public int getCapacity() {
	//returns the size of the table linkedList
	return table.length;
    }

    /**
     * Gets the list of keys in the table
     * @return the keys in the table
     */   
    @Override
    public LinkedList<KeyType> getKeys() {
        LinkedList<KeyType> keys = new LinkedList<>();
        for (LinkedList<Pair> keyValue : table) {
            if (keyValue != null) {
                for (Pair pair : keyValue) {
                    keys.add(pair.key);
                }
            }
        }
        return keys;
    }

    

    @Test
    /**
     * Test to ensure put method correctly stores key-value pairs and get method retrieves them.
     */
    public void testPutAndGetMethod() {
	//Creates a test map that contains string keys and integer values
 	HashtableMap<String, Integer> testMap = new HashtableMap<>();
	// stores different food key with ther value cost pair
	testMap.put("Pizza", 20);
	testMap.put("Chicken Wings", 10);
	testMap.put("Burger", 15);

	//Checks to see if the get method returns value when provided the key
	Assertions.assertEquals(20, testMap.get("Pizza"));
	Assertions.assertEquals(10, testMap.get("Chicken Wings"));
	Assertions.assertEquals(15, testMap.get("Burger"));
    }

    @Test
     /**
     * Test to check if containsKey identifies existing keys.
     */
    public void testContainKeyMethod() {
	//Creates a test map that contains string keys and integer values
	HashtableMap<String, Integer> testMap = new HashtableMap<>();
	// stores different food key with ther value cost pair
	testMap.put("Pasta", 14);
        testMap.put("Bread Stick", 5);

	//tests contain method by checking the the key exists
	Assertions.assertTrue(testMap.containsKey("Pasta"));
	Assertions.assertTrue(testMap.containsKey("Bread Stick"));
    }

    @Test
    /**
     * Test to ensure remove deletes the key-value pair and returns the correct value.
     */
    public void testRemoveMethod() {
	 //Creates a test map that contains string keys and integer values
        HashtableMap<String, Integer> testMap = new HashtableMap<>();
        // stores different food key with ther value cost pair
        testMap.put("Burrito", 25);
        testMap.put("Cheesecake", 7);

	//Removes the mapping and stores the value
	Integer removedValue = testMap.remove("Cheesecake");
       
        //tests contain method by checking the the key exists
        Assertions.assertEquals(7, removedValue);
        Assertions.assertFalse(testMap.containsKey("Cheesecake"));
    }

    @Test
    /**
     * Test to verify that clear removes all key-value pairs from the map.
     */
    public void testClearMethod() {
        //Creates a test map that contains string keys and integer values
        HashtableMap<String, Integer> testMap = new HashtableMap<>();
        // stores different food key with ther value cost pair
        testMap.put("Pancake", 11);
        testMap.put("Mozzarella", 2);

        //Removes all the keys in the map
        testMap.clear();

        //tests if the clear method cleared all the mappings
	Assertions.assertEquals(0, testMap.getSize());
    }

    @Test
    /**
     * Test to check if the capacity of the map is retrieved correctly.
     */
    public void testCapacityMethod() {
	//Creates a test map that contains string keys and integer values
        HashtableMap<String, Integer> testMap = new HashtableMap<>();
	//Checking if the default capcity is 64
	Assertions.assertEquals(64, testMap.getCapacity());

	//Checking for provided capcity
	HashtableMap<String, Integer> testMapWithCapcaity = new HashtableMap<>(34);
	Assertions.assertEquals(34, testMapWithCapcaity.getCapacity());
    }
}
