package part02;

import java.util.InputMismatchException;
import java.util.Scanner;

public class EmployeeMenu extends MenuTester {
    static Scanner input;

    public EmployeeMenu() {
        input = new Scanner(System.in);
        initEmpMenu();
        System.out.println("Going back to customer menu.");
    }

    private void initEmpMenu() {
        String menuOptions[] = {"View All Items", "View an Item", "Add an Item", "Restock an Item", "View Vending Machine Details", "Set Status", "Back"};
        Menu vendMenu = new Menu("VendOS v1.0 - EMPLOYEE MENU", menuOptions);

        //Condition to check if user has chose to quit and ensure last option is Quit
        //Maybe set it instead that if menuOptions[option-1].equals("Quit") for more robust?
        int choice = -1;
        do {
            String extraDetails = "";
            if(vendingMachine.getVmStatus() == Status.SERVICE_MODE) {
                extraDetails += vendingMachine.getVmStatus().getStatus();
                extraDetails += " - PURCHASING DISABLED\n";
            }

            vendMenu.setExtraDetails(extraDetails);
            choice = vendMenu.getChoice();
            processChoice(choice);
        } while (choice != menuOptions.length);

    }

    private void processChoice(int choice) {
        switch (choice) {
            case 1:
                super.listAll();
                break;
        
            case 2:
                viewItemDetails();
                break;
        
            case 3:
                addItem();
                break;

            case 4:
                restockItem();
                break;
            case 5:
                getDetails();
                break;

            case 6:
                setStatus();
                break;

            default:
                break;
        }
    }


    private void addItem() {
        System.out.println("Adding item");
        System.out.println("+++++++++++\n");
        System.out.print("Enter item name: ");
        String newName = input.nextLine();
    
        while(true) {
            System.out.print("Enter item price: £");
            try {
                double newPrice = input.nextDouble();
                input.nextLine();
                VendItem newItem = new VendItem(newName, newPrice);
                if(vendingMachine.addNewItem(newItem)) {
                    System.out.printf("Item named %s at price: £%.2f has been added.\n", newName, newPrice);
                    break;
                }
                else {
                    System.out.println("Adding item failed.");
                    break;
                }
                
            } catch (InputMismatchException e) {
                System.err.println("Please enter a valid value.\n");
                input.next();
                continue;
            }
        }

    }

    private VendItem selectItem() {
        while(true) {
            super.listAll();
            System.out.print("\nPlease enter the number of the item you wish to select: ");
            try {
                int choice = input.nextInt();
                return vendingMachine.findItem(choice);
            } catch (InputMismatchException e) {
                System.err.println("Please enter a valid number.");
                input.next();
                continue;
            }
            catch (NullPointerException e) {
                System.err.println("Please enter the ID of one the items displayed only.\n");
                continue;
            }
        }
        
    }

    private void viewItemDetails() {
        VendItem chosenItem = selectItem();
        System.out.println(chosenItem.getDetails());
    }

    private static void getDetails() {
        System.out.println(vendingMachine.getSystemInfo());
    }

    private void setStatus() {
        int choice = -1;
        while(choice != 0) {
            System.out.println("1. Vending Mode.");
            System.out.println("2. Service Mode.");
            System.out.print("Please select the vending machine status, enter 0 to cancel: ");

            try {
                choice = input.nextInt();
                if(choice-1 == 0) {
                    vendingMachine.setStatus(Status.VENDING_MODE);
                    System.out.println("\nMachine set to " + vendingMachine.getVmStatus().getStatus());
                    break;   
                }
                else if(choice-1 == 1) {
                    vendingMachine.setStatus(Status.SERVICE_MODE);
                    System.out.println("\nMachine set to " + vendingMachine.getVmStatus().getStatus());
                    break;
                }
                else if(choice != 0){
                    System.out.println("\nPlease choose either 1, 2 or 0 to cancel.");
                    input.next();
                    continue;
                }
            } catch (InputMismatchException e) {
                System.err.println("\nPlease choose a valid number.");
                input.next();
                continue;
            }
        }
    }

    private void restockItem() {
        System.out.println("Restocking Item");
        VendItem chosenItem = selectItem();
        
        while(true) {
            System.out.print("\nPlease enter the new stock number: ");
            try {
                int restockAmount = input.nextInt();
                if(chosenItem.restock(restockAmount)) {
                    System.out.println("\nItem " + chosenItem.getName() + " restocked with quantity: " + chosenItem.getQty());
                    break;
                }
                else {
                    System.out.println("\nItem restock failed.");
                }
            } catch (InputMismatchException e) {
                System.err.println("\nPlease enter a valid number.");
                input.next();
                continue;
            }
        }

    }

}