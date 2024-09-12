package indi.jeremy.bean;

import org.springframework.stereotype.Component;

@Component("bean2")
public class Bean2 {

    Bean1 value;

    public Bean2(Bean1 val) {
        value = val;
    }

    public void hello() {
        System.out.println(this.getClass().getName() + " " + value);
    }

}
