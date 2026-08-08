package cn.org.expect.util;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

/**
 * 提供 16 版本对应的 Java 平台能力适配
 */
public class Java16Dialect extends Java12Dialect {

    public boolean isTypeElement(Object obj) {
        TypeElement typeElement = (TypeElement) obj;
        return typeElement.getKind() == ElementKind.CLASS //
            || typeElement.getKind() == ElementKind.INTERFACE //
            || typeElement.getKind() == ElementKind.ENUM //
            || typeElement.getKind() == ElementKind.ANNOTATION_TYPE //
            || typeElement.getKind() == ElementKind.RECORD //
            ;
    }
}
