import java.sql.*;
import java.util.Scanner;

public class HotelReservationSystem {

    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String username = "root";
    private static final String password = "admin";

    public static void main(String[] args) throws ClassNotFoundException, SQLException{

        try{
            Class.forName("com.mysql.jdbc.Driver");
        }catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try{
            Connection con = DriverManager.getConnection(url,username,password);
            while (true){
                System.out.println();
                System.out.println("WELCOME TO HOTEL MANAGEMENT SYSTEM");
                Scanner sc = new Scanner(System.in);
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get room number");
                System.out.println("4. Update Reservation");
                System.out.println("5. Delete Reservation");
                System.out.println("6. Exit");
                System.out.print("Choose an option: ");
                int choice = sc.nextInt();

                switch (choice){
                    case 1:
                        reserveRoom(con,sc);
                        break;
                    case 2:
                        viewReservation(con);
                        break;
                    case 3:
                        getRoomNumber(con,sc);
                        break;
                    case 4:
                        updateReservation(con,sc);
                        break;
                    case 5:
                        deleteReservation(con,sc);
                        break;
                    case 6:
                        sc.close();
                        exit();
                        break;
                    default:
                        System.out.println("Invalid Option. Please select a valid option.");
                }

            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }

    }

    private static void reserveRoom(Connection con, Scanner sc){
        try{
            System.out.print("Enter Guest Name: ");
            String guestName = sc.next();
            sc.nextLine();
            System.out.print("Enter room number: ");
            int roomNumber = sc.nextInt();
            System.out.print("Enter contact number: ");
            String contactNumber = sc.next();

            String query = "INSERT INTO reservations (guest_name, room_no, contact_no)" +
                    "VALUES ('"+guestName+"',"+roomNumber+", '"+contactNumber+"')";

            try(Statement statement = con.createStatement()){
                int affectedRows = statement.executeUpdate(query);

                if(affectedRows>0){
                    System.out.println("Reservation successful.");
                }else {
                    System.out.println("Reservation Failed!");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void viewReservation(Connection con) throws SQLException{
        String query = "SELECT res_id, guest_name, room_no, contact_no, reservation_date FROM reservations;";

        try (Statement statement = con.createStatement()){
            ResultSet resultSet = statement.executeQuery(query);
            System.out.println("Current Reservations: ");
            System.out.println("+----------------+----------------+----------------+-----------------+------------------------+");
            System.out.println("| Reservation ID | Guest Name     | Room No        | Contact No      | Reservation Date       |");
            System.out.println("+----------------+----------------+----------------+-----------------+------------------------+");

            while (resultSet.next()){
                int reservationId = resultSet.getInt("res_id");
                String guestName = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_no");
                String contactNumber = resultSet.getString("contact_no");
                String reservationDate = resultSet.getString("reservation_date");

                System.out.printf("| %-14d | %-15s | %-13d | %-15s | %-19s  |\n",
                        reservationId,guestName,roomNumber,contactNumber,reservationDate); // Display formating
            }
            System.out.println("+----------------+----------------+----------------+-----------------+------------------------+");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void getRoomNumber(Connection con, Scanner sc){

        try{
            System.out.print("Enter reservation ID: ");
            int reservationId = sc.nextInt();
            System.out.print("Enter guest name: ");
            String gusetName = sc.next();

            String query = "SELECT room_no from reservations " +
                    "WHERE res_id =" + reservationId +
                    " AND guest_name = '" + gusetName + "'";

            try(Statement statement = con.createStatement()) {
                ResultSet rs = statement.executeQuery(query);

                if(rs.next()){
                    int roomNumber = rs.getInt("room_no");
                    System.out.println("Room number for Reservation ID " +reservationId + " and guest " +gusetName+ " is: "+roomNumber);
                }else {
                    System.out.println("Room number not found for the given ID and Name.");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void updateReservation(Connection con, Scanner sc){
        try{
            System.out.print("Enter reservation ID to Update: ");
            int reservationId = sc.nextInt();
            sc.nextLine();

            if(reservationExists(con, reservationId)){
                System.out.println("Reservation not found for the given ID.");
                return;
            }
            System.out.print("Enter new Guest name: ");
            String newGuestName = sc.nextLine();
            System.out.print("Enter new Room number: ");
            int newRoomNumber = sc.nextInt();
            System.out.print("Enter new Contact number: ");
            String newContactNumber = sc.next();

            String query = "UPDATE reservations SET guest_name = '" + newGuestName+"', " + "room_no = " + newRoomNumber + ", "
                    + "contact_no = '" + newContactNumber + "' "+ "WHERE res_id = " + reservationId;

            try(Statement statement = con.createStatement()){
                int affectedRows = statement.executeUpdate(query);

                if(affectedRows>0){
                    System.out.println("Reservation Updated Successfully.");
                }else {
                    System.out.println("Reservation Update Failed.");
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void deleteReservation(Connection con, Scanner sc){
        try{
            System.out.println("Enter reservation ID: ");
            int reservationId = sc.nextInt();

            if(reservationExists(con, reservationId)){
                System.out.println("Reservation not found for the given reservation ID.");
                return;
            }

            String query = "DELETE FROM reservations WHERE res_id = " + reservationId;

            try(Statement statement = con.createStatement()){
                int affectedRows = statement.executeUpdate(query);

                if(affectedRows>0){
                    System.out.println("Reservation deleted Successfully.");
                }else {
                    System.out.println("Reservation deletion failed.");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static boolean reservationExists(Connection con, int reservationId){
        try{
            String query = "SELECT res_id FROM reservations WHERE res_id = " + reservationId;

            try(Statement statement = con.createStatement()){
                ResultSet resultSet = statement.executeQuery(query);
                return !resultSet.next();
            }
        }catch (SQLException e){
            e.printStackTrace();
            return true;
        }
    }

    private static void exit() throws InterruptedException{
        System.out.print("Exiting System");
        int i =5;
        while (i>0){
            System.out.print(".");
            Thread.sleep(400);
            i--;
        }
        System.out.println();
        System.out.println("Thank You for Using Hotel Reservation System.");
    }
}
