package Redi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Redi implements Runnable{
    private Menu menu;
    //private Owner rediOwner;
    private final String rediId;
    public Redi(String rediId){
        this.menu = new Menu(rediId);
        this.rediId = rediId;
    }
    public String getRediId(){
        return this.rediId;
    }
    public Menu getMenu(){
        return this.menu;
    }
public void run(){
    rediMethod();
}
    public synchronized void getMostOrderedItem(){
        Scanner menuReader = null;
        ArrayList<String> itemIdString = new ArrayList<>();
        ArrayList<Integer> itemFrequency = new ArrayList<Integer>();
        try{
            menuReader = new Scanner(new FileInputStream(rediId + "orders.txt"));
            while(menuReader.hasNextLine()){
                String[] s = menuReader.nextLine().split(",");
                if(itemIdString.contains(s[2])){
                    int i = itemIdString.indexOf(s[2]);
                    itemFrequency.set(i,itemFrequency.get(i) +Integer.parseInt(s[3]));
                }
                else{
                    itemIdString.add(s[2]);
                    itemFrequency.add(Integer.parseInt(s[3]));
                }
            }
            int index = 0;
            for(int i=0; i<itemFrequency.size();i++){
                if(itemFrequency.get(i)>itemFrequency.get(index)){
                    index = i;
                }
            }
            System.out.println("The most ordered item at "  + rediId + " redi " + "is item " + itemIdString.get(index) + " with "
                    + itemFrequency.get(index) + " orders");
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
    public synchronized void deliverOrder(String timestamp, int id){
        Scanner orderReader = null;
        boolean flag = false;
        FileWriter delivery = null;
        try{
            orderReader = new Scanner(new FileInputStream(rediId + "orders.txt"));
            ArrayList<String []> orders = new ArrayList<>();
            while(orderReader.hasNextLine()){
                String[] s = orderReader.nextLine().split(",");
                if(s[0].equals((new java.util.Date(Integer.parseInt(timestamp)*1000L)).toString())&&s[1].equals("" + id)){
                    s[5] = "Delivered";
                    flag = true;
                }
                orders.add(s);
            }
            if(!flag){
                System.out.println("No order found with this id at the given timestamp");
                return;
            }
            delivery = new FileWriter(rediId + "orders.txt");
            for(int i=0; i< orders.size();i++){
                String[] t = orders.get(i);
                delivery.write(t[0] + "," + t[1] + "," + t[2] + "," + t[3] + "," + t[4] + "," + t[5] + "\n");
            }
            delivery.close();
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
    public synchronized void calculateRevenue(){
        float revenue = 0;
        Scanner calcRev = null;
        try{
            calcRev = new Scanner(new FileInputStream(rediId+"orders.txt"));
            while(calcRev.hasNextLine()){
                String[] s = calcRev.nextLine().split(",");
                if(s[5].equals("Delivered")){
                    revenue += Float.parseFloat((s[4].split(" "))[1]);
                }
            }
            System.out.println("The total revenue generated is ₹" + revenue);
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
    public synchronized void displayMenu(){
        Scanner printMenu = null;
        try{
            System.out.println("The menu for this redi is :");
            printMenu = new Scanner(new FileInputStream(rediId + "menu.txt"));
            while(printMenu.hasNextLine()){
                String[] s = printMenu.nextLine().split(",");
                System.out.println("ItemName: " + s[0] + "    " + "ItemId: " + s[1] + "   " + "Price: " + s[2]);
            }
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
    public synchronized void viewPendingOrders(){
        Scanner viewPenOrd = null;
        try{
            viewPenOrd = new Scanner(new FileInputStream(rediId + "orders.txt"));
            while(viewPenOrd.hasNextLine()){
                String s[] = viewPenOrd.nextLine().split(",");
                java.util.Date cuDate = new java.util.Date();
                long time = cuDate.getTime()/1000;
                SimpleDateFormat format = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
                java.util.Date date = format.parse(s[0]);
                long timestamp = date.getTime()/1000;
                if(s[5].equals("Pending")){
                    System.out.println("Timestamp: " + timestamp + "    OrderingId: " + s[1] + "    ItemId: " + s[2] + "    Quantity: "
                            + s[3] + "  Price: " + s[4]);
                }
            }
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
        catch(ParseException e){
            System.out.println(e.getMessage());
        }
    }
    public synchronized void mostFreqTime(){
        Scanner orderTime = null;
        try{
            orderTime =  new Scanner(new FileInputStream(rediId + "orders.txt"));
            int[] frequency = new int[4];
            String[] tinfo = {"Morning - 5AM to 10AM","Afternoon - 11AM to 4PM","Evening - 5PM to 10PM","Night - 11PM to 4AM"};
            while(orderTime.hasNextLine()){
                String time = orderTime.nextLine().split(",")[0].split(" ")[3].split(":")[0];
                int t = Integer.parseInt(time);
                if(t>=5&&t<=10){
                    frequency[0]++;
                }
                if(t>=11&&t<=16){
                    frequency[1]++;
                }
                if(t>=17&&t<=22){
                    frequency[2]++;
                }
                else{
                    frequency[3]++;
                }
            }
            int max_time = 0;
            for(int i=0; i<4; i++){
                if(frequency[i]>frequency[max_time]){
                    max_time = i;
                }
            }
            System.out.println("The most frequent time for orders is: " + tinfo[max_time]);
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
    public synchronized void viewLastWeekIncome(){
        Scanner lastWeek = null;
        try{
            lastWeek = new Scanner(new FileInputStream(rediId + "orders.txt"));
            float weekInc = 0;
            while(lastWeek.hasNextLine()){
                String[] s = lastWeek.nextLine().split(",");
                Date cuDate = new Date();
                long cuSec = cuDate.getTime()/1000;
                SimpleDateFormat format = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
                Date orderDate = format.parse(s[0]);
                long orderTime = orderDate.getTime()/1000;
                if(s[5].equals("Delivered")&&cuSec - orderTime<=604800){
                    weekInc = weekInc + Float.parseFloat(s[4].split(" ")[1]);
                }
            }
            System.out.println("The total earnings for the last week are - ₹" + weekInc);
        }
        catch(ParseException e){
            System.out.println(e.getMessage());
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
    public synchronized void rediMethod(){
        Scanner sc = new Scanner(System.in);
        System.out.println("To get most ordered item press 1");
        System.out.println("To calculate revenue press 2");
        System.out.println("To deliver order press 3");
        System.out.println("To display menu press 4");
        System.out.println("To view pending orders press 5");
        System.out.println("To update item quantity press 6");
        System.out.println("To add item press 7");
        System.out.println("To make item unavailable press 8");
        System.out.println("To update price of an item press 9");
        System.out.println("To view the most frequent time for orders press 10");
        System.out.println("To view the earnings of last week press 11");
        System.out.println("To exit press 0");
        int k = sc.nextInt();
        sc.nextLine();
        switch(k){
            case 1: getMostOrderedItem();
                    rediMethod();
                    break;
            case 2: calculateRevenue();
                    rediMethod();
                    break;
            case 3: viewPendingOrders();
                System.out.println("Enter the timestamp for the order which you want to deliver: ");
                long timest = sc.nextLong();
                sc.nextLine();
                System.out.println("Enter the ordering id whose order you want to deliver: ");
                int id = sc.nextInt();
                sc.nextLine();
                deliverOrder(""+timest,id);
                rediMethod();
                break;
            case 4: displayMenu();
                    rediMethod();        
                    break;
            case 5: viewPendingOrders();
                    rediMethod();
                    break;
            case 6: displayMenu();
                    System.out.println("Enter the item ID for which you want to update the quantity: ");
                    int itemid = sc.nextInt();
                    sc.nextLine();
                    System.out.println("Enter the new quantity: ");
                    int quantity = sc.nextInt();
                    sc.nextLine();
                    this.menu.updateQuantity(itemid, quantity);
                    rediMethod();
                    break;
            case 7: System.out.println("Enter the item name: ");
                    String name = sc.nextLine();
                    System.out.println("Enter the item ID: ");
                    int itemId = sc.nextInt();
                    sc.nextLine();
                    System.out.println("Enter quantity available: ");
                    int quant = sc.nextInt();
                    sc.nextLine();
                    System.out.println("Enter the price: ");
                    float price = sc.nextFloat();
                    sc.nextLine();
                    this.menu.addItem(new Item(name, "" + itemId, "" + quant, "" + price));
                    rediMethod();
                    break;
                    
            case 8: displayMenu();
                    System.out.println("Enter item ID which you make unavailable");
                    int item = sc.nextInt();
                    sc.nextLine();
                    this.menu.updateQuantity(item, -1);
                    rediMethod();
                    break;
            case 9: displayMenu();
                    System.out.println("Enter the item ID whose price needs to be updated");
                    int itemID = sc.nextInt();
                    sc.nextLine();
                    System.out.println("Enter the new price");
                    float newPrice = sc.nextFloat();
                    sc.nextLine();
                    this.getMenu().updatePrice(itemID, newPrice);
                    rediMethod();
                    break;
            case 10: mostFreqTime();
                    rediMethod();
                    break;
            case 11: viewLastWeekIncome();
                    rediMethod();
                    break;
            case 0: return;

            default: System.out.println("YOu didn't enter valid option. Try again");
                    rediMethod();
        }
    }
}
