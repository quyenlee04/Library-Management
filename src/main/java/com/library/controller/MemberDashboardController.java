package com.library.controller;

import com.library.model.User;
import com.library.service.AuthService;
import com.library.service.BookService;
import com.library.service.BorrowingService;
import com.library.service.ReaderService;
import com.library.util.AlertUtil;
import com.library.view.ViewManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MemberDashboardController implements Initializable {
    
    @FXML private Label memberNameLabel;
    @FXML private StackPane contentArea;
    
    private AuthService authService;
    private BookService bookService;
    private BorrowingService borrowingService;
    private ReaderService readerService;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        authService = AuthService.getInstance();
        bookService = BookService.getInstance();
        borrowingService = BorrowingService.getInstance();
        readerService = ReaderService.getInstance();
        
        // Hiển thị tên người dùng
        User currentUser = authService.getCurrentUser();
        if (currentUser != null) {
            memberNameLabel.setText("Xin chào, " + currentUser.getUsername());
        }
    }
    
    @FXML
    private void handleLogout(ActionEvent event) {
        authService.logout();
        ViewManager.getInstance().switchToLoginView();
    }
    
    @FXML
    private void handleDashboard(ActionEvent event) {
        // Hiển thị màn hình dashboard mặc định
        loadDefaultDashboard();
    }
    
    @FXML
    private void handleBrowseBooks(ActionEvent event) {
        loadView("/fxml/member/browse_books.fxml", "Tìm kiếm sách");
    }
    
    @FXML
    private void handleMyBorrowings(ActionEvent event) {
        loadView("/fxml/my_borrowings.fxml", "Sách đang mượn");
    }
    
    @FXML
    private void handleBorrowingHistory(ActionEvent event) {
        try {
            // Load the borrowing history view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/borrowing-history-view.fxml"));
            Parent view = loader.load();
            
            // Get the controller and set up any necessary data
            BorrowingHistoryController controller = loader.getController();
            
            // Replace the content in the contentArea with the borrowing history view
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showError("Lỗi", "Không thể mở lịch sử mượn/trả: " + e.getMessage());
        }
    }
    
    // Nếu có phương thức loadView, cần sửa như sau:
    private void loadView(String fxmlPath, String title, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Lỗi", "Không thể mở giao diện: " + e.getMessage());
        }
    }
    
    
    @FXML
    private void handleMyProfile(ActionEvent event) {
        loadView("/fxml/member/my_profile.fxml", "Thông tin cá nhân");
    }
    
    private void loadDefaultDashboard() {
        try {
            // Sử dụng nội dung mặc định từ contentArea thay vì tải file FXML mới
            VBox defaultContent = new VBox(20);
            defaultContent.setAlignment(Pos.CENTER);
            defaultContent.setPadding(new Insets(30));
            
            Text welcomeText = new Text("Chào mừng đến với Hệ thống Quản lý Thư viện");
            welcomeText.getStyleClass().add("welcome-text");
            
            // Lấy thông tin người dùng hiện tại
            User currentUser = authService.getCurrentUser();
            String username = currentUser != null ? currentUser.getUsername() : "Độc giả";
            
            Text userText = new Text("Bạn đã đăng nhập thành công với tư cách " + username);
            userText.getStyleClass().add("subtitle-text");
            
            // Hiển thị thông tin thống kê
            HBox statsBox = new HBox(50);
            statsBox.setAlignment(Pos.CENTER);
            statsBox.setPadding(new Insets(20));
            
            // Lấy dữ liệu thống kê từ các service có sẵn
            int activeBorrowingsCount = borrowingService.getActiveBorrowingsCount();
            int totalBorrowings = borrowingService.getAllBorrowings().size();
            int overdueBorrowingsCount = borrowingService.getOverdueBorrowingsCount();
            
            // Tạo các thẻ thống kê
            VBox borrowingStats = createStatCard("Sách đang mượn", String.valueOf(activeBorrowingsCount));
            VBox historyStats = createStatCard("Tổng số mượn", String.valueOf(totalBorrowings));
            VBox overdueStats = createStatCard("Sách quá hạn", String.valueOf(overdueBorrowingsCount));
            
            statsBox.getChildren().addAll(borrowingStats, historyStats, overdueStats);
            
            // Hiển thị các hoạt động gần đây
            VBox activitiesBox = new VBox(10);
            activitiesBox.setAlignment(Pos.CENTER_LEFT);
            activitiesBox.setPadding(new Insets(20, 0, 0, 0));
            
            Text activitiesTitle = new Text("Hoạt động gần đây");
            activitiesTitle.getStyleClass().add("section-title");
            
            ListView<String> activitiesList = new ListView<>();
            activitiesList.setPrefHeight(150);
            List<String> recentActivities = borrowingService.getRecentActivities(5);
            activitiesList.getItems().addAll(recentActivities);
            
            activitiesBox.getChildren().addAll(activitiesTitle, activitiesList);
            
            defaultContent.getChildren().addAll(welcomeText, userText, statsBox, activitiesBox);
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(defaultContent);
            
        } catch (Exception e) {
            e.printStackTrace();
            
            // Nếu có lỗi, hiển thị nội dung mặc định đơn giản
            VBox fallbackContent = new VBox(20);
            fallbackContent.setAlignment(Pos.CENTER);
            
            Text welcomeText = new Text("Chào mừng đến với Hệ thống Quản lý Thư viện");
            Text errorText = new Text("Đã xảy ra lỗi khi tải dữ liệu. Vui lòng thử lại sau.");
            errorText.setFill(javafx.scene.paint.Color.RED);
            
            fallbackContent.getChildren().addAll(welcomeText, errorText);
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(fallbackContent);
        }
    }
    
    // Phương thức hỗ trợ tạo thẻ thống kê
    private VBox createStatCard(String title, String value) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setMinWidth(150);
        card.setMinHeight(100);
        card.getStyleClass().add("stat-card");
        
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stat-title");
        
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value");
        
        card.getChildren().addAll(valueLabel, titleLabel);
        return card;
    }
    
    private void loadView(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            
            // Truyền dịch vụ cho controller nếu cần
            Object controller = loader.getController();
            if (controller instanceof BrowseBooksController) {
                ((BrowseBooksController) controller).setServices(bookService, borrowingService);
            }
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleReturnBook(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/ReturnBookView.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Trả sách");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Lỗi", "Không thể mở giao diện trả sách: " + e.getMessage());
        }
    }
}