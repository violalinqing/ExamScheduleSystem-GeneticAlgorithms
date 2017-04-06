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

    public static void main(String[] args) throws FileNotFoundException {
        int G, P, S, M, C, D, Cr, Mu, Re;
        Scanner in = new Scanner(System.in);
        G = getNumberFromUser("number of generations:", 0, in);
        P = getNumberFromUser("Population size:", 0, in);
        S = getNumberFromUser("number of students:", 0, in);
        M = getNumberFromUser("total number of modules:", 1, in);
        C = getNumberFromUserAndCheck("the number of modules in a course:", in, M);
        int[] percentage = getPercentageAndCheck(in);
        Re = percentage[0];
        Mu = percentage[1];
        Cr = 100 - (Re + Mu);
        D = (M / 2);

        System.setOut(new PrintStream(new FileOutputStream("AI17.txt")));

        ArrayList<ArrayList<Integer>> studentSchedule = new ArrayList<ArrayList<Integer>>();
        ArrayList<Boolean[]> studentScheduleMark = new ArrayList<Boolean[]>();

        for (int studentNum = 0; studentNum < S; studentNum++) {
            Boolean[] mark = new Boolean[M];
            Arrays.fill(mark, false);

            ArrayList<Integer> courseNum = newStudentModuleInfo(C, M, mark, studentNum);
            System.out.print("\n");

            studentSchedule.add(courseNum);
            studentScheduleMark.add(mark);
        }

        ArrayList<ArrayList<Integer>> orderings = new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> fitnessCost = new ArrayList<Integer>();
        int i = 0;
        while (i < P) {
            ArrayList<Integer> newOrdering = createNewOrdering(M);
            System.out.println();

            boolean flag = checkRepeatOrder(newOrdering, orderings);

            if (!flag) {
                orderings.add(newOrdering);
                int cost = fitnessFunction(studentScheduleMark, newOrdering);
                fitnessCost.add(cost);
                printOrdering(i, newOrdering, D);
                System.out.println(" cost: " + cost);
                i++;
            }
        }

        for (int j = 0; j < G; j++) {
            QuickSort(orderings, fitnessCost, 0, fitnessCost.size() - 1);
            performSelection(orderings,fitnessCost);
            System.out.println();
            //printOrdering(j, orderings.get(0), D);
           // System.out.println(" cost: " + fitnessCost.get(0));
            for(int k = 0; k < orderings.size(); k++)
            {
                printOrdering(k,orderings.get(k),D);
                System.out.println(" cost: " + fitnessCost.get(k));
            }
            fitnessCost.clear();
            orderings = chooseTechnique(orderings, Re, Mu, Cr, D, studentScheduleMark,fitnessCost);
        }

//
//        performCrossover(orderings.get(8),orderings.get(9));
//        printOrdering(8,orderings.get(8),D);
//        System.out.println();
//        printOrdering(9,orderings.get(9),D);

    }


    private static int getNumberFromUser(String require, int type, Scanner in) {
        String Beginning = "Please Enter the ";
        String[] errorMessage = {"Invalid input. Please enter a positive integer", "Invalid input. Please enter a positive integer and even"};
        int value = 0;
        boolean isValid = false;
        System.out.println(Beginning + require);
        while (!isValid) {
            String temp = in.nextLine();
            if (type == 0 && isNumeric(temp)) {
                isValid = true;
                value = Integer.parseInt(temp);
            } else if (type == 1 && isNumeric(temp) && Integer.parseInt(temp) % 2 == 0) {
                isValid = true;
                value = Integer.parseInt(temp);
            }
            if (!isValid)
                System.out.println(errorMessage[type]);
        }
        return value;
    }

    private static int[] getPercentageAndCheck(Scanner in) {
        String Beginning = "Please Enter the ";
        String[] errorMessage = {"Invalid input. Please enter a positive integer and less than 100", "Invalid input. Reproduction + Mutation should less than 100"};
        int[] value = new int[2];
        boolean isValidforRe = false, isValidforMu = false, isValid = false;
        while (!isValid) {
            while (!isValidforRe) {
                System.out.println(Beginning + "the percentage chance of Reproduction:");
                String temp = in.nextLine();
                if (isNumeric(temp)) {
                    isValidforRe = true;
                    value[0] = Integer.parseInt(temp);
                }
                if (!isValidforRe)
                    System.out.println(errorMessage[0]);
            }
            while (!isValidforMu) {
                System.out.println(Beginning + "the percentage chance of Mutation:");
                String temp = in.nextLine();
                if (isNumeric(temp)) {
                    isValidforMu = true;
                    value[1] = Integer.parseInt(temp);
                }
                if (!isValidforMu)
                    System.out.println(errorMessage[0]);
            }
            if (value[0] + value[1] < 100) {
                isValid = true;
            } else {
                isValidforRe = false;
                isValidforMu = false;
                System.out.println(errorMessage[1]);
            }
        }
        return value;
    }

    private static int getNumberFromUserAndCheck(String require, Scanner in, int totalNumberOfModule) {
        String Beginning = "Please Enter the ";
        String errorMessage = "Invalid input. Please enter a positive integer and less than the total number of modules";
        int value = 0;
        boolean isValid = false;
        System.out.println(Beginning + require);
        while (!isValid) {
            String temp = in.nextLine();
            if (isNumeric(temp) && (Integer.parseInt(temp) <= totalNumberOfModule)) {
                isValid = true;
                value = Integer.parseInt(temp);
            }
            if (!isValid)
                System.out.println(errorMessage);
        }
        return value;
    }


    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }


    private static ArrayList<Integer> newStudentModuleInfo(int n, int totalNumberOfModule, Boolean[] mark, int studentNum) {
        ArrayList<Integer> StudentModules = new ArrayList<Integer>();
        System.out.print("Student " + (studentNum + 1) + ":");
        while (StudentModules.size() < n) {
            int courseNum = (int) (Math.random() * totalNumberOfModule + 1);
            boolean flag = true;
            if (StudentModules.contains(courseNum)) {
                flag = false;
            }
            if (flag) {

                StudentModules.add(courseNum);
                mark[courseNum - 1] = true;
                System.out.print(" M" + courseNum + "\t");
            }
        }
        return StudentModules;
    }

    private static ArrayList<Integer> createNewOrdering(int totalNumberOfModule) {
        ArrayList<Integer> newOrdering = new ArrayList<Integer>();
        while (newOrdering.size() < totalNumberOfModule) {
            int courseNum = (int) (Math.random() * totalNumberOfModule + 1);
            boolean flag = true;
            if (newOrdering.contains(courseNum)) {
                flag = false;
            }
            if (flag) {
                newOrdering.add(courseNum);
            }
        }
        return newOrdering;
    }


    private static boolean checkRepeatOrder(ArrayList<Integer> ordering, ArrayList<ArrayList<Integer>> orderings) {
        if (orderings.contains(ordering)) {
            return true;
        }
        return false;
    }


    private static void printOrdering(int OrderNum, ArrayList<Integer> ordering, int examDays) {
        System.out.print("Ord " + (OrderNum + 1) + ":");
        for (int k = 0; k < ordering.size(); k++) {
            System.out.print("\t" + " m" + ordering.get(k));
            if (k == (examDays - 1))
                System.out.println();
        }

    }


    private static int fitnessFunction(ArrayList<Boolean[]> studentScheduleMark, ArrayList<Integer> ordering) {
        int cost = 0;
        for (int i = 0; i < studentScheduleMark.size(); i++) {
            for (int j = 0; j < studentScheduleMark.get(i).length/2; j++) {
                int n1 = ordering.get(j) - 1;
                int n2 = ordering.get(studentScheduleMark.get(i).length/2 + j) - 1;
                if (studentScheduleMark.get(i)[n1] && studentScheduleMark.get(i)[n2]) {
                    cost++;
                }
            }
        }
        return cost;
    }

    private static void QuickSort(ArrayList<ArrayList<Integer>> orderings, ArrayList<Integer> fitnessCost, int low, int high) {
        if (low < high) {
            int key1 = fitnessCost.get(low);
            ArrayList<Integer> key2 = orderings.get(low);
            int i = low, j;
            for (j = low + 1; j <= high; j++) {
                if (fitnessCost.get(j) < key1) {
                    Collections.swap(fitnessCost, i + 1, j);
                    Collections.swap(orderings, i + 1, j);
                    i++;
                }

            }
            fitnessCost.set(low, fitnessCost.get(i));
            orderings.set(low, orderings.get(i));
            fitnessCost.set(i, key1);
            orderings.set(i, key2);
            QuickSort(orderings, fitnessCost, low, i - 1);
            QuickSort(orderings, fitnessCost, i + 1, high);

        }
    }

    private static ArrayList<ArrayList<Integer>> performSelection(ArrayList<ArrayList<Integer>> initialOrderings,ArrayList<Integer> fitnessCost) {
        if (initialOrderings.size() > 3) {
            int mod = initialOrderings.size() % 3;
            int divisor = (int) Math.floor((initialOrderings.size() + mod) / 3);
            int startpoint = initialOrderings.size() - divisor;
            for (int i = 0; i < divisor; i++) {
                initialOrderings.set(startpoint, initialOrderings.get(i));
                fitnessCost.set(startpoint, fitnessCost.get(i));
                startpoint++;
            }
        }
        return initialOrderings;
    }

    private static ArrayList<ArrayList<Integer>> chooseTechnique(ArrayList<ArrayList<Integer>> orderings, int Re, int Mu, int Cr, int examDays, ArrayList<Boolean[]> studentScheduleMark,ArrayList<Integer> fitnessCostRecord) {
        ArrayList<ArrayList<Integer>> orderingAfterGA = new ArrayList<ArrayList<Integer>>();
        while (orderings.size() > 0) {
            typeOfTechnique type = getTypeOfTechnique(Re, Mu, Cr);
            int cost;
            switch (type) {
                case REPRODUCTION:
                    int randForReprodction = (int) (Math.random() * orderings.size());
                    ArrayList<Integer> newOrdering1 = orderings.get(randForReprodction);
                    orderingAfterGA.add(newOrdering1);
                    cost = fitnessFunction(studentScheduleMark,newOrdering1);
                    fitnessCostRecord.add(cost);
                    orderings.remove(randForReprodction);
                    break;
                case MUTATION:
                    int randForMutation = (int) (Math.random() * orderings.size());
                    ArrayList<Integer> newOrdering2 = performMutation(orderings.get(randForMutation), examDays);
                    orderingAfterGA.add(newOrdering2);
                    cost = fitnessFunction(studentScheduleMark,newOrdering2);
                    fitnessCostRecord.add(cost);
                    orderings.remove(randForMutation);
                    break;
                case CROSSOVER:
                    if (orderings.size() >= 2) {
                        int[] randForCrossover = getRandomIndexOfOrderings(orderings.size());
                        ArrayList<ArrayList<Integer>> AfterCrossover = performCrossover(orderings.get(randForCrossover[0]),orderings.get(randForCrossover[1]));

                        orderingAfterGA.add(AfterCrossover.get(0));
                        cost = fitnessFunction(studentScheduleMark,AfterCrossover.get(0));
                        fitnessCostRecord.add(cost);
                        orderingAfterGA.add(AfterCrossover.get(1));
                        cost = fitnessFunction(studentScheduleMark,AfterCrossover.get(1));
                        fitnessCostRecord.add(cost);
                        if(randForCrossover[1]>randForCrossover[0])
                        {
                            orderings.remove(randForCrossover[1]);
                            orderings.remove(randForCrossover[0]);
                        }
                        else
                        {
                            orderings.remove(randForCrossover[0]);
                            orderings.remove(randForCrossover[1]);
                        }
                    }
                    break;
            }
        }
        return orderingAfterGA;
    }

    private enum typeOfTechnique {
        REPRODUCTION, MUTATION, CROSSOVER
    }

    private static int[] getRandomIndexOfOrderings(int max) {
        int[] rand = new int[2];
        boolean flag = false;
        while (!flag) {
            rand[0] = (int) (Math.random() * max);
            rand[1] = (int) (Math.random() * max);
            if (rand[0] != rand[1])
                flag = true;
        }
        return rand;
    }

    private static typeOfTechnique getTypeOfTechnique(int Re, int Cr, int Mu) {
        typeOfTechnique type = typeOfTechnique.REPRODUCTION;
        int rand = (int) (Math.random() * 100) + 1;
        if (rand <= Re)
            type = typeOfTechnique.REPRODUCTION;
        else if (rand > Re && rand <= (Re + Cr))
            type = typeOfTechnique.CROSSOVER;
        else if (rand > (Re + Cr) && rand <= 100)
            type = typeOfTechnique.MUTATION;
        return type;
    }

    private static ArrayList<Integer> performMutation(ArrayList<Integer> ordering, int examDays) {
        int[] rand = getRandomIndexOfOrderings(examDays);
        Collections.swap(ordering, rand[0], examDays + rand[1]);
        return ordering;
    }

    private static ArrayList<ArrayList<Integer>> performCrossover(ArrayList<Integer> ordering1, ArrayList<Integer> ordering2) {
        ArrayList<ArrayList<Integer>> AfterCrossover = new ArrayList<>();
        ArrayList<Integer> swapRecordForO1 = new ArrayList<>();
        ArrayList<Integer> swapRecordForO2 = new ArrayList<>();
        int NumberOfModule = ordering1.size();
        int examDay = NumberOfModule/2;
        boolean flag = false;
        int cp = (int) (Math.random() * ((NumberOfModule - 4) + 1)) + 2;
//        System.out.println(cp+"!!!!!!!!!!!");

        if(cp <= examDay)
            flag = true;

        if(flag)
        {
            for(int i = 0; i < cp; i++)
            {
                swapRecordForO1.add(ordering1.get(i));
                swapRecordForO2.add(ordering2.get(i));
                swapItem(ordering1,ordering2,i);
            }
        }
        else
        {
            for(int i = cp; i < NumberOfModule; i++)
            {
                swapRecordForO1.add(ordering1.get(i));
                swapRecordForO2.add(ordering2.get(i));
                swapItem(ordering1,ordering2,i);
            }
        }

        repairDuplication(swapRecordForO1,swapRecordForO2,ordering1,ordering2,flag);

        AfterCrossover.add(ordering1);
        AfterCrossover.add(ordering2);
        return AfterCrossover;
    }

    private static void repairDuplication(ArrayList<Integer> swapRecordForO1,ArrayList<Integer> swapRecordForO2,ArrayList<Integer> ordering1,ArrayList<Integer> ordering2,boolean flag)
    {
        ArrayList<Integer> duplicateIndexForO1 = new ArrayList<>();
        ArrayList<Integer> duplicateIndexForO2 = new ArrayList<>();

        for(int i = 0; i < swapRecordForO1.size(); i++)
        {
            if(!swapRecordForO2.contains(swapRecordForO1.get(i)))
                duplicateIndexForO2.add(i);
        }
        for(int i = 0; i < swapRecordForO2.size(); i++)
        {
            if(!swapRecordForO1.contains(swapRecordForO2.get(i)))
                duplicateIndexForO1.add(i);
        }
//
//        System.out.println(duplicateIndexForO1);
//        System.out.println(duplicateIndexForO2);


        repairDuplicationForOneOrdering( duplicateIndexForO1, duplicateIndexForO2,ordering1, swapRecordForO1, flag,1 );
        repairDuplicationForOneOrdering( duplicateIndexForO1, duplicateIndexForO2,ordering2, swapRecordForO2, flag,2 );
    }

    private static void repairDuplicationForOneOrdering(ArrayList<Integer> duplicateIndexForO1, ArrayList<Integer> duplicateIndexForO2, ArrayList<Integer> ordering,ArrayList<Integer> swapRecord, boolean flag,int type )
    {
        ArrayList<Integer> tempDuplicateIndexForO1 = new ArrayList<>();
        tempDuplicateIndexForO1.addAll(duplicateIndexForO1);
        ArrayList<Integer> tempDuplicateIndexForO2 = new ArrayList<>();
        tempDuplicateIndexForO2.addAll(duplicateIndexForO2);
        while(tempDuplicateIndexForO1.size()>0)
        {
            int randIndex1 = (int) (Math.random() * tempDuplicateIndexForO1.size());
            int randIndex2 = (int) (Math.random() * tempDuplicateIndexForO2.size());

            int randNum = (int) (Math.random() * 2);

            int Index1 = tempDuplicateIndexForO1.get(randIndex1);
            int Index2 = tempDuplicateIndexForO2.get(randIndex2);


            if(type == 2)
            {
                Index1 = tempDuplicateIndexForO2.get(randIndex2);
                Index2 = tempDuplicateIndexForO1.get(randIndex1);
            }
            int IndexForSwapRecord = Index2;
            if(!flag)
            {
                Index1 = ordering.size() - swapRecord.size() + Index1;
            }
//            System.out.println("orderingofIndx1     "+ordering.get(Index1));
//            System.out.println("swapRecordForO2     "+swapRecord.get(IndexForSwapRecord));
//            System.out.println("randNum     "+randNum);
            if(randNum==0)
            {
                for(int i = 0; i < ordering.size(); i++)
                {
                    if(ordering.get(i) == ordering.get(Index1))
                    {
                        ordering.set(i,swapRecord.get(IndexForSwapRecord));
                        break;
                    }
                }
            }
            else if(randNum==1)
            {
                for(int i = ordering.size()-1; i >= 0 ; i--)
                {
                    if(ordering.get(i) == ordering.get(Index1))
                    {
                        ordering.set(i,swapRecord.get(IndexForSwapRecord));
                        break;

                    }
                }
            }
            tempDuplicateIndexForO1.remove(randIndex1);
            tempDuplicateIndexForO2.remove(randIndex2);
        }
    }
    private static void swapItem(ArrayList<Integer> ordering1, ArrayList<Integer> ordering2, int index)
    {
        int temp = ordering1.get(index);
        ordering1.set(index,ordering2.get(index));
        ordering2.set(index,temp);
    }

}

