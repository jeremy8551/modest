package cn.org.expect.controller;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import cn.org.expect.ioc.EasyContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 演示当前模块的基础调用方式
 */
@RestController
public class HelloControllerSample {

    @Autowired
    private ScriptEngine engine;

    @Autowired
    private EasyContext context;

    @RequestMapping("/helloWorld")
    public String execute() throws ScriptException {
        this.engine.eval("echo hello world!");
        return "success";
    }
}
