package indi.jeremy.bean;

import org.springframework.stereotype.Component;

@Component("bean1")
public class Bean1 {

    public void hello() {
        System.out.println(this.getClass().getName());
    }

}
