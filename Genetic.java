import java.util.Random;

class Genetic{

    //enum places
    public enum route{ brighton, bristol, cambridge, glasgow, liverpool, london, manchester, oxford};

    //distances matrix


    //returns random numbers based on routes between places
    int[] createMutant(){

        int[] arrr = new int[8];
        boolean done = false;
	Random rand = new Random();

	for (int i = 0; i < arrr.length; i++) {
		
	    arrr[i] = rand.nextInt(8) + 1;		
	}

        while( !done){
	  
	    for (int i = 0; i < arrr.length; i++) {

	    }
	}

        return arrr;
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

    //little mutant objects so that a new version can be created quickly and easily
    class mutant{

	int[] order = new int[8];


	void mutant( int[] arr){

	}

	void mutant( mutant _mum, mutant _dad){

	}
    }

    //main method
    public static void main( String[] args){

	Genetic problem = new Genetic();
        int[] arr = problem.createMutant();

	for (int i = 0; i < arr.length; i++) {

	    System.out.print( arr[i]);
	}
    }
}
