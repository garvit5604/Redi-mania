import Redi.Redi;
import Registration.UserLogin;
import Registration.UserRegistration;
import User.User;

import java.util.Scanner;

public class Initiator {
    public static void Initiate(){
        Scanner sc = new Scanner(System.in);    //scanner object to take inputs from the user regarding instructions and the 
        System.out.println("Welcome to Redi-Mania");
        System.out.println("If you are user, press 1 and then enter");
        System.out.println("If you are owner, press 2 and then enter");
        System.out.println("To exit, press 0 and then enter");
        int n = sc.nextInt();
        //reading the number entered by the user        
        sc.nextLine();  //clearing the scanner
        switch(n){
            //the client is a student who wishes to order food
            case 1: System.out.println("To register, press 1");
                System.out.println("To login, press 2");
                System.out.println("To exit, press 0");
                int m = sc.nextInt();
                sc.nextLine();
                switch(m){
                    //registration
                    case 1: System.out.println("Enter the following details :");
                        System.out.println("Enter name :");
                        String name = sc.nextLine();
                        System.out.println("Enter BITSid :");
                        String BITSid = sc.nextLine();
                        System.out.println("Enter email(must be a BITS email only) :");
                        String email = sc.nextLine();
                        System.out.println("Enter mobile number :");
                        String mobile = sc.nextLine();
                        new UserRegistration(name, BITSid, email, mobile);
                        //UserRegistration class is called with the parameters being the user's data gathered using the scanner
                        if(email.endsWith("@pilani.bits-pilani.ac.in")){
                            System.out.println("You can now login to use the service. ");
                        }
                        Initiate();
                        break;
                    case 2: System.out.println("Enter the following details :");
                        System.out.println("Enter your BITSID :");
                        String id = sc.nextLine();
                        User u = new UserLogin().verifyUser(id);
                        if(u!=null){
                            Thread t = new Thread(u);
                            t.start();
                        }
                        else{
                            System.out.println("You didn't enter correct inputs. Try again");
                            Initiate();
                        }
                        break;
                    case 0: return;
                    default: System.out.println("You didn't enter correct inputs. Try Again");
                            Initiate();
                }
                break;
            case 2: System.out.println("Please enter your Redi ID :");
                String rediName = sc.nextLine();
                if(rediName.equals("Shankar")||rediName.equals("Meera")||rediName.equals("CVR")||rediName.equals("SR")){
                    Thread s = new Thread(new Redi(rediName));
                    s.start();
                }
                else{
                    System.out.println("You didn't enter correct input. Try again");
                    Initiate();
                }
                break;
            case 0: return;
            default: System.out.println("You didn't enter a valid input. Try again");
            Initiate();
        }
    }
}
