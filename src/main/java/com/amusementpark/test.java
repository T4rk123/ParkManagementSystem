package com.amusementpark;
import org.mindrot.jbcrypt.BCrypt;


public class test {
    public static void Main(String args[]) {
        BCrypt.hashpw("admin123", BCrypt.gensalt());
    }
}
