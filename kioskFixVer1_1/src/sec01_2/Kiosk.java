package sec01_2;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

// Kiosk.java


public class Kiosk extends DBConnector {
	Scanner scanner = new Scanner(System.in);				//Kiosk() 생산자에서 필드전역 선언으로 이동
    private DBConnector dbConnector;
    private int loginAttempt;
    private String loginId; //로그인 값을 저장하는 문자열

    
    public Kiosk() {
        dbConnector = new DBConnector();
        loginAttempt = 0;
    }

    public void start() {
    	System.out.println(" ");
        System.out.println("========== Kiosk ==========");
        System.out.println("1. 로그인");
        System.out.println("2. 회원가입");
        System.out.println("0. 종료");
        System.out.print("메뉴 선택: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // 버퍼 비우기

        switch (choice) {
        case 1:
            login();
            break;
        case 2:
            register();
            break;
        case 0:
            System.out.println("프로그램을 종료합니다.");
            break;
        default:
            System.out.println("잘못된 메뉴 선택입니다.");
            start();
        }
    }
    
    
    private void login() {
        System.out.print("아이디를 입력하세요: ");
        String id = scanner.nextLine();
        System.out.print("비밀번호를 입력하세요: ");
        String password = scanner.nextLine();

        if (isLoginValid(id, password)) {
            System.out.println("로그인 성공!");
            loginAttempt = 0;
            showMenu();
        } else {
            loginAttempt++;
            System.out.println("로그인 실패!");
            if (loginAttempt >= 3) {
                System.out.println("로그인 시도 횟수가 초과되었습니다. 시스템을 종료합니다.");
                System.exit(0);
            }
        }
    }

    private void showMenu() {
       
        System.out.println("\n========== 메뉴 ==========");
        System.out.println("1. 물품 구매");
        System.out.println("2. 재고 확인");
        System.out.println("3. 현금 충전");
        System.out.println("0. 로그아웃");
        System.out.print("메뉴 선택: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // 버퍼 비우기

        switch (choice) {
            case 1:
                purchaseProduct();
                break;
            case 2:
                displayProducts();
                break;
            case 3:
                rechargeCash();
                break;
            case 0:
                System.out.println("로그아웃 되었습니다.");
                start();
            default:
                System.out.println("잘못된 메뉴 선택입니다.");
        }  
    }

    
    public void displayProducts() {
        try {
            ResultSet resultSet = dbConnector.executeQuery("SELECT * FROM product");
            System.out.println("========== 상품 목록 ==========");
            while (resultSet.next()) {
                int productId = resultSet.getInt("product_id");
                String productName = resultSet.getString("product_name");
                int price = resultSet.getInt("price");
                int quantity = resultSet.getInt("quantity");
                System.out.println("상품 ID: " + productId + ", 상품명: " + productName + ", 가격: " + price + ", 재고: " + quantity);
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void purchaseProduct() {
        System.out.print("구매할 상품 ID를 입력하세요: ");
        int productId = scanner.nextInt();
        scanner.nextLine(); // 버퍼 비우기
        System.out.print("구매 수량을 입력하세요: ");
        int quantity = scanner.nextInt();
        scanner.nextLine(); // 버퍼 비우기

        if (isProductAvailable(productId, quantity)) {
            double totalPrice = calculateTotalPrice(productId, quantity);
            double userCash = getUserCash();

            if (userCash >= totalPrice) {
                if (updateProductQuantity(productId, quantity) && updateUserCash(userCash - totalPrice)) {
                    System.out.println("상품을 구매하였습니다.");

                    // 보유 현금 확인
                    System.out.println("보유 현금: " + getUserCash());
                } else {
                    System.out.println("상품 구매에 실패했습니다.");
                }
            } else {
                System.out.println("보유 현금이 부족합니다.");
            }
        } else {
            System.out.println("상품의 재고가 부족합니다.");
        }
    }
    
    

    public void rechargeCash() {
        System.out.print("충전할 금액을 입력하세요: ");
        double cash = scanner.nextDouble();
        scanner.nextLine(); // 버퍼 비우기

        if (updateUserCash(getUserCash() + cash)) {
            System.out.println("현금을 충전하였습니다.");
        } else {
            System.out.println("현금 충전에 실패했습니다.");
        }
    }

    private boolean isLoginValid(String id, String password) {
        try {
            ResultSet resultSet = dbConnector.executeQuery("SELECT * FROM k_member WHERE id = '" + id + "' AND password = '" + password + "'");
            boolean isValid = resultSet.next();
            resultSet.close();
            return isValid;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isProductAvailable(int productId, int quantity) {
        try {
            ResultSet resultSet = dbConnector.executeQuery("SELECT * FROM product WHERE product_id = " + productId + " AND quantity >= " + quantity);
            boolean isAvailable = resultSet.next();
            resultSet.close();
            return isAvailable;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private double calculateTotalPrice(int productId, int quantity) {
        try {
            ResultSet resultSet = dbConnector.executeQuery("SELECT price FROM product WHERE product_id = " + productId);
            if (resultSet.next()) {
                double price = resultSet.getDouble("price");
                resultSet.close();
                return price * quantity;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private double getUserCash() {
        try {
            ResultSet resultSet = dbConnector.executeQuery("SELECT cash FROM k_member");
            if (resultSet.next()) {
                double cash = resultSet.getDouble("cash");
                resultSet.close();
                return cash;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private boolean updateProductQuantity(int productId, int quantity) {
        try {
            int updatedRows = dbConnector.executeUpdate("UPDATE product SET quantity = quantity - " + quantity + " WHERE product_id = " + productId);
            return updatedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean updateUserCash(double cash) {
        try {
            int updatedRows = dbConnector.executeUpdate("UPDATE k_member SET cash = " + cash);
            return updatedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private void register() {
    	KMember kmember = new KMember();
        System.out.print("사용할 아이디를 입력하세요: ");
        kmember.id = scanner.nextLine();
        System.out.print("사용할 비밀번호를 입력하세요: ");
        kmember.password = scanner.nextLine();

        if (isIdAvailable(kmember.id)) {
            try {
            	String query = "" +
            			"INSERT INTO k_member (id, password, cash)"+
            			" VALUES (?, ?, ?)";
            	PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, kmember.getId());
                statement.setString(2, kmember.getPassword());
                statement.setInt(3, 0); // 초기 보유 현금은 0으로 설정
                statement.executeUpdate();
                statement.close();    
                System.out.println("회원가입에 성공했습니다.");
           }catch (SQLException e) {
               e.printStackTrace();
               System.out.println("회원가입에 실패했습니다.");
           }
        }else {
            System.out.println("이미 사용 중인 아이디입니다. 다른 아이디를 선택해주세요.");
        }
        start();
    }
    
    private boolean isIdAvailable(String id) {
        try {
        	String query = "" +
        			"SELECT * FROM k_member"+
        			" WHERE id = ?";
        	PreparedStatement statement = connection.prepareStatement(query);
        	statement.setString(1, id);
        	ResultSet resultSet = statement.executeQuery();
            boolean isAvailable = !resultSet.next(); // 이미 존재하는 경우 false 반환
            resultSet.close();
            return isAvailable;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
}
