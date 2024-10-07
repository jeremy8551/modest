package cn.org.expect.script.spi;

import java.io.Reader;
import java.io.Writer;
import java.util.List;
import javax.script.Bindings;
import javax.script.ScriptContext;

import cn.org.expect.script.UniversalScriptContext;

public class ScriptContextImpl implements ScriptContext {

    protected UniversalScriptContext context;

    public ScriptContextImpl(UniversalScriptContext context) {
        this.context = context;
    }

    public void setBindings(Bindings bindings, int scope) {
        this.context.setVariable(new UniversalScriptVariableBindings(bindings), scope);
    }

    public Bindings getBindings(int scope) {
        return new BindingsImpl(this.context.getVariable(scope));
    }

    public void setAttribute(String name, Object value, int scope) {
        this.context.setAttribute(name, value, scope);
    }

    public Object getAttribute(String name, int scope) {
        return this.context.getAttribute(name, scope);
    }

    public Object removeAttribute(String name, int scope) {
        return this.context.removeAttribute(name, scope);
    }

    public Object getAttribute(String name) {
        return this.context.getAttribute(name);
    }

    public int getAttributesScope(String name) {
        return this.context.getAttributesScope(name);
    }

    public Writer getWriter() {
        return this.context.getWriter();
    }

    public Writer getErrorWriter() {
        return this.context.getErrorWriter();
    }

    public void setWriter(Writer writer) {
        this.context.setWriter(writer);
    }

    public void setErrorWriter(Writer writer) {
        this.context.setErrorWriter(writer);
    }

    public Reader getReader() {
        return this.context.getReader();
    }

    public void setReader(Reader reader) {
        this.context.setReader(reader);
    }

    public List<Integer> getScopes() {
        return this.context.getScopes();
    }
}
