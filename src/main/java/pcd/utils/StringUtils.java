/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.utils;

/**
 * This class is responsible for checking whether a string contains a white space.
 * @author Noemi Farkas
 */
public final class StringUtils {

    private StringUtils() {
    }
/**
 * This method is responsible for checking whether a string contains a white space 
 * and returning the location of the first white space withing the string if there is one.
 * If there is none, then it returns -1.
 * @param string String to be checked
 * @return int -1 if no white space found, and i containing the location if there is one.
 */
    public static int indexNonWhitespace(String string) {
        char[] characters = string.toCharArray();
        for (int i = 0; i < string.length(); i++) {
            if (!Character.isWhitespace(characters[i])) {
                return i;
            }
        }
        return -1;
    }

}
