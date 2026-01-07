package cn.org.expect.collection;

import cn.org.expect.util.StringComparator;
import org.junit.Assert;
import org.junit.Test;

public class MatrixTest {

    @Test
    public void test() {
        Matrix<String> matrix = this.buildMatrix();

        Assert.assertEquals("9,0", matrix.get(9, 0));
        Assert.assertEquals("9,9", matrix.get(9, 9));
        Assert.assertEquals("0,0", matrix.get(0, 0));
        Assert.assertEquals("0,4", matrix.get(0, 4));
        Matrix<String> copy = matrix.clone();

        Matrix<String> clone = matrix.clone();
        clone.sortRow(false, new int[]{0}, new StringComparator());

        clone = matrix.clone();
        clone.sortCol(false, new int[]{9}, new StringComparator());

        // 顺时针旋转 再 逆时针旋转
        matrix.rotate();
        matrix.reverseRotate();
        Assert.assertEquals(matrix, copy);

        matrix.rotate();
        matrix.rotate();
        matrix.rotate();
        matrix.rotate();
        Assert.assertEquals(matrix, copy);

        matrix.reverseRotate();
        matrix.reverseRotate();
        matrix.reverseRotate();
        matrix.reverseRotate();
        Assert.assertEquals(matrix, copy);
    }

    private Matrix<String> buildMatrix() {
        Matrix<String> matrix = new Matrix<String>(5, 5);
        for (int r = 0; r < matrix.getRow(); r++) {
            for (int c = 0; c < matrix.getColumn(); c++) {
                matrix.set(r, c, r + "," + c);
            }
        }
        matrix.expandCapacity(6, 6);

        matrix.set(5, 5, "5,5");
        matrix.expandCapacity(10, 10);
        matrix.set(9, 9, "9,9");
        matrix.set(9, 0, "9,0");
        return matrix;
    }
}
