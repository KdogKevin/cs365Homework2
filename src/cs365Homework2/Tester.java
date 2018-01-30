package cs365Homework2;

public class Tester {

    public static void main(String[] args) {
        Fp fp = new Fp();

        int v24_25 = 0x41C20000; //  24.25
        int v_1875 = 0xBE400000; // -0.1875
        int v5     = 0xC0A00000; // -5.0
        
        System.out.println(fp.add(v_1875, v_1875));

        System.out.println(fp.mul(v5, v_1875));

    }
}


