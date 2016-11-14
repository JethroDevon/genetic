import java.util.Random;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class Genetic{

    //enum places and add values for tidy outputsx
    public enum route{

	brighton(1), bristol(2), cambridge(3), glasgow(4), liverpool(5), london(6), manchester(7), oxford(8);

	private static  Map< Integer, route> destination = new HashMap< Integer, route>();
	private int placenum;
	static {
	    
	    for (route t : route.values()) {
		
		destination.put(t.placenum, t);
	    }
	}

	private route(final int p) { placenum = p; }

        public static route valueOf(int p) {

	    return destination.get(p);
        }

    }

    //global values
    ArrayList<mutant> mutants = new ArrayList<mutant>();
    int[][] distanceMatrix = new int[8][8];
    BufferedReader br;

    //constructor
    Genetic( String fileName, int startingPopulation){

	//parse file in args that contains a comma seperated list of values
	//and initialises distancesmatrix
	String[][] temp = new String[8][8];
	try{

	    int count = 0;
	    br = new BufferedReader( new InputStreamReader(new FileInputStream(fileName)));	  
	    String line;
	    while ((line = br.readLine()) != null) {

		temp[count] = line.split(",");
		int[][] container = new int[8][8];
		for (int i = 0; i < 8; i++) {

		    container[count][i] = Integer.parseInt( temp[count][i]);
		}
		
		distanceMatrix[count] = container[count];
		count++;
	    }
	    
	    br.close();
	}catch(Exception e){

	    System.out.println("Something is wrong with the text file formatting");
	}

	//outputs distance matrix
	System.out.println("\n     ~DISTANCES~    \n");
	for (int i = 0; i < 8; i++) {
	    for (int q = 0; q < 8; q++) {

		System.out.print(distanceMatrix[i][q] + " ");
	    }
	    System.out.println();
	}
	System.out.println();

	//initialises mutants array with startingPopulation number of random mutants
        for (int i = 0; i < startingPopulation; i++) {

	    mutants.add(new mutant(createMutant()));
	}
    }

    //returns random numbers based on routes between places, care has gone in so the array
    //does not contain duplicates
    int[] createMutant(){

        int[] arrr = new int[8];
        boolean done = false;
	Random rand = new Random();

	for (int i = 0; i < arrr.length; i++) {
		
	    arrr[i] = rand.nextInt(8) + 1;		
	}

        
	while( containsDuplicates(arrr)){
	    
	    for (int i = 0; i < arrr.length; i++) {

		int next = rand.nextInt(8) + 1;
	    
		if( !numIsPresent( next, arrr)){

		    arrr[i] = next;
		}
	    }
	}

	//shows that mutant for debugging
	System.out.print(" Creating mutant: ");
	for (int i = 0; i < arrr.length; i++) {

	    System.out.print(arrr[i]);
	}
	System.out.println();

        return arrr;
    }

    
    int getRoute( int from, int to){

	return distanceMatrix[from][to];
    }

    int getRoute( String from, String to){

	return distanceMatrix[route.valueOf(from).ordinal()][route.valueOf(to).ordinal()];
    }

    //if num in first arg is in array in second arg return true
    boolean numIsPresent( int _key, int[] checkAgainst){

        boolean check = false;

        for (int i = 0; i < checkAgainst.length; i++) {

            if( _key == checkAgainst[i]){
		
                check = true;
		break;
            }
        }

        return check;
    }

    //returns true if the array in arguments has duplicate characters in
    boolean containsDuplicates( int[] checkAgainst){

	boolean check = false;

	for (int i = 0; i < checkAgainst.length; i++) {
	    for (int d = 0; d < checkAgainst.length; d++) {

		if( checkAgainst[i] == checkAgainst[d] && i != d){

		    check = true;
		}
	    }
	}

	return check;
    }

    void test(){

	mutant child = new mutant( mutants.get(0), mutants.get(1), 3, 6);
	 
	child.showGenes();
    }

    //little mutant objects so that a new version can be created quickly and easily with rules based on
    //constructor arguments
    class mutant{

	int[] order = new int[8];


        mutant( int[] arr){

	    order = arr;
	}

	//this constructor makes a new mutant out of parent mutants, the following arguments
	//define the starting crossover point and the finishing crossover point
        mutant( mutant _mum, mutant _dad, int cpone, int cptwo){

	    try{
		
		//new genes made from the crossover of mum and dad
		int[] genes = new int[8];

		//set all genes to -1 for detecting if changes have been made
		for (int i = 0; i < 8; i++) {

		    genes[i] = -1;
		}

		//add genes to stay uncrossed
		for (int i = cpone; i < cptwo; i++) {

		    genes[i] = _mum.order[i];
		}

		//first half of crossover
		//cross genes over from dad on the condition that
		//they are not allready in the new genes
		for (int i = 0; i < cpone; i++) {
		    for (int q = 0; q < 8; q++) {
		   	   
			if( !numIsPresent( _dad.order[q], genes)){

			    genes[i] = _dad.order[q];
			    break;
			}		    
		    }
		}

		//second half of crossover
		for (int i = cptwo; i < 8; i++) {
		    for (int q = 0; q < 8; q++) {
		   	   
			if( !numIsPresent( _dad.order[q], genes)){

			    genes[i] = _dad.order[q];
			    break;
			}		    
		    }
		}

		order = genes;
	    }catch( Exception e){

		//if The algorithm in main is not being properly implemented
		System.out.println( " crossover points are likley to be out of bounds");
	    }
	}

	//this function returns the total distance of the rout that it takes
	int getDistance(){

	    int total = 0;
	    for (int i = 0; i < 7; i++) {

		total += getRoute( route.valueOf(order[i]).ordinal(), route.valueOf(order[i+1]).ordinal());

       
	    }

	    // System.out.println(total);
	    return total;
	}

	void showGenes(){

	    for ( int g: order) {

		System.out.print(g);
	    }

	    System.out.println();
	}

	//this function outputs the route based on the the array 'order'
	void showRoute(){

	    for (int i = 0; i < 7; i++) {

		System.out.print( route.valueOf(order[i]) + " to " + route.valueOf(order[i+1]));     
	    }

	    System.out.println();
	}
    }
    
    //main method
    public static void main( String[] args){

	Genetic problem = new Genetic("distances", 2);
	problem.test();

    }
}
