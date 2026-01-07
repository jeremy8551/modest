package cn.org.expect.script.command;

import java.io.File;
import java.util.List;

import cn.org.expect.expression.DataUnitExpression;
import cn.org.expect.os.OS;
import cn.org.expect.os.OSDisk;
import cn.org.expect.script.UniversalCommandCompiler;
import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptSession;
import cn.org.expect.script.UniversalScriptStderr;
import cn.org.expect.script.UniversalScriptStdout;
import cn.org.expect.script.command.feature.NohupCommandSupported;
import cn.org.expect.util.CharTable;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

public class DfCommand extends AbstractTraceCommand implements NohupCommandSupported {

    public DfCommand(UniversalCommandCompiler compiler, String command) {
        super(compiler, command);
    }

    public int execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, boolean forceStdout, File outfile, File errfile) throws Exception {
        OS os = context.getContainer().getBean(OS.class);
        try {
            CharTable table = new CharTable();
            String[] titles = ResourcesUtils.getMessageArray("script.stdout.message007");
            table.addTitle(titles[0]);
            table.addTitle(titles[1]);
            table.addTitle(titles[2]);
            table.addTitle(titles[3]);
            table.addTitle(titles[4]);
            table.addTitle(titles[5]);

            List<OSDisk> list = os.getOSDisk();
            for (OSDisk disk : list) {
                table.addCell(disk.getId());
                table.addCell(DataUnitExpression.toString(disk.total(), true));
                table.addCell(DataUnitExpression.toString(disk.free(), true));
                table.addCell(DataUnitExpression.toString(disk.used(), true));
                table.addCell(disk.getType());
                table.addCell(disk.getAmount());
            }

            if (session.isEchoEnable() || forceStdout) {
                stdout.println(table.toString(CharTable.Style.SHELL));
            }
            return 0;
        } finally {
            os.close();
        }
    }

    public boolean enableNohup() {
        return true;
    }
}
