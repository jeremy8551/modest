package cn.org.expect.javax.script;

import java.util.List;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

import cn.org.expect.script.UniversalScriptEngineFactory;
import cn.org.expect.util.Ensure;

public class ScriptEngineFactoryImpl implements ScriptEngineFactory {

    protected UniversalScriptEngineFactory factory;

    public ScriptEngineFactoryImpl() {
        this(new UniversalScriptEngineFactory());
    }

    public ScriptEngineFactoryImpl(UniversalScriptEngineFactory factory) {
        this.factory = Ensure.notNull(factory);
    }

    public String getEngineName() {
        return this.factory.getEngineName();
    }

    public String getEngineVersion() {
        return this.factory.getEngineVersion();
    }

    public List<String> getExtensions() {
        return this.factory.getExtensions();
    }

    public List<String> getMimeTypes() {
        return this.factory.getMimeTypes();
    }

    public List<String> getNames() {
        return this.factory.getNames();
    }

    public String getLanguageName() {
        return this.factory.getLanguageName();
    }

    public String getLanguageVersion() {
        return this.factory.getLanguageVersion();
    }

    public Object getParameter(String key) {
        return this.factory.getProperty(key);
    }

    public String getMethodCallSyntax(String obj, String m, String... args) {
        return this.factory.getMethodCallSyntax(obj, m, args);
    }

    public String getOutputStatement(String toDisplay) {
        return this.factory.getOutputStatement(toDisplay);
    }

    public String getProgram(String... statements) {
        return this.factory.getProgram(statements);
    }

    public ScriptEngine getScriptEngine() {
        return new ScriptEngineImpl(this, this.factory.getScriptEngine());
    }
}
