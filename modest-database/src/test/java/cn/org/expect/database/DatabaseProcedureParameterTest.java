package cn.org.expect.database;

import cn.org.expect.database.internal.StandardDatabaseProcedureParameter;
import org.junit.Assert;
import org.junit.Test;

public class DatabaseProcedureParameterTest {

    @Test
    public void testIsLinuxVariableName() {
        DatabaseProcedureParameter param = new StandardDatabaseProcedureParameter();
        param.setExpression("$1");
        Assert.assertTrue(param.isExpression());

        param.setExpression("$abc");
        Assert.assertTrue(param.isExpression());
        param.setExpression("$a_bc");
        Assert.assertTrue(param.isExpression());
        param.setExpression("$_a_bc");
        Assert.assertTrue(param.isExpression());
        param.setExpression("abc");
        Assert.assertFalse(param.isExpression());
        param.setExpression("$abc+");
        Assert.assertFalse(param.isExpression());
    }
}
