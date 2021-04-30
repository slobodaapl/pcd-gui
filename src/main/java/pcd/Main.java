/*
 * The MIT License
 *
 * Copyright 2021 Tibor Sloboda and Noemi Farkas.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package pcd;



/**
 *
 * @author Tibor Sloboda
 */
public class Main {

    /**
     *  
     * @param args arguments
     */
    public static void main(String[] args) {
        String projectFile = "";
        // Pass all files with the .pcd extension to be opened (project files)
        if (args.length != 0) {
            for (String arg : args) {
                if (arg.contains(".pcd")) {
                    projectFile = arg;
                    break;
                }
            }
        }

        Initializer init = new Initializer();

        init.run(projectFile);
    }
}
