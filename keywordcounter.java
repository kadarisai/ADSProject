import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 1. this class contains main().
 2. In this method we take in data from the input file and creates the output based on the requirement.
 3. Here we create FibbonacciHeap object and a hashmap to do our operations.
*/
public class keywordcounter {
	public static void main(String args[]) {
		
		//initialize Fibonacci heap
		FibbonacciHeap heap = new FibbonacciHeap();
		
		//create an output_file
		File file = new File("output_file.txt");
		
		//initialize writer
		BufferedWriter writer = null;
		
		//take the filename from the args
		String path = args[0];
		try {
			//create hashmap which stores the name and frequency of the node, we search based on name.
			HashMap<String, node> h = new HashMap<String, node>();
			writer = new BufferedWriter(new FileWriter(file));
			
			//initialize reader
			BufferedReader br = new BufferedReader(new FileReader(path));
			
			// reads the line from input file
			String s = br.readLine();
			
			//making patterns p1 and p2 to read keywords with number and numbers(for extractMax) respectively
			Pattern p1 = Pattern.compile("([$])([a-z_]+)(\\s)(\\d+)");
			Pattern p2 = Pattern.compile("(\\d+)");

			//loop runs until the we read stop from the file.
			while (!s.trim().equals("stop")) {
				
				Matcher m1 = p1.matcher(s);
				Matcher m2 = p2.matcher(s);
				if (m1.find()) {
					
					//if line starts with $ get the keyword and its frequency
					String st = m1.group(2);
					int freq = Integer.parseInt(m1.group(4));
					
					//if it is not in the heap insert the keyword with frequency
					if (!h.containsKey(st)) {
						node n = new node(freq, st);
						heap.insert(n);
						h.put(st, n);
					} else {
						
						//if it is in the heap increase with the given frequency
						int increaseKey = h.get(st).freq + freq;
						heap.increaseFrequency(h.get(st), increaseKey);
					}
				} else if (m2.find()) {
					
					//if the line only has a number get the number.
					StringBuilder output = new StringBuilder();
					int num = Integer.parseInt(m2.group(1));
					int i = 0;
					
					//initialize an array to store removed nodes
					node[] rnodes = new node[num];
					
					//do extractMax from the heap 'num' times, remove them from the heap and store in another removed nodes array.
					//keep appending the removed strings into output and write it to the file.
					for (i = 0; i < num; i++) {
						node n = heap.extractMax();
						if(n== null){
							output.append("null,");
							continue;
						}
						h.remove(n.name);
						node n2 = new node(n.freq, n.name);
						rnodes[i]=n2;
						output.append(n.name + ",");
					}
					int len = output.length();
					output.deleteCharAt(len - 1);
					writer.write(output.toString());
					writer.newLine();
					
					//insert all the elements back into the heap and hashmap
					for (i=0; i<num; i++) {
						heap.insert(rnodes[i]);
						h.put(rnodes[i].name, rnodes[i]);
					}
				}
				s = br.readLine();
			}
		} catch (Exception e) {
			
		}

		//close the writer.
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException ioe2) {

			}
		}
	}

}
