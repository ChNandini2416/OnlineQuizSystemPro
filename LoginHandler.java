package controller;

public class LoginHandler {

    public static boolean validateLogin(String username, String password) {
        // your JDBC logic here
        return true;
    }

    public static void main(String[] args) {
        boolean result = validateLogin("nandini", "123456");
        System.out.println(result ? "✅ Login successful!" : "❌ Login failed.");
    }
}