package cn.org.expect.javax.script;

import java.io.CharArrayReader;
import java.io.Reader;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

import cn.org.expect.script.UniversalScriptContext;
import cn.org.expect.script.UniversalScriptEngine;
import cn.org.expect.script.UniversalScriptException;
import cn.org.expect.util.Ensure;

public class ScriptEngineImpl implements ScriptEngine {

    protected ScriptEngineFactoryImpl factory;

    protected UniversalScriptEngine engine;

    public ScriptEngineImpl(ScriptEngineFactoryImpl factory, UniversalScriptEngine engine) {
        this.factory = Ensure.notNull(factory);
        this.engine = Ensure.notNull(engine);
    }

    /**
     * 将输入参数强制转换为脚本引擎上下文信息
     *
     * @param context 脚本引擎上下文信息
     * @return 脚本引擎上下文信息
     */
    protected UniversalScriptContext castScriptContext(ScriptContext context) {
        if (context instanceof UniversalScriptContext) {
            return (UniversalScriptContext) context;
        } else {
            throw new UniversalScriptException("engine.stdout.message001", context.getClass().getName(), UniversalScriptContext.class.getName());
        }
    }

    public Object get(String key) {
        return this.engine.getContext().getVariable(key);
    }

    public Bindings getBindings(int scope) {
        return new BindingsImpl(this.engine.getContext().getVariables(scope));
    }

    public ScriptEngineFactory getFactory() {
        return this.factory;
    }

    public void put(String name, Object value) {
        this.engine.getContext().addVariable(name, value, UniversalScriptContext.ENGINE_SCOPE);
    }

    public void setBindings(Bindings bindings, int scope) {
        this.engine.getContext().addVariable(new ScriptVariableBindings(bindings), scope);
    }

    public Bindings createBindings() {
        return new BindingsImpl(this.engine.getFactory().buildVariable());
    }

    public ScriptContext getContext() {
        return new ScriptContextImpl(this.engine.getContext());
    }

    public void setContext(ScriptContext context) {
        this.engine.setContext(this.castScriptContext(context));
    }

    public Object eval(String script) {
        return this.engine.evaluate(new CharArrayReader(script.toCharArray()), this.engine.getContext());
    }

    public Object eval(String script, ScriptContext scriptContext) {
        return this.engine.evaluate(new CharArrayReader(script.toCharArray()), this.castScriptContext(scriptContext));
    }

    public Object eval(String script, Bindings bindings) {
        this.setBindings(bindings, UniversalScriptContext.ENGINE_SCOPE);
        CharArrayReader in = new CharArrayReader(script.toCharArray());
        return this.engine.evaluate(in, this.engine.getContext());
    }

    public Object eval(Reader in, Bindings bindings) {
        this.setBindings(bindings, UniversalScriptContext.ENGINE_SCOPE);
        return this.engine.evaluate(in, this.engine.getContext());
    }

    public Object eval(Reader in) {
        return this.engine.evaluate(in, this.engine.getContext());
    }

    public Object eval(Reader in, ScriptContext scriptContext) {
        return this.engine.evaluate(in, this.castScriptContext(scriptContext));
    }
}
