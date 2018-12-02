import java.io.*;
import java.util.*;
import java.util.regex.*;

/*
 1. node structure for FibbonacciHeap.
 2. node contains all the required parameters like freq(frequency), name (the word parsed), childCut, degree, parent, child, left, right.
 3. constructor takes the name and frequency when initialised and assigns it to variables.
*/

class node {
	node parent, child, left, right;
	int freq;
	String name;
	boolean childCut = false;
	int degree = 0;

	node(int freq, String s) {
		this.freq = freq;
		this.parent = null;
		this.left = null;
		this.name = s;
		this.right = null;
	}
}

//contains all the variables and methods for FibbonacciHeap.

public class FibbonacciHeap {
	node max;

//constructor initializes max to null.	
	FibbonacciHeap() {
		max = null;
	}
/*
 1. method to insert a node into the heap.
 2. if the max is null(there are no elements in the heap), make the new node max and link it to itself making circular linked list.
 3. if max is not null attach the new node to the right of max, this is done by changing the required fields in the node, max node and max.right node.
 4. if the new frequency is greater than max, make it max.
*/
	public void insert(node n) {
		if (max == null) {
			max = n;
			max.right = max;
			max.left = max;
		} else {
			n.left = max;
			n.right = max.right;
			n.right.left = n;
			n.left.right = n;
			if (n.freq > max.freq) {
				max = n;
			}
		}
	}

/*
 1. this method return the max element in the heap.
 2. if there is no max,i.e, no elements in the heap return null;
 3. if there is only one node in the heap(max) and degree is 0 make max null and return max.
 4. if there are many elements in the heap and degree is 0, remove max from the heap and do pairwisecombine().
 5. if degree is greater than 0, take the child list of max and add it to the root list.
*/	
	
	public node extractMax() {
		node x = max;
		if(x!= null) {
			int deg = x.degree;
			if(deg == 0) {
				if(x.right == x) {
					//if deg is 0 and there is only one element make max null and return max.
					max = null;
					return x;
				}
				else {
					//remove max from the root level list
					max.left.right = max.right;
					max.right.left = max.left;
					max = max.right;
					//call pairwisecombine for the remaining list
					pairwiseCombine();
					return x;
				}
			}
			else {
				//make child and parent pointers 'null' and childcuts of all children 'false'.
				node temp = max.child.right;
				max.child.parent = null;
				max.child.childCut = false;
				while(temp!=max.child) {
					temp.parent = null;
					temp.childCut = false;
					temp = temp.right;
				}
				
				//add the child list of max to root level list
				temp = max.child;
				max.right.left = temp.left;
				temp.left.right = max.right;
				temp.left = max;
				max.right = temp;
				
				//remove max from the root level list and call pairwisecombine
				max.left.right = max.right;
				max.right.left = max.left;
				max = max.right;
				pairwiseCombine();
				return x;
			}
		}
		else {
			return null;
		}
	}

/*
 1. this method merges all similar degree heaps to make a new a list of heaps with distinct degrees.
 2. first we calculate number of nodes in root list and run a loop to combine all similar degree heaps and store them in degreetable.
 3. then we take all elements from the degree table table and combine them to make the Fibbonacci heap.
*/
	
	public void pairwiseCombine() {
		node x = max;
		int num = 0, size;
		//calculate number of roots
		if (x != null) {
			x = x.right;
			num++;
			while (x != max) {
				num++;
				x = x.right;
			}
		}
		//initialize degree table.
		size = 100;
		node[] degreeTable = new node[size];
		for (int i = 0; i < size; i++) {
			degreeTable[i] = null;
		}
		node z;
		z = x = max;
		//run a loop on all root level nodes of the heap
		while (num > 0) {
			x = z;
			z = z.right;
			int deg = x.degree;
			// loop to keep combining similar degree heaps
			while (true) {
				node temp = degreeTable[deg];
				if (temp == null) {
					
					//when we reach a unique degree add the root to the table and break the root. 
					degreeTable[deg] = x;
					break;
				} else {
					
					//if do not have a unique degree compare roots to determine high and low
					node high, low;
					if (temp.freq > x.freq) {
						high = temp;
						low = x;
					} else {
						high = x;
						low = temp;
					}
					high.degree++;
					
					//after determining high and low make low the child of high.
					
					if (high.child == null) {
						high.child = low;
						low.parent = high;
						low.left = low;
						low.right = low;
					} else {
						low.left = high.child;
						low.right = high.child.right;
						high.child.right = low;
						low.right.left = low;
						low.parent = high;
					}
					high.childCut = false;
					
					//because we increase the degree after combining remove the node in previous degree. 
					degreeTable[deg] = null;
					// increase the degree and make x high and continue on loop until it reaches a unique degree.
					deg++;
					x = high;
				}
			}
			num = num - 1;
		}
		//initialize max to null and run the loop on degree table to combine all the nodes to form a root level list
		max = null;
		for (int i = 0; i < size; i++) {
			node temp = degreeTable[i];
			if (temp == null)
				//if there is no node a particular degree continue
				continue;
			else {
				//if max is null add first node to the list
				if (max == null) {
					max = temp;
					max.left = max;
					max.right = max;
				} else {
					//add a node to the right of max in the list
					temp.left = max;
					temp.right = max.right;
					max.right = temp;
					temp.right.left = temp;
					//update max accordingly
					if (temp.freq > max.freq)
						max = temp;
				}
			}
		}
	}
	
/*
 1. ths method takes the node and increases its frequency to a given value.
 2. it checks if frequency of the new increased value is greater than the parents and does cut() and cascadingCut() if it is.
 3. if the new frequency is greater than max frequency set the new node as max.
*/	

	public void increaseFrequency(node n, int value) {

		node temp;
		n.freq = value;
		temp = n.parent;
		//check if node frequency is greater than its parents frequency
		if (temp != null && temp.freq < n.freq) {
			cut(temp, n);
			cascadingCut(temp);
		}
		//update max.
		if (n.freq > max.freq)
			max = n;
	}

/*
 1. this method removes a child heap from a parent heap and places it in its root list.
 2. it takes in two, i.e, nodes root of child heap which has to be cut and parent node to the child heap.
*/	
		
	public void cut(node parent, node child) {

		//make parent of root of child heap null and decrement parent degree by 1.
		child.parent = null;
		parent.degree--;
		
		//remove child from its circular list.
		child.left.right = child.right;
		child.right.left = child.left;
		
		//change child pointer of parent accordingly
		if (parent.degree == 0)
			parent.child = null;
		if (parent.child == child)
			parent.child = child.right;
		
		//add child to the root list and make its childcut false
		child.left = max;
		child.right = max.right;
		max.right = child;
		child.right.left = child;
		child.childCut = false;
	}
/*
 1. this method performs cascading cut on the heap.
 2. it keeps checking the parent of the input node recursively and keeps cutting as long as childcut value is true.
 3. once we reach childCut false we make it true and stop calling out recursion.
*/
	public void cascadingCut(node temp) {
		//store parent of a given node.
		node parent = temp.parent;
		if (parent != null) {
			//if childcut is false make it true.
			if (temp.childCut == false)
				temp.childCut = true;
			else
			{
				//if childCut is true cut the node from its parent and call cascadingCut on parent. 
				cut(parent, temp);
				cascadingCut(parent);
			}
		}
	}

	/*void printrootsHeap() {
		node x;
		x = max.right;
		System.out.print(max.freq);
		// node t = max.child;
		// System.out.print(t.freq);
		System.out.print(" -> ");
		while (x != max) {
			System.out.print(x.freq);
			System.out.print(" -> ");
			x = x.right;
		}
		System.out.println("\n");
	}*/
}

