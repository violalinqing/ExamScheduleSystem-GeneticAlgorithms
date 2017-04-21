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
        G = 150;//getNumberFromUser("number of generations:", 0, in);
        P = 30;//getNumberFromUser("Population size:", 0, in);
        S = 80;//getNumberFromUser("number of students:", 0, in);
        M = 36;//getNumberFromUser("total number of modules:", 1, in);
        C = 5;//getNumberFromUserAndCheck("the number of modules in a course:", in, M);
        //int[] percentage = getPercentageAndCheck(in);
        Re = 80;//percentage[0];
        Mu = 10;//percentage[1];
        Cr = 100 - (Re + Mu);
        D = (M / 2);

        System.setOut(new PrintStream(new FileOutputStream("AI17.txt")));

        ArrayList<ArrayList<Integer>> studentSchedule = new ArrayList<ArrayList<Integer>>();
        ArrayList<Boolean[]> studentScheduleMark = new ArrayList<Boolean[]>();

        for (int studentNum = 0; studentNum < S; studentNum++) {
            Boolean[] mark = new Boolean[M];
            Arrays.fill(mark, false);

            ArrayList<Integer> courseNum = newStudentModuleInfo(C, M, mark, studentNum);
            System.out.println("\n");

            studentSchedule.add(courseNum);
            studentScheduleMark.add(mark);
        }

        ArrayList<int[][]> orderings = new ArrayList<>();
        int i = 0;
        while (i < P) {
            int[][] newOrdering = createNewOrdering(studentScheduleMark,M);

            boolean flag = checkRepeatOrder(newOrdering, orderings);

            if (!flag) {
                orderings.add(newOrdering);
                i++;
            }
        }
        for (int j = 0; j <= G; j++) {
            QuickSort(orderings, 0, orderings.size() - 1,D);
            performSelection(orderings,D);
            printOrdering(j,orderings.get(0),D);
            System.out.println(" cost: " + orderings.get(0)[1][D]);
            System.out.print("\n\n");
            orderings = chooseTechnique(orderings, Re, Mu, Cr, D, studentScheduleMark);
        }
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
        System.out.print("Student" + (studentNum + 1) + ":");
        while (StudentModules.size() < n) {
            int courseNum = (int) (Math.random() * totalNumberOfModule + 1);
            boolean flag = true;
            if (StudentModules.contains(courseNum)) {
                flag = false;
            }
            if (flag) {

                StudentModules.add(courseNum);
                mark[courseNum - 1] = true;
                System.out.print("\t"+"M" + courseNum);
            }
        }
        return StudentModules;
    }

    private static int[][] createNewOrdering(ArrayList<Boolean[]> studentScheduleMark,int totalNumberOfModule) {
        int[][] newOrdering = new int[2][totalNumberOfModule/2+1];
        ArrayList<Integer> temp = new ArrayList<>();
        int cost = 0;
        for(int i = 0; i < 2; i++)
        {
            for(int j = 0; j < totalNumberOfModule/2; j++)
            {
                boolean flag = false;
                while (!flag&&temp.size() <= totalNumberOfModule) {
                    int courseNum = (int) (Math.random() * totalNumberOfModule + 1);
                    if (!temp.contains(courseNum)) {
                        flag = true;
                        temp.add(courseNum);
                        newOrdering[i][j] = courseNum;
                    }
                }
            }
        }
        cost = fitnessFunction(studentScheduleMark, newOrdering);
        newOrdering[0][totalNumberOfModule/2] = 0;
        newOrdering[1][totalNumberOfModule/2] = cost;
        return newOrdering;
    }


    private static boolean checkRepeatOrder(int[][] ordering, ArrayList<int[][]> orderings) {
        if (orderings.contains(ordering)) {
            return true;
        }
        return false;
    }


    private static void printOrdering(int OrderNum, int[][] ordering, int examDays) {
        System.out.print("Gen " + (OrderNum) + ":");
        for(int i = 0; i < 2; i++)
        {
            for (int k = 0; k < examDays; k++) {
                System.out.print("\t" + "m" + ordering[i][k]);

            }
            System.out.println();
        }

    }


    private static int fitnessFunction(ArrayList<Boolean[]> studentScheduleMark, int[][] ordering) {
        int cost = 0;
        for (int i = 0; i < studentScheduleMark.size(); i++) {
            for (int j = 0; j < studentScheduleMark.get(i).length/2; j++) {
                int n1 = ordering[0][j] - 1;
                int n2 = ordering[1][j] - 1;
                if (studentScheduleMark.get(i)[n1] && studentScheduleMark.get(i)[n2]) {
                    cost++;
                }
            }
        }
        return cost;
    }

    private static void QuickSort(ArrayList<int[][]> orderings,  int low, int high,int D) {
        if (low < high) {
            int key1 = orderings.get(low)[1][D];
            int[][] key2 = orderings.get(low);
            int i = low, j;
            for (j = low + 1; j <= high; j++) {
                if (orderings.get(j)[1][D] < key1) {
                    Collections.swap(orderings, i + 1, j);
                    i++;
                }
            }
            orderings.set(low, orderings.get(i));
            orderings.set(i, key2);
            QuickSort(orderings, low, i - 1,D);
            QuickSort(orderings, i + 1, high,D);

        }
    }

    private static ArrayList<int[][]> performSelection(ArrayList<int[][]> initialOrderings,int D) {
        if (initialOrderings.size() > 3) {
            int mod = initialOrderings.size() % 3;
            int divisor = (int) Math.floor((initialOrderings.size() + mod) / 3);
            int startpoint = initialOrderings.size() - divisor;
            for (int i = 0; i < divisor; i++) {
                initialOrderings.set(startpoint, copyArrayList(initialOrderings.get(i),D));
                startpoint++;
            }
        }
        return initialOrderings;
    }

    private static int[][] copyArrayList(int[][] old,int D)
    {
        int[][] copy = new int[2][D+1];
        for(int i = 0; i < 2; i++)
        {
            for(int j = 0;j <= D; j++)
            {
                copy[i][j] = old[i][j];
            }
        }

        return  copy;
    }

    private static ArrayList<int[][]> chooseTechnique(ArrayList<int[][]> orderings, int Re, int Mu, int Cr, int examDays, ArrayList<Boolean[]> studentScheduleMark) {
        ArrayList<int[][]> orderingAfterGA = new ArrayList<>();
        int i=0;
        while (orderings.size() > 0) {
            typeOfTechnique type = getTypeOfTechnique(Re, Mu, Cr);
            int cost;
            switch (type) {
                case REPRODUCTION:

                    int randForReprodction = (int) (Math.random() * orderings.size());
//                     System.out.println("Reproduction Ordering:"+randForReprodction);
                    int[][] newOrdering1 = orderings.get(randForReprodction);
                    cost = fitnessFunction(studentScheduleMark,newOrdering1);
                    newOrdering1[1][examDays] = cost;
                    orderingAfterGA.add(newOrdering1);
//                    printOrdering(i,newOrdering1,examDays);
//                    System.out.println(cost);
                    orderings.remove(randForReprodction);
                    i++;
//                     System.out.println();
                    break;
                case MUTATION:

                    int randForMutation = (int) (Math.random() * orderings.size());
//                    System.out.println("MUTATION Ordering:"+randForMutation );
                    int[][] newOrdering2 = performMutation(copyArrayList(orderings.get(randForMutation),examDays), examDays);
                    cost = fitnessFunction(studentScheduleMark,newOrdering2);
                    newOrdering2[1][examDays] = cost;
                    orderingAfterGA.add(newOrdering2);
//                    printOrdering(i,newOrdering2,examDays);
//                    System.out.println(cost);
//                    System.out.println();
                    orderings.remove(randForMutation);
                    i++;
                    break;
                case CROSSOVER:
                    if (orderings.size() >= 2) {
                        int[] randForCrossover = getRandomIndexOfOrderings(orderings.size());
//                        printOrdering(i,orderings.get(randForCrossover[0]),examDays);
//                        printOrdering(i,orderings.get(randForCrossover[1]),examDays);
//                        System.out.println("CROSSOVER Ordering:"+randForCrossover[0]+" and "+randForCrossover[1]);
                        ArrayList<int[][]> AfterCrossover = performCrossover(orderings.get(randForCrossover[0]),orderings.get(randForCrossover[1]), examDays);
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

                        cost = fitnessFunction(studentScheduleMark,AfterCrossover.get(0));
                        AfterCrossover.get(0)[1][examDays] = cost;
                        orderingAfterGA.add(AfterCrossover.get(0));
//                        printOrdering(i,AfterCrossover.get(0),examDays);
//                                                System.out.println(cost);
                        i++;
                        cost = fitnessFunction(studentScheduleMark,AfterCrossover.get(1));
                        AfterCrossover.get(1)[1][examDays] = cost;
                        orderingAfterGA.add(AfterCrossover.get(1));
//                        printOrdering(i,AfterCrossover.get(1),examDays);
//                                               System.out.println(cost);
                        i++;
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

    private static int[][] performMutation(int[][] ordering, int examDays) {
        int[] rand = getRandomIndexOfOrderings(examDays);
//        System.out.println(rand[0] + " to " + examDays + rand[1]);
        int temp = ordering[0][rand[0]];
        ordering[0][rand[0]] = ordering[1][rand[1]];
        ordering[1][rand[1]] = temp;
        return ordering;
    }

    private static ArrayList<int[][]> performCrossover(int[][] ordering1, int[][] ordering2,int D) {
        ArrayList<int[][]> AfterCrossover = new ArrayList<>();
        ArrayList<Integer> swapRecordForO1 = new ArrayList<>();
        ArrayList<Integer> swapRecordForO2 = new ArrayList<>();
        int NumberOfModule = D*2;
        boolean flag = false;
        int cp = (int) (Math.random() * ((NumberOfModule - 4) + 1)) + 2;
//        System.out.println(cp+"!!!!!!!!!!!");
        if(cp <= D)
            flag = true;

        if(flag)
        {
            for(int i = 0; i < cp; i++)
            {
                swapRecordForO1.add(ordering1[0][i]);
                swapRecordForO2.add(ordering2[0][i]);
                swapItem(ordering1,ordering2,i,D);
            }
        }
        else
        {
            for(int i = cp; i < NumberOfModule; i++)
            {
                swapRecordForO1.add(ordering1[1][i-D]);
                swapRecordForO2.add(ordering2[1][i-D]);
                swapItem(ordering1,ordering2,i,D);
            }
        }
//        System.out.println(swapRecordForO1);
//        System.out.println(swapRecordForO2);
        repairDuplication(swapRecordForO1,swapRecordForO2,ordering1,ordering2,flag,D);

        AfterCrossover.add(ordering1);
        AfterCrossover.add(ordering2);
        return AfterCrossover;
    }

    private static void repairDuplication(ArrayList<Integer> swapRecordForO1,ArrayList<Integer> swapRecordForO2,int[][] ordering1,int[][] ordering2,boolean flag,int D)
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
//        System.out.println(duplicateIndexForO1);
//        System.out.println(duplicateIndexForO2);
        repairDuplicationForOneOrdering( duplicateIndexForO1, duplicateIndexForO2,ordering1, swapRecordForO1, flag,1,D );
        repairDuplicationForOneOrdering( duplicateIndexForO1, duplicateIndexForO2,ordering2, swapRecordForO2, flag,2,D );
    }

    private static void repairDuplicationForOneOrdering(ArrayList<Integer> duplicateIndexForO1, ArrayList<Integer> duplicateIndexForO2, int[][] ordering,ArrayList<Integer> swapRecord, boolean flag,int type,int D )
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
            int row = 0;
            if(!flag)
            {
                row = 1;
                Index1 = D - swapRecord.size() + Index1;
            }
//
//                        System.out.println("orderingofIndx1     "+ordering[row][Index1]);
//                        System.out.println("swapRecordForO2     "+swapRecord.get(IndexForSwapRecord));
//                        System.out.println("randNum     "+randNum);
            if(randNum==0)
            {
                for(int i = 0; i < 2; i++)
                {
                    boolean found = false;
                    for(int j = 0; j < D; j++)
                    {
                        if(ordering[i][j] == ordering[row][Index1])
                        {
                            ordering[i][j] = swapRecord.get(IndexForSwapRecord);
                            found = true;
                            break;
                        }
                    }
                    if(found)
                    {
                        break;
                    }
                }

            }

            else if(randNum==1)
            {
                for(int i = 1; i >= 0; i--)
                {
                    boolean found = false;
                    for(int j = D-1; j >= 0 ; j--)
                    {
                        if(ordering[i][j] == ordering[row][Index1])
                        {
                            ordering[i][j] = swapRecord.get(IndexForSwapRecord);
                            found = true;
                            break;
                        }
                    }
                    if(found)
                    {
                        break;
                    }
                }
            }
            tempDuplicateIndexForO1.remove(randIndex1);
            tempDuplicateIndexForO2.remove(randIndex2);
        }
    }

    private static void swapItem(int[][] ordering1, int[][] ordering2, int index, int D)
    {
        if(index < D)
        {
            int temp = ordering1[0][index];
            ordering1[0][index] = ordering2[0][index];
            ordering2[0][index] = temp;
        }
        else
        {
            int temp = ordering1[1][index-D];
            ordering1[1][index-D] = ordering2[1][index-D];
            ordering2[1][index-D] = temp;
        }
    }

}

