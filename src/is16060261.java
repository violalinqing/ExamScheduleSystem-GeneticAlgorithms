import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class is16060261 {

	public static void main(String[] args) throws FileNotFoundException
	{
		int G,P,S,M,C,D,Cr,Mu,Re;
		Scanner in = new Scanner(System.in);
		G = getNumberFromUser("number of generations:",0,in);
		P = getNumberFromUser("Population size:",0,in);
		S = getNumberFromUser("number of students:",0,in);
		M = getNumberFromUser("total number of modules:",1,in);
		C = getNumberFromUserAndCheck("the number of modules in a course:",in,M);
		int[] percentage = getpercentageAndCheck(in);
		Re = percentage[0];
		Mu = percentage[1];
		Cr = 100 - (Re+Mu);
		D = (M / 2);
		
		System.setOut(new PrintStream(new FileOutputStream("AI17.txt")));
		
		ArrayList<ArrayList<Integer>> studentSchedule = new ArrayList<ArrayList<Integer>>(); 
		ArrayList<Boolean[]> studentScheduleMark = new ArrayList<Boolean[]>(); 
		
		for(int studentNum = 0; studentNum < S; studentNum++)
		{
			Boolean[] mark = new Boolean[M];
			Arrays.fill( mark,false);
			
			ArrayList<Integer> courseNum = newStudentModuleInfo(C,M, mark,studentNum);
			System.out.print("\n");
			
			studentSchedule.add(courseNum);
			studentScheduleMark.add(mark);
		}
		
		ArrayList<ArrayList<Integer>> orderings = new ArrayList<ArrayList<Integer>>(); 
		ArrayList<Integer> fitnessCost = new ArrayList<Integer>();
		int i = 0;
		while(i < P)
		{	
			ArrayList<Integer> newOrdering = createNewOrdering(M);
			System.out.println();
			
			boolean flag = checkRepeatOrder(newOrdering,orderings);
			
			if(!flag)
			{
				orderings.add(newOrdering);
				int cost = fitnessFunction(S, D, studentScheduleMark, newOrdering);
				fitnessCost.add(cost);
				printOrdering(i,newOrdering,D);
				System.out.println(" cost: "+cost);
				i++;
			}
		}
		QuickSort(orderings,fitnessCost, 0, fitnessCost.size()-1);
		performSelection(orderings);
		for(int j = 0;j < P; j++)
		{
			System.out.println();
			printOrdering(j,orderings.get(j),D);
			System.out.println(" cost: "+fitnessCost.get(j));
		}
		
		//ArrayList<ArrayList<Integer>> newOrderings = chooseTechnique(orderings, Re, Mu, Cr, D);
		
	}
	

	private static int getNumberFromUser(String require,int type, Scanner in)
	{
		String Beginning = "Please Enter the ";
		String[] errorMessage = {"Invalid input. Please enter a positive integer","Invalid input. Please enter a positive integer and even"};
		int value = 0;
		boolean isValid = false;
		System.out.println(Beginning+require);
		while(!isValid)
		{
			String temp = in.nextLine();
			if(type==0&&isNumeric(temp))
			{
				isValid = true;
				value = Integer.parseInt(temp);
			}
			else if(type==1&&isNumeric(temp)&&Integer.parseInt(temp)%2==0)
			{
				isValid = true;
				value = Integer.parseInt(temp);
			}
			if(!isValid)
				System.out.println(errorMessage[type]);
		}
		return value;
	}	
	
	private static int[] getpercentageAndCheck(Scanner in)
	{
		String Beginning = "Please Enter the ";
		String[] errorMessage = {"Invalid input. Please enter a positive integer and less than 100","Invalid input. Reproduction + Mutation should less than 100"};
		int[] value = new int[2];
		boolean isValidforRe = false,isValidforMu = false,isValid = false;
		while(!isValid)
		{
			while(!isValidforRe)
			{
				System.out.println(Beginning+"the percentage chance of Reproduction:");
				String temp = in.nextLine();
				if(isNumeric(temp))
				{
					isValidforRe = true;
					value[0] = Integer.parseInt(temp);
				}
				if(!isValidforRe)
					System.out.println(errorMessage[0]);
			}
			while(!isValidforMu)
			{
				System.out.println(Beginning+"the percentage chance of Mutation:");
				String temp = in.nextLine();
				if(isNumeric(temp))
				{
					isValidforMu = true;
					value[1] = Integer.parseInt(temp);
				}
				if(!isValidforMu)
					System.out.println(errorMessage[0]);
			}
			if(value[0]+value[1]<100)
			{
				isValid = true;
			}
			else
			{
				isValidforRe = false;
				isValidforMu = false;
				System.out.println(errorMessage[1]);
			}
		}
		return value;
	}	
	
	private static int getNumberFromUserAndCheck(String require,Scanner in,int totalNumberOfModule)
	{
		String Beginning = "Please Enter the ";
		String errorMessage = "Invalid input. Please enter a positive integer and less than the total number of modules";
		int value = 0;
		boolean isValid = false;
		System.out.println(Beginning+require);
		while(!isValid)
		{
			String temp = in.nextLine();
			if(isNumeric(temp)&&(Integer.parseInt(temp)<=totalNumberOfModule))
			{
				isValid = true;
				value = Integer.parseInt(temp);
			}
			if(!isValid)
				System.out.println(errorMessage);
		}
		return value;
	}
	

	private static boolean isNumeric(String str){ 
		   Pattern pattern = Pattern.compile("[0-9]*"); 
		   Matcher isNum = pattern.matcher(str);
		   if( !isNum.matches() ){
		       return false; 
		   } 
		   return true; 
		}
	

	private static ArrayList<Integer> newStudentModuleInfo(int n,int totalNumberOfModule, Boolean[] mark,int studentNum)
	{
		ArrayList<Integer> StudentModules = new ArrayList<Integer>() ;
		System.out.print("Student "+(studentNum+1)+":");
		while(StudentModules.size() < n)
		{
			int courseNum = (int)(Math.random()*totalNumberOfModule + 1);
			boolean flag = true;
			if(StudentModules.contains(courseNum))
			{
				flag = false;
			}
			if(flag)
			{
				
				StudentModules.add(courseNum); 
				mark[courseNum-1] = true;
				System.out.print(" M"+ courseNum+"\t");
			}
		}
		return StudentModules;
	}
	
	private static ArrayList<Integer> createNewOrdering(int totalNumberOfModule)
	{
		ArrayList<Integer> newOrdering = new ArrayList<Integer>();
		while(newOrdering.size() < totalNumberOfModule)
		{
			int courseNum = (int)(Math.random()*totalNumberOfModule + 1);
			boolean flag = true;
			if(newOrdering.contains(courseNum))
			{
				flag = false;
			}
			if(flag)
			{
				newOrdering.add(courseNum);
			}
		}
		return newOrdering;
	}
	

	private static boolean checkRepeatOrder(ArrayList<Integer> ordering, ArrayList<ArrayList<Integer>> orderings)
	{
		if(orderings.contains(ordering))
		{
			return true;
		}
		return false;
	}
	

	private static void printOrdering(int OrderNum, ArrayList<Integer> ordering, int examDays)
	{
		System.out.print("Ord "+(OrderNum+1)+":");
		for(int k = 0; k < ordering.size(); k++)
		{
			System.out.print("\t"+" m"+ ordering.get(k));
			if(k == (examDays-1))
				System.out.println();
		}
		
	}


	private static int fitnessFunction(int S, int D, ArrayList<Boolean[]> studentScheduleMark, ArrayList<Integer> ordering)
	{
		int cost = 0;
		for(int i = 0; i < S; i++ )
		{
			for(int j = 0; j < D; j++)
			{
				int n1 = ordering.get(j)-1;
				int n2 = ordering.get(D+j)-1;
				if(studentScheduleMark.get(i)[n1]&&studentScheduleMark.get(i)[n2])
				{
					cost++;
				}
			}
		}
		return cost;
	}
	
	private static void QuickSort(ArrayList<ArrayList<Integer>> orderings,ArrayList<Integer> fitnessCost, int low, int high)
    {
        if(low<high)
        {
            int key1=fitnessCost.get(low);
            ArrayList<Integer> key2=orderings.get(low);
            int i=low,j;
            for(j=low+1;j<=high;j++)
            {
                if(fitnessCost.get(j)<key1)
                {
                	Collections.swap(fitnessCost, i+1, j);
                	Collections.swap(orderings, i+1, j);
                    i++;
                }
                
            }
            fitnessCost.set(low,fitnessCost.get(i));
            orderings.set(low,orderings.get(i));
            fitnessCost.set(i, key1);
            orderings.set(i, key2);
            QuickSort(orderings,fitnessCost, low, i-1);
            QuickSort(orderings,fitnessCost, i+1, high);
            
        }
    }
	
	private static ArrayList<ArrayList<Integer>> performSelection(ArrayList<ArrayList<Integer>> initialOrderings)
	{
		if(initialOrderings.size()>3)
		{
			int mod = initialOrderings.size() % 3;
			int divisor = (int) Math.floor((initialOrderings.size()+mod)/3);
			int startpoint = initialOrderings.size() - divisor;
			for(int i = 0; i < divisor;i++)
			{
				initialOrderings.set(startpoint, initialOrderings.get(i));
				startpoint++;
			}
		}
		return initialOrderings;
	}
	
	private static ArrayList<ArrayList<Integer>> chooseTechnique(ArrayList<ArrayList<Integer>> orderings, int Re, int Mu, int Cr,int examDays)
	{
		ArrayList<ArrayList<Integer>> orderingAfterGA = new ArrayList<ArrayList<Integer>>();
		while(orderings.size() > 0)
		{
			typeOfTechnique type = getTypeOfTechnique(Re, Mu, Cr);
			int[] rand = getRandomIndexOfOrderings(orderings.size());
			switch(type){
			case REPRODUCTION:
				orderingAfterGA.add(orderings.get(rand[0]));
                orderings.remove(rand[0]);
                break;
            case MUTATION:
            	orderingAfterGA.add(performMutation(orderings.get(rand[0]),examDays));
                orderings.remove(rand[0]);
                break;
            case CROSSOVER:
                if (orderings.size() != 1) {
                    //TODO: Continue from here
                }
                break;
           }
		}
		return orderingAfterGA;
	}
	 
    private enum typeOfTechnique {
        REPRODUCTION, MUTATION, CROSSOVER
     }
    
    private static int[] getRandomIndexOfOrderings(int max)
    {
    	int[] rand = new int[2];
    	boolean flag = false;
		while(!flag)
		{
			rand[0] = (int)(Math.random()*max);
			rand[1] = (int)(Math.random()*max);
			if(rand[0]!=rand[1])
				flag = true;
		}
		return rand;
    }
    private static typeOfTechnique getTypeOfTechnique(int Re, int Cr, int Mu)
	{
		typeOfTechnique type = typeOfTechnique.REPRODUCTION;
		int rand = (int)(Math.random()*100)+1;
		if(rand<=Re)
			type = typeOfTechnique.REPRODUCTION;
		else if(rand > Re && rand <= (Re+Cr))
			type = typeOfTechnique.CROSSOVER;
		else if(rand > (Re+Cr) && rand <= 100)
			type = typeOfTechnique.MUTATION;
		return type;
	}
    
	private static ArrayList<Integer> performMutation(ArrayList<Integer> ordering, int examDays)
	{
		int[] rand = getRandomIndexOfOrderings(examDays);
		System.out.println(rand[0]+"      "+(examDays+rand[1]));
		Collections.swap(ordering,rand[0],examDays+rand[1]);
		printOrdering(1111111,ordering,examDays);
		return ordering;
	}
}
