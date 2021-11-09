package net.ishchenko.idea.minibatis.model.sqlmap;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * @author yanglin
 */
public interface PropertyGroup extends DomElement {

    @Attribute("property")
    GenericAttributeValue<String> getProperty();
}
