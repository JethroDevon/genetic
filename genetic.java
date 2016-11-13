import java.Math.*;

class problem{

    //enum places
    public enum route{ brighton, bristol, cambridge, glasgow, liverpool, london, manchester, oxford};

    //distances matrix


    //returns random numbers based on routes between places
    int[] createMutant(){

        int[] arrr = [8];

        boolean done = false;

        while( !done){

            int randomNum = rand.nextInt();
            for (int i = 0; i < 8; i++) {

                Random rand = new Random();
                int randomNum = rand.nextInt();
                if( numIsPresent( randomNum, arrr)){

                    i--;
                    break;
                }else{

                    arrr[i] = randomNum;
                }
            }
        }

        return arrr;
    }

    //if num in first arg is in array in second arg return true
    boolean numIsPresent( int _key, int[] checkAgainst){

        boolean check = false;

        for (int i = 0; i < int.length; i++) {

            if( _key == checkAgainst[i]){

                check = true;
                break;
            }
        }

        return check;
    }

    class mutant{

    int[] order = [8];


		void mutant( int[] arr){

		}

    void mutant( mutant _mum, mutant _dad){

    }
	}

	public static void main( String[] args){

	}
}
