import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HuffmanDecoder {

    private String compressedFileName = ".\\src\\WarAndPeace-compressed.bin";
    private String codesFileName = ".\\src\\WarAndPeace-codes.txt";
    private String outputFileName = ".\\src\\WarAndPeace-decompressed.txt";
    private String codesString;
    private byte[] encodedText;

    private String book;
    private StringBuilder bitText = new StringBuilder();

    MyHashTable<String,String> codesHash = new MyHashTable<>(32768);


    public HuffmanDecoder() throws IOException {
        readFiles();
        buildCodes();
        rebuildText();
        writeFile();

    }

    private void readFiles() throws IOException {
        long duration = 0;
        long start = System.currentTimeMillis();

        encodedText = Files.readAllBytes(Paths.get(compressedFileName));
        for (byte b : encodedText) {
            bitText.append(Integer.toBinaryString((b & 0xFF) + 0x100).substring(1));
        }
        try (FileInputStream fis = new FileInputStream(codesFileName)) {
            Reader r = new InputStreamReader(fis, "US-ASCII");
            StringBuilder str = new StringBuilder();
            int ch = r.read();
            while(ch >= 0){

                char character = (char) ch;
                str.append(character);
                ch = r.read();
            }
            codesString = str.toString();

        }

        long now = System.currentTimeMillis();
        duration = now - start;
        System.out.println("It took "+duration+" ms to read compressed files");
        System.out.println();


    }

    private void buildCodes(){
        long duration = 0;
        long start = System.currentTimeMillis();

        StringBuilder str = new StringBuilder();
        for (int i = 0; i < codesString.length(); i++) {


            str.append(codesString.charAt(i));
            if(i==codesString.length()-1 || (i>0 && codesString.charAt(i+1) == '\n')){
                /*CodeNode node = new CodeNode();*/
                StringBuilder codeStr = new StringBuilder();
                String word = "";
                boolean codingMode = false;
                for (int j = 0; j < str.length(); j++) {
                    if(!codingMode){
                        if(str.charAt(j+1) == ':'){
                            word += str.charAt(j);
                            j++;
                            codingMode = true;
                        } else{
                            word += str.charAt(j);

                        }
                    } else{
                        codeStr.append(str.charAt(j));
                    }

                }


                codesHash.put(codeStr.toString(), word);
                str.setLength(0);
                i++;
            }


        }
        codesHash.put("100101011100011", "?");

        long now = System.currentTimeMillis();
        duration = now - start;
        System.out.println("It took "+duration+" ms to build "+codesHash.size() + " codes");
        System.out.println();



    }



    private void rebuildText(){
        long duration = 0;
        long start = System.currentTimeMillis();


        StringBuilder str = new StringBuilder();
        StringBuilder strBook = new StringBuilder();
        int wordCount = 0;
        for (int i = 0; i < encodedText.length*8; i++) {

            str.append(bitText.charAt(i));

            if(codesHash.get(str.toString()) != null){
                strBook.append(codesHash.get(str.toString()));
                str.setLength(0);
            }

        }

        book = strBook.toString();

        long now = System.currentTimeMillis();
        duration = now - start;
        System.out.println("It took "+duration+" ms to decode "+book.length() + " characters");
        System.out.println();
    }


    private void writeFile() throws IOException {
        long duration = 0;
        long start = System.currentTimeMillis();

        try (PrintStream out = new PrintStream(new FileOutputStream(outputFileName))) {
            out.print(book);
        }

        long now = System.currentTimeMillis();
        duration = now - start;
        System.out.println("It took "+duration+" ms to write the decompressed files");
        System.out.println();

    }
/*    class CodeNode implements Comparable<CodeNode>{
        Character character;
        String code;

        public int compareTo(CodeNode other){
            return this.code.compareTo(other.code);
        }
        public String toString(){
            return character+":"+code;
        }


    }*/

}

