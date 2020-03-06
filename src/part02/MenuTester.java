package part02;

import java.util.InputMismatchException;
import java.util.Scanner;

public class MenuTester {

    protected static VendingMachine vendingMachine;
    static Scanner input;
    static double coinsInput[];
    static double total = 0;
    

    public static void main(String[] args) {
        //*Was thinking of doing extends VendingMachine so it could change what is printed by menu?

        initVendMachine();
        input = new Scanner(System.in);
        initMenu();
        input.close();
        System.out.println("Goodbye!");
        
    }

    private static void initVendMachine() {

        //Max of 50 coins can be input by user5
        //Change to variable adjustable by engineer

        //*On part 2, initially set to new VendingMachine("UNDEFINED", 0);
        //*Then check if those are still the values and if so produce an error
        vendingMachine = new VendingMachine("Coca Cola", 10);
        VendItem cocaCola = new VendItem("Coca Cola Zero 550ml", 1.35);
        VendItem fanta = new VendItem("Fanta Orange 550ml", 1.35);
        VendItem taytoCheese = new VendItem("Tayto Cheese and Onion", 0.70, 2);
        vendingMachine.setStatus(Status.VENDING_MODE);
        vendingMachine.addNewItem(cocaCola);
        vendingMachine.addNewItem(fanta);
        vendingMachine.addNewItem(taytoCheese);
        System.out.println(vendingMachine.getDetails());
    }

    private static void initMenu() {
        //set this to options and title as param for initMenu, then allow employeeMenu to just call this init menu
        String menuOptions[] = {"View All Items", "Insert Coins", "Purchase an Item", "Quit"};
        Menu vendMenu = new Menu("VendOS v1.0", menuOptions);

        //Condition to check if user has chose to quit and ensure last option is Quit
        //Maybe set it instead that if menuOptions[option-1].equals("Quit") for more robust?
        int choice = -1;
        do {
            String extraDetails = "";
            if(vendingMachine.getVmStatus() == Status.SERVICE_MODE) {
                extraDetails += vendingMachine.getVmStatus().getStatus();
                extraDetails += " - PURCHASING DISABLED\n";
            }
            if(vendingMachine.getInputCoins().size() > 0) {
                extraDetails += "Currently inserted coins: ";
                for (int coin : vendingMachine.getInputCoins()) {
                    if(coin < 5) {
                        extraDetails += "£" + coin + ", ";
                    }
                    else if(coin > 2) {
                        extraDetails += coin + "p, "; //TODO remove the , if it's the only one; cleans it up
                    }
                }
                extraDetails += "\n";
            }
            extraDetails += String.format("Current funds inserted: £%.2f\n", vendingMachine.getUserMoney());
            vendMenu.setExtraDetails(extraDetails);
            choice = vendMenu.getChoice();
            processChoice(choice);
        } while (choice != menuOptions.length);

    }

    private static void processChoice(int choice) {

        switch (choice) {
            case 1:
                listAll();
                break;

            case 2:
                insertCoins();
                break;

            case 3:
                purchaseItem();
                break;

            case 5:
                EmployeeMenu empMenu = new EmployeeMenu();
                break;

            default:
                break;
        }
    }

    public static void listAll() {
        for (String item : vendingMachine.listItems()) {
            System.out.println(item);
        }
    }

    private static void insertCoins() {
        System.out.println("Insert a Coin");
        System.out.println("+++++++++++\n");
        System.out.println("NOTE: This vending machine only accepts 5p, 10p, 20p, 50p, £1 and £2 denominations");;

        int inputCoin = -1;


        while(inputCoin != 0) {
            System.out.printf("Current inserted value: £%.2f\n", vendingMachine.getUserMoney());
            System.out.print("Please enter coin, enter 0 to finish: ");
            try {
                inputCoin = input.nextInt();
                if(inputCoin == 0) {
                    break;
                }
                if(vendingMachine.insertCoin(inputCoin) == false) {
                    if(vendingMachine.getVmStatus() == Status.SERVICE_MODE) {
                        System.out.println("Machine is in service mode.");
                        break;
                    }
                    System.out.println("Please enter only the denominations listed.");
                }
            } catch (InputMismatchException e) {
                System.err.println("Please insert a valid coin.");
                input.next();
                continue;
            }
        }
    }


    public static VendItem selectItem() {
        
        int chosenId = -1;
        VendItem chosenItem = null;
        
        while(chosenItem == null) {
            listAll();
            System.out.print("\nEnter the number of the item you wish to select, enter 0 to cancel: ");
            try {
                chosenId = input.nextInt();
                input.nextLine();
                if(chosenId == 0) {
                    return null;
                }
                chosenItem = vendingMachine.findItem(chosenId);
                return chosenItem;
            } catch (InputMismatchException e) {
                System.err.println("Please enter a valid number.");
                input.next();
                continue;
            }
            catch (NullPointerException e) {
                System.err.println("Item not found.");
                continue;
            }
        }
        return null;
        
    }

    private static void purchaseItem() {
        VendItem chosenItem = selectItem();
        if(chosenItem.getQty() == 0) {
            System.out.printf("Selected item: %d. %s at £%.2f.\n", chosenItem.getItemId(), chosenItem.getName(), chosenItem.getPrice());
            System.out.println("THIS ITEM IS OUT OF STOCK.");
            return;
        }
        else {
            System.out.printf("Selected item: %d. %s at £%.2f.\n", chosenItem.getItemId(), chosenItem.getName(), chosenItem.getPrice());
        }
        System.out.println("\nWould you like to purchase this item? Y/N: ");

        while (true) {
            char choice = input.nextLine().charAt(0);
            if(Character.toUpperCase(choice) == 'Y') {
                System.out.println(vendingMachine.purchaseItem(chosenItem.getItemId()));
                break;
            }
            else if(Character.toUpperCase(choice) == 'N') {
                System.out.println("Item not purchased.");
                break;
            }
            else {
                System.out.println("Please enter Y for yes or N for no.");
                continue;
            }
        }
    }


}

//Add refund method?
// TODO Add change etc for customer