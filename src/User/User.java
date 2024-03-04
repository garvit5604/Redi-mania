package User;

import Redi.Menu;
import Redi.Redi;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

//multithreading has been implemented on the methods of the User class
public class User implements Runnable{
    //the given class implements the Runnable interface, and thus has a run() method
    private String name;
    private String id;
    private String email;
    private String mobile;
    //constructor for the User class which uses the name, id, email and mobile address of the user
    public User(String name, String id, String email, String mobile) {
        this.name=name;
        this.id = id;
        this.email = email;
        this.mobile = mobile;
        FileWriter user = null; //a FileWriter object which opens the file made for the user during registration to house order information
        try{
            user = new FileWriter(id + ".txt",true);
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
    public void run(){
        userMethod();
    }
    //the run() method is calling the userMethod() which has 
    //code to further call any of the other methods defined for the class

    //this is the method called when the user attempts to order food
    public synchronized void orderFood(long timestamp, String BITSid, String redi, int itemId, int quantity){
        java.util.Date time = new java.util.Date((long)timestamp*1000); //the timestamp is defined using Unix epoch time
        FileWriter orderFood = null;    //object which logs food order into the file of the user
        FileWriter logOrder = null; //object which will be used to add food order of the user to the redi's orders file, which will be used by the owner to fulfill orders
        Scanner priceReader = null; //scanner which reads data from the menu of the specified redi
        String price = null;    //stores the price of the item
        int updatedQuantity = 0;    //variable to store the updated quantity if the order can be made
        ArrayList<String []> menuTrack = new ArrayList<>();
        try{
            priceReader = new Scanner(new FileInputStream(redi+"menu.txt"));
            while(priceReader.hasNextLine()){
                String[] s = priceReader.nextLine().split(",");
                //the array s stores the menu entry data
                if(s[1].equals(""+itemId)){
                    //checks if the required number of items are available
                    int k = Integer.parseInt(s[3]); //k is the number of items available of the itemtype
                    if(k>=quantity){
                        price = s[2];
                        s[3] = "" + (k - quantity); //updates the new quantity available and stores it in the string array s
                        updatedQuantity = k - quantity;
                        break;
                    }
                    else if(k<=0){
                        System.out.println("Sorry, the selected food item is currently unavailable");
                        return;
                    }
                    else{   //if the itemId is not available in the required numbers
                        System.out.println("Sorry the selected food item is not available in the required quantity");
                        return;
                    }
                }
            }
            priceReader.close();
            if(price == null){  //if the itemId does not match any of the items in the menu, the price is not updated and it can be seen that the item is not available at the redi at all
                System.out.println("Selected food item is not available at "+redi+" Redi");
                return;
            }
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
        try{
            if(this.id.equals(BITSid)) {    //ensures that the person placing the order is registered, and has the same id as the user
                orderFood = new FileWriter(BITSid + ".txt", true);
                orderFood.write(time.toString() + "," + BITSid + "," + redi + "," + "" + itemId + "," + quantity + "," +
                        "Rs " + "" + (Float.parseFloat(price)) * quantity + "\n");
                //this enters the food order of the user to the user's own text file
                orderFood.close();
                logOrder = new FileWriter(redi + "orders.txt",true);
                logOrder.write(time.toString() + "," + BITSid + "," + "" + itemId + "," + quantity + "," +
                        "Rs " + "" + (Float.parseFloat(price)*quantity) + "," + "Pending" + "\n");
                //this enters the food order of the user to the redi's orders file
                System.out.println("Order placed successfully");
                logOrder.close();
            }
            else{   //this block is triggered if the person placing the order is not the same as the user using the application
                System.out.println("Please register first to order");
            }
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
        
        Menu menu = new Menu(redi);
        //an object for the menu of the specified redi is created
        menu.updateQuantity(itemId, updatedQuantity);   //the updated quantity after the user has ordered is updated in the menu by calling the updateQuantity function
        
    }
    public synchronized void showTotalExpenses(){
        //this is a method to show the total lifetime expenses of the user
        float expense = 0;  //variable to store the expenses of the user
        Scanner expenseTracker = null;  //scanner object which will be linked to the usr's file and used to parse through the file to find teh total expenses
        try{
            expenseTracker = new Scanner(new FileInputStream(id+".txt"));
            while(expenseTracker.hasNextLine()){
                String[] s = expenseTracker.nextLine().split(",");
                //s is an array which stores the order data of each order from the user's file
                expense += Float.parseFloat((s[5].split(" "))[1]);
            }
            System.out.println("The total expenses for you are : Rs. " + expense);
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
    public synchronized void lastMonthExpense(){
        //method which shows only the expenses from the last one month for the user, and not lifetime expenses
        float expense = 0;  //variable to store the expenses for the user over the last one month
        Scanner expenseTracker = null;  //
        try{
            expenseTracker = new Scanner(new FileInputStream(id + ".txt"));
            while(expenseTracker.hasNextLine()){
                String[] s = expenseTracker.nextLine().split(",");
                Date cuDate = new Date();
                long time = cuDate.getTime()/1000;
                SimpleDateFormat format = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
                Date date = format.parse(s[0]);
                long timestamp = date.getTime()/1000;
                if(time-timestamp<=2592000){
                    expense += Float.parseFloat((s[5].split(" "))[1]);
                }
            }
            System.out.println("The expenses for you in the past one month are Rs. " + expense);
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
        catch (ParseException e){
            System.out.println(e.getMessage());
        }
    }
    public synchronized void amountSpentPerItem(String redi, int itemId){
        float expItem = 0;
        Scanner aspi = null;
        try{
            aspi = new Scanner(new FileInputStream(id+".txt"));
            while(aspi.hasNextLine()){
                String[] s = aspi.nextLine().split(",");
                if(s[2].equals(redi)&&s[3].equals(""+itemId)){
                    expItem = expItem + Float.parseFloat((s[5].split(" "))[1]);
                }
            }
            System.out.println("The total expense at " + redi + " on " + "item "+"" + itemId +" is Rs. " + expItem);
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
    public String getId(){
        return this.id;
    }
    public void userMethod(){
        Scanner sc = new Scanner(System.in);
        System.out.println("To place order press 1");
        System.out.println("To view total expenses press 2");
        System.out.println("To view expenses in the last month press 3");
        System.out.println("To view amount spent on individual item on each Redi press 4");
        System.out.println("To exit press 0");
        int n = sc.nextInt();
        sc.nextLine();
        switch(n){
            case 1: System.out.println("Enter the following details :");
                    //System.out.println("Getting timestamp :");
                    Date date = new Date();
                    long timestamp = date.getTime()/1000;
                    System.out.println("Enter redi (Please choose from Shankar, Meera, SR, CVR) :");
                    String redi = sc.nextLine();
                    new Redi(redi).displayMenu();
                    System.out.println("Enter itemId :");
                    int itemId = sc.nextInt();
                    sc.nextLine();
                    System.out.println("Enter quantity :");
                    int quantity = sc.nextInt();
                    sc.nextLine();
                    this.orderFood(timestamp, this.id, redi, itemId, quantity);
                    userMethod();
                    break;
            case 2: showTotalExpenses();
                    userMethod();
                    break;
            case 3: lastMonthExpense();
                    userMethod();
                    break;
            case 4: System.out.println("Enter the following details :");
                    System.out.println("Enter redi (Please choose from Shankar, Meera, SR, CVR) :");
                    String rediName = sc.nextLine();
                    new Redi(rediName).displayMenu();
                    System.out.println("Enter item ID :");
                    int item = sc.nextInt();
                    sc.nextLine();
                    amountSpentPerItem(rediName, item);
                    userMethod();
                    break;
            case 0: return;
            default:System.out.println("You didn't enter a valid option. Try again");
                    userMethod();
        }
        }
}
