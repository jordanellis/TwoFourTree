package termproject;
import java.util.Random;
/**
 * Title:       Term Project 2-4 Trees
 * Description: This class implements the dictionary interface and uses a 
 *  two-four tree to do it. The three main methods that we created for the
 *  interface are removeElement, findElement, and insertElement. However we
 *  also created helper methods as we saw fit to help keep the main methods
 *  more readable and easier to understand.
 * Copyright:
 * Company:
 * @author Jordan Ellis
 * @version 1.0
 */
public class TwoFourTree
        implements Dictionary {

    private Comparator treeComp;
    private int size = 0;
    private TFNode treeRoot = null;

    public TwoFourTree(Comparator comp) {
        treeComp = comp;
    }

    private TFNode root() {
        return treeRoot;
    }

    private void setRoot(TFNode root) {
        treeRoot = root;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return (size == 0);
    }

    /**
     * Searches dictionary to determine if key is present
     * @param key to be searched for
     * @return object corresponding to key; null if not found
     */
    @Override
    public Object findElement(Object key) {
        //Finds the node that the sought after data is in
        TFNode node = this.findNode(key);
        //Checks to see if the node is present or not
        if (node == null){
            //If the node is not present then that data is not currently inside
            //the data structure
            return null;
        }
        //Finds the index of the sought after data
        int index = FFGTE(node, key);
        //Returns the data that was sought after
        return node.getItem(index).element();
    }

    /**
     * Inserts provided element into the Dictionary
     * @param key of object to be inserted
     * @param element to be inserted
     */
    @Override
    public void insertElement(Object key, Object element) {
        //Makes a new item to hold the key and element to be placed on the 
        //data structure
        Item newItem = new Item(key,element);
        //Begins the search for which node to insert the data into
        TFNode searchNode = root();
        //This node is used so that we can have a accurate node if it possiblly
        //a leaf
        TFNode insertionNode = root();
        //An index to help with later accessing of data
        int index = 0;
        
        //Checks to see if the current root is null
        if(insertionNode == null) {
            //If the current root is null then the tree is empty and we make
            //a new node 
            TFNode newNode = new TFNode();
            //We add the new item in the node
            newNode.addItem(index, newItem);
            //Set the new node that we made to be the root of the tree
            setRoot(newNode);
            //Increment the size for the number of nodes in the data structure
            size++;
        } else { //If the root isn't null 
            //Then we search the tree until we reach the bottom
            while(searchNode != null) {
                //We find the appropriate indexes of children to keep walking
                //down the tree
                index = FFGTE(searchNode, key);
                //Have the node we want to insert at catch up to the searching
                //node so that we wont lose where we are when the node is null
                insertionNode = searchNode;
                //Move the search node down the appropriate child pointer to the
                //next node down the tree
                searchNode = searchNode.getChild(index);
            }
            //After we have found the proper place to insert we place the item
            //into the node at its proper index in the node
            if(isLeaf(insertionNode)) {
                insertionNode.insertItem(index, newItem);
                //Check for overflow in the node we inserted data into
                overflow(insertionNode);
            } else { //its not a leaf
                TFNode inorderNode = findInorderSuccessor(insertionNode, key);
                inorderNode.insertItem(0, newItem);
                //Check for overflow in the node we inserted data into
                overflow(inorderNode);
            }
        }
    }

    /**
     * Searches dictionary to determine if key is present, then
     * removes and returns corresponding object
     * @param key of data to be removed
     * @return object corresponding to key
     * @exception ElementNotFoundException if the key is not in dictionary
     */
    @Override
    public Object removeElement(Object key) throws ElementNotFoundException {
        //Find the node that the sought after data is in
        TFNode newNode = findNode(key);
        //If the node is null then the data was not in the structure
        if(newNode == null) {
            //Throws an exception to let the user know the data was not in the
            //structure
            throw new ElementNotFoundException("Element not found.");
        }
        //Declare an item to be used later when we need to return 
        Item returnItem = null;
        //Checks to see if the node is a leaf or not
        if(!(isLeaf(newNode))) {
            //If the node is not a leaf then we find its inorder successor 
            TFNode inorderNode = findInorderSuccessor(newNode, key);
            //We make a new item to hold onto the inorder nodes data
            Item newItem = inorderNode.getItem(0);
            //Removes the item from the inorderNode
            inorderNode.removeItem(0);
            //Find the appropriate index where the stored data is
            int index = FFGTE(newNode, key);
            //Make our returning item equal to the item that needs to be removed
            //and moves the item from the inorder node to the node
            returnItem = newNode.replaceItem(index, newItem);
            //Checks to make sure we have not underflown the node by removing 
            //data from it
            underflow(inorderNode);
        } else { //is a leaf node
            //Finds the appropriate index of the data in the node
            int index = FFGTE(newNode, key);
            //Checks to see if there is only one node and one piece of data 
            //still on the data structure and to remove the node if it is
            if(newNode == root() && root().getNumItems() == 1) {
                //Sets the root to null 
                setRoot(null);
                //Decrements the number of nodes in the structure
                size--;
            }
            //Checks to see how many items are in the node
            if(newNode.getNumItems() > 1){
                //If they are greater than one we do a shifting delete
                returnItem = newNode.removeItem(index);
            } else {
                //Otherwise we do a non-shifting delete
                returnItem = newNode.deleteItem(index);
            }
            //Checks to see if we have underflown the node after deletion
            underflow(newNode);
        }
        //Return the item's element that was sought after
        return returnItem.element();
    }
    /*
     * This is a helper method to help find the appropriate index of where data
     * should be removed from and inserted into
     */
    private int FFGTE(TFNode node, Object key) {
        //Index variable to keep track of where to insert
        int index = 0;
        //Make a new item to be the first item in the node
        Item newItem = (Item)node.getItem(index);
        //Walks through the node to compare the data in the node to the one
        //that is sought after
        while(!(treeComp.isGreaterThanOrEqualTo(newItem.key(), key))){
            //Increments the index
            index++;
            //Checks to make sure we dont go off of the item array
            if(index > node.getNumItems() -1) {
                //Breaks from the while loop if the index gets too large
                break;
            } else {
                //Otherwise we move to the next item
                newItem = (Item)node.getItem(index);
            }
        }
        if(newItem.key() == key) {
            index++;
        }
        //Returns the index that should be accessed
        return index;
    }
    /*
     * This method is the main helper function to decide and fix if we have 
     * overflow a node after an insertion
     */
    private void overflow(TFNode node) {
        //Checks to see if the node has been overflown
        if(node.getNumItems() == 4) {
            //Checks to see of the overflow occurred
            if(node == root()) {
                //Make a new node
                TFNode newNode = new TFNode();
                newNode.addItem(0, node.deleteItem(3));
                //Make a new root 
                TFNode newRoot = new TFNode();
                newRoot.addItem(0, node.deleteItem(2));
                //Set the children of the new node
                newNode.setChild(0, node.getChild(3));
                newNode.setChild(1, node.getChild(4));
                //Checks to see if the root has children or not
                if(node.getChild(0) != null) {
                    //Set the parent of the splitted children
                    node.getChild(3).setParent(newNode);
                    node.getChild(4).setParent(newNode);
                    //Null the 3 and 4 children pointers of the split node
                    node.setChild(3, null);
                    node.setChild(4, null);
                }
                
                //Set the parent of the new node
                newNode.setParent(newRoot);
                //Set the parent of the splitted node
                node.setParent(newRoot);
                //Set the children of the new root
                newRoot.setChild(0, node);
                newRoot.setChild(1, newNode);
                //Set the root to the new root
                setRoot(newRoot);
                //increments the size for the two new nodes added to the tree
                size += 2;
            } else {
                //Make a new node for the splitted data
                TFNode newNode = new TFNode();
                //Move the last item in the current node to the new node
                newNode.addItem(0, node.deleteItem(3));
                //Make two new nodes to be the children that need to be moved
                //and hook up the pointers between the new node and the new 
                //children
                TFNode childOne = node.getChild(3);
                TFNode childTwo = node.getChild(4);
                //Sets the new children to be the children of our new node
                newNode.setChild(0, childOne);
                newNode.setChild(1, childTwo);
                //Checks to make sure that the child is not null
                if(childOne != null) {
                    //If the child isn't null then we set its parent to the
                    //new node
                    childOne.setParent(newNode);
                }
                //Checks to make sure that the child is not null
                if(childTwo != null) {
                    //If the child isn't null then we set its parent to the 
                    //new node
                    childTwo.setParent(newNode);
                }
                //Hold onto the item that will be moved to the parent
                Item newItem = node.deleteItem(2);
                //Find the insertion point for the new item
                int insert = FFGTE(node.getParent(), newItem.key());
                //Insert the item into the current nodes parent
                node.getParent().insertItem(insert, newItem);
                //Set the parent of the new node to the parent of the current node
                newNode.setParent(node.getParent());
                //Set the parents children
                node.getParent().setChild(insert + 1, newNode);
                //increments the size of the tree
                size++;
            }
            //Calls overflow on the parent to check for overflow all the way 
            //up the tree as is necessary
            overflow(node.getParent());
        }
    }
    /*
     * This is the main helper function to decide if an underflow has happened
     * in a node and calls the appropriate fix up routine to fix the tree
     */
    private void underflow(TFNode node) {
        //Checks to see if the node has underflown and parent is not null
        if(node.getNumItems() == 0 && node.getParent() != null) {
            //Checks to see if a left transfer is possible
            if(lTransferPossible(node)) {
                //If a left transfer is possible then it will call the 
                //left transfer routine
                lTransfer(node);
            }
            //Checks to see if a right transfer is possible
            else if(rTransferPossible(node)) {
                //If a right transfer is possible then it will call the 
                //right transfer routine
                rTransfer(node);
            }
            //Checks to see if a left fusion is possible
            else if(lFusionPossible(node)) {
                //If a left fusion is possible then it will call the
                //left fusion routine
                lFusion(node);
                //Calls the underflow method on the node's parent to make sure
                //we have not caused an underflow from our fusion and if we did
                //to fix it
                underflow(node.getParent());
            } 
            //Otherwise we will do a right fusion
            else {
                //If none of the other fixes are possible then it will call
                //the right fusion routine
                rFusion(node);
                //Calls the underflow method on the node's parent ot make sure
                //we have not cause an underflow from our fusion and if we did
                //to fix it
                underflow(node.getParent());
            }
        }
    }
    /*
     * This is the method that will fix the underflow by performing a transfer
     * of some data from the current nodes left sibling to keep the structure
     * balanced
     */
    private void lTransfer(TFNode node) {
        //Finds which child this node is
        int index = whichChildIsThis(node);
        //Makes a parent node by getting the nodes parent
        TFNode parentNode = node.getParent();
        //Find the left sibling by finding the parent's child one index to the
        //left of the current node
        TFNode leftSibling = parentNode.getChild(index - 1);
        //A child node to keep track of the child that will need to be moved
        //during the transfer
        TFNode child = leftSibling.getChild(leftSibling.getNumItems());
        //Gets the last item in the left sibling and does a non-shifting removal of
        //the item
        Item newItem = leftSibling.deleteItem(leftSibling.getNumItems() - 1);
        //Swaps the items that we got from the left sibling and the parents
        //item that corresponds to where the left sibling is
        newItem = parentNode.replaceItem(index - 1, newItem);
        //Inserts the item from the parent into the node 
        node.insertItem(0, newItem);
        //Sets the child from the left sibling to the current nodes child
        node.setChild(0, child);
        //Checks to see if the child is null
        if(child != null) {
            //If the child is not null then it will set its parent to the
            //current node
            child.setParent(node);
        }
    }
    /*
     * This is the method that will fix the underflow by transfering some of 
     * the data from its right sibling to the current node to keep the 
     * data structure balanced
     */
    private void rTransfer(TFNode node) {
        //Finds which child index the current node is
        int index = whichChildIsThis(node);
        //Makes a parent node to hold onto the parent of the current node
        TFNode parentNode = node.getParent();
        //Finds the right sibling of the current node by getting the child to
        //the right of the current nodes index
        TFNode rightSibling = parentNode.getChild(index + 1);
        //Gets the child of the right sibling that will need to be moved during 
        //the transfer
        TFNode child = rightSibling.getChild(0);
        //Shiftingly removes the farthest left item of the right sibling
        Item newItem = rightSibling.removeItem(0);
        //Replaces the item in the parent with the item from the right sibling
        //and gives us the item from the parent
        newItem = parentNode.replaceItem(index, newItem);
        //Non-shiftingly adds the item from the parent into the current node
        node.addItem(0, newItem);
        //Sets the child from the right sibling to be this nodes second child
        node.setChild(1, child);
        //Checks to see if the child is null
        if(child != null) {
            //If the child isn't null then it will set the parent to the
            //current node
            child.setParent(node);
        }
    }
    /*
     * This is the method that will fix the underflow by fusing some data from
     * the left sibling and the parent of the current node to keep the data
     * structure balanced
     */
    private void lFusion(TFNode node) {
        //Finds which child this node is of its parent
        int index = whichChildIsThis(node);
        //Gets a parentNode to be able to find the left sibling
        TFNode parentNode = node.getParent();
        //Finds the left sibling by getting the child one less than the current
        //node
        TFNode leftSibling = parentNode.getChild(index - 1);
        //Makes a child node of the child that needs to be moved during the fusion
        TFNode child = node.getChild(0);
        //Gets the item from the parent that corresponds to the item before
        //the current node
        Item newItem = parentNode.removeItem(index - 1);
        //Reset the left sibling pointer
        parentNode.setChild(index-1, leftSibling);
        //Adds the item from the parent into the left siblings farthest possible
        //item placement
        leftSibling.addItem(leftSibling.getNumItems(), newItem);
        //sets the child of the left sibling to the child of the underflown node
        leftSibling.setChild(leftSibling.getNumItems(), child);
        //Checks to see if the child is null
        if(child != null) {
            //If the child isn't null then it will set its parent to the left
            //sibling
            child.setParent(leftSibling);
        }
        //Derements the number of nodes in the structure
        size--;
        //If the current node's parent is the root and the root is empty then
        //the left sibling will become the new root
        if(parentNode == root() && root().getNumItems() == 0){
            //Sets the left sibling to be the new root
            setRoot(leftSibling);
            //Nulls out the left sibling's parent's child pointer
            parentNode.setChild(0, null);
            //Nulls out the left sibling parent pointer
            leftSibling.setParent(null);
            //Decrements the number of nodes in the structure
            size--; 
        }
    }
    /*
     * This is the method that will fix the underflow by fusing some of the data 
     * from the right sibling and the parent of the current node to keep the
     * data structure balanced
     */
    private void rFusion(TFNode node) {
        //Finds the index of the node in its parent's child array
        int index = whichChildIsThis(node);
        //Makes a parent node to be able to access the current nodes right 
        //sibling
        TFNode parentNode = node.getParent();
        //Makes a node to be able to keep track of the right sibling
        TFNode rightSibling = parentNode.getChild(index + 1);
        //Makes a node to keep track of the child that needs to get moved
        //during the fusion
        TFNode child = node.getChild(0);
        //Gets the item from the parent node that corresponds the the item
        //before the current node
        Item newItem = parentNode.removeItem(index);
        //Performs a shifting insert of the item from the parent into the right
        //sibling
        rightSibling.insertItem(0, newItem);
        //Sets the child of the right sibling to be the old child of the under
        //flown node
        rightSibling.setChild(0, child);
        //Checks to see if the child is null
        if(child != null) {
            //If the child is not null then it will set its parent to the right
            //sibling
            child.setParent(rightSibling);
        }
        //Decrements the number of nodes in the structure
        size--;
        //If the current node's parent is the root and the root is empty then
        //the right sibling will become the new root
        if(parentNode == root() && root().getNumItems() == 0) {
            //Sets the right sibling to be the root
            setRoot(rightSibling);
            //Nulls out of right sibling's parent's child pointer
            parentNode.setChild(0, null);
            //Nulls out the right sibling's parent pointer
            rightSibling.setParent(null);
            //Decrements the number of nodes in the structure
            size--; 
        }
    }
    /*
     * This method helps to find which child this node of its parent node in the
     * data structure
     */
    private int whichChildIsThis(TFNode node) {
        //Gets a parent node so that we can search it to find where the current
        //node is in relation to its parent
        TFNode parentNode = node.getParent();
        //An index variable to keep track of where the node is indexed at
        int index = 0;
        //For loop to check the children of the parent node
        for(int i = 0; i < parentNode.getNumItems() + 1; i++) {
            //If the current node equals the parent nodes child at a particular
            //index then we found where it is located
            if(node == parentNode.getChild(i)) {
                //Makes index equal to the child index that we need
                    index = i;
            }
        }
        //Returns the index of the current node
        return index; 
    }
    /*
     * This is the main helper method we use to find the node that holds the
     * data that we want to access
     */
    private TFNode findNode(Object key) {
        //Makes a node to search the tree for the node that holds the data we 
        //want to access
        TFNode searchNode = root();
        //Am index variable to keep track of indexes while moving down the tree
        int index = 0;
        //We continue to search the tree until we find the node or we reach the
        //bottom of the tree
        while(searchNode != null) {
            //Finds the proper child to go down to find the data
            index = FFGTE(searchNode, key);
            //Makes sure that the index doesn't get too high and that the items key is equal to the
            //key in the node
            if(index < searchNode.getNumItems() && treeComp.isEqual(searchNode.getItem(index).key(), key)) {
                //We found the right node and we return it
                return searchNode;
            }
            //Move the searchNode farther down the tree to try to find the data
            searchNode = searchNode.getChild(index);
        }
        //If we can't find the data we return null to signify that we have not
        //found the data
        return null;
    }
    /*
     * This is a helper method that finds the inorder successor to the node
     * that we pass it for help in making deletions
     */
    private TFNode findInorderSuccessor(TFNode node, Object key) {
        //Finds the index of the current child pointer that we are at
        int index = FFGTE(node, key);
        //Goes to the child to the right of the current child pointer we are at 
        TFNode leafNode = node.getChild(index + 1);
        //Then we continue to keep going down the farthest left child of the node
        //to find the inorder successor
        while(leafNode.getChild(0) != null) {
            //Move down the tree properly
            leafNode = leafNode.getChild(0);
        }
        //We return node that is the inorder successor
        return leafNode;
    }
    /*
     * This is a helper method that helps us for figuring out different cases
     * in our other methods if we are at a leaf or not
     */
    private boolean isLeaf(TFNode node) {
        //Returns where it is a leaf or not
        return (node.getChild(0) == null);
    }
    /*
     * This is a helper method that decideds if a left transfer is possible
     * on the node that we pass it
     */
    private boolean lTransferPossible(TFNode node) {
        //Make a parent node to be able to access the left sibling and see if 
        //a left transfer is possible
        TFNode parentNode = node.getParent();
        //Make a temporary tf node to act as the left sibling
        TFNode leftSibling = new TFNode();
        //If the parent node is null then a left transfer is not possibe
        if(parentNode == null) {
            //And we return false
            return false;
        }
        //Then we check to make sure that the current node is not the farthest
        //possible child of the parent
        if(node != parentNode.getChild(0)) {
            //Then we search the parent to find the location of the current node
            for(int i = 0; i < parentNode.getNumItems() + 1; i++) {
                if(node == parentNode.getChild(i)) {
                    //Then we set the left sibling to the current nodes position
                    //one to the left
                    //int index = whichChildIsThis(node);
                    leftSibling = parentNode.getChild(i - 1);
                    //Then we check to make sure that the left sibling has more
                    //than one item in it
                    return (leftSibling.getNumItems() > 1);
                }
            }
        }
        //Otherwise we return false
        return false;
    }
    /*
     * This is a helper method that decides if a right transfer is possible on
     * the node that we pass it
     */
    private boolean rTransferPossible(TFNode node) {
        //Makes a parent node so that we can access the children around
        //the current node
        TFNode parentNode = node.getParent();
        //Make a right sibling to be used later
        TFNode rightSibling = new TFNode();
        //Checks to see if the parent node is null
        if(parentNode == null) {
            //If the parent node is null then a right transfer is not possible
            return false;
        }
        //Checks to make sure that the current node it not the farthest right
        //child
        if(node != parentNode.getChild(parentNode.getNumItems())) { 
            //Then checks to find the child in the parent's child array
            for(int i = 0; i < parentNode.getNumItems() + 1; i++) {
                //If the current node equals the parent's child at a particular
                //location then we have found the current node's position
                if(node == parentNode.getChild(i)) {
                    //Then we set the right sibling to the position to the right
                    //of the current node
                    //int index = whichChildIsThis(node);
                    rightSibling = parentNode.getChild(i + 1);
                    //Checks to see if the right sibling is null
                    if(rightSibling == null){
                        //If the right sibling is null then a right transfer is 
                        //not possible and we return false
                        return false;
                    }
                    //Then we check to see if the right sibling has more than
                    //one item
                    return (rightSibling.getNumItems() > 1);
                }
            }
        }
        //Otherwise we return false
        return false;
    }
    /*
     * This is a helper method that decides if a left fusion is possible on the
     * node that we passed it
     */
    private boolean lFusionPossible(TFNode node) {
        //Makes a parent node so that we can access the sibling of the current node
        TFNode parentNode = node.getParent();
        //Checks to see if the parent node is null
        if(parentNode == null) {
            //If the parent node is null then a left fusion is not possible and
            //we return false
            return false;
        }
        //Otherwise we just check to see if the current node is not the farthest
        //left child of its parent
        return (node != parentNode.getChild(0));
    }
    /*
     *  Main: in order to test some of the functionality of our two four tree
     */
    public static void main(String[] args) {
        Comparator myComp = new IntegerComparator();
        TwoFourTree myTree = new TwoFourTree(myComp);

        Integer myInt1 = new Integer(47);
        myTree.insertElement(myInt1, myInt1);
        Integer myInt2 = new Integer(83);
        myTree.insertElement(myInt2, myInt2);
        Integer myInt3 = new Integer(22);
        myTree.insertElement(myInt3, myInt3);
        Integer myInt4 = new Integer(16);
        myTree.insertElement(myInt4, myInt4);
        Integer myInt5 = new Integer(49);
        myTree.insertElement(myInt5, myInt5);
        Integer myInt6 = new Integer(100);
        myTree.insertElement(myInt6, myInt6);
        Integer myInt7 = new Integer(38);
        myTree.insertElement(myInt7, myInt7);
        Integer myInt8 = new Integer(3);
        myTree.insertElement(myInt8, myInt8);
        Integer myInt9 = new Integer(53);
        myTree.insertElement(myInt9, myInt9);
        Integer myInt10 = new Integer(66);
        myTree.insertElement(myInt10, myInt10);
        Integer myInt11 = new Integer(19);
        myTree.insertElement(myInt11, myInt11);
        Integer myInt12 = new Integer(23);
        myTree.insertElement(myInt12, myInt12);
        Integer myInt13 = new Integer(24);
        myTree.insertElement(myInt13, myInt13);
        Integer myInt14 = new Integer(88);
        myTree.insertElement(myInt14, myInt14);
        Integer myInt15 = new Integer(1);
        myTree.insertElement(myInt15, myInt15);
        Integer myInt16 = new Integer(97);
        myTree.insertElement(myInt16, myInt16);
        Integer myInt17 = new Integer(94);
        myTree.insertElement(myInt17, myInt17);
        Integer myInt18 = new Integer(35);
        myTree.insertElement(myInt18, myInt18);
        Integer myInt19 = new Integer(51);
        myTree.insertElement(myInt19, myInt19);
        
        
        myTree = new TwoFourTree(myComp);
        final int TEST_SIZE = 10000;
        Random generator = new Random(6);
        int randomNumber = 0;
        int [] myArray = new int[TEST_SIZE];

        for (int i = 0; i < TEST_SIZE; i++) {
            randomNumber = generator.nextInt(TEST_SIZE / 3);
            myArray[i] = (Integer)randomNumber;
            myTree.insertElement(new Integer(randomNumber), new Integer(randomNumber));
        }
        myTree.printAllElements();
        System.out.println("removing");
        for (int i = 0; i < TEST_SIZE; i++) {
             if (i > TEST_SIZE - 15) {
               System.out.println("      removing" + myArray[i]);
            }
            
            int out = (Integer)myTree.removeElement(new Integer(myArray[i]));
            if (out != myArray[i]) {
                throw new TwoFourTreeException("main: wrong element removed");
            }
            if (i > TEST_SIZE - 15) {
                myTree.printAllElements();
            }
        }
        System.out.println("done");
    }

    public void printAllElements() {
        int indent = 0;
        if (root() == null) {
            System.out.println("The tree is empty");
        }
        else {
            printTree(root(), indent);
        }
    }

    public void printTree(TFNode start, int indent) {
        if (start == null) {
            return;
        }
        for (int i = 0; i < indent; i++) {
            System.out.print(" ");
        }
        printTFNode(start);
        indent += 4;
        int numChildren = start.getNumItems() + 1;
        for (int i = 0; i < numChildren; i++) {
            printTree(start.getChild(i), indent);
        }
    }

    public void printTFNode(TFNode node) {
        int numItems = node.getNumItems();
        for (int i = 0; i < numItems; i++) {
            System.out.print(((Item) node.getItem(i)).element() + " ");
        }
        System.out.println();
    }

    // checks if tree is properly hooked up, i.e., children point to parents
    public void checkTree() {
        checkTreeFromNode(treeRoot);
    }

    private void checkTreeFromNode(TFNode start) {
        if (start == null) {
            return;
        }

        if (start.getParent() != null) {
            TFNode parent = start.getParent();
            int childIndex = 0;
            for (childIndex = 0; childIndex <= parent.getNumItems(); childIndex++) {
                if (parent.getChild(childIndex) == start) {
                    break;
                }
            }
            // if child wasn't found, print problem
            if (childIndex > parent.getNumItems()) {
                System.out.println("Child to parent confusion");
                printTFNode(start);
            }
        }

        if (start.getChild(0) != null) {
            for (int childIndex = 0; childIndex <= start.getNumItems(); childIndex++) {
                if (start.getChild(childIndex) == null) {
                    System.out.println("Mixed null and non-null children");
                    printTFNode(start);
                }
                else {
                    if (start.getChild(childIndex).getParent() != start) {
                        System.out.println("Parent to child confusion");
                        printTFNode(start);
                    }
                    for (int i = childIndex - 1; i >= 0; i--) {
                        if (start.getChild(i) == start.getChild(childIndex)) {
                            System.out.println("Duplicate children of node");
                            printTFNode(start);
                        }
                    }
                }

            }
        }

        int numChildren = start.getNumItems() + 1;
        for (int childIndex = 0; childIndex < numChildren; childIndex++) {
            checkTreeFromNode(start.getChild(childIndex));
        }

    }
}   
