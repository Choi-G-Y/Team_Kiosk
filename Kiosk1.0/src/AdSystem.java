import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AdSystem {
    private DBConnector dbConnector;
    private Scanner scanner;

    private boolean isAdminMode(String id, String password) {
        try {
            ResultSet resultSet = dbConnector.executeQuery("SELECT * FROM admin WHERE id = '" + id + "' AND password = '" + password + "'");
            boolean isValid = resultSet.next();
            resultSet.close();
            return isValid;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void adminMode() {
        System.out.print("관리자 아이디를 입력하세요: ");
        String id = scanner.nextLine();
        System.out.print("관리자 비밀번호를 입력하세요: ");
        String password = scanner.nextLine();

        if (isAdminMode(id, password)) {
            System.out.println("관리자 모드로 전환되었습니다.");
            while (true) {
                System.out.println("\n========== 관리자 모드 ==========");
                System.out.println("1. 재고 채우기");
                System.out.println("2. 재고 확인");
                System.out.println("0. 돌아가기");
                System.out.print("메뉴 선택: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // 버퍼 비우기

                switch (choice) {
                    case 1:
                        fillStock();
                        break;
                    case 2:
                        displayProducts();
                        break;
                    case 0:
                        System.out.println("관리자 모드를 종료합니다.");
                        return;
                    default:
                        System.out.println("잘못된 메뉴 선택입니다.");
                }
            }
        } else {
            System.out.println("관리자 아이디 또는 비밀번호가 올바르지 않습니다.");
        }
    }

	private void fillStock() {
        System.out.print("추가할 상품 ID를 입력하세요: ");
        int productId = scanner.nextInt();
        scanner.nextLine(); // 버퍼 비우기
        System.out.print("추가할 수량을 입력하세요: ");
        int quantity = scanner.nextInt();
        scanner.nextLine(); // 버퍼 비우기

        try {
            PreparedStatement statement = dbConnector.getConnection().prepareStatement("UPDATE product SET quantity = quantity + ? WHERE product_id = ?");
            statement.setInt(1, quantity);
            statement.setInt(2, productId);
            int rowsUpdated = statement.executeUpdate();
            statement.close();
            if (rowsUpdated > 0) {
                System.out.println("재고를 추가하였습니다.");
            } else {
                System.out.println("상품 ID를 확인하세요.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
    private void displayProducts() {
		// TODO Auto-generated method stub
		
	}
}
