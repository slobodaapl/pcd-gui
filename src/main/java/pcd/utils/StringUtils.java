/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcd.utils;

/**
 *
 * @author ixenr
 */
public final class StringUtils {

    private StringUtils() {
    }

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
