import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
public class A4_2019CS10360{
	public static float average(String name1, String name2) throws FileNotFoundException{
		int num_nodes=0;
		int num_edges=0;
		File node_csv_file=new File(name1);
		Scanner sc_node=new Scanner(node_csv_file);
		String header_node=sc_node.nextLine();
		while(sc_node.hasNextLine()==true){
			num_nodes++;
			sc_node.nextLine();
		}
		File edge_csv_file=new File(name2);
		Scanner sc_edge=new Scanner(edge_csv_file);
		String header_edge=sc_edge.nextLine();
		while(sc_edge.hasNextLine()==true){
			num_edges++;
			sc_edge.nextLine();
		}
		if(num_nodes==0 || num_edges==0) return 0;
		float a= (float) num_edges/num_nodes;
		return 2*a;
	}
	public static void comp_occur(String name1, String name2, HashMap<String, Integer> dict) throws FileNotFoundException{
		File node_csv_file=new File(name1);
		Scanner sc_node=new Scanner(node_csv_file);
		String header_node=sc_node.nextLine();
		while(sc_node.hasNextLine()==true){
			String info=sc_node.nextLine();
			String[] seperated=info.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
			if(seperated[0].startsWith("\"") && seperated[0].endsWith("\"")){dict.put(seperated[0].substring(1, seperated[0].length()-1),0);}
        	else{ dict.put(seperated[0],0);}
		}
		File edge_csv_file=new File(name2);
		Scanner sc_edge=new Scanner(edge_csv_file);
		String header_edge=sc_edge.nextLine();
		while(sc_edge.hasNextLine()==true){
			String info=sc_edge.nextLine();
			String[] seperated=info.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
			if(seperated[0].startsWith("\"") && seperated[0].endsWith("\"")){
				int val1=dict.get(seperated[0].substring(1, seperated[0].length()-1))+Integer.valueOf(seperated[2]);
				dict.put(seperated[0].substring(1, seperated[0].length()-1),val1);
			}else{
				int val1=dict.get(seperated[0])+Integer.valueOf(seperated[2]);
				dict.put(seperated[0],val1);
			}
			if(seperated[1].startsWith("\"") && seperated[1].endsWith("\"")){
				int val2=dict.get(seperated[1].substring(1, seperated[1].length()-1))+Integer.valueOf(seperated[2]);
				dict.put(seperated[1].substring(1, seperated[1].length()-1),val2);
			}
        	else{
				int val2=dict.get(seperated[1])+Integer.valueOf(seperated[2]);
				dict.put(seperated[1],val2);
			}
		}

	}
	public static void mergeSort(ArrayList<String> list, int left, int right, HashMap<String, Integer> dict){
		if (left<right){
			int mid=(left+right)/2;
			mergeSort(list,left, mid, dict);
			mergeSort(list, mid+1, right, dict);
			merge(list, left, right, mid, dict);
		}
	}
	public static void merge(ArrayList<String> list, int left, int right, int mid, HashMap<String, Integer> occurences_of_nodes){
		int n2=right-mid;
		int n1=mid-left+1;
		int i=0;
		int j=0;
		int k=left;
		String Left[]= new String[n1];
		String Right[]=new String[n2];
		for(int p=0; p<n1; p++){
			Left[p]=list.get(left+p);
		}
		for(int p=0; p<n2; p++){
			Right[p]=list.get(mid +1+p);
		}
		while(i<n1 && j<n2){
			int dl= occurences_of_nodes.get(Left[i]);
			int dr= occurences_of_nodes.get(Right[j]);
			if (dl>dr){
				list.set(k,Left[i]);
				i++;
			}else if (dl==dr){
				int a=Left[i].compareTo(Right[j]);
				if (a<0){
					list.set(k,Right[j]);
					j++;
				}else{
					list.set(k,Left[i]);
					i++;
				}
			}
			else{
				list.set(k,Right[j]);
				j++;
			}
			k++;
		}
		while (i<n1){
            list.set(k,Left[i]); 
            i++; 
            k++; 
        } 
        while (j < n2){
            list.set(k, Right[j]); 
            j++; 
            k++; 
        }
	}

	public static ArrayList<String> rank(String name1, String name2, HashMap<String, Integer> dict) throws FileNotFoundException{
		comp_occur(name1, name2, dict);
		ArrayList<String> list = new ArrayList<>(dict.keySet());
		mergeSort(list, 0, list.size()-1, dict);
		return list;
	}

	public static void main(String[] args) throws FileNotFoundException {
		String node_csv_file=args[0];
		String edge_csv_file=args[1];
		String function=args[2];
		HashMap<String, Integer> occurences_of_nodes=new HashMap<String, Integer>();
		HashMap<String, ArrayList<String>> adj_list=new HashMap<String, ArrayList<String>>();
		
		switch (function){
			case "average":
				float ans=average(node_csv_file, edge_csv_file);
				System.out.printf("%.2f",ans);
				break;
			case "rank":
				ArrayList<String> sorted_list=rank(node_csv_file, edge_csv_file, occurences_of_nodes);
				if (sorted_list.size() >= 1){
				    System.out.print(sorted_list.get(0));
				}
				for (int i = 1; i < sorted_list.size(); i++) {
				     System.out.print("," + sorted_list.get(i));
				}
				System.out.println();
				break;
			case "independent_storylines_dfs":
				ArrayList<ArrayList<String>> ans_dfs=indep_story_dfs(node_csv_file, edge_csv_file, adj_list);
				for (int i = 1; i < ans_dfs.size(); i++) {
				    if (ans_dfs.get(i).size() >= 1){
					    System.out.print(ans_dfs.get(i).get(0));
					}
					for (int j = 1; j < ans_dfs.get(i).size(); j++) {
					     System.out.print("," + ans_dfs.get(i).get(j));
					}
					System.out.println();
				}
		}
	}

	public static ArrayList<ArrayList<String>> indep_story_dfs(String node_csv_file, String edge_csv_file, HashMap<String, ArrayList<String>> adj_list) throws FileNotFoundException{
		HashMap<String, Integer> nodes_int=new HashMap<String, Integer>();
		HashMap<Integer, String> int_nodes=new HashMap<Integer, String>();
		adjacency_list(node_csv_file, edge_csv_file, adj_list, nodes_int, int_nodes);
		ArrayList<ArrayList<String>> ans_list=new ArrayList<ArrayList<String>>();
		boolean[] visited = new boolean[nodes_int.size()];
		connectedcomp(ans_list, int_nodes, adj_list, nodes_int, visited);

		for (int i=0; i<ans_list.size(); i++){
			mergeSort1(ans_list.get(i),0,ans_list.get(i).size()-1);
		}
		mergeSort2(ans_list,0, ans_list.size()-1);
		return ans_list;
	}
	

	public static void adjacency_list(String name1, String name2, HashMap<String, ArrayList<String>> adj_list, HashMap<String, Integer> nodes_int, HashMap<Integer, String> int_nodes) throws FileNotFoundException{
		File node_csv_file=new File(name1);
		Scanner sc_node=new Scanner(node_csv_file);
		String header_node=sc_node.nextLine();
		int i=0;
		while(sc_node.hasNextLine()==true){
			String info=sc_node.nextLine();
			String[] seperated=info.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
			if(seperated[0].startsWith("\"") && seperated[0].endsWith("\"")){
				adj_list.put(seperated[0].substring(1, seperated[0].length()-1),new ArrayList<String>());
				nodes_int.put(seperated[0].substring(1, seperated[0].length()-1),i);
				int_nodes.put(i,seperated[0].substring(1, seperated[0].length()-1));
				System.out.print(adj_list.get(seperated[0]));
			}else{
				adj_list.put(seperated[0],new ArrayList<String>());
				nodes_int.put(seperated[0],i);
				int_nodes.put(i,seperated[0]);
			}
			i++;
		}
		
		File edge_csv_file=new File(name2);
		Scanner sc_edge=new Scanner(edge_csv_file);
		String header_edge=sc_edge.nextLine();
		while(sc_edge.hasNextLine()==true){
			String info=sc_edge.nextLine();
			String[] seperated=info.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
			if(seperated[0].startsWith("\"") && seperated[0].endsWith("\"")){
				seperated[0]=seperated[0].substring(1, seperated[0].length()-1);
			}
			if(seperated[1].startsWith("\"") && seperated[1].endsWith("\"")){
				seperated[1]=seperated[1].substring(1, seperated[1].length()-1);
			}
			adj_list.get(seperated[0]).add(seperated[1]);
			adj_list.get(seperated[1]).add(seperated[0]);
			adj_list.put(seperated[0],adj_list.get(seperated[0]));
			adj_list.put(seperated[1],adj_list.get(seperated[1]));
		}
	}

	public static void connectedcomp(ArrayList<ArrayList<String>> ans_list, HashMap<Integer, String> int_nodes, HashMap<String, ArrayList<String>> adj_list, HashMap<String, Integer> nodes_int, boolean[] visited){
       //call dfs here
		int i=0;
        int index=0;
        while (i<visited.length){
       	if(visited[i]==false){
       		visited[i]=true;
       		dfs(i, ans_list,index, int_nodes, adj_list, nodes_int, visited);
       		index++;
       	}
       	i++;
       }
	}

	public static void dfs(int i, ArrayList<ArrayList<String>> ans_list, int index, HashMap int_nodes, HashMap<String, ArrayList<String>> adj_list, HashMap<String, Integer> nodes_int, boolean[] visited){
		String node = (String) int_nodes.get(i);
		if(visited[i]==true)
			{ ArrayList new_index= new ArrayList<String>();
				new_index.add(node);
				ans_list.add(new_index);}
		else{
			visited[i]=true;
			ArrayList<String> array_index = ans_list.get(index);
			array_index.add(node);
			ans_list.set(index, array_index);
		}
		ArrayList<String> w = adj_list.get(node);
		for(int j=0; j<w.size(); j++){
			int node_index=  nodes_int.get(w.get(j));
			if(visited[node_index]==false)
				dfs(node_index, ans_list,index,int_nodes, adj_list,nodes_int,visited);
		}
	}

	public static void mergeSort2(ArrayList<ArrayList<String>> list, int left, int right){
		if (left<right){
			int mid=(left+right)/2;
			mergeSort2(list,left, mid);
			mergeSort2(list, mid+1, right);
			merge2(list, left, right, mid);
		}
	}
	public static void merge2(ArrayList<ArrayList<String>> list, int left, int right, int mid){
		int n2=right-mid;
		int n1=mid-left+1;
		int i=0;
		int j=0;
		int k=left;
		ArrayList<ArrayList<String>> Left= new ArrayList<ArrayList<String>>(n1);
		ArrayList<ArrayList<String>> Right=new ArrayList<ArrayList<String>>(n2);
		for(int p=0; p<n1; p++){
			Left.set(p,list.get(left+p));
		}
		for(int p=0; p<n2; p++){
			Right.set(p,list.get(mid+1+p));
		}
		while(i<n1 && j<n2){
			int dl= Left.get(i).size();
			int dr= Right.get(j).size();
			if (dl>dr){
				list.set(k,Left.get(i));
				i++;
			}else if (dl==dr){
				int a=Left.get(i).get(0).compareTo(Right.get(j).get(0));
				if (a<0){
					list.set(k,Right.get(j));
					j++;
				}else{
					list.set(k,Left.get(i));
					i++;
				}
			}
			else{
				list.set(k,Right.get(j));
				j++;
			}
			k++;
		}
		while (i<n1){
            list.set(k,Left.get(i)); 
            i++; 
            k++; 
        } 
        while (j < n2){
            list.set(k, Right.get(j)); 
            j++; 
            k++; 
        }
	}

	public static void mergeSort1(ArrayList<String> list, int left, int right){
		if (left<right){
			int mid=(left+right)/2;
			mergeSort1(list,left, mid);
			mergeSort1(list, mid+1, right);
			merge1(list, left, right, mid);
		}
	}
	public static void merge1(ArrayList<String> list, int left, int right, int mid){
		int n2=right-mid;
		int n1=mid-left+1;
		int i=0;
		int j=0;
		int k=left;
		String Left[]= new String[n1];
		String Right[]=new String[n2];
		for(int p=0; p<n1; p++){
			Left[p]=list.get(left+p);
		}
		for(int p=0; p<n2; p++){
			Right[p]=list.get(mid +1+p);
		}
		while(i<n1 && j<n2){
			int a=Left[i].compareTo(Right[j]);
			if (a>0){
				list.set(k,Left[i]);
				i++;
			}else{
				list.set(k,Right[j]);
				j++;
			}
			k++;
		}
		while (i<n1){
            list.set(k,Left[i]); 
            i++; 
            k++; 
        } 
        while (j < n2){
            list.set(k, Right[j]); 
            j++; 
            k++; 
        }
	}
} 
