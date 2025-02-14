package cn.org.expect.zh;

import cn.org.expect.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class ChineseRandomTest {

    @Test
    public void test1() {
        ChineseRandom random = new ChineseRandom();
        for (int i = 0; i < 10000; i++) {
            random.get().nextInt();
        }
    }

    @Test
    public void test2() {
        String[] names = {"王", "李", "张"};
        String[] compoundSurnames = {"司马", "上官", "欧阳"};

        ChineseRandom random = new ChineseRandom();
        random.setSurname(names);
        random.setDoubleSurname(compoundSurnames);
        random.setNameWords(new String[]{"节", "本", "术"});

        for (int i = 0; i < 10000; i++) {
            Assert.assertTrue(StringUtils.isNotBlank(random.nextName()));
        }
    }
}
