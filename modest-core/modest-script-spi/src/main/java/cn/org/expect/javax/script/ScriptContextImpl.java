package cn.org.expect.javax.script;

import java.io.Reader;
import java.io.Writer;
import java.util.List;
import javax.script.Bindings;
import javax.script.ScriptContext;

import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.util.Ensure;

public class ScriptContextImpl implements ScriptContext {

    protected UniversalScriptContext context;

    public ScriptContextImpl(UniversalScriptContext context) {
        this.context = Ensure.notNull(context);
    }

    public void setBindings(Bindings bindings, int scope) {
        this.context.addVariable(new UniversalScriptVariableBindings(bindings), scope);
    }

    public Bindings getBindings(int scope) {
        return new BindingsImpl(this.context.getVariables(scope));
    }

    public void setAttribute(String name, Object value, int scope) {
        this.context.addVariable(name, value, scope);
    }

    public Object getAttribute(String name, int scope) {
        return this.context.getVariable(name, scope);
    }

    public Object removeAttribute(String name, int scope) {
        return this.context.removeVariable(name, scope);
    }

    public Object getAttribute(String name) {
        return this.context.getVariable(name);
    }

    public int getAttributesScope(String name) {
        return this.context.getVariableScope(name);
    }

    public Writer getWriter() {
        return this.context.getEngine().getWriter();
    }

    public Writer getErrorWriter() {
        return this.context.getEngine().getErrorWriter();
    }

    public void setWriter(Writer writer) {
        this.context.getEngine().setWriter(writer);
    }

    public void setErrorWriter(Writer writer) {
        this.context.getEngine().setErrorWriter(writer);
    }

    public Reader getReader() {
        return this.context.getEngine().getReader();
    }

    public void setReader(Reader reader) {
        this.context.getEngine().setReader(reader);
    }

    public List<Integer> getScopes() {
        return this.context.getScopes();
    }
}
