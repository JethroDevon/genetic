import java.util.Random;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.*;

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
    int totalmutations = 0;

    //constructor takes in file containing matrix of 6 distances
    //and the starting population of mutants
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

    //this runs the program for one generation, the first argument is the percentage of
    //the population to keep after sorting, the next two args are the crossover points
    //for breeding finaly the mutation rate has a chance of swapping two chromosones
    //based on percentage of the population
    void generation( double percpop, int pointOne, int pointTwo, double mutationRate){

        //the following block discards the remaining percentage of the sorted arraylist of mutants
        double pop = mutants.size();
        double total = (pop/100) * percpop;
        ArrayList<mutant>survivors = new ArrayList<mutant>();
        Collections.reverse(mutants);

        for (int i = 0; i < (int)total; i++) {

            survivors.add( mutants.get(i));
        }

        //make sure there is always two survivors
        if( survivors.size() == 0 ){

            survivors.add( mutants.get(0));
            survivors.add( mutants.get(1));
        }else if(survivors.size() == 1){

            survivors.add( mutants.get(1));
        }

        //the mutants are wiped out
        mutants.clear();

        //but the survivors are added to the final population
        mutants.addAll( survivors);

        //breeds each mutant against the other
        breed( pointOne, pointTwo, mutationRate);

        //prints a little population data
        printPop();
    }

    //breeds whole array against each other and adds the children to the array againm then sorts the array
    //based on distances
    void breed( int pointOne, int pointTwo, double mutationrate){

        ArrayList<mutant>babies = new ArrayList<mutant>();

        for (int i = 0; i < mutants.size(); i++) {
            for (int q = 0; q < mutants.size(); q++) {

                //if not somehow breeding with self create new baby mutants
                if( i != q){

                    babies.add( new mutant( mutants.get(i), mutants.get(q), pointOne, pointTwo));
                }
            }
        }

        mutants.addAll(babies);

        //mutate some of the mutants
        mutate( mutationrate ,pointOne, pointTwo);
        totalmutations = 0;

        //following block is a bubble sort operation on the arraylist of mutants
        boolean sorted = true;
        mutant tempmutant;

        while( sorted) {

            sorted = false;

            for (int i = 0; i < mutants.size()-1; i++) {

                if( mutants.get(i).distance < mutants.get(i+1).distance ){

                    tempmutant = mutants.get(i);
                    mutants.set( i, mutants.get(i+1));
                    mutants.set( i+1, tempmutant);
                    sorted = true;
                }
            }
        }
    }

    //chance of mutating a mutant
    void mutate( double rate, int pointone, int pointwo){

        //work out what the rate is in relation to the size
        //of the population
        double pop = mutants.size();
        double chance = (pop/100) * rate;

        for (int i = 0; i < mutants.size(); i++) {

            Random rand = new Random();
            int predict = rand.nextInt( (int)pop);

            //if the prediction is less than the chance
            //of mutation, mutate the gene
            if( chance > predict){

                int swapA = rand.nextInt(pointwo) + pointone;
                int swapB = rand.nextInt(pointwo) + pointone;
                // System.out.print("mutation: ");
                //    mutants.get(i).showGenes();
                int temp = mutants.get(i).order[swapA];
                mutants.get(i).order[swapA] = mutants.get(i).order[swapB];
                mutants.get(i).order[swapB] = temp;
                //    System.out.print( " ");
                //      mutants.get(i).showGenes();
                //   System.out.println();
                totalmutations++;
            }
        }
    }

    void printPop(){

        System.out.println( "\n\nTotal mutations this generation: " + totalmutations + "\n");
        System.out.print( "Population size = " + mutants.size() + ", shortest distance so far is - ");
        System.out.println( mutants.get(0).distance + "\n and best route is: \n");
        mutants.get(0).showRoute();
    }

    //little mutant objects so that a new version can be created quickly and easily with rules based on
    //constructor arguments
    class mutant{

        int[] order = new int[8];
        int distance;

        mutant( int[] arr){

            order = arr;
            distance = getDistance();
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

                //initialise member variables on creation
                order = genes;
                distance = getDistance();
            }catch( Exception e){

                //if The algorithm in main is not being properly implemented
                System.out.println( " crossover points are likley to be out of bounds, mutant failed to initialise");
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
        }

        //this function outputs the route based on the the array 'order'
        void showRoute(){

            for (int i = 0; i < 7; i++) {

                System.out.println( route.valueOf(order[i]) + " to " + route.valueOf(order[i+1]));
            }

            System.out.println();
        }
    }

    //main method
    public static void main( String[] args){

        Genetic problem = new Genetic("distances", 100);//<-not the starting population!!
        //the first arg in the following function only keeps that percentage of it!!!
        problem.generation( 20, 3, 5, 1);
        problem.generation( 8, 3, 5, 0.3);
        problem.generation( 3, 3, 5, 0.2);
        problem.generation( 2, 3, 5, 0.2);
        problem.generation( 1, 3, 5, 0.2);

    }
}
