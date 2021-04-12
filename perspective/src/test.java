import bv_ws20.GeometricTransform;

public class test {
    public static void main(String[] args) {
        int[][] matrix = new int[4][3];
        for(int i = 0; i < 4;i++){
            for(int j = 0; j < 3;j++){
                matrix[i][j] = j;
            }
        }
        System.out.println(matrix[0][0]);
        System.out.println(matrix[0][1]);
        System.out.println(matrix[0][2]);
        System.out.println();
        System.out.println(matrix[1][0]);
        System.out.println(matrix[1][1]);
        System.out.println(matrix[1][2]);
        System.out.println();
        System.out.println(matrix[2][0]);
        System.out.println(matrix[2][1]);
        System.out.println(matrix[2][2]);
        System.out.println();
        System.out.println(matrix[3][0]);
        System.out.println(matrix[3][1]);
        System.out.println(matrix[3][2]);
        System.out.println();

        for(int i = 0; i < 4;i++){
            for(int j = 0; j < 3;j++){
               System.out.print(matrix[i][j]);
            }
            System.out.print("\n");
        }

        System.out.println();



    }
}
