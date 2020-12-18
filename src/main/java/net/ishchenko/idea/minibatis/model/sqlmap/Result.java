package net.ishchenko.idea.minibatis.model.sqlmap;


import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 10.04.12
 * Time: 22:53
 */
public interface Result extends DomElement {

    @NameValue
    @Attribute("property")
    GenericAttributeValue<String> getProperty();

    @Attribute("typeHandler")
    GenericAttributeValue<TypeAlias> getTypeHandler();

    @Attribute("resultMap")
    GenericAttributeValue<ResultMap> getResultMap();

}
